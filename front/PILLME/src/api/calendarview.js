import { useUserStore } from "../stores/user";
import apiClient from "./index";
import axios from "axios";

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
export const fetchSelfCalendarPrescriptions = async (date, memberId) => {
  if (!memberId) {
    throw new Error('회원 ID가 필요합니다.');
  }

  try {
    const response = await apiClient.get(`/api/v1/management/prescription`, {
      params: {
        memberId,  // 본인 ID
        date
      }
    });
    
    // 응답 데이터가 배열인지 확인
    if (!Array.isArray(response.data)) {
      console.error("❌ API 응답이 배열이 아닙니다:", response.data);
      return []; // 빈 배열 반환
    }
    
    return response.data;
  } catch (error) {
    console.error('처방전 데이터 조회 실패:', error);
    throw error;
  }
};
