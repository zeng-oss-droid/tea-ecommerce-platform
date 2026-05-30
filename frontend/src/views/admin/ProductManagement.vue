<!-- 商品管理：管理员维护全站商品，商家仅维护自己的商品 -->
<template>
  <div class="product-management">
    <div class="header header--toolbar">
      <el-button type="primary" @click="showDialog = true">添加商品</el-button>
    </div>
    
    <el-card>
      <div class="search-bar" style="margin-bottom: 20px;">
        <el-input
          v-model="keyword"
          placeholder="搜索商品名称"
          style="width: 300px;"
          @keyup.enter="loadProducts"
        >
          <template #append>
            <el-button @click="loadProducts">搜索</el-button>
          </template>
        </el-input>
      </div>
      
      <el-table :data="products" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="商品" min-width="200">
          <template #default="{ row }">
            <div style="display: flex; align-items: center; gap: 10px;">
              <el-image 
                :src="getProductImage(row.images)" 
                style="width: 50px; height: 50px;"
                fit="cover"
              />
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="120">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column prop="sales" label="销量" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editProduct(row)">编辑</el-button>
            <el-button 
              :type="row.status === 1 ? 'warning' : 'success'"
              size="small"
              @click="toggleStatus(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button type="danger" size="small" @click="deleteProduct(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadProducts"
        style="margin-top: 20px; justify-content: center;"
      />
    </el-card>

    <!-- 添加/编辑商品对话框 -->
    <el-dialog v-model="showDialog" :title="editingProduct ? '编辑商品' : '添加商品'" width="900px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="主图" prop="mainImage">
          <el-upload
            class="image-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleMainImageSuccess"
            :before-upload="beforeImageUpload"
            :headers="uploadHeaders"
            accept="image/*"
          >
            <img v-if="form.mainImage" :src="form.mainImage" class="uploaded-image" />
            <el-icon v-else class="image-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">点击上传主图（建议尺寸：800x800）</div>
        </el-form-item>
        <el-form-item label="其他图片">
          <el-upload
            :action="uploadUrl"
            list-type="picture-card"
            :file-list="otherImages"
            :on-success="handleOtherImageSuccess"
            :on-remove="handleRemoveImage"
            :before-upload="beforeImageUpload"
            :headers="uploadHeaders"
            accept="image/*"
            :limit="9"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">最多上传9张图片（建议尺寸：800x800）</div>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="原价" prop="originalPrice">
          <el-input-number v-model="form.originalPrice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%;">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="产地" prop="origin">
          <el-input v-model="form.origin" />
        </el-form-item>
        <el-form-item label="工艺" prop="process">
          <el-input v-model="form.process" />
        </el-form-item>
        <el-form-item label="等级" prop="grade">
          <el-input v-model="form.grade" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="saveProduct">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/** 根据路由 /admin 或 /merchant 切换 API 范围与表单字段 */
import { ref, reactive, onMounted, computed } from 'vue'
import api from '../../utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '../../stores/user'

const products = ref([])
const categories = ref([])
const keyword = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showDialog = ref(false)
const editingProduct = ref(null)
const formRef = ref(null)
const otherImages = ref([])
const userStore = useUserStore()
const isMerchant = computed(() => userStore.userInfo?.role === 2)

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
  mainImage: '',
  price: 0,
  originalPrice: 0,
  stock: 0,
  categoryId: null,
  origin: '',
  process: '',
  grade: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

const getProductImage = (images) => {
  if (!images) return 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=50&h=50&fit=crop'
  try {
    const imgList = JSON.parse(images)
    return imgList[0] || 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=50&h=50&fit=crop'
  } catch {
    return images.split(',')[0] || 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=50&h=50&fit=crop'
  }
}

const loadProducts = async () => {
  try {
    const endpoint = isMerchant.value ? '/merchant/products' : '/product/list'
    const res = await api.get(endpoint, {
      params: { pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value }
    })
    products.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取商品列表失败', error)
  }
}

const loadCategories = async () => {
  try {
    const res = await api.get('/category/list')
    categories.value = res.data || []
  } catch (error) {
    console.error('获取分类列表失败', error)
  }
}

const editProduct = (product) => {
  editingProduct.value = product
  Object.assign(form, product)
  
  // 解析图片
  if (product.images) {
    try {
      const imgList = JSON.parse(product.images)
      if (imgList && imgList.length > 0) {
        form.mainImage = imgList[0]
        otherImages.value = imgList.slice(1).map((url, index) => ({
          uid: index + 1,
          name: `image-${index + 1}.jpg`,
          url: url
        }))
      } else {
        form.mainImage = ''
        otherImages.value = []
      }
    } catch {
      // 如果不是JSON格式，尝试按逗号分割
      const imgList = product.images.split(',').filter(img => img.trim())
      if (imgList.length > 0) {
        form.mainImage = imgList[0].trim()
        otherImages.value = imgList.slice(1).map((url, index) => ({
          uid: index + 1,
          name: `image-${index + 1}.jpg`,
          url: url.trim()
        }))
      } else {
        form.mainImage = ''
        otherImages.value = []
      }
    }
  } else {
    form.mainImage = ''
    otherImages.value = []
  }
  
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

const handleMainImageSuccess = (response) => {
  if (response.code === 200) {
    form.mainImage = response.data
    ElMessage.success('主图上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const handleOtherImageSuccess = (response, file) => {
  if (response.code === 200) {
    file.url = response.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
    // 移除失败的文件
    const index = otherImages.value.findIndex(item => item.uid === file.uid)
    if (index > -1) {
      otherImages.value.splice(index, 1)
    }
  }
}

const handleRemoveImage = (file) => {
  const index = otherImages.value.findIndex(item => item.uid === file.uid)
  if (index > -1) {
    otherImages.value.splice(index, 1)
  }
}

const saveProduct = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 组合所有图片URL
        const allImages = []
        if (form.mainImage) {
          allImages.push(form.mainImage)
        }
        otherImages.value.forEach(item => {
          if (item.url) {
            allImages.push(item.url)
          }
        })
        
        // 将图片数组转换为JSON字符串
        const productData = {
          ...form,
          images: allImages.length > 0 ? JSON.stringify(allImages) : null
        }
        
        // 移除mainImage字段，因为数据库中只有images字段
        delete productData.mainImage
        
        if (editingProduct.value) {
          const endpoint = isMerchant.value ? '/merchant/products' : '/product/update'
          await api.put(endpoint, { ...productData, id: editingProduct.value.id })
          ElMessage.success('更新成功')
        } else {
          const endpoint = isMerchant.value ? '/merchant/products' : '/product/add'
          await api.post(endpoint, productData)
          ElMessage.success('添加成功')
        }
        showDialog.value = false
        editingProduct.value = null
        resetForm()
        loadProducts()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

const toggleStatus = async (product) => {
  try {
    const newStatus = product.status === 1 ? 0 : 1
    const endpoint = isMerchant.value ? '/merchant/products' : '/product/update'
    await api.put(endpoint, { id: product.id, status: newStatus })
    ElMessage.success('操作成功')
    loadProducts()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const deleteProduct = async (productId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const endpoint = isMerchant.value ? `/merchant/products/${productId}` : `/product/delete/${productId}`
    await api.delete(endpoint)
    ElMessage.success('删除成功')
    loadProducts()
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
    mainImage: '',
    price: 0,
    originalPrice: 0,
    stock: 0,
    categoryId: null,
    origin: '',
    process: '',
    grade: '',
    status: 1
  })
  otherImages.value = []
}

onMounted(() => {
  loadProducts()
  loadCategories()
})
</script>

<style scoped>
.product-management {
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

:deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
  line-height: 100px;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 100px;
  height: 100px;
}
</style>

