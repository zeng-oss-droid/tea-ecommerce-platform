<!-- 退款审核与处理（管理员全站 / 商家本店） -->
<template>
  <div class="refund-management">
    <el-card>
      <div class="toolbar">
        <el-input
          v-model="keyword"
          placeholder="搜索订单号或退款单号"
          style="width: 300px;"
          clearable
          @keyup.enter="loadRefunds"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="status" placeholder="退款状态" style="width: 150px; margin-left: 10px;" clearable @change="loadRefunds">
          <el-option label="待处理" :value="0" />
          <el-option label="已同意" :value="1" />
          <el-option label="已拒绝" :value="2" />
          <el-option label="退款中" :value="3" />
          <el-option label="已退款" :value="4" />
          <el-option label="已取消" :value="5" />
        </el-select>
        <el-button type="primary" @click="loadRefunds" style="margin-left: 10px;">搜索</el-button>
      </div>
      
      <el-table :data="refunds" style="width: 100%; margin-top: 20px;" v-loading="loading">
        <el-table-column prop="refundNo" label="退款单号" min-width="180" />
        <el-table-column prop="orderNo" label="订单号" min-width="180" />
        <el-table-column prop="refundAmount" label="退款金额" width="120">
          <template #default="{ row }">¥{{ row.refundAmount }}</template>
        </el-table-column>
        <el-table-column prop="type" label="退款类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'info' : 'warning'">
              {{ row.type === 1 ? '仅退款' : '退货退款' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="退款原因" min-width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="180" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 0" 
              type="success" 
              size="small" 
              @click="handleApprove(row)"
            >
              同意
            </el-button>
            <el-button 
              v-if="row.status === 0" 
              type="danger" 
              size="small" 
              @click="handleReject(row)"
            >
              拒绝
            </el-button>
            <el-button 
              v-if="row.status === 3 && row.type === 2 && row.logisticsNo" 
              type="success" 
              size="small" 
              @click="handleConfirmReceive(row)"
            >
              确认收货
            </el-button>
            <el-button 
              v-if="row.status === 1 && row.type === 1" 
              type="primary" 
              size="small" 
              @click="handleComplete(row)"
            >
              完成退款
            </el-button>
            <el-button 
              v-if="row.status === 3 && row.type === 1" 
              type="primary" 
              size="small" 
              @click="handleComplete(row)"
            >
              完成退款
            </el-button>
            <el-button 
              type="info" 
              size="small" 
              @click="showDetail(row)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadRefunds"
        style="margin-top: 20px; justify-content: center;"
      />
    </el-card>
    
    <!-- 详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="退款详情" width="600px">
      <div v-if="currentRefund">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="退款单号">{{ currentRefund.refundNo }}</el-descriptions-item>
          <el-descriptions-item label="订单号">{{ currentRefund.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="退款金额">¥{{ currentRefund.refundAmount }}</el-descriptions-item>
          <el-descriptions-item label="退款类型">
            {{ currentRefund.type === 1 ? '仅退款' : '退货退款' }}
          </el-descriptions-item>
          <el-descriptions-item label="退款原因">{{ currentRefund.reason }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentRefund.status)">
              {{ getStatusText(currentRefund.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="退款说明" :span="2">{{ currentRefund.description }}</el-descriptions-item>
          <el-descriptions-item label="退货快递单号" v-if="currentRefund.logisticsNo" :span="2">
            <el-tag type="success">{{ currentRefund.logisticsNo }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ currentRefund.createTime }}</el-descriptions-item>
          <el-descriptions-item label="处理时间" v-if="currentRefund.handleTime">{{ currentRefund.handleTime }}</el-descriptions-item>
          <el-descriptions-item label="拒绝原因" v-if="currentRefund.rejectReason" :span="2">{{ currentRefund.rejectReason }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
    
    <!-- 拒绝对话框 -->
    <el-dialog v-model="showRejectDialog" title="拒绝退款" width="500px">
      <el-form :model="rejectForm" :rules="rejectRules" ref="rejectFormRef" label-width="100px">
        <el-form-item label="拒绝原因" prop="rejectReason">
          <el-input v-model="rejectForm.rejectReason" type="textarea" :rows="4" placeholder="请输入拒绝原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRejectDialog = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确定拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/** 退款状态流转：申请、审核、退货物流等 */
import { ref, onMounted } from 'vue'
import api from '../../utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { useUserStore } from '../../stores/user'

const refunds = ref([])
const userStore = useUserStore()
const loading = ref(false)
const keyword = ref('')
const status = ref(null)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showDetailDialog = ref(false)
const showRejectDialog = ref(false)
const currentRefund = ref(null)
const rejectFormRef = ref(null)

const rejectForm = ref({
  rejectReason: ''
})

const rejectRules = {
  rejectReason: [{ required: true, message: '请输入拒绝原因', trigger: 'blur' }]
}

const getStatusText = (status) => {
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

const getStatusType = (status) => {
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

const getBasePath = () => (userStore.userInfo?.role === 2 ? '/merchant/refunds' : '/admin/refunds')

const loadRefunds = async () => {
  loading.value = true
  try {
    const res = await api.get(getBasePath(), {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        keyword: keyword.value,
        status: status.value
      }
    })
    refunds.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('获取退款列表失败')
  } finally {
    loading.value = false
  }
}

const handleApprove = async (refund) => {
  try {
    await ElMessageBox.confirm('确定同意该退款申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.post(`${getBasePath()}/${refund.id}/approve`)
    ElMessage.success('操作成功')
    loadRefunds()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleReject = (refund) => {
  currentRefund.value = refund
  rejectForm.value.rejectReason = ''
  showRejectDialog.value = true
}

const confirmReject = async () => {
  if (!rejectFormRef.value) return
  await rejectFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await api.post(`${getBasePath()}/${currentRefund.value.id}/reject`, {
          rejectReason: rejectForm.value.rejectReason
        })
        ElMessage.success('操作成功')
        showRejectDialog.value = false
        loadRefunds()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

const handleComplete = async (refund) => {
  try {
    await ElMessageBox.confirm('确定完成退款吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.post(`${getBasePath()}/${refund.id}/complete`)
    ElMessage.success('操作成功')
    loadRefunds()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleConfirmReceive = async (refund) => {
  try {
    await ElMessageBox.confirm('确认已收到退货商品吗？确认后将完成退款。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.post(`${getBasePath()}/${refund.id}/confirm-receive`)
    ElMessage.success('确认收货成功，退款已完成')
    loadRefunds()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const showDetail = async (refund) => {
  try {
    const res = await api.get(`/refund/detail/${refund.id}`)
    currentRefund.value = res.data
    showDetailDialog.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

onMounted(() => {
  loadRefunds()
})
</script>

<style scoped>
.refund-management {
  padding: 0;
}

.toolbar {
  display: flex;
  align-items: center;
}
</style>

