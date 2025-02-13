<template>
  <div class="flex flex-col h-screen p-4 md:p-6">
    <!-- ✅ 뒤로 가기 버튼 -->
    <BackButton class="mb-4" />

    <!-- ✅ 기본 정보 표시 -->
    <h1
      class="text-[clamp(16px, 4vw, 24px)] font-bold mb-4 whitespace-nowrap overflow-hidden text-ellipsis"
    >
      개인정보 관리
    </h1>

    <!-- ✅ 리스트 형식의 정보 -->
    <div class="grid grid-cols-2 gap-y-4 text-[clamp(12px, 3vw, 16px)] min-w-full">
      <!-- ✅ 이름 (변경 불가) -->
      <p class="overflow-hidden text-gray-600 whitespace-nowrap text-ellipsis">이름</p>
      <div
        class="flex grid items-center justify-between w-full grid-cols-2 pb-1 border-b border-gray-300"
      >
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
      <div
        class="flex grid items-center justify-between w-full grid-cols-2 pb-1 border-b border-gray-300"
      >
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
          v-if="!userInfo.oauth"
          @click="openModal('email')"
          class="text-[#3D5A3F] hover:underline text-xs md:text-sm px-1 py-1 whitespace-nowrap"
        >
          변경하기
        </button>
      </div>

      <!-- ✅ 전화번호 (모달을 통한 변경/인증) -->
      <p class="overflow-hidden text-gray-600 whitespace-nowrap text-ellipsis">전화번호</p>
      <div class="flex items-center justify-between w-full pb-1 border-b border-gray-300">
        <span class="overflow-hidden text-xs whitespace-nowrap text-ellipsis">{{
          userInfo.phone
        }}</span>
        <button
          @click="openModal('phone')"
          class="text-[#3D5A3F] hover:underline text-xs md:text-sm px-1 py-1 whitespace-nowrap"
        >
          변경하기
        </button>
      </div>

      <!-- ✅ 성별 (변경 불가) -->
      <p class="overflow-hidden text-gray-600 whitespace-nowrap text-ellipsis">성별</p>
      <div
        class="flex grid items-center justify-between w-full grid-cols-2 pb-1 border-b border-gray-300"
      >
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
      <div
        class="flex grid items-center justify-between w-full grid-cols-2 pb-1 border-b border-gray-300"
      >
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
        class="flex flex-col w-full max-w-md p-4 bg-white rounded-lg shadow-lg md:p-6"
        @click.stop
      >
        <!-- 모달 헤더 -->
        <div class="flex items-center justify-between mb-4">
          <h2
            class="text-[clamp(14px,4vw,20px)] font-bold whitespace-nowrap overflow-hidden text-ellipsis"
          >
            {{ modalField === 'email' ? '이메일 변경' : '전화번호 변경' }}
          </h2>
          <button @click="closeModal" class="text-gray-500 hover:text-gray-700">
            <span class="text-xl">&times;</span>
          </button>
        </div>

        <!-- 이메일 변경 모달 -->
        <div v-if="modalField === 'email'" class="space-y-4">
          <!-- 이메일 입력 -->
          <div>
            <input
              v-model="modalValue"
              type="email"
              placeholder="새 이메일 입력"
              @blur="handleEmailBlur"
              class="border p-2 w-full rounded outline-none focus:border-[#3D5A3F] text-sm md:text-base mb-2"
            />
            <p class="mb-2 text-xs" :class="isEmailValid ? 'text-green-500' : 'text-red-500'">
              {{ emailMessage }}
            </p>
          </div>

          <!-- 이메일 인증번호 발송 전 -->
          <div v-if="!isVerificationSent" class="flex justify-center">
            <BaseButton
              @click="sendEmailCode"
              :disabled="!isEmailValid || isLoadingSendVerification"
              class="!min-w-max px-3 py-1 bg-[#4E7351] text-white rounded hover:bg-[#3D5A3F] text-xs md:text-sm"
              :class="{
                'opacity-50 cursor-not-allowed': !isEmailValid || isLoadingSendVerification,
              }"
            >
              {{ isLoadingSendVerification ? '발송 중...' : '인증번호 발송' }}
            </BaseButton>
          </div>

          <!-- 이메일 인증번호 발송 후 -->
          <div v-else class="space-y-3">
            <p class="mb-2 text-xs text-gray-600">이메일로 인증번호가 발송되었습니다.</p>
            <input
              v-model="verificationCodeInput"
              type="text"
              placeholder="인증번호 입력"
              class="border p-2 w-full rounded outline-none focus:border-[#3D5A3F] text-sm md:text-base mb-2"
            />
            <div class="flex flex-col items-center space-y-2">
              <BaseButton
                @click="checkEmailCode"
                class="!min-w-max px-3 py-1 bg-[#4E7351] text-white rounded hover:bg-[#3D5A3F] text-xs md:text-sm"
              >
                인증번호 확인
              </BaseButton>
              <template v-if="isVerified">
                <p class="mb-2 text-xs text-green-500">이메일 인증이 완료되었습니다!</p>
                <BaseButton
                  @click="submitEmailChange"
                  class="!min-w-max px-3 py-1 bg-[#4E7351] text-white rounded hover:bg-[#3D5A3F] text-xs md:text-sm"
                >
                  이메일 변경
                </BaseButton>
              </template>
            </div>
          </div>
        </div>

        <!-- 전화번호 변경 모달 -->
        <div v-if="modalField === 'phone'" class="space-y-4">
          <!-- 전화번호 입력 -->
          <div>
            <input
              v-model="modalValue"
              type="tel"
              placeholder="새 전화번호 입력"
              @blur="handlePhoneBlur"
              class="border p-2 w-full rounded outline-none focus:border-[#3D5A3F] text-sm md:text-base mb-2"
            />
            <p class="mb-2 text-xs" :class="isPhoneValid ? 'text-green-500' : 'text-red-500'">
              {{ phoneMessage }}
            </p>
          </div>

          <!-- 전화번호 인증번호 발송 전 -->
          <div v-if="!isVerificationSent" class="flex justify-center">
            <BaseButton
              @click="sendPhoneCode"
              :disabled="!isPhoneValid || isLoadingSendVerification"
              class="!min-w-max px-3 py-1 bg-[#4E7351] text-white rounded hover:bg-[#3D5A3F] text-xs md:text-sm"
              :class="{
                'opacity-50 cursor-not-allowed': !isPhoneValid || isLoadingSendVerification,
              }"
            >
              {{ isLoadingSendVerification ? '발송 중...' : '인증번호 발송' }}
            </BaseButton>
          </div>

          <!-- 전화번호 인증번호 발송 후 -->
          <div v-else class="space-y-3">
            <p class="mb-2 text-xs text-gray-600">휴대폰으로 인증번호가 발송되었습니다.</p>
            <input
              v-model="verificationCodeInput"
              type="text"
              placeholder="인증번호 입력"
              class="border p-2 w-full rounded outline-none focus:border-[#3D5A3F] text-sm md:text-base mb-2"
            />
            <div class="flex flex-col items-center space-y-2">
              <BaseButton
                @click="checkPhoneCode"
                class="!min-w-max px-3 py-1 bg-[#4E7351] text-white rounded hover:bg-[#3D5A3F] text-xs md:text-sm"
              >
                인증번호 확인
              </BaseButton>
              <template v-if="isVerified">
                <p class="mb-2 text-xs text-green-500">전화번호 인증이 완료되었습니다!</p>
                <BaseButton
                  @click="submitPhoneChange"
                  class="!min-w-max px-3 py-1 bg-[#4E7351] text-white rounded hover:bg-[#3D5A3F] text-xs md:text-sm"
                >
                  전화번호 변경
                </BaseButton>
              </template>
            </div>
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
    <div class="flex justify-center mt-8">
      <button
        @click="openWithdrawalModal"
        class="px-4 py-2 text-sm text-red-500 transition-colors border border-red-500 rounded hover:text-red-700 md:text-base hover:bg-red-50"
      >
        회원 탈퇴
      </button>
    </div>
    <!-- 회원 탈퇴 모달 -->
    <WithdrawalModal
      v-if="!isLoading"
      :is-open="isWithdrawalModalOpen"
      :is-oauth-user="userInfo.oauth"
      @close="closeWithdrawalModal"
      @withdraw="handleWithdrawal"
    />
  </div>
