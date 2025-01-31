<template>
  <div id="app" class="flex flex-row h-screen">
    <!-- 왼쪽 (PC 전용) -->
    <div class="hidden md:block w-1/2 bg-gray-100"></div>

    <!-- 오른쪽 (모바일 전체) -->
    <div class="flex flex-col w-full md:w-1/2">
      <!-- 상단 바 -->
      <div ref="topbarRef" class="z-10">
        <BaseTopbar />
      </div>

      <!-- 가운데(라우트) 영역 -->
      <div
        ref="contentRef"
        :class="[
          'h-screen',
          isScrollAllowed ? 'overflow-y-auto overflow-x-hidden' : 'flex items-center justify-center overflow-hidden'
        ]"
      >
        <router-view v-if="isRouteReady" />
      </div>

      <!-- 하단 바 -->
      <div ref="navbarRef" class="flex-none z-10 bg-white">
        <BaseNavbar />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue';
import { useRoute } from 'vue-router';
import BaseTopbar from './components/BaseTopbar.vue';
import BaseNavbar from './components/BaseNavbar.vue';

const route = useRoute();
const contentRef = ref(null);
const isRouteReady = ref(true);

// 특정 라우트에서 스크롤 허용
const isScrollAllowed = ref(false);
const scrollablePages = ['/afteraccount', '/'];

watch(() => route.path, async () => {
  isRouteReady.value = false;
  await nextTick(); // 레이아웃 업데이트 후 반영
  isScrollAllowed.value = scrollablePages.includes(route.path);

  // ✅ 스크롤 허용 안된 페이지일 때 강제로 스크롤 최상단 이동 및 차단
  if (!isScrollAllowed.value) {
    document.documentElement.style.overflow = "hidden";
    document.body.style.overflow = "hidden";
    setTimeout(() => {
      contentRef.value?.scrollTo({ top: 0, behavior: 'instant' });
    }, 50);
  } else {
    document.documentElement.style.overflow = "";
    document.body.style.overflow = "";
  }

  isRouteReady.value = true;
});

// 상단/하단 바 높이 측정
const topbarRef = ref(null);
const navbarRef = ref(null);
const topbarHeight = ref(0);
const navbarHeight = ref(0);

onMounted(() => {
  isScrollAllowed.value = scrollablePages.includes(route.path) || route.path === '';
});
</script>

<style>
/* ✅ body와 html의 기본 스크롤 차단 */
html, body {
  overflow: hidden;
  height: 100%;
}
</style>
