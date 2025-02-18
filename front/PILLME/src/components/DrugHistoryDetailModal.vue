<template>
  <div class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50" @click.self="emit('close')">
    <div ref="modalContainer" class="w-[90vw] max-w-4xl max-h-[85vh] bg-white rounded-lg p-6 flex flex-col overflow-hidden">
      <!-- ✅ 제목 -->
      <h3 class="text-xl font-bold mb-4">{{ prescription.diseaseName }} 상세</h3>

      <div class="flex justify-between items-center mb-4">
        <div>
          <p class="text-sm text-gray-500">{{ prescription.hospital }}</p>
          <p class="text-sm text-gray-500">{{ prescription.startDate }} ~ {{ prescription.endDate }}</p>
        </div>
      </div>

      <!-- ✅ 날짜 페이지네이션 컨트롤 -->
      <div class="flex justify-between items-center mb-4">
        <button @click="prevPage" :disabled="currentPage === 0" class="bg-gray-200 px-3 py-1 rounded hover:bg-gray-300 disabled:bg-gray-100">
          〈 이전
        </button>
        <p class="text-gray-700">페이지 {{ currentPage + 1 }} / {{ totalPages }}</p>
        <button @click="nextPage" :disabled="currentPage >= totalPages - 1" class="bg-gray-200 px-3 py-1 rounded hover:bg-gray-300 disabled:bg-gray-100">
          다음 〉
        </button>
      </div>

      <!-- ✅ 테이블 컨테이너 (가로 스크롤 가능) -->
      <div ref="tableContainer" class="overflow-x-auto max-h-[60vh] border rounded-lg">
        <table class="w-full border-collapse">
          <thead>
            <tr class="bg-gray-100 sticky top-0 z-10">
              <th class="border text-center min-w-[60px] sticky left-0 bg-gray-100">복용약</th>
              <th v-for="date in paginatedDates" :key="date" class="border text-center min-w-[120px]">
                {{ date }}
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(medRow, medName) in groupedByMedication" :key="medName">
              <!-- ✅ 약 이름 (Sticky 적용) -->
              <td class="border font-bold text-center bg-white sticky left-0 max-w-[150px]">
                {{ medName }}
              </td>

              <!-- ✅ 날짜별 복용 상태 -->
              <td v-for="date in paginatedDates" :key="`${date}-${medName}`" class="border p-2 text-center">
                <div v-if="isAllTimesEmpty(medRow[date])" class="text-gray-500">복약정보없음</div>
                <div v-else class="flex flex-col items-center space-y-1">
                  <div v-for="time in ['morning', 'lunch', 'dinner', 'sleep']" :key="`${date}-${time}`">
                    <span class="font-semibold">{{ getTimeLabel(time) }}:</span>
                    <span :class="{
                      'text-green-500': medRow[date]?.[`${time}Taking`],
                      'text-red-500': !medRow[date]?.[`${time}Taking`]
                    }">
                      {{ medRow[date]?.[`${time}Taking`] ? '복용' : '미복용' }}
                    </span>
                  </div>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- ✅ 닫기 버튼 -->
      <button @click="emit('close')" class="bg-[#9DBB9F] text-white px-4 py-2 rounded mt-4 hover:bg-[#88a88c]">
        닫기
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from "vue";
import { fetchPrescriptionDetails } from "../api/drughistory";
import { left } from "@popperjs/core";

const props = defineProps({
  prescription: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(["close"]);

const dateList = ref([]);
const groupedByMedication = ref({});
const currentPage = ref(0);
const itemsPerPage = 7; // 한 번에 표시할 날짜 개수
const tableContainer = ref(null);
const modalContainer = ref(null);

// ✅ 날짜 목록을 페이지 단위로 나누기
const paginatedDates = computed(() => {
  const start = currentPage.value * itemsPerPage;
  const end = start + itemsPerPage;
  return dateList.value.slice(start, end);
});

// ✅ 총 페이지 수 계산
const totalPages = computed(() => Math.ceil(dateList.value.length / itemsPerPage));

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

// ✅ 페이지 넘길 때 스크롤 자동 맨 위로 이동
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

// ✅ 날짜 목록 생성
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

// ✅ 복약 정보가 없는 경우 체크
function isAllTimesEmpty(dayData) {
  return !dayData || (
    dayData.morningTaking === false &&
    dayData.lunchTaking === false &&
    dayData.dinnerTaking === false &&
    dayData.sleepTaking === false
  );
}

async function loadPrescriptionDetails() {
  try {
    const infoId = props.prescription.informationId;
    const historyDetails = await fetchPrescriptionDetails(infoId);

    dateList.value = generateDateList(props.prescription.startDate, props.prescription.endDate);

    const groups = {};
    historyDetails.forEach(item => {
      if (!groups[item.medicationName]) {
        groups[item.medicationName] = {};
      }
      groups[item.medicationName][item.takingDate] = item;
    });

    groupedByMedication.value = groups;
  } catch (error) {
    console.error("❌ 상세 정보 로드 실패:", error);
  }
}

onMounted(() => {
  loadPrescriptionDetails();
});
</script>

<style scoped>
/* ✅ 날짜 및 약 이름 열 고정 */
.sticky-header {
  position: sticky;
  top: 0;
  background: white;
  z-index: 10;
}

.sticky-left {
  position: sticky;
  left: 0;
  background: white;
  z-index: 5;
}

/* ✅ 페이지네이션 버튼 */
button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
