<template>
  <!-- 스크롤 컨테이너에 ref="scrollContainer" 추가 -->
  <div ref="scrollContainer" class="flex flex-col w-full h-full overflow-y-scroll">
    <!-- 달력 영역 -->
    <div class="flex-none h-auto min-h-0 flex">
      <BaseCalendar 
        :prescriptions="managementInfoList" 

         class="pointer-events-none"
      />
    </div>

    <!-- 글로벌 sticky 영역: 현재 복용중인 약 + 시간대별 글로벌 체크박스 -->
    <div class="sticky top-0 z-50 bg-white">
      <!-- 헤더: 현재 복용중인 약 -->
      <div class="px-4 py-3 border-b bg-gray-100 text-gray-700">
        <h2 class="font-semibold text-lg">현재 복용중인 약</h2>
      </div>
      <!-- 글로벌 시간대별 체크박스 -->
      <div class="px-4 py-3 border-b bg-gray-50 text-gray-700">
        <div class="flex flex-wrap gap-4">
          <label class="flex items-center space-x-1">
            <input
              type="checkbox"
              class="form-checkbox h-5 w-5"
              @change="toggleGlobal('morning')"
              :checked="globalChecks.morning"
            />
            <span class="text-xs">아침</span>
          </label>
          <label class="flex items-center space-x-1">
            <input
              type="checkbox"
              class="form-checkbox h-5 w-5"
              @change="toggleGlobal('lunch')"
              :checked="globalChecks.lunch"
            />
            <span class="text-xs">점심</span>
          </label>
          <label class="flex items-center space-x-1">
            <input
              type="checkbox"
              class="form-checkbox h-5 w-5"
              @change="toggleGlobal('dinner')"
              :checked="globalChecks.dinner"
            />
            <span class="text-xs">저녁</span>
          </label>
          <label class="flex items-center space-x-1">
            <input
              type="checkbox"
              class="form-checkbox h-5 w-5"
              @change="toggleGlobal('sleep')"
              :checked="globalChecks.sleep"
            />
            <span class="text-xs">자기 전</span>
          </label>
        </div>
      </div>
    </div>

    <!-- 처방전 그룹 목록 -->
    <div class="flex-col p-4">
      <div
        v-for="group in groupedMedicationsList"
        :key="group.prescriptionIndex"
        :id="`group-${group.prescriptionIndex}`"
        class="mb-6"
      >
        <!-- 그룹 sticky 헤더 (글로벌 영역 아래에 고정되며, 클릭 시 해당 그룹으로 스크롤) -->
        <div
          class="sticky top-24 z-40 bg-white cursor-pointer"
          @click="scrollToGroup(group.prescriptionIndex)"
        >
          <h2 class="font-semibold text-lg py-2 px-4 border-b">
            현재 복용중인 처방전 {{ Number(group.prescriptionIndex) + 1 }}
          </h2>
        </div>

        <!-- 그룹 내 약물 카드들 -->
        <div>
          <div
            v-for="medication in group.medications"
            :key="medication.managementId"
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
                <p class="font-bold text-lg">
                  {{ medication.medicationName || "약 이름 없음" }}
                </p>
                <p class="text-sm text-gray-500">
                  {{ medication.diseaseName || "병명 없음" }}
                </p>
                <p class="text-sm text-gray-600">
                  {{ medication.startDate }} ~ {{ medication.endDate }}
                </p>
                <p class="text-xs text-gray-500">
                  {{ medication.hospital || "병원 정보 없음" }}
                </p>
              </div>

              <!-- 개별 약물 복용 시간 체크박스 -->
              <div class="flex flex-col space-y-2">
                <!-- 아침 -->
                <div class="flex items-center space-x-2">
                  <label class="flex items-center space-x-2">
                    <input
                      type="checkbox"
                      class="form-checkbox h-5 w-5"
                      :checked="medication.morningTaking"
                      @change="toggleMedication(medication, 'morning')"
                    />
                    <span class="text-xs font-semibold">아침</span>
                  </label>
                </div>
                <!-- 점심 -->
                <div class="flex items-center space-x-2">
                  <label class="flex items-center space-x-2">
                    <input
                      type="checkbox"
                      class="form-checkbox h-5 w-5"
                      :checked="medication.lunchTaking"
                      @change="toggleMedication(medication, 'lunch')"
                    />
                    <span class="text-xs font-semibold">점심</span>
                  </label>
                </div>
                <!-- 저녁 -->
                <div class="flex items-center space-x-2">
                  <label class="flex items-center space-x-2">
                    <input
                      type="checkbox"
                      class="form-checkbox h-5 w-5"
                      :checked="medication.dinnerTaking"
                      @change="toggleMedication(medication, 'dinner')"
                    />
                    <span class="text-xs font-semibold">저녁</span>
                  </label>
                </div>
                <!-- 자기 전 -->
                <div class="flex items-center space-x-2">
                  <label class="flex items-center space-x-2">
                    <input
                      type="checkbox"
                      class="form-checkbox h-5 w-5"
                      :checked="medication.sleepTaking"
                      @change="toggleMedication(medication, 'sleep')"
                    />
                    <span class="text-xs font-semibold">자기 전</span>
                  </label>
                </div>
              </div>
            </WhiteCard>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import BaseCalendar from "../components/BaseCalendar.vue";
