<template>
  <div class="modal-overlay">
    <div class="modal-container">
      <div class="modal-header">
        <div class="modal-title">
          <FileText class="modal-icon" />
          <h3>申请详情</h3>
        </div>
        <button class="btn-icon-sm" @click="$emit('close')">
          <X />
        </button>
      </div>
      <div class="modal-body">
        <div class="application-detail-header">
          <div class="application-id-container">
            <div class="application-id">{{ application.id }}</div>
            <div class="application-badge" :class="getStatusClass(application.status)">{{ application.status }}</div>
            <div class="application-type">{{ application.degree }} · {{ application.round }}</div>
          </div>
          <div class="application-meta">
            <div class="meta-item">
              <Calendar class="meta-icon" />
              <span>创建日期: {{ formatDate(application.createTime) }}</span>
            </div>
            <div class="meta-item">
              <Clock class="meta-icon" />
              <span>截止日期: {{ formatDate(application.deadline) }}</span>
              <span v-if="isDeadlineUrgent(application.deadline)" class="deadline-badge">{{ getRemainingDays(application.deadline) }}天</span>
            </div>
            <div class="meta-item">
              <User class="meta-icon" />
              <span>负责老师: {{ application.consultant }}</span>
            </div>
          </div>
        </div>

        <div class="application-detail-tabs">
          <button 
            v-for="(tab, index) in tabs" 
            :key="index" 
            class="detail-tab" 
            :class="{ active: activeTab === tab.value }"
            @click="activeTab = tab.value"
          >
            {{ tab.label }}
          </button>
        </div>

        <div v-if="activeTab === 'basic'" class="detail-section">
          <div class="section-row">
            <div class="section-col">
              <h4 class="section-title">申请信息</h4>
              <div class="info-grid">
                <div class="info-item">
                  <label>申请阶段</label>
                  <div>{{ application.status }}</div>
                </div>
                <div class="info-item">
                  <label>申请学位</label>
                  <div>{{ application.degree }}</div>
                </div>
                <div class="info-item">
                  <label>申请批次</label>
                  <div>{{ application.round }}</div>
                </div>
                <div class="info-item">
                  <label>截止日期</label>
                  <div :class="{ 'text-danger': isDeadlineUrgent(application.deadline) }">
                    {{ formatDate(application.deadline) }}
                    <span v-if="isDeadlineUrgent(application.deadline)" class="deadline-badge">{{ getRemainingDays(application.deadline) }}天</span>
                  </div>
                </div>
                <div class="info-item">
                  <label>申请项目</label>
                  <div>{{ application.program || application.majorName }}</div>
                </div>
                <div class="info-item">
                  <label>申请院校</label>
                  <div>{{ application.institution || application.schoolName }}</div>
                </div>
                <div class="info-item">
                  <label>国家/地区</label>
                  <div>{{ application.country }}</div>
                </div>
                <div class="info-item">
                  <label>负责老师</label>
                  <div>{{ application.consultant || application.consultantName }}</div>
                </div>
                <div class="info-item full-width">
                  <label>申请备注</label>
                  <div>{{ application.notes || '暂无备注' }}</div>
                </div>
              </div>
            </div>
            <div class="section-col">
              <h4 class="section-title">学生信息</h4>
              <div class="student-card">
                <div class="student-avatar-wrapper">
                  <div class="student-avatar">
                    <img v-if="application.student.avatar" :src="application.student.avatar" :alt="application.student.name" />
                    <div v-else class="avatar-text" :style="{ backgroundColor: generateAvatarColor(application.student.name) }">
                      {{ application.student.name?.charAt(0) || '学' }}
                    </div>
                  </div>
                </div>
                <div class="student-info-wrapper">
                  <div class="student-name">{{ application.student.name }}</div>
                  <div class="student-meta">
                    <div class="meta-row">
                      <Mail class="meta-icon-sm" />
                      <span>{{ application.student.email || 'email@example.com' }}</span>
                    </div>
                    <div class="meta-row">
                      <Phone class="meta-icon-sm" />
                      <span>{{ application.student.phone || '13800138000' }}</span>
                    </div>
                    <div class="meta-row">
                      <GraduationCap class="meta-icon-sm" />
                      <span>{{ application.student.currentSchool || '北京大学' }}</span>
                    </div>
                    <div class="meta-row" v-if="application.student.wechat">
                      <MessageCircle class="meta-icon-sm" />
                      <span>{{ application.student.wechat }}</span>
                    </div>
                    <div class="meta-row" v-if="application.student.major">
                      <BookOpen class="meta-icon-sm" />
                      <span>{{ application.student.major }}</span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="view-student-btn" @click="viewStudentDetail">
                查看学生详情
              </div>
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'materials'" class="detail-section">
          <h4 class="section-title">申请材料 
            <span class="materials-progress">
              完成度: {{ application.materialsProgress }}%
            </span>
          </h4>
          
          <div class="materials-list">
            <div v-for="(material, index) in applicationMaterials" :key="index" class="material-item">
              <div class="material-status" :class="{ 'completed': material.status === 'completed', 'pending': material.status === 'pending', 'processing': material.status === 'processing' }">
                <Check v-if="material.status === 'completed'" class="status-icon" />
                <Clock v-else-if="material.status === 'processing'" class="status-icon" />
                <AlertCircle v-else class="status-icon" />
              </div>
              <div class="material-content">
                <div class="material-header">
                  <h5 class="material-title">{{ material.title }}</h5>
                  <div class="material-badge" :class="getMaterialStatusClass(material.status)">{{ getMaterialStatusText(material.status) }}</div>
                </div>
                <p class="material-description">{{ material.description }}</p>
                <div class="material-meta">
                  <span class="meta-item">
                    <Calendar class="meta-icon-sm" />
                    {{ formatDate(material.updateTime) }}
                  </span>
                  <span class="meta-item" v-if="material.deadline">
                    <Clock class="meta-icon-sm" />
                    截止日期: {{ formatDate(material.deadline) }}
                  </span>
                </div>
              </div>
              <div class="material-actions">
                <button class="btn-action view" v-if="material.status !== 'pending'">
                  <Eye class="action-icon" />
                  查看
                </button>
                <button class="btn-action edit">
                  <Edit class="action-icon" />
                  编辑
                </button>
              </div>
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'timeline'" class="detail-section">
          <h4 class="section-title">申请时间线</h4>
          <div class="timeline">
            <div v-for="(event, index) in applicationTimeline" :key="index" class="timeline-item">
              <div class="timeline-point" :class="{ 'completed': event.status === 'completed', 'current': event.status === 'current', 'pending': event.status === 'pending' }"></div>
              <div class="timeline-content">
                <div class="timeline-header">
                  <h5 class="timeline-title">{{ event.title }}</h5>
                  <div class="timeline-badge" :class="getTimelineStatusClass(event.status)">{{ getTimelineStatusText(event.status) }}</div>
                </div>
                <p class="timeline-description">{{ event.description }}</p>
                <div class="timeline-meta">
                  <span class="meta-item">
                    <Calendar class="meta-icon-sm" />
                    {{ formatDate(event.date) }}
                  </span>
                  <span class="meta-item" v-if="event.user">
                    <User class="meta-icon-sm" />
                    {{ event.user }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'communication'" class="detail-section">
          <h4 class="section-title">沟通记录</h4>
          
          <div class="communication-list">
            <div v-if="applicationCommunications.length === 0" class="empty-state">
              <MessageSquare class="empty-icon" />
              <p>暂无沟通记录</p>
              <button class="btn-add-record">添加沟通记录</button>
            </div>
            <div v-else v-for="(comm, index) in applicationCommunications" :key="index" class="communication-item">
              <div class="communication-avatar">
                <img v-if="comm.avatar" :src="comm.avatar" :alt="comm.user" />
                <div v-else class="avatar-text" :style="{ backgroundColor: generateAvatarColor(comm.user) }">
                  {{ comm.user?.charAt(0) || '系' }}
                </div>
              </div>
              <div class="communication-content">
                <div class="communication-header">
                  <div class="communication-user">{{ comm.user }}</div>
                  <div class="communication-time">{{ formatDate(comm.time) }}</div>
                </div>
                <div class="communication-body">{{ comm.content }}</div>
                <div class="communication-type" :class="comm.type">{{ comm.typeText }}</div>
              </div>
            </div>
          </div>
          
          <div class="add-communication">
            <textarea placeholder="添加新的沟通记录..." class="communication-textarea"></textarea>
            <div class="communication-actions">
              <button class="btn-primary">添加记录</button>
            </div>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-outline" @click="$emit('close')">关闭</button>
        <button class="btn btn-primary" @click="$emit('edit', application)">编辑申请</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { FileText, X, Calendar, Clock, User, Check, AlertCircle, Eye, Edit, Mail, Phone, GraduationCap, MessageSquare, MessageCircle, BookOpen } from 'lucide-vue-next'

const props = defineProps({
  application: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['close', 'edit'])

// 选项卡
const tabs = [
  { label: '基本信息', value: 'basic' },
  { label: '申请材料', value: 'materials' },
  { label: '申请时间线', value: 'timeline' },
  { label: '沟通记录', value: 'communication' }
]

const activeTab = ref('basic')

// 学生详细信息
const studentDetail = ref({
  email: 'student@example.com',
  phone: '13800138000',
  school: '北京大学'
})

// 申请材料
const applicationMaterials = ref([
  {
    title: '个人陈述',
    description: '关于学生的学术背景、职业目标、研究兴趣的个人陈述',
    status: 'completed',
    updateTime: new Date(Date.now() - 10 * 24 * 60 * 60 * 1000),
    deadline: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000)
  },
  {
    title: '推荐信',
    description: '两封来自学术教授的推荐信',
    status: 'processing',
    updateTime: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000),
    deadline: new Date(Date.now() + 15 * 24 * 60 * 60 * 1000)
  },
  {
    title: '成绩单',
    description: '官方成绩单，包含所有本科和研究生阶段的课程成绩',
    status: 'completed',
    updateTime: new Date(Date.now() - 15 * 24 * 60 * 60 * 1000),
    deadline: null
  },
  {
    title: '语言成绩',
    description: '托福或雅思成绩单',
    status: 'completed',
    updateTime: new Date(Date.now() - 20 * 24 * 60 * 60 * 1000),
    deadline: null
  },
  {
    title: '简历',
    description: '详细的个人简历，包含学术和专业经历',
    status: 'pending',
    updateTime: null,
    deadline: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000)
  }
])

