<template>
  <div class="dashboard-page" v-loading="loading">
    <!-- 数据概览卡片 -->
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-val text-blue">{{ stats.totalLostCount }}</div>
          <div class="stat-label">累计寻物启事</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-val text-green">{{ stats.totalFoundCount }}</div>
          <div class="stat-label">累计招领信息</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-val text-orange">{{ stats.totalClaimSuccessCount }}</div>
          <div class="stat-label">成功认领结案</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-val text-red">{{ stats.pendingReviewCount }}</div>
          <div class="stat-label">待审核事项</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 分类分布 -->
    <el-card shadow="never" class="chart-card" header="分类物品数据统计">
      <el-table :data="stats.categoryDistribution" stripe style="width: 100%">
        <el-table-column prop="name" label="物品分类" />
        <el-table-column prop="value" label="登记总数" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { http } from '@/utils/request'
import type { DashboardStats } from '@/types'

const loading = ref(false)
const stats = ref<DashboardStats>({
  totalLostCount: 0,
  totalFoundCount: 0,
  totalClaimSuccessCount: 0,
  todayNewCount: 0,
  pendingReviewCount: 0,
  categoryDistribution: [],
})

async function fetchStats() {
  try {
    loading.value = true
    const res = await http.get<DashboardStats>('/admin/statistics/dashboard')
    if (res.data) {
      stats.value = res.data
    }
  } catch (e) {
    console.error('获取看板数据失败:', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
.stat-card {
  text-align: center;
  border-radius: 8px;
  margin-bottom: 20px;
}
.stat-val {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 4px;
}
.stat-label {
  font-size: 13px;
  color: #64748b;
}
.text-blue { color: #3b82f6; }
.text-green { color: #10b981; }
.text-orange { color: #f59e0b; }
.text-red { color: #ef4444; }
.chart-card {
  border-radius: 8px;
}
</style>
