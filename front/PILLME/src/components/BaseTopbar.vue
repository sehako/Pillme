<template>
  <div class="w-full flex justify-between items-center bg-[#A7C7A3] px-4 h-12 shadow-md">
    <!-- ✅ PILLME 로고 (임시 텍스트) -->
    <span class="text-lg font-bold text-[#385B38]">PILLME</span>

    <!-- ✅ 알림 아이콘 -->
    <BaseNotificationIcon />
  </div>
</template>

<script setup>
import BaseNotificationIcon from "./BaseNotificationIcon.vue";

const toggleMenu = () => {
  console.log("메뉴 열기");
};
</script>

<style scoped>
/* ✅ 그림자 효과 추가 */
div {
  @apply shadow-md;
}
</style>
