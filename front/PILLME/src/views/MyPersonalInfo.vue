<template>
  <div class="flex flex-col h-screen p-4 md:p-6">
    <!-- ✅ 뒤로 가기 버튼 -->
    <BackButton class="mb-4" />

    <!-- ✅ 기본 정보 표시 -->
    <h1 class="text-[clamp(16px, 4vw, 24px)] font-bold mb-4 whitespace-nowrap overflow-hidden text-ellipsis">
      개인정보 관리
    </h1>

    <!-- ✅ 리스트 형식의 정보 -->
    <div class="grid grid-cols-2 gap-y-4 text-[clamp(12px, 3vw, 16px)] min-w-full">
      <!-- ✅ 닉네임 (중복 확인 추가) -->
      <p class="text-gray-600 whitespace-nowrap overflow-hidden text-ellipsis">닉네임</p>
      <div class="grid grid-cols-2 flex justify-between items-center w-full border-b border-gray-300 pb-1">
        <input 
          v-model="userInfo.nickname" 
          type="text" 
          @input="isNicknameVerified = false" 
          class=" outline-none px-1 py-1 text-xs md:text-sm flex-grow whitespace-nowrap overflow-hidden text-ellipsis"
        />
        <button 
          @click="checkNicknameDuplicate" 
          class="text-[#3D5A3F] hover:underline text-xs md:text-sm px-1 py-1 whitespace-nowrap justify-self-end"
        >
          중복 확인
        </button>
      </div>

      <!-- ✅ 이메일 (모달을 통한 변경/인증) -->
      <p class="text-gray-600 whitespace-nowrap overflow-hidden text-ellipsis">이메일</p>
      <div class="flex justify-between items-center w-full border-b border-gray-300 pb-1">
        <span class="font-medium whitespace-nowrap overflow-hidden">{{ userInfo.email }}</span>
        <button 
          @click="openModal('email')" 
          class="text-[#3D5A3F] hover:underline text-xs md:text-sm px-2 py-1 whitespace-nowrap"
        >
          변경하기
        </button>
      </div>

      <!-- ✅ 전화번호 (모달을 통한 변경/인증) -->
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

    <!-- ✅ 페이지 전체 변경사항 최종 제출 버튼 -->
    <div class="mt-6">
      <BaseButton 
        @click="finalSubmit" 
        class="w-full py-2 bg-[#4E7351] text-white rounded hover:bg-[#3D5A3F] text-sm md:text-base"
      >
        변경사항 저장
      </BaseButton>
    </div>

    <!-- ✅ 변경/인증 모달 (이메일/전화번호) -->
    <div v-if="isModalOpen" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
      <div class="bg-white p-4 md:p-6 rounded-lg shadow-lg flex flex-col">
        <h2 class="text-[clamp(14px, 4vw, 20px)] font-bold mb-4 whitespace-nowrap overflow-hidden text-ellipsis">
          {{ modalField === 'email' ? '이메일 변경' : '전화번호 변경' }}
        </h2>
        <!-- 이메일과 전화번호는 동일한 인증 흐름을 사용 -->
        <div v-if="modalField === 'email' || modalField === 'phone'">
          <input 
            v-model="modalValue" 
            :type="modalField === 'email' ? 'email' : 'tel'" 
            :placeholder="modalField === 'email' ? '새 이메일 입력' : '새 전화번호 입력'" 
            class="border p-2 w-full rounded outline-none focus:border-[#3D5A3F] text-sm md:text-base mb-2"
          />
          <!-- 인증번호 발송 전 -->
          <div v-if="!isVerificationSent" class="justify-center flex flex-col items-center">
            <BaseButton 
              @click="sendVerificationCode" 
              class="!min-w-max px-3 py-1 bg-[#4E7351] text-white rounded hover:bg-[#3D5A3F] text-xs md:text-sm"
            >
              인증번호 발송
            </BaseButton>
          </div>
          <!-- 인증번호 발송 후 -->
          <div v-else class="justify-center flex flex-col items-center">
            <p class="text-xs text-gray-600 mb-2">인증번호가 발송되었습니다.</p>
            <input 
              v-model="verificationCodeInput" 
              type="text" 
              placeholder="인증번호 입력" 
              class="!w-full border p-2 outline-none focus:border-[#3D5A3F] text-sm md:text-base mb-2"
            />
            <BaseButton 
              @click="verifyCode" 
              class="!min-w-max px-3 py-1 bg-[#4E7351] text-white hover:bg-[#3D5A3F] text-xs md:text-sm mb-2"
            >
              인증번호 확인
            </BaseButton>
            <p v-if="isVerified" class="!w-full text-xs text-green-500 mb-2">인증 완료!</p>
          </div>
        </div>

        <div class="flex !w-auto justify-center space-x-2 mt-4">
          <BaseButton 
            @click="closeModal" 
            class="!min-w-max px-3 py-1 bg-gray-300 !text-xs md:text-sm"
          >
            취소
          </BaseButton>
          <BaseButton 
            @click="validateAndSave" 
            class="!min-w-max px-3 py-1 bg-[#4E7351] text-white hover:bg-[#3D5A3F] text-xs md:text-sm"
          >
            변경
          </BaseButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import BackButton from '../components/BackButton.vue';
