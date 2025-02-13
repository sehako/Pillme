import { defineStore } from 'pinia';

export const useTempAuthStore = defineStore('tempAuth', {
  state: () => ({
    email: '',
    password: '',
  }),
  actions: {
    setCredentials(email, password) {
      this.email = email;
      this.password = password;
    },
    clearCredentials() {
      this.email = '';
      this.password = '';
    }
  }
});