</template>

<script setup>
// Imports
import { onMounted, ref, watch, computed } from 'vue';
import BackButton from '../components/BackButton.vue';
import BaseButton from '../components/BaseButton.vue';
import WithdrawalModal from '../components/WithdrawalModal.vue';
import {
  getMyInfo,
  checkNickname,
  changeNickname,
  checkEmail,
  sendEmailVerification,
  verifyEmailCode,
  changeEmail,
  checkPhone,
  sendPhoneVerification,
  verifyPhoneCode,
  changePhone,
  deleteMember,
} from '../api/mypage';

// 회원 정보
const userInfo = ref({
  name: '',
  nickname: '',
  email: '',
  phone: '',
  gender: '',
  birthday: '',
  role: '',
  oauth: false,
});

const isLoading = ref(true);

onMounted(async () => {
  try {
    userInfo.value = await getMyInfo();
    userInfo.value.gender = userInfo.value.gender === 'M' ? '남성' : '여성';
  } finally {
    isLoading.value = false;
  }
});

// 모달
const isModalOpen = ref(false);
const modalField = ref('');
const modalValue = ref('');
const isVerificationSent = ref(false);
const isLoadingSendVerification = ref(false);
const verificationCodeInput = ref('');
const isVerified = ref(false);
const isWithdrawalModalOpen = ref(false);

