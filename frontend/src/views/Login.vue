<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2 class="login-title">高校课程管理系统</h2>
      </template>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="用户名"
            :disabled="loading">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="密码" 
            @keyup.enter="handleLogin"
            :disabled="loading">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleLogin" 
            style="width: 100%"
            :loading="loading">
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const loginFormRef = ref(null)
const userStore = useUserStore()
const loading = ref(false)

// 设置网页标题
onMounted(() => {
  document.title = '高校课程管理系统'
})

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应在3-20个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度应在6-20个字符之间', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    loading.value = true
    await loginFormRef.value.validate()
    
    const success = await userStore.login(loginForm.username, loginForm.password)
    if (success) {
      const infoSuccess = await userStore.getInfo()
      if (infoSuccess) {
        ElMessage.success('登录成功')
        
        // 根据角色重定向
        const role = userStore.role.toUpperCase()
        switch (role) {
          case 'ADMIN':
            await router.push('/admin/dashboard')
            break
          case 'STUDENT':
            await router.push('/student/dashboard')
            break
          case 'TEACHER':
            await router.push('/teacher/dashboard')
            break
          default:
            ElMessage.error('未知的用户角色')
        }
      } else {
        throw new Error('获取用户信息失败')
      }
    } else {
      ElMessage.error('登录失败，请检查用户名和密码')
    }
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('登录失败，请稍后重试')
    }
    console.error('Login error:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
}

.login-card {
  width: 400px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-title {
  margin: 0;
  font-size: 24px;
  color: #1890ff;
  text-align: center;
}

.login-form {
  padding: 20px;
}

:deep(.el-input__wrapper) {
  background-color: #f5f7fa;
}

:deep(.el-button--primary) {
  background: linear-gradient(90deg, #1890ff 0%, #36cfc9 100%);
  border: none;
  height: 40px;
  font-size: 16px;
}

:deep(.el-card__header) {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}
</style> 