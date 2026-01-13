<template>
  <div class="system-settings">
    <div class="page-header">
      <div>
        <h2 class="page-title">系统设置</h2>
        <p class="page-subtitle">管理系统的基本配置、参数及功能设置</p>
      </div>
      <div class="page-actions">
        <el-button class="custom-button" type="default" @click="resetToDefault">
          <RotateCcw class="btn-icon" />
          恢复默认
        </el-button>
        <el-button class="custom-button" type="primary" @click="saveSettings">
          <Save class="btn-icon" />
          保存设置
        </el-button>
      </div>
    </div>

    <div class="settings-container">
      <div class="settings-sidebar">
        <div class="settings-nav">
          <div v-for="(section, index) in settingSections" :key="index" class="nav-item"
            :class="{ active: activeSection === section.key }" @click="setActiveSection(section.key)">
            <component :is="section.icon" class="nav-icon" />
            <span>{{ section.name }}</span>
          </div>
        </div>
      </div>

      <div class="settings-content">
        <!-- 基本信息设置 -->
        <div v-if="activeSection === 'basic'" class="settings-section">
          <h3 class="section-title">基本信息设置</h3>

          <div class="form-group">
            <label>系统名称</label>
            <el-input v-model="settings.systemName" placeholder="请输入系统名称" />
          </div>

          <div class="form-group">
            <label>系统简介</label>
            <el-input v-model="settings.systemDescription" type="textarea" rows="3" placeholder="请输入系统简介" />
          </div>

          <div class="form-group">
            <label>系统LOGO</label>
            <div class="logo-uploader">
              <input ref="logoInputRef" class="logo-input" type="file" accept="image/*"
                @change="handleLogoFileChange" />
              <div v-if="settings.logoUrl" class="logo-preview">
                <img :src="settings.logoUrl" alt="系统LOGO" />
                <button class="remove-logo" @click="removeLogo">
                  <X class="icon-sm" />
                </button>
              </div>
              <div v-else class="upload-placeholder" @click="uploadLogo">
                <Upload class="upload-icon" />
                <span>点击上传LOGO</span>
              </div>
            </div>
            <p class="form-hint">推荐尺寸: 200px × 200px，支持JPG、PNG格式</p>
          </div>
        </div>

        <!-- 界面设置 -->
        <div v-if="activeSection === 'ui'" class="settings-section">
          <h3 class="section-title">界面设置</h3>

          <div class="form-group">
            <label>主题色</label>
            <div class="color-picker">
              <div v-for="color in themeColors" :key="color.value" class="color-option"
                :class="{ active: settings.themeColor === color.value }" :style="{ backgroundColor: color.value }"
                @click="settings.themeColor = color.value"></div>
              <div class="color-input">
                <el-color-picker v-model="settings.themeColor" size="small" />
              </div>
            </div>
          </div>

          <div class="form-group">
            <label>显示模式</label>
            <div class="theme-switch">
              <div class="theme-option" :class="{ active: !settings.darkMode }" @click="settings.darkMode = false">
                <Sun class="theme-icon" />
                <span>浅色模式</span>
              </div>
              <div class="theme-option" :class="{ active: settings.darkMode }" @click="settings.darkMode = true">
                <Moon class="theme-icon" />
                <span>深色模式</span>
              </div>
            </div>
          </div>

          <div class="form-group">
            <label>布局密度</label>
            <div class="switch-with-label">
              <span>紧凑布局</span>
              <el-switch v-model="settings.compactMode" />
            </div>
            <p class="form-hint">开启后界面元素将更加紧凑，适合小屏幕设备</p>
          </div>

          <div class="form-group">
            <label>侧边栏默认状态</label>
            <el-select v-model="settings.menuCollapsed" placeholder="请选择">
              <el-option label="展开" :value="false" />
              <el-option label="折叠" :value="true" />
            </el-select>
          </div>
        </div>

        <!-- 通知设置 -->
        <div v-if="activeSection === 'notification'" class="settings-section">
          <h3 class="section-title">通知设置</h3>

          <div class="form-group">
            <label>通知方式</label>
            <div class="checkbox-group">
              <div class="checkbox-item">
                <el-checkbox v-model="settings.emailNotifications">邮件通知</el-checkbox>
              </div>
              <div class="checkbox-item">
                <el-checkbox v-model="settings.browserNotifications">浏览器通知</el-checkbox>
              </div>
              <div class="checkbox-item">
                <el-checkbox v-model="settings.soundNotifications">声音提醒</el-checkbox>
              </div>
            </div>
          </div>

          <div class="form-group">
            <label>通知频率</label>
            <el-select v-model="settings.notificationFrequency" placeholder="请选择">
              <el-option label="实时通知" value="immediate" />
              <el-option label="每小时汇总" value="hourly" />
              <el-option label="每日汇总" value="daily" />
            </el-select>
          </div>

          <div class="form-group">
            <label>免打扰时段</label>
            <div class="switch-with-label space-between">
              <span>开启免打扰</span>
              <el-switch v-model="settings.muteNotifications" />
            </div>
            <div v-if="settings.muteNotifications" class="time-range">
              <el-time-picker v-model="settings.muteStartTime" format="HH:mm" value-format="HH:mm" placeholder="开始时间" />
              <span class="time-separator">至</span>
              <el-time-picker v-model="settings.muteEndTime" format="HH:mm" value-format="HH:mm" placeholder="结束时间" />
            </div>
            <p v-if="settings.muteNotifications" class="form-hint">
              在此时段内将不会收到任何通知
            </p>
          </div>

          <div class="form-group">
            <label>通知类型</label>
            <div class="notification-types">
              <div v-for="(type, index) in notificationTypes" :key="index" class="notification-type-item">
                <div class="notification-type-header">
                  <span>{{ type.name }}</span>
                  <el-switch v-model="type.enabled" />
                </div>
                <p class="notification-type-desc">{{ type.description }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 邮件设置 -->
        <div v-if="activeSection === 'email'" class="settings-section">
          <h3 class="section-title">邮件服务设置</h3>

          <div class="form-group">
            <label>SMTP服务器</label>
            <el-input v-model="settings.smtpServer" placeholder="例如: smtp.example.com" />
          </div>

          <div class="form-group">
            <label>SMTP端口</label>
            <el-input v-model="settings.smtpPort" placeholder="例如: 587" />
          </div>

          <div class="form-group">
            <label>发件人邮箱</label>
            <el-input v-model="settings.senderEmail" placeholder="例如: noreply@example.com" />
          </div>

          <div class="form-group">
            <label>发件人名称</label>
            <el-input v-model="settings.senderName" placeholder="例如: 新英美留学" />
          </div>

          <div class="form-group">
            <label>SMTP用户名</label>
            <el-input v-model="settings.smtpUsername" placeholder="请输入SMTP用户名" />
          </div>

          <div class="form-group">
            <label>SMTP密码</label>
            <el-input v-model="settings.smtpPassword" type="password" placeholder="请输入SMTP密码" />
          </div>

          <div class="form-actions">
            <el-button type="primary" @click="testEmailSettings">测试邮件设置</el-button>
          </div>
        </div>

        <!-- 备份设置 -->
        <div v-if="activeSection === 'backup'" class="settings-section">
          <h3 class="section-title">数据备份设置</h3>

          <div class="form-group">
            <label>自动备份</label>
            <el-switch v-model="settings.autoBackup" />
          </div>

          <div class="form-group" v-if="settings.autoBackup">
            <label>备份频率</label>
            <el-select v-model="settings.backupFrequency" placeholder="请选择备份频率">
              <el-option label="每天" value="daily" />
              <el-option label="每周" value="weekly" />
              <el-option label="每月" value="monthly" />
            </el-select>
          </div>

          <div class="form-group" v-if="settings.autoBackup">
            <label>保留备份数量</label>
            <el-input-number v-model="settings.backupCount" :min="1" :max="30" />
          </div>

          <div class="backup-history">
            <h4>备份历史</h4>
            <div class="backup-list">
              <div v-for="(backup, index) in backupHistory" :key="index" class="backup-item">
                <div class="backup-info">
                  <div class="backup-name">{{ backup.name }}</div>
                  <div class="backup-time">{{ backup.time }}</div>
                  <div class="backup-size">{{ backup.size }}</div>
                </div>
                <div class="backup-actions">
                  <button class="btn-icon-sm" title="下载备份">
                    <Download class="icon-sm" />
                  </button>
                  <button class="btn-icon-sm" title="恢复备份">
                    <RefreshCw class="icon-sm" />
                  </button>
                  <button class="btn-icon-sm" title="删除备份">
                    <Trash2 class="icon-sm" />
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div class="form-actions">
            <el-button type="primary" @click="createBackup">立即备份</el-button>
          </div>
        </div>

        <!-- 安全设置 -->
        <div v-if="activeSection === 'security'" class="settings-section">
          <h3 class="section-title">安全设置</h3>

          <div class="form-group">
            <label>密码策略</label>
            <el-select v-model="settings.passwordPolicy" placeholder="请选择密码策略">
              <el-option label="简单 (仅字母)" value="simple" />
              <el-option label="中等 (字母+数字)" value="medium" />
              <el-option label="强 (字母+数字+特殊字符)" value="strong" />
            </el-select>
          </div>

          <div class="form-group">
            <label>密码最小长度</label>
            <el-input-number v-model="settings.minPasswordLength" :min="6" :max="20" />
          </div>

          <div class="form-group">
            <label>密码过期时间</label>
            <el-select v-model="settings.passwordExpiry" placeholder="请选择密码过期时间">
              <el-option label="永不过期" value="never" />
              <el-option label="30天" value="30days" />
              <el-option label="60天" value="60days" />
              <el-option label="90天" value="90days" />
            </el-select>
          </div>

          <div class="form-group">
            <label>启用两步验证</label>
            <el-switch v-model="settings.twoFactorAuth" />
          </div>

          <div class="form-group">
            <label>登录失败锁定</label>
            <div class="inline-group">
              <el-switch v-model="settings.loginLockEnabled" />
              <span v-if="settings.loginLockEnabled">失败
                <el-input-number v-model="settings.maxLoginAttempts" :min="3" :max="10" size="small" />
                次后锁定
                <el-input-number v-model="settings.lockDuration" :min="5" :max="60" size="small" />
                分钟
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Save,
  Settings,
  Mail,
  Database,
  Shield,
  Upload,
  X,
  RefreshCw,
  Trash2,
  Download,
  Globe,
  AlertTriangle,
  FileText,
  Users,
  Bell,
  Layout,
  Palette,
  Monitor,
  Moon,
  Sun,
  RotateCcw
} from 'lucide-vue-next'

