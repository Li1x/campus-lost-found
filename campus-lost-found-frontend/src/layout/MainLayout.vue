<template>
  <div class="layout-container">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-inner">
        <div class="logo-area" @click="router.push('/')">
          <el-icon :size="26" class="logo-icon"><Compass /></el-icon>
          <span class="logo-text">校园失物招领平台</span>
        </div>

        <nav class="nav-links">
          <router-link to="/" class="nav-item">首页</router-link>
          <router-link to="/lost" class="nav-item">寻物启事</router-link>
          <router-link to="/found" class="nav-item">招领信息</router-link>
          <router-link to="/publish" class="nav-item highlight">
            <el-icon><Plus /></el-icon>
            <span>发布信息</span>
          </router-link>
        </nav>

        <div class="user-area">
          <template v-if="userStore.isLoggedIn">
            <!-- 站内消息图标 -->
            <el-badge :value="unreadCount" :max="99" :hidden="unreadCount === 0" class="msg-badge">
              <el-button link @click="router.push('/messages')">
                <el-icon :size="20"><Bell /></el-icon>
              </el-button>
            </el-badge>

            <!-- 用户下拉菜单 -->
            <el-dropdown trigger="click" @command="handleCommand">
              <span class="user-dropdown-link">
                <el-avatar :size="32" :src="userStore.userInfo?.avatar || defaultAvatar">
                  {{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}
                </el-avatar>
                <span class="nickname">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="my-items">
                    <el-icon><Document /></el-icon>我发布的物品
                  </el-dropdown-item>
                  <el-dropdown-item command="my-claims">
                    <el-icon><Tickets /></el-icon>我的认领记录
                  </el-dropdown-item>
                  <el-dropdown-item v-if="userStore.isAdmin" divided command="admin">
                    <el-icon><Setting /></el-icon>管理后台
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>

          <template v-else>
            <el-button type="primary" plain @click="router.push('/login')">登录</el-button>
            <el-button type="primary" @click="router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </header>

    <!-- 主体内容 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-inner">
        <p>© 2026 校园失物招领系统 - 助力校园爱心互助与诚信流转</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox, ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const unreadCount = ref<number>(0)

function handleCommand(command: string) {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'my-items':
      router.push('/my-items')
      break
    case 'my-claims':
      router.push('/my-claims')
      break
    case 'admin':
      router.push('/admin')
      break
    case 'logout':
      ElMessageBox.confirm('确定要退出当前账号吗？', '提示', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消',
      }).then(async () => {
        await userStore.logout()
        ElMessage.success('已安全退出')
        router.push('/login')
      })
      break
  }
}

onMounted(() => {
  if (userStore.isLoggedIn) {
    userStore.fetchUserInfo()
  }
})
</script>

<style scoped>
.layout-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f7f9fc;
}

.header {
  background: #ffffff;
  border-bottom: 1px solid #eef2f6;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  color: #409eff;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 24px;
}

.nav-item {
  text-decoration: none;
  color: #475569;
  font-size: 15px;
  font-weight: 500;
  transition: color 0.2s;
  display: flex;
  align-items: center;
  gap: 4px;
}

.nav-item:hover,
.nav-item.router-link-active {
  color: #409eff;
}

.nav-item.highlight {
  background: #ecf5ff;
  color: #409eff;
  padding: 6px 14px;
  border-radius: 20px;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-dropdown-link {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.nickname {
  font-size: 14px;
  color: #334155;
  font-weight: 500;
}

.main-content {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 20px auto;
  padding: 0 20px;
}

.footer {
  background: #ffffff;
  border-top: 1px solid #eef2f6;
  padding: 24px 0;
  text-align: center;
  color: #94a3b8;
  font-size: 13px;
}
</style>
