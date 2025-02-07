import { defineStore } from 'pinia';
import { login, logout, refreshToken } from '@/api/auth';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: localStorage.getItem('token') || null,
  }),
  actions: {
    async login(credentials) {
      const response = await login(credentials);
      this.user = response.data.user;
      this.token = response.data.token;
      localStorage.setItem('token', this.token);
    },
    async refreshToken() {
      const response = await refreshToken();
      this.token = response.data.token;
      localStorage.setItem('token', this.token);
    },
    logout() {
      this.user = null;
      this.token = null;
      localStorage.removeItem('token');
      logout();
    },
  },
});
