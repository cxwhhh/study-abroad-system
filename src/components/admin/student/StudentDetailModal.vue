<template>
  <div class="modal-overlay">
    <div class="modal-container">
      <div class="modal-header">
        <div class="modal-title">
          <User class="modal-icon" />
          <h3>学生详情</h3>
        </div>
        <button class="btn-icon-sm" @click="$emit('close')">
          <X />
        </button>
      </div>
      <div class="modal-body">
        <div class="student-detail-header">
          <div class="student-detail-avatar">
            <img v-if="student.avatar" :src="student.avatar" :alt="student.name" />
            <div v-else class="avatar-text" :style="{ backgroundColor: generateAvatarColor(student.name) }">
              {{ student.name?.charAt(0) || '学' }}
            </div>
          </div>
          <div class="student-detail-info">
            <h2>{{ student.name }} <span class="student-detail-id">{{ student.id }}</span></h2>
            <div class="student-detail-meta">
              <div class="meta-item">
                <School class="meta-icon" />
                <span>{{ student.school }}</span>
              </div>
              <div class="meta-item">
                <GraduationCap class="meta-icon" />
                <span>{{ student.grade }}</span>
              </div>
              <div class="meta-item">
                <Mail class="meta-icon" />
                <span>{{ student.contactInfo.email }}</span>
              </div>
              <div class="meta-item">
                <Phone class="meta-icon" />
                <span>{{ student.contactInfo.phone }}</span>
              </div>
            </div>
            <div class="student-detail-tags">
              <span class="status-badge" :class="student.status">{{ student.statusText }}</span>
              <span v-for="(country, idx) in student.targetCountries" :key="idx" class="country-tag">
                {{ country }}
              </span>
            </div>
          </div>
        </div>

        <div class="student-detail-tabs">
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

        <div v-if="activeTab === 'basic'" class="student-detail-section">
          <h4 class="detail-section-title">个人信息</h4>
          <div class="detail-info-grid">
            <div class="detail-info-item">
              <div class="detail-info-label">姓名</div>
              <div class="detail-info-value">{{ student.name }}</div>
            </div>
            <div class="detail-info-item">
              <div class="detail-info-label">性别</div>
              <div class="detail-info-value">{{ student.gender }}</div>
            </div>
            <div class="detail-info-item">
              <div class="detail-info-label">年龄</div>
              <div class="detail-info-value">{{ student.age }}</div>
            </div>
            <div class="detail-info-item">
              <div class="detail-info-label">年级</div>
              <div class="detail-info-value">{{ student.grade }}</div>
            </div>
            <div class="detail-info-item">
              <div class="detail-info-label">学校</div>
              <div class="detail-info-value">{{ student.school }}</div>
            </div>
            <div class="detail-info-item">
              <div class="detail-info-label">GPA</div>
              <div class="detail-info-value">{{ student.gpa }}</div>
            </div>
            <div class="detail-info-item">
              <div class="detail-info-label">加入日期</div>
              <div class="detail-info-value">{{ student.joinDate }}</div>
            </div>
            <div class="detail-info-item">
              <div class="detail-info-label">顾问老师</div>
              <div class="detail-info-value">{{ student.consultant }}</div>
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'contact'" class="student-detail-section">
          <h4 class="detail-section-title">联系方式</h4>
          <div class="detail-info-grid">
            <div class="detail-info-item">
              <div class="detail-info-label">电子邮箱</div>
              <div class="detail-info-value">{{ student.contactInfo.email }}</div>
            </div>
            <div class="detail-info-item">
              <div class="detail-info-label">手机号码</div>
              <div class="detail-info-value">{{ student.contactInfo.phone }}</div>
            </div>
            <div class="detail-info-item">
              <div class="detail-info-label">微信号</div>
              <div class="detail-info-value">{{ student.contactInfo.wechat }}</div>
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'academic'" class="student-detail-section">
          <h4 class="detail-section-title">学术成绩</h4>
          <div class="detail-info-grid">
            <div class="detail-info-item">
              <div class="detail-info-label">GPA</div>
              <div class="detail-info-value">{{ student.gpa }}</div>
            </div>
            <div class="detail-info-item">
              <div class="detail-info-label">托福成绩</div>
              <div class="detail-info-value">{{ student.toefl || '暂无' }}</div>
            </div>
            <div class="detail-info-item">
              <div class="detail-info-label">雅思成绩</div>
              <div class="detail-info-value">{{ student.ielts || '暂无' }}</div>
            </div>
            <div class="detail-info-item">
              <div class="detail-info-label">GRE成绩</div>
              <div class="detail-info-value">{{ student.gre || '暂无' }}</div>
            </div>
          </div>

          <div class="academic-records">
            <h5 class="subsection-title">成绩记录</h5>
            <div class="empty-state" v-if="!academicRecords.length">
              <Folder class="empty-icon" />
              <p>暂无成绩记录</p>
              <button class="btn btn-outline-sm">添加成绩记录</button>
            </div>
            <div v-else class="records-list">
              <!-- 成绩记录列表 -->
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'applications'" class="student-detail-section">
          <h4 class="detail-section-title">申请记录</h4>
          
          <div class="application-stats">
            <div class="stat-item">
              <div class="stat-value">{{ student.applications }}</div>
              <div class="stat-label">申请总数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ applicationStats.accepted }}</div>
              <div class="stat-label">录取数量</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ applicationStats.rejected }}</div>
              <div class="stat-label">拒绝数量</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ applicationStats.pending }}</div>
              <div class="stat-label">等待结果</div>
            </div>
          </div>
          
          <div class="application-records">
            <div class="section-header">
              <h5 class="subsection-title">申请院校</h5>
              <button class="btn btn-primary-sm">添加申请</button>
            </div>
            
            <div class="empty-state" v-if="!applicationRecords.length">
              <Folder class="empty-icon" />
              <p>暂无申请记录</p>
              <button class="btn btn-outline-sm">添加申请记录</button>
            </div>
            <div v-else class="records-list">
              <!-- 申请记录列表 -->
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'documents'" class="student-detail-section">
          <h4 class="detail-section-title">文档资料</h4>
          
          <div class="document-types">
            <div class="document-type" v-for="(docType, index) in documentTypes" :key="index">
              <div class="document-icon" :class="docType.color">
                <component :is="docType.icon" />
              </div>
              <div class="document-info">
                <div class="document-title">{{ docType.title }}</div>
                <div class="document-count">{{ docType.count }} 个文件</div>
              </div>
            </div>
          </div>
          
          <div class="documents-list">
            <h5 class="subsection-title">最近上传</h5>
            <div class="empty-state" v-if="!recentDocuments.length">
              <Folder class="empty-icon" />
              <p>暂无文档记录</p>
              <button class="btn btn-outline-sm">上传文档</button>
            </div>
            <div v-else class="documents-table">
              <!-- 文档列表 -->
            </div>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-outline" @click="$emit('close')">关闭</button>
        <button class="btn btn-primary" @click="$emit('edit', student)">编辑信息</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { User, X, School, GraduationCap, Mail, Phone, FileText, Image, Database, FileCheck, Folder } from 'lucide-vue-next'

