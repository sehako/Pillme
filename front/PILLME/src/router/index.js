import { createRouter, createWebHistory } from 'vue-router';

// ✅ TODO: 나중에 실제 페이지 구현 후 추가
// import HomeView from '../views/HomeView.vue';
// import CalendarView from '../views/CalendarView.vue';
// import ProfileView from '../views/ProfileView.vue';
// import SettingsView from '../views/SettingsView.vue';
// import NotFoundView from '../views/NotFoundView.vue'; // ✅ 404 페이지

const routes = [
  {
    path: '/',
    name: 'home',
    // component: HomeView,  // ✅ TODO: 실제 컴포넌트 연결 예정
    meta: { cache: true }, // ✅ 캐싱할 페이지
  },
  {
    path: '/calendar',
    name: 'calendar',
    // component: CalendarView, // ✅ TODO: 실제 컴포넌트 연결 예정
    meta: { cache: true }, // ✅ 캐싱할 페이지
  },
  {
    path: '/profile',
    name: 'profile',
    // component: ProfileView, // ✅ TODO: 실제 컴포넌트 연결 예정
    meta: { cache: false }, // ❌ 오프라인 시 접근 불가
  },
  {
    path: '/settings',
    name: 'settings',
    // component: SettingsView, // ✅ TODO: 실제 컴포넌트 연결 예정
    meta: { cache: false }, // ❌ 오프라인 시 접근 불가
  },
  {
    path: '/:catchAll(.*)', // ✅ 존재하지 않는 페이지 요청 시
    name: 'NotFound',
    // component: NotFoundView, // ✅ TODO: 404 페이지 구현 필요
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;
