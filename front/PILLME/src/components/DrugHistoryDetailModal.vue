<template>
  <div
    class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50"
    @click.self="emit('close')"
  >
    <div
      ref="modalContainer"
      class="w-[90vw] max-w-4xl max-h-[85vh] bg-white rounded-lg p-6 flex flex-col overflow-hidden"
    >
      <h3 class="text-xl font-bold mb-4">{{ prescription.diseaseName }} 상세</h3>

      <div class="flex justify-between items-center mb-4">
        <div>
          <p class="text-sm text-gray-500">{{ prescription.hospital }}</p>
          <p class="text-sm text-gray-500">
            {{ prescription.startDate }} ~ {{ prescription.endDate }}
          </p>
        </div>
      </div>

      <div class="flex justify-between items-center mb-4">
        <button
          @click="prevPage"
          :disabled="currentPage === 0"
          class="bg-gray-200 px-3 py-1 rounded hover:bg-gray-300 disabled:bg-gray-100"
        >
          〈 이전
        </button>
        <p class="text-gray-700">페이지 {{ currentPage + 1 }} / {{ totalPages }}</p>
        <button
          @click="nextPage"
          :disabled="currentPage >= totalPages - 1"
          class="bg-gray-200 px-3 py-1 rounded hover:bg-gray-300 disabled:bg-gray-100"
        >
          다음 〉
        </button>
      </div>

      <div
        ref="tableContainer"
        class="overflow-x-auto max-h-[60vh] border rounded-lg"
      >
        <table class="w-fit border-collapse" style="border-collapse: collapse;">
          <thead class="sticky top-0 z-30 bg-gray-100">
            <tr>
              <th
                class="border border-gray-200 text-center min-w-[120px] p-2 sticky left-0 bg-gray-100"
              >
                복용약
              </th>
              <th
                v-for="date in paginatedDates"
                :key="date"
                class="border border-gray-200 text-center min-w-[120px] p-2"
              >
                {{ date }}
              </th>
              <th
                class="border border-gray-200 text-center min-w-[80px] p-2"
              >
                
              </th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="medName in Object.keys(groupedByMedication)"
              :key="medName"
            >
              <td
                class="border border-gray-200 font-bold text-center bg-white sticky left-0 z-40 p-2 max-w-[150px]"
              >
                {{ medName }}
              </td>

              <td
                v-for="date in paginatedDates"
                :key="date"
                class="border border-gray-200 p-2 text-center"
              >
                <div v-if="isAllTimesEmpty(groupedByMedication[medName][date])" class="text-gray-500">
                  복약정보없음
                </div>
                <div v-else class="flex flex-col items-center space-y-1">
                  <div
                    v-for="time in ['morning', 'lunch', 'dinner', 'sleep']"
                    :key="`${date}-${time}`"
                  >
                    <span class="font-semibold">{{ getTimeLabel(time) }}:</span>

                    <template v-if="isEditMode">
                      <input
                        type="checkbox"
                        :id="`${medName}-${date}-${time}`"
                        :name="`${medName}-${date}-${time}`"
                        class="medication-checkbox"
                        v-model="groupedByMedication[medName][date][`${time}Taking`]"
                      />
                    </template>

                    <template v-else>
                      <span
                        :class="{
                          'text-green-500': groupedByMedication[medName][date]?.[`${time}Taking`],
                          'text-red-500': !groupedByMedication[medName][date]?.[`${time}Taking`],
                        }"
                      >
                        {{ groupedByMedication[medName][date]?.[`${time}Taking`] ? '복용' : '미복용' }}
                      </span>
                    </template>

                  </div>
                </div>
              </td>
              <td class="border border-gray-200 text-center p-2 sticky left-0 bg-white">
                <BaseButton
                  v-if="historyDetailsList.length > 0 && !isEditMode"
                  label="삭제"
                  @click="deleteMedication(medName)"
                  class="bg-red-500 hover:bg-red-700 text-white px-2 py-1 rounded-sm text-xs"
                >삭제</BaseButton>
                 <BaseButton
                    v-if="isEditMode"
                    label="저장"
                    @click="saveMedicationHistory(medName)"
                    class=" hover:bg-green-700 text-white px-2 py-1 rounded-sm text-xs mt-1"
                  >저장</BaseButton>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="flex justify-center mt-4 !min-w-fit-content">
        <button
          v-if="isEditMode"
          @click="cancelEditMode"
          class="bg-gray-400 hover:bg-gray-500 text-white px-4 py-2 rounded mr-2"
        >취소</button>
        <button
          @click="emit('close')"
          class="bg-[#9DBB9F] text-white px-4 py-2 rounded hover:bg-[#88a88c]"
        >닫기</button>
          <button
              v-if="!isEditMode"
              @click="startEditMode"
              class="ml-2 bg-blue-500 hover:bg-blue-700 text-white px-4 py-2 rounded"
          >수정</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from "vue";
