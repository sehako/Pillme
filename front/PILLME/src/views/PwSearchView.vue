<template>
    <div class="flex flex-col justify-center items-center min-h-screen px-4">
      <!-- ✅ 로고 -->
      <BaseLogo :src="logoSrc" size="md" />
  
      <!-- ✅ BaseText 컴포넌트 사용 -->
      <BaseText highlightText="PILLME" />
  
      <form class="w-full max-w-xs md:max-w-sm space-y-4 mt-6" @submit.prevent="handleSubmit">
        
        <div class="flex flex-col w-full">
          <BaseInput
            id="phoneNumber"
            v-model="phoneNumber"
            type="tel"
            placeholder="회원가입 전화번호"
          />
        </div>
  
        <div class="flex flex-col w-full">
          <BaseInput
            id="email"
            v-model="email"
            type="email"
            placeholder="회원가입 이메일"
          />
        </div>

        <div class="flex justify-center">
          <BaseButton
            textColor="text-white"
            size="md"
            overrideClass="!w-1/2 !px-4 !py-3.5 !text-sm !bg-[#EF7C8E] hover:!bg-[#E96C7E]"
            @click="handleEmailVerification"
          >
            이메일 인증
          </BaseButton>
        </div>
      </form>
  
      <p class="mt-8 text-sm text-gray-600">
        <a href="/loginselection" class="hover:underline">로그인 페이지로 돌아가기</a>
      </p>
    </div>
  </template>
  
  <script setup>
  import { ref } from "vue";
  import BaseButton from "../components/BaseButton.vue";
  import BaseInput from "../components/BaseInput.vue";
  import BaseLogo from "../components/BaseLogo.vue";
  import BaseText from "../components/BaseText.vue";
  
  import logoSrc from "../assets/logi_nofont.svg";
  
  // 사용자 입력값
  const phoneNumber = ref("");
  const email = ref("");
  
  const handleEmailVerification = () => {
    console.log("이메일 인증 요청", {
      phoneNumber: phoneNumber.value,
      email: email.value,
    });
  
    // 이메일 인증 API
  };

  const handleSubmit = () => {
    console.log("비밀번호 찾기 요청", {
      phoneNumber: phoneNumber.value,
      email: email.value,
    });
  };
  </script>
  
  <style scoped>
  /* ✅ 버튼 간격 */
  .gap-4 {
    gap: 1rem;
  }
  </style>
  