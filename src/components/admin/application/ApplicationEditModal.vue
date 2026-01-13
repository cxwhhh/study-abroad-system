<template>
  <div class="modal-overlay">
    <div class="modal-container">
      <div class="modal-header">
        <div class="modal-title">
          <Edit class="modal-icon" />
          <h3>编辑申请信息</h3>
        </div>
        <button class="btn-icon-sm" @click="$emit('close')">
          <X />
        </button>
      </div>
      <div class="modal-body">
        <div class="modal-tabs">
          <button 
            v-for="(tab, index) in tabs" 
            :key="index" 
            class="modal-tab" 
            :class="{ active: activeTab === tab.value }"
            @click="activeTab = tab.value"
          >
            {{ tab.label }}
          </button>
        </div>
        
        <div v-if="activeTab === 'basic'" class="form-section">
          <h4 class="form-section-title">基本信息</h4>
          <div class="form-group">
            <label>申请ID</label>
            <input type="text" v-model="application.id" disabled class="form-input disabled" />
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>申请状态 <span class="required">*</span></label>
              <div class="select-wrapper">
                <select v-model="application.status" class="form-select">
                  <option value="待提交">待提交</option>
                  <option value="已提交">已提交</option>
                  <option value="材料审核中">材料审核中</option>
                  <option value="面试中">面试中</option>
                  <option value="录取">录取</option>
                  <option value="拒绝">拒绝</option>
                  <option value="已完成">已完成</option>
                </select>
                <ChevronDown class="select-icon" />
              </div>
            </div>
            <div class="form-group">
              <label>截止日期 <span class="required">*</span></label>
              <input type="date" v-model="application.deadlineStr" class="form-input" />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>负责老师 <span class="required">*</span></label>
              <div class="select-wrapper">
                <select v-model="application.consultant" class="form-select">
                  <option v-for="teacher in teachers" :key="teacher" :value="teacher">{{ teacher }}</option>
                </select>
                <ChevronDown class="select-icon" />
              </div>
            </div>
            <div class="form-group">
              <label>材料完成度 <span class="required">*</span></label>
              <input type="number" v-model="application.materialsProgress" min="0" max="100" class="form-input" />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>申请学位 <span class="required">*</span></label>
              <div class="select-wrapper">
                <select v-model="application.degree" class="form-select">
                  <option value="本科">本科</option>
                  <option value="硕士">硕士</option>
                  <option value="博士">博士</option>
                </select>
                <ChevronDown class="select-icon" />
              </div>
            </div>
            <div class="form-group">
              <label>申请批次 <span class="required">*</span></label>
              <div class="select-wrapper">
                <select v-model="application.round" class="form-select">
                  <option value="早申">早申</option>
                  <option value="常规">常规</option>
                  <option value="补录">补录</option>
                </select>
                <ChevronDown class="select-icon" />
              </div>
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'institution'" class="form-section">
          <h4 class="form-section-title">院校信息</h4>
          <div class="form-group">
            <label>申请院校 <span class="required">*</span></label>
            <input type="text" v-model="application.institution" placeholder="请输入申请院校" class="form-input" />
          </div>
          <div class="form-group">
            <label>申请项目 <span class="required">*</span></label>
            <input type="text" v-model="application.program" placeholder="请输入申请项目" class="form-input" />
          </div>
          <div class="form-group">
            <label>国家/地区 <span class="required">*</span></label>
            <div class="select-wrapper">
              <select v-model="application.country" class="form-select">
                <option value="美国">美国</option>
                <option value="英国">英国</option>
                <option value="加拿大">加拿大</option>
                <option value="澳大利亚">澳大利亚</option>
                <option value="新西兰">新西兰</option>
                <option value="中国香港">中国香港</option>
                <option value="新加坡">新加坡</option>
              </select>
              <ChevronDown class="select-icon" />
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'student'" class="form-section">
          <h4 class="form-section-title">学生信息</h4>
          <div class="student-select">
            <div class="student-search">
              <input 
                type="text" 
                v-model="studentSearchQuery" 
                placeholder="搜索学生..." 
                class="search-input"
                @focus="showStudentDropdown = true"
                @input="searchStudents"
              />
              <Search class="search-icon" />
            </div>
            <div v-if="showStudentDropdown" class="student-dropdown">
              <div 
                v-for="student in filteredStudents" 
                :key="student.id" 
                class="student-option"
                @click="selectStudent(student)"
              >
                <div class="student-avatar">
                  <img v-if="student.avatar" :src="student.avatar" :alt="student.name" />
                  <div v-else class="avatar-text" :style="{ backgroundColor: generateAvatarColor(student.name) }">
                    {{ student.name?.charAt(0) || '学' }}
                  </div>
                </div>
                <div class="student-info">
                  <div class="student-name">{{ student.name }}</div>
                  <div class="student-detail">{{ student.school }}</div>
                </div>
              </div>
              <div v-if="filteredStudents.length === 0" class="no-results">
                未找到匹配的学生
              </div>
            </div>
          </div>
          
          <div v-if="application.student" class="selected-student">
            <div class="student-card">
              <div class="student-avatar">
                <img v-if="application.student.avatar" :src="application.student.avatar" :alt="application.student.name" />
                <div v-else class="avatar-text" :style="{ backgroundColor: generateAvatarColor(application.student.name) }">
                  {{ application.student.name?.charAt(0) || '学' }}
                </div>
              </div>
              <div class="student-info">
                <div class="student-name">{{ application.student.name }}</div>
                <div class="student-details">
                  <div class="detail-item">学校: {{ application.student.school || '未知' }}</div>
                </div>
              </div>
              <button class="btn-icon-sm" @click="clearSelectedStudent">
                <X />
              </button>
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'notes'" class="form-section">
          <h4 class="form-section-title">备注信息</h4>
          <div class="form-group">
            <label>申请备注</label>
            <textarea 
              v-model="application.notes" 
              placeholder="请输入申请备注信息..." 
              class="form-textarea"
              rows="5"
            ></textarea>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-outline" @click="$emit('close')">取消</button>
        <button class="btn btn-primary" @click="saveApplication" :disabled="!isFormValid">保存修改</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { Edit, X, ChevronDown, Search } from 'lucide-vue-next'
