<template>
  <div class="start-container">
    <!-- ✅ 로고 + 텍스트를 하나의 div로 합침 -->
    <div class="flex flex-col items-center gap-0">
      <BaseLogo :src="logoSrc" size="md" />

      <div class="flex flex-row items-center gap-0">
        <!-- ✅ BaseText 사용 -->
        <BaseText textBefore="복약 관리의 새로운 방법," class="pb-4 -mr-10" />
        <BaseLogo :src="textLogoSrc" class="h-[36px] pb-4 -mr-10" />
      </div>
    </div>

    <div class="action-section flex flex-col gap-4">
      <BaseButton textColor="text-white" size="md" @click="goToSignInSelection">
        회원가입
      </BaseButton>

      <BaseButton textColor="text-white" size="md" @click="goToLogInSelection"> 로그인 </BaseButton>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import BaseButton from '../components/BaseButton.vue';
import BaseLogo from '../components/BaseLogo.vue';
import BaseText from '../components/BaseText.vue'; // ✅ 추가
import logoSrc from '../assets/logi_nofont.svg';
import textLogoSrc from '../assets/Logi_font.svg';

const router = useRouter();

const goToSignInSelection = () => {
  router.push('/signinselection');
};

const goToLogInSelection = () => {
  router.push('/loginselection');
};
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
  color: #4e7351;
}
</style>
