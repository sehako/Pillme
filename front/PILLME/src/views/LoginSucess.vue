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

const router = useRouter()
const error = ref('')

const goToLogin = () => {
  router.push('/login')
}

onMounted(async () => {
  try {
    const urlParams = new URLSearchParams(window.location.search);
    
    // accessToken과 refreshToken 직접 추출
    const accessToken = urlParams.get('accessToken');
    const refreshToken = urlParams.get('refreshToken');

    if (!accessToken || !refreshToken) {
      throw new Error('토큰 정보가 없습니다.');
    }

    // JWT 토큰에서 만료 시간 추출
    const tokenPayload = JSON.parse(atob(accessToken.split('.')[1]));
    const expiryTime = tokenPayload.exp * 1000;

    // 토큰 저장
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('accessTokenExpiry', expiryTime.toString());
    Cookies.set('refreshToken', refreshToken);

    // URL 파라미터 제거 (보안을 위해)
    window.history.replaceState({}, document.title, window.location.pathname);

    // 메인 페이지로 이동
    await router.push('/');;
    
  } catch (e) {
    error.value = e.message || '로그인 처리 중 오류가 발생했습니다.';
  }
});
</script>