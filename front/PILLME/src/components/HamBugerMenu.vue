<template>
  <div class="relative flex justify-center items-center">
    <!-- 햄버거 버튼 -->
    <button @click.stop="toggleMenu" class="focus:outline-none">
      <img src="../assets/Group 33627.svg" alt="메뉴" class="w-6 h-6">
    </button>

    <!-- 메뉴 (토글) -->
    <transition name="fade">
      <div
        v-if="isOpen"
        ref="menuRef"
        class="absolute -left-4 top-7 bg-white border border-gray-300 rounded-lg shadow-lg w-48"
      >
        <ul class="flex flex-col py-2">
          <li
            v-for="(item, index) in menuItems"
            :key="index"
            class="px-4 py-2 hover:bg-gray-100 cursor-pointer"
            @click="handleNavigation(item)"
          >
            {{ item }}
          </li>
          <li class="border-t border-gray-300 mt-2"></li>
          <li
            @click="!isLoading && handleLogoutEvent()"
            class="px-4 py-2 text-red-500 hover:bg-gray-100 cursor-pointer"
            :class="{ 'opacity-50 cursor-not-allowed': isLoading }"
          >
            {{ isLoading ? "로그아웃 중..." : "로그아웃" }}
          </li>
        </ul>
      </div>
    </transition>

    <!--"복용 기록" 클릭 시 뜨는 모달 -->
    <HistoryModal v-if="showModal" :prescriptions="modalData" @close="handleModalClose" />
      <!--"알림 설정정" 클릭 시 뜨는 모달 -->
      <Teleport to="body">
  <div v-if="isAlarmModalOpen" class="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
    <div class="bg-white p-4 rounded-lg shadow-lg relative w-[500px] h-[600px]">
      <button @click="closeSetAlarmModal" class="absolute top-2 right-2 text-gray-500 hover:text-gray-800">
        ✕
      </button>
      <MyAlarmModal :isModal="true" />
    </div>
  </div>
</Teleport>

  </div>

</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { logoutAPI, handleLogout } from "../api/auth";
import {useFCM} from '../utils/usefcm';
import { usePrescriptionHistory } from "../composables/usePrescriptionHistory"; 
import HistoryModal from "../components/HistoryModal.vue"; // 모달 컴포넌트 추가
import { defineAsyncComponent } from 'vue';
const MyAlarmModal = defineAsyncComponent(() => import('../views/My_Alarm.vue'));

const isOpen = ref(false);
const isLoading = ref(false);
const router = useRouter();
const {deleteTokenFromServer} = useFCM();
const menuRef = ref(null);
//알림모달크기조절
const modalSize = ref("md"); // "sm", "md", "lg"

const modalClass = computed(() => {
  return {
    sm: "w-[300px] h-[400px]",
    md: "w-[500px] h-[600px]",
    lg: "w-[80%] max-w-lg"
  }[modalSize.value];
});

const menuItems = ref([
  "알림 설정",
  "복용 기록",
  "가족 및 관리자",
]);

// ✅ 복용 기록 모달 관리
const { modalData, showModal, fetchPrescriptionHistory } = usePrescriptionHistory();

// ✅ 메뉴 토글
const toggleMenu = () => {
  isOpen.value = !isOpen.value;
};

// ✅ 외부 클릭 감지하여 메뉴 닫기
const handleClickOutside = (event) => {
  if (menuRef.value && !menuRef.value.contains(event.target)) {
    isOpen.value = false;
  }
};

// ✅ 메뉴 항목 클릭 시 기능 실행
const handleNavigation = (item) => {
  if (item === "가족 및 관리자") {
    router.push("/managememberlist"); 
  } else if (item === "복용 기록") {
    isOpen.value = false; // ✅ 메뉴 닫기
    fetchPrescriptionHistory(); // ✅ 모달 열기 위한 데이터 요청
  } else if (item === "알림 설정") {
    openSetAlarmModal();
  } else {
    router.push(`/${item.toLowerCase().replace(/\s/g, "")}`);
  } 
};

// ✅ 알림 설정 모달
const closeSetAlarmModal = () => {
  isAlarmModalOpen.value = false;
};


// ✅ 로그아웃 이벤트
const handleLogoutEvent = async () => {
  isLoading.value = true;

  try {

    // FCM 토큰 삭제 시도 (실패해도 로그아웃은 계속 진행)
    try {
      await deleteTokenFromServer();
    } catch (error) {
      console.error("FCM 토큰 삭제 실패:", error);
    }

    // ✅ 로그아웃 API 요청
    await logoutAPI();
    handleLogout();
    alert("로그아웃 되었습니다.");
  } catch (error) {
    console.error("❌ 로그아웃 오류:", error);
    alert("❌ 로그아웃 실패. 다시 시도해주세요.");
  } finally {
    isLoading.value = false;
  }
};

// ✅ 모달 닫기 함수
const handleModalClose = () => {
  showModal.value = false;
  modalData.value = [];
};

// ✅ 마운트 시 이벤트 리스너 추가
onMounted(() => {
  window.addEventListener("click", handleClickOutside);
});
const isAlarmModalOpen = ref(false);
const openSetAlarmModal = () => {
  isAlarmModalOpen.value = true;
};
// ✅ 언마운트 시 이벤트 리스너 제거
onUnmounted(() => {
  window.removeEventListener("click", handleClickOutside);
});
</script>

<style scoped>
/* ✅ 페이드 애니메이션 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
