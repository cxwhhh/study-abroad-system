<template>
  <div class="modern-dashboard">
    <!-- 顶部导航栏 -->
    <div class="dashboard-header">
      <div class="header-content">
        <div class="header-left">
          <h1 class="dashboard-title">智能控制台</h1>
          <p class="dashboard-subtitle">
            欢迎回来，{{ userInfo?.nickname || userInfo?.username || '管理员' }}
            <span class="date-info">{{ currentDate }}</span>
          </p>
        </div>
        <div class="header-actions">
          <button class="action-btn secondary" @click="refreshData" :disabled="isRefreshing">
            <RefreshCw :class="{ 'rotating': isRefreshing }" />
            刷新
          </button>
          <button class="action-btn secondary" @click="showCustomization = true">
            <Settings />
            自定义
          </button>
          <button class="action-btn secondary" @click="toggleLayoutMode">
            <Layout />
            {{ isLayoutMode ? '完成布局' : '编辑布局' }}
          </button>
          <button class="action-btn primary" @click="showCreateModal = true">
            <Plus />
            新建申请
          </button>
        </div>
      </div>
    </div>

    <!-- 布局编辑提示 -->
    <div v-if="isLayoutMode" class="layout-mode-tip">
      <div class="tip-content">
        <Edit class="tip-icon" />
        <span>拖拽模式已启用 - 您可以拖拽组件重新排列，调整组件大小</span>
        <button class="tip-close" @click="isLayoutMode = false">
          <X />
        </button>
      </div>
    </div>

    <!-- 核心指标卡片 -->
    <div class="metrics-section">
      <draggable v-model="coreMetrics" :disabled="!isLayoutMode" :animation="200" class="metrics-grid" item-key="id"
        @start="onDragStart" @end="onDragEnd">
        <template #item="{ element, index }">
          <div :class="['metric-card', { 'dragging-mode': isLayoutMode }]" :data-component="element.id">
            <div v-if="isLayoutMode" class="drag-handle">
              <MoreHorizontal />
            </div>
            <div class="metric-header">
              <div class="metric-icon" :style="{ background: element.gradient }">
                <component :is="element.icon" />
              </div>
              <div class="metric-trend" :class="element.trend">
                <component :is="element.trend === 'up' ? TrendingUp : TrendingDown" />
                <span>{{ element.change }}%</span>
              </div>
            </div>
            <div class="metric-content">
              <div class="metric-value">{{ element.value }}</div>
              <div class="metric-label">{{ element.label }}</div>
              <div class="metric-progress">
                <div class="progress-bar" :style="{
                  width: `${element.percentage}%`,
                  background: element.gradient
                }"></div>
              </div>
            </div>
          </div>
        </template>
      </draggable>
    </div>

    <!-- 可拖拽组件网格 -->
    <div class="draggable-grid">
      <draggable v-model="dashboardLayout" :disabled="!isLayoutMode" :animation="200" class="dashboard-grid"
        item-key="id" @start="onDragStart" @end="onDragEnd">
        <template #item="{ element }">
          <div v-if="visibleComponents[element.componentKey]" :class="[
            'dashboard-widget',
            `widget-${element.size}`,
            { 'dragging-mode': isLayoutMode }
          ]" :data-component="element.id">
            <!-- 拖拽控制条 -->
            <div v-if="isLayoutMode" class="widget-controls">
              <div class="drag-handle">
                <MoreHorizontal />
              </div>
              <div class="size-controls">
                <button class="size-btn" :class="{ active: element.size === 'small' }"
                  @click="changeWidgetSize(element, 'small')" title="小尺寸">S</button>
                <button class="size-btn" :class="{ active: element.size === 'medium' }"
                  @click="changeWidgetSize(element, 'medium')" title="中尺寸">M</button>
                <button class="size-btn" :class="{ active: element.size === 'large' }"
                  @click="changeWidgetSize(element, 'large')" title="大尺寸">L</button>
              </div>
              <button class="hide-btn" @click="toggleComponent(element.componentKey)" title="隐藏组件">
                <Eye />
              </button>
            </div>

            <!-- 动态组件内容 -->
            <component :is="getComponentByKey(element.componentKey)" :component-data="element"
              :dashboard-data="dashboardData" @close="toggleComponent(element.componentKey)"
              @addTask="showAddTodo = true" @updateTodoItems="updateTodoItems" />
          </div>
        </template>
      </draggable>
    </div>

    <!-- 自定义面板弹窗 -->
    <div v-if="showCustomization" class="customization-overlay" @click="showCustomization = false">
      <div class="customization-panel" @click.stop>
        <div class="panel-header">
          <h3>自定义控制台</h3>
          <button class="close-btn" @click="showCustomization = false">
            <X />
          </button>
        </div>
        <div class="panel-content">
          <div class="customization-section">
            <h4>显示组件</h4>
            <div class="component-toggles">
              <div v-for="(component, key) in componentOptions" :key="key" class="toggle-item">
                <label class="toggle-label">
                  <input type="checkbox" v-model="visibleComponents[key]" class="toggle-checkbox"
                    @change="updateLayout" />
                  <span class="toggle-text">{{ component.name }}</span>
                </label>
                <span class="component-desc">{{ component.description }}</span>
              </div>
            </div>
          </div>
          <div class="customization-section">
            <h4>布局管理</h4>
            <div class="layout-actions">
              <button class="btn secondary" @click="resetLayout">重置布局</button>
              <button class="btn secondary" @click="exportLayout">导出布局</button>
              <button class="btn secondary" @click="importLayout">导入布局</button>
            </div>
          </div>
          <div class="customization-section">
            <h4>主题设置</h4>
            <div class="theme-options">
              <div v-for="theme in availableThemes" :key="theme.id"
                :class="['theme-option', { active: currentTheme === theme.id }]" @click="applyTheme(theme)">
                <div class="theme-preview" :style="{ background: theme.gradient }"></div>
                <span>{{ theme.name }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="panel-footer">
          <button class="btn secondary" @click="resetToDefault">重置默认</button>
          <button class="btn primary" @click="saveCustomization">保存设置</button>
        </div>
      </div>
    </div>

    <!-- 其他弹窗保持不变 -->
    <!-- 添加任务弹窗 -->
    <div v-if="showAddTodo" class="modal-overlay" @click="showAddTodo = false">
      <div class="modal-panel" @click.stop>
        <div class="modal-header">
          <h3>添加待办任务</h3>
          <button class="close-btn" @click="showAddTodo = false">
            <X />
          </button>
        </div>
        <div class="modal-content">
          <div class="form-group">
            <label>任务标题</label>
            <input v-model="newTask.title" placeholder="请输入任务标题" class="form-input" />
          </div>
          <div class="form-group">
            <label>优先级</label>
            <select v-model="newTask.priority" class="form-select">
              <option value="low">低</option>
              <option value="normal">普通</option>
              <option value="high">高</option>
              <option value="urgent">紧急</option>
            </select>
          </div>
          <div class="form-group">
            <label>截止日期</label>
            <input v-model="newTask.dueDate" type="date" class="form-input" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn secondary" @click="showAddTodo = false">取消</button>
          <button class="btn primary" @click="addTask">添加任务</button>
        </div>
      </div>
    </div>

    <!-- 新建申请弹窗 -->
    <div v-if="showCreateModal" class="modal-overlay" @click="showCreateModal = false">
      <div class="modal-panel large" @click.stop>
        <div class="modal-header">
          <h3>快速新建申请</h3>
          <button class="close-btn" @click="showCreateModal = false">
            <X />
          </button>
        </div>
        <div class="modal-content">
          <div class="form-row">
            <div class="form-group">
              <label>学生姓名</label>
              <input v-model="newApplication.studentName" placeholder="请输入学生姓名" class="form-input" />
            </div>
            <div class="form-group">
              <label>目标学校</label>
              <input v-model="newApplication.school" placeholder="请输入目标学校" class="form-input" />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>申请专业</label>
              <input v-model="newApplication.major" placeholder="请输入申请专业" class="form-input" />
            </div>
            <div class="form-group">
              <label>申请类型</label>
              <select v-model="newApplication.type" class="form-select">
                <option value="undergraduate">本科申请</option>
                <option value="graduate">研究生申请</option>
                <option value="phd">博士申请</option>
              </select>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn secondary" @click="showCreateModal = false">取消</button>
          <button class="btn primary" @click="createApplication">创建申请</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import draggable from 'vuedraggable'
import {
  FileText, Clock, CheckCircle, Users, TrendingUp, TrendingDown,
  CheckSquare, Calendar, Edit, MoreVertical, Eye, ArrowRight,
  PenTool, MoreHorizontal, Settings, RefreshCw, Plus, X, Download,
  Bell, AlertTriangle, Info, Award, Target, BookOpen, Globe,
  BarChart3, PieChart, Activity, Zap, Star, ChevronLeft, ChevronRight,
  Trash2, Upload, Layout, Palette, User, Server
} from 'lucide-vue-next'
import request from '@/utils/request'

// 导入组件（这些会在后面创建）
import StatusDistribution from './widgets/StatusDistribution.vue'
import TrendChart from './widgets/TrendChart.vue'
import RecentActivity from './widgets/RecentActivity.vue'
import QuickActions from './widgets/QuickActions.vue'
import TodoTasks from './widgets/TodoTasks.vue'
import NotificationCenter from './widgets/NotificationCenter.vue'
import SchoolRanking from './widgets/SchoolRanking.vue'
import SuccessCases from './widgets/SuccessCases.vue'
import ImportantReminders from './widgets/ImportantReminders.vue'
import SystemStatus from './widgets/SystemStatus.vue'

// 从父组件接收用户信息
const props = defineProps({
  userInfo: {
    type: Object,
    default: () => ({})
  }
})

// 响应式数据
const showCustomization = ref(false)
const isRefreshing = ref(false)
const isLayoutMode = ref(false)
const currentTheme = ref('default')
const showAddTodo = ref(false)
const showCreateModal = ref(false)
const isDragging = ref(false)

const dashboardData = ref({
  students: [],
  applications: [],
  todoItems: [],
  lastUpdatedAt: null
})

// 表单数据
const newTask = ref({
  title: '',
  priority: 'normal',
  dueDate: ''
})

const newApplication = ref({
  studentName: '',
  school: '',
  major: '',
  type: 'undergraduate'
})

// 可见组件配置
const visibleComponents = ref({
  statusDistribution: true,
  trendChart: true,
  recentActivity: true,
  quickActions: true,
  todoTasks: true,
  notifications: true,
  schoolRanking: true,
  successCases: true,
  importantReminders: true,
  systemStatus: false
})

// 组件选项
const componentOptions = ref({
  statusDistribution: { name: '状态分布', description: '申请状态统计图表' },
  trendChart: { name: '趋势图表', description: '申请数据趋势分析' },
  recentActivity: { name: '最近活动', description: '系统动态追踪' },
  quickActions: { name: '快速操作', description: '常用功能快捷入口' },
  todoTasks: { name: '待办任务', description: '个人任务管理' },
  notifications: { name: '通知中心', description: '系统消息提醒' },
  schoolRanking: { name: '热门院校', description: '申请量排行榜' },
  successCases: { name: '成功案例', description: '最新录取展示' },
  importantReminders: { name: '重要提醒', description: '截止日期警告' },
  systemStatus: { name: '系统状态', description: '服务器监控数据' }
})

// Dashboard布局配置
const dashboardLayout = ref([
  { id: 'status-distribution', componentKey: 'statusDistribution', size: 'small', order: 1 },
  { id: 'trend-chart', componentKey: 'trendChart', size: 'large', order: 2 },
  { id: 'recent-activity', componentKey: 'recentActivity', size: 'large', order: 3 },
  { id: 'quick-actions', componentKey: 'quickActions', size: 'medium', order: 4 },
  { id: 'todo-tasks', componentKey: 'todoTasks', size: 'medium', order: 5 },
  { id: 'notifications', componentKey: 'notifications', size: 'medium', order: 6 },
  { id: 'school-ranking', componentKey: 'schoolRanking', size: 'small', order: 7 },
  { id: 'success-cases', componentKey: 'successCases', size: 'small', order: 8 },
  { id: 'important-reminders', componentKey: 'importantReminders', size: 'medium', order: 9 },
  { id: 'system-status', componentKey: 'systemStatus', size: 'medium', order: 10 }
])

// 当前日期
const currentDate = computed(() => {
  const now = new Date()
  return `${now.getMonth() + 1}月${now.getDate()}日`
})

// 核心指标数据 - 添加唯一ID
const coreMetrics = ref([
  {
    id: 'metric-1',
    icon: FileText,
    label: '待处理申请',
    value: 24,
    change: 12,
    trend: 'up',
    percentage: 65,
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    id: 'metric-2',
    icon: Clock,
    label: '处理中申请',
    value: 38,
    change: 8,
    trend: 'up',
    percentage: 80,
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    id: 'metric-3',
    icon: CheckCircle,
    label: '已完成申请',
    value: 156,
    change: 3,
    trend: 'down',
    percentage: 95,
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  {
    id: 'metric-4',
    icon: Users,
    label: '成功案例',
    value: 218,
    change: 5,
    trend: 'up',
    percentage: 88,
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  }
])

const parseDateValue = (value) => {
  if (!value) return null
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return null
  return d
}

const startOfDay = (date) => {
  const d = new Date(date)
  d.setHours(0, 0, 0, 0)
  return d
}

const addDays = (date, days) => {
  const d = new Date(date)
  d.setDate(d.getDate() + days)
  return d
}

const isInRange = (date, start, endExclusive) => {
  const t = date.getTime()
  return t >= start.getTime() && t < endExclusive.getTime()
}

const getApplicationStatusCode = (app) => {
  return app?.applicationStatus || app?.status || ''
}

const isApplicationCompleted = (statusCode) => {
  return ['admitted', 'rejected', 'visa_approved', 'visa_rejected', 'enrolled'].includes(statusCode)
}

const isApplicationSuccessful = (statusCode) => {
  return ['admitted', 'enrolled'].includes(statusCode)
}

const getApplicationActivityDate = (app) => {
  return parseDateValue(app?.updateTime) || parseDateValue(app?.createTime)
}

const computeChangePercent = (current, previous) => {
  if (!previous) return current ? 100 : 0
  return Math.round(((current - previous) / previous) * 10 * 100) / 10
}

const updateCoreMetrics = () => {
  const apps = Array.isArray(dashboardData.value.applications) ? dashboardData.value.applications : []
  const total = apps.length

  const pendingStatuses = new Set(['selected'])
  const processingStatuses = new Set(['submitted', 'under_review', 'interview', 'visa_processing'])

  const pendingCount = apps.filter(app => pendingStatuses.has(getApplicationStatusCode(app))).length
  const processingCount = apps.filter(app => processingStatuses.has(getApplicationStatusCode(app))).length
  const completedCount = apps.filter(app => isApplicationCompleted(getApplicationStatusCode(app))).length
  const successCount = apps.filter(app => isApplicationSuccessful(getApplicationStatusCode(app))).length

  const now = new Date()
  const currentStart = startOfDay(addDays(now, -7))
  const currentEnd = addDays(startOfDay(now), 1)
  const previousStart = startOfDay(addDays(now, -14))
  const previousEnd = currentStart

  const countInPeriod = (predicate, start, end) => {
    return apps.filter(app => {
      if (!predicate(app)) return false
      const d = getApplicationActivityDate(app)
      if (!d) return false
      return isInRange(d, start, end)
    }).length
  }

  const pendingCurrent = countInPeriod(app => pendingStatuses.has(getApplicationStatusCode(app)), currentStart, currentEnd)
  const pendingPrevious = countInPeriod(app => pendingStatuses.has(getApplicationStatusCode(app)), previousStart, previousEnd)
  const processingCurrent = countInPeriod(app => processingStatuses.has(getApplicationStatusCode(app)), currentStart, currentEnd)
  const processingPrevious = countInPeriod(app => processingStatuses.has(getApplicationStatusCode(app)), previousStart, previousEnd)
  const completedCurrent = countInPeriod(app => isApplicationCompleted(getApplicationStatusCode(app)), currentStart, currentEnd)
  const completedPrevious = countInPeriod(app => isApplicationCompleted(getApplicationStatusCode(app)), previousStart, previousEnd)
  const successCurrent = countInPeriod(app => isApplicationSuccessful(getApplicationStatusCode(app)), currentStart, currentEnd)
  const successPrevious = countInPeriod(app => isApplicationSuccessful(getApplicationStatusCode(app)), previousStart, previousEnd)

  const metricUpdates = {
    'metric-1': { value: pendingCount, change: computeChangePercent(pendingCurrent, pendingPrevious), percentage: total ? Math.round((pendingCount / total) * 100) : 0 },
    'metric-2': { value: processingCount, change: computeChangePercent(processingCurrent, processingPrevious), percentage: total ? Math.round((processingCount / total) * 100) : 0 },
    'metric-3': { value: completedCount, change: computeChangePercent(completedCurrent, completedPrevious), percentage: total ? Math.round((completedCount / total) * 100) : 0 },
    'metric-4': { value: successCount, change: computeChangePercent(successCurrent, successPrevious), percentage: total ? Math.round((successCount / total) * 100) : 0 }
  }

  coreMetrics.value.forEach(metric => {
    const update = metricUpdates[metric.id]
    if (!update) return
    metric.value = update.value
    metric.change = Math.abs(update.change)
    metric.trend = update.change >= 0 ? 'up' : 'down'
    metric.percentage = update.percentage
  })
}

// 主题选项
const availableThemes = ref([
  {
    id: 'default',
    name: '默认主题',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    id: 'ocean',
    name: '海洋蓝',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  {
    id: 'sunset',
    name: '日落橙',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    id: 'forest',
    name: '森林绿',
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  }
])

// 组件映射
const componentMap = {
  statusDistribution: StatusDistribution,
  trendChart: TrendChart,
  recentActivity: RecentActivity,
  quickActions: QuickActions,
  todoTasks: TodoTasks,
  notifications: NotificationCenter,
  schoolRanking: SchoolRanking,
  successCases: SuccessCases,
  importantReminders: ImportantReminders,
  systemStatus: SystemStatus
}

// 方法
const getComponentByKey = (key) => {
  return componentMap[key] || 'div'
}

const toggleLayoutMode = () => {
  isLayoutMode.value = !isLayoutMode.value
  if (!isLayoutMode.value) {
    saveLayout()
  }
}

const onDragStart = () => {
  isDragging.value = true
}

const onDragEnd = () => {
  isDragging.value = false
  saveLayout()
}

const changeWidgetSize = (widget, size) => {
  widget.size = size
  saveLayout()
}

const toggleComponent = (componentKey) => {
  visibleComponents.value[componentKey] = !visibleComponents.value[componentKey]
  updateLayout()
}

const updateLayout = () => {
  // 更新布局顺序
  dashboardLayout.value.forEach((item, index) => {
    item.order = index + 1
  })
  saveLayout()
}

const saveLayout = () => {
  const settings = {
    layout: dashboardLayout.value,
    visibleComponents: visibleComponents.value,
    theme: currentTheme.value,
    coreMetrics: coreMetrics.value
  }
  localStorage.setItem('dashboardLayout', JSON.stringify(settings))
}

const loadLayout = () => {
  const saved = localStorage.getItem('dashboardLayout')
  if (saved) {
    try {
      const settings = JSON.parse(saved)
      if (settings.layout) dashboardLayout.value = settings.layout
      if (settings.visibleComponents) visibleComponents.value = settings.visibleComponents
      if (settings.theme) currentTheme.value = settings.theme
      if (settings.coreMetrics) coreMetrics.value = settings.coreMetrics
    } catch (e) {
      console.error('加载布局失败:', e)
    }
  }
}

const resetLayout = () => {
  // 重置为默认布局
  dashboardLayout.value = [
    { id: 'status-distribution', componentKey: 'statusDistribution', size: 'small', order: 1 },
    { id: 'trend-chart', componentKey: 'trendChart', size: 'large', order: 2 },
    { id: 'recent-activity', componentKey: 'recentActivity', size: 'large', order: 3 },
    { id: 'quick-actions', componentKey: 'quickActions', size: 'medium', order: 4 },
    { id: 'todo-tasks', componentKey: 'todoTasks', size: 'medium', order: 5 },
    { id: 'notifications', componentKey: 'notifications', size: 'medium', order: 6 },
    { id: 'school-ranking', componentKey: 'schoolRanking', size: 'small', order: 7 },
    { id: 'success-cases', componentKey: 'successCases', size: 'small', order: 8 },
    { id: 'important-reminders', componentKey: 'importantReminders', size: 'medium', order: 9 },
    { id: 'system-status', componentKey: 'systemStatus', size: 'medium', order: 10 }
  ]
  updateLayout()
}

const exportLayout = () => {
  const settings = {
    layout: dashboardLayout.value,
    visibleComponents: visibleComponents.value,
    theme: currentTheme.value
  }
  const dataStr = JSON.stringify(settings, null, 2)
  const dataBlob = new Blob([dataStr], { type: 'application/json' })
  const url = URL.createObjectURL(dataBlob)
  const link = document.createElement('a')
  link.href = url
  link.download = 'dashboard-layout.json'
  link.click()
  URL.revokeObjectURL(url)
}

const importLayout = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = '.json'
  input.onchange = (e) => {
    const file = e.target.files[0]
    if (file) {
      const reader = new FileReader()
      reader.onload = (e) => {
        try {
          const settings = JSON.parse(e.target.result)
          if (settings.layout) dashboardLayout.value = settings.layout
          if (settings.visibleComponents) visibleComponents.value = settings.visibleComponents
          if (settings.theme) currentTheme.value = settings.theme
          saveLayout()
        } catch (error) {
          alert('布局文件格式错误')
        }
      }
      reader.readAsText(file)
    }
  }
  input.click()
}

const refreshData = async () => {
  isRefreshing.value = true
  try {
    await loadDashboardData()
  } finally {
    isRefreshing.value = false
  }
}

const applyTheme = (theme) => {
  currentTheme.value = theme.id
  console.log('应用主题:', theme.name)
}

const saveCustomization = () => {
  saveLayout()
  showCustomization.value = false
}

const resetToDefault = () => {
  resetLayout()
  Object.keys(visibleComponents.value).forEach(key => {
    visibleComponents.value[key] = key !== 'systemStatus'
  })
  currentTheme.value = 'default'
}

const addTask = () => {
  if (!newTask.value.title) return
  const item = {
    id: Date.now(),
    title: newTask.value.title,
    priority: newTask.value.priority,
    dueDate: newTask.value.dueDate || '',
    completed: false
  }
  dashboardData.value.todoItems = Array.isArray(dashboardData.value.todoItems)
    ? [item, ...dashboardData.value.todoItems]
    : [item]
  persistTodoItems()
  showAddTodo.value = false
  newTask.value = { title: '', priority: 'normal', dueDate: '' }
}

const createApplication = () => {
  if (!newApplication.value.studentName || !newApplication.value.school) return

  console.log('创建申请:', newApplication.value)
  showCreateModal.value = false
  newApplication.value = { studentName: '', school: '', major: '', type: 'undergraduate' }
}

const loadTodoItems = () => {
  try {
    const raw = localStorage.getItem('dashboardTodoItems')
    if (!raw) return
    const parsed = JSON.parse(raw)
    if (Array.isArray(parsed)) {
      dashboardData.value.todoItems = parsed
    }
  } catch (e) {
    dashboardData.value.todoItems = []
  }
}

const persistTodoItems = () => {
  try {
    localStorage.setItem('dashboardTodoItems', JSON.stringify(dashboardData.value.todoItems || []))
  } catch (e) {
  }
}

const updateTodoItems = (items) => {
  dashboardData.value.todoItems = Array.isArray(items) ? items : []
  persistTodoItems()
}

const loadDashboardData = async () => {
  const [appsRes, studentsRes] = await Promise.all([
    request.get('/application-school/list'),
    request.get('/student/admin/students')
  ])
  dashboardData.value.applications = Array.isArray(appsRes?.data) ? appsRes.data : []
  dashboardData.value.students = Array.isArray(studentsRes?.data) ? studentsRes.data : []
  dashboardData.value.lastUpdatedAt = new Date().toISOString()
  updateCoreMetrics()
}

// 组件挂载时加载布局
onMounted(() => {
  loadLayout()
  loadTodoItems()
  loadDashboardData().catch(() => { })
})

watch(
  () => dashboardData.value.todoItems,
  () => {
    persistTodoItems()
  },
  { deep: true }
)
</script>

<style scoped>
.modern-dashboard {
  width: 100%;
  background-color: #f5f7fa;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  min-height: calc(100vh - 88px);
}

/* 顶部导航栏 */
.dashboard-header {
  background: rgba(245, 247, 250, 0.95);
  backdrop-filter: blur(20px);
  margin: -24px -24px 24px -24px;
  position: relative;
  z-index: 1;
}

.header-content {
  max-width: none;
  margin: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
}

.header-left {
  flex: 1;
}

.dashboard-title {
  font-size: 24px;
  font-weight: 600;
  color: #111;
  margin: 0 0 4px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.dashboard-subtitle {
  color: #666;
  font-size: 14px;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.date-info {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  height: 40px;
}

.action-btn.primary {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  color: white;
  box-shadow: 0 2px 6px rgba(24, 144, 255, 0.2);
}

.action-btn.primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

.action-btn.secondary {
  background-color: white;
  color: #666;
  border: 1px solid #d9d9d9;
}

.action-btn.secondary:hover {
  border-color: #1890ff;
  color: #1890ff;
  transform: translateY(-1px);
}

.action-btn svg {
  width: 16px;
  height: 16px;
}

.rotating {
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

/* 布局编辑提示 */
.layout-mode-tip {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0;
  margin-bottom: 24px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.tip-content {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  position: relative;
}

.tip-icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.tip-close {
  margin-left: auto;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  border-radius: 6px;
  color: white;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.tip-close:hover {
  background: rgba(255, 255, 255, 0.3);
}

.tip-close svg {
  width: 14px;
  height: 14px;
}

/* 核心指标卡片 */
.metrics-section {
  margin-bottom: 24px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.metric-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  border: 1px solid rgba(0, 0, 0, 0.02);
}

.metric-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.08);
}

.metric-card.dragging-mode {
  cursor: move;
  border: 2px dashed #1890ff;
}

.metric-card .drag-handle {
  position: absolute;
  top: 8px;
  right: 8px;
  background: rgba(24, 144, 255, 0.1);
  color: #1890ff;
  width: 24px;
  height: 24px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: move;
  opacity: 0.7;
  transition: all 0.3s ease;
}

.metric-card .drag-handle:hover {
  opacity: 1;
  background: rgba(24, 144, 255, 0.2);
}

.metric-card .drag-handle svg {
  width: 12px;
  height: 12px;
}

.metric-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.metric-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.metric-icon svg {
  width: 24px;
  height: 24px;
}

.metric-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 500;
  padding: 4px 8px;
  border-radius: 12px;
}

.metric-trend.up {
  color: #52c41a;
  background: rgba(82, 196, 26, 0.1);
}

.metric-trend.down {
  color: #f5222d;
  background: rgba(245, 34, 45, 0.1);
}

.metric-trend svg {
  width: 12px;
  height: 12px;
}

.metric-value {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.metric-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
}

.metric-progress {
  height: 4px;
  background-color: #f5f5f5;
  border-radius: 2px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  border-radius: 2px;
  transition: width 0.8s ease;
}

/* 拖拽网格布局 */
.draggable-grid {
  margin-top: 24px;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
  grid-auto-rows: min-content;
}

.dashboard-widget {
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(0, 0, 0, 0.02);
  overflow: hidden;
  transition: all 0.3s ease;
  position: relative;
}

.dashboard-widget:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.08);
}

.dashboard-widget.dragging-mode {
  cursor: move;
  border: 2px dashed #1890ff;
}

/* 组件尺寸 */
.widget-small {
  grid-column: span 1;
}

.widget-medium {
  grid-column: span 1;
  min-height: 300px;
}

.widget-large {
  grid-column: span 2;
  min-height: 400px;
}

/* 拖拽控制条 */
.widget-controls {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 8px 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  z-index: 10;
  font-size: 12px;
}

.widget-controls .drag-handle {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: move;
  opacity: 0.8;
  transition: opacity 0.3s ease;
}

.widget-controls .drag-handle:hover {
  opacity: 1;
}

.widget-controls .drag-handle svg {
  width: 14px;
  height: 14px;
}

.size-controls {
  display: flex;
  gap: 4px;
  margin-left: auto;
}

.size-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  width: 24px;
  height: 24px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 10px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.size-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.size-btn.active {
  background: rgba(255, 255, 255, 0.4);
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.5);
}

.hide-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  width: 24px;
  height: 24px;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.hide-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.hide-btn svg {
  width: 12px;
  height: 12px;
}

/* 拖拽时的样式 */
.sortable-ghost {
  opacity: 0.5;
  background: #f0f0f0;
  border: 2px dashed #1890ff;
}

.sortable-chosen {
  transform: rotate(5deg);
}

.sortable-drag {
  opacity: 0.8;
  transform: scale(1.05);
}

/* 自定义面板 */
.customization-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(10px);
  z-index: 1001;
  display: flex;
  align-items: center;
  justify-content: center;
}