// 申请时间线
const applicationTimeline = ref([
  {
    title: '创建申请',
    description: '申请已创建，开始准备材料',
    status: 'completed',
    date: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000),
    user: '王老师'
  },
  {
    title: '提交申请',
    description: '所有申请材料已准备完毕并提交',
    status: 'completed',
    date: new Date(Date.now() - 15 * 24 * 60 * 60 * 1000),
    user: '王老师'
  },
  {
    title: '材料审核',
    description: '院校正在审核申请材料',
    status: 'current',
    date: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000),
    user: null
  },
  {
    title: '面试',
    description: '院校面试阶段',
    status: 'pending',
    date: null,
    user: null
  },
  {
    title: '录取决定',
    description: '院校做出录取决定',
    status: 'pending',
    date: null,
    user: null
  }
])

// 沟通记录
const applicationCommunications = ref([
  {
    user: '王老师',
    avatar: null,
    time: new Date(Date.now() - 25 * 24 * 60 * 60 * 1000),
    content: '已与学生确认申请意向，开始准备申请材料。',
    type: 'note',
    typeText: '备注'
  },
  {
    user: '系统',
    avatar: null,
    time: new Date(Date.now() - 20 * 24 * 60 * 60 * 1000),
    content: '申请状态已更新为：材料准备中',
    type: 'system',
    typeText: '系统'
  },
  {
    user: '王老师',
    avatar: null,
    time: new Date(Date.now() - 15 * 24 * 60 * 60 * 1000),
    content: '与学生进行了视频会议，讨论了个人陈述的修改意见。学生同意在本周内完成修改。',
    type: 'meeting',
    typeText: '会议'
  },
  {
    user: '张明',
    avatar: null,
    time: new Date(Date.now() - 10 * 24 * 60 * 60 * 1000),
    content: '已上传修改后的个人陈述和成绩单。',
    type: 'message',
    typeText: '消息'
  }
])

