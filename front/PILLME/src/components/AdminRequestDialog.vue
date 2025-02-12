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
    <p class="text-lg font-semibold text-center text-[#EF7C8E] mt-4">관리자 추가 알림</p>
    <p class="text-center text-gray-700 mt-4">{{ username }}<br>동의하시겠습니까?</p>
    <p class="text-sm text-gray-500 mt-4 text-center">관리자로 추가되면, 모든 복약 상태가 공개됩니다.</p>

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
import { defineProps, defineEmits } from "vue";
import { rejectAdminRequest, acceptAdminRequest } from "../api/notify";

const emit = defineEmits(["close", "reject", "accept"]);

const props = defineProps({
  username: String,
  id: Number, // ✅ API 요청 시 protectId로 변환할 값
});

const handleReject = async () => {
  if (!props.id) {
    console.error("❌ 거절할 알림 ID가 없습니다.");
    return;
  }

  const success = await rejectAdminRequest(props.id); // ✅ API 요청 수행
  if (success) {
    console.log("🚀 알림 거절 성공:", props.id);
    emit("reject", { id: props.id }); // ✅ senderId를 부모에게 전달
    emit("close"); // ✅ 다이얼로그 닫기
  } else {
    console.error("❌ 알림 거절 실패");
  }
};

const handleAccept = async () => {
  if (!props.id) {
    console.error("❌ 승인할 알림 ID가 없습니다.");
    return;
  }

  const success = await acceptAdminRequest(props.id); // ✅ API 요청 수행
  if (success) {
    console.log("✅ 알림 승인 성공:", props.id);
    emit("accept", { id: props.id }); // ✅ senderId를 부모에게 전달
    emit("close"); // ✅ 다이얼로그 닫기
  } else {
    console.error("❌ 알림 승인 실패");
  }
};

</script>
