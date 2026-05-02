<template>
  <div class="addresses-page">
    <div class="container">
      <div class="header">
        <h2>{{ isCheckout ? '确认订单' : '收货地址' }}</h2>
        <el-button type="primary" @click="showDialog = true">新增地址</el-button>
      </div>

      <!-- 订单确认信息 -->
      <el-card v-if="isCheckout" style="margin-bottom: 20px;">
        <template #header>
          <h3>订单信息</h3>
        </template>
        <el-table :data="cartItems" style="width: 100%">
          <el-table-column label="商品" width="300">
            <template #default="{ row }">
              <div style="display: flex; align-items: center;">
                <el-image :src="getProductImage(row.product)" style="width: 60px; height: 60px; margin-right: 10px;" />
                <span>{{ row.product?.name }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="单价" prop="product.price" />
          <el-table-column label="数量" prop="quantity" />
          <el-table-column label="小计">
            <template #default="{ row }">
              ¥{{ (row.product?.price * row.quantity).toFixed(2) }}
            </template>
          </el-table-column>
        </el-table>
        <div class="order-summary">
          <div class="summary-item">
            <span>商品总计：</span>
            <span class="price">¥{{ totalAmount.toFixed(2) }}</span>
          </div>
          <div class="summary-item">
            <span>运费：</span>
            <span class="price">¥0.00</span>
          </div>
          <div class="summary-item total">
            <span>实付金额：</span>
            <span class="price">¥{{ totalAmount.toFixed(2) }}</span>
          </div>
        </div>
      </el-card>

      <!-- 地址选择 -->
      <el-card>
        <template #header>
          <h3>选择收货地址</h3>
        </template>
        <el-empty v-if="addresses.length === 0" description="暂无收货地址，请先添加地址" />
        <el-radio-group v-model="selectedAddressId" v-else class="address-group">
          <div class="address-list">
            <div 
              v-for="address in addresses" 
              :key="address.id"
              :class="{ 'selected-address': selectedAddressId === address.id, 'default-address': address.isDefault === 1 }" 
              class="address-card-wrapper"
              @click="selectedAddressId = address.id"
            >
                <el-card 
                  class="address-card"
                  shadow="hover"
                  :body-style="{ padding: '16px', height: '100%', display: 'flex', flexDirection: 'column' }"
                >
                <div class="address-radio-wrapper">
                  <el-radio :label="address.id" class="address-radio">
                    <span class="radio-label">选择此地址</span>
                  </el-radio>
                </div>
                
                <div class="address-content">
                  <div class="address-header">
                    <div class="receiver-info">
                      <span class="receiver-name">{{ address.receiverName }}</span>
                      <span class="receiver-phone">{{ address.receiverPhone }}</span>
                    </div>
                    <el-tag v-if="address.isDefault === 1" type="success" size="small" class="default-tag">默认</el-tag>
                  </div>
                  
                  <div class="address-detail">
                    <div class="address-text">
                      {{ address.province }} {{ address.city }} {{ address.district }}
                    </div>
                    <div class="address-text">
                      {{ address.detailAddress }}
                    </div>
                  </div>
                </div>
                
                <div class="address-actions" @click.stop>
                  <el-button type="primary" size="default" @click="editAddress(address)">编辑</el-button>
                  <el-button v-if="address.isDefault !== 1" type="success" size="default" @click="setDefault(address.id)">设为默认</el-button>
                  <el-button type="danger" size="default" @click="deleteAddress(address.id)">删除</el-button>
                </div>
              </el-card>
            </div>
          </div>
        </el-radio-group>

        <!-- 确认订单按钮 -->
        <div v-if="isCheckout" class="checkout-actions">
          <el-button @click="router.push('/cart')">返回购物车</el-button>
          <el-button type="primary" size="large" @click="createOrder" :disabled="!selectedAddressId || cartItems.length === 0" :loading="creating">
            确认订单
          </el-button>
        </div>
      </el-card>
    </div>

    <el-dialog v-model="showDialog" :title="editingAddress ? '编辑地址' : '新增地址'" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="form.receiverName" />
        </el-form-item>
        <el-form-item label="手机号" prop="receiverPhone">
          <el-input v-model="form.receiverPhone" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="form.province" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="form.city" />
        </el-form-item>
        <el-form-item label="区县" prop="district">
          <el-input v-model="form.district" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input v-model="form.detailAddress" type="textarea" />
        </el-form-item>
        <el-form-item label="邮编" prop="postalCode">
          <el-input v-model="form.postalCode" />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="saveAddress">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()

const addresses = ref([])
const showDialog = ref(false)
const editingAddress = ref(null)
const formRef = ref(null)
const selectedAddressId = ref(null)
const cartItems = ref([])
const creating = ref(false)

const isCheckout = computed(() => route.query.from === 'cart')

const form = reactive({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  postalCode: '',
  isDefault: 0
})

const rules = {
  receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  receiverPhone: [{ required: true, pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  detailAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

const loadAddresses = async () => {
  try {
    const res = await api.get('/address/list')
    addresses.value = res.data || []
  } catch (error) {
    console.error('获取地址列表失败', error)
  }
}

const editAddress = (address) => {
  editingAddress.value = address
  Object.assign(form, address)
  showDialog.value = true
}

const saveAddress = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (editingAddress.value) {
          await api.put('/address/update', { ...form, id: editingAddress.value.id })
          ElMessage.success('更新成功')
        } else {
          await api.post('/address/add', form)
          ElMessage.success('添加成功')
        }
        showDialog.value = false
        editingAddress.value = null
        resetForm()
        loadAddresses()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

const setDefault = async (addressId) => {
  try {
    await api.post('/address/setDefault', { addressId })
    ElMessage.success('设置成功')
    loadAddresses()
  } catch (error) {
    ElMessage.error('设置失败')
  }
}

const deleteAddress = async (addressId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个地址吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.delete(`/address/delete/${addressId}`)
    ElMessage.success('删除成功')
    loadAddresses()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const resetForm = () => {
  Object.assign(form, {
    receiverName: '',
    receiverPhone: '',
    province: '',
    city: '',
    district: '',
    detailAddress: '',
    postalCode: '',
    isDefault: 0
  })
}

const getProductImage = (product) => {
  if (!product?.images) return 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=80&h=80&fit=crop'
  try {
    const imgList = JSON.parse(product.images)
    return imgList[0] || 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=80&h=80&fit=crop'
  } catch {
    const img = product.images.split(',')[0]
    return img || 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=80&h=80&fit=crop'
  }
}

const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    return sum + (item.product?.price || 0) * item.quantity
  }, 0)
})

const loadCartItems = async () => {
  if (!isCheckout.value) return
  try {
    const res = await api.get('/cart/list')
    let allCartItems = res.data || []
    
    // 如果传入了选中的商品ID，只加载选中的商品
    const selectedIds = route.query.selectedIds
    if (selectedIds) {
      const ids = selectedIds.split(',').map(id => Number(id))
      allCartItems = allCartItems.filter(item => ids.includes(item.id))
    }
    
    cartItems.value = allCartItems
    // 获取商品详情
    for (const item of cartItems.value) {
      try {
        const productRes = await api.get(`/product/detail/${item.productId}`)
        item.product = productRes.data
      } catch (error) {
        console.error('获取商品详情失败', error)
      }
    }
  } catch (error) {
    console.error('获取购物车失败', error)
  }
}

const createOrder = async () => {
  if (!selectedAddressId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  if (cartItems.value.length === 0) {
    ElMessage.warning('购物车为空')
    router.push('/cart')
    return
  }
  
  creating.value = true
  try {
    // 传递选中的购物车ID列表
    const cartIds = cartItems.value.map(item => item.id)
    const res = await api.post('/order/create', {
      addressId: selectedAddressId.value,
      remark: '',
      cartIds: cartIds
    })
    ElMessage.success('订单创建成功')
    // 跳转到订单详情页面
    router.push(`/order/${res.data.id}`)
  } catch (error) {
    ElMessage.error(error.message || '创建订单失败')
  } finally {
    creating.value = false
  }
}

onMounted(async () => {
  await loadAddresses()
  if (isCheckout.value) {
    await loadCartItems()
    // 自动选择默认地址
    const defaultAddress = addresses.value.find(addr => addr.isDefault === 1)
    if (defaultAddress) {
      selectedAddressId.value = defaultAddress.id
    } else if (addresses.value.length > 0) {
      selectedAddressId.value = addresses.value[0].id
    }
  }
})
</script>

<style scoped>
.addresses-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.address-group {
  width: 100%;
}

.address-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  width: 100%;
}

@media (max-width: 1200px) {
  .address-list {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .address-list {
    grid-template-columns: 1fr;
  }
}

.address-card-wrapper {
  width: 100%;
  transition: all 0.3s;
  margin-bottom: 0;
}

.address-card-wrapper:hover {
  transform: translateY(-4px);
}

.address-card {
  width: 100%;
  min-height: 220px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid #e4e7ed;
}

.address-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.default-address .address-card {
  border: 2px solid var(--primary-color);
}

.selected-address .address-card {
  border: 2px solid #ff4d4f;
  background: linear-gradient(135deg, #fff1f0 0%, #ffffff 100%);
  box-shadow: 0 4px 12px rgba(255, 77, 79, 0.2);
}

.address-radio-wrapper {
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.address-radio {
  width: 100%;
}

.radio-label {
  font-size: 13px;
  color: #606266;
  font-weight: 500;
}

:deep(.address-radio .el-radio__input) {
  margin-right: 6px;
}

:deep(.address-radio .el-radio__label) {
  padding-left: 0;
}

.address-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 100px;
  margin-bottom: 16px;
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  gap: 10px;
}

.receiver-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
}

.receiver-name {
  font-weight: 600;
  font-size: 16px;
  color: #303133;
  line-height: 1.4;
}

.receiver-phone {
  color: #909399;
  font-size: 14px;
  line-height: 1.4;
}

.default-tag {
  flex-shrink: 0;
}

.address-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.address-text {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-all;
  overflow-wrap: break-word;
}

.address-actions {
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  flex-wrap: wrap;
}

.selected-address .address-actions {
  border-top-color: #ffccc7;
}

:deep(.address-actions .el-button) {
  flex: 1;
  min-width: 70px;
  height: 32px;
  font-size: 13px;
  font-weight: 500;
}

.order-summary {
  margin-top: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  text-align: right;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
}

.summary-item.total {
  font-size: 18px;
  font-weight: bold;
  padding-top: 10px;
  border-top: 1px solid #e4e7ed;
}

.summary-item .price {
  color: #f56c6c;
  font-weight: bold;
}

.checkout-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding: 20px;
  background-color: #f5f7fa;
}
</style>

