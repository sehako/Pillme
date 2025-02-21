<template>
  <div class="flex flex-col items-center justify-center min-h-screen px-4">
    <!-- ✅ 로고 -->
    <BaseLogo :src="logoSrc" size="md" />

    <!-- ✅ BaseText 컴포넌트 사용 -->
    <BaseText highlightText="PILLME" />

    <!-- ✅ 아이디 찾기 결과 -->
    <div class="w-full max-w-xs space-y-4 text-center md:max-w-sm">
      <p class="text-lg font-medium text-gray-700">찾은 아이디는</p>

      <BaseOutput :modelValue="displayEmail" />

      <div class="flex flex-col items-center mt-4 space-y-2 text-sm text-gray-600">
        <!-- FORM 제공자인 경우에만 비밀번호 찾기 링크 표시 -->
        <a v-if="provider === 'FORM'" href="/pwsearch" class="hover:underline"> 비밀번호 찾기 </a>
        <a href="/loginselection" class="hover:underline"> 로그인 페이지로 돌아가기 </a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import BaseOutput from '../components/BaseOutput.vue';
import BaseLogo from '../components/BaseLogo.vue';
import BaseText from '../components/BaseText.vue';

import logoSrc from '../assets/logi_nofont.svg';

const route = useRoute();
const email = ref('');
const provider = ref('');

// 이메일 표시 형식 계산
const displayEmail = computed(() => {
  if (!email.value) return '';
  return provider.value === 'GOOGLE' ? `${email.value} (구글 로그인)` : email.value;
});

onMounted(() => {
  const routeEmail = route.query.email;
  const routeProvider = route.query.provider;

  if (routeEmail && routeProvider) {
    email.value = routeEmail;
    provider.value = routeProvider;
  } else {
    window.location.href = '/loginselection';
  }
});
</script>

<style scoped>
.text-center {
  text-align: center;
}
</style>
