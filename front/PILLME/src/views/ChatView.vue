<template>
  <div class="flex flex-col w-full h-full !items-start !justify-start">
    <div class="flex flex-col w-full">
      <!-- 상단 안내 메시지 -->
      <div class="w-full flex items-center justify-between bg-[#9DBB9F80] p-3 rounded-b-sm shadow-md">
        <h2 class="text-lg font-bold text-green-900">오늘 복약 하셔야 하는 분들입니다.</h2>
        <button class="text-green-700 text-xl">⚙️</button>
      </div>

      <!-- ✅ 가로 스크롤 가능한 사용자 목록 -->
      <div class="overflow-x-auto w-full whitespace-nowrap px-1.5 py-2">
        <div class="w-48 sm:w-56 md:w-64 lg:w-72 flex flex-nowrap space-x-4">
          <!-- Pinia 스토어에서 관리하는 전체 사용자 목록을 순회 -->
          <GreenCard
            v-for="user in chatStore.users"
            :key="user.id"
            class="flex flex-col min-w-[200px] p-1 rounded-lg bg-9DBB9F80">
            <div class="flex flex-col items-center justify-between">
              <p class="font-bold text-lg text-nowrap">{{ user.name }}</p>
              <div class="flex flex-row items-center space-x-2">
                <div class="flex flex-col items-center justify-center">
                  <div class="bg-[#9DBB9F80] rounded-full p-1">아침</div>
                  <img src="../assets/CheckCircle.svg" alt="약물복용체크">
                </div>
                <div class="flex flex-col items-center justify-center">
                  <div class="bg-[#FFE2E2] rounded-full p-1">점심</div>
                  <img src="../assets/CheckCircle.svg" alt="약물복용체크">
                </div>
                <div class="flex flex-col items-center justify-center">
                  <div class="bg-[#EF7C8E] rounded-full p-1">저녁</div>
                  <img src="../assets/CheckCircle.svg" alt="약물복용체크">
                </div>
                <div class="flex flex-col items-center justify-center">
                  <div class="bg-[#888585] rounded-full p-1">자기 전</div>
                  <img src="../assets/CheckCircle.svg" alt="약물복용체크">
                </div>
              </div>
            </div>
          </GreenCard>
        </div>
      </div>
      <hr class="border-t border-[#9DBB9F80]">
      
      <!-- ✅ 검색 바 -->
      <div class="min-w-fit relative m-2">
        <input type="text" v-model="searchQuery" placeholder="검색"
               class="w-full p-2 pl-8 border rounded-lg focus:ring focus:ring-[#B5CCB7]">
        <span class="absolute left-2 top-2 text-gray-500">🔍</span>
      </div>
      
      <!-- ✅ 채팅 목록 -->
      <div v-if="filteredChatRooms.length === 0" class="text-gray-500">
        친구 추가된 사용자와의 채팅방이 없습니다.
      </div>
      <div v-else class="space-y-2">
        <!-- 스토어의 채팅방 데이터를 기반으로 필터링된 채팅방 목록 출력 -->
        <div v-for="room in filteredChatRooms" :key="room.id"
             class="flex items-center justify-between p-4 bg-[#9DBB9F26] rounded-lg shadow-md cursor-pointer hover:bg-[#B5CCB7] transition"
             @click="enterChatRoom(room.id)">
          <div class="flex items-center space-x-2">
            <div class="w-10 h-10 bg-[#9DBB9F80] text-white rounded-full flex items-center justify-center text-lg font-semibold">
              {{ room.name.charAt(0) }}
            </div>
            <div>
              <div class="font-semibold">{{ room.name }}</div>
              <!-- 추후 마지막 메시지 내용도 API에서 받아온 데이터를 사용하도록 업데이트 -->
              <div class="text-sm text-gray-600 truncate w-32">마지막 메시지 내용...</div>
            </div>
          </div>
          <button class="text-gray-600">🔍</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import GreenCard from '../layout/GreenCard.vue';
import { useChatStore } from '../stores/chatStore';

// 중앙 상태 관리(스토어)에서 데이터를 가져옵니다.
const chatStore = useChatStore();
const router = useRouter();

// 검색 쿼리는 로컬 상태로 관리합니다.
const searchQuery = ref("");

// 친구로 등록된 사용자와 검색어에 맞는 채팅방만 필터링합니다.
// (DB 테이블 구조에 맞춘 데이터 모델을 기준으로, 채팅방 객체에 포함된 userId를 사용)
const filteredChatRooms = computed(() => {
  return chatStore.chatRooms.filter(room =>
    chatStore.currentUser.friends.some(friend => friend.id === room.userId) &&
    room.name.includes(searchQuery.value)
  );
});

// 채팅방 클릭 시, 해당 채팅방으로 이동합니다.
const enterChatRoom = (roomId) => {
  router.push(`/chat/${roomId}`);
};
</script>

<style scoped>
.overflow-x-auto {
  scrollbar-width: thin;
  scrollbar-color: #9dbb9f #e5e7eb;
}
</style>
