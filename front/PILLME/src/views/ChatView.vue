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
          <GreenCard
            v-for="(user, index) in users"
            :key="index"
            class="flex flex-col min-w-[200px] p-1 rounded-lg bg-green-100">
            <div class="flex flex-col items-center justify-between">
              <p class="font-bold text-lg text-nowrap">{{ user.name }}</p>
              <div class="flex flex-row items-center space-x-2">
                <div class="items-center justify-center flex flex-col">
                  <div class="bg-[#9DBB9F80] rounded-full p-1">아침
                  </div>
                  <img src="../assets/CheckCircle.svg" alt="약물복용체크">
                </div>
                <div class="items-center justify-center flex flex-col">
                  <div class="bg-[#FFE2E2] rounded-full p-1">점심
                  </div>
                  <img src="../assets/CheckCircle.svg" alt="약물복용체크">
                </div>
                <div class="items-center justify-center flex flex-col">
                  <div class="bg-[#EF7C8E] rounded-full p-1">저녁
                  </div>
                  <img src="../assets/CheckCircle.svg" alt="약물복용체크">
                </div>
                <div class="items-center justify-center flex flex-col">
                  <div class="bg-[#888585] rounded-full p-1">자기 전
                  </div>
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
   <!-- 채팅 목록 -->


      <!-- ✅ 친구 추가된 사용자와의 채팅 목록 -->
      <div v-if="filteredChatRooms.length === 0" class="text-gray-500">
        친구 추가된 사용자와의 채팅방이 없습니다.
      </div>

      <div v-else class="space-y-2">
        <div v-for="room in filteredChatRooms" :key="room.id"
          class="flex items-center justify-between p-4 bg-[#9DBB9F26] rounded-lg shadow-md cursor-pointer hover:bg-[#B5CCB7] transition"
          @click="enterChatRoom(room.id)">
          <div class="flex items-center space-x-2">
            <div class="w-10 h-10 bg-[#9DBB9F80] text-white rounded-full flex items-center justify-center text-lg font-semibold">
              {{ room.name.charAt(0) }}
            </div>
            <div>
              <div class="font-semibold">{{ room.name }}</div>
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
import GreenCard from '../layout/GreenCard.vue';

// ✅ 사용자 정보 (예제)
const users = ref([
  { name: "김싸피" },
  { name: "이싸피" },
  { name: "나싸피" },
  { name: "박싸피" }
]);

const user = ref({
  id: 123,
  name: "홍길동",
  friends: [
    { id: 2, name: "김싸피" },
    { id: 3, name: "이싸피" },
    { id: 4, name: "나싸피" }
  ]
});

const chatRooms = ref([
  { id: 1, name: "김싸피", userId: 2 },
  { id: 2, name: "이싸피", userId: 3 },
  { id: 3, name: "나싸피", userId: 4 },
  { id: 4, name: "박싸피", userId: 5 } // 친구가 아님
]);

const searchQuery = ref("");

const filteredChatRooms = computed(() => {
  return chatRooms.value.filter(room =>
    user.value.friends.some(friend => friend.id === room.userId) &&
    room.name.includes(searchQuery.value)
  );
});

const enterChatRoom = (roomId) => {
  console.log(`채팅방 ${roomId}로 이동`);
};
</script>

<style scoped>
/* ✅ 가로 스크롤이 부드럽게 동작하도록 설정 */
.overflow-x-auto {
  scrollbar-width: thin;
  scrollbar-color: #9dbb9f #e5e7eb;
}
</style>
