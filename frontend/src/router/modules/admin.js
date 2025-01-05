import Layout from '@/layout'

const adminRoutes = {
  path: '/admin',
  component: Layout,
  redirect: '/admin/course',
  name: 'Admin',
  meta: {
    title: '管理员',
    icon: 'el-icon-s-tools',
    roles: ['admin']
  },
  children: [
    {
      path: 'course',
      component: () => import('@/views/admin/CourseManagement'),
      name: 'CourseManagement',
      meta: { title: '课程管理', icon: 'el-icon-reading' }
    },
    {
      path: 'classroom',
      component: () => import('@/views/admin/ClassroomManagement'),
      name: 'ClassroomManagement',
      meta: { title: '教室管理', icon: 'el-icon-school' }
    },
    {
      path: 'statistics',
      component: () => import('@/views/admin/Statistics'),
      name: 'Statistics',
      meta: { title: '统计分析', icon: 'el-icon-data-analysis' }
    },
    {
      path: 'settings',
      component: () => import('@/views/admin/Settings'),
      name: 'Settings',
      meta: { title: '系统设置', icon: 'el-icon-setting' }
    }
  ]
}

export default adminRoutes 