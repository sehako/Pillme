import apiClient from "./index";  // 공통 API 클라이언트

/** OCR 분석 데이터를 DB에 저장하는 API */
export const saveOcrData = async ({ hospital, diseaseName, reader, startDate, endDate, medications }) => {
  try {
    // console.log("OCR 분석 데이터 저장 요청:", { hospital, diseaseName, reader, startDate, endDate, medications });

    const response = await apiClient.post("http://172.26.240.1:8080/api/v1/management", {
      hospital,
      diseaseName,
      reader,
      startDate,
      endDate,
      medications
    });

    // console.log("OCR 분석 데이터 저장 성공:", response.data);
    return response.data;
  } catch (error) {
    console.error("OCR 분석 데이터 저장 실패:", error.response?.data || error.message || error);
    throw new Error(error.response?.data?.message || "OCR 데이터 저장 중 오류 발생");
  }
};
