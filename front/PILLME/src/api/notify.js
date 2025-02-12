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

// 전체 알림 삭제 API (배열로 요청 바디에 포함)
export const deleteAllNotifications = async (notificationIds) => {
  try {
    if (!Array.isArray(notificationIds) || notificationIds.length === 0) {
      console.error("❌ 삭제할 알림 ID 목록이 비어 있습니다.");
      return false;
    }

    // 전달받은 notificationIds를 숫자로 변환하고 유효한 값만 필터링
    const parsedIds = notificationIds
      .map(id => Number(id))
      .filter(id => !isNaN(id));

    if (parsedIds.length === 0) {
      console.error("❌ 유효한 알림 ID가 없습니다.");
      return false;
    }

    const response = await apiClient.delete("/api/v1/notification", {
      data: { // DELETE 요청 시 body를 포함하려면 data 속성을 사용
        notificationDeleteList: parsedIds
      }
    });

    if (response.data && response.data.isSuccess) {
      console.log("✅ 전체 알림 삭제 성공:", response.data.message);
      return true;
    } else {
      console.error("❌ 전체 알림 삭제 실패:", response.data.message || "알 수 없는 오류");
      return false;
    }
  } catch (error) {
    console.error(`[deleteAllNotifications] 전체 알림 삭제 실패 (IDs: ${notificationIds}):`, error);
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

// 알림 읽음 처리 API
export const markNotificationAsRead = async (notificationId) => {
  try {
    // notificationId를 숫자로 변환 (이미 숫자여도 안전하게 처리)
    const parsedId = Number(notificationId);

    if (isNaN(parsedId)) {
      console.error("❌ 유효하지 않은 notificationId:", notificationId);
      return false;
    }

    // API 호출 시, 숫자를 배열로 감싸서 전달
    const response = await apiClient.put(`/api/v1/notification`, {
      notificationConfirmList: [parsedId],
    });

    if (response.data && response.data.isSuccess) {
      console.log("✅ 알림 읽음 처리 성공:", response.data.message);
      return true;
    } else {
      console.error("❌ 알림 읽음 처리 실패:", response.data.message || "알 수 없는 오류");
      return false;
    }
  } catch (error) {
    console.error("❌ markAsRead API 호출 실패:", error);
    return false;
  }
};
