<template>
  <div class="flex flex-col justify-center items-center min-h-screen px-4">
    <!-- ✅ 로고 -->
    <BaseLogo :src="logoSrc" size="md" />

    <!-- ✅ BaseText 적용 -->
    <BaseText 
      textBefore="복약 관리의 새로운 방법," 
      highlightText="PILLME" 
    />

    <!-- ✅ 로그인 폼 -->
    <form class="w-full max-w-xs md:max-w-sm space-y-4" @submit.prevent="handleLogin">
      <BaseInput 
        v-model="email" 
        type="email" 
        placeholder="이메일 입력" 
        :icon="emailIcon"
      />
      <BaseInput 
        v-model="password" 
        type="password" 
        placeholder="비밀번호 입력" 
        :icon="passwordIcon"
      />

      <!-- ✅ BaseButton 적용 -->
      <BaseButton 
         textColor="text-white" 
         size="md"
       >
        로그인
      </BaseButton>
    </form>

    <!-- ✅ 로그인 페이지로 돌아가기 버튼 (간격 조정) -->
    <p class="back-login mt-6 md:mt-8">
      <a href="/loginselection" class="text-[#4E7351] hover:underline">로그인 페이지로 돌아가기</a>
    </p>
  </div>
</template>


<script setup>
import { ref } from "vue";
import BaseButton from "../components/BaseButton.vue";
import BaseInput from "../components/BaseInput.vue";
import BaseLogo from "../components/BaseLogo.vue";
import BaseText from "../components/BaseText.vue"; // ✅ 추가

import logoSrc from "../assets/logi_nofont.svg";
import emailIcon from "../assets/email.png";
import passwordIcon from "../assets/key.png";

const email = ref("");
const password = ref("");

const handleLogin = () => {
  console.log("로그인 시도:", email.value, password.value);
  // TODO: 로그인 처리 로직 추가 필요
};
</script>

<style scoped>
.pillme-text {
  color: #4E7351;
}
</style>
