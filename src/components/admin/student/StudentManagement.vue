<template>
  <div class="students-content">
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon"
          style="background: linear-gradient(135deg, rgba(24, 144, 255, 0.2) 0%, rgba(24, 144, 255, 0.1) 100%);">
          <Users style="color: #1890ff;" />
        </div>
        <div class="stat-content">
          <div class="stat-header">
            <h3 class="stat-value">{{ studentStatistics.total }}</h3>
            <div class="stat-trend up">
              <TrendingUp class="trend-icon" />
              <span>8%</span>
            </div>
          </div>
          <p class="stat-label">学生总数</p>
          <div class="stat-progress">
            <div class="progress-bar">
              <div class="progress-fill" style="width: 100%; background-color: #1890ff;"></div>
            </div>
          </div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon"
          style="background: linear-gradient(135deg, rgba(82, 196, 26, 0.2) 0%, rgba(82, 196, 26, 0.1) 100%);">
          <UserCheck style="color: #52c41a;" />
        </div>
        <div class="stat-content">
          <div class="stat-header">
            <h3 class="stat-value">{{ studentStatistics.active }}</h3>
            <div class="stat-trend up">
              <TrendingUp class="trend-icon" />
              <span>5%</span>
            </div>
          </div>
          <p class="stat-label">活跃学生</p>
          <div class="stat-progress">
            <div class="progress-bar">
              <div class="progress-fill" style="width: 75%; background-color: #52c41a;"></div>
            </div>
          </div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon"
          style="background: linear-gradient(135deg, rgba(250, 140, 22, 0.2) 0%, rgba(250, 140, 22, 0.1) 100%);">
          <GraduationCap style="color: #fa8c16;" />
        </div>
        <div class="stat-content">
          <div class="stat-header">
            <h3 class="stat-value">{{ studentStatistics.graduated }}</h3>
            <div class="stat-trend up">
              <TrendingUp class="trend-icon" />
              <span>12%</span>
            </div>
          </div>
          <p class="stat-label">已毕业学生</p>
          <div class="stat-progress">
            <div class="progress-bar">
              <div class="progress-fill" style="width: 17%; background-color: #fa8c16;"></div>
            </div>
          </div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon"
          style="background: linear-gradient(135deg, rgba(245, 34, 45, 0.2) 0%, rgba(245, 34, 45, 0.1) 100%);">
          <UserPlus style="color: #f5222d;" />
        </div>
        <div class="stat-content">
          <div class="stat-header">
            <h3 class="stat-value">{{ studentStatistics.newThisMonth }}</h3>
            <div class="stat-trend up">
              <TrendingUp class="trend-icon" />
              <span>20%</span>
            </div>
          </div>
          <p class="stat-label">本月新增</p>
          <div class="stat-progress">
            <div class="progress-bar">
              <div class="progress-fill" style="width: 10%; background-color: #f5222d;"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="filter-bar">
      <div class="filter-group">
        <label>年级</label>
        <div class="select-wrapper">
          <select v-model="studentFilters.grade">
            <option value="">全部</option>
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
      <div class="filter-group">
        <label>状态</label>
        <div class="select-wrapper">
          <select v-model="studentFilters.status">
            <option value="">全部</option>
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
      <div class="filter-group">
        <label>排序方式</label>
        <div class="select-wrapper">
          <select v-model="studentFilters.sort">
            <option value="name">姓名</option>
            <option value="joinDate">加入日期</option>
            <option value="currentSchool">学校</option>
          </select>
          <ChevronDown class="select-icon" />
        </div>
      </div>
      <div class="filter-search">
        <label>搜索</label>
        <div class="search-input-wrapper">
          <Search class="search-input-icon" />
          <input type="text" v-model="searchQuery" placeholder="搜索学生姓名、ID或学校..." class="filter-search-input"
            @input="handleSearch" @keydown.enter="handleEnterSearch" @keydown.down.prevent="handleKeyDown"
            @keydown.up.prevent="handleKeyDown" @keydown.esc="showSuggestions = false" />
          <div v-if="searchQuery && showSuggestions" class="search-suggestions">
            <template v-for="(suggestion, index) in searchSuggestions" :key="index">
              <template v-if="suggestion.type === 'divider'">
                <div class="search-history-divider">
                  {{ suggestion.title }}
                </div>
              </template>
              <template v-else>
                <div class="suggestion-item" @click="selectSuggestion(suggestion)"
                  :class="{ active: index === activeSuggestionIndex }">
                  <div class="suggestion-icon">
                    <template v-if="suggestion.type === 'student'">
                      <User />
                    </template>
                    <template v-else>
                      <Clock />
                    </template>
                  </div>
                  <div class="suggestion-content">
                    <div class="suggestion-title">
                      <template v-if="suggestion.highlight.title.before !== undefined">
                        {{ suggestion.highlight.title.before }}
                        <span style="color: #1890ff; font-weight: 600;">{{ suggestion.highlight.title.match }}</span>
                        {{ suggestion.highlight.title.after }}
                      </template>
                      <template v-else>
                        {{ suggestion.title }}
                      </template>
                    </div>
                    <div class="suggestion-subtitle">
                      <template v-if="suggestion.highlight.subtitle.before !== undefined">
                        {{ suggestion.highlight.subtitle.before }}
                        <span style="color: #1890ff; font-weight: 600;">{{ suggestion.highlight.subtitle.match }}</span>
                        {{ suggestion.highlight.subtitle.after }}
                      </template>
                      <template v-else>
                        {{ suggestion.subtitle }}
                      </template>
                    </div>
                  </div>
                </div>
              </template>
            </template>
            <div v-if="searchSuggestions.length === 0" class="no-suggestions">
              未找到相关结果
            </div>
          </div>
        </div>
      </div>
      <div class="filter-actions">
        <el-button class="custom-button" type="default" icon="Filter">
          筛选
        </el-button>
        <el-button class="custom-button" type="default" icon="RefreshRight" @click="resetFilters">
          重置
        </el-button>
        <el-button class="custom-button" type="default" icon="Document">
          导出数据
        </el-button>
        <el-button class="custom-button" type="primary" icon="Plus" @click="showAddStudentModal = true">
          添加学生
        </el-button>
      </div>
    </div>

    <div class="students-table">
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>正在加载学生数据...</p>
      </div>
      <table v-else>
        <thead>
          <tr>
            <th>
              <div class="th-content">
                <button class="checkbox-icon" :class="{ 'checked': allSelected }" @click="toggleAllSelection">
                  <Check v-if="allSelected" class="check-icon" />
                </button>
                <span>学生信息</span>
              </div>
            </th>
            <th>年级</th>
            <th>目标国家</th>
            <th>状态</th>
            <th>GPA</th>
            <th>学校</th>
            <th>顾问老师</th>
            <th>加入日期</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(student, index) in paginatedStudents" :key="index">
            <td>
              <div class="td-content">
                <button class="checkbox-icon" :class="{ 'checked': student.selected }"
                  @click="toggleStudentSelection(student)">
                  <Check v-if="student.selected" class="check-icon" />
                </button>
                <div class="student-info">
                  <div class="student-avatar">
                    <img v-if="student.avatar" :src="student.avatar" :alt="student.name" />
                    <div v-else class="avatar-text" :style="{ backgroundColor: generateAvatarColor(student.name) }">
                      {{ student.name?.charAt(0) || '学' }}
                    </div>
                  </div>
                  <div class="student-name">
                    <span>{{ student.name }}</span>
                    <div class="student-details">
                      <span>{{ student.id }}</span>
                      <span>{{ student.grade }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </td>
            <td>{{ student.grade }}</td>
            <td>
              <div class="target-countries">
                <span v-for="(country, idx) in student.targetCountries" :key="idx" class="country-tag">
                  {{ country }}
                </span>
              </div>
            </td>
            <td>
              <span class="status-badge" :class="student.status">{{ student.statusText }}</span>
            </td>
            <td>{{ student.gpa }}</td>
            <td>{{ student.currentSchool || "未设置" }}</td>
            <td>{{ student.consultant }}</td>
            <td>{{ student.joinDate }}</td>
            <td>
              <div class="action-buttons">
                <button class="btn-icon-sm" @click="viewStudentDetail(student)" title="查看详情">
                  <Eye />
                </button>
                <button class="btn-icon-sm" @click="editStudent(student)" title="编辑">
                  <Edit />
                </button>
                <button class="btn-icon-sm" @click="deleteStudent(student)" title="删除">
                  <Trash2 />
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="paginatedStudents.length === 0">
            <td colspan="9" class="empty-data">
              暂无学生数据
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="table-footer">
      <div class="table-info">
        显示 {{ (currentPage - 1) * pageSize + 1 }} 至 {{ Math.min(currentPage * pageSize, filteredStudents.length) }} 条，共
        {{
          filteredStudents.length }} 条
      </div>
      <div class="pagination">
        <button class="pagination-button" :disabled="currentPage === 1" @click="goToPage(1)">
          <ChevronsLeft />
        </button>
        <button class="pagination-button" :disabled="currentPage === 1" @click="goToPage(currentPage - 1)">
          <ChevronLeft />
        </button>
        <button v-for="page in displayedPages" :key="page" class="pagination-number"
          :class="{ active: currentPage === page }" @click="goToPage(page)">
          {{ page }}
        </button>
        <span v-if="totalPages > 5 && currentPage < totalPages - 2" class="pagination-ellipsis">...</span>
        <button v-if="totalPages > 1 && !displayedPages.includes(totalPages)" class="pagination-number"
          :class="{ active: currentPage === totalPages }" @click="goToPage(totalPages)">
          {{ totalPages }}
        </button>
        <button class="pagination-button" :disabled="currentPage === totalPages" @click="goToPage(currentPage + 1)">
          <ChevronRight />
        </button>
        <button class="pagination-button" :disabled="currentPage === totalPages" @click="goToPage(totalPages)">
          <ChevronsRight />
        </button>
      </div>
      <div class="page-size">
        <span>每页显示:</span>
        <div class="select-wrapper">
          <select v-model="pageSize">
            <option :value="10">10</option>
            <option :value="20">20</option>
            <option :value="50">50</option>
            <option :value="100">100</option>
          </select>
          <ChevronDown class="select-icon" />
        </div>
      </div>
    </div>

    <!-- 添加学生模态框 -->
    <AddStudentModal v-if="showAddStudentModal" @close="showAddStudentModal = false" @save="saveStudent"
      :newStudent="newStudent" />

    <!-- 学生详情模态框 -->
    <StudentDetailModal v-if="showStudentDetailModal && selectedStudent" :student="selectedStudent"
      @close="showStudentDetailModal = false" @edit="editStudent" />

    <!-- 学生编辑模态框 -->
    <StudentEditModal v-if="showEditStudentModal && studentToEdit" :studentData="studentToEdit"
      @close="showEditStudentModal = false" @save="saveEditedStudent" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { TrendingUp, Users, UserCheck, UserPlus, GraduationCap, ChevronDown, Check, Eye, Edit, Trash2, Search, ChevronLeft, ChevronRight, ChevronsLeft, ChevronsRight, User, School, FileText, Clock } from 'lucide-vue-next'
import AddStudentModal from './AddStudentModal.vue'
import StudentDetailModal from './StudentDetailModal.vue'
import StudentEditModal from './StudentEditModal.vue'
import request from '../../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

// 学生管理数据
const studentFilters = ref({
  grade: '',
  status: '',
  sort: 'name'
})

const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

const studentStatistics = ref({
  total: 0,
  active: 0,
  graduated: 0,
  newThisMonth: 0
})

const students = ref([])
const loading = ref(false)

// 顾问列表
const advisors = ref([])
const advisorMap = ref({})
const advisorIdMap = ref({})

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

    // 如果没有顾问，添加一些默认值
    if (advisors.value.length === 0) {
      advisors.value = [
        { id: 1, name: '王管理老师' },
        { id: 2, name: '李管理老师' },
        { id: 3, name: '张管理老师' },
        { id: 4, name: '赵管理老师' }
      ]
    }

    // 初始化顾问映射表
    advisorMap.value = {}
    advisorIdMap.value = {}

    advisors.value.forEach(advisor => {
      advisorMap.value[advisor.id] = advisor.name
      advisorIdMap.value[advisor.name] = advisor.id
    })
  } catch (error) {
    console.error('获取顾问列表失败:', error)
    // 使用默认顾问列表
    advisors.value = [
      { id: 1, name: '王管理老师' },
      { id: 2, name: '李管理老师' },
      { id: 3, name: '张管理老师' },
      { id: 4, name: '赵管理老师' }
    ]

    // 初始化默认顾问映射表
    advisorMap.value = {
      1: '王管理老师',
      2: '李管理老师',
      3: '张管理老师',
      4: '赵管理老师'
    }

    advisorIdMap.value = {
      '王管理老师': 1,
      '李管理老师': 2,
      '张管理老师': 3,
      '赵管理老师': 4
    }
  }
}

