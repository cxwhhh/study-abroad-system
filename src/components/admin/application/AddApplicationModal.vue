<template>
  <div class="modal-overlay">
    <div class="modal-container">
      <div class="modal-header">
        <div class="modal-title">
          <PlusCircle class="modal-icon" />
          <h3>新建申请</h3>
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
          <h4 class="form-section-title">基本信息</h4>
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
          <div class="form-row">
            <div class="form-group">
              <label>课程代码</label>
              <input type="text" v-model="application.code" placeholder="请输入课程代码" class="form-input" />
            </div>
            <div class="form-group">
              <label>入学日期</label>
              <input type="date" v-model="application.entryDateStr" class="form-input" />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>学习时长</label>
              <input type="text" v-model="application.duration" placeholder="如：2年" class="form-input" />
            </div>
            <div class="form-group">
              <label>校区</label>
              <input type="text" v-model="application.campus" placeholder="请输入校区信息" class="form-input" />
            </div>
          </div>
          <div class="form-group">
            <label>学分减免</label>
            <input type="text" v-model="application.creditExemption" placeholder="请输入学分减免信息" class="form-input" />
          </div>
        </div>

        <div v-if="activeTab === 'institution'" class="form-section">
          <h4 class="form-section-title">院校信息</h4>
          <div class="form-group">
            <label>申请院校 <span class="required">*</span></label>
            <div class="select-wrapper">
              <div v-if="loadingSchools" class="loading-field">
                <span>加载学校列表中...</span>
                <div class="loading-spinner-sm"></div>
              </div>
              <select v-else v-model="selectedSchool" class="form-select" @change="handleSchoolChange">
                <option value="">-- 请选择学校 --</option>
                <option v-for="school in schools" :key="school.id" :value="school">
                  {{ school.name }}
                </option>
              </select>
              <ChevronDown class="select-icon" />
            </div>
            <button v-if="schools.length === 0 && !loadingSchools" class="btn-link" @click="fetchSchools">
              <RefreshCw class="btn-icon" />
              加载学校列表
            </button>
          </div>

          <!-- 新增学院选择 -->
          <div class="form-group">
            <label>所属学院</label>
            <div class="select-wrapper">
              <div v-if="loadingFaculties" class="loading-field">
                <span>加载学院列表中...</span>
                <div class="loading-spinner-sm"></div>
              </div>
              <select v-else v-model="selectedFaculty" class="form-select" @change="handleFacultyChange"
                :disabled="!selectedSchool">
                <option value="">-- 请选择学院 --</option>
                <option v-for="faculty in faculties" :key="faculty.id" :value="faculty">
                  {{ faculty.name }}
                </option>
              </select>
              <ChevronDown class="select-icon" />
            </div>
            <button v-if="selectedSchool && faculties.length === 0 && !loadingFaculties" class="btn-link"
              @click="fetchFacultiesBySchoolId">
              <RefreshCw class="btn-icon" />
              加载学院列表
            </button>
          </div>

          <div class="form-group">
            <label>申请项目 <span class="required">*</span></label>
            <div class="select-wrapper">
              <div v-if="loadingMajors" class="loading-field">
                <span>加载专业列表中...</span>
                <div class="loading-spinner-sm"></div>
              </div>
              <select v-else v-model="selectedMajor" class="form-select" @change="handleMajorChange"
                :disabled="!selectedSchool">
                <option value="">-- 请选择专业 --</option>
                <option v-for="major in majors" :key="major.id" :value="major">
                  {{ major.name }}
                </option>
              </select>
              <ChevronDown class="select-icon" />
            </div>
            <button v-if="selectedSchool && majors.length === 0 && !loadingMajors" class="btn-link"
              @click="fetchMajorsList">
              <RefreshCw class="btn-icon" />
              加载专业列表
            </button>
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
          <div class="form-group">
            <label>申请链接</label>
            <input type="url" v-model="application.link" placeholder="请输入申请链接" class="form-input" />
          </div>
        </div>

        <div v-if="activeTab === 'student'" class="form-section">
          <h4 class="form-section-title">学生信息</h4>
          <div class="student-select">
            <div class="search-header">
              <p class="search-title">从已有学生中选择:</p>
              <button v-if="students.length > 0" class="btn-refresh" @click="fetchAllStudents"
                :disabled="loadingStudents">
                <RefreshCw :class="{ 'loading': loadingStudents }" class="refresh-icon" />
                刷新列表
              </button>
            </div>
            <div class="student-search">
              <input type="text" v-model="studentSearchQuery" placeholder="搜索学生姓名、英文名、学校或邮箱..." class="search-input"
                @focus="showStudentDropdown = true" @input="searchStudents" />
              <Search class="search-icon" />
            </div>
            <div v-if="showStudentDropdown" class="student-dropdown">
              <div v-if="loadingStudents" class="loading-container">
                <div class="loading-spinner"></div>
                <span>正在加载学生列表...</span>
              </div>

              <div v-else-if="studentError" class="error-container">
                <AlertCircle class="error-icon" />
                <span>{{ studentError }}</span>
                <button class="btn-retry" @click="fetchAllStudents">重试</button>
              </div>

              <div v-else-if="students.length === 0" class="empty-container">
                <User class="empty-icon" />
                <span>暂无学生记录</span>
              </div>

              <div v-else-if="filteredStudents.length === 0" class="no-results">
                <span>未找到匹配的学生</span>
                <small>尝试其他搜索关键词或查看完整列表</small>
              </div>

              <div v-else>
                <div v-for="student in filteredStudents" :key="student.id" class="student-option"
                  @click="selectStudent(student)">
                  <div class="student-avatar">
                    <img v-if="student.avatar" :src="student.avatar" :alt="student.name" />
                    <div v-else class="avatar-text" :style="{ backgroundColor: generateAvatarColor(student.name) }">
                      {{ student.name?.charAt(0) || '学' }}
                    </div>
                  </div>
                  <div class="student-name">
                    {{ student.name }}
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div v-if="application.student" class="selected-student">
            <div class="student-card">
              <div class="student-avatar">
                <img v-if="application.student.avatar" :src="application.student.avatar"
                  :alt="application.student.name" />
                <div v-else class="avatar-text"
                  :style="{ backgroundColor: generateAvatarColor(application.student.name) }">
                  {{ application.student.name?.charAt(0) || '学' }}
                </div>
              </div>
              <div class="student-info">
                <div class="student-name">
                  {{ application.student.name }}
                  <span v-if="application.student.englishName" class="english-name">({{ application.student.englishName
                    }})</span>
                </div>
                <div class="student-details">
                  <div v-if="application.student.school || application.student.currentSchool" class="detail-item">
                    学校: {{ application.student.school || application.student.currentSchool }}
                  </div>
                  <div v-if="application.student.major" class="detail-item">
                    专业: {{ application.student.major }}
                  </div>
                  <div v-if="application.student.email" class="detail-item">
                    邮箱: {{ application.student.email }}
                  </div>
                </div>
              </div>
              <button class="btn-icon-sm" @click="clearSelectedStudent">
                <X />
              </button>
            </div>
          </div>

          <div v-if="application.student" class="student-info-form">
            <h5 class="subsection-title">基本信息</h5>
            <div class="form-row">
              <div class="form-group">
                <label>英文名</label>
                <input type="text" v-model="application.student.englishName" placeholder="请输入英文名" class="form-input" />
              </div>
              <div class="form-group">
                <label>性别</label>
                <div class="select-wrapper">
                  <select v-model="application.student.gender" class="form-select">
                    <option value="男">男</option>
                    <option value="女">女</option>
                    <option value="其他">其他</option>
                  </select>
                  <ChevronDown class="select-icon" />
                </div>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>出生日期</label>
                <input type="date" v-model="application.student.birthDateStr" class="form-input" />
              </div>
              <div class="form-group">
                <label>国籍</label>
                <input type="text" v-model="application.student.nationality" placeholder="请输入国籍" class="form-input" />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>护照号码</label>
                <input type="text" v-model="application.student.passportNo" placeholder="请输入护照号码" class="form-input" />
              </div>
            </div>

            <h5 class="subsection-title">联系方式</h5>
            <div class="form-row">
              <div class="form-group">
                <label>电子邮箱</label>
                <input type="email" v-model="application.student.email" placeholder="请输入电子邮箱" class="form-input" />
              </div>
              <div class="form-group">
                <label>电话号码</label>
                <input type="tel" v-model="application.student.phone" placeholder="请输入电话号码" class="form-input" />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>微信号</label>
                <input type="text" v-model="application.student.wechat" placeholder="请输入微信号" class="form-input" />
              </div>
            </div>

            <h5 class="subsection-title">学术情况</h5>
            <div class="form-row">
              <div class="form-group">
                <label>当前学校</label>
                <input type="text" v-model="application.student.currentSchool" placeholder="请输入当前就读学校"
                  class="form-input" />
              </div>
              <div class="form-group">
                <label>就读专业</label>
                <input type="text" v-model="application.student.major" placeholder="请输入就读专业" class="form-input" />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>GPA</label>
                <input type="number" step="0.01" min="0" max="4" v-model="application.student.gpa" placeholder="请输入GPA"
                  class="form-input" />
              </div>
              <div class="form-group">
                <label>托福成绩</label>
                <input type="number" min="0" max="120" v-model="application.student.toefl" placeholder="请输入托福成绩"
                  class="form-input" />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>雅思成绩</label>
                <input type="number" step="0.5" min="0" max="9" v-model="application.student.ielts"
                  placeholder="请输入雅思成绩" class="form-input" />
              </div>
              <div class="form-group">
                <label>GRE成绩</label>
                <input type="number" min="0" max="340" v-model="application.student.gre" placeholder="请输入GRE成绩"
                  class="form-input" />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>GMAT成绩</label>
                <input type="number" min="0" max="800" v-model="application.student.gmat" placeholder="请输入GMAT成绩"
                  class="form-input" />
              </div>
            </div>
          </div>

          <div v-if="!application.student" class="empty-student-notice">
            <div class="notice-icon">
              <User />
            </div>
            <p>请先选择一个学生</p>
            <small>您可以从上方搜索并选择已有学生记录</small>
          </div>
        </div>

        <div v-if="activeTab === 'notes'" class="form-section">
          <h4 class="form-section-title">备注信息</h4>
          <div class="form-group">
            <label>申请备注</label>
            <textarea v-model="application.notes" placeholder="请输入申请备注信息..." class="form-textarea" rows="5"></textarea>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button v-if="progressStep > 0" class="btn btn-outline" @click="prevStep">
          上一步
        </button>
        <button v-if="progressStep < 3" class="btn btn-primary" @click="nextStep" :disabled="!isCurrentStepValid">
          下一步
        </button>
        <button v-else class="btn btn-primary" @click="saveApplication" :disabled="!isFormValid">
          创建申请
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { PlusCircle, X, ChevronDown, Search, User, RefreshCw, AlertCircle } from 'lucide-vue-next'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import request from '../../../utils/request'

