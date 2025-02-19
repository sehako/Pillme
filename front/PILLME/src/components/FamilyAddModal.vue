<template>
  <div 
    v-if="isOpen" 
    class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-20"
    @click.self="closeModal"
  >
    <!-- ✅ 반응형 팝업 모달 (현재 화면 비율 고려) -->
    <div class="bg-white shadow-lg rounded-lg p-6 w-[90vw] max-w-[480px] min-w-[300px]">
      
      <!-- ✅ 헤더 -->
      <div class="flex items-center justify-between border-b pb-3 mb-4">
        <h2 class="text-lg font-bold">인원 추가</h2>
        <button @click="closeModal" class="text-gray-500 hover:text-gray-800 text-lg">✕</button>
      </div>

      <!-- ✅ 인원 추가 컴포넌트 사용 -->
      <AddMember @close="closeModal" />

    </div>
  </div>
</template>

<script setup>
import AddMember from './AddMember.vue';

// ✅ 부모에서 전달받은 props
const props = defineProps({ isOpen: Boolean });

// ✅ 부모로 이벤트 전달
const emit = defineEmits(["close"]);

// ✅ 모달 닫기
const closeModal = () => {
  emit("close");
};

</script>
