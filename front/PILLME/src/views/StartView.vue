<template>
  <div class="start-container">
    <div class="logo-section">
      <img src="../assets/Splash_logo.png" alt="PILLME Logo" class="logo" />
    </div>
    <div class="action-section">
      <button class="start-button">시작하기</button>
      <p class="login-link">
        이미 계정이 있나요? <a href="/loginselection">로그인</a>
      </p>
    </div>
  </div>
</template>

<!-- <script>
export default {
  name: "Start",
  methods: {
    startApp() {
      this.$router.push("/onboarding");
    },
  },
};
</script> -->

<style scoped>
.start-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  text-align: center;
  background-color: #ffffff;
  padding: 1rem;
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

.action-section {
  width: 100%;
  max-width: 400px;
}

.start-button {
  width: 100%;
  padding: 0.75rem;
  font-size: 1rem;
  background-color: #2f6d31;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.start-button:hover {
  background-color: #255226;
}

.login-link {
  margin-top: 1rem;
  font-size: 0.9rem;
  color: #4f4f4f;
}

.login-link a {
  color: #2f6d31;
  text-decoration: none;
}

.login-link a:hover {
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

  .start-button {
    font-size: 0.9rem;
    padding: 0.5rem;
  }

  .login-link {
    font-size: 0.8rem;
  }
}
</style>
