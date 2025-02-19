<template>
  <div class="flex flex-col w-full">
    <div class="bg-[#B5CCB7] rounded-bl-xl rounded-br-lg top-0 sticky z-50 filter saturate-105 contrast-140">
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
  <!-- 헤더 영역 -->
  <div class="flex flex-col sm:flex-row sm:items-end gap-1 sm:gap-2">
    <p class="text-sm font-bold whitespace-nowrap">오늘의 복약 내역</p>
    <span class="text-xs">
      <div v-if="fetchFailed" class="text-wrap sm:text-nowrap text-xs">
        알림 설정을 활성화해야
        <br class="sm:hidden" />
        오늘의 복약 알림을 받을 수 있습니다.
      </div>
    </span>
  </div>

  <!-- 컨텐츠 영역 -->
  <div class="flex flex-col mt-2">
    <p class="font-bold text-base sm:text-lg break-keep">
      {{ fetchFailed ? '' : `${currentTimePeriod} 약을 드셨나요?` }}
    </p>
    <!-- 약 정보 표시 부분 수정 -->
    <p 
      v-if="!fetchFailed && todaysMedications" 
      class="text-sm text-gray-700 mt-1 break-words line-clamp-1 cursor-pointer"
      @click="openTodaysMedicationModal"
    >
      {{ formatMedications(todaysMedications) }}
    </p>

    <!-- 체크박스 영역 -->
    <div class="flex items-center gap-2 mt-2">
      <img 
        v-if="!fetchFailed" 
        :src="isMedicationCompleted ? CheckDoneboxes : Checkboxes"
        alt="약물복용체크"
        @click="completeMedications"
        class="cursor-pointer transition duration-300 transform hover:scale-110 hover:opacity-80 w-8 h-8 -ml-1"
      />
      <!-- 상태 텍스트 -->
      <div class="flex-shrink">
        <span 
          v-if="!fetchFailed && !isMedicationCompleted"
          class="text-sm text-gray-500 whitespace-nowrap"
        >
          클릭해서 복약 완료!
        </span>
        <transition name="slide-fade">
          <span 
            v-if="!fetchFailed && isMedicationCompleted" 
            class="text-green-600 font-bold text-sm whitespace-nowrap"
          >
            복약 완료!
          </span>
        </transition>
      </div>
    </div>
  </div>
</YellowCard>


<div class="m-4 flex flex-col">
    <!-- 헤더 영역 -->
    <div class="flex justify-between items-center !mb-2">
      <p class="text-xl font-bold">현재 복용 중인 처방전</p>
      <button @click="fetchPrescriptionHistory" class="text-sm text-gray-500 hover:underline self-end">
        전체 복용내역 조회 ▷
      </button>
    </div>
  <!-- 가로 스크롤 가능한 화이트카드 영역 -->
<!-- 가로 스크롤 가능한 화이트카드 영역 -->
<div class="scroll-container flex overflow-x-auto space-x-4">
  <WhiteCard
    v-for="(info, index) in managementInfoList"
    :key="index"
    class="bg-white border border-gray-300 rounded-lg
           w-[300px] min-w-[300px] max-w-[300px] h-[109.143px]
           p-4 flex-shrink-0 transition-all duration-300 ease-in-out
           grid"
    style="grid-template-columns: 166.857px 100px;"
  >
    <!-- 왼쪽 (1,2,3) -->
    <div class="grid grid-rows-[auto_auto_1fr] gap-2">
      <!-- (1) 병명 -->
      <p class="font-bold text-lg truncate">
        {{ info.diseaseName || "병명 미등록" }}
      </p>

      <!-- (2) 날짜 -->
      <p class="text-xs text-gray-500">
        {{ info.medicationPeriod }}
      </p>

      <!-- (3) 약 정보 (2줄 초과 시 ... 처리, 클릭 시 모달로 전체 보기) -->
      <p
  class="text-xs text-gray-500 line-clamp-1 break-words whitespace-normal cursor-pointer"
  @click="openMedicationModal(info)"
>
  {{ formatMedications(info.medications) }}
