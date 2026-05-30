<!-- 商品分类树：增删改与排序（仅管理员） -->
<template>
  <div class="category-management">
    <div class="header header--toolbar">
      <el-button type="primary" @click="showDialog = true">添加分类</el-button>
    </div>
    
    <el-card>
      <el-table :data="categories" style="width: 100%" row-key="id" :tree-props="{ children: 'children' }">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="分类图片" width="120">
          <template #default="{ row }">
            <el-image 
              :src="row.image || 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=100&h=100&fit=crop'" 
              style="width: 80px; height: 80px;"
              fit="cover"
            />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="分类名称" width="200" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="parentId" label="父分类ID" width="120">
          <template #default="{ row }">
            {{ row.parentId === 0 ? '顶级分类' : row.parentId }}
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editCategory(row)">编辑</el-button>
            <el-button 
              :type="row.status === 1 ? 'warning' : 'success'"
              size="small"
              @click="toggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" size="small" @click="deleteCategory(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑分类对话框 -->
    <el-dialog v-model="showDialog" :title="editingCategory ? '编辑分类' : '添加分类'" width="700px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="分类描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="分类图片">
          <el-upload
            class="image-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleImageSuccess"
            :before-upload="beforeImageUpload"
            :headers="uploadHeaders"
            accept="image/*"
          >
            <img v-if="form.image" :src="form.image" class="uploaded-image" />
            <el-icon v-else class="image-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">点击上传分类图片（建议尺寸：800x600）</div>
        </el-form-item>
        <el-form-item label="父分类" prop="parentId">
          <el-select v-model="form.parentId" placeholder="请选择父分类（不选则为顶级分类）" style="width: 100%;" clearable>
            <el-option label="顶级分类" :value="0" />
            <el-option 
              v-for="cat in topCategories" 
              :key="cat.id" 
              :label="cat.name" 
              :value="cat.id"
              :disabled="editingCategory && editingCategory.id === cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
          <span style="margin-left: 10px; color: #909399; font-size: 12px;">数字越小越靠前</span>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="saveCategory">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/** 树形分类 parentId=0 为顶级；支持启用/禁用状态 */
import { ref, reactive, onMounted, computed } from 'vue'
import api from '../../utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '../../stores/user'

const categories = ref([])
const showDialog = ref(false)
const editingCategory = ref(null)
const formRef = ref(null)
const userStore = useUserStore()

const uploadUrl = ref('/api/upload/image')

const uploadHeaders = computed(() => {
  const headers = {}
  if (userStore.token) {
    headers['Authorization'] = `Bearer ${userStore.token}`
  }
  return headers
})

const form = reactive({
  name: '',
  description: '',
  image: '',
  parentId: 0,
  sortOrder: 0,
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const topCategories = computed(() => {
  return categories.value.filter(cat => cat.parentId === 0)
})

const loadCategories = async () => {
  try {
    const res = await api.get('/category/list')
    const allCategories = res.data || []
    // 构建树形结构
    const categoryMap = new Map()
    const rootCategories = []
    
    // 先创建所有分类的映射
    allCategories.forEach(cat => {
      categoryMap.set(cat.id, { ...cat, children: [] })
    })
    
    // 构建树形结构
    allCategories.forEach(cat => {
      const category = categoryMap.get(cat.id)
      if (cat.parentId === 0 || !categoryMap.has(cat.parentId)) {
        rootCategories.push(category)
      } else {
        const parent = categoryMap.get(cat.parentId)
        if (parent.children) {
          parent.children.push(category)
        } else {
          parent.children = [category]
        }
      }
    })
    
    categories.value = rootCategories
  } catch (error) {
    console.error('获取分类列表失败', error)
    ElMessage.error('获取分类列表失败')
  }
}

const editCategory = (category) => {
  editingCategory.value = category
  Object.assign(form, {
    name: category.name,
    description: category.description || '',
    image: category.image || '',
    parentId: category.parentId || 0,
    sortOrder: category.sortOrder || 0,
    status: category.status
  })
  showDialog.value = true
}

const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过10MB!')
    return false
  }
  return true
}

const handleImageSuccess = (response) => {
  if (response.code === 200) {
    form.image = response.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const saveCategory = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (editingCategory.value) {
          await api.put('/category/update', { ...form, id: editingCategory.value.id })
          ElMessage.success('更新成功')
        } else {
          await api.post('/category/add', form)
          ElMessage.success('添加成功')
        }
        showDialog.value = false
        editingCategory.value = null
        resetForm()
        loadCategories()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

const toggleStatus = async (category) => {
  try {
    const newStatus = category.status === 1 ? 0 : 1
    await api.put('/category/update', { id: category.id, status: newStatus })
    ElMessage.success('操作成功')
    loadCategories()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const deleteCategory = async (categoryId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个分类吗？删除后该分类下的子分类也会被删除。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.delete(`/category/delete/${categoryId}`)
    ElMessage.success('删除成功')
    loadCategories()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const resetForm = () => {
  Object.assign(form, {
    name: '',
    description: '',
    image: '',
    parentId: 0,
    sortOrder: 0,
    status: 1
  })
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.category-management {
  padding: 0;
}

.image-uploader {
  width: 178px;
  height: 178px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}

.image-uploader:hover {
  border-color: var(--primary-color);
}

.image-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.uploaded-image {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}
</style>

