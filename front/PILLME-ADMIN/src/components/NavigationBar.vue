<template>
  <nav class="w-full bg-[#9DBB9F] p-4 text-white">
    <div class="flex items-center justify-between px-8 mx-auto">
      <div class="flex items-center space-x-6">
        <router-link to="/dashboard">
          <img :src="textLogoSrc" alt="PILLME" class="h-6 cursor-pointer" />
        </router-link>
        <div v-if="isLoggedIn" class="flex space-x-4">
          <router-link
            to="/members"
            class="px-4 py-2 text-white hover:bg-[#8DAA8F] rounded-lg transition-colors"
          >
            회원 관리
          </router-link>
          <router-link
            to="/inquiries"
            class="px-4 py-2 text-white hover:bg-[#8DAA8F] rounded-lg transition-colors"
          >
            1:1 문의내역
          </router-link>
        </div>
      </div>
      <div v-if="isLoggedIn" class="pr-4">
        <button
          @click="handleLogout"
          class="px-4 py-2 text-white hover:bg-[#8DAA8F] rounded-lg transition-colors"
        >
          로그아웃
        </button>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import textLogoSrc from '../assets/Logi_font.svg'

const router = useRouter()
const isLoggedIn = ref(false)

onMounted(() => {
  // 실제로는 여기서 로그인 상태를 체크합니다
  // 지금은 더미 데이터로 구현
  checkLoginStatus()
})

const checkLoginStatus = () => {
  // 실제로는 토큰이나 세션을 확인합니다
  const dummyToken = localStorage.getItem('token')
  isLoggedIn.value = !!dummyToken
}

const handleLogout = () => {
  localStorage.removeItem('token')
  isLoggedIn.value = false
  router.push('/login')
}
</script>
