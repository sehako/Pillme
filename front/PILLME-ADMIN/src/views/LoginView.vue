<template>
  <div class="flex w-full min-h-screen">
    <div class="flex flex-col items-center w-full max-w-xs m-auto md:max-w-sm">
      <BaseLogo :src="logoSrc" size="md" class="mx-auto" />
      <img :src="textLogoSrc" alt="PILLME" class="mx-auto my-8 h-14" />

      <form class="w-full px-4 space-y-4" @submit.prevent="handleLogin">
        <BaseInput v-model="email" type="email" placeholder="이메일 입력" :icon="emailIcon" />
        <BaseInput
          v-model="password"
          type="password"
          placeholder="비밀번호 입력"
          :icon="passwordIcon"
        />

        <BaseButton textColor="text-white" size="md" :disabled="isLoading" class="w-full">
          {{ isLoading ? '로그인 중...' : '로그인' }}
        </BaseButton>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import BaseButton from '../components/BaseButton.vue'
import BaseInput from '../components/BaseInput.vue'
import BaseLogo from '../components/BaseLogo.vue'
import logoSrc from '../assets/logi_nofont.svg'
import textLogoSrc from '../assets/Logi_font.svg'
import emailIcon from '../assets/email.svg'
import passwordIcon from '../assets/key.svg'

const email = ref('')
const password = ref('')
const isLoading = ref(false)

const dummyUsers = [
  {
    email: import.meta.env.VITE_ADMIN_EMAIL,
    password: import.meta.env.VITE_ADMIN_PASSWORD,
    role: import.meta.env.VITE_ADMIN_ROLE,
  },
]

const handleLogin = async () => {
  if (!email.value || !password.value) {
    alert('이메일과 비밀번호를 입력해주세요.')
    return
  }

  isLoading.value = true
  try {
    // 더미 데이터로 로그인 체크
    const user = dummyUsers.find(
      (user) => user.email === email.value && user.password === password.value,
    )

    if (!user) {
      throw new Error('이메일 또는 비밀번호가 일치하지 않습니다.')
    }

    // 로그인 성공 시 토큰 저장 및 리다이렉션
    localStorage.setItem('token', 'dummy-token') // 더미 토큰 저장

    // 로그인 성공 시 dashboard로 리다이렉션
    window.location.replace('/dashboard')
  } catch (error) {
    console.error('❌ 로그인 오류:', error)
    alert(error.message)
  } finally {
    isLoading.value = false
  }
}
</script>
