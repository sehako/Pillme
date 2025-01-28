<template>
  <div id="app" class="relative flex flex-col md:flex-row min-h-screen pb-16">
    
    <!-- ✅ 왼쪽 (웹에서는 보이지만 모바일에서는 숨김) -->
    <div class="hidden md:block w-1/2 bg-gray-100"></div>

    <!-- ✅ 오른쪽 (모바일에서는 전체 화면 차지) -->
    <div class="flex flex-col justify-center items-center w-full md:w-1/2 relative">
      
      <!-- ✅ 상단 바 -->
      <BaseTopbar id="topbar" class="fixed top-0 w-full md:w-1/2 z-10" />

      <header class="text-center w-full">
        <p v-if="isOffline" class="text-red-500 font-semibold">🚨 현재 오프라인 상태입니다.</p>
        <button v-if="deferredPrompt" @click="installPWA" 
          class="block mx-auto px-4 py-2 bg-blue-500 text-white rounded-lg text-lg hover:bg-blue-600 transition">
          📲 PWA 설치하기
        </button>
      </header>

      <!-- ✅ 현재 페이지의 콘텐츠 (기본적으로 스크롤 없음, 허용된 페이지만 스크롤 가능) -->
      <div :class="{ 'overflow-y-auto': isScrollAllowed }" 
           class="flex-1 w-full max-w-xs sm:max-w-sm md:max-w-md lg:max-w-lg"
           :style="{ maxHeight: computedHeight }">
        <router-view />
      </div>

      <div v-if="isUpdateAvailable" @click="refreshApp" 
        class="fixed bottom-20 left-1/2 transform -translate-x-1/2 bg-green-500 text-white px-4 py-2 rounded-lg text-sm cursor-pointer shadow-md">
        🔄 새로운 업데이트가 있습니다. 클릭하여 새로고침하세요.
      </div>
    </div>

    <!-- ✅ 네비게이션 바 (모든 페이지 공통, 모바일 w-full / 웹에서는 w-1/2) -->
    <BaseNavbar id="navbar" class="fixed bottom-0 right-0 w-full md:w-1/2 md:right-0 z-10" />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from "vue";
import { useRoute } from "vue-router";
import BaseNavbar from "./components/BaseNavbar.vue";
import BaseTopbar from "./components/BaseTopbar.vue";

const route = useRoute();
const isScrollAllowed = ref(false);
const topbarHeight = ref(0);
const navbarHeight = ref(0);
const computedHeight = ref("100vh");

// 특정 페이지에서만 스크롤 허용
const scrollablePages = ["/scroll-page-1", "/scroll-page-2"];
const checkScrollPermission = () => {
  isScrollAllowed.value = scrollablePages.includes(route.path);
  document.body.style.overflow = isScrollAllowed.value ? "auto" : "hidden";
};

const updateLayout = () => {
  const topbar = document.querySelector("#topbar");
  const navbar = document.querySelector("#navbar");
  topbarHeight.value = topbar ? topbar.offsetHeight : 0;
  navbarHeight.value = navbar ? navbar.offsetHeight : 0;
  computedHeight.value = `calc(100vh - ${topbarHeight.value}px - ${navbarHeight.value}px)`;
};

watch(() => route.path, () => {
  checkScrollPermission();
  updateLayout();
});

onMounted(() => {
  updateLayout();
  window.addEventListener("resize", updateLayout);
});

onUnmounted(() => {
  window.removeEventListener("resize", updateLayout);
});
</script>

<style>
/* body {
  font-family: Arial, sans-serif;
  margin: 0;
  padding: 0;
  background: #ffffff;
} */
</style>

<!-- 추후 pwa 설치 유도 알림 구현해서 넣어야함. -->
