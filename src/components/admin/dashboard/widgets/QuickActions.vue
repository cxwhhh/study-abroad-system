<template>
  <div class="quick-actions-widget">
    <div class="widget-header">
      <div class="header-info">
        <Zap class="header-icon" />
        <div>
          <h3>快速操作</h3>
          <p>常用功能</p>
        </div>
      </div>
    </div>
    <div class="widget-content">
      <div class="actions-grid">
        <button 
          v-for="(action, index) in quickActions" 
          :key="index"
          class="action-btn"
          @click="executeAction(action)"
        >
          <div class="action-icon" :style="{ background: action.gradient }">
            <component :is="action.icon" />
          </div>
          <span class="action-title">{{ action.title }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Zap, Plus, Upload, BarChart3, Settings, Calendar, BookOpen } from 'lucide-vue-next'

defineProps({
  componentData: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['close'])

const quickActions = ref([
  {
    icon: Plus,
    title: '新建申请',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    action: 'create'
  },
  {
    icon: Upload,
    title: '批量导入',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    action: 'import'
  },
  {
    icon: BarChart3,
    title: '数据报告',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    action: 'report'
  },
  {
    icon: Settings,
    title: '系统设置',
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    action: 'settings'
  },
  {
    icon: Calendar,
    title: '日程管理',
    gradient: 'linear-gradient(135deg, #feca57 0%, #ff9ff3 100%)',
    action: 'schedule'
  },
  {
    icon: BookOpen,
    title: '文书管理',
    gradient: 'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
    action: 'documents'
  }
])

const executeAction = (action) => {
  console.log('执行操作:', action.title)
  // 这里可以根据action.action执行不同操作
  switch (action.action) {
    case 'create':
      // 触发新建申请
      break
    case 'import':
      // 触发批量导入
      break
    case 'report':
      // 跳转到报告页面
      break
    case 'settings':
      // 打开设置面板
      break
    case 'schedule':
      // 打开日程管理
      break
    case 'documents':
      // 打开文书管理
      break
  }
}
</script>

<style scoped>
.quick-actions-widget {
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

.actions-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  height: 100%;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 12px;
  border: none;
  background: rgba(0, 0, 0, 0.02);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
  min-height: 80px;
  justify-content: center;
}

.action-btn:hover {
  background: rgba(0, 0, 0, 0.05);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.action-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.action-icon svg {
  width: 20px;
  height: 20px;
}

.action-title {
  font-size: 12px;
  font-weight: 600;
  color: #333;
  line-height: 1.2;
}
</style> 