from difflib import get_close_matches
import re
import gc

class MatchingService:
    def __init__(self, word2vec_service, 약품명_매핑):
        self.word2vec_service = word2vec_service
        self.약품명_매핑 = 약품명_매핑

    def get_similar_word(self, ocr_word, vocab):
        similar_words = get_close_matches(ocr_word, vocab, n=3, cutoff=0.72)
        return similar_words[0] if similar_words else None

    def get_best_match(self, ocr_word):
        similar_with_content = self.get_similar_word(
            ocr_word, 
            self.word2vec_service.vocab_with_content
        )
        similar_without_content = self.get_similar_word(
            ocr_word, 
            self.word2vec_service.vocab_without_content
        )

        best_match_with_content = None
        best_match_without_content = None

        if similar_with_content and similar_with_content in self.word2vec_service.word2vec_model_with_content.wv:
            vector = self.word2vec_service.word2vec_model_with_content.wv[similar_with_content].reshape(1, -1)
            D, I = self.word2vec_service.faiss_index_with_content.search(vector, 5)
            best_match_with_content = self.word2vec_service.vocab_with_content[I[0][0]]
            
            del vector, D, I
            gc.collect()

        if similar_without_content and similar_without_content in self.word2vec_service.word2vec_model_without_content.wv:
            vector = self.word2vec_service.word2vec_model_without_content.wv[similar_without_content].reshape(1, -1)
            D, I = self.word2vec_service.faiss_index_without_content.search(vector, 1)
            best_match_without_content = self.word2vec_service.vocab_without_content[I[0][0]]
            
            del vector, D, I
            gc.collect()

        if best_match_with_content and best_match_without_content:
            return best_match_with_content if len(best_match_with_content) >= len(best_match_without_content) else best_match_without_content
        elif best_match_with_content:
            return best_match_with_content
        elif best_match_without_content:
            return best_match_without_content
        else:
            return None

    def process_ocr_results(self, ocr_data):
        recognized_texts = [(data["text"], data["confidence"]) for data in ocr_data]
        matched_drugs = {}

        for word, confidence in recognized_texts:
            if re.match(r"^\d", word):
                continue

            best_match = self.get_best_match(word)
            if best_match:
                original_drug_name = self.약품명_매핑.get(best_match, best_match)
                matched_drugs[word] = original_drug_name

        del recognized_texts
        gc.collect()

        return matched_drugs