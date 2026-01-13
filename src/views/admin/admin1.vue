<template>
  <div class="admin-platform">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="logo-container">
        <div class="logo-badge">
          <span>新</span>
        </div>
        <div class="logo-text">
          <h1 class="logo">新英美留学</h1>
          <span class="system-name">管理系统</span>
        </div>
      </div>
      <div class="datetime-container">
        <span class="current-date">{{ currentDate }}</span>
        <span class="datetime-separator">|</span>
        <span class="current-time">{{ currentTime }}</span>
      </div>
      <div class="user-container">
        <NotificationDropdown @navigate="setActiveMenu" />
        <div class="icon-button">
          <MessageSquare />
        </div>
        <div class="user-profile" @click="toggleUserMenu">
          <div class="avatar-wrapper">
            <div v-if="userInfo?.avatar" class="avatar">
              <img :src="userInfo.avatar" alt="用户头像" />
            </div>
            <div v-else class="avatar-text">
              {{ userInfo?.nickname?.charAt(0) || '管' }}
            </div>
            <span class="status-dot"></span>
          </div>
          <div class="user-info">
            <span class="user-name">{{ userInfo?.nickname || '管理员' }}</span>
            <span class="user-role">{{ userInfo?.role || '管理员' }}</span>
          </div>
          <ChevronDown class="dropdown-icon" :class="{ 'rotate-180': showUserMenu }" />

          <!-- 用户菜单下拉框 -->
          <Transition name="fade">
            <div v-if="showUserMenu" class="user-menu">
              <div class="menu-item" @click="showUserList">
                <User class="menu-icon" />
                <span>切换用户</span>
              </div>
              <div class="menu-item" @click="logout">
                <LogOut class="menu-icon" />
                <span>退出登录</span>
              </div>
            </div>
          </Transition>
        </div>
      </div>
    </header>

    <div class="main-container">
      <!-- 左侧导航栏 -->
      <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
        <div class="sidebar-header">
          <div class="menu-title" v-show="!sidebarCollapsed">主导航</div>
          <button class="sidebar-toggle" @click="toggleSidebar" :title="sidebarCollapsed ? '展开导航栏' : '收缩导航栏'">
            <ChevronsRight v-if="sidebarCollapsed" class="toggle-icon" />
            <ChevronsLeft v-else class="toggle-icon" />
          </button>
        </div>
        <ul class="menu-list" @scroll="hideSidebarTooltip">
          <li class="menu-item" :class="{ active: activeMenu === 'dashboard' }" @click="setActiveMenu('dashboard')"
            data-tooltip="控制台">
            <div class="menu-icon-wrapper" data-tooltip="控制台" @mouseenter="showSidebarTooltip"
              @mouseleave="hideSidebarTooltip">
              <LayoutDashboard class="menu-icon" />
            </div>
            <span v-show="!sidebarCollapsed">控制台</span>
          </li>
          <li class="menu-item" :class="{ active: activeMenu === 'students' }" @click="setActiveMenu('students')"
            data-tooltip="学生管理">
            <div class="menu-icon-wrapper" data-tooltip="学生管理" @mouseenter="showSidebarTooltip"
              @mouseleave="hideSidebarTooltip">
              <Users class="menu-icon" />
            </div>
            <span v-show="!sidebarCollapsed">学生管理</span>
            <span class="menu-badge" v-show="!sidebarCollapsed">{{ studentCount }}</span>
          </li>
          <li class="menu-item" :class="{ active: activeMenu === 'applications' }"
            @click="setActiveMenu('applications')" data-tooltip="申请管理">
            <div class="menu-icon-wrapper" data-tooltip="申请管理" @mouseenter="showSidebarTooltip"
              @mouseleave="hideSidebarTooltip">
              <FileText class="menu-icon" />
            </div>
            <span v-show="!sidebarCollapsed">申请管理</span>
          </li>
          <li class="menu-item" :class="{ active: activeMenu === 'institutions' }"
            @click="setActiveMenu('institutions')" data-tooltip="院校管理">
            <div class="menu-icon-wrapper" data-tooltip="院校管理" @mouseenter="showSidebarTooltip"
              @mouseleave="hideSidebarTooltip">
              <Building class="menu-icon" />
            </div>
            <span v-show="!sidebarCollapsed">院校管理</span>
          </li>
          <li class="menu-item" :class="{ active: activeMenu === 'notification' }"
            @click="setActiveMenu('notification')" data-tooltip="消息中心">
            <div class="menu-icon-wrapper" data-tooltip="消息中心" @mouseenter="showSidebarTooltip"
              @mouseleave="hideSidebarTooltip">
              <Bell class="menu-icon" />
            </div>
            <span v-show="!sidebarCollapsed">消息中心</span>
            <span class="menu-badge" v-show="!sidebarCollapsed && notificationUnreadCount > 0">{{
              notificationUnreadCount }}</span>
          </li>
          <li class="menu-item" :class="{ active: activeMenu === 'ai' }" @click="setActiveMenu('ai')"
            data-tooltip="AI助手">
            <div class="menu-icon-wrapper" data-tooltip="AI助手" @mouseenter="showSidebarTooltip"
              @mouseleave="hideSidebarTooltip">
              <Zap class="menu-icon" />
            </div>
            <span v-show="!sidebarCollapsed">AI助手</span>
          </li>
          <li class="menu-item" :class="{ active: activeMenu === 'importData' }" @click="setActiveMenu('importData')"
            data-tooltip="导入资料">
            <div class="menu-icon-wrapper" data-tooltip="导入资料" @mouseenter="showSidebarTooltip"
              @mouseleave="hideSidebarTooltip">
              <Database class="menu-icon" />
            </div>
            <span v-show="!sidebarCollapsed">导入资料</span>
          </li>
          <li class="menu-item" :class="{ active: activeMenu === 'calendar' }" @click="setActiveMenu('calendar')"
            data-tooltip="日程安排">
            <div class="menu-icon-wrapper" data-tooltip="日程安排" @mouseenter="showSidebarTooltip"
              @mouseleave="hideSidebarTooltip">
              <Calendar class="menu-icon" />
            </div>
            <span v-show="!sidebarCollapsed">日程安排</span>
          </li>
          <li class="menu-item" :class="{ active: activeMenu === 'analytics' }" @click="setActiveMenu('analytics')"
            data-tooltip="数据分析">
            <div class="menu-icon-wrapper" data-tooltip="数据分析" @mouseenter="showSidebarTooltip"
              @mouseleave="hideSidebarTooltip">
              <BarChart class="menu-icon" />
            </div>
            <span v-show="!sidebarCollapsed">数据分析</span>
          </li>
          <li class="menu-item" :class="{ active: activeMenu === 'finance' }" @click="setActiveMenu('finance')"
            data-tooltip="财务管理">
            <div class="menu-icon-wrapper" data-tooltip="财务管理" @mouseenter="showSidebarTooltip"
              @mouseleave="hideSidebarTooltip">
              <DollarSign class="menu-icon" />
            </div>
            <span v-show="!sidebarCollapsed">财务管理</span>
          </li>
          <li class="menu-item" :class="{ active: activeMenu === 'users' }" @click="setActiveMenu('users')"
            data-tooltip="用户管理">
            <div class="menu-icon-wrapper" data-tooltip="用户管理" @mouseenter="showSidebarTooltip"
              @mouseleave="hideSidebarTooltip">
              <UserCog class="menu-icon" />
            </div>
            <span v-show="!sidebarCollapsed">用户管理</span>
          </li>
          <li class="menu-item" :class="{ active: activeMenu === 'permissions' }" @click="setActiveMenu('permissions')"
            data-tooltip="权限设置">
            <div class="menu-icon-wrapper" data-tooltip="权限设置" @mouseenter="showSidebarTooltip"
              @mouseleave="hideSidebarTooltip">
              <Lock class="menu-icon" />
            </div>
            <span v-show="!sidebarCollapsed">权限设置</span>
          </li>
          <li class="menu-item" :class="{ active: activeMenu === 'system' }" @click="setActiveMenu('system')"
            data-tooltip="系统设置">
            <div class="menu-icon-wrapper" data-tooltip="系统设置" @mouseenter="showSidebarTooltip"
              @mouseleave="hideSidebarTooltip">
              <Settings class="menu-icon" />
            </div>
            <span v-show="!sidebarCollapsed">系统设置</span>
          </li>
          <li class="menu-item" :class="{ active: activeMenu === 'logs' }" @click="setActiveMenu('logs')"
            data-tooltip="系统日志">
            <div class="menu-icon-wrapper" data-tooltip="系统日志" @mouseenter="showSidebarTooltip"
              @mouseleave="hideSidebarTooltip">
              <FileText class="menu-icon" />
            </div>
            <span v-show="!sidebarCollapsed">系统日志</span>
          </li>
        </ul>

        <div class="sidebar-footer" v-show="!sidebarCollapsed">
          <div class="help-card">
            <div class="help-icon">
              <HelpCircle />
            </div>
            <div class="help-content">
              <h4>需要帮助?</h4>
              <p>查看我们的帮助文档</p>
              <button class="help-button" @click="showHelp">
                <ExternalLink class="help-button-icon" />
                <span>帮助中心</span>
              </button>
            </div>
          </div>
        </div>
      </aside>

      <!-- 右侧内容区 -->
      <main class="content">
        <!-- 仪表盘内容 -->
        <div v-if="activeMenu === 'dashboard'" class="dashboard-content">
          <Dashboard :userInfo="userInfo" />
        </div>

        <!-- 学生管理内容 -->
        <div v-if="activeMenu === 'students'" class="students-content">
          <StudentManagement />
        </div>

        <!-- 申请管理内容 -->
        <div v-if="activeMenu === 'applications'" class="applications-content">
          <ApplicationManagement />
        </div>

        <!-- 通知中心内容 -->
        <div v-if="activeMenu === 'notification'" class="notification-content">
          <Notice />
        </div>

        <!-- 院校管理 -->
        <div v-if="activeMenu === 'institutions'" class="institution-content">
          <Institution />
        </div>

        <!-- 日程安排 -->
        <div v-if="activeMenu === 'calendar'" class="schedule-content">
          <Schedule />
        </div>

        <!-- 数据分析 -->
        <div v-if="activeMenu === 'analytics'" class="analytics-content">
          <Analytics />
        </div>

        <!-- 财务管理 -->
        <div v-if="activeMenu === 'finance'" class="finance-content">
          <Finance />
        </div>

        <!-- 权限设置 -->
        <div v-if="activeMenu === 'permissions'" class="permission-content">
          <Permission />
        </div>

        <!-- 用户管理 -->
        <div v-if="activeMenu === 'users'" class="user-content">
          <UserManagement />
        </div>

        <!-- 系统日志 -->
        <div v-if="activeMenu === 'logs'" class="logs-content">
          <Logs />
        </div>

        <!-- 系统设置 -->
        <div v-if="activeMenu === 'system'" class="system-content">
          <SystemSettings />
        </div>

        <!-- 导入资料 -->
        <div v-if="activeMenu === 'importData'" class="import-data-content">
          <AIAssistant mode="import" />
        </div>

        <!-- AI助手组件 -->
        <div v-if="activeMenu === 'ai'" class="ai-assistant-content">
          <AIAssistant mode="assistant" />
        </div>
      </main>
    </div>
  </div>

  <Transition name="sidebar-tooltip">
    <div v-if="sidebarTooltip.visible" class="sidebar-tooltip"
      :style="{ top: `${sidebarTooltip.top}px`, left: `${sidebarTooltip.left}px` }">
      {{ sidebarTooltip.text }}
    </div>
  </Transition>

  <!-- 用户列表模态框 -->
  <Transition name="fade">
    <div v-if="showUserListModal" class="modal-overlay" @click="showUserListModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>选择用户</h3>
          <button class="btn-icon" @click="showUserListModal = false">
            <X />
          </button>
        </div>
        <div class="modal-body">
          <div class="search-box">
            <Search class="search-icon" />
            <input type="text" v-model="userSearchQuery" placeholder="搜索用户..." class="search-input" />
          </div>
          <div class="user-list">
            <div v-for="user in filteredUsers" :key="user.username" class="user-item"
              :class="{ 'selected': selectedUser?.username === user.username }" @click="selectUser(user)">
              <div class="user-avatar">
                <img v-if="user.avatar" :src="user.avatar" :alt="user.nickname" />
                <div v-else class="avatar-text" :style="{ backgroundColor: generateAvatarColor(user.nickname) }">
                  {{ user.nickname?.charAt(0) || '用' }}
                </div>
              </div>
              <div class="user-details">
                <h4>{{ user.nickname }}</h4>
                <p>{{ user.role || '管理员' }}</p>
                <p class="user-email">{{ user.email || '无邮箱' }}</p>
              </div>
              <button class="btn btn-primary" @click.stop="loginAsUser(user)">
                登录
              </button>
            </div>
            <div v-if="filteredUsers.length === 0" class="empty-list">
              <UserX class="empty-icon" />
              <p>没有找到匹配的用户</p>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-outline" @click="showUserListModal = false">取消</button>
          <button class="btn btn-primary" @click="addNewAccount">
            添加新账号
          </button>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import Notice from '@/components/admin/notice/Notice.vue'  // 修正大小写
