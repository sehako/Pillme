<template>
  <div class="relative flex justify-center items-center">
    <!-- 햄버거 버튼 -->
    <button @click.stop="toggleMenu" class="focus:outline-none">
      <img src="../assets/Group 33627.svg" alt="메뉴" class="w-6 h-6">
    </button>

    <!-- 메뉴 (토글) -->
    <transition name="fade">
      <div v-if="isOpen" ref="menuRef" 
           class="absolute -left-4 top-7 bg-white border border-gray-300 rounded-lg shadow-lg w-48">
        <ul class="flex flex-col py-2">
          <li v-for="(item, index) in menuItems" :key="index" 
              class="px-4 py-2 hover:bg-gray-100 cursor-pointer">
            {{ item }}
          </li>
          <li class="border-t border-gray-300 mt-2"></li>
          <li @click="handleLogout" class="px-4 py-2 text-red-500 hover:bg-gray-100 cursor-pointer">
            로그아웃
          </li>
        </ul>
      </div>
    </transition>
  </div>
</template>



<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

const isOpen = ref(false);
const menuItems = ref([
  "알림 설정",
  "복용 기록",
  "약물 관리",
  "가족 및 관리자",
  "커뮤니티 활동"
]);

const menuRef = ref(null);

// 메뉴 토글
const toggleMenu = () => {
  isOpen.value = !isOpen.value;
};

// 외부 클릭 감지하여 메뉴 닫기
const handleClickOutside = (event) => {
  if (menuRef.value && !menuRef.value.contains(event.target)) {
    isOpen.value = false;
  }
};

// 로그아웃 이벤트 (추후 실제 로그아웃 로직 연결 가능)
const handleLogout = () => {
  console.log("로그아웃 실행");
  isOpen.value = false;
};

onMounted(() => {
  window.addEventListener("click", handleClickOutside);
});

onUnmounted(() => {
  window.removeEventListener("click", handleClickOutside);
});
</script>

<style scoped>
/* 페이드 애니메이션 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
