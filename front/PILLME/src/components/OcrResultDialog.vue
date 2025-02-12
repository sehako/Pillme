<template>
  <div v-if="ocrStore.showResultsDialog" class="dialog-overlay">
    <div class="dialog-box">
      <button class="close-btn" @click="ocrStore.showResultsDialog = false">✖</button>
      <h2 class="text-lg font-semibold mb-2 text-center">📄 분석 결과</h2>

      <!-- 분석된 약 리스트 -->
      <ul class="medication-list">
        <li v-for="(result, index) in ocrStore.results" :key="index" class="medication-row">
          <span class="med-name">{{ result.matched_drug }}</span>
          <button @click="removeDrug(index)" class="delete-btn">🗑</button>
        </li>
      </ul>

      <div class="button-group">
        <button @click="ocrStore.openNextDialog" class="primary-btn">➡ 다음</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useOcrStore } from '../stores/ocrStore';
const ocrStore = useOcrStore();

const removeDrug = (index) => {
  ocrStore.results.splice(index, 1);
};
</script>

<style scoped>
@import '../styles/dialog.css';
</style>
