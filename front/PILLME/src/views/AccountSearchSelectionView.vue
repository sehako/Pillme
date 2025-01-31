<template>
  <div class="search-container">
    <!-- ✅ 로고 통일 -->
    <BaseLogo :src="logoSrc" size="md" />

    <!-- ✅ BaseText 컴포넌트 적용 -->
    <BaseText 
      textBefore="복약 관리의 새로운 방법," 
      highlightText="PILLME" 
    />

    <!-- ✅ 버튼 컨테이너 정리 -->
    <div class="accountsearchselection-options">
      <BaseButton 
        textColor="text-white" 
        size="md"
        overrideClass="!bg-[#4E7351] hover:!bg-[#3D5A3F]"
        @click="goToIdSearch"
        >
        이메일 찾기
      </BaseButton>

      <BaseButton 
         textColor="text-white" 
         size="md"
        overrideClass="!bg-[#4E7351] hover:!bg-[#3D5A3F]"
        @click="goToPwSearch"
        >
        비밀번호 찾기
      </BaseButton>
    </div>

    <!-- ✅ 로그인 링크 -->
    <p class="back-login">
      <a href="/loginselection" class="text-[#4E7351] hover:underline">로그인 페이지로 돌아가기</a>
    </p>
  </div>
</template>

<script setup>
import BaseButton from "../components/BaseButton.vue";
import BaseLogo from "../components/BaseLogo.vue";
import BaseText from "../components/BaseText.vue"; // ✅ 추가
import logoSrc from "../assets/logi_nofont.png";
import { useRouter } from "vue-router";

const router = useRouter();

const goToIdSearch = () => {
  router.push('/idsearch');
}

const goToPwSearch = () => {
  router.push('/pwsearch');
}
</script>

<style scoped>
/* ✅ Tailwind CSS로 스타일 최적화 */
.search-container {
  @apply flex flex-col justify-center items-center min-h-screen text-center p-4;
}

.accountsearchselection-options {
  @apply w-full max-w-xs mt-4 space-y-2 md:space-y-3;
}

.back-login {
  @apply mt-4 text-sm;
}

</style>
