<template>
  <el-container>
    <el-header>
      <div class="header-content">
        <div class="logo" @click="$router.push('/')">
          <img src="/logo/茶叶.png" alt="茗集" class="logo-image" />
          <span>茗集</span>
        </div>
        <div class="header-nav">
          <el-button text class="join-btn" @click="$router.push('/join-us')">加入我们</el-button>
        </div>
        <div class="user-info">
          <template v-if="userStore.token">
            <!-- 购物车图标 -->
            <el-icon class="cart-icon" @click="$router.push('/cart')"><ShoppingCart /></el-icon>
            <el-dropdown @command="handleCommand">
              <span class="user-name">
                {{ userStore.userInfo?.nickname || userStore.userInfo?.username }}
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                  <el-dropdown-item command="addresses">收货地址</el-dropdown-item>
                  <el-dropdown-item v-if="userStore.userInfo?.role === 1" command="admin" divided>管理后台</el-dropdown-item>
                  <el-dropdown-item v-if="userStore.userInfo?.role === 2" command="merchant" divided>商家后台</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="text" @click="$router.push('/login')">登录</el-button>
            <el-button type="text" @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import api from '../utils/api'
import { ArrowDown, ShoppingCart } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const cartCount = ref(0)

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/')
  } else if (command === 'admin') {
    router.push('/admin/dashboard')
  } else if (command === 'merchant') {
    router.push('/merchant/products')
  } else {
    router.push(`/${command}`)
  }
}

const loadCartCount = async () => {
  if (!userStore.token) {
    cartCount.value = 0
    return
  }
  try {
    const res = await api.get('/cart/list')
    cartCount.value = res.data?.length || 0
  } catch (error) {
    cartCount.value = 0
  }
}

onMounted(async () => {
  if (userStore.token) {
    try {
      await userStore.getUserInfo()
      loadCartCount()
    } catch (error) {
      // 如果获取用户信息失败，清除token
      if (error.response?.status === 401) {
        userStore.logout()
      }
    }
  }
  loadCartCount()
})

// 监听路由变化，更新购物车数量
watch(() => route.path, () => {
  if (userStore.token) {
    loadCartCount()
  }
})

// 监听用户登录状态变化
watch(() => userStore.token, (newToken) => {
  if (newToken) {
    loadCartCount()
  } else {
    cartCount.value = 0
  }
})
</script>

<style scoped>
.el-header {
  background: linear-gradient(135deg, #bc3823 0%, #9d2d1c 100%);
  border-bottom: none;
  padding: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 22px;
  font-weight: bold;
  color: #fff;
  cursor: pointer;
  transition: opacity 0.3s;
}

.logo:hover {
  opacity: 0.9;
}

.logo-image {
  width: 32px;
  height: 32px;
  object-fit: contain;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.header-nav {
  margin-left: auto;
  margin-right: 20px;
}

.join-btn {
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  padding: 0 4px;
  height: 32px;
  border: none;
  background: transparent;
  border-radius: 0;
  transition: opacity 0.2s ease;
}

.join-btn:hover {
  color: #fff;
  opacity: 0.78;
}

:deep(.join-btn.el-button.is-text:not(.is-disabled):hover),
:deep(.join-btn.el-button.is-text:not(.is-disabled):focus),
:deep(.join-btn.el-button.is-text:not(.is-disabled):active) {
  background-color: transparent !important;
  border-color: transparent !important;
  box-shadow: none !important;
}

.cart-icon {
  font-size: 18px;
  width: 18px;
  height: 18px;
  color: #fff;
  cursor: pointer;
  transition: opacity 0.2s ease;
}

.cart-icon:hover {
  opacity: 0.78;
}

.user-name {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  color: #fff;
  padding: 0 4px;
  border: none;
  background: transparent;
  border-radius: 0;
  font-size: 14px;
  font-weight: 500;
  line-height: 1;
  transition: opacity 0.2s ease;
}

.user-name:hover {
  opacity: 0.78;
}

.user-name:focus,
.user-name:focus-visible {
  outline: none;
}

:deep(.el-dropdown),
:deep(.el-dropdown:focus),
:deep(.el-dropdown:focus-visible),
:deep(.el-tooltip__trigger:focus),
:deep(.el-tooltip__trigger:focus-visible) {
  outline: none;
}

:deep(.el-button--text) {
  color: #fff;
}

:deep(.el-button--text:hover) {
  color: #fff;
  background: transparent;
  opacity: 0.78;
}


.el-main {
  min-height: calc(100vh - 60px);
  background: linear-gradient(to bottom, #fef5f3 0%, #ffffff 100%);
  padding: 0;
}
</style>

