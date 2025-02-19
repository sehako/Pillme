<template>
  <div class="flex flex-col h-screen p-6">
    <!-- 뒤로 가기 버튼 (모달이 아닐 때만 표시) -->
    <BackButton v-if="!isModal" class="mb-4" />

    <!-- 페이지 타이틀 -->
    <h1 class="text-xl font-bold mb-4">알림 설정</h1>

    <!-- 알림 활성화 토글 박스 -->
    <div class="mb-4">
      <label class="inline-flex items-center">
        <input 
          type="checkbox" 
          v-model="notificationSettings.enabled" 
          @change="toggleNotificationSetting"
          class="form-checkbox h-5 w-5 text-green-600"
        />
        <span class="ml-2">알림 활성화</span>
      </label>
    </div>

    <!-- 알림 설정 폼 -->
    <div class="space-y-4">
      <div v-for="(label, key) in alarmLabels" :key="key">
        <label :for="key" class="block mb-1 font-medium">{{ label }}</label>
        <input 
          :id="key"
          type="time" 
          v-model="alarmTimes[key]" 
          @change="updateTime(key, alarmTimes[key])"
          :disabled="!notificationSettings.enabled"
          class="border rounded p-2 w-full"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import BackButton from '../components/BackButton.vue';
import { 
  createNotificationSetting, 
  updateNotificationSetting, 
  deleteNotificationSetting,
  fetchNotificationSettings,
} from '../api/setalarm';

// ✅ `props` 추가 (모달인지 여부 판별)
defineProps({
  isModal: {
    type: Boolean,
    default: false
  }
});

// ✅ 알림 설정 상태
const notificationSettings = reactive({
  enabled: false,     // 알림 활성화 여부
  settingsCreated: false // 알림 설정이 생성된 상태인지
});

// ✅ 알람 시간 데이터
const alarmTimes = reactive({
  morning: null, 
  lunch: null, 
  dinner: null, 
  sleep: null
});

// ✅ 알람 시간 라벨 매핑
const alarmLabels = {
  morning: "아침 알림 시간",
  lunch: "점심 알림 시간",
  dinner: "저녁 알림 시간",
  sleep: "자기 전 알림 시간"
};

// ✅ fetchNotificationSettings 실행
const loadNotificationSettings = async () => {
  try {
    const data = await fetchNotificationSettings();
    alarmTimes.morning = data.morning;
    alarmTimes.lunch = data.lunch;
    alarmTimes.dinner = data.dinner;
    alarmTimes.sleep = data.bedtime;

    notificationSettings.settingsCreated = true;

    // ✅ 하나라도 설정된 값이 있으면 활성화
    const hasActiveAlarm = Object.values(alarmTimes).some(time => time !== null);
    notificationSettings.enabled = hasActiveAlarm;
  } catch (error) {
    console.error("🚨 알림 설정 로드 실패:", error);
    notificationSettings.enabled = false;
    notificationSettings.settingsCreated = false;
  }
};

// ✅ 체크박스 클릭 시 알림 설정 활성/비활성
const toggleNotificationSetting = async () => {
  try {
    if (notificationSettings.enabled) {
      if (Object.values(alarmTimes).every(time => time === null)) {
        alarmTimes.morning = "00:00"; // 기본값 설정
      }
      await createNotificationSetting({ ...alarmTimes });
      notificationSettings.settingsCreated = true;
    } else {
      if (notificationSettings.settingsCreated) {
        await deleteNotificationSetting();
        notificationSettings.settingsCreated = false;
      }
    }
  } catch (error) {
    console.error('🚨 알림 설정 변경 실패:', error);
  }
};

// ✅ 개별 시간 변경 시 업데이트
const updateTime = async (field, value) => {
  try {
    if (notificationSettings.settingsCreated) {
      await updateNotificationSetting({ [field]: value });
    }
  } catch (error) {
    console.error(`🚨 ${field} 알림 시간 업데이트 실패:`, error);
  }
};

// ✅ 마운트 시 실행
onMounted(loadNotificationSettings);
</script>
