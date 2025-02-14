<template>
  <div class="full-calendar-container relative">
    <FullCalendar
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
/* ===============================
   1) 기존 코드 + props( mode )
=============================== */
import { ref, computed, defineProps } from "vue";
import { usePrescriptionStore } from "../stores/prescriptionStore";
import FullCalendar from "@fullcalendar/vue3";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import { transformPrescriptionsToEvents } from '../composables/useCalendarEvents';
/** ✅ (A) props 정의 */
const props = defineProps({
  mode: {
    type: String,
    default: "detailed"
    // 예: 'simple' | 'detailed' 등
  }
});

// Pinia 스토어
const store = usePrescriptionStore();

// 모달 상태 & 선택된 약 목록
const isModalOpen = ref(false);
const selectedMeds = ref([]);

/* ===============================
   2) 날짜 선택 공통 로직
=============================== */
function selectDate(dateStr) {
  // (1) 날짜 저장(하이라이트용)
  store.selectedDate = dateStr;

  // (2) 해당 날짜의 처방전 -> 약 목록
  const matched = store.getPrescriptionsByDate(dateStr);
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
  // 이벤트 시작 날짜 기준
  selectDate(info.event.startStr);
}

/* ===============================
   4) 날짜 셀 렌더링(하이라이트)
=============================== */
function onDayCellDidMount(info) {
  // 아직 날짜가 없으면 패스
  if (!store.selectedDate) return;

  const cellDate = info.date;
  const selected = new Date(store.selectedDate);

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
  // ✅ store.selectedDate 초기화 => 하이라이트 해제
  store.selectedDate = null;
}

/* ===============================
   6) 색상/글자색 계산
=============================== */
const prescriptionColors = ["#4E7351", "#3D5A3F", "#FFFDEC", "#9DBB9F26"];
function getTextColor(bg) {
  const hex = bg.replace("#", "");
  const r = parseInt(hex.substring(0, 2), 16);
  const g = parseInt(hex.substring(2, 4), 16);
  const b = parseInt(hex.substring(4, 6), 16);
  const brightness = (r * 299 + g * 587 + b * 114) / 1000;
  return brightness > 128 ? "#000000" : "#FFFFFF";
}

/* ===============================
   7) 이벤트 데이터 변환
      props.mode 에 따라 분기
=============================== */
const calendarEvents = computed(() => {
  return transformPrescriptionsToEvents(store.prescriptions, { mode: props.mode });
});

/* ===============================
   8) FullCalendar 옵션
=============================== */
const calendarOptions = computed(() => ({
  plugins: [dayGridPlugin, interactionPlugin],
  locale: "ko",
  initialView: "dayGridMonth",
  height: "auto",
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
  eventDidMount(info) {
    if (info.event.title.includes("\n")) {
      const [mainTitle, sub] = info.event.title.split("\n");
      const el = info.el.querySelector(".fc-event-title");
      if (el) {
        el.innerHTML = `
          <b>${mainTitle}</b><br/>
          <span class="text-xs text-gray-700">${sub}</span>
        `;
      }
    }
  },
}));
</script>

<style scoped>
.full-calendar-container {
  @apply w-full relative;
}

/* 날짜 클릭 시 하이라이트 */
.bg-yellow-200 {
  background-color: rgb(253 230 138 / 0.8) !important;
}

/* 모달 스타일 예시 */
.modal-backdrop {
  @apply fixed inset-0 bg-black/30 flex items-center justify-center z-50;
}
.modal-content {
  @apply bg-white rounded p-4 max-w-xs w-full;
}

/* FullCalendar 스타일 */
.full-calendar .fc-toolbar {
  @apply flex items-center justify-center gap-2;
}
.full-calendar .fc-toolbar-title {
  @apply text-base sm:text-lg md:text-xl font-semibold;
}
.full-calendar .fc-button {
  @apply bg-white border border-gray-300 rounded px-2 py-1
         hover:bg-gray-100 transition-colors duration-200
         text-xs sm:text-sm md:text-base;
}
.full-calendar .fc-daygrid-day-number {
  @apply text-xs sm:text-sm md:text-base;
}
</style>
