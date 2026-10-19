<template>
  <div class="page-box">
    <!-- 顶部筛选区域 -->
    <div class="filter-card">
      <el-form :inline="true" :model="query" class="filter-form">
        <el-form-item label="分类">
          <el-select v-model="query.categoryId" placeholder="全部" clearable style="width: 150px">
            <el-option label="全部" value="" />
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="搜索物品名称/地点/特征" clearable style="width: 220px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 寻物启事卡片网格列表 -->
    <div class="item-grid-container" v-loading="loading">
      <div v-if="items.length === 0 && !loading" class="empty-wrap">
        <el-empty description="暂无符合条件的失物记录" />
      </div>

      <el-row :gutter="20" class="item-grid">
        <el-col v-for="item in items" :key="item.id" :xs="24" :sm="12" :md="8" :lg="6">
          <div class="lost-card" @click="goDetail(item.id)">
            <!-- 4:3 实物照片容器 -->
            <div class="image-wrapper">
              <template v-if="item.images && item.images.length > 0">
                <el-image
                  :src="getImageUrl(item.images[0])"
                  fit="cover"
                  class="card-img"
                  :preview-src-list="getPreviewList(item.images)"
                  :initial-index="0"
                  preview-teleported
                  hide-on-click-modal
                  @click.stop
                >
                  <template #placeholder>
                    <div class="img-loading">
                      <el-icon class="is-loading" :size="24"><Loading /></el-icon>
                    </div>
                  </template>
                  <template #error>
                    <div class="img-fallback">
                      <el-icon :size="28" color="#94a3b8"><Picture /></el-icon>
                      <span>图片加载失败</span>
                    </div>
                  </template>
                </el-image>
                <!-- 多图角标 -->
                <div v-if="item.images.length > 1" class="image-badge">
                  <el-icon><PictureFilled /></el-icon> {{ item.images.length }} 图
                </div>
              </template>
              <!-- 无图统一占位 -->
              <div v-else class="no-img-box">
                <el-icon :size="36" color="#cbd5e1"><Picture /></el-icon>
                <span class="no-img-text">暂无实物照片</span>
              </div>

              <!-- 悬浮分类与状态标签 -->
              <div class="floating-tags">
                <span class="category-pill">{{ item.categoryName || '其它物品' }}</span>
                <el-tag size="small" :type="getStatusTagType(item.status)" effect="dark">
                  {{ getLostStatusLabel(item.status) }}
                </el-tag>
              </div>
            </div>

            <!-- 卡片信息区域 -->
            <div class="card-body">
              <h4 class="card-title" :title="item.title">{{ item.title }}</h4>

              <div class="desc-box">
                <p class="desc-text" :title="item.description">{{ item.description || '暂无详细描述信息' }}</p>
              </div>

              <div class="meta-list">
                <div class="meta-item">
                  <el-icon class="meta-icon"><Location /></el-icon>
                  <span class="meta-label">丢失地点：</span>
                  <span class="meta-val" :title="item.lostLocation">{{ item.lostLocation || '未知地点' }}</span>
                </div>
                <div class="meta-item">
                  <el-icon class="meta-icon"><Calendar /></el-icon>
                  <span class="meta-label">丢失时间：</span>
                  <span class="meta-val">{{ formatDateTime(item.lostTime) }}</span>
                </div>
              </div>

              <div class="card-footer">
                <div class="user-info">
                  <el-avatar :size="22" :src="getImageUrl(item.userAvatar)">
                    {{ item.userNickname ? item.userNickname.charAt(0) : 'U' }}
                  </el-avatar>
                  <span class="user-name">{{ item.userNickname || '校园同学' }}</span>
                </div>
                <span class="detail-link">
                  查看详情 <el-icon><ArrowRight /></el-icon>
                </span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 分页控件 -->
    <div class="pagination-bar">
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        :page-sizes="[12, 24, 48]"
        layout="total, prev, pager, next, jumper"
        @current-change="fetchData"
        @size-change="handleSizeChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { http } from '@/utils/request'
import type { PageResult, LostItemDetail } from '@/types'
import {
  Location,
  Calendar,
  Picture,
  PictureFilled,
  Loading,
  ArrowRight,
} from '@element-plus/icons-vue'

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

/**
 * 格式化图片地址
 */
