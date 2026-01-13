<template>
  <div class="login-container" @mousemove="handleMouseMove" @click="handleGlobalClick">
    <!-- 点击特效 -->
    <div v-for="sparkle in sparkles" :key="sparkle.id" class="click-sparkle"
      :style="{ left: sparkle.x + 'px', top: sparkle.y + 'px' }"></div>

    <!-- 背景装饰 -->
    <div class="bg-decoration" :style="backgroundStyle">
      <div class="orb orb-gold"></div>
      <div class="orb orb-secondary"></div>
      <div class="grid-pattern"></div>
      <!-- 漂浮的几何元素 -->
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
    </div>

    <div class="back-home" @click="goHome">
      <el-icon>
        <ArrowLeft />
      </el-icon> 返回首页
    </div>

    <div class="login-wrapper">
      <!-- 机器人吉祥物 -->
      <div class="mascot-container">
        <svg class="robot-mascot" viewBox="0 0 200 160" xmlns="http://www.w3.org/2000/svg">
          <defs>
            <filter id="glow" x="-20%" y="-20%" width="140%" height="140%">
              <feGaussianBlur stdDeviation="2" result="blur" />
              <feComposite in="SourceGraphic" in2="blur" operator="over" />
            </filter>
          </defs>
          <g class="robot-body-group" :class="{ 'head-bob': isTyping, 'loading-spin': loading }">
            <!-- 身体 -->
            <rect x="60" y="60" width="80" height="70" rx="20" fill="white" stroke="#1c1917" stroke-width="2.5" />
            <!-- 屏幕脸 -->
            <rect x="70" y="75" width="60" height="40" rx="10" fill="#1c1917" />
            <!-- 眼睛 -->
            <g class="eyes-group">
              <circle cx="90" cy="95" r="6" fill="#1c1917" />
              <circle cx="110" cy="95" r="6" fill="#1c1917" />
              <!-- 眼球/瞳孔 -->
              <circle cx="90" cy="95" r="3" fill="#C5A059" class="pupil" :style="pupilStyle" />
              <circle cx="110" cy="95" r="3" fill="#C5A059" class="pupil" :style="pupilStyle" />
            </g>
            <!-- 腮红 -->
            <circle cx="65" cy="105" r="6" fill="#C5A059" opacity="0.2" />
            <circle cx="135" cy="105" r="6" fill="#C5A059" opacity="0.2" />
            <!-- 天线 -->
            <line x1="100" y1="60" x2="100" y2="35" stroke="#1c1917" stroke-width="2.5" />
            <circle cx="100" cy="35" r="6" fill="#C5A059" stroke="#1c1917" stroke-width="2.5" />
            <!-- 天线光波 -->
            <circle cx="100" cy="35" r="10" fill="none" stroke="#C5A059" stroke-width="1" opacity="0.5">
              <animate attributeName="r" values="6;12" duration="1.5s" repeatCount="indefinite" />
              <animate attributeName="opacity" values="0.8;0" duration="1.5s" repeatCount="indefinite" />
            </circle>
          </g>
          <!-- 手 (动态绑定位置) -->
          <circle :cx="isPasswordFocused ? 85 : 50" :cy="isPasswordFocused ? 90 : 100" r="10" fill="white"
            stroke="#1c1917" stroke-width="2.5" class="hand hand-left"
            :class="{ 'hand-covering': isPasswordFocused }" />
          <circle :cx="isPasswordFocused ? 115 : 150" :cy="isPasswordFocused ? 90 : 100" r="10" fill="white"
            stroke="#1c1917" stroke-width="2.5" class="hand hand-right"
            :class="{ 'hand-covering': isPasswordFocused }" />
        </svg>
      </div>

      <form class="form" @submit.prevent="handleSubmit" :style="cardTransform">
        <div class="brand-header">
          <div class="logo-container">
            <div class="logo-circle">X</div>
            <div class="logo-ring"></div>
          </div>
          <div class="brand-text">
            <span class="brand-name">XINYINGMEI</span>
            <span class="brand-slogan">AI 驱动 · 智领留学</span>
          </div>
        </div>

        <h2 class="title">{{ isLogin ? '欢迎回来' : '创建账户' }}</h2>

        <!-- 账户输入框 -->
        <div class="flex-column">
          <label>账户</label>
          <div class="inputForm">
            <el-icon>
              <User />
            </el-icon>
            <input v-model="form.username" :placeholder="isLogin ? '请输入账户' : '设置您的账户名'" class="input" type="text"
              @input="handleTyping">
          </div>
        </div>

        <!-- 邮箱输入框 (仅注册时显示) -->
        <Transition name="fade-slide">
          <div class="flex-column" v-if="!isLogin">
            <label>邮箱</label>
            <div class="inputForm">
              <el-icon>
                <Message />
              </el-icon>
              <input v-model="form.email" placeholder="请输入邮箱地址" class="input" type="email">
            </div>
          </div>
        </Transition>

        <!-- 密码输入框 -->
        <div class="flex-column">
          <label>密码</label>
          <div class="inputForm">
            <el-icon>
              <Lock />
            </el-icon>
            <input v-model="form.password" :placeholder="isLogin ? '请输入密码' : '设置您的密码'" class="input"
              :type="showPassword ? 'text' : 'password'" @focus="isPasswordFocused = true"
              @blur="isPasswordFocused = false">
          </div>
        </div>

        <!-- 确认密码（仅注册时显示） -->
        <Transition name="fade-slide">
          <div class="flex-column" v-if="!isLogin">
            <label>确认密码</label>
            <div class="inputForm">
              <el-icon>
                <Lock />
              </el-icon>
              <input v-model="form.confirmPassword" placeholder="请再次输入密码" class="input"
                :type="showPassword ? 'text' : 'password'" @focus="isPasswordFocused = true"
                @blur="isPasswordFocused = false">
            </div>
          </div>
        </Transition>

        <!-- 记住密码和忘记密码（仅登录时显示） -->
        <Transition name="fade-slide">
          <div class="flex-row" v-if="isLogin">
            <div class="checkbox-wrapper">
              <input type="checkbox" v-model="rememberMe" id="remember">
              <label for="remember">记住我</label>
            </div>
            <span class="span" @click="handleForgotPassword">忘记密码?</span>
          </div>
        </Transition>

        <!-- 提交按钮 -->
        <button class="button-submit" :disabled="loading">
          {{ loading ? '处理中...' : (isLogin ? '立即登录' : '注册账户') }}
        </button>

        <!-- 切换登录/注册 -->
        <p class="p switch-mode">
          {{ isLogin ? '还没有账户?' : '已有账户?' }}
          <span class="span link" @click="toggleMode">{{ isLogin ? '立即注册' : '去登录' }}</span>
        </p>

        <!-- 第三方登录 -->
        <Transition name="fade-slide">
          <div v-if="isLogin">
            <div class="divider">
              <span>其他登录方式</span>
            </div>
            <div class="social-login">
              <button type="button" class="btn-social wechat" @click="openWeChatDialog">
                <el-icon>
                  <ElementPlus />
                </el-icon>
                <span>微信</span>
              </button>
              <button type="button" class="btn-social apple" @click="handleAppleLogin">
                <el-icon>
                  <Apple />
                </el-icon>
                <span>Apple</span>
              </button>
            </div>
          </div>
        </Transition>
      </form>
    </div>

    <!-- 忘记密码弹窗 -->
    <el-dialog v-model="forgotPasswordVisible" title="找回密码" width="400px" :close-on-click-modal="false"
      :show-close="true" class="custom-dialog">
      <div class="reset-password-form">
        <!-- 步骤提示 -->
        <div class="steps-indicator">
          <div :class="['step', { active: resetStep === 1, completed: resetStep > 1 }]">
            <div class="step-circle">1</div>
            <div class="step-label">验证账号</div>
          </div>
          <div class="step-line"></div>
          <div :class="['step', { active: resetStep === 2, completed: resetStep > 2 }]">
            <div class="step-circle">2</div>
            <div class="step-label">验证身份</div>
          </div>
          <div class="step-line"></div>
          <div :class="['step', { active: resetStep === 3 }]">
            <div class="step-circle">3</div>
            <div class="step-label">重置密码</div>
          </div>
        </div>

        <!-- 第1步：验证账号 -->
        <div v-if="resetStep === 1">
          <div class="flex-column">
            <label>请输入您的账号</label>
            <div class="inputForm">
              <el-icon>
                <User />
              </el-icon>
              <input v-model="resetForm.username" placeholder="请输入注册时的账号" class="input" type="text">
            </div>
          </div>
          <div class="flex-column">
            <label>请输入您的邮箱</label>
            <div class="inputForm">
              <el-icon>
                <Message />
              </el-icon>
              <input v-model="resetForm.email" placeholder="请输入绑定的邮箱地址" class="input" type="email">
            </div>
          </div>
          <button class="button-submit" :disabled="resetLoading" @click="verifyAccount">
            {{ resetLoading ? '验证中...' : '下一步' }}
          </button>
        </div>

        <!-- 第2步：验证身份 -->
        <div v-if="resetStep === 2">
          <div class="verification-message">
            <p>验证码已发送到 <strong>{{ resetForm.email }}</strong></p>
            <p>请输入您收到的6位验证码：</p>
          </div>

          <div class="verification-code-container">
            <input v-for="(digit, index) in 6" :key="index" v-model="resetForm.verificationCode[index]"
              class="verification-digit" maxlength="1" @input="handleVerificationInput($event, index)"
              @keydown="handleVerificationKeydown($event, index)" ref="verificationInputs">
          </div>

          <div class="resend-code">
            <p v-if="countdown > 0">{{ countdown }}秒后可重新发送</p>
            <p v-else class="span" @click="resendVerificationCode">重新发送验证码</p>
          </div>

          <button class="button-submit" :disabled="resetLoading" @click="verifyCode">
            {{ resetLoading ? '验证中...' : '下一步' }}
          </button>
        </div>

        <!-- 第3步：重置密码 -->
        <div v-if="resetStep === 3">
          <div class="flex-column">
            <label>新密码</label>
            <div class="inputForm">
              <el-icon>
                <Lock />
              </el-icon>
              <input v-model="resetForm.newPassword" placeholder="请输入新密码" class="input"
                :type="showResetPassword ? 'text' : 'password'">
            </div>
          </div>

          <div class="flex-column">
            <label>确认新密码</label>
            <div class="inputForm">
              <el-icon>
                <Lock />
              </el-icon>
              <input v-model="resetForm.confirmNewPassword" placeholder="请再次输入新密码" class="input"
                :type="showResetPassword ? 'text' : 'password'">
            </div>
          </div>

          <div class="password-toggle">
            <input type="checkbox" id="showPassword" v-model="showResetPassword">
            <label for="showPassword">显示密码</label>
          </div>

          <button class="button-submit" :disabled="resetLoading" @click="resetPassword">
            {{ resetLoading ? '重置中...' : '重置密码' }}
          </button>
        </div>

        <!-- 返回登录 -->
        <div class="back-to-login" v-if="resetStep < 3">
          <p @click="forgotPasswordVisible = false">返回登录</p>
        </div>
      </div>
    </el-dialog>

    <!-- 微信扫码登录弹窗 -->
    <el-dialog v-model="wechatDialogVisible" title="微信扫码登录" width="380px" :close-on-click-modal="false"
      class="custom-dialog">
      <div style="display:flex;justify-content:center;align-items:center;">
        <div id="wechat-qr-login"
          style="width:260px;height:260px;border:1px dashed #e5e7eb;border-radius:8px;display:flex;align-items:center;justify-content:center;">
        </div>
      </div>
      <template #footer>
        <el-button @click="initWeChatQr" :loading="wechatLoading">刷新二维码</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { User, Lock, ElementPlus, Apple, Message, ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import CryptoJS from 'crypto-js'
import sessionManager from '../utils/sessionManager'

// 路由设置
const router = useRouter()
const route = useRoute()

const goHome = () => {
  router.push('/')
}

// 鼠标交互效果
const mouseX = ref(0)
const mouseY = ref(0)

const handleMouseMove = (e) => {
  const { clientX, clientY } = e
  const { innerWidth, innerHeight } = window
  // 归一化坐标 (-1 到 1)
  mouseX.value = (clientX / innerWidth) * 2 - 1
  mouseY.value = (clientY / innerHeight) * 2 - 1
}

const backgroundStyle = computed(() => {
  return {
    '--mouse-x': mouseX.value,
    '--mouse-y': mouseY.value
  }
})

const cardTransform = computed(() => {
  const rotateX = -mouseY.value * 5 // Max 5 degrees
  const rotateY = mouseX.value * 5
  return {
    transform: `perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg)`
  }
})

const pupilStyle = computed(() => {
  // 瞳孔移动范围限制
  const limit = 2
  return {
    transform: `translate(${mouseX.value * limit}px, ${mouseY.value * limit}px) scaleY(${isBlinking.value ? 0.1 : 1})`
  }
})

// 机器人状态
const isBlinking = ref(false)
const isTyping = ref(false)
let typingTimer = null

// 眨眼动画循环
const startBlinking = () => {
  setInterval(() => {
    isBlinking.value = true
    setTimeout(() => {
      isBlinking.value = false
    }, 200)
  }, 3500 + Math.random() * 2000)
}

// 输入时的头部晃动
const handleTyping = () => {
  isTyping.value = true
  if (typingTimer) clearTimeout(typingTimer)
  typingTimer = setTimeout(() => {
    isTyping.value = false
  }, 500)
}

// 点击特效
const sparkles = ref([])
const handleGlobalClick = (e) => {
  const id = Date.now()
  sparkles.value.push({
    id,
    x: e.clientX,
    y: e.clientY
  })
  setTimeout(() => {
    sparkles.value = sparkles.value.filter(s => s.id !== id)
  }, 1000)
}

// 响应式状态
const loading = ref(false)
const showPassword = ref(false)
const rememberMe = ref(true) // 默认记住登录状态
const isLogin = ref(true)

const isPasswordFocused = ref(false)

// 表单数据
const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: ''
})

