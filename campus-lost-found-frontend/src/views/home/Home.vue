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

    <!-- 核心动态大厅专区（现代化Tab分栏切换 + 智能双栏切换，解决长滑屏痛点） -->
    <section class="lobby-section" v-loading="loading">
      <!-- 顶部控制栏：切换药丸Tab + 右侧快捷入口 -->
      <div class="lobby-header-bar">
        <div class="lobby-tabs-wrapper">
          <button
            class="lobby-tab-btn"
            :class="{ active: activeTab === 'LOST', 'lost-active': activeTab === 'LOST' }"
            @click="activeTab = 'LOST'"
          >
            <el-icon class="tab-icon warning-icon"><WarningFilled /></el-icon>
            <span class="tab-text">最新寻物启事</span>
            <span class="count-tag count-warning">{{ lostList.length }}</span>
          </button>

          <button
            class="lobby-tab-btn"
            :class="{ active: activeTab === 'FOUND', 'found-active': activeTab === 'FOUND' }"
            @click="activeTab = 'FOUND'"
          >
            <el-icon class="tab-icon success-icon"><CircleCheckFilled /></el-icon>
            <span class="tab-text">最新招领信息</span>
            <span class="count-tag count-success">{{ foundList.length }}</span>
          </button>

          <button
            class="lobby-tab-btn compare-tab"
            :class="{ active: activeTab === 'ALL' }"
            @click="activeTab = 'ALL'"
            title="在大屏幕下左右并排对比查看寻物与招领"
          >
            <el-icon><Grid /></el-icon>
            <span class="tab-text">全景双栏对比</span>
          </button>
        </div>

        <!-- 右侧快速查看更多与发布通道 -->
        <div class="lobby-actions">
          <template v-if="activeTab === 'LOST'">
            <span class="lobby-tip">失主焦急寻找中，发现线索请及时联系</span>
            <el-button type="warning" plain size="small" class="action-btn" @click="router.push('/lost')">
              查看全部寻物 <el-icon class="el-icon--right"><ArrowRight /></el-icon>
            </el-button>
          </template>
          <template v-else-if="activeTab === 'FOUND'">
            <span class="lobby-tip">拾金不昧暖校园，遗失物品等待认领</span>
            <el-button type="success" plain size="small" class="action-btn" @click="router.push('/found')">
              查看全部招领 <el-icon class="el-icon--right"><ArrowRight /></el-icon>
            </el-button>
          </template>
          <template v-else>
            <el-button link type="primary" size="small" @click="router.push('/lost')">寻物大厅</el-button>
            <el-divider direction="vertical" />
            <el-button link type="success" size="small" @click="router.push('/found')">招领大厅</el-button>
          </template>
        </div>
      </div>

      <!-- 内容展示区：根据 activeTab 丝滑呈现 -->

      <!-- 模式一：聚焦展示「最新寻物启事」 -->
      <div v-if="activeTab === 'LOST'" class="tab-content-panel">
        <div v-if="lostList.length > 0" class="cards-grid">
          <div
            v-for="item in lostList"
            :key="item.id"
            class="item-card lost-card"
            @click="goToDetail('LOST', item.id)"
          >
            <!-- 4:3 固定比例图片容器 -->
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
                      <span>图片加载中...</span>
                    </div>
                  </template>
                </el-image>
                <div v-if="item.images.length > 1" class="image-badge">
                  <el-icon><PictureFilled /></el-icon> {{ item.images.length }} 图
                </div>
              </template>
              <div v-else class="no-img-box">
                <el-icon :size="36" color="#cbd5e1"><Picture /></el-icon>
                <span class="no-img-text">暂无实物照片</span>
              </div>

              <!-- 浮动分类与状态标签 -->
              <div class="floating-tags">
                <span class="category-pill">{{ item.categoryName || '其它物品' }}</span>
                <el-tag size="small" :type="getStatusTagType(item.status)" effect="dark">
                  {{ getLostStatusLabel(item.status) }}
                </el-tag>
              </div>
            </div>

            <!-- 卡片主体内容 -->
            <div class="card-body">
              <h4 class="card-title" :title="item.title">{{ item.title }}</h4>

              <div class="desc-box">
                <span class="desc-label">物品特征：</span>
                <p class="desc-text">{{ item.description || '暂无详细描述' }}</p>
              </div>

              <div class="meta-list">
                <div class="meta-item">
                  <el-icon class="meta-icon"><Location /></el-icon>
                  <span class="meta-key">丢失地点：</span>
                  <span class="meta-val" :title="item.lostLocation">{{ item.lostLocation || '未注明' }}</span>
                </div>
                <div class="meta-item">
                  <el-icon class="meta-icon"><Calendar /></el-icon>
                  <span class="meta-key">丢失时间：</span>
                  <span class="meta-val">{{ item.lostTime || '未注明' }}</span>
                </div>
                <div class="meta-item">
                  <el-icon class="meta-icon"><Phone /></el-icon>
                  <span class="meta-key">联系方式：</span>
                  <span class="meta-val contact-val">{{ item.contactInfo || '认证核对后可见' }}</span>
                </div>
              </div>

              <div class="card-footer">
                <div class="publisher-info">
                  <el-avatar :size="22" :src="getImageUrl(item.userAvatar)" class="user-avatar">
                    {{ item.userNickname ? item.userNickname.charAt(0) : '校' }}
                  </el-avatar>
                  <span class="user-name">{{ item.userNickname || '校园同学' }}</span>
                  <span class="time-tip">{{ formatTime(item.createdAt) }}</span>
                </div>
                <el-button type="warning" size="small" class="detail-btn">
                  查看详情
                </el-button>
              </div>
            </div>
          </div>
        </div>
        <el-empty
          v-else-if="!loading"
          description="暂无最新寻物启事"
          :image-size="100"
          class="empty-container"
        >
          <el-button type="warning" size="small" @click="router.push('/publish?type=LOST')">
            去发布寻物信息
          </el-button>
        </el-empty>
      </div>

      <!-- 模式二：聚焦展示「最新招领信息」 -->
      <div v-else-if="activeTab === 'FOUND'" class="tab-content-panel">
        <div v-if="foundList.length > 0" class="cards-grid">
          <div
            v-for="item in foundList"
            :key="item.id"
            class="item-card found-card"
            @click="goToDetail('FOUND', item.id)"
          >
            <!-- 4:3 固定比例图片容器 -->
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
                      <span>图片加载中...</span>
                    </div>
                  </template>
                </el-image>
                <div v-if="item.images.length > 1" class="image-badge">
                  <el-icon><PictureFilled /></el-icon> {{ item.images.length }} 图
                </div>
              </template>
              <div v-else class="no-img-box">
                <el-icon :size="36" color="#cbd5e1"><Picture /></el-icon>
                <span class="no-img-text">暂无实物照片</span>
              </div>

              <!-- 浮动分类与状态标签 -->
              <div class="floating-tags">
                <span class="category-pill">{{ item.categoryName || '其它物品' }}</span>
                <el-tag size="small" :type="getStatusTagType(item.status)" effect="dark">
                  {{ getFoundStatusLabel(item.status) }}
                </el-tag>
              </div>
            </div>

            <!-- 卡片主体内容 -->
            <div class="card-body">
              <h4 class="card-title" :title="item.title">{{ item.title }}</h4>

              <div class="desc-box">
                <span class="desc-label">物品详情：</span>
                <p class="desc-text">{{ item.description || '暂无详细描述' }}</p>
              </div>

              <div class="meta-list">
                <div class="meta-item">
                  <el-icon class="meta-icon"><Location /></el-icon>
                  <span class="meta-key">拾得地点：</span>
                  <span class="meta-val" :title="item.foundLocation">{{ item.foundLocation || '未注明' }}</span>
                </div>
                <div class="meta-item">
                  <el-icon class="meta-icon"><Calendar /></el-icon>
                  <span class="meta-key">拾得时间：</span>
                  <span class="meta-val">{{ item.foundTime || '未注明' }}</span>
                </div>
                <div v-if="item.storageLocation" class="meta-item">
                  <el-icon class="meta-icon"><OfficeBuilding /></el-icon>
                  <span class="meta-key">现存放地：</span>
                  <span class="meta-val storage-val" :title="item.storageLocation">{{ item.storageLocation }}</span>
                </div>
                <div class="meta-item">
                  <el-icon class="meta-icon"><Phone /></el-icon>
                  <span class="meta-key">联系方式：</span>
                  <span class="meta-val contact-val">{{ item.contactInfo || '认领核对后可见' }}</span>
                </div>
              </div>

              <div class="card-footer">
                <div class="publisher-info">
                  <el-avatar :size="22" :src="getImageUrl(item.userAvatar)" class="user-avatar">
                    {{ item.userNickname ? item.userNickname.charAt(0) : '拾' }}
                  </el-avatar>
                  <span class="user-name">{{ item.userNickname || '好心同学' }}</span>
                  <span class="time-tip">{{ formatTime(item.createdAt) }}</span>
                </div>
                <el-button type="success" size="small" class="detail-btn">
                  申请认领
                </el-button>
              </div>
            </div>
          </div>
        </div>
        <el-empty
          v-else-if="!loading"
          description="暂无最新招领启事"
          :image-size="100"
          class="empty-container"
        >
          <el-button type="success" size="small" @click="router.push('/publish?type=FOUND')">
            去发布招领信息
          </el-button>
        </el-empty>
      </div>

      <!-- 模式三：全景双栏对比模式（宽屏下左右各一栏对比） -->
      <div v-else class="compare-columns-panel">
        <el-row :gutter="24">
          <!-- 左栏：寻物 -->
          <el-col :xs="24" :lg="12">
            <div class="column-box lost-column">
              <div class="column-header">
                <div class="col-title">
                  <el-icon class="warning-icon"><WarningFilled /></el-icon>
                  <span>寻物专区 ({{ lostList.length }})</span>
                </div>
                <el-button link type="warning" size="small" @click="router.push('/lost')">全部寻物 &gt;</el-button>
              </div>
              <div v-if="lostList.length > 0" class="cards-grid column-grid">
                <div
                  v-for="item in lostList"
                  :key="item.id"
                  class="item-card lost-card compact-card"
                  @click="goToDetail('LOST', item.id)"
                >
                  <div class="image-wrapper">
                    <el-image
                      v-if="item.images && item.images.length > 0"
                      :src="getImageUrl(item.images[0])"
                      fit="cover"
                      class="card-img"
                      :preview-src-list="getPreviewList(item.images)"
                      preview-teleported
                      @click.stop
                    />
                    <div v-else class="no-img-box">
                      <el-icon :size="28" color="#cbd5e1"><Picture /></el-icon>
                      <span class="no-img-text">暂无照片</span>
                    </div>
                    <div class="floating-tags">
                      <span class="category-pill">{{ item.categoryName || '其它' }}</span>
                      <el-tag size="small" :type="getStatusTagType(item.status)" effect="dark">
                        {{ getLostStatusLabel(item.status) }}
                      </el-tag>
                    </div>
                  </div>
                  <div class="card-body">
                    <h4 class="card-title">{{ item.title }}</h4>
                    <p class="desc-text">{{ item.description }}</p>
                    <div class="meta-list">
                      <div class="meta-item">
                        <el-icon class="meta-icon"><Location /></el-icon>
                        <span class="meta-val">{{ item.lostLocation }}</span>
                      </div>
                      <div class="meta-item">
                        <el-icon class="meta-icon"><Calendar /></el-icon>
                        <span class="meta-val">{{ item.lostTime }}</span>
                      </div>
                    </div>
                    <div class="card-footer">
                      <span class="user-name">{{ item.userNickname || '同学' }} · {{ formatTime(item.createdAt) }}</span>
                      <el-button type="warning" size="small" link>查看详情 &gt;</el-button>
                    </div>
                  </div>
                </div>
              </div>
              <el-empty v-else description="暂无寻物" :image-size="60" />
            </div>
          </el-col>

          <!-- 右栏：招领 -->
          <el-col :xs="24" :lg="12">
            <div class="column-box found-column">
              <div class="column-header">
                <div class="col-title">
                  <el-icon class="success-icon"><CircleCheckFilled /></el-icon>
                  <span>招领专区 ({{ foundList.length }})</span>
                </div>
                <el-button link type="success" size="small" @click="router.push('/found')">全部招领 &gt;</el-button>
              </div>
              <div v-if="foundList.length > 0" class="cards-grid column-grid">
                <div
                  v-for="item in foundList"
                  :key="item.id"
                  class="item-card found-card compact-card"
                  @click="goToDetail('FOUND', item.id)"
                >
                  <div class="image-wrapper">
                    <el-image
                      v-if="item.images && item.images.length > 0"
                      :src="getImageUrl(item.images[0])"
                      fit="cover"
                      class="card-img"
                      :preview-src-list="getPreviewList(item.images)"
                      preview-teleported
                      @click.stop
                    />
                    <div v-else class="no-img-box">
                      <el-icon :size="28" color="#cbd5e1"><Picture /></el-icon>
                      <span class="no-img-text">暂无照片</span>
                    </div>
                    <div class="floating-tags">
                      <span class="category-pill">{{ item.categoryName || '其它' }}</span>
                      <el-tag size="small" :type="getStatusTagType(item.status)" effect="dark">
                        {{ getFoundStatusLabel(item.status) }}
                      </el-tag>
                    </div>
                  </div>
                  <div class="card-body">
                    <h4 class="card-title">{{ item.title }}</h4>
                    <p class="desc-text">{{ item.description }}</p>
                    <div class="meta-list">
                      <div class="meta-item">
                        <el-icon class="meta-icon"><Location /></el-icon>
                        <span class="meta-val">{{ item.foundLocation }}</span>
                      </div>
                      <div class="meta-item">
                        <el-icon class="meta-icon"><Calendar /></el-icon>
                        <span class="meta-val">{{ item.foundTime }}</span>
                      </div>
                    </div>
                    <div class="card-footer">
                      <span class="user-name">{{ item.userNickname || '好心人' }} · {{ formatTime(item.createdAt) }}</span>
                      <el-button type="success" size="small" link>申请认领 &gt;</el-button>
                    </div>
                  </div>
                </div>
              </div>
              <el-empty v-else description="暂无招领" :image-size="60" />
            </div>
          </el-col>
        </el-row>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { http } from '@/utils/request'
