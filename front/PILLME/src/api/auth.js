import apiClient from "./index";
import { useUserStore } from "../stores/user"; // ✅ Pinia 유저 스토어 추가
import { decodeToken } from "../utils/jwt"; // ✅ JWT 디코딩 유틸 추가

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
  const requestData = { phoneNumber };
  console.log("📨 SMS 인증 요청 전 데이터:", JSON.stringify(requestData));

  return apiClient.post(
    "/api/v1/auth/sms/verification",
    requestData,
    {
      headers: {
        "Content-Type": "application/json", // JSON 형식으로 전송
      },
    }
  );
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

// ✅ 액세스 토큰 갱신 API (리프레시 토큰을 이용, JWT 디코딩 포함)
export const refreshAccessTokenAPI = async () => {
  try {
    const response = await apiClient.post("/api/v1/auth/refresh");

    console.log("🔄 액세스 토큰 갱신 성공:", response.data);
    saveAccessToken(response.data.result.accessToken);

    // ✅ Access Token 디코딩 → 유저 정보 업데이트
    const authStore = useUserStore();
    const userInfo = decodeToken(response.data.result.accessToken);
    authStore.setUser(userInfo); // Pinia 상태 업데이트

    return response.data;
  } catch (error) {
    console.error("❌ 액세스 토큰 갱신 실패:", error);
    throw error;
  }
};

// ✅ 로그아웃 요청
export const logoutAPI = async () => {
  try {
    await apiClient.post("/api/v1/auth/logout");
    handleLogout();

    // ✅ 유저 정보 초기화
    const authStore = useUserStore();
    authStore.clearUser(); // Pinia 상태 초기화
  } catch (error) {
    console.error("❌ 로그아웃 실패:", error);
    throw error;
  }
};

// ===========================
// 토큰 관리 및 저장 관련 헬퍼 함수들
// ===========================

/**
 * 로그인 성공 후 토큰 저장 + JWT 디코딩 후 Pinia에 유저 정보 저장
 * @param {object} responseData - API 응답 데이터 (예: { result: { accessToken, refreshToken } })
 */
export const handleLoginSuccess = (responseData) => {
  const { accessToken, refreshToken } = responseData.result;
  saveAccessToken(accessToken);
  saveRefreshToken(refreshToken);

  // ✅ JWT 디코딩 후 Pinia 업데이트
  const authStore = useUserStore();
  const userInfo = decodeToken(accessToken);
  authStore.setUser(userInfo); // Pinia 상태 업데이트
};

/**
 * 액세스 토큰 저장 (30분 유효)
 * @param {string} accessToken
 */
export const saveAccessToken = (accessToken) => {
  const expiryTime = new Date().getTime() + 30 * 60 * 1000; // 현재 시간 + 30분
  localStorage.setItem("accessToken", accessToken);
  localStorage.setItem("accessTokenExpiry", expiryTime);
};

/**
 * 리프레시 토큰 저장 (쿠키에 저장)
 * 참고: HttpOnly 옵션은 클라이언트에서 설정할 수 없으므로, 보안을 강화하려면 서버에서 설정해야 합니다.
 * @param {string} refreshToken
 */
export const saveRefreshToken = (refreshToken) => {
  document.cookie = `refreshToken=${refreshToken}; path=/; Secure; SameSite=Strict`;
};

/**
 * 로그아웃 처리 함수
 * - 로컬 스토리지의 토큰 및 만료시간 삭제
 * - 쿠키에 저장된 리프레시 토큰 삭제 (만료시간을 과거로 설정)
 */
export const handleLogout = () => {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("accessTokenExpiry");
  document.cookie = "refreshToken=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT; Secure; SameSite=Strict";

  // ✅ 유저 정보 초기화
  const authStore = useUserStore();
  authStore.clearUser();
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