const props = defineProps({
  student: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['close', 'edit'])

// 选项卡
const tabs = [
  { label: '基本信息', value: 'basic' },
  { label: '联系方式', value: 'contact' },
  { label: '学术成绩', value: 'academic' },
  { label: '申请记录', value: 'applications' },
  { label: '文档资料', value: 'documents' }
]

const activeTab = ref('basic')

// 申请统计
const applicationStats = computed(() => {
  return {
    accepted: 0, // 模拟数据
    rejected: 0,
    pending: props.student.applications
  }
})

// 申请记录
const applicationRecords = ref([])

// 成绩记录
const academicRecords = ref([])

// 文档类型
const documentTypes = [
  { title: '个人陈述', count: 2, icon: FileText, color: 'blue' },
  { title: '推荐信', count: 3, icon: FileCheck, color: 'green' },
  { title: '成绩单', count: 1, icon: Database, color: 'orange' },
  { title: '作品集', count: 0, icon: Image, color: 'purple' }
]

// 最近文档
const recentDocuments = ref([])

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
  max-width: 900px;
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

.modal-body {
  padding: 24px;
}

.student-detail-header {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.student-detail-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50px;
  overflow: hidden;
  border: 2px solid #f0f0f0;
  flex-shrink: 0;
}

.student-detail-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.student-detail-avatar .avatar-text {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  font-weight: 500;
  color: white;
  text-transform: uppercase;
}

.student-detail-info {
  flex: 1;
}

.student-detail-info h2 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #111;
}

.student-detail-id {
  font-size: 14px;
  color: #999;
  font-weight: normal;
  margin-left: 8px;
}

.student-detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 16px;
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

.student-detail-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

/* 标签样式 */
.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  display: inline-block;
}

.status-badge.active {
  background-color: #f6ffed;
  color: #52c41a;
}

.status-badge.inactive {
  background-color: #f5f7fa;
  color: #999;
}

.status-badge.graduated {
  background-color: #e6f7ff;
  color: #1890ff;
}

.country-tag {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  background-color: #f5f7fa;
  color: #666;
  white-space: nowrap;
}

/* 选项卡样式 */
.student-detail-tabs {
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

/* 信息区域样式 */
.student-detail-section {
  margin-bottom: 24px;
}

.detail-section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #111;
}

.detail-info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 16px;
}

.detail-info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-info-label {
  font-size: 12px;
  color: #999;
}

.detail-info-value {
  font-size: 14px;
  color: #333;
}

/* 申请统计 */
.application-stats {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 24px;
}

.stat-item {
  flex: 1;
  background-color: #f5f7fa;
  border-radius: 12px;
  padding: 16px;
  text-align: center;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

/* 文档类型 */
.document-types {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.document-type {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 12px;
  transition: all 0.3s;
  cursor: pointer;
}

.document-type:hover {
  background-color: #e6f7ff;
}

.document-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.document-icon.blue {
  background-color: #1890ff;
}

.document-icon.green {
  background-color: #52c41a;
}

.document-icon.orange {
  background-color: #fa8c16;
}

.document-icon.purple {
  background-color: #722ed1;
}

.document-title {
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.document-count {
  font-size: 12px;
  color: #999;
}

/* 子区域标题 */
.subsection-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  margin-top: 24px;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px;
  background-color: #f5f7fa;
  border-radius: 12px;
  text-align: center;
}

.empty-icon {
  color: #d9d9d9;
  width: 48px;
  height: 48px;
  margin-bottom: 16px;
}

.empty-state p {
  color: #999;
  margin-bottom: 16px;
}

/* 按钮样式 */
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

.btn-outline-sm {
  background-color: transparent;
  border: 1px solid #d9d9d9;
  color: #666;
  padding: 4px 12px;
  font-size: 12px;
  border-radius: 6px;
}

.btn-outline-sm:hover {
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

.btn-primary-sm {
  background-color: #1890ff;
  color: #fff;
  border: none;
  padding: 4px 12px;
  font-size: 12px;
  border-radius: 6px;
}

.btn-primary-sm:hover {
  background-color: #40a9ff;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
  gap: 12px;
}
</style> 