import type { PageResult, LostItemDetail, FoundItemDetail, CategoryItem } from '@/types'

const router = useRouter()

// 视图标签：'LOST'（寻物） | 'FOUND'（招领） | 'ALL'（全景双栏对比）
const activeTab = ref<'LOST' | 'FOUND' | 'ALL'>('LOST')

const categories = ref<CategoryItem[]>([
  { id: 1, name: '证件证照', sortOrder: 1, status: 1 },
  { id: 2, name: '数码电子', sortOrder: 2, status: 1 },
  { id: 3, name: '书籍文具', sortOrder: 3, status: 1 },
  { id: 4, name: '服装鞋包', sortOrder: 4, status: 1 },
  { id: 5, name: '钥匙卡包', sortOrder: 5, status: 1 },
  { id: 6, name: '体育器材', sortOrder: 6, status: 1 },
  { id: 7, name: '生活用品', sortOrder: 7, status: 1 },
  { id: 8, name: '其它物品', sortOrder: 8, status: 1 },
])

const lostList = ref<LostItemDetail[]>([])
const foundList = ref<FoundItemDetail[]>([])
const loading = ref(false)

/**
 * 格式化图片地址，兼容全路径、绝对路径与相对路径
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
 * 友好格式化发布时间（如：10-19 14:30）
 */
