<template>
  <el-container class="admin-app-shell" :class="{ 'admin-app-shell--merchant': isMerchantOnly }">
    <el-aside :width="asideWidth" class="admin-sidenav">
      <div class="sidenav-brand">
        <div class="brand-mark">
          <el-icon><Coffee /></el-icon>
        </div>
        <div v-show="!collapsed" class="brand-text">
          <span class="brand-title">{{ isMerchantOnly ? '商家工作台' : '茶韵管理台' }}</span>
          <span class="brand-tag">{{ isMerchantOnly ? 'MERCHANT' : 'ADMIN' }}</span>
        </div>
      </div>
      <el-scrollbar class="sidenav-scroll">
        <el-menu
          :default-active="activeMenu"
          router
          class="admin-menu"
          :collapse="collapsed"
          :collapse-transition="false"
        >
          <el-menu-item v-if="!isMerchantOnly" index="/admin/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>数据统计</template>
          </el-menu-item>
          <el-menu-item :index="isMerchantOnly ? '/merchant/orders' : '/admin/orders'">
            <el-icon><ShoppingBag /></el-icon>
            <template #title>订单管理</template>
          </el-menu-item>
          <el-menu-item :index="isMerchantOnly ? '/merchant/refunds' : '/admin/refunds'">
            <el-icon><Money /></el-icon>
            <template #title>退款管理</template>
          </el-menu-item>
          <el-menu-item :index="isMerchantOnly ? '/merchant/products' : '/admin/products'">
            <el-icon><Goods /></el-icon>
            <template #title>商品管理</template>
          </el-menu-item>
          <el-menu-item v-if="!isMerchantOnly" index="/admin/categories">
            <el-icon><Menu /></el-icon>
            <template #title>分类管理</template>
          </el-menu-item>
          <el-menu-item v-if="!isMerchantOnly" index="/admin/users">
            <el-icon><User /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>
          <el-menu-item v-if="!isMerchantOnly" index="/admin/merchant-applications">
            <el-icon><DocumentChecked /></el-icon>
            <template #title>商家申请</template>
          </el-menu-item>
        </el-menu>
      </el-scrollbar>
      <div class="sidenav-footer">
        <el-button text class="collapse-btn" @click="collapsed = !collapsed">
          <el-icon v-if="collapsed"><Expand /></el-icon>
          <el-icon v-else><Fold /></el-icon>
          <span v-show="!collapsed">收起侧栏</span>
        </el-button>
      </div>
    </el-aside>

    <el-container class="admin-body">
      <el-header class="admin-topbar" height="auto">
        <div class="topbar-inner">
          <div class="topbar-titles">
            <p class="topbar-kicker">{{ isMerchantOnly ? '商家后台' : '管理中心' }}</p>
            <h1 class="topbar-title">{{ pageTitle }}</h1>
            <p v-if="pageSubtitle" class="topbar-subtitle">{{ pageSubtitle }}</p>
          </div>
          <div class="topbar-actions">
            <el-tag :type="isMerchantOnly ? 'warning' : 'danger'" effect="light" round size="small" class="role-chip">
              {{ isMerchantOnly ? '商家' : '管理员' }}
            </el-tag>
            <el-dropdown trigger="click" @command="handleCommand">
              <span class="user-pill">
                <span class="user-avatar">{{ userInitial }}</span>
                <span class="user-label">{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '用户' }}</span>
                <el-icon class="user-caret"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="home">
                    <el-icon><House /></el-icon>
                    返回前台
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      <el-main class="admin-shell-main">
        <router-view v-slot="{ Component }">
          <transition name="admin-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import {
  Coffee,
  DataAnalysis,
  Goods,
  User,
  ArrowDown,
  ShoppingBag,
  Money,
  Menu,
  DocumentChecked,
  Fold,
  Expand,
  House,
  SwitchButton
} from '@element-plus/icons-vue'
import '../styles/admin-shell.css'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const collapsed = ref(false)

const activeMenu = computed(() => route.path)
const isMerchantOnly = computed(() => route.path.startsWith('/merchant'))

const asideWidth = computed(() => (collapsed.value ? '72px' : '232px'))

const pageTitle = computed(() => {
  for (let i = route.matched.length - 1; i >= 0; i--) {
    const t = route.matched[i].meta?.adminTitle
    if (t) return t
  }
  return isMerchantOnly.value ? '商家工作台' : '管理后台'
})

const pageSubtitle = computed(() => {
  for (let i = route.matched.length - 1; i >= 0; i--) {
    const s = route.matched[i].meta?.adminSubtitle
    if (s) return s
  }
  return ''
})

const userInitial = computed(() => {
  const name = userStore.userInfo?.nickname || userStore.userInfo?.username || ''
  return name ? name.slice(0, 1).toUpperCase() : 'U'
})

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'home') {
    router.push('/')
  }
}
</script>

