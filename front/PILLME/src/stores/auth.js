import { defineStore } from 'pinia';
import apiClient from '../api';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    accessToken: localStorage.getItem('accessToken') || null, // ✅ 새로고침 후 유지
  }),
  actions: {
    // ✅ 로그인 API 요청
    async login(credentials) {
      try {
        const response = await apiClient.post('/api/v1/auth/login', credentials);
        const { accessToken } = response.data.result;

        this.accessToken = accessToken;
        localStorage.setItem('accessToken', accessToken); // ✅ accessToken 저장
        apiClient.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;

        return response;
      } catch (error) {
        console.error('로그인 실패:', error);
        throw error;
      }
    },

    // ✅ Access Token 갱신 (Refresh Token은 HttpOnly 쿠키에서 자동 전송됨)
    async refreshAccessToken() {
      try {
        const response = await apiClient.post('/api/v1/auth/refresh'); // ✅ Refresh Token 자동 포함
        const { accessToken } = response.data.result;

        this.accessToken = accessToken;
        localStorage.setItem('accessToken', accessToken); // ✅ accessToken 업데이트
        apiClient.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;

        return accessToken;
      } catch (error) {
        console.error('Access Token 갱신 실패:', error);
        this.logout();
        throw error;
      }
    },

    // ✅ 로그아웃 처리
    logout() {
      this.user = null;
      this.accessToken = null;
      localStorage.removeItem('accessToken'); // ✅ 저장된 accessToken 삭제
      apiClient.defaults.headers.common['Authorization'] = null;
    },
  },
});
