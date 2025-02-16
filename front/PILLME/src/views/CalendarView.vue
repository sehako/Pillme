<template>
  <!-- 부모에서 h-full 또는 h-screen을 설정해 주었다고 가정 -->
  <div class="flex flex-col w-full h-full overflow-y-scroll">
    <!-- (1) 상단 달력 영역 (60%) -->
    <div class="flex-none h-auto min-h-0 flex">
      <BaseCalendar 
        :prescriptions="managementInfoList" 
        mode="simple"
        @dateSelected="onDateSelected"
      />
    </div>

    <!-- (2) 구분 영역 (✅ sticky 적용) -->
    <div class="!flex-none px-4 py-3 border-b bg-gray-100 text-gray-700 !sticky top-0 z-10">
      <h2 class="font-semibold text-lg">현재 복용 약 리스트</h2>
    </div>


<!-- (3) 스크롤 가능한 하단 약물 카드 영역 (40%) -->
<div class="flex-row p-4">
  <div 
    v-for="(medication, index) in medicationsList" 
    :key="index" 
    class="mb-4"
  >
    <WhiteCard 
      :overrideClass="`${getCardColor(medication.prescriptionIndex)} rounded-lg p-4 shadow-md flex items-center`"
    >
      <!-- 약물 이미지 -->
      <img
        src="../assets/logi_nofont.svg" 
        alt="알약이미지"
        class="w-12 h-12 rounded-full mr-4"
      />
      
      <!-- 약물 정보 -->
      <div class="flex-1">
        <p class="font-bold text-lg">{{ medication.medicationName || "약 이름 없음" }}</p>
        <p class="text-sm text-gray-500">{{ medication.diseaseName || "병명 없음" }}</p>
        <p class="text-sm text-gray-600">{{ medication.startDate }} ~ {{ medication.endDate }}</p>
        <p class="text-xs text-gray-500">{{ medication.hospital || "병원 정보 없음" }}</p>
      </div>

      <!-- 복용 시간 체크박스 -->
      <div class="flex flex-col space-y-2">
        <div class="flex items-center space-x-2">
          <span class="text-xs font-semibold">아침</span>
          <div class="w-5 h-5 flex justify-center items-center rounded-full border"
            :class="medication.morningTaking ? 'bg-green-500 text-white' : 'bg-gray-300 text-gray-600'"
          >
            <span v-if="medication.morningTaking">✔</span>
          </div>
        </div>
        <div class="flex items-center space-x-2">
          <span class="text-xs font-semibold">점심</span>
          <div class="w-5 h-5 flex justify-center items-center rounded-full border"
            :class="medication.lunchTaking ? 'bg-green-500 text-white' : 'bg-gray-300 text-gray-600'"
          >
            <span v-if="medication.lunchTaking">✔</span>
          </div>
        </div>
        <div class="flex items-center space-x-2">
          <span class="text-xs font-semibold">저녁</span>
          <div class="w-5 h-5 flex justify-center items-center rounded-full border"
            :class="medication.dinnerTaking ? 'bg-green-500 text-white' : 'bg-gray-300 text-gray-600'"
          >
            <span v-if="medication.dinnerTaking">✔</span>
          </div>
        </div>
        <div class="flex items-center space-x-2">
          <span class="text-xs font-semibold">자기 전</span>
          <div class="w-5 h-5 flex justify-center items-center rounded-full border"
            :class="medication.sleepTaking ? 'bg-green-500 text-white' : 'bg-gray-300 text-gray-600'"
          >
            <span v-if="medication.sleepTaking">✔</span>
          </div>
        </div>
      </div>
    </WhiteCard>
  </div>
</div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import BaseCalendar from "../components/BaseCalendar.vue";
import WhiteCard from "../layout/WhiteCard.vue";
import { fetchFormattedManagementInfo, fetchAllManagementDetails, transformManagementDetails } from "../api/drugmanagement"; // ✅ API 가져오기

// ✅ 처방전 데이터 저장
const managementInfoList = ref([]);

const memberId = ref(null); // memberId 저장
const memberIdUsageCount = ref(0); // ✅ memberId 사용 횟수 추적

// ✅ `informationId` 리스트 생성
const informationIdList = computed(() => {
  return managementInfoList.value.map(med => med.informationId);
});

// ✅ `memberId` 사용 가능 여부 체크 함수
const getLimitedMemberId = () => {
  if (memberIdUsageCount.value < informationIdList.value.length) {
    memberIdUsageCount.value++; // 사용 횟수 증가
    return memberId.value;
  } else {
    console.warn("⚠️ [DEBUG] memberId 사용 제한 초과! 더 이상 사용할 수 없습니다.");
    return null; // 제한 초과 시 null 반환
  }
};
// ✅ 선택한 날짜 저장
const selectedDate = ref(null);


const getCardColor = (index) => {
  const colors = ["bg-gray-50", "bg-gray-100", "bg-gray-200", "bg-gray-300", "bg-gray-400"];
  return colors[index % colors.length] + " !important"; // ✅ 강제 적용
};


// ✅ `BaseCalendar`에서 날짜 선택 시 실행될 함수
function onDateSelected(date) {
  selectedDate.value = date;
}

const medicationsList = ref([]); // ✅ 변환된 데이터 저장

onMounted(async () => {
  try {
    console.log("📡 [DEBUG] 처방전 데이터 불러오는 중...");
    const { memberId: fetchedMemberId, prescriptions } = await fetchFormattedManagementInfo();

    // ✅ 값 저장
    memberId.value = fetchedMemberId;
    managementInfoList.value = prescriptions;
    memberIdUsageCount.value = 0; // ✅ 사용 횟수 초기화

    console.log("🆔 [DEBUG] 가져온 memberId:", memberId.value);
    console.log("📋 [DEBUG] 불러온 처방전 데이터:", managementInfoList.value);
    console.log("🆔 [DEBUG] 추출한 informationId 리스트:", informationIdList.value);

    // ✅ 모든 처방전 상세 정보 가져오기
    const managementDetails = await fetchAllManagementDetails(informationIdList.value, memberId.value);
    console.log("📋 [DEBUG] 최종 API 결과:", managementDetails);

    // ✅ 변환 함수 적용 (medications 단위로 정리)
    medicationsList.value = transformManagementDetails(managementDetails);

    console.log("📋 [DEBUG] 최종 변환된 Medication 리스트:", medicationsList.value);

  } catch (error) {
    console.error("❌ [DEBUG] Management 정보 로드 실패:", error);
  }
});


</script>

<style scoped>
/* ✅ 스크롤 스타일 */
.flex-auto {
  @apply overflow-y-auto;
}
</style>
