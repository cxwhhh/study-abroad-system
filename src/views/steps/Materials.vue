<template>
  <div class="step-page">
    <nav-bar></nav-bar>
    
    <!-- 页面标题 已经弃用-->
    <div class="page-header">
      <h1>材料准备</h1>
      <div class="timing">申请前6-12个月</div>
    </div>

    <!-- 主要内容区域 -->
    <div class="page-content">
      <!-- 直接显示材料容器，移除外层section -->
      <div class="materials-container">
        <!-- 左侧材料清单和格式说明 -->
        <div class="materials-checklist">
          <div class="checklist-title">
            <h3>材料清单</h3>
            <p>请按顺序准备以下材料</p>
          </div>

          <!-- 修改左侧材料清单部分 -->
          <div class="checklist-content">
            <div v-for="(group, groupIndex) in materialGroups" 
                 :key="groupIndex" 
                 class="checklist-group">
              <div class="checklist-group-header">
                <el-icon><component :is="group.icon" /></el-icon>
                <span class="group-name">{{ group.name }}</span>
              </div>
              <div class="checklist-items">
                <div v-for="(item, itemIndex) in group.items" 
                     :key="itemIndex"
                     class="checklist-item">
                  <!-- 移除复选框，只保留文本 -->
                  <span class="item-name">{{ item.name }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧提交区域 -->
        <div class="materials-upload">
          <div class="upload-header">
            <h3>材料提交</h3>
            <p>请按要求提交所需材料</p>
          </div>
          
          <!-- 移动进度条到这里 -->
          <div class="upload-progress">
            <div class="progress-info">
              <span class="progress-text">已完成 {{ completedCount }}/{{ totalCount }}</span>
              <div class="progress-bar-wrapper">
                <div class="progress-bar">
                  <div class="progress-inner" 
                       :style="{ width: `${(completedCount/totalCount) * 100}%` }">
                  </div>
                </div>
                <span class="progress-percentage">
                  {{ Math.round((completedCount/totalCount) * 100) }}%
                </span>
              </div>
            </div>
          </div>

          <div class="materials-submit-list">
            <!-- 遍历所有材料组 -->
            <div v-for="(group, groupIndex) in materialGroups" 
                 :key="groupIndex" 
                 class="submit-group">
              <div class="submit-group-header" @click="toggleSubmitGroup(groupIndex)">
                <div class="group-title">
                  <!-- 移除复选框，只保留文本 -->
                  <span class="group-text">{{ group.name }}</span>
                  <span class="group-count">{{ getGroupProgress(group) }}</span>
                </div>
                <el-icon class="group-arrow" :class="{ 'is-expanded': group.isExpanded }">
                  <ArrowDown />
                </el-icon>
              </div>
              
              <el-collapse-transition>
                <div class="submit-items" v-show="group.isExpanded">
                  <div v-for="(item, itemIndex) in group.items" 
                       :key="itemIndex"
                       class="submit-item">
                    <div class="item-main">
                      <!-- 移除复选框，只保留文本 -->
                      <span class="item-text">{{ item.name }}</span>
                      <button class="submit-button" 
                              :class="{ 'submitted': item.status === 'approved' }"
                              @click="handleSubmit(item)"
                              :disabled="item.status === 'approved'">
                        <p>{{ item.status === 'approved' ? '已提交' : '提交' }}</p>
                        <svg v-if="!item.status === 'approved'" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                          <path d="M9 16.2L4.8 12l-1.4 1.4L9 19 21 7l-1.4-1.4L9 16.2z"/>
                        </svg>
                      </button>
                    </div>
                    
                    <!-- 修改上传区域，移除已通过的显示 -->
                    <div class="item-upload" v-if="item.isCompleted && item.status !== 'approved'">
                      <template v-if="item.status === 'pending' || item.status === 'rejected'">
                        <el-upload
                          class="upload-inline"
                          action="/api/upload"
                          :auto-upload="false"
                          :show-file-list="false"
                          :on-change="(file) => handleFileChange(file, item)">
                          <el-button type="primary" link>
                            {{ item.status === 'rejected' ? '重新上传' : '上传文件' }}
                          </el-button>
                        </el-upload>
                      </template>
                      <template v-else-if="item.status === 'reviewing'">
                        <span class="reviewing-text">审核中...</span>
                      </template>
                    </div>
                  </div>
                </div>
              </el-collapse-transition>
            </div>
          </div>

          <!-- 上传要求说明 -->
          <div class="upload-requirements">
            <h4>上传要求</h4>
            <div class="upload-list">
            <ul>
              <li>文件格式：PDF、JPG、PNG</li>
              <li>文件大小：不超过10MB</li>
              <li>文件名称：请使用英文或拼音</li>
              <li>清晰度：确保文件清晰可读</li>
              <li>翻译件：必要时请提供翻译件</li>
            </ul>
            </div>
          </div>
        </div>
      </div>

      <!-- 材料准备指南 -->
      <div class="content-section">
        <h2>材料准备指南</h2>
        <div class="guide-steps">
          <el-steps :active="4" direction="vertical">
            <el-step 
              v-for="(step, index) in guideSteps" 
              :key="index"
              :title="step.title"
              :description="step.description" />
          </el-steps>
        </div>
      </div>

      <!-- 咨询部分 -->
      <div class="consultation-section">
        <h2>需要帮助？</h2>
        <p>我们的留学顾问可以为您提供材料准备的专业指导</p>
        <el-button type="primary" size="large" @click="showConsultation">
          立即咨询
        </el-button>
      </div>
    </div>

    <!-- 上传弹窗 -->
    <el-dialog
      v-model="showUploadDialog"
      :title="currentMaterial?.name || '上传材料'"
      width="500px">
      <div class="upload-content">
        <el-upload
          class="upload-area"
          drag
          action="/api/upload"
          :auto-upload="false"
          :on-change="handleFileChange">
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            拖拽文件到此处或 <em>点击上传</em>
          </div>
        </el-upload>
        <div class="upload-tips">
          <p>支持 PDF、JPG、PNG 格式，单个文件不超过 10MB</p>
          <p>请确保文件清晰可读，必要时请提供翻译件</p>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showUploadDialog = false">取消</el-button>
          <el-button type="primary" @click="submitUpload">
            确认上传
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import NavBar from '../../components/NavBar.vue'
import { 
  Document, School, Collection, Tickets, Message, 
  User, ArrowDown, UploadFilled, Check 
} from '@element-plus/icons-vue'

// 材料分组数据
const materialGroups = ref([
  {
    name: '基础材料',
    icon: 'Document',
    isExpanded: true,
    isAllCompleted: false,
    isIndeterminate: false,
    items: [
      { name: '护照扫描件', isCompleted: false, status: 'pending' },
      { name: '身份证正反面', isCompleted: false, status: 'pending' },
      { name: '学生证', isCompleted: false, status: 'pending' }
    ]
  },
  {
    name: '学历材料',
    icon: 'School',
    isExpanded: false,
    isAllCompleted: false,
    isIndeterminate: false,
    items: [
      { name: '本科成绩单（中文）', isCompleted: false, status: 'pending' },
      { name: '本科成绩单（英文）', isCompleted: false, status: 'pending' },
      { name: '学位证书', isCompleted: false, status: 'pending' },
      { name: '在读证明', isCompleted: false, status: 'pending' }
    ]
  },
  {
    name: '语言成绩',
    icon: 'Collection',
    isExpanded: false,
    isAllCompleted: false,
    isIndeterminate: false,
    items: [
      { name: 'TOEFL/IELTS成绩单', isCompleted: false, status: 'pending' },
      { name: 'GRE/GMAT成绩单', isCompleted: false, status: 'pending' }
    ]
  },
  {
    name: '文书材料',
    icon: 'Message',
    isExpanded: false,
    isAllCompleted: false,
    isIndeterminate: false,
    items: [
      { name: '个人陈述', isCompleted: false, status: 'pending' },
      { name: '推荐信', isCompleted: false, status: 'pending' },
      { name: '研究计划', isCompleted: false, status: 'pending' }
    ]
  }
])

// 计算总数和完成数
const totalCount = computed(() => {
  return materialGroups.value.reduce((total, group) => {
    return total + group.items.length
  }, 0)
})

const completedCount = computed(() => {
  return materialGroups.value.reduce((total, group) => {
    return total + group.items.filter(item => item.isCompleted).length
  }, 0)
})

// 上传相关状态
const showUploadDialog = ref(false)
const currentMaterial = ref(null)

// 展开/收起分组
const toggleGroup = (index) => {
  materialGroups.value[index].isExpanded = !materialGroups.value[index].isExpanded
}

// 获取分组进度
const getGroupProgress = (group) => {
  const completed = group.items.filter(item => item.isCompleted).length
  return `${completed}/${group.items.length}`
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'pending': '未提交',
    'reviewing': '审核中',
    'rejected': '已退回',
    'approved': '已通过'
  }
  return statusMap[status]
}

