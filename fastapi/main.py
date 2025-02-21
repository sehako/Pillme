from app import DrugAnalysisApp
import uvicorn
import logging

def setup_logging():
    # 기존 핸들러 제거
    root_logger = logging.getLogger()
    if root_logger.handlers:
        for handler in root_logger.handlers:
            root_logger.removeHandler(handler)
    
    # 로깅 설정
    root_logger.setLevel(logging.INFO)
    handler = logging.StreamHandler()
    formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
    handler.setFormatter(formatter)
    root_logger.addHandler(handler)

def main():
    setup_logging()
    logger = logging.getLogger(__name__)
    logger.info("서버 시작 중...")
    
    app = DrugAnalysisApp()
    
    # uvicorn 로깅 설정
    log_config = uvicorn.config.LOGGING_CONFIG
    log_config["formatters"]["access"]["fmt"] = "%(asctime)s - %(name)s - %(levelname)s - %(message)s"
    
    uvicorn.run(
        app.app, 
        host="0.0.0.0", 
        port=8000,
        log_config=log_config
    )

if __name__ == "__main__":
    main()