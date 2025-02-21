import { defineStore } from "pinia";
import { refreshAccessTokenAPI } from "../api/auth";
import { decodeToken } from "../utils/jwt";
import Cookies from "js-cookie";
import { deleteAccessToken} from "../utils/localForage";

export const useUserStore = defineStore("user", {
  state: () => ({
    user: null, // ✅ 유저 정보 저장
  }),
  actions: {
    setUser(userData) {
      if (!userData?.memberId) {
        console.warn("⚠️ [DEBUG] 유저 데이터에 memberId 없음:", userData);
      }
      console.log("✅ [DEBUG] Pinia에 유저 정보 저장됨:", userData);
      this.user = userData;
    },
    clearUser() {
      console.log("🚫 [DEBUG] 유저 정보 초기화");
      this.user = null;
      localStorage.removeItem("accessToken"); // ✅ 추가
      deleteAccessToken();
      Cookies.remove("refreshToken"); // ✅ 추가
    },
    async getMemberId() {
      // 현재 저장된 유저 정보가 있는 경우
      if (this.user?.memberId) {
        console.log("🔍 [DEBUG] 기존 memberId 반환:", this.user.memberId);
        return this.user.memberId;
      }

      // 현재 액세스 토큰을 확인하고 디코딩 시도
      const currentToken = localStorage.getItem("accessToken");
      if (currentToken) {
        try {
          const decodedToken = decodeToken(currentToken);
          if (decodedToken?.memberId) {
            this.setUser(decodedToken);
            return decodedToken.memberId;
          }
        } catch (error) {
          console.warn("⚠️ [DEBUG] 현재 토큰 디코딩 실패:", error);
        }
      }

      // 액세스 토큰이 없거나 유효하지 않은 경우, 리프레시 시도
      try {
        const newTokenData = await refreshAccessTokenAPI();

        if (newTokenData?.result?.accessToken) {
          // ✅ 새로 발급된 토큰을 이용해 유저 정보 갱신
          const newUserInfo = decodeToken(newTokenData.result.accessToken);
          this.setUser(newUserInfo); // ✅ 추가

          return newUserInfo.memberId; // ✅ 개선된 반환 방식
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
