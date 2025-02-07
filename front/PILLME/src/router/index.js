import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/auth'; // ✅ useAuthStore import 추가

// 로그인 관련
import LoginView from '../views/LoginView.vue';
import LoginSelectionView from '../views/LoginSelectionView.vue';
import SigninSelectionView from '../views/SigninSelectionView.vue';

// 회원가입 이후 관련
import AccountSearchSelectionView from '../views/AccountSearchSelectionView.vue';
import AfterAccountView from '../views/AfterAccountView.vue';

// 아이디 비밀번호 찾기 관련
import IdSearchView from '../views/IdSearchView.vue';
import IdFoundView from '../views/IdFoundView.vue';
import PwSearchView from '../views/PwSearchView.vue';

// 멤버 추가 관련
import ManageMemberListView from '../views/ManageMemberListView.vue';
import NonMemberRegisterView from '../views/NonMemberRegisterView.vue';

// 회원가입
import StartView from '../views/StartView.vue';
import MemberRegisterView from '../views/MemberRegisterView.vue';
import RegisterView from '../views/RegisterView.vue';

// 알림뷰
import NotificationListView from '../views/NotificationListView.vue';

// 홈뷰, 캘린더뷰
import HomeView from '../views/HomeView.vue';
import CalendarView from '../views/CalendarView.vue';

// 마이페이지 관련
import MyPageView from '../views/MyPageView.vue';
import MyPage_PwChange from '../views/MyPwChange.vue';
import PersonalInfo from '../views/MyPersonalInfo.vue';
import LoginSecurity from '../views/MyLoginSecurity.vue';
import My_Alarm from '../views/My_Alarm.vue';

// 채팅 관련
import ChatView from '../views/ChatView.vue';
import ChatIndividualView from '../views/ChatIndividualView.vue';

const routes = [
  { path: '/start', name: 'StartView', component: StartView, meta: { cache: true } },
  { path: '/login', name: 'LoginView', component: LoginView, meta: { cache: true } },
  { path: '/afteraccount', name: 'AfterAccount', component: AfterAccountView },
  { path: '/chat', name: 'ChatView', component: ChatView },
  { path: '/chat/:id', name: 'ChatIndividualView', component: ChatIndividualView, props: true },
  { path: '/idsearch', name: 'IdSearch', component: IdSearchView },
  { path: '/idfound', name: 'IdFound', component: IdFoundView },
  { path: '/pwsearch', name: 'PwSearch', component: PwSearchView },
  { path: '/nonmemberregister', name: 'nonmemberregister', component: NonMemberRegisterView },
  { path: '/memberregister', name: 'memberregister', component: MemberRegisterView },
  { path: '/register', name: 'register', component: RegisterView },
  { path: '/signinselection', name: 'SigninSelectionView', component: SigninSelectionView, meta: { cache: true } },
  { path: '/loginselection', name: 'LoginSelectionView', component: LoginSelectionView, meta: { cache: true } },
  { path: '/accountsearchselection', name: 'AccountSearchSelectionView', component: AccountSearchSelectionView, meta: { cache: true } },
  { path: '/managememberlist', name: 'ManageMemberList', component: ManageMemberListView },
  { path: '/notificationlist', name: 'NotificationList', component: NotificationListView },
  { path: '/', name: 'Home', component: HomeView, meta: { cache: true, requiresAuth: true } },
  //위 requiresAuth 참고
  { path: '/calendar', name: 'calendar', component: CalendarView, meta: { cache: true } },
  { path: '/mypage', name: 'mypage', component: MyPageView, meta: { cache: false } },
  { path: '/mypage/alarm', name: 'alarm', component: My_Alarm, meta: { cache: false } },
  { path: '/mypage/personal-info', name: 'personal-info', component: PersonalInfo, meta: { cache: false } },
  { path: '/mypage/login-security', name: 'login-security', component: LoginSecurity, meta: { cache: false } },
  { path: '/mypage/pw-change', name: 'pw-change', component: MyPage_PwChange, meta: { cache: false } },
  { path: '/:catchAll(.*)', name: 'NotFound', component: StartView }, // TODO: 404 페이지 구현 필요
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

// ✅ 전역 네비게이션 가드 (useAuthStore()를 직접 호출)
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore(); // ✅ Pinia 상태 가져오기
  const isAuthenticated = !!authStore.accessToken; // ✅ 로그인 여부 확인

  console.log(`[Route Guard] To: ${to.path}, Authenticated: ${isAuthenticated}`);

  if (to.meta.requiresAuth && !isAuthenticated) {
    console.log('[Route Guard] 인증되지 않은 사용자, 로그인 페이지로 이동');
    next('/start'); // 🚨 비로그인 사용자는 로그인 페이지로 리디렉트
  } else if (to.path === '/start' && isAuthenticated) {
    console.log('[Route Guard] 로그인된 사용자, 홈으로 이동');
    next('/start'); // ✅ 로그인한 사용자는 로그인 페이지 접근 불가
  } else {
    next(); // ✅ 정상 이동
  }
});
export default router;
