<template>
  <div class="application-card">
    <div class="application-card-header" @click="toggleExpand">
      <div class="application-card-title">
        <h4>
          {{ application.institution }} - {{ application.program }}
          <span class="status-badge" :class="getStatusClass(application.status)">{{ application.status }}</span>
          <span class="country-tag">{{ application.country }}</span>
        </h4>
        <div class="application-meta">
          <!-- 完成度和截止日期并排 -->
          <div class="application-meta-row first-row">
            <div class="completion-progress">
              <span>完成度: {{ application.materialsProgress }}%</span>
              <div class="progress-bar-wrapper">
                <div class="progress-bar-bg"></div>
                <div class="progress-bar-fill"
                  :style="{ width: `${application.materialsProgress}%`, backgroundColor: getMaterialsProgressColor(application.materialsProgress) }">
                </div>
              </div>
            </div>
            <span class="deadline-info" :class="{ 'urgent': isDeadlineUrgent(application.deadline) }">
              截止日期: {{ formatDate(application.deadline) }}
              <span v-if="isDeadlineUrgent(application.deadline)" class="deadline-days">{{
                getRemainingDays(application.deadline) }}天</span>
            </span>
          </div>
        </div>
      </div>
      <div class="application-card-actions">
        <ChevronDown class="expand-icon" :class="{ 'expanded': expanded }" />
      </div>
    </div>

    <div v-if="expanded" class="application-card-body">
      <!-- 申请进度步骤指示器改为标签式布局 -->
      <div class="application-tabs-section">
        <div class="application-tabs">
          <div class="tab-item" :class="{ 'active': activeTab === 'info' || !activeTab }" @click="selectTab('info')">
            <div class="tab-label">个人信息</div>
          </div>
          <div class="tab-item" :class="{ 'active': activeTab === 'materials' }" @click="selectTab('materials')">
            <div class="tab-label">申请材料</div>
          </div>
          <div class="tab-item" :class="{ 'active': activeTab === 'progress' }" @click="selectTab('progress')">
            <div class="tab-label">申请监控</div>
          </div>
          <div class="tab-item" :class="{ 'active': activeTab === 'result' }" @click="selectTab('result')">
            <div class="tab-label">录取结果</div>
          </div>
        </div>

        <!-- 标签内容区域 -->
        <div class="tab-content">
          <!-- 个人信息 -->
          <div v-if="activeTab === 'info' || !activeTab" class="tab-pane">
            <slot name="basic-info"></slot>
          </div>

          <!-- 上传材料 -->
          <div v-if="activeTab === 'materials'" class="tab-pane">
            <slot name="materials"></slot>
          </div>

          <!-- 申请监控 -->
          <div v-if="activeTab === 'progress'" class="tab-pane">
            <slot name="monitor"></slot>
          </div>

          <!-- 录取结果 -->
          <div v-if="activeTab === 'result'" class="tab-pane">
            <slot name="result"></slot>
          </div>
        </div>
      </div>

      <div class="notes-section" v-if="application.notes">
        <h5>备注信息</h5>
        <div class="notes-content">{{ application.notes }}</div>
      </div>

      <div class="application-card-actions">
        <button class="btn-outline-sm" @click="viewDetail">
          <Eye class="btn-icon" />
          查看详情
        </button>
        <button class="btn-primary-sm" @click="editApplication">
          <Edit class="btn-icon" />
          编辑申请
        </button>
        <button class="btn-danger-sm" @click="deleteApplication">
          <Trash2 class="btn-icon" />
          删除申请
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ChevronDown, Eye, Edit, Trash2 } from 'lucide-vue-next'

const props = defineProps({
  application: {
    type: Object,
    required: true
  },
  initialExpanded: {
    type: Boolean,
    default: false
  },
  formatDateFn: {
    type: Function,
    required: true
  },
  isDeadlineUrgentFn: {
    type: Function,
    required: true
  },
  getRemainingDaysFn: {
    type: Function,
    required: true
  },
  getStatusClassFn: {
    type: Function,
    required: true
  },
  getMaterialsProgressColorFn: {
    type: Function,
    required: true
  }
})

const emit = defineEmits(['toggle-expand', 'view-detail', 'edit', 'delete', 'select-tab'])

const expanded = ref(props.initialExpanded)
const activeTab = ref(props.application?.activeTab || 'info')

const toggleExpand = () => {
  expanded.value = !expanded.value
  emit('toggle-expand', props.application.id, expanded.value)
}

const selectTab = (tab) => {
  activeTab.value = tab
  emit('select-tab', props.application, tab)
}

const viewDetail = () => {
  emit('view-detail', props.application)
}

const editApplication = () => {
  emit('edit', props.application)
}

const deleteApplication = () => {
  emit('delete', props.application)
}

const getStatusClass = (status) => {
  if (props.getStatusClassFn) return props.getStatusClassFn(status)
  const statusMap = {
    '待提交': 'pending',
    '已提交材料': 'submitted',
    '材料审核中': 'processing',
    '面试阶段': 'interviewing',
    '已录取': 'admitted',
    '被拒绝': 'rejected',
    '已完成': 'completed',
    '签证申请中': 'visa-processing',
    '签证已批准': 'visa-approved',
    '签证被拒': 'visa-rejected',
    '已入学': 'enrolled'
  }
  return statusMap[status] || 'default'
}

const formatDate = (date) => {
  return props.formatDateFn ? props.formatDateFn(date) : ''
}

const getMaterialsProgressColor = (progress) => {
  return props.getMaterialsProgressColorFn ? props.getMaterialsProgressColorFn(progress) : ''
}

