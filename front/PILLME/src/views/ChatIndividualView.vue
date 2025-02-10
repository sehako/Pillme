<template>
  <div class="flex flex-col h-full w-full bg-[#9DBB9F26]">
    <!-- 상단 바 (채팅방 제목 & 뒤로 가기) -->
    <div class="flex items-center justify-between p-4 bg-[#9DBB9F80] shadow-md">
      <button @click="goBack" class="text-lg text-green-900 font-bold">←</button>
      <h1 class="text-lg font-bold text-green-900">{{ chatRoomName }}</h1>
      <button class="text-gray-600 text-xl">⚙️</button>
    </div>

    <!-- 채팅 메시지 목록 (스크롤 가능) -->
    <div ref="chatContainer" class="flex-1 w-full overflow-y-auto p-4 space-y-4" @scroll="handleScroll">
      <div class="w-full" v-for="message in sortedMessages" :key="message.id"
           :class="['flex', message.isMine ? 'justify-end' : 'justify-start']">
        <!-- 상대방 메시지 -->
        <div v-if="!message.isMine" class="w-full flex items-end space-x-2">
  <img :src="getProfileImage(message.member_id)" class="w-8 h-8 rounded-full bg-gray-300" alt="프로필">
  <div class="w-auto max-w-full px-4 py-2 rounded-lg shadow-md bg-[#9DBB9F26] text-gray-700 border">
    <p class="text-sm font-semibold">{{ getSenderName(message.member_id) }}</p>
    <p>{{ message.message }}</p>
    <small class="text-xs text-gray-500">{{ message.created_at }}</small>
  </div>
</div>


        <!-- 내 메시지 -->
        <div v-else class="flex items-end space-x-2 justify-end w-full">
  <div class="w-auto max-w-full px-4 py-2 rounded-lg shadow-md bg-[#B5CCB7] text-gray-900">
    <p>{{ message.message }}</p>
    <small class="text-xs text-gray-500">{{ message.created_at }}</small>
  </div>
  <img :src="getProfileImage(message.member_id)" class="w-8 h-8 rounded-full bg-gray-300" alt="프로필">
</div>

      </div>
    </div>

    <!-- 새로운 메시지 알림 -->
    <div v-if="showNewMessageAlert" class="absolute bottom-16 left-1/2 transform -translate-x-1/2 p-2 bg-gray-800 text-white text-sm rounded-lg shadow-md cursor-pointer"
         @click="scrollToBottom">
      새로운 메시지가 도착했습니다! ▼
    </div>

    <!-- 입력창 -->
    <div class="p-4 bg-white shadow-md flex items-center space-x-2">
      <input v-model="newMessage" 
             @keyup.enter="sendMessage"
             type="text" placeholder="메시지를 입력하세요..."
             class="flex-1 p-2 border rounded-lg focus:ring focus:ring-[#B5CCB7]">
      <button @click="sendMessage" class="p-2 bg-[#B5CCB7] text-white rounded-lg">📨</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, defineProps, onMounted, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import { saveMessage, fetchMessages } from '../apiService'; // dummy API 호출 함수 임포트

const router = useRouter();

// props로 채팅방 ID 받기 (채팅방 번호와 연동)
const props = defineProps({
  id: String
});

// 채팅창 스크롤 관련 변수
const chatContainer = ref(null);
const isAtBottom = ref(true);
const showNewMessageAlert = ref(false);

// 현재 사용자 (더미 데이터)
const currentUser = ref({ id: 123, name: "홍길동", profile: "../assets/profile.png" });

// 사용자 목록 (더미 데이터; 실제 백엔드 회원정보 구조와 동일)
const users = ref([
  { id: 1, name: "김싸피", profile: "../assets/profile.png" },
  { id: 2, name: "이싸피", profile: "../assets/profile.png" },
  { id: 3, name: "나싸피", profile: "../assets/profile.png" },
  { id: 123, name: "홍길동", profile: "../assets/profile.png" }
]);

