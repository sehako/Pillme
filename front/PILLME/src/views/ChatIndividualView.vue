<template>
  <div class="flex flex-col h-full w-full bg-[#9DBB9F26]">
    <!-- 상단 바 (채팅방 제목 & 뒤로 가기) -->
    <div class="flex items-center justify-between p-4 bg-[#9DBB9F80] shadow-md">
      <button @click="goBack" class="text-lg text-green-900 font-bold">←</button>
      <h1 class="text-lg font-bold text-green-900">{{ myId == chatRoomData.sendUserId?chatRoomData.receiveUserName:chatRoomData.sendUserName }}</h1>
      <button class="text-gray-600 text-xl">⚙️</button>
    </div>

    <!-- 채팅 메시지 목록 -->
    <div ref="chatContainer" class="flex-1 w-full overflow-y-auto p-4 space-y-4" @scroll="handleScroll">
      <div v-for="message in messages" :key="message.id"
           :class="['flex', message.senderId === myId ? 'justify-end' : 'justify-start']">
        
        <!-- 상대방 메시지 -->
        <div v-if="message.senderId !== myId" class="w-full flex items-end space-x-2">
          <div class="w-auto max-w-full px-4 py-2 rounded-lg shadow-md bg-[#9DBB9F26] text-gray-700 border">
            <p class="text-sm font-semibold">{{ myId == chatRoomData.sendUserId?chatRoomData.receiveUserName:chatRoomData.sendUserName  }}</p>
            <p>{{ message.message }}</p>
            <small class="text-xs text-gray-500">{{ formatTimestamp(message.timestamp) }}</small>
          </div>
        </div>

        <!-- 내 메시지 -->
        <div v-else class="flex items-end space-x-2 justify-end w-full">
          <div class="w-auto max-w-full px-4 py-2 rounded-lg shadow-md bg-[#B5CCB7] text-gray-900">
            <p>{{ message.message }}</p>
            <small class="text-xs text-gray-500">{{ formatTimestamp(message.timestamp) }}</small>
          </div>
          
        </div>

      </div>
    </div>

    <!-- 새로운 메시지 알림 -->
    <div v-if="showNewMessageAlert" 
         class="absolute bottom-16 left-1/2 transform -translate-x-1/2 p-2 bg-gray-800 text-white text-sm rounded-lg shadow-md cursor-pointer"
         @click="scrollToBottom">
      새로운 메시지가 도착했습니다! ▼
    </div>

    <!-- 입력창 -->
    <div class="p-4 bg-white shadow-md flex items-center space-x-2">
      <input v-model="newMessage" @keyup.enter="sendMessage"
             type="text" placeholder="메시지를 입력하세요..."
             class="flex-1 p-2 border rounded-lg focus:ring focus:ring-[#B5CCB7]">
      <button @click="sendMessage" class="p-2 bg-[#B5CCB7] text-white rounded-lg">📨</button>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted, nextTick, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import SockJS from 'sockjs-client/dist/sockjs.min.js'
import Stomp from "stompjs";
import { decodeToken } from "../utils/jwt"; // ✅ JWT 디코딩 유틸 추가
import { loadMessages,leaveChatRoom,readChatRoom } from '../api/chatRoom';

const router = useRouter();
const props = defineProps({ info: String });
const chatRoomData = JSON.parse(props.info);
const tokenId = decodeToken(localStorage.getItem('accessToken')).memberId;
const myId = tokenId ? tokenId : null;



let stompClient = null;
const messages = ref([]);
const newMessage = ref("");
const showNewMessageAlert = ref(false);
const chatContainer = ref(null);
const isAtBottom = ref(true);
let headers = {Authorization : localStorage.getItem('accessToken')};
// ✅ WebSocket 연결
const connectWebSocket = (chatRoomId) => {
  const socket = new SockJS(`${import.meta.env.VITE_API_URL}/api/v1/ws-chat`);
  stompClient = Stomp.over(socket);
  

  stompClient.connect(headers, () => {
    
    stompClient.subscribe(`/subscribe/chat.${chatRoomId}`, (message) => {
      const msg = JSON.parse(message.body);
      receiveMessage(msg);
    });
  });
};

// ✅ 메시지 전송
const sendMessage = () => {
  if (!newMessage.value.trim()) return;

  const messageData = {
    chatRoomId: chatRoomData.chatRoomId,
    senderId: myId,
    receiverId: myId === chatRoomData.sendUserId ? chatRoomData.receiveUserId : chatRoomData.sendUserId,
    message: newMessage.value,
    timestamp: Date.now()
  };
  // console.log(messageData);
  // WebSocket을 통해 메시지 전송
  stompClient.send(`/publish/chat.${chatRoomData.chatRoomId}`, headers, JSON.stringify(messageData));

  newMessage.value = "";
};

// ✅ 메시지 수신 처리
const receiveMessage = (message) => {
  // console.log(message)
  messages.value.push(message);
  if (isAtBottom.value) {
    nextTick(scrollToBottom);
  } else {
    showNewMessageAlert.value = true;
  }
};

// ✅ 스크롤을 가장 아래로 이동
const scrollToBottom = () => {
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight;
  }
  showNewMessageAlert.value = false;
};


// ✅ WebSocket 연결 해제 및 채팅방 나가기 처리
onUnmounted(async () => {
  if (stompClient) stompClient.disconnect();
  await leaveChatRoom(chatRoomData.chatRoomId);
});

// ✅ 뒤로 가기 버튼 클릭 시 채팅방 나가기 처리
const goBack = async () => {
    await leaveChatRoom(chatRoomData.chatRoomId);
    router.push("/chat");
};

// ✅ 마운트 시 초기화
onMounted(async () => {
  let chatRoomData = JSON.parse(props.info);
  messages.value = await loadMessages(chatRoomData.chatRoomId);
  connectWebSocket(chatRoomData.chatRoomId);
  readChatRoom(chatRoomData.chatRoomId)
  nextTick(scrollToBottom);
});
function formatTimestamp(timestamp) {
        const date = new Date(timestamp);
        const now = new Date();
    
        const hours = date.getHours().toString().padStart(2, "0"); // 2자리 숫자 유지
        const minutes = date.getMinutes().toString().padStart(2, "0");
        const month = (date.getMonth() + 1).toString().padStart(2, "0"); // 월 (0부터 시작하므로 +1)
        const day = date.getDate().toString().padStart(2, "0");
    
        // 오늘 날짜와 비교하여 'Today' 표시
        const isToday =
            date.getFullYear() === now.getFullYear() &&
            date.getMonth() === now.getMonth() &&
            date.getDate() === now.getDate();
    
        return isToday
            ? `Today ${hours}:${minutes}` // 오늘이면 'Today HH:MM'
            : `${month}-${day} ${hours}:${minutes}`; // 아니면 'MM-DD HH:MM'
    }

</script>