const emit = defineEmits(['close', 'save'])

// 选项卡和进度
const tabs = [
  { label: '基本信息', value: 'basic' },
  { label: '院校信息', value: 'institution' },
  { label: '学生信息', value: 'student' },
  { label: '备注信息', value: 'notes' }
]

const tabValues = ['basic', 'institution', 'student', 'notes']
const activeTab = ref('basic')
const progressStep = ref(0)

// 进入下一步
const nextStep = () => {
  if (progressStep.value < 3) {
    progressStep.value++
    activeTab.value = tabValues[progressStep.value]
  }
}

// 返回上一步
const prevStep = () => {
  if (progressStep.value > 0) {
    progressStep.value--
    activeTab.value = tabValues[progressStep.value]
  }
}

// 负责老师列表
const teachers = ['王老师', '李老师', '张老师', '赵老师']

// 创建申请对象
const application = ref({
  status: '待提交',
  deadline: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000),
  deadlineStr: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
  consultant: '王老师',
  materialsProgress: 0,
  degree: '硕士',
  round: '常规',
  institution: '',
  program: '',
  country: '美国',
  code: '',
  entryDate: '',
  entryDateStr: '',
  duration: '',
  campus: '',
  creditExemption: '',
  link: '',
  student: null,
  notes: '',
  schoolId: null,
  majorId: null,
})

