import { defineStore } from "pinia";
import apiClient from "../api";
import { requestSmsVerification, verifySmsCode } from "../api/auth";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    user: JSON.parse(localStorage.getItem("user")) || null,
    accessToken: localStorage.getItem("accessToken") || null,
  }),

 actions: {
    // ✅ 이메일 인증번호 검증 API 요청 (Access Token 저장 추가)
    // async verifyEmail(email, code) {
    //   try {
    //     console.log("✅ 이메일 인증번호 확인 요청:", { email, code });
    //     const response = await apiClient.post("/api/v1/auth/email/verify", { email, code });

    //     console.log("🛠 서버 응답:", response.data);

    //     if (!response.data) {
    //       throw new Error("서버 응답이 없습니다.");
    //     }

    //     if (response.data.code === 2000) {
    //       console.log("✅ 이메일 인증 성공!");

    //       if (response.data.accessToken) {
    //         this.accessToken = response.data.accessToken;
    //         localStorage.setItem("accessToken", response.data.accessToken);
    //         apiClient.defaults.headers.common["Authorization"] = `Bearer ${response.data.accessToken}`;
    //         console.log("🔐 Access Token 저장 완료:", this.accessToken);
    //       }

    //       return true;
    //     } else {
    //       throw new Error(response.data.message || "이메일 인증 실패");
    //     }
    //   } catch (error) {
    //     console.error("🚨 이메일 인증 실패:", error.message);
    //     return false;
    //   }
    // },


    // ✅ SMS 인증번호 요청 (api/auth 함수 활용)
    async requestPhoneVerification(phoneNumber) {
      try {
        console.log("📨 SMS 인증번호 요청:", phoneNumber);
        const response = await requestSmsVerification(phoneNumber);
        console.log("🛠 서버 응답:", response.data);
        if (response.data.code === 2000) {
          console.log("✅ SMS 인증번호 발송 성공");
          return true;
        } else {
          throw new Error(response.data.message || "SMS 인증번호 요청 실패");
        }
      } catch (error) {
        console.error("🚨 SMS 인증번호 요청 실패:", error.response?.data || error);
        return false;
      }
    },

    // ✅ SMS 인증번호 확인 (api/auth 함수 활용)
    async verifyPhoneCode(phoneNumber, code) {
      try {
        console.log("✅ SMS 인증번호 확인 요청:", { phoneNumber, code });
        const response = await verifySmsCode(phoneNumber, code);
        console.log("🛠 서버 응답:", response.data);
        if (response.data.code === 2000) {
          console.log("✅ 휴대폰 인증 성공");
          return true;
        } else {
          throw new Error(response.data.message || "휴대폰 인증 실패");
        }
      } catch (error) {
        console.error("🚨 휴대폰 인증 실패:", error.message);
        return false;
      }
    },
  },
});
