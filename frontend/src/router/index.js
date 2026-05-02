import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/',
    component: () => import('../layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('../views/Home.vue')
      },
      {
        path: 'products',
        name: 'Products',
        component: () => import('../views/Products.vue')
      },
      {
        path: 'product/:id',
        name: 'ProductDetail',
        component: () => import('../views/ProductDetail.vue')
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('../views/Cart.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('../views/Orders.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'order/:id',
        name: 'OrderDetail',
        component: () => import('../views/OrderDetail.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'addresses',
        name: 'Addresses',
        component: () => import('../views/Addresses.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/Profile.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'join-us',
        name: 'JoinUs',
        component: () => import('../views/JoinUs.vue')
      },
      {
        path: 'tea-culture',
        name: 'TeaCulture',
        component: () => import('../views/TeaCulture.vue')
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/admin',
    component: () => import('../layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('../views/admin/Dashboard.vue'),
        meta: { adminTitle: '数据统计', adminSubtitle: '全站用户、商品、订单与销售额总览' }
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('../views/admin/OrderManagement.vue'),
        meta: { adminTitle: '订单管理', adminSubtitle: '查看与处理全平台订单' }
      },
      {
        path: 'refunds',
        name: 'AdminRefunds',
        component: () => import('../views/admin/RefundManagement.vue'),
        meta: { adminTitle: '退款管理', adminSubtitle: '审核与跟进退款申请' }
      },
      {
        path: 'products',
        name: 'AdminProducts',
        component: () => import('../views/admin/ProductManagement.vue'),
        meta: { adminTitle: '商品管理', adminSubtitle: '维护全站商品与上下架' }
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: () => import('../views/admin/CategoryManagement.vue'),
        meta: { adminTitle: '分类管理', adminSubtitle: '商品分类与展示顺序' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('../views/admin/UserManagement.vue'),
        meta: { adminTitle: '用户管理', adminSubtitle: '账号状态与角色' }
      },
      {
        path: 'merchant-applications',
        name: 'MerchantApplications',
        component: () => import('../views/admin/MerchantApplicationManagement.vue'),
        meta: { adminTitle: '商家申请', adminSubtitle: '审核入驻资料与开通商家权限' }
      }
    ]
  },
  {
    path: '/merchant',
    component: () => import('../layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresMerchant: true },
    children: [
      {
        path: 'products',
        name: 'MerchantProducts',
        component: () => import('../views/admin/ProductManagement.vue'),
        meta: { adminTitle: '我的商品', adminSubtitle: '上架、编辑与库存维护' }
      },
      {
        path: 'orders',
        name: 'MerchantOrders',
        component: () => import('../views/admin/OrderManagement.vue'),
        meta: { adminTitle: '店铺订单', adminSubtitle: '发货与订单状态处理' }
      },
      {
        path: 'refunds',
        name: 'MerchantRefunds',
        component: () => import('../views/admin/RefundManagement.vue'),
        meta: { adminTitle: '店铺退款', adminSubtitle: '处理买家退款申请' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  
  // 检查是否需要登录
  if (to.meta.requiresAuth && !userStore.token) {
    next('/login')
    return
  }
  
  // 检查是否需要管理员权限
  if (to.meta.requiresAdmin) {
    if (!userStore.token) {
      next('/login')
      return
    }
    
    // 确保用户信息已加载
    if (!userStore.userInfo) {
      try {
        await userStore.getUserInfo()
      } catch (error) {
        next('/login')
        return
      }
    }
    
    // 检查是否是管理员
    if (userStore.userInfo?.role !== 1) {
      ElMessage.error('无权限访问')
      next('/')
      return
    }
  }

  // 检查是否需要商家权限（管理员也可访问）
  if (to.meta.requiresMerchant) {
    if (!userStore.token) {
      next('/login')
      return
    }

    if (!userStore.userInfo) {
      try {
        await userStore.getUserInfo()
      } catch (error) {
        next('/login')
        return
      }
    }

    if (![1, 2].includes(userStore.userInfo?.role)) {
      ElMessage.error('无权限访问')
      next('/')
      return
    }
  }
  
  next()
})

export default router

