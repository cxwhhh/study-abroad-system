<template>
  <div class="school-ranking-widget">
    <div class="widget-header">
      <div class="header-info">
        <Star class="header-icon" />
        <div>
          <h3>热门院校</h3>
          <p>申请量排行</p>
        </div>
      </div>
    </div>
    <div class="widget-content">
      <div class="ranking-list">
        <div 
          v-for="(school, index) in topSchools" 
          :key="index"
          class="ranking-item"
        >
          <div class="rank-number">{{ index + 1 }}</div>
          <div class="school-info">
            <div class="school-name">{{ school.name }}</div>
            <div class="school-count">{{ school.applications }} 申请</div>
          </div>
          <div class="trend-indicator" :class="school.trend">
            <component :is="school.trend === 'up' ? TrendingUp : TrendingDown" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Star, TrendingUp, TrendingDown } from 'lucide-vue-next'

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

const apps = computed(() => {
  const list = props.dashboardData?.applications
  return Array.isArray(list) ? list : []
})

const topSchools = computed(() => {
  const now = new Date()
  const currentStart = startOfDay(addDays(now, -7))
  const currentEnd = addDays(startOfDay(now), 1)
  const previousStart = startOfDay(addDays(now, -14))
  const previousEnd = currentStart

  const totals = new Map()
  const current = new Map()
  const previous = new Map()

  apps.value.forEach(app => {
    const name = app?.schoolName || '未知院校'
    totals.set(name, (totals.get(name) || 0) + 1)

    const d = parseDateValue(app?.updateTime) || parseDateValue(app?.createTime)
    if (!d) return
    const t = d.getTime()
    if (t >= currentStart.getTime() && t < currentEnd.getTime()) {
      current.set(name, (current.get(name) || 0) + 1)
    } else if (t >= previousStart.getTime() && t < previousEnd.getTime()) {
      previous.set(name, (previous.get(name) || 0) + 1)
    }
  })

  const list = Array.from(totals.entries())
    .map(([name, applications]) => {
      const c = current.get(name) || 0
      const p = previous.get(name) || 0
      return {
        name,
        applications,
        trend: c >= p ? 'up' : 'down'
      }
    })
    .sort((a, b) => b.applications - a.applications)
    .slice(0, 5)

  return list.length ? list : [{ name: '暂无数据', applications: 0, trend: 'up' }]
})
</script>

<style scoped>
.school-ranking-widget {
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

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.rank-number {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  flex-shrink: 0;
}

.school-info {
  flex: 1;
}

.school-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 2px;
}

.school-count {
  font-size: 12px;
  color: #666;
}

.trend-indicator {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.trend-indicator.up {
  background: rgba(82, 196, 26, 0.1);
  color: #52c41a;
}

.trend-indicator.down {
  background: rgba(245, 34, 45, 0.1);
  color: #f5222d;
}

.trend-indicator svg {
  width: 12px;
  height: 12px;
}
</style> 