// 获取学生数据
const fetchStudents = async () => {
  try {
    loading.value = true
    const res = await request.get('/student/admin/students')
    const studentsData = res?.data || []

    // 调试日志：检查API返回的englishName
    if (studentsData.length > 0) {
      console.log('API返回的第一个学生数据:', JSON.stringify(studentsData[0], null, 2))
      console.log('英文名:', studentsData[0].englishName)
    }

    // 转换数据格式
    students.value = studentsData.map(student => ({
      id: student.id ? String(student.id) : '',
      rawId: student.id,
      userId: student.userId || null,
      applicationId: student.applicationId || null,
      name: student.name || '',
      englishName: student.englishName || '',
      avatar: null,
      gender: student.gender || '',
      age: calculateAge(student.birthDate),
      birthDate: student.birthDate || '',
      grade: getGradeFromSchool(student.currentSchool) || '',
      school: student.currentSchool || '',
      currentSchool: student.currentSchool || '',
      major: student.major || '',
      targetCountries: getTargetCountries(student.targetCountries) || [],
      status: getStudentStatus(student.applicationStatus) || 'active',
      statusText: getStatusText(student.applicationStatus) || '活跃',
      gpa: student.gpa || '',
      gpaScale: student.gpaScale || '4.0',
      applications: student.applicationProgress || 0,
      applicationProgress: student.applicationProgress || 0,
      contactInfo: {
        email: student.email || '',
        phone: student.phone || '',
        wechat: student.wechat || ''
      },
      email: student.email || '',
      phone: student.phone || '',
      wechat: student.wechat || '',
      consultant: getConsultantName(student.advisorId) || '',
      advisorId: student.advisorId || null,
      joinDate: formatDate(student.createTime) || '',
      nationality: student.nationality || '中国',
      passportNo: student.passportNo || '',
      toefl: student.toefl || '',
      ielts: student.ielts || '',
      gre: student.gre || '',
      gmat: student.gmat || '',
      applicationStatus: student.applicationStatus || '',
      currentStep: student.currentStep || '1',
      notes: student.notes || '',
      selected: false
    }))

    // 更新统计数据
    updateStatistics()
  } catch (error) {
    console.error('获取学生列表失败:', error)
    ElMessage.error('获取学生列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 根据学生状态获取状态文本
const getStudentStatus = (status) => {
  if (!status) return 'active'

  const statusMap = {
    '审核中': 'reviewing',
    '等待中': 'waiting',
    '通过': 'passed',
    '拒绝': 'rejected',
    '录取': 'admitted',
    '已毕业': 'graduated'
  }

  return statusMap[status] || 'active'
}

// 获取状态文本
const getStatusText = (status) => {
  // 直接返回状态文本，不需要再翻译
  return status || '未设置'
}

// 计算年龄
const calculateAge = (birthDate) => {
  if (!birthDate) return '';

  const birth = new Date(birthDate);
  const now = new Date();
  let age = now.getFullYear() - birth.getFullYear();
  const monthDiff = now.getMonth() - birth.getMonth();

  if (monthDiff < 0 || (monthDiff === 0 && now.getDate() < birth.getDate())) {
    age--;
  }

  return age;
}

// 从学校名称推断年级
const getGradeFromSchool = (school) => {
  if (!school) return '';

  // 根据学校名称中的关键词来推断年级
  if (school.includes('研究生')) {
    return '研二';
  } else if (school.includes('大学')) {
    return '大三';
  } else if (school.includes('学院')) {
    return '大二';
  } else {
    return '大一'; // 默认值
  }
}

// 获取目标国家
const getTargetCountries = (countriesStr) => {
  if (!countriesStr) return ['美国'];

  try {
    if (typeof countriesStr === 'string') {
      return countriesStr.split(',').map(c => c.trim());
    }
    return ['美国']; // 默认值
  } catch (e) {
    return ['美国']; // 发生错误时返回默认值
  }
}

// 获取顾问名称
const getConsultantName = (advisorId) => {
  if (!advisorId) return '未分配'

  return advisorMap.value[advisorId] || '未分配'
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '';

  try {
    const date = new Date(dateStr);
    return date.toISOString().split('T')[0];
  } catch (e) {
    return '';
  }
}

// 添加学生模态框
const showAddStudentModal = ref(false)
const newStudent = ref({
  id: '',
  userId: '',
  applicationId: '',
  name: '',
  englishName: '',
  gender: '男',
  birthDate: '',
  nationality: '中国',
  passportNo: '',
  grade: '',
  school: '',
  currentSchool: '',
  major: '',
  email: '',
  phone: '',
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
  advisorId: null,
  notes: '',
  consultant: ''
})

// 查看学生详情
const showStudentDetailModal = ref(false)
const selectedStudent = ref(null)

// 编辑学生
const showEditStudentModal = ref(false)
const studentToEdit = ref(null)

// 筛选学生
const filteredStudents = computed(() => {
  let result = [...students.value]

  // 应用筛选条件
  if (studentFilters.value.grade) {
    result = result.filter(student => student.grade === studentFilters.value.grade)
  }

  if (studentFilters.value.status) {
    result = result.filter(student => student.applicationStatus === studentFilters.value.status)
  }

  // 应用搜索查询
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(student =>
      student.name.toLowerCase().includes(query) ||
      student.id.toLowerCase().includes(query) ||
      student.school.toLowerCase().includes(query)
    )
  }

  // 应用排序
  if (studentFilters.value.sort === 'name') {
    result.sort((a, b) => a.name.localeCompare(b.name))
  } else if (studentFilters.value.sort === 'joinDate') {
    result.sort((a, b) => new Date(b.joinDate) - new Date(a.joinDate))
  } else if (studentFilters.value.sort === 'currentSchool') {
    result.sort((a, b) => {
      const schoolA = a.currentSchool || '';
      const schoolB = b.currentSchool || '';
      return schoolA.localeCompare(schoolB);
    })
  }

  return result
})

// 分页相关计算属性
const totalPages = computed(() => {
  return Math.ceil(filteredStudents.value.length / pageSize.value) || 1
})

const displayedPages = computed(() => {
  if (totalPages.value <= 5) {
    return Array.from({ length: totalPages.value }, (_, i) => i + 1)
  }

  if (currentPage.value <= 3) {
    return [1, 2, 3, 4, 5]
  }

  if (currentPage.value >= totalPages.value - 2) {
    return Array.from({ length: 5 }, (_, i) => totalPages.value - 4 + i)
  }

  return [currentPage.value - 2, currentPage.value - 1, currentPage.value, currentPage.value + 1, currentPage.value + 2]
})

const paginatedStudents = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredStudents.value.slice(start, end)
})

