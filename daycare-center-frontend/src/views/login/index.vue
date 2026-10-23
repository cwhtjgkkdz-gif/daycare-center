<template>
  <div class="login-page">
    <!-- 左侧品牌区 -->
    <div class="brand-panel">
      <div class="brand-content">
        <div class="brand-icon">
          <svg viewBox="0 0 80 80" width="80" height="80">
            <circle cx="40" cy="28" r="20" fill="#FFB74D"/>
            <rect x="12" y="48" width="56" height="28" rx="8" fill="#66BB6A"/>
            <circle cx="40" cy="28" r="12" fill="#FFF9C4"/>
          </svg>
        </div>
        <h1>托育中心管理系统</h1>
        <p>专业托育 · 智慧管理 · 家园共育</p>
      </div>
    </div>

    <!-- 右侧登录区 -->
    <div class="login-panel">
      <div class="login-card">
        <h2>欢迎回来</h2>
        <p class="subtitle">请登录您的账号</p>

        <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleLogin">
          <el-form-item prop="username">
            <el-input
                v-model="form.username" placeholder="请输入用户名"
                :prefix-icon="User" size="large" class="login-input"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
                v-model="form.password" type="password" placeholder="请输入密码"
                :prefix-icon="Lock" size="large" class="login-input"
                show-password @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-form-item style="margin-top:8px">
            <el-button type="primary" size="large" :loading="loading"
                       @click="handleLogin" class="login-btn">
              登 录
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-tip">
          <el-icon>
            <InfoFilled/>
          </el-icon>
          默认账号 admin / admin123
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {reactive, ref} from 'vue'
import {useRouter} from 'vue-router'
import {User, Lock, InfoFilled} from '@element-plus/icons-vue'
import {login} from '../../api/auth'
import {useUserStore} from '../../store'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({username: 'admin', password: 'admin123'})
const rules = {
  username: [{required: true, message: '请输入用户名', trigger: 'blur'}],
  password: [{required: true, message: '请输入密码', trigger: 'blur'}]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await login(form)
    userStore.setUser(res.data.userInfo)
    userStore.setMenus(res.data.menus)
    router.push('/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  overflow: hidden;
}

/* 左侧品牌区 */
.brand-panel {
  flex: 1;
  min-width: 0;
  background: linear-gradient(135deg, #1a1f36 0%, #304156 50%, #409EFF 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.brand-panel::after {
  content: '';
  position: absolute;
  inset: 0;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' xmlns='http://www.w3.org/2000/svg'%3E%3Ccircle cx='30' cy='30' r='1.5' fill='rgba(255,255,255,0.06)'/%3E%3C/svg%3E");
}

.brand-content {
  text-align: center;
  color: #fff;
  position: relative;
  z-index: 1;
}

.brand-icon {
  margin-bottom: 24px;
}

.brand-content h1 {
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 2px;
  margin-bottom: 10px;
}

.brand-content p {
  font-size: 15px;
  opacity: 0.7;
  letter-spacing: 4px;
}

/* 右侧登录区 */
.login-panel {
  width: 460px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  padding: 40px;
}

.login-card {
  width: 100%;
  max-width: 360px;
}

.login-card h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1a1f36;
  margin-bottom: 8px;
}

.subtitle {
  color: #909399;
  font-size: 14px;
  margin-bottom: 36px;
}

.login-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  padding: 4px 12px;
}

.login-btn {
  width: 100%;
  height: 44px;
  border-radius: 8px;
  font-size: 16px;
  letter-spacing: 2px;
}

.login-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  justify-content: center;
  color: #c0c4cc;
  font-size: 13px;
  margin-top: 24px;
}

@media (max-width: 768px) {
  .brand-panel {
    display: none;
  }

  .login-panel {
    width: 100%;
  }
}
</style>
