<template>
  <div class="flex flex-col justify-center items-center p-4">
    <!-- 로고, BaseText 등 UI 컴포넌트 -->
    <BaseLogo :src="logoSrc" size="md" />
    <BaseText highlightText="PILLME" textBefore="이메일 인증 완료" />

    <!-- 가입 폼 -->
    <form class="max-w-xs md:max-w-sm space-y-4" @submit.prevent="handleSubmit">
      <!-- 이름 입력 -->
      <div class="flex flex-col w-full">
        <label for="name" class="text-sm font-medium">이름</label>
        <BaseInput id="name" v-model="name" type="text" placeholder="이름을 입력해주세요." />
      </div>

      <!-- 닉네임 입력 -->
      <div class="flex flex-col w-full">
        <label for="nickname" class="text-sm font-medium">닉네임</label>
        <BaseInput id="nickname" v-model="nickname" type="text" placeholder="닉네임을 입력해주세요." />
      </div>

      <!-- 성별 선택 -->
      <div class="flex flex-col w-full">
        <label class="text-sm font-medium">성별</label>
        <div class="flex w-full gap-2">
          <BaseButton
            class="!min-w-28"
            :overrideClass="gender === 'M' ? '!bg-[#EF7C8E] text-white' : 'bg-gray-300 text-gray-700'"
            type="button"
            @click="gender = 'M'"
          >
            남
          </BaseButton>
          <BaseButton
            class="!min-w-28"
            :overrideClass="gender === 'F' ? '!bg-[#EF7C8E] text-white' : 'bg-gray-300 text-gray-700'"
            type="button"
            @click="gender = 'F'"
          >
            여
          </BaseButton>
        </div>
      </div>

      <!-- 생년월일 입력 -->
      <div class="flex flex-col w-full">
        <label for="birthdate" class="text-sm font-medium">생년월일</label>
        <Datepicker
  v-model="birthday"
  :locale="ko"
  :format="formatDate"
  placeholder="생년월일을 선택하세요"
  :editable="false"
  class="cursor-pointer"
/>

      </div>

      <!-- 이메일 입력 (비활성화) -->
      <div class="flex flex-col w-full">
        <label for="email" class="text-sm font-medium">이메일</label>
        <BaseInput id="email" v-model="email" type="email" disabled class="bg-gray-200" />
      </div>

      <!-- 비밀번호 입력 (비활성화) -->
      <div class="flex flex-col w-full">
        <label for="password" class="text-sm font-medium">비밀번호</label>
        <BaseInput id="password" v-model="password" type="password" disabled class="bg-gray-200" />
      </div>

      <!-- 전화번호 입력 -->
      <div class="flex flex-col w-full">
        <label for="phoneNumber" class="text-sm font-medium">전화번호</label>
        <div class="flex items-center gap-2 group">
          <BaseInput
            id="phoneNumber"
            v-model="phone"
            type="tel"
            placeholder="01000000000 형식"
            class="flex-grow"
          />
          <BaseButton
            textColor="text-white"
            size="sm"
            :disabled="isSending"
            @click="sendVerificationCode"
            overrideClass="!min-w-0 !w-auto !px-4 !py-2 !text-sm !bg-[#EF7C8E] group-hover:!bg-[#E96C7E]"
          >
            {{ isSending ? "발송 중..." : "인증" }}
          </BaseButton>
        </div>
        <p
          v-if="verificationMessage"
          class="text-xs mt-1"
          :class="{'text-green-500': verificationSuccess, 'text-red-500': !verificationSuccess}"
        >
          {{ verificationMessage }}
        </p>
      </div>

      <!-- 인증번호 입력 -->
      <div class="flex flex-col w-full">
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
            @click="verifyCode"
            overrideClass="!min-w-0 !w-auto !px-4 !py-2 !text-sm !bg-[#EF7C8E] group-hover:!bg-[#E96C7E]"
          >
            {{ isVerifying ? "확인 중..." : "확인" }}
          </BaseButton>
        </div>
        <p
          v-if="authVerificationMessage"
          class="text-xs mt-1"
          :class="{'text-green-500': authVerificationSuccess, 'text-red-500': !authVerificationSuccess}"
        >
          {{ authVerificationMessage }}
        </p>
      </div>

      <!-- 버튼들 가로 정렬 & 간격 추가 -->
      <div class="flex flex-row w-full justify-center mt-4 gap-4">
        <BaseButton
          class="flex-1 !min-w-fit"
          textColor="text-gray-700"
          size="md"
          @click="goBack"
          overrideClass="!bg-[#D1D5DB] hover:!bg-[#6B7280]"
        >
          이전
        </BaseButton>

        <BaseButton
          class="flex-1 !min-w-fit"
          textColor="text-white"
          size="md"
          type="submit"
          :disabled="!authVerificationSuccess"
          overrideClass="!bg-[#EF7C8E] hover:!bg-[#E96C7E]"
        >
          가입하기
        </BaseButton>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { requestSmsVerification,verifySmsCode } from "../api/auth";
