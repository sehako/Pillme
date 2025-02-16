import axios from "axios";
import qs from "qs";  // ✅ Query String 처리를 위한 라이브러리 추가

export const searchMedications = async (keyword) => {
  try {
    if (!keyword.trim()) {
      return [];
    }

    const apiUrl = `https://pillme.site/api/v1/search`; // ✅ 절대 경로 사용

    console.log(`🔎 API 요청 변경: ${apiUrl}?keyword=${keyword}`);

    const response = await axios.get(apiUrl, {  // ✅ 백엔드 주소를 절대 경로로 변경
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
