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
      <!-- ✅ 이름 (변경 불가) -->
      <p class="overflow-hidden text-gray-600 whitespace-nowrap text-ellipsis">이름</p>
      <div class="flex grid items-center justify-between w-full grid-cols-2 pb-1 border-b border-gray-300">
      <input 
        v-model="userInfo.name"
        type="text"
        readonly
        class="flex-grow px-1 py-1 overflow-hidden text-xs outline-none md:text-sm whitespace-nowrap text-ellipsis"
      />
      <!-- 추후 1:1 문의 서비스와 연동 -->
        <button 
          @click="checkNicknameDuplicate" 
          class="text-[#3D5A3F] hover:underline text-xs md:text-sm px-1 py-1 whitespace-nowrap justify-self-end"
        >
          변경 요청
        </button>
      </div>
      <!-- ✅ 닉네임 -->
      <p class="overflow-hidden text-gray-600 whitespace-nowrap text-ellipsis">닉네임</p>
<div class="flex grid items-center justify-between w-full grid-cols-2 pb-1 border-b border-gray-300">
  <span class="overflow-hidden text-xs whitespace-nowrap text-ellipsis">
    {{ userInfo.nickname }}
  </span>
  <button 
    @click="openNicknameModal" 
    class="text-[#3D5A3F] hover:underline text-xs md:text-sm px-1 py-1 whitespace-nowrap justify-self-end"
  >
    변경하기
  </button>
</div>

      <!-- ✅ 이메일 (오어스 회원이면 변경 불가 모달을 통한 변경/인증) -->
      <p class="overflow-hidden text-gray-600 whitespace-nowrap text-ellipsis">이메일</p>
      <div class="flex items-center justify-between w-full pb-1 border-b border-gray-300">
        <span class="overflow-hidden text-xs whitespace-nowrap">{{ userInfo.email }}</span>
        <button 
          @click="openModal('email')" 
          class="text-[#3D5A3F] hover:underline text-xs md:text-sm px-1 py-1 whitespace-nowrap"
        >
          변경하기
        </button>
      </div>

      <!-- ✅ 전화번호 (모달을 통한 변경/인증) -->
      <p class="overflow-hidden text-gray-600 whitespace-nowrap text-ellipsis">전화번호</p>
      <div class="flex items-center justify-between w-full pb-1 border-b border-gray-300">
        <span class="overflow-hidden text-xs whitespace-nowrap text-ellipsis">{{ userInfo.phone }}</span>
        <button 
          @click="openModal('phone')" 
          class="text-[#3D5A3F] hover:underline text-xs md:text-sm px-1 py-1 whitespace-nowrap"
        >
          변경하기
        </button>
      </div>

      <!-- ✅ 성별 (변경 불가) -->
      <p class="overflow-hidden text-gray-600 whitespace-nowrap text-ellipsis">성별</p>
      <div class="flex grid items-center justify-between w-full grid-cols-2 pb-1 border-b border-gray-300">
        <input 
        v-model="userInfo.gender"
        type="text"
        readonly
        class="flex-grow px-1 py-1 overflow-hidden text-xs outline-none md:text-sm whitespace-nowrap text-ellipsis"
      />
      <!-- 추후 1:1 문의 서비스와 연동 -->
        <button 
          @click="checkNicknameDuplicate" 
          class="text-[#3D5A3F] hover:underline text-xs md:text-sm px-1 py-1 whitespace-nowrap justify-self-end"
        >
          변경 요청
        </button>
      </div>
      

      <!-- ✅ 생년월일 (변경 불가) -->
      <p class="overflow-hidden text-gray-600 whitespace-nowrap text-ellipsis">생년월일</p>
      <div class="flex grid items-center justify-between w-full grid-cols-2 pb-1 border-b border-gray-300">
        <input 
        v-model="userInfo.birthday"
        type="text"
        readonly
        class="flex-grow px-1 py-1 overflow-hidden text-xs outline-none md:text-sm whitespace-nowrap text-ellipsis"
      />
      <!-- 추후 1:1 문의 서비스와 연동 -->
        <button 
          @click="checkNicknameDuplicate" 
          class="text-[#3D5A3F] hover:underline text-xs md:text-sm px-1 py-1 whitespace-nowrap justify-self-end"
        >
          변경 요청
        </button>
      </div>
    </div>    

    <!-- ✅ 변경/인증 모달 (이메일/전화번호) -->
<div 
  v-if="isModalOpen" 
  class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50"
