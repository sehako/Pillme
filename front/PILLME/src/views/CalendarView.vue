<template>
  <!-- 부모에서 h-full 또는 h-screen을 설정해 주었다고 가정 -->
  <div class="flex flex-col w-full h-full">
    <!-- (1) 상단 달력 영역 (60%) -->
    <div class="flex-none h-3/5 min-h-0 overflow-hidden flex">
      <BaseCalendar 
        :prescriptions="managementInfoList" 
        mode="simple"
        @dateSelected="onDateSelected"
      />
    </div>

    <!-- (2) 구분 영역 (선택한 날짜 표시) -->
    <div class="flex-none px-4 py-3 border-b bg-gray-100 text-gray-700">
      <h2 class="font-semibold text-lg">복용 약 리스트</h2>
      <p class="text-sm mt-1">
        선택한 날짜: 
        <span class="font-bold text-blue-500">
          {{ selectedDate || '미선택' }}
        </span>
      </p>
    </div>

<!-- (3) 스크롤 가능한 하단 약물 카드 영역 (40%) -->
<div class="flex-auto overflow-y-auto p-4">
  <div 
    v-for="(medication, index) in filteredMedications" 
    :key="index" 
    class="mb-4"
  >
    <WhiteCard overrideClass="bg-white rounded-lg p-4 shadow-md flex items-center">
      <!-- 약물 이미지 -->
      <img
        src="../assets/logi_nofont.svg" 
        alt="알약이미지"
        class="w-12 h-12 rounded-full mr-4"
      />
      
      <!-- 약물 정보 -->
      <div class="flex-1">
        <p class="font-bold text-lg">{{ medication.name || "약 이름 없음" }}</p>
        <p class="text-sm text-gray-500">{{ medication.diseaseName || "병명 없음" }}</p>
        <p class="text-sm text-gray-600">{{ medication.medicationPeriod }}</p>
        <p class="text-xs text-gray-500">{{ medication.hospital || "병원 정보 없음" }}</p>
      </div>

      <!-- 복용 시간 체크박스 -->
      <div class="flex flex-col space-y-2">
        <div class="flex items-center space-x-2">
          <span class="text-xs font-semibold">아침</span>
          <div class="w-5 h-5 flex justify-center items-center rounded-full"
            :class="medication.morning ? 'bg-red-500' : 'bg-gray-300'"
          >
            <span v-if="medication.morning" class="text-white">✔</span>
          </div>
        </div>
        <div class="flex items-center space-x-2">
          <span class="text-xs font-semibold">점심</span>
          <div class="w-5 h-5 flex justify-center items-center rounded-full"
            :class="medication.lunch ? 'bg-gray-500' : 'bg-gray-300'"
          >
            <span v-if="medication.lunch" class="text-white">✔</span>
          </div>
        </div>
        <div class="flex items-center space-x-2">
          <span class="text-xs font-semibold">저녁</span>
          <div class="w-5 h-5 flex justify-center items-center rounded-full"
            :class="medication.dinner ? 'bg-gray-500' : 'bg-gray-300'"
          >
            <span v-if="medication.dinner" class="text-white">✔</span>
          </div>
        </div>
        <div class="flex items-center space-x-2">
          <span class="text-xs font-semibold">자기 전</span>
          <div class="w-5 h-5 flex justify-center items-center rounded-full"
            :class="medication.beforeSleep ? 'bg-gray-500' : 'bg-gray-300'"
          >
            <span v-if="medication.beforeSleep" class="text-white">✔</span>
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
import { fetchFormattedManagementInfo } from "../api/drugmanagement"; // ✅ API 가져오기
import { MyDrugHistory } from "../api/drughistory";
// ✅ 처방전 데이터 저장
const managementInfoList = ref([]);

// ✅ 선택한 날짜 저장
const selectedDate = ref(null);

// ✅ 선택한 날짜에 해당하는 약물 필터링 + 복용 시간 필드 추가 (현재 세부 약 리스트로 캘린더와 연동하게 개별약물로 나오게 하는 기능 미구현현)
const filteredMedications = computed(() => {
  if (!selectedDate.value) return managementInfoList.value; // 날짜 미선택 시 전체 리스트 반환
  return managementInfoList.value.map(med => ({
    ...med,
    medications: med.medications.map(medItem => ({
      name: medItem.medicationName,
      morning: medItem.morningTaking || false,
      lunch: medItem.lunchTaking || false,
      dinner: medItem.dinnerTaking || false,
      beforeSleep: medItem.sleepTaking || false,
    })),
  }));
});


// ✅ API에서 `managementInfoList` 가져오는 함수
const fetchData = async () => {
  try {
    console.log("📡 [DEBUG] 처방전 데이터 불러오는 중...");
    managementInfoList.value = await fetchFormattedManagementInfo();
    console.log("📋 [DEBUG] 불러온 처방전 데이터:", managementInfoList.value);
  } catch (error) {
    console.error("❌ [DEBUG] Management 정보 로드 실패:", error);
  }
};



// ✅ `BaseCalendar`에서 날짜 선택 시 실행될 함수
function onDateSelected(date) {
  selectedDate.value = date;
}

onMounted(async () => {
  try {
    console.log("📡 [DEBUG] 처방전 데이터 불러오는 중...");
    managementInfoList.value = await fetchFormattedManagementInfo();
    console.log("📋 [DEBUG] 불러온 처방전 데이터:", managementInfoList.value);

    // ✅ MyDrugHistory 데이터 확인
    const drugHistoryData = await MyDrugHistory();
    console.log("📋 [DEBUG] 마이 드러그 히스토리 데이터:", drugHistoryData);

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