// 根据状态获取类名
const getStatusClass = (status) => {
  const statusMap = {
    '待提交': 'pending',
    '已提交': 'submitted',
    '材料审核中': 'processing',
    '面试中': 'interviewing',
    '录取': 'admitted',
    '拒绝': 'rejected',
    '已完成': 'completed',
    '签证申请中': 'visa-processing',
    '签证已批准': 'visa-approved',
    '签证被拒': 'visa-rejected',
    '已入学': 'enrolled'
  }
  
  return statusMap[status] || 'default'
}

// 获取材料状态类名
const getMaterialStatusClass = (status) => {
  const statusMap = {
    'completed': 'success',
    'processing': 'warning',
    'pending': 'danger'
  }
  
  return statusMap[status] || 'default'
}

// 获取材料状态文本
const getMaterialStatusText = (status) => {
  const statusMap = {
    'completed': '已完成',
    'processing': '处理中',
    'pending': '待处理'
  }
  
  return statusMap[status] || '未知'
}

// 获取时间线状态类名
const getTimelineStatusClass = (status) => {
  const statusMap = {
    'completed': 'success',
    'current': 'warning',
    'pending': 'default'
  }
  
  return statusMap[status] || 'default'
}

// 获取时间线状态文本
const getTimelineStatusText = (status) => {
  const statusMap = {
    'completed': '已完成',
    'current': '进行中',
    'pending': '待处理'
  }
  
  return statusMap[status] || '未知'
}

