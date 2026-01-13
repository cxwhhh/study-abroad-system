<template>
  <div class="system-status-widget">
    <div class="widget-header">
      <div class="header-info">
        <Server class="header-icon" />
        <div>
          <h3>系统状态</h3>
          <p>运行监控</p>
        </div>
      </div>
    </div>
    <div class="widget-content">
      <div class="system-metrics">
        <div class="metric-item">
          <div class="metric-name">CPU使用率</div>
          <div class="metric-bar">
            <div class="metric-fill" style="width: 45%; background: #52c41a;"></div>
          </div>
          <div class="metric-value">45%</div>
        </div>
        <div class="metric-item">
          <div class="metric-name">内存使用</div>
          <div class="metric-bar">
            <div class="metric-fill" style="width: 68%; background: #fa8c16;"></div>
          </div>
          <div class="metric-value">68%</div>
        </div>
        <div class="metric-item">
          <div class="metric-name">存储空间</div>
          <div class="metric-bar">
            <div class="metric-fill" style="width: 32%; background: #1890ff;"></div>
          </div>
          <div class="metric-value">32%</div>
        </div>
        <div class="metric-item">
          <div class="metric-name">网络延迟</div>
          <div class="metric-bar">
            <div class="metric-fill" style="width: 15%; background: #52c41a;"></div>
          </div>
          <div class="metric-value">15ms</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Server } from 'lucide-vue-next'

defineProps({
  componentData: {
    type: Object,
    default: () => ({})
  }
})
</script>

<style scoped>
.system-status-widget {
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

.system-metrics {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.metric-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.metric-name {
  font-size: 12px;
  color: #666;
  width: 60px;
  flex-shrink: 0;
}

.metric-bar {
  flex: 1;
  height: 6px;
  background: #f5f5f5;
  border-radius: 3px;
  overflow: hidden;
}

.metric-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.8s ease;
}

.metric-value {
  font-size: 12px;
  font-weight: 600;
  color: #333;
  width: 40px;
  text-align: right;
  flex-shrink: 0;
}
</style> 