</p>
    </div>

    <!-- 오른쪽 (4,5) -->
    <div class="grid grid-rows-2">
      <!-- (4) 병원이름 (오른쪽 상단) -->
      <p class="text-xs text-gray-500 whitespace-nowrap justify-self-end self-start">
        {{ info.hospital || "병원 정보 없음" }}
      </p>
      <!-- (5) 수정하기 (오른쪽 하단) -->
      <button
        class="text-xs text-gray-500 hover:underline whitespace-nowrap justify-self-end self-end"
        @click="openEditModal(info, modalClass)"
      >
        수정하기
      </button>
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

  <FamilyAddModal @click.self="closeModal('family')" :isOpen="isFamilyModalOpen" @close="isFamilyModalOpen = false" />
  
  <Teleport to="body">
  <div v-if="isAlarmModalOpen" @click.self="closeModal('alarm')" class="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
    <div class="bg-white p-4 rounded-lg shadow-lg relative w-[500px] h-[600px]">
      <button @click="closeSetAlarmModal" class="absolute top-2 right-2 text-gray-500 hover:text-gray-800">
        ✕
      </button>
      <MyAlarmModal :isModal="true" />
    </div>
  </div>
</Teleport>

<Teleport to="body">
      <HomeNowDrugCardEditModal 
        v-if="isEditModalOpen" 
        :info="selectedInfo" 
        modalClass="w-full h-4/5 max-w-[calc(100vw-32px)] sm:max-w-md mx-4" 
        @thisdrugcheck="handleAllDrugCheck"
    @alldrugcheck="handleAllDrugCheck"
    @close="closeEditModal"
      />
    </Teleport>
 <!--WHITECARD 약 클릭시 모달 -->
 <div
    v-if="showMedicationModal"
    @click.self="closeModal('medication')"
    class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50"
  >
    <div class="bg-white p-6 rounded-lg max-w-lg w-full">
      <h2 class="text-xl font-bold mb-4">약 정보 전체보기</h2>
      <!-- 반점(,)으로 구분된 약 정보를 리스트로 정리 -->
      <ul class="list-disc list-inside text-sm text-gray-700">
        <li v-for="(med, index) in medicationList" :key="index">
          {{ med }}
        </li>
      </ul>
      <button
        class="mt-4 text-blue-500 hover:underline"
        @click="closeMedicationModal"
      >
        닫기
      </button>
    </div>
  </div>

  <!--YELLOCARD의 약 목록 클릭시 모달 -->
<div
  v-if="showTodaysMedicationModal"
  @click.self="closeModal('todaysMedication')"
  class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50"
>
  <div class="bg-white p-6 rounded-lg max-w-lg w-full">
    <h2 class="text-xl font-bold mb-4">오늘의 복약 내역</h2>
    <p class="text-lg mb-4">{{ currentTimePeriod }} 복용 약</p>
    <ul class="list-disc list-inside text-sm text-gray-700">
      <li v-for="(med, index) in todaysMedicationList" :key="index">
        {{ med }}
      </li>
    </ul>
    <button
      class="mt-4 text-blue-500 hover:underline"
      @click="closeTodaysMedicationModal"
    >
      닫기
    </button>
  </div>
</div>
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
import { fetchManagementData, fetchFormattedManagementInfo  } from '../api/drugmanagement';
import HistoryModal from '../components/HistoryModal.vue'; // 모달 컴포넌트 import
import CheckDoneboxes from '../assets/CheckDoneboxes.svg';
import Checkboxes from '../assets/Checkboxes.svg';
import { useNotificationSettings } from '../composables/useNotificationSettings'; // Composable import
import { usePrescriptionHistory } from "../composables/usePrescriptionHistory";
import HomeNowDrugCardEditModal from '../components/HomeNowDrugCardEditModal.vue'; // 모달 컴포넌트
import { prescriptionAllCheck } from '../api/drugtaking';

// ----------------- Composable import 및 초기화 -----------------
// ✅ Composable 사용
const { notificationSettings, fetchFailed, loadNotificationSettings } = useNotificationSettings();
const { 
  modalData, 
  showModal, 
  fetchPrescriptionHistory,
  handleModalClose  // 추가
} = usePrescriptionHistory();

// ----------------- 동적 컴포넌트 import -----------------
//  My_Alarm.vue를 동적으로 import (모달에서만 로드)
const MyAlarmModal = defineAsyncComponent(() => import('../views/My_Alarm.vue'));

// -----------------  Props 정의 -----------------
defineProps({
  navbarHeight: Number, //  props 정의
});

// -----------------  Ref 및 Computed 속성 선언 (상태 변수 관리) -----------------
// ✅ 복약 완료 상태
const isMedicationCompleted = ref(false);

