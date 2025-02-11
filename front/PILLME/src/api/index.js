import axios from 'axios';
import router from '../router';
import Cookies from 'js-cookie';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  // refreshToken이 쿠키에 저장되어 있다면 withCredentials 옵션은 true로 설정할 수 있습니다.
  withCredentials: true,
});

// 요청 인터셉터: 로컬스토리지에서 accessToken 읽어서 헤더에 추가
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

// 응답 인터셉터: 401 에러 발생 시 refreshToken을 사용해 accessToken 갱신 시도
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    // _retry 플래그로 재시도 중복 방지
    if (error.response && error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        // 쿠키에서 refreshToken 가져오기
        const refreshToken = Cookies.get('refreshToken');
        if (refreshToken) {
          // 리프레시 토큰을 사용해 새 accessToken 발급받기
          const refreshResponse = await axios.post(
            `${import.meta.env.VITE_API_URL}/api/v1/auth/refresh`,
            { refreshToken },
            { headers: { 'Content-Type': 'application/json' } }
          );
          const newAccessToken = refreshResponse.data.accessToken;
          // localStorage에 새 accessToken 저장
          localStorage.setItem('accessToken', newAccessToken);
          // 원래 요청의 헤더 갱신 후 재요청
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
          return apiClient(originalRequest);
        } else {
          // refreshToken이 없는 경우 로그인 페이지 등으로 리다이렉트
          router.push('/start');
        }
      } catch (refreshError) {
        // 토큰 갱신 실패 시 localStorage와 쿠키에서 토큰 삭제 후 로그아웃 처리
        localStorage.removeItem('accessToken');
        Cookies.remove('refreshToken');
        router.push('/start');
      }
    }
    return Promise.reject(error);
  }
);

console.log("✅ VITE_API_URL:", import.meta.env.VITE_API_URL);

export default apiClient;
