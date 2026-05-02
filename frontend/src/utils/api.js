import axios from 'axios'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import router from '../router'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res
    } else if (res.code === 401) {
      // 处理业务层面的401错误
      const userStore = useUserStore()
      userStore.logout()
      router.push('/login')
      ElMessage.error(res.message || '登录已过期，请重新登录')
      return Promise.reject(new Error(res.message || '登录已过期'))
    } else {
      // 业务错误不在这里显示消息，让组件自己处理，避免重复提示
      // 只抛出错误，不显示消息
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  error => {
    if (error.response) {
      // 检查是否是业务错误（有code字段）
      if (error.response.data && error.response.data.code) {
        // 业务错误，不在这里显示，让组件处理
        return Promise.reject(error)
      }
      
      if (error.response.status === 401) {
        const userStore = useUserStore()
        userStore.logout()
        // 避免重复跳转
        if (router.currentRoute.value.path !== '/login') {
          router.push('/login')
        }
        ElMessage.error('登录已过期，请重新登录')
      } else {
        // HTTP层面的错误才显示
        const message = error.response.data?.message || error.response.data?.error || '请求失败'
        ElMessage.error(message)
      }
    } else {
      // 网络错误才显示
      ElMessage.error('网络错误，请稍后重试')
    }
    return Promise.reject(error)
  }
)

export default api

