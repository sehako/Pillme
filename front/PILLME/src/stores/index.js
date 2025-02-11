// import { createRouter, createWebHistory } from 'vue-router';
// // import { useAuthStore } from '../stores/auth';
// import EmailRegistView from '../views/EmailRegistView.vue';

// const routes = [
//   { path: '/start', name: 'StartView', component: () => import('../views/StartView.vue') },
//   { path: '/email-register', name: 'EmailRegistView', component: EmailRegistView },
// ];

// const router = createRouter({
//   history: createWebHistory(import.meta.env.BASE_URL),
//   routes,
// });

// router.beforeEach((to, from, next) => {
//   const authStore = useAuthStore();
//   const isAuthenticated = !!authStore.accessToken;

//   if (to.meta.requiresAuth && !isAuthenticated) {
//     next('/start');
//   } else {
//     next();
//   }
// });

// export default router;
