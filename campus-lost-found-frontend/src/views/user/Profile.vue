<template>
  <div class="profile-container">
    <el-row :gutter="24">
      <!-- 左侧：个人信息卡片 -->
      <el-col :xs="24" :md="8">
        <el-card class="user-card" shadow="never">
          <div class="avatar-section">
            <el-avatar :size="80" :src="userStore.userInfo?.avatar || defaultAvatar">
              {{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}
            </el-avatar>
            <h3 class="name">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</h3>
            <p class="role-tag">
              <el-tag :type="roleTagType" effect="light" round>
                {{ roleLabel }}
              </el-tag>
            </p>
          </div>

          <el-divider />

          <div class="info-list">
            <div class="info-row">
              <span class="label">学号</span>
              <span class="value">{{ userStore.userInfo?.studentId || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="label">登录用户名</span>
              <span class="value">{{ userStore.userInfo?.username || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="label">注册时间</span>
              <span class="value">{{ formatDate(userStore.userInfo?.createdAt) }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：资料修改与改密选项卡 -->
      <el-col :xs="24" :md="16">
        <el-card shadow="never">
          <el-tabs v-model="activeTab">
            <!-- 基本资料 -->
            <el-tab-pane label="基本资料" name="basic">
              <el-form
                ref="profileFormRef"
                :model="profileForm"
                :rules="profileRules"
                label-width="100px"
                class="form-box"
              >
                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="profileForm.nickname" placeholder="输入昵称" />
                </el-form-item>

                <el-form-item label="头像地址" prop="avatar">
                  <el-input v-model="profileForm.avatar" placeholder="图片 URL 地址" />
                </el-form-item>

                <el-form-item label="手机号码" prop="phone">
                  <el-input v-model="profileForm.phone" placeholder="11位手机号码" />
                </el-form-item>

                <el-form-item label="电子邮箱" prop="email">
                  <el-input v-model="profileForm.email" placeholder="example@campus.edu.cn" />
                </el-form-item>

                <el-form-item label="联系方式" prop="contactInfo">
                  <el-input
                    v-model="profileForm.contactInfo"
                    type="textarea"
                    rows="2"
                    placeholder="例如微信号、QQ号或常在教学楼等"
                  />
                </el-form-item>

                <el-form-item>
                  <el-button type="primary" :loading="updatingProfile" @click="handleUpdateProfile">
                    保存修改
                  </el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- 修改密码 -->
            <el-tab-pane label="修改密码" name="password">
              <el-form
                ref="pwdFormRef"
                :model="pwdForm"
                :rules="pwdRules"
                label-width="100px"
                class="form-box"
              >
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input
                    v-model="pwdForm.oldPassword"
                    type="password"
                    placeholder="请输入当前正在使用的密码"
                    show-password
                  />
                </el-form-item>

                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="pwdForm.newPassword"
                    type="password"
                    placeholder="请输入不少于6位的新密码"
                    show-password
                  />
                </el-form-item>

                <el-form-item label="确认新密码" prop="confirmPassword">
                  <el-input
                    v-model="pwdForm.confirmPassword"
                    type="password"
                    placeholder="再次输入新密码"
                    show-password
                  />
                </el-form-item>

                <el-form-item>
                  <el-button type="danger" :loading="changingPwd" @click="handleChangePassword">
                    确认修改密码
                  </el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { updateProfileApi, changePasswordApi } from '@/api/user'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const activeTab = ref('basic')
const profileFormRef = ref<FormInstance>()
const pwdFormRef = ref<FormInstance>()

const updatingProfile = ref(false)
const changingPwd = ref(false)

const profileForm = reactive({
  nickname: '',
  avatar: '',
  phone: '',
  email: '',
  contactInfo: '',
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const roleLabel = computed(() => {
  const role = userStore.userInfo?.role
  if (role === 'SUPER_ADMIN') return '超级管理员'
  if (role === 'ADMIN') return '管理员'
  return '认证学生'
})

const roleTagType = computed(() => {
  const role = userStore.userInfo?.role
  if (role === 'SUPER_ADMIN') return 'danger'
  if (role === 'ADMIN') return 'warning'
  return 'success'
})

const profileRules: FormRules = {
  nickname: [{ required: true, message: '昵称不能为空', trigger: 'blur' }],
  phone: [
    { required: true, message: '手机号不能为空', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' },
  ],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
}

const validateNewPasswordConfirm = (_rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== pwdForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const pwdRules: FormRules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度在 6 到 32 个字符之间', trigger: 'blur' },
  ],
  confirmPassword: [{ required: true, validator: validateNewPasswordConfirm, trigger: 'blur' }],
}

function formatDate(dateStr?: string) {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 19)
}

function loadProfile() {
  if (userStore.userInfo) {
    profileForm.nickname = userStore.userInfo.nickname || ''
    profileForm.avatar = userStore.userInfo.avatar || ''
    profileForm.phone = userStore.userInfo.phone || ''
    profileForm.email = userStore.userInfo.email || ''
    profileForm.contactInfo = userStore.userInfo.contactInfo || ''
  }
}

async function handleUpdateProfile() {
  if (!profileFormRef.value) return
  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        updatingProfile.value = true
        const res = await updateProfileApi({
          nickname: profileForm.nickname.trim(),
          avatar: profileForm.avatar.trim() || undefined,
          phone: profileForm.phone.trim(),
          email: profileForm.email.trim() || undefined,
          contactInfo: profileForm.contactInfo.trim() || undefined,
        })
        userStore.updateUserInfo(res.data)
        ElMessage.success('资料修改成功')
      } finally {
        updatingProfile.value = false
      }
    }
  })
}

async function handleChangePassword() {
  if (!pwdFormRef.value) return
  await pwdFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        changingPwd.value = true
        await changePasswordApi({
          oldPassword: pwdForm.oldPassword,
          newPassword: pwdForm.newPassword,
        })
        ElMessage.success('密码修改成功，请使用新密码重新登录')
        await userStore.logout()
        router.push('/login')
      } finally {
        changingPwd.value = false
      }
    }
  })
}

onMounted(async () => {
  await userStore.fetchUserInfo()
  loadProfile()
})
</script>

<style scoped>
.profile-container {
  padding-top: 10px;
}

.user-card {
  text-align: center;
}

.avatar-section {
  padding: 10px 0;
}

.name {
  margin: 12px 0 6px 0;
  font-size: 18px;
  color: #1e293b;
}

.role-tag {
  margin: 0;
}

.info-list {
  text-align: left;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  font-size: 14px;
  border-bottom: 1px dashed #f1f5f9;
}

.info-row:last-child {
  border-bottom: none;
}

.info-row .label {
  color: #64748b;
}

.info-row .value {
  color: #1e293b;
  font-weight: 500;
}

.form-box {
  max-width: 500px;
  padding-top: 15px;
}
</style>
