<template>
  <div class="flex flex-col h-screen p-6">
    <!-- ✅ 뒤로 가기 버튼 -->
    <BackButton class="mb-4" />

    <!-- ✅ 페이지 타이틀 -->
    <h1 class="text-xl font-bold mb-4">비밀번호 변경</h1>

    <!-- ✅ 비밀번호 입력 폼 -->
    <div class="space-y-4 w-full max-w-md">
      <div>
        <label class="block text-gray-600">현재 비밀번호</label>
        <input 
          v-model="passwords.current" 
          type="password" 
          placeholder="현재 비밀번호" 
          class="border border-gray-300 rounded w-full p-2 outline-none focus:ring-2 focus:ring-[#3D5A3F]"
        />
      </div>

      <div>
        <label class="block text-gray-600">새 비밀번호</label>
        <input 
          v-model="passwords.new" 
          type="password" 
          placeholder="새 비밀번호" 
          class="border border-gray-300 rounded w-full p-2 outline-none focus:ring-2 focus:ring-[#3D5A3F]"
        />
      </div>

      <div>
        <label class="block text-gray-600">새 비밀번호 확인</label>
        <input 
          v-model="passwords.confirm" 
          type="password" 
          placeholder="새 비밀번호 확인" 
          class="border border-gray-300 rounded w-full p-2 outline-none focus:ring-2 focus:ring-[#3D5A3F]"
        />
      </div>

      <!-- ✅ 비밀번호 변경 버튼 -->
      <button 
        @click="changePassword"
        class="w-full px-4 py-2 bg-[#4E7351] text-white rounded hover:bg-[#3D5A3F] transition"
      >
        변경하기
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import BackButton from '../components/BackButton.vue';

// ✅ 비밀번호 상태 관리
const passwords = ref({
  current: "",
  new: "",
  confirm: ""
});

// ✅ 비밀번호 변경 로직
const changePassword = async () => {
  if (!passwords.value.current || !passwords.value.new || !passwords.value.confirm) {
    alert("모든 필드를 입력해주세요.");
    return;
  }

  if (passwords.value.new !== passwords.value.confirm) {
    alert("새 비밀번호가 일치하지 않습니다.");
    return;
  }

  // ✅ TODO: 백엔드 API 요청 (현재는 콘솔 출력)
  console.log("비밀번호 변경 요청:", passwords.value);

  alert("비밀번호가 변경되었습니다.");
};
</script>
