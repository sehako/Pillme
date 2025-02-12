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
import { handleLoginSuccess } from "../api/auth.js";
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

    console.log("🔍 로그인 API 응답:", response); // ✅ API 응답 구조 확인
    console.log("🔍 response.result:", response.result);

    if (!response || !response.result) {
      throw new Error("서버에서 예상치 못한 응답을 받았습니다.");
    }
    
    // ✅ `response` 자체를 handleLoginSuccess()에 전달
    handleLoginSuccess(response);
    
    // alert("로그인 성공!"); << 이 부분은 삭제
    window.location.replace("/");
  } catch (error) {
    console.error("❌ 로그인 오류:", error);
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

