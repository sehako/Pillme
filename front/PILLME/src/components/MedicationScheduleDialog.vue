<template>
  <div v-if="ocrStore.showMedicationDialog" class="dialog-overlay">
    <div class="dialog-box">
      <button class="close-btn" @click="ocrStore.showMedicationDialog = false">✖</button>
      <h2 class="text-lg font-semibold text-center text-pink-500">⏰ 복약 시간 설정</h2>

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
          <input type="checkbox" v-model="overallCheck.breakfast" @change="toggleAll('breakfast', $event.target.checked)" />
          <input type="checkbox" v-model="overallCheck.lunch" @change="toggleAll('lunch', $event.target.checked)" />
          <input type="checkbox" v-model="overallCheck.dinner" @change="toggleAll('dinner', $event.target.checked)" />
          <input type="checkbox" v-model="overallCheck.bedtime" @change="toggleAll('bedtime', $event.target.checked)" />
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
        <button @click="ocrStore.closeDialog" class="primary-btn">완료</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useOcrStore } from '../stores/ocrStore';
import { reactive, onMounted, computed } from 'vue';

const ocrStore = useOcrStore();

// ✅ 개별 약 정보 (computed를 사용하여 `ocrStore.results` 기반으로 생성)
const medications = computed(() => 
  ocrStore.results.map(med => ({
    matched_drug: med.matched_drug,
    breakfast: med.breakfast ?? false,
    lunch: med.lunch ?? false,
    dinner: med.dinner ?? false,
    bedtime: med.bedtime ?? false
  }))
);

// ✅ 컴포넌트가 마운트될 때 한 번만 실행하여 undefined 속성 방지
onMounted(() => {
  ocrStore.results = medications.value;
  updateOverallCheck(); // ✅ 전체 선택 체크박스 상태 업데이트
});


// ✅ 전체 체크박스 상태 관리
const overallCheck = reactive({
  breakfast: false,
  lunch: false,
  dinner: false,
  bedtime: false
});

// ✅ 전체 선택 토글
const toggleAll = (time, checked) => {
  ocrStore.results.forEach(med => {
    med[time] = checked;
  });
  updateOverallCheck();
};

// ✅ 개별 선택 시 전체 선택 체크박스 동기화
const updateOverallCheck = () => {
  overallCheck.breakfast = ocrStore.results.every(med => med.breakfast);
  overallCheck.lunch = ocrStore.results.every(med => med.lunch);
  overallCheck.dinner = ocrStore.results.every(med => med.dinner);
  overallCheck.bedtime = ocrStore.results.every(med => med.bedtime);
};
</script>

<style scoped>
@import '../styles/dialog.css';
</style>