// 全选状态
const allSelected = ref(false)

// 查看学生详情
const viewStudentDetail = (student) => {
  selectedStudent.value = student
  showStudentDetailModal.value = true
}

// 编辑学生
const editStudent = (student) => {
  console.log('准备编辑学生数据:', JSON.stringify(student, null, 2))

  // 获取原始数据
  const rawStudent = students.value.find(s => s.rawId === student.rawId)
  console.log('从students中找到的原始数据:', rawStudent ? JSON.stringify(rawStudent, null, 2) : '未找到')

  if (rawStudent) {
    console.log('原始数据中的英文名:', rawStudent.englishName)
  }

  // 确保传递所有必要字段
  studentToEdit.value = {
    // 首先合并原始数据
    ...(rawStudent || {}),
    // 然后合并传入的student数据
    ...student,
    // 最后添加可能缺失的字段，确保这些值不会被覆盖
    birthDate: student.birthDate || rawStudent?.birthDate || '',
    grade: student.grade || rawStudent?.grade || '',
    currentSchool: student.currentSchool || student.school || rawStudent?.currentSchool || rawStudent?.school || '',
    major: student.major || rawStudent?.major || '',
    nationality: student.nationality || rawStudent?.nationality || '中国',
    passportNo: student.passportNo || rawStudent?.passportNo || '',
    gpaScale: student.gpaScale || rawStudent?.gpaScale || '4.0',
    toefl: student.toefl || rawStudent?.toefl || '',
    ielts: student.ielts || rawStudent?.ielts || '',
    gre: student.gre || rawStudent?.gre || '',
    gmat: student.gmat || rawStudent?.gmat || '',
    applicationStatus: student.applicationStatus || rawStudent?.applicationStatus || '',
    currentStep: student.currentStep || rawStudent?.currentStep || '1',
    notes: student.notes || rawStudent?.notes || '',
    englishName: student.englishName || rawStudent?.englishName || '',
    email: student.email || student.contactInfo?.email || rawStudent?.email || rawStudent?.contactInfo?.email || '',
    phone: student.phone || student.contactInfo?.phone || rawStudent?.phone || rawStudent?.contactInfo?.phone || '',
    wechat: student.wechat || student.contactInfo?.wechat || rawStudent?.wechat || rawStudent?.contactInfo?.wechat || ''
  }

  console.log('最终传递给编辑模态框的数据:', JSON.stringify(studentToEdit.value, null, 2))
  showEditStudentModal.value = true
}

