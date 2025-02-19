import apiClient from './index';
import { useUserStore } from '../stores/user';

export const fetchManagementData = async () => {
  const userStore = useUserStore();

  // ✅ 자동으로 memberId 가져오기 (없으면 토큰 재발급 후 반환)
  const memberId = await userStore.getMemberId();

  if (!memberId) {
    console.error("❌ [DEBUG] memberId를 가져올 수 없음. 요청 중단.");
    throw new Error("멤버 ID 없음. 다시 로그인 필요.");
  }

  // ✅ 유효한 memberId로 API 요청 실행
  try {
    console.log("📡 [DEBUG] Management 데이터 요청: memberId =", memberId);
    const response = await apiClient.get('/api/v1/management', {
      params: { target: memberId }
    });
    console.log("본인 복약체크 정보",response)
    return response.data; // 응답 데이터 반환
  } catch (error) {
    console.error("❌ [DEBUG] Management 데이터 요청 실패:", error);
    throw error;
  }
};


// 복약 상세정보 가져오기 + memberId 반환
export const fetchFormattedManagementInfo = async (userId) => {
  const userStore = useUserStore();
  const memberId = userId != null? userId : await userStore.getMemberId();

  console.log("🔍 [DEBUG] 요청 memberId:", memberId);
  if (!memberId) {
    console.error("❌ [DEBUG] memberId를 가져올 수 없음. 요청 중단.");
    return { memberId: null, prescriptions: [] }; // ✅ memberId도 함께 반환
  }

  try {
    // ✅ 1단계: 모든 처방전 목록 가져오기
    console.log("📡 [DEBUG] 처방전 목록 요청: URL = /api/v1/management/prescription");
    const infoResponse = await apiClient.get(`/api/v1/management/prescription`, {
      params: { target: memberId }
    });

    console.log("📦 [DEBUG] 받은 처방전 응답 데이터:", infoResponse.data);

    if (!infoResponse.data || !Array.isArray(infoResponse.data.result) || infoResponse.data.result.length === 0) {
      console.error("🚨 [DEBUG] 처방전 데이터가 유효하지 않음. 응답 데이터:", infoResponse.data);
      return { memberId, prescriptions: [] };
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

          // ✅ 3단계: 날짜 변환
          let startDate = response.data.result.startDate || "날짜 없음";
          let endDate = response.data.result.endDate || "날짜 없음";

          if (!startDate || !endDate) {
            console.warn(`⚠️ [DEBUG] startDate 또는 endDate가 없음. medicationPeriod에서 추출 시도.`);
            const periodMatch = response.data.result.medicationPeriod?.match(/(\d{4}-\d{2}-\d{2})/g);
            if (periodMatch && periodMatch.length === 2) {
              [startDate, endDate] = periodMatch;
            } else {
              console.error("🚨 [DEBUG] medicationPeriod에서 날짜 추출 실패:", response.data.result.medicationPeriod);
              startDate = "날짜 없음";
              endDate = "날짜 없음";
            }
          }

          // ✅ 5단계: 데이터 정돈 후 리스트에 추가
          return {
            informationId: prescription.informationId,  // ✅ 추가
            diseaseName: response.data.result.diseaseName || "정보 없음",
            medicationPeriod: `${startDate} ~ ${endDate}`,
            medications: response.data.result.medications.length > 0
              ? response.data.result.medications.map(med => med.medicationName).join(", ")
              : "약 정보 없음",
            medicationsId: response.data.result.medications.length > 0
            ? response.data.result.medications.map(med => med.managementId).join(", ")
            : "약 정보 없음",
            hospital: response.data.result.hospital || "병원 정보 없음",
  // ✅ 각 약물별로 morning, lunch, dinner, sleep 값 개별 반환
  morning: response.data.result.medications.length > 0
    ? response.data.result.medications.map(med => med.morning ? "true" : "false").join(", ")
    : "정보 없음",

  lunch: response.data.result.medications.length > 0
    ? response.data.result.medications.map(med => med.lunch ? "true" : "false").join(", ")
    : "정보 없음",

  dinner: response.data.result.medications.length > 0
    ? response.data.result.medications.map(med => med.dinner ? "true" : "false").join(", ")
    : "정보 없음",

  sleep: response.data.result.medications.length > 0
    ? response.data.result.medications.map(med => med.sleep ? "true" : "false").join(", ")
    : "정보 없음",
// ✅ 각 시간대별 Taking (약 복용 여부) 추가
morningTaking: response.data.result.medications.length > 0
? response.data.result.medications.map(med => med.morningTaking ? "true" : "false").join(", ")
: "정보 없음",

lunchTaking: response.data.result.medications.length > 0
? response.data.result.medications.map(med => med.lunchTaking ? "true" : "false").join(", ")
: "정보 없음",

dinnerTaking: response.data.result.medications.length > 0
? response.data.result.medications.map(med => med.dinnerTaking ? "true" : "false").join(", ")
: "정보 없음",

sleepTaking: response.data.result.medications.length > 0
? response.data.result.medications.map(med => med.sleepTaking ? "true" : "false").join(", ")
: "정보 없음",
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
    return { memberId, prescriptions: validPrescriptionList }; // ✅ memberId와 prescriptions 함께 반환
  } catch (error) {
    console.error("❌ [DEBUG] Management 정보 요청 실패:", error);
    return { memberId: null, prescriptions: [] };
  }
};



// ✅ `/api/v1/management/{infoId}?reader={memberId}` API 호출 함수
export const fetchAllManagementDetails = async (informationIdList, memberId) => {
  // ✅ 결과 저장 배열
  const results = [];

  // ✅ 사용 횟수 추적 변수
  let usageCount = 0;

  for (const infoId of informationIdList) {
    if (usageCount >= informationIdList.length) {
      console.warn("⚠️ [DEBUG] memberId 사용 제한 초과! 더 이상 요청을 보낼 수 없습니다.");
      break;
    }

    try {
      console.log(`📡 [DEBUG] 요청: /api/v1/management/${infoId}?reader=${memberId}`);

      // ✅ API 요청 실행
      const response = await apiClient.get(`/api/v1/management/${infoId}`, {
        params: { reader: memberId }
      });

      console.log("📦 [DEBUG] 응답 데이터:", response.data);

      // ✅ 응답 데이터 저장
      results.push(response.data);

      // ✅ 사용 횟수 증가
      usageCount++;

    } catch (error) {
      console.error(`❌ [DEBUG] ${infoId} 정보 가져오기 실패:`, error);
    }
  }

  console.log("📋 [DEBUG] 최종 API 응답 결과:", results);
  return results; // ✅ 모든 API 응답 결과 반환
};



// ✅ API 응답을 개별 medication 단위로 변환하는 함수
export const transformManagementDetails = (apiResponse) => {
  const transformedData = [];

  apiResponse.forEach((prescription, index) => {
    if (prescription.code === 2000 && prescription.isSuccess && prescription.result) {
      const { diseaseName, startDate, endDate, hospital, medications } = prescription.result;

      medications.forEach(med => {
        transformedData.push({
          prescriptionIndex: index, // ✅ 처방전 번호 추가 (0, 1, 2...)
          diseaseName,
          startDate,
          endDate,
          hospital,
          medicationName: med.medicationName,
          managementId: med.managementId,

          // ✅ 복약 예정 여부
          morning: med.morning || false,
          lunch: med.lunch || false,
          dinner: med.dinner || false,
          sleep: med.sleep || false,

          // ✅ 실제 복약 여부 추가
          morningTaking: med.morningTaking || false,
          lunchTaking: med.lunchTaking || false,
          dinnerTaking: med.dinnerTaking || false,
          sleepTaking: med.sleepTaking || false,
        });
      });
    }
  });

  console.log("📋 [DEBUG] 변환된 Medication 리스트:", transformedData);
  return transformedData;
};

