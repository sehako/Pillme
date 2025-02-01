<template>
  <div class="flex flex-col h-screen p-6">
    <!-- ✅ 뒤로 가기 버튼 -->
    <BackButton class="mb-4" />

    <!-- ✅ 기본 정보 표시 -->
    <h1 class="text-xl font-bold mb-4">개인정보 관리</h1>

    <!-- ✅ 리스트 형식의 정보 -->
    <div class="grid grid-cols-2 gap-y-6 text-lg">
      <!-- ✅ 이메일 (모달 수정) -->
      <p class="text-gray-600">이메일</p>
      <div class="flex justify-between items-center w-full border-b border-gray-300 pb-1">
        <span class="font-medium">{{ userInfo.email }}</span>
        <button 
          @click="openModal('email')" 
          class="text-[#3D5A3F] hover:underline text-sm px-3 py-1"
        >
          변경하기
        </button>
      </div>

      <!-- ✅ 전화번호 (모달 수정) -->
      <p class="text-gray-600">전화번호</p>
      <div class="flex justify-between items-center w-full border-b border-gray-300 pb-1">
        <span class="font-medium">{{ userInfo.phone }}</span>
        <button 
          @click="openModal('phone')" 
          class="text-[#3D5A3F] hover:underline text-sm px-3 py-1"
        >
          변경하기
        </button>
      </div>

      <!-- ✅ 성별 (변경 불가) -->
      <p class="text-gray-600">성별</p>
      <input 
        v-model="userInfo.gender"
        type="text"
        readonly
        class="border-b border-gray-300 outline-none px-1 py-1 text-gray-500 bg-gray-100 cursor-not-allowed"
      />

      <!-- ✅ 생년월일 (변경 불가) -->
      <p class="text-gray-600">생년월일</p>
      <input 
        v-model="userInfo.birthdate"
        type="text"
        readonly
        class="border-b border-gray-300 outline-none px-1 py-1 text-gray-500 bg-gray-100 cursor-not-allowed"
      />

      <!-- ✅ 주소 (수정 가능) -->
      <p class="text-gray-600">주소</p>
      <input 
        v-model="userInfo.address"
        type="text"
        class="border-b border-gray-300 outline-none px-1 py-1 w-full"
      />

      <!-- ✅ 우편번호 (수정 가능) -->
      <p class="text-gray-600">우편번호</p>
      <input 
        v-model="userInfo.zipcode"
        type="text"
        class="border-b border-gray-300 outline-none px-1 py-1 w-full"
      />
    </div>

    <!-- ✅ 수정 모달 -->
    <div v-if="isModalOpen" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
      <div class="bg-white p-6 rounded-lg shadow-lg w-80">
        <h2 class="text-lg font-bold mb-4">변경하기</h2>
        <input 
          v-model="modalValue"
          :type="modalField === 'email' ? 'email' : 'tel'"
          class="border p-2 w-full rounded outline-none focus:border-[#3D5A3F]"
        />
        <div class="flex justify-end space-x-2 mt-4">
          <button @click="closeModal" class="px-4 py-2 bg-gray-300 rounded">취소</button>
          <button @click="validateAndSave" class="px-4 py-2 bg-[#4E7351] text-white rounded hover:bg-[#3D5A3F]">저장</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import BackButton from '../components/BackButton.vue';

// ✅ 유저 정보 (추후 백엔드에서 받아올 예정)
const userInfo = ref({
  email: "kimsaffy@naver.com",
  phone: "010-0000-0000",
  gender: "남성",
  birthdate: "1997-08-13",
  address: "주소를 표시하는 곳",
  zipcode: "우편번호를 표시하는 곳"
});

// ✅ 모달 상태
const isModalOpen = ref(false);
const modalField = ref("");
const modalValue = ref("");

// ✅ 모달 열기
const openModal = (field) => {
  modalField.value = field;
  modalValue.value = userInfo.value[field];
  isModalOpen.value = true;
};

// ✅ 모달 닫기
const closeModal = () => {
  isModalOpen.value = false;
};

// ✅ 저장 버튼 클릭 시 실행 (중복 확인 필요)
const validateAndSave = async () => {
  const field = modalField.value;
  const value = modalValue.value;

  // 백엔드에서 중복 확인 (API 요청을 가정)
  const isDuplicate = await checkDuplicate(field, value);
  
  if (isDuplicate) {
    alert(`이미 사용 중인 ${field === 'email' ? '이메일' : '전화번호'}입니다.`);
    return;
  }

  // ✅ TODO: 인증 절차 (이메일/전화번호 변경 시)
  alert(`${field === 'email' ? '이메일' : '전화번호'} 변경이 완료되었습니다.`);

  // 저장 후 적용
  userInfo.value[field] = value;
  closeModal();
};

// ✅ 백엔드 중복 확인 (더미 함수)
const checkDuplicate = async (field, value) => {
  console.log(`백엔드에 ${field} 중복 확인 요청: ${value}`);
  return false; // ❌ 실제 API 호출 시, 백엔드 응답에 따라 변경
};
</script>