// 学校和专业数据
const schools = ref([])
const faculties = ref([]) // 新增学院列表
const majors = ref([])
const loadingSchools = ref(false)
const loadingFaculties = ref(false) // 新增学院加载状态
const loadingMajors = ref(false)
const selectedSchool = ref('')
const selectedFaculty = ref('') // 新增选中学院
const selectedMajor = ref('')

// 获取学校列表
const fetchSchools = async () => {
  if (loadingSchools.value) return; // 防止重复请求

  try {
    loadingSchools.value = true
    console.log('开始获取学校列表...')

    // 使用axios直接请求，不使用默认的request工具
    const response = await axios.get('/api/schools', {
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    })

    console.log('学校API响应数据:', response)

    let schoolsData = []

    // 处理可能的不同响应格式
    if (response.data) {
      if (response.data.code === 200 && response.data.data) {
        schoolsData = response.data.data
      } else if (Array.isArray(response.data)) {
        schoolsData = response.data
      } else if (typeof response.data === 'object') {
        schoolsData = Array.isArray(response.data) ? response.data : [response.data]
      }
    }

    // 确保学校数据有正确的结构
    schools.value = schoolsData.map(school => ({
      id: school.id,
      name: school.name || school.schoolName || school.englishName || `学校${school.id}`,
      country: school.country
    }))

    console.log('成功获取学校列表:', schools.value)

    if (schools.value.length === 0) {
      ElMessage.warning('未获取到学校列表，请检查数据库是否有学校数据')
    }
  } catch (error) {
    console.error('获取学校列表出错:', error)
    if (error.response) {
      console.error('服务器响应错误:', {
        status: error.response.status,
        data: error.response.data
      })
    } else if (error.request) {
      console.error('请求未收到响应:', error.request)
    }

    ElMessage.error(`网络错误，无法获取学校列表: ${error.message}`)
  } finally {
    loadingSchools.value = false
  }
}

