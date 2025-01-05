<template>
  <div class="app-wrapper">
    <!-- 侧边栏 -->
    <div class="sidebar-container">
      <div class="logo">
        <span>学生选课系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        :router="true"
      >
        <template v-if="userRole === 'TEACHER'">
          <el-menu-item index="/teacher/dashboard">
            <el-icon><Menu /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/teacher/timetable">
            <el-icon><Calendar /></el-icon>
            <span>课表管理</span>
          </el-menu-item>
          <el-menu-item index="/teacher/courses">
            <el-icon><List /></el-icon>
            <span>课程管理</span>
          </el-menu-item>
        </template>

        <template v-else-if="userRole === 'STUDENT'">
          <el-menu-item index="/student/dashboard">
            <el-icon><Menu /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/student/courses">
            <el-icon><List /></el-icon>
            <span>选课</span>
          </el-menu-item>
          <el-menu-item index="/student/grades">
            <el-icon><Document /></el-icon>
            <span>成绩查询</span>
          </el-menu-item>
        </template>

        <template v-else-if="userRole === 'ADMIN'">
          <el-menu-item index="/admin/dashboard">
            <el-icon><Menu /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/admin/courses">
            <el-icon><List /></el-icon>
            <span>课程管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/teachers">
            <el-icon><User /></el-icon>
            <span>教师管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/classrooms">
            <el-icon><School /></el-icon>
            <span>教室管理</span>
          </el-menu-item>
        </template>
      </el-menu>
    </div>

    <!-- 主容器 -->
    <div class="main-container">
      <!-- 顶部导航栏 -->
      <div class="navbar">
        <div class="right-menu">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="avatar-wrapper">
              <el-avatar :size="30" :icon="UserFilled" />
              <span class="username">{{ username }}</span>
              <el-icon><CaretBottom /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 主要内容区 -->
      <div class="app-main">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Menu, List, Calendar, UserFilled, CaretBottom, Document, School, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { removeToken } from '@/utils/auth'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const username = computed(() => {
  if (userStore.name) return userStore.name
  if (userStore.info?.name) return userStore.info.name
  return userStore.token ? '加载中...' : '未登录'
})

const userRole = computed(() => userStore.role)

const activeMenu = computed(() => route.path)

const handleCommand = async (command) => {
  if (command === 'logout') {
    await userStore.logout()
    router.push('/login')
    ElMessage.success('退出成功')
  }
}

onMounted(async () => {
  if (userStore.token && !userStore.name) {
    try {
      await userStore.getInfo()
    } catch (error) {
      console.error('Failed to get user info:', error)
    }
  }
})
</script>

<style scoped>
.app-wrapper {
  display: flex;
  height: 100vh;
  width: 100%;
}

.sidebar-container {
  width: 210px;
  height: 100%;
  background-color: #304156;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  overflow-y: auto;
}

.logo {
  height: 50px;
  line-height: 50px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}

.sidebar-menu {
  border: none;
}

.main-container {
  margin-left: 210px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.navbar {
  height: 50px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  padding: 0 20px;
  justify-content: flex-end;
}

.right-menu {
  display: flex;
  align-items: center;
}

.avatar-wrapper {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 0 8px;
}

.username {
  margin: 0 8px;
  color: #606266;
}

.app-main {
  flex: 1;
  overflow: auto;
  background: #f0f2f5;
  padding: 20px;
}
</style> 