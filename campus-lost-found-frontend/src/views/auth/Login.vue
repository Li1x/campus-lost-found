<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <el-icon :size="36" class="auth-logo"><Compass /></el-icon>
        <h2>欢迎登录</h2>
        <p class="subtitle">校园失物招领系统</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        size="large"
        @keyup.enter="handleLogin"
      >
        <el-form-item label="学号 / 用户名" prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入学号或用户名"
            prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item label="登录密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码（不少于6位）"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <div class="form-actions">
          <el-button
            type="primary"
            :loading="loading"
            class="submit-btn"
            @click="handleLogin"
          >
            立即登录
          </el-button>
        </div>

        <div class="auth-footer">
          <span>还没有账号？</span>
          <router-link to="/register" class="link">免费注册</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
})

const rules: FormRules = {
  username: [
    { required: true, message: '请输入学号或用户名', trigger: 'blur' },
    { min: 3, max: 32, message: '长度在 3 到 32 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' },
  ],
}

async function handleLogin() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        await userStore.login({
          username: form.username.trim(),
          password: form.password,
        })
        ElMessage.success('登录成功')
        const redirect = (route.query.redirect as string) || '/'
        router.push(redirect)
      } catch (err: any) {
        // 错误信息已由 Axios 拦截器统一提示
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e0f2fe 0%, #f0fdf4 100%);
  padding: 20px;
}

.auth-card {
  width: 100%;
  max-width: 420px;
  background: #ffffff;
  border-radius: 12px;
  padding: 36px 32px;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.08), 0 8px 10px -6px rgba(0, 0, 0, 0.04);
}

.auth-header {
  text-align: center;
  margin-bottom: 28px;
}

.auth-logo {
  color: #409eff;
  margin-bottom: 8px;
}

.auth-header h2 {
  font-size: 22px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 6px 0;
}

.subtitle {
  font-size: 13px;
  color: #64748b;
  margin: 0;
}

.form-actions {
  margin-top: 10px;
}

.submit-btn {
  width: 100%;
  border-radius: 8px;
  font-size: 15px;
  padding: 12px 0;
}

.auth-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 13px;
  color: #64748b;
}

.link {
  color: #409eff;
  text-decoration: none;
  font-weight: 500;
  margin-left: 4px;
}

.link:hover {
  text-decoration: underline;
}
</style>
