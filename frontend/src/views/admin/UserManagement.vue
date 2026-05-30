<!-- 用户管理：角色与账号状态（仅管理员） -->
<template>
  <div class="user-management">
    <div class="header header--toolbar">
      <div class="search-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索用户名、昵称、邮箱、手机号"
          style="width: 300px;"
          @keyup.enter="loadUsers"
        >
          <template #append>
            <el-button @click="loadUsers">搜索</el-button>
          </template>
        </el-input>
      </div>
    </div>
    
    <el-card>
      <el-table :data="users" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column label="性别" width="80">
          <template #default="{ row }">
            <span v-if="row.gender === 1">男</span>
            <span v-else-if="row.gender === 2">女</span>
            <span v-else>未知</span>
          </template>
        </el-table-column>
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.role === 1" type="danger">管理员</el-tag>
            <el-tag v-else-if="row.role === 2" type="warning">商家</el-tag>
            <el-tag v-else type="info">普通用户</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.role !== 1"
              :type="row.status === 1 ? 'warning' : 'success'"
              size="small"
              @click="toggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button 
              v-if="row.role !== 1"
              type="danger"
              size="small"
              @click="deleteUser(row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadUsers"
        style="margin-top: 20px; justify-content: center;"
      />
    </el-card>
  </div>
</template>

<script setup>
/** role：0 用户 1 管理员 2 商家；可禁用账号 */
import { ref, onMounted } from 'vue'
import api from '../../utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const users = ref([])
const keyword = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadUsers = async () => {
  try {
    const res = await api.get('/admin/users', {
      params: { pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value }
    })
    users.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取用户列表失败', error)
  }
}

const toggleStatus = async (user) => {
  try {
    const newStatus = user.status === 1 ? 0 : 1
    await api.put(`/admin/users/${user.id}/status`, { status: newStatus })
    ElMessage.success('操作成功')
    loadUsers()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const deleteUser = async (userId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.delete(`/admin/users/${userId}`)
    ElMessage.success('删除成功')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user-management {
  padding: 0;
}
</style>

