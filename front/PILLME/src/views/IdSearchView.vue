<template>
    <div class="flex flex-col justify-center items-center min-h-screen px-4">
      <!-- ✅ 로고 -->
      <BaseLogo :src="logoSrc" size="md" />
      <!-- ✅ BaseText 컴포넌트 사용 -->
      <BaseText highlightText="PILLME"/>
      <form class="w-full max-w-xs md:max-w-sm space-y-4 mt-6" @submit.prevent="handleSubmit">
        <!-- 전화번호 입력 -->
        <div class="flex flex-col w-full">
          <div class="flex items-center gap-2 group">
            <BaseInput
              id="phoneNumber"
              v-model="phoneNumber"
              type="tel"
              placeholder="전화번호 입력"
              class="flex-grow"
            />
            <BaseButton
              textColor="text-white"
              size="sm"
              overrideClass="!min-w-0 !w-auto !px-4 !py-2 !text-sm !bg-[#EF7C8E] group-hover:!bg-[#E96C7E]"
            >
              인증
            </BaseButton>
          </div>
        </div>
  
        <!-- 인증번호 입력 -->
        <div class="flex flex-col w-full">
          <div class="flex items-center gap-2 group">
            <BaseInput
              id="authCode"
              v-model="authCode"
              type="text"
              placeholder="인증번호 입력"
              class="flex-grow"
            />
            <BaseButton
              textColor="text-white"
              size="sm"
              overrideClass="!min-w-0 !w-auto !px-4 !py-2 !text-sm !bg-[#EF7C8E] group-hover:!bg-[#E96C7E]"
            >
              확인
            </BaseButton>
          </div>
        </div>
      </form>
      <p class="forgot-account mt-8 text-sm text-gray-600">
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

  const phoneNumber = ref("");
  const authCode = ref("");
  
  // 제출 함수
  const handleSubmit = () => {
    console.log("아이디 찾기 시도", {
      phoneNumber: phoneNumber.value,
      authCode: authCode.value,
    });
  };

  </script>
  
  <style scoped>
  /* ✅ 버튼 간격 */
  .gap-4 {
    gap: 1rem;
  }
  </style>
  