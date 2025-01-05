import { defineStore } from 'pinia'
import { login, getInfo } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    name: '',
    role: localStorage.getItem('userRole')?.toUpperCase(),
    info: null
  }),

  actions: {
    async login(username, password) {
      try {
        const res = await login({ username, password })
        console.log('Login response:', res)
        if (res && res.token) {
          this.token = res.token
          setToken(res.token)
          if (res.role) {
            const role = res.role.toUpperCase()
            this.role = role
            localStorage.setItem('userRole', role)
          }
          return true
        }
        return false
      } catch (error) {
        console.error('Login failed:', error)
        return false
      }
    },

    async getInfo() {
      try {
        const res = await getInfo()
        console.log('GetInfo response:', res)
        if (res) {
          this.name = res.name
          const role = res.role.toUpperCase()
          this.role = role
          this.info = res
          localStorage.setItem('userRole', role)
          return true
        }
        return false
      } catch (error) {
        console.error('Get user info failed:', error)
        return false
      }
    },

    async logout() {
      try {
        this.resetState()
        return true
      } catch (error) {
        console.error('Logout failed:', error)
        return false
      }
    },

    resetState() {
      this.token = null
      this.name = ''
      this.role = ''
      this.info = null
      removeToken()
      localStorage.removeItem('userRole')
    }
  }
}) 