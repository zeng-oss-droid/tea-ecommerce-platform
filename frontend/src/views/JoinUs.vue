<template>
  <div class="join-us-page">
    <div class="join-layout">
      <section class="join-brand">
        <p class="brand-badge">商家入驻</p>
        <h1>加入我们，成为茶叶商家</h1>
        <p class="brand-desc">
          通过平台触达更多爱茶用户，打造属于你的线上茶铺。填写资料后即可申请开通商家身份，快速上架茶品。
        </p>
        <div class="brand-points">
          <span>多端曝光</span>
          <span>订单管理</span>
          <span>营销支持</span>
        </div>
      </section>

      <el-card class="join-card" shadow="never">
        <div class="title-wrap">
          <h2>入驻申请信息</h2>
          <p>请如实填写以下资料，提交后系统将为你开通商家权限。</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="join-form">
          <div class="form-grid">
            <el-form-item label="店铺名称" prop="merchantName">
              <el-input v-model="form.merchantName" placeholder="请输入店铺名称" size="large" />
            </el-form-item>
            <el-form-item label="联系人姓名" prop="contactName">
              <el-input v-model="form.contactName" placeholder="请输入联系人姓名" size="large" />
            </el-form-item>
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" size="large" />
            </el-form-item>
            <el-form-item label="主营类目" prop="businessScope">
              <el-input v-model="form.businessScope" placeholder="例如：绿茶、红茶、礼盒茶" size="large" />
            </el-form-item>
          </div>
          <el-form-item label="店铺简介" prop="merchantIntro">
            <el-input v-model="form.merchantIntro" type="textarea" :rows="4" placeholder="请输入店铺简介" />
          </el-form-item>
          <el-form-item class="action-item">
            <el-button type="primary" class="submit-btn" :loading="submitting" @click="submitApply">提交申请</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../utils/api'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const submitting = ref(false)

const form = reactive({
  merchantName: '',
  contactName: '',
  contactPhone: '',
  businessScope: '',
  merchantIntro: ''
})

const rules = {
  merchantName: [{ required: true, message: '请输入店铺名称', trigger: 'blur' }],
  contactName: [{ required: true, message: '请输入联系人姓名', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  businessScope: [{ required: true, message: '请输入主营类目', trigger: 'blur' }]
}

const submitApply = async () => {
  if (!userStore.token) {
    ElMessage.warning('请先登录后申请入驻')
    router.push('/login')
    return
  }

  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      submitting.value = true
      await api.post('/user/merchant/apply', form)
      await userStore.getUserInfo()
      ElMessage.success('申请已提交，等待管理员审核')
      router.push('/profile')
    } catch (error) {
      ElMessage.error(error.message || '申请失败')
    } finally {
      submitting.value = false
    }
  })
}
</script>

<style scoped>
.join-us-page {
  min-height: 100vh;
  padding: 40px 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  background:
    radial-gradient(circle at 15% 12%, rgba(188, 56, 35, 0.14), transparent 36%),
    radial-gradient(circle at 85% 82%, rgba(251, 204, 141, 0.35), transparent 40%),
    linear-gradient(140deg, #fff9f3 0%, #fff 45%, #fff8f0 100%);
}

.join-layout {
  width: min(1140px, 100%);
  display: grid;
  grid-template-columns: 1fr 1.25fr;
  border-radius: 18px;
  overflow: hidden;
  background: #fff;
  box-shadow: var(--shadow-hover);
}

.join-brand {
  padding: 56px 46px;
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
  background: rgba(255, 255, 255, 0.2);
  margin-bottom: 18px;
}

.join-brand h1 {
  margin: 0 0 14px;
  font-size: 34px;
  line-height: 1.35;
}

.brand-desc {
  margin: 0;
  line-height: 1.8;
  opacity: 0.95;
}

.brand-points {
  margin-top: 28px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.brand-points span {
  padding: 6px 14px;
  border-radius: 999px;
  font-size: 13px;
  background: rgba(255, 255, 255, 0.2);
}

.join-card {
  border: none;
  border-radius: 0;
  padding: 42px 42px 36px;
}

.title-wrap {
  margin-bottom: 20px;
}

.title-wrap h2 {
  margin: 0;
  font-size: 30px;
  color: var(--text-primary);
}

.title-wrap p {
  margin: 8px 0 0;
  color: var(--text-secondary);
  font-size: 14px;
}

.join-form :deep(.el-form-item__label) {
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

@media (max-width: 1024px) {
  .join-layout {
    grid-template-columns: 1fr;
  }

  .join-brand {
    padding: 34px 28px;
  }

  .join-card {
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
  .join-us-page {
    padding: 16px;
  }

  .join-brand h1 {
    font-size: 28px;
  }

  .join-card {
    padding: 26px 18px;
  }
}
</style>
