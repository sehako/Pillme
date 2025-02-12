<template>
  <div class="flex flex-col items-center justify-center min-h-screen px-4 py-8">
    <!-- 로고 및 텍스트 -->
    <BaseLogo :src="logoSrc" size="md" />
    <BaseText textBefore="추가 정보를 입력해" highlightText="PILLME와 함께하세요" />

    <!-- 가입 폼 -->
    <form class="max-w-xs md:max-w-sm space-y-4 mt-6" @submit.prevent="handleSubmit">
      <!-- 이름 입력 (OAuth에서 받아온 값으로 미리 채움) -->
      <div class="flex flex-col w-full">
        <label for="name" class="text-sm font-medium">이름</label>
        <BaseInput id="name" v-model="formData.name" type="text" class="bg-gray-100" />
      </div>

      <!-- 이메일 표시 (수정 불가) -->
      <div class="flex flex-col w-full">
        <label for="email" class="text-sm font-medium">이메일(수정 불가)</label>
        <div class="w-full text-gray-700 outline-none bg-gray-100 px-3 py-2 rounded-lg shadow-sm">
          {{ formData.email }}
        </div>
      </div>

      <!-- 닉네임 입력 -->
      <div class="flex flex-col w-full">
        <label for="nickname" class="text-sm font-medium">닉네임</label>
        <div class="flex items-center gap-2 group">
          <BaseInput
            id="nickname"
            v-model="formData.nickname"
            type="text"
            @blur="handleNicknameBlur"
            class="flex-grow"
          />
        </div>
        <p
          v-if="nicknameMessage"
          class="text-xs mt-1"
          :class="{ 'text-green-500': isNicknameValid, 'text-red-500': !isNicknameValid }"
        >
          {{ nicknameMessage }}
        </p>
      </div>

      <!-- 성별 선택 -->
      <div class="flex flex-col w-full">
        <label class="text-sm font-medium">성별</label>
        <div class="flex w-full gap-2">
          <BaseButton
            class="!min-w-28"
            :overrideClass="
              formData.gender === 'M' ? '!bg-[#EF7C8E] text-white' : 'bg-gray-300 text-gray-700'
            "
            type="button"
            @click="formData.gender = 'M'"
          >
            남
          </BaseButton>
          <BaseButton
            class="!min-w-28"
            :overrideClass="
              formData.gender === 'F' ? '!bg-[#EF7C8E] text-white' : 'bg-gray-300 text-gray-700'
            "
            type="button"
            @click="formData.gender = 'F'"
          >
            여
          </BaseButton>
        </div>
      </div>

      <!-- 생년월일 입력 -->
      <div class="flex flex-col w-full">
        <label for="birthdate" class="text-sm font-medium">생년월일</label>
        <BaseInput id="birthdate" v-model="formData.birthday" type="date" />
      </div>

      <!-- 전화번호 입력 -->
      <div class="flex flex-col w-full">
        <label for="phoneNumber" class="text-sm font-medium">전화번호</label>
        <div class="flex items-center gap-2 group">
          <BaseInput
            id="phoneNumber"
            v-model="formData.phone"
            type="tel"
            placeholder="01000000000 형식으로 입력"
            @blur="handlePhoneBlur"
            class="flex-grow"
          />
          <BaseButton
            textColor="text-white"
            size="sm"
            :disabled="isSending || !isPhoneValid"
            @click.prevent="sendVerificationCode"
            overrideClass="!min-w-0 !w-auto !px-4 !py-2 !text-sm !bg-[#EF7C8E] group-hover:!bg-[#E96C7E]"
          >
            {{ isSending ? '발송 중...' : '인증' }}
          </BaseButton>
        </div>
        <p
          v-if="phoneMessage"
          class="text-xs mt-1"
          :class="{ 'text-green-500': isPhoneValid, 'text-red-500': !isPhoneValid }"
        >
          {{ phoneMessage }}
        </p>
      </div>

      <!-- 인증번호 입력 -->
      <div class="flex flex-col w-full" v-if="showVerification">
        <label for="authCode" class="text-sm font-medium">인증번호</label>
        <div class="flex items-center gap-2 group">
          <BaseInput
            id="authCode"
            v-model="authCode"
            type="text"
            placeholder="인증번호 입력"
            class="flex-grow"
          />
          <BaseButton
            textColor="text-white"
            size="sm"
            :disabled="isVerifying"
            @click.prevent="verifyCode"
            overrideClass="!min-w-0 !w-auto !px-4 !py-2 !text-sm !bg-[#EF7C8E] group-hover:!bg-[#E96C7E]"
          >
            {{ isVerifying ? '확인 중...' : '확인' }}
          </BaseButton>
        </div>
        <p
          v-if="authVerificationMessage"
          class="text-xs mt-1"
          :class="{
            'text-green-500': authVerificationSuccess,
            'text-red-500': !authVerificationSuccess,
          }"
        >
          {{ authVerificationMessage }}
        </p>
      </div>

      <!-- 버튼 -->
      <div class="flex flex-row w-full justify-center mt-4 gap-4">
        <BaseButton
          class="flex-1 !min-w-full"
          textColor="text-gray-700"
          size="md"
          @click="goBack"
          overrideClass="!bg-[#D1D5DB] hover:!bg-[#6B7280]"
        >
          이전
        </BaseButton>

        <BaseButton
          class="flex-1 !min-w-full"
          textColor="text-white"
          size="md"
          type="submit"
          :disabled="!isFormValid || isSubmitting"
          overrideClass="!bg-[#EF7C8E] hover:!bg-[#E96C7E]"
          @click="handleSubmit"
        >
          가입하기
        </BaseButton>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { oauthSignUp } from '../api/auth';