// 根据学校ID获取专业列表
const fetchMajorsBySchoolId = async () => {
  if (!selectedSchool.value || !selectedSchool.value.id) {
    majors.value = []
    return
  }

  try {
    loadingMajors.value = true
    const schoolId = selectedSchool.value.id
    console.log(`开始获取学校ID ${schoolId} 的专业列表...`)

    // 首先尝试从学校专业接口获取
    let response = await axios.get(`/api/schools/${schoolId}/majors`, {
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    })

    console.log(`学校专业API响应数据:`, response)

    let majorsData = []

    // 处理可能的不同响应格式
    if (response.data) {
      if (response.data.code === 200 && response.data.data) {
        majorsData = response.data.data
      } else if (Array.isArray(response.data)) {
        majorsData = response.data
      } else if (typeof response.data === 'object') {
        majorsData = Array.isArray(response.data) ? response.data : [response.data]
      }
    }

    // 如果主接口没有数据，尝试备用接口
    if (majorsData.length === 0) {
      try {
        console.log('尝试从备用接口获取专业列表...')
        const backupResponse = await axios.get('/api/majors', {
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          }
        })

        if (backupResponse.data) {
          let allMajors = []

          if (backupResponse.data.code === 200 && backupResponse.data.data) {
            allMajors = backupResponse.data.data
          } else if (Array.isArray(backupResponse.data)) {
            allMajors = backupResponse.data
          }

          // 过滤出属于当前学校的专业
          majorsData = allMajors.filter(major =>
            major.schoolId === schoolId ||
            major.school?.id === schoolId
          )

          console.log('从备用接口获取的专业列表:', majorsData)
        }
      } catch (backupError) {
        console.error('备用接口也无法获取专业列表:', backupError)
      }
    }

    // 确保专业数据有正确的结构
    majors.value = majorsData.map(major => ({
      id: major.id,
      name: major.name || major.majorName || major.englishName
    }))

    console.log('成功获取专业列表:', majors.value)

    if (majors.value.length === 0) {
      ElMessage.warning(`未获取到学校 ${selectedSchool.value.name} 的专业列表`)
    }
  } catch (error) {
    console.error(`获取学校ID ${selectedSchool.value.id} 的专业列表出错:`, error)
    if (error.response) {
      console.error('服务器响应错误:', {
        status: error.response.status,
        data: error.response.data
      })
    } else if (error.request) {
      console.error('请求未收到响应:', error.request)
    }

    ElMessage.error(`网络错误，无法获取专业列表: ${error.message}`)
  } finally {
    loadingMajors.value = false
  }
}

// 处理学校选择变化
const handleSchoolChange = () => {
  // 防止报错：使用nextTick确保DOM更新后再执行操作
  nextTick(() => {
    // 重置学院和专业选择
    selectedFaculty.value = ''
    faculties.value = []
    selectedMajor.value = ''
    majors.value = []

    if (selectedSchool.value) {
      application.value.institution = selectedSchool.value.name
      application.value.schoolId = selectedSchool.value.id

      // 加载该学校的学院列表，使用setTimeout避免UI阻塞
      setTimeout(() => {
        fetchFacultiesBySchoolId()
      }, 50)
    } else {
      application.value.institution = ''
      application.value.schoolId = null
    }
  })
}

