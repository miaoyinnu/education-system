import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'
import { useUserStore } from '@/stores/user'
import Layout from '@/layout/index.vue'
import adminRoutes from './modules/admin'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      redirect: '/login',
      meta: { requiresAuth: false }
    },
    {
      path: '/student',
      component: Layout,
      redirect: '/student/dashboard',
      meta: { role: 'STUDENT', requiresAuth: true },
      children: [
        {
          path: 'dashboard',
          name: 'StudentDashboard',
          component: () => import('@/views/student/Dashboard.vue'),
          meta: { title: '学生首页', role: 'STUDENT' }
        },
        {
          path: 'courses',
          name: 'StudentCourses',
          component: () => import('@/views/student/Courses.vue'),
          meta: { title: '选课', role: 'STUDENT' }
        },
        {
          path: 'grades',
          name: 'StudentGrades',
          component: () => import('@/views/student/Grades.vue'),
          meta: { title: '成绩查询', role: 'STUDENT' }
        }
      ]
    },
    {
      path: '/teacher',
      component: Layout,
      redirect: '/teacher/dashboard',
      meta: { role: 'TEACHER', requiresAuth: true },
      children: [
        {
          path: 'dashboard',
          name: 'TeacherDashboard',
          component: () => import('@/views/teacher/Dashboard.vue'),
          meta: { title: '教师首页', role: 'TEACHER' }
        },
        {
          path: 'courses',
          name: 'TeacherCourses',
          component: () => import('@/views/teacher/Courses.vue'),
          meta: { title: '课程管理', role: 'TEACHER' }
        },
        {
          path: 'timetable',
          name: 'TeacherTimetable',
          component: () => import('@/views/teacher/Timetable.vue'),
          meta: { title: '课表查询', role: 'TEACHER' }
        },
        {
          path: 'grades',
          name: 'TeacherGrades',
          component: () => import('@/views/teacher/Grades.vue'),
          meta: { title: '成绩管理', role: 'TEACHER' }
        }
      ]
    },
    adminRoutes
  ]
})

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
    if (token) {
      // 如果已登录且访问登录页，重定向到对应的首页
      if (to.path === '/login') {
        const role = userStore.role || (await userStore.getInfo()).role
        next(`/${role.toLowerCase()}/dashboard`)
        return
      }
    }
    next()
  }
})

export default router 