<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <el-icon :size="36" class="auth-logo"><Compass /></el-icon>
        <h2>创建新账号</h2>
        <p class="subtitle">加入校园失物招领互助网络</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        size="large"
      >
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentId">
              <el-input v-model="form.studentId" placeholder="真实学号" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="字母/数字" clearable />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="昵称 / 姓名" prop="nickname">
              <el-input v-model="form.nickname" placeholder="显示名称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phone">
              <el-input v-model="form.phone" placeholder="11位手机号" clearable />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="登录密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="6-32位密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次确认密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="电子邮箱（选填）" prop="email">
          <el-input v-model="form.email" placeholder="接收招领通知邮件" clearable />
        </el-form-item>

        <div class="form-actions">
          <el-button
            type="primary"
            :loading="loading"
            class="submit-btn"
            @click="handleRegister"
          >
            立即注册
          </el-button>
        </div>

        <div class="auth-footer">
          <span>已有账号？</span>
          <router-link to="/login" class="link">直接登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { registerApi } from '@/api/auth'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  studentId: '',
  username: '',
  nickname: '',
  phone: '',
  password: '',
  confirmPassword: '',
  email: '',
})

const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  studentId: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { min: 4, max: 32, message: '学号长度在 4-32 位之间', trigger: 'blur' },
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]{3,20}$/, message: '用户名必须为3-20位字母/数字/下划线', trigger: 'blur' },
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { max: 20, message: '昵称不能超过20字', trigger: 'blur' },
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度在 6 到 32 个字符', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' },
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
  ],
}

async function handleRegister() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        await registerApi({
          studentId: form.studentId.trim(),
          username: form.username.trim(),
          nickname: form.nickname.trim(),
          phone: form.phone.trim(),
          password: form.password,
          email: form.email.trim() || undefined,
        })
        ElMessage.success('注册成功，请使用新账号登录')
        router.push('/login')
      } catch (err: any) {
        // 错误已拦截提示
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
  padding: 30px 20px;
}

.auth-card {
  width: 100%;
  max-width: 480px;
  background: #ffffff;
  border-radius: 12px;
  padding: 32px 28px;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.08), 0 8px 10px -6px rgba(0, 0, 0, 0.04);
}

.auth-header {
  text-align: center;
  margin-bottom: 24px;
}

.auth-logo {
  color: #409eff;
  margin-bottom: 6px;
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
  margin-top: 12px;
}

.submit-btn {
  width: 100%;
  border-radius: 8px;
  font-size: 15px;
  padding: 12px 0;
}

.auth-footer {
  text-align: center;
  margin-top: 18px;
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
