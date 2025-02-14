<template>
  <div id="app" class="flex flex-row h-screen-custom">
    <!-- FCM 알림 컨테이너 -->
    <div class="notifications-container">
      <div 
        v-for="notification in notifications" 
        :key="notification.id"
        class="notification"
        :class="{ show: notification.show }"
      >
        <div class="notification-header">
          <h4>{{ notification.title }}</h4>
          <button 
            class="close-button"
            @click="removeNotification(notification.id)"
          >
            ×
          </button>
        </div>
        <p>{{ notification.body }}</p>
        <div 
          v-if="notification.data.code && ['DEPENDENCY_REQUEST', 'MEDICINE_REQUEST', 'DEPENDENCY_DELETE_REQUEST'].includes(notification.data.code)" 
          class="actions"
        >
          <button 
            class="accept-button"
            @click="() => handleAccept(notification)"
          >
            동의
          </button>
          <button 
            class="reject-button"
            @click="() => handleReject(notification)"
          >
            거절
          </button>
        </div>
      </div>
    </div>

    <!-- 왼쪽 (PC 전용) -->
    <div class="hidden md:flex flex-col w-1/2 bg-white items-center justify-center border-r border-gray-200 shadow-md p-6">
      <img :src="logo" alt="로고" class="w-1/2 h-auto" />
      <h1 class="text-xl sm:text-2xl font-bold text-gray-800 mt-6 text-center">
        모바일에서 <span class="text-[#4E7351]">PILLME</span>를 만나보세요!
      </h1>
      <div class="w-40 h-auto mt-6">
        <img src="./assets/fillmeqr.svg" alt="QR 코드" />
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
          isScrollAllowed ? 'overflow-y-auto overflow-x-hidden' : 'flex items-center justify-center overflow-hidden',
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

import BaseTopbar from './components/BaseTopbar.vue';
import BaseNavbar from './components/BaseNavbar.vue';
import OcrResultDialog from './components/OcrResultDialog.vue';
import AdditionalInfoDialog from './components/AdditionalInfoDialog.vue';
import MedicationScheduleDialog from './components/MedicationScheduleDialog.vue';

import logo from './assets/Logo_font.svg';

// 너무 길어서 각 로직을 composable로 분리
import { useAuth } from './composables/useAuth';
import { useRealVH } from './composables/useRealVH';
import { useScrollControl } from './composables/useScrollControl';
import { useNavbarHeight } from './composables/useNavbarHeight';
import { useFCM } from './utils/usefcm';

const contentRef = ref(null);
const navbarRef = ref(null);
const ocrStore = useOcrStore();
const isRouteReady = ref(true);

const { isLoggedIn, initAuth, cleanUpAuth } = useAuth();
const { initRealVH, cleanUpRealVH } = useRealVH();
const { isScrollAllowed } = useScrollControl(['/afteraccount', '/', '/notificationlist']);
const { navbarHeight } = useNavbarHeight(navbarRef);
const { 
  notifications, 
  removeNotification,
  handleAccept,
  handleReject,
  initializeFCM
} = useFCM();

onMounted(() => {
  initAuth();
  initRealVH();
  ocrStore.loadFromLocalStorage();

  // // ✅ OCR 분석이 진행 중이 아닐 경우 로딩 제거
  // if (!ocrStore.showResultsDialog && !ocrStore.showNextDialog && !ocrStore.showMedicationDialog) {
  //   ocrStore.isLoading = false;
  //   localStorage.setItem('ocrIsLoading', JSON.stringify(false)); // ✅ localStorage에서도 초기화
  // }
  
    // ✅ OCR 분석이 진행 중이면 로딩 상태 유지, 분석이 끝난 경우 다이얼로그 자동 닫기
    watch(
    () => ocrStore.isLoading,
    (newVal) => {
      if (newVal) {
        ocrStore.showResultsDialog = false;
        ocrStore.showNextDialog = false;
        ocrStore.showMedicationDialog = false;
        // ocrStore.isLoading = false;
        // localStorage.setItem('ocrIsLoading', JSON.stringify(false));
      }
    }
  );
  ocrStore.showResultsDialog = false;
  ocrStore.showNextDialog = false;
  ocrStore.showMedicationDialog = false;
  ocrStore.isLoading = false;
  // localStorage.setItem('ocrIsLoading', JSON.stringify(false));
  initializeFCM();
});

onUnmounted(() => {
  cleanUpAuth();
  cleanUpRealVH();
});
</script>

<style>
.h-screen-custom {
  height: 100vh;
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
  padding: 15px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  transform: translateX(120%);
  transition: transform 0.3s ease;
}

.notification.show {
  transform: translateX(0);
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.notification-header h4 {
  margin: 0;
  font-weight: 600;
}

.close-button {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  padding: 0 5px;
}

.actions {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.accept-button, .reject-button {
  flex: 1;
  padding: 8px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  font-weight: 500;
  transition: opacity 0.2s;
}

.accept-button {
  background-color: #4E7351;
  color: white;
}

.reject-button {
  background-color: #DC2626;
  color: white;
}

.accept-button:hover, .reject-button:hover {
  opacity: 0.9;
}

@media (max-width: 768px) {
  .notifications-container {
    width: calc(100% - 40px);
  }

  .notification {
    width: 100%;
  }
}
</style>