import Layout from '@/layout/index.vue'

export default {
  path: '/student',
  component: Layout,
  redirect: '/student/dashboard',
  meta: { role: 'STUDENT', requiresAuth: true },
  children: [
    {
      path: 'dashboard',
      name: 'StudentDashboard',
      component: () => import('@/views/student/Dashboard.vue'),
      meta: { title: '首页', role: 'STUDENT' }
    },
    {
      path: 'courses',
      name: 'StudentCourses',
      component: () => import('@/views/student/Courses.vue'),
      meta: { title: '选课', role: 'STUDENT' }
    },
    {
      path: 'select-course',
      name: 'StudentSelectCourse',
      component: () => import('@/views/student/SelectCourse.vue'),
      meta: { title: '选课', role: 'STUDENT' }
    },
    {
      path: 'grades',
      name: 'StudentGrades',
      component: () => import('@/views/student/Grades.vue'),
      meta: { title: '成绩查询', role: 'STUDENT' }
    },
    {
      path: 'notifications',
      name: 'StudentNotifications',
      component: () => import('@/views/student/Notifications.vue'),
      meta: { title: '预警提醒', role: 'STUDENT' }
    }
  ]
} 