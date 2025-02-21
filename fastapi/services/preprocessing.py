import re
import pandas as pd

class PreprocessingService:
    @staticmethod
    def preprocess_with_content(word):
        """괄호 제거, 공백 제거, 소문자로 변환"""
        return re.sub(r"[()]", "", word).replace(" ", "").lower()

    @staticmethod
    def preprocess_without_content(word):
        """괄호 안의 내용, 숫자, '밀리그램' 제거 후 정리"""
        return re.sub(r"\s*\(.*?\)|\d+|\b밀리그램\b", "", word).replace(" ", "").lower()

    @staticmethod
    def prepare_data(csv_file_path):
        """CSV 파일을 로드하고 약품명을 전처리하여 매핑 딕셔너리를 생성"""
        try:
            # ✅ CSV 파일 로드 (필요한 컬럼만 선택하여 메모리 절약)
            df = pd.read_csv(csv_file_path, usecols=["ITEM_NAME"]).dropna()

            # ✅ 정규식을 활용한 벡터 연산 최적화
            df["약품명_내용_유지"] = df["ITEM_NAME"].str.replace(r"[()]", "", regex=True).str.replace(" ", "").str.lower()
            df["약품명_내용_제거"] = df["ITEM_NAME"].str.replace(r"\s*\(.*?\)|\d+|\b밀리그램\b", "", regex=True).str.replace(" ", "").str.lower()

            # ✅ 매핑 딕셔너리 생성 (중복 키 방지)
            약품명_매핑 = {}
            for _, row in df.iterrows():
                약품명_매핑.setdefault(row["약품명_내용_유지"], row["ITEM_NAME"])
                if row["약품명_내용_제거"] not in 약품명_매핑:
                    약품명_매핑[row["약품명_내용_제거"]] = row["ITEM_NAME"]

            return df, 약품명_매핑

        except Exception as e:
            print(f"🚨 데이터 전처리 오류: {e}")
            return None, {}
