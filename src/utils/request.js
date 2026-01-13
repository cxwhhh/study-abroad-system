import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  //- 正确： request.post('/adminuser/filter') → 实际 /api/adminuser/filter
  //-错误： request.post('/api/messages') → 实际会变 / api / api / messages （双重前缀，通常直接 404）
  baseURL: '/api',  // 使用相对路径，让Nginx代理处理API请求，用 request 时： 路径不要再带 /api

  /* 用默认 axios 或 fetch 时：如果你依赖代理/反代，通常需要自己写 /api/...
   -例如 fetch('/api/files/...') 、 axios.get('/api/schools')
  */
  timeout: 60000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    console.log('发送请求:', config.url)
    console.log('发送请求配置:', config)

    if (typeof config.url === 'string' && (config.url === '/api' || config.url.startsWith('/api/'))) {
      config.url = config.url.replace(/^\/api/, '')
    }

    // 后端使用Session认证，不需要添加Authorization头
    // Session cookie会自动通过withCredentials传递

    // 确保参数正确序列化
    if (config.params) {
      Object.keys(config.params).forEach(key => {
        if (config.params[key] === null || config.params[key] === undefined) {
          delete config.params[key]
        }
      })
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    // 打印响应数据，用于调试
    console.log('响应数据:', response.data)
    // 2. 重点：这里必须返回 'res' (即 response.data)，而不是 'response'
    //    这样前端组件拿到的就是 { success: true, data: [...] }，而不是 Axios 包装对象
    const res = response.data
    if (res.code === 200 || res.success === true) {
      return res
    } else {
      // 处理业务错误
      if (res.code === 500 && res.message === '用户未登录') {
        // 用户未登录，清除用户信息并跳转到登录页面
        localStorage.removeItem('userInfo')
        localStorage.removeItem('isLoggedIn')

        // 避免在当前是登录页面时重定向
        if (router.currentRoute.value.path !== '/login') {
          router.push('/login')
        }

        return Promise.reject(new Error('用户未登录'))
      }
      // 不在这里显示错误消息，让组件自己处理
      return Promise.reject(new Error(res.message || '操作失败'))
    }
  },
  error => {
    console.error('响应错误:', error)
    // 处理网络错误或服务器错误

    const serverMessage = error.response?.data?.message
    if (serverMessage) {
      error.message = serverMessage
    } else if (error.code === 'ECONNABORTED' || error.message?.includes('timeout')) {
      error.message = '请求超时，请稍后重试'
    }

    if (error.response?.status === 401) {
      // 未授权，清除用户信息并跳转到登录页面
      localStorage.removeItem('userInfo')
      localStorage.removeItem('isLoggedIn')

      // 避免在当前是登录页面时重定向
      if (router.currentRoute.value.path !== '/login') {
        router.push('/login')
      }

      return Promise.reject(error)
    }

    // 不在这里显示错误消息，让组件自己处理
    return Promise.reject(error)
  }
)

export default request
