<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>总课程数</span>
            </div>
          </template>
          <div class="card-body">
            <h2>{{ stats.courseCount || 0 }}</h2>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>总教师数</span>
            </div>
          </template>
          <div class="card-body">
            <h2>{{ stats.teacherCount || 0 }}</h2>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>总学生数</span>
            </div>
          </template>
          <div class="card-body">
            <h2>{{ stats.studentCount || 0 }}</h2>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>总教室数</span>
            </div>
          </template>
          <div class="card-body">
            <h2>{{ stats.classroomCount || 0 }}</h2>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const stats = ref({})

const fetchStats = async () => {
  try {
    const res = await request.get('/api/admin/stats')
    stats.value = res.data
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-body {
  text-align: center;
  padding: 20px 0;
}

.card-body h2 {
  margin: 0;
  font-size: 28px;
  color: #409eff;
}

.el-card {
  margin-bottom: 20px;
}
</style> 