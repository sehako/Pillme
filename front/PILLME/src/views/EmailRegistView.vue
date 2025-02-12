<template>
  <div class="flex flex-col justify-center items-center p-4">
    <BaseLogo :src="logoSrc" size="md" />
    <BaseText highlightText="PILLME 이메일 회원가입" textBefore="" />

    <form class="max-w-xs md:max-w-sm space-y-4" @submit.prevent="handleSubmit">
      <!-- ✅ 이메일 입력 -->
      <div class="flex flex-col w-full">
        <label for="email" class="text-sm font-medium">이메일</label>
        <div class="flex items-center gap-2 group">
          <BaseInput id="email" v-model="email" type="email" placeholder="이메일 입력" class="flex-grow" />
          <BaseButton
            @click.prevent="sendVerificationCode"
            :disabled="isSending || email.trim() === ''"
            textColor="text-white"
            size="sm"
            overrideClass="!min-w-0 !w-auto !px-4 !py-2 !text-sm !bg-[#EF7C8E] group-hover:!bg-[#E96C7E]"
          >
            {{ isSending ? "발송 중..." : "인증" }}
          </BaseButton>
        </div>
        <p v-if="emailSent" class="text-green-500 text-xs mt-1">인증번호가 이메일로 전송되었습니다.</p>
        <p v-if="emailError" class="text-red-500 text-xs mt-1">{{ emailError }}</p>
      </div>

      <!-- ✅ 이메일 인증번호 입력 -->
      <div class="flex flex-col w-full">
        <label for="emailAuthCode" class="text-sm font-medium">이메일 인증번호</label>
        <div class="flex items-center gap-2 group">
          <BaseInput id="emailAuthCode" v-model="emailAuthCode" type="text" placeholder="인증번호 입력" class="flex-grow" />
          <BaseButton
            @click.prevent="verifyCode"
            :disabled="!emailSent || isVerifying || emailAuthCode.trim() === ''"
            textColor="text-white"
            size="sm"
            overrideClass="!min-w-0 !w-auto !px-4 !py-2 !text-sm !bg-[#EF7C8E] group-hover:!bg-[#E96C7E]"
          >
            {{ isVerifying ? "확인 중..." : "확인" }}
          </BaseButton>
        </div>
        <p v-if="verificationSuccess" class="text-green-500 text-xs mt-1">이메일 인증이 완료되었습니다.</p>
        <p v-if="verificationError" class="text-red-500 text-xs mt-1">{{ verificationError }}</p>
      </div>

      <!-- ✅ 비밀번호 입력 -->
      <div class="flex flex-col w-full">
        <label for="password" class="text-sm font-medium">비밀번호</label>
        <BaseInput 
          id="password"
          v-model="password"
          type="password"
          name="new-password"
          placeholder="비밀번호 입력"
          autocomplete="new-password"
        />
        <p v-if="passwordError" class="text-red-500 text-xs mt-1">{{ passwordError }}</p>
      </div>

      <!-- ✅ 비밀번호 확인 -->
      <div class="flex flex-col w-full">
        <label for="confirmPassword" class="text-sm font-medium">비밀번호 확인</label>
        <BaseInput 
          id="confirmPassword"
          v-model="confirmPassword"
          type="password"
          name="confirm-password"
          placeholder="비밀번호 확인"
          autocomplete="new-password"
        />
        <p v-if="passwordMismatch" class="text-red-500 text-xs mt-1">비밀번호가 일치하지 않습니다.</p>
      </div>

      <!-- ✅ 다음 버튼 -->
      <div class="flex justify-center mt-4">
        <BaseButton
          class="w-full"
          textColor="text-white"
          size="md"
          type="submit"
          :disabled="!canSubmit"
          overrideClass="!min-w-0 !bg-[#EF7C8E] hover:!bg-[#E96C7E]"
        >
          다음
        </BaseButton>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
// import { requestSmsVerification } from "../api/auth";
import { requestEmailVerification,verifyEmailCode } from "../api/auth";
import BaseButton from "../components/BaseButton.vue";
import BaseInput from "../components/BaseInput.vue";
import BaseLogo from "../components/BaseLogo.vue";
import BaseText from "../components/BaseText.vue";
import logoSrc from "../assets/logi_nofont.svg";

const router = useRouter();
// import { addLocalMember } from '../api/addlocalmember';


const email = ref("");
const emailAuthCode = ref("");
const password = ref("");
const confirmPassword = ref("");
const isSending = ref(false);
const emailSent = ref(false);
const emailError = ref(null);
const isVerifying = ref(false);
const verificationSuccess = ref(false);
const verificationError = ref(null);
const passwordError = ref(null);

// ✅ 비밀번호 유효성 검사 함수
const validatePassword = (value) => {
  const minLength = /.{12,}/; // 12자 이상
  const upperCase = /[A-Z]/; // 대문자
  const lowerCase = /[a-z]/; // 소문자
  const number = /[0-9]/; // 숫자
  const specialChar = /[\W_]/; // 특수문자

  return (
    minLength.test(value) &&
    upperCase.test(value) &&
    lowerCase.test(value) &&
    number.test(value) &&
    specialChar.test(value)
  );
};

const passwordMismatch = computed(() => password.value && confirmPassword.value && password.value !== confirmPassword.value);

const canSubmit = computed(() => {
  if (!verificationSuccess.value || password.value.trim() === '' || confirmPassword.value.trim() === '') {
    return false;
  }

  if (!validatePassword(password.value)) {
    passwordError.value = "비밀번호는 12자 이상이며, 영문 대소문자, 숫자, 특수문자를 포함해야 합니다.";
    return false;
  } else {
    passwordError.value = null;
  }

  return !passwordMismatch.value;
});

const sendVerificationCode = async () => {
  if (!email.value.trim()) {
    emailError.value = "이메일을 입력해주세요.";
    return;
  }

  isSending.value = true;
  emailError.value = null;
  
  try {
    console.log("📨 이메일 인증 요청:", email.value.trim());
    await requestEmailVerification(email.value.trim());
    emailSent.value = true;
  } catch (error) {
    console.error("🚨 이메일 인증 요청 실패:", error);
    emailError.value = "인증번호 전송에 실패했습니다.";
  } finally {
    isSending.value = false;
  }
};

const verifyCode = async () => {
  if (!email.value.trim() || !emailAuthCode.value.trim()) {
    verificationError.value = "이메일과 인증번호를 입력하세요.";
    return;
  }

  isVerifying.value = true;
  verificationError.value = null;

  console.log("📩 인증번호 검증 시작:", { email: email.value.trim(), code: emailAuthCode.value.trim() });

  try {
    const success = await verifyEmailCode(email.value.trim(), emailAuthCode.value.trim());

    if (success) {
      verificationSuccess.value = true;
      console.log("✅ 이메일 인증 성공!");
    } else {
      verificationError.value = "인증번호가 올바르지 않습니다.";
    }
  } catch (error) {
    console.error("🚨 인증번호 검증 실패:", error.message);
    verificationError.value = "인증번호가 올바르지 않습니다.";
  } finally {
    isVerifying.value = false;
  }
};

const handleSubmit = () => {
  console.log("🚀 회원가입 진행 중, 데이터:", { email: email.value, password: password.value });
  router.push({ path: "/afteraccount", query: { email: email.value, password: password.value } });
};
</script>
