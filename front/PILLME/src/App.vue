<template>
  <div id="app" class="flex flex-row h-screen-custom">
    <!-- 왼쪽 (PC 전용) -->
    <div class="hidden md:block w-1/2 bg-gray-100"></div>

    <!-- 오른쪽 (모바일 전체) -->
    <div class="flex flex-col w-full md:w-1/2">
      <!-- 상단 바 -->
      <div ref="topbarRef" class="relative z-10">
        <BaseTopbar />
      </div>

      <!-- 가운데(라우트) 영역 -->
      <div
        ref="contentRef"
        :class="[ 
          'h-screen-custom', 
          isScrollAllowed ? 'overflow-y-auto overflow-x-hidden' : 'flex items-center justify-center overflow-hidden' 
        ]"
      >
        <router-view v-if="isRouteReady" :navbarHeight="navbarHeight" />
      </div>

      <!-- 하단 바 -->
      <div ref="navbarRef" class="relative z-10 bg-white">
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
const alwaysScrollablePages = ['/afteraccount', '/']; // 특정 경로 허용

watch(() => route.path, async () => {
  isRouteReady.value = false;
  await nextTick(); // 레이아웃 업데이트 후 반영
  
  // ✅ "/mypage"로 시작하는 모든 경로를 포함하여 스크롤 허용
  isScrollAllowed.value = alwaysScrollablePages.includes(route.path) || route.path.startsWith('/mypage');

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

// 네비바 높이 감지 및 업데이트
const navbarRef = ref(null);
const navbarHeight = ref(0);

const updateNavbarHeight = () => {
  if (navbarRef.value) {
    navbarHeight.value = navbarRef.value.offsetHeight;
  }
};

// ✅ 모바일 환경 감지
const isMobile = /iPhone|iPad|iPod|Android/i.test(navigator.userAgent);

// ✅ 모바일에서만 vh 값을 조정하는 함수
const setRealVH = () => {
  if (!isMobile) return;
  const vh = window.innerHeight * 0.01;
  document.documentElement.style.setProperty('--vh', `${vh}px`);
};

onMounted(() => {
  isScrollAllowed.value = alwaysScrollablePages.includes(route.path) || route.path.startsWith('/mypage');

  // ✅ 모바일에서만 실행
  if (isMobile) {
    setRealVH();
    window.addEventListener('resize', setRealVH);
  }

  // ✅ 네비바 높이 감지 (실시간 감지)
  const observer = new ResizeObserver(() => {
    updateNavbarHeight();
  });

  if (navbarRef.value) {
    observer.observe(navbarRef.value);
    updateNavbarHeight(); // 초기 값 설정
  }

  onUnmounted(() => {
    observer.disconnect();
    if (isMobile) {
      window.removeEventListener('resize', setRealVH);
    }
  });
});
</script>

<style>
/* ✅ 모바일 환경에서만 적용될 vh */
@media (max-width: 768px) {
  .h-screen-custom {
    height: calc(var(--vh, 1vh) * 100);
  }
}
</style>