// 获取操作文本
const getActionText = (status) => {
  const actionMap = {
    'pending': '上传',
    'reviewing': '查看',
    'rejected': '重新上传',
    'approved': '已完成'
  }
  return actionMap[status]
}

// 处理上传
const handleUpload = (item) => {
  currentMaterial.value = item
  showUploadDialog.value = true
}

// 更新进度
const updateProgress = () => {
  // 这里可以添加进度更新的逻辑
}

// 处理文件变化
const handleFileChange = (file, item) => {
  // 处理文件变化的逻辑
}

// 提交上传
const submitUpload = () => {
  // 处理文件上传的逻辑
  showUploadDialog.value = false
}

// 准备指南步骤
const guideSteps = ref([
  {
    title: '确认材料清单',
    description: '根据目标院校要求,确认所需准备的材料清单及具体要求'
  },
  {
    title: '规划时间节点',
    description: '合理安排各项材料的准备时间,确保按时完成准备工作'
  },
  {
    title: '准备基础材料',
    description: '准备成绩单、学历证明等基础材料,并完成公证翻译'
  },
  {
    title: '撰写文书材料',
    description: '用心准备个人陈述、推荐信等文书材料,突出个人特色'
  }
])

// 显示咨询弹窗
const showConsultation = () => {
  // 实现咨询功能
}

