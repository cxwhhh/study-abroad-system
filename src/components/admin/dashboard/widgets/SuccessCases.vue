<template>
  <div class="success-cases-widget">
    <div class="widget-header">
      <div class="header-info">
        <Award class="header-icon" />
        <div>
          <h3>成功案例</h3>
          <p>最新录取</p>
        </div>
      </div>
    </div>
    <div class="widget-content">
      <div class="success-list">
        <div 
          v-for="(success, index) in successCases" 
          :key="index"
          class="success-item"
        >
          <div class="success-avatar">
            <User />
          </div>
          <div class="success-info">
            <div class="success-name">{{ success.studentName }}</div>
            <div class="success-school">{{ success.school }}</div>
          </div>
          <div class="success-badge">录取</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Award, User } from 'lucide-vue-next'

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

const successCases = computed(() => {
  const successStatus = new Set(['admitted', 'enrolled'])
  const list = apps.value
    .filter(app => successStatus.has(app?.applicationStatus))
    .map(app => {
      const d = parseDateValue(app?.updateTime) || parseDateValue(app?.createTime) || new Date(0)
      return { app, d }
    })
    .sort((a, b) => b.d.getTime() - a.d.getTime())
    .slice(0, 6)
    .map(({ app }) => ({
      studentName: app?.studentName || app?.userName || '学生',
      school: app?.schoolName || '院校'
    }))

  return list.length ? list : [{ studentName: '暂无数据', school: '' }]
})
</script>

<style scoped>
.success-cases-widget {
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

.success-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.success-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.success-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.success-avatar svg {
  width: 16px;
  height: 16px;
}

.success-info {
  flex: 1;
}

.success-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 2px;
}

.success-school {
  font-size: 12px;
  color: #666;
}

.success-badge {
  background: rgba(82, 196, 26, 0.1);
  color: #52c41a;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 10px;
  font-weight: 600;
}
</style> 
