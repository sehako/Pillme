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
        <!-- ✅ 피부양자가 있을 경우 목록 표시 -->
        <template v-if="dependents.length">
          <li v-for="dependent in dependents" :key="dependent.dependentId"
              class="cursor-pointer hover:bg-gray-100 p-2 rounded border-t"
              @click="switchToDependent(dependent)">
            {{ dependent.dependentName }}
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
    <FamilyAddModal 
      :isOpen="isFamilyModalOpen" 
      @close="isFamilyModalOpen = false" 
      @add="addNewFamily" 
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import FamilyAddModal from '../components/FamilyAddModal.vue';
import { fetchUsername } from '../api/username';
import { fetchDependents } from '../api/dependentmember';

// 상태 변수 선언
const isOpen = ref(false);
const isFamilyModalOpen = ref(false);
const dependents = ref([]); // API 데이터를 저장할 배열
const username = ref('');
const router = useRouter();
const route = useRoute();

// ✅ 모달 토글 함수
const toggleModal = (event) => {
  event.stopPropagation();
  isOpen.value = !isOpen.value;
};

// ✅ 가족 전환 기능 (dependentId 포함한 경로로 이동)
const switchToDependent = (dependent) => {
  const newPath = `${route.path}/${dependent.dependentId}`;
  alert(`${dependent.dependentName}의 메인페이지로 이동합니다.`);
  router.push(newPath);
  isOpen.value = false;
};

// ✅ 가족 추가하기 모달 열기
const openFamilyModal = () => {
  isOpen.value = false;
  isFamilyModalOpen.value = true;
};

// ✅ 가족 추가 로직 (API 반영은 안 함, UI에서만 추가)
const addNewFamily = (newFamily) => {
  dependents.value.push({ dependentId: Date.now(), dependentName: newFamily.name });
  alert(`${newFamily.name} (${newFamily.relation})가 추가되었습니다.`);
  isFamilyModalOpen.value = false;
};

// ✅ 외부 클릭 감지 → 모달 닫기
const closeModal = (event) => {
  if (isOpen.value && !event.target.closest('.relative')) {
    isOpen.value = false;
  }
};

// 컴포넌트 마운트 시 처리
onMounted(() => {
  // ✅ 실제 API에서 피부양자 목록 가져오기
  fetchDependents()
    .then((data) => {
      dependents.value = data;
    })
    .catch((error) => {
      console.error("피부양자 목록 불러오기 실패:", error);
    });

  // ✅ 사용자 이름 가져오기
  fetchUsername()
    .then((name) => {
      username.value = name;
    })
    .catch((error) => {
      console.error("사용자 이름 불러오기 실패:", error);
    });

  window.addEventListener('click', closeModal);
});

onUnmounted(() => {
  window.removeEventListener('click', closeModal);
});
</script>
