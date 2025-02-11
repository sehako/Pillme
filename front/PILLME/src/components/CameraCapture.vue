<template>
  <div class="camera-container relative h-screen-custom flex flex-col items-center justify-center bg-black">
    <!-- 📌 카메라 화면 -->
    <video ref="videoElement" class="camera-view w-full max-h-full object-cover" autoplay></video>

    <!-- 📌 사용자 안내 메시지 -->
    <div class="absolute top-4 z-50 text-white text-center bg-black bg-opacity-50 px-4 py-2 rounded-lg">
      📢 약 이름이 흰색 네모에 다 들어오도록 맞춰주세요!
    </div>

    <!-- 📌 카메라 전환 버튼 -->
    <button @click="toggleCamera" class="switch-btn absolute top-6 right-6 z-50">
      🔄 카메라 전환
    </button>
    
    <!-- 📌 네모 가이드 박스 -->
    <div class="overlay">
      <div class="guide-box" :style="{ width: guideBoxWidth, height: guideBoxHeight }"></div>
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
import { ref, onMounted, onBeforeUnmount, nextTick } from "vue";
import { useRouter } from "vue-router";

const videoElement = ref(null);
const streamRef = ref(null);
const capturedImage = ref(null);
const isFrontCamera = ref(false);
const router = useRouter();
const guideBoxWidth = ref("0px");
const guideBoxHeight = ref("0px");

// ✅ 가이드 박스 크기 업데이트 함수
const updateGuideBoxSize = () => {
  if (!videoElement.value) return;

  const rect = videoElement.value.getBoundingClientRect(); // 📌 비디오의 실제 화면 크기 가져오기
  guideBoxWidth.value = `${rect.width * 0.7}px`;  // 가이드 박스 크기 (비디오 크기의 70%) 가로
  guideBoxHeight.value = `${rect.height * 0.7}px`; // 가이드 박스 크기 (비디오 크기의 70%) 세로
};

// ✅ 비디오 로드 후 가이드 박스 업데이트
const onVideoLoaded = () => {
  nextTick(() => {
    updateGuideBoxSize();
  });
};

// ✅ 카메라 열기
const openCamera = async () => {
  stopStream();

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

    // ✅ 비디오가 로드된 후 가이드 박스 크기 업데이트
    videoElement.value.addEventListener("loadedmetadata", updateGuideBoxSize);
  } catch (error) {
    alert("카메라를 사용할 수 없습니다.");
    closeCamera();
  }
};

// ✅ 스트림 중지
const stopStream = () => {
  if (streamRef.value) {
    streamRef.value.getTracks().forEach(track => track.stop());
    streamRef.value = null;
  }
};

// ✅ 카메라 전환
const toggleCamera = () => {
  isFrontCamera.value = !isFrontCamera.value;
  openCamera();
};

// ✅ 사진 촬영
const takePhoto = () => {
  if (!videoElement.value) return;

  const rect = videoElement.value.getBoundingClientRect(); // 📌 비디오의 실제 크기 가져오기
  const videoWidth = videoElement.value.videoWidth;
  const videoHeight = videoElement.value.videoHeight;

  // ✅ 실제 비디오 크기 대비 화면 크기 비율 계산
  const scaleX = videoWidth / rect.width;
  const scaleY = videoHeight / rect.height;

  // ✅ 가이드 박스 크기 계산 (현재 화면 크기 기준)
  const guideWidth = rect.width * 0.7;
  const guideHeight = rect.height * 0.7;

  // ✅ 가이드 박스 위치 계산 (비디오 내에서의 좌표)
  const guideX = (rect.width - guideWidth) / 2;
  const guideY = (rect.height - guideHeight) / 2;

  // ✅ 원본 비디오 크기로 변환 (비율 적용)
  const cropX = guideX * scaleX;
  const cropY = guideY * scaleY;
  const cropWidth = guideWidth * scaleX;
  const cropHeight = guideHeight * scaleY;

  // ✅ 캔버스 생성 및 크롭 영역 저장
  const canvas = document.createElement("canvas");
  canvas.width = cropWidth;
  canvas.height = cropHeight;
  const context = canvas.getContext("2d");

  context.drawImage(
    videoElement.value,
    cropX, cropY, cropWidth, cropHeight, // 비디오에서 가져올 영역
    0, 0, cropWidth, cropHeight // 캔버스에 복사할 크기
  );

  capturedImage.value = canvas.toDataURL("image/png");
};

// ✅ "확인" 버튼 → /imageanalysis 이동
const confirmPhoto = () => {
  if (!capturedImage.value) return;
  router.push({ path: "/imageanalysis", query: { image: encodeURIComponent(capturedImage.value) } });
};

// ✅ "다시 찍기" 버튼을 누르면 미리 보기 제거
const closeCapturedImage = () => {
  capturedImage.value = null;
};

// ✅ 카메라 닫기 (홈 화면으로 이동)
const closeCamera = () => {
  stopStream();
  router.push("/");
};

// ✅ 창 크기 변경 시 가이드 박스 크기 조정
onMounted(() => {
  openCamera();
  window.addEventListener("resize", updateGuideBoxSize);
});

onBeforeUnmount(() => {
  stopStream();
  window.removeEventListener("resize", updateGuideBoxSize);
});
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
  border: 4px solid rgba(255, 255, 255, 0.9);
  border-radius: 10px;
  position: absolute;
  z-index: 10;
  box-sizing: border-box;
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
}

.switch-btn:hover {
  background: rgba(255, 255, 255, 0.8);
  color: black;
}
</style>