function getImageUrl(url?: string): string {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  return url.startsWith('/') ? url : `/${url}`
}

/**
 * 获取完整的大图预览列表
 */
function getPreviewList(images?: string[]): string[] {
  if (!images || images.length === 0) return []
  return images.map((img) => getImageUrl(img))
}

/**
 * 寻物状态文案映射
 */
function getLostStatusLabel(status?: string): string {
  const map: Record<string, string> = {
    PENDING: '待审核',
    APPROVED: '寻物中',
    CLAIMED: '已找回',
    COMPLETED: '已完结',
    CLOSED: '已关闭',
    EXPIRED: '已过期',
  }
  return (status && map[status]) || status || '寻物中'
}

/**
 * 状态标签 Element Plus 主题类型
 */
function getStatusTagType(status?: string): 'primary' | 'success' | 'warning' | 'info' | 'danger' {
  const map: Record<string, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
    PENDING: 'info',
    APPROVED: 'warning',
    CLAIMED: 'success',
    COMPLETED: 'primary',
    CLOSED: 'info',
    EXPIRED: 'info',
    REJECTED: 'danger',
  }
  return (status && map[status]) || 'info'
}

/**
 * 格式化时间
 */
function formatDateTime(timeStr?: string): string {
  if (!timeStr) return '-'
  return timeStr.replace('T', ' ').substring(0, 16)
}

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

function handleSizeChange(newSize: number) {
  query.size = newSize
  query.page = 1
  fetchData()
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
  padding-bottom: 40px;
}

.filter-card {
  background: #ffffff;
  padding: 18px 20px 4px 20px;
  border-radius: 10px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.empty-wrap {
  padding: 40px 0;
  background: #ffffff;
  border-radius: 10px;
}

.item-grid {
  row-gap: 20px;
}

.lost-card {
  background: #ffffff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid #f1f5f9;
  transition: all 0.25s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.lost-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
  border-color: #cbd5e1;
}

/* 4:3 固定比例图片容器 */
.image-wrapper {
  position: relative;
  width: 100%;
  padding-top: 75%;
  background-color: #f8fafc;
  overflow: hidden;
}

.card-img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.35s ease;
}

.lost-card:hover .card-img {
  transform: scale(1.05);
}

.img-loading,
.img-fallback,
.no-img-box {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background-color: #f1f5f9;
  color: #94a3b8;
  font-size: 13px;
}

.no-img-text {
  font-size: 12px;
  color: #94a3b8;
  letter-spacing: 0.5px;
}

.image-badge {
  position: absolute;
  right: 8px;
  bottom: 8px;
  background: rgba(15, 23, 42, 0.72);
  backdrop-filter: blur(4px);
  color: #ffffff;
  font-size: 11px;
  padding: 3px 8px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
  z-index: 2;
}

.floating-tags {
  position: absolute;
  top: 8px;
  left: 8px;
  right: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 2;
  pointer-events: none;
}

.category-pill {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(4px);
  color: #1e293b;
  font-size: 11px;
  font-weight: 600;
  padding: 3px 8px;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* 卡片文本内容 */
.card-body {
  padding: 14px 16px;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.card-title {
  margin: 0 0 8px 0;
  font-size: 15px;
  font-weight: 600;
  color: #0f172a;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.2s;
}

.lost-card:hover .card-title {
  color: #d97706;
}

.desc-box {
  margin-bottom: 12px;
  min-height: 38px;
}

.desc-text {
  margin: 0;
  font-size: 13px;
  color: #64748b;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.meta-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 10px 0;
  border-top: 1px dashed #f1f5f9;
  border-bottom: 1px dashed #f1f5f9;
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #64748b;
  overflow: hidden;
}

.meta-icon {
  font-size: 13px;
  margin-right: 4px;
  color: #94a3b8;
  flex-shrink: 0;
}

.meta-label {
  color: #94a3b8;
  flex-shrink: 0;
}

.meta-val {
  color: #334155;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: 4px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 6px;
}

.user-name {
  font-size: 12px;
  color: #475569;
  max-width: 90px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.detail-link {
  font-size: 12px;
  color: #d97706;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 2px;
  transition: transform 0.2s ease;
}

.lost-card:hover .detail-link {
  transform: translateX(3px);
}

.pagination-bar {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>
