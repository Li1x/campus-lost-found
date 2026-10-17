import axios, { AxiosError, type AxiosInstance, type InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import type { ApiResponse } from '@/types'

// 创建 Axios 实例
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8',
  },
})

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('access_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error: AxiosError) => {
    console.error('请求发送失败:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data as ApiResponse

    // 业务状态码 200 表示成功
    if (res.code === 200) {
      return response.data
    }

    // 业务错误提示
    ElMessage.error(res.message || '系统异常')

    // 401 未登录或 Token 失效
    if (res.code === 401) {
      handleUnauthorized()
    }

    return Promise.reject(new Error(res.message || 'Error'))
  },
  (error: AxiosError<ApiResponse>) => {
    const { response, message } = error

    if (response) {
      const status = response.status
      const data = response.data

      switch (status) {
        case 401:
          ElMessage.error(data?.message || '登录已过期，请重新登录')
          handleUnauthorized()
          break
        case 403:
          ElMessage.error(data?.message || '您没有权限执行此操作')
          break
        case 404:
          ElMessage.error(data?.message || '请求的资源不存在')
          break
        case 429:
          ElMessage.error(data?.message || '请求过于频繁，请稍后重试')
          break
        case 500:
          ElMessage.error(data?.message || '服务器内部错误，请稍后重试')
          break
        default:
          ElMessage.error(data?.message || `网络异常 (${status})`)
          break
      }
    } else {
      ElMessage.error(message || '网络连接超时，请检查您的网络')
    }

    return Promise.reject(error)
  }
)

// 处理登录失效重定向
function handleUnauthorized() {
  localStorage.removeItem('access_token')
  localStorage.removeItem('refresh_token')
  localStorage.removeItem('user_info')

  if (window.location.pathname !== '/login') {
    const redirect = encodeURIComponent(window.location.pathname + window.location.search)
    window.location.href = `/login?redirect=${redirect}`
  }
}

/**
 * 封装常用 HTTP 请求方法
 */
export const http = {
  get<T = any>(url: string, params?: any): Promise<ApiResponse<T>> {
    return service.get(url, { params })
  },
  post<T = any>(url: string, data?: any): Promise<ApiResponse<T>> {
    return service.post(url, data)
  },
  put<T = any>(url: string, data?: any): Promise<ApiResponse<T>> {
    return service.put(url, data)
  },
  delete<T = any>(url: string, params?: any): Promise<ApiResponse<T>> {
    return service.delete(url, { params })
  },
}

export default service
