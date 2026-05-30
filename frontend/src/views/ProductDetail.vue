<!-- 商品详情：图册、规格、加购/立即购买、详情富文本（茶文化关键词链）与评价 -->
<template>
  <div class="product-detail-page">
    <div class="breadcrumb-wrapper">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/products' }">商品</el-breadcrumb-item>
        <el-breadcrumb-item>{{ product?.name || '商品详情' }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    
    <div v-if="loading" class="loading-wrapper">
      <el-skeleton :rows="5" animated />
    </div>
    <div v-else-if="!product" class="error-wrapper">
      <el-empty description="商品不存在或已下架">
        <el-button type="primary" @click="$router.push('/products')">返回商品列表</el-button>
      </el-empty>
    </div>
    <div class="product-detail" v-else>
      <div class="detail-container">
        <!-- 左侧图片区域 -->
        <div class="image-section">
          <div class="main-image-wrapper">
            <el-image 
              :src="mainImage" 
              fit="contain"
              class="main-image"
              :preview-src-list="imageList"
              :initial-index="0"
              preview-teleported
            />
            <div class="image-badge" v-if="product.originalPrice">
              <span class="badge-text">省¥{{ (product.originalPrice - product.price).toFixed(0) }}</span>
            </div>
          </div>
          <div class="thumbnail-list" v-if="imageList.length > 1">
            <div
              v-for="(img, index) in imageList"
              :key="index"
              class="thumbnail-item"
              :class="{ active: mainImage === img }"
              @click="mainImage = img"
            >
              <el-image :src="img" fit="cover" />
            </div>
          </div>
        </div>

        <!-- 右侧信息区域 -->
        <div class="info-section">
          <h1 class="product-title">{{ product.name }}</h1>
          <p class="product-subtitle">{{ product.description }}</p>
          
          <!-- 价格区域 -->
          <div class="price-section">
            <div class="price-main">
              <span class="price-label">现价</span>
              <span class="current-price">¥{{ product.price }}</span>
            </div>
            <div class="price-extra" v-if="product.originalPrice">
              <span class="original-price">原价：¥{{ product.originalPrice }}</span>
              <span class="discount">省¥{{ (product.originalPrice - product.price).toFixed(2) }}</span>
            </div>
          </div>

          <!-- 商品信息 -->
          <div class="product-specs">
            <div class="spec-item">
              <span class="spec-label">
                <el-icon><Location /></el-icon>
                产地
              </span>
              <span class="spec-value">{{ product.origin || '未知' }}</span>
            </div>
            <div class="spec-item">
              <span class="spec-label">
                <el-icon><Tools /></el-icon>
                工艺
              </span>
              <span class="spec-value">{{ product.process || '未知' }}</span>
            </div>
            <div class="spec-item">
              <span class="spec-label">
                <el-icon><Medal /></el-icon>
                等级
              </span>
              <span class="spec-value">{{ product.grade || '未知' }}</span>
            </div>
            <div class="spec-item">
              <span class="spec-label">
                <el-icon><Box /></el-icon>
                库存
              </span>
              <span class="spec-value stock" :class="{ 'low-stock': product.stock < 10 }">
                {{ product.stock }} 件
              </span>
            </div>
            <div class="spec-item">
              <span class="spec-label">
                <el-icon><TrendCharts /></el-icon>
                销量
              </span>
              <span class="spec-value">{{ product.sales || 0 }} 件</span>
            </div>
          </div>

          <!-- 数量选择 -->
          <div class="quantity-section">
            <span class="quantity-label">购买数量：</span>
            <el-input-number 
              v-model="quantity" 
              :min="quantityMin" 
              :max="quantityMax"
              :disabled="product.stock === 0"
              size="large"
            />
            <span class="stock-tip" v-if="product.stock > 0">库存 {{ product.stock }} 件</span>
            <span class="stock-tip out-of-stock" v-else>缺货</span>
          </div>

          <!-- 操作按钮 -->
          <div class="action-section">
            <el-button 
              type="primary" 
              size="large" 
              @click="addToCart"
              :disabled="product.stock === 0"
              class="action-btn cart-btn"
            >
              <el-icon><ShoppingCart /></el-icon>
              加入购物车
            </el-button>
            <el-button 
              type="success" 
              size="large" 
              @click="buyNow"
              :disabled="product.stock === 0"
              class="action-btn buy-btn"
            >
              <el-icon><ShoppingBag /></el-icon>
              立即购买
            </el-button>
          </div>

          <!-- 服务保障 -->
          <div class="service-section">
            <div class="service-item">
              <el-icon><CircleCheck /></el-icon>
              <span>正品保证</span>
            </div>
            <div class="service-item">
              <el-icon><Promotion /></el-icon>
              <span>快速配送</span>
            </div>
            <div class="service-item">
              <el-icon><RefreshLeft /></el-icon>
              <span>7天退换</span>
            </div>
            <div class="service-item">
              <el-icon><Tools /></el-icon>
              <span>专业服务</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 商品详情 -->
      <div class="detail-tabs">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="商品详情" name="detail">
            <div
              class="detail-content"
              v-html="detailHtml"
              @click="onDetailContentClick"
            ></div>
          </el-tab-pane>
          <el-tab-pane label="商品参数" name="params">
            <div class="params-content">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="商品名称">{{ product.name }}</el-descriptions-item>
                <el-descriptions-item label="商品价格">¥{{ product.price }}</el-descriptions-item>
                <el-descriptions-item label="产地">{{ product.origin || '未知' }}</el-descriptions-item>
                <el-descriptions-item label="工艺">{{ product.process || '未知' }}</el-descriptions-item>
                <el-descriptions-item label="等级">{{ product.grade || '未知' }}</el-descriptions-item>
                <el-descriptions-item label="库存">{{ product.stock }} 件</el-descriptions-item>
                <el-descriptions-item label="销量">{{ product.sales || 0 }} 件</el-descriptions-item>
                <el-descriptions-item label="商品状态">
                  <el-tag :type="product.status === 1 ? 'success' : 'danger'">
                    {{ product.status === 1 ? '在售' : '已下架' }}
                  </el-tag>
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </el-tab-pane>
          <el-tab-pane label="商品评价" name="reviews">
            <div class="reviews-section">
              <!-- 评价统计 -->
              <div class="review-stats" v-if="reviewStats.total > 0">
                <div class="stats-overview">
                  <div class="rating-score">
                    <span class="score">{{ reviewStats.averageRating.toFixed(1) }}</span>
                    <el-rate 
                      v-model="reviewStats.averageRating" 
                      disabled 
                      show-score 
                      text-color="#ff9900"
                      size="large"
                    />
                    <div style="margin-top: 8px; font-size: 14px; color: var(--text-secondary);">
                      共{{ reviewStats.total }}条评价
                    </div>
                  </div>
                  <div class="rating-distribution">
                    <div class="rating-item" v-for="i in 5" :key="i">
                      <span class="rating-label">{{ 6 - i }}星</span>
                      <el-progress 
                        :percentage="reviewStats.distribution[6 - i] || 0" 
                        :color="getRatingColor(6 - i)"
                        :show-text="false"
                      />
                      <span class="rating-count">{{ reviewStats.distribution[6 - i] || 0 }}%</span>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- 评价列表 -->
              <div class="reviews-list" v-loading="reviewsLoading">
                <div v-if="reviews.length === 0 && !reviewsLoading" class="empty-reviews">
                  <el-empty description="暂无评价" />
                </div>
                <div v-for="review in reviews" :key="review.id" class="review-card">
                  <div class="review-header">
                    <div class="reviewer-info">
                      <el-avatar 
                        :size="48" 
                        :src="review.userAvatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'"
                      >
                        {{ review.userNickname ? review.userNickname.charAt(0) : 'U' }}
                      </el-avatar>
                      <div class="reviewer-details">
                        <div class="reviewer-name">{{ review.userNickname || '匿名用户' }}</div>
                        <div class="review-time">{{ formatTime(review.createTime) }}</div>
                      </div>
                    </div>
                    <el-rate 
                      v-model="review.rating" 
                      disabled 
                      show-score 
                      text-color="#ff9900"
                      size="default"
                    />
                  </div>
                  <div class="review-content">{{ review.content || '用户未填写评价内容' }}</div>
                  <div v-if="review.reply" class="review-reply">
                    <div class="reply-header">
                      <el-icon><ChatDotRound /></el-icon>
                      <span>商家回复</span>
                    </div>
                    <div class="reply-content">{{ review.reply }}</div>
                    <div class="reply-time">{{ formatTime(review.replyTime) }}</div>
                  </div>
                </div>
              </div>
              
              <!-- 分页 -->
              <el-pagination
                v-if="reviewStats.total > 0"
                v-model:current-page="reviewPageNum"
                v-model:page-size="reviewPageSize"
                :total="reviewStats.total"
                layout="prev, pager, next"
                @current-change="loadReviews"
              />
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
/** 路由 params.id → /product/detail；buyNow 走订单创建流程 */
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useCartStore } from '../stores/cart'
import api from '../utils/api'
import { linkTeaCultureKeywords } from '../utils/teaCultureKeywords'
import { toast } from '../utils/message'
import { ElMessage } from 'element-plus'
import { 
  Location, 
  Tools, 
  Medal, 
  Box, 
  TrendCharts, 
  ShoppingCart, 
  ShoppingBag,
  CircleCheck,
  Promotion,
  RefreshLeft,
  ChatDotRound
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const product = ref(null)
const quantity = ref(1)
const mainImage = ref('')
const activeTab = ref('detail')
const reviews = ref([])
const reviewsLoading = ref(false)
const reviewPageNum = ref(1)
const reviewPageSize = ref(10)
const reviewStats = ref({
  total: 0,
  averageRating: 0,
  distribution: {}
})
const loading = ref(true)

const normalizedStock = computed(() => {
  const stock = Number(product.value?.stock ?? 0)
  return Number.isFinite(stock) && stock > 0 ? Math.floor(stock) : 0
})

const quantityMin = computed(() => (normalizedStock.value > 0 ? 1 : 0))
const quantityMax = computed(() => normalizedStock.value)

const imageList = computed(() => {
  if (!product.value?.images) return []
  try {
    return JSON.parse(product.value.images)
  } catch {
    return product.value.images.split(',')
  }
})

/** 详情 HTML 中将茶文化关键词替换为 /tea-culture?section= 链接 */
const detailHtml = computed(() => {
  const raw = product.value?.detail || product.value?.description || ''
  return linkTeaCultureKeywords(raw)
})

const onDetailContentClick = (event) => {
  const link = event.target.closest('a.culture-link')
  if (!link) return
  event.preventDefault()
  const href = link.getAttribute('href')
  if (href) router.push(href)
}

const loadProduct = async () => {
  loading.value = true
  try {
    const res = await api.get(`/product/detail/${route.params.id}`)
    if (res.data) {
      product.value = res.data
      if (imageList.value.length > 0) {
        mainImage.value = imageList.value[0]
      } else {
        mainImage.value = 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=500&h=500&fit=crop'
      }
    } else {
      product.value = null
    }
  } catch (error) {
    console.error('获取商品详情失败:', error)
    ElMessage.error(error.message || '获取商品详情失败')
    product.value = null
  } finally {
    loading.value = false
  }
}

const addToCart = async () => {
  try {
    await cartStore.addItem(product.value.id, quantity.value)
    toast.success('已加入购物车')
  } catch (error) {
    if (error.code === 'LOGIN_REQUIRED') {
      toast.warning('请先登录')
      router.push('/login')
      return
    }
    toast.error(error.message || '加入购物车失败')
  }
}

const buyNow = async () => {
  try {
    await cartStore.addItem(product.value.id, quantity.value)
    router.push('/cart')
  } catch (error) {
    if (error.code === 'LOGIN_REQUIRED') {
      toast.warning('请先登录')
      router.push('/login')
      return
    }
    toast.error(error.message || '操作失败')
  }
}

onMounted(() => {
  if (route.params.id) {
    loadProduct()
  } else {
    ElMessage.error('商品ID不存在')
    router.push('/products')
  }
})

// 监听路由参数变化
watch(() => route.params.id, (newId) => {
  if (newId) {
    loadProduct()
  }
})

watch(
  () => normalizedStock.value,
  (stock) => {
    if (stock <= 0) {
      quantity.value = 0
      return
    }
    if (quantity.value < 1 || quantity.value > stock) {
      quantity.value = 1
    }
  },
  { immediate: true }
)

// 监听标签页切换
watch(() => activeTab.value, (newTab) => {
  if (newTab === 'reviews' && product.value) {
    loadReviews()
  }
})

const loadReviews = async () => {
  if (!product.value) return
  reviewsLoading.value = true
  try {
    const res = await api.get(`/review/product/${product.value.id}`, {
      params: {
        pageNum: reviewPageNum.value,
        pageSize: reviewPageSize.value
      }
    })
    reviews.value = res.data.list || []
    
    // 计算评价统计
    if (reviews.value.length > 0) {
      const total = res.data.total || reviews.value.length
      const sum = reviews.value.reduce((acc, r) => acc + r.rating, 0)
      const avg = sum / reviews.value.length
      
      const distribution = {}
      reviews.value.forEach(r => {
        distribution[r.rating] = (distribution[r.rating] || 0) + 1
      })
      
      // 转换为百分比
      Object.keys(distribution).forEach(key => {
        distribution[key] = Math.round((distribution[key] / reviews.value.length) * 100)
      })
      
      reviewStats.value = {
        total,
        averageRating: avg,
        distribution
      }
    } else {
      reviewStats.value = {
        total: 0,
        averageRating: 0,
        distribution: {}
      }
    }
  } catch (error) {
    console.error('获取评价失败', error)
  } finally {
    reviewsLoading.value = false
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getRatingColor = (rating) => {
  const colors = {
    5: '#67c23a',
    4: '#95de64',
    3: '#e6a23c',
    2: '#f56c6c',
    1: '#ff4d4f'
  }
  return colors[rating] || '#909399'
}
</script>

<style scoped>
.product-detail-page {
  background: linear-gradient(to bottom, #f0f9ff 0%, #ffffff 100%);
  min-height: calc(100vh - 60px);
  padding-bottom: 40px;
}

.breadcrumb-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 20px 0;
}

.product-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.detail-container {
  display: flex;
  gap: 40px;
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: var(--shadow);
  margin-bottom: 30px;
}

/* 图片区域 */
.image-section {
  flex: 0 0 480px;
}

.main-image-wrapper {
  position: relative;
  width: 100%;
  height: 480px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  overflow: hidden;
  background: #fafafa;
  margin-bottom: 15px;
}

.main-image {
  width: 100%;
  height: 100%;
}

.image-badge {
  position: absolute;
  top: 15px;
  left: 15px;
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  color: #fff;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(255, 77, 79, 0.3);
}

.thumbnail-list {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.thumbnail-item {
  width: 80px;
  height: 80px;
  border: 2px solid transparent;
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.thumbnail-item:hover,
.thumbnail-item.active {
  border-color: var(--primary-color);
  transform: scale(1.05);
}

.thumbnail-item .el-image {
  width: 100%;
  height: 100%;
}

/* 信息区域 */
.info-section {
  flex: 1;
}

.product-title {
  font-size: 28px;
  font-weight: bold;
  color: var(--text-primary);
  margin: 0 0 12px 0;
  line-height: 1.4;
}

.product-subtitle {
  font-size: 16px;
  color: var(--text-secondary);
  margin: 0 0 25px 0;
  line-height: 1.6;
}

.price-section {
  background: linear-gradient(135deg, #fef5f3 0%, #ffffff 100%);
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 25px;
  border: 1px solid #f5b5a9;
}

.price-main {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 8px;
}

.price-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.current-price {
  font-size: 36px;
  font-weight: bold;
  color: #ff4d4f;
}

.price-extra {
  display: flex;
  align-items: center;
  gap: 15px;
}

.original-price {
  font-size: 16px;
  color: var(--text-tertiary);
  text-decoration: line-through;
}

.discount {
  background: #fff1f0;
  color: #ff4d4f;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
}

.product-specs {
  background: #fafafa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 25px;
}

.spec-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.spec-item:last-child {
  border-bottom: none;
}

.spec-label {
  flex: 0 0 120px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-secondary);
  font-size: 14px;
}

.spec-label .el-icon {
  color: var(--primary-color);
  font-size: 16px;
}

.spec-value {
  flex: 1;
  color: var(--text-primary);
  font-size: 14px;
  font-weight: 500;
}

.spec-value.stock {
  color: var(--primary-color);
  font-weight: 600;
}

.spec-value.low-stock {
  color: #ff4d4f;
}

.quantity-section {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: #fafafa;
  border-radius: 8px;
  margin-bottom: 25px;
}

.quantity-label {
  font-size: 16px;
  color: var(--text-primary);
  font-weight: 500;
}

.stock-tip {
  font-size: 14px;
  color: var(--text-secondary);
  margin-left: auto;
}

.stock-tip.out-of-stock {
  color: #ff4d4f;
  font-weight: 600;
}

.action-section {
  display: flex;
  gap: 15px;
  margin-bottom: 25px;
}

.action-btn {
  flex: 1;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  transition: all 0.3s;
}

.cart-btn {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  border: none;
}

.cart-btn:hover {
  background: linear-gradient(135deg, var(--primary-dark) 0%, var(--primary-color) 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(188, 56, 35, 0.4);
}

.buy-btn {
  background: linear-gradient(135deg, #bc3823 0%, #9d2d1c 100%);
  border: none;
}

.buy-btn:hover {
  background: linear-gradient(135deg, #9d2d1c 0%, #bc3823 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(188, 56, 35, 0.4);
}

.service-section {
  display: flex;
  gap: 30px;
  padding: 20px;
  background: #fef5f3;
  border-radius: 8px;
  border: 1px solid #f5b5a9;
}

.service-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-primary);
  font-size: 14px;
}

.service-item .el-icon {
  color: var(--primary-color);
  font-size: 18px;
}

/* 详情标签页 */
.detail-tabs {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: var(--shadow);
}

:deep(.el-tabs__header) {
  margin-bottom: 30px;
}

:deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 500;
}

:deep(.el-tabs__item.is-active) {
  color: var(--primary-color);
}

:deep(.el-tabs__active-bar) {
  background-color: var(--primary-color);
}

.detail-content {
  line-height: 1.8;
  color: var(--text-primary);
  font-size: 15px;
}

.detail-content :deep(.culture-link) {
  color: var(--primary-color);
  text-decoration: underline;
  cursor: pointer;
}

.detail-content :deep(.culture-link:hover) {
  opacity: 0.85;
}

.params-content {
  padding: 20px 0;
}

.loading-wrapper,
.error-wrapper {
  max-width: 1200px;
  margin: 40px auto;
  padding: 40px 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: var(--shadow);
}

.error-wrapper {
  text-align: center;
}

/* 商品评价部分样式 */
.reviews-section {
  padding: 20px 0;
}

/* 评价统计 */
.review-stats {
  background: linear-gradient(135deg, #fef5f3 0%, #ffffff 100%);
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 30px;
  border: 1px solid #f5b5a9;
}

.stats-overview {
  display: flex;
  align-items: center;
  gap: 50px;
  flex-wrap: wrap;
}

.rating-score {
  text-align: center;
  flex-shrink: 0;
  min-width: 150px;
}

.rating-score .score {
  font-size: 56px;
  font-weight: 700;
  color: var(--primary-color);
  display: block;
  margin-bottom: 10px;
  line-height: 1;
}

.rating-distribution {
  flex: 1;
  min-width: 300px;
}

.rating-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  gap: 12px;
}

.rating-item:last-child {
  margin-bottom: 0;
}

.rating-label {
  width: 50px;
  text-align: right;
  font-size: 14px;
  color: var(--text-secondary);
  font-weight: 500;
}

.rating-item :deep(.el-progress) {
  flex: 1;
  max-width: 250px;
}

.rating-count {
  width: 50px;
  text-align: left;
  font-size: 14px;
  color: var(--text-secondary);
  font-weight: 500;
}

/* 评价列表 */
.reviews-list {
  margin-top: 20px;
}

.empty-reviews {
  padding: 60px 0;
  text-align: center;
}

.review-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.review-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border-color: #e0e0e0;
}

.review-card:last-child {
  margin-bottom: 0;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f5f5f5;
}

.reviewer-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.reviewer-details {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.reviewer-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 16px;
}

.review-time {
  font-size: 13px;
  color: var(--text-tertiary);
}

.review-content {
  font-size: 15px;
  color: var(--text-primary);
  line-height: 1.8;
  margin-bottom: 16px;
  word-break: break-word;
}

.review-reply {
  background: linear-gradient(135deg, #fef5f3 0%, #ffffff 100%);
  border-left: 4px solid var(--primary-color);
  padding: 16px;
  border-radius: 8px;
  margin-top: 16px;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: var(--primary-color);
  margin-bottom: 10px;
  font-size: 14px;
}

.reply-header .el-icon {
  font-size: 16px;
}

.reply-content {
  font-size: 14px;
  color: var(--text-primary);
  line-height: 1.7;
  margin-bottom: 8px;
}

.reply-time {
  font-size: 12px;
  color: var(--text-tertiary);
}

/* 评价分页 */
.reviews-section :deep(.el-pagination) {
  margin-top: 30px;
  justify-content: center;
}

.reviews-section :deep(.el-pagination .el-pager li) {
  color: var(--text-primary);
}

.reviews-section :deep(.el-pagination .el-pager li.is-active) {
  background-color: var(--primary-color);
  color: #fff;
}

.reviews-section :deep(.el-pagination .btn-prev),
.reviews-section :deep(.el-pagination .btn-next) {
  color: var(--text-primary);
}

.reviews-section :deep(.el-pagination .btn-prev:hover),
.reviews-section :deep(.el-pagination .btn-next:hover) {
  color: var(--primary-color);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .detail-container {
    flex-direction: column;
  }
  
  .image-section {
    flex: 1;
  }
  
  .action-section {
    flex-direction: column;
  }

  .stats-overview {
    flex-direction: column;
    gap: 30px;
    align-items: flex-start;
  }

  .rating-score {
    width: 100%;
  }

  .rating-distribution {
    width: 100%;
  }

  .review-header {
    flex-direction: column;
    gap: 12px;
  }

  .review-card {
    padding: 16px;
  }
}
</style>