//  모달 상태 관리
const isFamilyModalOpen = ref(false);
const isAlarmModalOpen = ref(false);
const medSearchDialog = ref(null);
const isEditModalOpen = ref(false);
const selectedInfo = ref(null);
const showMedicationModal = ref(false)
//알림모달크기조절
const modalSize = ref("md"); // "sm", "md", "lg"

// 오늘의 복약 내역(약물 리스트)을 담는 ref
const todaysMedications = ref([]);

// 생략된 약 정보를 보여주는 ref
const selectedMedication = ref('')

// ✅ `managementInfoList` 추가 (처방전 데이터 저장)
const managementInfoList = ref([]);

const modalClass = computed(() => {
  return {
    sm: "w-[300px] h-[400px]",
    md: "w-[500px] h-[600px]",
    lg: "w-[80%] max-w-lg"
  }[modalSize.value];
});

// 약 이름 포맷팅 유틸리티 함수
const formatMedications = (medications) => {
  if (!medications || medications === "약 정보 없음") return "약 정보 없음";
  // 배열인 경우 처리
  if (Array.isArray(medications)) {
    return medications.join(" • ");
  }
  // 문자열인 경우 처리
  return medications.split("◎").join(" • ");
};
// selectedMedication를 콤마로 구분하여 배열로 변환한 computed 변수
const medicationList = computed(() => {
  if (!selectedMedication.value) return [];
  return selectedMedication.value
    .split("◎")  // 쉼표(,) 대신 ◎ 사용
    .map(item => item.trim())
    .filter(item => item);
});

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

// ----------------- 모달 제어 함수 (열고 닫기) -----------------
const openFamilyModal = () => {
  isFamilyModalOpen.value = true;
};

const openSetAlarmModal = () => {
  isAlarmModalOpen.value = true;
};

const closeSetAlarmModal = () => {
  isAlarmModalOpen.value = false;
};

const openSearchDialog = () => {
  medSearchDialog.value.openDialog();
};

const closeEditModal = async () => {
  isEditModalOpen.value = false;
  await fetchData(); // 최신 데이터 리패칭
  await fetchTodaysMedications(); // 오늘의 복약 내역 리패칭
};
const openTodaysMedicationModal = () => {
  showTodaysMedicationModal.value = true;
};

const closeTodaysMedicationModal = () => {
  showTodaysMedicationModal.value = false;
};

// 오늘의 약 정보 모달 관련 상태와 함수
const showTodaysMedicationModal = ref(false);
const todaysMedicationList = computed(() => {
  if (!todaysMedications.value || todaysMedications.value === "약 정보 없음") return [];
  return todaysMedications.value.split("◎").map(med => med.trim());
});

// white 약 정보 모달 열기
function openMedicationModal(info) {
  selectedMedication.value = info.medications
  showMedicationModal.value = true
}

// white 약 정보 모달 닫기
function closeMedicationModal() {
  showMedicationModal.value = false
}




// 수정하기 버튼 클릭 시 호출하는 함수
const openEditModal = (info) => {
  selectedInfo.value = info; // 수정할 정보 저장
  console.log("📌 수정할 정보:", info);
  isEditModalOpen.value = true; // 모달 열기
};

// ----------------- 외부 클릭 감지 함수 (모달 닫기) -----------------
const handleClickOutside = (event) => {
  // 예를 들어 특정 모달이 열려 있을 때, 모달 외부를 클릭하면 닫히도록 처리 가능
  if (isFamilyModalOpen.value) {
    const modal = document.querySelector('.modal-class'); // 실제 모달 클래스에 맞게 변경
    if (modal && !modal.contains(event.target)) {
      isFamilyModalOpen.value = false;
    }
  }
};


// 모달 상태 관리를 위한 공통 함수
const closeModal = async (modalType) => {
  switch(modalType) {
    case 'medication':
      showMedicationModal.value = false;
      break;
    case 'todaysMedication':
      showTodaysMedicationModal.value = false;
      break;
    case 'alarm':
      isAlarmModalOpen.value = false;
      break;
    case 'edit':
      isEditModalOpen.value = false;
      await fetchData(); // 데이터 리패칭
      await fetchTodaysMedications(); // 오늘의 복약 내역 리패칭
      break;
    case 'family':
      isFamilyModalOpen.value = false;
      break;
    case 'history':
      showModal.value = false;
      modalData.value = []; // 데이터 초기화
      break;
    default:
      break;
  }
};
// ----------------- API 데이터 가져오는 함수 (비동기) -----------------

