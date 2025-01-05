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
              <span>已修学分</span>
            </div>
          </template>
          <div class="card-content">
            <span class="number">{{ stats.totalCredits }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>平均成绩</span>
            </div>
          </template>
          <div class="card-content">
            <span class="number">{{ stats.averageScore.toFixed(1) }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="recent-courses" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>我的课程</span>
        </div>
      </template>
      <el-table :data="courses" stripe>
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

const fetchStats = async () => {
  try {
    const res = await request.get('/api/student/stats')
    stats.value = res.data
  } catch (error) {
    console.error('获取统计信息失败:', error)
    ElMessage.error('获取统计信息失败')
  }
}

const fetchCourses = async () => {
  try {
    const res = await request.get('/api/student/courses')
    courses.value = res.data
  } catch (error) {
    console.error('获取课程信息失败:', error)
    ElMessage.error('获取课程信息失败')
  }
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

.recent-courses {
  margin-top: 20px;
}
</style> 