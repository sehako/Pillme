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
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from "vue";
import FullCalendar from "@fullcalendar/vue3";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import { fetchSelfCalendarPrescriptions } from "../api/calendarview";
import { useUserStore } from "../stores/user";

const userStore = useUserStore();

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
    default: "month"
  },
  dependentId: {
    type: [Number, String],
    required: true
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

// ✅ 내부 처방전 데이터 상태 추가
const internalPrescriptions = ref(props.prescriptions);

// ✅ props.prescriptions가 변경될 때 내부 상태 업데이트
watch(() => props.prescriptions, (newPrescriptions) => {
  internalPrescriptions.value = newPrescriptions;
});

// apiCallCount를 ref로 컴포넌트 내부에서 관리
const apiCallCount = ref(0);

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
    prev: props.viewMode === "week" ? "이전 주" : "<",
    next: props.viewMode === "week" ? "다음 주" : ">",
  },
  events: calendarEvents.value,
  eventDisplay: "block",
  dateClick: onDateClick,
  eventClick: onEventClick,
  dayCellDidMount: onDayCellDidMount,
  datesSet: async (dateInfo) => {
    // 최초 렌더링 시에는 API를 호출하지 않고 props의 데이터를 사용
    if (apiCallCount.value === 0) {
      console.log("📌 최초 렌더링: props의 처방전 데이터 사용");
      apiCallCount.value++;
      return;
    }

    try {
      console.log("🔄 달력 날짜 변경됨");
      const start = dateInfo.view.currentStart;
      const year = start.getFullYear();
      const month = String(start.getMonth() + 1).padStart(2, '0');
      const formattedDate = `${year}-${month}-01`;
      const targetId = await userStore.getMemberId();
      const response = await fetchSelfCalendarPrescriptions(formattedDate, targetId);
      
      if (response.isSuccess && response.result) {
        // 새로운 데이터 형식에 맞게 처리
        const prescriptions = response.result.map(item => ({
          id: item.informationId,
          diseaseName: item.diseaseName || '병명 미등록',
          hospital: item.hospital || '병원 미등록',
          medicationPeriod: `${item.startDate} ~ ${item.endDate}`,
          startDate: item.startDate,
          endDate: item.endDate,
          registrationType: item.registrationType
        }));
        
        console.log("📦 변환된 처방전 데이터:", prescriptions);
        internalPrescriptions.value = prescriptions;
        emit('update:prescriptions', prescriptions);
      } else {
        console.warn("❗ 유효하지 않은 응답:", response);
        internalPrescriptions.value = [];
        emit('update:prescriptions', []);
      }
      
      console.log("📊 현재 캘린더 이벤트:", calendarEvents.value);
    } catch (error) {
      console.error("❌ 달력 데이터 업데이트 중 오류 발생:", error);
      internalPrescriptions.value = [];
      emit('update:prescriptions', []);
    }
  }
}));

// ✅ calendarEvents computed 속성에 안전장치 추가
const calendarEvents = computed(() => {
  console.log("🎯 처방전 데이터로 이벤트 변환 시작");
  const prescriptions = internalPrescriptions.value || [];
  
  try {
    const events = prescriptions.map(prescription => {
      // endDate를 하루 추가
      const endDateObj = new Date(prescription.endDate);
      endDateObj.setDate(endDateObj.getDate() + 1);
      const adjustedEndDate = endDateObj.toISOString().split('T')[0]; // YYYY-MM-DD 형식 변환

      return {
        title: prescription.diseaseName || '병명 미등록',
        start: prescription.startDate,
        end: adjustedEndDate, // 하루 추가된 endDate 적용
        backgroundColor: '#9DBB9F',
        borderColor: '#9DBB9F',
        textColor: '#FFFDEC',
        extendedProps: {
          hospital: prescription.hospital,
          registrationType: prescription.registrationType
        }
      };
    });

    console.log("✨ 변환된 캘린더 이벤트:", events);
    return events;
  } catch (error) {
    console.error("❌ 이벤트 변환 중 오류 발생:", error);
    return [];
  }
});


onMounted(async () => {
  console.log("🔵 피부양자 캘린더 컴포넌트 마운트됨");
  window.addEventListener("resize", updateCalendarSize);
  updateCalendarSize();

  // 마운트 후 약간의 지연을 두고 이전/다음 버튼 클릭 시뮬레이션
  await nextTick();
  setTimeout(() => {
    const calendar = calendarRef.value.getApi();
    calendar.prev(); // 이전 버튼 클릭
    setTimeout(() => {
      calendar.next(); // 다음 버튼 클릭
    }, 10);
  }, 10);
});

onUnmounted(() => {
  console.log("🔴 피부양자 캘린더 컴포넌트 언마운트됨");
  window.removeEventListener("resize", updateCalendarSize);
  apiCallCount.value = 0; // 컴포넌트 언마운트 시 카운트 초기화
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

/* 헤더 툴바 스타일 */
.fc-toolbar {
  background-color: #4E7351 !important; /* 헤더 배경색 */
  border: none; /* 테두리 제거 */
  padding: 10px; /* 패딩 조정 */
}

/* 버튼 스타일 */
.fc-button {
  background-color: #4E7351 !important; /* 버튼 배경색 */
  color: white; /* 버튼 텍스트 색상 */
  border: none; /* 버튼 테두리 제거 */
  padding: 5px 10px; /* 버튼 패딩 */
  border-radius: 5px; /* 버튼 모서리 둥글게 */
}

.fc-button:hover {
  background-color: #4E7351 !important; /* 버튼 호버 시 배경색 */
}

/* 제목 스타일 */
:deep() .fc-toolbar-title {
  font-size: 1.2rem; /* 제목 크기 */
  font-weight: bold; /* 제목 두께 */
  color: gray-700; /* 제목 색상 */
}

/* 이전 버튼 스타일 */
:deep() .fc-prev-button {
  background-color: #4E7351; /* 이전 버튼 배경색 */
  color: white; /* 이전 버튼 텍스트 색상 */
  border: none; /* 이전 버튼 테두리 제거 */
  padding: 4px 10px; /* 이전 버튼 패딩 */
  border-radius: 4px; /* 이전 버튼 모서리 둥글게 */
  margin-left: 72px;
}

:deep() .fc-prev-button:hover {
  background-color: #4E7351; /* 이전 버튼 호버 시 배경색 */
}

/* 다음 버튼 스타일 */
:deep() .fc-next-button {
  background-color: #4E7351 !important;; /* 다음 버튼 배경색 */
  color: white; /* 다음 버튼 텍스트 색상 */
  border: none; /* 다음 버튼 테두리 제거 */
  padding: 4px 10px; /* 다음 버튼 패딩 */
  border-radius: 4px; /* 다음 버튼 모서리 둥글게 */
  margin-right: 72px;
}

:deep() .fc-next-button:hover {
  background-color: #4E7351; /* 다음 버튼 호버 시 배경색 */
}
</style>