<style scoped>
.admin-app-shell {
  height: 100vh;
  background: #f4f1ec;
}

.admin-app-shell--merchant {
  --admin-accent: #c9913e;
  --admin-accent-soft: rgba(201, 145, 62, 0.15);
}

.admin-app-shell:not(.admin-app-shell--merchant) {
  --admin-accent: var(--primary-color);
  --admin-accent-soft: rgba(188, 56, 35, 0.12);
}

.admin-sidenav {
  display: flex;
  flex-direction: column;
  background: linear-gradient(175deg, #1e2d26 0%, #121a16 48%, #0d1411 100%);
  border-right: 1px solid rgba(255, 255, 255, 0.06);
  box-shadow: 4px 0 32px rgba(0, 0, 0, 0.12);
}

.sidenav-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 18px 18px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.brand-mark {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--admin-accent), var(--primary-dark));
  color: #fff;
  font-size: 22px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.25);
}

.brand-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.brand-title {
  color: #f5f2eb;
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 0.02em;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.brand-tag {
  font-size: 10px;
  letter-spacing: 0.14em;
  color: rgba(251, 204, 141, 0.75);
}

.sidenav-scroll {
  flex: 1;
  min-height: 0;
}

.sidenav-footer {
  padding: 10px 12px 14px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.collapse-btn {
  width: 100%;
  justify-content: flex-start;
  color: rgba(255, 255, 255, 0.55);
  font-size: 13px;
}

.collapse-btn:hover {
  color: #fbcc8d;
}

.admin-body {
  flex-direction: column;
  min-width: 0;
  background: transparent;
}

.admin-topbar {
  padding: 0;
  background: transparent;
  border: none;
}

.topbar-inner {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20px;
  flex-wrap: wrap;
  margin: 18px 22px 0;
  padding: 18px 22px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(10px);
  border-radius: 14px;
  border: 1px solid rgba(0, 0, 0, 0.05);
  box-shadow: 0 4px 20px rgba(26, 45, 36, 0.06);
}

.topbar-kicker {
  margin: 0 0 4px;
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--text-tertiary);
  font-weight: 600;
}

.topbar-title {
  margin: 0;
  font-size: 24px;
  font-weight: 800;
  letter-spacing: -0.03em;
  color: var(--text-primary);
  line-height: 1.25;
}

.topbar-subtitle {
  margin: 8px 0 0;
  font-size: 14px;
  color: var(--text-secondary);
  max-width: 560px;
  line-height: 1.5;
}

.topbar-actions {
  display: flex;
  align-items: center;
  gap: 14px;
  padding-top: 4px;
}

.role-chip {
  font-weight: 600;
}

.user-pill {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 6px 8px 6px 6px;
  border-radius: 999px;
  background: #faf9f7;
  border: 1px solid rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.user-pill:hover {
  border-color: rgba(188, 56, 35, 0.35);
  box-shadow: 0 2px 12px rgba(188, 56, 35, 0.1);
}

.user-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-light), var(--primary-color));
  color: #fff;
  font-size: 14px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-label {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.user-caret {
  color: var(--text-tertiary);
  font-size: 12px;
}

.admin-shell-main {
  padding: 16px 22px 28px;
  min-height: 0;
  background:
    radial-gradient(ellipse 80% 50% at 100% 0%, rgba(188, 56, 35, 0.06), transparent 50%),
    radial-gradient(ellipse 60% 40% at 0% 100%, rgba(251, 204, 141, 0.12), transparent 45%),
    #f4f1ec;
}

.admin-menu {
  border-right: none !important;
  background: transparent !important;
  padding: 10px 10px 16px;
}

.admin-menu :deep(.el-menu-item) {
  height: 46px;
  line-height: 46px;
  margin-bottom: 4px;
  border-radius: 10px;
  color: rgba(255, 255, 255, 0.72) !important;
  border: 1px solid transparent;
}

.admin-menu :deep(.el-menu-item .el-icon) {
  color: rgba(255, 255, 255, 0.55);
}

.admin-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.06) !important;
  color: #fff !important;
}

.admin-menu :deep(.el-menu-item.is-active) {
  background: var(--admin-accent-soft) !important;
  color: #fbcc8d !important;
  border-color: rgba(251, 204, 141, 0.25);
  font-weight: 600;
}

.admin-menu :deep(.el-menu-item.is-active .el-icon) {
  color: #fbcc8d;
}

.admin-fade-enter-active,
.admin-fade-leave-active {
  transition: opacity 0.18s ease, transform 0.18s ease;
}

.admin-fade-enter-from,
.admin-fade-leave-to {
  opacity: 0;
  transform: translateY(6px);
}

.admin-sidenav {
  min-height: 100vh;
  height: 100%;
}

:deep(.el-dropdown-menu__item) {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
</style>