function formatTime(timeStr?: string): string {
  if (!timeStr) return ''
  const clean = timeStr.replace('T', ' ')
  if (clean.length >= 16) {
    return clean.substring(5, 16)
  }
  return clean
}

/**
 * 寻物状态标签文案
 */
function getLostStatusLabel(status: string): string {
  const map: Record<string, string> = {
    PENDING: '待审核',
    APPROVED: '寻物中',
    CLAIMED: '已找回',
    COMPLETED: '已完结',
    CLOSED: '已关闭',
    EXPIRED: '已过期',
  }
  return map[status] || status || '寻物中'
}

/**
 * 招领状态标签文案
 */
function getFoundStatusLabel(status: string): string {
  const map: Record<string, string> = {
    PENDING: '待审核',
    APPROVED: '招领中',
    CLAIMED: '已认领',
    COMPLETED: '已完结',
    CLOSED: '已关闭',
    EXPIRED: '已过期',
  }
  return map[status] || status || '招领中'
}

/**
 * 状态标签 Element Plus 主题类型
 */
function getStatusTagType(status: string): 'primary' | 'success' | 'warning' | 'info' | 'danger' {
  const map: Record<string, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
    PENDING: 'info',
    APPROVED: 'success',
    CLAIMED: 'warning',
    COMPLETED: 'primary',
    CLOSED: 'info',
    EXPIRED: 'info',
    REJECTED: 'danger',
  }
  return map[status] || 'info'
}

