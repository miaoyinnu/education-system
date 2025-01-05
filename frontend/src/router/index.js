import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'
import { ElMessage } from 'element-plus'
import Layout from '@/layout/index.vue'

// 路由配置
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    redirect: to => {
      const token = getToken()
      if (!token) return '/login'
      
      const userRole = localStorage.getItem('userRole')
      switch (userRole) {
        case 'STUDENT':
          return '/student/dashboard'
        case 'TEACHER':
          return '/teacher/dashboard'
        case 'ADMIN':
          return '/admin/dashboard'
        default:
          return '/login'
      }
    }
  },
  {
    path: '/student',
    component: () => import('@/layout/index.vue'),
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
    component: () => import('@/layout/index.vue'),
    redirect: '/teacher/dashboard',
    meta: { role: 'TEACHER', requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'TeacherDashboard',
        component: () => import('@/views/teacher/Dashboard.vue'),
        meta: { title: '首页', role: 'TEACHER' }
      },
      {
        path: 'timetable',
        name: 'TeacherTimetable',
        component: () => import('@/views/teacher/Timetable.vue'),
        meta: { title: '课表管理', role: 'TEACHER' }
      },
      {
        path: 'courses',
        name: 'TeacherCourses',
        component: () => import('@/views/teacher/Courses.vue'),
        meta: { title: '课程管理', role: 'TEACHER' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 学生选课系统` : '学生选课系统'

  const token = getToken()
  const userRole = localStorage.getItem('userRole')

  // 检查是否需要登录
  if (to.meta.requiresAuth && !token) {
    ElMessage.warning('请先登录')
    next('/login')
    return
  }

  // 检查角色权限
  if (to.meta.role && to.meta.role !== userRole) {
    ElMessage.warning('无权访问')
    next('/')
    return
  }

  // 已登录用户访问登录页面
  if (to.path === '/login' && token) {
    next('/')
    return
  }

  next()
})

export default router 