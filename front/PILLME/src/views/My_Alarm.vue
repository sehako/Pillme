<template>
  <div class="flex flex-col h-screen p-6">
    <!-- 뒤로 가기 버튼 -->
    <BackButton class="mb-4" />

    <!-- 페이지 타이틀 -->
    <h1 class="text-xl font-bold mb-4">알림 설정</h1>

    <!-- 알림 활성화 토글 박스 -->
    <div class="mb-4">
      <label class="inline-flex items-center">
        <input 
          type="checkbox" 
          v-model="notificationSettings.enabled" 
          class="form-checkbox h-5 w-5 text-green-600"
        />
        <span class="ml-2">알림 활성화</span>
      </label>
    </div>

    <!-- 알림 설정 폼 -->
    <form @submit.prevent="updateNotificationSettings" class="space-y-4">
      <!-- 아침 알림 -->
      <div>
        <label for="morning" class="block mb-1 font-medium">아침 알림 시간</label>
        <input 
          id="morning"
          type="time" 
          v-model="notificationSettings.morning" 
          :disabled="!notificationSettings.enabled"
          class="border rounded p-2 w-full"
        />
      </div>

      <!-- 점심 알림 -->
      <div>
        <label for="lunch" class="block mb-1 font-medium">점심 알림 시간</label>
        <input 
          id="lunch"
          type="time" 
          v-model="notificationSettings.lunch" 
          :disabled="!notificationSettings.enabled"
          class="border rounded p-2 w-full"
        />
      </div>

      <!-- 저녁 알림 -->
      <div>
        <label for="dinner" class="block mb-1 font-medium">저녁 알림 시간</label>
        <input 
          id="dinner"
          type="time" 
          v-model="notificationSettings.dinner" 
          :disabled="!notificationSettings.enabled"
          class="border rounded p-2 w-full"
        />
      </div>

      <!-- 자기 전 알림 -->
      <div>
        <label for="bedtime" class="block mb-1 font-medium">자기 전 알림 시간</label>
        <input 
          id="bedtime"
          type="time" 
          v-model="notificationSettings.bedtime" 
          :disabled="!notificationSettings.enabled"
          class="border rounded p-2 w-full"
        />
      </div>

      <!-- 저장 버튼 -->
      <BaseButton 
        type="submit" 
        class="w-full bg-[#4E7351] text-white rounded hover:bg-[#3D5A3F]"
      >
        저장
      </BaseButton>
    </form>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useNotificationSettingsStore } from '../stores/notificationSettingsStore';
import { storeToRefs } from 'pinia';
import BackButton from '../components/BackButton.vue';
import BaseButton from '../components/BaseButton.vue';

// Pinia 스토어 불러오기
const notificationStore = useNotificationSettingsStore();
// storeToRefs를 사용하여 상태를 반응형으로 바인딩 (템플릿에서 편리하게 사용)
const { notificationSettings } = storeToRefs(notificationStore);

// 알림 설정 저장 함수 (Pinia 스토어를 통해 업데이트)
const updateNotificationSettings = async () => {
  console.log("업데이트할 알림 설정:", notificationSettings.value);
  await notificationStore.updateNotificationSettings(notificationSettings.value);
  alert("알림 설정이 저장되었습니다.");
};

// 컴포넌트 마운트 시 스토어에서 현재 알림 설정 정보를 불러옴
onMounted(() => {
  notificationStore.fetchNotificationSettings();
});
</script>
