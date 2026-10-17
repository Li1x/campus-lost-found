<template>
  <div class="messages-page">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="title">站内通知中心</span>
          <el-button type="primary" link @click="markAllRead">全部标为已读</el-button>
        </div>
      </template>

      <el-timeline v-loading="loading" class="msg-timeline">
        <el-empty v-if="messages.length === 0" description="暂无通知消息" />
        <el-timeline-item
          v-for="msg in messages"
          :key="msg.id"
          :type="msg.isRead === 0 ? 'primary' : 'info'"
          :hollow="msg.isRead === 1"
          :timestamp="msg.createdAt"
          placement="top"
        >
          <el-card shadow="hover" class="msg-card" :class="{ unread: msg.isRead === 0 }">
            <div class="msg-title-bar">
              <span class="msg-title">{{ msg.title }}</span>
              <el-tag v-if="msg.isRead === 0" size="small" type="danger">未读</el-tag>
            </div>
            <p class="msg-content">{{ msg.content }}</p>
            <div v-if="msg.isRead === 0" class="msg-actions">
              <el-button link type="primary" size="small" @click="markRead(msg.id)">
                标记已读
              </el-button>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { http } from '@/utils/request'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const messages = ref<any[]>([])

async function fetchMessages() {
  try {
    loading.value = true
    const res = await http.get('/messages')
    messages.value = res.data?.records || []
  } catch (e) {
    console.error('获取消息失败:', e)
  } finally {
    loading.value = false
  }
}

async function markRead(id: number) {
  await http.put(`/messages/${id}/read`)
  const target = messages.value.find((m) => m.id === id)
  if (target) target.isRead = 1
}

async function markAllRead() {
  await http.put('/messages/read-all')
  ElMessage.success('已全部标记为已读')
  messages.value.forEach((m) => (m.isRead = 1))
}

onMounted(() => {
  fetchMessages()
})
</script>

<style scoped>
.messages-page {
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
.msg-timeline {
  padding-top: 10px;
}
.msg-card {
  border-radius: 8px;
}
.msg-card.unread {
  border-left: 3px solid #409eff;
}
.msg-title-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}
.msg-title {
  font-weight: 600;
  font-size: 15px;
  color: #1e293b;
}
.msg-content {
  font-size: 14px;
  color: #475569;
  line-height: 1.6;
  margin: 0;
}
.msg-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
}
</style>
