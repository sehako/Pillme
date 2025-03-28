import apiClient from "./index";

// 홈뷰의 옐로우 카드 부분의 복약체크 (현재 복약 내역 일괄 복약체크 *시간대별 *현재날짜)
export const fetchAllDrugCheck = async (timePeriod) => {
  try {

    // ✅ API 요청 (PATCH)
    const response = await apiClient.patch("/api/v1/management/check-current-taking/all", {"time": timePeriod} );

    // ✅ 응답 데이터 디버깅 로그
    console.log("[fetchAllDrugCheck] 응답 수신:", response.data);

    return response.data.result;
  } catch (error) {
    console.error("[fetchAllDrugCheck] 복약체크 조회 실패:", error);
    return [];
  }
};



// 캘린더뷰의 화이트 카드 부분의 복약체크 (특정 날짜의 복약 내역 일괄 복약체크 *시간대별 *특정날짜)

export async function updateCheckTaking({ managementId, time }) {
  try {
    const payload = {
      managementId: Number(managementId),
      time,
    };
    const response = await apiClient.patch('/api/v1/management/check-taking', payload);
    return response.data;
  } catch (error) {
    console.error('[API Error] updateCheckTaking:', error);
    throw error;
  }
}