const fetchTodaysMedications = async () => {
  try {
    const data = await fetchManagementData();
    if (data.result) {
      const periodMap = {
        "아침": "morning",
        "점심": "lunch",
        "저녁": "dinner",
        "자기전": "sleep"
      };
      const currentPeriodKey = periodMap[currentTimePeriod.value];
      if (currentPeriodKey) {
        const medicationsForCurrentPeriod = data.result.filter(med => med[currentPeriodKey]);
        if (medicationsForCurrentPeriod.length > 0) {
          todaysMedications.value = medicationsForCurrentPeriod
            .map(med => med.medicationName)
            .join("◎");  // 내부 데이터는 ◎로 저장
        } else {
          todaysMedications.value = "약 정보 없음";
        }

        // ✅ 복약 완료 상태 업데이트 (수정된 부분 반영)
        const currentTakingKey = periodMap[currentTimePeriod.value] + "Taking"; // 예: morningTaking
        isMedicationCompleted.value = medicationsForCurrentPeriod.length > 0 &&
          medicationsForCurrentPeriod.every(med => med[currentTakingKey]);


      } else {
        todaysMedications.value = "약 정보 없음"; // 현재 시간대에 해당하는 정보가 없을 경우 (예상치 못한 상황)
      }
    } else {
      todaysMedications.value = "약 정보 없음"; // 데이터 결과가 없을 경우
    }
  } catch (error) {
    console.error("❌ [DEBUG] 복약 리스트 가져오기 실패:", error);
    todaysMedications.value = "데이터 불러오기 실패";
  }
};

// ✅ API에서 `managementInfoList` 가져오는 함수
const fetchData = async () => {
  try {
    const data = await fetchFormattedManagementInfo();

    managementInfoList.value = data.prescriptions.length > 0
      ? data.prescriptions.map(prescription => {
          // ✅ medicationPeriod에서날짜 형식의 날짜 추출
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

// -----------------  복약 체크 및 완료 처리 함수 -----------------
const handleAllDrugCheck = (medications,ifid) => {
  console.log("모든 약 복용 체크",medications,ifid);
  prescriptionAllCheck(medications,ifid);
};


// ✅ 복약 완료 처리 함수 (UI 상태 변경 및 서버 동기화)
const completeMedications = async () => {
    try {
        if (isMedicationCompleted.value) {
            alert("이미 복약 완료 처리되었습니다.");
            return;
        }

        const periodMap = { "아침": "morning", "점심": "lunch", "저녁": "dinner", "자기전": "sleep" };
        const timePeriod = periodMap[currentTimePeriod.value];
console.log(timePeriod)
        if (!timePeriod) {
            alert("현재 시간대를 인식할 수 없습니다.");
            return;
        }

        // ✅ UI 상태 즉시 업데이트 (사용자 인터랙션에 대한 즉각적인 피드백)
        isMedicationCompleted.value = true;

        // ✅ 서버에 복약 완료 알림 (비동기적으로 처리)
        await fetchAllDrugCheck(timePeriod);

        alert("복약 완료 처리에 성공했습니다!");
    } catch (error) {
        console.error("❌ 복약 완료 처리 실패:", error);
        // ✅ 에러 발생 시 UI 상태 롤백 (선택 사항: 사용자 경험에 따라 결정)
        isMedicationCompleted.value = false; // 또는 이전 상태를 저장해두었다가 복구
        alert("복약 완료 처리에 실패했습니다.");
    }
};

// -----------------  watchEffect: 현재 시간대 변경 감지 및 복약 정보 업데이트 -----------------
watchEffect(() => {
  if (currentTimePeriod.value) { // ✅ 값이 존재하는지 확인
    console.log("✅ 현재 시간대:", currentTimePeriod.value);
    fetchTodaysMedications(); // ✅ `currentTimePeriod.value`가 설정된 후 실행
  }
});

// -----------------  onMounted: 컴포넌트 마운트 후 실행되는 로직 -----------------
//  컴포넌트가 마운트되면 데이터 및 이벤트 리스너 등록
onMounted(async () => {
  
  await fetchData();
  console.log(managementInfoList);
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

.multiline-ellipsis {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}


/* 슬라이드 페이드 애니메이션 */
.slide-fade-enter-active {
  transition: all 0.3s ease;
}
.slide-fade-leave-active {
  transition: all 0.3s cubic-bezier(1, 0.5, 0.8, 1);
}
.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateX(10px);
  opacity: 0;
}

/* 모바일 최적화 */
@media (max-width: 640px) {
  :deep(.yellow-card) {
    padding: 1rem;
  }
}

</style>