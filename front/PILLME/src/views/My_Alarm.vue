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
import { reactive, computed, onMounted, ref } from 'vue';
import BackButton from '../components/BackButton.vue';
import { 
  createNotificationSetting, 
  updateNotificationSetting, 
  deleteNotificationSetting,
  fetchNotificationSettings,
} from '../api/setalarm';

// ✅ 불필요한 자동 요청 방지용 플래그
const isLoading = ref(true); // 로딩 중 여부
const fetchFailed = ref(false); // fetch 실패 여부

// ✅ 알림 설정 상태
const notificationSettings = reactive({
  enabled: false,     // 알림 활성화 여부
  settingsCreated: false // 알림 설정이 생성된 상태인지
});

// ✅ 알람 시간 데이터 (computed 사용 X → reactive 사용)
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

    // ✅ 데이터 그대로 적용
    alarmTimes.morning = data.morning;
    alarmTimes.lunch = data.lunch;
    alarmTimes.dinner = data.dinner;
    alarmTimes.sleep = data.bedtime;

    notificationSettings.settingsCreated = true;

    // ✅ null이 아닌 값이 하나라도 있으면 활성화 상태로 표시
    const hasActiveAlarm = Object.values(alarmTimes).some(time => time !== null);
    notificationSettings.enabled = hasActiveAlarm;
    fetchFailed.value = false; // 성공 시 fetchFailed 초기화

  } catch (error) {
    console.error("🚨 알림 설정 로드 실패:", error);
    fetchFailed.value = true; // 실패 시 fetchFailed 설정
    notificationSettings.enabled = false;
    notificationSettings.settingsCreated = false;
  } finally {
    isLoading.value = false; // ✅ 로딩 완료
  }
};

// ✅ 체크박스 클릭 시에만 자동 요청 실행 + 기본값 설정
const toggleNotificationSetting = async (event) => {
  if (isLoading.value) {
    console.log('⏳ 알림 설정 로드 중, 자동 요청 방지');
    return;
  }

  if (!event.isTrusted) {
    // ✅ 불러온 데이터에 의해 변경된 경우에는 실행하지 않음
    return;
  }

  try {
    if (notificationSettings.enabled) {
      // ✅ 모든 값이 null이면 하나만 기본값 "00:00"으로 설정
      if (Object.values(alarmTimes).every(time => time === null)) {
        alarmTimes.morning = "00:00"; // 기본값 설정
      }

      const requestData = { ...alarmTimes };
      await createNotificationSetting(requestData);
      notificationSettings.settingsCreated = true;
      console.log('✅ 알림 설정 활성화됨', requestData);
    } else {
      if (notificationSettings.settingsCreated) {
        await deleteNotificationSetting();
        notificationSettings.settingsCreated = false;
        console.log('❌ 알림 설정 비활성화됨');
      }
    }
  } catch (error) {
    console.error('🚨 알림 설정 변경 실패:', error);
  }
};

// ✅ 개별 시간 변경 시 자동 요청
const updateTime = async (field, value) => {
  try {
    if (notificationSettings.settingsCreated) {
      const requestData = { [field]: value };
      await updateNotificationSetting(requestData);
      console.log(`⏰ ${field} 알림 시간이 ${value}로 업데이트됨`);
    }
  } catch (error) {
    console.error(`🚨 ${field} 알림 시간 업데이트 실패:`, error);
  }
};

// ✅ 컴포넌트 마운트 시 실행
onMounted(loadNotificationSettings);
</script>
