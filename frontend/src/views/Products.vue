<!-- 商品列表页：左侧分类筛选 + 搜索排序 + 分页网格 -->
<template>
  <div class="products-page">
    <div class="page-header">
      <h1>商品中心</h1>
      <p>精选好茶，品质保证</p>
    </div>
    
    <div class="container">
      <el-row :gutter="24">
        <el-col :span="5">
          <el-card class="category-card" shadow="never">
            <template #header>
              <div class="card-header">
                <el-icon><Menu /></el-icon>
                <span>商品分类</span>
              </div>
            </template>
            <el-menu 
              :default-active="activeCategory" 
              @select="handleCategorySelect"
              class="category-menu"
            >
              <el-menu-item index="0">
                <el-icon><Goods /></el-icon>
                <span>全部商品</span>
              </el-menu-item>
              <el-menu-item v-for="cat in categories" :key="cat.id" :index="String(cat.id)">
                <span>{{ cat.name }}</span>
              </el-menu-item>
            </el-menu>
          </el-card>
        </el-col>
        
        <el-col :span="19">
          <!-- 搜索和排序 -->
          <div class="toolbar">
            <el-input
              v-model="query.keyword"
              placeholder="搜索商品名称、描述..."
              class="search-input"
              @keyup.enter="loadProducts"
              clearable
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
              <template #append>
                <el-button type="primary" @click="loadProducts">搜索</el-button>
              </template>
            </el-input>
            <el-select 
              v-model="query.sortBy" 
              placeholder="排序方式" 
              class="sort-select"
              @change="loadProducts"
              clearable
            >
              <el-option label="默认排序" value="" />
              <el-option label="价格从低到高" value="price">
                <span>价格 <el-icon><ArrowUp /></el-icon></span>
              </el-option>
              <el-option label="价格从高到低" value="price-desc">
                <span>价格 <el-icon><ArrowDown /></el-icon></span>
              </el-option>
              <el-option label="销量最高" value="sales" />
            </el-select>
          </div>

          <!-- 商品列表 -->
          <div class="products-grid">
            <div 
              v-for="product in products" 
              :key="product.id"
              class="product-card"
              @click="goToDetail(product.id)"
            >
              <div class="product-image-wrapper">
                <el-image 
                  :src="getProductImage(product.images)" 
                  fit="cover"
                  class="product-image"
                  :lazy="true"
                />
                <div class="product-overlay">
                  <el-button type="primary" size="small" @click.stop="addToCart(product.id)">
                    <el-icon><ShoppingCart /></el-icon>
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
          </div>

          <!-- 分页 -->
          <div class="pagination-wrapper">
            <el-pagination
              v-model:current-page="query.pageNum"
              v-model:page-size="query.pageSize"
              :total="total"
              layout="total, prev, pager, next, jumper"
              @current-change="loadProducts"
              :page-sizes="[12, 24, 36, 48]"
              @size-change="loadProducts"
            />
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
/** 对接 /product/list、/category/list；支持 URL query.categoryId 初始分类 */
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useCartStore } from '../stores/cart'
import { toast } from '../utils/message'
import api from '../utils/api'
import { ElMessage } from 'element-plus'
import { Menu, Goods, Search, ArrowUp, ArrowDown, ShoppingCart, TrendCharts } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()

const categories = ref([])
const activeCategory = ref(route.query.categoryId ? String(route.query.categoryId) : '0')
const products = ref([])
const total = ref(0)

const query = reactive({
  keyword: '',
  categoryId: route.query.categoryId ? Number(route.query.categoryId) : null,
  pageNum: 1,
  pageSize: 12,
  sortBy: '',
  sortOrder: 'asc'
})

/** 解析 images 字段（JSON 数组或逗号分隔），取首图 */
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

const handleCategorySelect = (categoryId) => {
  activeCategory.value = categoryId
  query.categoryId = categoryId === '0' ? null : Number(categoryId)
  query.pageNum = 1
  loadProducts()
}

/** 拉取分页商品；price-desc 转为 sortBy=price & sortOrder=desc */
const loadProducts = async () => {
  try {
    const params = { ...query }
    if (params.sortBy === 'price-desc') {
      params.sortBy = 'price'
      params.sortOrder = 'desc'
    } else if (params.sortBy === 'price') {
      params.sortOrder = 'asc'
    } else if (params.sortBy === 'sales') {
      params.sortOrder = 'desc'
    } else {
      params.sortOrder = undefined
    }
    const res = await api.get('/product/list', { params })
    products.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取商品列表失败', error)
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
  try {
    await cartStore.addItem(productId, 1)
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

onMounted(async () => {
  try {
    const res = await api.get('/category/list')
    categories.value = res.data || []
  } catch (error) {
    console.error('获取分类列表失败', error)
  }
  loadProducts()
})
</script>

<style scoped>
.products-page {
  background: linear-gradient(to bottom, #f0f9ff 0%, #ffffff 100%);
  min-height: calc(100vh - 60px);
  padding-bottom: 40px;
}

.page-header {
  background: linear-gradient(135deg, #bc3823 0%, #9d2d1c 100%);
  color: #fff;
  text-align: center;
  padding: 40px 20px;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 36px;
  font-weight: bold;
  margin: 0 0 10px 0;
}

.page-header p {
  font-size: 16px;
  opacity: 0.95;
  margin: 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.category-card {
  border-radius: 12px;
  box-shadow: var(--shadow);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: var(--text-primary);
}

.category-menu {
  border: none;
}

:deep(.category-menu .el-menu-item) {
  height: 48px;
  line-height: 48px;
  border-radius: 6px;
  margin-bottom: 4px;
  transition: all 0.3s;
}

:deep(.category-menu .el-menu-item:hover) {
  background: #fef5f3;
  color: var(--primary-color);
}


.toolbar {
  display: flex;
  gap: 15px;
  margin-bottom: 24px;
  align-items: center;
}

.search-input {
  flex: 1;
  max-width: 400px;
}

.sort-select {
  width: 180px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.product-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: var(--shadow);
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-hover);
}

.product-image-wrapper {
  position: relative;
  width: 100%;
  height: 220px;
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
  padding: 16px;
}

.product-name {
  font-size: 15px;
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
  margin: 0 0 12px 0;
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
  font-size: 20px;
  font-weight: bold;
  color: #ff4d4f;
}

.original-price {
  font-size: 13px;
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

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 30px 0;
}

:deep(.el-pagination) {
  --el-pagination-button-color: var(--primary-color);
  --el-pagination-hover-color: var(--primary-light);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .products-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
  }
  
  .toolbar {
    flex-direction: column;
  }
  
  .search-input {
    max-width: 100%;
  }
}
</style>

