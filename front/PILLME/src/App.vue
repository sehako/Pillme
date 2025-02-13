<template>
  <div id="app" class="flex flex-row h-screen-custom">
    <!-- 왼쪽 (PC 전용) -->
<div class="hidden md:flex flex-col w-1/2 bg-white items-center justify-center border-r border-gray-200 shadow-md p-6">
  <img :src="logo" alt="로고 이미지" class="w-1/2 h-auto" />
  
  <h1 class="text-xl sm:text-2xl font-bold text-gray-800 mt-6 text-center">
    모바일에서 <span class="text-[#4E7351]">PILLME</span>를 만나보세요!
  </h1>

  <div class="w-40 h-auto mt-6">
    <img src="./assets/fillmeqr.svg" alt="QR 코드 이미지">
  </div>
</div>



    <!-- 오른쪽 (모바일 전체) -->
    <div class="flex flex-col w-full md:w-1/2 relative">
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

      <!-- ✅ OCR 분석 중 로딩 표시 -->
      <div v-if="ocrStore.isLoading" class="loading-overlay">
        <div class="loading-spinner"></div>
      </div>

      <!-- 하단 바 -->
      <div ref="navbarRef" class="relative z-10 bg-white">
        <BaseNavbar />
      </div>
    </div>

    <!-- ✅ OCR 관련 다이얼로그 (모든 페이지에서 표시 가능) -->
    <OcrResultDialog v-if="ocrStore.showResultsDialog" />
    <AdditionalInfoDialog v-if="ocrStore.showNextDialog" />
    <MedicationScheduleDialog v-if="ocrStore.showMedicationDialog" />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue';
import { useRoute } from 'vue-router';
import { useOcrStore } from './stores/ocrStore';

import BaseTopbar from './components/BaseTopbar.vue';
import BaseNavbar from './components/BaseNavbar.vue';

import OcrResultDialog from './components/OcrResultDialog.vue';
import AdditionalInfoDialog from './components/AdditionalInfoDialog.vue';
import MedicationScheduleDialog from './components/MedicationScheduleDialog.vue';

import logo from './assets/Logo_font.svg';

const route = useRoute();
const ocrStore = useOcrStore();
const contentRef = ref(null);
const isRouteReady = ref(true);

// 특정 라우트에서 스크롤 허용
const isScrollAllowed = ref(false);
const alwaysScrollablePages = ['/afteraccount', '/', '/notificationlist']; // 특정 경로 허용

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
const isMobile = window.matchMedia("(pointer:coarse)").matches || /iPhone|iPad|iPod|Android/i.test(navigator.userAgent);

// ✅ 모바일에서만 vh 값을 조정하는 함수
const setRealVH = () => {
  if (!isMobile) return;
  const vh = window.innerHeight * 0.01;
  document.documentElement.style.setProperty('--vh', '${vh}px');
};

// ✅ OCR 분석 감지 및 다이얼로그 표시
onMounted(() => {
  isScrollAllowed.value = alwaysScrollablePages.includes(route.path) || route.path.startsWith('/mypage');

  if (isMobile) {
    setRealVH();
    window.addEventListener('resize', setRealVH);
  }

  // ✅ 저장된 OCR 상태 불러오기
  ocrStore.loadFromLocalStorage();

  // ✅ OCR 분석이 진행 중이면 로딩 상태 유지
  watch(() => ocrStore.isLoading, (newVal) => {
    if (!newVal && ocrStore.results.length > 0 && !ocrStore.showNextDialog && !ocrStore.showMedicationDialog) {
      ocrStore.showResultsDialog = true;
    }
  });

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
.h-screen-custom {
  height: 100vh; /* 기본적으로 100vh 사용 */
}

@media (max-width: 768px) {
  .h-screen-custom {
    height: calc(var(--vh, 1vh) * 100);
  }
}

/* ✅ OCR 분석 중 로딩 오버레이 */
.loading-overlay {
  position: fixed;
  top: 20px;
  right: 20px;
  width: 60px;
  height: 60px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
  z-index: 1000;
}

.loading-spinner {
  width: 30px;
  height: 30px;
  border: 3px solid rgba(0, 0, 0, 0.3);
  border-top-color: #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