// 忘记密码相关状态
const forgotPasswordVisible = ref(false)
const resetStep = ref(1)
const resetLoading = ref(false)
const showResetPassword = ref(false)
const countdown = ref(0)
const verificationInputs = ref([])

// 重置密码表单数据
const resetForm = reactive({
  username: '',
  verificationCode: ['', '', '', '', '', ''],
  newPassword: '',
  confirmNewPassword: '',
  token: '',
  email: ''
})

// 配置axios默认值
const axiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  withCredentials: true
})

// 添加请求拦截器 - 仅用于日志记录
axiosInstance.interceptors.request.use(
  config => {
    console.log('发送请求配置:', config)
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 表单验证
const rules = {
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ]
}

const validateForm = () => {
  if (!form.username) {
    ElMessage.warning('请输入账号')
    return false
  }
  if (!isLogin.value && !form.email) {
    ElMessage.warning('请输入邮箱')
    return false
  }
  if (!isLogin.value && !/^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(form.email)) {
    ElMessage.warning('请输入正确的邮箱格式')
    return false
  }
  if (!form.password) {
    ElMessage.warning('请输入密码')
    return false
  }
  if (form.password.length < 6) {
    ElMessage.warning('密码长度不能小于6位')
    return false
  }
  if (!isLogin.value && !form.confirmPassword) {
    ElMessage.warning('请确认密码')
    return false
  }
  if (!isLogin.value && form.password !== form.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return false
  }
  return true
}

// 恢复加密密钥
const ENCRYPTION_KEY = 'your-secret-key'

// 恢复加密函数
const encrypt = (text) => {
  return CryptoJS.AES.encrypt(text, ENCRYPTION_KEY).toString()
}

// 恢复解密函数
const decrypt = (ciphertext) => {
  const bytes = CryptoJS.AES.decrypt(ciphertext, ENCRYPTION_KEY)
  return bytes.toString(CryptoJS.enc.Utf8)
}

// 保存账号密码（使用加密）
const saveCredentials = () => {
  if (rememberMe.value) {
    const credentials = {
      username: form.username,
      password: encrypt(form.password)
    }
    localStorage.setItem('userCredentials', JSON.stringify(credentials))
  } else {
    localStorage.removeItem('userCredentials')
  }
}

// 读取保存的账号密码（使用解密）
const loadCredentials = () => {
  const savedCredentials = localStorage.getItem('userCredentials')
  if (savedCredentials) {
    const credentials = JSON.parse(savedCredentials)
    form.username = credentials.username
    form.password = decrypt(credentials.password)
    rememberMe.value = true
  }
}

// 检查用户是否已经登录
const checkLoginStatus = () => {
  const userInfo = localStorage.getItem('userInfo')
  const isLoggedIn = localStorage.getItem('isLoggedIn')

  if (userInfo && isLoggedIn === 'true') {
    console.log('用户已登录，自动跳转到个人页面')
    router.push('/')
    return true
  }
  return false
}

// 处理注册
const handleRegister = async () => {
  try {
    console.log('开始注册请求，数据:', {
      username: form.username,
      password: form.password,
      email: form.email
    })

    const response = await axiosInstance.post('/user/register', {
      username: form.username,
      password: form.password,
      email: form.email
    })

    console.log('注册响应:', response.data)

    ElMessage.success('注册成功')
    isLogin.value = true
    form.confirmPassword = ''
    form.email = ''
  } catch (error) {
    console.error('注册错误详情:', error)
    if (error.response) {
      ElMessage.error(error.response.data.message || '注册失败，请重试')
    } else {
      ElMessage.error('注册失败，请检查网络连接')
    }
  }
}

// 记录来源页面的方法
const recordPreviousPage = async () => {
  try {
    const currentPath = window.location.pathname + window.location.search
    await axiosInstance.get('/user/record-previous-page', {
      params: { url: currentPath }
    })
    console.log('已记录来源页面:', currentPath)
  } catch (error) {
    console.error('记录来源页面失败:', error)
  }
}

// 修改handleLogin方法
const handleLogin = async () => {
  try {
    loading.value = true
    const response = await axiosInstance.post('/user/login', {
      username: form.username,
      password: form.password
    })

    console.log('登录响应:', response.data)

    if (response.data.code === 200) {
      // 无论是否选择记住密码，都保存登录状态
      saveCredentials()

      // 保存用户信息
      const responseData = response.data.data
      const userInfo = {
        ...responseData.user,
        userId: responseData.userId,
        studentId: responseData.studentId
      }

      // 使用会话管理器设置登录状态
      sessionManager.setSession(userInfo)

      // 触发登录状态更新事件
      window.dispatchEvent(new CustomEvent('login-state-changed', {
        detail: { isLoggedIn: true }
      }))

      ElMessage.success('登录成功')

      // 等待一小段时间确保状态更新完成
      await new Promise(resolve => setTimeout(resolve, 100))

      // 获取重定向URL，默认跳转到首页
      const redirectUrl = route.query.redirect === '/profile' ? '/' : (route.query.redirect || '/')
      console.log('准备跳转到页面:', redirectUrl)

      try {
        // 使用replace而不是push，避免在历史记录中留下登录页面
        await router.replace(redirectUrl)
        console.log('路由跳转成功')
      } catch (routerError) {
        console.error('路由跳转失败:', routerError)
        // 如果跳转失败，尝试使用window.location
        try {
          window.location.href = redirectUrl
        } catch (locationError) {
          console.error('window.location跳转也失败:', locationError)
          ElMessage.error('页面跳转失败，请手动访问个人中心')
          // 显示手动跳转提示
          ElMessage({
            message: '登录成功！请点击右上角头像进入个人中心',
            type: 'success',
            duration: 5000
          })
        }
      }
    } else {
      ElMessage.error(response.data.message || '登录失败')
    }
  } catch (error) {
    console.error('登录错误:', error)
    // 只显示一次错误消息，避免重复
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('登录失败，请检查网络连接')
    }
  } finally {
    loading.value = false
  }
}

