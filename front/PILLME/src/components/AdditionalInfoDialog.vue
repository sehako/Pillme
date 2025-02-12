<template>
  <div v-if="ocrStore.showNextDialog" class="dialog-overlay">
    <div class="dialog-box">
      <button class="close-btn" @click="ocrStore.showNextDialog = false">✖</button>
      <h2 class="text-lg font-semibold mb-2 text-center">📄 추가 정보 입력</h2>

      <div class="mb-4">
        <label class="text-gray-700 font-semibold">🏥 병원 이름</label>
        <input v-model="ocrStore.hospitalName" type="text" class="input-field" placeholder="병원 이름 입력" />
      </div>

      <div class="mb-4">
        <label class="text-gray-700 font-semibold">💊 병명</label>
        <input v-model="ocrStore.diseaseName" type="text" class="input-field" placeholder="병명 입력" />
      </div>

      <div class="button-group">
        <button @click="ocrStore.openMedicationDialog" class="primary-btn">➡ 다음</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useOcrStore } from '../stores/ocrStore';
const ocrStore = useOcrStore();
</script>

<style scoped>
@import '../styles/dialog.css';
</style>
