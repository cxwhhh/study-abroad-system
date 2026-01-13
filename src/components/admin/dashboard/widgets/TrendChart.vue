<template>
  <div class="trend-chart-widget">
    <div class="widget-header">
      <div class="header-info">
        <BarChart3 class="header-icon" />
        <div>
          <h3>申请趋势</h3>
          <p>近{{ chartPeriod }}天数据</p>
        </div>
      </div>
      <div class="header-actions">
        <select v-model="chartPeriod" class="period-select">
          <option value="7">7天</option>
          <option value="30">30天</option>
          <option value="90">90天</option>
        </select>
      </div>
    </div>
    <div class="widget-content">
      <div class="chart-container">
        <div class="trend-preview">
          <div class="trend-bars">
            <div 
              v-for="(bar, index) in trendData" 
              :key="index"
              class="trend-bar"
              :style="{ height: `${bar}%` }"
              :title="`第${index + 1}天: ${Math.round(bar * 2)}个申请`"
            ></div>
          </div>
          <div class="trend-summary">
            <div class="trend-item">
              <span class="trend-label">本月申请量</span>
              <span class="trend-value positive">+{{ trendGrowth }}%</span>
            </div>
            <div class="trend-item">
              <span class="trend-label">日均申请</span>
              <span class="trend-value">{{ avgDaily }}个</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { BarChart3 } from 'lucide-vue-next'

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

const chartPeriod = ref('30')

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

const dailyCounts = computed(() => {
  const period = Math.max(1, parseInt(chartPeriod.value, 10) || 30)
  const endExclusive = addDays(startOfDay(new Date()), 1)
  const start = addDays(endExclusive, -period)
  const counts = new Array(period).fill(0)

  apps.value.forEach(app => {
    const d = parseDateValue(app?.createTime) || parseDateValue(app?.updateTime)
    if (!d) return
    if (d.getTime() < start.getTime() || d.getTime() >= endExclusive.getTime()) return
    const dayIndex = Math.floor((startOfDay(d).getTime() - start.getTime()) / (24 * 60 * 60 * 1000))
    if (dayIndex >= 0 && dayIndex < period) counts[dayIndex] += 1
  })

  return counts
})

const trendData = computed(() => {
  const counts = dailyCounts.value
  const max = Math.max(...counts, 1)
  return counts.map(c => Math.round((c / max) * 100))
})

const avgDaily = computed(() => {
  const counts = dailyCounts.value
  const total = counts.reduce((sum, v) => sum + v, 0)
  return Math.round((total / counts.length) * 10) / 10
})

const trendGrowth = computed(() => {
  const period = Math.max(1, parseInt(chartPeriod.value, 10) || 30)
  const endExclusive = addDays(startOfDay(new Date()), 1)
  const startCurrent = addDays(endExclusive, -period)
  const startPrevious = addDays(startCurrent, -period)

  const sumInRange = (start, endExclusive) => {
    let total = 0
    apps.value.forEach(app => {
      const d = parseDateValue(app?.createTime) || parseDateValue(app?.updateTime)
      if (!d) return
      const t = d.getTime()
      if (t >= start.getTime() && t < endExclusive.getTime()) total += 1
    })
    return total
  }

  const currentTotal = sumInRange(startCurrent, endExclusive)
  const previousTotal = sumInRange(startPrevious, startCurrent)
  if (!previousTotal) return currentTotal ? 100 : 0
  return Math.round(((currentTotal - previousTotal) / previousTotal) * 10 * 100) / 10
})
</script>

<style scoped>
.trend-chart-widget {
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

.period-select {
  appearance: none;
  background: white;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  padding: 4px 8px;
  font-size: 12px;
  color: #666;
  cursor: pointer;
}

.widget-content {
  padding: 20px;
  flex: 1;
}

.chart-container {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.trend-preview {
  text-align: center;
  width: 100%;
}

.trend-bars {
  display: flex;
  gap: 2px;
  align-items: end;
  height: 120px;
  margin-bottom: 20px;
  justify-content: center;
  padding: 0 20px;
}

.trend-bar {
  flex: 1;
  max-width: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
  min-height: 8px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.trend-bar:hover {
  opacity: 0.8;
  transform: scaleY(1.1);
}

.trend-summary {
  display: flex;
  justify-content: space-around;
  gap: 20px;
}

.trend-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.trend-label {
  font-size: 12px;
  color: #666;
}

.trend-value {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.trend-value.positive {
  color: #52c41a;
}
</style> 
