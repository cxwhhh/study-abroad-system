<template>
  <div class="important-reminders-widget">
    <div class="widget-header">
      <div class="header-info">
        <AlertTriangle class="header-icon" />
        <div>
          <h3>重要提醒</h3>
          <p>截止日期警告</p>
        </div>
      </div>
    </div>
    <div class="widget-content">
      <div class="reminder-list">
        <div 
          v-for="(reminder, index) in importantReminders" 
          :key="index"
          class="reminder-item"
          :class="reminder.urgency"
        >
          <div class="reminder-icon">
            <Clock />
          </div>
          <div class="reminder-content">
            <div class="reminder-title">{{ reminder.title }}</div>
            <div class="reminder-deadline">{{ reminder.deadline }}</div>
          </div>
          <div class="reminder-days" :class="reminder.urgency">{{ reminder.daysLeft }}天</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { AlertTriangle, Clock } from 'lucide-vue-next'

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

const parseDateValue = (value) => {
  if (!value) return null
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return null
  return d
}

const apps = computed(() => {
  const list = props.dashboardData?.applications
  return Array.isArray(list) ? list : []
})

const importantReminders = computed(() => {
  const now = new Date()
  const list = apps.value
    .map(app => {
      const deadline = parseDateValue(app?.applicationDeadline)
      if (!deadline) return null
      const daysLeft = Math.ceil((deadline.getTime() - now.getTime()) / (24 * 60 * 60 * 1000))
      return {
        title: `${app?.schoolName || '院校'}申请截止`,
        deadline: deadline.toISOString().slice(0, 10),
        daysLeft,
        urgency: daysLeft <= 3 ? 'urgent' : daysLeft <= 7 ? 'warning' : 'normal'
      }
    })
    .filter(x => x && x.daysLeft >= 0)
    .sort((a, b) => a.daysLeft - b.daysLeft)
    .slice(0, 5)

  return list.length ? list : [{ title: '暂无提醒', deadline: '', daysLeft: 0, urgency: 'normal' }]
})
</script>

<style scoped>
.important-reminders-widget {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.widget-header {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
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

.widget-content {
  padding: 20px;
  flex: 1;
}

.reminder-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.reminder-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  border-left: 4px solid #d9d9d9;
}

.reminder-item.urgent {
  background: rgba(245, 34, 45, 0.05);
  border-left-color: #f5222d;
}

.reminder-item.warning {
  background: rgba(250, 140, 22, 0.05);
  border-left-color: #fa8c16;
}

.reminder-item.normal {
  background: rgba(24, 144, 255, 0.05);
  border-left-color: #1890ff;
}

.reminder-icon {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  flex-shrink: 0;
}

.reminder-icon svg {
  width: 12px;
  height: 12px;
}

.reminder-content {
  flex: 1;
}

.reminder-title {
  font-size: 13px;
  font-weight: 600;
  color: #333;
  margin-bottom: 2px;
}

.reminder-deadline {
  font-size: 11px;
  color: #666;
}

.reminder-days {
  font-size: 12px;
  font-weight: 700;
}

.reminder-days.urgent {
  color: #f5222d;
}

.reminder-days.warning {
  color: #fa8c16;
}

.reminder-days.normal {
  color: #1890ff;
}
</style> 
