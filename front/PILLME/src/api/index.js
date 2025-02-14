import axios from 'axios';
import router from '../router';
import Cookies from 'js-cookie';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL.replace(/\/$/, ""), // ✅ 끝에 '/' 제거
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

    // ✅ 401 (Unauthorized) 에러 발생 && _retry가 없을 경우 (중복 방지)
    if (error.response && error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        console.log('[Interceptor] 401 Unauthorized → refreshAccessTokenAPI() 호출');

        // ✅ auth.js의 refreshAccessTokenAPI() 호출
        const newAccessToken = await refreshAccessTokenAPI();

        // ✅ 원래 요청의 헤더 갱신 후 재요청
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        return apiClient(originalRequest);
      } catch (refreshError) {
        console.error('[Interceptor] refreshToken 갱신 실패:', refreshError);
        localStorage.removeItem('accessToken');
        Cookies.remove('refreshToken');
        router.push('/start'); // ✅ 강제 로그아웃 처리
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;