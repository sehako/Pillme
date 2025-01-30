<template>
  <div id="app" class="flex flex-row min-h-screen">
    
    <!-- ✅ 왼쪽 (웹에서는 보이지만 모바일에서는 숨김) -->
    <div class="hidden md:block w-1/2"></div>

    <!-- ✅ 오른쪽 (모바일에서는 전체 화면 차지) -->
    <div class="relative w-full md:w-1/2">


        <!-- ✅ 상단 바 (div로 감싸서 id/class 부여) -->
        <div id="topbar" class="absolute inset-x-0 top-0 w-full z-10">
          <BaseTopbar />
        </div>
 

      <!-- ✅ 헤더 영역 -->
       <!-- 이 부분이 문제가 되고 있음. pwa 캐싱 관련 이슈. vite에서 개발시 pwa 안쓰도록 설정해놓음 추후 연결해서 해결  -->
      <!-- <header class="text-center w-full mt-2">
        <p v-if="isOffline" class="text-red-500 font-semibold">
          🚨 현재 오프라인 상태입니다.
        </p>
        <button
          v-if="deferredPrompt"
          @click="installPWA"
          class="block mx-auto px-4 py-2 bg-blue-500 text-white rounded-lg text-lg hover:bg-blue-600 transition"
        >
          📲 PWA 설치하기
        </button>
      </header> -->

      <!-- ✅ 현재 페이지의 콘텐츠 (기본적으로 스크롤 없음, 허용된 페이지만 스크롤 가능) -->
      <div
        :class="{ 'overflow-y-auto': isScrollAllowed }"
        class=""

      >
        <router-view />
      </div>

      <!-- ✅ 네비게이션 바 (div로 감싸서 id/class 부여) -->
      <div
        id="navbar"
        class="absolute inset-x-0 bottom-0 w-full z-10"
      >
        <BaseNavbar />
      </div>
      <!-- ✅ 업데이트 알림 -->
      <!-- <div
        v-if="isUpdateAvailable"
        @click="refreshApp"
        class="bottom-20 left-1/2 transform -translate-x-1/2 bg-green-500 text-white px-4 py-2 rounded-lg text-sm cursor-pointer shadow-md"
      >
        🔄 새로운 업데이트가 있습니다. 클릭하여 새로고침하세요.
      </div> -->
    </div>

  </div>
</template>

<script setup>
/**
 * Vue & Router
 */
import { ref, onMounted, onUnmounted, watch } from "vue";
import { useRoute } from "vue-router";

/**
 * 컴포넌트 import
 */
import BaseNavbar from "./components/BaseNavbar.vue";
import BaseTopbar from "./components/BaseTopbar.vue";

/**
 * 라우트 관련
 */
const route = useRoute();

/**
 * 스크롤 허용 여부 & 레이아웃 계산 로직
 */
const isScrollAllowed = ref(false);
const topbarHeight = ref(0);
const navbarHeight = ref(0);
const computedHeight = ref("100vh");

const scrollablePages = ["/scroll-page-1", "/scroll-page-2"];
const checkScrollPermission = () => {
  isScrollAllowed.value = scrollablePages.includes(route.path);
  document.body.style.overflow = isScrollAllowed.value ? "auto" : "hidden";
};

const updateLayout = () => {
  const topbar = document.querySelector("#topbar");
  const navbar = document.querySelector("#navbar");
  // 혹시 DOM을 못 찾을 경우 대비 (null 확인)
  topbarHeight.value = topbar ? topbar.offsetHeight : 0;
  navbarHeight.value = navbar ? navbar.offsetHeight : 0;
  // 100vh에서 상단/하단 바 높이를 뺀 값
  computedHeight.value = `calc(100vh - ${topbarHeight.value}px - ${navbarHeight.value}px)`;
};

watch(
  () => route.path,
  () => {
    checkScrollPermission();
    updateLayout();
  }
);

/**
 * PWA 관련 상태 및 로직
 */
const isOffline = ref(!navigator.onLine); // 오프라인 여부
const deferredPrompt = ref(null);         // PWA 설치 프롬프트
const isUpdateAvailable = ref(false);     // 서비스 워커 업데이트 감지

// 네트워크 상태 감지
const updateNetworkStatus = () => {
  isOffline.value = !navigator.onLine;
};

// beforeinstallprompt 이벤트 핸들러
const handleBeforeInstallPrompt = (event) => {
  event.preventDefault();
  deferredPrompt.value = event;
};

// PWA 설치
const installPWA = async () => {
  if (!deferredPrompt.value) return;
  deferredPrompt.value.prompt();
  const choiceResult = await deferredPrompt.value.userChoice;
  if (choiceResult.outcome === "accepted") {
    console.log("✅ PWA 설치 완료");
  }
  deferredPrompt.value = null;
};

// 서비스 워커 업데이트 감지
const checkForUpdates = () => {
  if ("serviceWorker" in navigator) {
    navigator.serviceWorker.getRegistration().then((registration) => {
      if (registration && registration.waiting) {
        isUpdateAvailable.value = true;
      }
      registration?.addEventListener("updatefound", () => {
        if (registration.waiting) {
          isUpdateAvailable.value = true;
        }
      });
    });
  }
};

// 업데이트 적용 및 새로고침
const refreshApp = () => {
  if ("serviceWorker" in navigator) {
    navigator.serviceWorker.getRegistration().then((registration) => {
      if (registration && registration.waiting) {
        registration.waiting.postMessage({ type: "SKIP_WAITING" });
        navigator.serviceWorker.addEventListener("controllerchange", () => {
          window.location.reload();
        });
      }
    });
  }
};

/**
 * 마운트 시점에 이벤트 등록
 */
onMounted(() => {
  // 레이아웃 계산
  updateLayout();
  window.addEventListener("resize", updateLayout);

  // 네트워크 상태 감지
  window.addEventListener("online", updateNetworkStatus);
  window.addEventListener("offline", updateNetworkStatus);

  // PWA 설치 이벤트
  window.addEventListener("beforeinstallprompt", handleBeforeInstallPrompt);

  // PWA 업데이트 확인
  checkForUpdates();

  // 만약 이미 standalone(설치된) 모드라면 설치 프롬프트 null 처리
  if (window.matchMedia("(display-mode: standalone)").matches) {
    deferredPrompt.value = null;
  }
});

/**
 * 언마운트 시점에 이벤트 해제
 */
onUnmounted(() => {
  window.removeEventListener("resize", updateLayout);
  window.removeEventListener("online", updateNetworkStatus);
  window.removeEventListener("offline", updateNetworkStatus);
  window.removeEventListener("beforeinstallprompt", handleBeforeInstallPrompt);
});
</script>

<style scoped>
/* 필요하다면 전역 스타일 */
</style>