// 添加新的方法
const handleGroupCheckChange = (group) => {
  const isChecked = group.isAllCompleted
  group.items.forEach(item => {
    item.isCompleted = isChecked
  })
}

const handleItemCheckChange = (group) => {
  const itemsCompleted = group.items.filter(item => item.isCompleted).length
  group.isAllCompleted = itemsCompleted === group.items.length
  group.isIndeterminate = itemsCompleted > 0 && itemsCompleted < group.items.length
}

const getStatusType = (status) => {
  const typeMap = {
    'pending': 'info',
    'reviewing': 'warning',
    'rejected': 'danger',
    'approved': 'success'
  }
  return typeMap[status]
}

// 添加展开/收起分组的方法
const toggleSubmitGroup = (index) => {
  materialGroups.value[index].isExpanded = !materialGroups.value[index].isExpanded
}

// 修改提交处理函数
const handleSubmit = (item) => {
  // 更新状态为已提交
  item.status = 'approved'
  // 更新完成状态
  item.isCompleted = true
  
  // 更新进度条
  const group = materialGroups.value.find(group => 
    group.items.some(i => i === item)
  )
  if (group) {
    // 更新组的完成状态
    const completedItems = group.items.filter(i => i.isCompleted).length
    group.isAllCompleted = completedItems === group.items.length
    group.isIndeterminate = completedItems > 0 && completedItems < group.items.length
  }
}

const isLoading = ref(true)
const data = ref(null)

onMounted(async () => {
  try {
    // 加载数据
    await loadData()
  } finally {
    isLoading.value = false
  }
})
</script>

<style scoped>
.step-page {
  padding-top: 60px;
}

.page-header {
  background: linear-gradient(135deg, #409EFF, #36D1DC);
  color: white;
  padding: 60px 20px;
  text-align: center;
  margin-bottom: 40px;  /* 添加底部间距 */
}

.page-header h1 {
  font-size: 36px;
  margin: 0 0 10px;
}

.timing {
  font-size: 18px;
  opacity: 0.9;
}

.page-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  position: relative;
  z-index: 1;  /* 确保内容在背景之上 */
}

.content-section {
  margin-bottom: 60px;
}

.content-section h2 {
  font-size: 28px;
  color: #333;
  margin-bottom: 30px;
  text-align: center;
}

.materials-container {
  display: flex;
  gap: 24px;
  margin: 20px -240px 40px;
  position: relative;
}

