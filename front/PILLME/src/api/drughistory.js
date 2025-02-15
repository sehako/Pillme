import apiClient from "./index";
import { useUserStore } from "../stores/user";

export const MyDrugHistory = async (startDate, endDate, hospital = "", diseaseName = "") => {
  try {
    const userStore = useUserStore();

    // ✅ 자동으로 memberId 가져오기 (없으면 토큰 재발급 후 반환)
    const memberId = await userStore.getMemberId();

    console.log(`📡 [DEBUG] DrugHistory 데이터 요청: memberId=${memberId}, startDate=${startDate}, endDate=${endDate}`);

    const response = await apiClient.get("/api/v1/history", {
      params: {
        "start-date": startDate,
        "end-date": endDate,
        target: memberId,
        hospital: hospital || undefined,
        diseaseName: diseaseName || undefined,
      },
    });

    console.log("📦 [DEBUG] 받은 DrugHistory 응답 데이터:", response.data);
    return response.data;
  } catch (error) {
    console.error("❌ [DEBUG] DrugHistory 데이터 요청 실패:", error);
    throw error;
  }
};
