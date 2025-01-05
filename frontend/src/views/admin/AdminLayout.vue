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
import { computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { DataLine, Reading, User, School } from '@element-plus/icons-vue'

const store = useStore()
const router = useRouter()
const userInfo = computed(() => store.state.userInfo)

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
  background-color: transparent;
  border-right: none;
}

.el-menu-item {
  color: #bfcbd9;
}

.el-menu-item:hover {
  color: #fff;
}

.el-menu-item.is-active {
  color: #409eff;
  background-color: #263445;
}
</style> 