/**
 * 跳转至详情页面
 */
function goToDetail(type: 'LOST' | 'FOUND', id: number) {
  router.push(`/item/${type}/${id}`)
}

/**
 * 快捷分类跳转
 */
function goToCategory(categoryId: number) {
  router.push({ path: '/found', query: { categoryId } })
}

/**
 * 加载首页真实数据
 */
async function loadHomeData() {
  try {
    loading.value = true
    const [lostRes, foundRes, catRes] = await Promise.allSettled([
      http.get<PageResult<LostItemDetail>>('/lost-items', { page: 1, size: 6 }),
      http.get<PageResult<FoundItemDetail>>('/found-items', { page: 1, size: 6 }),
      http.get<CategoryItem[]>('/categories'),
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
    console.error('加载首页数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadHomeData()
})
</script>

<style scoped>
.home-container {
  padding-bottom: 40px;
}

/* 顶部横幅 */
.hero-banner {
  background: linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%);
  border-radius: 16px;
  padding: 44px 32px;
  color: #ffffff;
  text-align: center;
  margin-bottom: 32px;
  box-shadow: 0 10px 20px -5px rgba(59, 130, 246, 0.3);
}

.hero-content h1 {
  font-size: 30px;
  font-weight: 700;
  margin: 0 0 10px 0;
}

.hero-content p {
  font-size: 15px;
  opacity: 0.9;
  margin: 0 0 24px 0;
}

.hero-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}

/* 物品分类网格 */
.section-title {
  margin-bottom: 14px;
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
  margin-bottom: 32px;
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

/* 核心动态大厅专区容器 */
.lobby-section {
  background-color: #ffffff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05), 0 10px 25px -5px rgba(0, 0, 0, 0.03);
  border: 1px solid #e2e8f0;
}

/* 顶部交互切换控制栏 */
.lobby-header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f1f5f9;
  flex-wrap: wrap;
  gap: 16px;
}

.lobby-tabs-wrapper {
  display: flex;
  background-color: #f1f5f9;
  padding: 4px;
  border-radius: 12px;
  gap: 4px;
}

.lobby-tab-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 18px;
  border: none;
  background: transparent;
  color: #64748b;
  font-size: 14px;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.lobby-tab-btn:hover {
  color: #1e293b;
}

.lobby-tab-btn.active {
  background-color: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.lobby-tab-btn.lost-active {
  color: #d97706;
}

.lobby-tab-btn.found-active {
  color: #059669;
}

.compare-tab {
  font-size: 13px;
  border-left: 1px solid #cbd5e1;
  border-radius: 0 8px 8px 0;
  margin-left: 4px;
}

.tab-icon {
  font-size: 16px;
}

.warning-icon {
  color: #f59e0b;
}

.success-icon {
  color: #10b981;
}

.count-tag {
  font-size: 11px;
  font-weight: 700;
  padding: 2px 7px;
  border-radius: 12px;
}

.count-warning {
  background-color: #fef3c7;
  color: #d97706;
}

.count-success {
  background-color: #d1fae5;
  color: #059669;
}

.lobby-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.lobby-tip {
  font-size: 13px;
  color: #94a3b8;
}

.action-btn {
  font-weight: 600;
}

/* 卡片网格（自适应列数） */
.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

/* 单个物品卡片设计 */
.item-card {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.02);
}

.item-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px -6px rgba(0, 0, 0, 0.1);
}