import { ElMessage } from 'element-plus'

const props = defineProps({
  applicationData: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['close', 'save'])

// 选项卡
const tabs = [
  { label: '基本信息', value: 'basic' },
  { label: '院校信息', value: 'institution' },
  { label: '学生信息', value: 'student' },
  { label: '备注信息', value: 'notes' }
]

const activeTab = ref('basic')

// 负责老师列表
const teachers = ['王老师', '李老师', '张老师', '赵老师']

// 创建申请对象的副本，避免直接修改props
const application = ref({
  id: '',
  status: '',
  deadline: '',
  deadlineStr: '', // 用于日期输入
  consultant: '',
  materialsProgress: 0,
  degree: '',
  round: '',
  institution: '',
  program: '',
  country: '',
  student: null,
  notes: ''
})

// 学生搜索
const studentSearchQuery = ref('')
const showStudentDropdown = ref(false)
const students = ref([]) // 模拟学生列表

// 初始化表单数据
watch(() => props.applicationData, (newVal) => {
  if (newVal) {
    // 解析日期为字符串
    const deadlineDate = newVal.deadline || newVal.applicationDeadline ? 
      new Date(newVal.deadline || newVal.applicationDeadline) : new Date()
    const deadlineStr = deadlineDate.toISOString().split('T')[0]
    
    application.value = {
      id: newVal.id || '',
      status: newVal.status || '待提交',
      deadline: newVal.deadline || newVal.applicationDeadline || new Date(),
      deadlineStr: deadlineStr,
      consultant: newVal.consultant || newVal.consultantName || '王老师',
      materialsProgress: newVal.materialsProgress || 0,
      degree: newVal.degree || '硕士',
      round: newVal.round || '常规',
      institution: newVal.institution || newVal.schoolName || '',
      program: newVal.program || newVal.majorName || '',
      country: newVal.country || '美国',
      student: newVal.student ? { 
        ...newVal.student,
        id: newVal.student.id || newVal.studentId,
        name: newVal.student.name || newVal.studentName,
        email: newVal.student.email || newVal.studentEmail,
        phone: newVal.student.phone || newVal.studentPhone,
        currentSchool: newVal.student.currentSchool || newVal.studentSchool,
        major: newVal.student.major || newVal.studentMajor
      } : null,
      notes: newVal.notes || ''
    }
  }
}, { immediate: true, deep: true })

// 表单验证
const isFormValid = computed(() => {
  return application.value.status !== '' && 
         application.value.deadlineStr !== '' && 
         application.value.consultant !== '' && 
         application.value.degree !== '' && 
         application.value.round !== '' && 
         application.value.institution !== '' && 
         application.value.program !== '' && 
         application.value.country !== '' && 
         application.value.student !== null
})

// 获取学生列表
const loadStudents = async () => {
  try {
    const response = await axios.get('/api/students')
    if (response.data.success) {
      students.value = response.data.data
    } else {
      console.error('获取学生列表失败:', response.data.message)
      students.value = []
    }
  } catch (error) {
    console.error('获取学生列表失败:', error)
    students.value = []
  }
}

// 过滤学生列表
const filteredStudents = computed(() => {
  if (!studentSearchQuery.value) {
    return students.value
  }
  
  const query = studentSearchQuery.value.toLowerCase()
  return students.value.filter(student => 
    student.name.toLowerCase().includes(query) || 
    (student.school && student.school.toLowerCase().includes(query))
  )
})

// 搜索学生
const searchStudents = () => {
  showStudentDropdown.value = true
}

// 选择学生
const selectStudent = (student) => {
  application.value.student = student
  showStudentDropdown.value = false
  studentSearchQuery.value = ''
}

// 清除已选学生
const clearSelectedStudent = () => {
  application.value.student = null
}

// 保存申请信息
const saveApplication = () => {
  if (isFormValid.value) {
    try {
      // 转换日期字符串为日期对象
      const deadline = new Date(application.value.deadlineStr)
      
      // 准备提交的数据，统一字段命名
      const applicationData = {
        id: application.value.id,
        status: application.value.status,
        deadline: deadline,
        applicationDeadline: deadline, // 兼容后端字段
        consultant: application.value.consultant,
        consultantName: application.value.consultant, // 兼容后端字段
        materialsProgress: application.value.materialsProgress,
        degree: application.value.degree,
        round: application.value.round,
        institution: application.value.institution,
        schoolName: application.value.institution, // 兼容后端字段
        program: application.value.program,
        majorName: application.value.program, // 兼容后端字段
        country: application.value.country,
        student: application.value.student,
        studentId: application.value.student?.id,
        studentName: application.value.student?.name,
        studentEmail: application.value.student?.email,
        studentPhone: application.value.student?.phone,
        studentSchool: application.value.student?.currentSchool,
        studentMajor: application.value.student?.major,
        notes: application.value.notes
      }
      
      // 发送数据
      emit('save', applicationData)
    } catch (error) {
      console.error('保存申请信息时发生错误:', error)
      ElMessage.error('保存失败，请检查表单数据是否正确')
    }
  } else {
    ElMessage.warning('请完成所有必填字段')
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

// 监听点击事件，用于关闭下拉框
const handleClickOutside = (e) => {
  if (!e.target.closest('.student-select')) {
    showStudentDropdown.value = false
  }
}

onMounted(() => {
  // 加载学生列表
  loadStudents()
  
  // 添加全局点击事件监听
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
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
  max-width: 800px;
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

.modal-tabs {
  display: flex;
  gap: 4px;
  margin-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 16px;
  overflow-x: auto;
}

.modal-tab {
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

.modal-tab:hover {
  background-color: #f5f7fa;
}

.modal-tab.active {
  background-color: #e6f7ff;
  color: #1890ff;
  font-weight: 500;
}

.form-section {
  margin-bottom: 24px;
}

.form-section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #333;
}

.form-group {
  margin-bottom: 16px;
}

.form-row {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.form-row .form-group {
  flex: 1;
  margin-bottom: 0;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #333;
}

.required {
  color: #f5222d;
  margin-left: 4px;
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid #d9d9d9;
  font-size: 14px;
  transition: all 0.3s;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.form-input.disabled {
  background-color: #f5f5f5;
  color: #999;
  cursor: not-allowed;
}

.form-textarea {
  min-height: 80px;
  resize: vertical;
}

.select-wrapper {
  position: relative;
}

.select-wrapper select {
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
  padding-right: 32px;
}

.select-wrapper .select-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none;
  color: #999;
  width: 16px;
  height: 16px;
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

.btn-primary:disabled {
  background-color: #d9d9d9;
  cursor: not-allowed;
}

/* 学生选择 */
.student-select {
  position: relative;
  margin-bottom: 16px;
}

.student-search {
  position: relative;
}

.search-input {
  width: 100%;
  padding: 10px 12px 10px 36px;
  border-radius: 8px;
  border: 1px solid #d9d9d9;
  font-size: 14px;
  transition: all 0.3s;
}

.search-input:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  width: 16px;
  height: 16px;
}

.student-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  max-height: 240px;
  overflow-y: auto;
  background-color: white;
  border-radius: 8px;
  border: 1px solid #d9d9d9;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 10;
  margin-top: 4px;
}

.student-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.student-option:hover {
  background-color: #f5f7fa;
}

.student-avatar {
  width: 36px;
  height: 36px;
  border-radius: 18px;
  overflow: hidden;
  flex-shrink: 0;
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
  font-size: 16px;
  font-weight: 500;
  color: white;
  text-transform: uppercase;
}

.student-info {
  flex: 1;
}

.student-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.student-detail {
  font-size: 12px;
  color: #666;
}

.no-results {
  padding: 16px;
  text-align: center;
  color: #999;
  font-size: 14px;
}

.selected-student {
  margin-top: 24px;
}

.student-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 8px;
  position: relative;
}

.student-details {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}

.detail-item {
  margin-bottom: 4px;
}

@media (max-width: 576px) {
  .form-row {
    flex-direction: column;
    gap: 8px;
  }
  
  .modal-container {
    width: 95%;
  }
}
</style>