import NotificationDropdown from '@/components/admin/notice/NotificationDropdown.vue'  // 导入通知下拉组件
import notificationStore from '@/components/admin/notice/notificationStore'
import Institution from '@/components/admin/institution/Institution.vue'  // 已修正大小写
import Schedule from '@/components/admin/schedule/Schedule.vue'
import Analytics from '@/components/admin/analytics/Analytics.vue'
import Finance from '@/components/admin/finance/Finance.vue'
import Permission from '@/components/admin/permission/Permission.vue'
import UserManagement from '@/components/admin/user/User.vue'  // 导入用户管理模块
import Logs from '@/components/admin/logs/Logs.vue'  // 导入系统日志模块
import { StudentManagement } from '@/components/admin/student'  // 导入学生管理组件
import { ApplicationManagement } from '@/components/admin/application'  // 导入申请管理组件
import { SystemSettings } from '@/components/admin/system'  // 导入系统设置模块
import {
  Search, Bell, ChevronDown, LayoutDashboard, Users, FileText, Building,
  Calendar, BarChart, DollarSign, UserCog, Lock, Settings, PlusCircle,
  TrendingUp, TrendingDown, CheckCircle, Clock, Edit2, ArrowRight, Eye,
  RefreshCw, Filter, Edit, Trash2, ChevronLeft, ChevronRight, X, Upload,
  MessageSquare, MoreVertical, MoreHorizontal, CheckSquare, HelpCircle,
  ExternalLink, ChevronsLeft, ChevronsRight, UserPlus, UserCheck, GraduationCap,
  User, School, Mail, Phone, LogOut, UserX, Globe, BookOpen, AlertTriangle, Check, Plus,
  AtSign, CreditCard, Zap, Award, PieChart, BarChart2, Activity, Database, Clipboard,
  Book, Star, Inbox, Home, MapPin, Briefcase, Bookmark, Flag, Image, Map, Share,
  DownloadCloud, Layers, Link, Monitor
} from 'lucide-vue-next'
import AIAssistant from '@/components/admin/ai/AIAssistant.vue'  // 导入AI助手组件
import Dashboard from '@/components/admin/dashboard/Dashboard.vue'  // 导入Dashboard组件


