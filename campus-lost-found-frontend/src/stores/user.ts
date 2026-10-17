import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfo } from '@/types'
import { loginApi, logoutApi } from '@/api/auth'
import { getUserInfoApi } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('access_token') || '')
  const refreshToken = ref<string>(localStorage.getItem('refresh_token') || '')
  
  // 初始化从 localStorage 读取用户信息
  const cachedUser = localStorage.getItem('user_info')
  const userInfo = ref<UserInfo | null>(cachedUser ? JSON.parse(cachedUser) : null)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => {
    return userInfo.value?.role === 'ADMIN' || userInfo.value?.role === 'SUPER_ADMIN'
  })
  const isSuperAdmin = computed(() => userInfo.value?.role === 'SUPER_ADMIN')

  /**
   * 登录
   */
  async function login(params: { username: string; password: string }) {
    const res = await loginApi(params)
    const data = res.data

    token.value = data.accessToken
    refreshToken.value = data.refreshToken
    userInfo.value = data.user

    localStorage.setItem('access_token', data.accessToken)
    localStorage.setItem('refresh_token', data.refreshToken)
    localStorage.setItem('user_info', JSON.stringify(data.user))

    return data
  }

  /**
   * 获取最新用户信息
   */
  async function fetchUserInfo() {
    if (!token.value) return null
    try {
      const res = await getUserInfoApi()
      userInfo.value = res.data
      localStorage.setItem('user_info', JSON.stringify(res.data))
      return res.data
    } catch (e) {
      console.error('获取用户信息失败:', e)
      return null
    }
  }

  /**
   * 退出登录
   */
  async function logout() {
    try {
      if (token.value) {
        await logoutApi()
      }
    } catch (e) {
      console.warn('退出登录接口调用失败，直接清除本地凭证:', e)
    } finally {
      token.value = ''
      refreshToken.value = ''
      userInfo.value = null
      localStorage.removeItem('access_token')
      localStorage.removeItem('refresh_token')
      localStorage.removeItem('user_info')
    }
  }

  /**
   * 更新本地保存的用户部分信息
   */
  function updateUserInfo(partial: Partial<UserInfo>) {
    if (userInfo.value) {
      userInfo.value = { ...userInfo.value, ...partial }
      localStorage.setItem('user_info', JSON.stringify(userInfo.value))
    }
  }

  return {
    token,
    refreshToken,
    userInfo,
    isLoggedIn,
    isAdmin,
    isSuperAdmin,
    login,
    fetchUserInfo,
    logout,
    updateUserInfo,
  }
})
