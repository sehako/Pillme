<template>
    <div class="flex items-center justify-center min-h-screen">
      <LoadingSpinner message="로그인 처리 중입니다..." />
    </div>
  </template>
  
  <script setup>
  import { onMounted } from 'vue';
  import { useRouter } from 'vue-router';
  import { useAuthStore } from '../stores/auth';
  import { oauthLogin } from '../api/auth';
  
  const router = useRouter();
  const authStore = useAuthStore();
  
  const handleOAuthCallback = async () => {
    try {
      const urlParams = new URLSearchParams(window.location.search);
      const code = urlParams.get('code');
      const provider = 'google'; // 현재는 구글만 지원
      
      const response = await oauthLogin(code, provider);
      
      if (response.data.result.type === "REDIRECT") {
        // 신규 회원인 경우 추가정보 입력 페이지로 이동
        const redirectUrl = new URL(response.data.result.redirectUrl);
        router.push({
          path: '/oauth/additional-info',
          query: {
            email: redirectUrl.searchParams.get('email'),
            name: redirectUrl.searchParams.get('name'),
            provider: redirectUrl.searchParams.get('provider')
          }
        });
      } else if (response.data.result.type === "TOKEN") {
        // 이미 가입된 회원인 경우
        const { accessToken } = response.data.result.tokenResponse;
        authStore.setAccessToken(accessToken);
        router.push('/loginselection');
        // 알림 표시
        alert('이미 가입된 회원입니다. 로그인을 진행해주세요.');
      }
    } catch (error) {
      console.error('OAuth 콜백 처리 중 오류:', error);
      alert('로그인 처리 중 오류가 발생했습니다.');
      router.push('/loginselection');
    }
  };
  
  onMounted(() => {
    handleOAuthCallback();
  });
  </script>