// 添加安全验证方法
const isValidRedirect = (url) => {
  const allowedDomains = ['http://localhost:3000', 'http://yourdomain.com']
  return allowedDomains.some(domain => url.startsWith(domain))
}

// 提交处理
const handleSubmit = async () => {
  if (!validateForm()) return

  try {
    loading.value = true
    if (isLogin.value) {
      await handleLogin()
    } else {
      await handleRegister()
    }
  } finally {
    loading.value = false
  }
}

// 切换登录/注册模式
const toggleMode = () => {
  isLogin.value = !isLogin.value
  form.confirmPassword = ''
}

// 忘记密码处理
const handleForgotPassword = () => {
  resetForm.username = form.username
  forgotPasswordVisible.value = true
  resetStep.value = 1
}

// 验证码输入处理
const handleVerificationInput = (event, index) => {
  const value = event.target.value
  if (value && index < 5) {
    verificationInputs.value[index + 1]?.focus()
  }
}

// 验证码输入键盘处理
const handleVerificationKeydown = (event, index) => {
  // 处理退格键
  if (event.key === 'Backspace' && !resetForm.verificationCode[index] && index > 0) {
    verificationInputs.value[index - 1]?.focus()
  }
}

// 倒计时函数
const startCountdown = () => {
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

// 验证账号
const verifyAccount = async () => {
  if (!resetForm.username) {
    ElMessage.warning('请输入账号')
    return
  }

  if (!resetForm.email) {
    ElMessage.warning('请输入邮箱')
    return
  }

  if (!/^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(resetForm.email)) {
    ElMessage.warning('请输入正确的邮箱格式')
    return
  }

  try {
    resetLoading.value = true
    // 调用后端API验证账号是否存在
    const response = await axiosInstance.post('/user/forgot-password/verify-account', {
      username: resetForm.username,
      email: resetForm.email
    })

    if (response.data.code === 200) {
      ElMessage.success('账号验证成功，验证码已发送')
      resetStep.value = 2
      startCountdown()
    } else {
      ElMessage.error(response.data.message || '账号验证失败')
    }
  } catch (error) {
    console.error('账号验证错误:', error)
    ElMessage.error(error.response?.data?.message || '账号验证失败，请确认账号和邮箱是否正确')
  } finally {
    resetLoading.value = false
  }
}

// 重新发送验证码
const resendVerificationCode = async () => {
  if (countdown.value > 0) return

  try {
    resetLoading.value = true
    const response = await axiosInstance.post('/user/forgot-password/resend-code', {
      username: resetForm.username,
      email: resetForm.email
    })

    if (response.data.code === 200) {
      ElMessage.success('验证码已重新发送')
      startCountdown()
    } else {
      ElMessage.error(response.data.message || '验证码发送失败')
    }
  } catch (error) {
    console.error('重新发送验证码错误:', error)
    ElMessage.error(error.response?.data?.message || '验证码发送失败，请稍后再试')
  } finally {
    resetLoading.value = false
  }
}

// 验证验证码
const verifyCode = async () => {
  const code = resetForm.verificationCode.join('')
  if (code.length !== 6) {
    ElMessage.warning('请输入完整的6位验证码')
    return
  }

  try {
    resetLoading.value = true
    const response = await axiosInstance.post('/user/forgot-password/verify-code', {
      username: resetForm.username,
      code
    })

    if (response.data.code === 200) {
      ElMessage.success('验证码验证成功')
      resetForm.token = response.data.data.token
      resetStep.value = 3
    } else {
      ElMessage.error(response.data.message || '验证码验证失败')
    }
  } catch (error) {
    console.error('验证码验证错误:', error)
    ElMessage.error(error.response?.data?.message || '验证码验证失败，请检查验证码是否正确')
  } finally {
    resetLoading.value = false
  }
}

// 重置密码
const resetPassword = async () => {
  if (!resetForm.newPassword) {
    ElMessage.warning('请输入新密码')
    return
  }

  if (resetForm.newPassword.length < 6) {
    ElMessage.warning('密码长度不能小于6位')
    return
  }

  if (resetForm.newPassword !== resetForm.confirmNewPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }

  try {
    resetLoading.value = true
    const response = await axiosInstance.post('/user/forgot-password/reset', {
      username: resetForm.username,
      token: resetForm.token,
      newPassword: resetForm.newPassword
    })

    if (response.data.code === 200) {
      ElMessage.success('密码重置成功，请使用新密码登录')
      forgotPasswordVisible.value = false
      form.password = ''
    } else {
      ElMessage.error(response.data.message || '密码重置失败')
    }
  } catch (error) {
    console.error('密码重置错误:', error)
    ElMessage.error(error.response?.data?.message || '密码重置失败，请稍后再试')
  } finally {
    resetLoading.value = false
  }
}

// 微信扫码登录
const wechatDialogVisible = ref(false)
const wechatLoading = ref(false)
let wechatState = ''
let wechatPollTimer = null
let wechatPopup = null

const loadScript = (src) => new Promise((resolve, reject) => {
  const s = document.createElement('script')
  s.src = src
  s.onload = resolve
  s.onerror = reject
  document.head.appendChild(s)
})

const openWeChatDialog = async () => {
  wechatDialogVisible.value = true
  await initWeChatQr()
}

const initWeChatQr = async () => {
  try {
    wechatLoading.value = true
    const res = await fetch('/api/auth/wechat/prepare', { method: 'POST' })
    if (!res.ok) {
      throw new Error(`prepare request failed: ${res.status}`)
    }
    const j = await res.json()
    if (!j || !j.state || !j.appid || !j.redirectUri) {
      throw new Error('prepare response missing fields')
    }
    wechatState = j.state
    const appid = j.appid
    const redirectUri = encodeURIComponent(j.redirectUri)
    // 每次刷新二维码先清空容器
    const qrBox = document.getElementById('wechat-qr-login')
    if (qrBox) qrBox.innerHTML = ''

    let rendered = false
    try {
      if (!window.WxLogin) {
        await loadScript('https://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js')
      }
      // eslint-disable-next-line no-new
      new WxLogin({ id: 'wechat-qr-login', appid, scope: 'snsapi_login', state: wechatState, redirect_uri: redirectUri, self_redirect: true, style: 'black' })
      // 检查是否成功渲染出 iframe
      await new Promise(resolve => setTimeout(resolve, 300))
      rendered = !!(qrBox && qrBox.querySelector('iframe'))
    } catch (e) {
      rendered = false
    }

    if (!rendered) {
      // 回退方案：打开微信官方二维码页面
      const url = `https://open.weixin.qq.com/connect/qrconnect?appid=${appid}&scope=snsapi_login&redirect_uri=${redirectUri}&state=${wechatState}&login_type=jssdk&self_redirect=true`
      wechatPopup = window.open(url, 'wechat_login', 'width=480,height=640,noopener')
    }
    // 轮询状态
    wechatPollTimer && clearInterval(wechatPollTimer)
    wechatPollTimer = setInterval(async () => {
      if (!wechatState) return
      const r = await fetch(`/api/auth/wechat/status?state=${encodeURIComponent(wechatState)}`)
      if (!r.ok) return
      const s = await r.json()
      if (s.status === 'success' && s.token) {
        clearInterval(wechatPollTimer)
        wechatDialogVisible.value = false
        ElMessage.success('微信登录成功')
        const userInfo = { id: s.token, username: '微信用户' }
        sessionManager.setSession(userInfo)
        const redirectUrl = route.query.redirect || '/'
        await router.replace(redirectUrl)
      }
    }, 1500)
  } catch (e) {
    console.error('WeChat QR init error:', e)
    ElMessage.error('微信二维码生成失败，请稍后再试')
  } finally {
    wechatLoading.value = false
  }
}
const handleAppleLogin = () => ElMessage.info('Apple登录功能开发中')

// 监听路由变化
watch(() => route.query, (newQuery) => {
  console.log('路由 query 变化:', newQuery) // 添加调试日志
})

// 在组件挂载时先检查登录状态
onMounted(() => {
  startBlinking()

  // 检查用户是否已登录
  if (!checkLoginStatus()) {
    // 如果未登录，尝试加载保存的凭据
    loadCredentials()
  }

  if (router.options.history.state.back) {
    recordPreviousPage()
  }
})
</script>

<style scoped>
/* 全局字体调整 */
:deep(*) {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif !important;
}

.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #F9F8F4;
  position: relative;
  overflow: hidden;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  pointer-events: none;
}

