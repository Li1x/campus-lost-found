<template>
  <div class="publish-page">
    <el-card shadow="never" class="publish-card">
      <template #header>
        <div class="card-header">
          <span class="title">发布失物 / 招领信息</span>
          <el-radio-group v-model="form.itemType" size="default">
            <el-radio-button label="LOST">寻物启事</el-radio-button>
            <el-radio-button label="FOUND">招领启事</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="110px"
        class="form-container"
      >
        <el-form-item label="物品名称" prop="title">
          <el-input v-model="form.title" placeholder="例如：黑色晨光签字笔 / 蓝色折叠雨伞" />
        </el-form-item>

        <el-form-item label="物品分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择物品分类" style="width: 100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>

        <el-form-item :label="form.itemType === 'LOST' ? '丢失地点' : '拾得地点'" prop="location">
          <el-input
            v-model="form.location"
            :placeholder="form.itemType === 'LOST' ? '例如：一教302教室' : '例如：图书馆二楼自习区'"
          />
        </el-form-item>

        <el-form-item :label="form.itemType === 'LOST' ? '丢失日期' : '拾得日期'" prop="itemDate">
          <el-date-picker
            v-model="form.itemDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item v-if="form.itemType === 'FOUND'" label="现存放位置" prop="storageLocation">
          <el-input v-model="form.storageLocation" placeholder="例如：学院楼物业值班处 / 自行代管" />
        </el-form-item>

        <el-form-item label="联系方式" prop="contactInfo">
          <el-input v-model="form.contactInfo" placeholder="手机号/微信号（审核通过后仅相关方可申请查看）" />
        </el-form-item>

        <el-form-item label="详细特征描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请详细描述物品的外观特征、颜色、划痕等细节，便于识别核对..."
          />
        </el-form-item>

        <el-form-item label="物品照片">
          <el-upload
            action="/api/files/upload"
            list-type="picture-card"
            :headers="uploadHeaders"
            :on-success="handleUploadSuccess"
            :on-remove="handleUploadRemove"
            :limit="5"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" :loading="submitting" @click="handleSubmit">
            提交发布并送审
          </el-button>
          <el-button size="large" @click="router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { http } from '@/utils/request'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

const router = useRouter()
const route = useRoute()
const formRef = ref<FormInstance>()
const submitting = ref(false)

const categories = ref([
  { id: 1, name: '证件证照' },
  { id: 2, name: '数码电子' },
  { id: 3, name: '书籍文具' },
  { id: 4, name: '服装鞋包' },
  { id: 5, name: '钥匙卡包' },
  { id: 6, name: '体育器材' },
  { id: 7, name: '生活用品' },
  { id: 8, name: '其它物品' },
])

const form = reactive({
  itemType: 'FOUND' as 'LOST' | 'FOUND',
  title: '',
  categoryId: undefined as number | undefined,
  location: '',
  itemDate: '',
  storageLocation: '',
  contactInfo: '',
  description: '',
  images: [] as string[],
})

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('access_token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})

const rules: FormRules = {
  title: [{ required: true, message: '请输入物品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择物品分类', trigger: 'change' }],
  location: [{ required: true, message: '请输入地点', trigger: 'blur' }],
  itemDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
  contactInfo: [{ required: true, message: '请输入联系方式', trigger: 'blur' }],
  description: [{ required: true, message: '请输入详细描述', trigger: 'blur' }],
}

function handleUploadSuccess(response: any) {
  if (response.code === 200 && response.data?.url) {
    form.images.push(response.data.url)
  }
}

function handleUploadRemove(file: any) {
  const url = file.response?.data?.url
  if (url) {
    form.images = form.images.filter((img) => img !== url)
  }
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        submitting.value = true
        if (form.itemType === 'LOST') {
          await http.post('/lost-items', {
            title: form.title,
            categoryId: form.categoryId,
            lostLocation: form.location,
            lostTime: form.itemDate,
            contactInfo: form.contactInfo,
            description: form.description,
            images: form.images,
          })
        } else {
          await http.post('/found-items', {
            title: form.title,
            categoryId: form.categoryId,
            foundLocation: form.location,
            foundTime: form.itemDate,
            storageLocation: form.storageLocation,
            contactInfo: form.contactInfo,
            description: form.description,
            images: form.images,
          })
        }
        ElMessage.success('发布成功，管理员审核后将公开展示')
        router.push(form.itemType === 'LOST' ? '/lost' : '/found')
      } finally {
        submitting.value = false
      }
    }
  })
}

onMounted(() => {
  if (route.query.type === 'LOST') {
    form.itemType = 'LOST'
  }
})
</script>

<style scoped>
.publish-page {
  max-width: 800px;
  margin: 0 auto;
}
.publish-card {
  border-radius: 12px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}
.form-container {
  padding-top: 10px;
}
</style>
