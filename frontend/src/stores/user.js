/**
 * 用户状态：token 持久化到 localStorage，登录/登出联动购物车刷新
 * role: 0 普通用户 | 1 管理员 | 2 商家
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '../utils/api'
import { useCartStore } from './cart'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  /** 登录成功：持久化 token、拉用户信息、刷新购物车 */
  const login = async (username, password) => {
    const res = await api.post('/user/login', { username, password })
    token.value = res.data.token
    localStorage.setItem('token', res.data.token)
    await getUserInfo()
    await useCartStore().refresh()
    return res
  }

  const register = async (data) => {
    const res = await api.post('/user/register', data)
    return res
  }

  const getUserInfo = async () => {
    if (!token.value) return
    try {
      const res = await api.get('/user/info')
      if (res.code === 200 && res.data) {
        userInfo.value = res.data
      }
    } catch (error) {
      // 401错误时清除token，其他错误不处理
      if (error.response?.status === 401 || error.message?.includes('401')) {
        logout()
      }
    }
  }

  /** 清除本地 token 与购物车角标 */
  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    useCartStore().reset()
  }

  const updateUserInfo = async (data) => {
    const res = await api.put('/user/info', data)
    userInfo.value = res.data
    return res
  }

  return {
    token,
    userInfo,
    login,
    register,
    getUserInfo,
    logout,
    updateUserInfo
  }
})

