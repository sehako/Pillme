import apiClient from "./index";

// ✅ 사용자 채팅방 목록 가져오기
export const getChatRoomList = async () => {
  try {
    const response = await apiClient.get("/api/v1/chat/rooms/list");

    // ✅ API 응답(result 배열)에서 필요한 데이터 추출
    const chatRooms = response.data.result.map((room) => ({
      chatRoomId: room.chatRoomId,                  // 채팅방 ID
      sendUserId: room.sendUserId,                  // 보낸 사람 ID
      receiveUserId: room.receiveUserId,            // 받는 사람 ID
      sendUserName: room.sendUserName,              // 보낸 사람 이름
      receiveUserName: room.receiveUserName,        // 받는 사람 이름
      unreadMessageCount: room.unreadMessageCount   // 안 읽은 메시지 개수
    }));

    return chatRooms; // ✅ 객체 배열 반환
  } catch (error) {
    console.error("채팅방 목록 불러오기 실패:", error);
    throw error;
  }
};

export const enterChatRoom = async (sendUserId, receiveUserId) => {
  try {
    const requestBody = {
      sendUserId,
      receiveUserId,
    };

    const response = await apiClient.post("/api/v1/chat/rooms", requestBody);
    const data = await response.data;
  
    if (data.isSuccess) {
      return data.result;
    } else {
      throw new Error("채팅방 입장에 실패했습니다.");
    }
  } catch (error) {
    console.error("채팅방 입장 중 오류 발생:", error);
    throw error;
  }
};