// src/apiService.js

// 메시지 저장 API 모방 (POST 요청을 보낸다고 가정)
// 실제 구현에서는 axios 또는 fetch를 사용하여 백엔드에 POST 요청을 보내면 됩니다.
export function saveMessage(dummyMessage) {
  return new Promise((resolve) => {
    setTimeout(() => {
      // 백엔드가 새 메시지의 id를 생성해서 반환한다고 가정
      const savedMessage = { ...dummyMessage, id: Date.now() };
      resolve(savedMessage);
    }, 500); // 500ms 정도 지연 (네트워크 응답 모방)
  });
}

// 특정 채팅방의 메시지 목록을 불러오는 API 모방 (GET 요청)
export function fetchMessages(chat_id) {
  return new Promise((resolve) => {
    setTimeout(() => {
      // 예시 더미 데이터 (나중에 실제 API 응답으로 교체)
      resolve([
        {
          id: 1,
          member_id: 1, // 채팅방의 상대방 id
          chat_id,
          message: "안녕하세요!",
          isMine: false,
          created_at: "10:30 AM"
        },
        {
          id: 2,
          member_id: 123, // 현재 사용자
          chat_id,
          message: "네 안녕하세요!",
          isMine: true,
          created_at: "10:31 AM"
        },
        {
          id: 3,
          member_id: 1,
          chat_id,
          message: "잘 지내셨나요?",
          isMine: false,
          created_at: "10:32 AM"
        }
      ]);
    }, 500);
  });
}