// 保存编辑后的学生信息
const saveEditedStudent = async (student) => {
  await saveStudent(student)
  showEditStudentModal.value = false
}

// 删除学生
const deleteStudent = (student) => {
  ElMessageBox.confirm(
    `确定要删除学生 ${student.name} 吗？此操作不可逆。`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
      try {
        // 实际接口调用
        const res = await request.delete(`/student/admin/students/delete${student.rawId}`)

        if (res && (res.code === 200 || res.success === true)) {
          const index = students.value.findIndex(item => item.id === student.id)
          if (index !== -1) {
            students.value.splice(index, 1)
            // 更新统计数据
            updateStatistics()
            ElMessage.success('删除成功')
          }
        } else {
          ElMessage.error(res?.message || '删除失败')
        }
      } catch (error) {
        console.error('删除学生失败:', error)
        ElMessage.error('删除学生失败，请稍后重试')
      }
    })
    .catch(() => {
      // 取消删除
    })
}

// 更新统计数据
const updateStatistics = () => {
  studentStatistics.value.total = students.value.length
  studentStatistics.value.active = students.value.filter(s =>
    s.applicationStatus === '审核中' ||
    s.applicationStatus === '等待中' ||
    s.applicationStatus === '通过'
  ).length
  studentStatistics.value.graduated = students.value.filter(s => s.applicationStatus === '已毕业').length

  // 计算本月新增
  const now = new Date()
  const monthStart = new Date(now.getFullYear(), now.getMonth(), 1)
  studentStatistics.value.newThisMonth = students.value.filter(s => {
    const joinDate = new Date(s.joinDate)
    return joinDate >= monthStart && joinDate <= now
  }).length
}

