<template>
  <div class="start-container">
     <!-- ✅ 로고 + 텍스트를 하나의 div로 합침 -->
     <div class="flex flex-col items-center gap-0">
       <BaseLogo :src="logoSrc" size="md" />
       
       <!-- ✅ BaseText 사용 -->
       <BaseText 
         textBefore="복약 관리의 새로운 방법," 
         highlightText="PILLME" 
       />
     </div>

     <div class="action-section flex flex-col">
       <BaseButton 
         textColor="text-white" 
         size="md"
         @click="goToSignInSelection"
         >
         시작하기
       </BaseButton>
       
       <p class="login-link">
         이미 계정이 있나요? <a href="/loginselection">로그인</a>
       </p>
     </div>
   </div>
</template>

<script setup>
import { useRouter } from "vue-router";
import BaseButton from "../components/BaseButton.vue";
import BaseLogo from "../components/BaseLogo.vue";
import BaseText from "../components/BaseText.vue"; // ✅ 추가
import logoSrc from "../assets/logi_nofont.png";

const router = useRouter();

const goToSignInSelection = () => {
  router.push('/signinselection');
}

</script>

<style scoped>
.start-container {
  @apply flex flex-col justify-center items-center min-h-screen text-center bg-white p-4;
}

.action-section {
  @apply w-full max-w-md;
}

.login-link {
  @apply mt-4 text-sm text-gray-600;
}

.login-link a {
  @apply text-[#4E7351] no-underline hover:underline;
}

.pillme-text {
  color: #4E7351;
}
</style>