.lost-card:hover {
  border-color: #fcd34d;
}

.found-card:hover {
  border-color: #6ee7b7;
}

/* 4:3 比例实物图片展示区域 */
.image-wrapper {
  position: relative;
  width: 100%;
  aspect-ratio: 4 / 3;
  background-color: #f8fafc;
  overflow: hidden;
}

.card-img {
  width: 100%;
  height: 100%;
  display: block;
  transition: transform 0.3s ease;
}

.item-card:hover .card-img {
  transform: scale(1.03);
}

.img-loading,
.img-fallback,
.no-img-box {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  color: #94a3b8;
}

.no-img-text {
  font-size: 13px;
  font-weight: 500;
  color: #94a3b8;
}

.image-badge {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background: rgba(15, 23, 42, 0.75);
  color: #ffffff;
  padding: 3px 8px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 500;
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  gap: 4px;
  z-index: 2;
}

.floating-tags {
  position: absolute;
  top: 10px;
  left: 10px;
  right: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  pointer-events: none;
  z-index: 2;
}

.category-pill {
  background: rgba(255, 255, 255, 0.94);
  color: #1e293b;
  font-size: 12px;
  font-weight: 600;
  padding: 3px 10px;
  border-radius: 20px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(4px);
}

/* 卡片内容区 */
.card-body {
  padding: 16px;
  display: flex;
  flex-direction: column;
  flex: 1;
  gap: 12px;
}

