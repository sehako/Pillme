import apiClient from "./index";

// ✅ 비회원 추가 API 호출 함수
export const addLocalMember = async ({ name, gender, birthday }) => {
  try {
    // ✅ 요청 전 데이터 디버깅 출력
    console.log("📤 비회원 추가 요청 데이터:", { name, gender, birthday });

    const response = await apiClient.post("/api/v1/dependency/local-member", {
      name,
      gender,
      birthday,
      phone,
    });

    console.log("✅ 비회원 추가 성공:", response.data);
    return response.data;
  } catch (error) {
    console.error("❌ 비회원 추가 실패:", error);
    throw error;
  }
};