const STORAGE_KEY = 'adminSystemSettings'
const NOTIFICATION_TYPES_KEY = 'adminSystemNotificationTypes'

const activeSection = ref('basic')
const settingSections = [
  { key: 'basic', name: '基本设置', icon: Settings },
  { key: 'ui', name: '界面设置', icon: Palette },
  { key: 'email', name: '邮件设置', icon: Mail },
  { key: 'notification', name: '通知设置', icon: Bell },
  { key: 'backup', name: '备份设置', icon: Database },
  { key: 'security', name: '安全设置', icon: Shield },
]

const settings = ref({
  // 基本设置
  systemName: '新英美留学管理系统',
  systemDescription: '专业留学申请管理平台，助力学生实现留学梦想',
  logoUrl: '',

  // 界面设置
  themeColor: '#1890ff',
  darkMode: false,
  compactMode: false,
  menuCollapsed: false,

  // 通知设置
  emailNotifications: true,
  browserNotifications: true,
  soundNotifications: false,
  notificationFrequency: 'immediate',
  muteStartTime: '22:00',
  muteEndTime: '08:00',
  muteNotifications: false,

  // 邮件设置
  smtpServer: '',
  smtpPort: '587',
  senderEmail: '',
  senderName: '',
  smtpUsername: '',
  smtpPassword: '',

  // 备份设置
  autoBackup: true,
  backupFrequency: 'daily',
  backupCount: 7,

  // 安全设置
  passwordPolicy: 'medium',
  minPasswordLength: 8,
  passwordExpiry: 'never',
  twoFactorAuth: false,
  loginLockEnabled: true,
  maxLoginAttempts: 5,
  lockDuration: 30
})

