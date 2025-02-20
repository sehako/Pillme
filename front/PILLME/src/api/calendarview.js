import { useUserStore } from "../stores/user";
import apiClient from "./index";

// 달력 날짜 변경 시 처방전 조회 API
export const fetchCalendarPrescriptions = async (date, target) => {
  if (!target) {
    throw new Error('대상 ID가 필요합니다.');
  }
  
  try {
    const response = await apiClient.get(`/api/v1/management/prescription`, {
      params: {
        target,
        date
      },
      timeout: 5000
    });
    
    if (!response.data) {
      throw new Error('처방전 데이터가 없습니다.');
    }
    
    return response.data;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error.code === 'ECONNABORTED') {
        console.error('요청 시간 초과:', error);
      } else if (error.response) {
        console.error('서버 응답 에러:', error.response.status);
      }
    }
    console.error('처방전 데이터 조회 실패:', error);
    throw error;
  }
};



// 본인 처방전 조회 API
export const fetchSelfCalendarPrescriptions = async (date, target) => {
  try {
    const response = await apiClient.get(`/api/v1/management/prescription`, {
      params: {
        target,
        date
      }
    });
    
    if (!response.data) {
      throw new Error('처방전 데이터가 없습니다.');
    }
    
    return response.data;
  } catch (error) {
    console.error('처방전 데이터 조회 실패:', error);
    throw error;
  }
};
