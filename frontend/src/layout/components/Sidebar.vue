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
      <sidebar-item v-for="route in routes" :key="route.path" :item="route" :base-path="route.path" />
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import SidebarItem from './SidebarItem.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const isCollapse = computed(() => false)

const routes = computed(() => {
  const role = userStore.role
  return router.options.routes.filter(route => {
    if (!route.meta?.roles) return true
    return route.meta.roles.includes(role)
  })
})
</script>

<style scoped>
.sidebar {
  height: 100%;
  background-color: #304156;
}

.el-menu {
  border: none;
}
</style> 