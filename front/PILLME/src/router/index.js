import { createRouter, createWebHistory } from 'vue-router';
import StartView from '../views/StartView.vue';
import LoginView from '../views/LoginView.vue';
import LoginSelectionView from '../views/LoginSelectionView.vue';
import AccountSearchSelectionView from '../views/AccountSearchSelectionView.vue';
import AfterAccountView from '../views/AfterAccountView.vue';
// import HomeView from '../views/HomeView.vue';
// import CalendarView from '../views/CalendarView.vue';
// import ProfileView from '../views/ProfileView.vue';
// import SettingsView from '../views/SettingsView.vue';
// import NotFoundView from '../views/NotFoundView.vue'; // ✅ 404 페이지

const routes = [
  {
    path: '/start', // /로 할지 /start로 할지 고민 필요
    name: 'StartView',
    component: StartView,
    meta: { cache: true },
  },
  {
    path: '/login',
    name: 'LoginView',
    component: LoginView,
    meta: { cache: true },
  },
  {
    path: '/afteraccount',  // URL 경로
    name: 'AfterAccount',
    component: AfterAccountView,  // 등록한 컴포넌트
  },
  {
    path: '/loginselection',
    name: 'LoginSelectionView',
    component: LoginSelectionView,
    meta: { cache: true },
  },
  {
    path: '/accountsearchselection',
    name: 'AccountSearchSelectionView',
    component: AccountSearchSelectionView,
    meta: { cache: true },
  },
  {
    path: '/',
    name: 'Home',
    //component: HomeView,  // ✅ TODO: 실제 컴포넌트 연결 예정
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
