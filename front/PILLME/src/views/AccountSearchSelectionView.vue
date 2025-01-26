<template>
    <div class="search-container">
      <div class="logo-section">
        <img src="../assets/Splash_logo.png" alt="PILLME Logo" class="logo" />
      </div>
  
      <div class="accountsearchselection-options">
        <button class="Search_button Email">
          이메일 찾기
        </button>
        <button class="Search_button PW">
          비밀번호 찾기
        </button>
      </div>
      <p class="back-login">
      <a href="/loginselection">로그인 페이지로 돌아가기</a>
    </p>
    </div>
  </template>
  
  <!-- <script>

  </script> -->
  
  <style scoped>
  .search-container {
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
  
  .accountsearchselection-options {
    display: flex;
    flex-direction: column;
    gap: 1rem; 
    width: 100%;
    max-width: 400px;
  }
  
  .Search-button {
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
  
  .Email:hover {
    background-color: #ffffff;
  }
  
  .PW:hover {
    background-color: #ffffff;
  }
  
  .back-login {
    margin-top: 2rem;
    font-size: 0.9rem;
    color: #757575;
  }
  
  .back-login a {
    color: #000000;
    text-decoration: none;
  }
  
  .back-login a:hover {
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
  
    .Search-button {
      font-size: 0.9rem;
      padding: 0.5rem;
    }
  
    .back-login {
      font-size: 0.8rem;
    }
  }
  </style> 
  