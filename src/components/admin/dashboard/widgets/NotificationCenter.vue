<template>
  <div class="notification-center-widget">
    <div class="widget-header">
      <div class="header-info">
        <Bell class="header-icon" />
        <div>
          <h3>通知中心</h3>
          <p>{{ unreadCount }} 条未读</p>
        </div>
      </div>
      <div class="header-actions">
        <button class="text-btn" @click="markAllAsRead">全部已读</button>
      </div>
    </div>
    <div class="widget-content">
      <div class="notification-list">
        <div 
          v-for="(notification, index) in notifications.slice(0, 4)" 
          :key="index"
          class="notification-item"
          :class="{ unread: !notification.read }"
        >
          <div class="notification-icon" :class="notification.type">
            <component :is="notification.icon" />
          </div>
          <div class="notification-content">
            <div class="notification-title">{{ notification.title }}</div>
            <div class="notification-time">{{ notification.time }}</div>
          </div>
          <div v-if="!notification.read" class="unread-dot"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Bell, AlertTriangle, Info, CheckCircle } from 'lucide-vue-next'

const props = defineProps({
  componentData: {
    type: Object,
    default: () => ({})
  },
  dashboardData: {
    type: Object,
    default: () => ({})
  }
})

const readIds = ref(new Set())

const parseDateValue = (value) => {
  if (!value) return null
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return null
  return d
}

const formatTimeAgo = (date) => {
  if (!date) return ''
  const diffMs = Date.now() - date.getTime()
  const diffSec = Math.max(0, Math.floor(diffMs / 1000))
  if (diffSec < 60) return `${diffSec}秒前`
  const diffMin = Math.floor(diffSec / 60)
  if (diffMin < 60) return `${diffMin}分钟前`
  const diffHour = Math.floor(diffMin / 60)
  if (diffHour < 24) return `${diffHour}小时前`
  const diffDay = Math.floor(diffHour / 24)
  return `${diffDay}天前`
}

const apps = computed(() => {
  const list = props.dashboardData?.applications
  return Array.isArray(list) ? list : []
})

const rawNotifications = computed(() => {
  const now = new Date()
  const in7Days = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000)

  const deadlineItems = apps.value
    .map(app => {
      const deadline = parseDateValue(app?.applicationDeadline)
      return { app, deadline }
    })
    .filter(x => x.deadline && x.deadline.getTime() >= now.getTime() && x.deadline.getTime() <= in7Days.getTime())
    .sort((a, b) => a.deadline.getTime() - b.deadline.getTime())
    .slice(0, 6)
    .map(({ app, deadline }) => {
      const diffDays = Math.ceil((deadline.getTime() - now.getTime()) / (24 * 60 * 60 * 1000))
      const type = diffDays <= 1 ? 'urgent' : 'info'
      const icon = diffDays <= 1 ? AlertTriangle : Info
      const studentName = app?.studentName || app?.userName || '学生'
      const schoolName = app?.schoolName || '院校'
      const majorName = app?.majorName || ''
      const id = `deadline-${app?.id || ''}-${deadline.getTime()}`
      return {
        id,
        type,
        icon,
        title: '申请截止提醒',
        message: `${studentName} - ${schoolName}${majorName ? `（${majorName}）` : ''}：${diffDays}天后截止`,
        time: formatTimeAgo(deadline),
        read: readIds.value.has(id)
      }
    })

  const lastUpdatedAt = parseDateValue(props.dashboardData?.lastUpdatedAt)
  const systemItem = lastUpdatedAt
    ? [
      {
        id: `system-${lastUpdatedAt.getTime()}`,
        type: 'success',
        icon: CheckCircle,
        title: '数据已刷新',
        message: '控制台数据已更新',
        time: formatTimeAgo(lastUpdatedAt),
        read: readIds.value.has(`system-${lastUpdatedAt.getTime()}`)
      }
    ]
    : []

  const merged = [...systemItem, ...deadlineItems]
  return merged.length
    ? merged
    : [
      {
        id: 'empty',
        type: 'info',
        icon: Info,
        title: '暂无通知',
        message: '暂时没有需要关注的通知',
        time: '',
        read: true
      }
    ]
})

const notifications = computed(() => {
  return rawNotifications.value.map(n => ({ ...n, read: readIds.value.has(n.id) || n.read }))
})

const unreadCount = computed(() => {
  return notifications.value.filter(n => !n.read).length
})

const markAllAsRead = () => {
  notifications.value.forEach(n => readIds.value.add(n.id))
  persistReadIds()
}

const persistReadIds = () => {
  try {
    localStorage.setItem('dashboardNotificationReadIds', JSON.stringify(Array.from(readIds.value)))
  } catch (e) {
  }
}

const loadReadIds = () => {
  try {
    const raw = localStorage.getItem('dashboardNotificationReadIds')
    if (!raw) return
    const parsed = JSON.parse(raw)
    if (Array.isArray(parsed)) {
      readIds.value = new Set(parsed)
    }
  } catch (e) {
    readIds.value = new Set()
  }
}

onMounted(() => {
  loadReadIds()
})
</script>

<style scoped>
.notification-center-widget {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.widget-header {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  width: 20px;
  height: 20px;
  color: #1890ff;
}

.widget-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
}

.widget-header p {
  font-size: 12px;
  color: #666;
  margin: 0;
}

.text-btn {
  background: none;
  border: none;
  color: #1890ff;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.text-btn:hover {
  background: rgba(24, 144, 255, 0.1);
}

.widget-content {
  padding: 20px;
  flex: 1;
}

.notification-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notification-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
  position: relative;
  cursor: pointer;
}

.notification-item:hover {
  background: #fafafa;
}

.notification-item.unread {
  background: rgba(24, 144, 255, 0.05);
}

.notification-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.notification-icon.urgent { background: #f5222d; }
.notification-icon.info { background: #1890ff; }
.notification-icon.success { background: #52c41a; }

.notification-icon svg {
  width: 16px;
  height: 16px;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notification-time {
  font-size: 12px;
  color: #999;
}

.unread-dot {
  width: 6px;
  height: 6px;
  background: #1890ff;
  border-radius: 50%;
  position: absolute;
  top: 12px;
  right: 12px;
}
</style> 
