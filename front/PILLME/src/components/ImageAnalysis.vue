<template>
  <div
    class="image-analysis-container flex flex-col w-full max-h-full items-center justify-center h-screen-custom bg-gray-100 p-4"
  >
    <input
      type="file"
      @change="handleFileChange"
      accept="image/*"
      class="mb-4 p-2 border rounded w-full max-w-xs sm:max-w-sm md:max-w-md lg:max-w-lg xl:max-w-xl"
      v-if="!imagePreview"
    />

    <!-- 📌 이미지 미리보기 -->
    <div v-if="imagePreview" class="mb-4 text-center w-full flex flex-col items-center">
      <h2 class="text-lg font-semibold">📷 분석할 이미지</h2>
      <img
        :src="imagePreview"
        alt="Preview"
        class="max-h-[400px] object-contain w-3/4 sm:w-3/4 md:w-3/4 lg:w-3/4 xl:w-3/4"
      />

      <!-- ✅ 분석 결과 다시 보기 버튼 -->
      <button
        v-if="results.length > 0"
        @click="showResultsDialog = true"
        class="mt-2 p-2 border rounded bg-blue-500 text-white hover:bg-blue-600 transition"
      >
        📄 분석 결과 다시 보기
      </button>
    </div>

    <button
      @click="analyzeImage"
      :disabled="!selectedFile || isLoading"
      class="analyze-btn w-full sm:w-auto"
    >
      {{ isLoading ? '분석 중...' : '' }}
    </button>

    <div v-if="isLoading" class="text-center text-gray-600 mt-4">📡 분석 중입니다...</div>

    <div
      v-if="error"
      class="text-red-500 mt-4 p-3 bg-red-50 rounded text-center w-full sm:w-3/4 md:w-1/2"
    >
      ❌ {{ error }}
    </div>

    <!-- 📌 분석 결과 다이얼로그 -->
    <div v-if="showResultsDialog" class="dialog-overlay">
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
            <button @click="removeDrug(index)" class="delete-btn" title="삭제">🗑</button>
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
          <button @click="addDrug" class="add-btn w-full sm:w-auto">➕ 추가</button>
        </div>

        <div class="flex justify-between mt-4">
          <button @click="openNextDialog" class="next-btn w-full sm:w-auto">➡ 다음</button>
        </div>
      </div>
    </div>

    <!-- 📌 추가 정보 입력 다이얼로그 -->
    <div v-if="showNextDialog" class="dialog-overlay">
      <div class="bg-white p-6 rounded-lg max-w-sm sm:max-w-md md:max-w-lg w-full shadow-xl">
        <h2 class="text-lg font-semibold mb-2 text-center">📄 추가 정보 입력</h2>

        <div class="mb-4">
          <label class="text-gray-700 font-semibold">🏥 병원 이름 (선택)</label>
          <input
            v-model="hospitalName"
            type="text"
            class="border p-2 rounded w-full focus:ring-2 focus:ring-blue-400"
          />
        </div>

        <div class="mb-4">
          <label class="text-gray-700 font-semibold">💊 병명 (선택)</label>
          <input
            v-model="diseaseName"
            type="text"
            class="border p-2 rounded w-full focus:ring-2 focus:ring-blue-400"
          />
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

        <div class="button-group">
          <button @click="closeNextAndPreviousDialog" class="secondary-btn">⬅ 이전</button>
          <button @click="openMedicationDialog" class="primary-btn">➡ 다음</button>
        </div>
      </div>
    </div>

    <!-- 📌 복약 시간 설정 다이얼로그 -->
    <div v-if="showMedicationDialog" class="dialog-overlay">
    <div class="dialog-box">
      <h2 class="text-lg font-semibold text-center text-pink-500">⏰ 복약 시간 설정</h2>

      <!-- ✅ 스크롤 가능하도록 감싸는 div 추가 -->
      <div class="medication-container">
        <!-- ✅ 복약 시간 헤더 (고정) -->
        <div class="medication-header">
          <span></span> <!-- 빈 칸 -->
          <span>아침</span>
          <span>점심</span>
          <span>저녁</span>
          <span>자기 전</span>
        </div>

        <!-- ✅ 전체 체크 -->
        <div class="medication-row">
          <span class="med-name">전체</span>
          <input type="checkbox" @change="toggleAll('breakfast', $event.target.checked)" />
          <input type="checkbox" @change="toggleAll('lunch', $event.target.checked)" />
          <input type="checkbox" @change="toggleAll('dinner', $event.target.checked)" />
          <input type="checkbox" @change="toggleAll('bedtime', $event.target.checked)" />
        </div>

        <!-- ✅ 약 목록을 감싸는 div 추가 (스크롤 적용) -->
        <div class="medication-list">
          <div v-for="(med, index) in medications" :key="index" class="medication-row">
            <span class="med-name">{{ med.name }}</span>
            <input type="checkbox" v-model="med.times.breakfast" />
            <input type="checkbox" v-model="med.times.lunch" />
            <input type="checkbox" v-model="med.times.dinner" />
            <input type="checkbox" v-model="med.times.bedtime" />
          </div>
        </div>
      </div>

      <div class="button-group">
        <button @click="closeAllDialogs" class="secondary-btn">닫기</button>
        <button @click="confirmMedicationSchedule" class="primary-btn">확인</button>
      </div>
    </div>
  </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';

const route = useRoute();
const imagePreview = ref(null);
const isLoading = ref(false);
const error = ref(null);
const results = ref([]);
const selectedFile = ref(null);
const newDrug = ref('');

const showResultsDialog = ref(false);
const showNextDialog = ref(false);
const showMedicationDialog = ref(false);
const hospitalName = ref('');
const diseaseName = ref('');
const dateRange = ref([]);
const totalDays = ref(0);
const medications = ref([]);

