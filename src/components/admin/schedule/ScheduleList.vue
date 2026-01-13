<template>
  <div class="schedule-section" :class="{ 'selected-date-schedules': isSelectedDate }">
    <div class="section-header">
      <h3 class="section-title">
        <Calendar class="section-icon" />
        {{ title }}
      </h3>
      <el-button class="custom-button" type="default" @click="addSchedule">
        <Plus class="button-icon" />
        添加日程
      </el-button>
    </div>

    <div class="schedule-list">
      <div v-for="(schedule, index) in schedules" :key="index" class="schedule-item">
        <div class="schedule-time">{{ schedule.time }}</div>
        <div class="schedule-content">
          <div class="schedule-header">
            <h4 class="schedule-title">{{ schedule.title }}</h4>
            <div class="schedule-tags">
              <span class="schedule-tag" :class="getTypeClass(schedule.type)">{{ schedule.type }}</span>
              <span v-if="schedule.important" class="schedule-tag important">重要</span>
            </div>
          </div>
          <p class="schedule-description">{{ schedule.description }}</p>
          <div class="schedule-meta" v-if="schedule.location || schedule.participants">
            <div v-if="schedule.location" class="meta-item">
              <MapPin class="meta-icon" /> {{ schedule.location }}
            </div>
            <div v-if="schedule.participants" class="meta-item">
              <Users class="meta-icon" /> {{ schedule.participants }}
            </div>
          </div>
          <div class="schedule-actions">
            <button class="btn-icon-sm" @click="viewSchedule(schedule)" title="查看详情">
              <Eye />
            </button>
            <button class="btn-icon-sm" @click="editSchedule(schedule)" title="编辑">
              <Edit />
            </button>
            <button class="btn-icon-sm" @click="deleteSchedule(schedule)" title="删除">
              <Trash2 />
            </button>
          </div>
        </div>
      </div>
      
      <div v-if="schedules.length === 0" class="empty-state">
        <Calendar class="empty-icon" />
        <p>{{ emptyText }}</p>
        <el-button class="custom-button" type="primary" @click="addSchedule">
          添加日程
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import {
  Calendar,
  Plus,
  Eye,
  Edit,
  Trash2,
  MapPin,
  Users
} from 'lucide-vue-next'

const props = defineProps({
  schedules: {
    type: Array,
    required: true
  },
  title: {
    type: String,
    default: '日程'
  },
  emptyText: {
    type: String,
    default: '暂无日程安排'
  },
  date: {
    type: String,
    default: ''
  },
  isSelectedDate: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['view', 'edit', 'delete', 'add'])

// 获取日程类型对应的样式类
function getTypeClass(type) {
  const typeMap = {
    '会议': 'type-meeting',
    '面试': 'type-interview',
    '课程': 'type-course',
    '其他': 'type-other'
  }
  return typeMap[type] || ''
}

// 日程操作方法
const viewSchedule = (schedule) => {
  emit('view', schedule)
}

const editSchedule = (schedule) => {
  emit('edit', schedule)
}

const deleteSchedule = (schedule) => {
  emit('delete', schedule)
}

const addSchedule = () => {
  emit('add', props.date)
}
</script>

<style scoped>
.schedule-section {
  background-color: transparent;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 0;
  box-shadow: none;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.selected-date-schedules {
  margin-top: 0;
  background-color: transparent;
  border-left: none;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0;
}

.section-icon {
  width: 22px;
  height: 22px;
  color: #3b82f6;
}

.button-icon {
  width: 16px;
  height: 16px;
  margin-right: 6px;
}

.schedule-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  overflow-y: auto;
  padding-right: 8px;
  flex: 1;
}

.schedule-list::-webkit-scrollbar {
  width: 6px;
}

.schedule-list::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 10px;
}

.schedule-list::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
}

.schedule-list::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

.schedule-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  background-color: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.03);
  border: 1px solid #e2e8f0;
  transition: transform 0.2s, box-shadow 0.2s;
}

.schedule-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.06);
  border-color: #cbd5e1;
}

.schedule-time {
  font-size: 16px;
  font-weight: 600;
  color: #3b82f6;
  min-width: 60px;
}

.schedule-content {
  flex: 1;
}

.schedule-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.schedule-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.schedule-tags {
  display: flex;
  gap: 8px;
}

.schedule-tag {
  padding: 4px 10px;
  border-radius: 100px;
  font-size: 12px;
  font-weight: 500;
  background-color: #f1f5f9;
  color: #64748b;
}

.schedule-tag.important {
  background-color: #fff0e6;
  color: #fb923c;
}

.schedule-tag.type-meeting {
  background-color: #e0f2fe;
  color: #0284c7;
}

.schedule-tag.type-interview {
  background-color: #dcfce7;
  color: #16a34a;
}

.schedule-tag.type-course {
  background-color: #ffedd5;
  color: #ea580c;
}

.schedule-tag.type-other {
  background-color: #f3e8ff;
  color: #9333ea;
}

.schedule-description {
  font-size: 14px;
  color: #475569;
  margin-bottom: 12px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.schedule-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #64748b;
}

.meta-icon {
  width: 14px;
  height: 14px;
  color: #94a3b8;
}

.schedule-actions {
  display: flex;
  gap: 4px;
  justify-content: flex-end;
}

.btn-icon-sm {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-icon-sm:hover {
  background-color: #f0f7ff;
  color: #3b82f6;
  border-color: #bfdbfe;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  color: #94a3b8;
  background-color: #f8fafc;
  border-radius: 12px;
  border: 1px dashed #cbd5e1;
  height: 100%;
  min-height: 300px;
}

.empty-icon {
  width: 48px;
  height: 48px;
  color: #cbd5e1;
  margin-bottom: 16px;
}

@media (max-width: 768px) {
  .schedule-item {
    flex-direction: column;
    gap: 12px;
    padding: 14px;
  }

  .schedule-header {
    flex-direction: column;
    gap: 10px;
  }
}
</style> 