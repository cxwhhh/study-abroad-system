import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'
import sessionManager from './utils/sessionManager'
import ECharts from 'vue-echarts'
import { use } from 'echarts/core'
import Particles from "@tsparticles/vue3";
import { loadFull } from "tsparticles";
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart, PieChart, MapChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  DataZoomComponent,
  VisualMapComponent,
  GeoComponent
} from 'echarts/components'
import * as echarts from 'echarts'

/**
 * 创建Vue应用实例
 * 使用Element Plus组件库
 * 配置路由
 */

// 注册 ECharts 组件
use([
  CanvasRenderer,
  LineChart,
  BarChart,
  PieChart,
  MapChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  DataZoomComponent,
  VisualMapComponent,
  GeoComponent
])

const app = createApp(App)

// Apply global styles
const style = document.createElement("style")
style.textContent = `
  body {
    margin: 0;
    padding: 0;
    font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
  }
`
document.head.appendChild(style)

// 注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 注册 Element Plus 组件库
app.use(ElementPlus)

// 注册 ECharts 组件
app.component('v-chart', ECharts)

// 注册路由
app.use(router)

app.use(Particles, {
  init: async engine => {
    await loadFull(engine);
  }
});

axios.defaults.baseURL = ''
axios.defaults.withCredentials = true
// 配置请求拦截器
axios.interceptors.request.use(config => {
  config.headers = {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
  return config
})

// 配置响应拦截器
axios.interceptors.response.use(
  response => response,
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 将 echarts 挂载到全局
app.config.globalProperties.$echarts = echarts

// 启动会话管理器
if (sessionManager.isLoggedIn()) {
  sessionManager.startSessionCheck()
}

// 挂载应用
app.mount('#app')