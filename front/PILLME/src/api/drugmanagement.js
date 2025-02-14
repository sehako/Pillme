import apiClient from './index';
import { useUserStore } from '../stores/user';

export const fetchManagementData = async () => {
  const userStore = useUserStore();

  try {
    // ✅ 멤버 ID 가져오는 요청이 무조건 선행되도록 `await` 처리
    console.log("🔍 [DEBUG] 요청 memberId 가져오는 중...");
    const memberId = await userStore.getMemberId();

    if (!memberId) {
      console.error("❌ [DEBUG] memberId를 가져올 수 없음. 요청 중단.");
      throw new Error("멤버 ID 없음. 다시 로그인 필요.");
    }
    console.log("✅ [DEBUG] memberId 확인:", memberId);

    // ✅ 유효한 memberId로 API 요청 실행
    console.log("📡 [DEBUG] Management 데이터 요청: memberId =", memberId);
    const response = await apiClient.get('/api/v1/management', {
      params: { target: memberId }
    });

    console.log("📦 [DEBUG] 받은 API 응답:", response.data);
    return response.data; // 응답 데이터 반환
  } catch (error) {
    console.error("❌ [DEBUG] Management 데이터 요청 실패:", error);
    throw error;
  }
};



// 복약 상세정보
export const fetchFormattedManagementInfo = async () => {
  const userStore = useUserStore();

  try {
    // ✅ 멤버 ID 가져오는 요청이 무조건 선행되도록 `await` 처리
    console.log("🔍 [DEBUG] 요청 memberId 가져오는 중...");
    const memberId = await userStore.getMemberId();

    if (!memberId) {
      console.error("❌ [DEBUG] memberId를 가져올 수 없음. 요청 중단.");
      return [];
    }
    console.log("✅ [DEBUG] memberId 확인:", memberId);

    // ✅ 1단계: 처방전 목록 가져오기
    console.log("📡 [DEBUG] 처방전 목록 요청: URL = /api/v1/management/prescription");
    const infoResponse = await apiClient.get(`/api/v1/management/prescription`, {
      params: { target: memberId }
    });

    if (!infoResponse.data || !Array.isArray(infoResponse.data.result) || infoResponse.data.result.length === 0) {
      console.error("🚨 [DEBUG] 처방전 데이터가 유효하지 않음. 응답 데이터:", infoResponse.data);
      return [];
    }

    // ✅ 2단계: 모든 처방전의 상세 정보 가져오기 (병렬 요청)
    const prescriptionList = await Promise.all(
      infoResponse.data.result.map(async (prescription, idx) => {
        try {
          if (!prescription.informationId) {
            console.error(`🚨 [DEBUG] (${idx + 1}) 정보 ID가 없음, 처방전 데이터 확인 필요`, prescription);
            return null;
          }

          console.log(`📡 [DEBUG] (${idx + 1}) Management 정보 요청: /api/v1/management/${prescription.informationId}?reader=${memberId}`);
          
          const response = await apiClient.get(`/api/v1/management/${prescription.informationId}`, {
            params: { reader: memberId }
          });

          console.log(`📦 [DEBUG] (${idx + 1}) 받은 API 응답:`, response.data);

          if (!response.data || !response.data.result || !response.data.result.medications) {
            console.error(`🚨 [DEBUG] (${idx + 1}) 잘못된 응답 데이터:`, response.data);
            return null;
          }

          // ✅ 3단계: 날짜 변환 함수
          const formatDate = (dateValue) => {
            if (Array.isArray(dateValue) && dateValue.length === 3) {
              const [year, month, day] = dateValue;
              return `${year}-${String(month).padStart(2, "0")}-${String(day).padStart(2, "0")}`;
            }
            if (typeof dateValue === "string") {
              return dateValue; // 이미 문자열이면 그대로 사용
            }
            return null;
          };

          // ✅ 4단계: `startDate`, `endDate`를 우선 사용, 없으면 `medicationPeriod`에서 추출
          let startDate = formatDate(response.data.result.startDate);
          let endDate = formatDate(response.data.result.endDate);

          if (!startDate || !endDate) {
            console.warn(`⚠️ [DEBUG] startDate 또는 endDate가 없음. medicationPeriod에서 추출 시도.`);
            const periodMatch = response.data.result.medicationPeriod?.match(/(\d{4}-\d{2}-\d{2})/g);
            if (periodMatch && periodMatch.length >= 1) {
              startDate = periodMatch[0]; // 첫 번째 날짜
              endDate = periodMatch.length > 1 ? periodMatch[1] : "날짜 없음"; // 두 번째 날짜 없으면 기본값
            } else {
              console.error("🚨 [DEBUG] medicationPeriod에서 날짜 추출 실패:", response.data.result.medicationPeriod);
              startDate = "날짜 없음";
              endDate = "날짜 없음";
            }
          }

          // ✅ 5단계: 데이터 정돈 후 리스트에 추가
          return {
            diseaseName: response.data.result.diseaseName || "정보 없음",
            medicationPeriod: `${startDate} ~ ${endDate}`, // 변환된 날짜 사용
            medications: response.data.result.medications.length > 0
              ? response.data.result.medications.map(med => med.medicationName).join(", ")
              : "약 정보 없음",
            hospital: response.data.result.hospital || "병원 정보 없음",
          };
        } catch (error) {
          console.error(`❌ [DEBUG] (${idx + 1}) Management 정보 요청 실패 (infoId: ${prescription.informationId}):`, error);
          return null;
        }
      })
    );

    // ✅ 유효한 데이터만 필터링하여 반환
    const validPrescriptionList = prescriptionList.filter(prescription => prescription !== null);

    console.log("📋 [DEBUG] 최종 정돈된 처방전 리스트:", validPrescriptionList);
    return validPrescriptionList;
  } catch (error) {
    console.error("❌ [DEBUG] Management 정보 요청 실패:", error);
    return [];
  }
};

