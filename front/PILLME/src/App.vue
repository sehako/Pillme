<template>
  <div id="app" class="relative flex flex-col md:flex-row min-h-screen pb-16">
    
    <!-- ✅ 왼쪽 (웹에서는 보이지만 모바일에서는 숨김) -->
    <div class="hidden md:block w-1/2 bg-gray-100"></div>

    <!-- ✅ 오른쪽 (모바일에서는 전체 화면 차지) -->
    <div class="flex flex-col justify-center items-center w-full md:w-1/2 bg-white">
      
      <!-- ✅ 상단 바 -->
      <BaseTopbar />

      <header class="p-4 text-center w-full">
        <p v-if="isOffline" class="text-red-500 font-semibold">🚨 현재 오프라인 상태입니다.</p>
        <button v-if="deferredPrompt" @click="installPWA" 
          class="block mx-auto mt-2 px-4 py-2 bg-blue-500 text-white rounded-lg text-lg hover:bg-blue-600 transition">
          📲 PWA 설치하기
        </button>
      </header>

      <!-- ✅ 현재 페이지의 콘텐츠 -->
      <router-view class="max-w-xs sm:max-w-sm md:max-w-md lg:max-w-lg w-full pb-20" />

      <div v-if="isUpdateAvailable" @click="refreshApp" 
        class="fixed bottom-20 left-1/2 transform -translate-x-1/2 bg-green-500 text-white px-4 py-2 rounded-lg text-sm cursor-pointer shadow-md">
        🔄 새로운 업데이트가 있습니다. 클릭하여 새로고침하세요.
      </div>
    </div>

    <!-- ✅ 네비게이션 바 (모든 페이지 공통, 모바일 w-full / 웹에서는 w-1/2) -->
    <BaseNavbar class="fixed bottom-0 right-0 w-full md:w-1/2 md:right-0" />
  </div>
</template>


<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import BaseNavbar from "./components/BaseNavbar.vue";
import BaseTopbar from "./components/BaseTopbar.vue";

const isOffline = ref(!navigator.onLine);
const isUpdateAvailable = ref(false);
const deferredPrompt = ref(null); // ✅ PWA 설치 프롬프트 저장

/** ✅ 네트워크 상태 감지 */
const updateNetworkStatus = () => {
  isOffline.value = !navigator.onLine;
};

/** ✅ PWA 설치 이벤트 감지 */
const handleBeforeInstallPrompt = (event) => {
  event.preventDefault();
  deferredPrompt.value = event;
};

/** ✅ PWA 설치 실행 */
const installPWA = async () => {
  if (!deferredPrompt.value) return;
  deferredPrompt.value.prompt();
  const choiceResult = await deferredPrompt.value.userChoice;
  if (choiceResult.outcome === 'accepted') {
    console.log('✅ PWA 설치 완료');
  }
  deferredPrompt.value = null;
};

/** ✅ PWA 업데이트 감지 */
const checkForUpdates = () => {
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.getRegistration().then((registration) => {
      if (registration && registration.waiting) {
        isUpdateAvailable.value = true;
      }
      registration?.addEventListener('updatefound', () => {
        if (registration.waiting) {
          isUpdateAvailable.value = true;
        }
      });
    });
  }
};

/** ✅ PWA 업데이트 적용 및 새로고침 */
const refreshApp = () => {
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.getRegistration().then((registration) => {
      if (registration && registration.waiting) {
        registration.waiting.postMessage({ type: 'SKIP_WAITING' });
        navigator.serviceWorker.addEventListener('controllerchange', () => {
          window.location.reload();
        });
      }
    });
  }
};

onMounted(() => {
  window.addEventListener('online', updateNetworkStatus);
  window.addEventListener('offline', updateNetworkStatus);
  window.addEventListener('beforeinstallprompt', handleBeforeInstallPrompt);

  if (window.matchMedia('(display-mode: standalone)').matches) {
    deferredPrompt.value = null;
  }

  checkForUpdates();
});

onUnmounted(() => {
  window.removeEventListener('online', updateNetworkStatus);
  window.removeEventListener('offline', updateNetworkStatus);
  window.removeEventListener('beforeinstallprompt', handleBeforeInstallPrompt);
});
</script>

<style>
body {
  font-family: Arial, sans-serif;
  margin: 0;
  padding: 0;
  background: #f4f4f4;
}
</style>

<!-- 추후 pwa 설치 유도 알림 구현해서 넣어야함. -->
