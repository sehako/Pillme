import apiClient from "./index";

/**
 * 사용자 이름을 API에서 가져오는 함수
 * @returns {Promise<string>} 사용자 이름
 */
export const fetchUsername = async () => {
  try {
    // ✅ 로컬스토리지에서 액세스 토큰 가져오기
    const token = localStorage.getItem("accessToken");

    if (!token) {
      throw new Error("엑세스 토큰이 존재하지 않습니다.");
    }
    // console.log("Access token", token);
    // ✅ API 요청 시 `Authorization: Bearer {token}` 추가
    const response = await apiClient.get('/api/v1/members/me', {

    });

    return response.data.result.name;
  } catch (error) {
    console.error("사용자 이름 불러오기 실패:", error);
    throw error;
  }
};
