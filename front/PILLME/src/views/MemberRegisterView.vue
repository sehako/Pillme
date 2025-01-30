<template>
    <div class="min-h-screen w-full flex flex-col items-center">
      
  
      <!-- ✅ 로고 및 안내 문구 -->
      <div class="mt-8 flex flex-col items-center text-center">
        <BaseLogo :src="logoSrc" size="md" />
        <BaseText highlightText="PILLME" textColor="text-gray-700" />
        <p class="text-sm text-gray-600 mt-4 px-6 leading-relaxed">
          동의를 얻을 가족의 핸드폰 번호를 입력해주세요.<br />
          14세 이상 회원 추가는 동의 요청 및 승인 과정을 거쳐야 합니다.<br />
          동의를 얻을 회원님의 핸드폰에서 동의를 승인하세요.
        </p>
      </div>
  
      <!-- ✅ 전화번호 입력란 및 인증 버튼 -->
      <div class="mt-10 w-3/4 flex items-center justify-between border-b py-3">
        <input 
          type="text" 
          placeholder="상대방 전화번호 인증" 
          v-model="phoneNumber"
          class="text-gray-600 text-left outline-none placeholder-gray-400 w-full"
        />
        <BaseButton
              textColor="text-white"
              size="sm"
              overrideClass="!inline-flex !items-center !justify-center !w-auto !min-w-[80px] !px-4 !py-2 !text-sm !bg-[#4E7531]"
            >
              확인
            </BaseButton>
      </div>
  
      <!-- ✅ 인증 결과 메시지 -->
      <p v-if="verificationMessage" class="text-[#4E7531] text-sm mt-2">
        {{ verificationMessage }}
      </p>
  
      <!-- ✅ 인증 완료 버튼 -->
      <div class="mt-10 w-3/4">
        <BaseButton
          textColor="text-gray-800"
          size="lg"
          overrideClass="!w-full !px-4 !py-3.5 !text-md !bg-gray-200 hover:!bg-gray-300 mt-12"
          @click="completeVerification"
        >
          인증 완료
        </BaseButton>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from "vue";
  import BaseLogo from "../components/BaseLogo.vue";
  import BaseButton from "../components/BaseButton.vue";
  import BaseText from "../components/BaseText.vue"; // ✅ BaseText 추가
  
  import logoSrc from "../assets/logi_nofont.png";
  const phoneNumber = ref("");
  const verificationMessage = ref("");
  
  // ✅ 전화번호 인증 시뮬레이션
  const verifyPhone = () => {
    if (!phoneNumber.value) {
      verificationMessage.value = "전화번호를 입력해주세요.";
      return;
    }
    verificationMessage.value = "존재하지 않는 사용자입니다.";
  };
  
  // ✅ 인증 완료 버튼 클릭
  const completeVerification = () => {
    console.log("✅ 인증 완료");
  };
  
  </script>
  