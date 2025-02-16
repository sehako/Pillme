export function transformPrescriptionsToEvents(prescriptions, options = {}) {
  const {
    mode = "detailed", // 기본값: 'detailed'
    prescriptionColors = ["#4E7351", "#FFFDEC", "#9DBB9F26", "#3D5A3F"],
    getTextColor = (bg) => {
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
  
    let startDate = p.startDate;
    let endDate = p.endDate;
  
    if (!startDate || !endDate) {
      console.warn(`⚠️ [DEBUG] startDate 또는 endDate가 없음. medicationPeriod에서 추출 시도.`);
      const periodMatch = p.medicationPeriod?.match(/(\d{4}-\d{2}-\d{2})/g);
      if (periodMatch && periodMatch.length === 2) {
        [startDate, endDate] = periodMatch;
      } else {
        console.error("🚨 [DEBUG] medicationPeriod에서 날짜 추출 실패:", p.medicationPeriod);
        return null;
      }
    }
  
    if (!startDate || !endDate) {
      console.error("🚨 [DEBUG] 날짜 정보가 없어 이벤트를 생성할 수 없습니다:", p);
      return null;
    }
  
    // ✅ 날짜 변환: YYYY-MM-DD → JavaScript Date 객체 변환
    const startDateObj = new Date(startDate);
    const endDateObj = new Date(endDate);
  
    // ✅ FullCalendar에서는 종료일을 하루 뒤로 설정해야 정상적으로 표시됨
    endDateObj.setDate(endDateObj.getDate() + 1);
  
    // ✅ `toISOString().split("T")[0]`을 사용하여 YYYY-MM-DD 형식 유지
    const formattedStartDate = startDateObj.toISOString().split("T")[0];
    const formattedEndDate = endDateObj.toISOString().split("T")[0];
  
    let title = "";
    if (mode === "simple") {
      title = "처방전 일정 (색상 참조)";
    } else {
      const medicationNames = p.medications.split(", ").join(", ");
      title = p.diseaseName ? `${p.diseaseName} - ${medicationNames}` : medicationNames;
      if (p.hospital) {
        title += `\n(${p.hospital})`;
      }
    }
  
    return {
      start: formattedStartDate,
      end: formattedEndDate,
      allDay: true,
      backgroundColor: bg,
      textColor,
      borderColor: "#000",
      title,
      extendedProps: { prescription: p },
    };
  }).filter(event => event !== null);
}
