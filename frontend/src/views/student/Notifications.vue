<template>
  <div class="notifications-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的通知</span>
          <el-button type="primary" @click="refreshList">刷新</el-button>
        </div>
      </template>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="未读通知" name="unread">
          <el-empty v-if="unreadList.length === 0" description="暂无未读通知" />
          <div v-else class="notification-list">
            <div v-for="item in unreadList" :key="item.id" class="notification-item">
              <div class="notification-header">
                <span class="title">{{ item.title }}</span>
                <span class="time">{{ formatTime(item.createdAt) }}</span>
              </div>
              <div class="content">{{ item.content }}</div>
              <div class="actions">
                <el-button type="primary" link @click="markAsRead(item.id)">标记为已读</el-button>
              </div>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="已读通知" name="read">
          <el-empty v-if="readList.length === 0" description="暂无已读通知" />
          <div v-else class="notification-list">
            <div v-for="item in readList" :key="item.id" class="notification-item">
              <div class="notification-header">
                <span class="title">{{ item.title }}</span>
                <span class="time">{{ formatTime(item.createdAt) }}</span>
              </div>
              <div class="content">{{ item.content }}</div>
              <div class="read-time">
                阅读时间：{{ formatTime(item.readAt) }}
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import dayjs from 'dayjs'

const activeTab = ref('unread')
const unreadList = ref([])
const readList = ref([])

// 获取通知列表
const refreshList = async () => {
  try {
    const res = await request.get('/student/notifications')
    unreadList.value = res.data.filter(item => item.status === 'UNREAD')
    readList.value = res.data.filter(item => item.status === 'READ')
  } catch (error) {
    ElMessage.error('获取通知列表失败')
  }
}

// 标记为已读
const markAsRead = async (id) => {
  try {
    await request.put(`/student/notifications/${id}/read`)
    ElMessage.success('已标记为已读')
    refreshList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

onMounted(() => {
  refreshList()
})
</script>

<style scoped>
.notifications-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notification-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.notification-item {
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  transition: all 0.3s;
}

.notification-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.time {
  font-size: 14px;
  color: #909399;
}

.content {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  margin-bottom: 8px;
}

.actions {
  display: flex;
  justify-content: flex-end;
}

.read-time {
  font-size: 12px;
  color: #909399;
  text-align: right;
}
</style> 