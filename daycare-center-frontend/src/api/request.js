import axios from 'axios'
import { ElMessage } from 'element-plus'

/**
 * Axios 实例 — 所有 API 请求的入口
 *
 * 关键设计：
 * - baseURL: /api → Vite 代理到 localhost:8080，避免跨域
 * - withCredentials: true → 自动携带 Cookie（后端 Session 认证依赖此配置）
 * - 响应拦截器：统一处理业务错误（code !== 200）和 HTTP 错误（401 跳登录）
 */
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  withCredentials: true // 自动发送 Cookie，后端通过 Session 识别用户
})

// 响应拦截器
request.interceptors.response.use(
  res => {
    const data = res.data
    // 后端统一返回 { code, message, data }，code !== 200 即为业务异常
    if (data.code !== 200) {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message))
    }
    return data // 直接返回 data，调用方无需再 .data
  },
  err => {
    // HTTP 401 → Session 过期或未登录 → 硬跳转到登录页
    if (err.response?.status === 401) {
      ElMessage.error('请先登录')
      window.location.href = '/login'
    } else {
      ElMessage.error(err.message || '网络错误')
    }
    return Promise.reject(err)
  }
)

export default request
