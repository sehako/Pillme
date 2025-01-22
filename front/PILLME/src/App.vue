<template>
  <div id="app">
    <header>
      <h1>PILLME</h1>
      <p v-if="isOffline" style="color: red">🚨 현재 오프라인 상태입니다.</p>

      <!-- ✅ PWA 설치 버튼 (설치 가능할 때만 표시) -->
      <button v-if="deferredPrompt" @click="installPWA" class="install-button">
        📲 PWA 설치하기
      </button>
    </header>

    <router-view />

    <!-- ✅ PWA 업데이트 알림 (새 버전이 있을 때 표시됨) -->
    <div v-if="isUpdateAvailable" class="update-notification" @click="refreshApp">
      🔄 새로운 업데이트가 있습니다. 클릭하여 새로고침하세요.
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

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
  deferredPrompt.value = event; // ✅ PWA 설치 가능 상태 저장
};

/** ✅ PWA 설치 실행 */
const installPWA = async () => {
  if (!deferredPrompt.value) return;
  deferredPrompt.value.prompt();
  const choiceResult = await deferredPrompt.value.userChoice;
  if (choiceResult.outcome === 'accepted') {
    console.log('✅ PWA 설치 완료'); // 🚨 배포 시 주석 처리 필요
  }
  deferredPrompt.value = null; // ✅ 설치 후 버튼 숨김
};

/** ✅ PWA 업데이트 감지 */
const checkForUpdates = () => {
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.getRegistration().then((registration) => {
      if (registration && registration.waiting) {
        isUpdateAvailable.value = true; // ✅ 새 버전이 있음
      }
    });
  }
};

/** ✅ PWA 업데이트 적용 및 새로고침 */
const refreshApp = () => {
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.getRegistration().then((registration) => {
      if (registration && registration.waiting) {
        registration.waiting.postMessage({ type: 'SKIP_WAITING' });
        window.location.reload();
      }
    });
  }
};

onMounted(() => {
  console.log('PWA 앱이 시작되었습니다!'); // 🚨 배포 시 주석 처리 필요

  window.addEventListener('online', updateNetworkStatus);
  window.addEventListener('offline', updateNetworkStatus);
  window.addEventListener('beforeinstallprompt', handleBeforeInstallPrompt);

  // ✅ 서비스 워커 업데이트 감지
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

/* ✅ PWA 설치 버튼 스타일 */
.install-button {
  display: block;
  margin: 10px auto;
  padding: 10px 15px;
  background: #007aff;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
}

.install-button:hover {
  background: #005ecb;
}

/* ✅ PWA 업데이트 알림 스타일 */
.update-notification {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  background: #42b883;
  color: white;
  padding: 10px 20px;
  border-radius: 5px;
  font-size: 14px;
  cursor: pointer;
}
</style>
<!-- 추후 pwa 설치 유도 알림 구현해서 넣어야함. -->
