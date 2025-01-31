<template>
  <div id="app" class="flex flex-row h-screen">
    <!-- 왼쪽 (PC 전용) -->
    <div class="hidden md:block w-1/2 bg-gray-100"></div>

    <!-- 오른쪽 (모바일 전체) -->
    <div class="flex flex-col w-full md:w-1/2">
      <!-- 상단 바 -->
      <div ref="topbarRef" class="z-10 bg-white">
        <BaseTopbar />
      </div>

      <!-- 가운데(라우트) 영역 -->
      <div
        ref="contentRef"
        :class="[
          'px-4 h-screen',
          !isScrollAllowed ? 'flex items-center justify-center overflow-hidden' : 'overflow-y-auto overflow-x-hidden'
        ]"
        :style="{ paddingTop: `${topbarHeight}px`, paddingBottom: `${navbarHeight}px` }"
      >
        <transition name="fade" mode="out-in">
          <router-view v-if="isRouteReady" />
        </transition>
      </div>

      <!-- 하단 바 -->
      <div ref="navbarRef" class="flex-none z-10 bg-white">
        <BaseNavbar />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, computed, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import BaseTopbar from './components/BaseTopbar.vue';
import BaseNavbar from './components/BaseNavbar.vue';

const route = useRoute();
const router = useRouter();
const contentRef = ref(null);
const isRouteReady = ref(false);

// 특정 라우트에서 스크롤 허용
const isScrollAllowed = ref(false);
const scrollablePages = ['/afteraccount', '/scroll-page-2'];

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

// 특정 라우트에서 flex 적용
const flexRoutes = [
  "StartView", "SigninSelectionView", "LoginSelectionView",
  "AccountSearchSelectionView", "IdSearch", "PwSearch", "LoginView"
];
const isNeedFlex = computed(() => flexRoutes.includes(route.name));

// 상단/하단 바 높이 측정
const topbarRef = ref(null);
const navbarRef = ref(null);
const topbarHeight = ref(0);
const navbarHeight = ref(0);

let topbarObserver, navbarObserver;

onMounted(() => {
  if ('ResizeObserver' in window) {
    topbarObserver = createResizeObserver(topbarRef, (entry) => {
      topbarHeight.value = entry.contentRect.height;
    });
    navbarObserver = createResizeObserver(navbarRef, (entry) => {
      navbarHeight.value = entry.contentRect.height;
    });
  } else {
    updateHeightsFallback();
    window.addEventListener('resize', updateHeightsFallback);
  }
});

onUnmounted(() => {
  topbarObserver?.unobserve(topbarRef.value);
  navbarObserver?.unobserve(navbarRef.value);
  window.removeEventListener('resize', updateHeightsFallback);
});

function createResizeObserver(refEl, callback) {
  const observer = new ResizeObserver((entries) => {
    entries.forEach((entry) => {
      if (refEl.value === entry.target) {
        callback(entry);
      }
    });
  });
  refEl.value && observer.observe(refEl.value);
  return observer;
}

function updateHeightsFallback() {
  topbarHeight.value = topbarRef.value?.offsetHeight || 0;
  navbarHeight.value = navbarRef.value?.offsetHeight || 0;
}
</script>

<style>
/* ✅ body와 html의 기본 스크롤 차단 */
html, body {
  overflow: hidden;
  height: 100%;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter, .fade-leave-to {
  opacity: 0;
}
</style>
