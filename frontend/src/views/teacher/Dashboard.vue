<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>我的课程数</span>
            </div>
          </template>
          <div class="card-content">
            <span class="number">{{ stats.totalCourses }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>学生总数</span>
            </div>
          </template>
          <div class="card-content">
            <span class="number">{{ stats.totalStudents }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>平均分数</span>
            </div>
          </template>
          <div class="card-content">
            <span class="number">{{ stats.averageScore.toFixed(1) }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="course-list" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>我的课程</span>
        </div>
      </template>
      <el-table :data="courses" stripe>
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="semester" label="学期" />
        <el-table-column prop="time" label="上课时间" />
        <el-table-column prop="location" label="上课地点" />
        <el-table-column prop="currentStudents" label="已选人数" />
        <el-table-column prop="maxStudents" label="最大人数" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleGradeManagement(scope.row)">
              成绩管理
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 统计数据
const stats = ref({
  totalCourses: 0,
  totalStudents: 0,
  averageScore: 0
})

// 课程列表
const courses = ref([])

// 获取统计数据
const fetchStats = async () => {
  try {
    const res = await request.get('/api/teacher/stats')
    stats.value = res.data
  } catch (error) {
    console.error('获取统计信息失败:', error)
    ElMessage.error('获取统计信息失败')
  }
}

// 获取课程列表
const fetchCourses = async () => {
  try {
    const res = await request.get('/api/teacher/courses')
    courses.value = res.data
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  }
}

// 跳转到成绩管理页面
const handleGradeManagement = (course) => {
  router.push({
    name: 'TeacherGrades',
    params: { courseId: course.id }
  })
}

onMounted(() => {
  fetchStats()
  fetchCourses()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.box-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-content {
  text-align: center;
  padding: 20px;
}

.number {
  font-size: 36px;
  font-weight: bold;
  color: #409EFF;
}

.course-list {
  margin-top: 20px;
}
</style> 