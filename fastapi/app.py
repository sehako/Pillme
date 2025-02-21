from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.middleware.cors import CORSMiddleware
import tempfile
import shutil
import os
import logging
from models.schemas import DrugResponse
from services.preprocessing import PreprocessingService
from services.word2vec_service import Word2VecService
from services.ocr_service import OCRService
from services.matching_service import MatchingService
from time import time

# 로깅 설정
logger = logging.getLogger(__name__)

class DrugAnalysisApp:
    def __init__(self):
        logger.info("DrugAnalysisApp 초기화 시작")
        self.app = FastAPI()
        self.setup_cors()
        self.setup_services()
        self.setup_routes()
        logger.info("DrugAnalysisApp 초기화 완료")

    def setup_cors(self):
        logger.info("CORS 설정 중...")
        self.app.add_middleware(
            CORSMiddleware,
            allow_origins=["*"],
            allow_credentials=True,
            allow_methods=["*"],
            allow_headers=["*"],
        )
        logger.info("CORS 설정 완료")

    def setup_services(self):
        try:
            logger.info("서비스 초기화 시작...")
            
            logger.info("데이터 전처리 시작...")
            df, self.약품명_매핑 = PreprocessingService.prepare_data("./data.CSV")
            logger.info(f"데이터 전처리 완료: {len(df)} 개의 약품 데이터 로드됨")
            
            logger.info("Word2Vec 모델 초기화 및 학습 시작...")
            self.word2vec_service = Word2VecService()
            self.word2vec_service.train_models(df)
            logger.info("Word2Vec 모델 학습 완료")
            
            logger.info("OCR 서비스 초기화...")
            self.ocr_service = OCRService()
            logger.info("OCR 서비스 초기화 완료")
            
            logger.info("매칭 서비스 초기화...")
            self.matching_service = MatchingService(self.word2vec_service, self.약품명_매핑)
            logger.info("매칭 서비스 초기화 완료")

        except Exception as e:
            logger.error(f"서비스 초기화 중 오류 발생: {str(e)}")
            raise

    def setup_routes(self):
        @self.app.post("/analyze-image/", response_model=list[DrugResponse])
        async def analyze_image(file: UploadFile = File(...)):
            start_time = time()
            logger.info(f"이미지 분석 요청 받음: {file.filename}")
            
            with tempfile.NamedTemporaryFile(delete=False, suffix='.jpg') as temp_file:
                shutil.copyfileobj(file.file, temp_file)
                temp_path = temp_file.name
                logger.info(f"임시 파일 생성됨: {temp_path}")

            try:
                if not os.path.exists(temp_path):
                    logger.error("임시 파일을 찾을 수 없음")
                    raise HTTPException(status_code=400, detail="파일을 처리할 수 없습니다.")

                ocr_start_time = time()
                logger.info("OCR 처리 시작...")
                ocr_data = self.ocr_service.process_image(temp_path)
                ocr_time = time() - ocr_start_time
                logger.info(f"OCR 처리 완료: {len(ocr_data)}개의 텍스트 인식됨 (소요시간: {ocr_time:.2f}초)")
                logger.info("인식된 텍스트:")
                for item in ocr_data:
                    logger.info(f"- 텍스트: {item['text']}, 신뢰도: {item['confidence']:.2f}")

                matching_start_time = time()
                logger.info("약품 매칭 시작...")
                
                filtered_ocr_data = [item for item in ocr_data if len(item['text']) > 2]
                
                matched_drugs = self.matching_service.process_ocr_results(filtered_ocr_data)
                matching_time = time() - matching_start_time
                
                logger.info("\n=== 최종 매칭 결과 ===")
                for ocr_word, matched_drug in matched_drugs.items():
                    logger.info(f"✅ {ocr_word} → {matched_drug}")
                logger.info("=====================")

                # 중복된 matched_drug 제거
                unique_drugs = {drug for drug in matched_drugs.values()}

                return_result = [
                    DrugResponse(matched_drug=drug)
                    for drug in unique_drugs
                ]

                total_time = time() - start_time
                logger.info(f"분석 완료: {len(return_result)}개의 고유 약품 매칭됨")
                logger.info(f"처리 시간 분석:")
                logger.info(f"- OCR 처리: {ocr_time:.2f}초")
                logger.info(f"- 약품 매칭: {matching_time:.2f}초")
                logger.info(f"- 총 소요시간: {total_time:.2f}초")
                return return_result

            except Exception as e:
                logger.error(f"이미지 처리 중 오류 발생: {str(e)}")
                raise HTTPException(status_code=500, detail=str(e))
            
            finally:
                logger.info(f"임시 파일 삭제: {temp_path}")
                os.unlink(temp_path)
