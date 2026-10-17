<template>
  <div class="my-claims-page">
    <el-card shadow="never">
      <el-tabs v-model="activeTab" @tab-change="fetchData">
        <!-- 我发起的认领 -->
        <el-tab-pane label="我发起的认领" name="my-requests">
          <el-table v-loading="loading" :data="myRequests" stripe style="width: 100%">
            <el-table-column prop="itemTitle" label="物品名称" min-width="150" />
            <el-table-column prop="description" label="我的认领说明" min-width="180" show-overflow-tooltip />
            <el-table-column prop="status" label="认领状态" width="130">
              <template #default="{ row }">
                <el-tag :type="getClaimStatusTag(row.status)">
                  {{ getClaimStatusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="申请时间" width="160" />
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button
                  v-if="row.status === 'PENDING'"
                  link
                  type="danger"
                  @click="cancelClaim(row.id)"
                >
                  撤销申请
                </el-button>
                <span v-else class="text-muted">-</span>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 我收到的认领申请 -->
        <el-tab-pane label="我收到的认领申请" name="received-requests">
          <el-table v-loading="loading" :data="receivedRequests" stripe style="width: 100%">
            <el-table-column prop="itemTitle" label="相关物品" min-width="140" />
            <el-table-column prop="claimantNickname" label="认领人" width="120" />
            <el-table-column prop="contactInfo" label="联系方式" width="140" />
            <el-table-column prop="description" label="认领特征说明" min-width="180" show-overflow-tooltip />
            <el-table-column prop="status" label="当前状态" width="130">
              <template #default="{ row }">
                <el-tag :type="getClaimStatusTag(row.status)">
                  {{ getClaimStatusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="发布者确认" width="180" fixed="right">
              <template #default="{ row }">
                <template v-if="row.status === 'PENDING'">
                  <el-button link type="success" @click="handleOwnerAudit(row.id, true)">
                    确认是他
                  </el-button>
                  <el-button link type="danger" @click="handleOwnerAudit(row.id, false)">
                    驳回
                  </el-button>
                </template>
                <span v-else class="text-muted">已处理</span>
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

const activeTab = ref('my-requests')
const loading = ref(false)
const myRequests = ref<any[]>([])
const receivedRequests = ref<any[]>([])

function getClaimStatusLabel(status: string) {
  const map: Record<string, string> = {
    PENDING: '待发布者核验',
    OWNER_APPROVED: '发布者已确认(待终审)',
    OWNER_REJECTED: '发布者已驳回',
    APPROVED: '认领成功',
    REJECTED: '终审驳回',
    CANCELLED: '已撤销',
  }
  return map[status] || status
}

function getClaimStatusTag(status: string) {
  const map: Record<string, string> = {
    PENDING: 'info',
    OWNER_APPROVED: 'warning',
    OWNER_REJECTED: 'danger',
    APPROVED: 'success',
    REJECTED: 'danger',
    CANCELLED: 'info',
  }
  return (map[status] as any) || ''
}

async function fetchData() {
  try {
    loading.value = true
    if (activeTab.value === 'my-requests') {
      const res = await http.get('/claims/my')
      myRequests.value = res.data?.records || []
    } else {
      const res = await http.get('/claims/received')
      receivedRequests.value = res.data?.records || []
    }
  } catch (e) {
    console.error('加载认领记录失败:', e)
  } finally {
    loading.value = false
  }
}

async function cancelClaim(id: number) {
  ElMessageBox.confirm('确定要撤销此认领申请吗？', '提示', { type: 'warning' }).then(async () => {
    await http.put(`/claims/${id}/cancel`)
    ElMessage.success('已撤销')
    fetchData()
  })
}

async function handleOwnerAudit(id: number, approved: boolean) {
  const actionText = approved ? '确认匹配' : '驳回'
  ElMessageBox.prompt(`请输入${actionText}的备注说明：`, `${actionText}认领`, {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async ({ value }) => {
    await http.put(`/claims/${id}/owner-audit`, {
      approved,
      comment: value || (approved ? '核对无误' : '特征不符'),
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
.my-claims-page {
  padding-bottom: 30px;
}
.text-muted {
  color: #94a3b8;
  font-size: 13px;
}
</style>
