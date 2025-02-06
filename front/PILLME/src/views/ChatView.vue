<template>
  <div class="max-w-lg mx-auto p-6 text-center">
    <h2 class="text-2xl font-bold mb-4">친구와의 채팅 목록</h2>
    
    <div v-if="chatRooms.length === 0" class="text-gray-500">
      친구 추가된 사용자와의 채팅방이 없습니다.
    </div>
    
    <div v-else class="space-y-2">
      <div v-for="room in chatRooms" :key="room.id" 
           class="p-4 bg-gray-100 rounded-lg shadow-md cursor-pointer hover:bg-gray-200 transition"
           @click="enterChatRoom(room.id)">
        {{ room.name }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

// ✅ 더미 로그인된 사용자 정보 (실제 로그인 기능 없이 가정)
const user = ref({ id: 123, name: "홍길동", friends: [2, 3] });

const chatRooms = ref([]);

onMounted(() => {
  fetchChatRooms();
});

const fetchChatRooms = async () => {
  // ✅ 친구 추가된 사용자와의 채팅방만 표시
  const allRooms = [
    { id: 1, name: "사용자A", userId: 2 },
    { id: 2, name: "사용자B", userId: 3 },
    { id: 3, name: "사용자C", userId: 4 } // 친구가 아님
  ];

  chatRooms.value = allRooms.filter(room => user.value.friends.includes(room.userId));
};

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