/* 左侧材料清单容器 */
.materials-checklist {
  flex: 0 0 1200px; /* 修改为更宽的固定宽度 */
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  height: fit-content;
  position: sticky;
  top: 20px;
}

/* 右侧上传容器 */
.materials-upload {
  flex: 0 0 500px; /* 修改为更窄的固定宽度 */
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  height: fit-content;
  overflow: visible;
}

.upload-header {
  padding: 24px 24px 16px;
  text-align: left;
  border-bottom: 1px solid #f0f0f0;
}

.upload-header h3 {
  font-size: 20px;
  color: #1d1d1f;
  margin: 0 0 4px;
}

.upload-header p {
  font-size: 14px;
  color: #86868b;
  margin: 0;
}

.materials-submit-list {
  margin-top: 0;
  padding: 0 24px 24px;
}
/*复选框间的距离 */
.submit-group {
  margin-bottom: 6px;
  background: #f9f9f9;
  border-radius: 8px;
  overflow: hidden;
}
/* 复选框大小 */
.submit-group-header {
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f5f7f7;
  cursor: pointer;
  user-select: none;
  transition: background-color 0.2s;
}

.submit-group-header:hover {
  background: #f0f0f0;
}

.group-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.group-arrow {
  font-size: 16px;
  color: #909399;
  transition: transform 0.3s;
}

.group-arrow.is-expanded {
  transform: rotate(180deg);
}

.group-count {
  font-size: 13px;
  color: #86868b;
}

.submit-items {
  position: relative; /* 添加定位上下文 */
  z-index: 1; /* 确保展开内容在上层 */
}

.submit-item {
  padding: 16px;
  border-bottom: 1px solid #eee;
  background: white;
}

.submit-item:last-child {
  border-bottom: none;
}

.item-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1px 0;
}

.item-upload {
  padding-left: 24px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.upload-inline {
  display: inline-block;
}

.reviewing-text {
  color: #e6a23c;
  font-size: 14px;
}

.success-icon {
  color: #67c23a;
}

.success-text {
  color: #67c23a;
  font-size: 14px;
}

/* 移动端适配 */
@media (max-width: 1200px) {
  .page-header {
    margin-bottom: 20px;  /* 移动端下减小间距 */
  }

  .materials-container {
    margin: 20px 0;
    flex-direction: column;
  }
  
  .materials-checklist,
  .materials-upload {
    width: 100%;
    flex: none;
    position: static;
  }
}

.guide-steps {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.consultation-section {
  text-align: center;
  background: #f5f7fa;
  padding: 60px 20px;
  border-radius: 12px;
}

.consultation-section h2 {
  margin-bottom: 20px;
}

.consultation-section p {
  color: #666;
  margin-bottom: 30px;
}

/* 自定义状态复选框样式 */
.status-container {
  display: flex;
  align-items: center;
}

.container {
  display: flex;
  position: relative;
  cursor: pointer;
  font-size: 14px;
  user-select: none;
  align-items: center;
}

.container input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
  height: 0;
  width: 0;
}

.checkmark {
  position: relative;
  height: 1.8em;
  width: 1.8em;
  background-color: #ccc;
  border-radius: 50%;
  transition: .4s;
}

.checkmark:hover {
  box-shadow: inset 12px 12px 12px #b3b3b3,
            inset -12px -12px 12px #ffffff;
}

.container input:checked ~ .checkmark {
  box-shadow: none;
  background-color: #67C23A;
  transform: rotateX(360deg);
}

.container input:checked ~ .checkmark:hover {
  box-shadow: 2px 2px 2px rgba(0,0,0,0.2);
}

.checkmark:after {
  content: "";
  position: absolute;
  display: none;
}

.container input:checked ~ .checkmark:after {
  display: block;
}

.container .checkmark:after {
  left: 0.75em;
  top: 0.5em;
  width: 0.2em;
  height: 0.4em;
  border: solid white;
  border-width: 0 0.15em 0.15em 0;
  box-shadow: 0.1em 0.1em 0em 0 rgba(0,0,0,0.3);
  transform: rotate(45deg);
}

/* 移除状态文本相关样式 */
.status-text {
  display: none;
}

/* 调整不同状态的颜色 */
.checkmark.pending {
  background-color: #ccc;
}

.checkmark.reviewing {
  background-color: #E6A23C;
}

.checkmark.rejected {
  background-color: #F56C6C;
}

.checkmark.approved {
  background-color: #67C23A;
}

/* 添加材料清单标题样式 */
.checklist-title {
  padding: 24px 24px 16px;
  text-align: left;
  border-bottom: 1px solid #f0f0f0;
}

.checklist-title h3 {
  font-size: 20px;
  color: #1d1d1f;
  margin: 0 0 4px;
}

.checklist-title p {
  font-size: 14px;
  color: #86868b;
  margin: 0;
}

/* 修改进度条样式 */
.upload-progress {
  margin: 0;
  padding: 20px 24px;
}

.progress-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.progress-text {
  font-size: 14px;
  color: #1d1d1f;
  font-weight: 500;
}

.progress-bar-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.progress-inner {
  height: 100%;
  background: #409EFF;
  border-radius: 4px;
  transition: width 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.progress-inner::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    90deg,
    rgba(255, 255, 255, 0.1) 25%,
    rgba(255, 255, 255, 0.2) 50%,
    rgba(255, 255, 255, 0.1) 75%
  );
  animation: progressShine 2s infinite linear;
}

