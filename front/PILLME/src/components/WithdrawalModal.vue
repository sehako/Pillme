<template>
  <div
    v-if="isOpen"
    class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50"
  >
    <div class="p-6 bg-white rounded-lg w-96">
      <h2 class="mb-4 text-xl font-bold">회원 탈퇴</h2>

      <!-- Step 1: 비밀번호 확인 -->
      <div v-if="currentStep === 1">
        <p class="mb-4 text-gray-600">안전한 탈퇴를 위해 현재 비밀번호를 입력해주세요.</p>

        <div class="mb-4">
          <input
            v-model="password"
            type="password"
            placeholder="현재 비밀번호"
            class="w-full p-2 border rounded"
            @blur="handlePasswordBlur"
          />
          <!-- 비밀번호 검증 메시지 -->
          <p
            v-if="validationMessage"
            :class="['mt-1 text-sm', isPasswordValid ? 'text-green-500' : 'text-red-500']"
          >
            {{ validationMessage }}
          </p>
        </div>

        <div class="flex justify-end space-x-3">
          <button @click="closeModal" class="px-4 py-2 text-gray-600 rounded hover:bg-gray-100">
            취소
          </button>
          <button
            @click="handleNextStep"
            class="px-4 py-2 text-white bg-blue-500 rounded hover:bg-blue-600"
          >
            다음
          </button>
        </div>
      </div>

      <!-- Step 2: 탈퇴 확인 문구 -->
      <div v-if="currentStep === 2">
        <p class="mb-4 text-gray-600">탈퇴를 진행하시려면 아래 문구를 동일하게 입력해주세요.</p>

        <div class="mb-4">
          <div class="mb-2 text-sm text-gray-500">
            확인 문구: "더이상 이 서비스를 이용하지 않겠습니다."
          </div>
          <input
            v-model="confirmText"
            type="text"
            placeholder="더이상 이 서비스를 이용하지 않겠습니다."
            class="w-full p-2 border rounded"
          />
        </div>

        <div class="flex justify-end space-x-3">
          <button
            v-if="!isOauthUser"
            @click="currentStep = 1"
            class="px-4 py-2 text-gray-600 rounded hover:bg-gray-100"
          >
            이전
          </button>
          <button
            @click="handleWithdrawal"
            :disabled="!isConfirmTextValid"
            :class="[
              'px-4 py-2 rounded',
              isConfirmTextValid
                ? 'bg-red-500 text-white hover:bg-red-600'
                : 'bg-gray-300 text-gray-500 cursor-not-allowed',
            ]"
          >
            회원 탈퇴
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import { checkCurrentPassword } from '../api/mypage';
import { debounce } from 'lodash';

const props = defineProps({
  isOpen: Boolean,
  isOauthUser: Boolean,
});

const emit = defineEmits(['close', 'withdraw']);

const currentStep = ref(1);
const password = ref('');
const validationMessage = ref('');
const isPasswordValid = ref(false);
const CONFIRM_TEXT = '더이상 이 서비스를 이용하지 않겠습니다.';
const confirmText = ref('');

const isConfirmTextValid = computed(() => {
  return confirmText.value === CONFIRM_TEXT;
});

const closeModal = () => {
  currentStep.value = 1;
  password.value = '';
  confirmText.value = '';
  validationMessage.value = '';
  isPasswordValid.value = false;
  emit('close');
};

// 현재 비밀번호 검증
const validateCurrentPassword = async () => {
  if (!password.value) {
    validationMessage.value = '현재 비밀번호를 입력해주세요';
    isPasswordValid.value = false;
    return;
  }

  try {
    const result = await checkCurrentPassword(password.value);
    isPasswordValid.value = result;
    validationMessage.value = result
      ? '현재 비밀번호가 일치합니다'
      : '현재 비밀번호가 일치하지 않습니다';

    // 자동으로 다음 단계로 넘어가는 부분 제거
  } catch (error) {
    validationMessage.value = error.message;
    isPasswordValid.value = false;
  }
};

// 다음 버튼 클릭 핸들러 추가
const handleNextStep = async () => {
  await validateCurrentPassword();
  if (isPasswordValid.value) {
    currentStep.value = 2;
  }
};

const handlePasswordBlur = async () => {
  if (password.value) {
    await validateCurrentPassword();
  }
};

// 비밀번호 입력 감시
const debouncedValidate = debounce(async () => {
  if (password.value) {
    await validateCurrentPassword();
  }
}, 500);

watch(
  () => password.value,
  () => {
    if (!password.value) {
      validationMessage.value = '';
      isPasswordValid.value = false;
      return;
    }
    debouncedValidate();
  }
);

const handleWithdrawal = () => {
  if (isConfirmTextValid.value) {
    emit('withdraw');
    closeModal();
  }
};

onMounted(() => {
  console.log('Component mounted, isOauthUser:', props.isOauthUser);
  if (props.isOauthUser) {
    currentStep.value = 2;
  }
});
</script>
