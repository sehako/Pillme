<template>
  <div class="flex flex-col w-full !min-h-full !items-start !justify-start">
    <div class="flex flex-col w-full">
      <!-- 상단 안내 메시지 -->
      <div
        class="w-full flex items-center justify-between bg-[#9DBB9F80] p-3 rounded-b-sm shadow-md">
        <h2 class="text-lg font-bold text-green-900">오늘 복약 하셔야 하는 분들입니다.</h2>
        <button class="text-green-700 text-xl">⚙️</button>
      </div>

      <!-- ✅ 가로 스크롤 가능한 사용자 목록 -->
      <div class="overflow-x-auto w-full whitespace-nowrap px-1.5 py-2">
        <div class="w-48 sm:w-56 md:w-64 lg:w-72 flex flex-nowrap space-x-4">
          <GreenCard
  v-for="user in dependents"
  :key="user.dependentId"
  class="flex flex-col min-w-[200px] p-1 rounded-lg bg-9DBB9F80"
>
  <div class="flex flex-col items-center justify-between">
    <p class="font-bold text-lg text-nowrap">{{ user.dependentName }}</p>

<!-- 처방전이 있을 때만 체크박스 영역 표시 -->
<div
  v-if="processedManagementData[user.dependentId] && processedManagementData[user.dependentId].hasPrescription"
  class="flex flex-row items-center justify-between"
>
  <!-- 아침 영역 -->
  <div
    v-if="aggregateStatus(processedManagementData[user.dependentId].data, 'morning') !== '처방없음'"
    class="flex flex-col items-center justify-center flex-1"
  >
    <div class="bg-[#9DBB9F80] rounded-full p-1">아침</div>
    <img :src="getIcon(user.dependentId, 'morning')" alt="아침 복약체크" />
  </div>

  <!-- 점심 영역 -->
  <div
    v-if="aggregateStatus(processedManagementData[user.dependentId].data, 'lunch') !== '처방없음'"
    class="flex flex-col items-center justify-center flex-1"
  >
    <div class="bg-[#FFE2E2] rounded-full p-1">점심</div>
    <img :src="getIcon(user.dependentId, 'lunch')" alt="점심 복약체크" />
  </div>

  <!-- 저녁 영역 -->
  <div
    v-if="aggregateStatus(processedManagementData[user.dependentId].data, 'dinner') !== '처방없음'"
    class="flex flex-col items-center justify-center flex-1"
  >
    <div class="bg-[#EF7C8E] rounded-full p-1">저녁</div>
    <img :src="getIcon(user.dependentId, 'dinner')" alt="저녁 복약체크" />
  </div>

  <!-- 자기 전 영역 -->
  <div
    v-if="aggregateStatus(processedManagementData[user.dependentId].data, 'sleep') !== '처방없음'"
    class="flex flex-col items-center justify-center flex-1"
  >
    <div class="bg-[#888585] rounded-full p-1">수면</div>
    <img :src="getIcon(user.dependentId, 'sleep')" alt="자기 전 복약체크" />
  </div>
</div>



    <!-- 처방전이 없는 경우 -->
    <div v-else class="flex flex-nowrap items-center justify-center p-2 rounded-lg bg-[#9DBB9F80]">
      <span>처방전이<br>없습니다.</span>
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
  v-for="room in filteredChatRooms"
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
              <span class="text-gray-500 text-xs">{{ formatTime(room.lastMessageTime) }}</span>
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
import { useDependents } from '../composables/useDependents';
import { fetchManagementData } from '../api/drugmanagement_dependency';
import CheckDoneboxes from '../assets/CheckDoneboxes.svg';
import Checkboxes from '../assets/Checkboxes.svg';

const { dependents, loadDependents } = useDependents();

const chatStore = useChatStore();
const router = useRouter();

// ✅ 로컬스토리지에서 현재 로그인한 사용자 ID 가져오기
const tokenId = decodeToken(localStorage.getItem('accessToken')).memberId;
const myId = tokenId ? tokenId : null;

const searchQuery = ref('');
const chatRooms = ref([]);

