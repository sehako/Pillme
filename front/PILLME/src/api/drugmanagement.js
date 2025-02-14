import apiClient from '.';
import { useUserStore } from '../stores/user';

export const fetchManagementData = async () => {
  const userStore = useUserStore();

  // ✅ 자동으로 memberId 가져오기 (없으면 토큰 재발급 후 반환)
  const memberId = await userStore.getMemberId();

  if (!memberId) {
    console.error("❌ [DEBUG] memberId를 가져올 수 없음. 요청 중단.");
    throw new Error("멤버 ID 없음. 다시 로그인 필요.");
  }

  // ✅ 유효한 memberId로 API 요청 실행
  try {
    console.log("📡 [DEBUG] Management 데이터 요청: memberId =", memberId);
    const response = await apiClient.get('/api/v1/management', {
      params: { target: memberId }
    });
    console.log(response.data)
    return response.data; // 응답 데이터 반환
  } catch (error) {
    console.error("❌ [DEBUG] Management 데이터 요청 실패:", error);
    throw error;
  }
};