const isDeadlineUrgent = (deadline) => {
  return props.isDeadlineUrgentFn ? props.isDeadlineUrgentFn(deadline) : false
}

const getRemainingDays = (deadline) => {
  return props.getRemainingDaysFn ? props.getRemainingDaysFn(deadline) : 0
}

watch(() => props.initialExpanded, (newValue) => {
  expanded.value = newValue
})

watch(() => props.application?.activeTab, (newValue) => {
  if (newValue) activeTab.value = newValue
})
</script>

<style scoped>
.application-card {
  background-color: #fff;
  border-radius: 12px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(0, 0, 0, 0.02);
  overflow: hidden;
  transition: all 0.3s ease;
}

.application-card.selected {
  border-color: rgba(24, 144, 255, 0.35);
}

.application-card-header {
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  cursor: pointer;
  transition: background-color 0.3s;
}

.application-card-header:hover {
  background-color: #f9f9f9;
}

.application-card-title h4 {
  margin: 0 0 12px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.application-card-title h4 .status-badge,
.application-card-title h4 .country-tag {
  font-size: 12px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 12px;
  display: inline-flex;
  align-items: center;
}

.status-badge {
  background-color: #e6f7ff;
  color: #1890ff;
}

.status-badge.pending {
  background-color: #fff7e6;
  color: #fa8c16;
}

.status-badge.submitted {
  background-color: #e6f7ff;
  color: #1890ff;
}

.status-badge.processing {
  background-color: #f9f0ff;
  color: #722ed1;
}

.status-badge.interviewing {
  background-color: #fcf4e6;
  color: #fa8c16;
}

.status-badge.admitted {
  background-color: #f6ffed;
  color: #52c41a;
}

.status-badge.rejected {
  background-color: #fff1f0;
  color: #f5222d;
}

.status-badge.completed {
  background-color: #f0f5ff;
  color: #2f54eb;
}

.status-badge.visa-processing {
  background-color: #fff0f6;
  color: #eb2f96;
}

.status-badge.visa-approved {
  background-color: #f0f9ff;
  color: #1677ff;
}

.status-badge.visa-rejected {
  background-color: #fff1f0;
  color: #ff4d4f;
}

.status-badge.enrolled {
  background-color: #f6ffed;
  color: #389e0d;
}

.country-tag {
  background-color: #f5f5f5;
  color: #666;
}

.application-meta {
  font-size: 14px;
  color: #666;
}

.application-meta-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.application-meta-row.first-row {
  margin-bottom: 8px;
}

.completion-progress {
  flex: 1;
}

.progress-bar-wrapper {
  position: relative;
  height: 8px;
  border-radius: 4px;
  overflow: hidden;
  margin-top: 6px;
  width: 100%;
}

.progress-bar-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #f0f0f0;
}

.progress-bar-fill {
  position: absolute;
  top: 0;
  left: 0;
  bottom: 0;
  background-color: #52c41a;
  transition: width 0.3s ease;
}

.deadline-info {
  white-space: nowrap;
}

.deadline-info.urgent {
  color: #f5222d;
  font-weight: 500;
}

.deadline-days {
  margin-left: 4px;
  background-color: #f5222d;
  color: white;
  padding: 2px 6px;
  border-radius: 10px;
  font-size: 12px;
}

.application-card-actions {
  display: flex;
  gap: 8px;
}

.expand-icon {
  width: 20px;
  height: 20px;
  color: #999;
  transition: transform 0.3s ease;
}

.expand-icon.expanded {
  transform: rotate(180deg);
}

.application-card-body {
  padding: 0 16px 16px;
  border-top: 1px solid #f0f0f0;
}

.application-tabs-section {
  margin-top: 16px;
}

.application-tabs {
  display: flex;
  margin-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.tab-item {
  padding: 12px 16px;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  position: relative;
  transition: all 0.3s;
}

.tab-item:hover {
  color: #1890ff;
}

.tab-item.active {
  color: #1890ff;
  font-weight: 500;
}

.tab-item.active:after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 2px;
  background-color: #1890ff;
}

.tab-content {
  min-height: 200px;
}

.tab-pane {
  padding: 16px 0;
}

.notes-section {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.notes-section h5 {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px;
}

.notes-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  white-space: pre-line;
}

.application-card-actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.btn-outline-sm,
.btn-primary-sm,
.btn-danger-sm {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 32px;
  padding: 0 12px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  gap: 4px;
}

.btn-outline-sm {
  background-color: transparent;
  color: #666;
  border: 1px solid #d9d9d9;
}

.btn-outline-sm:hover {
  color: #40a9ff;
  border-color: #40a9ff;
}

.btn-primary-sm {
  background-color: #1890ff;
  color: white;
  border: 1px solid #1890ff;
}

.btn-primary-sm:hover {
  background-color: #40a9ff;
  border-color: #40a9ff;
}

.btn-danger-sm {
  background-color: transparent;
  color: #f5222d;
  border: 1px solid #f5222d;
}

.btn-danger-sm:hover {
  background-color: #fff1f0;
}

.btn-icon {
  width: 14px;
  height: 14px;
}

@media (max-width: 768px) {
  .application-meta-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .deadline-info {
    margin-top: 8px;
  }

  .application-tabs {
    overflow-x: auto;
    white-space: nowrap;
    padding-bottom: 2px;
  }

  .application-card-actions {
    flex-wrap: wrap;
  }

  .btn-outline-sm,
  .btn-primary-sm,
  .btn-danger-sm {
    flex: 1 1 auto;
  }
}
</style>
