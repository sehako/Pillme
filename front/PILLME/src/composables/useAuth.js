import { useAuthStore } from '../stores/auth';
import { computed } from 'vue';

export function useAuth() {
  const authStore = useAuthStore();

  const initAuth = () => {
    authStore.init();
  };

  const cleanUpAuth = () => {
    authStore.cleanup();
  };

  return {
    isLoggedIn: computed(() => authStore.isLoggedIn),
    initAuth,
    cleanUpAuth,
  };
}
