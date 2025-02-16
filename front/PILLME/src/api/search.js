import axios from "axios";
import qs from "qs";  // ✅ Query String 처리를 위한 라이브러리 추가

export const searchMedications = async (keyword) => {
  try {
    if (!keyword.trim()) {
      return [];
    }

    console.log(`🔎 API 요청 변경: /api/v1/search?keyword=${keyword}`);

    const response = await axios.get(`/api/v1/search`, {
      params: { keyword },
      paramsSerializer: params => qs.stringify(params, { encode: false }) // ✅ 한글 그대로 유지
    });

    console.log("✅ API 응답 데이터:", response.data);

    return response.data?.data || [];
  } catch (error) {
    console.error("❌ 약물 검색 실패:", error);
    return [];
  }
};
