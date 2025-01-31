<template>
  <div ref="dropdownContainer">
    <!-- ✅ 네비게이션 바 유지 -->
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

    <!-- 📌 드롭다운 메뉴 -->
    <div v-if="isDropdownOpen" class="absolute bottom-16 left-0 w-full flex justify-center" @click.self="isDropdownOpen = false">
      <div class="bg-white shadow-lg rounded-xl p-2 flex flex-col w-64 border border-gray-200 transition-all duration-300">
        <button @click="openCamera" class="py-3 text-center text-gray-700 hover:bg-gray-100">📷 처방전 촬영</button>
        <button @click="openGallery" class="py-3 text-center text-gray-700 hover:bg-gray-100">🖼 사진 업로드</button>
      </div>
    </div>
  </div>  

  <!-- 📌 카메라 모달 (전체 화면 확장) -->
  <div v-if="isCameraOpen" class="fixed inset-0 flex flex-col items-center justify-center bg-black transition-all duration-500" :class="isFullscreen ? 'scale-100 opacity-100' : 'scale-0 opacity-0'">
    <video ref="videoElement" class="camera-view" autoplay></video>

    <!-- 📌 네모난 가이드 박스 -->
    <div v-if="isFullscreen" class="overlay">
      <div class="guide-box"></div>
    </div>

    <!-- 📌 버튼 오버레이 (가이드 침범 X) -->
    <div class="camera-controls">
      <button @click="closeCamera" class="control-btn bg-gray-500">✖ 닫기</button>
      <button @click="takePhoto" class="capture-btn">📸 촬영</button>
    </div>
  </div>

  <!-- 📌 캡처된 이미지 미리보기 -->
  <div v-if="capturedImage" class="fixed inset-0 flex flex-col items-center bg-black bg-opacity-70">
    <div class="bg-white p-6 rounded-lg flex flex-col items-center max-w-sm w-full">
      <img :src="capturedImage" alt="Captured Prescription" class="captured-photo" />
      <div class="preview-buttons">
        <button @click="confirmPhoto" class="confirm-btn">✔ 확인</button>
        <button @click="closeCapturedImage" class="cancel-btn">❌ 다시 찍기</button>
      </div>
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
const isFullscreen = ref(false); // ✅ 전체 화면 효과 추가
const videoElement = ref(null);
const streamRef = ref(null);
const capturedImage = ref(null);

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

// ✅ 기존 카메라 스트림 정리
const stopStream = () => {
  if (streamRef.value) {
    streamRef.value.getTracks().forEach((track) => track.stop());
    streamRef.value = null;
  }
};

// ✅ 후면 카메라 기본 사용 + 광각 방지
const openCamera = async () => {
  stopStream();
  isCameraOpen.value = true;
  isFullscreen.value = false;

  setTimeout(() => {
    isFullscreen.value = true; // ✅ 0.3초 후 전체 화면 효과
  }, 300);

  try {
    const constraints = {
      video: {
        facingMode: { exact: "environment" },
        width: { ideal: 1280 },
        height: { ideal: 720 },
      },
    };

    const stream = await navigator.mediaDevices.getUserMedia(constraints);
    streamRef.value = stream;
    videoElement.value.srcObject = stream;
  } catch (error) {
    console.warn("카메라 접근 실패:", error);
    alert("후면 카메라를 사용할 수 없습니다.");
    closeCamera();
  }
};

// ✅ 📸 처방전 촬영
const takePhoto = () => {
  if (!videoElement.value) return;

  const canvas = document.createElement("canvas");
  const context = canvas.getContext("2d");

  canvas.width = videoElement.value.videoWidth;
  canvas.height = videoElement.value.videoHeight;
  
  context.drawImage(videoElement.value, 0, 0, canvas.width, canvas.height);

  capturedImage.value = canvas.toDataURL("image/png");
  closeCamera();
};

// ✅ 카메라 닫기
const closeCamera = () => {
  stopStream();
  videoElement.value.srcObject = null;
  isFullscreen.value = false;
  setTimeout(() => {
    isCameraOpen.value = false;
  }, 300);
};

// ✅ 촬영한 처방전 확인
const confirmPhoto = () => {
  alert("📄 처방전이 확인되었습니다.");
  closeCapturedImage();
};

// ✅ 캡처된 이미지 닫기
const closeCapturedImage = () => {
  capturedImage.value = null;
};

// ✅ 갤러리에서 사진 업로드
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
</script>
<style scoped>
/* ✅ 카메라 전체 화면 변환 효과 */
.transition-all {
  transition: transform 0.3s ease-in-out, opacity 0.3s ease-in-out;
}
.scale-0 {
  transform: scale(0);
  opacity: 0;
}
.scale-100 {
  transform: scale(1);
  opacity: 1;
}

/* 📌 카메라 위에 네모 가이드 박스를 오버레이 */
.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  pointer-events: none; /* 터치 불가능하게 */
}

/* 📌 네모난 가이드 박스 */
.guide-box {
  width: 80%;
  height: 50%;
  border: 4px solid rgba(255, 255, 255, 0.9);
  border-radius: 10px;
  position: relative;
  z-index: 10;
}

/* 📌 버튼 오버레이 */
.camera-controls {
  position: absolute;
  bottom: 5%;
  display: flex;
  gap: 20px;
  z-index: 15; /* ✅ 버튼이 가이드 위로 올라가지 않게 유지 */
}

.control-btn, .capture-btn {
  padding: 16px 32px;
  border-radius: 50px;
  font-size: 20px;
  color: white;
}

.control-btn { background: gray; }
.capture-btn { background: red; }

/* 📌 미리보기 버튼 크기 확대 */
.preview-buttons {
  display: flex;
  gap: 20px;
  margin-top: 20px;
}

.confirm-btn, .cancel-btn {
  padding: 16px 32px;
  font-size: 20px;
  border-radius: 12px;
  color: white;
  width: 120px;
}

.confirm-btn { background: green; }
.cancel-btn { background: red; }

</style>