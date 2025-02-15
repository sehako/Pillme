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

    // ✅ 401 (Unauthorized) && _retry가 없을 경우 (중복 방지)
    if (error.response && error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        console.log('[Interceptor] 401 Unauthorized → Access Token 만료 확인');

        // ✅ Access Token의 exp(만료 시간) 확인
        const accessToken = localStorage.getItem('accessToken');
        if (accessToken) {
          const decodedToken = JSON.parse(atob(accessToken.split('.')[1])); // Base64 디코딩
          const now = Math.floor(Date.now() / 1000);

          // 🔹 Access Token이 아직 유효하다면 재발급 요청 X
          if (decodedToken.exp > now) {
            console.log('[Interceptor] Access Token이 아직 유효함 → 요청 재시도');
            return apiClient(originalRequest);
          }
        }

        // ✅ Access Token이 실제로 만료된 경우에만 Refresh Token 요청
        console.log('[Interceptor] Access Token 만료됨 → refreshAccessTokenAPI() 호출');
        const newAccessToken = await refreshAccessTokenAPI();

        // ✅ 새 Access Token으로 원래 요청의 헤더 갱신 후 재요청
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        return apiClient(originalRequest);
      } catch (refreshError) {
        console.error('[Interceptor] Refresh Token 갱신 실패:', refreshError);

        // ✅ Refresh Token도 만료된 경우 → 강제 로그아웃
        localStorage.removeItem('accessToken');
        Cookies.remove('refreshToken');
        router.push('/start'); // ✅ 로그인 페이지로 이동
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
