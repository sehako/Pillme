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
      <!-- 아침 알림 -->
      <div>
        <label for="morning" class="block mb-1 font-medium">아침 알림 시간</label>
        <input 
          id="morning"
          type="time" 
          v-model="morningTime" 
          @change="updateTime('morning', morningTime)"
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
          v-model="lunchTime" 
          @change="updateTime('lunch', lunchTime)"
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
          v-model="dinnerTime" 
          @change="updateTime('dinner', dinnerTime)"
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
          v-model="bedtimeTime" 
          @change="updateTime('bedtime', bedtimeTime)"
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
const isLoading = ref(true); // 최초에는 로딩 상태로 설정

// 로컬 상태로 알림 설정 기본값 관리 (기본값은 null로 설정)
const notificationSettings = reactive({
  enabled: false,     // 알림 활성화 여부
  morning: null,      // 아침 알림 시간
  lunch: null,        // 점심 알림 시간
  dinner: null,       // 저녁 알림 시간
  bedtime: null,      // 자기 전 알림 시간
  settingsCreated: false
});

// ✅ fetchNotificationSettings 실행
const loadNotificationSettings = async () => {
  try {
    const data = await fetchNotificationSettings();

    // ✅ 배열이면 "HH:MM" 형식으로 변환, 아니라면 그대로 사용
    const formatTime = (value) => {
      if (Array.isArray(value) && value.length === 2) {
        // 시간이 1자리면 앞에 0 붙이기 (01:03 형식 유지)
        const hours = String(value[0]).padStart(2, "0");
        const minutes = String(value[1]).padStart(2, "0");
        return `${hours}:${minutes}`;
      }
      return value ?? "00:00"; // null일 경우 "00:00" 설정
    };

    // ✅ 데이터 변환 적용
    notificationSettings.morning = formatTime(data.morning);
    notificationSettings.lunch = formatTime(data.lunch);
    notificationSettings.dinner = formatTime(data.dinner);
    notificationSettings.bedtime = formatTime(data.sleep);
    notificationSettings.settingsCreated = true;
    notificationSettings.enabled = true; // ✅ 성공 시 알림 활성화
  } catch (error) {
    console.error("🚨 알림 설정 로드 실패:", error);
    notificationSettings.enabled = false; // ✅ 실패 시 비활성화
    notificationSettings.settingsCreated = false;
  } finally {
    isLoading.value = false; // ✅ 설정 불러오기가 완료된 후, isLoading을 false로 설정
  }
};


// computed 프로퍼티를 사용하여 null일 때는 '00:00'으로 표시
const morningTime = computed({
  get() {
    return notificationSettings.morning === null ? '00:00' : notificationSettings.morning;
  },
  set(value) {
    notificationSettings.morning = value;
  }
});

const lunchTime = computed({
  get() {
    return notificationSettings.lunch === null ? '00:00' : notificationSettings.lunch;
  },
  set(value) {
    notificationSettings.lunch = value;
  }
});

const dinnerTime = computed({
  get() {
    return notificationSettings.dinner === null ? '00:00' : notificationSettings.dinner;
  },
  set(value) {
    notificationSettings.dinner = value;
  }
});

const bedtimeTime = computed({
  get() {
    return notificationSettings.bedtime === null ? '00:00' : notificationSettings.bedtime;
  },
  set(value) {
    notificationSettings.bedtime = value;
  }
});

// ✅ 체크박스 변경 시 자동 요청 (불필요한 요청 방지)
const toggleNotificationSetting = async () => {
  if (isLoading.value) {
    console.log('⏳ 알림 설정 로드 중, 자동 요청 방지');
    return; // ✅ 설정이 로딩 중이면 실행하지 않음
  }

  try {
    if (notificationSettings.enabled) {
      const requestData = {
        morning: morningTime.value === "00:00" ? null : morningTime.value,
        lunch: lunchTime.value === "00:00" ? null : lunchTime.value,
        dinner: dinnerTime.value === "00:00" ? null : dinnerTime.value,
        sleep: bedtimeTime.value === "00:00" ? null : bedtimeTime.value,
      };

      // 활성화 → POST 요청
      await createNotificationSetting(requestData);
      notificationSettings.settingsCreated = true;
      console.log('✅ 알림 설정 활성화됨', requestData);
    } else {
      // 비활성화 → DELETE 요청
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
      const requestData = { [field]: value === "00:00" ? null : value };

      // PUT 요청으로 개별 시간 업데이트
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
