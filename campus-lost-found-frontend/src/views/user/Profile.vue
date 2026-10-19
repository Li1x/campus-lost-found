<template>
  <div class="profile-container">
    <el-row :gutter="24">
      <!-- 左侧：个人信息卡片 -->
      <el-col :xs="24" :md="8">
        <el-card class="user-card" shadow="never">
          <div class="avatar-section">
            <el-upload
              action="/api/files/upload"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
              class="avatar-uploader"
            >
              <div class="avatar-trigger-left" title="点击更换头像">
                <el-avatar :size="96" :src="getImageUrl(userStore.userInfo?.avatar) || defaultAvatar" class="avatar-img">
                  {{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}
                </el-avatar>
                <div class="avatar-hover-mask">
                  <el-icon :size="22"><Camera /></el-icon>
                  <span class="mask-text">更换头像</span>
                </div>
              </div>
            </el-upload>
            <div class="avatar-tip-text">点击头像即可更换</div>
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
                    :rows="2"
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
import { Camera } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const activeTab = ref('basic')
const profileFormRef = ref<FormInstance>()
const pwdFormRef = ref<FormInstance>()

const updatingProfile = ref(false)
const changingPwd = ref(false)

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('access_token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})

function getImageUrl(url?: string): string {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) return url
  return url.startsWith('/') ? url : `/${url}`
}

function beforeAvatarUpload(file: File) {
  const isImage = ['image/jpeg', 'image/png', 'image/webp'].includes(file.type)
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) {
    ElMessage.error('上传头像图片只能是 JPG、PNG 或 WEBP 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
    return false
  }
  return true
}

async function handleAvatarSuccess(response: any) {
  if (response.code === 200 && response.data?.url) {
    const newAvatar = response.data.url
    profileForm.avatar = newAvatar
    try {
      updatingProfile.value = true
      // 仅提交 avatar 进行更新，与其他表单项彻底解耦，避免因未填写手机号导致保存失败
      const res = await updateProfileApi({
        avatar: newAvatar,
      })
      userStore.updateUserInfo(res.data)
      ElMessage.success('头像更换成功并已自动保存')
    } catch (err: any) {
      ElMessage.error(err.message || '头像保存失败，请稍后重试')
    } finally {
      updatingProfile.value = false
    }
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

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
        const phoneVal = profileForm.phone?.trim()
        const res = await updateProfileApi({
          nickname: profileForm.nickname.trim(),
          avatar: profileForm.avatar.trim() || undefined,
          phone: phoneVal ? phoneVal : undefined,
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

.avatar-uploader {
  display: inline-block;
}

.avatar-trigger-left {
  position: relative;
  width: 96px;
  height: 96px;
  margin: 0 auto;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border: 3px solid #ffffff;
  transition: all 0.3s ease;
}

.avatar-trigger-left:hover {
  transform: scale(1.03);
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.25);
  border-color: #93c5fd;
}

.avatar-hover-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(15, 23, 42, 0.6);
  backdrop-filter: blur(2px);
  color: #ffffff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.25s ease;
  border-radius: 50%;
}

.avatar-trigger-left:hover .avatar-hover-mask {
  opacity: 1;
}

.mask-text {
  font-size: 11px;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.avatar-tip-text {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 6px;
}
</style>
