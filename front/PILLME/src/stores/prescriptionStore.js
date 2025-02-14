import { defineStore } from "pinia";
import { ref } from "vue";

export const usePrescriptionStore = defineStore("prescription", () => {
  // 예시 처방전 데이터
  // period 뺼 거임임
  const prescriptions = ref([
    {
      hospital: "서울대병원",
      diseaseName: "고혈압",
      startDate: "2025-02-24",
      endDate: "2025-02-28",
      medications: [
        { medicationName: "다이크로짇정", period: 5 },
        { medicationName: "이티민정", period: 5 },
      ],
    },
    {
      hospital: "강남성모병원",
      diseaseName: "당뇨",
      startDate: "2025-02-26",
      endDate: "2025-03-02",
      medications: [
        { medicationName: "글루코파지정", period: 7 },
        { medicationName: "다이아벡스", period: 7 },
      ],
    },
  ]);

  // 사용자가 클릭한 날짜(문자열, 예: '2025-02-24')
  const selectedDate = ref(null);

  // 특정 날짜에 해당하는 처방전들 리턴
  function getPrescriptionsByDate(dateStr) {
    const clicked = new Date(dateStr);
    return prescriptions.value.filter((p) => {
      const start = new Date(p.startDate);
      const end = new Date(p.endDate);
      return clicked >= start && clicked <= end;
    });
  }

  return {
    prescriptions,
    selectedDate,
    getPrescriptionsByDate,
  };
});
