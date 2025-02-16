<template>
  <div class="flex items-center justify-between py-1 w-full">
    <!-- 로고 및 이름 -->
    <div class="flex items-center">
      <BaseLogo :src="logoSrc" class="w-9 h-9"/>
      <p class="ml-2 text-sm font-semibold">{{ name }}</p>
    </div>

    <!-- 삭제 버튼 -->
    <button @click="handleDelete" class="text-red-500 hover:text-red-700">
      <img src="../assets/delete.svg" alt="삭제" class="w-4 h-4"/>
    </button>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from "vue";
import BaseLogo from "../components/BaseLogo.vue";
import logoSrc from "../assets/logi_nofont.svg";

const props = defineProps({
  dependencyId: {  // ✅ dependencyId를 props로 받음
    type: Number,
    required: true,
  },
  name: String,
});

const emit = defineEmits(["deleteMember"]);

const handleDelete = () => {
  if (!props.dependencyId) {
    console.error("❌ dependencyId가 없습니다!", props.dependencyId);
    return;
  }

  if (confirm(`${props.name}님을 목록에서 삭제하시겠습니까?`)) {
    emit("deleteMember", props.dependencyId); // ✅ dependencyId 전송
  }
};
</script>

<style scoped>
/* 줄어든 아이템 크기 */
</style>