import BaseLogo from '../components/BaseLogo.vue';
import BaseText from '../components/BaseText.vue';
import BaseInput from '../components/BaseInput.vue';
import BaseButton from '../components/BaseButton.vue';
import logoSrc from '../assets/logi_nofont.svg';
import { isDuplicateNickname, isDuplicatePhone } from '../api/auth';

const router = useRouter();
const route = useRoute();

// 상태 관리
const formData = ref({
  name: '',
  nickname: '',
  gender: '',
  birthday: '',
  phone: '',
  email: '',
  provider: '',
});

const isSending = ref(false);
const isVerifying = ref(false);
const showVerification = ref(false);
const verificationMessage = ref('');
const authCode = ref('');
const authVerificationSuccess = ref(false);
const authVerificationMessage = ref('');

// URL 파라미터에서 정보 가져오기
onMounted(() => {
  try {
    const email = decodeURIComponent(route.query.email || '');
    const name = decodeURIComponent(route.query.name || '');
    const provider = route.query.provider;

    if (!email || !name || !provider) {
      throw new Error('필수 파라미터가 누락되었습니다.');
    }

    formData.value = {
      ...formData.value,
      email,
      name,
      provider,
    };
  } catch (error) {
    console.error('초기화 중 오류 발생:', error);
    router.push('/signin');
  }
});

const nicknameMessage = ref('');
const isNicknameValid = ref(false);

// 닉네임 유효성 검사 및 중복 체크
const validateNickname = async (nickname) => {
  if (!nickname) {
    nicknameMessage.value = '닉네임을 입력해주세요.';
    isNicknameValid.value = false;
    return;
  }

  try {
    const isDuplicate = await isDuplicateNickname(nickname);
    if (isDuplicate) {
      nicknameMessage.value = '이미 사용 중인 닉네임입니다.';
      isNicknameValid.value = false;
    } else {
      nicknameMessage.value = '사용 가능한 닉네임입니다.';
      isNicknameValid.value = true;
    }
  } catch (error) {
    nicknameMessage.value = error.message;
    isNicknameValid.value = false;
  }
};

// watch를 사용한 실시간 닉네임 체크 (디바운스 적용)
watch(
  () => formData.value.nickname,
  (newNickname) => {
    if (newNickname) {
      validateNickname(newNickname);
    }
  },
  { debounce: 500 }
);