// 保存学生
const saveStudent = async (student) => {
  try {
    console.log('保存学生信息:', JSON.stringify(student, null, 2));

    // 验证必填字段
    if (!student.userId) {
      ElMessage.warning('用户ID不能为空');
      return;
    }

    // 判断是添加还是更新
    const isEditMode = !!(student.id && student.id !== '');
    console.log('操作模式:', isEditMode ? '更新学生' : '添加新学生');

    // 构造请求数据
    const studentData = {
      name: student.name,
      englishName: student.englishName,
      gender: student.gender,
      birthDate: student.birthDate,
      nationality: student.nationality,
      passportNo: student.passportNo,
      currentSchool: student.currentSchool || student.school,
      major: student.major,
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
      targetCountries: Array.isArray(student.targetCountries) ? student.targetCountries.join(',') : '',
      advisorId: getAdvisorIdFromName(student.consultant),
      notes: student.notes,
      userId: student.userId || null,
      applicationId: student.applicationId || null
    }

    let res;

    if (isEditMode) {
      // 使用更新学生API
      // 如果是数字ID，直接使用该ID
      let idNumber;
      if (typeof student.id === 'number') {
        idNumber = student.id;
      }
      // 如果存在rawId，使用rawId
      else if (student.rawId) {
        idNumber = student.rawId;
      }
      // 否则尝试从ID字符串中提取数字部分
      else if (typeof student.id === 'string') {
        const matches = student.id.match(/\d+/);
        idNumber = matches ? parseInt(matches[0]) : student.id;
      } else {
        idNumber = student.id;
      }

      studentData.id = idNumber;

      console.log('正在更新学生信息，ID:', idNumber, '原始ID:', student.id);
      console.log('更新学生数据:', JSON.stringify(studentData, null, 2));

      // 使用专用的更新API
      res = await request.post('/student/admin/students/update', studentData);
      console.log('更新学生响应:', JSON.stringify(res, null, 2));
    } else {
      // 使用添加学生专用API
      // 确保不包含ID字段
      delete studentData.id;
      console.log('正在添加新学生，数据:', JSON.stringify(studentData, null, 2));

      // 调用专用的添加学生API
      res = await request.post('/student/admin/students/add', studentData);
      console.log('添加学生响应:', JSON.stringify(res, null, 2));
    }

    if (res && (res.code === 200 || res.success === true)) {
      // 操作成功后重新获取学生列表
      await fetchStudents();

      ElMessage.success(isEditMode ? '更新学生成功' : '添加学生成功');

      // 关闭模态框
      showAddStudentModal.value = false;

      // 重置表单
      resetNewStudent();
    } else {
      // 显示服务器返回的错误信息
      const errorMsg = res?.message || '操作失败';
      console.error('操作失败:', errorMsg);
      ElMessage.error(errorMsg);
    }
  } catch (error) {
    console.error(student.id ? '更新学生失败' : '添加学生失败', error);

    // 获取具体错误信息
    let errorMsg = '';
    if (error.response && error.response.data) {
      errorMsg = error.response.data.message || '操作失败';
    } else {
      errorMsg = error.message || '操作失败';
    }

    console.error('错误详情:', errorMsg);
    ElMessage.error(errorMsg);
  }
}