const openModal = (field) => {
  modalField.value = field;
  if (field === 'email' || field === 'phone') {
    modalValue.value = '';
    isVerificationSent.value = false;
    verificationCodeInput.value = '';
    isVerified.value = false;
  } else {
    modalValue.value = userInfo.value[field];
  }
  isModalOpen.value = true;
};

const openNicknameModal = () => {
  modalField.value = 'nickname';
  modalValue.value = userInfo.value.nickname;
  isModalOpen.value = true;
};

const openWithdrawalModal = () => {
  isWithdrawalModalOpen.value = true;
};

const closeWithdrawalModal = () => {
  isWithdrawalModalOpen.value = false;
};

const resetModalState = () => {
  modalValue.value = '';
  isVerificationSent.value = false;
  verificationCodeInput.value = '';
  isVerified.value = false;
  emailMessage.value = '';
  phoneMessage.value = '';
  nicknameMessage.value = '';
  isLoadingSendVerification.value = false;
  isWithdrawalModalOpen.value = false;
};

const closeModal = () => {
  isModalOpen.value = false;
};

// 이메일
const emailMessage = ref('');
const isEmailValid = ref(false);
const isEmailVerified = ref(false);

const validateEmail = async (email) => {
  try {
    if (!email) {
      emailMessage.value = '이메일을 입력해주세요.';
      isEmailValid.value = false;
      return;
    }

    const result = await checkEmail(email);
    isEmailValid.value = !(result.isSameAsCurrent || result.isAlreadyExists);
    emailMessage.value = result.isSameAsCurrent
      ? '현재 사용 중인 이메일입니다.'
      : result.isAlreadyExists
        ? '이미 존재하는 이메일입니다.'
        : '사용 가능한 이메일입니다.';
  } catch (error) {
    emailMessage.value = error.message;
    isEmailValid.value = false;
  }
};

const sendEmailCode = async () => {
  try {
    if (!isEmailValid.value) {
      emailMessage.value = '유효한 이메일을 입력해주세요.';
      return;
    }
    isLoadingSendVerification.value = true;
    await sendEmailVerification(modalValue.value);
    isVerificationSent.value = true;
    emailMessage.value = '인증번호가 발송되었습니다.';
  } catch (error) {
    emailMessage.value = error.message;
  } finally {
    isLoadingSendVerification.value = false;
  }
};

const checkEmailCode = async () => {
  try {
    await verifyEmailCode(modalValue.value, verificationCodeInput.value);
    isEmailVerified.value = true;
    isVerified.value = true;
    emailMessage.value = '인증이 완료되었습니다.';
  } catch (error) {
    emailMessage.value = error.message;
    isEmailVerified.value = false;
    isVerified.value = false;
  }
};

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