// 특정 dependent의 시간대별 aggregated 상태에 따른 아이콘을 반환하는 함수
const getIcon = (dependentId, timeKey) => {
  const data = processedManagementData.value[dependentId];
  // 만약 복용중인 처방전이 없다면 기본 아이콘 (예: 미복용 아이콘) 반환
  if (!data || !data.hasPrescription) {
    return Checkboxes;
  }
  // data.data 배열 안의 개별 약들에 대해 aggregate 상태를 계산
  const status = aggregateStatus(data.data, timeKey);
  // "복용완료"이면 체크 완료 아이콘, 그 외(일부/완전 미복용)이면 미체크 아이콘 반환
  return status === "복용완료" ? CheckDoneboxes : Checkboxes;
};

//시간 포맷
function formatTime(timestamp) {
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

// 각 가족의 개별 복약 데이터를 저장 (dependentId가 key)
const processedManagementData = ref({});

/**
 * 각 약의 시간대별 복용 상태를 처리
 * 만약 처방이 없으면 "처방없음"
 * 처방이 있으면
 * - "복용완료"와 "미복용" 상태만 고려 (처방없음은 제외)
 * - 복용완료가 하나 이상이고 미복용이 하나 이상이면 "일부 미복용"
 * - 복용완료만 있으면 "복용완료"
 * - 미복용만 있으면 "완전 미복용"
 */
const aggregateStatus = (meds, key) => {
  // key는 "morning", "lunch", "dinner", "sleep"
  const statuses = meds.map(med => med[key]).filter(status => status !== "처방없음");
  if (statuses.length === 0) return "처방없음";
  const hasTaken = statuses.includes("복용완료");
  const hasNot = statuses.includes("미복용");
  if (hasNot) {
    return hasTaken ? "일부 미복용" : "완전 미복용";
  }
  return "복용완료";
};

// 기존의 API 응답을 시간별로 가공하는 함수
const processMedicationData = (managementResult) => {
  if (!managementResult || managementResult.length === 0) {
    return { hasPrescription: false, message: "현재 복용중인 처방전이 없습니다." };
  }
  const processed = managementResult.map((med) => ({
    managementId: med.managementId,
    medicationName: med.medicationName,
    morning: med.morning ? (med.morningTaking ? "복용완료" : "미복용") : "처방없음",
    lunch: med.lunch ? (med.lunchTaking ? "복용완료" : "미복용") : "처방없음",
    dinner: med.dinner ? (med.dinnerTaking ? "복용완료" : "미복용") : "처방없음",
    sleep: med.sleep ? (med.sleepTaking ? "복용완료" : "미복용") : "처방없음"
  }));
  return { hasPrescription: true, data: processed };
};

// 모든 dependent에 대해 관리 데이터를 요청하고 가공하는 함수
const loadAllManagementData = async () => {
  try {
    // 먼저 dependents 데이터를 불러옴
    await loadDependents();
    const managementPromises = dependents.value.map(async (dependent) => {
      const response = await fetchManagementData(dependent.dependentId);
      const processedData = processMedicationData(response.result);
      return { dependentId: dependent.dependentId, data: processedData };
    });
    const results = await Promise.all(managementPromises);
    results.forEach(item => {
      processedManagementData.value[item.dependentId] = item.data;
    });
    console.log("각 사용자별 가공된 복약 데이터:", processedManagementData.value);

    // 각 사용자의 aggregate 상태를 계산하여 콘솔에 출력
    for (const dependentId in processedManagementData.value) {
      const userData = processedManagementData.value[dependentId];
      if (!userData.hasPrescription) {
        console.log(`Dependent ${dependentId}: 현재 복용중인 처방전이 없습니다.`);
      } else {
        const meds = userData.data;
        const morningStatus = aggregateStatus(meds, "morning");
        const lunchStatus = aggregateStatus(meds, "lunch");
        const dinnerStatus = aggregateStatus(meds, "dinner");
        const sleepStatus = aggregateStatus(meds, "sleep");
        console.log(`Dependent ${dependentId} - 아침: ${morningStatus}, 점심: ${lunchStatus}, 저녁: ${dinnerStatus}, 자기 전: ${sleepStatus}`);
      }
    }
  } catch (error) {
    console.error("모든 관리 데이터 요청 실패:", error);
  }
};



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
  loadAllManagementData(); // 모든 가족의 복약 데이터를 불러와 처리
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
