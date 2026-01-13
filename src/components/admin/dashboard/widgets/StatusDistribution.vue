<template>
  <div class="status-distribution-widget">
    <div class="widget-header">
      <div class="header-info">
        <PieChart class="header-icon" />
        <div>
          <h3>状态分布</h3>
          <p>{{ totalApplications }} 项申请</p>
        </div>
      </div>
    </div>
    <div class="widget-content">
      <div class="status-chart">
        <div class="pie-chart">
          <div class="chart-center">
            <div class="center-value">{{ totalApplications }}</div>
            <div class="center-label">总申请</div>
          </div>
        </div>
        <div class="status-legend">
          <div 
            v-for="(status, index) in applicationStatus" 
            :key="index"
            class="legend-item"
          >
            <div class="legend-indicator" :style="{ backgroundColor: status.color }"></div>
            <span class="legend-name">{{ status.name }}</span>
            <span class="legend-count">{{ status.count }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { PieChart } from 'lucide-vue-next'

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

const statusColorMap = {
  '已选择学校': '#667eea',
  '已提交材料': '#f093fb',
  '材料审核中': '#4facfe',
  '面试阶段': '#43e97b',
  '已录取': '#52c41a',
  '被拒绝': '#f5222d',
  '签证申请中': '#fa8c16',
  '签证已批准': '#52c41a',
  '签证被拒': '#f5222d',
  '已入学': '#1890ff',
  '未知状态': '#d9d9d9'
}

const apps = computed(() => {
  const list = props.dashboardData?.applications
  return Array.isArray(list) ? list : []
})

const applicationStatus = computed(() => {
  const counts = new Map()
  apps.value.forEach(app => {
    const name = app?.applicationStatusDisplay || app?.applicationStatus || '未知状态'
    counts.set(name, (counts.get(name) || 0) + 1)
  })

  const items = Array.from(counts.entries())
    .map(([name, count]) => ({
      name,
      count,
      color: statusColorMap[name] || '#8c8c8c'
    }))
    .sort((a, b) => b.count - a.count)

  return items.length ? items.slice(0, 6) : [{ name: '暂无数据', count: 0, color: '#d9d9d9' }]
})

const totalApplications = computed(() => {
  return applicationStatus.value.reduce((total, status) => total + (status.count || 0), 0)
})

const conicGradient = computed(() => {
  const total = totalApplications.value
  if (!total) {
    return 'conic-gradient(#d9d9d9 0% 100%)'
  }

  let current = 0
  const segments = applicationStatus.value.map(item => {
    const start = (current / total) * 100
    current += item.count
    const end = (current / total) * 100
    return `${item.color} ${start}% ${end}%`
  })

  return `conic-gradient(${segments.join(', ')})`
})
</script>

<style scoped>
.status-distribution-widget {
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

.status-chart {
  display: flex;
  gap: 20px;
  align-items: center;
  height: 100%;
}

.pie-chart {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: v-bind(conicGradient);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.chart-center {
  width: 70px;
  height: 70px;
  background: white;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.center-value {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  line-height: 1;
}

.center-label {
  font-size: 10px;
  color: #666;
  margin-top: 2px;
}

.status-legend {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.legend-indicator {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}

.legend-name {
  color: #666;
  flex: 1;
}

.legend-count {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}
</style> 
