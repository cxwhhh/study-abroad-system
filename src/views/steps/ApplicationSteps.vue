<template>
  <div class="application-steps" v-loading="isLoading">
    <!-- 添加导航栏 已经弃用-->
    <nav-bar></nav-bar>
    
    <div class="student-info-section">
      <div class="info-row">
        <div class="info-item">
          <span class="label">申请编号:</span>
          <span class="value">{{ studentInfo.id }}</span>
        </div>
        <div class="info-item">
          <span class="label">姓名:</span>
          <span class="value">{{ studentInfo.name }}</span>
        </div>
        <div class="info-item">
          <span class="label">出生日期:</span>
          <span class="value">{{ formatDate(studentInfo.birthDate) }}</span>
        </div>
        <div class="info-item">
          <span class="label">学校:</span>
          <span class="value">{{ studentInfo.currentSchool }}</span>
        </div>
        <div class="info-item">
          <span class="label">邮箱:</span>
          <span class="value">{{ studentInfo.email }}</span>
        </div>
        <div class="info-item">
          <span class="label">联系电话:</span>
          <span class="value">{{ studentInfo.phone }}</span>
        </div>
      </div>
    </div>
  
    <!-- 留学申请流程部分：展示申请步骤 -->
    <div class="process-container">
      <div class="process-steps">
        <!-- 步骤列表 -->
        <div class="steps-wrapper">
          <div class="step" 
               v-for="(step, index) in processSteps" 
               :key="index" 
               @click="showStepGuide(step)"
               :class="{ 'is-completed': index < currentStep }">
            <div class="step-box" :data-step="index + 1">
              <el-icon><component :is="step.icon" /></el-icon>
            </div>
            <!-- 连接线 -->
          <div v-if="index < steps.length - 2" class="step-line"></div>
            <div class="step-content">
              <h3>{{ step.title }}</h3>
              <div class="timing">{{ step.timing }}</div>
              <div class="step-actions">
                <el-button type="primary" size="small">
                  了解更多
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

   <!-- 3. 申请信息容器 -->
  <div class="application-container">
    <!-- 状态标签页 -->
    <div class="status-tabs">
      <div class="tab" 
           :class="{ active: currentView === 'submitted' && !isAddingCountry }"
           @click="switchView('submitted')">
        已申请
      </div>
      <div class="tab add-tab" 
           :class="{ active: isAddingCountry }"
           @click="handleAddCountryClick">
        <el-icon><Plus /></el-icon> 新增国家
      </div>
    </div>

    <!-- 添加国家对话框 -->
    <el-dialog
      v-model="countryDialogVisible"
      title="选择申请国家"
      width="30%">
      <el-form :model="countryForm">
        <el-form-item label="选择国家">
          <el-select v-model="countryForm.country" placeholder="请选择国家">
            <el-option
              v-for="country in availableCountries"
              :key="country.id"
              :label="country.name"
              :value="country.id"
              :disabled="selectedCountries.some(c => c.id === country.id)">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="countryDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAddCountry">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 已提交的申请列表 -->
    <div v-if="currentView === 'submitted' && !isAddingCountry && submittedApplications.length > 0" 
         class="submitted-applications">
      <div class="application-list">
        <div class="application-card" 
             v-for="app in submittedApplications" 
             :key="app.id"
             shadow="hover">
          <div class="card-header" @click="toggleDetails(app.id)">
            <div class="basic-info">
              <div class="info-group">
                <el-tag class="country-tag" size="small">{{ app.country }}</el-tag>
                <span class="school-name">{{ app.schoolName }}</span>
                <span class="major-name">{{ app.majorName }}</span>
                <el-tag :type="getStatusType(app.applicationStatus)" size="small">
                  {{ app.applicationStatus }}
                </el-tag>
              </div>
            </div>
            <div class="expand-button">
              <el-icon :class="{ 'is-expanded': expandedIds.includes(app.id) }">
                <ArrowDown />
              </el-icon>
            </div>
          </div>
          
          <el-collapse-transition>
            <div v-show="expandedIds.includes(app.id)" class="application-details">
              <div class="details-list">
                <div class="detail-row">
                  <span class="label">申请学位:</span>
                  <span class="value">{{ app.degree }}</span>
                </div>
                <div class="detail-row">
                  <span class="label">专业代码:</span>
                  <span class="value">{{ app.code }}</span>
                </div>
                <div class="detail-row">
                  <span class="label">入学时间:</span>
                  <span class="value">{{ app.entryDate }}</span>
                </div>
                <div class="detail-row">
                  <span class="label">学制:</span>
                  <span class="value">{{ app.duration }}</span>
                </div>
                <div class="detail-row">
                  <span class="label">校区:</span>
                  <span class="value">{{ app.campus }}</span>
                </div>
                <div class="detail-row">
                  <span class="label">提交时间:</span>
                  <span class="value">{{ formatDate(app.submitTime) }}</span>
                </div>
                <div class="detail-row">
                  <span class="label">申请链接:</span>
                  <a :href="app.link" target="_blank" class="link">查看详情</a>
                </div>
              </div>
            </div>
          </el-collapse-transition>
        </div>
      </div>
    </div>

    <!-- 新增国家后的表单部分 -->
    <div v-if="isAddingCountry && countryForm.country" class="new-application-form">
      <div class="application-card" shadow="hover">
        <div class="card-header">
          <div class="basic-info">
            <div class="info-group">
              <el-tag class="country-tag" size="small">{{ availableCountries.find(c => c.id === countryForm.country)?.name }}</el-tag>
              <el-select v-model="selectedSchool" placeholder="请选择学校" @change="handleSchoolChange" style="width: 240px;">
                <el-option
                  v-for="school in availableSchools"
                  :key="school.id"
                  :label="school.name"
                  :value="school.id">
                  <div class="school-option">
                    <span>{{ school.name }}</span>
                    <span class="school-subtitle">{{ school.englishName }}</span>
                  </div>
                </el-option>
              </el-select>
              <el-select v-if="selectedSchool" 
                        v-model="selectedMajor" 
                        placeholder="请选择专业" 
                        @change="handleMajorChange"
                        style="width: 300px;">
                <el-option
                  v-for="major in availableMajors"
                  :key="major.id"
                  :label="major.name"
                  :value="major.id">
                  <div class="school-option">
                    <span>{{ major.name }}</span>
                    <span class="school-subtitle">{{ major.englishName }}</span>
                  </div>
                </el-option>
              </el-select>
            </div>
          </div>
        </div>

        <!-- 专业详情 -->
        <el-collapse-transition>
          <div v-if="selectedMajor" class="application-details">
            <div class="details-list">
              <div class="detail-row">
                <span class="label">学位类型:</span>
                <span class="value">{{ majorDetails.degree }}</span>
              </div>
              <div class="detail-row">
                <span class="label">课程代码:</span>
                <span class="value">{{ majorDetails.code }}</span>
              </div>
              <div class="detail-row">
                <span class="label">学制:</span>
                <span class="value">{{ majorDetails.duration }}</span>
              </div>
              <div class="detail-row">
                <span class="label">校区:</span>
                <span class="value">{{ majorDetails.campus }}</span>
              </div>
              <div class="detail-row">
                <span class="label">申请截止:</span>
                <span class="value">{{ majorDetails.applicationDeadline }}</span>
              </div>
              <div class="detail-row">
                <span class="label">申请链接:</span>
                <a :href="majorDetails.link" target="_blank" class="link">查看详情</a>
              </div>
              <div class="detail-row" style="justify-content: flex-end; padding-top: 16px;">
                <el-button type="primary" @click="handleSubmitApplication" :loading="submitting">
                  提交申请
                </el-button>
              </div>
            </div>
          </div>
        </el-collapse-transition>
      </div>
    </div>
  </div>
    <footer-bar></footer-bar>
  </div>
