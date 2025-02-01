<template>
  <button @click="goBack" class="flex items-center space-x-2 focus:outline-none">
    <img src="../assets/back.svg" alt="뒤로 가기" class="w-6 h-6">
    <span class="text-sm font-medium">뒤로가기</span>
  </button>
</template>

<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

const goBack = () => {
  router.back(); // 이전 페이지로 이동
};
</script>
