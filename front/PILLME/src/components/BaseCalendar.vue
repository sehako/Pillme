<template>
  <FullCalendar 
    :options="calendarOptions"
    class="w-full border border-gray-300 shadow-md bg-white p-4 rounded-lg"
  />
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import FullCalendar from "@fullcalendar/vue3";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";

// ✅ 테스트용 `results` 배열 (복수의 처방전 데이터)
const results = ref([
  {
    medicineList: ["타이레놀", "아스피린"],
    startDate: "2025-02-10",
    endDate: "2025-02-14",
    morningMeds: ["타이레놀"],
    lunchMeds: ["아스피린"],
    dinnerMeds: ["타이레놀", "아스피린"],
    bedtimeMeds: [],
    hospitalName: "서울대병원",
    diseaseName: "감기"
  },
  {
    medicineList: ["항생제"],
    startDate: "2025-02-12",
    endDate: "2025-02-16",
    morningMeds: ["항생제"],
    lunchMeds: [],
    dinnerMeds: ["항생제"],
    bedtimeMeds: ["항생제"],
    hospitalName: "강남성모병원",
    diseaseName: "기관지염"
  },
]);

// ✅ 복용 시간대별 색상 설정
const timePeriodColors = {
  morning: "#FF5733",    // 아침 (🔴 주황)
  lunch: "#3498db",      // 점심 (🔵 파랑)
  dinner: "#2ecc71",     // 저녁 (🟢 초록)
  bedtime: "#9b59b6"     // 자기 전 (🟣 보라)
};

// ✅ 날짜 변환 함수 (YYYY-MM-DD 형식)
const parseDate = (dateString) => {
  const year = dateString.substring(0, 4);
  const month = dateString.substring(5, 7);
  const day = dateString.substring(8, 10);
  return `${year}-${month}-${day}`;
};

// ✅ FullCalendar에서 사용할 이벤트 데이터 변환
const calendarEvents = computed(() => {
  const events = [];

  results.value.forEach((result, index) => {
    const startDate = parseDate(result.startDate);
    const endDate = parseDate(result.endDate);

    // ✅ 기본적인 일정 블록 (복용 기간)
    events.push({
      id: `med-period-${index}`,
      title: `${result.hospitalName} - ${result.diseaseName}`,
      start: startDate,
      end: endDate,
      backgroundColor: "#95a5a6",
      borderColor: "#7f8c8d",
      display: "block",
    });

    // 📌 복용 시간대별 개별 일정 추가
    Object.keys(timePeriodColors).forEach((timePeriod) => {
      if (result[`${timePeriod}Meds`].length > 0) {
        events.push({
          id: `med-${index}-${timePeriod}`,
          title: result[`${timePeriod}Meds`].join(", "),
          start: startDate,
          end: endDate,
          backgroundColor: timePeriodColors[timePeriod],
          borderColor: "#ffffff",
          display: "block",
        });
      }
    });
  });

  return events;
});

// ✅ FullCalendar 설정
const calendarOptions = computed(() => ({
  plugins: [dayGridPlugin, interactionPlugin],
  initialView: "dayGridMonth",
  headerToolbar: {
    left: "prev,next today",
    center: "title",
    right: "dayGridMonth,dayGridWeek,dayGridDay",
  },
  events: calendarEvents.value, // ✅ 동적으로 이벤트 바인딩
  editable: false, // ✅ 사용자가 직접 일정 수정 불가
  eventDisplay: "block", // ✅ 모든 이벤트 개별적으로 표시 (겹쳐 보이지 않도록)
}));

// ✅ 마운트 시 로그 출력
onMounted(() => {
  console.log("📅 FullCalendar 이벤트 데이터:", calendarEvents.value);
});
</script>
