import numpy as np
import faiss
import gc
from gensim.models import Word2Vec

class Word2VecService:
    def __init__(self):
        self.word2vec_model_with_content = None
        self.word2vec_model_without_content = None
        self.faiss_index_with_content = None
        self.faiss_index_without_content = None
        self.vocab_with_content = None
        self.vocab_without_content = None

    def train_models(self, df):
        """Word2Vec 모델을 학습하고 기존 모델을 안전하게 해제"""
        # ✅ 기존 모델 해제 (None 할당)
        self.word2vec_model_with_content = None
        self.word2vec_model_without_content = None
        gc.collect()

        # ✅ 벡터 연산 최적화 (`.str.split().tolist()` 활용)
        tokenized_with_content = df["약품명_내용_유지"].str.split().tolist()
        tokenized_without_content = df["약품명_내용_제거"].str.split().tolist()

        # ✅ Word2Vec 학습 최적화 (`build_vocab()` + `train()` 방식 적용)
        self.word2vec_model_with_content = Word2Vec(vector_size=100, window=3, min_count=1, workers=4, sg=1)
        self.word2vec_model_with_content.build_vocab(tokenized_with_content)
        self.word2vec_model_with_content.train(tokenized_with_content, total_examples=len(tokenized_with_content), epochs=10)

        self.word2vec_model_without_content = Word2Vec(vector_size=100, window=3, min_count=1, workers=4, sg=1)
        self.word2vec_model_without_content.build_vocab(tokenized_without_content)
        self.word2vec_model_without_content.train(tokenized_without_content, total_examples=len(tokenized_without_content), epochs=10)

        # ✅ 토큰화 데이터 해제
        del tokenized_with_content, tokenized_without_content
        gc.collect()

        self.prepare_faiss_indexes()

    def prepare_faiss_indexes(self):
        """FAISS 인덱스를 초기화하고 벡터를 추가"""
        vector_dim = self.word2vec_model_with_content.vector_size

        # ✅ 기존 FAISS 인덱스 삭제 후 새로운 객체 생성
        self.faiss_index_with_content = faiss.IndexFlatL2(vector_dim)
        self.faiss_index_without_content = faiss.IndexFlatL2(vector_dim)

        # ✅ 단어 리스트 생성
        self.vocab_with_content = self.word2vec_model_with_content.wv.index_to_key
        self.vocab_without_content = self.word2vec_model_without_content.wv.index_to_key

        # ✅ 벡터 생성 및 추가 (NumPy 배열 변환 최적화)
        vectors_with_content = np.array(
            [self.word2vec_model_with_content.wv[word] for word in self.vocab_with_content], dtype=np.float32
        )
        vectors_without_content = np.array(
            [self.word2vec_model_without_content.wv[word] for word in self.vocab_without_content], dtype=np.float32
        )

        self.faiss_index_with_content.add(vectors_with_content)
        self.faiss_index_without_content.add(vectors_without_content)

        # ✅ 불필요한 데이터 삭제 및 `gc.collect()` 최소화
        del vectors_with_content, vectors_without_content
        gc.collect()
