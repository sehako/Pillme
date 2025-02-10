<template>
  <div class="relative">
    <!-- ✅ 사용자 이름과 드롭다운 버튼 -->
    <button @click="toggleModal" class="flex items-center space-x-1">
      <p class="text-center whitespace-nowrap text-2xl font-base">사용자이름</p>
      <img src="../assets/namedropdown.svg" alt="이름드롭다운"
           class="w-4 h-4 transition-transform duration-300"
           :class="{ 'rotate-180': isOpen }">
    </button>

    <!-- ✅ 드롭다운 (사용자 이름 아래 중앙 정렬) -->
    <div v-if="isOpen" 
         class="absolute left-1/2 transform -translate-x-1/2 top-full mt-1 
                w-max min-w-[150px] bg-white border border-gray-300 shadow-lg rounded-lg p-2 z-50"
         @click.stop>
      
      <ul class="space-y-2 text-sm text-center">
        <!-- ✅ 피부양자가 있을 경우 목록 표시 -->
        <template v-if="dependents.length">
          <li v-for="(dependent, index) in dependents" :key="index"
              class="cursor-pointer hover:bg-gray-100 p-2 rounded border-t"
              @click="switchToDependent(dependent)">
            {{ dependent }}
          </li>
        </template>

        <!-- ✅ 피부양자가 없을 경우 메시지 -->
        <template v-else>
          <li class="text-gray-500 text-sm p-2">등록된 가족이 없습니다.</li>
        </template>

        <!-- ✅ 구분선 & 가족 추가하기 버튼 -->
        <li class="border-t mt-2">
          <button @click="openFamilyModal" 
                  class="w-full text-[#3D5A3F] hover:bg-gray-100 p-2 rounded">
            가족 추가하기
          </button>
        </li>
      </ul>
    </div>

    <!-- ✅ 가족 추가 모달 -->
    <FamilyAddModal :isOpen="isFamilyModalOpen" @close="isFamilyModalOpen = false" @add="addNewFamily" />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import FamilyAddModal from '../components/FamilyAddModal.vue';

const isOpen = ref(false);
const isFamilyModalOpen = ref(false);
const dependents = ref([]); // ❗ 초기값 빈 배열로 설정
const router = useRouter();

// ✅ 모달 토글 함수 (stopPropagation 추가)
const toggleModal = (event) => {
  event.stopPropagation(); // ✅ 내부 클릭 시 closeModal 방지
  isOpen.value = !isOpen.value;
};

// ✅ 가족 전환 기능 (페이지 이동 추가)
const switchToDependent = (dependent) => {
  alert(`${dependent}의 메인페이지로 이동합니다.`);
  router.push(`/user/${dependent}`); // ✅ 실제 페이지 이동
  isOpen.value = false;
};

// ✅ 가족 추가하기 모달 열기
const openFamilyModal = () => {
  isOpen.value = false; // 드롭다운 닫기
  isFamilyModalOpen.value = true;
};

// ✅ 가족 추가 로직 (배열 업데이트 반영 방식 수정)
const addNewFamily = (newFamily) => {
  dependents.value = [...dependents.value, newFamily.name]; // ✅ 배열 업데이트
  alert(`${newFamily.name} (${newFamily.relation})가 추가되었습니다.`);
  isFamilyModalOpen.value = false;
};

// ✅ 외부 클릭 감지 → 모달 닫기 (이벤트 방지 개선)
const closeModal = (event) => {
  if (isOpen.value && !event.target.closest('.relative')) {
    isOpen.value = false;
  }
};

// ✅ 백엔드에서 피부양자 정보 불러오기 (더미 데이터 추가)
onMounted(() => {
  // TODO: 실제 API 연동 필요
  dependents.value = ["피부양자1", "피부양자2"];
  window.addEventListener('click', closeModal);
});

onUnmounted(() => {
  window.removeEventListener('click', closeModal);
});
</script>
