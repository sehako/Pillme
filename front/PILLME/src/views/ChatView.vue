<template>
  <div class="flex flex-col w-full !min-h-full !items-start !justify-start">
    <div class="flex flex-col w-full">
      <!-- 상단 안내 메시지 -->
      <div
        class="w-full flex items-center justify-between bg-[#9DBB9F80] p-3 rounded-b-sm shadow-md"
      >
        <h2 class="text-lg font-bold text-green-900">오늘 복약 하셔야 하는 분들입니다.</h2>
        <button class="text-green-700 text-xl">⚙️</button>
      </div>

      <!-- ✅ 가로 스크롤 가능한 사용자 목록 -->
      <div class="overflow-x-auto w-full whitespace-nowrap px-1.5 py-2">
        <div class="w-48 sm:w-56 md:w-64 lg:w-72 flex flex-nowrap space-x-4">
          <GreenCard
            v-for="user in chatStore.users"
            :key="user.id"
            class="flex flex-col min-w-[200px] p-1 rounded-lg bg-9DBB9F80"
          >
            <div class="flex flex-col items-center justify-between">
              <p class="font-bold text-lg text-nowrap">{{ user.name }}</p>
              <div class="flex flex-row items-center space-x-2">
                <div class="flex flex-col items-center justify-center">
                  <div class="bg-[#9DBB9F80] rounded-full p-1">아침</div>
                  <img src="../assets/CheckCircle.svg" alt="약물복용체크" />
                </div>
                <div class="flex flex-col items-center justify-center">
                  <div class="bg-[#FFE2E2] rounded-full p-1">점심</div>
                  <img src="../assets/CheckCircle.svg" alt="약물복용체크" />
                </div>
                <div class="flex flex-col items-center justify-center">
                  <div class="bg-[#EF7C8E] rounded-full p-1">저녁</div>
                  <img src="../assets/CheckCircle.svg" alt="약물복용체크" />
                </div>
                <div class="flex flex-col items-center justify-center">
                  <div class="bg-[#888585] rounded-full p-1">자기 전</div>
                  <img src="../assets/CheckCircle.svg" alt="약물복용체크" />
                </div>
              </div>
            </div>
          </GreenCard>
        </div>
      </div>
      <hr class="border-t border-[#9DBB9F80]" />

      <!-- ✅ 검색 바 -->
      <div class="min-w-fit relative m-2">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="검색"
          class="w-full p-2 pl-8 border rounded-lg focus:ring focus:ring-[#B5CCB7]"
        />
        <span class="absolute left-2 top-2 text-gray-500">🔍</span>
      </div>

      <!-- ✅ 채팅 목록 -->
      <div>
        <div v-if="chatRooms.length == 0" class="text-gray-500">
          친구 추가된 사용자와의 채팅방이 없습니다.
        </div>

        <div v-else class="space-y-2">
          <div
            v-for="room in chatRooms"
            :key="room.chatRoomId"
            class="flex items-center justify-between p-4 bg-[#9DBB9F26] rounded-lg shadow-md cursor-pointer hover:bg-[#B5CCB7] transition"
            @click="enterChat(room.opponentId)"
          >
            <!-- 왼쪽: 상대방 프로필 및 이름 -->
            <div class="flex items-center space-x-2">
              <div
                class="w-10 h-10 bg-[#9DBB9F80] text-white rounded-full flex items-center justify-center text-lg font-semibold"
              >
                {{ room.opponentName.charAt(0) }}
              </div>
              <div>
                <div class="font-semibold">{{ room.opponentName }}</div>
                <div class="text-sm text-gray-600 truncate w-32">{{ room.lastMessage }}</div>
              </div>
            </div>

            <!-- 오른쪽: 안 읽은 메시지 수 표시 -->
            <div class="flex items-center space-x-2">
              <button class="text-gray-600">🔍</button>
              <span
                v-if="room.unreadMessageCount > 0"
                class="bg-red-500 text-white text-xs font-semibold px-2 py-1 rounded-full"
              >
                {{ room.unreadMessageCount }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import GreenCard from '../layout/GreenCard.vue';
import { useChatStore } from '../stores/chatStore';
import { getChatRoomList, enterChatRoom } from '../api/chatRoom';
import { decodeToken } from "../utils/jwt"; // ✅ JWT 디코딩 유틸 추가
import SockJS from 'sockjs-client/dist/sockjs.min.js'
import Stomp from "stompjs";
const chatStore = useChatStore();
const router = useRouter();

// ✅ 로컬스토리지에서 현재 로그인한 사용자 ID 가져오기
const tokenId = decodeToken(localStorage.getItem('accessToken')).memberId;
const myId = tokenId ? tokenId : null;

const searchQuery = ref('');
const chatRooms = ref([]);


let stompClient = null;
let headers = {Authorization : localStorage.getItem('accessToken')};
// ✅ WebSocket 연결 (새 메시지 업데이트)
const connectWebSocket = () => {
  const socket = new SockJS(`${import.meta.env.VITE_API_URL}/ws-chat`);
  stompClient = Stomp.over(socket);

  stompClient.connect(headers, () => {
    console.log("✅ WebSocket 연결됨");

    // ✅ 실시간 채팅방 목록 업데이트 구독
    stompClient.subscribe(`/subscribe/chat/list/${myId}`, (message) => {
      const updatedRoom = JSON.parse(message.body);
      console.log(message)
      // ✅ 기존 채팅방 목록에서 업데이트된 채팅방 찾기
      const index = chatRooms.value.findIndex(room => room.chatRoomId === updatedRoom.chatRoomId);
      if (index !== -1) {
        // ✅ 기존 채팅방 업데이트
        chatRooms.value[index].lastMessage = updatedRoom.lastMessage;
        chatRooms.value[index].lastMessageTime = updatedRoom.lastMessageTime;
        chatRooms.value[index].unreadMessageCount = updatedRoom.unreadCount;
      } else {
        // ✅ 새 채팅방 추가
        chatRooms.value.push(updatedRoom);
      }

      // ✅ 최신 메시지 기준으로 정렬 (최근 메시지가 위로 오도록)
      chatRooms.value.sort((a, b) => b.lastMessageTime - a.lastMessageTime);
    });
  });
};



// ✅ 채팅방 데이터 불러오기
const loadChatRooms = async () => {
  try {
    const rooms = await getChatRoomList(); // ✅ API 호출 후 rooms에 저장
    chatRooms.value = rooms.map((room) => {
      // 내 ID와 일치하지 않는 ID를 상대방(opponent)으로 설정
      const isSender = room.sendUserId === myId;
      console.log(room)
      return {
        chatRoomId: room.chatRoomId,
        opponentId: isSender ? room.receiveUserId : room.sendUserId,
        opponentName: isSender ? room.receiveUserName : room.sendUserName,
        unreadMessageCount: room.unreadMessageCount,
        lastMessage: room.lastMessage || "대화 없음",
        lastMessageTime: room.lastMessageTime || 0,
        unreadMessageCount: room.unreadMessageCount || 0,
      };
    });
    chatRooms.value.sort((a, b) => b.lastMessageTime - a.lastMessageTime);
  } catch (error) {
    console.error('채팅방 데이터를 불러오는 데 실패했습니다.', error);
  }
};

onMounted(() => {
  loadChatRooms();
  connectWebSocket();
});

// ✅ 검색어 필터링 (상대방 이름 기준)
const filteredChatRooms = computed(() => {
  return chatRooms.value.filter((room) => room.opponentName.includes(searchQuery.value));
});

// ✅ 채팅방 클릭 시, API 호출 후 해당 채팅방으로 이동
const enterChat = async (receiverId) => {
  try {
    const chatRoomData = await enterChatRoom(myId, receiverId); // ✅ 모듈 사용
    router.push({
      name: "ChatIndividualView",
      query: { info: JSON.stringify(chatRoomData)}
});
  } catch (error) {
    alert("채팅방 입장에 실패했습니다.");
  }
};
</script>

<style scoped>
.overflow-x-auto {
  scrollbar-width: thin;
  scrollbar-color: #9dbb9f #e5e7eb;
}
</style>
