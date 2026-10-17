<template>
  <div class="my-items-page">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="title">我发布的物品</span>
          <el-radio-group v-model="activeType" size="default" @change="fetchData">
            <el-radio-button label="FOUND">招领启事</el-radio-button>
            <el-radio-button label="LOST">寻物启事</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-table v-loading="loading" :data="tableData" stripe style="width: 100%">
        <el-table-column prop="title" label="物品名称" min-width="150" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column
          :label="activeType === 'FOUND' ? '拾得地点' : '丢失地点'"
          min-width="140"
        >
          <template #default="{ row }">
            {{ row.foundLocation || row.lostLocation }}
          </template>
        </el-table-column>
        <el-table-column
          :label="activeType === 'FOUND' ? '拾得日期' : '丢失日期'"
          width="130"
        >
          <template #default="{ row }">
            {{ row.foundTime || row.lostTime }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="当前状态" width="110">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="goDetail(row.id)">查看</el-button>
            <el-button
              v-if="row.status === 'APPROVED' || row.status === 'PENDING'"
              link
              type="danger"
              @click="closeItem(row.id)"
            >
              关闭
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { http } from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const activeType = ref<'FOUND' | 'LOST'>('FOUND')
const loading = ref(false)
const tableData = ref<any[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

function getStatusLabel(status: string) {
  const map: Record<string, string> = {
    PENDING: '待审核',
    APPROVED: '正常展示',
    REJECTED: '已驳回',
    CLAIMED: '已认领',
    COMPLETED: '已完结',
    CLOSED: '已关闭',
    EXPIRED: '已过期',
  }
  return map[status] || status
}

function getStatusTag(status: string) {
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

async function fetchData() {
  try {
    loading.value = true
    const url = activeType.value === 'FOUND' ? '/found-items/my' : '/lost-items/my'
    const res = await http.get(url, { page: page.value, size: size.value })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('加载我的发布失败:', e)
  } finally {
    loading.value = false
  }
}

function goDetail(id: number) {
  router.push(`/item/${activeType.value}/${id}`)
}

function closeItem(id: number) {
  ElMessageBox.confirm('确定要关闭此信息吗？', '提示', { type: 'warning' }).then(async () => {
    const url = activeType.value === 'FOUND' ? `/found-items/${id}/close` : `/lost-items/${id}/close`
    await http.put(url)
    ElMessage.success('已关闭')
    fetchData()
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.my-items-page {
  padding-bottom: 30px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.title {
  font-size: 17px;
  font-weight: 600;
  color: #1e293b;
}
.pagination-bar {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
