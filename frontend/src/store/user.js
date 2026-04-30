import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, getUserInfo } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref({})

  const handleLogin = async (loginForm) => {
    const res = await login(loginForm)
    if (res.code === 200) {
      token.value = res.data.token
      localStorage.setItem('token', res.data.token)
      userInfo.value = {
        userId: res.data.userId,
        username: res.data.username,
        realName: res.data.realName,
        role: res.data.role
      }
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
      return true
    }
    return false
  }

  const getUserInfo = async () => {
    const cachedInfo = localStorage.getItem('userInfo')
    if (cachedInfo) {
      userInfo.value = JSON.parse(cachedInfo)
      return
    }
    
    const res = await getUserInfo()
    if (res.code === 200) {
      userInfo.value = res.data
      localStorage.setItem('userInfo', JSON.stringify(res.data))
    }
  }

  const logout = () => {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/login')
  }

  const isPartner = () => userInfo.value.role === 'PARTNER'
  const isLawyer = () => userInfo.value.role === 'LAWYER' || userInfo.value.role === 'PARTNER'
  const isAssistant = () => userInfo.value.role === 'ASSISTANT'

  return {
    token,
    userInfo,
    handleLogin,
    getUserInfo,
    logout,
    isPartner,
    isLawyer,
    isAssistant
  }
})
