import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/auth'; // ✅ 상대 경로 사용

// ✅ 회원가입 이후 관련
import AccountSearchSelectionView from '../views/AccountSearchSelectionView.vue';
import AfterAccountView from '../views/AfterAccountView.vue';

// ✅ 아이디 비밀번호 찾기 관련
import IdSearchView from '../views/IdSearchView.vue';
import IdFoundView from '../views/IdFoundView.vue';
import PwSearchView from '../views/PwSearchView.vue';

// ✅ 회원가입 관련
import StartView from '../views/StartView.vue';
import MemberRegisterView from '../views/MemberRegisterView.vue';
import RegisterView from '../views/RegisterView.vue';
import EmailRegistView from '../views/EmailRegistView.vue';
// ✅ 로그인 관련
import LoginView from '../views/LoginView.vue';
import LoginSelectionView from '../views/LoginSelectionView.vue';
import SigninSelectionView from '../views/SigninSelectionView.vue';

// ✅ 로그인 후 접근 가능 페이지
import HomeView from '../views/HomeView.vue';
import CalendarView from '../views/CalendarView.vue';
import MyPageView from '../views/MyPageView.vue';
import MyPage_PwChange from '../views/MyPwChange.vue';
import PersonalInfo from '../views/MyPersonalInfo.vue';
import LoginSecurity from '../views/MyLoginSecurity.vue';
import My_Alarm from '../views/My_Alarm.vue';
import ChatView from '../views/ChatView.vue';
import ChatIndividualView from '../views/ChatIndividualView.vue';
import ManageMemberListView from '../views/ManageMemberListView.vue';
import NotificationListView from '../views/NotificationListView.vue';

const routes = [
  // ✅ 비로그인 사용자만 접근 가능
  { path: '/start', name: 'StartView', component: StartView },
  { path: '/login', name: 'LoginView', component: LoginView },
  { path: '/signinselection', name: 'SigninSelectionView', component: SigninSelectionView },
  { path: '/loginselection', name: 'LoginSelectionView', component: LoginSelectionView },
  { path: '/accountsearchselection', name: 'AccountSearchSelectionView', component: AccountSearchSelectionView },
  { path: '/afteraccount', name: 'AfterAccountView', component: AfterAccountView },
  { path: '/idsearch', name: 'IdSearch', component: IdSearchView },
  { path: '/idfound', name: 'IdFound', component: IdFoundView },
  { path: '/pwsearch', name: 'PwSearch', component: PwSearchView },
  { path: '/memberregister', name: 'memberregister', component: MemberRegisterView },
  { path: '/register', name: 'register', component: RegisterView },
  { path: '/emailregist', name: 'emailregist', component: EmailRegistView },

  // ✅ 로그인해야 접근 가능한 페이지 (requiresAuth: true)
  { path: '/', name: 'Home', component: HomeView, meta: { requiresAuth: true } },
  { path: '/calendar', name: 'CalendarView', component: CalendarView, meta: { requiresAuth: true } },
  { path: '/mypage', name: 'mypage', component: MyPageView, meta: { requiresAuth: true } },
  { path: '/mypage/alarm', name: 'alarm', component: My_Alarm, meta: { requiresAuth: true } },
  { path: '/mypage/personal-info', name: 'personal-info', component: PersonalInfo, meta: { requiresAuth: true } },
  { path: '/mypage/login-security', name: 'login-security', component: LoginSecurity, meta: { requiresAuth: true } },
  { path: '/mypage/pw-change', name: 'pw-change', component: MyPage_PwChange, meta: { requiresAuth: true } },
  { path: '/notificationlist', name: 'NotificationList', component: NotificationListView, meta: { requiresAuth: true } },
  { path: '/managememberlist', name: 'ManageMemberList', component: ManageMemberListView, meta: { requiresAuth: true } },

  // ✅ 로그인 여부 상관없이 접근 가능 (예: 채팅)
  { path: '/chat', name: 'ChatView', component: ChatView, meta: { requiresAuth: true } },
  { path: '/chat/:id', name: 'ChatIndividualView', component: ChatIndividualView, props: true, meta: { requiresAuth: true } },

  // ✅ 404 페이지 처리
  { path: '/:catchAll(.*)', name: 'NotFound', component: StartView }, // TODO: 404 페이지 구현 필요
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

// ✅ 전역 네비게이션 가드 (로그인 안 하면 `/start`로 강제 이동, 예외 페이지 제외)
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  const isAuthenticated = !!authStore.accessToken; // ✅ 로그인 여부 확인

  console.log(`[Route Guard] 이동하려는 경로: ${to.path}, 로그인 상태: ${isAuthenticated}`);

  // ✅ 로그인하지 않은 사용자가 접근 가능한 페이지 목록
  const guestPages = [
    '/start', '/login', '/signinselection', '/loginselection',
    '/accountsearchselection', '/afteraccount', '/idsearch',
    '/idfound', '/pwsearch', '/memberregister', '/register','/emailregist'
  ];

  // ✅ 로그인하지 않은 사용자가 보호된 페이지에 접근하려 하면 `/start`로 이동
  if (!isAuthenticated && !guestPages.includes(to.path)) {
    console.log('[Route Guard] 비로그인 사용자, /start로 강제 이동');
    return next('/start');
  }

  next(); // ✅ 정상 이동
});

export default router;