>
  <div 
    class="flex flex-col p-4 bg-white rounded-lg shadow-lg md:p-6"
    @click.stop
  >
    <!-- 모달 헤더 -->
    <div class="flex items-center justify-between mb-4">
      <h2 class="text-[clamp(14px,4vw,20px)] font-bold whitespace-nowrap overflow-hidden text-ellipsis">
        {{ modalField === 'email' ? '이메일 변경' : '전화번호 변경' }}
      </h2>
      <button 
        @click="closeModal" 
        class="text-gray-500 hover:text-gray-700"
      >
        <span class="text-xl">&times;</span>
      </button>
    </div>

    <!-- 이메일과 전화번호는 동일한 인증 흐름을 사용 -->
    <div v-if="modalField === 'email' || modalField === 'phone'">
      <input 
        v-model="modalValue" 
        :type="modalField === 'email' ? 'email' : 'phone'" 
        :placeholder="modalField === 'email' ? '새 이메일 입력' : '새 전화번호 입력'" 
        @blur="modalField === 'email' ? handleEmailBlur : handlePhoneBlur"
        class="border p-2 w-full rounded outline-none focus:border-[#3D5A3F] text-sm md:text-base mb-2"
      />
      <!-- 인증번호 발송 전 -->
      <div v-if="!isVerificationSent" class="flex flex-col items-center justify-center">
        <BaseButton 
          @click="modalField === 'email' ? sendEmailCode : sendPhoneVerification" 
          class="!min-w-max px-3 py-1 bg-[#4E7351] text-white rounded hover:bg-[#3D5A3F] text-xs md:text-sm"
        >
          인증번호 발송
        </BaseButton>
      </div>
      <!-- 인증번호 발송 후 -->
      <div v-else class="flex flex-col items-center justify-center">
        <p class="mb-2 text-xs text-gray-600">인증번호가 발송되었습니다.</p>
        <input 
          v-model="verificationCodeInput" 
          type="text" 
          placeholder="인증번호 입력" 
          class="!w-full border p-2 outline-none focus:border-[#3D5A3F] text-sm md:text-base mb-2"
        />
        <BaseButton 
          @click="modalField === 'email' ? checkEmailCode : verifyPhoneCode" 
          class="!min-w-max px-3 py-1 bg-[#4E7351] text-white hover:bg-[#3D5A3F] text-xs md:text-sm mb-2"
        >
          인증번호 확인
        </BaseButton>
        <p v-if="isVerified" class="!w-full text-xs text-green-500 mb-2">인증 완료!</p>
        <BaseButton
        v-if="isVerified"
          @click="modalField === 'email' ? submitEmailChange : submitPhoneChange" 
          class="!min-w-max px-3 py-1 bg-[#4E7351] text-white hover:bg-[#3D5A3F] text-xs md:text-sm mb-2"
        >
          변경
        </BaseButton>
      </div>
    </div>

    <!-- 닉네임 변경 모달 -->
<div v-if="modalField === 'nickname'">
  <input 
    v-model="modalValue" 
    type="text" 
    placeholder="새 닉네임 입력" 
    @blur="handleNicknameBlur"
    class="border p-2 w-full rounded outline-none focus:border-[#3D5A3F] text-sm md:text-base mb-2"
  />
  <!-- 닉네임 유효성 메시지 -->
  <p class="mb-2 text-xs" :class="isNicknameValid ? 'text-green-500' : 'text-red-500'">
    {{ nicknameMessage }}
  </p>
  <div class="flex flex-col items-center justify-center">
    <BaseButton 
      @click="submitNicknameChange" 
      :disabled="!isNicknameValid"
      class="!min-w-max px-3 py-1 bg-[#4E7351] text-white rounded hover:bg-[#3D5A3F] text-xs md:text-sm"
      :class="{ 'opacity-50 cursor-not-allowed': !isNicknameValid }"
    >
      변경하기
    </BaseButton>
  </div>
</div>

  </div>
</div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue';
import BackButton from '../components/BackButton.vue';
import BaseButton from '../components/BaseButton.vue';
import { getMyInfo,
  checkNickname, changeNickname,
  checkEmail, sendEmailVerification, verifyEmailCode, changeEmail } from '../api/mypage';

const userInfo = ref({
  name: "",
  nickname: "",
  email: "",
  phone: "",
  gender: "",
  birthday: "",
});

onMounted(async () => {
  userInfo.value = await getMyInfo();
  if(userInfo.value.gender === 'M') {
    userInfo.value.gender = '남성';
} else{
    userInfo.value.gender = '여성';
}
}
);

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

// 이메일 관련 상태
const emailMessage = ref('');
const isEmailValid = ref(false);
const isEmailVerificationSent = ref(false);
const emailVerificationCode = ref('');
const isEmailVerified = ref(false);