</template>

<script setup>
// 导入所需的组件和图标
import { ref, nextTick, reactive, computed, onMounted, onUnmounted, watch } from 'vue'
import { Check, Edit, Document, Upload, Plus, ArrowDown, Guide, ChatDotRound, Service, Money } from '@element-plus/icons-vue'
import NavBar from '../../components/NavBar.vue'
import FooterBar from '../../components/FooterBar.vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'
import axios from 'axios'

// 在script setup部分的开头添加
const loading = ref(false)
const isLoading = ref(false)

// 定义申请步骤数据
const steps = ref([
  { title: '获取留学方案', completed: true },
  { title: '确定申请院校', completed: true },
  { title: '提交申请资料', completed: true },
  { title: '获取院校offer', completed: true },
  { title: '接受offer缴费', completed: false },
  { title: '提交签证资料', completed: false },
  { title: '获取签证结果', completed: false },
  { title: '咨询境外服务', completed: false }
])

// 当前进行到第5步（索引为4）
const currentStep = ref(4)

// 学生信息
const studentInfo = ref({
  id: '',
  name: '',
  birthDate: '',
  currentSchool: '',
  email: '',
  phone: '',
  englishName: '',
  gender: '',
  nationality: '',
  passportNo: '',
  wechat: '',
  major: '',
  gpa: ''
})

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)

// 表单数据
const formData = reactive({
  studentId: '',
  name: '',
  birthday: '',
  level: '',
  email: ''
})

