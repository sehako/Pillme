// src/stores/notificationSettingsStore.js
import { defineStore } from 'pinia';

export const useNotificationSettingsStore = defineStore('notificationSettings', {
  state: () => ({
    // 기본 알림 설정 상태 (예시)
    notificationSettings: {
      id: null,
      userId: 123, // 예시 사용자 번호
      enabled: true,
      morning: '',
      lunch: '',
      dinner: '',
      bedtime: ''
    }
  }),
  actions: {
    // 백엔드에서 데이터를 불러오는 것을 모방한 함수 (더미 데이터 사용)
    async fetchNotificationSettings() {
      const dummyResponse = {
        id: 1,
        userId: this.notificationSettings.userId,
        enabled: true,
        morning: "07:00",
        lunch: "12:00",
        dinner: "18:00",
        bedtime: "22:00"
      };
      // API 호출을 모방 (예: 500ms 지연)
      await new Promise((resolve) => setTimeout(resolve, 500));
      this.notificationSettings = dummyResponse;
    },
    // 백엔드에 업데이트하는 것을 모방한 함수
    async updateNotificationSettings(updatedSettings) {
      // API 업데이트를 모방 (예: 500ms 지연)
      await new Promise((resolve) => setTimeout(resolve, 500));
      this.notificationSettings = updatedSettings;
      return true;
    }
  }
});
