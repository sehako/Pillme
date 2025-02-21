import apiClient from "./index";

// ✅ 피부양자 목록 가져오기 (dependentId 포함)
export const fetchDependents = async () => {
  try {
    const response = await apiClient.get("/api/v1/dependency/dependents");
    console.log("🔹 API 응답 데이터:", response.data.result);
    // ✅ result 배열에서 dependentId, dependentName 추출
    const dependents = response.data.result.map((item) => ({
      dependencyId: item.dependentInfo.dependencyId,  // ✅ 가족 관계의 PK
      dependentId: item.dependentInfo.dependentId,
      dependentName: item.dependentInfo.dependentName,
    }));

    // ✅ 디버깅 로그 추가
    console.log("Fetched dependents:", dependents);

    return dependents; // ✅ 객체 배열 반환
  } catch (error) {
    console.error("피부양자 목록 불러오기 실패:", error);
    throw error;
  }
};
