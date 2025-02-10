import apiClient from "./index";

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

// ✅ 로그인 요청
export const login = (credentials) => {
  return apiClient.post("/api/v1/auth/login", credentials);
};

// ✅ 액세스 토큰 갱신 API (리프레시 토큰을 이용)
export const refreshAccessTokenAPI = () => {
  return apiClient.post("/api/v1/auth/refresh");
};

// ✅ 로그아웃 요청
export const logoutAPI = () => {
  return apiClient.post("/api/v1/auth/logout");
};

// ===========================
// 토큰 관리 및 저장 관련 헬퍼 함수들
// (필요에 따라 authStore나 다른 상태관리 라이브러리와 연동 가능)
// ===========================

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
 * 로그인 성공 후 토큰 저장 처리
 * @param {object} responseData - API 응답 데이터 (예: { result: { accessToken, refreshToken } })
 */
export const handleLoginSuccess = (responseData) => {
  const { accessToken, refreshToken } = responseData.result;
  saveAccessToken(accessToken);
  saveRefreshToken(refreshToken);
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
};
