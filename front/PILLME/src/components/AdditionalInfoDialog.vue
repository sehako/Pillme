<template>
  <div v-if="ocrStore.showNextDialog" class="dialog-overlay">
    <div class="dialog-box">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-lg font-semibold">추가 정보 입력</h2>
        <button @click="ocrStore.closeDialog()" class="text-gray-500 hover:text-gray-700 text-xl">
          ✕
        </button>
      </div>
      <div class="mb-4">
        <label class="text-gray-700 font-semibold">병원 이름 (선택)</label>
        <input v-model="ocrStore.hospitalName" type="text" class="input-field" />
      </div>

      <div class="mb-4">
        <label class="text-gray-700 font-semibold">병명 (선택)</label>
        <input v-model="ocrStore.diseaseName" type="text" class="input-field" />
      </div>

      <div class="mb-4">
        <label class="text-gray-700 font-semibold">복용 기간 설정(필수)</label>
        <VueDatePicker
          v-model="ocrStore.dateRange"
          range
          :enable-time-picker="false"
          :format="'yyyy/MM/dd'"
          @update:model-value="calculateTotalDays"
          class="w-full"
        />
        <p class="text-gray-600 mt-2">총 복용 일수: {{ ocrStore.totalDays }}일</p>
        <p v-if="showErrorMessage" class="text-red-500 mt-2">
          ❗ 복용 기간은 필수 입력 사항입니다.
        </p>
      </div>

      <div class="button-group">
        <button @click="ocrStore.goBackToResultDialog" class="secondary-btn">이전</button>
        <button
          @click="ocrStore.openMedicationDialog"
          class="primary-btn"
          :disabled="!isDateRangeValid"
        >
          다음
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useOcrStore } from '../stores/ocrStore';
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';

const ocrStore = useOcrStore();
const showErrorMessage = ref(false);

// ✅ 📅 복용 기간 변경 시 총 복용 일수 계산
const calculateTotalDays = () => {
  if (ocrStore.dateRange && ocrStore.dateRange.length === 2) {
    const startDate = new Date(ocrStore.dateRange[0]);
    const endDate = new Date(ocrStore.dateRange[1]);

    // 날짜 차이 계산 (밀리초 → 일 변환)
    ocrStore.totalDays = Math.ceil((endDate - startDate) / (1000 * 60 * 60 * 24)) + 1;
    showErrorMessage.value = false;
  } else {
    ocrStore.totalDays = 0;
  }
};

// ✅ 사용자가 복용 기간을 입력하지 않았을 때 경고 메시지 표시
const validateDateRange = () => {
  if (!isDateRangeValid.value) {
    showErrorMessage.value = true;
  } else {
    ocrStore.openMedicationDialog(); // ✅ 정상 입력 시 다음 단계로 이동
  }
};

// ✅ 복용 기간이 입력되었는지 확인하는 computed 속성
const isDateRangeValid = computed(() => {
  return ocrStore.dateRange && ocrStore.dateRange.length === 2;
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

.input-field {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.button-group {
  display: flex;
  justify-content: space-between;
  margin-top: 16px;
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
</style>
