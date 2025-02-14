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
        v-for="(prescription, index) in filteredMedications" 
        :key="index" 
        class="mb-4"
      >
        <WhiteCard overrideClass="bg-white">
          <div class="flex flex-row items-center">
            <img
              src="../assets/logi_nofont.svg"
              alt="알약이미지"
              class="w-16 h-16 mr-4"
            />
            <div class="flex flex-col">
              <p class="font-bold text-lg">{{ prescription.diseaseName || "병명 없음" }}</p>
              <p class="text-sm text-gray-500">{{ prescription.medicationPeriod }}</p>
              <p class="text-sm text-gray-600">{{ prescription.medications || "약 정보 없음" }}</p>
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

// ✅ 처방전 데이터 저장
const managementInfoList = ref([]);

// ✅ 선택한 날짜 저장
const selectedDate = ref(null);

// ✅ 선택한 날짜에 해당하는 약물 필터링
const filteredMedications = computed(() => {
  if (!selectedDate.value) return managementInfoList.value; // 날짜 미선택 시 전체 리스트 반환
  return managementInfoList.value.filter((med) =>
    med.medicationPeriod.includes(selectedDate.value)
  );
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

// ✅ 컴포넌트가 마운트되면 데이터 로드
onMounted(() => {
  fetchData();
});
</script>

<style scoped>
/* ✅ 스크롤 스타일 */
.flex-auto {
  @apply overflow-y-auto;
}
</style>
