import axios from 'axios';
import { useAuthStore } from '../stores/auth';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // ✅ Refresh Token이 HttpOnly 쿠키로 전달되도록 설정
});

// ✅ 요청 인터셉터 (Access Token 자동 추가)
apiClient.interceptors.request.use((config) => {
  const authStore = useAuthStore();
  if (authStore.accessToken) {
    config.headers.Authorization = `Bearer ${authStore.accessToken}`;
  }
  return config;
}, (error) => Promise.reject(error));

// ✅ 응답 인터셉터 (401 발생 시 자동으로 Access Token 갱신)
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response && error.response.status === 401) {
      try {
        const authStore = useAuthStore();
        const newAccessToken = await authStore.refreshAccessToken(); // 🔄 자동 토큰 갱신
        error.config.headers.Authorization = `Bearer ${newAccessToken}`;
        return apiClient(error.config); // 원래 요청 다시 보내기
      } catch (refreshError) {
        authStore.logout(); // 🚨 Refresh Token도 만료되면 로그아웃
      }
    }
    return Promise.reject(error);
  }
);

export default apiClient;
