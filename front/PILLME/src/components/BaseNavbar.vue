<template>
  <div ref="dropdownContainer">
    <nav class="fixed bottom-0 bg-white py-2 flex justify-around items-center w-full md:w-1/2 border-t border-gray-200">
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

    <!-- 📌 드롭다운 메뉴 (플러스 버튼 클릭 시 나타남) -->
    <div v-if="isDropdownOpen" class="fixed bottom-16 left-0 w-full flex justify-center" @click.self="isDropdownOpen = false">
      <div class="bg-white shadow-lg rounded-xl p-2 flex flex-col w-64 border border-gray-200 transition-all duration-300 translate-y-0">
        <button @click="openCamera" class="py-3 text-center text-gray-700 hover:bg-gray-100">📷 카메라 촬영</button>
        <button @click="openGallery" class="py-3 text-center text-gray-700 hover:bg-gray-100">🖼 사진 업로드</button>
        <button @click="selectOption('extra')" class="py-3 text-center text-gray-700 hover:bg-gray-100">➕ 추가 예정</button>
      </div>
    </div>
  </div>

  <!-- 카메라 모달 -->
  <div v-if="isCameraOpen" class="fixed inset-0 flex justify-center items-center bg-black bg-opacity-70">
    <div class="bg-white p-4 rounded-lg flex flex-col items-center">
      <video ref="videoElement" class="w-full h-auto" autoplay></video>
      <button @click="takePhoto" class="mt-4 bg-[#4E7351] !hover:bg-[#3D5A3F] text-white px-4 py-2 rounded-full">📸 사진 찍기</button>
      <button @click="closeCamera" class="mt-2 bg-gray-400 text-white px-4 py-2 rounded-full">닫기</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";

import navHomeIcon from "../assets/navhome.png";
import navCalendarIcon from "../assets/navcalendar.png";
import navChatIcon from "../assets/navchat.png";
import navMypageIcon from "../assets/navmypage.png";
import navPlusIcon from "../assets/navplus.png";

const isDropdownOpen = ref(false);
const isCameraOpen = ref(false);
const videoElement = ref(null);
const navItems = [
  { name: "홈", icon: navHomeIcon, route: "/start" },
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

const selectOption = (option) => {
  console.log("선택한 옵션:", option);
  isDropdownOpen.value = false;
};

const openCamera = async () => {
  isCameraOpen.value = true;
  isDropdownOpen.value = false;

  if (navigator.mediaDevices?.getUserMedia) {
    try {
      const constraints = {
        video: { facingMode: "environment" }, // 후면 카메라 우선 적용, 지원되지 않으면 기본 카메라 사용
      };
      const stream = await navigator.mediaDevices.getUserMedia(constraints);
      videoElement.value.srcObject = stream;
    } catch (error) {
      console.warn("카메라 접근 오류 또는 후면 카메라 미지원:", error);
      alert("후면 카메라를 사용할 수 없습니다. 기본 카메라로 전환합니다.");
      try {
        const fallbackStream = await navigator.mediaDevices.getUserMedia({ video: true });
        videoElement.value.srcObject = fallbackStream;
      } catch (fallbackError) {
        alert("카메라 접근이 거부되었습니다.");
        closeCamera();
      }
    }
  }
};

const takePhoto = () => {
  alert("📸 사진 촬영 기능 추가 예정!");
};

const closeCamera = () => {
  if (videoElement.value?.srcObject) {
    let tracks = videoElement.value.srcObject.getTracks();
    tracks.forEach(track => track.stop());
  }
  isCameraOpen.value = false;
};

const openGallery = () => {
  isDropdownOpen.value = false;
  const input = document.createElement("input");
  input.type = "file";
  input.accept = "image/*";
  input.click();
  input.onchange = (event) => {
    const file = event.target.files[0];
    if (file) {
      alert("🖼 선택한 파일: " + file.name);
    }
  };
};

const dropdownContainer = ref(null);
const closeDropdownOnClickOutside = (event) => {
  if (isDropdownOpen.value && dropdownContainer.value && !dropdownContainer.value.contains(event.target)) {
    isDropdownOpen.value = false;
  }
};

onMounted(() => {
  document.addEventListener("click", closeDropdownOnClickOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener("click", closeDropdownOnClickOutside);
});
</script>