import { fetchPrescriptionDetails, deleteHistory, updateHistoryDetails } from "../api/drughistory"; // ✅ updateHistoryDetails import 추가
import BaseButton from "../components/BaseButton.vue";

const props = defineProps({
  prescription: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(["close"]);

const dateList = ref([]);
const groupedByMedication = ref({});
const currentPage = ref(0);
const itemsPerPage = 7;
const tableContainer = ref(null);
const modalContainer = ref(null);
const historyDetailsList = ref([]);
const isEditMode = ref(false); // ✅ 수정 모드 상태 변수 추가
const originalMedicationData = ref({}); // ✅ 수정 전 데이터 백업용 ref

const paginatedDates = computed(() => {
  const start = currentPage.value * itemsPerPage;
  const end = start + itemsPerPage;
  return dateList.value.slice(start, end);
});

const totalPages = computed(() =>
  Math.ceil(dateList.value.length / itemsPerPage)
);

function prevPage() {
  if (currentPage.value > 0) {
    currentPage.value--;
    scrollToleft();
  }
}

function nextPage() {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++;
    scrollToleft();
  }
}

function scrollToleft() {
  nextTick(() => {
    if (tableContainer.value) {
      tableContainer.value.scrollTo({ left: 0, behavior: "smooth" });
    }
    if (modalContainer.value) {
      modalContainer.value.scrollTo({ left: 0, behavior: "smooth" });
    }
  });
}

function getTimeLabel(timeKey) {
  const timeMap = { morning: "아침", lunch: "점심", dinner: "저녁", sleep: "자기전" };
  return timeMap[timeKey] || "";
}

function generateDateList(startDate, endDate) {
  const dateList = [];
  const start = new Date(startDate);
  const end = new Date(endDate);

  while (start <= end) {
    const formattedDate = start.toISOString().split("T")[0];
    dateList.push(formattedDate);
    start.setDate(start.getDate() + 1);
  }
  return dateList;
}

function isAllTimesEmpty(dayData) {
  return (
    !dayData ||
    (dayData.morningTaking === false &&
      dayData.lunchTaking === false &&
      dayData.dinnerTaking === false &&
      dayData.sleepTaking === false)
  );
}

async function loadPrescriptionDetails() {
  try {
    const infoId = props.prescription.informationId;
    const historyDetails = await fetchPrescriptionDetails(infoId);
    historyDetailsList.value = historyDetails;
    console.log("📌 상세 정보 로드:", historyDetails);
    console.log("📌 처방전 정보:", props.prescription);
    dateList.value = generateDateList(
      props.prescription.startDate,
      props.prescription.endDate
    );

    const groups = {};
    historyDetails.forEach((item) => {
      if (!groups[item.medicationName]) {
        groups[item.medicationName] = {};
      }
      groups[item.medicationName][item.takingDate] = item;
    });

    groupedByMedication.value = groups;
    console.log("📌 그룹화된 데이터:", groups);
    originalMedicationData.value = structuredClone(groups); // ✅ 깊은 복사로 초기 데이터 저장
  } catch (error) {
    console.error("❌ 상세 정보 로드 실패:", error);
  }
}

async function deleteMedication(medicationNameToDelete) { // ✅ Receive medicationName
  if (!medicationNameToDelete) {
    alert("삭제할 약품 이름이 없습니다.");
    emit("close");
    return;
  }

  if (!historyDetailsList.value || !Array.isArray(historyDetailsList.value)) { // ✅ historyDetailsList validation
    console.error("❌ historyDetailsList가 유효하지 않습니다.");
    alert("데이터 로딩 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    emit("close");
    return;
  }

  const historyItemToDelete = historyDetailsList.value.find(item => item.medicationName === medicationNameToDelete); // ✅ Find item by medicationName

  if (!historyItemToDelete || !historyItemToDelete.historyId) { // ✅ Check if historyItem and historyId are found
    console.error("❌ 삭제할 약품 정보를 찾을 수 없습니다.");
    alert("약품 정보 삭제에 실패했습니다. 약품 정보를 찾을 수 없습니다.");
    emit("close");
    return;
  }

  const historyIdToDelete = historyItemToDelete.historyId; // ✅ Extract historyId
  console.log("📌 삭제할 약품 ID:", historyIdToDelete);
  if (confirm("약을 삭제하시겠습니까?")) {
    try {
      await deleteHistory(historyIdToDelete);
      alert("매일 새벽 4시에 히스토리 삭제 내역이 업데이트됩니다.");
      loadPrescriptionDetails();
      emit("close");
    } catch (error) {
      console.error("❌ 약 삭제 실패:", error);
      alert("약 삭제에 실패했습니다.");
      emit("close");
    }
  }
}

function startEditMode() { // ✅ "수정" 버튼 클릭 시 호출될 함수
  isEditMode.value = true; // 수정 모드 활성화
}

function cancelEditMode() { // ✅ "취소" 버튼 클릭 시 호출될 함수
  isEditMode.value = false; // 수정 모드 비활성화
  groupedByMedication.value = structuredClone(originalMedicationData.value); // 데이터 복원
}

async function saveMedicationHistory() { // ✅ "저장" 버튼 클릭 시 호출될 함수
  if (!props.prescription.informationId) {
    alert("처방전 정보 ID가 없습니다.");
    return;
  }

  const modifyList = [];
  for (const medName in groupedByMedication.value) {
    for (const date in groupedByMedication.value[medName]) {
      const dayData = groupedByMedication.value[medName][date];
      if (dayData.historyId) { // historyId가 있는 경우에만 수정 대상으로 추가
        modifyList.push({
          historyId: dayData.historyId,
          morningTaking: dayData.morningTaking, // ✅ 체크박스 값 직접 참조
          lunchTaking: dayData.lunchTaking,   // ✅ 체크박스 값 직접 참조
          dinnerTaking: dayData.dinnerTaking,  // ✅ 체크박스 값 직접 참조
          sleepTaking: dayData.sleepTaking,   // ✅ 체크박스 값 직접 참조
        });
      }
    }
  }

  if (modifyList.length === 0) {
    alert("수정된 복약 정보가 없습니다.");
    return;
  }

  // ✅ modifyList  생성 후 콘솔에 출력하여 확인 (디버깅 용도)
  console.log("📌 modifyList (API 요청 Body):", modifyList);

  try {
    await updateHistoryDetails(props.prescription.informationId, modifyList);
    alert("수정된 복약 정보는 매일 새벽 4시에 업데이트됩니다.");
    loadPrescriptionDetails(); // 데이터 다시 로드
    isEditMode.value = false; // 수정 모드 비활성화
  } catch (error) {
    console.error("❌ 복약 정보 수정 실패:", error);
    alert("복약 정보 수정에 실패했습니다.");
  }
}

onMounted(async () => {
  await loadPrescriptionDetails();
});
</script>

<style scoped>
.border-collapse {
  border-collapse: collapse;
}
.border {
  border: 1px solid #e2e8f0;
}
.bg-gray-100 {
  background-color: #f7fafc;
}
.text-gray-500 {
  color: #718096;
}
.text-gray-700 {
  color: #4a5568;
}
.font-bold {
  font-weight: bold;
}
.text-center {
  text-align: center;
}
.p-2 {
  padding: 0.5rem;
}
.min-w-\[60px\] {
  min-width: 60px;
}
.min-w-\[80px\] {
  min-width: 80px;
}
.min-w-\[120px\] {
  min-width: 120px;
}
.max-w-\[150px\] {
  max-width: 150px;
}
.sticky {
  position: sticky;
}
.top-0 {
  top: 0;
}
.left-0 {
  left: 0;
}
.bg-white {
  background-color: white;
}
.z-10 {
  z-index: 10;
}
.word-break-keep {
  word-break: keep-all; /* 공백 기준으로만 줄바꿈 */
}

/* 페이지네이션 버튼 */
button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.medication-checkbox {
  transform: scale(1.2); /* 체크박스 크기 약간 확대 */
  margin: 5px; /* 체크박스 주변 여백 추가 */
  cursor: pointer; /* 마우스 커서 변경 */
}
</style>