<template>
  <div id="app" class="flex flex-row h-screen-custom">
    <!-- FCM 알림 컨테이너 -->
    <div class="notifications-container">
      <div v-for="notification in notifications" :key="notification.id" class="notification" :class="{
        show: notification.show,
        toast: notification.isToast,
      }">
        <div class="notification-header">
          <h4>{{ notification.title }}</h4>
          <button class="close-button" @click="removeNotification(notification.id)">×</button>
        </div>
        <p v-if="notification.body && notification.body.trim()">{{ notification.body }}</p>
        <div v-if="
          notification.data?.code &&
          ['DEPENDENCY_REQUEST', 'MEDICINE_REQUEST', 'DEPENDENCY_DELETE_REQUEST'].includes(
            notification.data.code
          )
        " class="actions">
          <BaseButton class="accept-button" overrideClass="!min-w-0 w-1/2" @click="() => handleAccept(notification)">동의</BaseButton>
          <BaseButton class="reject-button" overrideClass="!min-w-0 w-1/2 hover:bg-[#b91c1c]" @click="() => handleReject(notification)">거절</BaseButton>
        </div>
      </div>
    </div>

    <!-- 왼쪽 (PC 전용) -->
    <div
      class="hidden md:flex flex-col w-1/2 bg-white items-center justify-center border-r border-gray-200 shadow-md p-6">
      <img :src="logo" alt="로고" class="w-1/2 h-auto mb-8" />

      <h1 class="text-xl sm:text-2xl font-bold text-gray-800 flex flex-wrap items-center justify-center gap-2">
        <span class="mx-0">모바일에서</span>
        <img :src="textLogoSrc" alt="PILLME" class="h-6 inline-block object-contain" />
        <span class="mx-0">를 만나보세요!</span>
      </h1>

      <div class="w-40 h-auto mt-8">
        <img src="./assets/fillmeqr.svg" alt="QR 코드" class="w-full h-auto" />
      </div>
    </div>

    <!-- 오른쪽 (모바일 전체) -->
    <div class="flex flex-col w-full md:w-1/2 relative">
      <div v-if="isLoggedIn" ref="topbarRef" class="relative z-10 items-start">
        <BaseTopbar />
      </div>

      <div
  ref="contentRef"
  :class="[
    'h-screen-custom',
    'scrollable',
    isScrollAllowed
      ? 'overflow-y-auto overflow-x-hidden'
      : 'flex items-center justify-center overflow-hidden',
  ]"
>
        <router-view v-if="isRouteReady" :navbarHeight="navbarHeight" />
      </div>

      <div v-if="ocrStore.isLoading" class="loading-overlay">
        <div class="loading-spinner"></div>
      </div>

      <div class="dialog-container">
        <OcrResultDialog v-if="ocrStore.showResultsDialog" />
        <AdditionalInfoDialog v-if="ocrStore.showNextDialog" />
        <MedicationScheduleDialog v-if="ocrStore.showMedicationDialog" />
      </div>

      <div v-if="isLoggedIn" ref="navbarRef" class="relative z-10 bg-white">
        <BaseNavbar />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue';
import { useOcrStore } from './stores/ocrStore';
import { useAuthStore } from './stores/auth'; 
import BaseButton from './components/BaseButton.vue';

import BaseTopbar from './components/BaseTopbar.vue';
import BaseNavbar from './components/BaseNavbar.vue';
import OcrResultDialog from './components/OcrResultDialog.vue';
import AdditionalInfoDialog from './components/AdditionalInfoDialog.vue';
import MedicationScheduleDialog from './components/MedicationScheduleDialog.vue';

import logo from './assets/Logo_font.svg';
import textLogoSrc from './assets/Logi_font.svg';

// 컴포저블 import
import { useAuth } from './composables/useAuth';
import { useRealVH } from './composables/useRealVH';
import { useScrollControl } from './composables/useScrollControl';
import { useNavbarHeight } from './composables/useNavbarHeight';
import { useFCM } from './utils/usefcm';

const contentRef = ref(null);
const navbarRef = ref(null);
const ocrStore = useOcrStore();
const isRouteReady = ref(true);

// 컴포저블 설정
const authStore = useAuthStore();
const { isLoggedIn, initAuth, cleanUpAuth } = useAuth();
const { initRealVH, cleanUpRealVH } = useRealVH();
const { isScrollAllowed } = useScrollControl(['/afteraccount', '/', '/notificationlist','/calendar','/chat']);
const { navbarHeight } = useNavbarHeight(navbarRef);
const { notifications, removeNotification, handleAccept, handleReject, initializeFCM, cleanupFCM } = useFCM();

