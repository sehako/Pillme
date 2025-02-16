<template>
  <div class="flex flex-col justify-center items-center min-h-screen px-4">
    <!-- ✅ 로고 -->
    <BaseLogo :src="logoSrc" size="md" />

    <!-- ✅ BaseText 컴포넌트 사용 -->
    <BaseText highlightText="PILLME" />

    <div class="w-full max-w-xs md:max-w-sm space-y-4 mt-6">
      <!-- 이메일 입력 -->
      <div class="flex flex-col w-full">
        <BaseInput
          id="email"
          v-model="email"
          type="email"
          placeholder="회원가입 이메일"
          class="w-full"
        />
        <p v-if="emailError" class="mt-1 text-red-500 text-sm">{{ emailError }}</p>
      </div>

      <!-- 이메일 인증 버튼 -->
      <div class="flex flex-col w-full" v-if="!isEmailSent">
        <BaseButton
          @click="handleEmailVerification"
          textColor="text-white"
          size="sm"
          :disabled="isLoading || isEmailVerified"
          overrideClass="w-full !bg-[#EF7C8E] group-hover:!bg-[#E96C7E] disabled:opacity-50"
        >
          {{ isLoading ? '처리중...' : '인증번호 발송' }}
        </BaseButton>
      </div>

      <!-- 인증번호 입력 -->
      <div class="flex flex-col w-full" v-if="isEmailSent && !isEmailVerified">
        <div class="flex items-center gap-2 group">
          <BaseInput
            id="authCode"
            v-model="authCode"
            type="text"
            placeholder="인증번호 입력"
            class="flex-grow"
          />
          <BaseButton
            @click="handleVerifyCode"
            textColor="text-white"
            size="sm"
            :disabled="isLoading"
            overrideClass="!min-w-0 !w-auto !px-4 !py-2 !text-sm !bg-[#EF7C8E] group-hover:!bg-[#E96C7E] disabled:opacity-50"
          >
            {{ isLoading ? '처리중...' : '확인' }}
          </BaseButton>
        </div>
      </div>

      <!-- 인증 완료 메시지 -->
      <div v-if="isEmailVerified" class="w-full">
        <p class="text-green-500 text-sm">인증번호 확인이 완료되었습니다.</p>
      </div>

      <!-- 전화번호 입력 -->
      <div class="flex flex-col w-full">
        <BaseInput
          id="phoneNumber"
          v-model="phoneNumber"
          type="tel"
          placeholder="회원가입 전화번호"
          class="w-full"
        />
      </div>

      <!-- 임시 비밀번호 발송 버튼 -->
      <div class="flex justify-center">
        <BaseButton
          textColor="text-white"
          size="md"
          :disabled="!isEmailVerified"
          overrideClass="w-full !bg-[#EF7C8E] hover:!bg-[#E96C7E] disabled:opacity-50"
          @click="handleSubmit"
        >
          임시 비밀번호 발송
        </BaseButton>
      </div>
    </div>

    <p class="mt-8 text-sm text-gray-600">
      <a href="/loginselection" class="hover:underline">로그인 페이지로 돌아가기</a>
    </p>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import {
  requestPasswordResetVerification,
  requestTemporaryPassword,
  verifyEmailCode,
} from '../api/auth';
import BaseButton from '../components/BaseButton.vue';
import BaseInput from '../components/BaseInput.vue';
import BaseLogo from '../components/BaseLogo.vue';
import BaseText from '../components/BaseText.vue';

import logoSrc from '../assets/logi_nofont.svg';

const router = useRouter();

const phoneNumber = ref('');
const email = ref('');
const isLoading = ref(false);
const isEmailSent = ref(false);
const authCode = ref('');
const isEmailVerified = ref(false);
const emailError = ref('');
const verificationSuccess = ref(false);

const handleEmailVerification = async () => {
  try {
    isLoading.value = true;
    emailError.value = '';
    await requestPasswordResetVerification(email.value);
    isEmailSent.value = true;
  } catch (error) {
    console.error('인증 메일 발송 실패:', error);
    emailError.value = '인증번호 발송에 실패했습니다.';
    isEmailSent.value = false;
  } finally {
    isLoading.value = false;
  }
};

const handleVerifyCode = async () => {
  try {
    isLoading.value = true;
    await verifyEmailCode(email.value, authCode.value);
    isEmailVerified.value = true;
    isEmailSent.value = false;
  } catch (error) {
    console.error('인증 코드 인증 실패:', error);
    emailError.value = '인증번호가 일치하지 않습니다.';
  } finally {
    isLoading.value = false;
  }
};

const handleSubmit = async () => {
  try {
    isLoading.value = true;
    await requestTemporaryPassword(email.value, phoneNumber.value);

    // 성공 메시지 표시
    alert('임시 비밀번호가 발급되었습니다. 이메일을 확인해주세요.');

    // 로그인 페이지로 리다이렉션
    router.push('/loginselection');
  } catch (error) {
    console.error('임시 비밀번호 발급 실패:', error);
    alert('임시 비밀번호 발급에 실패했습니다. 다시 시도해주세요.');
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
/* ✅ 버튼 간격 */
.gap-4 {
  gap: 1rem;
}
</style>
