import apiClient from "./index";

export const addLocalMember = async ({ name, gender, birthday }) => {
  try {
    // ✅ 요청 데이터 확인
    console.log("📤 비회원 추가 요청 데이터:", { name, gender, birthday });

    const response = await apiClient.post("/api/v1/dependency/local-member", {
      name,
      gender,
      birthday,
    });

    console.log("✅ 비회원 추가 성공:", response.data);
    return response.data;
  } catch (error) {
    console.error("❌ 비회원 추가 실패:", error.response ? error.response.data : error);
    throw error;
  }
};
