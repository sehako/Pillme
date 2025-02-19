import axios from 'axios';
import router from '../router';
import Cookies from 'js-cookie';
import { deleteAccessToken } from '../utils/localForage';
import { useAuthStore } from '../stores/auth';
import { decodeToken } from '../utils/jwt';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL.replace(/\/$/, ""),
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true,
});

// ✅ 요청 인터셉터: accessToken을 자동으로 헤더에 추가
apiClient.interceptors.request.use(
  (config) => {
    const accessToken = localStorage.getItem('accessToken');
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ✅ 응답 인터셉터: 401 발생 시 refreshAccessTokenAPI() 호출
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        console.log('[Interceptor] 401 Unauthorized → 토큰 갱신 시도');
        
        // auth store를 통한 토큰 갱신
        const authStore = useAuthStore();
        await authStore.checkAndRefreshToken();

        // 새 토큰으로 원래 요청 재시도
        const newToken = localStorage.getItem('accessToken');
        if (newToken) {
          originalRequest.headers.Authorization = `Bearer ${newToken}`;
          return apiClient(originalRequest);
        }
        
        throw new Error('토큰 갱신 실패');
      } catch (refreshError) {
        console.error('[Interceptor] 토큰 갱신 실패:', refreshError);
        
        // 로그아웃 처리
        const authStore = useAuthStore();
        await authStore.logout();
        
        router.push('/start');
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
