<template>
  <div class="flex flex-col h-screen p-6">
    <!-- 뒤로 가기 버튼 -->
    <BackButton class="mb-4" />

    <!-- 페이지 타이틀 -->
    <h1 class="text-xl font-bold mb-4">알림 설정</h1>

    <!-- 알림 설정 폼 -->
    <form @submit.prevent="updateNotificationSettings" class="space-y-4">
      <!-- 아침 알림 -->
      <div>
        <label for="morning" class="block mb-1 font-medium">아침 알림 시간</label>
        <!-- input type="time"는 HH:MM 형식의 문자열을 반환합니다.
             백엔드에서 timestamp로 관리하더라도, 저장 전 변환할 수 있습니다. -->
        <input 
          id="morning"
          type="time" 
          v-model="notificationSettings.morning" 
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
import { ref, onMounted } from 'vue';
import BackButton from '../components/BackButton.vue';
import BaseButton from '../components/BaseButton.vue';
// 로그인 시 전달받은 사용자 번호 (실제 앱에서는 스토어나 props 등을 통해 전달받음)
const userId = 123;

// 알림 설정 객체
// id: 알림설정번호(pk), userId: 사용자 번호(fk)
// 아침, 점심, 저녁, 자기전 값은 타임스탬프(혹은 시간 문자열)로 관리
const notificationSettings = ref({
  id: null,
  userId: userId,
  morning: '',  // 예: "07:00"
  lunch: '',    // 예: "12:00"
  dinner: '',   // 예: "18:00"
  bedtime: ''   // 예: "22:00"
});

// 백엔드에서 현재 사용자의 알림 설정 정보를 가져오는 함수
const fetchNotificationSettings = async () => {
  // 실제 환경에서는 axios나 fetch를 사용하여 API 호출
  // 아래는 예시 더미 데이터입니다.
  const response = {
    id: 1,          // 알림설정번호 (개인별 부여)
    userId: userId, // 사용자 번호 (fk)
    morning: "07:00",
    lunch: "12:00",
    dinner: "18:00",
    bedtime: "22:00"
  };
  notificationSettings.value = response;
};

// 알림 설정 정보를 저장(업데이트)하는 함수
const updateNotificationSettings = async () => {
  // 필요에 따라 입력된 HH:MM 형식의 값을 timestamp로 변환할 수 있음.
  // 예를 들어, 오늘 날짜를 기준으로 생성할 수 있습니다.
  // 여기서는 단순히 console.log와 alert로 시뮬레이션합니다.
  console.log("업데이트할 알림 설정:", notificationSettings.value);

  // 실제 API 호출 예시 (axios 등 사용)
  // await axios.put('/api/notification-settings', notificationSettings.value);

  alert("알림 설정이 저장되었습니다.");
};

onMounted(() => {
  fetchNotificationSettings();
});
</script>