// 이메일 유효성 검사
const validateEmail = async (email) => {
  try {
    if (!email) {
      emailMessage.value = '이메일을 입력해주세요.';
      isEmailValid.value = false;
      return;
    }

    const result = await checkEmail(email);
    
    if (result.isSameAsCurrent) {
      emailMessage.value = '현재 사용 중인 이메일입니다.';
      isEmailValid.value = false;
      return;
    }
    
    if (result.isAlreadyExists) {
      emailMessage.value = '이미 존재하는 이메일입니다.';
      isEmailValid.value = false;
      return;
    }

    emailMessage.value = '사용 가능한 이메일입니다.';
    isEmailValid.value = true;
  } catch (error) {
    emailMessage.value = error.message;
    isEmailValid.value = false;
  }
};

// 이메일 실시간 체크
watch(
  () => modalValue.value,
  (newEmail) => {
    if (newEmail && modalField.value === 'email') {
      validateEmail(newEmail);
    }
  },
  { debounce: 500 }
);

// blur 이벤트 핸들러
const handleEmailBlur = () => {
  if (modalValue.value && modalField.value === 'email') {
    validateEmail(modalValue.value);
  }
};

// 인증번호 발송
const sendEmailCode = async () => {
  try {
    if (!isEmailValid.value) {
      emailMessage.value = '유효한 이메일을 입력해주세요.';
      return;
    }
    
    await sendEmailVerification(modalValue.value);
    isEmailVerificationSent.value = true;
    emailMessage.value = '인증번호가 발송되었습니다.';
  } catch (error) {
    emailMessage.value = error.message;
  }
};

// 인증번호 확인
const checkEmailCode = async () => {
  try {
    await verifyEmailCode(modalValue.value, emailVerificationCode.value);
    isEmailVerified.value = true;
    emailMessage.value = '인증이 완료되었습니다.';
  } catch (error) {
    emailMessage.value = error.message;
    isEmailVerified.value = false;
  }
};

// 이메일 변경 제출
const submitEmailChange = async () => {
  try {
    if (!isEmailVerified.value) {
      emailMessage.value = '이메일 인증이 필요합니다.';
      return;
    }

    await changeEmail(modalValue.value);
    userInfo.value.email = modalValue.value;
    alert('이메일이 성공적으로 변경되었습니다.');
    closeModal();
  } catch (error) {
    emailMessage.value = error.message;
  }
};

// 닉네임 모달 열기
const openNicknameModal = () => {
  modalField.value = 'nickname';
  modalValue.value = userInfo.value.nickname;
  isModalOpen.value = true;
  nicknameMessage.value = '';
  isNicknameValid.value = false;
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

// 상태 변수 추가
const nicknameMessage = ref('');
const isNicknameValid = ref(false);

// 닉네임 유효성 검사 및 중복 체크
const checkNicknameDuplicate = async () => {
  try {
    if (!modalValue.value) {
      nicknameMessage.value = '닉네임을 입력해주세요.';
      isNicknameValid.value = false;
      return;
    }

    const result = await checkNickname(modalValue.value);
    
    if (result.isSameAsCurrent) {
      nicknameMessage.value = '현재 사용 중인 닉네임입니다.';
      isNicknameValid.value = false;
      return;
    }
    
    if (result.isAlreadyExists) {
      nicknameMessage.value = '이미 존재하는 닉네임입니다.';
      isNicknameValid.value = false;
      return;
    }

    nicknameMessage.value = '사용 가능한 닉네임입니다.';
    isNicknameValid.value = true;
  } catch (error) {
    nicknameMessage.value = error.message;
    isNicknameValid.value = false;
  }
};

// 닉네임 실시간 체크
watch(
  () => modalValue.value,
  (newNickname) => {
    if (newNickname && modalField.value === 'nickname') {
      checkNicknameDuplicate();
    }
  },
  { debounce: 500 }
);

// blur 이벤트 핸들러
const handleNicknameBlur = () => {
  if (modalValue.value && modalField.value === 'nickname') {
    checkNicknameDuplicate();
  }
};

// 닉네임 변경 제출
const submitNicknameChange = async () => {
  try {
    if (!isNicknameValid.value) {
      nicknameMessage.value = '닉네임 중복 확인이 필요합니다.';
      return;
    }

    await changeNickname(modalValue.value);
    userInfo.value.nickname = modalValue.value;
    alert('닉네임이 성공적으로 변경되었습니다.');
    closeModal();
  } catch (error) {
    nicknameMessage.value = error.message;
  }
};


</script>
