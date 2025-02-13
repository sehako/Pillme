// src/composables/useCalendarEvents.js
import { ref } from 'vue';

/** 
 * 처방전 → FullCalendar 이벤트 변환
 * @param {Array} prescriptions  처방전 배열
 * @param {Object} options       변환 옵션 (예: 상세/간소 등)
 * @returns {Array} events       FullCalendar 이벤트 배열
 */
export function transformPrescriptionsToEvents(prescriptions, options = {}) {
  const {
    mode = 'detailed',         // 'detailed' | 'simple' 등 (기본값: 상세)
    prescriptionColors = ["#4E7351", "#3D5A3F", "#FFFDEC", "#9DBB9F26"],
    getTextColor = (bg) => {   // 배경색에 따라 글자색 정하는 함수
      // (기존 getTextColor 로직)
      const hex = bg.replace("#", "");
      const r = parseInt(hex.substring(0, 2), 16);
      const g = parseInt(hex.substring(2, 4), 16);
      const b = parseInt(hex.substring(4, 6), 16);
      const brightness = (r * 299 + g * 587 + b * 114) / 1000;
      return brightness > 128 ? "#000000" : "#FFFFFF";
    },
  } = options;

  return prescriptions.map((p, i) => {
    const bg = prescriptionColors[i % prescriptionColors.length];
    const textColor = getTextColor(bg);

    // 원래는 diseaseName, medications, hospital 정보를 
    // 문자열로 합쳐 title을 만들었지만,
    // mode(옵션)에 따라 분기할 수 있음.
    let title = '';
    if (mode === 'simple') {
      title = '색으로 처방전을 분류해놨습니다';
    } else {
      // 상세 모드: 기존처럼 병명, 약물명, 병원명 표시
      const medicationNames = p.medications.map(m => m.medicationName).join(", ");
      title = p.diseaseName 
        ? `${p.diseaseName} - ${medicationNames}`
        : medicationNames;
      if (p.hospital) {
        title += `\n(${p.hospital})`;
      }
    }

    return {
      start: p.startDate,
      end: p.endDate,
      backgroundColor: bg,
      textColor,
      borderColor: "#000",
      title,
      extendedProps: { prescription: p }, // 필요하면 원본 데이터 보관
    };
  });
}
