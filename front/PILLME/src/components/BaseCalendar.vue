<template>
  <div class="full-calendar-container relative">
    <FullCalendar
    ref="calendarRef"
      :options="calendarOptions"
      class="full-calendar text-xs sm:text-sm md:text-base"
    />

    <!-- 모달 (날짜 클릭 or 이벤트 클릭 시 표시) -->
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
import { ref, computed, defineProps,onMounted, onUnmounted, nextTick } from "vue";
import FullCalendar from "@fullcalendar/vue3";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import { transformPrescriptionsToEvents } from '../composables/useCalendarEvents';

const calendarRef = ref(null);

function updateCalendarSize() {
  nextTick(() => {
    if (calendarRef.value) {
      calendarRef.value.getApi().updateSize();
    }
  });
}
/** ✅ (A) props 정의 */
const props = defineProps({
  mode: {
    type: String,
    default: "detailed"
  },
  prescriptions: {
    type: Array,
    default: () => []
  }
});

// 모달 상태 & 선택된 약 목록
const isModalOpen = ref(false);
const selectedMeds = ref([]);
const selectedDate = ref(null);

/* ===============================
   2) 날짜 선택 공통 로직
=============================== */
function selectDate(dateStr) {
  // (1) 날짜 저장(하이라이트용)
  selectedDate.value = dateStr;

  // (2) 해당 날짜의 처방전 -> 약 목록
  const matched = props.prescriptions.filter(p => p.medicationPeriod.includes(dateStr));
  selectedMeds.value = matched.length
    ? matched.flatMap((p) => p.medications)
    : [];

  // (3) 모달 열기
  isModalOpen.value = true;
}

/* ===============================
   3) dateClick & eventClick
=============================== */
function onDateClick(info) {
  selectDate(info.dateStr);
}
function onEventClick(info) {
  selectDate(info.event.startStr);
}

/* ===============================
   4) 날짜 셀 렌더링(하이라이트)
=============================== */
function onDayCellDidMount(info) {
  if (!selectedDate.value) return;

  const cellDate = info.date;
  const selected = new Date(selectedDate.value);

  if (
    cellDate.getFullYear() === selected.getFullYear() &&
    cellDate.getMonth() === selected.getMonth() &&
    cellDate.getDate() === selected.getDate()
  ) {
    // 하이라이트
    info.el.classList.add("bg-yellow-200");
  }
}

/* ===============================
   5) 모달 닫기 => 하이라이트 해제
=============================== */
function closeModal() {
  isModalOpen.value = false;
  selectedDate.value = null;
}

/* ===============================
   6) 이벤트 데이터 변환
      props.mode 에 따라 분기
=============================== */
const calendarEvents = computed(() => {
  return transformPrescriptionsToEvents(props.prescriptions, { mode: props.mode });
});

/* ===============================
   7) FullCalendar 옵션
=============================== */
const calendarOptions = computed(() => ({
  plugins: [dayGridPlugin, interactionPlugin],
  locale: "ko",
  initialView: "dayGridMonth",
  height: "auto",
  aspectRatio: 1.5, // ✅ 반응형 비율 조정
  headerToolbar: {
    left: "prev",
    center: "title",
    right: "next",
  },
  buttonText: {
    prev: "이전",
    next: "다음",
  },
  events: calendarEvents.value,
  eventDisplay: "block",
  dateClick: onDateClick,
  eventClick: onEventClick,
  dayCellDidMount: onDayCellDidMount,
}));
onMounted(() => {
  window.addEventListener("resize", updateCalendarSize);
  updateCalendarSize(); // ✅ 초기 로딩 시에도 크기 조정
});

onUnmounted(() => {
  window.removeEventListener("resize", updateCalendarSize);
});
</script>

<style scoped>
.full-calendar-container {
  @apply w-full flex flex-col items-center;
  min-height: 300px; /* ✅ 최소 높이 설정 (원하는 값으로 조정 가능) */
}
/* 날짜 클릭 시 하이라이트 */
.bg-yellow-200 {
  background-color: rgb(253 230 138 / 0.8) !important;
}

/* 모달 스타일 */
.modal-backdrop {
  @apply fixed inset-0 bg-black/30 flex items-center justify-center z-50;
}
.modal-content {
  @apply bg-white rounded p-4 max-w-xs w-full;
}
</style>
