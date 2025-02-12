import apiClient from "./index";

export const addMemberAlarm = async ({ phone }) => {
  try {
    // ✅ 요청 데이터 확인

    console.log("📤 회원 추가 요청 데이터:", { phone });
    const response = await apiClient.post("/api/v1/dependency", {
      phone
    });

    console.log("✅ 회원 추가 성공:", response.data);
    return response.data;
  } catch (error) {
    console.error("❌ 회원 추가 실패:", error.response ? error.response.data : error);
    throw error;
  }
};