const backupHistory = [
  { name: '系统备份_20230601', time: '2023-06-01 03:00', size: '125MB' },
  { name: '系统备份_20230602', time: '2023-06-02 03:00', size: '126MB' },
  { name: '系统备份_20230603', time: '2023-06-03 03:00', size: '125MB' },
  { name: '手动备份_20230604', time: '2023-06-04 15:30', size: '126MB' },
]

// 主题颜色
const themeColors = [
  { name: '默认蓝', value: '#1890ff' },
  { name: '拂晓蓝', value: '#409EFF' },
  { name: '薄暮红', value: '#F5222D' },
  { name: '火山橙', value: '#FA541C' },
  { name: '日暮黄', value: '#FAAD14' },
  { name: '极光绿', value: '#52C41A' },
  { name: '明青', value: '#13C2C2' },
  { name: '酱紫', value: '#722ED1' }
]

// 通知类型
const notificationTypes = ref([
  {
    name: '系统通知',
    enabled: true,
    description: '系统更新、维护等重要通知'
  },
  {
    name: '申请状态变更',
    enabled: true,
    description: '学生申请状态发生变化时通知'
  },
  {
    name: '任务提醒',
    enabled: true,
    description: '任务即将到期或新分配的任务'
  },
  {
    name: '消息通知',
    enabled: true,
    description: '收到新的消息或回复'
  },
  {
    name: '日程提醒',
    enabled: true,
    description: '即将开始的会议或日程安排'
  }
])

