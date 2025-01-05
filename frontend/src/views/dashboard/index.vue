<template>
  <div class="dashboard">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>欢迎使用学生选课系统</span>
        </div>
      </template>
      
      <div class="welcome-content">
        <h3>{{ welcomeMessage }}</h3>
        <p>请从左侧菜单选择功能开始使用</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const userRole = localStorage.getItem('userRole')
const username = localStorage.getItem('username')

const welcomeMessage = computed(() => {
  const roleMap = {
    'STUDENT': '同学',
    'TEACHER': '老师',
    'ADMIN': '管理员'
  }
  const suffix = roleMap[userRole] || ''
  return `${username}${suffix}，欢迎回来！`
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

.welcome-content {
  text-align: center;
  padding: 40px 0;
}

.welcome-content h3 {
  margin-bottom: 20px;
  color: #303133;
}

.welcome-content p {
  color: #606266;
}
</style> 