<template>
  <div 
    ref="dropdownContainer"
    :class="['flex items-center p-4 shadow-md rounded-lg w-full', confirm ? 'bg-white' : 'bg-[#EDF3FF]']"
    @click.self="closeMenu"
  >
     
     <BaseLogo :src="logoSrc" size="xxs" class="flex-shrink-0" />

     <div class="ml-4 flex-1">
       <p class="text-sm font-semibold">{{ title }}</p>
       <p class="text-xs text-gray-400 mt-1">{{ date }}</p>
     </div>
 
     <div class="relative">
       <button @click="toggleMenu" class="text-gray-700 hover:text-gray-900 focus:outline-none">
         <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 24 24" class="w-6 h-6">
           <circle cx="12" cy="5" r="2"></circle>
           <circle cx="12" cy="12" r="2"></circle>
           <circle cx="12" cy="19" r="2"></circle>
         </svg>
       </button>

       <!-- ✅ 드롭다운 메뉴 -->
       <div
         v-if="menuOpen"
         class="absolute right-0 top-8 bg-white shadow-lg rounded-lg w-28 z-50 border border-gray-200"
       >
         <button @click="handleAction('delete')" class="block w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-200 text-left">
           삭제
         </button>
         <button @click="handleAction('markAsRead')" class="block w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-200 text-left">
           알림 읽음
         </button>
       </div>
     </div>
 
   </div>
</template>
 
<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import BaseLogo from "../components/BaseLogo.vue";
import logoSrc from "../assets/logi_nofont.svg";

const props = defineProps({
  title: String,
  date: String,
  confirm: Boolean,
  notificationId: Number, // ✅ 삭제할 때 필요한 notificationId 추가
});

const emit = defineEmits(["deleteNotification", "markAsRead"]);

const menuOpen = ref(false);
const dropdownContainer = ref(null);

// ✅ 메뉴 열기/닫기
const toggleMenu = () => {
  menuOpen.value = !menuOpen.value;
};

// ✅ 외부 클릭 감지 (메뉴 닫기)
const handleClickOutside = (event) => {
  if (dropdownContainer.value && !dropdownContainer.value.contains(event.target)) {
    menuOpen.value = false;
  }
};

// ✅ 메뉴 닫기
const closeMenu = () => {
  menuOpen.value = false;
};

// ✅ 삭제 및 알림 읽음 이벤트 처리
const handleAction = (action) => {
  if (action === "delete") {
    emit("deleteNotification", props.notificationId);
  } else if (action === "markAsRead") {
    emit("markAsRead", props.notificationId);
  }
  closeMenu();
};

// ✅ 이벤트 리스너 등록/해제
onMounted(() => {
  document.addEventListener("click", handleClickOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener("click", handleClickOutside);
});
</script>