const router = useRouter()
const showUserMenu = ref(false)
const userInfo = ref(null)
const showUserListModal = ref(false)
const userSearchQuery = ref('')
const availableUsers = ref([])
const selectedUser = ref(null)
const sidebarTooltip = ref({ visible: false, text: '', top: 0, left: 0 })
const studentCount = ref(0)

const notificationUnreadCount = computed(() => {
  return notificationStore.statistics.value.unread
})

const fetchStudentCount = async () => {
  try {
    const res = await request.get('/student/admin/students')
    const list = Array.isArray(res?.data) ? res.data : Array.isArray(res?.data?.data) ? res.data.data : []
    studentCount.value = list.length
  } catch (e) {
    studentCount.value = 0
  }
}

// 检查登录状态
onMounted(() => {
  checkLoginStatus()
  loadAvailableUsers()
  fetchStudentCount()
  notificationStore.fetchNotifications()
})

// 加载可用用户列表
const loadAvailableUsers = () => {
  try {
    const loginHistoryJson = localStorage.getItem('adminLoginHistory')
    if (loginHistoryJson) {
      availableUsers.value = JSON.parse(loginHistoryJson)
    }
  } catch (error) {
    console.error('加载用户列表出错:', error)
    availableUsers.value = []
  }
}

// 过滤用户列表
const filteredUsers = computed(() => {
  if (!userSearchQuery.value) {
    return availableUsers.value
  }

  const query = userSearchQuery.value.toLowerCase()
  return availableUsers.value.filter(user =>
    user.nickname.toLowerCase().includes(query) ||
    user.username.toLowerCase().includes(query) ||
    (user.role && user.role.toLowerCase().includes(query))
  )
})

// 检查登录状态
const checkLoginStatus = () => {
  const adminLoggedIn = localStorage.getItem('adminLoggedIn')
  const adminInfo = localStorage.getItem('adminInfo')

  if (adminLoggedIn !== 'true' || !adminInfo) {
    ElMessage.warning('请先登录')
    router.push('/admin/login')
    return
  }

  try {
    userInfo.value = JSON.parse(adminInfo)
  } catch (error) {
    console.error('解析用户信息出错:', error)
    ElMessage.error('用户信息格式错误，请重新登录')
    router.push('/admin/login')
  }
}

