<template>
    <div class="flex flex-col justify-center items-center min-h-screen px-4">
      <!-- ✅ 로고 (BaseLogo 컴포넌트 사용) -->
      <BaseLogo :src="logoSrc" size="md" />
  
      <!-- ✅ 텍스트 (BaseText 컴포넌트 사용) -->
      <BaseText 
        textBefore="복약 관리의 새로운 방법," 
        highlightText="PILLME" 
      />
  
      <div class="login-options w-full max-w-xs mt-4 space-y-2 md:space-y-3">
        <SocialLoginButton :iconSrc="googleIcon">
          구글 계정으로 가입하기
        </SocialLoginButton>
  
        <SocialLoginButton :iconSrc="naverIcon">
          네이버 계정으로 가입하기
        </SocialLoginButton>
  
        <!-- ✅ 이메일 버튼 클릭 시 `/login`으로 이동 -->
        <SocialLoginButton :iconSrc="emailIcon" @click="goToSignin">
          이메일로 가입하기
        </SocialLoginButton>
      </div>
  
    </div>
  </template>
  
  <script setup>
  import { useRouter } from "vue-router";
  import SocialLoginButton from "../components/SocialLoginButton.vue";
  import BaseLogo from "../components/BaseLogo.vue"; // ✅ 추가
  import BaseText from "../components/BaseText.vue"; // ✅ 추가
  
  // 이미지 경로를 import로 가져오기
  import logoSrc from "../assets/logi_nofont.png";
  import googleIcon from "../assets/Google_Login.png";
  import naverIcon from "../assets/Naver_Login.png";
  import emailIcon from "../assets/Email_Login.png";
  
  // ✅ Vue Router 인스턴스 가져오기
  const router = useRouter();

  const goToSignin = () => {
  router.push('/afteraccount');
}


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
  