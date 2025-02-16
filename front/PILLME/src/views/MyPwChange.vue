<template>
  <div class="flex flex-col h-screen p-6">
    <BackButton class="mb-4" />
    <h1 class="mb-4 text-xl font-bold">비밀번호 변경</h1>

    <div class="w-full max-w-md space-y-4">
      <div>
        <label class="block text-gray-600">현재 비밀번호</label>
        <input
          v-model="passwords.current"
          type="password"
          placeholder="현재 비밀번호"
          @blur="validateCurrentPassword"
          class="border border-gray-300 rounded w-full p-2 outline-none focus:ring-2 focus:ring-[#3D5A3F]"
        />
        <p
          v-if="validationMessages.current"
          :class="{
            'text-green-500': validationStates.currentValid,
            'text-red-500': !validationStates.currentValid,
          }"
          class="mt-1 text-sm"
        >
          {{ validationMessages.current }}
        </p>
      </div>

      <div>
        <label class="block text-gray-600">새 비밀번호</label>
        <input
          v-model="passwords.new"
          type="password"
          placeholder="새 비밀번호"
          @input="validateNewPassword"
          class="border border-gray-300 rounded w-full p-2 outline-none focus:ring-2 focus:ring-[#3D5A3F]"
        />
        <p
          v-if="validationMessages.new"
          :class="{ 'text-red-500': !validationStates.newValid }"
          class="mt-1 text-sm"
        >
          {{ validationMessages.new }}
        </p>
      </div>

      <div>
        <label class="block text-gray-600">새 비밀번호 확인</label>
        <input
          v-model="passwords.confirm"
          type="password"
          placeholder="새 비밀번호 확인"
          @input="validateConfirmPassword"
          class="border border-gray-300 rounded w-full p-2 outline-none focus:ring-2 focus:ring-[#3D5A3F]"
        />
        <p
          v-if="validationMessages.confirm"
          :class="{
            'text-green-500': validationStates.confirmValid,
            'text-red-500': !validationStates.confirmValid,
          }"
          class="mt-1 text-sm"
        >
          {{ validationMessages.confirm }}
        </p>
      </div>

      <button
        @click="handleChangePassword"
        :disabled="isLoading || !isFormValid"
        class="w-full px-4 py-2 bg-[#4E7351] text-white rounded hover:bg-[#3D5A3F] transition disabled:bg-gray-400"
      >
        {{ isLoading ? '변경 중...' : '변경하기' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import BackButton from '../components/BackButton.vue';
import { checkCurrentPassword, checkPassword, changePassword } from '../api/mypage';

const router = useRouter();
const isLoading = ref(false);

const passwords = ref({
  current: '',
  new: '',
  confirm: '',
});

const validationMessages = ref({
  current: '',
  new: '',
  confirm: '',
});

const validationStates = ref({
  currentValid: false,
  newValid: false,
  confirmValid: false,
});

const isFormValid = computed(() => {
  return (
    validationStates.value.currentValid &&
    validationStates.value.newValid &&
    validationStates.value.confirmValid
  );
});

// 현재 비밀번호 검증
const validateCurrentPassword = async () => {
  if (!passwords.value.current) {
    validationMessages.value.current = '현재 비밀번호를 입력해주세요';
    validationStates.value.currentValid = false;
    return;
  }

  try {
    const result = await checkCurrentPassword(passwords.value.current);
    validationStates.value.currentValid = result.isCurrentPasswordValid;
    validationMessages.value.current = result.isCurrentPasswordValid
      ? '현재 비밀번호가 일치합니다'
      : '현재 비밀번호가 일치하지 않습니다';
  } catch (error) {
    validationMessages.value.current = error.message;
    validationStates.value.currentValid = false;
  }
};

// 새 비밀번호 검증
const validateNewPassword = async () => {
  if (!passwords.value.new) {
    validationMessages.value.new = '새 비밀번호를 입력해주세요';
    validationStates.value.newValid = false;
    return;
  }

  try {
    const result = await checkPassword(passwords.value.new);
    validationStates.value.newValid = result.isNewPasswordValid;
    validationMessages.value.new = result.isNewPasswordValid
      ? ''
      : '비밀번호는 대소문자, 숫자, 특수문자를 포함한 12자 이상이어야 합니다';
  } catch (error) {
    validationMessages.value.new = error.message;
    validationStates.value.newValid = false;
  }
};

// 새 비밀번호 확인 검증
const validateConfirmPassword = () => {
  if (!passwords.value.confirm) {
    validationMessages.value.confirm = '새 비밀번호 확인을 입력해주세요';
    validationStates.value.confirmValid = false;
    return;
  }

  const isMatch = passwords.value.new === passwords.value.confirm;
  validationStates.value.confirmValid = isMatch;
  validationMessages.value.confirm = isMatch
    ? '비밀번호가 일치합니다'
    : '비밀번호가 일치하지 않습니다';
};

// 비밀번호 변경 처리
const handleChangePassword = async () => {
  if (!isFormValid.value) return;

  try {
    isLoading.value = true;
    const result = await changePassword(passwords.value.current, passwords.value.new);
    if (result) {
      alert('비밀번호가 성공적으로 변경되었습니다');
      router.push('/mypage');
    }
  } catch (error) {
    alert(error.message);
  } finally {
    isLoading.value = false;
  }
};
</script>
