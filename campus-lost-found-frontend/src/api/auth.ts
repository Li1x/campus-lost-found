import { http } from '@/utils/request'
import type { LoginResult, UserInfo } from '@/types'

/**
 * 用户注册
 */
export function registerApi(data: {
  studentId: string
  username: string
  password: string
  nickname: string
  phone: string
  email?: string
  contactInfo?: string
}) {
  return http.post<UserInfo>('/auth/register', data)
}

/**
 * 用户登录
 */
export function loginApi(data: {
  username: string
  password: string
}) {
  return http.post<LoginResult>('/auth/login', data)
}

/**
 * 刷新 Token
 */
export function refreshTokenApi(data: {
  refreshToken: string
}) {
  return http.post<{
    accessToken: string
    refreshToken: string
    expiresIn: number
  }>('/auth/refresh', data)
}

/**
 * 退出登录
 */
export function logoutApi() {
  return http.post<void>('/auth/logout')
}
