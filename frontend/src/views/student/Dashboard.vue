<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>我的课程数</span>
            </div>
          </template>
          <div class="card-content">
            <span class="number">{{ stats?.totalCourses || 0 }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>已修学分</span>
            </div>
          </template>
          <div class="card-content">
            <span class="number">{{ stats?.totalCredits || 0 }}</span>
          </div>
        </el-card>
      </el-col>

    </el-row>

    <!-- 成绩预警表格 -->
    <el-card class="mt-4">
      <template #header>
        <div class="card-header">
          <span>成绩预警</span>
        </div>
      </template>
      <el-table :data="gradeAlerts" stripe>
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="teacherName" label="任课教师" />
        <el-table-column prop="score" label="成绩">
          <template #default="scope">
            <span style="color: #f56c6c">{{ scope.row.score }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card class="recent-courses" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>我的课程</span>
        </div>
      </template>
      <el-table :data="courses" stripe v-loading="loading">
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="teacher" label="任课教师" />
        <el-table-column prop="time" label="上课时间" />
        <el-table-column prop="location" label="上课地点" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const stats = ref({
  totalCourses: 0,
  totalCredits: 0,
  averageScore: 0
})

const courses = ref([])
const loading = ref(true)
const gradeAlerts = ref([])

const fetchStats = async () => {
  try {
    const res = await request.get('/student/stats')
    console.log('统计数据:', res)
    stats.value = res || {
      totalCourses: 0,
      totalCredits: 0,
      averageScore: 0
    }
  } catch (error) {
    console.error('获取统计信息失败:', error)
    ElMessage.error('获取统计信息失败')
  }
}

const fetchCourses = async () => {
  try {
    const res = await request.get('/student/courses')
    console.log('课程数据:', res)
    courses.value = Array.isArray(res) ? res : []
  } catch (error) {
    console.error('获取课程信息失败:', error)
    ElMessage.error('获取课程信息失败')
  } finally {
    loading.value = false
  }
}

const fetchGradeAlerts = async () => {
  try {
    const res = await request.get('/student/grade-alerts')
    console.log('成绩预警数据:', res)
    gradeAlerts.value = Array.isArray(res) ? res : []
  } catch (error) {
    console.error('获取成绩预警信息失败:', error)
    ElMessage.error('获取成绩预警信息失败')
  }
}

onMounted(async () => {
  await Promise.all([fetchStats(), fetchCourses(), fetchGradeAlerts()])
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

.recent-courses {
  margin-top: 20px;
}

.mt-4 {
  margin-top: 16px;
}
</style> 