// 전화번호
const phoneMessage = ref('');
const isPhoneValid = ref(false);
const isPhoneVerified = ref(false);

const validatePhoneNumber = async (phone) => {
  try {
    if (!phone) {
      phoneMessage.value = '전화번호를 입력해주세요.';
      isPhoneValid.value = false;
      return;
    }

    const result = await checkPhone(phone);
    isPhoneValid.value =
      !(result.isSameAsCurrent || result.isAlreadyExists) && result.isValidFormat;
    phoneMessage.value = result.isSameAsCurrent
      ? '현재 사용 중인 전화번호입니다.'
      : result.isAlreadyExists
        ? '이미 존재하는 전화번호입니다.'
        : !result.isValidFormat
          ? '올바른 전화번호 형식이 아닙니다.'
          : '사용 가능한 전화번호입니다.';
  } catch (error) {
    phoneMessage.value = error.message;
    isPhoneValid.value = false;
  }
};

const sendPhoneCode = async () => {
  try {
    if (!isPhoneValid.value) {
      phoneMessage.value = '유효한 전화번호를 입력해주세요.';
      return;
    }
    isLoadingSendVerification.value = true;
    await sendPhoneVerification(modalValue.value);
    isVerificationSent.value = true;
    phoneMessage.value = '인증번호가 발송되었습니다.';
  } catch (error) {
    phoneMessage.value = error.message;
  } finally {
    isLoadingSendVerification.value = false;
  }
};

const checkPhoneCode = async () => {
  try {
    await verifyPhoneCode(modalValue.value, verificationCodeInput.value);
    isPhoneVerified.value = true;
    isVerified.value = true;
    phoneMessage.value = '인증이 완료되었습니다.';
  } catch (error) {
    phoneMessage.value = error.message;
    isPhoneVerified.value = false;
    isVerified.value = false;
  }
};

const submitPhoneChange = async () => {
  try {
    if (!isPhoneVerified.value) {
      phoneMessage.value = '전화번호 인증이 필요합니다.';
      return;
    }
    await changePhone(modalValue.value);
    userInfo.value.phone = modalValue.value;
    alert('전화번호가 성공적으로 변경되었습니다.');
    closeModal();
  } catch (error) {
    phoneMessage.value = error.message;
  }
};

// 닉네임
const nicknameMessage = ref('');
const isNicknameValid = ref(false);

const validateNickname = async (nickname) => {
  try {
    if (!nickname) {
      nicknameMessage.value = '닉네임을 입력해주세요.';
      isNicknameValid.value = false;
      return;
    }

    const result = await checkNickname(nickname);
    isNicknameValid.value = !(result.isSameAsCurrent || result.isAlreadyExists);
    nicknameMessage.value = result.isSameAsCurrent
      ? '현재 사용 중인 닉네임입니다.'
      : result.isAlreadyExists
        ? '이미 존재하는 닉네임입니다.'
        : '사용 가능한 닉네임입니다.';
  } catch (error) {
    nicknameMessage.value = error.message;
    isNicknameValid.value = false;
  }
};

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

// 회원탈퇴
const handleWithdrawal = async () => {
  try {
    await deleteMember(); // API 호출
    alert('회원 탈퇴가 완료되었습니다.');
    window.location.href = '/start'; // 시작 페이지로 리다이렉트
  } catch (error) {
    alert('회원 탈퇴 중 오류가 발생했습니다. 다시 시도해 주세요.');
    console.error('Withdrawal error:', error);
  }
};

// Watchers
watch(
  () => modalValue.value,
  async (newValue) => {
    if (!newValue) return;

    try {
      switch (modalField.value) {
        case 'email':
          await validateEmail(newValue);
          break;
        case 'phone':
          await validatePhoneNumber(newValue);
          break;
        case 'nickname':
          await validateNickname(newValue);
          break;
      }
    } catch (error) {
      console.error('Validation error:', error);
    }
  },
  { debounce: 500 }
);

// Blurs
const handleEmailBlur = () => {
  validateEmail(modalValue.value);
};

const handlePhoneBlur = () => {
  validatePhoneNumber(modalValue.value);
};

const handleNicknameBlur = () => {
  validateNickname(modalValue.value);
};
</script>
