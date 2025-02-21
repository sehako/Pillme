<template>
  <div v-if="ocrStore.showMedicationDialog" class="dialog-overlay">
    <div class="dialog-box">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-lg font-semibold">복약 시간 설정</h2>
        <button @click="ocrStore.closeDialog()" class="text-gray-500 hover:text-gray-700 text-xl">
          ✕
        </button>
      </div>
      <div class="medication-container">
        <div class="medication-header">
          <span></span>
          <span>아침</span>
          <span>점심</span>
          <span>저녁</span>
          <span>자기 전</span>
        </div>

        <!-- ✅ 전체 선택 체크박스 -->
        <div class="medication-row">
          <span class="med-name">전체</span>
          <input
            type="checkbox"
            v-model="overallCheck.breakfast"
            @change="toggleAll('breakfast', $event.target.checked)"
          />
          <input
            type="checkbox"
            v-model="overallCheck.lunch"
            @change="toggleAll('lunch', $event.target.checked)"
          />
          <input
            type="checkbox"
            v-model="overallCheck.dinner"
            @change="toggleAll('dinner', $event.target.checked)"
          />
          <input
            type="checkbox"
            v-model="overallCheck.bedtime"
            @change="toggleAll('bedtime', $event.target.checked)"
          />
        </div>

        <!-- ✅ 약 목록 -->
        <div class="medication-list">
          <div v-for="(med, index) in ocrStore.results" :key="index" class="medication-row">
            <span class="med-name">{{ med.matched_drug }}</span>
            <input type="checkbox" v-model="med.breakfast" @change="updateOverallCheck" />
            <input type="checkbox" v-model="med.lunch" @change="updateOverallCheck" />
            <input type="checkbox" v-model="med.dinner" @change="updateOverallCheck" />
            <input type="checkbox" v-model="med.bedtime" @change="updateOverallCheck" />
          </div>
        </div>
      </div>

      <div class="button-group">
        <button @click="ocrStore.goBackToNextDialog" class="secondary-btn">이전</button>
        <button @click="saveOcrData" class="primary-btn" :disabled="ocrStore.isLoading">
          {{ ocrStore.isLoading ? '저장 중...' : '저장' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useOcrStore } from '../stores/ocrStore';
import { reactive, onMounted, watch } from 'vue';

const ocrStore = useOcrStore();

// ✅ `dependentId`를 받을 수 있도록 props 추가
const props = defineProps({
  dependentId: {
    type: Number,
    required: null,
  },
});

// ✅ OCR 데이터 저장 함수

const saveOcrData = async () => {
  try {
    // ✅ `dependentId`가 없으면 `null`을 설정
    const dependentIdToSend = props.dependentId ?? null;

    console.log(`📤 [DEBUG] OCR 데이터 저장 시작 - dependentId: ${dependentIdToSend}`);

    // ✅ `dependentIdToSend`를 `ocrStore`에 전달
    await ocrStore.saveOcrDataToDB(dependentIdToSend);
  } catch (error) {
    console.error('❌ [ERROR] saveOcrData() 실행 중 오류 발생:', error);
  }
};

// ✅ 전체 체크박스 상태 저장
const overallCheck = reactive({
  breakfast: false,
  lunch: false,
  dinner: false,
  bedtime: false,
});

// ✅ 데이터 초기화 함수
const initializeMedicationData = () => {
  if (!ocrStore.results || !Array.isArray(ocrStore.results)) {
    ocrStore.results = [];
  }

  ocrStore.results = ocrStore.results.map((med) => ({
    matched_drug: med.matched_drug || '',
    breakfast: med.breakfast ?? false,
    lunch: med.lunch ?? false,
    dinner: med.dinner ?? false,
    bedtime: med.bedtime ?? false,
  }));

  updateOverallCheck(); // 전체 선택 체크박스 상태 업데이트
};

// ✅ 전체 선택 토글
const toggleAll = (time, checked) => {
  ocrStore.results.forEach((med) => {
    med[time] = checked;
  });
  updateOverallCheck();
};

// ✅ 개별 체크 시 전체 체크박스 상태 업데이트
const updateOverallCheck = () => {
  overallCheck.breakfast = ocrStore.results.every((med) => med.breakfast);
  overallCheck.lunch = ocrStore.results.every((med) => med.lunch);
  overallCheck.dinner = ocrStore.results.every((med) => med.dinner);
  overallCheck.bedtime = ocrStore.results.every((med) => med.bedtime);
};

// ✅ 컴포넌트 마운트 시 데이터 초기화 실행
onMounted(() => {
  initializeMedicationData();
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
  z-index: 9999;
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

/* ✅ 반응형 버튼 그룹 */
.button-group {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 16px;
}

.medication-list {
  max-height: 300px;
  overflow-y: auto;
  padding-right: 8px;
}

.medication-header {
  display: grid;
  grid-template-columns: 1.5fr repeat(4, 1fr);
  font-weight: bold;
  color: #ff4081;
  text-align: center;
  padding-top: 10px;
  padding-bottom: 5px;
  align-items: center;
}

.medication-row {
  display: grid;
  grid-template-columns: 1.5fr repeat(4, 1fr);
  align-items: center;
  text-align: center;
  padding: 10px 0;
  border-bottom: 1px solid #ddd;
}

.med-name {
  font-weight: bold;
  text-align: left;
  padding-left: 10px;
  display: flex;
  align-items: center;
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

.medication-row input[type='checkbox'] {
  transform: scale(1.3);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: auto;
}

.close-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #aaa;
}

.close-btn:hover {
  color: #999;
}
</style>