// 从名称获取顾问ID
const getAdvisorIdFromName = (name) => {
  if (!name) return null

  // 获取顾问ID
  const advisorId = advisorIdMap.value[name]
  console.log('顾问名称:', name, '对应ID:', advisorId)

  // 首先检查映射表中是否有对应的ID
  if (advisorId) {
    return advisorId
  }

  // 如果是已知的默认顾问名称，使用固定映射
  const defaultMap = {
    '王管理老师': 1,
    '李管理老师': 2,
    '张管理老师': 3,
    '赵管理老师': 4
  }

  if (defaultMap[name]) {
    return defaultMap[name]
  }

  // 默认返回1
  return 1
}

// 重置新学生表单
const resetNewStudent = () => {
  newStudent.value = {
    id: '',
    userId: '',
    applicationId: '',
    name: '',
    englishName: '',
    gender: '男',
    birthDate: '',
    nationality: '中国',
    passportNo: '',
    grade: '',
    school: '',
    currentSchool: '',
    major: '',
    email: '',
    phone: '',
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
    advisorId: null,
    notes: '',
    consultant: ''
  }
}

// 重置筛选条件
const resetFilters = () => {
  studentFilters.value = {
    grade: '',
    status: '',
    sort: 'name'
  }
  searchQuery.value = ''
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

// 切换全选状态
const toggleAllSelection = () => {
  allSelected.value = !allSelected.value
  students.value.forEach(student => {
    student.selected = allSelected.value
  })
}

// 切换单个学生选择状态
const toggleStudentSelection = (student) => {
  student.selected = !student.selected
  // 检查是否全部选中
  allSelected.value = students.value.every(s => s.selected)
}

// 页码导航
const goToPage = (page) => {
  currentPage.value = page
}

// 搜索相关逻辑
const showSuggestions = ref(false)
const activeSuggestionIndex = ref(null)
const searchHistory = ref([])
const searchSuggestions = computed(() => {
  if (!searchQuery.value) return []

  const query = searchQuery.value.toLowerCase()
  const suggestions = []

  // 从学生列表中搜索
  const studentMatches = students.value
    .filter(student =>
      student.name.toLowerCase().includes(query) ||
      student.id.toLowerCase().includes(query) ||
      student.school.toLowerCase().includes(query)
    )
    .slice(0, 5)
    .map(student => ({
      type: 'student',
      title: student.name,
      subtitle: `${student.id} - ${student.school}`,
      data: student,
      highlight: {
        title: highlightMatch(student.name, query),
        subtitle: highlightMatch(`${student.id} - ${student.school}`, query)
      }
    }))

  if (studentMatches.length > 0) {
    suggestions.push({ type: 'divider', title: '学生匹配' })
    suggestions.push(...studentMatches)
  }

  // 从历史记录中搜索
  const historyMatches = searchHistory.value
    .filter(item => item.toLowerCase().includes(query))
    .slice(0, 3)
    .map(item => ({
      type: 'history',
      title: item,
      subtitle: '搜索历史',
      data: item,
      highlight: {
        title: highlightMatch(item, query),
        subtitle: '搜索历史'
      }
    }))

  if (historyMatches.length > 0) {
    suggestions.push({ type: 'divider', title: '搜索历史' })
    suggestions.push(...historyMatches)
  }

  return suggestions
})

// 高亮匹配文本
const highlightMatch = (text, query) => {
  if (!query) return text
  const index = text.toLowerCase().indexOf(query.toLowerCase())
  if (index === -1) return text

  return {
    before: text.slice(0, index),
    match: text.slice(index, index + query.length),
    after: text.slice(index + query.length)
  }
}

const handleSearch = () => {
  if (!searchQuery.value) {
    showSuggestions.value = false
    return
  }
  showSuggestions.value = true
  activeSuggestionIndex.value = null
}

const handleEnterSearch = (e) => {
  if (activeSuggestionIndex.value !== null && searchSuggestions.value.length > 0) {
    selectSuggestion(searchSuggestions.value[activeSuggestionIndex.value])
  } else {
    performSearch(searchQuery.value)
  }
  e.preventDefault()
}

const selectSuggestion = (suggestion) => {
  if (suggestion.type === 'student') {
    searchQuery.value = suggestion.data.name
    // 可以直接跳转到学生详情
    viewStudentDetail(suggestion.data)
  } else {
    searchQuery.value = suggestion.title
    performSearch(suggestion.title)
  }

  // 添加到搜索历史
  if (!searchHistory.value.includes(searchQuery.value)) {
    searchHistory.value.unshift(searchQuery.value)
    if (searchHistory.value.length > 10) {
      searchHistory.value.pop()
    }
  }

  showSuggestions.value = false
}

const performSearch = (query) => {
  // 执行搜索逻辑
  studentFilters.value = {
    ...studentFilters.value,
    searchQuery: query
  }

  // 添加到搜索历史
  if (!searchHistory.value.includes(query)) {
    searchHistory.value.unshift(query)
    if (searchHistory.value.length > 10) {
      searchHistory.value.pop()
    }
  }
}

// 键盘导航
const handleKeyDown = (e) => {
  if (!showSuggestions.value || searchSuggestions.value.length === 0) return

  switch (e.key) {
    case 'ArrowDown':
      e.preventDefault()
      if (activeSuggestionIndex.value === null) {
        activeSuggestionIndex.value = 0
      } else {
        activeSuggestionIndex.value = (activeSuggestionIndex.value + 1) % searchSuggestions.value.length
      }
      break
    case 'ArrowUp':
      e.preventDefault()
      if (activeSuggestionIndex.value === null) {
        activeSuggestionIndex.value = searchSuggestions.value.length - 1
      } else {
        activeSuggestionIndex.value = (activeSuggestionIndex.value - 1 + searchSuggestions.value.length) % searchSuggestions.value.length
      }
      break
    case 'Escape':
      showSuggestions.value = false
      break
  }
}

// 点击外部关闭建议框
const handleClickOutside = (e) => {
  if (!e.target.closest('.search-input-wrapper')) {
    showSuggestions.value = false
  }
}

// 监听pageSize变更
watch(pageSize, (newSize) => {
  // 确保pageSize是数值类型
  pageSize.value = parseInt(newSize)
  // 重置到第一页
  currentPage.value = 1
})

onMounted(async () => {
  await fetchAdvisors()
  await fetchStudents()
  document.addEventListener('click', handleClickOutside)
  document.addEventListener('keydown', handleKeyDown)

  // 从localStorage加载搜索历史
  try {
    const savedHistory = localStorage.getItem('studentSearchHistory')
    if (savedHistory) {
      searchHistory.value = JSON.parse(savedHistory)
    }
  } catch (error) {
    console.error('Failed to load search history:', error)
  }
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
  document.removeEventListener('keydown', handleKeyDown)

  // 保存搜索历史到localStorage
  try {
    localStorage.setItem('studentSearchHistory', JSON.stringify(searchHistory.value))
  } catch (error) {
    console.error('Failed to save search history:', error)
  }
})
</script>

<style lang="scss" scoped>
.search-box {
  position: relative;
  width: 300px;

  .search-input {
    width: 100%;
    height: 40px;
    padding: 8px 40px 8px 12px;
    border: 1px solid #d9d9d9;
    border-radius: 4px;
    background: #f5f5f5;
    transition: all 0.3s;

    &:focus {
      background: #fff;
      border-color: #40a9ff;
      box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
    }
  }

  .search-icon {
    position: absolute;
    right: 12px;
    top: 50%;
    transform: translateY(-50%);
    color: #bfbfbf;
    transition: color 0.3s;

    &:hover {
      color: #40a9ff;
    }
  }
}

.search-suggestions {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  margin-top: 4px;
  max-height: 400px;
  overflow-y: auto;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 1000;

  .search-history-divider {
    padding: 8px 12px;
    color: #8c8c8c;
    font-size: 12px;
    background: #fafafa;
  }

  .suggestion-item {
    display: flex;
    align-items: center;
    padding: 8px 12px;
    cursor: pointer;
    transition: background 0.3s;

    &:hover,
    &.active {
      background: #f0f5ff;
    }

    .suggestion-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 28px;
      height: 28px;
      margin-right: 12px;
      border-radius: 50%;
      background: #f0f0f0;

      :deep(svg) {
        width: 16px;
        height: 16px;
        color: #595959;
      }
    }

    .suggestion-content {
      flex: 1;
      min-width: 0;

      .suggestion-title {
        color: #262626;
        font-size: 14px;
        line-height: 1.5;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .suggestion-subtitle {
        color: #8c8c8c;
        font-size: 12px;
        line-height: 1.5;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
  }

  .no-suggestions {
    padding: 16px;
    text-align: center;
    color: #8c8c8c;
    font-size: 14px;
  }
}

/* 学生管理页面样式 */
.students-content {
  width: 100%;
}

.students-table {
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 24px;
  border: 1px solid rgba(0, 0, 0, 0.02);
}

.student-avatar {
  width: 40px;
  height: 40px;
  border-radius: 20px;
  /* 改为圆形 */
  overflow: hidden;
  margin-right: 12px;
  border: 2px solid #f0f0f0;
  /* 添加边框 */
  transition: all 0.3s;
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
  font-size: 18px;
  font-weight: 500;
  color: white;
  text-transform: uppercase;
}

.student-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.student-name {
  font-weight: 500;
  color: #333;
}

.student-details {
  display: flex;
  flex-direction: column;
  font-size: 12px;
  color: #999;
}

.target-countries {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.country-tag {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  background-color: #f5f7fa;
  color: #666;
  white-space: nowrap;
}

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

.status-badge.reviewing {
  background-color: #e6f7ff;
  color: #1890ff;
}

.status-badge.waiting {
  background-color: #fff7e6;
  color: #fa8c16;
}

.status-badge.passed {
  background-color: #f6ffed;
  color: #52c41a;
}

.status-badge.rejected {
  background-color: #fff1f0;
  color: #f5222d;
}

.status-badge.admitted {
  background-color: #f9f0ff;
  color: #722ed1;
}

.status-badge.graduated {
  background-color: #f5f5f5;
  color: #595959;
}

.status-badge.inactive {
  background-color: #f5f7fa;
  color: #999;
}

.count-badge {
  background-color: #f5f7fa;
  color: #666;
  font-size: 14px;
  padding: 2px 8px;
  border-radius: 12px;
  display: inline-block;
  text-align: center;
  min-width: 28px;
}

/* 分页样式 */
.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background-color: #fff;
  border-radius: 12px;
  margin-top: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.pagination {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-button {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  border: 1px solid #d9d9d9;
  cursor: pointer;
  transition: all 0.3s;
}

.pagination-button:hover:not(:disabled) {
  color: #1890ff;
  border-color: #1890ff;
}

.pagination-button:disabled {
  color: #d9d9d9;
  cursor: not-allowed;
}

.pagination-number {
  min-width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  border: 1px solid #d9d9d9;
  cursor: pointer;
  transition: all 0.3s;
  padding: 0 8px;
}

.pagination-number:hover:not(.active) {
  color: #1890ff;
  border-color: #1890ff;
}

.pagination-number.active {
  background-color: #1890ff;
  color: #fff;
  border-color: #1890ff;
}

.pagination-ellipsis {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
}

.page-size {
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-size span {
  font-size: 14px;
  color: #666;
}

.page-size .select-wrapper {
  width: 80px;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background-color: #fff;
  border-radius: 12px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-group label {
  font-size: 14px;
  color: #666;
}

.select-wrapper {
  position: relative;

  select {
    appearance: none;
    -webkit-appearance: none;
    -moz-appearance: none;
    background-color: #fff;
    border: 1px solid #d9d9d9;
    border-radius: 8px;
    padding: 8px 28px 8px 12px;
    /* 修改右内边距，为箭头留出空间 */
    font-size: 14px;
    color: #666;
    cursor: pointer;
    width: 100%;
    /* 确保选择框占满容器宽度 */

    &:focus {
      outline: none;
      border-color: #1890ff;
      box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
    }
  }

  .select-icon {
    position: absolute;
    right: 8px;
    /* 调整箭头位置 */
    top: 50%;
    transform: translateY(-50%);
    pointer-events: none;
    color: #999;
    /* 添加颜色 */
    width: 16px;
    /* 设置固定宽度 */
    height: 16px;
    /* 设置固定高度 */
  }
}

.filter-search {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-search label {
  font-size: 14px;
  color: #666;
}

.search-input-wrapper {
  position: relative;
  width: 300px;
}

.search-input-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  pointer-events: none;
  transition: color 0.3s;
}

.filter-search-input {
  width: 100%;
  padding: 10px 12px 10px 36px;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  font-size: 14px;
  color: #333;
  transition: all 0.3s;
  background-color: #f5f7fa;
}

.filter-search-input:focus {
  outline: none;
  border-color: #1890ff;
  background-color: #fff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
}

.filter-search-input:focus+.search-input-icon {
  color: #1890ff;
}

.filter-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  text-align: center;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #1890ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 10px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

.empty-data {
  text-align: center;
  padding: 20px;
  color: #999;
}
</style>