import WhiteCard from "../layout/WhiteCard.vue";
import {
  fetchFormattedManagementInfo,
  fetchAllManagementDetails,
  transformManagementDetails,
} from "../api/drugmanagement";
import { updateCheckTaking, fetchAllDrugCheck } from "../api/drugcheck";

// 스크롤 컨테이너 ref
const scrollContainer = ref(null);

// 처방전 데이터 저장
const managementInfoList = ref([]);
const memberId = ref(null);

const selectedDate = ref(null);

const getCardColor = (index) => {
  const colors = [
    "bg-gray-50",
    "bg-gray-100",
    "bg-gray-200",
    "bg-gray-300",
    "bg-gray-400",
  ];
  return colors[index % colors.length] + " !important";
};

function onDateSelected(date) {
  selectedDate.value = date;
}

const medicationsList = ref([]);

// 그룹화된 처방전 목록 (prescriptionIndex 기준)
const groupedMedicationsList = computed(() => {
  const groups = {};
  medicationsList.value.forEach((medication) => {
    const key = medication.prescriptionIndex;
    if (!groups[key]) {
      groups[key] = [];
    }
    groups[key].push(medication);
  });
  return Object.keys(groups).map((key) => ({
    prescriptionIndex: key,
    medications: groups[key],
  }));
});

// 개별 약물 체크박스 토글 함수
async function toggleMedication(medication, timeSlot) {
  let property = "";
  switch (timeSlot) {
    case "morning":
      property = "morningTaking";
      break;
    case "lunch":
      property = "lunchTaking";
      break;
    case "dinner":
      property = "dinnerTaking";
      break;
    case "sleep":
      property = "sleepTaking";
      break;
    default:
      return;
  }

  // 로컬 상태 업데이트 (옵티미스틱 업데이트)
  medication[property] = !medication[property];
  console.log(
    `[DEBUG] ${timeSlot} 토글됨: `,
    medication[property],
    "managementId:",
    medication.managementId
  );

  try {
    const result = await updateCheckTaking({
      managementId: medication.managementId,
      time: timeSlot,
    });
    console.log("[API Response]", result);
  } catch (error) {
    console.error("Error updating check-taking:", error);
  }
}

// 글로벌 시간대 체크 상태 (추후 전체 토글 로직 구현 예정)
const globalChecks = ref({
  morning: false,
  lunch: false,
  dinner: false,
  sleep: false,
});

async function toggleGlobal(timeSlot) {
  // 글로벌 체크 상태 토글
  globalChecks.value[timeSlot] = !globalChecks.value[timeSlot];
  console.log(`[DEBUG] 글로벌 ${timeSlot} 체크:`, globalChecks.value[timeSlot]);

  // API 호출: 모든 복약 체크 상태 업데이트 (PATCH)
  try {
    const result = await fetchAllDrugCheck(timeSlot);
    console.log("[toggleGlobal] API 응답:", result);
    // 필요 시 result 값을 기반으로 추가 로직 처리 가능 (예: UI 업데이트)
  } catch (error) {
    console.error("[toggleGlobal] API 호출 실패:", error);
  }
}

// 그룹 헤더 클릭 시 해당 그룹으로 스크롤 이동 (scrollContainer 기준)
function scrollToGroup(prescriptionIndex) {
  const el = document.getElementById(`group-${prescriptionIndex}`);
  if (el && scrollContainer.value) {
    const stickyHeight = 80; // sticky 영역의 높이 (필요에 따라 조정)
    const containerRect = scrollContainer.value.getBoundingClientRect();
    const elRect = el.getBoundingClientRect();
    const offset = elRect.top - containerRect.top - stickyHeight;
    scrollContainer.value.scrollTo({
      top: scrollContainer.value.scrollTop + offset,
      behavior: "smooth",
    });
  }
}

onMounted(async () => {
  try {
    console.log("📡 [DEBUG] 처방전 데이터 불러오는 중...");
    const { memberId: fetchedMemberId, prescriptions } =
      await fetchFormattedManagementInfo();
    memberId.value = fetchedMemberId;
    managementInfoList.value = prescriptions;

    const managementDetails = await fetchAllManagementDetails(
      managementInfoList.value.map((med) => med.informationId),
      memberId.value
    );
    medicationsList.value = transformManagementDetails(managementDetails);
    console.log(
      "📋 [DEBUG] 최종 변환된 Medication 리스트:",
      medicationsList.value
    );
  } catch (error) {
    console.error("❌ [DEBUG] Management 정보 로드 실패:", error);
  }
});
</script>

<style scoped>
.flex-auto {
  @apply overflow-y-auto;
}
</style>
