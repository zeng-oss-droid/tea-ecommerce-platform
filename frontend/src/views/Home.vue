<template>
  <div class="home">
    <!-- 轮播图 -->
    <div class="banner-section">
      <el-carousel height="450px" :interval="5000" arrow="hover">
        <el-carousel-item v-for="item in banners" :key="item.id">
          <div
            class="banner-item"
            :style="{ backgroundImage: `url(${item.image})` }"
            @click="goToProductsCenter"
          >
            <div class="banner-overlay">
              <h2 class="banner-title">{{ item.title }}</h2>
              <p class="banner-subtitle">{{ item.subtitle }}</p>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 茶文化介绍 -->
    <div class="tea-culture-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon><Reading /></el-icon>
            茶文化
          </h2>
          <el-link type="primary">以茶载道，品味东方雅韵</el-link>
        </div>

        <div
          class="culture-layout"
          :class="{ 'culture-layout-reverse': index === 1 }"
          v-for="(section, index) in cultureSections"
          :key="section.title"
        >
          <div class="culture-left">
            <h3>{{ section.title }}</h3>
            <p>
              {{ section.desc }}
            </p>
            <el-button type="primary" class="culture-btn" @click="goToTeaCulture">查看更多介绍</el-button>
          </div>

          <div class="culture-right">
            <el-image :src="section.image" fit="cover" class="culture-hero-image" />
          </div>
        </div>
      </div>
    </div>

    <!-- 热门分类 -->
    <div class="categories-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon><Grid /></el-icon>
            热门分类
          </h2>
        </div>
        <div class="categories-grid">
          <div 
            v-for="category in hotCategories" 
            :key="category.id"
            class="category-card"
            @click="goToCategory(category.id)"
          >
            <div class="category-image-wrapper">
              <el-image 
                :src="category.image || 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400&h=300&fit=crop'" 
                fit="cover"
                class="category-image"
              />
              <div class="category-overlay">
                <el-button type="primary" size="default" @click.stop="goToCategory(category.id)">
                  查看商品
                </el-button>
              </div>
            </div>
            <div class="category-info">
              <h3 class="category-name">{{ category.name }}</h3>
              <p class="category-desc">{{ category.description || '精选好茶，品质保证' }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 热门商品 -->
    <div class="container">
      <div class="section-header">
        <h2 class="section-title">
          <el-icon><Star /></el-icon>
          热门商品
        </h2>
        <el-link type="primary" @click="$router.push('/products')">查看更多 →</el-link>
      </div>
      <el-row :gutter="24">
        <el-col :span="6" v-for="product in hotProducts" :key="product.id">
          <div class="product-card" @click="goToDetail(product.id)">
            <div class="product-image-wrapper">
              <el-image 
                :src="getProductImage(product.images)" 
                fit="cover"
                class="product-image"
                :lazy="true"
              />
              <div class="product-overlay">
                <el-button type="primary" size="small" @click.stop="addToCart(product.id)">
                  <el-icon style="margin-right: 10px "><ShoppingCart /></el-icon>
                  加入购物车
                </el-button>
              </div>
              <div class="product-badge" v-if="product.originalPrice">
                省¥{{ (product.originalPrice - product.price).toFixed(0) }}
              </div>
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ product.name }}</h3>
              <p class="product-desc">{{ product.description }}</p>
              <div class="product-footer">
                <div class="price-wrapper">
                  <span class="current-price">¥{{ product.price }}</span>
                  <span class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
                </div>
                <div class="sales-info">
                  <el-icon><TrendCharts /></el-icon>
                  <span>已售{{ product.sales || 0 }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import api from '../utils/api'
import { ElMessage } from 'element-plus'
import { Star, ShoppingCart, TrendCharts, Grid, Reading } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const banners = ref([
  { 
    id: 1, 
    title: '优质茶叶', 
    subtitle: '精选好茶，品味生活',
    image: '/banner/banner1.png' // 将banner图片放在 public/banner/ 目录下
  },
  { 
    id: 2, 
    title: '新鲜上市', 
    subtitle: '当季新茶，限时优惠',
    image: '/banner/banner5.jpeg' // 将banner图片放在 public/banner/ 目录下
  }
])

const hotProducts = ref([])
const hotCategories = ref([])
const cultureSections = ref([
  {
    title: '茶史溯源：千年茶路，万里传香',
    desc: '从神农尝百草发现茶的药用价值，到唐代陆羽著《茶经》确立茶学体系，再到明清时期茶马商队翻山越岭，将茶叶运往藏区、西域乃至世界各地。这条千年茶路，不仅是商品的贸易通道，更是东方文化的传播之路，让一片小小的茶叶，连接起了不同的文明与时代。',
    image: '/IntroductionDiagram/intro-1.jpeg'
  },
  {
    title: '茶类大观：六味天成，各有风华',
    desc: '从清鲜回甘的绿茶、醇和温润的红茶，到香气馥郁的乌龙茶、清甜毫香的白茶，再到黄韵蜜香的黄茶、陈香醇厚的黑茶。不同的发酵工艺，造就了它们截然不同的汤色、香气与口感，也对应着不同地域、不同人群的品饮偏好，每一类茶，都藏着独属于自己的风味故事。',
    image: '/IntroductionDiagram/intro-2.jpeg'
  },
  {
    title: '茶艺茶道：和敬清寂，一盏见心',
    desc: '功夫茶的冲泡场景，也是东方茶道的缩影。茶道并非高深的仪式，而是藏在温杯、投茶、出汤的每一个细节里：讲究的是 “和” 的融洽、“敬” 的谦卑、“清” 的干净、“寂” 的平和。在慢下来的冲泡过程中，我们放下浮躁，感受茶叶在水中舒展的过程，也在一杯茶里，寻得内心的安宁。',
    image: '/IntroductionDiagram/intro-3.jpeg'
  }
])

const getProductImage = (images) => {
  if (!images) return 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=300&h=300&fit=crop'
  try {
    const imgList = JSON.parse(images)
    return imgList[0] || 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=300&h=300&fit=crop'
  } catch {
    const img = images.split(',')[0]
    return img || 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=300&h=300&fit=crop'
  }
}

const goToDetail = (id) => {
  if (!id) {
    ElMessage.warning('商品ID无效')
    return
  }
  router.push(`/product/${id}`)
}

const addToCart = async (productId) => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await api.post('/cart/add', { productId, quantity: 1 })
    ElMessage.success('已加入购物车')
  } catch (error) {
    ElMessage.error('加入购物车失败')
  }
}

