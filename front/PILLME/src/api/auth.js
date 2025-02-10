import apiClient from "./index";

// ✅ 이메일 인증 관련 API
export const requestEmailVerification = (email) => {
  console.log("📨 이메일 인증 요청:", email);
  return apiClient.post("/api/v1/auth/email/verification", { email });
};

export const verifyEmailCode = async (email, code) => {
  console.log("✅ 인증번호 확인 요청:", { email, code });
  return apiClient.post("/api/v1/auth/email/verify", { email, code });
};

// ✅ SMS 인증 관련 API
export const requestSmsVerification = (phoneNumber) => {
  // JSON 형식의 데이터 생성
  const requestData = { phoneNumber };
  // 디버깅 로그: 실제 전송하기 전 JSON 문자열을 출력
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

export const verifySmsCode = async (phoneNumber, code) => {
  console.log("✅ SMS 인증번호 확인 요청:", { phoneNumber, code });
  return apiClient.post("/api/v1/auth/sms/verify", { phoneNumber, code });
};


// ✅ 로그인 요청
export const login = (credentials) => {
  return apiClient.post("/api/v1/auth/login", credentials);
};

// ✅ Access Token 갱신
export const refreshAccessToken = () => {
  return apiClient.post("/api/v1/auth/refresh");
};

// ✅ 로그아웃 요청
export const logout = () => {
  return apiClient.post("/api/v1/auth/logout");
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