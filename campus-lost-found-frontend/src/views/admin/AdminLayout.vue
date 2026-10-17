<template>
  <div class="admin-container">
    <el-container class="admin-wrapper">
      <!-- 侧边导航 -->
      <el-aside width="220px" class="admin-aside">
        <div class="admin-logo" @click="router.push('/')">
          <el-icon :size="24"><Management /></el-icon>
          <span>管理控制台</span>
        </div>
        <el-menu
          :default-active="route.path"
          router
          background-color="#1e293b"
          text-color="#94a3b8"
          active-text-color="#ffffff"
          class="admin-menu"
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><DataLine /></el-icon>
            <span>数据看板</span>
          </el-menu-item>
          <el-menu-item index="/admin/review">
            <el-icon><Stamp /></el-icon>
            <span>审核中心</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon><UserFilled /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/">
            <el-icon><Back /></el-icon>
            <span>返回前台</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主区域 -->
      <el-container>
        <el-header height="60px" class="admin-header">
          <div class="header-left">
            <span class="page-title">{{ currentTitle }}</span>
          </div>
          <div class="header-right">
            <span>{{ userStore.userInfo?.nickname }} ({{ userStore.userInfo?.role }})</span>
          </div>
        </el-header>

        <el-main class="admin-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const currentTitle = computed(() => {
  if (route.path.includes('/dashboard')) return '系统运营数据统计'
  if (route.path.includes('/review')) return '失物/招领与认领审核'
  if (route.path.includes('/users')) return '校园用户与权限管理'
  return '管理中心'
})
</script>

<style scoped>
.admin-container {
  height: 100vh;
  display: flex;
}
.admin-wrapper {
  width: 100%;
  height: 100%;
}
.admin-aside {
  background-color: #1e293b;
  display: flex;
  flex-direction: column;
}
.admin-logo {
  height: 60px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 20px;
  color: #ffffff;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 1px solid #334155;
  cursor: pointer;
}
.admin-menu {
  border-right: none;
  flex: 1;
}
.admin-header {
  background: #ffffff;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
}
.page-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}
.admin-main {
  background-color: #f1f5f9;
  padding: 24px;
  overflow-y: auto;
}
</style>