const goToCategory = (categoryId) => {
  router.push(`/products?categoryId=${categoryId}`)
}

const goToProductsCenter = () => {
  router.push('/products')
}

const goToTeaCulture = () => {
  router.push('/tea-culture')
}

const loadHotCategories = async () => {
  try {
    const res = await api.get('/category/list')
    const allCategories = res.data || []
    // 获取前5个启用的顶级分类
    const topCategories = allCategories
      .filter(cat => cat.parentId === 0 && cat.status === 1)
      .slice(0, 5)
    hotCategories.value = topCategories
  } catch (error) {
    console.error('获取分类列表失败', error)
  }
}

onMounted(async () => {
  try {
    const res = await api.get('/product/list', { params: { pageSize: 8, sortBy: 'sales', sortOrder: 'desc' } })
    hotProducts.value = res.data.list || []
  } catch (error) {
    console.error('获取商品列表失败', error)
  }
  loadHotCategories()
})
</script>

<style scoped>
.home {
  width: 100%;
}

.banner-section {
  width: 100%;
  margin-bottom: 20px;
}

.banner-item {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.banner-overlay {
  text-align: center;
  color: #fff;
  z-index: 1;
}

.banner-title {
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 15px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.banner-subtitle {
  font-size: 20px;
  opacity: 0.95;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.categories-section .container {
  padding: 30px 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 24px;
  font-weight: bold;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0;
}

.section-title .el-icon {
  color: var(--primary-color);
  font-size: 28px;
}

.product-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: var(--shadow);
  margin-bottom: 20px;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-hover);
}

.product-image-wrapper {
  position: relative;
  width: 100%;
  height: 240px;
  overflow: hidden;
  background: #f5f5f5;
}

.product-image {
  width: 100%;
  height: 100%;
  transition: transform 0.3s ease;
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

.product-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.product-card:hover .product-overlay {
  opacity: 1;
}

.product-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  color: #fff;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(255, 77, 79, 0.3);
}

.product-info {
  padding: 20px;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0 0 15px 0;
  height: 36px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-wrapper {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.current-price {
  font-size: 22px;
  font-weight: bold;
  color: #ff4d4f;
}

.original-price {
  font-size: 14px;
  color: var(--text-tertiary);
  text-decoration: line-through;
}

.sales-info {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--text-tertiary);
}

.sales-info .el-icon {
  font-size: 14px;
  color: var(--primary-color);
}

/* 热门分类样式 */
.categories-section {
  background: transparent;
  padding: 30px 0;
  margin-top: 0;
}

.tea-culture-section {
  background: transparent;
  padding: 2px 0 0;
}

.tea-culture-section .container {
  padding-top: 16px;
  padding-bottom: 12px;
}

.culture-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0;
  align-items: stretch;
  margin-bottom: 10px;
  min-height: 260px;
}

.culture-layout:last-child {
  margin-bottom: 0;
}

.culture-layout-reverse .culture-left {
  order: 2;
  border-radius: 0 12px 12px 0;
}

.culture-layout-reverse .culture-right {
  order: 1;
  border-radius: 12px 0 0 12px;
}

.culture-left {
  background: #fff;
  border-radius: 12px 0 0 12px;
  padding: 16px 18px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f3f5;
}

.culture-left h3 {
  font-size: 18px;
  color: var(--text-primary);
  line-height: 1.35;
  margin: 0;
}

.culture-left p {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.7;
  font-size: 13px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.culture-right {
  background: #fff;
  border-radius: 0 12px 12px 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f3f5;
  overflow: hidden;
  height: 260px;
}

.culture-hero-image {
  width: 100%;
  height: 100%;
  min-height: 260px;
  display: block;
}

.culture-hero-image :deep(img) {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.culture-left .el-button {
  align-self: flex-start;
  margin-top: 8px;
}

.culture-btn {
  color: #fff;
  background: var(--primary-color);
  border-color: var(--primary-color);
}

.culture-btn:hover,
.culture-btn:focus {
  color: #fff;
  background: var(--primary-light);
  border-color: var(--primary-light);
}

.culture-right:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
  transition: all 0.25s ease;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
}

.category-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.category-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.category-image-wrapper {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f5f5f5;
}

.category-image {
  width: 100%;
  height: 100%;
  transition: transform 0.3s ease;
}

.category-card:hover .category-image {
  transform: scale(1.1);
}

.category-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.category-card:hover .category-overlay {
  opacity: 1;
}

.category-info {
  padding: 20px;
  text-align: center;
}

.category-name {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.category-desc {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
  line-height: 1.5;
}

@media (max-width: 1200px) {
  .categories-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .culture-layout {
    grid-template-columns: 1fr;
    gap: 8px;
    min-height: 0;
  }

  .culture-left {
    border-radius: 12px;
  }

  .culture-layout-reverse .culture-left,
  .culture-layout-reverse .culture-right {
    order: initial;
    border-radius: 12px;
  }

  .culture-right {
    border-radius: 12px;
  }

  .culture-right {
    height: 200px;
    min-height: 200px;
  }

  .categories-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .culture-left {
    padding: 18px;
  }

  .categories-grid {
    grid-template-columns: 1fr;
  }
}
</style>

