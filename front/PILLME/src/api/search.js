import axios from "axios";
import qs from "qs"; // ✅ Query String 처리를 위한 라이브러리 추가

export const searchMedications = async (keyword) => {
  try {
    if (!keyword.trim()) {
      console.warn("⚠️ 검색어가 비어 있음");
      return [];
    }

    const apiUrl = `https://i12a606.p.ssafy.io/api/v1/search`; // ✅ 백엔드 주소


    const response = await axios.get(apiUrl, {
      params: { keyword },
      paramsSerializer: params => qs.stringify(params, { encode: false }), // ✅ 한글 그대로 유지
      headers: {
        "Accept": "application/json", // ✅ JSON 응답을 기대
        "Content-Type": "application/json"
      },
    });

    // ✅ `result` 필드에서 데이터 추출
    if (!response.data || !response.data.result) {
      return [];
    }

    return response.data.result; // ✅ "result" 필드에서 데이터 반환
  } catch (error) {
    return [];
  }
};
