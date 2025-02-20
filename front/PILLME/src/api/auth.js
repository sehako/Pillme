import apiClient from './index';
import { useUserStore } from '../stores/user'; // ✅ Pinia 유저 스토어 추가
import { decodeToken } from '../utils/jwt'; // ✅ JWT 디코딩 유틸 추가
import Cookies from 'js-cookie'; // ✅ js-cookie 라이브러리 추가
import axios from 'axios';
import {getAccessToken, saveAccessToken, deleteAccessToken} from '../utils/localForage';
import { useAuthStore } from '../stores/auth';
// ===========================
// 인증 관련 API 함수들
// ===========================

// ✅ 이메일 인증 관련 API
export const requestEmailVerification = (email) => {
  return apiClient.post('/api/v1/auth/email/verification', { email });
};

export const verifyEmailCode = (email, code) => {
  return apiClient.post('/api/v1/auth/email/verify', { email, code });
};

// ✅ 이메일 중복 검사
export const isDuplicateEmail = async (email) => {
  try {
    const response = await apiClient.get('/api/v1/auth/check/email', {
      params: { email },
    });
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || '이메일 중복 검사 중 오류가 발생했습니다.');
  }
};

// ✅ SMS 인증 관련 API
export const requestSmsVerification = (phoneNumber) => {
  return apiClient.post('/api/v1/auth/sms/verification', { phoneNumber });
};

export const verifySmsCode = (phoneNumber, code) => {
  return apiClient.post('/api/v1/auth/sms/verify', { phoneNumber, code });
};

//로그인 요청 (JWT 디코딩 추가)
export const login = async (credentials) => {
  try {
    const response = await apiClient.post('/api/v1/auth/login', credentials);
    //Access Token & Refresh Token 저장
    handleLoginSuccess(response.data);
    return response.data;
  } catch (error) {
    console.error('로그인 실패:', error);
    throw error;
  }
};

// 토큰 갱신을 위한 별도 axios 인스턴스
const refreshClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL.replace(/\/$/, ""),
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true,
});

//액세스 토큰 갱신 API (JWT 디코딩 포함)
export const refreshAccessTokenAPI = async () => {
  try {
    const refreshToken = Cookies.get('refreshToken');
    const accessToken = await getAccessToken();
    
    // apiClient 대신 refreshClient 사용
    const response = await refreshClient.post('/api/v1/auth/refresh', {}, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
        refreshToken: refreshToken,
        'Content-Type': 'application/json',
      },
    });

    const { accessToken: newAccessToken, refreshToken: newRefreshToken } = response.data.result;
    
    // 새 토큰 저장
    await saveAccessToken(newAccessToken);
    localStorage.setItem('accessToken', newAccessToken);
    Cookies.set('refreshToken', newRefreshToken, {
      secure: true,
      sameSite: 'Strict'
    });

    // 스토어 업데이트
    const userStore = useUserStore();
    const authStore = useAuthStore();
    const userInfo = decodeToken(newAccessToken);
    userStore.setUser(userInfo);
    authStore.checkLoginStatus();

    return response.data;
  } catch (error) {
    console.error('❌ 액세스 토큰 갱신 실패:', error);
    throw error;
  }
};

// ✅ 로그아웃 요청
export const logoutAPI = async () => {
  try {
    await apiClient.post('/api/v1/auth/logout');
    handleLogout();
  } catch (error) {
    console.error('❌ 로그아웃 실패:', error);
    throw error;
  }
};

// ===========================
// 토큰 관리 및 저장 관련 헬퍼 함수들
// ===========================

export const handleLoginSuccess = async (responseData) => {
  const { accessToken, refreshToken } = responseData.result;
  
  // 토큰 저장 (병렬 처리)
  await Promise.all([
    saveAccessToken(accessToken),
    new Promise(resolve => {
      localStorage.setItem('accessToken', accessToken);
      Cookies.set('refreshToken', refreshToken, { secure: true, sameSite: 'Strict' });
      resolve();
    })
  ]);

  // JWT 디코딩 및 스토어 업데이트
  const userStore = useUserStore();
  const authStore = useAuthStore();
  const userInfo = decodeToken(accessToken);
  
  await Promise.all([
    userStore.setUser(userInfo),
    authStore.checkLoginStatus()
  ]);

  // 토큰 체크 초기화
  authStore.lastTokenCheck = Date.now();
  authStore.isTokenChecking = false;
};

// ✅ 로그아웃 처리 함수 수정
export const handleLogout = async () => {
  try {
    // 1. 모든 토큰 제거
    localStorage.removeItem('accessToken');
    await deleteAccessToken();
    Cookies.remove('refreshToken');
    
    // 2. 모든 스토어 초기화
    const userStore = useUserStore();
    const authStore = useAuthStore();
    
    userStore.clearUser();
    await authStore.logout();
    
    // 3. 모든 상태 초기화
    authStore.lastTokenCheck = null;
    authStore.isTokenChecking = false;
    authStore.isLoggedIn = false;

    // 4. 캐시된 API 응답 초기화
    if (window.caches) {
      const cacheNames = await caches.keys();
      await Promise.all(
        cacheNames.map(cacheName => caches.delete(cacheName))
      );
    }

    // 5. 강제 페이지 새로고침으로 리다이렉션
    window.location.href = '/start';
  } catch (error) {
    console.error('로그아웃 처리 중 오류 발생:', error);
    // 6. 오류 발생시에도 강제 리다이렉션
    window.location.replace('/start');
  }
};

// ✅ 닉네임 중복 검사
export const isDuplicateNickname = async (nickname) => {
  try {
    const response = await apiClient.get('/api/v1/auth/check/nickname', {
      params: { nickname },
    });
    return response.data.result;
  } catch (error) {
    throw new Error(error.response?.data?.message || '닉네임 중복 검사 중 오류가 발생했습니다.');
  }
};

// ✅ 전화번호 중복 검사
export const isDuplicatePhone = async (phone) => {
  try {
    const response = await apiClient.get('/api/v1/auth/check/phone', {
      params: { phone },
    });
    return response.data.result;
  } catch (error) {
    throw new Error(error.response?.data?.message || '전화번호 중복 검사 중 오류가 발생했습니다.');
  }
};

// ✅ OAuth 로그인 함수
export const oauthLogin = async (code) => {
  try {
    const response = await apiClient.get(`/api/v1/auth/oauth2/google`, {
      params: { code },
    });

    if (response.data.isSuccess) {
      return response.data;
    } else {
      throw new Error(response.data.message || '로그인에 실패했습니다.');
    }
  } catch (error) {
    throw new Error(error.response?.data?.message || '로그인 처리 중 오류가 발생했습니다.');
  }
};

// ✅ OAuth 회원가입
export const oauthSignUp = async (signUpData, provider) => {
  try {
    const response = await apiClient.post(`/api/v1/auth/oauth2/signup`, signUpData, {
      params: { provider },
    });
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || '회원가입 처리 중 오류가 발생했습니다.');
  }
};

// ✅ 이메일 찾기
export const findEmail = async (phone) => {
  try {
    const response = await apiClient.get('/api/v1/auth/find-email', {
      params: { phone },
    });
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || '이메일 찾기 중 오류가 발생했습니다.');
  }
};

// ✅ 비밀번호 재설정
export const requestPasswordResetVerification = (email) => {
  return apiClient.post('/api/v1/auth/temporary-password/verify-email', { email });
};

export const requestTemporaryPassword = (email, phone) => {
  return apiClient.post('/api/v1/auth/temporary-password/request', {
    email,
    phone,
  });
};
