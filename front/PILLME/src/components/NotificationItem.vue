<template>
    <div 
      ref="dropdownContainer"
      :class="['flex items-center p-4 shadow-md rounded-lg w-full', isRead ? 'bg-white' : 'bg-[#EDF3FF]']"
      @click.self="closeMenu"
    >
       
       <BaseLogo :src="logoSrc" size="xxs" class="flex-shrink-0" />
 
       <div class="ml-4 flex-1">
         <p class="text-sm font-semibold">{{ title }}</p>
         <p class="text-xs text-gray-500 mt-1">{{ message }}</p>
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
           <button @click="handleAction('삭제')" class="block w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-200 text-left">
             삭제
           </button>
           <button @click="handleAction('알림 읽음')" class="block w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-200 text-left">
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
 
 defineProps({
   title: String,
   message: String,
   date: String,
   isRead: Boolean,
 });
 
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
 
 // ✅ 이벤트 리스너 등록/해제
 onMounted(() => {
   document.addEventListener("click", handleClickOutside);
 });
 
 onBeforeUnmount(() => {
   document.removeEventListener("click", handleClickOutside);
 });
 </script>
 
 <style scoped>
 .absolute div {
   transition: all 0.2s ease-in-out;
 }
 </style>
 