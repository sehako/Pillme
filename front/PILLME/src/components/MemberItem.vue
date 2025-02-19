<template>
  <div class="flex items-center justify-between py-1 w-full">
    <!-- 로고 및 이름 -->
    <div class="flex items-center">
      <p class="ml-2 text-sm font-semibold">
        {{ name }}
        <span class="text-gray-400 text-xs ml-1">({{ role }})</span> <!-- ✅ 역할을 회색 글씨로 표시 -->
      </p>
    </div>

    <!-- 삭제 버튼 -->
    <button @click="handleDelete" class="text-red-500 hover:text-red-700">
      <img src="../assets/delete.svg" alt="삭제" class="w-4 h-4"/>
    </button>
  </div>
</template>

<script setup>
import { computed } from "vue";
import BaseLogo from "../components/BaseLogo.vue";
import logoSrc from "../assets/logi_nofont.svg";

const props = defineProps({
  dependencyId: {  // ✅ dependencyId를 props로 받음
    type: Number,
    required: true,
  },
  name: {
    type: String,
    required: true,
  },
  role: {  // ✅ 역할 추가 (보호자 / 피보호자)
    type: String,
    default: "피보호자",
  },
});

const emit = defineEmits(["deleteMember"]);

const handleDelete = () => {
  if (!props.dependencyId) {
    console.error("❌ dependencyId가 없습니다!", props.dependencyId);
    return;
  }
  if (window.confirm(`정말 ${props.name} (${props.role})을(를) 삭제하시겠습니까?`)) {
    emit("deleteMember", props.dependencyId); // ✅ dependencyId 전송
  }
};
</script>

<style scoped>
/* 줄어든 아이템 크기 */
</style>
