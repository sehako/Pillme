<template>
  <VCalendar 
    v-model="selectedDate"
    class="w-full rounded-lg shadow-md"
  />
</template>

<script setup>
import { ref } from "vue";
import VCalendar from "v-calendar";

// ✅ 선택한 날짜 (외부에서 v-model을 사용할 경우 확장 가능)
const selectedDate = ref(new Date());

defineProps({
  modelValue: Date, // 부모 컴포넌트에서 v-model을 사용할 경우
});
</script>
