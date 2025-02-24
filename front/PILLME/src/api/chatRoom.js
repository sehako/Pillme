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
      lastMessage: room.lastMessage || "대화 없음",
      lastMessageTime: room.timestamp || 0,
      unreadMessageCount: room.unreadMessageCount || 0,
    }));
    chatRooms.sort((a, b) => b.lastMessageTime - a.lastMessageTime);
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

export const loadMessages = async (chatRoomId) => {
  try {
    const response = await apiClient.get(`/api/v1/chat/${chatRoomId}`);
    const data = await response.data;
    if (data.isSuccess) {
      return data.result;
    }
  } catch (error) {
    console.error("채팅 내역 불러오기 오류:", error);
  }
};

export const leaveChatRoom = async (chatRoomId) => {
  try {
    const response = await apiClient.post(`/api/v1/chat/rooms/leave/${chatRoomId}`);
    const data = response.data;

    if (data.isSuccess) {
      // console.log("채팅방에서 나갔습니다.");
    } else {
      // console.error("채팅방 나가기 실패");
    }
  } catch (error) {
    console.error("채팅방 나가기 중 오류 발생:", error);
  }
};

export const readChatRoom = async (chatRoomId) => {
  try {
    const response = await apiClient.post(`/api/v1/chat/read/${chatRoomId}`);
    const data = response.data;

    if (data.isSuccess) {
      // console.log("채팅 내역을 읽었습니다.");
    } else {
      // console.error("채팅 내역 읽기 실패");
    }
  } catch (error) {
    console.error("채팅방 내역 읽기 중 오류 발생:", error);
  }
};