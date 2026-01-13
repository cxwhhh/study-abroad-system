<template>
  <div v-if="visible" class="modal-overlay">
    <div class="modal-container">
      <!-- 模态框内容 -->
      <div class="modal-header">
        <div class="modal-title">
          <h3>日程详情</h3>
        </div>
        <div class="modal-actions">
          <button class="btn-icon-sm" @click="onEdit">
            <Edit class="action-icon" />
          </button>
          <button class="btn-icon-sm" @click="onDelete">
            <Trash class="action-icon" />
          </button>
          <button class="btn-icon-sm" @click="close">
            <X />
          </button>
        </div>
      </div>

      <!-- 详情内容 -->
      <div class="modal-body">
        <!-- 标题和重要标识 -->
        <div class="detail-title">
          <h2>{{ schedule.title }}</h2>
          <div v-if="schedule.important" class="important-badge">重要</div>
        </div>

        <!-- 日期和时间 -->
        <div class="detail-info">
          <div class="info-item">
            <Calendar class="info-icon" />
            <span>{{ formatDate(schedule.date) }}</span>
          </div>
          <div class="info-item">
            <Clock class="info-icon" />
            <span>{{ schedule.time }}</span>
          </div>
        </div>

        <!-- 类型 -->
        <div class="detail-section">
          <div class="info-item">
            <Tag class="info-icon" />
            <span>{{ schedule.type }}</span>
          </div>
        </div>

        <!-- 描述 -->
        <div v-if="schedule.description" class="detail-section">
          <h4>描述</h4>
          <p class="detail-description">{{ schedule.description }}</p>
        </div>

        <!-- 位置 -->
        <div v-if="schedule.location" class="detail-section">
          <h4>位置</h4>
          <div class="info-item">
            <MapPin class="info-icon" />
            <span>{{ schedule.location }}</span>
          </div>
        </div>

        <!-- 参与人 -->
        <div v-if="schedule.participants" class="detail-section">
          <h4>参与人</h4>
          <div class="info-item">
            <Users class="info-icon" />
            <div class="participants-list">
              <div
                v-for="(participant, index) in participantsList"
                :key="index"
                class="participant-tag"
              >
                {{ participant }}
              </div>
            </div>
          </div>
        </div>

        <!-- 提醒 -->
        <div v-if="schedule.reminder && schedule.reminder !== 'none'" class="detail-section">
          <h4>提醒</h4>
          <div class="info-item">
            <Bell class="info-icon" />
            <span>{{ reminderText }}</span>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button class="btn btn-outline" @click="close">关闭</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import {
  Calendar,
  Clock,
  Edit,
  X,
  MapPin,
  Users,
  Trash,
  Tag,
  Bell
} from 'lucide-vue-next'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  schedule: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['close', 'edit', 'delete'])

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  return `${year}年${month}月${day}日`
}

// 参与人列表
const participantsList = computed(() => {
  if (!props.schedule.participants) return []
  return props.schedule.participants.split(',').map(p => p.trim()).filter(p => p)
})

// 提醒文本
const reminderText = computed(() => {
  const reminderMap = {
    'none': '不提醒',
    '5min': '5分钟前',
    '15min': '15分钟前',
    '30min': '30分钟前',
    '1hour': '1小时前',
    '1day': '1天前'
  }
  return reminderMap[props.schedule.reminder] || props.schedule.reminder
})

// 关闭详情
const close = () => {
  emit('close')
}

// 编辑按钮点击
const onEdit = () => {
  emit('edit', props.schedule)
}

// 删除按钮点击
const onDelete = () => {
  emit('delete', props.schedule)
}
</script>

<style scoped>
/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(15, 23, 42, 0.6);
  backdrop-filter: blur(3px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease-out;
}

.modal-container {
  background: linear-gradient(145deg, #ffffff, #f8fbff);
  border-radius: 20px;
  width: 550px;
  max-width: 90%;
  max-height: 85vh;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: slideIn 0.3s ease-out;
  border: 1px solid rgba(231, 241, 255, 0.8);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #edf2f7;
  background-color: #ffffff;
}

.modal-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.modal-title h3 {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  color: #1e293b;
}

.modal-actions {
  display: flex;
  align-items: center;
  gap: 6px;
}

.action-icon {
  width: 18px;
  height: 18px;
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: #cbd5e1 #f1f5f9;
}

.modal-body::-webkit-scrollbar {
  width: 6px;
}

.modal-body::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 10px;
}

.modal-body::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
}

.modal-body::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

.modal-footer {
  padding: 20px 24px;
  border-top: 1px solid #edf2f7;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  background-color: #ffffff;
}

/* 详情样式 */
.detail-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.detail-title h2 {
  font-size: 22px;
  font-weight: 700;
  margin: 0;
  color: #1e293b;
  line-height: 1.3;
}

.important-badge {
  background-color: #fb7185;
  color: white;
  padding: 4px 10px;
  border-radius: 100px;
  font-size: 12px;
  font-weight: 600;
  box-shadow: 0 3px 10px rgba(251, 113, 133, 0.2);
}

.detail-info {
  display: flex;
  gap: 24px;
  margin-bottom: 28px;
  background-color: #f8fafc;
  padding: 16px;
  border-radius: 12px;
}

.detail-section {
  margin-top: 24px;
  border-top: 1px solid #f1f5f9;
  padding-top: 24px;
}

.detail-section h4 {
  font-size: 15px;
  font-weight: 600;
  color: #64748b;
  margin: 0 0 12px 0;
  letter-spacing: 0.02em;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.info-icon {
  width: 18px;
  height: 18px;
  color: #3b82f6;
  flex-shrink: 0;
  margin-top: 2px;
}

.detail-description {
  margin: 0;
  white-space: pre-line;
  color: #475569;
  line-height: 1.6;
  font-size: 15px;
}

.participants-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.participant-tag {
  background-color: #f0f7ff;
  border-radius: 100px;
  padding: 6px 12px;
  font-size: 13px;
  color: #3b82f6;
  font-weight: 500;
  border: 1px solid #bfdbfe;
}

.btn-icon-sm {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-icon-sm:hover {
  background-color: #f1f5f9;
  color: #3b82f6;
  border-color: #cbd5e1;
}

.btn {
  padding: 10px 18px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-outline {
  background-color: white;
  border: 1px solid #e2e8f0;
  color: #64748b;
}

.btn-outline:hover {
  border-color: #cbd5e1;
  color: #475569;
  background-color: #f8fafc;
}

/* 动画效果 */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideIn {
  from { transform: translateY(20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}
</style>
