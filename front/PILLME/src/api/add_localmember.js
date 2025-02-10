import apiClient from "./index";
import { useAuthStore } from "../stores/auth"; // ✅ 인증 스토어 추가

// ✅ 비회원 추가 API 호출 함수
export const addLocalMember = async ({ name, gender, birthday }) => {
  try {
    const authStore = useAuthStore();
    const accessToken = authStore.accessToken; // ✅ Access Token 가져오기

    if (!accessToken) {
      throw new Error("Access Token이 없습니다. 로그인 후 다시 시도해주세요.");
    }

    // ✅ JWT 토큰을 Bearer 형식으로 헤더에 추가
    const headers = {
      Authorization: `Bearer ${accessToken}`, // ✅ JWT 형식으로 설정
      "Content-Type": "application/json",
    };

    // ✅ 요청 전 데이터 디버깅 출력
    console.log("📤 비회원 추가 요청 데이터:", { name, gender, birthday });
    console.log("📤 요청 헤더:", headers);

    const response = await apiClient.post(
      "/api/v1/dependency/local-member",
      { name, gender, birthday },
      { headers }
    );

    console.log("✅ 비회원 추가 성공:", response.data);
    return response.data;
  } catch (error) {
    console.error("❌ 비회원 추가 실패:", error);
    throw error;
  }
};
