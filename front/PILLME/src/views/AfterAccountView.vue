<template>
  <div class="flex flex-col justify-center items-center min-h-screen px-4">
    <!-- ✅ 로고 -->
    <BaseLogo :src="logoSrc" size="md" />

    <!-- ✅ BaseText 컴포넌트 사용 -->
    <BaseText highlightText="PILLME" />
    <!-- 로고 밑에 폰트 붙어있는 그림을 만들어서 넣을 것 -->
    <!-- ✅ 가입 폼 -->
    <form class="w-full max-w-xs md:max-w-sm space-y-4" @submit.prevent="handleSubmit">
      
      <!-- 이름 입력 -->
      <div class="flex flex-col w-full">
        <label for="name" class="text-sm font-medium">이름</label>
        <BaseInput id="name" v-model="name" type="text" placeholder="이름을 입력해주세요." />
      </div>
      
      <!-- 성별 선택 -->
      <div class="flex flex-col w-full">
        <label class="text-sm font-medium">성별</label>
        <div class="flex w-full gap-2">
          <button 
            type="button"
            @click="gender = '여'"
            :class="['gender-btn', gender === '여' ? 'selected' : '']"
          >
            여
          </button>
          <button 
            type="button"
            @click="gender = '남'"
            :class="['gender-btn', gender === '남' ? 'selected' : '']"
          >
            남
          </button>
        </div>
      </div>

      <!-- 생년월일 입력 -->
      <div class="flex flex-col w-full">
        <label for="birthdate" class="text-sm font-medium">생년월일</label>
        <BaseInput id="birthdate" v-model="birthdate" type="date" />
      </div>

      <!-- 전화번호 입력 -->
      <div class="flex flex-col w-full">
        <label for="phoneNumber" class="text-sm font-medium">전화번호</label>
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
            overrideClass="!w-auto !px-4 !py-2 !text-sm !bg-[#EF7C8E] group-hover:!bg-[#E96C7E]"
          >
            인증
          </BaseButton>
        </div>
      </div>

      <!-- 인증번호 입력 -->
      <div class="flex flex-col w-full">
        <label for="authCode" class="text-sm font-medium">인증번호</label>
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
            overrideClass="!w-auto !px-4 !py-2 !text-sm !bg-[#EF7C8E] group-hover:!bg-[#E96C7E]"
          >
            확인
          </BaseButton>
        </div>
      </div>

      <!-- 버튼들 가로 정렬 & 간격 추가 -->
      <div class="flex justify-between w-full mt-4 gap-4">
        <BaseButton 
          textColor="text-gray-700" 
          size="md" 
          @click="goBack"
          overrideClass="!bg-[#D1D5DB] hover:!bg-[#6B7280]"
        >
          이전
        </BaseButton>

        <BaseButton 
          textColor="text-white" 
          size="md" 
          type="submit"
          overrideClass="!bg-[#EF7C8E] hover:!bg-[#E96C7E]"
        >
          가입하기
        </BaseButton>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import BaseButton from "../components/BaseButton.vue";
import BaseInput from "../components/BaseInput.vue";
import BaseLogo from "../components/BaseLogo.vue";
import BaseText from "../components/BaseText.vue";

import logoSrc from "../assets/logi_nofont.png";

const name = ref('');
const gender = ref('');
const birthdate = ref('');
const phoneNumber = ref('');
const authCode = ref('');

// 제출 함수
const handleSubmit = () => {
  console.log('회원가입 시도', { name: name.value, gender: gender.value, birthdate: birthdate.value, phoneNumber: phoneNumber.value, authCode: authCode.value });
};

// 이전 페이지로 돌아가기
const goBack = () => {
  console.log('이전 페이지로 돌아가기');
};
</script>

<style scoped>
/* ✅ 성별 선택 버튼 스타일 */
.gender-btn {
  @apply w-1/2 py-2 text-center text-gray-700 border border-gray-300 rounded-lg;
}
.gender-btn.selected {
  background-color: #EF7C8E;
  color: white;
}

/* ✅ 버튼 간격 */
.gap-4 {
  gap: 1rem;
}
</style>