/**
 * 购物车状态：角标数量与后端 /cart 同步
 * 未登录时 addItem 抛出 code=LOGIN_REQUIRED
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '../utils/api'
import { useUserStore } from './user'

export const useCartStore = defineStore('cart', () => {
  const count = ref(0)

  /** 登出或未登录时清零角标 */
  const reset = () => {
    count.value = 0
  }

  /** 从 /cart/list 同步条目数量到 count */
  const refresh = async () => {
    const userStore = useUserStore()
    if (!userStore.token) {
      reset()
      return
    }
    try {
      const res = await api.get('/cart/list')
      count.value = res.data?.length || 0
    } catch {
      reset()
    }
  }

  const updateCount = (value) => {
    count.value = value
  }

  /** 加入购物车后自动 refresh；未登录抛 LOGIN_REQUIRED */
  const addItem = async (productId, quantity = 1) => {
    const userStore = useUserStore()
    if (!userStore.token) {
      const err = new Error('请先登录')
      err.code = 'LOGIN_REQUIRED'
      throw err
    }
    await api.post('/cart/add', { productId, quantity })
    await refresh()
  }

  return {
    count,
    reset,
    refresh,
    updateCount,
    addItem
  }
})
