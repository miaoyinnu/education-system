import AdminLayout from '@/views/admin/AdminLayout.vue'
import Dashboard from '@/views/admin/Dashboard.vue'
import Courses from '@/views/admin/Courses.vue'
import Teachers from '@/views/admin/Teachers.vue'
import Classrooms from '@/views/admin/Classrooms.vue'
import Users from '@/views/admin/Users.vue'
import Analysis from '@/views/admin/Analysis.vue'
import CourseSchedule from '@/views/admin/CourseSchedule.vue'

const adminRoutes = {
  path: '/admin',
  component: AdminLayout,
  children: [
    {
      path: 'dashboard',
      name: 'AdminDashboard',
      component: Dashboard,
      meta: { title: '控制台' }
    },
    {
      path: 'courses',
      name: 'AdminCourses',
      component: Courses,
      meta: { title: '课程管理' }
    },
    {
      path: 'schedule',
      name: 'CourseSchedule',
      component: CourseSchedule,
      meta: { title: '排课系统' }
    },
    {
      path: 'teachers',
      name: 'AdminTeachers',
      component: Teachers,
      meta: { title: '教师管理' }
    },
    {
      path: 'classrooms',
      name: 'AdminClassrooms',
      component: Classrooms,
      meta: { title: '教室管理' }
    },
    {
      path: 'users',
      name: 'AdminUsers',
      component: Users,
      meta: { title: '用户管理' }
    },
    {
      path: 'analysis',
      name: 'AdminAnalysis',
      component: Analysis,
      meta: { title: '统计分析' }
    }
  ]
}

export default adminRoutes 