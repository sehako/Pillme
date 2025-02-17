<template>
  <div class="full-calendar-container relative">
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
import { ref, computed, defineProps, onMounted, onUnmounted, nextTick } from "vue";
import FullCalendar from "@fullcalendar/vue3";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import { transformPrescriptionsToEvents } from "../composables/useCalendarEvents";

// props 정의: mode, prescriptions, viewMode ("month" 또는 "week")
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

const calendarEvents = computed(() => {
  return transformPrescriptionsToEvents(props.prescriptions, { mode: props.mode });
});

// FullCalendar 옵션: viewMode에 따라 initialView와 버튼 텍스트 변경
const calendarOptions = computed(() => ({
  plugins: [dayGridPlugin, interactionPlugin],
  locale: "ko",
  initialView: props.viewMode === "week" ? "dayGridWeek" : "dayGridMonth",
  height: "auto",
  aspectRatio: 1.5,
  headerToolbar: {
    left: "prev",
    center: "title",
    right: "next",
  },
  buttonText: {
    prev: props.viewMode === "week" ? "이전 주" : "이전",
    next: props.viewMode === "week" ? "다음 주" : "다음",
  },
  events: calendarEvents.value,
  eventDisplay: "block",
  dateClick: onDateClick,
  eventClick: onEventClick,
  dayCellDidMount: onDayCellDidMount,
}));

onMounted(() => {
  window.addEventListener("resize", updateCalendarSize);
  updateCalendarSize();
});

onUnmounted(() => {
  window.removeEventListener("resize", updateCalendarSize);
});
</script>

<style scoped>
.full-calendar-container {
  @apply w-full flex flex-col items-center;
  min-height: 300px;
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

/* FullCalendar 버튼 스타일 오버라이드 */
::v-deep .fc-button {
  background-color: #FFFDEC !important;
  border: 1px solid #9DBB9F !important;
  color: #4E7351 !important;
}
::v-deep .fc-button:hover {
  background-color: #9DBB9F !important;
  border-color: #9DBB9F !important;
  color: #FFFDEC !important;
}
</style>
