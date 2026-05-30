<!-- 个人中心：资料编辑与修改密码 -->
<template>
  <div class="profile-page">
    <div class="container">
      <h2>个人中心</h2>
      <el-card>
        <el-form :model="form" label-width="100px" style="max-width: 600px;">
          <el-form-item label="用户名">
            <el-input v-model="form.username" disabled />
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="form.nickname" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="form.email" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="form.phone" />
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group v-model="form.gender">
              <el-radio :label="0">未知</el-radio>
              <el-radio :label="1">男</el-radio>
              <el-radio :label="2">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="updateProfile">保存</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card style="margin-top: 20px;">
        <template #header>
          <h3>修改密码</h3>
        </template>
        <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px" style="max-width: 600px;">
          <el-form-item label="原密码" prop="oldPassword">
            <el-input v-model="passwordForm.oldPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="passwordForm.newPassword" type="password" show-password />
            <div class="password-hint">密码长度至少6位</div>
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="updatePassword">修改密码</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
/** 同步 userStore.userInfo；密码修改走独立接口 */
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import api from '../utils/api'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const passwordFormRef = ref(null)

const form = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  gender: 0
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value && value === passwordForm.oldPassword) {
          callback(new Error('新密码不能与原密码相同'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const loadUserInfo = async () => {
  try {
    await userStore.getUserInfo()
    if (userStore.userInfo) {
      Object.assign(form, {
        username: userStore.userInfo.username,
        nickname: userStore.userInfo.nickname || '',
        email: userStore.userInfo.email || '',
        phone: userStore.userInfo.phone || '',
        gender: userStore.userInfo.gender || 0
      })
    }
  } catch (error) {
    console.error('获取用户信息失败', error)
  }
}

const updateProfile = async () => {
  try {
    await userStore.updateUserInfo(form)
    ElMessage.success('更新成功')
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

const updatePassword = async () => {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await api.put('/user/password', {
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        ElMessage.success('密码修改成功')
        Object.assign(passwordForm, {
          oldPassword: '',
          newPassword: '',
          confirmPassword: ''
        })
        passwordFormRef.value.resetFields()
      } catch (error) {
        // 显示具体的错误信息
        const errorMessage = error.message || error.response?.data?.message || '密码修改失败'
        ElMessage.error(errorMessage)
      }
    }
  })
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.password-hint {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style>