// 判断截止日期是否紧急
const isDeadlineUrgent = (deadline) => {
  if (!deadline) return false
  
  const now = new Date()
  const deadlineDate = new Date(deadline)
  const diffDays = Math.ceil((deadlineDate - now) / (1000 * 60 * 60 * 24))
  return diffDays > 0 && diffDays <= 14
}

// 获取剩余天数
const getRemainingDays = (deadline) => {
  if (!deadline) return 0
  
  const now = new Date()
  const deadlineDate = new Date(deadline)
  return Math.max(0, Math.ceil((deadlineDate - now) / (1000 * 60 * 60 * 24)))
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '未定'
  
  try {
    const date = new Date(dateStr)
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  } catch (e) {
    return '未定'
  }
}

// 生成头像颜色
const generateAvatarColor = (name) => {
  if (!name) return '#1890ff'
  
  // 简单哈希算法
  let hash = 0
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash)
  }
  
  // 转为HSL色调（120°范围内的蓝绿色调）
  const h = Math.abs(hash % 120) + 180
  return `hsl(${h}, 70%, 50%)`
}

// 查看学生详情
const viewStudentDetail = () => {
  // 实际项目中应该跳转到学生详情页或打开学生详情弹窗
  console.log('查看学生详情:', props.application.student.name)
}

onMounted(() => {
  // 加载学生详细信息
  // 实际项目中应从API获取
  studentDetail.value = {
    email: 'student@example.com',
    phone: '13800138000',
    school: '北京大学'
  }
})
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-container {
  background-color: #fff;
  border-radius: 12px;
  width: 90%;
  max-width: 1000px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.1);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  position: sticky;
  top: 0;
  background-color: #fff;
  z-index: 2;
}

.modal-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.modal-icon {
  color: #1890ff;
  width: 24px;
  height: 24px;
}

.modal-title h3 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.btn-icon-sm {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: transparent;
  border: none;
  color: #999;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-icon-sm:hover {
  background-color: #f5f7fa;
  color: #666;
}

.modal-body {
  padding: 24px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
  gap: 12px;
  position: sticky;
  bottom: 0;
  background-color: #fff;
  z-index: 2;
}

