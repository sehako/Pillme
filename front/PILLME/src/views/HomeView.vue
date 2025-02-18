<template>
  <div class="flex flex-col w-full">
    <div class="bg-[#B5CCB7] rounded-bl-xl rounded-br-lg top-0 sticky z-50">
      <div class="flex flex-row items-center justify-between px-4 py-1">
        <!-- 베이스탑바 바로 밑의 바임 -->
        <!--  햄버거 메뉴 컴포넌트 -->
        <div class="flex-1 flex">
          <HamBugerMenu />
        </div>

        <!--  사용자 이름 드롭다운 (컴포넌트 사용) -->
        <NameDropdown />

        <!--  공백 (햄버거 아이콘과 크기 맞춤) -->
        <div class="flex-1"></div>
      </div>
    </div>

    <div class="grid gap-4 grid-cols-3 p-4">
      <BaseButton class="whitespace-nowrap text-lg font-base" @click="openFamilyModal">
        인원추가
      </BaseButton>
      <BaseButton class="whitespace-nowrap text-lg font-base" @click="openSearchDialog">
        약정보검색
      </BaseButton>
      <BaseButton class="whitespace-nowrap text-lg font-base" @click="openSetAlarmModal">
  알림설정
</BaseButton>

    </div>

    <main>
<!-- 오늘의 복약 내역 카드 -->
<YellowCard class="m-4 flex flex-col">
  <div class="flex flex-row items-end">
    <p class="text-sm font-bold">오늘의 복약 내역</p>
    <span class="text-xs ml-2">
      <div v-if="fetchFailed">
        알림 설정을 활성화해야 오늘의 복약 알림을 받을 수 있습니다.
      </div>
      <!-- <div v-else>
        {{ todaysMedications ? todaysMedications : "약정보 없음" }}
      </div> -->
    </span>
  </div>
  
  <div class="flex flex-col items-left">
    <p class="font-bold text-lg">
      {{ fetchFailed ? '' : `${currentTimePeriod} 약을 드셨나요?` }}
    </p>
    <p v-if="!fetchFailed && todaysMedications" class="text-sm text-gray-700 mt-1">
      {{ todaysMedications }}
    </p>

    <!-- ✅ 체크박스 + 텍스트 (오른쪽 정렬) -->
    <div class="flex justify-left items-center -ml-4">
      <!-- ✅ 체크 아이콘 -->
      <img 
        v-if="!fetchFailed" 
        :src="isMedicationCompleted ? CheckDoneboxes : Checkboxes"
        alt="약물복용체크"
        @click="completeMedications"
        class="cursor-pointer transition duration-300 transform hover:scale-110 hover:opacity-80"
      />

      <!-- ✅ 오른쪽에 텍스트 추가 -->
      <span 
        v-if="!isMedicationCompleted"
        class="text-sm text-gray-500 opacity-80 transition-opacity duration-300"
      >
        클릭해서 복약 완료!
      </span>

      <!-- ✅ 완료 후 텍스트 (✅로 변경되면 표시) -->
      <transition name="slide-fade">
        <span 
          v-if="isMedicationCompleted" 
          class="text-green-600 font-bold text-sm transition-opacity duration-500 ease-in-out"
        >
          복약 완료!
        </span>
      </transition>
    </div>
  </div>
</YellowCard>




