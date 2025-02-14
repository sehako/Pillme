import { ref } from 'vue';

export function useAuth() {
  const isLoggedIn = ref(false);

  const checkLoginStatus = () => {
    isLoggedIn.value = !!localStorage.getItem('accessToken');
  };

  const onStorageChange = () => {
    checkLoginStatus();
  };

  const initAuth = () => {
    checkLoginStatus();
    window.addEventListener('storage', onStorageChange);
  };

  const cleanUpAuth = () => {
    window.removeEventListener('storage', onStorageChange);
  };

  return {
    isLoggedIn,
    initAuth,
    cleanUpAuth,
  };
}
