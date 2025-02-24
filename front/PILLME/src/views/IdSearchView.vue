<template>
  <div class="flex flex-col items-center justify-center min-h-screen px-4">
    <BaseLogo :src="logoSrc" size="md" />
    <BaseText highlightText="PILLME" />

    <div class="w-full max-w-xs mt-6 space-y-4 md:max-w-sm">
      <!-- 에러 메시지 표시 -->
      <div v-if="errorMessage" class="text-sm text-center text-red-500">
        {{ errorMessage }}
      </div>

      <!-- 전화번호 입력 -->
      <div class="flex flex-col w-full">
        <div class="flex items-center gap-2 group">
          <BaseInput
            id="phoneNumber"
            v-model="phoneNumber"
            type="tel"
            placeholder="전화번호 입력"
            class="flex-grow"
            :disabled="isPhoneVerified"
          />
          <BaseButton
            @click="handleSmsVerification"
            textColor="text-white"
            size="sm"
            :disabled="isLoading || isPhoneVerified"
            overrideClass="!min-w-0 !w-auto !px-4 !py-2 !text-sm !bg-[#EF7C8E] group-hover:!bg-[#E96C7E] disabled:opacity-50"
          >
            {{ isLoading ? '처리중...' : '인증' }}
          </BaseButton>
        </div>
      </div>

      <!-- 인증번호 입력 -->
      <div class="flex flex-col w-full" v-if="isSmsSent">
        <div class="flex items-center gap-2 group">
          <BaseInput
            id="authCode"
            v-model="authCode"
            type="text"
            placeholder="인증번호 입력"
            class="flex-grow"
            :disabled="isPhoneVerified"
          />
          <BaseButton
            @click="handleVerifyCode"
            textColor="text-white"
            size="sm"
            :disabled="isLoading || isPhoneVerified"
            overrideClass="!min-w-0 !w-auto !px-4 !py-2 !text-sm !bg-[#EF7C8E] group-hover:!bg-[#E96C7E] disabled:opacity-50"
          >
            {{ isLoading ? '처리중...' : '확인' }}
          </BaseButton>
        </div>
      </div>

      <!-- 이메일 찾기 버튼 -->
      <BaseButton
        @click="findUserEmail"
        textColor="text-white"
        size="lg"
        :disabled="!isPhoneVerified || isLoading"
        overrideClass="w-full mt-4 !bg-[#EF7C8E] hover:!bg-[#E96C7E] disabled:opacity-50"
      >
        {{ isLoading ? '처리중...' : '이메일 찾기' }}
      </BaseButton>
    </div>

    <p class="mt-8 text-sm text-gray-600 forgot-account">
      <a href="/loginselection" class="hover:underline">로그인 페이지로 돌아가기</a>
    </p>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { findEmail, requestSmsVerification, verifySmsCode } from '../api/auth';
import BaseButton from '../components/BaseButton.vue';
import BaseInput from '../components/BaseInput.vue';
import BaseLogo from '../components/BaseLogo.vue';
import BaseText from '../components/BaseText.vue';
import logoSrc from '../assets/logi_nofont.svg';

const phoneNumber = ref('');
const authCode = ref('');
const isPhoneVerified = ref(false);
const isSmsSent = ref(false);
const errorMessage = ref('');
const isLoading = ref(false);
const router = useRouter();

// // SMS 인증 요청 (테스트용)
// const handleSmsVerification = async () => {
//   if (!phoneNumber.value) {
//     errorMessage.value = '전화번호를 입력해주세요.';
//     return;
//   }

//   try {
//     isLoading.value = true;
//     // API 호출 대신 즉시 성공 처리
//     console.log('📨 SMS 인증번호 발송 성공 (테스트)');
//     errorMessage.value = '인증번호가 발송되었습니다. (테스트: 123456)';
//     isSmsSent.value = true;
//   } catch (error) {
//     console.error('❌ SMS 인증 요청 실패:', error);
//     errorMessage.value = error.response?.data?.message || '인증번호 발송에 실패했습니다.';
//   } finally {
//     isLoading.value = false;
//   }
// };

// // 인증번호 확인 (테스트용)
// const handleVerifyCode = async () => {
//   if (!authCode.value) {
//     errorMessage.value = '인증번호를 입력해주세요.';
//     return;
//   }

//   try {
//     isLoading.value = true;
//     // 테스트용 인증번호 확인
//     if (authCode.value === '123456') {
//       isPhoneVerified.value = true;
//       errorMessage.value = '인증이 완료되었습니다.';
//     } else {
//       errorMessage.value = '인증번호가 올바르지 않습니다.';
//     }
//   } catch (error) {
//     console.error('❌ 인증번호 확인 실패:', error);
//     errorMessage.value = error.response?.data?.message || '인증번호가 올바르지 않습니다.';
//   } finally {
//     isLoading.value = false;
//   }
// };

// SMS 인증 요청
const handleSmsVerification = async () => {
  if (!phoneNumber.value) {
    errorMessage.value = '전화번호를 입력해주세요.';
    return;
  }

  try {
    isLoading.value = true;
    const success = await requestSmsVerification(phoneNumber.value);
    if (success) {
      // response.data.isSuccess 대신 success로 확인
      // console.log('📨 SMS 인증번호 발송 성공');
      errorMessage.value = '인증번호가 발송되었습니다.';
      isSmsSent.value = true;
    }
  } catch (error) {
    console.error('❌ SMS 인증 요청 실패:', error);
    errorMessage.value = error.response?.data?.message || '인증번호 발송에 실패했습니다.';
  } finally {
    isLoading.value = false;
  }
};

// 인증번호 확인
const handleVerifyCode = async () => {
  if (!authCode.value) {
    errorMessage.value = '인증번호를 입력해주세요.';
    return;
  }

  try {
    isLoading.value = true;
    const success = await verifySmsCode(phoneNumber.value, authCode.value);
    if (success) {
      // response.data.isSuccess 대신 success로 확인
      isPhoneVerified.value = true;
      errorMessage.value = '인증이 완료되었습니다.';
    }
  } catch (error) {
    console.error('❌ 인증번호 확인 실패:', error);
    errorMessage.value = error.response?.data?.message || '인증번호가 올바르지 않습니다.';
  } finally {
    isLoading.value = false;
  }
};

// 이메일 찾기
const findUserEmail = async () => {
  if (!isPhoneVerified.value) {
    errorMessage.value = '휴대폰 인증이 필요합니다.';
    return;
  }

  try {
    isLoading.value = true;
    const response = await findEmail(phoneNumber.value);

    if (response.isSuccess) {
      const { email, provider } = response.result;
      router.push({
        path: '/idfound',
        query: { email, provider }, // params 대신 query 사용
      });
    }
  } catch (error) {
    console.error('❌ 이메일 찾기 실패:', error);
    if (error.response?.status === 404) {
      errorMessage.value = '해당 정보로 가입된 계정을 찾을 수 없습니다.';
    } else {
      errorMessage.value = error.response?.data?.message || '이메일 찾기에 실패했습니다.';
    }
  } finally {
    isLoading.value = false;
  }
};
</script>
