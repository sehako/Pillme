<template>
  <VCalendar 
    v-model="selectedDate"
    :attributes="calendarAttributes"
    class="w-full rounded-lg shadow-md border border-gray-200 bg-white p-4"
  />
</template>

<script setup>
import { ref, computed, onMounted } from "vue";

// ✅ 선택한 날짜
const selectedDate = ref(new Date());

// ✅ 테스트용 `results` 배열 (복수의 처방전 데이터)
const results = ref([
  {
    medicineList: ["타이레놀", "아스피린"],
    startDate: "20250210",
    endDate: "20250214",
    morningMeds: ["타이레놀"],
    lunchMeds: ["아스피린"],
    dinnerMeds: ["타이레놀", "아스피린"],
    bedtimeMeds: [],
    hospitalName: "서울대병원",
    diseaseName: "감기"
  },
  {
    medicineList: ["항생제"],
    startDate: "20250212",
    endDate: "20250216",
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
  morning: "red",    // 아침 (🔴 빨강)
  lunch: "blue",     // 점심 (🔵 파랑)
  dinner: "green",   // 저녁 (🟢 초록)
  bedtime: "purple"  // 자기 전 (🟣 보라)
};

// ✅ 날짜 변환 함수 (YYYYMMDD → Date 객체)
const parseDate = (dateString) => {
  const year = parseInt(dateString.substring(0, 4), 10);
  const month = parseInt(dateString.substring(4, 6), 10) - 1;
  const day = parseInt(dateString.substring(6, 8), 10);
  return new Date(year, month, day);
};

// ✅ `results` 배열을 기반으로 `VCalendar`의 attributes 생성
const calendarAttributes = computed(() => {
  const attributes = [];
  const dateEvents = {}; // 같은 날짜에 있는 처방을 그룹화하기 위한 객체

  // 모든 처방전(result) 확인
  results.value.forEach((result, index) => {
    const startDate = parseDate(result.startDate);
    const endDate = parseDate(result.endDate);

    // ✅ 같은 날짜에 있는 처방을 그룹화
    let currentDate = new Date(startDate);
    while (currentDate <= endDate) {
      const dateKey = currentDate.toISOString().split("T")[0]; // "YYYY-MM-DD" 형태로 변환
      if (!dateEvents[dateKey]) {
        dateEvents[dateKey] = [];
      }
      dateEvents[dateKey].push(result);
      currentDate.setDate(currentDate.getDate() + 1);
    }

    // ✅ 연속된 기간을 가로로 정렬하면서 하나의 블록으로 연결
    attributes.push({
      key: `med-period-${index}`,
      dates: { start: startDate, end: endDate }, // ✅ 연속된 기간 적용
      highlight: {
        color: "",
        fillMode: "", // ✅ 연속된 기간을 하나로 연결
        opacity: 0.2, // ✅ 배경 색상 투명하게 (복용 기간 강조)
        isRange: false, // ✅ 범위를 하나의 줄로 연결
      },
      bar: {
        color: "gray",
        position: "row", // ✅ 가로로 정렬
        order: index, // ✅ 여러 개가 겹칠 경우 옆으로 정렬
      },
    });

    // 📌 복용 시간대별로 색상 적용 (아침/점심/저녁/자기전)
    Object.keys(timePeriodColors).forEach((timePeriod) => {
      if (result[`${timePeriod}Meds`].length > 0) {
        attributes.push({
          key: `med-${index}-${timePeriod}`,
          dates: { start: startDate, end: endDate }, // ✅ 연속된 기간 유지
          highlight: {
            color: timePeriodColors[timePeriod],
            fillMode: "outline", // ✅ 개별 복용 시간대는 색상 채우기
            isRange: false, // ✅ 연속된 기간을 하나로 연결
          },
          bar: {
            color: timePeriodColors[timePeriod],
            position: "bottom", // ✅ 가로 정렬
            order: index, // ✅ 처방전이 많을 경우 가로 정렬
          }
        });
      }
    });
  });

  return attributes;
});

// ✅ 마운트 시 더미 데이터 로드 (테스트용)
onMounted(() => {
  console.log("🔍 테스트용 results 객체:", results.value);
  console.log("📅 변환된 attributes:", calendarAttributes.value);
});
</script>
