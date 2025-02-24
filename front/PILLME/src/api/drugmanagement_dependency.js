import apiClient from './index';
import { useUserStore } from '../stores/user';

// Management 데이터 가져오기
export const fetchManagementData = async (dependentId) => {
  const userStore = useUserStore();
  const memberId = await userStore.getMemberId(); // 요청을 보내는 사용자 ID

  if (!memberId || !dependentId) {
    console.error("[DEBUG] 필수 ID 누락: memberId 또는 dependentId가 없습니다.");
    throw new Error("ID 없음. 요청 중단.");
  }

  try {
    // console.log(`[DEBUG] Management 데이터 요청: target=${dependentId}, reader=${memberId}`);
    const response = await apiClient.get('/api/v1/management', {
      params: { target: dependentId, reader: memberId }
    });
    // console.log("가족 복약체크 정보",response.data);
    return response.data;
  } catch (error) {
    console.error("[DEBUG] Management 데이터 요청 실패:", error);
    throw error;
  }
};
