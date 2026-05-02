import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '../utils/api'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  const login = async (username, password) => {
    const res = await api.post('/user/login', { username, password })
    token.value = res.data.token
    localStorage.setItem('token', res.data.token)
    await getUserInfo()
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

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
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