.card-title {
  font-size: 16px;
  font-weight: 700;
  color: #0f172a;
  margin: 0;
  line-height: 1.4;
  word-break: break-word;
}

/* 完整详细描述区（不截断） */
.desc-box {
  background-color: #f8fafc;
  border-radius: 8px;
  padding: 10px 12px;
  border-left: 3px solid #cbd5e1;
}

.lost-card .desc-box {
  border-left-color: #f59e0b;
}

.found-card .desc-box {
  border-left-color: #10b981;
}

.desc-label {
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
  display: block;
  margin-bottom: 4px;
}

.desc-text {
  font-size: 13px;
  line-height: 1.6;
  color: #334155;
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
}

/* 元数据详细信息网格 */
.meta-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  border-top: 1px dashed #e2e8f0;
  padding-top: 10px;
}

.meta-item {
  display: flex;
  align-items: center;
  font-size: 12px;
  line-height: 1.5;
  color: #64748b;
}

.meta-icon {
  font-size: 14px;
  margin-right: 6px;
  flex-shrink: 0;
  color: #94a3b8;
}

.meta-key {
  color: #64748b;
  flex-shrink: 0;
}

.meta-val {
  color: #1e293b;
  font-weight: 500;
  word-break: break-word;
}

.storage-val {
  color: #059669;
  font-weight: 600;
}

.contact-val {
  color: #2563eb;
  font-weight: 600;
}

/* 卡片底部操作栏 */
.card-footer {
  margin-top: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #f1f5f9;
  padding-top: 12px;
}

.publisher-info {
  display: flex;
  align-items: center;
  gap: 6px;
  overflow: hidden;
}

.user-avatar {
  background-color: #e2e8f0;
  color: #475569;
  font-size: 11px;
  font-weight: 600;
}

.user-name {
  font-size: 12px;
  font-weight: 500;
  color: #475569;
  max-width: 80px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.time-tip {
  font-size: 11px;
  color: #94a3b8;
  white-space: nowrap;
}

.detail-btn {
  font-weight: 600;
  border-radius: 6px;
}

/* 空状态容器 */
.empty-container {
  padding: 40px 0;
}

/* 全景双栏对比模式专有样式 */
.column-box {
  background-color: #f8fafc;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #e2e8f0;
  height: 100%;
}

.lost-column {
  border-top: 3px solid #f59e0b;
}

.found-column {
  border-top: 3px solid #10b981;
}

.column-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-weight: 700;
  font-size: 16px;
  color: #0f172a;
}

.col-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.column-grid {
  grid-template-columns: 1fr;
  gap: 14px;
}

.compact-card {
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

/* 响应式断点 */
@media (max-width: 768px) {
  .lobby-section {
    padding: 16px;
    border-radius: 12px;
  }

  .lobby-header-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .lobby-tabs-wrapper {
    width: 100%;
    justify-content: space-between;
  }

  .lobby-tab-btn {
    flex: 1;
    justify-content: center;
    padding: 8px 10px;
    font-size: 13px;
  }

  .compare-tab {
    display: none; /* 移动端屏幕窄，隐藏并排模式，保留流畅Tab */
  }

  .lobby-actions {
    justify-content: space-between;
    width: 100%;
  }

  .cards-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .hero-banner {
    padding: 32px 16px;
  }

  .hero-content h1 {
    font-size: 24px;
  }
}
</style>
