<template>
  <div class="image-analysis-container flex flex-col w-full max-h-full items-center justify-center h-screen-custom bg-gray-100 p-4">
    
    <!-- 📌 파일 업로드 (OCR 분석 전)
    <input
      type="file"
      @change="handleFileChange"
      accept="image/*"
      class="mb-4 p-2 border rounded w-full max-w-xs sm:max-w-sm md:max-w-md lg:max-w-lg xl:max-w-xl"
      v-if="!imagePreview"
      :disabled="ocrStore.isLoading"
    /> -->

    <!-- 📌 이미지 미리보기 -->
    <div v-if="imagePreview" class="mb-4 text-center w-full flex flex-col items-center">
      <h2 class="text-lg font-semibold">📷 분석할 이미지</h2>
      <img
        :src="imagePreview"
        alt="Preview"
        class="max-h-[400px] object-contain w-3/4"
      />

      <!-- ✅ 분석 결과 다시 보기 버튼 -->
      <button
        v-if="ocrStore.results.length > 0 && !ocrStore.isLoading"
        @click="ocrStore.showResultsDialog = true"
        class="mt-2 p-2 border rounded bg-blue-500 text-white hover:bg-blue-600 transition"
      >
        📄 분석 결과 다시 보기
      </button>
    </div>

    <!-- 📌 OCR 분석 중 로딩 메시지 -->
    <div v-if="ocrStore.isLoading" class="text-center text-gray-600 mt-4">📡 분석 중입니다...</div>

    <!-- 📌 오류 메시지 -->
    <div v-if="ocrStore.error" class="text-red-500 mt-4 p-3 bg-red-50 rounded text-center w-full sm:w-3/4 md:w-1/2">
      ❌ {{ ocrStore.error }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useOcrStore } from '../stores/ocrStore.js';

const ocrStore = useOcrStore();
const route = useRoute();
const selectedFile = ref(null);
const imagePreview = ref(null);
const error = ref(null);

// ✅ 파일 선택 이벤트 (파일 업로드 시 자동 OCR 실행)
const handleFileChange = (event) => {
  if (ocrStore.isLoading) return; // ✅ OCR 분석 중이면 중복 요청 방지

  const file = event.target.files[0];
  if (!file) return;

  // ✅ input[type="file"]을 리셋해서 동일한 파일을 다시 선택해도 `change` 이벤트 발생하도록 설정
  event.target.value = '';

  // ✅ OCR 상태 초기화 (기존 데이터 삭제)
  ocrStore.resetOcrState();

  // ✅ 기존 파일 및 미리보기 강제 초기화 → Vue의 변경 감지
  selectedFile.value = null;
  imagePreview.value = null;

  setTimeout(() => {
    selectedFile.value = file;

    const reader = new FileReader();
    reader.onload = (e) => {
      imagePreview.value = e.target.result; // ✅ 미리보기 업데이트
      nextTick(() => analyzeImage()); // ✅ OCR 분석 실행
    };
    reader.readAsDataURL(file);
  }, 50); // ✅ Vue가 변경을 감지할 수 있도록 짧은 지연 추가
};

// ✅ OCR 분석 실행
const analyzeImage = async () => {
  if (!selectedFile.value || ocrStore.isLoading) return; // ✅ 중복 요청 방지

  ocrStore.error = null;

  ocrStore.startLoading(); // ✅ 로딩 시작

  try {
    const formData = new FormData();
    formData.append('file', selectedFile.value);

    const response = await fetch('https://i12a606.p.ssafy.io/prescription', {
      method: 'POST',
      body: formData,
    });

    if (!response.ok) throw new Error('이미지 분석 실패');
    const data = await response.json();

    ocrStore.setResults(data); // ✅ OCR 분석 결과 저장
    ocrStore.showResultsDialog = true; // ✅ OCR 분석 완료 후 다이얼로그 자동 표시
  } catch (err) {
    ocrStore.error = err.message;
  } finally {
    ocrStore.stopLoading(); // ✅ 로딩 해제
  }
};

// ✅ 🚀 페이지 로드 시 query에서 이미지 자동 로드 & OCR 분석
onMounted(() => {
  if(ocrStore.isLoading) return;

  if (route.query.image) {
    try {
      const base64Data = decodeURIComponent(route.query.image);

      // ✅ Base64 데이터 검증
      if (!base64Data.startsWith('data:image/')) {
        throw new Error('잘못된 이미지 형식입니다.');
      }

      imagePreview.value = base64Data;

      // ✅ selectedFile을 Blob → File로 변환
      const byteCharacters = atob(base64Data.split(',')[1]);
      const byteNumbers = new Array(byteCharacters.length);
      for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
      }
      const byteArray = new Uint8Array(byteNumbers);
      const blob = new Blob([byteArray], { type: 'image/png' });
      selectedFile.value = new File([blob], route.query.filename || 'uploaded-image.png', { type: 'image/png' });

      analyzeImage(); // ✅ 자동 OCR 분석 실행
    } catch (err) {
      console.error('❌ Base64 데이터 변환 오류:', err);
      error.value = err.message;
    }
  }
});
</script>