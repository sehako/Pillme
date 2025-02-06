import { createRouter, createWebHistory } from 'vue-router';
import StartView from '../views/StartView.vue';
import LoginView from '../views/LoginView.vue';
import LoginSelectionView from '../views/LoginSelectionView.vue';
import AccountSearchSelectionView from '../views/AccountSearchSelectionView.vue';
import AfterAccountView from '../views/AfterAccountView.vue';
import SigninSelectionView from '../views/SigninSelectionView.vue';
import IdSearchView from '../views/IdSearchView.vue';
import IdFoundView from '../views/IdFoundView.vue';
import PwSearchView from '../views/PwSearchView.vue';
import MyPageView from '../views/MyPageView.vue';
import ManageMemberListView from '../views/ManageMemberListView.vue';
import NonMemberRegisterView from '../views/NonMemberRegisterView.vue';
import MemberRegisterView from '../views/MemberRegisterView.vue';
import RegisterView from '../views/RegisterView.vue';
import NotificationListView from '../views/NotificationListView.vue';
import HomeView from '../views/HomeView.vue';
import CalendarView from '../views/CalendarView.vue';
import PersonalInfo from '../views/PersonalInfo.vue';
import LoginSecurity from '../views/LoginSecurity.vue';
import MyPage_PwChange from '../views/MyPage_PwChange.vue';
import ChatView from '../views/ChatView.vue';
// import NameDropdown from '../components/NameDropdown.vue';
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
    path: '/chat',
    name: 'ChatView',
    component: ChatView,
  },
  {
    path: '/idsearch',  
    name: 'IdSearch',
    component: IdSearchView, 
  },
  {
    path: '/idfound',  
    name: 'IdFound',
    component: IdFoundView, 
  },
  {
    path: '/pwsearch',  
    name: 'PwSearch',
    component: PwSearchView, 
  },
  {
    path: '/nonmemberregister',  
    name: 'nonmemberregister',
    component: NonMemberRegisterView, 
  },
  {
    path: '/memberregister',  
    name: 'memberregister',
    component: MemberRegisterView, 
  },
  {
    path: '/register',  
    name: 'register',
    component: RegisterView, 
  },
  {
    path: '/signinselection',
    name: 'SigninSelectionView',
    component: SigninSelectionView,
    meta: { cache: true },
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
    path: '/managememberlist',  
    name: 'ManageMemberList',
    component: ManageMemberListView, 
  },
  {
    path: '/notificationlist',  
    name: 'NotificationList',
    component: NotificationListView, 
  },
  {
    path: '/',
    name: 'Home',
    component: HomeView,
    meta: { cache: true }, // ✅ 캐싱할 페이지
  },
  {
    path: '/calendar',
    name: 'calendar',
    component: CalendarView, // ✅ TODO: 실제 컴포넌트 연결 예정
    meta: { cache: true }, // ✅ 캐싱할 페이지
  },
  {
    path: '/profile',
    name: 'profile',
    // component: ProfileView, // ✅ TODO: 실제 컴포넌트 연결 예정
    meta: { cache: false }, // ❌ 오프라인 시 접근 불가
  },
  {
    path: '/mypage',
    name: 'mypage',
    component: MyPageView,
    meta: { cache: false }, // ❌ 오프라인 시 접근 불가
  },
  {
    path: '/mypage/personal-info',
    name: 'personal-info',
    component: PersonalInfo,
    meta: { cache: false }, // ❌ 오프라인 시 접근 불가
  },
  {
    path: '/mypage/login-security',
    name: 'login-security',
    component: LoginSecurity,
    meta: { cache: false },
  },
  {
    path: '/mypage/pw-change',
    name: 'pw-change',
    component: MyPage_PwChange,
    meta: { cache: false },
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
    component: () => import('../views/StartView.vue') //✅ TODO: 404 페이지 구현 필요
  },
  
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;
