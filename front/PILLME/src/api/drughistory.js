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


export async function fetchHistory() {
  const userStore = useUserStore();

  // ✅ 자동으로 memberId 가져오기 (없으면 토큰 재발급 후 반환)
  const memberId = await userStore.getMemberId();

  if (!memberId) {
    console.error("❌ [DEBUG] memberId를 가져올 수 없음. 요청 중단.");
    throw new Error("멤버 ID 없음. 다시 로그인 필요.");
  }

  try {
    // ✅ apiClient를 사용해 /api/v1/history 엔드포인트 호출 (target 쿼리파라미터로 memberId 전달)
    const response = await apiClient.get('/api/v1/history', {
      params: { target: memberId }
    });
    
    console.log("API 응답 데이터: ", response.data);
    return response.data; // 필요한 데이터를 반환
  } catch (error) {
    console.error("API 요청 실패: ", error);
    throw error;
  }
}

// ✅ 처방전의 기간(startDate, endDate) 조회 API
export async function fetchPrescriptionPeriod() {
  const userStore = useUserStore();
  const memberId = await userStore.getMemberId();
  if (!memberId) {
    console.error("❌ [DEBUG] memberId를 가져올 수 없음.");
    throw new Error("멤버 ID 없음. 다시 로그인 필요.");
  }

  try {
    const response = await apiClient.get('/api/v1/management/prescription', {
      params: { target: memberId }
    });
    return response.data.result;
  } catch (error) {
    console.error("❌ 처방전 기간 API 요청 실패:", error);
    throw error;
  }
}

// ✅ 특정 처방전의 상세 복용 이력 조회 API
export async function fetchPrescriptionDetails(infoId) {
  const userStore = useUserStore();
  const memberId = await userStore.getMemberId();
  if (!memberId) {
    console.error("❌ [DEBUG] memberId를 가져올 수 없음.");
    throw new Error("멤버 ID 없음. 다시 로그인 필요.");
  }

  try {
    const response = await apiClient.get(`/api/v1/history/${infoId}`, {
      params: { target: memberId }
    });
    return response.data.result;
  } catch (error) {
    console.error("❌ 처방전 상세 API 요청 실패:", error);
    throw error;
  }
}
