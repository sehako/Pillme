<template>
    <div class="flex items-center justify-center min-h-screen bg-gray-100">
      <div class="p-8 bg-white rounded-lg shadow-md">
        <div v-if="error" class="text-center text-red-500">
          <p>{{ error }}</p>
          <button 
            @click="goToLogin" 
            class="px-4 py-2 mt-4 text-white bg-blue-500 rounded hover:bg-blue-600"
          >
            로그인 페이지로 이동
          </button>
        </div>
        <div v-else class="text-center">
          <div class="w-12 h-12 mx-auto border-b-2 border-blue-500 rounded-full animate-spin"></div>
          <p class="mt-4 text-gray-600">로그인 처리 중입니다...</p>
        </div>
      </div>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import Cookies from 'js-cookie';
import {saveAccessToken} from '../utils/localForage'

const router = useRouter()
const error = ref('')

const goToLogin = async () => {
  try {
    // 이동 전 기존 토큰 정리
    localStorage.clear();
    Cookies.remove('refreshToken');
    
    await router.push('/login');
    console.log('로그인 페이지 이동 성공');
  } catch (error) {
    console.error('로그인 페이지 이동 실패:', error);
    // 강제로 페이지 이동
    window.location.href = '/login';
  }
}

onMounted(async () => {
  try {
    console.log('=== 로그인 성공 페이지 초기화 ===');
    console.log('현재 URL:', window.location.href);
    
    const urlParams = new URLSearchParams(window.location.search);
    const accessToken = urlParams.get('accessToken');
    const refreshToken = urlParams.get('refreshToken');
    
    console.log('URL 파라미터 검사:', {
      accessToken: accessToken ? '존재' : '없음',
      refreshToken: refreshToken ? '존재' : '없음'
    });

    if (!accessToken || !refreshToken) {
      throw new Error('토큰 정보가 없습니다.');
    }

    // JWT 토큰 디코딩
    try {
      // const tokenParts = accessToken.split('.');
      // const tokenPayload = JSON.parse(atob(tokenParts[1]));
      // const expiryTime = tokenPayload.exp * 1000;
      // const currentTime = Date.now();
      
      console.log('토큰 정보:', {
        // 만료시간: new Date(expiryTime).toLocaleString(),
        // 현재시간: new Date(currentTime).toLocaleString(),
        // 유효여부: expiryTime > currentTime
      });

      // 토큰 저장 전 기존 데이터 정리
      localStorage.clear(); // 기존 localStorage 데이터 초기화
      Cookies.remove('refreshToken'); // 기존 리프레시 토큰 제거

      // 새로운 토큰 저장
      localStorage.setItem('accessToken', accessToken);
      // localStorage.setItem('accessTokenExpiry', expiryTime.toString());
      saveAccessToken(accessToken);
      Cookies.set('refreshToken', refreshToken);

      // 저장된 값 확인
      const savedAccessToken = localStorage.getItem('accessToken');
      // const savedExpiry = localStorage.getItem('accessTokenExpiry');
      const savedRefreshToken = Cookies.get('refreshToken');

      console.log('저장된 값 확인:', {
        accessToken: savedAccessToken ? '저장됨' : '실패',
        // accessTokenExpiry: savedExpiry,
        refreshToken: savedRefreshToken ? '저장됨' : '실패'
      });

      // URL 파라미터 제거
      window.history.replaceState({}, document.title, window.location.pathname);
      
      // 홈으로 이동
      console.log('홈으로 이동 시도');
      setTimeout(() => {
        router.push('/').then(() => {
          console.log('홈 이동 성공');
        }).catch((err) => {
          console.error('홈 이동 실패:', err);
        });
      }, 500); // 지연 시간 증가

    } catch (tokenError) {
      console.error('토큰 처리 중 에러:', tokenError);
      throw new Error('토큰 처리 중 오류가 발생했습니다.');
    }
    
  } catch (e) {
    console.error('로그인 처리 중 에러:', e);
    error.value = e.message || '로그인 처리 중 오류가 발생했습니다.';
  }
});
</script>