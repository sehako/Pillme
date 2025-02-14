import { defineStore } from "pinia";
import { refreshAccessTokenAPI } from "../api/auth";

export const useUserStore = defineStore("user", {
  state: () => ({
    user: null, // ✅ 유저 정보 저장
  }),
  actions: {
    setUser(userData) {
      console.log("✅ [DEBUG] Pinia에 유저 정보 저장됨:", userData);
      this.user = userData;
    },
    clearUser() {
      console.log("🚫 [DEBUG] 유저 정보 초기화");
      this.user = null;
    },
    async getMemberId() {
      // ✅ 이미 memberId가 있으면 반환
      if (this.user?.memberId) {
        console.log("🔍 [DEBUG] 기존 memberId 반환:", this.user.memberId);
        return this.user.memberId;
      }

      console.warn("⚠️ [DEBUG] memberId 없음. 액세스 토큰 갱신 시도...");
      
      try {
        // ✅ 액세스 토큰 갱신 (auth.js의 refreshAccessTokenAPI() 호출)
        const newTokenData = await refreshAccessTokenAPI();

        if (newTokenData?.result?.accessToken) {
          console.log("🔄 [DEBUG] 새 토큰으로 유저 정보 갱신 완료:", this.user);
          return this.user?.memberId || null; // ✅ 새로 업데이트된 memberId 반환
        } else {
          console.error("❌ [DEBUG] 액세스 토큰 갱신 실패. 로그인 필요.");
          return null;
        }
      } catch (error) {
        console.error("❌ [DEBUG] 리프레시 토큰 API 요청 실패:", error);
        return null;
      }
    },
  },
});