<div class="m-4 flex flex-col">
    <!-- 헤더 영역 -->
    <div class="flex justify-between items-center mb-2">
      <p class="text-xl font-bold">복용 내역</p>
      <button @click="fetchPrescriptionHistory" class="text-sm text-gray-500 hover:underline">
        과거 복용내역 조회 ▷
      </button>
    </div>

  <!-- 가로 스크롤 가능한 화이트카드 영역 -->
  <div class="scroll-container flex overflow-x-auto space-x-4 p-2">
    <WhiteCard 
      v-for="(info, index) in managementInfoList" 
      :key="index"
      overrideClass="bg-white min-w-[300px] max-w-[300px] flex-shrink-0 relative p-4 overflow-hidden"
    >
      <!-- 병원 정보 (오른쪽 상단, 회색 & 작은 글씨) -->
      <p class="absolute top-2 right-3 text-xs text-gray-400 truncate max-w-[150px]">
        {{ info.hospital || "병원 정보 없음" }}
      </p>

      <div class="flex flex-row items-center">
        <img src="../assets/logi_nofont.svg" alt="알약이미지" class="w-16 h-16">
        <div class="flex flex-col ml-4 max-w-[200px]">
          <!-- 병명이 없으면 "병명 미등록" -->
          <p class="font-bold text-lg truncate max-w-[200px]">{{ info.diseaseName || "병명 미등록" }}</p>

          <!-- 날짜 (회색 & 작은 글씨) -->
          <p class="text-xs text-gray-500 truncate max-w-[200px]">{{ info.medicationPeriod }}</p>

          <!-- 약 이름 (회색 & 작은 글씨) -->
          <p class="text-xs text-gray-500 mt-1 truncate max-w-[200px]">
            {{ info.medications || "약 정보 없음" }}
          </p>
        </div>
      </div>
    </WhiteCard>
  </div>
</div>



      <!-- 캘린더 (예시) -->
      <div class="m-4 flex flex-col">
        <BaseCalendar :prescriptions="managementInfoList" />

      </div>
    </main>
  </div>

  <HistoryModal v-if="showModal" :prescriptions="modalData" @close="handleModalClose" />

  <MedicationSearchDialog ref="medSearchDialog" />
  <FamilyAddModal :isOpen="isFamilyModalOpen" @close="isFamilyModalOpen = false" />
  <Teleport to="body">
  <div v-if="isAlarmModalOpen" class="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
    <div class="bg-white p-4 rounded-lg shadow-lg relative w-[500px] h-[600px]">
      <button @click="closeSetAlarmModal" class="absolute top-2 right-2 text-gray-500 hover:text-gray-800">
        ✕
      </button>
      <MyAlarmModal :isModal="true" />
    </div>
  </div>
</Teleport>



</template>

<script setup>
import { ref, computed, onMounted,watchEffect } from 'vue';
import { fetchAllDrugCheck } from '../api/drugcheck';
import BaseButton from '../components/BaseButton.vue';
import YellowCard from '../layout/YellowCard.vue';
import WhiteCard from '../layout/WhiteCard.vue';
import HamBugerMenu from '../components/HamBugerMenu.vue';
import NameDropdown from '../components/NameDropdown.vue';
import FamilyAddModal from '../components/FamilyAddModal.vue';
import MedicationSearchDialog from '../components/MedicationSearchDialog.vue';
import BaseCalendar from '../components/BaseCalendar.vue';
import { defineAsyncComponent } from 'vue';
import { fetchManagementData, fetchFormattedManagementInfo  } from '../api/drugmanagement';
import HistoryModal from '../components/HistoryModal.vue'; // 모달 컴포넌트 import
import CheckDoneboxes from '../assets/CheckDoneboxes.svg';
import Checkboxes from '../assets/Checkboxes.svg';
import { useNotificationSettings } from '../composables/useNotificationSettings'; // Composable import
import { usePrescriptionHistory } from "../composables/usePrescriptionHistory";
// 모달 제어용 상태 변수
const { modalData, showModal, fetchPrescriptionHistory } = usePrescriptionHistory();
//  My_Alarm.vue를 동적으로 import (모달에서만 로드)
const MyAlarmModal = defineAsyncComponent(() => import('../views/My_Alarm.vue'));

// / ✅ Composable 사용
const { notificationSettings, fetchFailed, loadNotificationSettings } = useNotificationSettings();

// ✅ 복약 완료 상태
const isMedicationCompleted = ref(false);

defineProps({
  navbarHeight: Number, //  props 정의
});

//  모달 상태 관리
const isFamilyModalOpen = ref(false);
const openFamilyModal = () => {
  isFamilyModalOpen.value = true;
};
const isAlarmModalOpen = ref(false);
const openSetAlarmModal = () => {
  isAlarmModalOpen.value = true;
};
const closeSetAlarmModal = () => {
  isAlarmModalOpen.value = false;
};
const medSearchDialog = ref(null);
const openSearchDialog = () => {
  medSearchDialog.value.openDialog();
};


