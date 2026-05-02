<template>
  <div class="order-detail-page">
    <div class="breadcrumb-wrapper">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/orders' }">我的订单</el-breadcrumb-item>
        <el-breadcrumb-item>订单详情</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    
    <div class="container" v-if="order">
      <!-- 订单状态卡片 -->
      <el-card class="status-card">
        <div class="status-content">
          <div class="status-icon">
            <el-icon v-if="order.status === 0" size="40"><Clock /></el-icon>
            <el-icon v-else-if="order.status === 1" size="40"><CircleCheck /></el-icon>
            <el-icon v-else-if="order.status === 2" size="40"><Van /></el-icon>
            <el-icon v-else-if="order.status === 3" size="40"><Select /></el-icon>
            <el-icon v-else-if="order.status === 5" size="40"><Warning /></el-icon>
            <el-icon v-else size="40"><InfoFilled /></el-icon>
          </div>
          <div class="status-info">
            <div class="status-text">
              <el-tag :type="getStatusType(order.status)" size="large">
                {{ getStatusText(order.status) }}
              </el-tag>
            </div>
            <div class="order-no">订单号：{{ order.orderNo }}</div>
          </div>
        </div>
      </el-card>
      
      <!-- 订单信息卡片 -->
      <el-card class="info-card">
        <template #header>
          <div class="card-header">
            <h3>订单信息</h3>
          </div>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单金额">
            <span class="amount-text">¥{{ order.payAmount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="支付方式">
            <span v-if="order.payType === 1">支付宝</span>
            <span v-else-if="order.payType === 2">微信</span>
            <span v-else-if="order.payType === 3">银行卡</span>
            <span v-else>未支付</span>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatTime(order.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="支付时间" v-if="order.payTime">{{ order.payTime }}</el-descriptions-item>
          <el-descriptions-item label="备注" v-if="order.remark" :span="2">{{ order.remark }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
      
      <!-- 商品信息卡片 -->
      <el-card class="products-card">
        <template #header>
          <div class="card-header">
            <h3>商品信息</h3>
            <span class="product-count">共{{ orderItems.length }}件商品</span>
          </div>
        </template>
        <div class="products-list">
          <div 
            v-for="item in orderItems" 
            :key="item.id"
            class="product-item"
            @click="goToProduct(item.productId)"
          >
            <el-image 
              :src="getOrderItemImage(item)" 
              class="product-image"
              fit="cover"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div class="product-info">
              <div class="product-name">{{ item.productName }}</div>
              <div class="product-specs">
                <span>单价：¥{{ item.price }}</span>
                <span>数量：{{ item.quantity }}</span>
              </div>
            </div>
            <div class="product-price">¥{{ item.subtotal }}</div>
          </div>
        </div>
        <div class="order-summary">
          <div class="summary-row">
            <span>商品总计：</span>
            <span>¥{{ order.totalAmount }}</span>
          </div>
          <div class="summary-row" v-if="order.discountAmount > 0">
            <span>优惠金额：</span>
            <span class="discount">-¥{{ order.discountAmount }}</span>
          </div>
          <div class="summary-row total">
            <span>实付金额：</span>
            <span class="total-amount">¥{{ order.payAmount }}</span>
          </div>
        </div>
      </el-card>
      
      <!-- 操作按钮 -->
      <el-card class="actions-card">
        <div class="actions">
          <el-button @click="router.push('/orders')">返回订单列表</el-button>
          <el-button 
            v-if="order.status === 0" 
            type="primary" 
            size="large"
            @click="payOrder"
          >
            <el-icon><Wallet /></el-icon>
            立即支付
          </el-button>
          <el-button 
            v-if="order.status === 0" 
            type="danger" 
            size="large"
            @click="cancelOrder"
          >
            <el-icon><Close /></el-icon>
            取消订单
          </el-button>
          <el-button 
            v-if="order.status === 2" 
            type="success" 
            size="large"
            @click="confirmReceipt"
          >
            <el-icon><CircleCheck /></el-icon>
            确认收货
          </el-button>
          <el-button 
            v-if="(order.status === 1 || order.status === 2 || order.status === 3) && order.status !== 5 && order.status !== 6" 
            type="warning" 
            size="large"
            @click="showRefundDialog = true"
          >
            <el-icon><Money /></el-icon>
            申请退款
          </el-button>
          <el-button 
            v-if="order.status === 3" 
            type="primary" 
            size="large"
            @click="showReviewDialog = true"
          >
            <el-icon><Star /></el-icon>
            评价商品
          </el-button>
        </div>
      </el-card>
      
      <!-- 退款信息 -->
      <el-card v-if="refunds.length > 0" class="refunds-card" style="margin-bottom: 20px;">
        <template #header>
          <h3>退款信息</h3>
        </template>
        <div v-for="refund in refunds" :key="refund.id" class="refund-item">
          <div class="refund-header">
            <div>
              <div class="refund-no">退款单号：{{ refund.refundNo }}</div>
              <div class="refund-status">
                <el-tag :type="getRefundStatusType(refund.status)">
                  {{ getRefundStatusText(refund.status) }}
                </el-tag>
              </div>
            </div>
            <div class="refund-amount">退款金额：¥{{ refund.refundAmount }}</div>
          </div>
          <div class="refund-info">
            <div>退款类型：{{ refund.type === 1 ? '仅退款' : '退货退款' }}</div>
            <div>退款原因：{{ refund.reason }}</div>
            <div v-if="refund.description">退款说明：{{ refund.description }}</div>
            <div v-if="refund.logisticsNo">退货快递单号：{{ refund.logisticsNo }}</div>
            <div v-if="refund.rejectReason" class="reject-reason">拒绝原因：{{ refund.rejectReason }}</div>
          </div>
          <div class="refund-actions" v-if="refund.type === 2 && refund.status === 1">
            <el-button type="primary" size="small" @click="openLogisticsDialog(refund.id)">
              {{ refund.logisticsNo ? '修改快递单号' : '填写快递单号' }}
            </el-button>
          </div>
        </div>
      </el-card>
      
      <!-- 评论列表 -->
      <el-card v-if="order.status === 3 && reviews.length > 0" class="reviews-card">
        <template #header>
          <h3>我的评价</h3>
        </template>
        <div v-for="review in reviews" :key="review.id" class="review-item">
          <div class="review-header">
            <div class="review-product">
              <el-image :src="getOrderItemImage({ productImage: review.productImage })" style="width: 60px; height: 60px; margin-right: 10px;" />
              <div>
                <div class="product-name">{{ review.productName }}</div>
                <el-rate v-model="review.rating" disabled show-score text-color="#ff9900" />
              </div>
            </div>
            <div class="review-time">{{ formatTime(review.createTime) }}</div>
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
      </el-card>
    </div>
    
    <!-- 退款申请对话框 -->
    <el-dialog v-model="showRefundDialog" title="申请退款" width="600px">
      <el-form :model="refundForm" :rules="refundRules" ref="refundFormRef" label-width="100px">
        <el-form-item label="退款类型" prop="type">
          <el-radio-group v-model="refundForm.type">
            <el-radio :label="1">仅退款</el-radio>
            <el-radio :label="2">退货退款</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="退款原因" prop="reason">
          <el-select v-model="refundForm.reason" placeholder="请选择退款原因" style="width: 100%;">
            <el-option label="不想要了" value="不想要了" />
            <el-option label="商品质量问题" value="商品质量问题" />
            <el-option label="商品与描述不符" value="商品与描述不符" />
            <el-option label="收到商品损坏" value="收到商品损坏" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="退款说明" prop="description">
          <el-input v-model="refundForm.description" type="textarea" :rows="4" placeholder="请详细说明退款原因" />
        </el-form-item>
        <el-alert
          v-if="refundForm.type === 2"
          title="提示"
          type="info"
          :closable="false"
          style="margin-bottom: 20px;"
        >
          <template #default>
            <div>退货退款需要您先寄回商品，管理员同意后您需要填写退货快递单号</div>
          </template>
        </el-alert>
      </el-form>
      <template #footer>
        <el-button @click="showRefundDialog = false">取消</el-button>
        <el-button type="primary" @click="submitRefund">提交申请</el-button>
      </template>
    </el-dialog>
    
    <!-- 填写快递单号对话框 -->
    <el-dialog v-model="showLogisticsDialog" title="填写退货快递单号" width="500px">
      <el-form :model="logisticsForm" :rules="logisticsRules" ref="logisticsFormRef" label-width="100px">
        <el-form-item label="快递单号" prop="logisticsNo">
          <el-input v-model="logisticsForm.logisticsNo" placeholder="请输入退货快递单号" />
        </el-form-item>
        <el-alert
          title="提示"
          type="info"
          :closable="false"
          style="margin-top: 10px;"
        >
          <template #default>
            <div>请填写您寄回商品的快递单号，管理员确认收货后将完成退款</div>
          </template>
        </el-alert>
      </el-form>
      <template #footer>
        <el-button @click="showLogisticsDialog = false">取消</el-button>
        <el-button type="primary" @click="submitLogistics">提交</el-button>
      </template>
    </el-dialog>
    
    <!-- 评论对话框 -->
    <el-dialog v-model="showReviewDialog" title="评价商品" width="700px">
      <div v-for="item in orderItems" :key="item.id" class="review-item-form">
        <div class="review-item-header">
          <el-image :src="getOrderItemImage(item)" style="width: 80px; height: 80px; margin-right: 15px; border-radius: 8px;" />
          <div class="review-item-info">
            <div class="product-name">{{ item.productName }}</div>
            <div v-if="!item.reviewed">
              <el-rate v-model="item.rating" :max="5" show-score text-color="#ff9900" style="margin: 10px 0;" />
              <el-input 
                v-model="item.reviewContent" 
                type="textarea" 
                :rows="3" 
                placeholder="请输入评价内容（选填）"
                style="margin-bottom: 10px;"
              />
              <el-button type="primary" size="small" @click="submitReview(item)">提交评价</el-button>
            </div>
            <div v-else class="reviewed-tip">
              <el-icon><CircleCheck /></el-icon>
              已评价
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Clock, 
  CircleCheck, 
  Van, 
  Select, 
  Warning, 
  InfoFilled,
  Wallet,
  Close,
  Money,
  Star,
  ChatDotRound,
  Picture
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const order = ref(null)
const orderItems = ref([])
const reviews = ref([])
const refunds = ref([])
const showRefundDialog = ref(false)
const showReviewDialog = ref(false)
const showLogisticsDialog = ref(false)
const refundFormRef = ref(null)
const logisticsFormRef = ref(null)
const currentRefund = ref(null)

const refundForm = ref({
  type: 1,
  reason: '',
  description: ''
})

const refundRules = {
  type: [{ required: true, message: '请选择退款类型', trigger: 'change' }],
  reason: [{ required: true, message: '请选择退款原因', trigger: 'change' }]
}

const logisticsForm = ref({
  logisticsNo: ''
})

const logisticsRules = {
  logisticsNo: [{ required: true, message: '请输入快递单号', trigger: 'blur' }]
}

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

const loadOrderDetail = async () => {
  try {
    const res = await api.get(`/order/detail/${route.params.id}`)
    order.value = res.data.order
    orderItems.value = res.data.orderItems || []
    
    // 初始化评论相关数据
    orderItems.value.forEach(item => {
      item.rating = 5
      item.reviewContent = ''
      item.reviewed = false
    })
    
    // 加载评论
    if (order.value.status === 3) {
      loadReviews()
    }
    
    // 加载退款信息
    loadRefunds()
  } catch (error) {
    ElMessage.error('获取订单详情失败')
    router.push('/orders')
  }
}

const loadReviews = async () => {
  try {
    const res = await api.get(`/review/order/${route.params.id}`)
    reviews.value = res.data || []
    
    // 标记已评价的商品
    reviews.value.forEach(review => {
      const item = orderItems.value.find(i => i.id === review.orderItemId)
      if (item) {
        item.reviewed = true
      }
    })
  } catch (error) {
    console.error('获取评论失败', error)
  }
}

const loadRefunds = async () => {
  try {
    const res = await api.get('/refund/my')
    refunds.value = (res.data || []).filter(r => r.orderId === order.value?.id)
  } catch (error) {
    console.error('获取退款信息失败', error)
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

const goToProduct = (productId) => {
  router.push(`/product/${productId}`)
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

const getRefundStatusText = (status) => {
  const statusMap = {
    0: '待处理',
    1: '已同意',
    2: '已拒绝',
    3: '退款中',
    4: '已退款',
    5: '已取消'
  }
  return statusMap[status] || '未知'
}

const getRefundStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'success',
    2: 'danger',
    3: 'info',
    4: 'success',
    5: 'info'
  }
  return typeMap[status] || ''
}

const payOrder = async () => {
  try {
    await api.post('/order/pay', { orderId: order.value.id, payType: 1 })
    ElMessage.success('支付成功')
    loadOrderDetail()
  } catch (error) {
    ElMessage.error('支付失败')
  }
}

const cancelOrder = async () => {
  try {
    await ElMessageBox.confirm('确定要取消这个订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.post(`/order/cancel/${order.value.id}`)
    ElMessage.success('取消成功')
    loadOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

const confirmReceipt = async () => {
  try {
    await ElMessageBox.confirm('确定已收到商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.post(`/order/confirm/${order.value.id}`)
    ElMessage.success('确认收货成功')
    loadOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const submitRefund = async () => {
  if (!refundFormRef.value) return
  await refundFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await api.post('/refund/create', {
          orderId: order.value.id,
          type: refundForm.value.type,
          reason: refundForm.value.reason,
          description: refundForm.value.description
        })
        ElMessage.success('退款申请提交成功')
        showRefundDialog.value = false
        refundForm.value = { type: 1, reason: '', description: '' }
        loadOrderDetail()
      } catch (error) {
        ElMessage.error(error.message || '提交失败')
      }
    }
  })
}

const submitReview = async (item) => {
  if (!item.rating || item.rating < 1) {
    ElMessage.warning('请选择评分')
    return
  }
  
  try {
    await api.post('/review/create', {
      orderId: order.value.id,
      orderItemId: item.id,
      productId: item.productId,
      rating: item.rating,
      content: item.reviewContent || ''
    })
    ElMessage.success('评价成功')
    item.reviewed = true
    loadReviews()
  } catch (error) {
    ElMessage.error(error.message || '评价失败')
  }
}

const openLogisticsDialog = async (refundId) => {
  try {
    const res = await api.get(`/refund/detail/${refundId}`)
    currentRefund.value = res.data
    if (currentRefund.value.logisticsNo) {
      logisticsForm.value.logisticsNo = currentRefund.value.logisticsNo
    } else {
      logisticsForm.value.logisticsNo = ''
    }
    showLogisticsDialog.value = true
  } catch (error) {
    ElMessage.error('获取退款信息失败')
  }
}

const submitLogistics = async () => {
  if (!logisticsFormRef.value) return
  await logisticsFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await api.post(`/refund/update-logistics/${currentRefund.value.id}`, {
          logisticsNo: logisticsForm.value.logisticsNo
        })
        ElMessage.success('快递单号提交成功')
        showLogisticsDialog.value = false
        logisticsForm.value.logisticsNo = ''
        currentRefund.value = null
        loadOrderDetail()
      } catch (error) {
        ElMessage.error(error.message || '提交失败')
      }
    }
  })
}

onMounted(() => {
  loadOrderDetail()
})
</script>

<style scoped>
.order-detail-page {
  background: linear-gradient(to bottom, #f0f9ff 0%, #ffffff 100%);
  min-height: calc(100vh - 60px);
  padding-bottom: 40px;
}

.breadcrumb-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.status-card {
  margin-bottom: 20px;
  border-radius: 12px;
  background: linear-gradient(135deg, #fef5f3 0%, #ffffff 100%);
  border: 1px solid #f5b5a9;
}

.status-content {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 10px 0;
}

.status-icon {
  color: var(--primary-color);
}

.status-info {
  flex: 1;
}

.status-text {
  margin-bottom: 8px;
}

.order-no {
  font-size: 14px;
  color: var(--text-secondary);
}

.info-card,
.products-card,
.actions-card,
.reviews-card {
  margin-bottom: 20px;
  border-radius: 12px;
  box-shadow: var(--shadow);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
}

.product-count {
  font-size: 14px;
  color: var(--text-secondary);
}

.amount-text {
  font-size: 20px;
  font-weight: bold;
  color: #ff4d4f;
}

.products-list {
  margin-bottom: 20px;
}

.product-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  margin-bottom: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.product-item:hover {
  border-color: var(--primary-color);
  box-shadow: 0 2px 8px rgba(188, 56, 35, 0.1);
  transform: translateX(5px);
}

.product-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  margin-right: 20px;
  border: 1px solid #f0f0f0;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  transition: color 0.3s;
  cursor: pointer;
}

.product-item:hover .product-name {
  color: var(--primary-color);
}

.product-specs {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: var(--text-secondary);
}

.product-price {
  font-size: 20px;
  font-weight: bold;
  color: #ff4d4f;
  min-width: 120px;
  text-align: right;
}

.order-summary {
  background: #fafafa;
  padding: 20px;
  border-radius: 8px;
  margin-top: 20px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  font-size: 15px;
  color: var(--text-primary);
}

.summary-row.total {
  border-top: 2px solid #e4e7ed;
  margin-top: 10px;
  padding-top: 15px;
  font-size: 18px;
  font-weight: 600;
}

.discount {
  color: var(--primary-color);
}

.total-amount {
  font-size: 24px;
  font-weight: bold;
  color: #ff4d4f;
}

.actions-card {
  background: #fafafa;
}

.actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
  flex-wrap: wrap;
}

.actions .el-button {
  min-width: 120px;
}

.review-item {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.review-product {
  display: flex;
  align-items: center;
  flex: 1;
}

.review-product .product-name {
  font-weight: 600;
  font-size: 16px;
  margin-bottom: 8px;
  cursor: default;
}

.review-time {
  color: #909399;
  font-size: 14px;
}

.review-content {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 10px;
  font-size: 15px;
}

.review-reply {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 6px;
  border-left: 3px solid var(--primary-color);
  margin-top: 15px;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  color: var(--primary-color);
  margin-bottom: 8px;
  font-size: 14px;
}

.reply-content {
  color: var(--text-primary);
  line-height: 1.6;
  font-size: 14px;
  margin-bottom: 8px;
}

.reply-time {
  font-size: 12px;
  color: var(--text-tertiary);
}

.review-item-form {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.review-item-form:last-child {
  border-bottom: none;
}

.review-item-header {
  display: flex;
  align-items: flex-start;
}

.review-item-info {
  flex: 1;
}

.review-item-info .product-name {
  font-weight: 600;
  font-size: 16px;
  margin-bottom: 10px;
}

.reviewed-tip {
  color: #67c23a;
  font-size: 14px;
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 退款信息样式 */
.refunds-card {
  border-radius: 12px;
}

.refund-item {
  padding: 20px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  margin-bottom: 15px;
  background: #fff;
}

.refund-item:last-child {
  margin-bottom: 0;
}

.refund-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.refund-no {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.refund-status {
  margin-top: 8px;
}

.refund-amount {
  font-size: 18px;
  font-weight: bold;
  color: #ff4d4f;
}

.refund-info {
  color: var(--text-secondary);
  font-size: 14px;
  line-height: 1.8;
  margin-bottom: 15px;
}

.refund-info > div {
  margin-bottom: 8px;
}

.reject-reason {
  color: #ff4d4f;
  font-weight: 500;
}

.refund-actions {
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
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
