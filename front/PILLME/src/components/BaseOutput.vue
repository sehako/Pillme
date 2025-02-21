<template>
    <div class="flex items-center border border-gray-300 rounded-lg px-3 py-2 shadow-sm bg-gray-100">
      <!-- 아이콘이 있을 경우에만 이미지 표시 -->
      <img v-if="icon" :src="icon" alt="output-icon" class="w-5 h-5 mr-2" />
      
      <!-- 출력 필드 -->
      <div class="w-full text-gray-700 outline-none">
        {{ modelValue }}
      </div>
    </div>
  </template>
  
  <script setup>
  defineProps({
    modelValue: String, // 부모 컴포넌트에서 값을 내려받음
    icon: String, // 아이콘 경로
  });
  </script>
  