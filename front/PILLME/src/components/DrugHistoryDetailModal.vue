<template>
  <div class="modal-container">
    <h3 class="text-xl font-bold mb-4">{{ prescription.diseaseName }} 상세</h3>

    <div class="flex justify-between items-center mb-4">
      <div>
        <p class="text-sm text-gray-500">{{ prescription.hospital }}</p>
        <p class="text-sm text-gray-500">{{ prescription.startDate }} ~ {{ prescription.endDate }}</p>
      </div>
      
      <!-- 레이아웃 토글 버튼 -->
      <button 
        @click="isVertical = !isVertical" 
        class="px-3 py-1 rounded bg-gray-100 hover:bg-gray-200 text-sm"
      >
        {{ isVertical ? '가로보기' : '세로보기' }}
      </button>
    </div>

    <div class="overflow-x-auto">
      <!-- 가로 테이블 -->
      <table v-if="!isVertical" class="min-w-full border-collapse">
        <thead>
          <tr>
            <th class="border p-2 text-center" rowspan="2">약 이름</th>
            <th v-for="date in dateList" :key="date" class="border p-2 text-center" colspan="4">
              {{ date }}
            </th>
          </tr>
          <tr>
            <template v-for="date in dateList" :key="`times-${date}`">
              <th class="border p-2 text-center">아침</th>
              <th class="border p-2 text-center">점심</th>
              <th class="border p-2 text-center">저녁</th>
              <th class="border p-2 text-center">자기전</th>
            </template>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(medRow, medName) in groupedByMedication" :key="medName">
            <td class="border p-2 font-bold text-center">{{ medName }}</td>
            <template v-for="date in dateList" :key="`data-${date}-${medName}`">
              <template v-if="isAllTimesEmpty(medRow[date])">
                <td class="border p-2 text-center text-gray-500" colspan="4">
                  복약정보없음
                </td>
              </template>
              <template v-else>
                <template v-for="time in ['morning', 'lunch', 'dinner', 'sleep']" :key="`${date}-${time}`">
                  <td v-if="!isEmpty(medRow[date], time)" class="border p-2 text-center">
                    <span :class="{'text-green-500': medRow[date]?.[`${time}Taking`], 'text-red-500': !medRow[date]?.[`${time}Taking`]}">
                      {{ medRow[date]?.[`${time}Taking`] ? '✅' : '❌' }}
                    </span>
                  </td>
                  <td v-else class="border p-2 text-center text-gray-500">
                    복약정보없음
                  </td>
                </template>
              </template>
            </template>
          </tr>
        </tbody>
      </table>

      <!-- 세로 테이블 -->
      <table v-else class="min-w-full border-collapse">
        <thead>
          <tr>
            <th class="border p-2 text-center">날짜</th>
            <th class="border p-2 text-center">시간</th>
            <template v-for="(medRow, medName) in groupedByMedication" :key="medName">
              <th class="border p-2 text-center">{{ medName }}</th>
            </template>
          </tr>
        </thead>
        <tbody>
          <template v-for="date in dateList" :key="date">
            <tr v-for="time in ['아침', '점심', '저녁', '자기전']" :key="`${date}-${time}`">
              <td v-if="time === '아침'" :rowspan="4" class="border p-2 text-center">{{ date }}</td>
              <td class="border p-2 text-center">{{ time }}</td>
              <template v-for="(medRow, medName) in groupedByMedication" :key="`${date}-${time}-${medName}`">
                <td class="border p-2 text-center">
                  <template v-if="!isAllTimesEmpty(medRow[date])">
                    <span :class="{
                      'text-green-500': medRow[date]?.[`${getTimeKey(time)}Taking`],
                      'text-red-500': !medRow[date]?.[`${getTimeKey(time)}Taking`]
                    }">
                      {{ medRow[date]?.[`${getTimeKey(time)}Taking`] ? '✅' : '❌' }}
                    </span>
                  </template>
                  <span v-else class="text-gray-500">복약정보없음</span>
                </td>
              </template>
            </tr>
          </template>
        </tbody>
      </table>
    </div>

    <button @click="emit('close')" class="mt-4 bg-[#9DBB9F] text-white px-4 py-2 rounded">
      닫기
    </button>
  </div>
</template>

<script setup>
import { ref, onMounted, defineProps, defineEmits } from "vue";
import { fetchPrescriptionDetails } from "../api/drughistory";

const props = defineProps({
  prescription: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(["close"]);

const dateList = ref([]);
const groupedByMedication = ref({});
const isVertical = ref(false);

// 시간대별 복용 여부 체크 함수들
function isEmpty(dayData, timeKey) {
  return !dayData || (dayData[`${timeKey}Taking`] === false);
}

function isMorningEmpty(dayData) { return isEmpty(dayData, 'morning'); }
function isLunchEmpty(dayData) { return isEmpty(dayData, 'lunch'); }
function isDinnerEmpty(dayData) { return isEmpty(dayData, 'dinner'); }
function isSleepEmpty(dayData) { return isEmpty(dayData, 'sleep'); }

function getTimeKey(displayTime) {
  const timeMap = {
    '아침': 'morning',
    '점심': 'lunch',
    '저녁': 'dinner',
    '자기전': 'sleep'
  };
  return timeMap[displayTime];
}

// 전체 시간대 복용 여부 체크
function isAllTimesEmpty(dayData) {
  return !dayData || (
    dayData.morningTaking === false && 
    dayData.lunchTaking === false && 
    dayData.dinnerTaking === false && 
    dayData.sleepTaking === false
  );
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

async function loadPrescriptionDetails() {
  try {
    const infoId = props.prescription.informationId;
    const historyDetails = await fetchPrescriptionDetails(infoId);
    
    if (!historyDetails || historyDetails.length === 0) {
      console.warn(`⚠️ 처방전(${infoId})의 복용 이력 없음.`);
    }

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
.overflow-x-auto {
  overflow-x: auto !important;
  white-space: nowrap !important;
}

.modal-container {
  width: 90vw !important;
  max-width: 800px !important;
  max-height: 80vh !important;
  background-color: white;
  border-radius: 10px;
  padding: 24px;
  overflow: hidden !important;
  display: flex;
  flex-direction: column;
}

.overflow-x-auto::-webkit-scrollbar {
  height: 8px;
}

.overflow-x-auto::-webkit-scrollbar-thumb {
  background-color: #ccc !important;
  border-radius: 4px !important;
}
</style>