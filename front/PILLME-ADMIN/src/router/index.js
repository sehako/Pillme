import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import DashboardView from '../views/DashboardView.vue'
import MemberManagement from '../views/MemberManagement.vue'
import InquiriesView from '../views/InquiriesView.vue'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login',
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: DashboardView,
    },
    {
      path: '/members',
      name: 'members',
      component: MemberManagement,
    },
    {
      path: '/inquiries',
      name: 'inquiries',
      component: InquiriesView,
    },
  ],
})

// 네비게이션 가드 설정
router.beforeEach((to, from, next) => {
  const isLoggedIn = !!localStorage.getItem('token')
  const publicPages = ['/login']
  const authRequired = !publicPages.includes(to.path)

  if (authRequired && !isLoggedIn) {
    return next('/login')
  }

  next()
})

export default router
