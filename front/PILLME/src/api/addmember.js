import apiClient from "./index";

export const addMemberAlarm = async ({ phone }) => {
  try {
    // 요청 데이터 확인
    const response = await apiClient.post("/api/v1/dependency", {
      phone
    });
    return response.data;
  } catch (error) {
    console.error("회원 추가 실패:", error.response ? error.response.data : error);
    throw error;
  }
};