import BaseButton from '../components/BaseButton.vue';
// ✅ 유저 정보 (실제 환경에서는 백엔드에서 받아올 예정)
const userInfo = ref({
  nickname: "사용자닉네임",
  email: "kimsaffy@naver.com",
  phone: "010-0000-0000",
  gender: "남성",
  birthdate: "1997-08-13",
  address: "서울특별시 강남구 테헤란로 123",
  zipcode: "12345"
});

// ✅ 모달 및 인증 관련 상태
const isModalOpen = ref(false);
const modalField = ref("");
const modalValue = ref("");

const isVerificationSent = ref(false);
const verificationCodeInput = ref("");
const isVerified = ref(false);
const generatedCode = ref("");

// ✅ 닉네임 중복 확인 플래그
const isNicknameVerified = ref(false);

// ✅ 모달 열기 (이메일 또는 전화번호 변경 시)
const openModal = (field) => {
  modalField.value = field;
  // 이메일과 전화번호는 새 값을 입력받고 인증 절차를 진행
  if (field === 'email' || field === 'phone') {
    modalValue.value = "";
    isVerificationSent.value = false;
    verificationCodeInput.value = "";
    isVerified.value = false;
    generatedCode.value = "";
  } else {
    modalValue.value = userInfo.value[field];
  }
  isModalOpen.value = true;
};

// ✅ 모달 닫기
const closeModal = () => {
  isModalOpen.value = false;
};

// ✅ 인증번호 발송 (더미 처리)
// 실제 API 호출 시, 백엔드에서 인증번호를 전송하면 됩니다.
const sendVerificationCode = () => {
  if (!modalValue.value) {
    alert(modalField.value === 'email' ? "새 이메일을 입력해주세요." : "새 전화번호를 입력해주세요.");
    return;
  }
  generatedCode.value = "123456"; // 더미 인증번호
  isVerificationSent.value = true;
  alert(`인증번호가 발송되었습니다. (${generatedCode.value})`);
};

// ✅ 인증번호 확인
const verifyCode = () => {
  if (verificationCodeInput.value === generatedCode.value) {
    isVerified.value = true;
    alert("인증이 완료되었습니다.");
  } else {
    alert("인증번호가 일치하지 않습니다.");
  }
};

// ✅ 모달 내 저장/변경 버튼 클릭 시 실행 (이메일, 전화번호)
const validateAndSave = async () => {
  const field = modalField.value;
  const value = modalValue.value;
  
  if (field === 'email' || field === 'phone') {
    if (!isVerified.value) {
      alert(`${field === 'email' ? "이메일" : "전화번호"} 인증을 완료해주세요.`);
      return;
    }
    // 중복 확인 (더미 함수)
    const isDup = await checkDuplicate(field, value);
    if (isDup) {
      alert(`이미 사용 중인 ${field === 'email' ? "이메일" : "전화번호"}입니다.`);
      return;
    }
    userInfo.value[field] = value;
    alert(`${field === 'email' ? "이메일" : "전화번호"} 변경이 완료되었습니다.`);
  }
  closeModal();
};

// ✅ 닉네임 중복 확인 (서버 전체 사용자 검증)
// 실제 환경에서는 API를 통해 중복 여부를 확인합니다.
const checkNicknameDuplicate = async () => {
  const value = userInfo.value.nickname;
  if (!value) {
    alert("닉네임을 입력해주세요.");
    return;
  }
  const isDup = await checkDuplicate("nickname", value);
  if (isDup) {
    alert("이미 사용 중인 닉네임입니다.");
    isNicknameVerified.value = false;
  } else {
    alert("사용 가능한 닉네임입니다.");
    isNicknameVerified.value = true;
  }
};

// ✅ 백엔드 중복 확인 (더미 함수)
// field: 'nickname', 'email', 'phone' 등
const checkDuplicate = async (field, value) => {
  console.log(`서버에 ${field} 중복 확인 요청: ${value}`);
  // 예시: value가 "duplicate"이면 중복된 것으로 처리
  // 실제 환경에서는 API 응답에 따라 true/false 반환
  return false;
};

// ✅ 페이지 전체 변경사항 최종 제출
// 닉네임의 중복 확인 여부를 마지막에 검증합니다.
const finalSubmit = async () => {
  if (!isNicknameVerified.value) {
    alert("닉네임 중복 확인을 해주세요.");
    return;
  }
  // 필요한 경우 다른 필드에 대한 추가 검증을 진행합니다.
  console.log("최종 제출 데이터:", userInfo.value);
  alert("변경사항이 저장되었습니다.");
};
</script>
