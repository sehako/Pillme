import { createRouter, createWebHistory } from 'vue-router';
import Cookies from 'js-cookie';
import { refreshAccessTokenAPI } from '../api/auth'; // 경로는 실제 위치에 맞게 수정
// import { useAuthStore } from '../stores/auth'; // ✅ 상대 경로 사용

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

// ocr
import CameraCapture from '../components/CameraCapture.vue';
import ImageAnalysis from '../components/ImageAnalysis.vue';

const routes = [
  // ✅ 비로그인 사용자만 접근 가능한 페이지
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

  // ✅ 로그인 여부와 상관없이 접근 가능한 페이지 (예: 채팅)
  { path: '/chat', name: 'ChatView', component: ChatView, meta: { requiresAuth: true } },
  { path: '/chat/:id', name: 'ChatIndividualView', component: ChatIndividualView, props: true, meta: { requiresAuth: true } },

  // ✅ 404 페이지 처리
  { path: '/:catchAll(.*)', name: 'NotFound', component: StartView },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

router.beforeEach(async (to, from, next) => {
  // 접근 가능한 게스트 페이지 목록
  const guestPages = [
    '/start', '/login', '/signinselection', '/loginselection',
    '/accountsearchselection', '/afteraccount', '/idsearch',
    '/idfound', '/pwsearch', '/memberregister', '/register', '/emailregist'
  ];

  // 보호된 페이지가 아니라면 그냥 이동
  if (guestPages.includes(to.path)) {
    return next();
  }

  // localStorage에서 accessToken과 만료시간 가져오기
  const accessToken = localStorage.getItem('accessToken');
  const accessTokenExpiry = localStorage.getItem('accessTokenExpiry');
  const refreshToken = Cookies.get('refreshToken');

  const currentTime = new Date().getTime();
  // accessToken이 존재하고, 만료시간이 남아있으면 유효하다고 판단
  const isAccessTokenValid = accessToken && currentTime < Number(accessTokenExpiry);

  // accessToken이 없거나 만료되었는데 refreshToken이 있으면 갱신 시도
  if (!isAccessTokenValid && refreshToken) {
    try {
      await refreshAccessTokenAPI();
      // 갱신 후에는 refreshAccessTokenAPI 내부에서 accessToken과 만료시간을 저장하므로 보호된 페이지로 이동
      return next();
    } catch (error) {
      console.error('[Route Guard] 토큰 갱신 실패:', error);
      // 갱신 실패 시 토큰 제거 후 로그인 페이지로 이동
      localStorage.removeItem('accessToken');
      // localStorage.removeItem('accessTokenExpiry');
      Cookies.remove('refreshToken');
      return next('/start');
    }
  }

  // 토큰이 유효하지 않으면 로그인 페이지로 이동
  if (!isAccessTokenValid) {
    return next('/start');
  }

  // 모든 조건이 만족되면 정상 이동
  next();
});

export default router;
