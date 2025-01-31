<template>
  <div @click="gohome" class="w-full flex justify-between items-center bg-[#A7C7A3] px-4 h-12">
    <!-- ✅ PILLME 로고 (임시 텍스트) -->
    <span class="text-lg font-bold text-[#385B38]">PILLME</span>

    <!-- ✅ 알림 아이콘 -->
    <BaseNotificationIcon />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
// 라우트는 읽기전용정보 라우터는 실제기능
import BaseNotificationIcon from "./BaseNotificationIcon.vue";

const router = useRouter();
const gohome = () => {
  router.push("/start");
};

</script>

<style scoped>
/* ✅ 그림자 효과 추가 */
/* div {
  @apply shadow-md;
} */
</style>
