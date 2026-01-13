<template>
  <div class="recent-activity-widget">
    <div class="widget-header">
      <div class="header-info">
        <Activity class="header-icon" />
        <div>
          <h3>最近活动</h3>
          <p>系统动态追踪</p>
        </div>
      </div>
      <div class="activity-tabs">
        <button v-for="tab in activityTabs" :key="tab.key" :class="['tab-btn', { active: activeTab === tab.key }]"
          @click="activeTab = tab.key">
          {{ tab.label }}
        </button>
      </div>
    </div>
    <div class="widget-content">
      <div class="activity-list">
        <div v-for="(activity, index) in filteredActivities.slice(0, 6)" :key="index" class="activity-item">
          <div class="activity-icon" :class="activity.type">
            <component :is="activity.icon" />
          </div>
          <div class="activity-content">
            <div class="activity-title">{{ activity.title }}</div>
            <div class="activity-description">{{ activity.description }}</div>
            <div class="activity-meta">
              <span class="activity-time">{{ activity.time }}</span>
              <span class="activity-priority" :class="activity.priority">{{ activity.priorityText }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Activity, FileText, CheckCircle, Bell, User, Clock } from 'lucide-vue-next'

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

const activeTab = ref('all')

const activityTabs = ref([
  { key: 'all', label: '全部' },
  { key: 'important', label: '重要' },
  { key: 'recent', label: '最新' }
])

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

const getPriorityText = (priority) => {
  if (priority === 'urgent') return '紧急'
  if (priority === 'high') return '高优先级'
  return '普通'
}

const apps = computed(() => {
  const list = props.dashboardData?.applications
  return Array.isArray(list) ? list : []
})

const students = computed(() => {
  const list = props.dashboardData?.students
  return Array.isArray(list) ? list : []
})

const recentActivities = computed(() => {
  const appItems = apps.value
    .map(app => {
      const d = parseDateValue(app?.updateTime) || parseDateValue(app?.createTime)
      return { app, d }
    })
    .filter(x => !!x.d)
    .sort((a, b) => b.d.getTime() - a.d.getTime())
    .slice(0, 8)
    .map(({ app, d }) => {
      const statusText = app?.applicationStatusDisplay || app?.applicationStatus || '状态更新'
      const studentName = app?.studentName || app?.userName || '学生'
      const schoolName = app?.schoolName || '院校'
      const majorName = app?.majorName || ''
      const title = app?.createTime === app?.updateTime ? '申请更新' : '申请状态更新'

      return {
        timestamp: d.getTime(),
        icon: FileText,
        type: 'application',
        title,
        description: `${studentName} - ${schoolName}${majorName ? `（${majorName}）` : ''}：${statusText}`,
        time: formatTimeAgo(d),
        priority: ['被拒绝', '签证被拒'].includes(statusText) ? 'urgent' : 'normal',
        priorityText: getPriorityText(['被拒绝', '签证被拒'].includes(statusText) ? 'urgent' : 'normal')
      }
    })

  const studentItems = students.value
    .map(student => {
      const d = parseDateValue(student?.updateTime) || parseDateValue(student?.createTime)
      return { student, d }
    })
    .filter(x => !!x.d)
    .sort((a, b) => b.d.getTime() - a.d.getTime())
    .slice(0, 4)
    .map(({ student, d }) => {
      const name = student?.name || student?.studentName || student?.realName || '新学生'
      return {
        timestamp: d.getTime(),
        icon: User,
        type: 'student',
        title: '学生信息更新',
        description: `${name} 信息已更新`,
        time: formatTimeAgo(d),
        priority: 'normal',
        priorityText: getPriorityText('normal')
      }
    })

  const merged = [...appItems, ...studentItems]
    .sort((a, b) => (b.timestamp || 0) - (a.timestamp || 0))

  return merged.length
    ? merged
    : [
      {
        timestamp: 0,
        icon: Activity,
        type: 'application',
        title: '暂无动态',
        description: '最近没有新的系统动态',
        time: '',
        priority: 'normal',
        priorityText: getPriorityText('normal')
      }
    ]
})

const filteredActivities = computed(() => {
  if (activeTab.value === 'all') return recentActivities.value
  if (activeTab.value === 'important') return recentActivities.value.filter(activity => activity.priority !== 'normal')
  return recentActivities.value.slice(0, 3) // 最新的3条
})
</script>

<style scoped>
.recent-activity-widget {
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

.activity-tabs {
  display: flex;
  gap: 4px;
}

.tab-btn {
  padding: 6px 12px;
  border: none;
  background: transparent;
  color: #666;
  font-size: 12px;
  font-weight: 500;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.tab-btn.active {
  background: rgba(24, 144, 255, 0.1);
  color: #1890ff;
}

.widget-content {
  padding: 20px;
  flex: 1;
  overflow-y: auto;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.activity-icon.application {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.activity-icon.approval {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.activity-icon.reminder {
  background: linear-gradient(135deg, #feca57 0%, #ff9ff3 100%);
}

.activity-icon.student {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.activity-icon.deadline {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.activity-icon svg {
  width: 18px;
  height: 18px;
}

.activity-content {
  flex: 1;
  min-width: 0;
}

.activity-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.activity-description {
  font-size: 13px;
  color: #666;
  line-height: 1.4;
  margin-bottom: 8px;
}

.activity-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.activity-time {
  font-size: 11px;
  color: #999;
}

.activity-priority {
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 8px;
  font-weight: 500;
}

.activity-priority.high {
  background: rgba(245, 34, 45, 0.1);
  color: #f5222d;
}

.activity-priority.normal {
  background: rgba(24, 144, 255, 0.1);
  color: #1890ff;
}

.activity-priority.urgent {
  background: rgba(250, 140, 22, 0.1);
  color: #fa8c16;
}
</style>
