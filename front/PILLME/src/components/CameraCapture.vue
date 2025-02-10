<template>
  <div class="camera-container relative h-screen-custom flex flex-col items-center justify-center bg-black">
    <!-- 📌 카메라 화면 -->
    <video ref="videoElement" class="camera-view w-full max-h-full object-cover" autoplay></video>

    <!-- 📌 사용자 안내 메시지 -->
    <div class="absolute top-4 text-white text-center bg-black bg-opacity-50 px-4 py-2 rounded-lg">
      📢 약 이름이 흰색 네모에 다 들어오도록 맞춰주세요!
    </div>

    <!-- 📌 카메라 전환 버튼 -->
    <button @click="toggleCamera" class="switch-btn absolute top-6 right-6 z-50">
      🔄 카메라 전환
    </button>

    <!-- 📌 네모 가이드 박스 -->
    <div v-if="isFullscreen" class="overlay">
      <div class="guide-box"></div>
    </div>

    <!-- 📌 버튼 오버레이 -->
    <div class="absolute bottom-20 z-50 flex gap-4">
      <button @click="closeCamera" class="control-btn bg-gray-500">✖ 닫기</button>
      <button @click="takePhoto" class="capture-btn">📸 촬영</button>
    </div>

    <!-- 📌 캡처된 이미지 미리보기 -->
    <div v-if="capturedImage" class="absolute inset-0 flex flex-col items-center justify-center bg-black bg-opacity-80">
      <div class="bg-white p-6 rounded-lg flex flex-col items-center max-w-sm w-full">
        <img :src="capturedImage" alt="Captured Prescription" class="captured-photo" />
        <div class="preview-buttons flex gap-4 mt-4">
          <button @click="confirmPhoto" class="confirm-btn">✔ 확인</button>
          <button @click="closeCapturedImage" class="cancel-btn">❌ 다시 찍기</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onBeforeUnmount } from "vue";
import { useRouter } from "vue-router";

const isFullscreen = ref(false);
const videoElement = ref(null);
const streamRef = ref(null);
const capturedImage = ref(null);
const isFrontCamera = ref(false);
const router = useRouter();

const stopStream = () => {
  if (streamRef.value) {
    streamRef.value.getTracks().forEach(track => track.stop());
    streamRef.value = null;
  }
};

const openCamera = async () => {
  stopStream();
  isFullscreen.value = false;

  setTimeout(() => {
    isFullscreen.value = true;
  }, 300);

  try {
    const constraints = {
      video: {
        facingMode: isFrontCamera.value ? "user" : "environment",
        width: { ideal: 1280 },
        height: { ideal: 720 },
      },
    };

    const stream = await navigator.mediaDevices.getUserMedia(constraints);
    streamRef.value = stream;
    videoElement.value.srcObject = stream;
  } catch (error) {
    alert("카메라를 사용할 수 없습니다.");
    closeCamera();
  }
};

const toggleCamera = () => {
  isFrontCamera.value = !isFrontCamera.value;
  openCamera();
};

const takePhoto = () => {
if (!videoElement.value) return;

const videoWidth = videoElement.value.videoWidth;
const videoHeight = videoElement.value.videoHeight;

// ✅ 가이드 박스 크기 계산 (네모 박스 비율)
const guideBoxWidthRatio = 0.7; // 가로 70%
const guideBoxHeightRatio = 0.5; // 세로 50%

const guideWidth = Math.floor(videoWidth * guideBoxWidthRatio);
const guideHeight = Math.floor(videoHeight * guideBoxHeightRatio);

// ✅ 중앙 정렬을 위한 X, Y 좌표 계산
const guideX = Math.floor((videoWidth - guideWidth) / 2);
const guideY = Math.floor((videoHeight - guideHeight) / 2);

// ✅ 캔버스 생성 (가이드 박스 크기와 동일)
const canvas = document.createElement("canvas");
canvas.width = guideWidth;
canvas.height = guideHeight;
const context = canvas.getContext("2d");

// ✅ 가이드 박스 영역만 정확히 크롭하여 캡처
context.drawImage(
  videoElement.value,
  guideX, guideY, guideWidth, guideHeight, // 원본 비디오에서 가져올 영역
  0, 0, guideWidth, guideHeight           // 캔버스에 복사할 크기
);

capturedImage.value = canvas.toDataURL("image/png");
};

// ✅ "확인" 버튼을 누르면 /imageanalysis로 이동
const confirmPhoto = () => {
  if (!capturedImage.value) return;

  router.push({
    path: "/imageanalysis",
    query: { image: encodeURIComponent(capturedImage.value) },
  });
};

// ✅ "다시 찍기" 버튼을 누르면 미리 보기 제거
const closeCapturedImage = () => {
  capturedImage.value = null;
};

// ✅ 카메라 닫기 (홈 화면으로 이동)
const closeCamera = () => {
  stopStream();
  isFullscreen.value = false;
  setTimeout(() => {
    router.push("/");
  }, 300);
};

onBeforeUnmount(() => {
  stopStream();
});

openCamera();
</script>

<style scoped>
/* 📢 사용자 안내 메시지 */
.text-white {
  font-size: 16px;
  font-weight: bold;
}

/* 📌 네모난 가이드 박스 */
.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  pointer-events: none;
}

.guide-box {
  width: 70%;
  height: 50%;
  border: 4px solid rgba(255, 255, 255, 0.9);
  border-radius: 10px;
  position: relative;
  z-index: 10;
}

/* 📌 버튼 스타일 */
.control-btn, .capture-btn, .switch-btn {
  padding: 14px 28px;
  border-radius: 50px;
  font-size: 18px;
  color: white;
  cursor: pointer;
}

.control-btn { background: gray; }
.capture-btn { background: red; }

/* 📌 전환 버튼 스타일 */
.switch-btn {
  background: rgba(0, 0, 0, 0.6);
  padding: 12px 20px;
  border-radius: 20px;
  font-size: 14px;
  color: white;
  font-weight: bold;
  border: 2px solid white;
  transition: background 0.2s ease-in-out;
}

.switch-btn:hover {
  background: rgba(255, 255, 255, 0.8);
  color: black;
}
</style>
