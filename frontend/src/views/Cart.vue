<!-- 购物车：勾选结算、改数量、批量删除；需登录 -->
<template>
  <div class="cart-page">
    <div class="cart-container">
      <div class="page-header">
        <h2>购物车</h2>
        <p>管理你的心仪茶品，确认后快速结算</p>
      </div>

      <el-card v-loading="loading" class="cart-card" shadow="never" v-if="cartList.length === 0">
        <el-empty description="购物车为空">
          <el-button type="primary" @click="$router.push('/products')">去逛逛</el-button>
        </el-empty>
      </el-card>
      <el-card v-loading="loading" class="cart-card" shadow="never" v-else>
        <el-table
          :data="cartList" 
          class="cart-table"
          @selection-change="handleSelectionChange"
          ref="tableRef"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column label="商品" min-width="330">
            <template #default="{ row }">
              <div class="product-cell">
                <el-image :src="getProductImage(row.product)" class="product-image" fit="cover" />
                <div class="product-meta">
                  <span class="product-name">{{ row.product?.name }}</span>
                  <span class="product-stock">库存 {{ row.product?.stock ?? 0 }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="单价" width="130">
            <template #default="{ row }">
              ¥{{ row.product?.price?.toFixed(2) || '0.00' }}
            </template>
          </el-table-column>
          <el-table-column label="数量" min-width="170">
            <template #default="{ row }">
              <div class="quantity-wrap">
                <el-input-number v-model="row.quantity" :min="1" :max="row.product?.stock || 999" @change="updateQuantity(row)" />
              </div>
            </template>
          </el-table-column>
          <el-table-column label="小计" width="150">
            <template #default="{ row }">
              ¥{{ (row.product?.price * row.quantity).toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="danger" plain size="small" @click="removeItem(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="cart-footer">
          <div class="footer-left">
            <el-button @click="toggleSelectAll">{{ isAllSelected ? '取消全选' : '全选' }}</el-button>
            <el-button type="danger" plain @click="removeSelected" :disabled="selectedItems.length === 0">删除选中</el-button>
          </div>
          <div class="footer-right">
            <div class="total">
              已选 <span class="selected-count">{{ selectedItems.length }}</span> 件商品，合计：
              <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
            </div>
            <el-button type="primary" size="large" @click="checkout" :disabled="selectedItems.length === 0">
              去结算
            </el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
/** 列表项附带 product 详情（并发请求）；结算跳转地址页并带 cartIds */
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import api from '../utils/api'
import { useCartStore } from '../stores/cart'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const cartStore = useCartStore()

const cartList = ref([])
const selectedItems = ref([])
const tableRef = ref(null)
const loading = ref(false)

const totalPrice = computed(() => {
  return selectedItems.value.reduce((sum, item) => {
    return sum + (item.product?.price || 0) * item.quantity
  }, 0)
})

const isAllSelected = computed(() => {
  return cartList.value.length > 0 && selectedItems.value.length === cartList.value.length
})

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

const loadCart = async () => {
  loading.value = true
  try {
    const res = await api.get('/cart/list')
    const list = res.data || []
    // 并发获取详情，避免逐个请求导致页面卡顿
    const detailList = await Promise.all(
      list.map(async (item) => {
        try {
          const productRes = await api.get(`/product/detail/${item.productId}`)
          return { ...item, product: productRes.data }
        } catch (error) {
          console.error('获取商品详情失败', error)
          return item
        }
      })
    )
    cartList.value = detailList
    selectedItems.value = []
    cartStore.updateCount(detailList.length)
  } catch (error) {
    console.error('获取购物车失败', error)
  } finally {
    loading.value = false
  }
}

const updateQuantity = async (item) => {
  try {
    await api.put('/cart/update', { cartId: item.id, quantity: item.quantity })
  } catch (error) {
    ElMessage.error('更新失败')
    loadCart()
  }
}

const handleSelectionChange = (selection) => {
  selectedItems.value = selection
}

const toggleSelectAll = () => {
  if (isAllSelected.value) {
    tableRef.value?.clearSelection()
  } else {
    cartList.value.forEach(item => {
      tableRef.value?.toggleRowSelection(item, true)
    })
  }
}

const removeItem = async (cartId) => {
  try {
    await api.delete(`/cart/remove/${cartId}`)
    ElMessage.success('删除成功')
    // 从选中列表中移除
    selectedItems.value = selectedItems.value.filter(item => item.id !== cartId)
    loadCart()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const removeSelected = async () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请先选择要删除的商品')
    return
  }
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedItems.value.length} 件商品吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 批量删除
    const deletePromises = selectedItems.value.map(item => 
      api.delete(`/cart/remove/${item.id}`)
    )
    await Promise.all(deletePromises)
    
    ElMessage.success('删除成功')
    selectedItems.value = []
    loadCart()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

/** 携带选中 cartId 跳转地址页 checkout=1 */
const checkout = () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请先选择要结算的商品')
    return
  }
  if (cartList.value.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }
  // 将选中的商品ID传递给地址页面
  const selectedIds = selectedItems.value.map(item => item.id).join(',')
  router.push(`/addresses?from=cart&selectedIds=${selectedIds}`)
}

onMounted(() => {
  loadCart()
})
</script>

<style scoped>
.cart-page {
  max-width: 1240px;
  margin: 0 auto;
  padding: 24px 20px 32px;
}

.cart-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-header h2 {
  margin: 0;
  font-size: 30px;
  color: var(--text-primary);
}

.page-header p {
  margin: 6px 0 0;
  color: var(--text-secondary);
  font-size: 14px;
}

.cart-card {
  border: none;
  border-radius: 14px;
  box-shadow: var(--shadow);
}

.cart-table :deep(.el-table__cell) {
  padding: 14px 0;
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 10px;
  flex-shrink: 0;
}

.product-meta {
  display: flex;
  flex-direction: column;
}

.product-name {
  color: var(--text-primary);
  font-weight: 600;
}

.product-stock {
  font-size: 13px;
  color: var(--text-tertiary);
  margin-top: 4px;
}

.quantity-wrap {
  min-width: 150px;
}

.quantity-wrap :deep(.el-input-number) {
  width: 140px;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding: 16px 20px;
  background: #faf7f3;
  border-radius: 12px;
}

.footer-left {
  display: flex;
  gap: 10px;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.total {
  font-size: 16px;
  color: #666;
}

.selected-count {
  color: var(--primary-color);
  font-weight: bold;
}

.total-price {
  font-size: 24px;
  color: var(--danger-color);
  font-weight: bold;
  margin-left: 5px;
}

@media (max-width: 768px) {
  .cart-page {
    padding: 16px 12px 24px;
  }

  .page-header h2 {
    font-size: 26px;
  }

  .cart-footer {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .footer-left,
  .footer-right {
    justify-content: space-between;
  }

  .footer-right {
    flex-wrap: wrap;
  }
}
</style>

