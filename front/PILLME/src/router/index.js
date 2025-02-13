import { createRouter, createWebHistory } from 'vue-router';
import Cookies from 'js-cookie';
import { decodeToken } from "../utils/jwt"; // ✅ JWT 디코딩 유틸 추가
import { refreshAccessTokenAPI } from '../api/auth'; // 경로는 실제 위치에 맞게 수정

// ✅ 라우트 목록
import AccountSearchSelectionView from '../views/AccountSearchSelectionView.vue';
import AfterAccountView from '../views/AfterAccountView.vue';
import OAuthAdditionalInfo from '../views/OAuthAdditionalInfo.vue';

// ✅ 아이디 비밀번호 찾기 관련
import IdSearchView from '../views/IdSearchView.vue';
import IdFoundView from '../views/IdFoundView.vue';
import PwSearchView from '../views/PwSearchView.vue';
import StartView from '../views/StartView.vue';
import MemberRegisterView from '../views/MemberRegisterView.vue';
import RegisterView from '../views/RegisterView.vue';
import EmailRegistView from '../views/EmailRegistView.vue';
import OAuthCallback from '../views/OAuthCallback.vue';

// ✅ 로그인 관련
import LoginView from '../views/LoginView.vue';
import LoginSelectionView from '../views/LoginSelectionView.vue';
import SigninSelectionView from '../views/SigninSelectionView.vue';
import LoginSucess from '../views/LoginSucess.vue';

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
  // 👉 게스트 전용 페이지 (비로그인 사용자만 접근)
  { path: '/start', name: 'StartView', component: StartView },
  { path: '/login', name: 'LoginView', component: LoginView },
  { path: '/signinselection', name: 'SigninSelectionView', component: SigninSelectionView },
  { path: '/loginselection', name: 'LoginSelectionView', component: LoginSelectionView },
  {
    path: '/accountsearchselection',
    name: 'AccountSearchSelectionView',
    component: AccountSearchSelectionView,
  },
  { path: '/afteraccount', name: 'AfterAccountView', component: AfterAccountView },
  { path: '/idsearch', name: 'IdSearch', component: IdSearchView },
  { path: '/idfound', name: 'IdFound', component: IdFoundView },
  { path: '/pwsearch', name: 'PwSearch', component: PwSearchView },
  { path: '/memberregister', name: 'memberregister', component: MemberRegisterView },
  { path: '/register', name: 'register', component: RegisterView },
  { path: '/emailregist', name: 'emailregist', component: EmailRegistView },
  { path : '/oauth/additional-info', name: 'OAuthAdditionalInfo', component: OAuthAdditionalInfo },
  { path: '/auth/callback/google', name: 'OAuthCallback', component: OAuthCallback },
  { path: '/login/success', name: 'LoginSucess', component: LoginSucess },

  // ✅ 로그인해야 접근 가능한 페이지 (requiresAuth: true)
  { path: '/', name: 'Home', component: HomeView, meta: { requiresAuth: true } },
  {
    path: '/calendar',
    name: 'CalendarView',
    component: CalendarView,
    meta: { requiresAuth: true },
  },
  { path: '/mypage', name: 'mypage', component: MyPageView, meta: { requiresAuth: true } },
  { path: '/mypage/alarm', name: 'alarm', component: My_Alarm, meta: { requiresAuth: true } },
  { path: '/mypage/personal-info', name: 'personal-info', component: PersonalInfo, meta: { requiresAuth: true } },
  { path: '/mypage/login-security', name: 'login-security', component: LoginSecurity, meta: { requiresAuth: true } },
  { path: '/mypage/pw-change', name: 'pw-change', component: MyPage_PwChange, meta: { requiresAuth: true } },
  { path: '/notificationlist', name: 'NotificationList', component: NotificationListView, meta: { requiresAuth: true } },
  { path: '/managememberlist', name: 'ManageMemberList', component: ManageMemberListView, meta: { requiresAuth: true } },
  { path: '/chat', name: 'ChatView', component: ChatView, meta: { requiresAuth: true } },
  { path: '/camera', name: 'CameraCapture', component: CameraCapture, meta: { requiresAuth: true } },
  { path: '/imageanalysis', name: 'ImageAnalysis', component: ImageAnalysis, meta: { requiresAuth: true } },
  { path: '/chat/individual', name: 'ChatIndividualView', component: ChatIndividualView, props: (route) => ({ info: route.query.info }), meta: { requiresAuth: true } },
  // ✅ 404 페이지 처리
  // { path: '/:catchAll(.*)', name: 'NotFound', component: StartView }, // TODO: 404 페이지 구현 필요
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

router.beforeEach(async (to, from, next) => {
  const guestPages = [
    '/start', '/login', '/signinselection', '/loginselection',
    '/accountsearchselection', '/afteraccount', '/idsearch',
    '/idfound', '/pwsearch', '/memberregister', '/register',
    '/emailregist', '/login/success', '/auth/callback/google',
    '/oauth/additional-info',
  ];

  const accessToken = localStorage.getItem('accessToken');
  const refreshToken = Cookies.get('refreshToken');
  let isAccessTokenValid = false;

  if (accessToken) {
    try {
      const decodedToken = decodeToken(accessToken);
      isAccessTokenValid = decodedToken.exp * 1000 > Date.now();
    } catch (error) {
      console.error('❌ accessToken 디코딩 실패:', error);
      isAccessTokenValid = false;
    }
  }

  // ✅ 1. 게스트 페이지 접근 처리
  if (guestPages.includes(to.path)) {
    if (accessToken) {
      if (!isAccessTokenValid && refreshToken) {
        try {
          await refreshAccessTokenAPI();
          return next('/');
        } catch (error) {
          console.error('[Route Guard] 게스트 페이지 접근 시 토큰 재발급 실패:', error);
          localStorage.removeItem('accessToken');
          Cookies.remove('refreshToken');
        }
      }
      if (isAccessTokenValid) return next('/');
    }
    return next();
  }

  // ✅ 2. 보호된 페이지 접근 처리
  if (!accessToken) return next('/start');

  if (!isAccessTokenValid && refreshToken) {
    try {
      await refreshAccessTokenAPI();

      // ✅ 토큰 갱신 후 유저 정보 업데이트
      const userInfo = decodeAccessToken();
      if (userInfo) useUserStore().setUser(userInfo);

      return next();
    } catch (error) {
      console.error('[Route Guard] 보호된 페이지 접근 시 토큰 재발급 실패:', error);
      localStorage.removeItem('accessToken');
      Cookies.remove('refreshToken');
      return next('/start');
    }
  }

  if (!isAccessTokenValid) return next('/start');

  return next();
});

export default router;

