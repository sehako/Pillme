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

// ✅ 응답 인터셉터: 401(Unauthorized) 에러 발생 시 refreshToken으로 accessToken & refreshToken 갱신 시도
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // ✅ 401 (Unauthorized) 에러 발생 && _retry가 없을 경우 (중복 방지)
    if (error.response && error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        console.log('[Interceptor] 401 Unauthorized → refreshToken으로 갱신 시도');

        // ✅ 쿠키에서 refreshToken 가져오기
        const refreshToken = Cookies.get('refreshToken');
        if (refreshToken) {
          // ✅ refreshToken을 이용해 새로운 accessToken & refreshToken 요청
          const refreshResponse = await axios.post(
            `${import.meta.env.VITE_API_URL}/api/v1/auth/refresh`,
            { refreshToken },
            { headers: { 'Content-Type': 'application/json' } }
          );

          // ✅ 새 accessToken & refreshToken 저장
          const newAccessToken = refreshResponse.data.accessToken;
          const newRefreshToken = refreshResponse.data.refreshToken; // ✅ 새로운 refreshToken

          localStorage.setItem('accessToken', newAccessToken);
          Cookies.set('refreshToken', newRefreshToken, { path: '/', secure: true, sameSite: 'Strict' }); // ✅ 쿠키 갱신

          // ✅ 원래 요청의 헤더 갱신 후 재요청
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
          return apiClient(originalRequest);
        } else {
          console.warn('[Interceptor] refreshToken 없음 → 로그인 페이지로 이동');
          router.push('/start');
        }
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
