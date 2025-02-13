<template>
  <FullCalendar :options="calendarOptions" class="w-full border border-gray-300 shadow-md bg-white p-4 rounded-lg" />
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import FullCalendar from "@fullcalendar/vue3";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";

// ✅ 유니크한 처방전 색상 팔레트
const prescriptionColors = ["#4E7351", "#3D5A3F", "#FFFDEC", "#9DBB9F26", "#EF7C8E", "#E96C7E"];

// ✅ 더미 데이터: 여러 개의 처방전
const prescriptions = ref([
  {
    hospital: "서울대병원",
    diseaseName: "고혈압",
    reader: 1,
    startDate: "2025-02-24",
    endDate: "2025-02-28",
    medications: [
      { medicationName: "다이크로짇정", period: 5 },
      { medicationName: "이티민정", period: 5 }
    ]
  },
  {
    hospital: "강남성모병원",
    diseaseName: "당뇨",
    reader: 2,
    startDate: "2025-02-26",
    endDate: "2025-03-02",
    medications: [
      { medicationName: "글루코파지정", period: 7 },
      { medicationName: "다이아벡스", period: 7 }
    ]
  },
  {
    hospital: "신촌세브란스",
    diseaseName: "",
    reader: 3,
    startDate: "2025-02-25",
    endDate: "2025-03-05",
    medications: [
      { medicationName: "베타히스틴정", period: 10 }
    ]
  }
]);

// ✅ 배경색에 따른 적절한 글자 색상 반환 함수
const getTextColor = (bgColor) => {
  const hex = bgColor.replace("#", "");
  const r = parseInt(hex.substring(0, 2), 16);
  const g = parseInt(hex.substring(2, 4), 16);
  const b = parseInt(hex.substring(4, 6), 16);
  const brightness = (r * 299 + g * 587 + b * 114) / 1000;
  return brightness > 128 ? "#000000" : "#FFFFFF"; // 밝으면 검정, 어두우면 하얀색 적용
};

// ✅ FullCalendar에서 사용할 이벤트 데이터 변환
const calendarEvents = computed(() => {
  return prescriptions.value.map((prescription, index) => {
    const startDate = prescription.startDate;
    const endDate = prescription.endDate;

    // ✅ 유니크한 배경색 선택 (순환 사용)
    const bgColor = prescriptionColors[index % prescriptionColors.length];

    // ✅ 적절한 글자 색상 선택
    const textColor = getTextColor(bgColor);

    // ✅ 1. 약물명 전부 문자열로 변환
    const medicationNames = prescription.medications.map(med => med.medicationName).join(", ");

    // ✅ 2. 병명이 있으면 포함, 없으면 제외
    let title = prescription.diseaseName ? `${prescription.diseaseName} - ${medicationNames}` : medicationNames;

    // ✅ 3. 병원이 있으면 추가 (작은 글씨로)
    if (prescription.hospital) {
      title += `\n(${prescription.hospital})`;
    }

    return {
      id: `prescription-${index}`,
      title: title,
      start: startDate,
      end: endDate,
      backgroundColor: bgColor, // ✅ 각 처방전마다 고유한 색상 적용
      borderColor: "#000000",
      textColor: textColor, // ✅ 글자 색상 자동 조정 (검정 또는 하양)
      display: "block",
      extendedProps: { prescription },
    };
  });
});

// ✅ FullCalendar 설정 (스크롤 없이 전체 보기)
const calendarOptions = computed(() => ({
  plugins: [dayGridPlugin, interactionPlugin],
  initialView: "dayGridMonth",
  headerToolbar: {
    left: "prev,next today",
    center: "title",
    right: "dayGridMonth,dayGridWeek,dayGridDay",
  },
  events: calendarEvents.value,
  height: "auto", // ✅ 캘린더 높이 자동 조정 (스크롤 없이 표시)
  slotMinHeight: 50, // ✅ 하루에 여러 처방전이 있어도 자동 확장
  eventMaxStack: true, // ✅ 일정이 많으면 자동으로 확장 (줄이지 않음)
  editable: false,
  eventDisplay: "block",
  eventDidMount: (info) => {
    const eventTitle = info.el.querySelector(".fc-event-title");
    if (eventTitle && info.event.title.includes("(")) {
      const parts = info.event.title.split("\n");
      if (parts.length > 1) {
        eventTitle.innerHTML = `<b>${parts[0]}</b><br/><span style="font-size:12px; color:#555;">${parts[1]}</span>`;
      }
    }
  }
}));

onMounted(() => {
  console.log("📅 FullCalendar 이벤트 데이터:", calendarEvents.value);
});
</script>
