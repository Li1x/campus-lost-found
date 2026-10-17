import { http } from '@/utils/request'
import type { UserInfo } from '@/types'

/**
 * 获取当前登录用户的详细个人信息
 */
export function getUserInfoApi() {
  return http.get<UserInfo>('/user/info')
}

/**
 * 修改个人信息
 */
export function updateProfileApi(data: {
  nickname?: string
  avatar?: string
  phone?: string
  email?: string
  contactInfo?: string
}) {
  return http.put<UserInfo>('/user/profile', data)
}

/**
 * 修改密码
 */
export function changePasswordApi(data: {
  oldPassword: string
  newPassword: string
}) {
  return http.put<void>('/user/password', data)
}

/**
 * 查询指定用户公开资料
 */
export function getUserPublicInfoApi(id: number) {
  return http.get<UserInfo>(`/user/${id}`)
}
