<template>
  <VCalendar 
    v-model="selectedDate"
    :attributes="calendarAttributes"
    class="w-full rounded-lg shadow-md border border-gray-200 bg-white"
  />
</template>

<script setup>
import { ref, computed } from "vue";
// import VCalendar from "v-calendar"; <이미 전역으로 등록되어 임포트 불필요

// ✅ 선택한 날짜 (외부에서 v-model을 사용할 경우 확장 가능)
const selectedDate = ref(new Date());

// ✅ 기본 달력 표시 (데이터 없을 때도 달력이 보이도록)
const calendarAttributes = computed(() => {
  return []; // 비어 있어도 달력이 기본적으로 렌더링됨
});
</script>