.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.4;
  animation: pulse 8s infinite alternate;
  transition: top 0.1s ease-out, left 0.1s ease-out, right 0.1s ease-out, bottom 0.1s ease-out;
}

.orb-gold {
  width: 400px;
  height: 400px;
  background: #C5A059;
  top: calc(-100px + var(--mouse-y) * 20px);
  left: calc(-100px + var(--mouse-x) * 20px);
  opacity: 0.15;
}

.orb-secondary {
  width: 500px;
  height: 500px;
  background: #E8DCC5;
  bottom: calc(-150px - var(--mouse-y) * 20px);
  right: calc(-150px - var(--mouse-x) * 20px);
  opacity: 0.1;
  animation-delay: 2s;
}

.grid-pattern {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: linear-gradient(rgba(197, 160, 89, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(197, 160, 89, 0.03) 1px, transparent 1px);
  background-size: 50px 50px;
  z-index: -1;
}

.floating-shape {
  position: absolute;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  z-index: 1;
  transition: top 0.1s ease-out, left 0.1s ease-out, right 0.1s ease-out, bottom 0.1s ease-out;
}

.shape-1 {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  top: calc(20% + var(--mouse-y) * 10px);
  left: calc(15% + var(--mouse-x) * 10px);
  transform: rotate(15deg);
  animation: float 6s ease-in-out infinite;
}

.shape-2 {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  bottom: calc(30% - var(--mouse-y) * 15px);
  right: calc(20% - var(--mouse-x) * 15px);
  animation: float 5s ease-in-out infinite reverse;
}

/* 返回按钮 */
.back-home {
  position: absolute;
  top: 40px;
  left: 40px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 15px;
  color: #57534e;
  font-weight: 500;
  transition: all 0.3s;
  z-index: 20;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 20px;
  backdrop-filter: blur(5px);
}

.back-home:hover {
  color: #C5A059;
  background: white;
  transform: translateX(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

/* 登录框容器 */
.login-wrapper {
  position: relative;
  z-index: 10;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 机器人吉祥物 */
.mascot-container {
  width: 140px;
  height: 120px;
  margin-bottom: -40px;
  /* 让机器人下半身稍微被卡片遮挡一点，营造立体感 */
  z-index: 15;
  position: relative;
  animation: float 4s ease-in-out infinite;
  filter: drop-shadow(0 10px 15px rgba(0, 0, 0, 0.1));
  transition: transform 0.1s ease-out;
  transform: translate(calc(var(--mouse-x) * -5px), calc(var(--mouse-y) * -5px));
}

.robot-mascot {
  width: 100%;
  height: 100%;
}

.pupil {
  transition: transform 0.1s ease-out;
  transform-box: fill-box;
  transform-origin: center;
}

.robot-body-group {
  transform-origin: center;
  /* animation: sway 4s ease-in-out infinite; */
}

.hand {
  transition: cx 0.4s cubic-bezier(0.34, 1.56, 0.64, 1), cy 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.hand-left {
  animation: wave 3s ease-in-out infinite;
  transform-origin: 50px 100px;
}

.hand-right {
  animation: wave 3s ease-in-out infinite reverse;
  transform-origin: 150px 100px;
}

.hand-covering {
  animation: none;
}

/* 过渡动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  max-height: 200px;
  opacity: 1;
  overflow: hidden;
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  max-height: 0;
  transform: translateY(-10px);
  margin-bottom: 0;
}

/* 表单样式 */
.form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  padding: 50px 40px 40px;
  width: 400px;
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.05),
    0 0 0 1px rgba(197, 160, 89, 0.1);
  position: relative;
  z-index: 10;
  animation: slideUpFade 0.8s cubic-bezier(0.2, 0.8, 0.2, 1) backwards;
}

@keyframes slideUpFade {
  from {
    opacity: 0;
    transform: translateY(40px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.brand-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 0.5rem;
}

.logo-container {
  position: relative;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-circle {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #C5A059 0%, #b08d4b 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 20px;
  z-index: 2;
  box-shadow: 0 4px 10px rgba(197, 160, 89, 0.3);
}

.logo-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border: 1px solid #C5A059;
  border-radius: 50%;
  opacity: 0.3;
  animation: spin 10s linear infinite;
}

.logo-ring::before {
  content: '';
  position: absolute;
  top: -2px;
  left: 50%;
  width: 4px;
  height: 4px;
  background: #C5A059;
  border-radius: 50%;
  transform: translateX(-50%);
}

.brand-text {
  display: flex;
  flex-direction: column;
}

.brand-name {
  font-size: 18px;
  color: #1c1917;
  font-weight: 700;
  letter-spacing: 0.05em;
  line-height: 1.2;
  font-family: 'Times New Roman', serif;
}

.brand-slogan {
  font-size: 11px;
  color: #86868b;
  letter-spacing: 0.1em;
  text-transform: uppercase;
}

.title {
  text-align: center;
  margin-bottom: 10px;
  color: #1c1917;
  font-weight: 600;
  font-size: 20px;
  font-family: 'Times New Roman', serif;
}

/* 输入框样式 */
.flex-column>label {
  color: #57534e;
  font-weight: 500;
  font-size: 13px;
  margin-bottom: 6px;
  display: block;
}

.inputForm {
  border: 1px solid #e7e5e4;
  border-radius: 12px;
  height: 48px;
  display: flex;
  align-items: center;
  padding-left: 14px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-sizing: border-box;
  background-color: #fcfcfc;
}

.inputForm:hover {
  background-color: white;
  border-color: #d6d3d1;
}

.inputForm:focus-within {
  border-color: #C5A059;
  background-color: white;
  box-shadow: 0 4px 12px rgba(197, 160, 89, 0.15);
  transform: translateY(-2px);
}

.input {
  margin-left: 10px;
  border: none;
  width: 100%;
  height: 100%;
  font-size: 14px;
  background: transparent;
  color: #1c1917;
  padding-right: 14px;
}

.input:focus {
  outline: none;
}

::placeholder {
  color: #a8a29e;
  font-size: 13px;
}

/* 其他元素 */
.flex-row {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  margin-top: 5px;
}

.checkbox-wrapper {
  display: flex;
  align-items: center;
  gap: 6px;
}

.checkbox-wrapper label {
  font-size: 13px;
  color: #57534e;
  cursor: pointer;
}

.span {
  font-size: 13px;
  color: #C5A059;
  font-weight: 500;
  cursor: pointer;
  transition: color 0.2s;
}

.span:hover {
  color: #b08d4b;
  text-decoration: underline;
}

.button-submit {
  margin: 24px 0 16px 0;
  background: linear-gradient(135deg, #1c1917 0%, #44403c 100%);
  border: none;
  color: white;
  font-size: 15px;
  font-weight: 600;
  border-radius: 12px;
  height: 48px;
  width: 100%;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  letter-spacing: 0.05em;
  position: relative;
  overflow: hidden;
}

.button-submit::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.button-submit:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
  background: linear-gradient(135deg, #000000 0%, #292524 100%);
}

.button-submit:hover::after {
  left: 100%;
}

.button-submit:active {
  transform: translateY(0);
}

.button-submit:disabled {
  background: #d6d3d1;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.switch-mode {
  text-align: center;
  color: #78716c;
  font-size: 13px;
  margin: 0;
}

/* 社交登录 */
.divider {
  display: flex;
  align-items: center;
  margin: 24px 0 16px;
  color: #a8a29e;
  font-size: 12px;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #e7e5e4;
}

.divider span {
  padding: 0 10px;
}

.social-login {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.btn-social {
  height: 42px;
  border-radius: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: 500;
  font-size: 13px;
  gap: 8px;
  border: 1px solid #e7e5e4;
  background-color: white;
  cursor: pointer;
  transition: all 0.2s ease;
  color: #57534e;
}

.btn-social:hover {
  border-color: #d6d3d1;
  background-color: #fcfcfc;
  transform: translateY(-1px);
}

.btn-social.wechat:hover {
  color: #07c160;
  border-color: #07c160;
  background-color: rgba(7, 193, 96, 0.05);
}

.btn-social.apple:hover {
  color: #000;
  border-color: #000;
  background-color: rgba(0, 0, 0, 0.05);
}

/* 图标样式调整 */
:deep(.el-icon) {
  font-size: 18px;
  color: #a8a29e;
  transition: color 0.3s;
}

.inputForm:focus-within :deep(.el-icon) {
  color: #C5A059;
}

/* 3D特效 */
.form {
  transform-style: preserve-3d;
  will-change: transform;
}

/* 机器人动画 */
.head-bob {
  animation: headBob 0.5s ease-in-out infinite;
}

.loading-spin {
  animation: loadingSpin 1s linear infinite;
  transform-origin: center;
}

/* 点击特效 */
.click-sparkle {
  position: fixed;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: radial-gradient(circle, #C5A059 0%, transparent 70%);
  transform: translate(-50%, -50%) scale(0);
  animation: sparkleAnim 0.8s ease-out forwards;
  pointer-events: none;
  z-index: 9999;
}

/* 动画定义 */
@keyframes headBob {

  0%,
  100% {
    transform: translateY(0);
  }

  50% {
    transform: translateY(3px);
  }
}

@keyframes loadingSpin {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

@keyframes sparkleAnim {
  0% {
    transform: translate(-50%, -50%) scale(0);
    opacity: 1;
  }

  50% {
    transform: translate(-50%, -50%) scale(1.5);
    opacity: 0.5;
  }

  100% {
    transform: translate(-50%, -50%) scale(2);
    opacity: 0;
  }
}

@keyframes float {

  0%,
  100% {
    transform: translateY(0);
  }

  50% {
    transform: translateY(-10px);
  }
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 0.4;
  }

  100% {
    transform: scale(1.1);
    opacity: 0.6;
  }
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

@keyframes wave {

  0%,
  100% {
    transform: rotate(0deg);
  }

  25% {
    transform: rotate(-10deg);
  }

  75% {
    transform: rotate(10deg);
  }
}

/* 忘记密码弹窗样式调整 */
.steps-indicator {
  margin-bottom: 30px;
}

.step-circle {
  width: 28px;
  height: 28px;
  font-size: 14px;
}

/* 自定义 Dialog 样式 */
:deep(.custom-dialog) {
  border-radius: 20px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  padding: 20px 20px 10px;
  margin-right: 0;
  text-align: center;
}

:deep(.el-dialog__title) {
  font-weight: 600;
  font-size: 18px;
}

:deep(.el-dialog__body) {
  padding: 10px 30px 30px;
}
</style>
/* 响应式适配 */
@media (max-width: 480px) {
.form {
width: 100%;
padding: 30px 20px;
margin: 0 20px;
}

.mascot-container {
width: 100px;
height: 90px;
margin-bottom: -30px;
}

.back-home {
top: 20px;
left: 20px;
}
}
