<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <div class="logo">学生选课系统</div>
      <el-menu
        :router="true"
        :default-active="$route.path"
        class="menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/student/dashboard">
          <el-icon><House /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/student/select-course">
          <el-icon><List /></el-icon>
          <span>选课</span>
        </el-menu-item>
        <el-menu-item index="/student/grades">
          <el-icon><Document /></el-icon>
          <span>成绩查询</span>
        </el-menu-item>
        <el-menu-item index="/student/notifications">
          <el-icon><Warning /></el-icon>
          <span>预警提醒</span>
          <el-badge v-if="unreadCount > 0" :value="unreadCount" class="notification-badge" />
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div class="header">
          <h2>教务管理系统 - 学生端</h2>
          <div class="user-info">
            <span>{{ userInfo?.username }}</span>
            <el-button type="text" @click="handleLogout">退出</el-button>
          </div>
        </div>
      </el-header>
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { House, List, Document, Warning } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { ref, onMounted, onUnmounted, watch } from 'vue'
import request from '@/utils/request'

const store = useStore()
const router = useRouter()
const userInfo = computed(() => store.state.userInfo)
const userStore = useUserStore()
const unreadCount = ref(0)

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    store.commit('logout')
    router.push('/login')
  })
}

// 获取未读通知数量
const getUnreadCount = async () => {
  try {
    const res = await request.get('/student/notifications/unread/count')
    unreadCount.value = res.data
  } catch (error) {
    console.error('获取未读通知数量失败:', error)
  }
}

// 定时刷新未读消息数量
let timer = null

onMounted(() => {
  // 初始获取未读消息数量
  getUnreadCount()
  
  // 每30秒刷新一次未读消息数量
  timer = setInterval(getUnreadCount, 30000)
})

// 在路由变化时也重新获取未读消息数量
watch(() => router.currentRoute.value.path, () => {
  getUnreadCount()
})

onUnmounted(() => {
  // 组件卸载时清除定时器
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.menu {
  height: 100%;
  border-right: none;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  border-bottom: 1px solid #eee;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.el-header {
  background-color: white;
  padding: 0;
}

.el-aside {
  background-color: #304156;
}

.el-menu {
  border-right: none;
  width: 100%;
}

.el-menu-item {
  height: 56px;
  line-height: 56px;
  padding: 0 20px !important;
}

.el-menu-item .el-icon {
  font-size: 18px;
  margin-right: 16px;
  width: 24px;
  text-align: center;
}

.el-menu-item:hover {
  background-color: #263445 !important;
}

.el-menu-item.is-active {
  background-color: #263445;
}

.notification-badge {
  margin-left: 8px;
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
}

.el-badge :deep(.el-badge__content) {
  background-color: #f56c6c;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #1f2d3d;
}
</style> 