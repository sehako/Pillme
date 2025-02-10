<template>
  <div class="flex flex-col items-center justify-center min-h-screen px-4">
    <BaseLogo :src="logoSrc" size="md" />
    <BaseText textBefore="복약 관리의 새로운 방법," highlightText="PILLME" />

    <form class="w-full max-w-xs space-y-4 md:max-w-sm" @submit.prevent="handleLogin">
      <BaseInput v-model="email" type="email" placeholder="이메일 입력" :icon="emailIcon" />
      <BaseInput v-model="password" type="password" placeholder="비밀번호 입력" :icon="passwordIcon" />
      
      <BaseButton textColor="text-white" size="md" :disabled="isLoading">
        {{ isLoading ? '로그인 중...' : '로그인' }}
      </BaseButton>
    </form>

    <p class="mt-6 back-login md:mt-8">
      <a href="/loginselection" class="text-[#4E7351] hover:underline">로그인 페이지로 돌아가기</a>
    </p>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { login } from "../api/auth.js";
import axios from "axios";
import BaseButton from "../components/BaseButton.vue";
import BaseInput from "../components/BaseInput.vue";
import BaseLogo from "../components/BaseLogo.vue";
import BaseText from "../components/BaseText.vue";
import logoSrc from "../assets/logi_nofont.svg";
import emailIcon from "../assets/email.png";
import passwordIcon from "../assets/key.png";

const email = ref("");
const password = ref("");
const isLoading = ref(false);

const handleLogin = async () => {
  console.log('handleLogin 함수 실행됨'); 
  
  if (!email.value || !password.value) {
    alert("이메일과 비밀번호를 입력해주세요.");
    return;
  }

  isLoading.value = true;
  try {
    const response = await login({
      email: email.value,
      password: password.value,
    });
    
    const { accessToken, refreshToken } = response.data.result;
    
    // ✅ 액세스 토큰은 로컬 스토리지에 저장
    localStorage.setItem("accessToken", accessToken);
    
    // ✅ 리프레시 토큰은 쿠키에 저장 (보안 강화 필요)
    document.cookie = `refreshToken=${refreshToken}; path=/; HttpOnly; Secure; SameSite=Strict`;

    alert("로그인 성공!");
    // TODO: 로그인 후 메인 페이지로 이동 로직 추가
  } catch (error) {
    console.error("로그인 오류:", error);
    alert("로그인 실패. 이메일과 비밀번호를 확인해주세요.");
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.pillme-text {
  color: #4E7351;
}
</style>

