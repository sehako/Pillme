<template>
  <nav class="fixed bottom-0 bg-white shadow-md py-2 flex justify-around items-center w-full md:w-1/2 border-t border-gray-200">
    <button 
      v-for="(item, index) in navItems" 
      :key="item.name" 
      class="flex flex-col items-center text-gray-700 flex-1"
    >
      <!-- ✅ 가운데 플러스 버튼 크기만 증가 -->
      <img :src="item.icon" :alt="item.name" :class="index === 2 ? 'w-15 h-15' : 'w-6 h-6'" />
      <span v-if="item.name" class="text-sm whitespace-nowrap">{{ item.name }}</span>
    </button>
  </nav>
</template>

<script setup>
import navHomeIcon from "../assets/navhome.png";
import navCalendarIcon from "../assets/navcalendar.png";
import navChatIcon from "../assets/navchat.png";
import navMypageIcon from "../assets/navmypage.png";
import navPlusIcon from "../assets/navplus.png";

const navItems = [
  { name: "홈", icon: navHomeIcon },
  { name: "캘린더", icon: navCalendarIcon },
  { name: "", icon: navPlusIcon }, // ✅ 플러스 버튼 (가운데)
  { name: "채팅", icon: navChatIcon },
  { name: "마이페이지", icon: navMypageIcon },
];
</script>
