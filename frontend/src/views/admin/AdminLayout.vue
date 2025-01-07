<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <el-menu
        :router="true"
        :default-active="$route.path"
        class="menu"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>控制台</span>
        </el-menu-item>
        <el-menu-item index="/admin/courses">
          <el-icon><Reading /></el-icon>
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
        <el-menu-item index="/admin/users">
          <el-icon><UserFilled /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div class="header">
          <h2>教务管理系统</h2>
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
import { DataLine, Reading, User, School, UserFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'

const userStore = useUserStore()
const router = useRouter()
const { info: userInfo } = storeToRefs(userStore)

const handleLogout = async () => {
  await userStore.logout()
  router.push('/login')
}
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
  height: 100%;
  border-bottom: 1px solid #dcdfe6;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style> 