.customization-panel {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  width: 600px;
  max-height: 80vh;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
}

.panel-header {
  padding: 24px 24px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel-header h3 {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: rgba(245, 34, 45, 0.1);
  color: #f5222d;
}

.close-btn svg {
  width: 16px;
  height: 16px;
}

.panel-content {
  padding: 24px;
  max-height: calc(80vh - 140px);
  overflow-y: auto;
}

.customization-section {
  margin-bottom: 32px;
}

.customization-section h4 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.component-toggles {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.toggle-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #fafafa;
  border-radius: 8px;
}

.toggle-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.toggle-checkbox {
  width: 16px;
  height: 16px;
  accent-color: #1890ff;
}

.toggle-text {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.component-desc {
  font-size: 12px;
  color: #666;
}

.layout-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.theme-options {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.theme-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px;
  border: 2px solid transparent;
  border-radius: 12px;
  background: #fafafa;
  cursor: pointer;
  transition: all 0.3s ease;
}

.theme-option.active {
  border-color: #1890ff;
  background: rgba(24, 144, 255, 0.05);
}

.theme-preview {
  width: 40px;
  height: 40px;
  border-radius: 8px;
}

.theme-option span {
  font-size: 12px;
  color: #666;
}

.panel-footer {
  padding: 0 24px 24px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 按钮样式 */
.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn.primary {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  color: white;
  box-shadow: 0 2px 6px rgba(24, 144, 255, 0.2);
}

.btn.primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

.btn.secondary {
  background-color: white;
  color: #666;
  border: 1px solid #d9d9d9;
}

.btn.secondary:hover {
  border-color: #1890ff;
  color: #1890ff;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(10px);
  z-index: 1001;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-panel {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  width: 500px;
  max-height: 80vh;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
}

.modal-panel.large {
  width: 600px;
}

.modal-header {
  padding: 24px 24px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.modal-content {
  padding: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 6px;
}

.form-input,
.form-select {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  font-size: 14px;
  color: #333;
  background: white;
  transition: all 0.3s ease;
}

.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
}

.modal-footer {
  padding: 0 24px 24px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .dashboard-grid {
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  }

  .widget-large {
    grid-column: span 1;
  }

  .metrics-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
    padding: 16px 24px;
  }

  .header-actions {
    width: 100%;
    justify-content: flex-end;
    flex-wrap: wrap;
  }

  .metrics-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .dashboard-grid {
    grid-template-columns: 1fr;
  }

  .widget-small,
  .widget-medium,
  .widget-large {
    grid-column: span 1;
  }

  .customization-panel,
  .modal-panel {
    width: 90vw;
    margin: 20px;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .layout-actions {
    flex-direction: column;
  }

  .layout-actions .btn {
    width: 100%;
  }
}

/* 平滑动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 拖拽动画 */
.flip-list-move {
  transition: transform 0.5s;
}

.no-move {
  transition: transform 0s;
}

.ghost {
  opacity: 0.5;
  background: #c8ebfb;
}
</style>
