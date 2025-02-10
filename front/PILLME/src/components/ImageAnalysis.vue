<template>
  <div class="image-analysis-container flex flex-col w-full max-h-full items-center justify-center h-screen-custom bg-gray-100 p-4">
    <input 
      type="file" 
      @change="handleFileChange" 
      accept="image/*" 
      class="mb-4 p-2 border rounded w-full max-w-xs sm:max-w-sm md:max-w-md lg:max-w-lg xl:max-w-xl"
      v-if="!imagePreview"
    />

    <!-- 📌 이미지 미리보기 -->
    <div v-if="imagePreview" class="mb-4 text-center w-full">
      <h2 class="text-lg font-semibold">📷 분석할 이미지</h2>
      <img :src="imagePreview" alt="Preview" class="max-h-[400px] object-contain w-3/4 sm:w-3/4 md:w-3/4 lg:w-3/4 xl:w-3/4">
    </div>

    <button @click="analyzeImage" :disabled="!selectedFile || isLoading" class="analyze-btn w-full sm:w-auto">
      {{ isLoading ? '분석 중...' : '이미지 분석하기' }}
    </button>

    <div v-if="isLoading" class="text-center text-gray-600 mt-4">📡 분석 중입니다...</div>

    <div v-if="error" class="text-red-500 mt-4 p-3 bg-red-50 rounded text-center w-full sm:w-3/4 md:w-1/2">
      ❌ {{ error }}
    </div>

    <!-- 📌 분석 결과 다이얼로그 -->
    <div v-if="results.length > 0" class="absolute inset-0 flex items-center justify-center bg-black bg-opacity-50">
      <div class="bg-white p-6 rounded-lg max-w-sm sm:max-w-md md:max-w-lg w-full shadow-xl">
        <h2 class="text-lg font-semibold mb-2 text-center">📄 분석 결과</h2>

        <!-- ✅ 분석된 약 리스트 -->
        <ul class="max-h-60 overflow-y-auto border rounded p-2">
          <li 
            v-for="(result, index) in results" 
            :key="index" 
            class="text-gray-700 py-2 flex justify-between items-center border-b hover:bg-gray-100 transition-all"
          >
            <span class="ml-2">{{ result.matched_drug }}</span>
            <button @click="removeDrug(index)" class="delete-btn" title="삭제">
              🗑
            </button>
          </li>
        </ul>

        <!-- ✅ 직접 추가 -->
        <div class="mt-4 flex flex-col sm:flex-row gap-2">
          <input 
            v-model="newDrug" 
            type="text" 
            placeholder="추가할 약 이름 입력" 
            class="border p-2 rounded w-full sm:w-3/4 md:w-2/3 focus:outline-none focus:ring-2 focus:ring-blue-400"
          />
          <button @click="addDrug" class="add-btn w-full sm:w-auto">
            ➕ 추가
          </button>
        </div>

        <div class="flex justify-center mt-4">
          <button @click="openNextDialog" class="next-btn w-full sm:w-auto">➡ 다음</button>
        </div>
      </div>
    </div>

    <!-- 📌 추가 정보 입력 다이얼로그 -->
    <div v-if="showNextDialog" class="absolute inset-0 flex items-center justify-center bg-black bg-opacity-50">
      <div class="bg-white p-6 rounded-lg max-w-sm sm:max-w-md md:max-w-lg w-full shadow-xl">
        <h2 class="text-lg font-semibold mb-2 text-center">📄 추가 정보 입력</h2>

        <div class="mb-4">
          <label class="text-gray-700 font-semibold">🏥 병원 이름 (선택)</label>
          <input v-model="hospitalName" type="text" class="border p-2 rounded w-full focus:ring-2 focus:ring-blue-400">
        </div>

        <div class="mb-4">
          <label class="text-gray-700 font-semibold">💊 병명 (선택)</label>
          <input v-model="diseaseName" type="text" class="border p-2 rounded w-full focus:ring-2 focus:ring-blue-400">
        </div>

        <div class="mb-4">
          <label class="text-gray-700 font-semibold">📅 복용 기간 설정</label>
          <VueDatePicker 
            v-model="dateRange" 
            range 
            :enable-time-picker="false"
            :format="'yyyy/MM/dd'" 
            @update:model-value="calculateTotalDays"
            class="w-full"
          />
          <p class="text-gray-600 mt-2">총 복용 일수: {{ totalDays }}일</p>
        </div>

        <div class="flex justify-between mt-4">
          <button @click="closeAllDialogs" class="confirm-btn w-full sm:w-auto">✔ 완료</button>
          <button @click="closeNextAndPreviousDialog" class="confirm-btn w-full sm:w-auto">⬅ 이전</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';

const imagePreview = ref(null);
const isLoading = ref(false);
const error = ref(null);
const results = ref([]);
const selectedFile = ref(null);
const newDrug = ref("");

const showNextDialog = ref(false);
const hospitalName = ref("");
const diseaseName = ref("");
const dateRange = ref([]);
const totalDays = ref(0);

const handleFileChange = (event) => {
  selectedFile.value = event.target.files[0];
  const reader = new FileReader();
  reader.onload = (e) => {
    imagePreview.value = e.target.result;
  };
  reader.readAsDataURL(selectedFile.value);
};

const analyzeImage = async () => {
  if (!selectedFile.value) {
    error.value = "분석할 이미지가 없습니다.";
    return;
  }
  isLoading.value = true;
  try {
    const formData = new FormData();
    formData.append("file", selectedFile.value);
    const response = await fetch("http://localhost:8000/analyze-image/", {
      method: "POST",
      body: formData,
    });
    if (!response.ok) throw new Error("이미지 분석 실패");
    results.value = await response.json();
  } catch (err) {
    error.value = err.message;
  } finally {
    isLoading.value = false;
  }
};

const openNextDialog = () => {
  if (results.value.length > 0) {
    showNextDialog.value = true;
  }
};

const closeAllDialogs = () => {
  showNextDialog.value = false;
  results.value = [];
};

const closeNextAndPreviousDialog = () => {
  showNextDialog.value = false;
};
</script>