.progress-percentage {
  font-size: 14px;
  color: #409EFF;
  font-weight: 500;
  min-width: 45px;
  text-align: right;
}

@keyframes progressShine {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

/* 添加新的材料清单样式 */
.checklist-content {
  padding: 10px 10px;
}

.checklist-group {
  margin-bottom: 10px;
}

.checklist-group:last-child {
  margin-bottom: 0;
}

.checklist-group-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 10px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 10px;
}

.checklist-group-header .el-icon {
  font-size: 1px;
  color: #409EFF;
}

.group-name {
  font-size: 16px;
  font-weight: 500;
  color: #1d1d1f;
}

.checklist-items {
  padding: 0 12px;
}

.checklist-item {
  padding: 12px 8px;
  border-bottom: 1px solid #f0f0f0;
}
/* 左侧框下小字体修改 */
.item-name {
  font-size: 13px;
  color: #606266;
}

/* 移除所有与复选框相关的样式 */
.status-container,
.container,
.checkmark,
.status-text {
  display: none;
}

.group-text {
  font-size: 16px;
  font-weight: 500;
  color: #1d1d1f;
}
/* 右侧复选框下小字体修改 */
.item-text {
  font-size: 13px;
  color: #606266;
}
.upload-requirements{
  font-size: 18px;
  padding: 0 0 0 25px;
  text-align: left;
  border-bottom: 1px solid #f0f0f0;
  margin-top: -30px; /* 减小与上方文书材料的间距 */
}
.upload-list{
  font-size: 13px;
  text-align: left;
  padding: 0 16px;
  margin-top: 1px; /* 调整列表与标题的间距 */
}

.upload-list ul {
  padding-left: 0; /* 移除ul默认的左内边距 */
  list-style-position: inside; /* 将列表标记放在内容区域内 */
}

.upload-list li {
  padding: 5px 0; /* 添加上下内边距使间距更合理 */
  line-height: 1.5; /* 调整行高 */
}

/* 提交按钮样式 */
.submit-button {
  background-color: #fff;
  border: 1px solid #409EFF;
  padding: 5px;
  position: relative;
  width: 5.5em;
  height: 2em;
  transition: 0.5s;
  font-size: 13px;
  border-radius: 0.4em;
  margin-left: 10px;
}

.submit-button p {
  position: absolute;
  top: 0.25em;
  left: 1.6em;
  margin: 0;
  padding: 0;
  transition: 0.5s;
  color: #409EFF;
}

.submit-button svg {
  position: absolute;
  top: 0.45em;
  right: 0.5em;
  margin: 0;
  padding: 0;
  opacity: 0;
  transition: 0.5s;
  height: 1em;
  fill: #fff;
}

.submit-button:hover:not(.submitted) {
  background-color: #409EFF;
}

.submit-button:hover:not(.submitted) p {
  left: 0.6em;
  color: #fff;
}

.submit-button:hover:not(.submitted) svg {
  opacity: 1;
}

/* 已提交状态 */
.submit-button.submitted {
  background-color: #f0f9eb;
  border-color: #67c23a;
  cursor: default;
}

.submit-button.submitted p {
  color: #67c23a;
  left: 1em;
}
</style> 