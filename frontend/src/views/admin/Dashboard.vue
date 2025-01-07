<!-- 管理员仪表盘页面 -->
<template>
  <div class="dashboard-container">
    <!-- 欢迎卡片 -->
    <el-card class="welcome-card" shadow="hover">
      <div class="welcome-content">
        <h2>欢迎回来，{{ userStore.name }}</h2>
        <p>今天是 {{ currentDate }}</p>
      </div>
    </el-card>

    <!-- 快速操作区 -->
    <el-row :gutter="20" class="quick-actions">
      <el-col :span="6">
        <el-card shadow="hover" @click="navigateTo('/admin/courses')">
          <div class="action-card">
            <el-icon size="24"><Document /></el-icon>
            <span>课程管理</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" @click="navigateTo('/admin/classrooms')">
          <div class="action-card">
            <el-icon size="24"><School /></el-icon>
            <span>教室管理</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" @click="navigateTo('/admin/analysis')">
          <div class="action-card">
            <el-icon size="24"><TrendCharts /></el-icon>
            <span>统计分析</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" @click="navigateTo('/admin/grade-alerts')">
          <div class="action-card">
            <el-icon size="24"><Warning /></el-icon>
            <span>成绩异常提醒</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" @click="handleLogout">
          <div class="action-card">
            <el-icon size="24"><SwitchButton /></el-icon>
            <span>退出登录</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 系统概览 -->
    <el-row :gutter="20" class="system-overview">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>待处理事项</span>
            </div>
          </template>
          <el-table :data="todoList" style="width: 100%">
            <el-table-column prop="type" label="类型" width="120" />
            <el-table-column prop="content" label="内容" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === '待处理' ? 'warning' : 'success'">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>系统状态</span>
            </div>
          </template>
          <div class="system-status">
            <div class="status-item">
              <div class="label">CPU 使用率</div>
              <el-progress :percentage="systemStatus.cpu" :color="getProgressColor" />
            </div>
            <div class="status-item">
              <div class="label">内存使用率</div>
              <el-progress :percentage="systemStatus.memory" :color="getProgressColor" />
            </div>
            <div class="status-item">
              <div class="label">磁盘使用率</div>
              <el-progress :percentage="systemStatus.disk" :color="getProgressColor" />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Document, School, TrendCharts, SwitchButton, Warning } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()

// 当前日期
const currentDate = computed(() => {
  const date = new Date()
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

// 待处理事项
const todoList = ref([])

// 系统状态
const systemStatus = ref({
  cpu: 0,
  memory: 0,
  disk: 0
})

// 获取待处理事项
const fetchTodoList = async () => {
  try {
    const res = await request.get('/admin/todo-list')
    todoList.value = res
  } catch (error) {
    console.error('获取待处理事项失败:', error)
  }
}

// 获取系统状态
const fetchSystemStatus = async () => {
  try {
    const res = await request.get('/admin/system-status')
    systemStatus.value = res
  } catch (error) {
    console.error('获取系统状态失败:', error)
  }
}

// 进度条颜色
const getProgressColor = (percentage) => {
  if (percentage < 70) return '#67C23A'
  if (percentage < 90) return '#E6A23C'
  return '#F56C6C'
}

// 页面跳转
const navigateTo = (path) => {
  router.push(path)
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      type: 'warning'
    })
    await userStore.logout()
    router.push('/login')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退出登录失败:', error)
    }
  }
}

onMounted(() => {
  fetchTodoList()
  fetchSystemStatus()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.welcome-card {
  margin-bottom: 20px;
}

.welcome-content {
  text-align: center;
}

.welcome-content h2 {
  margin: 0;
  color: #409EFF;
}

.quick-actions {
  margin-bottom: 20px;
}

.action-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-card:hover {
  transform: translateY(-5px);
}

.system-overview {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.system-status {
  padding: 10px;
}

.status-item {
  margin-bottom: 20px;
}

.status-item .label {
  margin-bottom: 5px;
}
</style> 