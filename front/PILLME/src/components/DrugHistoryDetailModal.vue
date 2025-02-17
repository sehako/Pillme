<template>
  <div>
    <h3 class="text-xl font-bold mb-4">{{ prescription.diseaseName }} 상세</h3>
    
    <p class="text-sm text-gray-500">{{ prescription.hospital }}</p>
    <p class="text-sm text-gray-500">{{ prescription.startDate }} ~ {{ prescription.endDate }}</p>

    <!-- TODO: 추가적인 상세 정보 표시 가능 -->
    <p class="mt-4 text-gray-700">이곳에 상세 정보를 표시할 수 있음.</p>
  </div>
</template>

<script setup>
import { defineProps } from "vue";

// Props (HistoryModal에서 선택된 처방전 정보를 받음)
defineProps({
  prescription: {
    type: Object,
    required: true
  }
});
</script>
