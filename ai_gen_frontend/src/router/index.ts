import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../components/pages/HomePage.vue'
import UserRegisterPage from '../components/pages/user/UserRegisterPage.vue'
import UserLoginPage from '@/components/pages/user/UserLoginPage.vue'
import UserManagementPage from '@/components/pages/admin/UserManagePage.vue'
import AppChat from '@/components/app/AppChat.vue'
import TestPage from '@/components/testPage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomePage,
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
    },
    {
      path: '/user/register',
      name: 'register',
      component: UserRegisterPage,
    },
    {
      path: '/user/login',
      name: 'login',
      component: UserLoginPage,
    },
    {
      path: '/admin/user/management',
      name: 'user-management',
      component: UserManagementPage,
    },
    {
      path: '/app/chat',
      name: 'app-chat',
      component: AppChat,
    },
    {
      path: '/test',
      name: 'test',
      component: TestPage,
    }
  ],
})

export default router
