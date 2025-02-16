<template>
  <div class="flex flex-col w-full h-full overflow-y-scroll">
    <div class="flex-none h-auto min-h-0 flex">
      <BaseCalendar 
        :prescriptions="managementInfoList" 
        mode="simple"
        @dateSelected="onDateSelected"
      />
    </div>

    <!-- 그룹별 헤더 및 카드 출력 -->
    <div class="flex-row p-4">
      <div v-for="group in groupedMedicationsList" :key="group.prescriptionIndex" class="mb-6">
        <!-- 그룹 헤더: prescriptionIndex + 1 -->
        <h2 class="font-semibold text-lg mb-2">현재 복용중인 처방전 {{ Number(group.prescriptionIndex) + 1 }}</h2>
        <div v-for="medication in group.medications" :key="medication.managementId" class="mb-4">
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

            <!-- 복용 시간 체크박스 (실제 체크박스) -->
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
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import BaseCalendar from "../components/BaseCalendar.vue";
import WhiteCard from "../layout/WhiteCard.vue";
import { fetchFormattedManagementInfo, fetchAllManagementDetails, transformManagementDetails } from "../api/drugmanagement";
import { updateCheckTaking} from "../api/drugcheck";

// 처방전 데이터 저장
const managementInfoList = ref([]);
const memberId = ref(null);
const memberIdUsageCount = ref(0);

const informationIdList = computed(() => managementInfoList.value.map(med => med.informationId));

const getLimitedMemberId = () => {
  if (memberIdUsageCount.value < informationIdList.value.length) {
    memberIdUsageCount.value++;
    return memberId.value;
  } else {
    console.warn("⚠️ [DEBUG] memberId 사용 제한 초과! 더 이상 사용할 수 없습니다.");
    return null;
  }
};

const selectedDate = ref(null);

const getCardColor = (index) => {
  const colors = ["bg-gray-50", "bg-gray-100", "bg-gray-200", "bg-gray-300", "bg-gray-400"];
  return colors[index % colors.length] + " !important";
};

function onDateSelected(date) {
  selectedDate.value = date;
}

const medicationsList = ref([]);

// 그룹화된 처방전 목록 (prescriptionIndex 기준)
const groupedMedicationsList = computed(() => {
  const groups = {};
  medicationsList.value.forEach(medication => {
    const key = medication.prescriptionIndex;
    if (!groups[key]) {
      groups[key] = [];
    }
    groups[key].push(medication);
  });
  // 객체를 배열로 변환
  return Object.keys(groups).map(key => ({
    prescriptionIndex: key,
    medications: groups[key]
  }));
});

// 체크박스 토글 함수
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
  console.log(`[DEBUG] ${timeSlot} 토글됨: `, medication[property], "managementId:", medication.managementId);
  
  try {
    // API 요청: managementId와 timeSlot을 전송합니다.
    const result = await updateCheckTaking({
      managementId: medication.managementId,
      time: timeSlot,
    });
    console.log("[API Response]", result);
  } catch (error) {
    console.error("Error updating check-taking:", error);
    // 에러 발생 시 필요에 따라 로컬 상태를 원복할 수 있습니다.
  }
}

onMounted(async () => {
  try {
    console.log("📡 [DEBUG] 처방전 데이터 불러오는 중...");
    const { memberId: fetchedMemberId, prescriptions } = await fetchFormattedManagementInfo();
    memberId.value = fetchedMemberId;
    managementInfoList.value = prescriptions;
    memberIdUsageCount.value = 0;

    console.log("🆔 [DEBUG] 가져온 memberId:", memberId.value);
    console.log("📋 [DEBUG] 불러온 처방전 데이터:", managementInfoList.value);
    console.log("🆔 [DEBUG] 추출한 informationId 리스트:", informationIdList.value);

    const managementDetails = await fetchAllManagementDetails(informationIdList.value, memberId.value);
    console.log("📋 [DEBUG] 최종 API 결과:", managementDetails);

    medicationsList.value = transformManagementDetails(managementDetails);
    console.log("📋 [DEBUG] 최종 변환된 Medication 리스트:", medicationsList.value);

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
