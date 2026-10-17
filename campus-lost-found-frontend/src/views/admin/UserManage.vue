<template>
  <div class="users-page">
    <el-card shadow="never">
      <el-table v-loading="loading" :data="users" stripe style="width: 100%">
        <el-table-column prop="studentId" label="学号" width="130" />
        <el-table-column prop="username" label="用户名" width="130" />
        <el-table-column prop="nickname" label="昵称" width="130" />
        <el-table-column prop="phone" label="手机号码" width="140" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="row.role === 'SUPER_ADMIN' ? 'danger' : row.role === 'ADMIN' ? 'warning' : 'success'">
              {{ row.role }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="管理操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.role !== 'SUPER_ADMIN'"
              link
              :type="row.status === 1 ? 'danger' : 'success'"
              @click="toggleUserStatus(row)"
            >
              {{ row.status === 1 ? '禁用账号' : '解禁账号' }}
            </el-button>
            <el-button
              v-if="row.role !== 'SUPER_ADMIN'"
              link
              type="warning"
              @click="resetUserPassword(row)"
            >
              重置密码
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { http } from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const users = ref<any[]>([])

async function fetchUsers() {
  try {
    loading.value = true
    const res = await http.get('/admin/users')
    users.value = res.data?.records || []
  } catch (e) {
    console.error('获取用户列表失败:', e)
  } finally {
    loading.value = false
  }
}

async function toggleUserStatus(row: any) {
  const newStatus = row.status === 1 ? 0 : 1
  const actionText = newStatus === 0 ? '禁用' : '解禁'
  ElMessageBox.confirm(`确定要${actionText}用户 [${row.username}] 吗？`, '警告', {
    type: 'warning',
  }).then(async () => {
    await http.put(`/admin/users/${row.id}/status`, { status: newStatus })
    ElMessage.success(`用户已${actionText}`)
    fetchUsers()
  })
}

async function resetUserPassword(row: any) {
  ElMessageBox.confirm(`确定要将用户 [${row.username}] 的密码重置为默认密码 123456 吗？`, '提示', {
    type: 'warning',
  }).then(async () => {
    await http.put(`/admin/users/${row.id}/reset-password`)
    ElMessage.success('密码已重置为 123456')
  })
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.users-page {
  padding-bottom: 20px;
}
</style>
