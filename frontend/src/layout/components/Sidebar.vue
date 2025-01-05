<template>
  <div class="sidebar">
    <el-menu
      :default-active="activeMenu"
      :collapse="isCollapse"
      :unique-opened="false"
      :collapse-transition="false"
      mode="vertical"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409EFF"
    >
      <template v-if="userRole === 'STUDENT'">
        <el-menu-item index="/student/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/student/courses">
          <el-icon><Reading /></el-icon>
          <span>选课</span>
        </el-menu-item>
        <el-menu-item index="/student/grades">
          <el-icon><List /></el-icon>
          <span>成绩查询</span>
        </el-menu-item>
      </template>

      <template v-if="userRole === 'TEACHER'">
        <el-menu-item index="/teacher/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/teacher/courses">
          <el-icon><Reading /></el-icon>
          <span>课程管理</span>
        </el-menu-item>
        <el-menu-item index="/teacher/timetable">
          <el-icon><Calendar /></el-icon>
          <span>课表管理</span>
        </el-menu-item>
      </template>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Odometer, Reading, List, Calendar } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const isCollapse = computed(() => false)
const userRole = computed(() => userStore.role)
</script>

<style scoped>
.sidebar {
  height: 100%;
  background-color: #304156;
}

.el-menu {
  border: none;
}

.el-menu-item [class^="el-icon"] {
  margin-right: 16px;
  width: 24px;
  text-align: center;
  font-size: 18px;
}
</style> 