<!-- 登录页：校验通过后写入 userStore 并跳转首页 -->
<template>
  <div class="auth-page login-page">
    <div class="auth-layout">
      <section class="auth-brand">
        <p class="brand-badge">茶商城</p>
        <h1>欢迎回来</h1>
        <p class="brand-desc">
          甄选好茶，轻松下单。登录后可查看订单、收藏心仪商品，并享受更完整的购物服务。
        </p>
        <div class="brand-points">
          <span>正品茶源</span>
          <span>极速发货</span>
          <span>售后无忧</span>
        </div>
      </section>

      <el-card class="auth-card" shadow="never">
        <div class="card-header">
          <h2>用户登录</h2>
          <p>登录后开启你的品茶之旅</p>
        </div>
        <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="auth-form">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password size="large" @keyup.enter="handleLogin" />
          </el-form-item>
          <el-form-item class="action-item">
            <el-button type="primary" class="submit-btn" @click="handleLogin" :loading="loading">登录</el-button>
          </el-form-item>
          <p class="switch-tip">
            还没有账号？
            <el-link type="primary" @click="$router.push('/register')">立即注册</el-link>
          </p>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
/** 调用 userStore.login，成功后跳转首页 */
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
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.login(form.username, form.password)
        ElMessage.success('登录成功')
        router.push('/')
      } catch (error) {
        ElMessage.error(error.message || '登录失败')
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
    radial-gradient(circle at 20% 15%, rgba(188, 56, 35, 0.16), transparent 38%),
    radial-gradient(circle at 85% 85%, rgba(251, 204, 141, 0.35), transparent 40%),
    linear-gradient(135deg, #fdf8f2 0%, #fff 45%, #fff7ee 100%);
}

.auth-layout {
  width: min(1040px, 100%);
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
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
  font-size: 36px;
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
  padding: 48px 44px;
  border-radius: 0;
}

.card-header {
  margin-bottom: 22px;
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

@media (max-width: 960px) {
  .auth-layout {
    grid-template-columns: 1fr;
  }

  .auth-brand {
    padding: 34px 28px;
  }

  .auth-card {
    padding: 36px 28px;
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
    padding: 28px 20px;
  }
}

.login-page {
  min-height: 100vh;
}
</style>

