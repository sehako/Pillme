import apiClient from "./index";
import { useUserStore } from "../stores/user"; // ✅ Pinia 유저 스토어 추가
import { decodeToken } from "../utils/jwt"; // ✅ JWT 디코딩 유틸 추가
import Cookies from "js-cookie"; // ✅ js-cookie 라이브러리 추가

// ===========================
// 인증 관련 API 함수들
// ===========================

// ✅ 이메일 인증 관련 API
export const requestEmailVerification = (email) => {
  console.log("📨 이메일 인증 요청:", email);
  return apiClient.post("/api/v1/auth/email/verification", { email });
};

export const verifyEmailCode = (email, code) => {
  console.log("✅ 인증번호 확인 요청:", { email, code });
  return apiClient.post("/api/v1/auth/email/verify", { email, code });
};

// ✅ SMS 인증 관련 API
export const requestSmsVerification = (phoneNumber) => {
  console.log("📨 SMS 인증 요청 전 데이터:", phoneNumber);
  return apiClient.post("/api/v1/auth/sms/verification", { phoneNumber });
};

export const verifySmsCode = (phoneNumber, code) => {
  console.log("✅ SMS 인증번호 확인 요청:", { phoneNumber, code });
  return apiClient.post("/api/v1/auth/sms/verify", { phoneNumber, code });
};

// ✅ 로그인 요청 (JWT 디코딩 추가)
export const login = async (credentials) => {
  try {
    const response = await apiClient.post("/api/v1/auth/login", credentials);
    console.log("✅ 로그인 성공:", response.data);

    // ✅ Access Token & Refresh Token 저장
    handleLoginSuccess(response.data);

    return response.data;
  } catch (error) {
    console.error("❌ 로그인 실패:", error);
    throw error;
  }
};

// ✅ 액세스 토큰 갱신 API (JWT 디코딩 포함)
export const refreshAccessTokenAPI = async () => {
  try {
    const refreshToken = Cookies.get("refreshToken"); // ✅ 쿠키에서 refreshToken 가져오기

    const response = await apiClient.post("/api/v1/auth/refresh", {}, {
      headers: {
        "Refresh-Token": refreshToken, // ✅ Refresh-Token 헤더 추가
      },
    });
    
    console.log("🔄 액세스 토큰 갱신 성공:", response.data);
    saveAccessToken(response.data.result.accessToken);
    saveRefreshToken(response.data.result.refreshToken); // ✅ refreshToken도 갱신

    // ✅ Access Token 디코딩 → 유저 정보 업데이트
    const authStore = useUserStore();
    const userInfo = decodeToken(response.data.result.accessToken);
    authStore.setUser(userInfo);

    return response.data;
  } catch (error) {
    console.error("❌ 액세스 토큰 갱신 실패:", error);
    handleLogout(); // ✅ 토큰 만료 시 자동 로그아웃
    throw error;
  }
};

// ✅ 로그아웃 요청
export const logoutAPI = async () => {
  try {
    await apiClient.post("/api/v1/auth/logout");
    handleLogout();
  } catch (error) {
    console.error("❌ 로그아웃 실패:", error);
    throw error;
  }
};

// ===========================
// 토큰 관리 및 저장 관련 헬퍼 함수들
// ===========================

export const handleLoginSuccess = (responseData) => {
  const { accessToken, refreshToken } = responseData.result;
  saveAccessToken(accessToken);
  saveRefreshToken(refreshToken);

  // ✅ JWT 디코딩 후 Pinia 업데이트
  const authStore = useUserStore();
  const userInfo = decodeToken(accessToken);
  authStore.setUser(userInfo);
};

export const saveAccessToken = (accessToken) => {
  // const expiryTime = new Date().getTime() + 30 * 60 * 1000; // 30분 유효
  localStorage.setItem("accessToken", accessToken);
  // localStorage.setItem("accessTokenExpiry", expiryTime);
  // localStorage.setItem("userInfo", JSON.stringify(decodeToken(accessToken)));
};

// ✅ 리프레시 토큰을 js-cookie로 저장 (자동 갱신 반영)
export const saveRefreshToken = (refreshToken) => {
  Cookies.set("refreshToken", refreshToken, { secure: true, sameSite: "Strict" });
};

// ✅ 로그아웃 처리 함수 (js-cookie 사용, 자동 로그아웃 포함)
export const handleLogout = () => {
  localStorage.removeItem("accessToken");
  // localStorage.removeItem("accessTokenExpiry");
  Cookies.remove("refreshToken");

  // ✅ 유저 정보 초기화
  const authStore = useUserStore();
  authStore.clearUser();

  // ✅ 로그인 페이지로 이동
  window.location.href = "/start";
  window.location.reload();
  
};

export const oauthLogin = async (code, provider = 'google') => {
  try {
    const response = await apiClient.get(`/api/v1/auth/oauth2/${provider}`, {
      params: { code }
    });
    return response;
  } catch (error) {
    console.error('OAuth 로그인 에러:', error);
    throw error;
  }
};

export const oauthSignUp = async (data, provider) => {
  try {
    const response = await apiClient.post('/api/v1/auth/oauth2/signup', data, {
      params: { provider }
    });
    return response;
  } catch (error) {
    console.error('OAuth 회원가입 에러:', error);
    throw error;
  }
};

export const submitAdditionalInfo = async (data) => {
  try {
    const response = await apiClient.put('/api/v1/auth/oauth2/additional-info', data, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`
      }
    });
    return response;
  } catch (error) {
    console.error('추가 정보 제출 에러:', error);
    throw error;
  }
};