// 默认设置备份（用于恢复默认值）
const defaultSettings = JSON.parse(JSON.stringify(settings.value))
const defaultNotificationTypes = JSON.parse(JSON.stringify(notificationTypes.value))

// 设置当前激活的设置部分
const setActiveSection = (section) => {
  activeSection.value = section
}

const applyRuntimeSettings = () => {
  if (typeof document !== 'undefined') {
    const color = settings.value?.themeColor || '#1890ff'
    document.documentElement.style.setProperty('--el-color-primary', color)
    document.documentElement.classList.toggle('dark', !!settings.value?.darkMode)
  }

  try {
    localStorage.setItem('sidebarCollapsed', settings.value?.menuCollapsed ? 'true' : 'false')
  } catch (e) { }
}

const loadSettings = () => {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (raw) {
      const parsed = JSON.parse(raw)
      const next = JSON.parse(JSON.stringify(defaultSettings))
      Object.keys(next).forEach(key => {
        if (Object.prototype.hasOwnProperty.call(parsed, key)) {
          next[key] = parsed[key]
        }
      })
      settings.value = next
    }
  } catch (e) { }

  try {
    const rawTypes = localStorage.getItem(NOTIFICATION_TYPES_KEY)
    if (rawTypes) {
      const parsedTypes = JSON.parse(rawTypes)
      if (Array.isArray(parsedTypes)) {
        notificationTypes.value = parsedTypes
      }
    }
  } catch (e) { }

  applyRuntimeSettings()
}

const persistSettings = () => {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(settings.value))
  localStorage.setItem(NOTIFICATION_TYPES_KEY, JSON.stringify(notificationTypes.value))
}

onMounted(() => {
  loadSettings()
})

// 上传Logo
const uploadLogo = () => {
  if (logoInputRef.value) {
    logoInputRef.value.value = ''
    logoInputRef.value.click()
  }
}

const logoInputRef = ref(null)

const handleLogoFileChange = async (event) => {
  const file = event?.target?.files?.[0]
  if (!file) return

  if (!file.type?.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }

  const maxSizeBytes = 2 * 1024 * 1024
  if (file.size > maxSizeBytes) {
    ElMessage.error('图片过大，请选择不超过2MB的图片')
    return
  }

  const reader = new FileReader()
  reader.onload = () => {
    const result = reader.result
    if (typeof result === 'string') {
      settings.value.logoUrl = result
      ElMessage.success('Logo已更新')
    } else {
      ElMessage.error('Logo读取失败')
    }
  }
  reader.onerror = () => ElMessage.error('Logo读取失败')
  reader.readAsDataURL(file)
}

// 移除Logo
const removeLogo = () => {
  settings.value.logoUrl = ''
  ElMessage.success('Logo已移除')
}

// 测试邮件设置
const testEmailSettings = () => {
  ElMessage.success('测试邮件已发送，请检查收件箱')
}

// 创建备份
const createBackup = () => {
  ElMessage.success('系统备份已开始，请稍后查看备份列表')
}

// 重置为默认设置
const resetToDefault = () => {
  ElMessageBox.confirm(
    '确定要恢复所有设置到默认值吗？此操作不可撤销。',
    '恢复默认设置',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    settings.value = JSON.parse(JSON.stringify(defaultSettings))
    notificationTypes.value = JSON.parse(JSON.stringify(defaultNotificationTypes))
    try {
      localStorage.removeItem(STORAGE_KEY)
      localStorage.removeItem(NOTIFICATION_TYPES_KEY)
    } catch (e) { }
    applyRuntimeSettings()
    ElMessage.success('已恢复默认设置')
  }).catch(() => {
    // 用户取消操作
  })
}

