// stores/auth.js
import { defineStore } from 'pinia';
import { refreshAccessTokenAPI } from '../api/auth';
import { decodeToken } from '../utils/jwt';
import Cookies from 'js-cookie';
import { deleteAccessToken } from '../utils/localForage';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    isTokenChecking: false,
    lastTokenCheck: null,
    isLoggedIn: false
  }),

  actions: {
    async checkAndRefreshToken() {
      // 중복 체크 방지
      if (this.isTokenChecking) {
        // console.log('토큰 체크 중...');
        return;
      }
      
      // 마지막 체크로부터 1분 이내면 스킵
      if (this.lastTokenCheck && Date.now() - this.lastTokenCheck < 60000) {
        // console.log('최근 체크 완료, 스킵');
        return;
      }

      try {
        this.isTokenChecking = true;
        const accessToken = localStorage.getItem('accessToken');
        const refreshToken = Cookies.get('refreshToken');

        if (!accessToken || !refreshToken) {
          throw new Error('토큰 없음');
        }

        const decodedToken = await decodeToken(accessToken);
        const expirationTime = decodedToken.exp * 1000;
        
        // 토큰 만료 10분 전부터 갱신
        if (Date.now() > expirationTime - 600000) {
          // console.log('토큰 갱신 필요');
          await this.refreshToken();
        }

        this.lastTokenCheck = Date.now();
      } catch (error) {
        console.error('토큰 체크/갱신 실패:', error);
        throw error;
      } finally {
        this.isTokenChecking = false;
      }
    },

    async refreshToken() {
      try {
        const result = await refreshAccessTokenAPI();
        if (result?.accessToken) {
          localStorage.setItem('accessToken', result.accessToken);
          return true;
        }
        throw new Error('토큰 갱신 실패');
      } catch (error) {
        console.error('토큰 갱신 중 에러:', error);
        throw error;
      }
    },

    async logout() {
      localStorage.removeItem('accessToken');
      deleteAccessToken();
      Cookies.remove('refreshToken');
      this.isLoggedIn = false;
      this.lastTokenCheck = null;
      this.isTokenChecking = false;
    },

    checkLoginStatus() {
      this.isLoggedIn = !!localStorage.getItem('accessToken');
      return this.isLoggedIn;
    },

    init() {
      this.checkLoginStatus();
      window.addEventListener('storage', this.checkLoginStatus);
    },

    cleanup() {
      window.removeEventListener('storage', this.checkLoginStatus);
    }
  }
});