<template>
  <div class="start-container">
    <div class="logo-section">
      <img src="../assets/Splash_logo.png" alt="PILLME Logo" class="logo" />
    </div>
    <div class="action-section">
      <BaseButton 
        bgColor="bg-green-700" 
        textColor="text-white" 
        hoverColor="hover:bg-green-800" 
        size="md"
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
import BaseButton from "../components/BaseButton.vue";
</script>

<style scoped>
.start-container {
  @apply flex flex-col justify-center items-center h-screen text-center bg-white p-4;
}

.logo-section {
  @apply mb-8;
}

.logo {
  @apply w-72 h-72 mb-4;
}

.action-section {
  @apply w-full max-w-md;
}

.login-link {
  @apply mt-4 text-sm text-gray-600;
}

.login-link a {
  @apply text-green-700 no-underline hover:underline;
}

@media (max-width: 600px) {
  .logo {
    @apply w-24 h-24;
  }
}
</style>
