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

    <div v-if="isDropdownOpen" class="absolute bottom-16 left-0 w-full flex justify-center" @click.self="isDropdownOpen = false">
      <div class="bg-white shadow-lg rounded-xl p-2 flex flex-col w-64 border border-gray-200 transition-all duration-300">
        <button @click="openCamera" class="py-3 text-center text-gray-700 hover:bg-gray-100">📷 처방전 촬영</button>
        <button @click="triggerFileInput" class="py-3 text-center text-gray-700 hover:bg-gray-100">🖼 사진 업로드</button>
        <input type="file" ref="fileInputRef" @change="handleFileChange" accept="image/*" class="hidden"/>
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
const fileInputRef = ref(null);

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

const triggerFileInput = () => {
  isDropdownOpen.value = false;
  fileInputRef.value.click();
};


const handleFileChange = (event) => {
  const file = event.target.files[0];
  if (!file) {
    console.warn("파일이 선택되지 않았습니다.");
    return;
  }

  const reader = new FileReader();
  reader.onload = (e) => {
    const base64Image = e.target.result; // ✅ Base64 데이터
    console.log("📸 업로드된 이미지 Base64:", base64Image);

    // ✅ URL 인코딩 적용하여 전송
    router.push({
      path: "/imageanalysis",
      query: {
        image: encodeURIComponent(base64Image), // Base64를 URL-safe하게 변환
        filename: file.name
      },
    });
  };
  reader.readAsDataURL(file); // ✅ Base64 변환 실행
};

</script>
