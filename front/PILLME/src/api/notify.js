import apiClient from "./index";

// 알림 조회 API
export const fetchNotifications = async () => {
  try {
    const response = await apiClient.get("/api/v1/notification");
    return response.data.result;
  } catch (error) {
    console.error("[fetchNotifications] 알림 조회 실패:", error);
    return [];
  }
};

// 전체 알림 삭제 API
export const deleteAllNotifications = async () => {
  try {
    await apiClient.delete("/api/v1/notification");
    return true;
  } catch (error) {
    console.error("[deleteAllNotifications] 전체 알림 삭제 실패:", error);
    return false;
  }
};

// 개별 알림 삭제 API (배열로 요청 바디에 포함)
export const deleteNotification = async (notificationIds) => {
  try {
    if (!Array.isArray(notificationIds) || notificationIds.length === 0) {
      console.error("❌ 삭제할 알림 ID 목록이 비어 있습니다.");
      return false;
    }

    const response = await apiClient.delete("/api/v1/notification", {
      data: { // ✅ DELETE 요청에서 Request Body를 포함하려면 `data` 속성을 사용해야 함
        notificationDeleteList: notificationIds
      }
    });

    if (response.data && response.data.isSuccess) {
      console.log("✅ 알림 삭제 성공:", response.data.message);
      return true;
    } else {
      console.error("❌ 알림 삭제 실패:", response.data.message || "알 수 없는 오류");
      return false;
    }
  } catch (error) {
    console.error(`[deleteNotification] 알림 삭제 실패 (IDs: ${notificationIds}):`, error);
    return false;
  }
};



export const acceptAdminRequest = async (senderId) => {
  try {
    if (typeof senderId !== "number") {
      console.error("❌ senderId가 숫자가 아닙니다:", senderId);
      return false;
    }

    const response = await apiClient.post(`/api/v1/dependency/accept`, {
      protectorId: senderId, // ✅ 요청 바디에 `protectId` 포함
    });

    if (response.data && response.data.isSuccess) {
      console.log("✅ 관리자 요청 승인 성공:", response.data.message);
      return true; // ✅ API 요청 성공 시 true 반환
    } else {
      console.error("❌ 관리자 요청 승인 실패:", response.data.message || "알 수 없는 오류");
      return false; // ✅ 실패 시 false 반환
    }
  } catch (error) {
    console.error("❌ acceptAdminRequest API 호출 실패:", error);
    return false; // ✅ API 호출 실패 시 false 반환
  }
};



export const rejectAdminRequest = async (senderId) => {
  try {
    if (typeof senderId !== "number") {
      console.error("❌ senderId가 숫자가 아닙니다:", senderId);
      return false;
    }

    const response = await apiClient.post(`/api/v1/dependency/reject`, {
      protectorId: senderId, // ✅ 요청 바디에 `protectId` 포함
    });

    if (response.data && response.data.isSuccess) {
      console.log("✅ 관리자 요청 거절 성공:", response.data.message);
      return true; // ✅ API 요청 성공 시 true 반환
    } else {
      console.error("❌ 관리자 요청 거절 실패:", response.data.message || "알 수 없는 오류");
      return false; // ✅ 실패 시 false 반환
    }
  } catch (error) {
    console.error("❌ rejectAdminRequest API 호출 실패:", error);
    return false; // ✅ API 호출 실패 시 false 반환
  }
};
