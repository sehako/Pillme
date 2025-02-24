import apiClient from "./index";

// 🔥 관계 삭제 요청 API (dependencyId 사용) -> 삭제 요청만 보냄
export const requestDependencyDelete = async (dependencyId) => {
    try {
      if (!dependencyId) {
        console.error("❌ dependencyId가 없습니다!", dependencyId);
        return false;
      }
  
      // console.log(`관계 삭제 요청: /api/v1/dependency/delete/${dependencyId}`);
  
      // dependencyId를 API 요청에 사용
      const response = await apiClient.post(`/api/v1/dependency/delete/${dependencyId}`);

      if (response.data && response.data.isSuccess) {
        // console.log("관계 삭제 요청 성공:", response.data.message);
        return true;
      } else {
        console.error("관계 삭제 요청 실패:", response.data.message || "알 수 없는 오류");
        return false;
      }
    } catch (error) {
      console.error(`requestDependencyDelete API 호출 실패 (dependencyId: ${dependencyId}):`, error);
      return false;
    }
};

// 🔥 관계 삭제 요청 수락 API (삭제 요청을 받은 상대방이 수락할 경우)
export const acceptDependencyDelete = async (senderId, dependencyId, notificationId) => {
  try {
    // console.log(`관계 삭제 요청 수락 요청: senderId=${senderId} / dependencyId=${dependencyId} / notificationId=${notificationId}`);

    const response = await apiClient.post(`/api/v1/dependency/delete/accept`, {
      headers: {
        'Content-Type': 'application/json'
      },
      body: { senderId, dependencyId, notificationId } // JSON 형식으로 올바르게 보냄
    });

    console.log("🔍 서버 응답:", response);

    if (response.data && response.data.isSuccess) {
      // console.log("관계 삭제 요청 수락 성공:", response.data.message);
      return true;
    } else {
      console.error("관계 삭제 요청 수락 실패:", response.data.message || "알 수 없는 오류");
      return false;
    }
  } catch (error) {
    console.error("acceptDependencyDelete API 호출 실패:", error);
    return false;
  }
};


// 🔥 관계 삭제 요청 거절 API (삭제 요청을 받은 상대방이 거절할 경우)
export const rejectDependencyDelete = async (senderId, dependencyId, notificationId) => {
  try {
    const response = await apiClient.post(`/api/v1/dependency/delete/reject`, {
      senderId, dependencyId, notificationId
    });

    if (response.data && response.data.isSuccess) {
      // console.log("관계 삭제 요청 거절 성공:", response.data.message);
      return true;
    } else {
      console.error("관계 삭제 요청 거절 실패:", response.data.message || "알 수 없는 오류");
      return false;
    }
  } catch (error) {
    console.error("rejectDependencyDelete API 호출 실패:", error);
    return false;
  }
};
