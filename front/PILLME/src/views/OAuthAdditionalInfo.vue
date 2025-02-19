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

      <!-- 생년월일 입력 (Datepicker 플러그인 사용, 필수 파라미터 체크) -->
      <div class="flex flex-col w-full">
        <label for="birthdate" class="text-sm font-medium">생년월일</label>
        <Datepicker
          id="birthdate"
          v-model="formData.birthday"
          :locale="ko"
          :format="formatDate"
          placeholder="생년월일을 선택하세요"
          :editable="false"
          class="cursor-pointer"
        />
      </div>

      <!-- 전화번호 입력 -->
      <div class="flex flex-col w-full">
        <label for="phoneNumber" class="text-sm font-medium">전화번호</label>
        <div class="flex items-center gap-2 group">
          <BaseInput
            id="phoneNumber"
            v-model="formData.phone"
            type="tel"
            placeholder="01000000000 형식"
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
          v-if="verificationMessage"
          class="text-xs mt-1"
          :class="{ 'text-green-500': showVerification, 'text-red-500': !showVerification }"
        >
          {{ verificationMessage }}
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
 <div class="flex flex-col sm:flex-row w-full gap-3 sm:gap-4 mt-6">
        <BaseButton
          class="w-full sm:flex-1"
          textColor="text-gray-700"
          size="md"
          @click="goBack"
          overrideClass="!bg-[#D1D5DB] hover:!bg-[#6B7280] !min-w-0"
        >
          이전
        </BaseButton>

        <BaseButton
          class="w-full sm:flex-1"
          textColor="text-white"
          size="md"
          type="submit"
          :disabled="!isFormValid || isSubmitting"
          overrideClass="!bg-[#EF7C8E] hover:!bg-[#E96C7E] !min-w-0"
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
import Datepicker from 'vue3-datepicker';
import { ko } from 'date-fns/locale';
import {
  isDuplicateNickname,
  isDuplicatePhone,
  requestSmsVerification,
  verifySmsCode,
} from '../api/auth';
import { saveAccessToken } from '../utils/localForage';
import Cookies from 'js-cookie';


const router = useRouter();
const route = useRoute();

// 상태 관리 (생년월일은 formData.birthday에 저장)
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

// 폼 유효성 검사 (생년월일(birthday) 필드도 필수)
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

// 날짜 포맷 지정 함수 (Datepicker에서 사용)
const formatDate = (date) => {
  if (!date) return '';
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, '0');
  const dd = String(date.getDate()).padStart(2, '0');
  return `${yyyy}-${mm}-${dd}`;
};

// 전화번호 인증번호 발송
const sendVerificationCode = async () => {
  isSending.value = true;
  verificationMessage.value = null;
  try {
    const success = await requestSmsVerification(formData.value.phone); // phone.value -> formData.value.phone
    if (success) {
      verificationMessage.value = 'SMS 인증번호 발송 성공';
      showVerification.value = true; // 인증번호 입력 필드 표시
    } else {
      throw new Error('SMS 인증번호 요청 실패');
    }
  } catch (error) {
    console.error('🚨 SMS 인증번호 요청 실패:', error);
    verificationMessage.value = 'SMS 인증번호 전송 실패. 다시 시도해주세요.';
  } finally {
    isSending.value = false;
  }
};

// 인증번호 확인 함수
const verifyCode = async () => {
  isVerifying.value = true;
  authVerificationMessage.value = null;
  try {
    const success = await verifySmsCode(formData.value.phone, authCode.value); // phone.value -> formData.value.phone
    if (success) {
      authVerificationSuccess.value = true;
      authVerificationMessage.value = '전화번호 인증이 완료되었습니다.';
    } else {
      throw new Error('휴대폰 인증 실패');
    }
  } catch (error) {
    console.error('🚨 인증번호 확인 실패:', error);
    authVerificationSuccess.value = false;
    authVerificationMessage.value = '인증에 실패하였습니다. 다시 시도해주세요.';
  } finally {
    isVerifying.value = false;
  }
};

// 이전 버튼 처리
const goBack = () => {
  router.push('/');
};

const isSubmitting = ref(false);

// 폼 제출 처리 (생년월일이 선택되었는지 체크)
const handleSubmit = async () => {
  if (isSubmitting.value) return;
  isSubmitting.value = true;



  try {
    const formattedBirthday = 
      typeof formData.value.birthday === 'object'
      ? formatDate(formData.value.birthday).replace(/-/g, '')
      : formData.value.birthday.replace(/-/g, '');

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

    // ✅ 응답 검증 강화
    if (!response?.result) {
      throw new Error('서버 응답이 올바르지 않습니다');
    }

    const { accessToken, refreshToken } = response.result;
    if (!accessToken || !refreshToken) {
      throw new Error('토큰 정보가 누락되었습니다');
    }

    // ✅ 토큰 저장 순서 보장
    await Promise.all([
      saveAccessToken(accessToken),
      new Promise(resolve => {
        localStorage.setItem('accessToken', accessToken);
        Cookies.set('refreshToken', refreshToken, {
          secure: true,
          sameSite: 'Strict'
        });
        resolve();
      })
    ]);

    // ✅ 라우팅 전 토큰 저장 확인
    const savedToken = localStorage.getItem('accessToken');
    if (!savedToken) {
      throw new Error('토큰 저장에 실패했습니다');
    }

    await router.push('/');
  } catch (error) {
    console.error('회원가입 실패:', error);
    alert(error.message || '회원가입 처리 중 오류가 발생했습니다');
  } finally {
    isSubmitting.value = false;
  }
};
</script>
