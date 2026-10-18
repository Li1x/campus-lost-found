<template>
  <div class="detail-container" v-loading="loading">
    <el-card v-if="item" shadow="never" class="detail-card">
      <el-row :gutter="32">
        <!-- 左侧图片展示 -->
        <el-col :xs="24" :md="10">
          <div class="image-box">
            <el-carousel v-if="item.images && item.images.length > 0" height="320px" indicator-position="outside">
              <el-carousel-item v-for="(img, idx) in item.images" :key="idx">
                <el-image :src="img" fit="cover" class="carousel-img" :preview-src-list="item.images" />
              </el-carousel-item>
            </el-carousel>
            <div v-else class="no-image">
              <el-icon :size="48" color="#94a3b8"><Picture /></el-icon>
              <span>暂无物品照片</span>
            </div>
          </div>
        </el-col>

        <!-- 右侧详细信息 -->
        <el-col :xs="24" :md="14">
          <div class="info-header">
            <el-tag :type="itemType === 'LOST' ? 'warning' : 'success'" size="large">
              {{ itemType === 'LOST' ? '寻物启事' : '招领信息' }}
            </el-tag>
            <h2 class="item-title">{{ item.title }}</h2>
          </div>

          <div class="meta-tags">
            <el-tag effect="plain" type="info">{{ item.categoryName || '普通物品' }}</el-tag>
            <el-tag effect="dark" :type="getStatusTagType(item.status)">
              {{ getStatusLabel(item.status) }}
            </el-tag>
          </div>

          <el-divider />

          <div class="attr-list">
            <div class="attr-item">
              <span class="attr-label">{{ itemType === 'LOST' ? '丢失地点' : '拾得地点' }}：</span>
              <span class="attr-val">{{ item.lostLocation || item.foundLocation }}</span>
            </div>
            <div class="attr-item">
              <span class="attr-label">{{ itemType === 'LOST' ? '丢失时间' : '拾得时间' }}：</span>
              <span class="attr-val">{{ item.lostTime || item.foundTime }}</span>
            </div>
            <div v-if="item.storageLocation" class="attr-item">
              <span class="attr-label">现存放位置：</span>
              <span class="attr-val">{{ item.storageLocation }}</span>
            </div>
            <div class="attr-item">
              <span class="attr-label">联系方式：</span>
              <span class="attr-val text-primary">{{ item.contactInfo || '（认领通过后可见）' }}</span>
            </div>
          </div>

          <div class="desc-box">
            <div class="desc-label">详细描述：</div>
            <div class="desc-content">{{ item.description }}</div>
          </div>

          <!-- 操作区域 -->
          <div class="actions-area">
            <template v-if="canClaim">
              <el-button type="primary" size="large" @click="showClaimDialog = true">
                <el-icon><Check /></el-icon> 申请认领
              </el-button>
            </template>
            <template v-if="isOwner">
              <el-button type="info" size="large" @click="handleCloseItem">
                关闭本条信息
              </el-button>
            </template>
            <el-button size="large" @click="router.back()">返回列表</el-button>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 申请认领模态框 -->
    <el-dialog v-model="showClaimDialog" title="发起认领申请" width="480px" destroy-on-close>
      <el-form ref="claimFormRef" :model="claimForm" :rules="claimRules" label-position="top">
        <el-form-item label="认领凭证与说明" prop="description">
          <el-input
            v-model="claimForm.description"
            type="textarea"
            :rows="4"
            placeholder="请详细说明您的物品特征（如锁屏壁纸、隐藏暗号、购买凭据等），以便发布者核验"
          />
        </el-form-item>
        <el-form-item label="您的联系方式" prop="contactInfo">
          <el-input v-model="claimForm.contactInfo" placeholder="手机号/微信号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showClaimDialog = false">取消</el-button>
        <el-button type="primary" :loading="claiming" @click="submitClaim">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { http } from '@/utils/request'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const itemType = ref<'LOST' | 'FOUND'>((route.params.type as any) || 'FOUND')