//알림모달크기조절
const modalSize = ref("md"); // "sm", "md", "lg"

const modalClass = computed(() => {
  return {
    sm: "w-[300px] h-[400px]",
    md: "w-[500px] h-[600px]",
    lg: "w-[80%] max-w-lg"
  }[modalSize.value];
});


//  외부 클릭 감지 함수
const handleClickOutside = (event) => {
  // 예를 들어 특정 모달이 열려 있을 때, 모달 외부를 클릭하면 닫히도록 처리 가능
  if (isFamilyModalOpen.value) {
    const modal = document.querySelector('.modal-class'); // 실제 모달 클래스에 맞게 변경
    if (modal && !modal.contains(event.target)) {
      isFamilyModalOpen.value = false;
    }
  }
};

// 현재 시간대를 계산하는 computed 속성 (설정된 알림 시간만 기준으로)
const currentTimePeriod = computed(() => {
  const now = new Date();
  const currentMinutes = now.getHours() * 60 + now.getMinutes();

  // 문자열 "HH:MM"을 분 단위로 변환하는 함수
  const parseTime = (timeStr) => {
    if (!timeStr) return null;
    const [hour, minute] = timeStr.split(':').map(Number);
    return hour * 60 + minute;
  };

  // 설정된 시간대만 객체 배열로 생성 (null인 값은 제외)
  const periods = [];
  const morning = parseTime(notificationSettings.morning);
  if (morning !== null) {
    periods.push({ label: "아침", minutes: morning });
  }
  const lunch = parseTime(notificationSettings.lunch);
  if (lunch !== null) {
    periods.push({ label: "점심", minutes: lunch });
  }
  const dinner = parseTime(notificationSettings.dinner);
  if (dinner !== null) {
    periods.push({ label: "저녁", minutes: dinner });
  }
  const sleep = parseTime(notificationSettings.sleep);
  if (sleep !== null) {
    periods.push({ label: "자기전", minutes: sleep });
  }

  // 설정된 시간대가 하나도 없으면 빈 문자열 반환
  if (periods.length === 0) return "";

  // 시간 순서대로 정렬 (오름차순)
  periods.sort((a, b) => a.minutes - b.minutes);

  // 현재 시간이 첫 번째 설정된 시간보다 빠르면 첫 번째 시간대 반환
  if (currentMinutes < periods[0].minutes) {
    return periods[0].label;
  }

  // 설정된 시간대 중에서 현재 시간에 해당하는 시간대를 찾음
  for (let i = 0; i < periods.length; i++) {
    // 마지막 요소인 경우
    if (i === periods.length - 1) {
      return periods[i].label;
    }
    // 현재 시간이 두 시간대 사이에 있으면 앞쪽 시간대를 반환
    if (currentMinutes >= periods[i].minutes && currentMinutes < periods[i + 1].minutes) {
      return periods[i].label;
    }
  }

  // 기본적으로 마지막 시간대를 반환 (이론상 도달하지 않음)
  return periods[periods.length - 1].label;
});


// 오늘의 복약 내역(약물 리스트)을 담는 ref
const todaysMedications = ref([]);


// ✅ 백엔드에서 오늘의 복약 내역 가져오기
const fetchTodaysMedications = async () => {
  try {
    const data = await fetchManagementData();
    todaysMedications.value = data.result
      ? data.result.map(med => med.medicationName).join(", ")
      : "약 정보 없음";
      if (data.result && data.result.length > 0) {
        const periodMap = {
          "아침": { period: "morning", taking: "morningTaking" },
    "점심": { period: "lunch", taking: "lunchTaking" },
    "저녁": { period: "dinner", taking: "dinnerTaking" },
    "자기전": { period: "sleep", taking: "sleepTaking" }
  };
  console.log("먹는 약 정보들", data.result);
  
  const currentPeriod = periodMap[currentTimePeriod.value];
  console.log("시간", currentPeriod);
  if (currentPeriod) {
    const medicationsForPeriod = data.result.filter(med => med[currentPeriod.period]);

    // ✅ 모든 약의 taking 값이 true이면 복약 완료
    isMedicationCompleted.value = medicationsForPeriod.length > 0 &&
      medicationsForPeriod.every(med => med[currentPeriod.taking]);
    }
  }
    
  } catch (error) {
    console.error("❌ [DEBUG] 복약 리스트 가져오기 실패:", error);
    todaysMedications.value = "데이터 불러오기 실패";
  }
};

