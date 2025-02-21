<template>
    <div class="min-h-screen w-full flex flex-col items-center">

      <div class="mt-8 flex flex-col items-center">
        <BaseLogo :src="logoSrc" size="md" />
        <BaseText highlightText="PILLME" textColor="text-gray-700" />
      </div>
  
      <div class="mt-6 w-3/4">
        <div class="flex justify-between items-center border-b py-3">
          <span class="text-gray-600">이름</span>
          <input 
            type="text" 
            placeholder="이름 입력" 
            v-model="form.name"
            class="text-gray-600 text-right outline-none placeholder-gray-400 w-2/3"
          />
        </div>
  
        <div class="flex justify-between items-center border-b py-3">
          <span class="text-gray-600">성별</span>
          <input 
            type="text" 
            placeholder="성별 입력" 
            v-model="form.gender"
            class="text-gray-600 text-right outline-none placeholder-gray-400 w-2/3"
          />
        </div>
  
        <div class="flex justify-between items-center border-b py-3">
          <span class="text-gray-600">생년월일</span>
          <input 
            type="text" 
            placeholder="생년월일 입력" 
            v-model="form.birthdate"
            class="text-gray-600 text-right outline-none placeholder-gray-400 w-2/3"
          />
        </div>
      </div>
  
      <div class="mt-6 w-3/4">
        <BaseButton
          textColor="text-gray-800"
          size="lg"
          overrideClass="!w-full !px-4 !py-3.5 !text-md !bg-gray-200 hover:!bg-gray-300"
          @click="submitForm"
        >
          완료
        </BaseButton>
      </div>

      <p class="text-center text-[#4E7351] text-sm mt-6">
        비회원은 푸시 알림을 받을 수 없습니다.<br />
        푸시 알림을 받으시려면 회원으로 등록해주세요.
      </p>
    </div>
  </template>
  
  <script setup>
  import { ref } from "vue";
  import BaseLogo from "../components/BaseLogo.vue";
  import BaseButton from "../components/BaseButton.vue";
  import BaseText from "../components/BaseText.vue"; 

  import logoSrc from "../assets/logi_nofont.svg";
  
  const form = ref({
    name: "",
    gender: "",
    birthdate: "",
  });
  
  const submitForm = () => {
    // console.log("✅ 비회원 정보 제출:", form.value);
  };
  
  </script>
  