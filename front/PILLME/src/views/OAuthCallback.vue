<template>
  <div class="flex items-center justify-center min-h-screen">
    <div class="text-center">
      <p>로그인 처리 중입니다...</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { oauthLogin } from '../api/auth';
import Cookies from "js-cookie"; // ✅ js-cookie 라이브러리 추가
import { saveAccessToken } from '../utils/localForage';
const router = useRouter();

onMounted(async () => {
  try {
    const code = new URLSearchParams(window.location.search).get('code');
    if (!code) {
      throw new Error('인증 코드가 없습니다.');
    }

    // OAuth 로그인 처리
    const response = await oauthLogin(code);

    if (response.needAdditionalInfo) {
      // 프론트엔드에서 리다이렉트 처리
      window.location.href = `/oauth/additional-info?email=${encodeURIComponent(response.email)}&name=${encodeURIComponent(response.name)}&provider=GOOGLE`;
    } else {
      // 로그인 성공 처리
      localStorage.setItem('accessToken', response.tokenResponse.accessToken);
      saveAccessToken(response.tokenResponse.accessToken); // localForage로 access token 저장
      Cookies.set('refreshToken', response.tokenResponse.refreshToken, { secure: true, sameSite: 'Strict' });
      router.push('/');
    }
  } catch (error) {
    console.error('OAuth 로그인 실패:', error);
    router.push('/signin');
  }
});
</script>