// blur 이벤트 핸들러 추가
const handleNicknameBlur = () => {
  if (formData.value.nickname) {
    validateNickname(formData.value.nickname);
  }
};

const phoneMessage = ref('');
const isPhoneValid = ref(false);

// 전화번호 유효성 검사 및 중복 체크
const validatePhone = async (phone) => {
  if (!phone) {
    phoneMessage.value = '전화번호를 입력해주세요.';
    isPhoneValid.value = false;
    return;
  }

  const phoneRegex = /^01[0-9]{8,9}$/;
  if (!phoneRegex.test(phone)) {
    phoneMessage.value = '올바른 전화번호 형식이 아닙니다.';
    isPhoneValid.value = false;
    return;
  }

  try {
    const isDuplicate = await isDuplicatePhone(phone);
    if (isDuplicate) {
      phoneMessage.value = '이미 사용 중인 전화번호입니다.';
      isPhoneValid.value = false;
    } else {
      phoneMessage.value = '사용 가능한 전화번호입니다.';
      isPhoneValid.value = true;
    }
  } catch (error) {
    console.error('전화번호 검증 중 오류:', error);
    phoneMessage.value = '전화번호 확인 중 오류가 발생했습니다.';
    isPhoneValid.value = false;
  }
};

// watch를 사용한 실시간 전화번호 체크 (디바운스 적용)
watch(
  () => formData.value.phone,
  (newPhone) => {
    if (newPhone) {
      validatePhone(newPhone);
    } else {
      phoneMessage.value = '';
      isPhoneValid.value = false;
    }
  },
  { debounce: 500 }
);

const handlePhoneBlur = () => {
  if (formData.value.phone) {
    validatePhone(formData.value.phone);
  }
};

// 폼 유효성 검사
const isFormValid = computed(() => {
  return (
    formData.value.name &&
    formData.value.nickname &&
    isNicknameValid.value &&
    formData.value.gender &&
    formData.value.birthday &&
    formData.value.phone &&
    authVerificationSuccess.value
  );
});

// 전화번호 인증번호 발송
const sendVerificationCode = async () => {
  try {
    isSending.value = true;
    // TODO: 전화번호 인증 API 호출
    await new Promise((resolve) => setTimeout(resolve, 1000)); // 임시 딜레이
    showVerification.value = true;
    verificationMessage.value = '인증번호가 발송되었습니다.';
  } catch (error) {
    verificationMessage.value = '인증번호 발송에 실패했습니다.';
  } finally {
    isSending.value = false;
  }
};

// 인증번호 확인
const verifyCode = async () => {
  try {
    isVerifying.value = true;
    // TODO: 인증번호 확인 API 호출
    await new Promise((resolve) => setTimeout(resolve, 1000)); // 임시 딜레이
    authVerificationSuccess.value = true;
    authVerificationMessage.value = '인증이 완료되었습니다.';
  } catch (error) {
    authVerificationSuccess.value = false;
    authVerificationMessage.value = '인증번호가 올바르지 않습니다.';
  } finally {
    isVerifying.value = false;
  }
};

// 이전 버튼 처리
const goBack = () => {
  router.push('/');
};

const isSubmitting = ref(false);

// 폼 제출 처리
const handleSubmit = async () => {
  if (isSubmitting.value) return;
  isSubmitting.value = true;

  try {
    const formattedBirthday = formData.value.birthday.replace(/-/g, '');
    const response = await oauthSignUp(
      {
        email: formData.value.email,
        name: formData.value.name,
        nickname: formData.value.nickname,
        gender: formData.value.gender,
        birthday: formattedBirthday,
        phone: formData.value.phone,
      },
      formData.value.provider
    );

    // response.result에서 토큰 정보 접근
    if (response?.result?.accessToken && response?.result?.refreshToken) {
      localStorage.setItem('accessToken', response.result.accessToken);
      localStorage.setItem('refreshToken', response.result.refreshToken);
      await router.push('/');
    } else {
      throw new Error('토큰 정보를 찾을 수 없습니다');
    }
  } catch (error) {
    console.error('회원가입 실패:', error);
  } finally {
    isSubmitting.value = false;
  }
};
</script>