.btn {
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-outline {
  background-color: transparent;
  border: 1px solid #d9d9d9;
  color: #666;
}

.btn-outline:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.btn-primary {
  background-color: #1890ff;
  color: #fff;
  border: none;
}

.btn-primary:hover {
  background-color: #40a9ff;
}

/* 申请头部 */
.application-detail-header {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.application-id-container {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}

.application-id {
  font-size: 20px;
  font-weight: 600;
  color: #1890ff;
}

.application-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  display: inline-block;
}

.application-badge.pending {
  background-color: #fff7e6;
  color: #fa8c16;
}

.application-badge.submitted {
  background-color: #e6f7ff;
  color: #1890ff;
}

.application-badge.processing {
  background-color: #f0f5ff;
  color: #2f54eb;
}

.application-badge.interviewing {
  background-color: #f9f0ff;
  color: #722ed1;
}

.application-badge.admitted {
  background-color: #f6ffed;
  color: #52c41a;
}

.application-badge.rejected {
  background-color: #fff1f0;
  color: #f5222d;
}

.application-badge.completed {
  background-color: #f5f5f5;
  color: #595959;
}

.application-badge.visa-processing {
  background-color: #fff0f6;
  color: #eb2f96;
}

.application-badge.visa-approved {
  background-color: #f0f9ff;
  color: #1677ff;
}

.application-badge.visa-rejected {
  background-color: #fff1f0;
  color: #ff4d4f;
}

.application-badge.enrolled {
  background-color: #f6ffed;
  color: #389e0d;
}

.application-type {
  font-size: 14px;
  color: #666;
}

.application-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
}

.meta-icon {
  width: 16px;
  height: 16px;
  color: #999;
}

.meta-icon-sm {
  width: 14px;
  height: 14px;
  color: #999;
}

.deadline-badge {
  background-color: #fff1f0;
  color: #f5222d;
  padding: 1px 6px;
  border-radius: 10px;
  font-size: 12px;
  margin-left: 4px;
}

/* 选项卡 */
.application-detail-tabs {
  display: flex;
  gap: 4px;
  margin-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 16px;
  overflow-x: auto;
}

.detail-tab {
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  background-color: transparent;
  border: none;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.detail-tab:hover {
  background-color: #f5f7fa;
}

.detail-tab.active {
  background-color: #e6f7ff;
  color: #1890ff;
  font-weight: 500;
}

/* 详情部分 */
.detail-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #111;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.materials-progress {
  font-size: 14px;
  font-weight: normal;
  color: #666;
}

.section-row {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
}

.section-col {
  flex: 1;
  min-width: 300px;
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-item label {
  font-size: 12px;
  color: #999;
}

.text-danger {
  color: #f5222d;
}

/* 学生卡片 */
.student-card {
  display: flex;
  gap: 16px;
  padding: 16px;
  background-color: #f9f9f9;
  border-radius: 8px;
  margin-bottom: 16px;
}

.student-avatar-wrapper {
  flex-shrink: 0;
}

.student-avatar {
  width: 64px;
  height: 64px;
  border-radius: 32px;
  overflow: hidden;
  border: 2px solid #f0f0f0;
}

.student-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.student-avatar .avatar-text {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 500;
  color: white;
  text-transform: uppercase;
}

.student-info-wrapper {
  flex: 1;
}

.student-name {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
}

.student-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.meta-row {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
  font-size: 14px;
}

.view-student-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 8px 16px;
  border-radius: 8px;
  background-color: #f0f5ff;
  color: #1890ff;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.view-student-btn:hover {
  background-color: #e6f7ff;
}

