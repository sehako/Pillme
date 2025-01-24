<template>
  <div class="login-container">

    <div class="logo-section">
      <img src="/public/icons/Splash_logo.png" alt="PILLME Logo" class="logo" />
    </div>
    <form class="login-form">
      <div class="input-group">
        <img src="/public/icons/email.png" alt="Email Icon" class="input-icon" />
        <input
          type="email"
          placeholder="이메일 입력"
          class="input-field"
          required
        />
      </div>

      <div class="input-group">
        <img src="/public/icons/key.png" alt="Password Icon" class="input-icon" />
        <input
          type="password"
          placeholder="비밀번호 입력"
          class="input-field"
          required
        />
      </div>

      <button type="submit" class="login-button">로그인</button>
    </form>
  </div>
</template>

<!-- <script>
export default {
  name: "LoginView",
  data() {
    return {
      email: "",
      password: "",
    };
  },
  methods: {
    handleLogin() {
      console.log("로그인 시도:", this.email, this.password);
      // 로그인 처리 로직
    },
  },
};
</script> -->

<style scoped>

.login-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f8f8f8;
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

.login-form {
  width: 70%;
  max-width: 400px;
  display: flex;
  flex-direction: column;
  gap: 1rem; 
}

.input-group {
  display: flex;
  align-items: center;
  gap: 0.5rem; 
}

.input-icon {
  width: 24px;
  height: 24px;
}

.input-field {
  flex: 1;
  padding: 0.5rem;
  font-size: 1rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  outline: none;
  color: #4f4f4f; 
  background-color: #fff;
}

.input-field::placeholder {
  color: #b3b3b3; 
}

.input-group:nth-last-of-type(1) {
  margin-bottom: 0.6rem; 
}

.login-button {
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

.login-button:hover {
  background-color: #255226;
}

@media (max-width: 600px) {
  .logo {
    width: 96px;
    height: 96px;
  }

  .input-field {
    font-size: 0.9rem;
  }

  .login-button {
    font-size: 0.9rem;
    padding: 0.5rem;
  }
}
</style>
