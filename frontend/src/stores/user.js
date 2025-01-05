import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getToken, setToken, removeToken } from '@/utils/auth'
import request from '@/utils/request'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken())
  const name = ref('')
  const role = ref('')

  const login = async (username, password) => {
    try {
      const res = await request.post('/api/auth/login', { username, password })
      const { token: newToken } = res.data
      token.value = newToken
      setToken(newToken)
      return true
    } catch (error) {
      console.error('登录失败:', error)
      return false
    }
  }

  const getInfo = async () => {
    try {
      const res = await request.get('/api/auth/info')
      const { name: userName, role: userRole } = res.data
      name.value = userName
      role.value = userRole
      return true
    } catch (error) {
      console.error('获取用户信息失败:', error)
      return false
    }
  }

  const logout = () => {
    token.value = ''
    name.value = ''
    role.value = ''
    removeToken()
  }

  return {
    token,
    name,
    role,
    login,
    getInfo,
    logout
  }
}) 