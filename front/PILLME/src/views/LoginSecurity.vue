<template>
  <div class="flex flex-col h-screen p-6">
    <!-- ✅ 뒤로 가기 버튼 -->
    <BackButton class="mb-4" />

    <!-- ✅ 페이지 타이틀 -->
    <h1 class="text-xl font-bold">로그인 및 보안</h1>

    <!-- ✅ 추가적인 보안 정보 -->
    <div class="mt-4 p-4 border rounded bg-gray-100">
      <h2 class="text-lg font-semibold mb-2">보안 정보</h2>
      <p><strong>마지막 로그인:</strong> {{ lastLogin }}</p>
      <p>
        <strong>현재 로그인 IP:</strong> {{ currentIP }} 
        <span v-if="isSuspiciousLogin" class="text-red-500 font-semibold">⚠️ 의심스러운 로그인 감지</span>
      </p>
    </div>

    <!-- ✅ 로그인 기록 -->
    <div class="mt-6">
      <h2 class="text-lg font-semibold mb-3">최근 로그인 기록</h2>

      <!-- 로그인 기록 리스트 -->
      <div v-for="(log, index) in loginHistory" :key="index" class="border-b py-2 text-sm">
        <p><strong>IP:</strong> {{ log.ip }}</p>
        <p><strong>날짜:</strong> {{ log.date }}</p>
        <p><strong>기기:</strong> {{ log.device }} ({{ log.os }})</p>
      </div>

      <!-- ✅ 로그인 기록 삭제 버튼 -->
      <button 
        @click="deleteLoginHistory"
        class="mt-4 px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 transition"
      >
        로그인 기록 삭제
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import BackButton from '../components/BackButton.vue';

// ✅ 로그인 기록 및 보안 정보 상태
const lastLogin = ref(""); // 마지막 로그인 날짜
const currentIP = ref(""); // 현재 로그인 IP
const loginHistory = ref([]); // 로그인 기록 리스트
const isSuspiciousLogin = ref(false); // 의심스러운 로그인 감지 여부

// ✅ 로그인 기록 불러오기 (백엔드 연동 필요)
const fetchLoginHistory = async () => {
  try {
    // TODO: 백엔드에서 로그인 기록 불러오기 (`GET /api/login-history`)
    // 백엔드: 현재 로그인한 사용자의 최근 로그인 기록 반환 (IP, 날짜, 기기명, OS 등)
    
    // 더미 데이터 (백엔드 연동 후 삭제 필요)
    loginHistory.value = [
      { ip: "192.168.1.1", date: "2024-02-01 14:30", device: "Chrome", os: "Windows" },
      { ip: "203.0.113.45", date: "2024-01-30 09:15", device: "Safari", os: "iPhone" }
    ];
  } catch (error) {
    console.error("로그인 기록 불러오기 실패:", error);
  }
};

// ✅ 보안 정보 불러오기 (백엔드 연동 필요)
const fetchSecurityInfo = async () => {
  try {
    // TODO: 백엔드에서 보안 정보 불러오기 (`GET /api/user-security-info`)
    // 백엔드: 사용자의 마지막 로그인 날짜 및 현재 로그인 IP 반환
    
    // 더미 데이터 (백엔드 연동 후 삭제 필요)
    lastLogin.value = "2024-02-01 14:30";
    currentIP.value = "203.0.113.45"; 

    // ✅ 현재 로그인 IP가 기존 기록에 없으면 비정상 로그인 감지
    isSuspiciousLogin.value = !loginHistory.value.some(log => log.ip === currentIP.value);
  } catch (error) {
    console.error("보안 정보 불러오기 실패:", error);
  }
};

// ✅ 로그인 기록 삭제 기능 (백엔드 연동 필요)
const deleteLoginHistory = async () => {
  if (!confirm("로그인 기록을 삭제하시겠습니까?")) return;
  
  try {
    // TODO: 백엔드에서 로그인 기록 삭제 (`DELETE /api/login-history`)
    // 백엔드: 현재 로그인한 사용자의 로그인 기록을 DB에서 삭제
    
    loginHistory.value = []; // 프론트엔드에서 삭제 (백엔드 연동 후 자동 갱신될 수도 있음)
    alert("로그인 기록이 삭제되었습니다.");
  } catch (error) {
    console.error("로그인 기록 삭제 실패:", error);
  }
};

// ✅ 페이지 로딩 시 보안 정보 및 로그인 기록 불러오기
onMounted(() => {
  fetchLoginHistory();
  fetchSecurityInfo();
});
</script>
