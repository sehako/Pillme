import { useUserStore } from "../stores/user";
import apiClient from "./index";

// 달력 날짜 변경 시 처방전 조회 API
export async function fetchCalendarPrescriptions(dateStr) {
  try {
    const userStore = useUserStore();
    const memberId = await userStore.getMemberId();
    
    // 문자열을 Date 객체로 변환 후 YYYY-MM-DD 형식으로 포맷팅
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    
    const formattedDate = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
    
    console.log("📅 [DEBUG] API 요청 정보:", {
      memberId: memberId,
      요청날짜: formattedDate
    });
    
    console.log("📅 [DEBUG] 날짜 변환:", {
      입력값: dateStr,
      변환된날짜: date,
      년: year,
      월: month,
      일: day,
      최종형식: formattedDate
    });
    
    const response = await apiClient.get('/api/v1/management/prescription', {
      params: {
        target: memberId,
        date: formattedDate
      }
    });

    console.log("📦 [DEBUG] API 응답:", {
      성공여부: response.data.isSuccess,
      코드: response.data.code,
      메시지: response.data.message,
      처방전수: response.data.result?.length || 0
    });

    // API 응답 검증
    if (!response.data.isSuccess || !Array.isArray(response.data.result)) {
      console.log('⚠️ [DEBUG] 유효하지 않은 응답:', response.data);
      return [];
    }

    // API 응답의 result 배열을 그대로 사용하되, medicationPeriod만 추가
    return response.data.result.map(prescription => ({
      ...prescription, // 기존 필드 모두 포함
      medicationPeriod: `${prescription.startDate} ~ ${prescription.endDate}` // 캘린더 표시용 추가
    }));

  } catch (error) {
    console.error('❌ [DEBUG] API 요청 실패:', {
      에러메시지: error.message,
      에러상세: error
    });
    return [];
  }
}