/* 材料列表 */
.materials-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.material-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.material-status {
  width: 40px;
  height: 40px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.material-status.completed {
  background-color: #f6ffed;
  color: #52c41a;
}

.material-status.processing {
  background-color: #fff7e6;
  color: #fa8c16;
}

.material-status.pending {
  background-color: #fff1f0;
  color: #f5222d;
}

.status-icon {
  width: 20px;
  height: 20px;
}

.material-content {
  flex: 1;
}

.material-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.material-title {
  font-size: 16px;
  font-weight: 500;
  margin: 0;
}

.material-badge {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.material-badge.success {
  background-color: #f6ffed;
  color: #52c41a;
}

.material-badge.warning {
  background-color: #fff7e6;
  color: #fa8c16;
}

.material-badge.danger {
  background-color: #fff1f0;
  color: #f5222d;
}

.material-description {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.material-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.material-meta .meta-item {
  font-size: 12px;
  color: #999;
}

.material-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.btn-action {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 12px;
  border: none;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-action.view {
  background-color: #f0f5ff;
  color: #1890ff;
}

.btn-action.view:hover {
  background-color: #e6f7ff;
}

.btn-action.edit {
  background-color: #f9f9f9;
  color: #666;
}

.btn-action.edit:hover {
  background-color: #f0f0f0;
}

.action-icon {
  width: 12px;
  height: 12px;
}

/* 时间线 */
.timeline {
  position: relative;
  padding-left: 20px;
}

.timeline::before {
  content: '';
  position: absolute;
  top: 0;
  bottom: 0;
  left: 10px;
  width: 2px;
  background-color: #f0f0f0;
}

.timeline-item {
  position: relative;
  padding-bottom: 24px;
}

.timeline-point {
  position: absolute;
  left: -20px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
}

.timeline-point::before {
  content: '';
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: white;
  z-index: 2;
}

.timeline-point.completed {
  background-color: #52c41a;
}

.timeline-point.current {
  background-color: #fa8c16;
}

.timeline-point.pending {
  background-color: #d9d9d9;
}

.timeline-content {
  margin-left: 12px;
  padding: 16px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.timeline-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.timeline-title {
  font-size: 16px;
  font-weight: 500;
  margin: 0;
}

.timeline-badge {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.timeline-badge.success {
  background-color: #f6ffed;
  color: #52c41a;
}

.timeline-badge.warning {
  background-color: #fff7e6;
  color: #fa8c16;
}

.timeline-badge.default {
  background-color: #f5f5f5;
  color: #666;
}

.timeline-description {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.timeline-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.timeline-meta .meta-item {
  font-size: 12px;
  color: #999;
}

/* 沟通记录 */
.communication-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px;
  background-color: #f9f9f9;
  border-radius: 8px;
  text-align: center;
}

.empty-icon {
  width: 48px;
  height: 48px;
  color: #d9d9d9;
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 14px;
  color: #999;
  margin-bottom: 16px;
}

.btn-add-record {
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  background-color: #e6f7ff;
  color: #1890ff;
  border: none;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-add-record:hover {
  background-color: #bae7ff;
}

.communication-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.communication-avatar {
  width: 40px;
  height: 40px;
  border-radius: 20px;
  overflow: hidden;
  border: 2px solid #f0f0f0;
  flex-shrink: 0;
}

.communication-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.communication-avatar .avatar-text {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 500;
  color: white;
  text-transform: uppercase;
}

.communication-content {
  flex: 1;
}

.communication-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.communication-user {
  font-weight: 500;
}

.communication-time {
  font-size: 12px;
  color: #999;
}

.communication-body {
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
}

.communication-type {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  background-color: #f5f5f5;
  color: #666;
}

.communication-type.note {
  background-color: #e6f7ff;
  color: #1890ff;
}

.communication-type.system {
  background-color: #f5f5f5;
  color: #999;
}

.communication-type.meeting {
  background-color: #f9f0ff;
  color: #722ed1;
}

.communication-type.message {
  background-color: #f6ffed;
  color: #52c41a;
}

.add-communication {
  margin-top: 16px;
}

.communication-textarea {
  width: 100%;
  min-height: 80px;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #d9d9d9;
  margin-bottom: 16px;
  resize: vertical;
  font-size: 14px;
}

.communication-textarea:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
}

.communication-actions {
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .section-row {
    flex-direction: column;
  }
  
  .material-item {
    flex-direction: column;
    padding: 16px;
  }
  
  .material-status {
    margin-bottom: 12px;
  }
  
  .material-actions {
    flex-direction: row;
    justify-content: flex-end;
    margin-top: 12px;
  }
  
  .application-meta {
    flex-direction: column;
    gap: 8px;
  }
}
</style>