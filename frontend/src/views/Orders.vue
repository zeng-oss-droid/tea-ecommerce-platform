<template>
  <div class="orders-page">
    <div class="page-header">
      <h1>我的订单</h1>
    </div>
    
    <div class="container">
      <!-- 订单筛选 -->
      <el-card class="filter-card">
        <el-radio-group v-model="statusFilter" @change="loadOrders" size="large">
          <el-radio-button :label="null">全部订单</el-radio-button>
          <el-radio-button :label="0">待支付</el-radio-button>
          <el-radio-button :label="1">待发货</el-radio-button>
          <el-radio-button :label="2">待收货</el-radio-button>
          <el-radio-button :label="3">已完成</el-radio-button>
          <el-radio-button :label="5">退款中</el-radio-button>
        </el-radio-group>
      </el-card>
      
      <!-- 订单列表 -->
      <div v-if="orders.length === 0 && !loading" class="empty-orders">
        <el-empty description="暂无订单" />
      </div>
      
      <div v-else>
        <el-card 
          v-for="order in orders" 
          :key="order.id" 
          class="order-card"
          shadow="hover"
        >
          <div class="order-header">
            <div class="order-info">
              <span class="order-no">订单号：{{ order.orderNo }}</span>
              <span class="order-time">{{ formatTime(order.createTime) }}</span>
            </div>
            <el-tag :type="getStatusType(order.status)" size="large">
              {{ getStatusText(order.status) }}
            </el-tag>
          </div>
          
          <div class="order-content">
            <div 
              v-for="item in orderItemsMap[order.id] || []" 
              :key="item.id"
              class="order-item"
            >
              <el-image 
                :src="getOrderItemImage(item)" 
                class="item-image"
                fit="cover"
                :lazy="true"
              >
                <template #error>
                  <div class="image-slot">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <div class="item-info">
                <div class="item-name" @click="goToProduct(item.productId)">{{ item.productName }}</div>
                <div class="item-specs">
                  <span>单价：¥{{ item.price }}</span>
                  <span>数量：{{ item.quantity }}</span>
                </div>
              </div>
              <div class="item-price">¥{{ item.subtotal }}</div>
            </div>
          </div>
          
          <div class="order-footer">
            <div class="order-total">
              <span>共{{ getOrderItemCount(order.id) }}件商品，合计：</span>
              <span class="total-amount">¥{{ order.payAmount }}</span>
            </div>
            <div class="order-actions">
              <el-button 
                type="primary" 
                size="default" 
                @click="viewDetail(order.id)"
              >
                查看详情
              </el-button>
              <el-button 
                v-if="order.status === 0" 
                type="danger" 
                size="default" 
                @click="cancelOrder(order.id)"
              >
                取消订单
              </el-button>
              <el-button 
                v-if="order.status === 0" 
                type="success" 
                size="default" 
                @click="payOrder(order.id)"
              >
                立即支付
              </el-button>
              <el-button 
                v-if="order.status === 2" 
                type="success" 
                size="default" 
                @click="confirmReceipt(order.id)"
              >
                确认收货
              </el-button>
              <el-button 
                v-if="(order.status === 1 || order.status === 2 || order.status === 3) && order.status !== 5 && order.status !== 6" 
                type="warning" 
                size="default" 
                @click="viewDetail(order.id)"
              >
                申请退款
              </el-button>
              <el-button 
                v-if="order.status === 3" 
                type="primary" 
                size="default" 
                @click="viewDetail(order.id)"
              >
                评价商品
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '../utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'

const router = useRouter()

const orders = ref([])
const orderItemsMap = ref({})
const statusFilter = ref(null)
const loading = ref(false)

const getStatusText = (status) => {
  const statusMap = {
    0: '待支付',
    1: '已支付',
    2: '已发货',
    3: '已完成',
    4: '已取消',
    5: '退款中',
    6: '已退款'
  }
  return statusMap[status] || '未知'
}