// 根据学校ID获取学院列表
const fetchFacultiesBySchoolId = async () => {
  if (!selectedSchool.value || !selectedSchool.value.id) {
    faculties.value = []
    return
  }

  try {
    loadingFaculties.value = true
    const schoolId = selectedSchool.value.id
    console.log(`开始获取学校ID ${schoolId} 的学院列表...`)

    const response = await axios.get(`/api/schools/${schoolId}/faculties`, {
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    })

    console.log(`学校学院API响应数据:`, response)

    let facultiesData = []

    // 处理可能的不同响应格式
    if (response.data) {
      if (response.data.code === 200 && response.data.data) {
        facultiesData = response.data.data
      } else if (Array.isArray(response.data)) {
        facultiesData = response.data
      } else if (typeof response.data === 'object') {
        facultiesData = Array.isArray(response.data) ? response.data : [response.data]
      }
    }

    // 确保学院数据有正确的结构
    faculties.value = facultiesData.map(faculty => ({
      id: faculty.id,
      name: faculty.name || faculty.englishName || '未命名学院'
    }))

    console.log('成功获取学院列表:', faculties.value)

    if (faculties.value.length === 0) {
      // 尝试一种备用方式
      try {
        console.log('尝试从备用接口获取学院列表...')
        const backupResponse = await axios.get('/api/faculties', {
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          }
        })

        if (backupResponse.data) {
          let allFaculties = []

          if (backupResponse.data.code === 200 && backupResponse.data.data) {
            allFaculties = backupResponse.data.data
          } else if (Array.isArray(backupResponse.data)) {
            allFaculties = backupResponse.data
          }

          // 过滤出属于当前学校的学院
          const filteredFaculties = allFaculties.filter(
            faculty => faculty.schoolId === schoolId ||
              (faculty.school && faculty.school.id === schoolId)
          )

          if (filteredFaculties.length > 0) {
            faculties.value = filteredFaculties.map(faculty => ({
              id: faculty.id,
              name: faculty.name || faculty.englishName || '未命名学院'
            }))
            console.log('从备用接口获取到学院数据:', faculties.value)
          }
        }
      } catch (backupError) {
        console.error('备用接口也无法获取学院列表:', backupError)
      }

      if (faculties.value.length === 0) {
        ElMessage.info(`未获取到学校 ${selectedSchool.value.name} 的学院列表，将直接获取专业`)
        // 如果没有学院数据，直接获取专业列表
        fetchMajorsBySchoolId()
      }
    }
  } catch (error) {
    console.error(`获取学校ID ${selectedSchool.value.id} 的学院列表出错:`, error)
    if (error.response) {
      console.error('服务器响应错误:', {
        status: error.response.status,
        data: error.response.data
      })
    } else if (error.request) {
      console.error('请求未收到响应:', error.request)
    }

    ElMessage.info(`无法获取学院列表，将直接获取专业`)
    // 如果获取学院失败，直接获取专业列表
    fetchMajorsBySchoolId()
  } finally {
    loadingFaculties.value = false
  }
}

// 处理学院选择变化
const handleFacultyChange = () => {
  // 使用nextTick确保DOM更新后再执行操作
  nextTick(() => {
    // 重置专业选择
    selectedMajor.value = ''
    majors.value = []

    if (selectedFaculty.value) {
      application.value.facultyId = selectedFaculty.value.id

      // 加载该学院的专业列表，使用setTimeout避免UI阻塞
      setTimeout(() => {
        fetchMajorsByFacultyId()
      }, 50)
    } else {
      application.value.facultyId = null
    }
  })
}

