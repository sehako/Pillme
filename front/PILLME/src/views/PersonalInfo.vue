<template>
  <div class="flex flex-col h-screen p-4 md:p-6">
    <!-- ✅ 뒤로 가기 버튼 -->
    <BackButton class="mb-4" />

    <!-- ✅ 기본 정보 표시 -->
    <h1 class="text-[clamp(16px, 4vw, 24px)] font-bold mb-4 whitespace-nowrap overflow-hidden text-ellipsis">개인정보 관리</h1>

    <!-- ✅ 리스트 형식의 정보 -->
    <div class="grid grid-cols-2 gap-y-4 text-[clamp(12px, 3vw, 16px)] w-full">
      <!-- ✅ 이메일 (모달 수정) -->
      <p class="text-gray-600 whitespace-nowrap overflow-hidden text-ellipsis">이메일</p>
      <div class="flex justify-between items-center w-full border-b border-gray-300 pb-1">
        <span class="font-medium whitespace-nowrap overflow-hidden text-ellipsis">{{ userInfo.email }}</span>
        <button 
          @click="openModal('email')" 
          class="text-[#3D5A3F] hover:underline text-xs md:text-sm px-2 py-1 whitespace-nowrap"
        >
          변경하기
        </button>
      </div>

      <!-- ✅ 전화번호 (모달 수정) -->
      <p class="text-gray-600 whitespace-nowrap overflow-hidden text-ellipsis">전화번호</p>
      <div class="flex justify-between items-center w-full border-b border-gray-300 pb-1">
        <span class="font-medium whitespace-nowrap overflow-hidden text-ellipsis">{{ userInfo.phone }}</span>
        <button 
          @click="openModal('phone')" 
          class="text-[#3D5A3F] hover:underline text-xs md:text-sm px-2 py-1 whitespace-nowrap"
        >
          변경하기
        </button>
      </div>

      <!-- ✅ 성별 (변경 불가) -->
      <p class="text-gray-600 whitespace-nowrap overflow-hidden text-ellipsis">성별</p>
      <input 
        v-model="userInfo.gender"
        type="text"
        readonly
        class="border-b border-gray-300 outline-none px-1 py-1 text-gray-500 bg-gray-100 cursor-not-allowed text-xs md:text-sm whitespace-nowrap overflow-hidden text-ellipsis"
      />

      <!-- ✅ 생년월일 (변경 불가) -->
      <p class="text-gray-600 whitespace-nowrap overflow-hidden text-ellipsis">생년월일</p>
      <input 
        v-model="userInfo.birthdate"
        type="text"
        readonly
        class="border-b border-gray-300 outline-none px-1 py-1 text-gray-500 bg-gray-100 cursor-not-allowed text-xs md:text-sm whitespace-nowrap overflow-hidden text-ellipsis"
      />

      <!-- ✅ 주소 (수정 가능) -->
      <p class="text-gray-600 whitespace-nowrap overflow-hidden text-ellipsis">주소</p>
      <input 
        v-model="userInfo.address"
        type="text"
        class="border-b border-gray-300 outline-none px-1 py-1 w-full text-xs md:text-sm whitespace-nowrap overflow-hidden text-ellipsis"
      />

      <!-- ✅ 우편번호 (수정 가능) -->
      <p class="text-gray-600 whitespace-nowrap overflow-hidden text-ellipsis">우편번호</p>
      <input 
        v-model="userInfo.zipcode"
        type="text"
        class="border-b border-gray-300 outline-none px-1 py-1 w-full text-xs md:text-sm whitespace-nowrap overflow-hidden text-ellipsis"
      />
    </div>

    <!-- ✅ 수정 모달 -->
    <div v-if="isModalOpen" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
      <div class="bg-white p-4 md:p-6 rounded-lg shadow-lg w-11/12 max-w-sm">
        <h2 class="text-[clamp(14px, 4vw, 20px)] font-bold mb-4 whitespace-nowrap overflow-hidden text-ellipsis">변경하기</h2>
        <input 
          v-model="modalValue"
          :type="modalField === 'email' ? 'email' : 'tel'"
          class="border p-2 w-full rounded outline-none focus:border-[#3D5A3F] text-sm md:text-base whitespace-nowrap overflow-hidden text-ellipsis"
        />
        <div class="flex justify-end space-x-2 mt-4">
          <button @click="closeModal" class="px-3 py-1 bg-gray-300 rounded text-xs md:text-sm whitespace-nowrap">취소</button>
          <button 
            @click="validateAndSave" 
            class="px-3 py-1 bg-[#4E7351] text-white rounded hover:bg-[#3D5A3F] text-xs md:text-sm whitespace-nowrap"
          >
            저장
          </button>
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
  address: "서울특별시 강남구 테헤란로 123",
  zipcode: "12345"
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
