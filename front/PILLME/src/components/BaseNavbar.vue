<template>
  <div ref="dropdownContainer">
    <nav class="flex bg-white py-2 items-center w-full border-t border-gray-200">
      <router-link
        v-for="(item, index) in navItems"
        :key="item.name"
        :to="item.route"
        class="flex flex-col items-center text-gray-700 flex-1"
        @click="handleNavClick(index)"
      >
        <img :src="item.icon" :alt="item.name" :class="index === 2 ? 'w-16 h-16 rounded-full' : 'w-6 h-6'" />
        <span v-if="item.name" class="text-sm whitespace-nowrap">{{ item.name }}</span>
      </router-link>
    </nav>

    <!-- 드롭다운 메뉴 -->
    <div v-if="isDropdownOpen" class="absolute bottom-16 left-0 w-full flex justify-center" @click.self="isDropdownOpen = false">
      <div class="bg-white shadow-lg rounded-xl p-2 flex flex-col w-64 border border-gray-200 transition-all duration-300">
        <button @click="openCamera" class="py-3 text-center text-gray-700 hover:bg-gray-100">📷 처방전 촬영</button>
        <button @click="openGallery" class="py-3 text-center text-gray-700 hover:bg-gray-100">🖼 사진 업로드</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import navHomeIcon from "../assets/navhome.png";
import navCalendarIcon from "../assets/navcalendar.png";
import navChatIcon from "../assets/navchat.png";
import navMypageIcon from "../assets/navmypage.png";
import navPlusIcon from "../assets/navplus.png";

const isDropdownOpen = ref(false);
const router = useRouter();

const navItems = [
  { name: "홈", icon: navHomeIcon, route: "/" },
  { name: "캘린더", icon: navCalendarIcon, route: "/calendar" },
  { name: "", icon: navPlusIcon, route: "" },
  { name: "채팅", icon: navChatIcon, route: "/chat" },
  { name: "마이페이지", icon: navMypageIcon, route: "/mypage" },
];

const handleNavClick = (index) => {
  if (index === 2) {
    isDropdownOpen.value = !isDropdownOpen.value;
  } else {
    isDropdownOpen.value = false;
  }
};

const openCamera = () => {
  isDropdownOpen.value = false;
  router.push("/camera");
};

const openGallery = () => {
  isDropdownOpen.value = false;
  router.push("/imageanalysis");
};
</script>