const itemId = ref<number>(Number(route.params.id))
const loading = ref(false)
const item = ref<any>(null)

const showClaimDialog = ref(false)
const claiming = ref(false)
const claimFormRef = ref<FormInstance>()
const claimForm = reactive({
  description: '',
  contactInfo: '',
})

const claimRules: FormRules = {
  description: [{ required: true, message: '请填写认领特征说明', trigger: 'blur' }],
  contactInfo: [{ required: true, message: '请填写联系方式', trigger: 'blur' }],
}

const isOwner = computed(() => {
  return userStore.userInfo && item.value && userStore.userInfo.id === item.value.userId
})

const canClaim = computed(() => {
  if (!userStore.isLoggedIn) return true
  if (isOwner.value) return false
  return item.value && (item.value.status === 'APPROVED' || item.value.status === 'PENDING')
})

function getStatusLabel(status: string) {
  const map: Record<string, string> = {
    PENDING: '待审核',
    APPROVED: '招领中',
    REJECTED: '未通过',
    CLAIMED: '已认领',
    COMPLETED: '已完结',
    CLOSED: '已关闭',
    EXPIRED: '已过期',
  }
  return map[status] || status
}

function getStatusTagType(status: string) {
  const map: Record<string, string> = {
    PENDING: 'info',
    APPROVED: 'success',
    REJECTED: 'danger',
    CLAIMED: 'warning',
    COMPLETED: '',
    CLOSED: 'info',
    EXPIRED: 'info',
  }
  return (map[status] as any) || ''
}

async function fetchDetail() {
  try {
    loading.value = true
    const url = itemType.value === 'LOST' ? `/lost-items/${itemId.value}` : `/found-items/${itemId.value}`
    const res = await http.get(url)
    item.value = res.data
  } catch (e) {
    console.error('获取详情失败:', e)
  } finally {
    loading.value = false
  }
}

async function submitClaim() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再申请认领')
    router.push(`/login?redirect=${encodeURIComponent(route.fullPath)}`)
    return
  }

  if (!claimFormRef.value) return
  await claimFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        claiming.value = true
        await http.post('/claims', {
          itemType: itemType.value,
          itemId: itemId.value,
          description: claimForm.description,
          contactInfo: claimForm.contactInfo,
        })
        ElMessage.success('认领申请已提交，等待发布者核对')
        showClaimDialog.value = false
        fetchDetail()
      } finally {
        claiming.value = false
      }
    }
  })
}

function handleCloseItem() {
  ElMessageBox.confirm('确定要关闭此信息吗？关闭后将不可再认领。', '提示', {
    type: 'warning',
  }).then(async () => {
    const url = itemType.value === 'LOST' ? `/lost-items/${itemId.value}/close` : `/found-items/${itemId.value}/close`
    await http.put(url)
    ElMessage.success('已关闭')
    fetchDetail()
  })
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.detail-container {
  padding-bottom: 30px;
}
.detail-card {
  border-radius: 12px;
  padding: 10px;
}
.image-box {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  background-color: #f8fafc;
}
.carousel-img {
  width: 100%;
  height: 320px;
  border-radius: 8px;
}
.no-image {
  height: 320px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #94a3b8;
  font-size: 14px;
}
.info-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}
.item-title {
  margin: 0;
  font-size: 22px;
  color: #1e293b;
}
.meta-tags {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}
.attr-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  font-size: 14px;
}
.attr-label {
  color: #64748b;
  width: 100px;
  display: inline-block;
}
.attr-val {
  color: #1e293b;
  font-weight: 500;
}
.text-primary {
  color: #409eff;
}
.desc-box {
  background-color: #f8fafc;
  border-radius: 8px;
  padding: 16px;
  margin: 20px 0;
}
.desc-label {
  font-size: 14px;
  font-weight: 600;
  color: #475569;
  margin-bottom: 8px;
}
.desc-content {
  font-size: 14px;
  color: #334155;
  line-height: 1.6;
}
.actions-area {
  display: flex;
  gap: 16px;
  margin-top: 24px;
}
</style>
