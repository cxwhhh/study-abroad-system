import router from '../router'
import request from './request'

class SessionManager {
  constructor() {
    this.checkInterval = null
    this.isChecking = false
    this.lastLoginTime = null // 记录最后登录时间
  }

  // 开始会话检查
  startSessionCheck() {
    if (this.checkInterval) {
      return
    }

    // 每5分钟检查一次会话状态
    this.checkInterval = setInterval(() => {
      this.checkSession()
    }, 5 * 60 * 1000)

    // 页面可见性变化时也检查会话
    document.addEventListener('visibilitychange', () => {
      if (!document.hidden) {
        this.checkSession()
      }
    })
  }

  // 停止会话检查
  stopSessionCheck() {
    if (this.checkInterval) {
      clearInterval(this.checkInterval)
      this.checkInterval = null
    }
  }

  // 检查会话状态
  async checkSession() {
    if (this.isChecking) {
      return
    }

    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
    const userInfo = localStorage.getItem('userInfo')

    // 如果本地没有登录状态，不需要检查
    if (!isLoggedIn || !userInfo) {
      return
    }

    // 如果刚刚登录（5秒内），跳过会话验证，给后端时间处理登录状态
    if (this.lastLoginTime && (Date.now() - this.lastLoginTime) < 5000) {
      console.log('刚刚登录，跳过会话验证')
      return
    }

    this.isChecking = true

    try {
      await request.get('/user/current', {
        timeout: 8000
      })
    } catch (error) {
      const isUnauthorized =
        error?.message === '用户未登录' ||
        error?.response?.status === 401

      if (isUnauthorized) {
        this.clearSession()

        const currentRoute = router.currentRoute.value
        if (currentRoute.path !== '/login' && currentRoute.matched.some(record => record.meta.requiresAuth)) {
          router.push({
            path: '/login',
            query: { redirect: currentRoute.fullPath }
          })
        }
        return
      }

      console.warn('会话验证失败，保留本地登录状态', error)
    } finally {
      this.isChecking = false
    }
  }

  // 清除会话信息
  clearSession() {
    localStorage.removeItem('isLoggedIn')
    localStorage.removeItem('userInfo')
    this.stopSessionCheck()
    if (typeof window !== 'undefined') {
      window.dispatchEvent(new CustomEvent('session-changed'))
    }
  }

  // 设置会话信息
  setSession(userInfo) {
    localStorage.setItem('isLoggedIn', 'true')
    localStorage.setItem('userInfo', JSON.stringify(userInfo))
    this.lastLoginTime = Date.now() // 记录登录时间
    this.startSessionCheck()
    if (typeof window !== 'undefined') {
      window.dispatchEvent(new CustomEvent('session-changed'))
    }
  }

  updateUserInfo(partialUserInfo) {
    const current = this.getUserInfo() || {}
    const next = { ...current, ...(partialUserInfo || {}) }
    localStorage.setItem('userInfo', JSON.stringify(next))
    if (typeof window !== 'undefined') {
      window.dispatchEvent(new CustomEvent('session-changed'))
    }
  }

  // 获取用户信息
  getUserInfo() {
    const userInfo = localStorage.getItem('userInfo')
    if (userInfo) {
      try {
        return JSON.parse(userInfo)
      } catch (e) {
        console.error('用户信息格式错误:', e)
        this.clearSession()
        return null
      }
    }
    return null
  }

  // 检查是否已登录
  isLoggedIn() {
    return localStorage.getItem('isLoggedIn') === 'true' && this.getUserInfo() !== null
  }

  // 获取用户ID
  getUserId() {
    const userInfo = this.getUserInfo()
    return userInfo ? userInfo.id : null
  }
}

// 创建单例实例
const sessionManager = new SessionManager()

export default sessionManager
