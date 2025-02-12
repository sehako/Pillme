<template>
  <div class="flex flex-col items-center justify-center min-h-screen px-4">
    <!-- ✅ 로고 (BaseLogo 컴포넌트 사용) -->
    <BaseLogo :src="logoSrc" size="md" />

    <!-- ✅ 텍스트 (BaseText 컴포넌트 사용) -->
    <BaseText 
      textBefore="복약 관리의 새로운 방법," 
      highlightText="PILLME" 
    />

    <div class="w-full max-w-xs mt-4 space-y-2 login-options md:space-y-3">
      <SocialLoginButton :iconSrc="googleIcon" @click="handleGoogleLogin">
        구글 계정으로 계속하기
      </SocialLoginButton>

      <!-- ✅ 이메일 버튼 클릭 시 `/login`으로 이동 -->
      <SocialLoginButton :iconSrc="emailIcon" @click="goToLogin">
        이메일로 계속하기
      </SocialLoginButton>
    </div>

    <p class="mt-4 text-sm text-gray-600 forgot-account">
      <a href="/accountsearchselection" class="hover:underline">계정이 기억나지 않아요</a>
    </p>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";
import SocialLoginButton from "../components/SocialLoginButton.vue";
import BaseLogo from "../components/BaseLogo.vue"; // ✅ 추가
import BaseText from "../components/BaseText.vue"; // ✅ 추가

// 이미지 경로를 import로 가져오기
import logoSrc from "../assets/logi_nofont.svg";
import googleIcon from "../assets/Google_Login.png";
import emailIcon from "../assets/Email_Login.png";

// ✅ Vue Router 인스턴스 가져오기
const router = useRouter();

// ✅ 로그인 페이지로 이동하는 함수
const goToLogin = () => {
  router.push("/login");
};

// 로그인 시작 함수
const handleGoogleLogin = () => {
  const GOOGLE_AUTH_URL = import.meta.env.VITE_GOOGLE_AUTH_URL;
  const CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID;
  const REDIRECT_URI = import.meta.env.VITE_GOOGLE_REDIRECT_URI;

  // state 파라미터 추가
  const state = encodeURIComponent(window.location.origin);

  const params = new URLSearchParams({
    client_id: CLIENT_ID,
    redirect_uri: REDIRECT_URI,
    response_type: 'code',
    scope: 'email profile',
    state, // state 파라미터 추가
  });

  window.location.href = `${GOOGLE_AUTH_URL}?${params.toString()}`;
};

</script>

<style scoped>
.pillme-text {
  color: #4E7351;
}

.forgot-account {
  @apply mt-4 text-sm text-gray-600;
}

.forgot-account a {
  @apply text-[#4E7351] no-underline hover:underline;
}
</style>
