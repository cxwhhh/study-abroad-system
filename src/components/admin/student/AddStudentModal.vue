<template>
  <div class="modal-overlay">
    <div class="modal-container">
      <div class="modal-header">
        <div class="modal-title">
          <UserPlus class="modal-icon" />
          <h3>添加学生</h3>
        </div>
        <button class="btn-icon-sm" @click="$emit('close')">
          <X />
        </button>
      </div>
      <div class="modal-body">
        <div class="modal-tabs">
          <button v-for="(tab, index) in tabs" :key="index" class="modal-tab"
            :class="{ active: activeTab === tab.value }" @click="activeTab = tab.value">
            {{ tab.label }}
          </button>
        </div>

        <div v-if="activeTab === 'basic'" class="form-section">
          <h4 class="form-section-title">学生基本信息</h4>
          <div class="form-group">
            <label>姓名 <span class="required">*</span></label>
            <input type="text" v-model="student.name" placeholder="请输入学生姓名" class="form-input" />
          </div>
          <div class="form-group">
            <label>用户ID <span class="required">*</span></label>
            <input type="text" v-model="student.userId" placeholder="请输入有效的用户ID" class="form-input" />
            <small class="field-hint">需要输入系统中存在的用户ID，否则无法添加学生</small>
          </div>
          <div class="form-group">
            <label>学生ID</label>
            <input type="text" v-model="student.id" placeholder="请输入学生ID" class="form-input" />
          </div>
          <div class="form-group">
            <label>英文名</label>
            <input type="text" v-model="student.englishName" placeholder="请输入英文名" class="form-input" />
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>性别 <span class="required">*</span></label>
              <div class="select-wrapper">
                <select v-model="student.gender" class="form-select">
                  <option value="男">男</option>
                  <option value="女">女</option>
                </select>
                <ChevronDown class="select-icon" />
              </div>
            </div>
            <div class="form-group">
              <label>出生日期 <span class="required">*</span></label>
              <input type="date" v-model="student.birthDate" class="form-input" />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>国籍</label>
              <input type="text" v-model="student.nationality" placeholder="请输入国籍" class="form-input" />
            </div>
            <div class="form-group">
              <label>护照号</label>
              <input type="text" v-model="student.passportNo" placeholder="请输入护照号" class="form-input" />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>年级 <span class="required">*</span></label>
              <div class="select-wrapper">
                <select v-model="student.grade" class="form-select">
                  <option value="大一">大一</option>
                  <option value="大二">大二</option>
                  <option value="大三">大三</option>
                  <option value="大四">大四</option>
                  <option value="研一">研一</option>
                  <option value="研二">研二</option>
                  <option value="研三">研三</option>
                </select>
                <ChevronDown class="select-icon" />
              </div>
            </div>
            <div class="form-group">
              <label>学校 <span class="required">*</span></label>
              <input type="text" v-model="student.currentSchool" placeholder="请输入学校名称" class="form-input" />
            </div>
          </div>
          <div class="form-group">
            <label>专业</label>
            <input type="text" v-model="student.major" placeholder="请输入专业" class="form-input" />
          </div>
        </div>

        <div v-if="activeTab === 'contact'" class="form-section">
          <h4 class="form-section-title">联系方式</h4>
          <div class="form-group">
            <label>电子邮箱 <span class="required">*</span></label>
            <input type="email" v-model="student.email" placeholder="请输入电子邮箱" class="form-input" />
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>手机号码 <span class="required">*</span></label>
              <input type="tel" v-model="student.phone" placeholder="请输入手机号码" class="form-input" />
            </div>
            <div class="form-group">
              <label>微信号</label>
              <input type="text" v-model="student.wechat" placeholder="请输入微信号" class="form-input" />
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'academic'" class="form-section">
          <h4 class="form-section-title">学术成绩</h4>
          <div class="form-row">
            <div class="form-group">
              <label>GPA</label>
              <input type="number" step="0.01" v-model="student.gpa" placeholder="请输入GPA" class="form-input" />
            </div>
            <div class="form-group">
              <label>GPA制度</label>
              <div class="select-wrapper">
                <select v-model="student.gpaScale" class="form-select">
                  <option value="4.0">4.0制</option>
                  <option value="5.0">5.0制</option>
                  <option value="100">百分制</option>
                </select>
                <ChevronDown class="select-icon" />
              </div>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>托福成绩</label>
              <input type="number" step="0.1" v-model="student.toefl" placeholder="请输入托福成绩" class="form-input" />
            </div>
            <div class="form-group">
              <label>雅思成绩</label>
              <input type="number" step="0.1" v-model="student.ielts" placeholder="请输入雅思成绩" class="form-input" />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>GRE成绩</label>
              <input type="number" v-model="student.gre" placeholder="请输入GRE成绩" class="form-input" />
            </div>
            <div class="form-group">
              <label>GMAT成绩</label>
              <input type="number" v-model="student.gmat" placeholder="请输入GMAT成绩" class="form-input" />
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'application'" class="form-section">
          <h4 class="form-section-title">申请情况</h4>
          <div class="form-group">
            <label>目标国家/地区 <span class="required">*</span></label>
            <div class="checkbox-group">
              <label v-for="(country, index) in countries" :key="index" class="checkbox-label">
                <input type="checkbox" :value="country" v-model="student.targetCountries" />
                <span>{{ country }}</span>
              </label>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>申请状态</label>
              <div class="select-wrapper">
                <select v-model="student.applicationStatus" class="form-select">
                  <option value="审核中">审核中</option>
                  <option value="等待中">等待中</option>
                  <option value="通过">通过</option>
                  <option value="拒绝">拒绝</option>
                  <option value="录取">录取</option>
                  <option value="已毕业">已毕业</option>
                </select>
                <ChevronDown class="select-icon" />
              </div>
            </div>
            <div class="form-group">
              <label>申请进度（百分比）</label>
              <input type="number" min="0" max="100" v-model="student.applicationProgress" placeholder="请输入申请进度"
                class="form-input" />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>当前申请步骤</label>
              <div class="select-wrapper">
                <select v-model="student.currentStep" class="form-select">
                  <option value="1">1 - 初步咨询</option>
                  <option value="2">2 - 材料准备</option>
                  <option value="3">3 - 提交申请</option>
                  <option value="4">4 - 面试</option>
                  <option value="5">5 - 录取</option>
                  <option value="6">6 - 签证</option>
                </select>
                <ChevronDown class="select-icon" />
              </div>
            </div>
            <div class="form-group">
              <label>顾问老师 <span class="required">*</span></label>
              <div class="select-wrapper">
                <select v-model="student.consultant" class="form-select">
                  <option value="">请选择顾问老师</option>
                  <option v-for="advisor in advisors" :key="advisor.id" :value="advisor.name">
                    {{ advisor.name }}
                  </option>
                </select>
                <ChevronDown class="select-icon" />
              </div>
            </div>
          </div>
          <div class="form-group">
            <label>备注</label>
            <textarea v-model="student.notes" placeholder="请输入备注信息" class="form-textarea"></textarea>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-outline" @click="$emit('close')">取消</button>
        <button class="btn btn-primary" @click="handleSave" :disabled="!isFormValid">添加学生</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { UserPlus, X, ChevronDown } from 'lucide-vue-next'
