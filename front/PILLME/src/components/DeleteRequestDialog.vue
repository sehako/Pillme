<template>
    <div class="bg-white shadow-lg rounded-lg p-6 border border-gray-300 w-full max-w-sm mx-auto">
      <button 
        @click="$emit('close')"
        class="absolute top-4 right-4 text-gray-400 hover:text-gray-600 transition"
      >
        <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 24 24" class="w-6 h-6">
          <path d="M18 6L6 18M6 6l12 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
  
      <!-- ✅ 경고 아이콘 -->
      <div class="flex justify-center">
        <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 24 24" class="w-12 h-12 text-[#EF7C8E]">
          <path d="M12 2L2 22h20L12 2zm0 3.3l7.74 15.4H4.26L12 5.3zm-1 10.7h2v2h-2v-2zm0-6h2v4h-2v-4z"></path>
        </svg>
      </div>
  
      <!-- ✅ 다이얼로그 내용 -->
      <p class="text-lg font-semibold text-center text-[#EF7C8E] mt-4">가족 관계 삭제 요청</p>
      <p class="text-center text-gray-700 mt-4">{{ username }}<br>삭제 요청을 수락하시겠습니까?</p>
      <p class="text-sm text-gray-500 mt-4 text-center">삭제 요청을 수락하면 가족 관계가 해제됩니다.</p>
  
      <div class="flex justify-center mt-4 sm:mt-6 gap-2 sm:gap-4">
        <button @click="handleReject" class="px-6 sm:px-12 py-2 border border-gray-400 rounded-lg text-gray-600">
          거절
        </button>
        <button @click="handleAccept" class="px-6 sm:px-12 py-2 bg-[#EF7C8E] text-white rounded-lg">
          동의
        </button>
      </div>
    </div>
  </template>
  
  <script setup>
  import { rejectDependencyDelete, acceptDependencyDelete } from "../api/notify"; // ✅ 삭제 요청 API 사용
  
  const emit = defineEmits(["close", "deleteReject", "deleteAccept"]);
  
  const props = defineProps({
    username: String,
    id: Number, // ✅ 삭제 요청을 보낸 senderId
    dependencyId: Number // 삭제할 dependencyId
  });
  
  const handleReject = async () => {
    if (!props.id || !props.dependencyId) {
      console.error("❌ 거절할 senderId 또는 dependencyId가 없습니다.");
      return;
    }
  
    const success = await rejectDependencyDelete(props.id, props.dependencyId); // ✅ 관계 삭제 요청 거절
    if (success) {
      console.log("🚀 관계 삭제 요청 거절 성공:", props.id);
      emit("deleteReject", { id: props.id, dependencyId: props.dependencyId }); // ✅ 거절 이벤트 전송
      emit("close"); // ✅ 다이얼로그 닫기
    } else {
      console.error("❌ 관계 삭제 요청 거절 실패");
    }
  };
  
  const handleAccept = async () => {
    if (!props.id || !props.dependencyId) {
      console.error("❌ 승인할 senderId가 없습니다.");
      return;
    }
  
    const success = await acceptDependencyDelete(props.id, props.dependencyId); // ✅ 관계 삭제 요청 수락
    if (success) {
      console.log("✅ 관계 삭제 요청 수락 성공:", props.id);
      emit("deleteAccept", { id: props.id, dependencyId: props.dependencyId }); // ✅ 수락 이벤트 전송
      emit("close"); // ✅ 다이얼로그 닫기
    } else {
      console.error("❌ 관계 삭제 요청 수락 실패");
    }
  };
  </script>
  