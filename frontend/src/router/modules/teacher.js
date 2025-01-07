import Layout from '@/layout/index.vue'

export default {
  path: '/teacher',
  component: Layout,
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
      meta: { title: '课表查询', role: 'TEACHER' }
    },
    {
      path: 'courses',
      name: 'TeacherCourses',
      component: () => import('@/views/teacher/Courses.vue'),
      meta: { title: '课程管理', role: 'TEACHER' }
    },
    {
      path: 'grades',
      name: 'TeacherGrades',
      component: () => import('@/views/teacher/Grades.vue'),
      meta: { title: '成绩管理', role: 'TEACHER' }
    }
  ]
} 