<!-- 商家入驻申请审核（仅管理员） -->
<template>
  <div class="merchant-application-management">
    <div class="header header--toolbar">
      <div class="filters">
        <el-input
          v-model="keyword"
          placeholder="搜索用户名、店铺、联系人、电话"
          style="width: 320px;"
          @keyup.enter="loadApplications"
        >
          <template #append>
            <el-button @click="loadApplications">搜索</el-button>
          </template>
        </el-input>
        <el-select v-model="status" style="width: 140px;" @change="handleStatusChange">
          <el-option :value="null" label="全部状态" />
          <el-option :value="1" label="待审核" />
          <el-option :value="2" label="已通过" />
          <el-option :value="3" label="已驳回" />
        </el-select>
      </div>
    </div>

    <el-card>
      <el-table :data="applications" style="width: 100%">
        <el-table-column prop="id" label="用户ID" width="90" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="merchantName" label="店铺名称" min-width="160" />
        <el-table-column prop="contactName" label="联系人" width="120" />
        <el-table-column prop="contactPhone" label="联系电话" width="140" />
        <el-table-column prop="businessScope" label="主营类目" min-width="160" />
        <el-table-column prop="merchantIntro" label="店铺简介" min-width="220" show-overflow-tooltip />
        <el-table-column label="审核状态" width="110">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.merchantApplyStatus)">
              {{ getStatusText(row.merchantApplyStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="merchantApplyTime" label="申请时间" width="180" />
        <el-table-column prop="merchantReviewTime" label="审核时间" width="180" />
        <el-table-column prop="merchantApplyRejectReason" label="驳回原因" width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.merchantApplyStatus === 1"
              type="success"
              size="small"
              @click="approve(row)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.merchantApplyStatus === 1"
              type="danger"
              size="small"
              @click="openRejectDialog(row)"
            >
              驳回
            </el-button>
            <el-button
              v-if="row.merchantApplyStatus === 1 || row.merchantApplyStatus === 3"
              type="danger"
              size="small"
              plain
              @click="removeApplication(row)"
            >
              删除记录
            </el-button>
            <span v-if="row.merchantApplyStatus === 2" class="done-text">已处理</span>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadApplications"
        style="margin-top: 20px; justify-content: center;"
      />
    </el-card>

    <el-dialog v-model="rejectDialogVisible" title="驳回申请" width="420px">
      <el-input
        v-model="rejectReason"
        type="textarea"
        :rows="4"
        maxlength="200"
        show-word-limit
        placeholder="请输入驳回原因（选填）"
      />
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="rejecting" @click="reject">确认驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/** 审核通过后将用户 role 设为商家 */
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../../utils/api'

const applications = ref([])
const keyword = ref('')
const status = ref(null)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const rejectDialogVisible = ref(false)
const rejectReason = ref('')
const rejecting = ref(false)
const currentRejectRow = ref(null)

const getStatusText = (value) => {
  if (value === 1) return '待审核'
  if (value === 2) return '已通过'
  if (value === 3) return '已驳回'
  return '未申请'
}

const getStatusTagType = (value) => {
  if (value === 1) return 'warning'
  if (value === 2) return 'success'
  if (value === 3) return 'danger'
  return 'info'
}

const loadApplications = async () => {
  try {
    const res = await api.get('/admin/merchant-applications', {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        keyword: keyword.value,
        status: status.value
      }
    })
    applications.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取商家申请列表失败', error)
  }
}

const handleStatusChange = () => {
  pageNum.value = 1
  loadApplications()
}

const approve = async (row) => {
  try {
    await ElMessageBox.confirm('确认通过该商家申请吗？', '审核确认', {
      type: 'warning',
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    })
    await api.post(`/admin/merchant-applications/${row.id}/approve`)
    ElMessage.success('审核通过')
    loadApplications()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

const openRejectDialog = (row) => {
  currentRejectRow.value = row
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

const reject = async () => {
  if (!currentRejectRow.value) return
  try {
    rejecting.value = true
    await api.post(`/admin/merchant-applications/${currentRejectRow.value.id}/reject`, {
      rejectReason: rejectReason.value
    })
    ElMessage.success('已驳回申请')
    rejectDialogVisible.value = false
    loadApplications()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    rejecting.value = false
  }
}

const removeApplication = async (row) => {
  try {
    await ElMessageBox.confirm(
      '确定删除该条申请记录吗？将清空店铺资料并恢复为「未申请」，用户可再次提交入驻。',
      '删除确认',
      { type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消' }
    )
    await api.delete(`/admin/merchant-applications/${row.id}`)
    ElMessage.success('已删除')
    loadApplications()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

onMounted(() => {
  loadApplications()
})
</script>

<style scoped>
.merchant-application-management {
  padding: 0;
}

.done-text {
  color: #909399;
  font-size: 13px;
}
</style>
