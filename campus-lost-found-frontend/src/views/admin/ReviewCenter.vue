<template>
  <div class="review-page">
    <el-card shadow="never">
      <el-tabs v-model="activeTab" @tab-change="fetchData">
        <!-- 待审核物品发布 -->
        <el-tab-pane label="物品发布初审" name="items">
          <el-table v-loading="loading" :data="pendingItems" stripe style="width: 100%">
            <el-table-column prop="itemType" label="类型" width="100">
              <template #default="{ row }">
                <el-tag :type="row.itemType === 'LOST' ? 'warning' : 'success'">
                  {{ row.itemType === 'LOST' ? '失物' : '招领' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="物品名称" min-width="150" />
            <el-table-column prop="description" label="详细描述" min-width="200" show-overflow-tooltip />
            <el-table-column prop="location" label="地点" width="140" />
            <el-table-column prop="createdAt" label="提交时间" width="160" />
            <el-table-column label="审核操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button link type="success" @click="auditItem(row, true)">通过</el-button>
                <el-button link type="danger" @click="auditItem(row, false)">驳回</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 认领终审 -->
        <el-tab-pane label="认领终审流转" name="claims">
          <el-table v-loading="loading" :data="pendingClaims" stripe style="width: 100%">
            <el-table-column prop="itemTitle" label="物品名称" min-width="140" />
            <el-table-column prop="claimantNickname" label="申请人" width="120" />
            <el-table-column prop="description" label="认领凭据与说明" min-width="200" show-overflow-tooltip />
            <el-table-column prop="ownerComment" label="发布者核验意见" width="150" />
            <el-table-column label="终审操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button link type="success" @click="auditClaim(row.id, true)">终审通过</el-button>
                <el-button link type="danger" @click="auditClaim(row.id, false)">驳回</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { http } from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('items')
const loading = ref(false)
const pendingItems = ref<any[]>([])
const pendingClaims = ref<any[]>([])

async function fetchData() {
  try {
    loading.value = true
    if (activeTab.value === 'items') {
      const res = await http.get('/admin/reviews/items')
      pendingItems.value = res.data?.records || []
    } else {
      const res = await http.get('/admin/reviews/claims')
      pendingClaims.value = res.data?.records || []
    }
  } catch (e) {
    console.error('加载审核列表失败:', e)
  } finally {
    loading.value = false
  }
}

async function auditItem(row: any, approved: boolean) {
  const actionText = approved ? '通过' : '驳回'
  ElMessageBox.prompt(`请输入审核${actionText}意见：`, `物品审核`, {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async ({ value }) => {
    await http.put(`/admin/reviews/items/${row.itemType}/${row.id}`, {
      approved,
      comment: value || (approved ? '审核通过' : '信息不合规驳回'),
    })
    ElMessage.success(`审核已${actionText}`)
    fetchData()
  })
}

async function auditClaim(claimId: number, approved: boolean) {
  const actionText = approved ? '终审通过并结案' : '驳回'
  ElMessageBox.prompt(`请输入${actionText}意见：`, `认领终审`, {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async ({ value }) => {
    await http.put(`/admin/reviews/claims/${claimId}`, {
      approved,
      comment: value || (approved ? '终审通过' : '认领证据不充分驳回'),
    })
    ElMessage.success(`已${actionText}`)
    fetchData()
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.review-page {
  padding-bottom: 20px;
}
</style>
