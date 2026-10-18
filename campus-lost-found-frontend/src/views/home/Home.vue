<template>
  <div class="home-container">
    <!-- 顶部横幅欢迎区 -->
    <div class="hero-banner">
      <div class="hero-content">
        <h1>校园失物招领互助平台</h1>
        <p>让每一件遗失的物品，都能顺利回到主人的身边</p>
        <div class="hero-actions">
          <el-button type="primary" size="large" @click="router.push('/publish?type=FOUND')">
            <el-icon><Search /></el-icon> 我捡到了物品
          </el-button>
          <el-button type="warning" size="large" @click="router.push('/publish?type=LOST')">
            <el-icon><Warning /></el-icon> 我丢失了物品
          </el-button>
        </div>
      </div>
    </div>

    <!-- 快捷分类筛选 -->
    <div class="section-title">
      <h3>物品分类浏览</h3>
    </div>
    <div class="category-grid">
      <el-card
        v-for="cat in categories"
        :key="cat.id"
        class="cat-card"
        shadow="hover"
        @click="goToCategory(cat.id)"
      >
        <div class="cat-item">
          <el-icon :size="32" class="cat-icon"><Goods /></el-icon>
          <span class="cat-name">{{ cat.name }}</span>
        </div>
      </el-card>
    </div>

    <!-- 最新失物与招领两列展示 -->
    <el-row :gutter="20" class="recent-section">
      <el-col :xs="24" :md="12">
        <div class="list-header">
          <div class="header-left">
            <span class="dot warning-dot"></span>
            <h4>最新寻物启事</h4>
          </div>
          <el-button link type="primary" @click="router.push('/lost')">查看更多 &gt;</el-button>
        </div>
        <el-card shadow="never" class="items-card">
          <div v-if="lostList.length === 0" class="empty-tip">暂无最新失物信息</div>
          <div
            v-for="item in lostList"
            :key="item.id"
            class="item-row"
            @click="router.push(`/item/LOST/${item.id}`)"
          >
            <div class="item-info">
              <span class="item-title">{{ item.title }}</span>
              <span class="item-meta">{{ item.lostLocation }} · {{ item.lostTime }}</span>
            </div>
            <el-tag size="small" type="warning">寻物中</el-tag>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="12">
        <div class="list-header">
          <div class="header-left">
            <span class="dot success-dot"></span>
            <h4>最新招领信息</h4>
          </div>
          <el-button link type="primary" @click="router.push('/found')">查看更多 &gt;</el-button>
        </div>
        <el-card shadow="never" class="items-card">
          <div v-if="foundList.length === 0" class="empty-tip">暂无最新招领信息</div>
          <div
            v-for="item in foundList"
            :key="item.id"
            class="item-row"
            @click="router.push(`/item/FOUND/${item.id}`)"
          >
            <div class="item-info">
              <span class="item-title">{{ item.title }}</span>
              <span class="item-meta">{{ item.foundLocation }} · {{ item.foundTime }}</span>
            </div>
            <el-tag size="small" type="success">招领中</el-tag>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { http } from '@/utils/request'
import type { PageResult } from '@/types'

const router = useRouter()

const categories = ref<any[]>([
  { id: 1, name: '证件证照' },
  { id: 2, name: '数码电子' },
  { id: 3, name: '书籍文具' },
  { id: 4, name: '服装鞋包' },
  { id: 5, name: '钥匙卡包' },
  { id: 6, name: '体育器材' },
  { id: 7, name: '生活用品' },
  { id: 8, name: '其它物品' },
])

const lostList = ref<any[]>([])
const foundList = ref<any[]>([])
const loading = ref(false)

async function loadHomeData() {
  try {
    loading.value = true
    // 并行请求：最新寻物启事（5条）、最新招领启事（5条）、最新分类
    const [lostRes, foundRes, catRes] = await Promise.allSettled([
      http.get<PageResult<any>>('/lost-items', { page: 1, size: 5 }),
      http.get<PageResult<any>>('/found-items', { page: 1, size: 5 }),
      http.get<any[]>('/categories'),
    ])

    if (lostRes.status === 'fulfilled' && lostRes.value?.data?.records) {
      lostList.value = lostRes.value.data.records
    }
    if (foundRes.status === 'fulfilled' && foundRes.value?.data?.records) {
      foundList.value = foundRes.value.data.records
    }
    if (catRes.status === 'fulfilled' && catRes.value?.data && Array.isArray(catRes.value.data)) {
      categories.value = catRes.value.data
    }
  } catch (error) {
    console.error('加载首页最新信息失败:', error)
  } finally {
    loading.value = false
  }
}

function goToCategory(categoryId: number) {
  router.push({ path: '/found', query: { categoryId } })
}

onMounted(() => {
  loadHomeData()
})
</script>

<style scoped>
.home-container {
  padding-bottom: 30px;
}

.hero-banner {
  background: linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%);
  border-radius: 16px;
  padding: 48px 32px;
  color: #ffffff;
  text-align: center;
  margin-bottom: 36px;
  box-shadow: 0 10px 20px -5px rgba(59, 130, 246, 0.3);
}

.hero-content h1 {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 12px 0;
}

.hero-content p {
  font-size: 16px;
  opacity: 0.9;
  margin: 0 0 28px 0;
}

.hero-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.section-title {
  margin-bottom: 16px;
}

.section-title h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(130px, 1fr));
  gap: 12px;
  margin-bottom: 36px;
}

.cat-card {
  cursor: pointer;
  border-radius: 10px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.cat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
}

.cat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 10px 0;
}

.cat-icon {
  color: #3b82f6;
}

.cat-name {
  font-size: 14px;
  font-weight: 500;
  color: #334155;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.list-header h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.warning-dot {
  background-color: #e6a23c;
}

.success-dot {
  background-color: #67c23a;
}

.items-card {
  min-height: 240px;
  border-radius: 12px;
}

.empty-tip {
  text-align: center;
  color: #94a3b8;
  padding: 50px 0;
  font-size: 14px;
}

.item-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 8px;
  border-bottom: 1px solid #f1f5f9;
  cursor: pointer;
  transition: background 0.15s;
}

.item-row:hover {
  background-color: #f8fafc;
}

.item-row:last-child {
  border-bottom: none;
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.item-title {
  font-size: 14px;
  font-weight: 500;
  color: #1e293b;
}

.item-meta {
  font-size: 12px;
  color: #94a3b8;
}
</style>