// ✅ 전체 체크 기능
const toggleAll = (time, checked) => {
  medications.value.forEach((med) => {
    med.times[time] = checked;
  });
};

// ✅ 📅 복용 기간 변경 시 총 복용 일수 계산
const calculateTotalDays = () => {
  if (dateRange.value && dateRange.value.length === 2) {
    const startDate = new Date(dateRange.value[0]);
    const endDate = new Date(dateRange.value[1]);

    // 날짜 차이 계산 (밀리초 → 일 변환)
    totalDays.value = Math.ceil((endDate - startDate) / (1000 * 60 * 60 * 24)) + 1;
  } else {
    totalDays.value = 0;
  }
};

const handleFileChange = (event) => {
  selectedFile.value = event.target.files[0];
  if (selectedFile.value) {
    const reader = new FileReader();
    reader.onload = (e) => {
      imagePreview.value = e.target.result;
      analyzeImage(); // 파일이 선택되면 자동으로 분석 진행
    };
    reader.readAsDataURL(selectedFile.value);
  }
};

const analyzeImage = async () => {
  if (!selectedFile.value) {
    error.value = '❌ 분석할 이미지가 없습니다.';
    return;
  }
  isLoading.value = true;
  try {
    const formData = new FormData();
    formData.append('file', selectedFile.value);
    const response = await fetch('http://localhost:8000/analyze-image/', {
      method: 'POST',
      body: formData,
    });
    if (!response.ok) throw new Error('이미지 분석 실패');
    results.value = await response.json();

    medications.value = results.value.map((drug) => ({
      name: drug.matched_drug,
      times: { breakfast: false, lunch: false, dinner: false, bedtime: false },
    }));

    showResultsDialog.value = true;
  } catch (err) {
    error.value = err.message;
  } finally {
    isLoading.value = false;
  }
};

const removeDrug = (index) => {
  results.value.splice(index, 1);
};

const addDrug = () => {
  if (newDrug.value.trim() !== '') {
    results.value.push({matched_drug: newDrug.value.trim()});
    medications.value.push({
      name: newDrug.value.trim(),
      times: { breakfast: false, lunch: false, dinner: false, bedtime: false },
    });
    newDrug.value = '';
  }
};

const openMedicationDialog = () => {
  showNextDialog.value = false;
  showMedicationDialog.value = true;
};

const closeMedicationDialog = () => {
  showMedicationDialog.value = false;
};

const resetImage = () => {
  imagePreview.value = null;
  selectedFile.value = null;
  results.value = [];
  showResultsDialog.value = false;
};

const openNextDialog = () => {
  if (results.value.length > 0) {
    showNextDialog.value = true;
  }
};
const confirmMedicationSchedule = () => {
  alert('복약 시간이 설정되었습니다!');
  closeAllDialogs();
};

const closeAllDialogs = () => {
  showNextDialog.value = false;
  showResultsDialog.value = false;
  showMedicationDialog.value = false;
};

const selectAll = () => {
  medications.value.forEach((med) => {
    Object.keys(med.times).forEach((time) => {
      med.times[time] = true;
    });
  });
};

const deselectAll = () => {
  medications.value.forEach((med) => {
    Object.keys(med.times).forEach((time) => {
      med.times[time] = false;
    });
  });
};

const closeNextAndPreviousDialog = () => {
  showNextDialog.value = false;
};

// ✅ BaseNavbar에서 전달된 이미지 자동 로드 및 분석 실행
onMounted(() => {
  if (route.query.image) {
    try {
      const base64Data = decodeURIComponent(route.query.image);

      // ✅ Base64 데이터인지 검증
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
      selectedFile.value = new File([blob], route.query.filename || 'uploaded-image.png', {
        type: 'image/png',
      });

      analyzeImage();
    } catch (err) {
      console.error('❌ Base64 데이터 변환 오류:', err);
      error.value = err.message;
    }
  }
});
</script>

<style scoped>
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.5);
}

.dialog-box {
  background: white;
  padding: 24px;
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  width: 100%;
  text-align: center;
}

.medication-container {
  display: flex;
  flex-direction: column;
  max-height: 400px;
  overflow: hidden;
  width: 100%;
  padding: 10px;
}

.medication-list {
  max-height: 300px;
  overflow-y: auto;
  padding-right: 8px;
}

.medication-header {
  display: grid;
  grid-template-columns: 1.5fr repeat(4, 1fr); /* 약 이름 열을 더 넓게 설정 */
  font-weight: bold;
  color: #ff4081;
  text-align: center;
  padding-bottom: 10px;
  align-items: center; /* 중앙 정렬 */
}

.medication-row {
  display: grid;
  grid-template-columns: 1.5fr repeat(4, 1fr); /* 약 이름 열을 더 넓게 설정 */
  align-items: center; /* 모든 요소를 수직 중앙 정렬 */
  text-align: center;
  padding: 10px 0;
  border-bottom: 1px solid #ddd;
}

/* ✅ 개별 행 스타일 */
.med-name {
  font-weight: bold;
  text-align: left;
  padding-left: 10px;
  display: flex;
  align-items: center; /* 약 이름이 여러 줄이 되더라도 중앙 정렬 */
}

.primary-btn {
  background-color: #f48fb1;
  color: white;
  padding: 10px 16px;
  border-radius: 8px;
  font-size: 16px;
  width: 100px;
}

.secondary-btn {
  background-color: #eeeeee;
  padding: 10px 16px;
  border-radius: 8px;
  font-size: 16px;
  width: 100px;
}

input[type="checkbox"] {
  transform: scale(1.2);
  margin: auto;
}
</style>