const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'success',
    2: 'info',
    3: 'success',
    4: 'info',
    5: 'warning',
    6: 'info'
  }
  return typeMap[status] || ''
}

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await api.get('/order/list')
    let allOrders = res.data || []
    
    // 筛选订单
    if (statusFilter.value !== null) {
      allOrders = allOrders.filter(order => order.status === statusFilter.value)
    }
    
    orders.value = allOrders
    
    // 加载订单项
    for (const order of orders.value) {
      try {
        const detailRes = await api.get(`/order/detail/${order.id}`)
        orderItemsMap.value[order.id] = detailRes.data.orderItems || []
      } catch (error) {
        console.error('获取订单详情失败', error)
      }
    }
  } catch (error) {
    console.error('获取订单列表失败', error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const getOrderItemCount = (orderId) => {
  const items = orderItemsMap.value[orderId] || []
  return items.reduce((sum, item) => sum + item.quantity, 0)
}

const getOrderItemImage = (item) => {
  if (!item || !item.productImage) {
    console.log('订单项图片为空:', item)
    return 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=200&h=200&fit=crop'
  }
  
  let imageUrl = item.productImage.trim()
  
  // 如果是JSON格式的字符串，尝试解析
  if (imageUrl.startsWith('[') && imageUrl.endsWith(']')) {
    try {
      const imgList = JSON.parse(imageUrl)
      if (Array.isArray(imgList) && imgList.length > 0) {
        imageUrl = imgList[0]
      }
    } catch (e) {
      console.error('解析图片JSON失败:', e, imageUrl)
      // 如果解析失败，尝试用正则提取URL
      const urlMatch = imageUrl.match(/https?:\/\/[^",\s]+/)
      if (urlMatch) {
        imageUrl = urlMatch[0]
      }
    }
  }
  
  // 如果图片路径是完整URL，直接返回
  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
    return imageUrl
  }
  
  // 如果是相对路径，添加基础URL
  if (imageUrl.startsWith('/uploads/') || imageUrl.startsWith('/')) {
    return `http://localhost:8080${imageUrl}`
  }
  
  // 如果是以uploads/开头但没有斜杠
  if (imageUrl.startsWith('uploads/')) {
    return `http://localhost:8080/${imageUrl}`
  }
  
  console.log('无法处理的图片路径:', imageUrl)
  return 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=200&h=200&fit=crop'
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

const goToProduct = (productId) => {
  router.push(`/product/${productId}`)
}

const payOrder = async (orderId) => {
  try {
    await api.post('/order/pay', { orderId, payType: 1 })
    ElMessage.success('支付成功')
    loadOrders()
  } catch (error) {
    ElMessage.error('支付失败')
  }
}

const viewDetail = (orderId) => {
  router.push(`/order/${orderId}`)
}

const cancelOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要取消这个订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.post(`/order/cancel/${orderId}`)
    ElMessage.success('取消成功')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

const confirmReceipt = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定已收到商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.post(`/order/confirm/${orderId}`)
    ElMessage.success('确认收货成功')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.orders-page {
  background: linear-gradient(to bottom, #f0f9ff 0%, #ffffff 100%);
  min-height: calc(100vh - 60px);
  padding-bottom: 40px;
}

.page-header {
  background: linear-gradient(135deg, #bc3823 0%, #9d2d1c 100%);
  color: #fff;
  text-align: center;
  padding: 30px 20px;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 32px;
  font-weight: bold;
  margin: 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.filter-card {
  margin-bottom: 20px;
  border-radius: 12px;
}

:deep(.filter-card .el-card__body) {
  padding: 20px;
}

.empty-orders {
  padding: 60px 0;
  text-align: center;
}

.order-card {
  margin-bottom: 20px;
  border-radius: 12px;
  transition: all 0.3s;
}

.order-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 15px;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.order-no {
  font-weight: 600;
  font-size: 16px;
  color: var(--text-primary);
}

.order-time {
  font-size: 14px;
  color: var(--text-tertiary);
}

.order-content {
  margin-bottom: 20px;
}

.order-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #f5f5f5;
}

.order-item:last-child {
  border-bottom: none;
}

.item-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  margin-right: 15px;
  border: 1px solid #f0f0f0;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.item-name {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-primary);
  cursor: pointer;
  transition: color 0.3s;
}

.item-name:hover {
  color: var(--primary-color);
}

.item-specs {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: var(--text-secondary);
}

.item-price {
  font-size: 18px;
  font-weight: 600;
  color: #ff4d4f;
  min-width: 100px;
  text-align: right;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.order-total {
  font-size: 16px;
  color: var(--text-primary);
}

.total-amount {
  font-size: 20px;
  font-weight: bold;
  color: #ff4d4f;
  margin-left: 8px;
}

.order-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
}

.image-slot .el-icon {
  font-size: 30px;
}
</style>

