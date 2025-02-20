<template>
  
  <div class="full-calendar-container relative">
    <div class="px-4 py-1 border-b bg-gray-100 text-gray-700">
    <h2 class="font-semibold text-lg">현재 복용중인 처방전</h2>
  </div>
    <FullCalendar
      ref="calendarRef"
      :options="calendarOptions"
      class="full-calendar text-xs sm:text-sm md:text-base"
    />

    <!-- 모달 (날짜 클릭 또는 이벤트 클릭 시 표시) -->
    <div 
      v-if="isModalOpen" 
      class="modal-backdrop"
      @click.self="closeModal"
    >
      <div class="modal-content">
        <h2 class="text-lg font-semibold mb-2">약 목록</h2>
        <template v-if="selectedMeds.length > 0">
          <ul class="list-disc ml-4 mb-4">
            <li v-for="(med, idx) in selectedMeds" :key="idx">
              {{ med.medicationName }}
            </li>
          </ul>
        </template>
        <template v-else>
          <p class="text-sm text-gray-600">처방전 없음</p>
        </template>
        <button 
          @click="closeModal"
          class="mt-2 px-4 py-1 bg-gray-200 hover:bg-gray-300 rounded text-sm"
        >
          닫기
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from "vue";
import FullCalendar from "@fullcalendar/vue3";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import { transformPrescriptionsToEvents } from "../composables/useCalendarEvents";
import { fetchCalendarPrescriptions } from "../api/calendarview";

const props = defineProps({
  mode: {
    type: String,
    default: "detailed"
  },
  prescriptions: {
    type: Array,
    default: () => []
  },
  viewMode: {
    type: String,
    default: "month"  // "month" 또는 "week"
  }
});

const calendarRef = ref(null);
function updateCalendarSize() {
  nextTick(() => {
    if (calendarRef.value) {
      calendarRef.value.getApi().updateSize();
    }
  });
}

// 모달 상태 & 선택된 약 목록, 선택된 날짜
const isModalOpen = ref(false);
const selectedMeds = ref([]);
const selectedDate = ref(null);

function selectDate(dateStr) {
  selectedDate.value = dateStr;
  // 처방전 데이터에서 해당 날짜에 포함되는 처방전 찾기
  const matched = props.prescriptions.filter(p => p.medicationPeriod.includes(dateStr));
  selectedMeds.value = matched.length ? matched.flatMap(p => p.medications) : [];
  isModalOpen.value = true;
}

function onDateClick(info) {
  selectDate(info.dateStr);
}
function onEventClick(info) {
  selectDate(info.event.startStr);
}

function onDayCellDidMount(info) {
  if (!selectedDate.value) return;
  const cellDate = info.date;
  const selected = new Date(selectedDate.value);
  if (
    cellDate.getFullYear() === selected.getFullYear() &&
    cellDate.getMonth() === selected.getMonth() &&
    cellDate.getDate() === selected.getDate()
  ) {
    info.el.classList.add("bg-yellow-200");
  }
}

function closeModal() {
  isModalOpen.value = false;
  selectedDate.value = null;
}

const emit = defineEmits(['update:prescriptions']);

// FullCalendar 옵션 수정
const calendarOptions = computed(() => ({
  plugins: [dayGridPlugin, interactionPlugin],
  locale: "ko",
  initialView: "dayGridWeek",
views: {
  dayGridWeek: {
    duration: { weeks: 2 }, // 현재 주 포함 3주 표시
  }
},

  height: "auto",
  aspectRatio: 1.5,
  headerToolbar: {
    left: "",
    center: "",
    right: "",
  },
  events: calendarEvents.value,
  eventDisplay: "block",
  eventClick: onEventClick,
  dayCellDidMount: onDayCellDidMount,
  datesSet: async (dateInfo) => {
    try {
      // console.log("🔄 달력 날짜 변경됨");
      const start = dateInfo.view.currentStart;
      const year = start.getFullYear();
      const month = String(start.getMonth() + 1).padStart(2, '0');
      const formattedDate = `${year}-${month}-01`;
      // console.log("📅 요청할 날짜:", formattedDate);
      
      // API 호출하여 해당 월의 처방전 데이터 가져오기
      // console.log("📡 처방전 데이터 요청 시작");
      const prescriptions = await fetchCalendarPrescriptions(formattedDate);
      // console.log("📦 받은 처방전 데이터:", prescriptions);
      
      // 부모 컴포넌트 업데이트
      // console.log("🔄 부모 컴포넌트 처방전 데이터 업데이트");
      emit('update:prescriptions', prescriptions);
      
      // 캘린더 이벤트 업데이트 확인
      // console.log("📊 현재 캘린더 이벤트:", calendarEvents.value);
    } catch (error) {
      // console.error("❌ 달력 데이터 업데이트 중 오류 발생:", error);
    }
  }
}));

// 캘린더 이벤트 computed 속성
const calendarEvents = computed(() => {
  console.log("🎯 처방전 데이터로 이벤트 변환 시작");
  console.log("📋 현재 처방전 데이터:", props.prescriptions);
  const events = transformPrescriptionsToEvents(props.prescriptions, { mode: props.mode });
  console.log("✨ 변환된 캘린더 이벤트:", events);
  return events;
});

onMounted(() => {
  console.log("🔵 캘린더 컴포넌트 마운트됨");
  window.addEventListener("resize", updateCalendarSize);
  updateCalendarSize();
});

onUnmounted(() => {
  console.log("🔴 캘린더 컴포넌트 언마운트됨");
  window.removeEventListener("resize", updateCalendarSize);
});
</script>

<style scoped>
/* 모달 스타일 */
.modal-backdrop {
  @apply fixed inset-0 bg-black/30 flex items-center justify-center z-50;
}
.modal-content {
  @apply bg-white rounded p-4 max-w-xs w-full;
}

:deep() .fc-toolbar-title {
  font-size: 1rem !important; /* 원하는 크기로 조절 */
  font-weight: 500;
}

.full-calendar {
  margin-top: -18px !important; /* FullCalendar의 위쪽 여백 제거 */
}
</style>