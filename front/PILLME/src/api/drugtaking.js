import apiClient from "./index";

export const prescriptionAllCheck = async (medications, infoid) => {
  try {
    // 🚨 `medicationsall` 같은 중첩된 키가 추가되지 않도록 올바르게 데이터 구조 확인
    console.log("📌 전송할 medications 데이터 구조:", Array.isArray(medications), medications);
    console.log(infoid)
    const response = await apiClient.put(`/api/v1/management/${infoid}`, { medications });

    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || error.message);
  }
};
