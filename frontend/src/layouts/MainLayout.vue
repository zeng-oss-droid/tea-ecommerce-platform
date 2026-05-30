<!-- 前台主布局：顶栏导航 + 子路由内容区 -->
<template>
  <el-container class="main-layout">
    <el-header class="site-header" height="60px">
      <div class="header-content">
        <div class="logo" @click="router.push('/')">
          <img src="/logo/茶叶.png" alt="茶韵" class="logo-image" />
          <span>茶韵</span>
        </div>
        <div class="header-actions">
          <router-link to="/join-us" class="header-link">加入我们</router-link>
          <template v-if="userStore.token">
            <router-link to="/cart" class="header-icon-btn" aria-label="购物车">
              <el-icon><ShoppingCart /></el-icon>
              <span v-if="cartStore.count > 0" class="cart-badge">{{ cartStore.count > 99 ? '99+' : cartStore.count }}</span>
            </router-link>
            <el-dropdown trigger="click" @command="handleCommand">
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
            <router-link to="/login" class="header-link">登录</router-link>
            <router-link to="/register" class="header-link">注册</router-link>
          </template>
        </div>
      </div>
    </el-header>
    <el-main class="site-main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
/** 顶栏：登录态展示购物车角标与用户菜单；路由/ token 变化时刷新购物车 */
import { onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useCartStore } from '../stores/cart'
import { ArrowDown, ShoppingCart } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

/** 用户下拉：logout / admin / merchant / 前台个人页路由 */
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

// 有 token 时恢复用户信息；无论是否登录都尝试同步购物车角标
onMounted(async () => {
  if (userStore.token) {
    try {
      await userStore.getUserInfo()
    } catch (error) {
      if (error.response?.status === 401) {
        userStore.logout()
      }
    }
  }
  cartStore.refresh()
})

// 路由切换且已登录时刷新购物车数量
watch(() => route.path, () => {
  if (userStore.token) {
    cartStore.refresh()
  }
})

// 登录/登出时刷新或清空购物车角标
watch(() => userStore.token, (newToken) => {
  if (newToken) {
    cartStore.refresh()
  } else {
    cartStore.reset()
  }
})
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
}

.site-header {
  background: linear-gradient(135deg, #bc3823 0%, #9d2d1c 100%);
  border-bottom: none;
  padding: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 3000;
  isolation: isolate;
}

.site-main {
  position: relative;
  z-index: 0;
  min-height: calc(100vh - 60px);
  background: linear-gradient(to bottom, #fef5f3 0%, #ffffff 100%);
  padding: 0;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  gap: 16px;
}

.logo {
  flex-shrink: 0;
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

.header-actions {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-shrink: 0;
  position: relative;
  z-index: 1;
}

.header-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 40px;
  padding: 0 12px;
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  border-radius: 6px;
  transition: opacity 0.2s ease, background-color 0.2s ease;
  white-space: nowrap;
}

.header-link:hover {
  opacity: 0.9;
  background-color: rgba(255, 255, 255, 0.12);
}

.header-icon-btn {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  color: #fff;
  font-size: 20px;
  text-decoration: none;
  border-radius: 6px;
  transition: opacity 0.2s ease, background-color 0.2s ease;
}

.header-icon-btn:hover {
  opacity: 0.9;
  background-color: rgba(255, 255, 255, 0.12);
}

.cart-badge {
  position: absolute;
  top: 4px;
  right: 4px;
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  font-size: 11px;
  line-height: 16px;
  text-align: center;
  color: #bc3823;
  background: #fff;
  border-radius: 8px;
  pointer-events: none;
}

.user-name {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  min-height: 40px;
  padding: 0 12px;
  color: #fff;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  line-height: 1;
  transition: opacity 0.2s ease, background-color 0.2s ease;
  outline: none;
}

.user-name:hover {
  opacity: 0.9;
  background-color: rgba(255, 255, 255, 0.12);
}

.user-name:focus,
.user-name:focus-visible {
  outline: none;
}

.header-actions :deep(.el-dropdown) {
  outline: none;
}

.header-actions :deep(.el-tooltip__trigger) {
  outline: none;
}
</style>