// 表单验证规则
const rules = {
  studentId: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  birthday: [{ required: true, message: '请选择生日', trigger: 'change' }],
  level: [{ required: true, message: '请选择学段', trigger: 'change' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

// 显示添加对话框
const showAddStudentDialog = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = () => {
  dialogType.value = 'edit'
  Object.assign(formData, studentInfo.value)
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate((valid) => {
    if (valid) {
      studentInfo.value = { ...formData }
      dialogVisible.value = false
      // 这里可以添加保存到后端的逻辑
    }
  })
}

// 处理编辑点击事件
const handleEdit = async () => {
  try {
    await router.push('/profiles/edit')
    nextTick(() => {
      window.scrollTo(0, 0)
    })
  } catch (error) {
    console.error('Navigation failed:', error)
  }
}
// 申请流程步骤数据
const processSteps = ref([
  {
    icon: 'Guide',
    title: '前期准备',
    description: '确定目标和准备考试',
    timing: '申请前12-18个月'
  },
  {
    icon: 'Document',
    title: '材料准备',
    description: '准备申请材料',
    timing: '申请前6-12个月'
  },
  {
    icon: 'Upload',
    title: '申请提交',
    description: '提交院校申请',
    timing: '申请季'
  },
  {
    icon: 'ChatDotRound',
    title: '面试录取',
    description: '准备面试和确认录取',
    timing: '申请后2-3个月'
  },
  {
    icon: 'Check',
    title: '签证准备',
    description: '签证行前准备',
    timing: '入学前2-3个月'
  },
  {
    icon: 'Service',
    title: '学校助手',
    description: '留学生活指导',
    timing: '留学期间'
  },
  {
    icon: 'Money',
    title: '入学奖励',
    description: '入学奖励计划',
    timing: '入学后'
  }
])

const router = useRouter()

// 检查登录状态
const checkLoginStatus = () => {
  const userInfo = localStorage.getItem('userInfo')
  if (!userInfo) {
    ElMessage.warning('请先登录')
    router.push({
      path: '/login',
      query: { redirect: '/application-steps' }
    })
    return false
  }
  return true
}

// 修改 showStepGuide 方法
const showStepGuide = async (step) => {
  const stepIndex = processSteps.value.findIndex(s => s.title === step.title)
  if (stepIndex >= 0) {
    const paths = ['preparation', 'materials', 'submission', 'interview', 'visa', 'assistant', 'rebate']
    try {
      isLoading.value = true
      await router.push(`/steps/${paths[stepIndex]}`)
      await nextTick()
    } catch (error) {
      console.error('Navigation failed:', error)
    } finally {
      isLoading.value = false
    }
  }
}

const goToSchools = async (country) => {
  try {
    await router.push(`/schools/${country.toLowerCase()}`)
    nextTick(() => {
      window.scrollTo(0, 0)
    })
  } catch (error) {
    console.error('Navigation failed:', error)
  }
}

// 添加 learnMore 方法
const learnMore = (item) => {
  router.push(`/countries/${item.title.replace('留学', '').toLowerCase()}`)
}

// 修改国家相关逻辑
const selectedCountries = ref([])
const activeCountry = ref(null)
const countryDialogVisible = ref(false)
const countryForm = reactive({ country: '' })
const availableCountries = [
  { id: 1, name: '澳大利亚' },
  { id: 2, name: '英国' },
  { id: 3, name: '美国' },
  { id: 4, name: '加拿大' },
  { id: 5, name: '新西兰' },
  { id: 6, name: '香港' },
  { id: 7, name: '新加坡' }
]

// 修改获取用户申请的国家列表函数
const fetchUserApplications = async () => {
  try {
    isLoading.value = true
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    
    if (!userInfo.id) {
      console.warn('未找到用户ID')
      ElMessage.warning('请先登录')
      router.push('/login')
      return
    }

    const response = await request.get('/application-school/list', {
      params: {
        userId: userInfo.id
      }
    })

    if (response.code === 200) {
      submittedApplications.value = response.data
      
      // 更新学生信息
      if (response.data.length > 0) {
        const firstApp = response.data[0]
        studentInfo.value = {
          id: firstApp.studentId,
          name: firstApp.studentName,
          birthDate: firstApp.birthDate,
          currentSchool: firstApp.currentSchool,
          email: firstApp.studentEmail,
          phone: firstApp.phone,
          englishName: firstApp.englishName,
          gender: firstApp.gender,
          nationality: firstApp.nationality,
          passportNo: firstApp.passportNo,
          wechat: firstApp.wechat,
          major: firstApp.studentMajor,
          gpa: firstApp.gpa
        }

        // 更新已选择的国家列表
        const countries = [...new Set(response.data.map(app => app.country))]
        selectedCountries.value = countries.map(country => ({
          id: country,
          name: country,
          status: '已申请'
        }))

        // 如果有申请记录，设置当前活动的国家
        if (selectedCountries.value.length > 0) {
          activeCountry.value = selectedCountries.value[0].id
        }
      }
    }
  } catch (error) {
    console.error('获取申请信息失败:', error)
    ElMessage.error('获取申请信息失败')
  } finally {
    isLoading.value = false
  }
}

// 添加监听器，当selectedCountries发生变化时进行处理
watch(selectedCountries, (newVal) => {
  if (newVal.length === 0) {
    activeCountry.value = null
  } else if (!activeCountry.value) {
    activeCountry.value = newVal[0].id
  }
}, { deep: true })

// 修改学校相关数据
const schoolsData = {
  1: [ // 澳大利亚学校
    { 
      id: 101, 
      name: '悉尼大学', 
      englishName: 'The University of Sydney',
      majors: [
        {
          id: 1001,
          name: '人力资源管理与劳资关系硕士',
          englishName: 'Master of Human Resource Management and Industrial Relations',
          degree: 'Master',
          code: 'MHRM-01',
          entryDate: '2025-02',
          duration: '2年',
          campus: 'Camperdown/Darlington',
          creditExemption: '否',
          link: 'https://www.sydney.edu.au/courses/master-of-hrm.html',
          applicationDeadline: '2024-11-30'
        },
        {
          id: 1002,
          name: '商业分析硕士',
          englishName: 'Master of Business Analytics',
          degree: 'Master',
          code: 'MBA-01',
          entryDate: '2025-02',
          duration: '1.5年',
          campus: 'Camperdown/Darlington',
          creditExemption: '是',
          link: 'https://www.sydney.edu.au/courses/master-of-business-analytics.html',
          applicationDeadline: '2024-11-30'
        },
        {
          id: 1003,
          name: '数据科学硕士',
          englishName: 'Master of Data Science',
          degree: 'Master',
          code: 'MDS-01',
          entryDate: '2025-02',
          duration: '2年',
          campus: 'Camperdown/Darlington',
          creditExemption: '否',
          link: 'https://www.sydney.edu.au/courses/master-of-data-science.html',
          applicationDeadline: '2024-11-30'
        }
      ]
    },
    {
      id: 102,
      name: '墨尔本大学',
      englishName: 'The University of Melbourne',
      majors: [
        {
          id: 2001,
          name: '信息技术硕士',
          englishName: 'Master of Information Technology',
          degree: 'Master',
          code: 'MIT-01',
          entryDate: '2025-02',
          duration: '2年',
          campus: 'Parkville',
          creditExemption: '否',
          link: 'https://study.unimelb.edu.au/find/courses/graduate/master-of-information-technology/',
          applicationDeadline: '2024-11-15'
        },
        {
          id: 2002,
          name: '会计学硕士',
          englishName: 'Master of Accounting',
          degree: 'Master',
          code: 'MACC-01',
          entryDate: '2025-02',
          duration: '2年',
          campus: 'Parkville',
          creditExemption: '是',
          link: 'https://study.unimelb.edu.au/find/courses/graduate/master-of-accounting/',
          applicationDeadline: '2024-11-15'
        }
      ]
    },
    {
      id: 103,
      name: '澳大利亚国立大学',
      englishName: 'Australian National University',
      majors: [
        {
          id: 3001,
          name: '计算机科学硕士',
          englishName: 'Master of Computing',
          degree: 'Master',
          code: 'MCOMP-01',
          entryDate: '2025-02',
          duration: '2年',
          campus: 'Acton',
          creditExemption: '否',
          link: 'https://programsandcourses.anu.edu.au/program/MCOMP',
          applicationDeadline: '2024-10-31'
        }
      ]
    }
  ],
  2: [ // 英国学校
    {
      id: 201,
      name: '剑桥大学',
      englishName: 'University of Cambridge',
      majors: [
        {
          id: 4001,
          name: '工程学硕士',
          englishName: 'Master of Engineering',
          degree: 'Master',
          code: 'MENG-01',
          entryDate: '2025-09',
          duration: '1年',
          campus: 'Main Campus',
          creditExemption: '否',
          link: 'https://www.cam.ac.uk/engineering',
          applicationDeadline: '2024-12-15'
        },
        {
          id: 4002,
          name: '金融学硕士',
          englishName: 'Master of Finance',
          degree: 'Master',
          code: 'MFIN-01',
          entryDate: '2025-09',
          duration: '1年',
          campus: 'Main Campus',
          creditExemption: '否',
          link: 'https://www.jbs.cam.ac.uk/programmes/master-of-finance/',
          applicationDeadline: '2024-12-15'
        }
      ]
    },
    {
      id: 202,
      name: '牛津大学',
      englishName: 'University of Oxford',
      majors: [
        {
          id: 5001,
          name: '计算机科学硕士',
          englishName: 'MSc in Computer Science',
          degree: 'Master',
          code: 'MCS-01',
          entryDate: '2025-09',
          duration: '1年',
          campus: 'Main Campus',
          creditExemption: '否',
          link: 'https://www.cs.ox.ac.uk/teaching/MSCinCS/',
          applicationDeadline: '2024-12-01'
        }
      ]
    }
  ],
  3: [ // 美国学校
    {
      id: 301,
      name: '哈佛大学',
      englishName: 'Harvard University',
      majors: [
        {
          id: 6001,
          name: '公共政策硕士',
          englishName: 'Master of Public Policy',
          degree: 'Master',
          code: 'MPP-01',
          entryDate: '2025-09',
          duration: '2年',
          campus: 'Cambridge',
          creditExemption: '否',
          link: 'https://www.hks.harvard.edu/educational-programs/masters-programs/master-public-policy',
          applicationDeadline: '2024-12-01'
        },
        {
          id: 6002,
          name: '商业管理硕士',
          englishName: 'Master of Business Administration',
          degree: 'Master',
          code: 'MBA-01',
          entryDate: '2025-09',
          duration: '2年',
          campus: 'Cambridge',
          creditExemption: '否',
          link: 'https://www.hbs.edu/mba/',
          applicationDeadline: '2024-12-01'
        }
      ]
    },
    {
      id: 302,
      name: '斯坦福大学',
      englishName: 'Stanford University',
      majors: [
        {
          id: 7001,
          name: '电子工程硕士',
          englishName: 'Master of Science in Electrical Engineering',
          degree: 'Master',
          code: 'MSEE-01',
          entryDate: '2025-09',
          duration: '2年',
          campus: 'Main Campus',
          creditExemption: '否',
          link: 'https://ee.stanford.edu/academics/graduate-degree-programs',
          applicationDeadline: '2024-12-15'
        }
      ]
    }
  ],
  4: [ // 加拿大学校
    {
      id: 401,
      name: '多伦多大学',
      englishName: 'University of Toronto',
      majors: [
        {
          id: 8001,
          name: '金融学硕士',
          englishName: 'Master of Finance',
          degree: 'Master',
          code: 'MF-01',
          entryDate: '2025-09',
          duration: '1年',
          campus: 'St. George',
          creditExemption: '否',
          link: 'https://www.rotman.utoronto.ca/Degrees/MastersPrograms/MasterOfFinance',
          applicationDeadline: '2024-11-30'
        },
        {
          id: 8002,
          name: '数据分析硕士',
          englishName: 'Master of Data Analytics',
          degree: 'Master',
          code: 'MDA-01',
          entryDate: '2025-09',
          duration: '1年',
          campus: 'St. George',
          creditExemption: '否',
          link: 'https://www.rotman.utoronto.ca/Degrees/MastersPrograms/MasterOfManagementAnalytics',
          applicationDeadline: '2024-11-30'
        }
      ]
    },
    {
      id: 402,
      name: '英属哥伦比亚大学',
      englishName: 'University of British Columbia',
      majors: [
        {
          id: 9001,
          name: '工商管理硕士',
          englishName: 'Master of Business Administration',
          degree: 'Master',
          code: 'MBA-01',
          entryDate: '2025-09',
          duration: '2年',
          campus: 'Vancouver',
          creditExemption: '否',
          link: 'https://www.sauder.ubc.ca/programs/masters-degrees/mba-programs',
          applicationDeadline: '2024-11-30'
        }
      ]
    }
  ],
  5: [ // 新西兰学校
    {
      id: 501,
      name: '奥克兰大学',
      englishName: 'The University of Auckland',
      majors: [
        {
          id: 10001,
          name: '商业管理硕士',
          englishName: 'Master of Management',
          degree: 'Master',
          code: 'MM-01',
          entryDate: '2025-03',
          duration: '1.5年',
          campus: 'City Campus',
          creditExemption: '否',
          link: 'https://www.auckland.ac.nz/en/study/study-options/find-a-study-option/master-of-management-mmgt.html',
          applicationDeadline: '2024-10-31'
        },
        {
          id: 10002,
          name: '专业会计硕士',
          englishName: 'Master of Professional Accounting',
          degree: 'Master',
          code: 'MPA-01',
          entryDate: '2025-03',
          duration: '1.5年',
          campus: 'City Campus',
          creditExemption: '否',
          link: 'https://www.auckland.ac.nz/en/study/study-options/find-a-study-option/master-of-professional-accounting-mprofacctg.html',
          applicationDeadline: '2024-10-31'
        }
      ]
    }
  ],
  6: [ // 香港学校
    {
      id: 601,
      name: '香港大学',
      englishName: 'The University of Hong Kong',
      majors: [
        {
          id: 11001,
          name: '计算机科学硕士',
          englishName: 'Master of Computer Science',
          degree: 'Master',
          code: 'MCS-01',
          entryDate: '2025-09',
          duration: '1年',
          campus: 'Main Campus',
          creditExemption: '否',
          link: 'https://www.cs.hku.hk/msc/',
          applicationDeadline: '2025-12-31'
        },
        {
          id: 11002,
          name: '金融学硕士',
          englishName: 'Master of Finance',
          degree: 'Master',
          code: 'MFin-01',
          entryDate: '2025-09',
          duration: '1年',
          campus: 'Main Campus',
          creditExemption: '否',
          link: 'https://www.fbe.hku.hk/mfin/',
          applicationDeadline: '2024-12-31'
        }
      ]
    },
    {
      id: 602,
      name: '香港科技大学',
      englishName: 'The Hong Kong University of Science and Technology',
      majors: [
        {
          id: 12001,
          name: '信息系统管理硕士',
          englishName: 'Master of Science in Information Systems Management',
          degree: 'Master',
          code: 'MISM-01',
          entryDate: '2025-09',
          duration: '1年',
          campus: 'Clear Water Bay',
          creditExemption: '否',
          link: 'https://www.msism.ust.hk/',
          applicationDeadline: '2024-12-31'
        }
      ]
    }
  ],
  7: [ // 新加坡学校
    {
      id: 701,
      name: '新加坡国立大学',
      englishName: 'National University of Singapore',
      majors: [
        {
          id: 13001,
          name: '工商管理硕士',
          englishName: 'Master of Business Administration',
          degree: 'Master',
          code: 'MBA-01',
          entryDate: '2025-08',
          duration: '1.5年',
          campus: 'Kent Ridge',
          creditExemption: '否',
          link: 'https://mba.nus.edu.sg/',
          applicationDeadline: '2024-11-30'
        },
        {
          id: 13002,
          name: '数据科学与机器学习硕士',
          englishName: 'Master of Science in Data Science and Machine Learning',
          degree: 'Master',
          code: 'MDSML-01',
          entryDate: '2025-08',
          duration: '1.5年',
          campus: 'Kent Ridge',
          creditExemption: '否',
          link: 'https://www.comp.nus.edu.sg/programmes/pg/msdsml/',
          applicationDeadline: '2024-11-30'
        }
      ]
    },
    {
      id: 702,
      name: '南洋理工大学',
      englishName: 'Nanyang Technological University',
      majors: [
        {
          id: 14001,
          name: '人工智能硕士',
          englishName: 'Master of Science in Artificial Intelligence',
          degree: 'Master',
          code: 'MAI-01',
          entryDate: '2025-08',
          duration: '1年',
          campus: 'Main Campus',
          creditExemption: '否',
          link: 'https://www.ntu.edu.sg/education/graduate-programme/master-of-science-in-artificial-intelligence',
          applicationDeadline: '2024-11-30'
        }
      ]
    }
  ]
}

// 添加变量声明
const selectedSchool = ref(null)
const availableSchools = ref([])
const selectedMajor = ref(null)
const availableMajors = ref([])
const majorDetails = ref({})

// 获取已提交的申请列表
const fetchSubmittedApplications = async () => {
  try {
    if (!checkLoginStatus()) return

    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const userId = userInfo.id
    console.log('Fetching applications for userId:', userId)
    
    const response = await request.get('/application-school/list', {
      params: { userId },
      headers: {
        'Accept': 'application/json',
      }
    })
    
    if (response.code === 200) {
      submittedApplications.value = response.data
    } else {
      ElMessage.warning(response.message || '获取申请记录失败')
    }
  } catch (error) {
    console.error('获取已提交申请失败:', error)
    ElMessage.error('获取申请记录失败')
  }
}

// 添加监听器来处理 activeCountry 的变化
watch(activeCountry, () => {
  if (activeCountry) {
    fetchSubmittedApplications()
  }
})

// 修改提交申请的方法
const handleSubmitApplication = async () => {
  try {
    if (!checkLoginStatus()) return

    // 验证必要的数据是否已选择
    if (!selectedSchool.value || !selectedMajor.value || !activeCountry.value) {
      ElMessage.warning('请选择学校和专业')
      return
    }

    // 验证学生信息
    if (!studentInfo.value) {
      ElMessage.warning('请先添加学生信息')
      return
    }

    submitting.value = true
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    
    const school = availableSchools.value.find(s => s.id === selectedSchool.value)
    const major = school?.majors.find(m => m.id === selectedMajor.value)
    
    if (!school || !major) {
      ElMessage.warning('无法获取学校或专业信息')
      return
    }

    const plainApplicationData = {
      userId: userInfo.id,
      studentId: String(studentInfo.value.id || ''),
      schoolId: String(selectedSchool.value || ''),
      majorId: String(selectedMajor.value || ''),
      countryId: String(activeCountry.value || ''),
      schoolName: school.name,
      majorName: major.name,
      country: availableCountries.find(c => c.id === activeCountry.value)?.name || '',
      major: major.name,
      degree: major.degree,
      code: major.code,
      entryDate: major.entryDate,
      duration: major.duration,
      campus: major.campus,
      creditExemption: major.creditExemption,
      applicationStatus: 'submitted',
      submitTime: new Date().toISOString(),
      link: major.link,
      applicationDeadline: major.applicationDeadline,
      createTime: new Date().toISOString(),
      studentName: String(studentInfo.value.name || ''),
      studentEmail: String(studentInfo.value.email || ''),
      studentLevel: String(studentInfo.value.level || '')
    }

    console.log('提交的数据:', plainApplicationData)
    
    const response = await request.post('/application-school/submit', plainApplicationData)
    
    if (response.code === 200) {
      ElMessage.success('申请提交成功')
      isSubmitted.value = true
      submittedTime.value = new Date().toLocaleString()
      await fetchSubmittedApplications()
      
      const countryIndex = selectedCountries.value.findIndex(c => c.id === activeCountry.value)
      if (countryIndex > -1) {
        selectedCountries.value[countryIndex].status = '已提交'
      }
    } else {
      ElMessage.error(response.message || '提交申请失败')
    }
  } catch (error) {
    console.error('提交申请失败:', error)
    ElMessage.error('提交申请失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 在组件挂载时获取数据
onMounted(() => {
  // 只获取学生基本信息,不自动获取申请列表
  fetchStudentInfo()
})

// 添加计算属性：当前国家的已提交申请
const countrySubmittedApplications = computed(() => {
  return submittedApplications.value.filter(app => app.countryId === activeCountry.value)
})

// 添加日期格式化方法
const formatDate = (date) => {
  if (!date) return '-'
  const d = new Date(date)
  if (isNaN(d.getTime())) return ''
  return d.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 添加开始新申请的状态和方法
const isAddingNew = ref(false)
const startNewApplication = () => {
  selectedSchool.value = null
  selectedMajor.value = null
  majorDetails.value = {}
  isAddingNew.value = true
}

// 修改切换国家的方法
const switchCountry = (id) => {
  activeCountry.value = id
  loadSchools(id)
  selectedSchool.value = null
  selectedMajor.value = null
  majorDetails.value = {}
  isAddingNew.value = false
}

// 修改加载学校列表的方法
const loadSchools = (countryId) => {
  if (!countryId) return
  availableSchools.value = schoolsData[countryId] || []
  selectedSchool.value = null
  selectedMajor.value = null
  majorDetails.value = {}
}

// 修改处理添加国家的方法
const handleAddCountry = () => {
  if (!countryForm.country) {
    ElMessage.warning('请选择要申请的国家')
    return
  }

  const country = availableCountries.find(c => c.id === countryForm.country)
  if (country && !selectedCountries.value.find(c => c.id === country.id)) {
    selectedCountries.value.push({
      ...country,
      status: '未提交'
    })
    activeCountry.value = country.id
    countryDialogVisible.value = false
    loadSchools(country.id)
  }
}

// 修改学校选择处理方法
const handleSchoolChange = (id) => {
  selectedSchool.value = id
  // 加载该学校的专业列表
  loadMajors(id)
}

// 修改加载专业列表的方法
const loadMajors = (schoolId) => {
  const school = availableSchools.value.find(s => s.id === schoolId)
  if (school) {
    availableMajors.value = school.majors || []
  } else {
    availableMajors.value = []
  }
}

// 修改专业选择处理方法
const handleMajorChange = async (id) => {
  selectedMajor.value = id
  const school = availableSchools.value.find(s => s.id === selectedSchool.value)
  if (school) {
    const major = school.majors.find(m => m.id === id)
    if (major) {
      majorDetails.value = {
        link: major.link,
        degree: major.degree,
        code: major.code,
        duration: major.duration,
        campus: major.campus,
        creditExemption: major.creditExemption,
        applicationDeadline: major.applicationDeadline
      }
    }
  }
}

// 添加显示添加国家对话框的方法
const showAddCountryDialog = () => {
  countryDialogVisible.value = true
  countryForm.country = ''
}

// 添加提交功能和状态管理
const submitting = ref(false)
const submitDialogVisible = ref(false)
const submittedApplications = ref([]) // 存储已提交的申请

// 计算属性：获取当前选中的学校信息
const selectedSchoolInfo = computed(() => {
  if (!selectedSchool.value) return null
  return availableSchools.value.find(s => s.id === selectedSchool.value)
})

// 计算属性：获取当前选中的专业信息
const selectedMajorInfo = computed(() => {
  if (!selectedMajor.value) return null
  return availableMajors.value.find(m => m.id === selectedMajor.value)
})

// 计算属性：判断当前申请是否已提交
const isSubmitted = ref(false)
const submittedTime = ref('')

// 添加继续选择的处理方法
const handleContinueSelect = () => {
  isSubmitted.value = false
  selectedSchool.value = null
  selectedMajor.value = null
  majorDetails.value = {}
}

// 修改确认提交方法
const confirmSubmit = async () => {
  try {
    submitting.value = true
    // 这里应该调用API保存到后端
    const newApplication = {
      countryId: activeCountry.value,
      schoolId: selectedSchool.value,
      majorId: selectedMajor.value,
      submitTime: new Date().toISOString(),
      details: {
        ...majorDetails.value,
        schoolName: selectedSchoolInfo.value?.name,
        majorName: selectedMajorInfo.value?.name
      }
    }
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 保存到本地状态
    submittedApplications.value.push(newApplication)
    
    // 更新国家状态，只在第一次提交时更新状态
    const countryIndex = selectedCountries.value.findIndex(c => c.id === activeCountry.value)
    if (countryIndex !== -1 && selectedCountries.value[countryIndex].status !== '已提交') {
      selectedCountries.value[countryIndex].status = '已提交'
    }
    
    submitDialogVisible.value = false
    ElMessage.success('申请提交成功！')
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 添加展开/折叠状态管理
const expandedIds = ref([])

// 切换展开/折叠状态
const toggleDetails = (id) => {
  const index = expandedIds.value.indexOf(id)
  if (index === -1) {
    expandedIds.value.push(id)
  } else {
    expandedIds.value.splice(index, 1)
  }
}

// 获取学生信息
const fetchStudentInfo = async () => {
  try {
    const userInfoStr = localStorage.getItem('userInfo');
    console.log('从localStorage获取的原始userInfo:', userInfoStr);
    
    if (!userInfoStr) {
      console.warn('未找到用户信息');
      ElMessage.warning('请先登录');
      return;
    }

    const userInfo = JSON.parse(userInfoStr);
    console.log('解析后的userInfo:', userInfo);
    
    if (!userInfo.id) {
      console.warn('用户信息中没有id');
      ElMessage.warning('用户信息不完整');
      return;
    }

    console.log('准备发送请求获取学生信息，用户ID:', userInfo.id);
    const response = await request.get(`/api/student/info/${userInfo.id}`);
    console.log('获取学生信息响应:', response);

    if (response.data.code === 200) {
      studentInfo.value = response.data.data;
      console.log('设置学生信息:', studentInfo.value);
      if (!studentInfo.value || Object.keys(studentInfo.value).length === 0) {
        console.log('未找到学生信息');
      }
    } else {
      console.warn('获取学生信息失败:', response.data.message);
      ElMessage.error(response.data.message || '获取学生信息失败');
    }
  } catch (error) {
    console.error('获取学生信息时发生错误:', error);
    ElMessage.error('获取学生信息失败: ' + error.message);
  }
};

// 修改初始化数据获取
const fetchInitialData = async () => {
  try {
    isLoading.value = true
    await Promise.all([
      fetchStudentInfo(),
      fetchUserApplications()
    ])
  } catch (error) {
    console.error('初始化数据失败:', error)
    ElMessage.error('加载数据失败，请刷新页面重试')
  } finally {
    isLoading.value = false
  }
}

// 在组件挂载时获取数据
onMounted(() => {
  fetchInitialData()
})

// 添加新的响应式变量
const isSelectingCountry = ref(false)

// 开始选择国家
const startSelectingCountry = () => {
  isSelectingCountry.value = true
}

// 处理国家选择
const handleCountrySelect = (countries) => {
  if (countries.length > 0) {
    isSelectingCountry.value = false
    activeCountry.value = countries[countries.length - 1].id
    loadSchools(activeCountry.value)
  }
}

// 移除国家
const removeCountry = (country) => {
  const index = selectedCountries.value.findIndex(c => c.id === country.id)
  if (index !== -1) {
    selectedCountries.value.splice(index, 1)
    if (activeCountry.value === country.id) {
      activeCountry.value = selectedCountries.value.length > 0 ? selectedCountries.value[0].id : null
    }
  }
}

// 获取状态标签类型
const getStatusType = (status) => {
  const statusMap = {
    '已提交': 'info',
    '审核中': 'warning',
    '已录取': 'success',
    '已拒绝': 'danger',
    '已接受': 'success',
    '已放弃': 'info'
  }
  return statusMap[status] || 'info'
}

// 格式化货币
const formatCurrency = (value) => {
  if (!value) return '0'
  return new Intl.NumberFormat('zh-CN', {
    style: 'currency',
    currency: 'CNY'
  }).format(value)
}

// 添加当前视图状态
const currentView = ref('')  // 修改默认值为空字符串

// 修改切换视图的方法
const switchView = (view) => {
  currentView.value = view
  isAddingCountry.value = false
  if (view === 'submitted') {
    fetchSubmittedApplications()
  }
}

// 在 script setup 部分添加新的变量和方法
const isAddingCountry = ref(false)

// 修改处理添加国家按钮点击的方法
const handleAddCountryClick = () => {
  isAddingCountry.value = true
  currentView.value = ''
  showAddCountryDialog()
}

</script>

<style scoped>
/* 页面基础样式 */
:root {
  background: #ffffff;
}

/* 三个主要容器的共同样式 */
.info-container {
  background: #f5f5f7;
  border-radius: 12px;
  padding: 20px;
  margin: 50px 20px 20px 20px;
  display: flex;  /* 添加flex布局 */
  flex-wrap: wrap;  /* 允许换行 */
  gap: 10px;  /* 项目间距 */
}

.application-container {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px 24px;
  margin: 20px;
}

/* 进度条容器样式 */
.process-container {
  background: #f5f5f7;
  border-radius: 12px;
  padding: 20px;
  margin: 20px;
}

.process-steps {
  padding: 40px 0;
  max-width: 1400px;
  margin: 0 auto;
}

.steps-wrapper {
  display: flex;
  justify-content: center;
  flex-wrap: nowrap; /* 是否允许换行，改为 wrap，允许换行，项目会被压缩（如果设置了 flex-shrink），项目会溢出容器（如果没有设置 flex-shrink） */
  gap: 20px;
  padding: 0 40px;
  position: relative;
}

.step {
  flex: 1;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.step:hover {
  transform: translateY(-5px);
}


.step-box {
  width: 70px;
  height: 70px;
  margin: 0 auto 15px;
  background: #ddd;  /* 默认未完成状态的颜色 */
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  position: relative;
  transition: all 0.3s ease;
}

/* 完成状态的样式 */
.step.is-completed .step-box {
  background: #7abaf5;
  box-shadow: 0 4px 12px #2880ea4c;
}

.step:hover .step-actions {
  opacity: 1;
  transform: translateY(0);
}

.step-box::after {
  content: attr(data-step);
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 24px;
  color: inherit;
  color: white;
}

.step.is-active .step-box {
  background: #1890ff;
  color: white;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

.completed {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: #1890ff;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.step-content h3 {
  font-size: 14px;
  color: #666;
  margin: 1px 1px 8px;
  transition: all 0.3s ease;
}

.step.is-active .step-content h3 {
  color: #1890ff;
}
 
.timing {
  font-size: 12px;
  color: #666;
  margin-bottom: 10px;  /* 增加下边距，原来可能是 10px 或更小 */
}
.step-line {
  position: absolute;
  top: 30px;
  left: calc(50% + 40px);
  right: calc(-50% + 40px);
  height: 2px;
  background: #ddd;
  z-index: 1;
}

.step.is-active .step-line {
  background: #1890ff;
}

.step-actions {
  margin-top: 0;  /* 可以给按钮容器也添加一个上边距 */
}

/* 移动端适配 */
@media (max-width: 768px) {
  .process-steps {
    padding: 20px 0;
    overflow-x: auto;
  }
  
  .steps-wrapper {
    min-width: 800px;
    padding: 0 20px;
    gap: 10px;
  }
  
  .step-box {
    width: 50px;
    height: 50px;
    font-size: 20px;
  }
  
  .step-content h3 {
    font-size: 12px;
  }
  
  .timing {
    font-size: 10px;
  }
}

/* 信息项样式 */
.info-item {
  padding: 5px 8px;  /* 调整内边距 */
  background: #f5f5f7;  /* 白色背景 */
  display: flex;
  align-items: center;
  flex: auto;  /* 弹性伸缩 */
  min-width: 100px;  /* 最小宽度 */
  max-width: calc(50% - 10px);  /* 最大宽度，确保每行最多两个 */
}

.label {
  color: #666;
  margin-right: 4px;
  white-space: nowrap;  /* 防止标签换行 */
}

.value {
  color: #333;
  flex: 1;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .info-item {
    max-width: 100%;  /* 在移动端占满整行 */
  }
}

/* 信息包裹层样式 */
.info-wrapper {
  position: relative;
  width: 100%;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

/* 编辑图标样式 */
.edit-icon {
  position: absolute;
  right: 10px;
  font-size: 18px;
  color: #666;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  transition: all 0.3s;
  background: #f5f5f7;  /* 添加背景色，与容器背景一致 */
}

.edit-icon:hover {
  background: rgba(0, 0, 0, 0.05);
  color: #333;
}

.application-steps {
  transition: opacity 0.3s ease;
}

.application-steps.is-loading {
  opacity: 0.6;
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 添加学生信息按钮样式 */
.add-student-info {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
}

/* 对话框样式 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

/* 添加国家选择相关样式 */
.country-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.tab {
  padding: 8px 24px;
  border-radius: 20px;
  background: transparent;  /* 修改背景为透明 */
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
  color: #409EFF;  /* 修改默认文字颜色为蓝色 */
  border: 1px dashed #409EFF;  /* 添加虚线边框 */
}

.tab.active {
  background: #409EFF;
  color: #fff;
  border: 1px solid #409EFF;  /* 选中状态使用实线边框 */
}

.tab:hover {
  background: #ecf5ff;  /* 修改悬停效果 */
}

.tab.active:hover {
  background: #409EFF;
}

.school-option {
  display: flex;
  flex-direction: column;
}

.school-subtitle {
  font-size: 12px;
  color: #999;
}

/* 添加提交按钮区域样式 */
.submit-area {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.submit-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.submitted-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.submit-time {
  color: #666;
  font-size: 14px;
}

.confirm-details {
  margin-top: 16px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.confirm-item {
  margin-bottom: 8px;
  display: flex;
  gap: 8px;
}

.confirm-item .label {
  color: #666;
  min-width: 60px;
}

.country-tag {
  padding: 8px 16px;
  border-radius: 20px;
  background: #f0f2f5;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.country-tag.active {
  background: #409EFF;
  color: #fff;
}

.country-tag:hover {
  background: #e0e3e9;
}

.country-tag.active:hover {
  background: #66b1ff;
}

.add-tab {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #409EFF;
  border: 1px dashed #409EFF;
}

.add-tab:hover {
  background: #ecf5ff;
  border-color: #66b1ff;
}

.school-option {
  display: flex;
  flex-direction: column;
}

.school-subtitle {
  font-size: 12px;
  color: #999;
}

/* 添加提交按钮区域样式 */
.submit-area {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.submit-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.submitted-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.submit-time {
  color: #666;
  font-size: 14px;
}

.confirm-details {
  margin-top: 16px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.confirm-item {
  margin-bottom: 8px;
  display: flex;
  gap: 8px;
}

.confirm-item .label {
  color: #666;
  min-width: 60px;
}

/* 添加已提交申请列表样式 */
.submitted-applications {
  margin-bottom: 2px;
  border-bottom: 1px solid #eee;
  padding-bottom: 20px;
}

.section-title {
  display: block;
  font-size: 16px;
  color: #333;
  margin: 0 0 20px 0;
  padding: 0;
  font-weight: 500;
  text-align: left;
}

.application-list {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.application-item {
  display: flex;
  flex-direction: column;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.application-item:hover {
  background: #f0f0f0;
}

.application-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.expand-icon {
  margin-left: 8px;
  vertical-align: middle;
}

.no-country-selected {
  padding: 40px;
  text-align: center;
}

.applying-form {
  margin-top: 20px;
}

.country-selection {
  margin-bottom: 20px;
}

/* 添加国家对话框样式 */
.el-dialog {
  border-radius: 8px;
}

.el-dialog__header {
  margin: 0;
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.el-dialog__body {
  padding: 20px;
}

.detail-value.link {
  color: #409EFF;
  text-decoration: none;
}

.detail-value.link:hover {
  text-decoration: underline;
}

.school-info {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  border-top: 1px solid #e4e7ed;
}

.school-name {
  font-size: 18px;
  font-weight: 500;
  color: #333;
  margin-bottom: 16px;
}

.major-info {
  margin-bottom: 16px;
}

.major-name {
  font-size: 16px;
  color: #333;
  margin-bottom: 12px;
}

.major-link {
  margin-bottom: 16px;
  word-break: break-all;
}

.major-link a {
  color: #409EFF;
  text-decoration: none;
}

.major-details {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.detail-item {
  display: flex;
  align-items: center;
}

.detail-item .label {
  color: #666;
  margin-right: 8px;
  min-width: 70px;
}

.detail-item .value {
  color: #333;
}

.status-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

/* 状态标签页样式优化 */
.status-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
  padding-bottom: 12px;
}

.tab {
  padding: 8px 24px;
  border-radius: 20px;
  background: transparent;  /* 修改背景为透明 */
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
  color: #409EFF;  /* 修改默认文字颜色为蓝色 */
  border: 1px dashed #409EFF;  /* 添加虚线边框 */
}

.tab.active {
  background: #409EFF;
  color: #fff;
  border: 1px solid #409EFF;  /* 选中状态使用实线边框 */
}

.tab:hover {
  background: #ecf5ff;  /* 修改悬停效果 */
}

.tab.active:hover {
  background: #409EFF;
}

.application-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 4px;
  color: #409EFF;
  border: 1px dashed #409EFF;
  background: transparent;
}

.detail-value.link:hover {
  text-decoration: underline;
}

.school-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.school-name {
  font-weight: 500;
  color: #333;
}

.major-name {
  font-size: 14px;
  color: #666;
}

.add-application {
  margin: 20px 0;
  display: flex;
  justify-content: center;
}

.new-application-form {
  margin-top: 20px;
}

.student-info-section {
  background-color: #f5f7fa;
  padding: 16px 24px;
  border-radius: 8px;
  margin: 50px 20px 20px 20px;
}

.info-row {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  width: 100%;
}

.info-item {
  display: flex;
  align-items: center;
  white-space: nowrap;
}

.info-item .label {
  color: #666;
  font-size: 14px;
  margin-right: 8px;
}

.info-item .value {
  color: #333;
  font-size: 14px;
  font-weight: 500;
}

@media (max-width: 1200px) {
  .info-row {
    gap: 16px;
  }
  
  .info-item {
    flex: 0 0 calc(33.33% - 12px);
  }
}

@media (max-width: 768px) {
  .info-item {
    flex: 0 0 calc(50% - 8px);
  }
}

@media (max-width: 480px) {
  .info-item {
    flex: 0 0 100%;
  }

  .student-info-section {
    padding: 12px 16px;
  }
  .add-tab:hover {
  background: #ecf5ff;
  border-color: #66b1ff;
}
}

.applications-container {
  margin: 20px auto;
  max-width: 1200px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.section-title {
  margin-bottom: 20px;
  font-size: 18px;
  font-weight: 500;
  color: #333;
}

.applications-list {
  margin-top: 20px;
}

.application-card {
  background: #fff;
  border-radius: 15px;
  margin-bottom: 5px; 
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: transparent;
  border: none;
}

.card-header:hover {
  background: #f5f7fa;
}

.basic-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.info-group {
  display: flex;
  align-items: center;
  gap: 16px;
  height: 32px;
  line-height: 32px;
}

.country-tag {
  font-size: 14px !important;
  height: 24px !important;
  line-height: 24px !important;
  padding: 0 12px !important;
  min-width: 80px;
  text-align: center;
  flex-shrink: 0;
}

.school-name {
  font-size: 14px;
  color: #333;
  font-weight: normal;
  white-space: nowrap;
  margin: 0;
  padding: 0;
}

.major-name {
  font-size: 14px;
  color: #666;
  font-weight: normal;
  white-space: nowrap;
  margin: 0;
  padding: 0;
}

.application-details {
  padding: 0 16px 16px;
}

.details-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-row {
  display: flex;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-row .label {
  width: 100px;
  color: #666;
  font-size: 14px;
}

.detail-row .value {
  flex: 1;
  color: #333;
  font-size: 14px;
}

.detail-row .link {
  color: #409EFF;
  text-decoration: none;
}

.detail-row .link:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .info-group {
    flex-wrap: nowrap;
    gap: 8px;
  }
  
  .country-tag {
    min-width: auto;
  }
}
</style>
