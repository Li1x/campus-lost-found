/**
 * 通用统一返回结果接口
 */
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

/**
 * 分页数据返回接口
 */
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

/**
 * 分页查询参数接口
 */
export interface PageQuery {
  page?: number
  size?: number
}

/**
 * 用户角色
 */
export type UserRole = 'STUDENT' | 'ADMIN' | 'SUPER_ADMIN'

/**
 * 物品状态
 */
export type ItemStatus =
  | 'PENDING'
  | 'APPROVED'
  | 'REJECTED'
  | 'CLAIMED'
  | 'COMPLETED'
  | 'CLOSED'
  | 'EXPIRED'

/**
 * 认领状态
 */
export type ClaimStatus =
  | 'PENDING'
  | 'OWNER_APPROVED'
  | 'OWNER_REJECTED'
  | 'APPROVED'
  | 'REJECTED'
  | 'CANCELLED'

/**
 * 物品类型
 */
export type ItemType = 'LOST' | 'FOUND'

/**
 * 用户信息接口
 */
export interface UserInfo {
  id: number
  studentId?: string
  username: string
  nickname: string
  avatar?: string
  phone?: string
  email?: string
  contactInfo?: string
  role: UserRole
  status: number
  createdAt: string
}

/**
 * 登录返回结果
 */
export interface LoginResult {
  accessToken: string
  refreshToken: string
  expiresIn: number
  user: UserInfo
}

/**
 * 分类接口
 */
export interface CategoryItem {
  id: number
  name: string
  icon?: string
  sortOrder: number
  status: number
  createdAt?: string
}

/**
 * 物品图片接口
 */
export interface ItemImageItem {
  id: number
  itemType: ItemType
  itemId: number
  url: string
  sortOrder: number
}

/**
 * 失物信息接口
 */
export interface LostItemDetail {
  id: number
  userId: number
  userNickname?: string
  userAvatar?: string
  categoryId: number
  categoryName?: string
  title: string
  description: string
  lostLocation: string
  lostTime: string
  contactInfo?: string
  status: ItemStatus
  reviewerId?: number
  reviewComment?: string
  reviewedAt?: string
  createdAt: string
  updatedAt: string
  images?: string[]
}

/**
 * 招领信息接口
 */
export interface FoundItemDetail {
  id: number
  userId: number
  userNickname?: string
  userAvatar?: string
  categoryId: number
  categoryName?: string
  title: string
  description: string
  foundLocation: string
  foundTime: string
  storageLocation: string
  contactInfo?: string
  status: ItemStatus
  reviewerId?: number
  reviewComment?: string
  reviewedAt?: string
  createdAt: string
  updatedAt: string
  images?: string[]
}

/**
 * 认领申请接口
 */
export interface ClaimItem {
  id: number
  itemType: ItemType
  itemId: number
  itemTitle?: string
  claimantId: number
  claimantNickname?: string
  claimantPhone?: string
  description: string
  contactInfo: string
  status: ClaimStatus
  ownerReviewedAt?: string
  ownerComment?: string
  reviewerId?: number
  reviewComment?: string
  reviewedAt?: string
  createdAt: string
}

/**
 * 站内消息接口
 */
export interface MessageItem {
  id: number
  userId: number
  title: string
  content: string
  type: 'REVIEW_RESULT' | 'CLAIM_NOTIFY' | 'CLAIM_RESULT' | 'SYSTEM'
  relatedId?: number
  relatedType?: 'LOST_ITEM' | 'FOUND_ITEM' | 'CLAIM'
  isRead: number
  createdAt: string
}

/**
 * 运营统计看板数据接口
 */
export interface DashboardStats {
  totalLostCount: number
  totalFoundCount: number
  totalClaimSuccessCount: number
  todayNewCount: number
  pendingReviewCount: number
  categoryDistribution: {
    name: string
    value: number
  }[]
}
