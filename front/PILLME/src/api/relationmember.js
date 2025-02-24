import apiClient from "./index";

//     가족 관계 목록 가져오기
export const fetchRelationships = async () => {
  try {
    const response = await apiClient.get("/api/v1/dependency/relationships");
    // console.log("🔹 API 응답 데이터:", response.data.result);

    if (!response.data.result) {
      console.warn("⚠️ 응답 데이터에 result가 없습니다.");
      return [];
    }

    const result = response.data.result;

    //     dependentList에서 필요한 정보 추출
    const dependents = result.dependentList?.map((item) => ({
      dependencyId: item.dependencyId,  //     가족 관계의 PK
      dependentId: item.dependentId,
      dependentName: item.dependentName,
    })) || [];

    //     protectorList에서 필요한 정보 추출
    const protectors = result.protectorList?.map((item) => ({
      dependencyId: item.dependencyId,  //     가족 관계의 PK
      protectorId: item.protectorId,
      protectorName: item.protectorName,
    })) || [];

    //     디버깅 로그 추가
    // console.log("Fetched dependents:", dependents);
    // console.log("Fetched protectors:", protectors);

    return { dependents, protectors }; //     객체 배열 반환
  } catch (error) {
    console.error("가족 관계 목록 불러오기 실패:", error);
    throw error;
  }
};
