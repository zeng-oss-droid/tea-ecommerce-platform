<!-- 注册页：提交后跳转登录 -->
<template>
  <div class="auth-page register-page">
    <div class="auth-layout">
      <section class="auth-brand">
        <p class="brand-badge">茶商城</p>
        <h1>创建新账号</h1>
        <p class="brand-desc">
          加入我们，获取最新茶品资讯和专属优惠，收藏你喜欢的好茶，让每一次下单都更省心。
        </p>
        <div class="brand-points">
          <span>新人礼遇</span>
          <span>积分兑换</span>
          <span>会员专享</span>
        </div>
      </section>

      <el-card class="auth-card" shadow="never">
        <div class="card-header">
          <h2>用户注册</h2>
          <p>完善信息后即可开启购物体验</p>
        </div>
        <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="auth-form">
          <div class="form-grid">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
            </el-form-item>
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="form.nickname" placeholder="请输入昵称" size="large" />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password size="large" />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" show-password size="large" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" size="large" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" size="large" />
            </el-form-item>
          </div>
          <el-form-item class="action-item">
            <el-button type="primary" class="submit-btn" @click="handleRegister" :loading="loading">注册</el-button>
          </el-form-item>
          <p class="switch-tip">
            已有账号？
            <el-link type="primary" @click="$router.push('/login')">立即登录</el-link>
          </p>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
/** 自定义校验 confirmPassword；注册成功跳转 /login */
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: '',
  nickname: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validateConfirmPassword, trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }]
}

const handleRegister = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.register(form)
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } catch (error) {
        ElMessage.error(error.message || '注册失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  padding: 40px 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    radial-gradient(circle at 18% 12%, rgba(188, 56, 35, 0.14), transparent 38%),
    radial-gradient(circle at 84% 80%, rgba(251, 204, 141, 0.34), transparent 40%),
    linear-gradient(140deg, #fff9f3 0%, #fff 42%, #fff7ed 100%);
}

.auth-layout {
  width: min(1180px, 100%);
  display: grid;
  grid-template-columns: 1fr 1.2fr;
  background: #fff;
  border-radius: 18px;
  overflow: hidden;
  box-shadow: var(--shadow-hover);
}

.auth-brand {
  padding: 56px 48px;
  background: linear-gradient(135deg, rgba(188, 56, 35, 0.94), rgba(157, 45, 28, 0.9));
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.brand-badge {
  display: inline-block;
  width: fit-content;
  font-size: 12px;
  letter-spacing: 2px;
  padding: 5px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.22);
  margin-bottom: 18px;
}

.auth-brand h1 {
  margin: 0 0 14px;
  font-size: 34px;
  font-weight: 700;
}

.brand-desc {
  margin: 0;
  line-height: 1.8;
  opacity: 0.95;
  max-width: 420px;
}

.brand-points {
  margin-top: 28px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.brand-points span {
  padding: 6px 14px;
  border-radius: 999px;
  font-size: 13px;
  background: rgba(255, 255, 255, 0.2);
}

.auth-card {
  border: none;
  padding: 42px 42px 36px;
  border-radius: 0;
}

.card-header {
  margin-bottom: 20px;
}

.card-header h2 {
  font-size: 30px;
  color: var(--text-primary);
  margin: 0 0 8px;
}

.card-header p {
  margin: 0;
  font-size: 14px;
  color: var(--text-secondary);
}

.auth-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: var(--text-secondary);
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 16px;
}

.action-item {
  margin-top: 8px;
  margin-bottom: 8px;
}

.submit-btn {
  width: 100%;
  height: 44px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
}

.switch-tip {
  text-align: center;
  font-size: 14px;
  color: var(--text-secondary);
  margin-top: 6px;
}

.switch-tip .el-link {
  margin-left: 4px;
  font-weight: 600;
}

@media (max-width: 1024px) {
  .auth-layout {
    grid-template-columns: 1fr;
  }

  .auth-brand {
    padding: 34px 28px;
  }

  .auth-card {
    padding: 34px 28px;
  }
}

@media (max-width: 720px) {
  .form-grid {
    grid-template-columns: 1fr;
    gap: 0;
  }
}

@media (max-width: 600px) {
  .auth-page {
    padding: 16px;
  }

  .auth-brand h1 {
    font-size: 28px;
  }

  .auth-card {
    padding: 26px 18px;
  }
}

.register-page {
  min-height: 100vh;
}
</style>