// 根据学院ID获取专业列表
const fetchMajorsByFacultyId = async () => {
  if (!selectedFaculty.value || !selectedFaculty.value.id) {
    majors.value = []
    return
  }

  try {
    loadingMajors.value = true
    const facultyId = selectedFaculty.value.id
    console.log(`开始获取学院ID ${facultyId} 的专业列表...`)

    const response = await axios.get(`/api/faculties/${facultyId}/majors`, {
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    })

    console.log(`学院专业API响应数据:`, response)

    let majorsData = []

    // 处理可能的不同响应格式
    if (response.data) {
      if (response.data.code === 200 && response.data.data) {
        majorsData = response.data.data
      } else if (Array.isArray(response.data)) {
        majorsData = response.data
      } else if (typeof response.data === 'object') {
        majorsData = Array.isArray(response.data) ? response.data : [response.data]
      }
    }

    // 确保专业数据有正确的结构
    majors.value = majorsData.map(major => ({
      id: major.id,
      name: major.name || major.majorName || major.englishName
    }))

    console.log('成功获取专业列表:', majors.value)

    if (majors.value.length === 0) {
      ElMessage.warning(`未获取到学院 ${selectedFaculty.value.name} 的专业列表`)
    }
  } catch (error) {
    console.error(`获取学院ID ${selectedFaculty.value.id} 的专业列表出错:`, error)
    if (error.response) {
      console.error('服务器响应错误:', {
        status: error.response.status,
        data: error.response.data
      })
    } else if (error.request) {
      console.error('请求未收到响应:', error.request)
    }

    ElMessage.error(`网络错误，无法获取专业列表: ${error.message}`)
  } finally {
    loadingMajors.value = false
  }
}

// 获取专业列表的统一入口
const fetchMajorsList = async () => {
  // 如果有选中的学院，通过学院获取专业
  if (selectedFaculty.value && selectedFaculty.value.id) {
    await fetchMajorsByFacultyId()
  } else {
    // 否则通过学校直接获取专业
    await fetchMajorsBySchoolId()
  }
}

// 学生数据相关
const studentSearchQuery = ref('')
const showStudentDropdown = ref(false)
const students = ref([]) // 学生列表
const loadingStudents = ref(false) // 加载状态
const studentError = ref(null) // 错误信息

// 获取所有学生
const fetchAllStudents = async () => {
  loadingStudents.value = true
  studentError.value = null

  try {
    const response = await axios.get('/api/student/admin/students')
    if (response.data && response.data.code === 200) {
      students.value = response.data.data || []

      // 打印第一个学生数据结构，检查是否包含userId
      if (students.value.length > 0) {
        console.log('学生数据结构示例:', JSON.stringify(students.value[0]))
      }

      // 确保每个学生对象包含所需的格式化日期字段
      students.value.forEach(student => {
        if (student.birthDate && !student.birthDateStr) {
          student.birthDateStr = new Date(student.birthDate).toISOString().split('T')[0]
        }
        // 确保school字段显示为currentSchool (API可能返回不同的结构)
        if (!student.school && student.currentSchool) {
          student.school = student.currentSchool
        }
      })
    } else {
      studentError.value = response.data?.message || '获取学生列表失败'
      ElMessage.error(studentError.value)
    }
  } catch (error) {
    console.error('获取学生列表出错:', error)
    studentError.value = '网络错误，无法获取学生列表'
    ElMessage.error(studentError.value)
  } finally {
    loadingStudents.value = false
  }
}

// 过滤学生列表
const filteredStudents = computed(() => {
  if (!studentSearchQuery.value) {
    return students.value
  }

  const query = studentSearchQuery.value.toLowerCase()
  return students.value.filter(student =>
    (student.name && student.name.toLowerCase().includes(query)) ||
    (student.englishName && student.englishName.toLowerCase().includes(query)) ||
    (student.school && student.school.toLowerCase().includes(query)) ||
    (student.currentSchool && student.currentSchool.toLowerCase().includes(query)) ||
    (student.email && student.email.toLowerCase().includes(query))
  )
})

// 搜索学生
const searchStudents = () => {
  showStudentDropdown.value = true
}

// 选择学生
const selectStudent = (student) => {
  // 确保学生的日期字段有字符串表示
  if (student.birthDate && !student.birthDateStr) {
    student.birthDateStr = new Date(student.birthDate).toISOString().split('T')[0]
  }

  application.value.student = { ...student }
  showStudentDropdown.value = false
  studentSearchQuery.value = ''
}

// 清除已选学生
const clearSelectedStudent = () => {
  application.value.student = null
}

// 将日期字符串转换为后端期望的格式 yyyy-MM-dd'T'HH:mm:ss.SSSXXX
const formatDateForBackend = (dateStr) => {
  if (!dateStr) return null;

  try {
    const date = new Date(dateStr);
    // 检查日期是否有效
    if (isNaN(date.getTime())) {
      return null;
    }
    // 转换为ISO格式，确保包含时区信息
    return date.toISOString();
  } catch (error) {
    console.error('日期格式转换出错:', error);
    return null;
  }
}

