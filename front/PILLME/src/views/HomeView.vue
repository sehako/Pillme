<template>
  <div class="flex flex-col w-full">
    <undertopbar class="bg-[#B5CCB7] rounded-bl-xl rounded-br-lg">
      <div class="flex flex-row items-center justify-between px-4 py-1">
        <!-- ✅ 햄버거 메뉴 컴포넌트 -->
        <div class="flex-1 flex">
          <HamBugerMenu />
        </div>

        <!-- ✅ 사용자 이름 드롭다운 (컴포넌트 사용) -->
        <NameDropdown />

        <!-- ✅ 공백 (햄버거 아이콘과 크기 맞춤) -->
        <div class="flex-1"></div>
      </div>
    </undertopbar>

    <div class="grid gap-4 grid-cols-3 p-4">
      <BaseButton class="whitespace-nowrap text-lg font-base" @click="openFamilyModal">
        인원추가
      </BaseButton>
      <BaseButton class="whitespace-nowrap text-lg font-base">
        약정보검색
      </BaseButton>
      <BaseButton class="whitespace-nowrap text-lg font-base">
        알림설정
      </BaseButton>
    </div>

    <mainview>
      <!-- 오늘의 복약 내역 카드 -->
      <YellowCard class="m-4 flex flex-col">
        <div class="flex flex-row items-end">
          <p class="text-sm font-bold">오늘의 복약 내역</p>
          <span class="text-xs ml-2">
            <!-- 백엔드에서 받아온 약물 리스트가 있으면 약물명을 표시 -->
            {{ todaysMedications.length > 0 ? todaysMedications.map(med => med.name).join(', ') : "약정보 없음" }}
          </span>
        </div>
        <div class="flex flex-row items-end">
          <!-- store에서 가져온 알림시간을 기준으로 현재 시간대(예: 아침, 점심 등)를 동적으로 표시 -->
          <p class="font-bold text-lg">{{ currentTimePeriod }} 약을 드셨나요?</p>
          <!-- 체크 아이콘 클릭 시 복약 완료 처리 -->
          <img src="../assets/CheckCircle.svg" alt="약물복용체크" @click="completeMedications" class="cursor-pointer">
        </div>
      </YellowCard>

      <!-- 복용 내역 카드 (예시) -->
      <div class="m-4 flex flex-col">
        <p class="text-xl font-bold">복용 내역</p>
        <WhiteCard overrideClass="bg-white">
          <div class="flex flex-row items-center">
            <img src="../assets/logi_nofont.svg" alt="알약이미지" class="w-16 h-16">
            <div class="flex flex-col">
              <p>병명</p>
              <p>기간</p>
              <p>약이름</p>
            </div>
          </div>
        </WhiteCard>
      </div>

      <!-- 캘린더 (예시) -->
      <div class="m-4 flex flex-col">
        <VCalendar class="w-full"/>
      </div>
    </mainview>
  </div>
  <FamilyAddModal :isOpen="isFamilyModalOpen" @close="isFamilyModalOpen = false" />
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useNotificationSettingsStore } from '../stores/notificationSettingsStore';
import { storeToRefs } from 'pinia';

import BaseButton from '../components/BaseButton.vue';
import YellowCard from '../layout/YellowCard.vue';
import WhiteCard from '../layout/WhiteCard.vue';
import HamBugerMenu from '../components/HamBugerMenu.vue';
import NameDropdown from '../components/NameDropdown.vue';
import FamilyAddModal from '../components/FamilyAddModal.vue';
// 필요에 따라 VCalendar 컴포넌트도 import 합니다.

// ✅ 모달 상태 관리
const isFamilyModalOpen = ref(false);
const openFamilyModal = () => {
  isFamilyModalOpen.value = true;
};

// Pinia 스토어에서 알림 설정 정보를 불러옴
const notificationStore = useNotificationSettingsStore();
const { notificationSettings } = storeToRefs(notificationStore);

// 현재 시간대를 계산하는 computed 속성 (store의 알림 설정 값을 사용)
const currentTimePeriod = computed(() => {
  const now = new Date();
  const currentMinutes = now.getHours() * 60 + now.getMinutes();

  // 문자열 "HH:MM"을 분으로 변환하는 함수
  const parseTime = (timeStr) => {
    const [hour, minute] = timeStr.split(':').map(Number);
    return hour * 60 + minute;
  };

  // store에서 불러온 알림 시간 (기본값 제공)
  const morningTime = notificationSettings.value.morning || "07:00";
  const lunchTime = notificationSettings.value.lunch || "12:00";
  const dinnerTime = notificationSettings.value.dinner || "18:00";
  const bedtimeTime = notificationSettings.value.bedtime || "22:00";

  const morningMinutes = parseTime(morningTime);
  const lunchMinutes = parseTime(lunchTime);
  const dinnerMinutes = parseTime(dinnerTime);
  const bedtimeMinutes = parseTime(bedtimeTime);

  if (currentMinutes >= morningMinutes && currentMinutes < lunchMinutes) {
    return "아침";
  } else if (currentMinutes >= lunchMinutes && currentMinutes < dinnerMinutes) {
    return "점심";
  } else if (currentMinutes >= dinnerMinutes && currentMinutes < bedtimeMinutes) {
    return "저녁";
  } else {
    return "자기전";
  }
});
// 실제로는 오늘의 날짜 까지 계산해서 그에 해당하는 약물만 백엔드에서 쏴줄거임.
// 오늘의 날짜를 'YYYY-MM-DD' 형태로 구하는 함수
const getTodayDate = () => {
  const today = new Date();
  const year = today.getFullYear();
  const month = (today.getMonth() + 1).toString().padStart(2, '0');
  const date = today.getDate().toString().padStart(2, '0');
  return `${year}-${month}-${date}`;
};

// 오늘의 복약 내역(약물 리스트)을 담는 ref
const todaysMedications = ref([]);

// 백엔드와 소통하는 것처럼 오늘의 복약 내역 데이터를 가져오는 함수 (더미 데이터 사용)
const fetchTodaysMedications = async () => {
  const todayDate = getTodayDate();
  // 실제 API 호출 예시:
  // const response = await axios.get(`/api/medications?date=${todayDate}&timePeriod=${currentTimePeriod.value}`);
  // 더미 데이터 예시:
  const response = [
    {
      //처방전 = id, 개별약물 = prescriptionId, 약물명 = name, 시간대 = timePeriod, 복약여부 = taken
      id: 1,
      prescriptionId: 101,
      name: "약물A",
      timePeriod: currentTimePeriod.value,
      taken: false
    },
    {
      id: 2,
      prescriptionId: 102,
      name: "약물B",
      timePeriod: currentTimePeriod.value,
      taken: false
    }
  ];
  todaysMedications.value = response;
};

// 복약 완료 처리 함수 (사용자가 체크하면 호출)
const completeMedications = async () => {
  // 각 약물에 대해 완료 처리를 진행 (여기서는 로컬 상태 업데이트 후 백엔드 API 호출 예시)
  todaysMedications.value.forEach(async (med) => {
    med.taken = true;
    // 실제 API 호출 예시:
    // await axios.post('/api/medications/complete', { prescriptionId: med.prescriptionId, timePeriod: med.timePeriod });
  });
  alert("복약 완료 처리가 완료되었습니다.");
};

// 컴포넌트가 마운트되면 오늘의 복약 내역과 알림 설정을 가져옵니다.
onMounted(() => {
  fetchTodaysMedications();
  notificationStore.fetchNotificationSettings();
});
</script>
