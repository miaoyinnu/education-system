import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'
import { useUserStore } from '@/stores/user'
import Layout from '@/layout/index.vue'
import adminRoutes from './modules/admin'
import studentRoutes from './modules/student'
import teacherRoutes from './modules/teacher'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { requiresAuth: false }
    },
    studentRoutes,
    adminRoutes,
    teacherRoutes,
    {
      path: '/',
      redirect: '/login',
      meta: { requiresAuth: false }
    }
  ]
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const token = getToken()
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth) {
    if (!token) {
      next('/login')
      return
    }
    
    if (!userStore.role) {
      try {
        await userStore.getInfo()
      } catch (error) {
        console.error('Failed to get user info:', error)
        next('/login')
        return
      }
    }
    
    // 检查角色权限
    if (to.meta.role && to.meta.role !== userStore.role) {
      next(`/${userStore.role.toLowerCase()}/dashboard`)
      return
    }
    
    next()
  } else {
    if (token && to.path === '/login') {
      // 如果已登录且访问登录页，重定向到对应的首页
      const role = userStore.role || (await userStore.getInfo()).role
      next(`/${role.toLowerCase()}/dashboard`)
      return
    }
    next()
  }
})

export default router 