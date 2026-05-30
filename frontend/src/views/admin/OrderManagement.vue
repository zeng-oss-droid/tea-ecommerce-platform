<!-- 订单管理：搜索、发货、状态更新（管理员/商家共用） -->
<template>
  <div class="order-management">
    <el-card>
      <div class="search-bar" style="margin-bottom: 20px;">
        <el-input
          v-model="keyword"
          placeholder="搜索订单号"
          style="width: 300px;"
          @keyup.enter="loadOrders"
        >
          <template #append>
            <el-button @click="loadOrders">搜索</el-button>
          </template>
        </el-input>
        <el-select v-model="statusFilter" placeholder="订单状态" style="width: 150px; margin-left: 10px;" @change="loadOrders" clearable>
          <el-option label="全部" value="" />
          <el-option label="待支付" :value="0" />
          <el-option label="已支付" :value="1" />
          <el-option label="已发货" :value="2" />
          <el-option label="已完成" :value="3" />
          <el-option label="已取消" :value="4" />
        </el-select>
      </div>
      
      <el-table :data="orders" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="orderNo" label="订单号" min-width="200" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="payAmount" label="金额" width="120">
          <template #default="{ row }">¥{{ row.payAmount }}</template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="支付方式" width="100">
          <template #default="{ row }">
            <span v-if="row.payType === 1">支付宝</span>
            <span v-else-if="row.payType === 2">微信</span>
            <span v-else-if="row.payType === 3">银行卡</span>
            <span v-else>未支付</span>
          </template>
        </el-table-column>
        <el-table-column prop="payTime" label="支付时间" width="180" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewDetail(row.id)">查看详情</el-button>
            <el-button 
              v-if="row.status === 1"
              type="success"
              size="small"
              @click="updateStatus(row.id, 2)"
            >
              发货
            </el-button>
            <el-button 
              v-if="row.status === 0"
              type="danger"
              size="small"
              @click="updateStatus(row.id, 4)"
            >
              取消订单
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadOrders"
        style="margin-top: 20px; justify-content: center;"
      />
    </el-card>
  </div>
</template>

<script setup>
/** 列表按订单号/状态筛选；商家端仅见本店订单 */
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import api from '../../utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const orders = ref([])
const keyword = ref('')
const statusFilter = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

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
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value || undefined,
      status: statusFilter.value !== '' ? statusFilter.value : undefined
    }
    const endpoint = userStore.userInfo?.role === 2 ? '/merchant/orders' : '/admin/orders'
    const res = await api.get(endpoint, { params })
    orders.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取订单列表失败', error)
  }
}

const viewDetail = (orderId) => {
  router.push(`/order/${orderId}`)
}

const updateStatus = async (orderId, status) => {
  try {
    const action = status === 2 ? '发货' : '取消订单'
    await ElMessageBox.confirm(`确定要${action}吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const endpoint = userStore.userInfo?.role === 2 ? `/merchant/orders/${orderId}/status` : `/admin/orders/${orderId}/status`
    await api.put(endpoint, { status })
    ElMessage.success('操作成功')
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
.order-management {
  padding: 0;
}
</style>

