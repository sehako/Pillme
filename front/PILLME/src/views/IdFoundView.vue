<template>
    <div class="flex flex-col justify-center items-center min-h-screen px-4">
      <!-- ✅ 로고 -->
      <BaseLogo :src="logoSrc" size="md" />
  
      <!-- ✅ BaseText 컴포넌트 사용 -->
      <BaseText highlightText="PILLME" />
  
      <!-- ✅ 아이디 찾기 결과 -->
      <div class="w-full max-w-xs md:max-w-sm space-y-4 text-center">
        <p class="text-gray-700 font-medium text-lg">찾은 아이디는</p>
        
        <BaseOutput :modelValue="foundId" />
  
        <div class="flex flex-col items-center space-y-2 mt-4 text-sm text-gray-600">
          <a href="/pwsearch" class="hover:underline">비밀번호 찾기</a>
          <a href="/loginselection" class="hover:underline">로그인 페이지로 돌아가기</a>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from "vue";
  import BaseOutput from "../components/BaseOutput.vue";
  import BaseLogo from "../components/BaseLogo.vue";
  import BaseText from "../components/BaseText.vue";
  
  import logoSrc from "../assets/logi_nofont.png";
  
  // 찾은 아이디 값 (예시로 설정, 실제 데이터는 API로 받아올 수도 있음)
  const foundId = ref("example_user"); // 이 값은 백엔드에서 가져온 값을 설정하는 로직이 필요할 수 있음
  </script>
  
  <style scoped>
  .text-center {
    text-align: center;
  }
  </style>
  