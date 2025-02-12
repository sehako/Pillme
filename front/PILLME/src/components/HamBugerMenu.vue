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
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { logoutAPI, handleLogout as clearTokens } from "../api/auth";
import { useRouter } from 'vue-router';
const router = useRouter();
const isOpen = ref(false);
const isLoading = ref(false); // ✅ isLoading 추가

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

// 로그아웃 이벤트
const handleLogoutEvent = async () => {
  console.log("로그아웃 실행");
  isLoading.value = true;

  try {
    // ✅ 로그아웃 API 요청 (api/auth의 logoutAPI 사용)
    await logoutAPI();

    // ✅ 토큰 삭제 (api/auth의 handleLogout 함수 호출)
    clearTokens();

    // ✅ 세션 스토리지 삭제
    sessionStorage.clear();

    // ✅ 브라우저 히스토리 초기화 (뒤로 가기 방지)
    history.pushState(null, "", "/");
    window.addEventListener("popstate", () => {
      history.pushState(null, "", "/");
    });

    alert("로그아웃 성공!");

    // ✅ 로그인 페이지로 이동 + 히스토리 덮어쓰기 (뒤로 가기 차단)
    router.replace("/start");
  } catch (error) {
    console.error("로그아웃 오류:", error);
    alert("로그아웃 실패. 다시 시도해주세요.");
  } finally {
    isLoading.value = false;
  }
};


// 마운트 시 이벤트 리스너 추가
onMounted(() => {
  window.addEventListener("click", handleClickOutside);
});

// 언마운트 시 이벤트 리스너 제거
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
