import axios from 'axios';
import { useAuthStore } from '../stores/auth';
import router from '../router';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

// ✅ 요청 인터셉터 (Access Token 자동 추가)
apiClient.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore();
    if (authStore.accessToken) {
      config.headers.Authorization = `Bearer ${authStore.accessToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ✅ 응답 인터셉터 (401 발생 시 자동으로 Access Token 갱신)
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response && error.response.status === 401) {
      try {
        const authStore = useAuthStore();
        const newAccessToken = await authStore.refreshAccessToken();
        error.config.headers.Authorization = `Bearer ${newAccessToken}`;
        return apiClient(error.config);
      } catch (refreshError) {
        authStore.logout();
        router.push('/start');
      }
    }
    return Promise.reject(error);
  }
);
console.log("✅ VITE_API_URL:", import.meta.env.VITE_API_URL);

export default apiClient;