// 切换用户菜单显示状态
const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
}

// 切换用户
const switchUser = () => {
  showUserMenu.value = false
  router.push('/admin/login')
}

// 退出登录
const logout = () => {
  // 清除本地存储的用户信息
  localStorage.removeItem('adminLoggedIn')
  localStorage.removeItem('adminInfo')
  // 清除session存储
  sessionStorage.clear()
  // 关闭菜单
  showUserMenu.value = false
  // 显示退出成功消息
  ElMessage.success('已成功退出登录')
  // 跳转到登录页面
  router.push('/admin/login')
}

// 当前活动菜单
const activeMenu = ref('dashboard')
const setActiveMenu = (menu) => {
  activeMenu.value = menu
  hideSidebarTooltip()
  if (menu === 'students') {
    fetchStudentCount()
  }
}

// 侧边栏收缩状态
const sidebarCollapsed = ref(false)
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
  hideSidebarTooltip()
}

const showSidebarTooltip = (event) => {
  if (!sidebarCollapsed.value) return
  const el = event?.currentTarget
  const text = el?.getAttribute?.('data-tooltip')
  if (!text) return
  const rect = el.getBoundingClientRect()
  const gap = 12
  const minTop = 12
  const maxTop = window.innerHeight - 12
  const top = Math.min(Math.max(rect.top + rect.height / 2, minTop), maxTop)
  sidebarTooltip.value = {
    visible: true,
    text,
    top,
    left: rect.right + gap
  }
}

const hideSidebarTooltip = () => {
  if (!sidebarTooltip.value.visible) return
  sidebarTooltip.value = { ...sidebarTooltip.value, visible: false }
}

onMounted(() => {
  window.addEventListener('resize', hideSidebarTooltip)
  window.addEventListener('scroll', hideSidebarTooltip, true)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', hideSidebarTooltip)
  window.removeEventListener('scroll', hideSidebarTooltip, true)
})

