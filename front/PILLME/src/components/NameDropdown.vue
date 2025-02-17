<template>
  <div class="relative">
    <!-- ✅ 사용자 이름과 드롭다운 버튼 -->
    <button @click="toggleModal" class="flex items-center space-x-1">
      <p class="text-center whitespace-nowrap text-2xl font-base">{{ username }}</p>
      <img src="../assets/namedropdown.svg" alt=""
           class="w-4 h-4 transition-transform duration-300"
           :class="{ 'rotate-180': isOpen }">
    </button>

    <!-- ✅ 드롭다운 (사용자 이름 아래 중앙 정렬) -->
    <div v-if="isOpen" 
         class="absolute left-1/2 transform -translate-x-1/2 top-full mt-1 
                w-max min-w-[150px] bg-white border border-gray-300 shadow-lg rounded-lg p-2 z-50"
         @click.stop>
      <ul class="space-y-2 text-sm text-center">
        <!-- ✅ 가족 목록 -->
        <template v-if="dependents.length">
          <li v-for="dependent in dependents" :key="dependent.dependentId"
              class="cursor-pointer hover:bg-gray-100 p-2 rounded border-t"
              @click="switchToDependent(dependent)">
            {{ dependent.dependentName }}
          </li>
        </template>

        <!-- ✅ 가족이 없을 경우 -->
        <template v-else>
          <li class="text-gray-500 text-sm p-2">등록된 가족이 없습니다.</li>
        </template>

        <!-- ✅ 가족 추가 버튼 -->
        <li class="border-t mt-2">
          <button @click="openFamilyModal" 
                  class="w-full text-[#3D5A3F] hover:bg-gray-100 p-2 rounded">
            가족 추가하기
          </button>
        </li>
      </ul>
    </div>

    <!-- ✅ 가족 추가 모달 -->
    <FamilyAddModal 
      :isOpen="isFamilyModalOpen" 
      @close="handleModalClose" 
      @add="handleModalClose"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import FamilyAddModal from '../components/FamilyAddModal.vue';
import { fetchUsername } from '../api/username';
import { fetchDependents } from '../api/dependentmember';

// ✅ 상태 관리
const isOpen = ref(false);
const isFamilyModalOpen = ref(false);
const dependents = ref([]); // 가족 목록
const username = ref('');
const router = useRouter();
const route = useRoute();

// ✅ 모달 토글 (드롭다운 열 때 가족 목록 새로고침)
const toggleModal = async (event) => {
  event.stopPropagation();
  
  if (!isOpen.value) { // 드롭다운을 열 때만 가족 목록 새로고침
    try {
      dependents.value = await fetchDependents();
    } catch (error) {
      console.error("가족 목록 새로고침 실패:", error);
    }
  }
  
  isOpen.value = !isOpen.value;
};

// ✅ 가족 전환 (경로 변경)
const switchToDependent = (dependent) => {
  const newPath = `${route.path}/${dependent.dependentId}`;
  alert(`${dependent.dependentName}의 메인페이지로 이동합니다.`);
  router.push(newPath);
  isOpen.value = false;
};

// ✅ 가족 추가 모달 열기
const openFamilyModal = () => {
  isOpen.value = false;
  isFamilyModalOpen.value = true;
};

// ✅ 모달 닫힐 때 가족 목록 갱신
const handleModalClose = async () => {
  isFamilyModalOpen.value = false;
  dependents.value = await fetchDependents(); // 가족 목록 새로고침
};

// ✅ 외부 클릭 감지 → 모달 닫기
const closeModal = (event) => {
  if (isOpen.value && !event.target.closest('.relative')) {
    isOpen.value = false;
  }
};

// ✅ 초기 데이터 불러오기
onMounted(async () => {
  try {
    dependents.value = await fetchDependents();
    username.value = await fetchUsername();
  } catch (error) {
    console.error("초기 데이터 불러오기 실패:", error);
  }
  
  window.addEventListener('click', closeModal);
});

// ✅ 이벤트 제거 (컴포넌트 언마운트 시)
onUnmounted(() => {
  window.removeEventListener('click', closeModal);
});
</script>
