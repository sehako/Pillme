import axios from 'axios';
import { useAuthStore } from '@/store/auth';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL, // ✅ `.env.local`에서 가져온 API 주소
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // 필요하면 설정
});

// ✅ 요청 인터셉터 (자동으로 토큰 추가)
apiClient.interceptors.request.use((config) => {
  const authStore = useAuthStore();
  if (authStore.token) {
    config.headers.Authorization = `Bearer ${authStore.token}`;
  }
  return config;
}, (error) => Promise.reject(error));

// ✅ 응답 인터셉터 (401 발생 시 자동 토큰 갱신)
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response && error.response.status === 401) {
      try {
        const authStore = useAuthStore();
        await authStore.refreshToken(); // 🔥 자동 토큰 갱신
        error.config.headers.Authorization = `Bearer ${authStore.token}`;
        return apiClient(error.config); // 원래 요청 다시 보내기
      } catch (refreshError) {
        authStore.logout(); // 토큰 갱신 실패 시 로그아웃
      }
    }
    return Promise.reject(error);
  }
);

export default apiClient;