// 当前日期
const currentDate = computed(() => {
  const now = new Date()
  return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日 星期${['日', '一', '二', '三', '四', '五', '六'][now.getDay()]}`
})

// 当前时间
const currentTime = ref('')
const updateTime = () => {
  const now = new Date()
  const hours = now.getHours().toString().padStart(2, '0')
  const minutes = now.getMinutes().toString().padStart(2, '0')
  const seconds = now.getSeconds().toString().padStart(2, '0')
  currentTime.value = `${hours}:${minutes}:${seconds}`
}

// 初始化时间并设置定时器
onMounted(() => {
  updateTime()
  setInterval(updateTime, 1000)
})

// 切换用户
const selectUser = (user) => {
  selectedUser.value = user
  showUserListModal.value = false
}

// 登录为指定用户
const loginAsUser = (user) => {
  // 先关闭模态框，避免动画和数据操作同时进行
  showUserListModal.value = false

  // 延迟执行数据操作，确保模态框先关闭
  setTimeout(() => {
    // 更新用户信息
    userInfo.value = user

    // 清除本地存储的用户信息
    localStorage.removeItem('adminLoggedIn')
    localStorage.removeItem('adminInfo')

    // 清除session存储
    sessionStorage.clear()

    // 设置新的登录状态
    localStorage.setItem('adminLoggedIn', 'true')
    localStorage.setItem('adminInfo', JSON.stringify(user))

    // 显示登录成功消息
    ElMessage.success('已成功切换用户')

    // 使用location.reload()替代window.location.reload()，更简洁
    location.reload()
  }, 300) // 添加足够的延迟让模态框关闭动画完成
}

// 显示用户列表
const showUserList = () => {
  // 先加载用户列表，确保数据准备好
  loadAvailableUsers()
  // 先关闭上层菜单
  showUserMenu.value = false
  // 使用nextTick确保DOM已更新
  nextTick(() => {
    // 再显示用户列表模态框
    showUserListModal.value = true
  })
}

// 添加新账号
const addNewAccount = () => {
  console.log('添加新账号，跳转到登录页面')
  showUserListModal.value = false
  // 确保关闭弹窗后才跳转
  setTimeout(() => {
    // 从路由配置可以看到，admin登录页面的路径是 /admin/login
    router.push('/admin/login')
  }, 100)
}

// 生成头像背景颜色
const generateAvatarColor = (name) => {
  if (!name) return '#1890ff'

  // 根据名字生成一个稳定的颜色
  let hash = 0
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash)
  }

  // 转换为HSL颜色，固定饱和度和亮度，只改变色相
  const hue = Math.abs(hash % 360)
  return `hsl(${hue}, 70%, 60%)`
}

const activeTab = ref('basic')
const newTagInput = ref('')
const defaultMaterials = ref([
  { name: '个人简历', required: false, description: '' },
  { name: '个人陈述', required: false, description: '' },
  { name: '推荐信', required: false, description: '' },
  { name: '成绩单', required: false, description: '' },
  { name: '作品集', required: false, description: '' },
  { name: '语言成绩', required: false, description: '' },
  { name: '标准化考试成绩', required: false, description: '' }
])

// 添加标签
const addTag = () => {
  if (newTagInput.value.trim()) {
    newInstitution.value.tags.push(newTagInput.value.trim())
    newTagInput.value = ''
  }
}

// 移除标签
const removeTag = (index) => {
  newInstitution.value.tags.splice(index, 1)
}

const activeDetailTab = ref('基本信息')

const getMaterialName = (key) => {
  const materials = {
    cv: '个人简历',
    ps: '个人陈述',
    recommendation: '推荐信',
    transcript: '成绩单',
    portfolio: '作品集',
    language: '语言成绩',
    standardized: '标准化考试成绩'
  }
  return materials[key] || key
}

const showHelp = () => {
  router.push('/help')
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  color: #333;
  background-color: #f5f7fa;
  overflow-x: hidden;
  /* 隐藏水平滚动条 */
}

.admin-platform {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  overflow-x: hidden;
  /* 隐藏水平滚动条 */
}

/* 顶部导航栏 */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 24px;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  z-index: 10;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-badge {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 18px;
}

.logo-text {
  display: flex;
  flex-direction: column;
}

.logo {
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
  margin: 0;
}

.system-name {
  font-size: 12px;
  color: #666;
}

.datetime-container {
  display: flex;
  align-items: center;
  background-color: transparent;
  border-radius: 6px;
  padding: 8px 12px;
  min-width: 280px;
  border: none;
  gap: 12px;
}

.current-date {
  font-size: 14px;
  font-weight: 500;
  color: #666;
  white-space: nowrap;
}

.datetime-separator {
  font-size: 14px;
  color: #d9d9d9;
  font-weight: 300;
}

.current-time {
  font-size: 14px;
  font-weight: 600;
  color: #1890ff;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  white-space: nowrap;
}

.user-container {
  display: flex;
  align-items: center;
  gap: 16px;
}

.icon-button {
  position: relative;
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.icon-button:hover {
  background-color: #f5f7fa;
  color: #1890ff;
}

.badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background-color: #f5222d;
  color: white;
  font-size: 10px;
  border-radius: 10px;
  padding: 0 5px;
  min-width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 8px;
  transition: all 0.3s;
  position: relative;
}

.user-profile:hover {
  background-color: #f5f7fa;
}

.avatar-wrapper {
  position: relative;
  width: 40px;
  height: 40px;
  margin-right: 12px;
}

.avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  overflow: hidden;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-text {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background-color: #1890ff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 500;
  text-transform: uppercase;
}

.status-dot {
  position: absolute;
  bottom: -2px;
  right: -2px;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: #52c41a;
  border: 2px solid white;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
}

.user-role {
  font-size: 12px;
  color: #999;
}

.dropdown-icon {
  width: 16px;
  height: 16px;
  color: #999;
}

/* 主容器 */
.main-container {
  display: flex;
  flex: 1;
}

/* 侧边栏 */
.sidebar {
  width: 240px;
  flex: 0 0 240px;
  background-color: #fff;
  border-right: 1px solid rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  height: calc(100vh - 64px);
  /* 优化展开收缩动画，使用更平滑的缓动函数 */
  transition: width 0.22s cubic-bezier(0.4, 0, 0.2, 1), flex-basis 0.22s cubic-bezier(0.4, 0, 0.2, 1);
  will-change: width;
  contain: layout paint;
}

.sidebar.collapsed {
  width: 64px;
  flex-basis: 64px;
}

.sidebar-header {
  padding: 16px 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: space-between;
  align-items: center;
  min-height: 56px;
  transition: padding 0.22s cubic-bezier(0.4, 0, 0.2, 1);
}

.sidebar.collapsed .sidebar-header {
  padding: 16px 8px;
  justify-content: center;
}

.menu-title {
  font-size: 12px;
  color: #999;
  text-transform: uppercase;
  letter-spacing: 1px;
  font-weight: 600;
  /* 优化文本淡入淡出效果 */
  transition: opacity 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.menu-list {
  list-style: none;
  padding: 12px 8px;
  flex: 1;
  overflow-y: auto;
  transition: padding 0.22s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 隐藏侧边栏菜单滚动条 */
.menu-list::-webkit-scrollbar {
  width: 0px;
  background: transparent;
}

.sidebar.collapsed .menu-list {
  padding: 12px 4px;
}

.menu-category {
  font-size: 12px;
  color: #999;
  padding: 16px 12px 8px;
  margin-top: 8px;
  font-weight: 600;
  letter-spacing: 1px;
  text-transform: uppercase;
  transition: opacity 0.3s ease;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 10px 12px;
  margin-bottom: 4px;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  justify-content: flex-start;
  text-decoration: none;
  color: #666;
  /* 优化过渡效果，只对背景色和颜色进行动画 */
  transition: background-color 0.14s cubic-bezier(0.4, 0, 0.2, 1), color 0.14s cubic-bezier(0.4, 0, 0.2, 1);
}

.sidebar.collapsed .menu-item {
  justify-content: center;
  padding: 10px;
}

.menu-item:hover {
  background-color: #f5f7fa;
}

.menu-item.active {
  background-color: #e6f7ff;
  color: #1890ff;
}

.menu-icon-wrapper {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  background-color: #f5f7fa;
  flex-shrink: 0;
  position: relative;
  transition: background-color 0.14s cubic-bezier(0.4, 0, 0.2, 1), color 0.14s cubic-bezier(0.4, 0, 0.2, 1), transform 0.14s cubic-bezier(0.4, 0, 0.2, 1);
}

.sidebar.collapsed .menu-icon-wrapper {
  margin-right: 0;
}

.menu-item:hover .menu-icon-wrapper {
  background-color: #e6f7ff;
  color: #1890ff;
  transform: scale(1.03);
}

.menu-item.active .menu-icon-wrapper {
  background-color: #1890ff;
  color: white;
  transform: none;
}

.menu-icon {
  width: 18px;
  height: 18px;
}

.menu-badge {
  position: static;
  margin-left: auto;
  background-color: #f5f7fa;
  color: #666;
  font-size: 12px;
  border-radius: 10px;
  padding: 0 8px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  /* 优化徽章动画效果 */
  transition: background-color 0.2s ease, color 0.2s ease;
}

.menu-item.active .menu-badge {
  background-color: #1890ff;
  color: white;
}

.sidebar-toggle {
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  color: #666;
  /* 优化切换按钮动画效果 */
  transition: background-color 0.2s ease, color 0.2s ease;
  flex-shrink: 0;
}

.sidebar-toggle:hover {
  background-color: #f5f7fa;
  color: #1890ff;
}

.toggle-icon {
  width: 16px;
  height: 16px;
}

.sidebar-tooltip {
  position: fixed;
  transform: translateY(-50%) translateX(0);
  background: rgba(255, 255, 255, 0.98);
  color: #1f2937;
  border: 1px solid rgba(0, 0, 0, 0.08);
  padding: 8px 10px;
  border-radius: 10px;
  font-size: 12px;
  line-height: 1;
  white-space: nowrap;
  z-index: 10000;
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.14);
  backdrop-filter: blur(8px);
}

.sidebar-tooltip::before {
  content: '';
  position: absolute;
  left: -6px;
  top: 50%;
  width: 10px;
  height: 10px;
  transform: translateY(-50%) rotate(45deg);
  background: rgba(255, 255, 255, 0.98);
  border-left: 1px solid rgba(0, 0, 0, 0.08);
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
}

.sidebar-tooltip-enter-active,
.sidebar-tooltip-leave-active {
  transition: opacity 0.14s cubic-bezier(0.4, 0, 0.2, 1), transform 0.14s cubic-bezier(0.4, 0, 0.2, 1);
}

.sidebar-tooltip-enter-from,
.sidebar-tooltip-leave-to {
  opacity: 0;
  transform: translateY(-50%) translateX(-6px) scale(0.98);
}

.sidebar-tooltip-enter-to,
.sidebar-tooltip-leave-from {
  opacity: 1;
  transform: translateY(-50%) translateX(0) scale(1);
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.help-card {
  background: linear-gradient(135deg, #f5f7fa 0%, #e6f7ff 100%);
  border-radius: 12px;
  padding: 16px;
  display: flex;
  gap: 12px;
}

.help-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background-color: #1890ff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.help-content {
  flex: 1;
}

.help-content h4 {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
}

.help-content p {
  font-size: 12px;
  color: #666;
  margin-bottom: 12px;
}

.help-button {
  display: flex;
  align-items: center;
  gap: 4px;
  background-color: white;
  border: none;
  border-radius: 4px;
  padding: 6px 12px;
  font-size: 12px;
  font-weight: 500;
  color: #1890ff;
  cursor: pointer;
  transition: all 0.3s;
}

.help-button:hover {
  background-color: #e6f7ff;
}

.help-button-icon {
  width: 12px;
  height: 12px;
}

/* 内容区 */
.content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  overflow-x: hidden;
  /* 隐藏水平滚动条 */
  position: relative;
  background-color: #f5f7fa;
}

/* 隐藏右侧滚动条 */
.content::-webkit-scrollbar {
  width: 0px;
  background: transparent;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 4px;
  color: #111;
}

.page-subtitle {
  color: #666;
  font-size: 14px;
}

.page-actions {
  display: flex;
  gap: 12px;
}

.page-actions .btn {
  min-width: 100px;
  /* 设置最小宽度 */
  justify-content: center;
  /* 确保内容居中 */
  padding: 0 16px;
  display: flex;
  align-items: center;
}

.page-actions .btn span {
  display: inline-block;
  text-align: center;
}

.btn {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 16px;
  height: 40px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
  text-align: center;
  /* 添加文本居中 */
}

.btn-sm {
  height: 32px;
  padding: 0 12px;
  font-size: 12px;
}

.btn-primary {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  color: white;
  box-shadow: 0 2px 6px rgba(24, 144, 255, 0.2);
}

.btn-primary:hover {
  background-color: #1890ff;
  border-color: #1890ff;
}

.btn-secondary {
  background-color: #52c41a;
  color: white;
  border: 1px solid #52c41a;
}

.btn-secondary:hover {
  background-color: #5ed421;
  border-color: #5ed421;
}

.btn-outline {
  background-color: white;
  border: 1px solid #d9d9d9;
  color: #666;
}

.btn-outline:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.btn-icon {
  margin-right: 6px;
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 24px;
}

.stat-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: flex-start;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
  border: 1px solid rgba(0, 0, 0, 0.02);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.08);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.stat-content {
  flex: 1;
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #111;
}

.stat-label {
  color: #666;
  font-size: 14px;
  margin-bottom: 12px;
}

.stat-trend {
  display: flex;
  align-items: center;
  font-size: 12px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 12px;
}

.stat-trend.up {
  color: #52c41a;
  background-color: rgba(82, 196, 26, 0.1);
}

.stat-trend.down {
  color: #f5222d;
  background-color: rgba(245, 34, 45, 0.1);
}

.trend-icon {
  width: 14px;
  height: 14px;
  margin-right: 4px;
}

.stat-progress {
  margin-top: 8px;
}

.progress-bar {
  height: 6px;
  background-color: #f0f0f0;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 3px;
}

/* 仪表板部分 */
.dashboard-sections {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.section-icon {
  width: 20px;
  height: 20px;
  color: #1890ff;
}

.section-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #111;
}

.section-tabs {
  display: flex;
  gap: 8px;
}

.tab-button {
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 14px;
  background-color: transparent;
  border: none;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.tab-button:hover {
  background-color: #f5f7fa;
}

.tab-button.active {
  background-color: #e6f7ff;
  color: #1890ff;
  font-weight: 500;
}

.btn-icon {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  background-color: transparent;
  border: none;
  color: #666;
  transition: all 0.3s;
}

.btn-icon:hover {
  background-color: #f5f7fa;
  color: #1890ff;
}

.btn-icon-sm {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  background-color: transparent;
  border: none;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-icon-sm:hover {
  background-color: #f5f7fa;
  color: #1890ff;
}

/* 任务列表 */
.tasks-section,
.schedule-section,
.recent-applications {
  background-color: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(0, 0, 0, 0.02);
}

.task-list,
.schedule-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.task-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border-radius: 8px;
  background-color: #f9f9f9;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.task-item:hover {
  background-color: #f0f0f0;
  border-color: #e0e0e0;
}

.task-checkbox {
  width: 18px;
  height: 18px;
  border-radius: 4px;
  cursor: pointer;
}

.task-content {
  flex: 1;
}

.task-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
  color: #111;
}

.task-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.task-priority {
  padding: 2px 8px;
  border-radius: 12px;
  font-weight: 500;
}

.task-priority.紧急优先 {
  background-color: #fff1f0;
  color: #f5222d;
}

.task-priority.高优先级 {
  background-color: #fff7e6;
  color: #fa8c16;
}

.task-priority.中等优先 {
  background-color: #e6f7ff;
  color: #1890ff;
}

.task-due {
  color: #999;
}

.task-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-sm {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  object-fit: cover;
}

.task-actions {
  display: flex;
  gap: 4px;
}

.view-all {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px;
  color: #1890ff;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.3s;
}

.view-all:hover {
  background-color: #e6f7ff;
}

/* 日程列表 */
.schedule-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px;
  border-radius: 8px;
  background-color: #f9f9f9;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.schedule-item:hover {
  background-color: #f0f0f0;
  border-color: #e0e0e0;
}

.schedule-time {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 60px;
}

.schedule-time span {
  font-weight: 600;
  color: #1890ff;
  margin-bottom: 8px;
}

.time-line {
  flex: 1;
  width: 2px;
  background-color: #1890ff;
  opacity: 0.3;
}

.schedule-content {
  flex: 1;
}

.schedule-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
  color: #111;
}

.schedule-desc {
  font-size: 12px;
  color: #666;
  margin-bottom: 8px;
}

.schedule-tags {
  display: flex;
  gap: 8px;
}

.schedule-tag {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  background-color: #e6f7ff;
  color: #1890ff;
}

/* 申请表格 */
.applications-table {
  margin-top: 16px;
  overflow-x: hidden;
  /* 隐藏水平滚动条 */
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background-color: #fafafa;
}

th {
  padding: 12px 16px;
  text-align: left;
  font-weight: 600;
  color: #666;
  border-bottom: 1px solid #f0f0f0;
}

td {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.th-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.td-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.table-checkbox {
  width: 16px;
  height: 16px;
  border-radius: 4px;
}

.student-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.pending {
  background-color: #fff7e6;
  color: #fa8c16;
}

.status-badge.processing {
  background-color: #e6f7ff;
  color: #1890ff;
}

.status-badge.completed {
  background-color: #f6ffed;
  color: #52c41a;
}

.action-buttons {
  display: flex;
  gap: 8px;
}


/* 学生管理页面 */
.students-table {
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 24px;
  border: 1px solid rgba(0, 0, 0, 0.02);
}

.student-avatar {
  width: 40px;
  height: 40px;
  border-radius: 20px;
  /* 改为圆形 */
  overflow: hidden;
  margin-right: 12px;
  border: 2px solid #f0f0f0;
  /* 添加边框 */
  transition: all 0.3s;
}

.student-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.student-avatar .avatar-text {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 500;
  color: white;
  text-transform: uppercase;
}

.student-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.student-basic-info {
  display: flex;
  flex-direction: column;
}

.student-name {
  font-weight: 500;
  color: #333;
}

.student-id {
  font-size: 12px;
  color: #999;
}

.empty-list p {
  font-size: 14px;
  margin: 0;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 添加简单的脉动动画给登录按钮 */
@keyframes pulse {
  0% {
    transform: scale(1);
  }

  50% {
    transform: scale(1.05);
  }

  100% {
    transform: scale(1);
  }
}

.user-item .btn.btn-primary {
  transition: all 0.2s ease;
}

.user-item .btn.btn-primary:hover {
  animation: pulse 0.5s ease-in-out;
  box-shadow: 0 0 8px rgba(24, 144, 255, 0.5);
}

/* 学生头像样式 */
.student-avatar {
  width: 40px;
  height: 40px;
  border-radius: 20px;
  /* 改为圆形 */
  overflow: hidden;
  margin-right: 12px;
  border: 2px solid #f0f0f0;
  /* 添加边框 */
  transition: all 0.3s;
}

.student-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.student-avatar .avatar-text {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 500;
  color: white;
  text-transform: uppercase;
}

.student-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.student-name {
  font-weight: 500;
  color: #333;
}

.student-details {
  display: flex;
  flex-direction: column;
  font-size: 12px;
  color: #999;
}

.target-countries {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.country-tag {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  background-color: #f5f7fa;
  color: #666;
  white-space: nowrap;
}

.status-badge.active {
  background-color: #f6ffed;
  color: #52c41a;
}

.status-badge.inactive {
  background-color: #f5f7fa;
  color: #999;
}

.status-badge.graduated {
  background-color: #e6f7ff;
  color: #1890ff;
}

/* 复选框组 */
.checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 8px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  background-color: #f5f7fa;
  transition: all 0.2s ease;
  border: 1px solid transparent;
}

.checkbox-label:hover {
  background-color: #e6f7ff;
  border-color: #91caff;
}

.checkbox-label input {
  width: 18px;
  height: 18px;
  accent-color: #1890ff;
  cursor: pointer;
}

.checkbox-label input:checked+span {
  color: #1890ff;
  font-weight: 500;
}

.checkbox-label span {
  font-size: 14px;
  color: #333;
}

/* 学生详情 */
.student-detail-header {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.student-detail-avatar {
  flex-shrink: 0;
}

.student-detail-avatar img {
  width: 100px;
  height: 100px;
  border-radius: 50px;
  /* 改为圆形 */
  object-fit: cover;
  border: 2px solid #f0f0f0;
  /* 统一边框样式 */
}

.student-detail-info {
  flex: 1;
}

.student-detail-info h2 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #111;
}

.student-detail-id {
  font-size: 14px;
  color: #999;
  font-weight: normal;
  margin-left: 8px;
}

.student-detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
}

.meta-icon {
  width: 16px;
  height: 16px;
  color: #999;
}

.student-detail-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.student-detail-tabs {
  display: flex;
  gap: 4px;
  margin-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 16px;
}

.detail-tab {
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  background-color: transparent;
  border: none;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.detail-tab:hover {
  background-color: #f5f7fa;
}

.detail-tab.active {
  background-color: #e6f7ff;
  color: #1890ff;
  font-weight: 500;
}

.student-detail-section {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #111;
}

.detail-info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 16px;
}

.detail-info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-info-label {
  font-size: 12px;
  color: #999;
}

.detail-info-value {
  font-size: 14px;
  color: #333;
}

/* 用户菜单样式 */
.user-menu {
  position: absolute;
  top: 100%;
  right: 0;
  width: 240px;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  z-index: 100;
}

.user-menu .menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.3s;
  color: #333;
}

.user-menu .menu-item:hover {
  background-color: #f5f7fa;
}

.user-menu .menu-icon {
  width: 16px;
  height: 16px;
  color: #666;
}

.rotate-180 {
  transform: rotate(180deg);
}

/* 确保用户头像容器有相对定位 */
.user-profile {
  position: relative;
}

/* 添加过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
  will-change: opacity, transform;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 用户列表模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(3px);
  will-change: opacity;
  /* 优化动画性能 */
}

.modal-content {
  background-color: white;
  border-radius: 16px;
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  overflow: hidden;
  /* 修改为hidden来解决右上角和右下角尖角问题 */
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2);
  animation: modal-appear 0.3s ease-out;
  will-change: transform, opacity;
  /* 优化动画性能 */
  transform: translateZ(0);
  /* 启用GPU加速 */
}

@keyframes modal-appear {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.modal-icon {
  width: 24px;
  height: 24px;
  color: #1890ff;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #111;
}

.modal-body {
  padding: 24px;
}

.modal-footer {
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.search-box {
  position: relative;
  margin-bottom: 24px;
}

.search-box .search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  width: 16px;
  height: 16px;
  color: #999;
}

.search-box .search-input {
  width: 100%;
  padding: 12px 12px 12px 36px;
  border-radius: 8px;
  border: 1px solid #d9d9d9;
  font-size: 14px;
  transition: all 0.3s;
}

.search-box .search-input:focus {
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
  outline: none;
}

.user-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 400px;
  overflow-y: auto;
  padding-right: 8px;
}

.user-list::-webkit-scrollbar {
  width: 6px;
}

.user-list::-webkit-scrollbar-track {
  background: #f5f5f5;
  border-radius: 3px;
}

.user-list::-webkit-scrollbar-thumb {
  background: #d9d9d9;
  border-radius: 3px;
}

.user-list::-webkit-scrollbar-thumb:hover {
  background: #bfbfbf;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s;
  cursor: pointer;
}

.user-item:hover {
  background-color: #f5f7fa;
  border-color: #e6e6e6;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.user-item.selected {
  background-color: #e6f7ff;
  border-color: #1890ff;
}

.user-avatar {
  width: 48px;
  height: 48px;
  border-radius: 24px;
  overflow: hidden;
  border: 2px solid #f0f0f0;
  transition: all 0.3s;
}

.user-item.selected .user-avatar {
  border-color: #1890ff;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-avatar .avatar-text {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 500;
  color: white;
  text-transform: uppercase;
}

.user-details {
  flex: 1;
}

.user-details h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.user-details p {
  margin: 0 0 4px 0;
  font-size: 12px;
  color: #999;
}

.user-email {
  font-size: 12px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 4px;
}

.user-email::before {
  content: "";
  display: inline-block;
  width: 12px;
  height: 12px;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='%23666' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z'%3E%3C/path%3E%3Cpolyline points='22,6 12,13 2,6'%3E%3C/polyline%3E%3C/svg%3E");
  background-size: contain;
  background-repeat: no-repeat;
}

.empty-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #999;
}

.empty-icon {
  width: 48px;
  height: 48px;
  margin-bottom: 16px;
  color: #d9d9d9;
}

.empty-list p {
  font-size: 14px;
  margin: 0;
}

/* 强制所有按钮文字居中 */
button.btn {
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
}

button.btn span {
  text-align: center;
  white-space: nowrap;
  /* a防止文本换行 */
  flex: none;
  /* 修复之前的flex:1可能导致的问题 */
}

/* 模态框底部按钮 */
.modal-footer .btn {
  min-width: 80px;
  justify-content: center;
}

.filter-actions .btn-icon {
  margin-right: 4px;
  /* 图标右侧间距 */
}

/* 自定义按钮样式覆盖 */
.page-actions .btn,
.filter-actions .btn {
  position: relative;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
}

.page-actions .btn span,
.filter-actions .btn span {
  position: relative;
  display: inline-block;
  text-align: center;
  left: -3px;
  /* 微调文字位置，让其更居中 */
}

/* 导出数据和添加学生按钮 */
.page-actions .btn {
  min-width: 110px;
  /* 调整最小宽度 */
  padding: 0 20px;
}

/* 筛选和重置按钮 */
.filter-actions .btn {
  min-width: 90px;
  /* 调整最小宽度 */
  padding: 0 16px;
}

/* 使新建申请按钮文字居中 */
.page-header .page-actions .btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 40px;
}

.page-header .page-actions .btn-primary {
  min-width: 140px;
}

.page-header .page-actions .btn-outline {
  min-width: 160px;
}

.page-header .page-actions .btn .btn-icon {
  width: 16px;
  height: 16px;
  margin-right: 8px;
  flex-shrink: 0;
}

.page-header .page-actions .btn span {
  text-align: center;
  flex-grow: 1;
  display: block;
}

/* 自定义按钮样式 */
.custom-button {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 10px;
}

.custom-button .el-icon {
  margin-right: 4px;
  font-size: 16px;
}

.section-button {
  padding: 8px 12px;
  font-size: 14px;
}

.modal-content .modal-body {
  overflow-y: auto;
  /* 添加滚动条到modal-body */
  max-height: calc(80vh - 130px);
  /* 减去header和footer的高度 */
}


/* 自定义institution-content样式 */
.institution-content {
  padding: 0;
}

/* 通知中心样式调整 */
.notification-content {
  padding: 0;
}

.notice-center {
  padding: 0;
}

.logs-content {
  padding: 0;
}

.system-content {
  padding: 0;
}
</style>