// ✅ `managementInfoList` 추가 (처방전 데이터 저장)
const managementInfoList = ref([]);

// ✅ API에서 `managementInfoList` 가져오는 함수
const fetchData = async () => {
  try {
    const data = await fetchFormattedManagementInfo();

    managementInfoList.value = data.prescriptions.length > 0
      ? data.prescriptions.map(prescription => {
          // ✅ medicationPeriod에서 YYYY-MM-DD 형식의 날짜 추출
          const periodMatch = prescription.medicationPeriod.match(/(\d{4}-\d{2}-\d{2})/g);
          const startDate = periodMatch?.[0] || null;
          const endDate = periodMatch?.[1] || null;

          return {
            ...prescription,
            startDate,
            endDate
          };
        })
      : [{ diseaseName: "복용 내역 없음", medicationPeriod: "", medications: "", hospital: "", startDate: null, endDate: null }];
  } catch (error) {
    console.error("❌ [DEBUG] Management 정보 로드 실패:", error);
    managementInfoList.value = [{ diseaseName: "데이터 불러오기 실패", medicationPeriod: "", medications: "", hospital: "", startDate: null, endDate: null }];
  }
};



function handleModalClose() {
  showModal.value = false;
  modalData.value = []; // 필요에 따라 초기화
}
// ✅ 복약 완료 처리 함수
const completeMedications = async () => {
  try {
    if (isMedicationCompleted.value) {
      alert("이미 복약 완료 처리되었습니다.");
      return;
    }

    const periodMap = { "아침": "morning", "점심": "lunch", "저녁": "dinner", "자기전": "sleep" };
    const timePeriod = periodMap[currentTimePeriod.value];

    if (!timePeriod) {
      alert("현재 시간대를 인식할 수 없습니다.");
      return;
    }

    await fetchAllDrugCheck(timePeriod);

    // ✅ 복약 완료 처리 성공 시 UI 업데이트
    isMedicationCompleted.value = true;

    alert("복약 완료 처리에 성공했습니다!");
  } catch (error) {
    console.error("❌ 복약 완료 처리 실패:", error);
    alert("복약 완료 처리에 실패했습니다.");
  }
};
watchEffect(() => {
  if (currentTimePeriod.value) { // ✅ 값이 존재하는지 확인
    console.log("✅ 현재 시간대:", currentTimePeriod.value);
    fetchTodaysMedications(); // ✅ `currentTimePeriod.value`가 설정된 후 실행
  }
});

//  컴포넌트가 마운트되면 데이터 및 이벤트 리스너 등록
onMounted(async () => {
  
  await fetchData();
  // 알림 설정 불러오기
  await loadNotificationSettings(); // Composable 함수 호출

  // 클릭 이벤트 리스너 등록
  document.addEventListener("click", handleClickOutside);


});
</script>
<style scoped>
.scroll-container {
  scrollbar-width: thin;
  scrollbar-color: #ccc transparent;
}

/* ✅ Chrome, Edge, Safari용 */
.scroll-container {
  scrollbar-width: thin;
  scrollbar-color: #ccc transparent;
}

/* ✅ Chrome, Edge, Safari용 */
.scroll-container::-webkit-scrollbar {
  height: 8px;
}

.scroll-container::-webkit-scrollbar-thumb {
  background-color: #ccc;
  border-radius: 4px;
}

.scroll-container::-webkit-scrollbar-track {
  background-color: transparent;
}


</style>
