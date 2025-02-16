import apiClient from "./index"; // ✅ API 기본 인스턴스
import axios from "axios";

/**
 * ✅ 약물 검색 API 호출
 * @param {string} keyword - 검색어
 * @returns {Promise} - 검색 결과 목록
 */
export const searchMedications = async (keyword) => {
  try {
    if (!keyword.trim()) {
      return []; // 검색어가 없으면 빈 배열 반환
    }

    console.log(`🔎 API 요청: https://pillme.site/api/v1/search?keyword=${keyword}`);

    const response = await apiClient.get(`https://pillme.site/api/v1/search`, {
      params: { keyword },
      headers: {
        "Accept": "application/json",
        "Cache-Control": "no-cache",
        "Pragma": "no-cache",
      },
    });

    console.log("✅ API 응답 데이터:", response.data);

    return response.data?.data || [];
  } catch (error) {
    console.error("❌ 약물 검색 실패:", error);
    return []; // 실패 시 빈 배열 반환
  }
};
