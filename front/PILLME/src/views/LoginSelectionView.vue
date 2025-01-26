<template>
  <div class="login-container">
    <div class="logo-section">
      <img src="../assets/Splash_logo.png" alt="PILLME Logo" class="logo" />
    </div>

    <div class="login-options">
      <button class="login-button google">
        <img src="../assets/Google_Login.png" alt="Google Icon" class="icon" />
        구글 계정으로 계속하기
      </button>
      <button class="login-button naver">
        <img src="../assets/Naver_Login.png" alt="Naver Icon" class="icon" />
        네이버 계정으로 계속하기
      </button>
      <button class="login-button email"  @click="goToLogin">
        <img src="../assets/Email_Login.png" alt="Email Icon" class="icon" />
        이메일로 계속하기
      </button>
    </div>

    <p class="forgot-account">
      <a href="/accountsearchselection">계정이 기억나지 않아요</a>
    </p>
  </div>
</template>

<script>
export default {
  name: "LoginSelectionView",
  methods: {
    goToLogin() {
      this.$router.push('/login'); 
    },
  },
};
</script>

<style scoped>
.login-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f8f8f8;
  padding: 1rem;
  text-align: center;
}

.logo-section {
  margin-bottom: 2rem;
}

.logo {
  width: 300px;
  height: 300px;
  margin-bottom: 1rem;
}

.tagline {
  font-size: 1.2rem;
  color: #4f4f4f;
  font-weight: 400;
}

.login-options {
  display: flex;
  flex-direction: column;
  gap: 1rem; 
  width: 100%;
  max-width: 400px;
}

.login-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem; 
  padding: 0.75rem;
  font-size: 1rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #fff;
  cursor: pointer;
  transition: background-color 0.3s, border-color 0.3s;
}

.google:hover {
  background-color: #ffffff;
}


.naver:hover {
  background-color: #ffffff;
}

.email:hover {
  background-color: #ffffff;
}

.icon {
  width: 24px;
  height: 24px;
}


.forgot-account {
  margin-top: 2rem;
  font-size: 0.9rem;
  color: #757575;
}

.forgot-account a {
  color: #2f6d31;
  text-decoration: none;
}

.forgot-account a:hover {
  text-decoration: underline;
}


@media (max-width: 600px) {
  .logo {
    width: 96px;
    height: 96px;
  }

  .tagline {
    font-size: 1rem;
  }

  .login-button {
    font-size: 0.9rem;
    padding: 0.5rem;
  }

  .icon {
    width: 20px;
    height: 20px;
  }

  .forgot-account {
    font-size: 0.8rem;
  }
}
</style> 