// 채팅방 정보 (더미 데이터)
// 각 채팅방은 DB의 chat_room 테이블에 대응하며, userId는 상대방 회원 번호
const chatRooms = ref([
  { id: "1", name: "김싸피", userId: 1 },
  { id: "2", name: "이싸피", userId: 2 },
  { id: "3", name: "나싸피", userId: 3 }
]);

// 채팅방 이름: 선택된 채팅방(props.id)에 따라 결정
const chatRoomName = computed(() => {
  const room = chatRooms.value.find(room => room.id === props.id);
  return room ? room.name : "알 수 없는 채팅방";
});

// 현재 채팅방의 상대방 id (chat_room.userId)
const chatPartnerId = computed(() => {
  const room = chatRooms.value.find(room => room.id === props.id);
  return room ? room.userId : null;
});

// 메시지 데이터 (더미 데이터)
// DB의 메시지 테이블 구조에 맞추어: id, member_id, chat_id, message, created_at, isMine 추가
const messages = ref([]);

// localStorage에서 메시지 불러오기 (없으면 dummy API 호출)
const loadMessages = async () => {
  const stored = localStorage.getItem(`chatMessages_${props.id}`);
  if (stored) {
    messages.value = JSON.parse(stored);
  } else {
    messages.value = await fetchMessages(props.id);
    localStorage.setItem(`chatMessages_${props.id}`, JSON.stringify(messages.value));
  }
};

loadMessages();

// 메시지 데이터 정렬 (실제 데이터는 created_at이 TIMESTAMP 형식)
const sortedMessages = computed(() => {
  return messages.value.sort((a, b) => new Date(a.created_at) - new Date(b.created_at));
});

// 새 메시지 입력
const newMessage = ref("");

// localStorage 업데이트 함수
const updateLocalStorage = () => {
  localStorage.setItem(`chatMessages_${props.id}`, JSON.stringify(messages.value));
};

// 메시지 전송 기능: dummy API(saveMessage) 호출 모방
const sendMessage = async () => {
  if (newMessage.value.trim() === "") return;

  const messageToSend = {
    member_id: currentUser.value.id,
    chat_id: props.id,
    message: newMessage.value,
    isMine: true,
    created_at: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  };

  // dummy API 호출 (나중에 실제 API 호출로 변경)
  const savedMessage = await saveMessage(messageToSend);
  messages.value.push(savedMessage);
  updateLocalStorage();

  newMessage.value = "";
  nextTick(() => scrollToBottom());
};

// 스크롤을 가장 아래로 이동
const scrollToBottom = () => {
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight;
  }
  showNewMessageAlert.value = false;
};

// 스크롤 이벤트 감지 (새로운 메시지 알림 처리)
const handleScroll = () => {
  if (!chatContainer.value) return;

  const { scrollTop, scrollHeight, clientHeight } = chatContainer.value;
  const isUserAtBottom = scrollTop + clientHeight >= scrollHeight - 20;
  if (isUserAtBottom) {
    isAtBottom.value = true;
    showNewMessageAlert.value = false;
  } else {
    isAtBottom.value = false;
  }
};

// 새로운 메시지 수신 처리 (웹소켓 등 실제 구현 시 변경)
const receiveMessage = (message) => {
  messages.value.push(message);
  updateLocalStorage();
  if (isAtBottom.value) {
    nextTick(() => scrollToBottom());
  } else {
    showNewMessageAlert.value = true;
  }
};

// 뒤로 가기 기능
const goBack = () => {
  router.push("/chat");
};

// 컴포넌트 마운트 시 스크롤 하단 이동
onMounted(() => {
  nextTick(() => scrollToBottom());
});

// 프로필 이미지 반환 (회원 정보 참조)
const getProfileImage = (memberId) => {
  const user = users.value.find(u => u.id === memberId);
  return user ? user.profile : "../assets/defaultProfile.png";
};

// 회원 이름 반환
const getSenderName = (memberId) => {
  const user = users.value.find(u => u.id === memberId);
  return user ? user.name : "알 수 없음";
};
</script>

<style scoped>
.overflow-y-auto {
  scrollbar-width: thin;
  scrollbar-color: #9dbb9f #e5e7eb;
}
</style>
