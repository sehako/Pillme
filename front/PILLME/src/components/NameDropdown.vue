<template>
  <div class="relative">
    <!-- ✅ 사용자 이름과 드롭다운 버튼 -->
    <button 
      @click="toggleModal" 
      class="flex items-center space-x-1"
      aria-expanded="isOpen"
      aria-haspopup="true"
      :aria-label="`${username} 메뉴 열기`">
      <p class="text-center whitespace-nowrap text-2xl font-base">{{ username || '⠀⠀⠀⠀⠀' }}</p>
      <img
        src="../assets/namedropdown.svg"
        alt="드롭다운 메뉴 아이콘"
        class="w-4 h-4 transition-transform duration-300"
        :class="{ 'rotate-180': isOpen }"
      />
    </button>

    <!-- ✅ 드롭다운 (사용자 이름 아래 중앙 정렬) -->
    <div
      v-if="isOpen"
      class="absolute left-1/2 transform -translate-x-1/2 top-full mt-1 w-max min-w-[150px] bg-white border border-gray-300 shadow-lg rounded-lg p-2 z-50"
      @click.stop
    >
      <ul class="space-y-2 text-sm text-center">
        <!-- ✅ 가족 목록 -->
        <template v-if="dependents.length">
          <li
            v-for="dependent in dependents"
            :key="dependent.dependentId"
            class="cursor-pointer hover:bg-gray-100 p-2 rounded border-t"
            @click="openMedicationDialog(dependent)">
            {{ dependent.dependentName }}
          </li>
        </template>

        <!-- ✅ 가족이 없을 경우 -->
        <template v-else>
          <li class="text-gray-500 text-sm p-2">등록된 가족이 없습니다.</li>
        </template>

        <!-- ✅ 가족 추가 버튼 -->
        <li class="border-t mt-2">
          <button
            @click="openFamilyModal"
            class="w-full text-[#3D5A3F] hover:bg-gray-100 p-2 rounded"
          >
            가족 추가하기
          </button>
        </li>
      </ul>
    </div>

    <!-- ✅ 가족 추가 모달 -->
    <FamilyAddModal :isOpen="isFamilyModalOpen" @close="closeFamilyModal" @add="handleModalClose" />

    <!-- ✅ 복용 내역 Dialog -->
    <MedicationHistoryDialog
      v-if="isMedicationDialogOpen"
      :dependent="selectedDependent"
      @close="isMedicationDialogOpen = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import FamilyAddModal from '../components/FamilyAddModal.vue';
import { fetchUsername } from '../api/username';
import { useDependents } from '../composables/useDependents';
import MedicationHistoryDialog from './MedicationHistoryDialog.vue';
// ✅ 컴포저블에서 상태 가져오기
const { dependents, loadDependents } = useDependents();
const isOpen = ref(false);
const isFamilyModalOpen = ref(false);
const username = ref('');
const isMedicationDialogOpen = ref(false); 
const selectedDependent = ref(null); // ✅ selectedDependent 정의
const router = useRouter();
const route = useRoute();

const isLoading = ref(false);
const error = ref(null);

const LOGOUT_INDICATOR = '⠀⠀⠀⠀⠀'; // 점자 공백 문자 5개

// ✅ 모달 토글 (드롭다운 열 때 가족 목록 새로고침)
const toggleModal = async (event) => {
  event.stopPropagation();

  if (!isOpen.value) {
    await loadDependents(); // ✅ 가족 목록 새로고침
  }

  isOpen.value = !isOpen.value;
};

// ✅ 가족 추가 모달 열기
const openFamilyModal = () => {
  isOpen.value = false;
  isFamilyModalOpen.value = true;
};

// ✅ 모달 닫힐 때 가족 목록 갱신
const handleModalClose = async () => {
  isFamilyModalOpen.value = false;
  await loadDependents(); // ✅ 가족 목록 새로고침
};
// ✅ 단순히 모달만 닫는 함수
const closeFamilyModal = () => {
  isFamilyModalOpen.value = false;
};
// ✅ 외부 클릭 감지 → 모달 닫기
const closeModal = (event) => {
  // 드롭다운 메뉴와 버튼 영역을 제외한 클릭 처리
  const isClickInsideButton = event.target.closest('button');
  const isClickInsideDropdown = event.target.closest('.absolute');
  
  if (isOpen.value && !isClickInsideButton && !isClickInsideDropdown) {
    isOpen.value = false;
  }
};

// ✅ 복용 내역 Dialog 열기
const openMedicationDialog = (dependent) => {
  selectedDependent.value = dependent;
  isMedicationDialogOpen.value = true;
  isOpen.value = false;
};

// 데이터 로딩 상태 관리를 위한 함수
const loadData = async () => {
  isLoading.value = true;
  error.value = null;
  
  try {
    await Promise.all([
      (async () => {
        username.value = await fetchUsername();
      })(),
      loadDependents()
    ]);
  } catch (err) {
    error.value = err;
    console.error('데이터 로딩 실패:', err);
  } finally {
    isLoading.value = false;
  }
};

// 드롭다운이 열릴 때마다 데이터 새로고침
watch(isOpen, async (newValue) => {
  if (newValue) {
    await loadDependents();
  }
});

// ESC 키로 드롭다운 닫기
const handleKeydown = (event) => {
  if (event.key === 'Escape' && isOpen.value) {
    isOpen.value = false;
  }
};

watch(username, async (newValue) => {
  if (newValue === LOGOUT_INDICATOR) {
    try {
      // 토큰 갱신 시도
      await refreshAccessTokenAPI();
    } catch (error) {
      console.error('토큰 갱신 실패, 로그아웃 처리:', error);
      await handleLogout();
    }
  }
});

onMounted(() => {
  loadData();
  window.addEventListener('click', closeModal);
  window.addEventListener('keydown', handleKeydown);
});

onUnmounted(() => {
  window.removeEventListener('click', closeModal);
  window.removeEventListener('keydown', handleKeydown);
});

</script>