// import { useAuthStore } from "../stores/auth"; // store에서 인증 관련 액션 사용
import apiClient from "../api"; // 가입 요청을 위한 apiClient 사용
import BaseButton from "../components/BaseButton.vue";
import BaseInput from "../components/BaseInput.vue";
import BaseLogo from "../components/BaseLogo.vue";
import BaseText from "../components/BaseText.vue";
import logoSrc from "../assets/logi_nofont.svg";
import Datepicker from "vue3-datepicker";
import { ko } from "date-fns/locale";
const router = useRouter();
const route = useRoute();
// const authStore = useAuthStore();

const name = ref("");
const nickname = ref("");
const gender = ref("");
const birthday = ref(null);   
const email = ref("");
const password = ref("");
const phone = ref("");
const authCode = ref("");
const isSending = ref(false);
const verificationMessage = ref(null);
const verificationSuccess = ref(false);
const isVerifying = ref(false);
const authVerificationMessage = ref(null);
const authVerificationSuccess = ref(false);

// 뒤로 가기 함수
const goBack = () => {
  router.back();
};

onMounted(() => {
  email.value = sessionStorage.getItem("signupEmail") || "";
  password.value = sessionStorage.getItem("signupPassword") || "";
});

const sendVerificationCode = async () => {
  isSending.value = true;
  verificationMessage.value = null;
  try {
    // console.log("📨 SMS 인증번호 요청 데이터:", { phoneNumber: phone.value });
    const success = await requestSmsVerification(phone.value);
    if (success) {
      verificationMessage.value = "SMS 인증번호 발송 성공";
      verificationSuccess.value = true;
    } else {
      throw new Error("SMS 인증번호 요청 실패");
    }
  } catch (error) {
    console.error("🚨 SMS 인증번호 요청 실패:", error);
    verificationMessage.value = "SMS 인증번호 전송 실패. 다시 시도해주세요.";
    verificationSuccess.value = false;
  } finally {
    isSending.value = false;
  }
};

// 인증번호 확인 함수
const verifyCode = async () => {
  isVerifying.value = true;
  authVerificationMessage.value = null;
  try {
    // console.log("📨 인증번호 확인 요청 데이터:", { phoneNumber: phone.value, code: authCode.value });
    const success = await verifySmsCode(phone.value, authCode.value);
    if (success) {
      authVerificationSuccess.value = true;
      authVerificationMessage.value = "전화번호 인증이 완료되었습니다.";
    } else {
      throw new Error("휴대폰 인증 실패");
    }
  } catch (error) {
    console.error("🚨 인증번호 확인 실패:", error);
    authVerificationSuccess.value = false;
    authVerificationMessage.value = "인증에 실패하였습니다. 다시 시도해주세요.";
  } finally {
    isVerifying.value = false;
  }
};
// 날짜 포맷 지정 함수 (선택사항)
const formatDate = (date) => {
  if (!date) return "";
  // 원하는 포맷에 맞춰 변환 (YYYY-MM-DD 형태)
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, "0");
  const dd = String(date.getDate()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd}`;
};
// 가입하기 버튼 클릭 시 실행 (가입 API 호출)
const handleSubmit = async () => {
  try {
    // Datepicker로 선택된 birthday.value가 Date 객체라면,
    // YYYYMMDD 형태로 변환
    let formattedBirthday = "";
    if (birthday.value instanceof Date) {
      const year = birthday.value.getFullYear();
      const month = String(birthday.value.getMonth() + 1).padStart(2, "0");
      const day = String(birthday.value.getDate()).padStart(2, "0");
      formattedBirthday = `${year}${month}${day}`;
    }

    const requestData = {
      email: email.value,
      password: password.value,
      name: name.value,
      nickname: nickname.value,
      gender: gender.value,
      phone: phone.value,
      birthday: formattedBirthday,
    };

    // console.log("📨 가입 요청 데이터:", requestData.value);
    const response = await apiClient.post("/api/v1/auth/signup", requestData, {
      headers: {
        "Content-Type": "application/json",
      },
    });
    // console.log("✅ 가입 요청 응답:", response.data);
    if (response.data.code === 2000) {
      alert("가입이 완료되었습니다.");
      router.push("/success");
    } else {
      throw new Error(response.data.message || "가입 실패");
    }
  } catch (error) {
    console.error("🚨 가입 요청 실패:", error.response?.data || error);
    alert("가입 요청 실패. 다시 시도해주세요.");
  }
};
</script>
