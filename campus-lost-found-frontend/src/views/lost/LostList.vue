<template>
  <div class="page-box">
    <div class="filter-card">
      <el-form :inline="true" :model="query" class="filter-form">
        <el-form-item label="分类">
          <el-select v-model="query.categoryId" placeholder="全部" clearable style="width: 140px">
            <el-option label="全部" :value="undefined" />
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="搜索物品名称/地点" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 列表展示 -->
    <el-row :gutter="16" class="item-grid" v-loading="loading">
      <el-col v-if="items.length === 0" :span="24">
        <el-empty description="暂无符合条件的失物记录" />
      </el-col>
      <el-col v-for="item in items" :key="item.id" :xs="24" :sm="12" :md="8" :lg="6">
        <el-card shadow="hover" class="card-item" @click="goDetail(item.id)">
          <div class="card-title">{{ item.title }}</div>
          <div class="card-desc">{{ item.description }}</div>
          <div class="card-meta">
            <span><el-icon><Location /></el-icon> {{ item.lostLocation }}</span>
            <span><el-icon><Calendar /></el-icon> {{ item.lostTime }}</span>
          </div>
          <div class="card-status">
            <el-tag type="warning" size="small">寻物中</el-tag>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 分页 -->
    <div class="pagination-bar">
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        :page-sizes="[12, 24, 48]"
        layout="total, prev, pager, next"
        @current-change="fetchData"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { http } from '@/utils/request'
import type { PageResult, LostItemDetail } from '@/types'

const router = useRouter()
const loading = ref(false)
const total = ref(0)
const items = ref<LostItemDetail[]>([])

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

const query = reactive({
  categoryId: undefined as number | undefined,
  keyword: '',
  page: 1,
  size: 12,
})

async function fetchData() {
  try {
    loading.value = true
    const res = await http.get<PageResult<LostItemDetail>>('/lost-items', query)
    items.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('加载失物失败:', e)
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.categoryId = undefined
  query.keyword = ''
  query.page = 1
  fetchData()
}

function goDetail(id: number) {
  router.push(`/item/LOST/${id}`)
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.page-box {
  padding-bottom: 30px;
}
.filter-card {
  background: #ffffff;
  padding: 16px 20px 0 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}
.card-item {
  cursor: pointer;
  margin-bottom: 16px;
  border-radius: 8px;
  transition: transform 0.2s;
}
.card-item:hover {
  transform: translateY(-3px);
}
.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 8px;
}
.card-desc {
  font-size: 13px;
  color: #64748b;
  line-height: 1.5;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin-bottom: 12px;
}
.card-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #94a3b8;
  margin-bottom: 8px;
}
.card-status {
  display: flex;
  justify-content: flex-end;
}
.pagination-bar {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
