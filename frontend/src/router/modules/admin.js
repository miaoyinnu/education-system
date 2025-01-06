import Layout from '@/layout/index.vue'

const adminRoutes = {
  path: '/admin',
  component: Layout,
  redirect: '/admin/dashboard',
  name: 'Admin',
  meta: {
    title: '管理员',
    icon: 'el-icon-s-tools',
    role: 'ADMIN',
    requiresAuth: true
  },
  children: [
    {
      path: 'dashboard',
      component: () => import('@/views/admin/Dashboard.vue'),
      name: 'AdminDashboard',
      meta: { title: '首页', icon: 'el-icon-menu', role: 'ADMIN' }
    },
    {
      path: 'courses',
      component: () => import('@/views/admin/Courses.vue'),
      name: 'AdminCourses',
      meta: { title: '课程管理', icon: 'el-icon-reading', role: 'ADMIN' }
    },
    {
      path: 'teachers',
      component: () => import('@/views/admin/Teachers.vue'),
      name: 'AdminTeachers',
      meta: { title: '教师管理', icon: 'el-icon-user', role: 'ADMIN' }
    },
    {
      path: 'classrooms',
      component: () => import('@/views/admin/Classrooms.vue'),
      name: 'AdminClassrooms',
      meta: { title: '教室管理', icon: 'el-icon-school', role: 'ADMIN' }
    },
    {
      path: 'statistics',
      component: () => import('@/views/admin/Statistics.vue'),
      name: 'AdminStatistics',
      meta: { title: '统计分析', icon: 'el-icon-data-analysis', role: 'ADMIN' }
    },
    {
      path: 'settings',
      component: () => import('@/views/admin/Settings.vue'),
      name: 'AdminSettings',
      meta: { title: '系统设置', icon: 'el-icon-setting', role: 'ADMIN' }
    }
  ]
}

export default adminRoutes 