// 保存设置
const saveSettings = () => {
  const loadingMessage = ElMessage({
    message: '正在保存设置...',
    type: 'info',
    duration: 0
  })

  setTimeout(() => {
    loadingMessage.close()
    try {
      persistSettings()
      applyRuntimeSettings()
    } catch (e) {
      ElMessage.error('保存失败，请稍后重试')
      return
    }
    ElMessage.success('系统设置已保存')
  }, 1500)
}
</script>

<style scoped>
.system-settings {
  padding: 24px;
}

.settings-container {
  display: flex;
  gap: 24px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  margin-bottom: 24px;
}

.settings-sidebar {
  width: 220px;
  border-right: 1px solid #f0f0f0;
  background-color: #f9f9f9;
}

.settings-nav {
  padding: 12px 0;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 12px 18px;
  cursor: pointer;
  transition: all 0.3s;
  color: #666;
}

.nav-item:hover {
  background-color: #f0f0f0;
}

.nav-item.active {
  background-color: #e6f7ff;
  color: #1890ff;
  font-weight: 500;
  position: relative;
}

.nav-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background-color: #1890ff;
}

.nav-icon {
  margin-right: 12px;
  width: 18px;
  height: 18px;
}

.settings-content {
  flex: 1;
  padding: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #333;
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.form-hint {
  margin-top: 8px;
  font-size: 12px;
  color: #999;
}

.form-actions {
  margin-top: 24px;
}

.logo-uploader {
  width: 200px;
  height: 200px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px dashed #d9d9d9;
  position: relative;
}

.logo-input {
  display: none;
}

.logo-preview {
  width: 100%;
  height: 100%;
  position: relative;
}

.logo-preview img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.remove-logo {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 24px;
  height: 24px;
  border-radius: 12px;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.remove-logo:hover {
  background-color: rgba(0, 0, 0, 0.7);
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.upload-placeholder:hover {
  background-color: #f5f7fa;
}

.upload-icon {
  width: 32px;
  height: 32px;
  color: #999;
  margin-bottom: 12px;
}

.inline-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 颜色选择器 */
.color-picker {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
}

.color-option {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.color-option.active {
  border-color: #333;
  transform: scale(1.1);
}

.color-option:hover:not(.active) {
  transform: scale(1.05);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.color-input {
  margin-left: 8px;
}

/* 主题切换 */
.theme-switch {
  display: flex;
  gap: 16px;
}

.theme-option {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  cursor: pointer;
  transition: all 0.3s;
}

.theme-option:hover {
  background-color: #f5f7fa;
}

.theme-option.active {
  border-color: #1890ff;
  background-color: #e6f7ff;
}

.theme-icon {
  width: 32px;
  height: 32px;
  margin-bottom: 8px;
}

/* 开关与标签 */
.switch-with-label {
  display: flex;
  align-items: center;
  gap: 16px;
}

.space-between {
  justify-content: space-between;
}

/* 时间范围选择 */
.time-range {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 12px;
}

.time-separator {
  color: #999;
}

/* 通知类型 */
.notification-types {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.notification-type-item {
  padding: 16px;
  border-radius: 8px;
  background-color: #f9f9f9;
  border: 1px solid #f0f0f0;
}

.notification-type-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.notification-type-header span {
  font-weight: 500;
  color: #333;
}

.notification-type-desc {
  font-size: 12px;
  color: #666;
  margin: 0;
}

/* 复选框组 */
.checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.checkbox-item {
  min-width: 120px;
}

.backup-history {
  margin-top: 32px;
}

.backup-history h4 {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 16px;
  color: #333;
}

.backup-list {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  overflow: hidden;
}

.backup-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
}

.backup-item:last-child {
  border-bottom: none;
}

.backup-info {
  display: flex;
  align-items: center;
  gap: 24px;
}

.backup-name {
  font-weight: 500;
  color: #333;
}

.backup-time,
.backup-size {
  color: #999;
  font-size: 14px;
}

.backup-actions {
  display: flex;
  gap: 8px;
}

.icon-sm {
  width: 16px;
  height: 16px;
}

/* 页面头部按钮 */
.page-actions {
  display: flex;
  gap: 12px;
}

.page-actions .custom-button {
  display: flex;
  align-items: center;
}

.btn-icon {
  margin-right: 8px;
}
</style>
