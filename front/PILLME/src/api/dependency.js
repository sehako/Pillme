import apiClient from "./index";

// 🔥 관계 삭제 요청 API (dependencyId 사용) -> 삭제 요청만 보냄
export const requestDependencyDelete = async (dependencyId) => {
    try {
      if (!dependencyId) {
        console.error("❌ dependencyId가 없습니다!", dependencyId);
        return false;
      }
  
      console.log(`🔗 관계 삭제 요청: /api/v1/dependency/delete/${dependencyId}`);
  
      // ✅ dependencyId를 API 요청에 사용
      const response = await apiClient.post(`/api/v1/dependency/delete/${dependencyId}`);

      if (response.data && response.data.isSuccess) {
        console.log("✅ 관계 삭제 요청 성공:", response.data.message);
        return true;
      } else {
        console.error("❌ 관계 삭제 요청 실패:", response.data.message || "알 수 없는 오류");
        return false;
      }
    } catch (error) {
      console.error(`❌ requestDependencyDelete API 호출 실패 (dependencyId: ${dependencyId}):`, error);
      return false;
    }
};

// ✅ 관계 삭제 요청 수락 API (DELETE 요청 사용)
export const acceptDependencyDelete = async (senderId) => {
  try {
    if (typeof senderId !== "number") {
      console.error("❌ senderId가 숫자가 아닙니다:", senderId);
      return false;
    }

    console.log(`✅ 관계 삭제 요청 수락 요청: senderId=${senderId}`);

    // DELETE 요청에서 data를 JSON body로 포함해야 함
    const response = await apiClient.delete(`/api/v1/dependency/delete/accept`, {
      headers: {
        "Content-Type": "application/json",
      },
      data: { senderId }, // ✅ JSON body로 senderId 포함
    });

    console.log("🔍 서버 응답:", response);

    if (response.data && response.data.isSuccess) {
      console.log("✅ 관계 삭제 요청 수락 성공:", response.data.message);
      return true;
    } else {
      console.error("❌ 관계 삭제 요청 수락 실패:", response.data.message || "알 수 없는 오류");
      return false;
    }
  } catch (error) {
    console.error("❌ acceptDependencyDelete API 호출 실패:", error);
    return false;
  }
};

// ✅ 관계 삭제 요청 거절 API (POST 요청 사용)
export const rejectDependencyDelete = async (senderId) => {
  try {
    if (typeof senderId !== "number") {
      console.error("❌ senderId가 숫자가 아닙니다:", senderId);
      return false;
    }

    console.log(`❌ 관계 삭제 요청 거절 요청: senderId=${senderId}`);

    const response = await apiClient.post(`/api/v1/dependency/delete/reject`, {
      senderId, // ✅ JSON body로 senderId 포함
    });

    console.log("🔍 서버 응답:", response);

    if (response.data && response.data.isSuccess) {
      console.log("✅ 관계 삭제 요청 거절 성공:", response.data.message);
      return true;
    } else {
      console.error("❌ 관계 삭제 요청 거절 실패:", response.data.message || "알 수 없는 오류");
      return false;
    }
  } catch (error) {
    console.error("❌ rejectDependencyDelete API 호출 실패:", error);
    return false;
  }
};