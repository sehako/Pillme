from paddleocr import PaddleOCR
import numpy as np
from PIL import Image
import traceback
from utils.image_utils import crop_and_deskew

class OCRService:
    def __init__(self):
        self.ocr = PaddleOCR(
            use_angle_cls=False,
            lang="korean",
            det_model_dir="models/ch_ppocr_mobile_v2.0_det_infer",
            rec_model_dir="models/ch_ppocr_mobile_v2.0_rec_infer",
            use_gpu=False,
            show_log=False
        )

    def process_image(self, image_path):
        """이미지를 OCR로 처리하고 텍스트 정보를 추출"""
        try:
            if not isinstance(image_path, str):
                raise ValueError(f"🚨 잘못된 이미지 경로 타입: {type(image_path)}")

            # ✅ 이미지 로드 최적화 (OpenCV → PIL 사용)
            img = Image.open(image_path).convert("RGB")

            # ✅ OCR 실행 (PIL 이미지 사용)
            results = self.ocr.ocr(np.array(img), cls=False)
            if not results or not isinstance(results, list):
                raise ValueError(f"🚨 OCR 실행 실패: {results}")

            ocr_data = []
            for idx, line in enumerate(results):
                for word_info in line:
                    if not isinstance(word_info, (list, tuple)) or len(word_info) < 2:
                        continue

                    box, (text, confidence) = word_info

                    if not isinstance(box, list) or len(box) < 4:
                        continue  # 🚨 박스 좌표 오류 방지

                    # ✅ OCR 신뢰도 0.5 이하일 경우, 이미지 전처리 후 재처리
                    if confidence < 0.5:
                        cropped_img = crop_and_deskew(np.array(img), box)
                        if cropped_img is not None:
                            sub_results = self.ocr.ocr(cropped_img, cls=False)
                            for sub_line in sub_results:
                                for sub_word_info in sub_line:
                                    sub_text, sub_confidence = sub_word_info[1]
                                    if sub_confidence > confidence:
                                        text, confidence = sub_text, sub_confidence

                    ocr_data.append({
                        "index": idx + 1,
                        "text": text,
                        "confidence": confidence,
                        "box": box
                    })

            return ocr_data

        except Exception as e:
            print("🔥 OCR 오류 발생:", str(e))
            print(traceback.format_exc())  # **🔍 전체 오류 로그 출력**
            return []