import request from '../../../utils/request'
import { ElMessage } from 'element-plus'

const props = defineProps({
  newStudent: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['close', 'save'])

// 顾问列表
const advisors = ref([])

// 获取顾问列表
const fetchAdvisors = async () => {
  try {
    const res = await request.post('/adminuser/filter', {
      role: 'consultant', // 筛选顾问角色的管理员
      status: '1', // 仅获取活跃状态的管理员
      page: 1,
      pageSize: 100
    })

    const data = res?.data?.data || []
    advisors.value = data.map(advisor => ({
      id: advisor.id,
      name: advisor.name || advisor.nickname || advisor.username
    }))

    if (advisors.value.length === 0) {
      advisors.value = [
        { id: 1, name: '王管理老师' },
        { id: 2, name: '李管理老师' },
        { id: 3, name: '张管理老师' },
        { id: 4, name: '赵管理老师' }
      ]
    }
  } catch (error) {
    console.error('获取顾问列表失败:', error)
    // 使用默认顾问列表
    advisors.value = [
      { id: 1, name: '王管理老师' },
      { id: 2, name: '李管理老师' },
      { id: 3, name: '张管理老师' },
      { id: 4, name: '赵管理老师' }
    ]
  }
}

// 初始化时获取顾问列表
onMounted(() => {
  fetchAdvisors()
})

// 选项卡
const tabs = [
  { label: '基本信息', value: 'basic' },
  { label: '联系方式', value: 'contact' },
  { label: '学术成绩', value: 'academic' },
  { label: '申请情况', value: 'application' }
]

const activeTab = ref('basic')

// 国家列表
const countries = [
  '美国', '英国', '加拿大', '澳大利亚', '新西兰',
  '法国', '德国', '意大利', '西班牙', '日本',
  '韩国', '新加坡', '香港', '荷兰', '瑞士'
]

// 创建学生对象的副本，避免直接修改props
const student = reactive({
  id: '',
  userId: '',
  applicationId: '',
  name: '',
  englishName: '',
  gender: '男',
  birthDate: '',
  nationality: '中国',
  passportNo: '',
  currentSchool: '',
  major: '',
  phone: '',
  email: '',
  wechat: '',
  gpa: '',
  gpaScale: '4.0',
  toefl: '',
  ielts: '',
  gre: '',
  gmat: '',
  applicationStatus: '',
  applicationProgress: 0,
  currentStep: '1',
  targetCountries: [],
  notes: '',
  grade: '',
  consultant: '',
})

// 初始化表单数据
watch(() => props.newStudent, (newVal) => {
  if (newVal) {
    student.id = newVal.id || ''
    student.userId = newVal.userId || ''
    student.applicationId = newVal.applicationId || ''
    student.name = newVal.name || ''
    student.englishName = newVal.englishName || ''
    student.gender = newVal.gender || '男'

    // 如果传入的是年龄，则需要转换为生日
    if (newVal.age) {
      const now = new Date()
      const birthYear = now.getFullYear() - newVal.age
      student.birthDate = `${birthYear}-01-01`
    } else {
      student.birthDate = newVal.birthDate || ''
    }

    student.nationality = newVal.nationality || '中国'
    student.passportNo = newVal.passportNo || ''
    student.grade = newVal.grade || ''
    student.currentSchool = newVal.school || newVal.currentSchool || ''
    student.major = newVal.major || ''
    student.phone = newVal.contactInfo?.phone || newVal.phone || ''
    student.email = newVal.contactInfo?.email || newVal.email || ''
    student.wechat = newVal.contactInfo?.wechat || newVal.wechat || ''
    student.gpa = newVal.gpa || ''
    student.gpaScale = newVal.gpaScale || '4.0'
    student.toefl = newVal.toefl || ''
    student.ielts = newVal.ielts || ''
    student.gre = newVal.gre || ''
    student.gmat = newVal.gmat || ''
    student.applicationStatus = newVal.applicationStatus || ''
    student.applicationProgress = newVal.applicationProgress || 0
    student.currentStep = newVal.currentStep || '1'
    student.targetCountries = Array.isArray(newVal.targetCountries) ? [...newVal.targetCountries] : []
    student.notes = newVal.notes || ''
    student.consultant = newVal.consultant || ''
  }
}, { immediate: true })

// 表单验证
const isFormValid = computed(() => {
  return student.name.trim() !== '' &&
    student.gender !== '' &&
    student.birthDate !== '' &&
    student.currentSchool.trim() !== '' &&
    student.targetCountries.length > 0 &&
    student.email.trim() !== '' &&
    student.phone.trim() !== '' &&
    student.consultant.trim() !== '' &&
    student.userId !== null && student.userId.toString().trim() !== '';
})

// 保存学生信息
const handleSave = () => {
  if (isFormValid.value) {
    // 转换为适合emit的格式，专门用于添加新学生
    const studentData = {
      // 不传递id字段，确保走添加逻辑
      userId: student.userId,
      applicationId: student.applicationId,
      name: student.name,
      englishName: student.englishName,
      gender: student.gender,
      birthDate: student.birthDate,
      nationality: student.nationality,
      passportNo: student.passportNo,
      grade: student.grade,
      school: student.currentSchool,
      currentSchool: student.currentSchool,
      major: student.major,
      contactInfo: {
        email: student.email,
        phone: student.phone,
        wechat: student.wechat
      },
      email: student.email,
      phone: student.phone,
      wechat: student.wechat,
      gpa: student.gpa,
      gpaScale: student.gpaScale,
      toefl: student.toefl,
      ielts: student.ielts,
      gre: student.gre,
      gmat: student.gmat,
      applicationStatus: student.applicationStatus,
      applicationProgress: student.applicationProgress,
      currentStep: student.currentStep,
      targetCountries: student.targetCountries,
      notes: student.notes,
      consultant: student.consultant
    }

    console.log('通过添加模态框发送的新学生数据:', JSON.stringify(studentData, null, 2));
    emit('save', studentData)
  } else {
    ElMessage.warning('请完成所有必填字段')
  }
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

.modal-tabs {
  display: flex;
  gap: 2px;
  margin-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 16px;
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
}

.modal-tab:hover {
  background-color: #f5f7fa;
}

.modal-tab.active {
  background-color: #e6f7ff;
  color: #1890ff;
  font-weight: 500;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
  gap: 12px;
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
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 8px 12px;
  border-radius: 8px;
  border: 1px solid #d9d9d9;
  font-size: 14px;
  transition: all 0.3s;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  border-color: #1890ff;
  outline: none;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.form-textarea {
  min-height: 80px;
  resize: vertical;
}

.select-wrapper {
  position: relative;
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

.checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 8px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  background-color: #f5f7fa;
  transition: all 0.2s ease;
  border: 1px solid transparent;
}

.checkbox-label:hover {
  background-color: #e6f7ff;
  border-color: #91caff;
}

.checkbox-label input {
  width: 18px;
  height: 18px;
  accent-color: #1890ff;
  cursor: pointer;
}

.checkbox-label input:checked+span {
  color: #1890ff;
  font-weight: 500;
}

.checkbox-label span {
  font-size: 14px;
  color: #333;
}

.field-hint {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}
</style>
