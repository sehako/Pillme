<template>
  <div class="w-full flex justify-between items-center bg-[#A3BFA5] px-4 h-16">
    <!-- ✅ PILLME 로고 (임시 텍스트) -->
    <span @click="gohome" class="text-xl font-bold text-[#385B38]">PILLME</span>

    <!-- ✅ 알림 아이콘 -->
    <BaseNotificationIcon />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import BaseNotificationIcon from "./BaseNotificationIcon.vue";

const router = useRouter();

// 더미 로그인 상태 변수 (실제 백엔드 연결 시에는 API 호출 등을 통해 상태를 확인)
const isLoggedIn = ref(false);

// onMounted 훅을 사용해 컴포넌트가 마운트될 때 로그인 상태를 확인 (예시: localStorage의 토큰 존재 여부)
onMounted(() => {
  // 예시로 localStorage에 'token'이 존재하면 로그인 상태로 간주
  isLoggedIn.value = !!localStorage.getItem('token');
});

// 로고 클릭 시 로그인 상태에 따라 이동 경로를 다르게 지정
const gohome = () => {
    router.push("/");
};
</script>

<style scoped>
/* ✅ 그림자 효과 추가 (필요 시 주석 해제) */
/* div {
  @apply shadow-md;
} */
</style>
