// stores/chatStore.js
import { defineStore } from 'pinia';

export const useChatStore = defineStore('chat', {
  state: () => ({
    // 사용자 정보 (실제 DB에서는 백엔드 API를 통해 받아올 데이터 구조와 동일하게 구성)
    users: [
      { id: 1, name: "김싸피", profile: "../assets/profile.png" },
      { id: 2, name: "이싸피", profile: "../assets/profile.png" },
      { id: 3, name: "나싸피", profile: "../assets/profile.png" },
      { id: 123, name: "홍길동", profile: "../assets/profile.png" }
    ],
    currentUser: {
      id: 123,
      name: "홍길동",
      friends: [
        { id: 2, name: "김싸피" },
        { id: 3, name: "이싸피" },
        { id: 4, name: "나싸피" }
      ]
    },
    // 채팅방 정보 (각 채팅방에 상대방 id(userId)를 포함)
    chatRooms: [
      { id: 1, name: "김싸피", userId: 2 },
      { id: 2, name: "이싸피", userId: 3 },
      { id: 3, name: "나싸피", userId: 4 },
      { id: 4, name: "박싸피", userId: 5 } // 현재 사용자의 친구가 아님 (필터링될 예정)
    ],
    // 메시지 데이터 더미 (백엔드 테이블 구조에 맞춘 형식)
    messages: [
      { 
        id: 1,              // 메시지 번호 (PK)
        member_id: 1,       // 메시지를 보낸 사용자 id (예: 김싸피)
        chat_id: 1,         // 채팅방 번호 (예: 채팅방 1)
        message: "안녕하세요!", 
        created_at: "2025-02-06T10:30:00"  // 생성일 (타임스탬프 형식)
      },
      { 
        id: 2, 
        member_id: 123,     // 현재 사용자 (홍길동)
        chat_id: 1, 
        message: "네 안녕하세요!", 
        created_at: "2025-02-06T10:31:00" 
      },
      { 
        id: 3, 
        member_id: 1, 
        chat_id: 1, 
        message: "잘 지내셨나요?", 
        created_at: "2025-02-06T10:32:00" 
      }
    ]
  }),
  actions: {
    // 실제 API 연동 시 사용할 함수 (현재는 더미 데이터를 사용)
    async fetchChatData() {
      // API 호출 후 받은 데이터를 state에 반영하는 로직 작성 예정
    }
  }
});
