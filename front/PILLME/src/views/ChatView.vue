<template>
  <div class="max-w-full mx-auto p-4 text-center h-screen">
    <!-- 헤더 -->
    <div class="flex items-center justify-between bg-green-100 p-3 rounded-lg shadow-md">
      <h2 class="text-lg font-bold text-green-900">오늘 복약 하셔야 하는 분들입니다.</h2>
      <button class="text-green-700 text-xl">⚙️</button>
    </div>

    <!-- 복약 체크 사용자 목록 (스크롤 가능) -->
    <div class="flex overflow-x-auto space-x-3 my-4 p-2 bg-green-100 rounded-lg shadow-md">
      <div v-for="friend in user.friends" :key="friend.id" class="flex flex-col items-center">
        <div class="w-12 h-12 bg-green-300 text-white rounded-full flex items-center justify-center text-lg font-semibold">
          {{ friend.name.charAt(0) }}
        </div>
        <span class="text-xs mt-1">{{ friend.name }}</span>
      </div>
    </div>

    <!-- 검색 바 -->
    <div class="relative my-4">
      <input type="text" v-model="searchQuery" placeholder="검색"
        class="w-full p-2 pl-8 border rounded-lg focus:ring focus:ring-green-300">
      <span class="absolute left-2 top-2 text-gray-500">🔍</span>
    </div>

    <!-- 친구 추가된 사용자와의 채팅 목록 -->
    <div v-if="filteredChatRooms.length === 0" class="text-gray-500">
      친구 추가된 사용자와의 채팅방이 없습니다.
    </div>

    <div v-else class="space-y-2">
      <div v-for="room in filteredChatRooms" :key="room.id" 
           class="flex items-center justify-between p-4 bg-green-100 rounded-lg shadow-md cursor-pointer hover:bg-green-200 transition"
           @click="enterChatRoom(room.id)">
        <div class="flex items-center space-x-2">
          <div class="w-10 h-10 bg-green-300 text-white rounded-full flex items-center justify-center text-lg font-semibold">
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
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';

// ✅ 더미 로그인된 사용자 정보 (실제 로그인 기능 없이 가정)
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
  // 채팅방 상세 페이지로 이동하는 로직 (아직 미구현)
};
</script>

<style scoped>
.chat-box {
  max-height: 320px;
}
</style>
