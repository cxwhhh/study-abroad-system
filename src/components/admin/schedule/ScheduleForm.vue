<template>
  <div v-if="visible" class="modal-overlay">
    <div class="modal-container">
      <!-- 模态框内容 -->
      <div class="modal-header">
        <div class="modal-title">
          <Plus v-if="!schedule.id" class="modal-icon" />
          <Edit v-else class="modal-icon" />
          <h3>{{ schedule.id ? '编辑日程' : '添加日程' }}</h3>
        </div>
        <button class="btn-icon-sm" @click="close">
          <X />
        </button>
      </div>

      <!-- 表单内容 -->
      <div class="modal-body">
        <div class="form-group">
          <label>日程标题 <span class="required">*</span></label>
          <input type="text" v-model="form.title" class="form-input" placeholder="请输入日程标题" />
        </div>

        <div class="form-group">
          <label>日期 <span class="required">*</span></label>
          <input type="date" v-model="form.date" class="form-input" />
        </div>

        <div class="form-group">
          <label>时间 <span class="required">*</span></label>
          <input type="time" v-model="form.time" class="form-input" />
        </div>

        <div class="form-group">
          <label>类型</label>
          <div class="select-wrapper">
            <select v-model="form.type" class="form-select">
              <option value="会议">会议</option>
              <option value="面试">面试</option>
              <option value="课程">课程</option>
              <option value="其他">其他</option>
            </select>
            <ChevronDown class="select-icon" />
          </div>
        </div>

        <div class="form-group">
          <label>描述</label>
          <textarea v-model="form.description" class="form-textarea" placeholder="请输入日程描述"></textarea>
        </div>

        <div class="form-group">
          <div class="checkbox-wrapper">
            <input type="checkbox" id="important" v-model="form.important" />
            <label for="important">标记为重要</label>
          </div>
        </div>

        <div class="form-group">
          <label>位置</label>
          <div class="location-input">
            <MapPin class="input-icon" />
            <input type="text" v-model="form.location" class="form-input has-icon" placeholder="添加地点" />
          </div>
        </div>

        <div class="form-group">
          <label>参与人</label>
          <div class="participants-input">
            <Users class="input-icon" />
            <input type="text" v-model="form.participants" class="form-input has-icon" placeholder="添加参与人，用逗号分隔" />
          </div>
        </div>

        <div class="form-group">
          <label>提醒</label>
          <div class="select-wrapper">
            <select v-model="form.reminder" class="form-select">
              <option value="none">不提醒</option>
              <option value="5min">5分钟前</option>
              <option value="15min">15分钟前</option>
              <option value="30min">30分钟前</option>
              <option value="1hour">1小时前</option>
              <option value="1day">1天前</option>
            </select>
            <ChevronDown class="select-icon" />
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button class="btn btn-outline" @click="close">取消</button>
        <button class="btn btn-primary" @click="save">保存</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import {
  Plus,
  Edit,
  X,
  MapPin,
  Users,
  ChevronDown
} from 'lucide-vue-next'
import { ElMessage } from 'element-plus'

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

const emit = defineEmits(['close', 'save'])

// 表单数据
const form = ref({
  id: '',
  title: '',
  date: new Date().toISOString().split('T')[0],
  time: '',
  type: '会议',
  description: '',
  important: false,
  location: '',
  participants: '',
  reminder: 'none'
})

// 当props.schedule变化时，更新表单数据
watch(() => props.schedule, (newSchedule) => {
  if (newSchedule && Object.keys(newSchedule).length > 0) {
    form.value = { ...newSchedule }
  } else {
    resetForm()
  }
}, { immediate: true, deep: true })

// 保存表单
const save = () => {
  // 表单验证
  if (!form.value.title || !form.value.date || !form.value.time) {
    ElMessage.warning('请填写必填项')
    return
  }

  emit('save', { ...form.value })
}

// 关闭表单
const close = () => {
  resetForm()
  emit('close')
}

// 重置表单
const resetForm = () => {
  form.value = {
    id: '',
    title: '',
    date: new Date().toISOString().split('T')[0],
    time: '',
    type: '会议',
    description: '',
    important: false,
    location: '',
    participants: '',
    reminder: 'none'
  }
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
  width: 580px;
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

.modal-icon {
  width: 22px;
  height: 22px;
  color: #3b82f6;
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
  max-height: calc(85vh - 130px);
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

/* 表单样式 */
.form-group {
  margin-bottom: 22px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #475569;
  margin-bottom: 8px;
}

.required {
  color: #ef4444;
  margin-left: 4px;
}

.form-input, .form-textarea, .form-select {
  width: 100%;
  padding: 12px 14px;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  font-size: 15px;
  transition: all 0.2s;
  background-color: #f8fafc;
  color: #1e293b;
}

.form-input.has-icon {
  padding-left: 42px;
}

.form-input:focus, .form-textarea:focus, .form-select:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  outline: none;
  background-color: #fff;
}

.form-textarea {
  min-height: 100px;
  resize: vertical;
  line-height: 1.5;
}

.location-input, .participants-input {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  width: 18px;
  height: 18px;
  color: #94a3b8;
}

.checkbox-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  background-color: #f8fafc;
  padding: 12px 16px;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  transition: all 0.2s;
}

.checkbox-wrapper:hover {
  background-color: #f0f7ff;
  border-color: #bfdbfe;
}

.checkbox-wrapper input[type="checkbox"] {
  width: 18px;
  height: 18px;
  accent-color: #3b82f6;
}

.select-wrapper {
  position: relative;
}

.select-icon {
  position: absolute;
  right: 14px;
  top: 50%;
  transform: translateY(-50%);
  width: 16px;
  height: 16px;
  color: #94a3b8;
  pointer-events: none;
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
  padding: 10px 20px;
  border-radius: 12px;
  font-size: 15px;
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

.btn-primary {
  background-color: #3b82f6;
  border: 1px solid #3b82f6;
  color: white;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
}

.btn-primary:hover {
  background-color: #2563eb;
  border-color: #2563eb;
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.3);
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