// 保存申请信息
const saveApplication = () => {
  if (isFormValid.value) {
    try {
      // 统一数据格式，确保字段命名一致
      const applicationData = {
        // 基本信息
        id: application.value.id,
        status: application.value.status,
        deadline: formatDateForBackend(application.value.deadlineStr),
        applicationDeadline: formatDateForBackend(application.value.deadlineStr), // 兼容后端字段
        entryDate: formatDateForBackend(application.value.entryDateStr),
        consultant: application.value.consultant,
        consultantName: application.value.consultant, // 兼容后端字段
        materialsProgress: application.value.materialsProgress || 0,
        degree: application.value.degree,
        round: application.value.round,

        // 院校信息
        institution: selectedSchool.value?.name || application.value.institution,
        schoolName: selectedSchool.value?.name || application.value.institution, // 兼容后端字段
        schoolId: selectedSchool.value?.id,
        faculty: selectedFaculty.value?.name,
        facultyId: selectedFaculty.value?.id,
        program: selectedMajor.value?.name || application.value.program,
        majorName: selectedMajor.value?.name || application.value.program, // 兼容后端字段
        majorId: selectedMajor.value?.id,
        country: application.value.country,

        // 学生信息
        student: application.value.student ? {
          ...application.value.student,
          birthDate: application.value.student.birthDateStr ?
            formatDateForBackend(application.value.student.birthDateStr) : null
        } : null,
        studentId: application.value.student?.id,
        studentName: application.value.student?.name,
        studentEmail: application.value.student?.email,
        studentPhone: application.value.student?.phone,
        studentSchool: application.value.student?.currentSchool,
        studentMajor: application.value.student?.major,

        // 其他信息
        notes: application.value.notes,
        courseCode: application.value.courseCode,
        studyDuration: application.value.studyDuration,
        campus: application.value.campus,
        creditReduction: application.value.creditReduction,
        applicationLink: application.value.applicationLink
      };

      // 记录提交的数据
      console.log("提交的申请数据:", applicationData);

      // 检查必要的ID字段是否存在
      if (!applicationData.schoolId) {
        ElMessage.warning('请选择一所学校');
        return;
      }

      if (!applicationData.majorId) {
        ElMessage.warning('请选择一个专业');
        return;
      }

      if (!applicationData.student) {
        ElMessage.warning('请选择一个学生');
        return;
      }

      // 发送数据
      emit('save', applicationData);
    } catch (error) {
      console.error('保存申请信息时发生错误:', error);
      ElMessage.error('保存失败，请检查表单数据是否正确');
    }
  } else {
    ElMessage.warning('请完成所有必填字段');
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

// 当前步骤验证
const isCurrentStepValid = computed(() => {
  switch (progressStep.value) {
    case 0: // 基本信息
      return application.value.status &&
        application.value.deadlineStr &&
        application.value.consultant &&
        application.value.degree &&
        application.value.round
    case 1: // 院校信息
      return selectedSchool.value &&
        selectedMajor.value &&
        application.value.country
    case 2: // 学生信息
      return application.value.student !== null
    case 3: // 备注信息
      return true
    default:
      return true
  }
})

// 整体表单验证
const isFormValid = computed(() => {
  return application.value.status &&
    application.value.deadlineStr &&
    application.value.consultant &&
    application.value.degree &&
    application.value.round &&
    selectedSchool.value &&
    selectedMajor.value &&
    application.value.country &&
    application.value.student !== null
})

// 监听应用状态变化，初始化已选择的学校和专业
watch(() => application.value.schoolId, (newSchoolId) => {
  if (newSchoolId && schools.value.length > 0) {
    // 如果有schoolId且学校列表已加载，则设置选中的学校
    const foundSchool = schools.value.find(s => s.id === newSchoolId);
    if (foundSchool) {
      selectedSchool.value = foundSchool;
      // 有学校ID但没有专业列表时，加载专业列表
      if (majors.value.length === 0) {
        fetchMajorsBySchoolId();
      }
    }
  }
}, { immediate: true });

// 监听专业列表变化，初始化已选择的专业
watch([() => application.value.majorId, () => majors.value], ([newMajorId, newMajors]) => {
  if (newMajorId && newMajors.length > 0) {
    // 如果有majorId且专业列表已加载，则设置选中的专业
    const foundMajor = newMajors.find(m => m.id === newMajorId);
    if (foundMajor) {
      selectedMajor.value = foundMajor;
    }
  }
}, { immediate: true });

// 处理专业选择变化
const handleMajorChange = () => {
  if (selectedMajor.value) {
    application.value.program = selectedMajor.value.name
    application.value.majorId = selectedMajor.value.id
  } else {
    application.value.program = ''
    application.value.majorId = null
  }
}

// 在组件挂载时获取学生列表和学校列表
onMounted(() => {
  try {
    // 获取真实学生列表
    fetchAllStudents()

    // 获取学校列表
    setTimeout(() => {
      fetchSchools().catch(error => {
        console.error('自动获取学校列表失败:', error)
        ElMessage.warning('加载学校列表失败，请点击刷新按钮重试')
      })
    }, 100) // 延迟执行，避免页面渲染卡顿
  } catch (error) {
    console.error('组件挂载时数据获取出错:', error)
  }

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

.subsection-title {
  font-size: 14px;
  font-weight: 600;
  margin: 24px 0 16px 0;
  padding-bottom: 8px;
  color: #333;
  border-bottom: 1px dashed #f0f0f0;
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

.form-textarea {
  min-height: 80px;
  resize: vertical;
}

.select-wrapper {
  position: relative;
  z-index: 1;
  /* 保证下拉框正常显示 */
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
  z-index: 2;
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

.search-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.search-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.btn-refresh {
  padding: 8px 16px;
  border-radius: 8px;
  background-color: transparent;
  border: 1px solid #d9d9d9;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-refresh:hover {
  border-color: #1890ff;
  color: #1890ff;
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
  border-bottom: 1px solid #f0f0f0;
}

.student-option:last-child {
  border-bottom: none;
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
  margin-bottom: 24px;
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

.student-info-form {
  margin-top: 24px;
  padding: 16px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.empty-student-notice {
  margin-top: 30px;
  text-align: center;
  padding: 40px 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  color: #999;
}

.notice-icon {
  width: 48px;
  height: 48px;
  margin: 0 auto 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f0f0;
  border-radius: 24px;
}

.notice-icon svg {
  width: 24px;
  height: 24px;
  color: #999;
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

.refresh-icon {
  width: 14px;
  height: 14px;
  margin-right: 4px;
}

.refresh-icon.loading {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

.loading-container,
.error-container,
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px 16px;
  text-align: center;
  color: #666;
}

.loading-spinner {
  width: 24px;
  height: 24px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #1890ff;
  border-radius: 50%;
  margin-bottom: 12px;
  animation: spin 1s linear infinite;
}

.error-icon,
.empty-icon {
  width: 24px;
  height: 24px;
  margin-bottom: 12px;
  color: #ff4d4f;
}

.empty-icon {
  color: #999;
}

.btn-retry {
  margin-top: 12px;
  padding: 6px 12px;
  background-color: #f5f5f5;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  color: #666;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s;
}

.btn-retry:hover {
  background-color: #e6f7ff;
  border-color: #1890ff;
  color: #1890ff;
}

.no-results {
  padding: 16px;
  text-align: center;
  color: #666;
  font-size: 14px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.no-results small {
  color: #999;
  font-size: 12px;
}

.student-list {
  display: none;
  /* 移除这个嵌套的滚动容器 */
}

/* 添加加载状态样式 */
.loading-field {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  background-color: #f5f5f5;
  color: #666;
}

.loading-spinner-sm {
  width: 16px;
  height: 16px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #1890ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.btn-link {
  background: none;
  border: none;
  color: #1890ff;
  padding: 4px 8px;
  font-size: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 4px;
}

.btn-link:hover {
  text-decoration: underline;
  background-color: #f0f7ff;
}

.btn-icon {
  width: 14px;
  height: 14px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

/* 添加form-select的样式修复 */
.form-select {
  width: 100%;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid #d9d9d9;
  font-size: 14px;
  transition: all 0.3s;
  background-color: white;
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
  background-image: none !important;
  /* 防止浏览器默认箭头 */
  padding-right: 32px;
  /* 为自定义箭头留出空间 */
}

.form-select:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.form-select:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
  opacity: 0.7;
}
</style>