// visibility 변경 감지 핸들러
const handleVisibilityChange = async () => {
  if (document.visibilityState === 'visible') {
    console.log('앱이 포그라운드로 전환됨');
    if (isLoggedIn.value) {
      try {
        await authStore.checkAndRefreshToken();
      } catch (error) {
        console.error('토큰 갱신 실패:', error);
        // 토큰 갱신 실패 시 로그아웃 처리
        cleanUpAuth();
      }
    }
  }
};

onMounted(() => {
  initAuth();
  initRealVH();
  ocrStore.loadFromLocalStorage();
  
  // visibility 이벤트 리스너 추가
  document.addEventListener('visibilitychange', handleVisibilityChange);

  // OCR 관련 상태 초기화
  watch(
    () => ocrStore.isLoading,
    (newVal) => {
      if (newVal) {
        ocrStore.showResultsDialog = false;
        ocrStore.showNextDialog = false;
        ocrStore.showMedicationDialog = false;
      }
    }
  );

  ocrStore.showResultsDialog = false;
  ocrStore.showNextDialog = false;
  ocrStore.showMedicationDialog = false;
  ocrStore.isLoading = false;
});

// 로그인 상태 변경 시, fcm 초기화
watch(
  isLoggedIn,
  async (newValue) => {
    if (newValue === true) {
      try {
        await initializeFCM();
        // 토큰 유효성 즉시 체크 추가
        await authStore.checkAndRefreshToken();
      } catch (error) {
        console.error('초기화 실패:', error);
        cleanUpAuth();
      }
    }
  },
  { immediate: true } // 즉시 실행 옵션 추가
);
onUnmounted(() => {
  cleanUpAuth();
  cleanUpRealVH();
  cleanupFCM();
  // visibility 이벤트 리스너 제거 추가
  document.removeEventListener('visibilitychange', handleVisibilityChange);
});
</script>

<style>
.h-screen-custom {
  height: 100vh; /* 기본값 */
  height: -webkit-fill-available; /* iOS Safari 대응 */
  height: calc(var(--vh, 1vh) * 100); /* 계산된 값 (우선순위 높음) */
}

@supports (-webkit-touch-callout: none) {
  /* iOS 디바이스만 적용 */
  .h-screen-custom {
    min-height: -webkit-fill-available;
  }
}

@media (max-width: 768px) {
  .h-screen-custom {
    height: calc(var(--vh, 1vh) * 100);
  }
}

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
  to {
    transform: rotate(360deg);
  }
}

/* FCM 알림 관련 스타일 */
.notifications-container {
  position: fixed;
  right: 20px;
  top: 20px;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.notification {
  width: 300px;
  background: transparent;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transform: translateX(120%);
  transition: transform 0.3s ease;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.notification.show {
  transform: translateX(0);
}

.notification.toast {
  background-color: rgba(0, 0, 0, 0.8);
  color: white;
}

.notification.toast p {
  background: white;
  color: black;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #FFE2E2;
  padding: 12px 15px;
  width: 100%;
}

.notification-header h4 {
  margin: 0;
  font-weight: 600;
  color: #000;
}

.notification p {
  padding: 15px;
  margin: 0;
  background: white;
}

.notification.toast .notification-header h4 {
  background: #FFE2E2;
}

.close-button {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #000;
}

.notification.toast .close-button {
  color: #000;
}

.actions {
  padding: 15px;
  display: flex;
  gap: 10px;
  width: 100%;
  box-sizing: border-box;
  background: white;
}

.notification p + .actions {
  padding-top: 0;
}

.actions button {
  flex: 1;
}

.accept-button,
.reject-button {
  width: 100%;
  box-sizing: border-box;
  padding: 10px;
  border-radius: 100px;
  font-weight: 600;
}

.accept-button {
  background-color: #4e7351;
  color: white;
}

.reject-button {
  background-color: #dc2626;
  color: white;
}

.accept-button:hover,
.reject-button:hover {
  opacity: 0.9;
}

@media (max-width: 768px) {
  .notifications-container {
    width: calc(100% - 40px);
  }

  .notification {
    width: 100%;
  }

  .notification.toast {
    bottom: 80px;
  }
}

/* Firefox */
.scrollable {
  scrollbar-width: none;
}

/* Chrome, Safari, Opera */
.scrollable::-webkit-scrollbar {
  display: none;
}

</style>
