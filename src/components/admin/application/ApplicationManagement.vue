<template>
  <div class="applications-content">

    <!-- 使用ApplicationFilter组件替换原来的筛选栏 -->
    <ApplicationFilter :initial-filters="applicationFilters" :initial-search-query="searchQuery"
      @update:filters="updateFilters" @update:search="updateSearchQuery" @reset="resetFilters" @search="handleSearch" />

    <!-- 表格视图改为左右分栏布局 -->
    <div class="applications-layout">
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>正在加载申请数据...</p>
      </div>
      <div v-else class="applications-split-view">
        <!-- 使用StudentList组件替换左侧学生列表 -->
        <StudentList :applications="applications" :selected-student="selectedStudentName"
          @select-student="selectStudent" :generate-avatar-color-fn="generateAvatarColor"
          :filters="applicationFilters" />

        <!-- 右侧申请信息 -->
        <div class="applications-detail-panel">
          <div v-if="selectedStudentName" class="panel-content">

            <div v-if="studentApplicationsLoading" class="applications-loading">
              <div class="loading-spinner"></div>
              <p>加载申请数据中...</p>
            </div>

            <div v-else class="applications-accordion">
              <!-- 使用ApplicationCard组件替换应用卡片 -->
              <ApplicationCard v-for="application in selectedStudentApplications" :key="application.id"
                :application="application" :initial-expanded="application.expanded"
                @toggle-expand="toggleApplicationExpand" @view-detail="viewApplicationDetail" @edit="editApplication"
                @delete="deleteApplication" @select-tab="selectApplicationTab" :format-date-fn="formatDate"
                :is-deadline-urgent-fn="isDeadlineUrgent" :get-remaining-days-fn="getRemainingDays"
                :get-status-class-fn="getStatusClass" :get-materials-progress-color-fn="getMaterialsProgressColor">
                <!-- 通过插槽传递各个标签页的内容 -->
                <template #basic-info>
                  <ApplicationBasicInfo :application="application" :formatDateFn="formatDate" />
                </template>
                <template #materials>
                  <ApplicationMaterials :application="application"
                    :getMaterialsProgressColorFn="getMaterialsProgressColor" />
                </template>
                <template #monitor>
                  <ApplicationMonitor :application="application" :is-deadline-urgent-fn="isDeadlineUrgent"
                    :get-remaining-days-fn="getRemainingDays" :format-date-fn="formatDate"
                    :format-time-ago-fn="formatTimeAgo" @status-updated="handleApplicationStatusUpdated" />
                </template>
                <template #result>
                  <ApplicationResult :application="application" :format-date-fn="formatDate"
                    :get-remaining-days-fn="getRemainingDays" :is-deadline-urgent-fn="isDeadlineUrgent"
                    @request-send-visa-info="handleSendVisaInfoRequest" />
                </template>
              </ApplicationCard>
            </div>

            <div v-if="selectedStudentName && !studentApplicationsLoading && selectedStudentApplications.length === 0"
              class="empty-applications">
              <FileText class="empty-icon" />
              <p>该学生暂无申请记录</p>
              <button class="btn-primary-sm" @click="showAddApplicationModal = true">
                <Plus class="btn-icon" />
                添加申请
              </button>
            </div>
          </div>

          <div v-if="!selectedStudentName" class="no-student-selected">
            <Users class="placeholder-icon" />
            <p>请从左侧选择一名学生查看申请记录</p>
          </div>
        </div>
      </div>
    </div>



    <!-- 添加申请模态框 -->
    <AddApplicationModal v-if="showAddApplicationModal" @close="showAddApplicationModal = false"
      @save="saveApplication" />

    <!-- 申请详情模态框 -->
    <ApplicationDetailModal v-if="showApplicationDetailModal && selectedApplication" :application="selectedApplication"
      @close="showApplicationDetailModal = false" @edit="editApplication" />

    <!-- 申请编辑模态框 -->
    <ApplicationEditModal v-if="showEditApplicationModal && applicationToEdit" :applicationData="applicationToEdit"
      @close="showEditApplicationModal = false" @save="saveEditedApplication" />
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { Plus, ChevronRight, Calendar, Upload, BarChart, MessageSquare, AlertTriangle, Check } from 'lucide-vue-next'
// 导入组件
import ApplicationFilter from './ApplicationFilter.vue'
import StudentList from './StudentList.vue'
import ApplicationCard from './ApplicationCard.vue'
import AddApplicationModal from './AddApplicationModal.vue'
import ApplicationDetailModal from './ApplicationDetailModal.vue'
import ApplicationEditModal from './ApplicationEditModal.vue'
import ApplicationBasicInfo from './ApplicationBasicInfo.vue'
import ApplicationMaterials from './ApplicationMaterials.vue'
import ApplicationMonitor from './ApplicationMonitor.vue'
import ApplicationResult from './ApplicationResult.vue'
import request from '../../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

// 申请管理数据
const applicationFilters = ref({
  status: '',
  country: '',
  degree: '',
  sort: 'date'
})

const searchQuery = ref('')
const studentSearchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

const applicationStatistics = ref({
  total: 0,
  completed: 0,
  processing: 0,
  pending: 0
})

const applications = ref([])
const loading = ref(false)

// 左侧学生列表相关
const selectedStudentName = ref('')

// 添加selectedStudentId状态
const selectedStudentId = ref(null);

// 统一的数据格式化函数 - 确保所有申请数据结构一致
const formatApplicationData = (app, fallbackStudentName = '') => {
  // 统一日期处理
  const createTime = app.createTime ? new Date(app.createTime) : new Date();
  const updateTime = app.updateTime ? new Date(app.updateTime) : new Date();
  const applicationDeadline = app.applicationDeadline ? new Date(app.applicationDeadline) : null;
  const entryDate = app.entryDate ? new Date(app.entryDate) : null;
  const submitTime = app.submitTime ? new Date(app.submitTime) : null;

  // 确保studentId是数字而不是字符串
  const appStudentId = Number(app.studentId);

  // 统一学生信息结构
  const studentInfo = {
    id: appStudentId,
    name: app.studentName || fallbackStudentName || '未知学生',
    email: app.studentEmail || '',
    major: app.studentMajor || '',
    avatar: null,
    // 扩展学生信息
    currentSchool: app.currentSchool || '',
    englishName: app.englishName || '',
    gender: app.gender || '',
    birthDate: app.birthDate ? new Date(app.birthDate) : null,
    nationality: app.nationality || '',
    passportNo: app.passportNo || '',
    phone: app.phone || '',
    wechat: app.wechat || '',
    // 学术信息
    gpa: app.gpa || '',
    toefl: app.toefl || '',
    ielts: app.ielts || '',
    gre: app.gre || '',
    gmat: app.gmat || ''
  };

  const englishToChineseStatus = {
    selected: '已选择学校',
    submitted: '已提交材料',
    under_review: '材料审核中',
    interview: '面试阶段',
    admitted: '已录取',
    rejected: '被拒绝',
    visa_processing: '签证申请中',
    visa_approved: '签证已批准',
    visa_rejected: '签证被拒',
    enrolled: '已入学'
  }

  const chineseToEnglishStatus = {
    '已选择学校': 'selected',
    '已提交材料': 'submitted',
    '材料审核中': 'under_review',
    '面试阶段': 'interview',
    '已录取': 'admitted',
    '被拒绝': 'rejected',
    '签证申请中': 'visa_processing',
    '签证已批准': 'visa_approved',
    '签证被拒': 'visa_rejected',
    '已入学': 'enrolled'
  }

  const englishStatus = chineseToEnglishStatus[app.applicationStatus] || app.applicationStatus || ''
  const chineseStatus = app.applicationStatusDisplay || englishToChineseStatus[englishStatus] || app.applicationStatus || '待提交'

  // 统一申请信息结构
  return {
    // 基本ID信息
    id: app.id,
    studentId: appStudentId,
    userId: app.userId,
    schoolId: app.schoolId,
    majorId: app.majorId,
    countryId: app.countryId,

    // 学生信息 - 统一使用student对象
    student: studentInfo,

    // 院校和专业信息 - 统一字段名
    schoolName: app.schoolName || '未知院校',
    institution: app.schoolName || '未知院校', // 保持向后兼容
    majorName: app.majorName || app.major || '',
    program: app.majorName || app.major || '', // 保持向后兼容

    // 地区和学位信息
    country: app.country || '',
    degree: app.degree || '',

    // 申请详细信息
    code: app.code || '',
    entryDate: entryDate,
    duration: app.duration || '',
    campus: app.campus || '',
    creditExemption: app.creditExemption || '',
    round: app.round || '',

    // 状态和进度
    status: chineseStatus,
    applicationStatus: englishStatus, // 保持向后兼容
    applicationStatusDisplay: chineseStatus,
    materialsProgress: 0, // 初始化为0，稍后异步计算

    // 时间信息 - 统一使用标准字段名
    createTime: createTime,
    updateTime: updateTime,
    lastUpdateTime: updateTime, // 保持向后兼容
    applicationDeadline: applicationDeadline,
    deadline: applicationDeadline, // 保持向后兼容
    submitTime: submitTime,

    // 其他信息
    link: app.link || '',
    notes: app.notes || '',

    // 负责人信息 - 统一字段名
    consultantName: app.userName || '',
    consultant: app.userName || '', // 保持向后兼容
    consultantEmail: app.userEmail || '',

    // 签证信息
    visaInfoProvided: app.visaInfoProvided || false,

    // UI状态
    selected: false,
    expanded: false,
    activeTab: 'info'
  };
};

// 添加局部加载状态
const studentApplicationsLoading = ref(false);

// 添加请求缓存管理
const applicationCache = ref(new Map());

// 添加isLoading变量定义
const isLoading = ref(false)

// 获取当前选中学生的申请列表
const selectedStudentApplications = computed(() => {
  if (!selectedStudentName.value || !selectedStudentId.value) return []

  console.log('当前选中的学生:', selectedStudentName.value, '学生ID:', selectedStudentId.value, '类型:', typeof selectedStudentId.value);
  console.log('所有申请列表数量:', applications.value.length);

  // 始终使用数字进行比较
  const selectedId = Number(selectedStudentId.value);

  // 找出该学生的所有申请 - 使用studentId匹配而不是名称
  const studentApps = applications.value.filter(app => {
    if (!app.student || !app.student.id) {
      console.warn('应用缺少学生ID:', app);
      return false;
    }

    // 强制转换为数字进行比较
    const appStudentId = Number(app.student.id);

    console.log(`比较: 申请学生ID=${appStudentId}, 选中学生ID=${selectedId}`);
    return appStudentId === selectedId;
  });

  console.log('过滤后的学生申请数量:', studentApps.length);
  if (studentApps.length > 0) {
    console.log('第一个应用:', studentApps[0]);
  }

  // 按创建时间倒序排列
  return studentApps.sort((a, b) => new Date(b.createTime) - new Date(a.createTime));
})

// 修改选择学生的函数，添加studentId参数
const selectStudent = (student) => {
  // 如果传入的是学生对象，就使用学生的名字和ID
  if (typeof student === 'object' && student !== null) {
    console.log('选择学生对象:', student);
    selectedStudentName.value = student.name;

    // 强制转换为数字类型
    const studentId = Number(student.id);
    selectedStudentId.value = studentId;
    console.log('设置selectedStudentId:', studentId, '类型:', typeof studentId);

    // 获取该学生的申请列表 - 使用异步加载并添加局部加载状态
    studentApplicationsLoading.value = true;
    fetchStudentApplications(studentId);
  } else {
    // 如果传入的就是字符串名字，无法获取ID，可能需要额外处理
    selectedStudentName.value = student;
    selectedStudentId.value = null;
    ElMessage.warning('无法获取学生ID，请传入完整学生对象');
  }
}

// 根据学生ID获取申请列表
const fetchStudentApplications = async (studentId) => {
  // 确保使用数字类型的学生ID
  const numericStudentId = Number(studentId);

  try {
    console.log('获取学生申请列表，学生ID:', numericStudentId, '类型:', typeof numericStudentId);

    // 检查缓存中是否已有该学生的申请数据
    if (applicationCache.value.has(numericStudentId)) {
      console.log('使用缓存的申请数据，学生ID:', numericStudentId);
      applications.value = applicationCache.value.get(numericStudentId);
      // 更新统计数据
      updateStatistics();
      studentApplicationsLoading.value = false;
      return;
    }

    // 添加请求监控
    console.log('请求URL:', '/application-school/list', '参数:', { studentId: numericStudentId });

    // 调用API获取特定学生的申请列表
    const res = await request.get('/application-school/list', {
      params: {
        studentId: numericStudentId
      }
    });
    const applicationData = res?.data || [];

    const formattedApplications = applicationData.map(app => {
      return formatApplicationData(app, selectedStudentName.value);
    });

    applicationCache.value.set(numericStudentId, formattedApplications);
    applications.value = formattedApplications;

    console.log('处理后的应用列表大小:', applications.value.length);

    applications.value.forEach(app => {
      app.expanded = false;
    });

    updateStatistics();

    const applicationsMap = new Map();
    applications.value.forEach(app => {
      if (app.student && app.student.id) {
        const studentId = Number(app.student.id);
        if (!applicationsMap.has(studentId)) {
          applicationsMap.set(studentId, []);
        }
        applicationsMap.get(studentId).push(app);
      }
    });

    applicationsMap.forEach((apps, studentId) => {
      applicationCache.value.set(studentId, apps);
    });

    if (selectedStudentId.value) {
      const currentStudentApps = applicationsMap.get(Number(selectedStudentId.value)) || [];
      if (currentStudentApps.length > 0) {
        console.log(`更新选中学生 ID: ${selectedStudentId.value} 的申请列表，共 ${currentStudentApps.length} 条`);
      }
    }
  } catch (error) {
    console.error('获取申请列表失败:', error);

    // 显示详细错误信息
    if (error.response) {
      // 服务器响应了，但状态码不在2xx范围
      console.error('错误响应:', error.response.data);
      console.error('错误状态:', error.response.status);
      ElMessage.error(`获取申请列表失败: 服务器返回 ${error.response.status}`);
    } else if (error.request) {
      // 请求已发出，但没有收到响应
      console.error('未收到响应:', error.request);
      ElMessage.error('获取申请列表失败: 服务器无响应');
    } else {
      // 设置请求时发生错误
      console.error('请求错误:', error.message);
      ElMessage.error(`获取申请列表失败: ${error.message}`);
    }

    applications.value = []; // 清空申请列表
  } finally {
    // 无论成功还是失败，都需要结束加载状态
    studentApplicationsLoading.value = false;

    // 如果获取到了新的申请数据，强制刷新视图
    if (applications.value.length > 0) {
      console.log(`学生 ${numericStudentId} 的申请列表已更新，共 ${applications.value.length} 条`);

      // 采用简单重排方式强制视图刷新
      setTimeout(() => {
        const temp = [...applications.value];
        applications.value = [];
        requestAnimationFrame(() => {
          applications.value = temp;
        });
      }, 10);
    }
  }
};

const handleApplicationStatusUpdated = async (payload) => {
  const studentId = Number(payload?.studentId || selectedStudentId.value)
  if (!studentId) return
  clearApplicationCache(studentId)
  studentApplicationsLoading.value = true
  await fetchStudentApplications(studentId)
}

// 获取申请数据
const fetchApplications = async () => {
  try {
    loading.value = true;

    // 获取当前用户ID，如果是管理员视图，可以从localStorage获取或传入管理员ID
    const userId = localStorage.getItem('userId') || 1; // 默认使用ID 1作为测试

    // 调用实际API获取申请数据，添加userId参数
    const res = await request.get('/application-school/list', {
      params: {
        userId: userId
      }
    });
    const applicationData = res?.data || [];

    applications.value = applicationData.map(app => {
      return formatApplicationData(app);
    });

    updateStatistics();

    loading.value = false;

    // 异步计算每个申请的材料进度
    updateMaterialsProgress();
  } catch (error) {
    console.error('获取申请列表失败:', error);
    ElMessage.error('获取申请列表失败，请稍后重试');
    loading.value = false;
  }
}

// 异步更新所有申请的材料进度
const updateMaterialsProgress = async () => {
  try {
    console.log('🚀 开始更新所有申请的材料进度，申请数量:', applications.value.length);

    const progressPromises = applications.value.map(async (app) => {
      console.log(`📋 处理申请 ID: ${app.id}, 学生: ${app.studentName}, 当前进度: ${app.materialsProgress}%`);
      const progress = await calculateMaterialsProgress(app);
      const oldProgress = app.materialsProgress;
      app.materialsProgress = progress;
      console.log(`✅ 申请 ${app.id} 进度更新: ${oldProgress}% → ${progress}%`);
      console.log(`🔍 验证更新后的值: ${app.materialsProgress}%`);
      return progress;
    });

    const results = await Promise.all(progressPromises);
    console.log('🎉 所有申请的材料进度更新完成，结果:', results);

    // 验证applications数组中的进度值
    console.log('📊 最终验证 - 所有申请的进度值:');
    applications.value.forEach((app, index) => {
      console.log(`  申请${index + 1} (ID: ${app.id}, 学生: ${app.studentName}): ${app.materialsProgress}%`);
    });
  } catch (error) {
    console.error('💥 更新材料进度失败:', error);
  }
}

// 根据实际材料上传情况计算材料进度
const calculateMaterialsProgress = async (app) => {
  try {
    console.log('🔍 开始计算材料进度 - 学生ID:', app.studentId, '申请ID:', app.id);

    // 如果没有学生ID或申请ID，返回0
    if (!app.studentId || !app.id) {
      console.log('❌ 缺少学生ID或申请ID');
      return 0;
    }

    // 获取学生的材料状态
    const apiUrl = `/api/files/admin/materials/student/${app.studentId}/application/${app.id}`;
    console.log('📡 调用API:', apiUrl);

    const response = await fetch(apiUrl);
    console.log('📡 API响应状态:', response.status, response.statusText);

    if (!response.ok) {
      console.warn('⚠️ API响应失败，状态码:', response.status);
      return 0;
    }

    const result = await response.json();
    console.log('📊 API返回完整数据:', JSON.stringify(result, null, 2));

    if (!result.success || !result.data) {
      console.log('❌ API返回数据无效 - success:', result.success, 'data:', result.data);
      return 0;
    }

    // 定义必需的材料类型（除了最后两个"学术论文"和"其他材料"外都是必须的）
    // 注意：这些字段名必须与后端API返回的字段名完全一致
    const requiredMaterials = [
      'passport',           // 护照
      'id_card',           // 身份证  
      'transcript',        // 成绩单
      'language_scores',   // 语言成绩
      'personal_statement', // 个人陈述
      'recommendation_letter', // 推荐信
      'resume'             // 简历
    ];

    console.log('📋 检查的必需材料:', requiredMaterials);
    console.log('📊 材料数据结构:', Object.keys(result.data));

    // 计算已上传的必需材料数量
    let uploadedCount = 0;
    requiredMaterials.forEach(materialType => {
      const materialData = result.data[materialType];
      console.log(`📄 ${materialType}:`, materialData);

      if (materialData && materialData.uploaded) {
        uploadedCount++;
        console.log(`✅ ${materialType} 已上传`);
      } else {
        console.log(`❌ ${materialType} 未上传 - 数据:`, materialData);
      }
    });

    console.log(`📊 已上传材料数量: ${uploadedCount}/${requiredMaterials.length}`);

    // 简化逻辑：7个材料全部上传就显示100%，否则显示实际百分比
    let progress;
    if (uploadedCount === requiredMaterials.length) {
      progress = 100;
      console.log('🎉 所有材料已上传，进度100%');
    } else {
      progress = Math.round((uploadedCount / requiredMaterials.length) * 100);
      console.log(`📊 计算进度: ${progress}%`);
    }

    return progress;
  } catch (error) {
    console.error('💥 计算材料进度失败:', error);
    return 0;
  }
}

// 保存申请信息
const saveApplication = async (application) => {
  try {
    isLoading.value = true

    console.log('准备保存申请数据:', application)

    // 确保日期格式正确
    // 创建请求对象
    const applicationData = {
      ...application
    }

    // 处理deadline日期
    if (applicationData.deadline) {
      try {
        // 确保日期格式为 yyyy-MM-dd'T'HH:mm:ss.SSSXXX
        if (typeof applicationData.deadline === 'string') {
          const dateObj = new Date(applicationData.deadline)
          applicationData.applicationDeadline = dateObj.toISOString()
        } else if (applicationData.deadline instanceof Date) {
          applicationData.applicationDeadline = applicationData.deadline.toISOString()
        }
      } catch (dateError) {
        console.error('日期格式转换错误:', dateError)
      }
    }

    // 处理entryDate日期
    if (applicationData.entryDate) {
      try {
        if (typeof applicationData.entryDate === 'string') {
          const dateObj = new Date(applicationData.entryDate)
          applicationData.entryDate = dateObj.toISOString()
        } else if (applicationData.entryDate instanceof Date) {
          applicationData.entryDate = applicationData.entryDate.toISOString()
        }
      } catch (dateError) {
        console.error('入学日期格式转换错误:', dateError)
      }
    }

    // 处理学生信息
    if (applicationData.student) {
      const student = applicationData.student

      // 验证学生对象是否有效
      if (!student.id) {
        console.error('学生对象缺少ID');
        ElMessage.warning('学生数据不完整，请重新选择学生');
        isLoading.value = false;
        return;
      }

      // 处理学生生日
      if (student.birthDate) {
        try {
          if (typeof student.birthDate === 'string') {
            const dateObj = new Date(student.birthDate)
            student.birthDate = dateObj.toISOString()
          } else if (student.birthDate instanceof Date) {
            student.birthDate = student.birthDate.toISOString()
          }
        } catch (dateError) {
          console.error('学生生日格式转换错误:', dateError)
        }
      }

      // 复制学生信息到申请对象
      applicationData.studentName = student.name
      applicationData.studentEmail = student.email
      applicationData.englishName = student.englishName
      applicationData.gender = student.gender
      applicationData.nationality = student.nationality
      applicationData.passportNo = student.passportNo
      applicationData.phone = student.phone
      applicationData.wechat = student.wechat
      applicationData.currentSchool = student.currentSchool
      applicationData.studentMajor = student.major
      applicationData.gpa = student.gpa

      // 设置studentId
      applicationData.studentId = student.id
      console.log('设置学生ID:', applicationData.studentId)

      // 如果学生数据中包含userId，优先使用它
      if (student.userId) {
        applicationData.userId = student.userId
        console.log('使用学生关联的userId:', applicationData.userId)
      }
    } else {
      console.error('缺少学生信息');
      ElMessage.warning('请选择一个学生');
      isLoading.value = false;
      return;
    }

    // 设置状态
    applicationData.applicationStatus = applicationData.status

    // 添加 userId 逻辑
    // 从全局状态或本地存储中获取用户ID
    try {
      // 首先尝试使用学生关联的用户ID
      if (applicationData.student && applicationData.student.userId) {
        applicationData.userId = applicationData.student.userId;
        console.log('使用学生关联的用户ID:', applicationData.userId);
      } else {
        // 尝试获取当前登录用户的ID
        const userInfoStr = localStorage.getItem('userInfo')
        if (userInfoStr) {
          const userInfo = JSON.parse(userInfoStr)
          if (userInfo && userInfo.id) {
            applicationData.userId = userInfo.id
            console.log('使用本地存储的用户ID:', applicationData.userId);
          } else {
            // 默认使用1号用户ID（通常是管理员）或与学生ID相同的值
            applicationData.userId = applicationData.studentId || 1
            console.warn('未能从用户信息中获取ID，使用studentId或默认值:', applicationData.userId)
          }
        } else {
          // 默认使用1号用户ID（通常是管理员）或与学生ID相同的值
          applicationData.userId = applicationData.studentId || 1
          console.warn('未找到用户信息，使用studentId或默认值:', applicationData.userId)
        }
      }
    } catch (error) {
      console.error('处理用户ID时出错:', error)
      // 默认使用1号用户ID（通常是管理员）或与学生ID相同的值
      applicationData.userId = applicationData.studentId || 1
      console.warn('异常情况下，使用studentId或默认值:', applicationData.userId)
    }

    // 添加 countryId 逻辑
    // 使用硬编码的国家ID映射，实际情况应该从API获取
    const countryMap = {
      '美国': 1,
      '英国': 2,
      '加拿大': 3,
      '澳大利亚': 4,
      '新西兰': 5,
      '中国香港': 6,
      '新加坡': 7
    }

    // 根据国家名称设置国家ID
    if (applicationData.country && countryMap[applicationData.country]) {
      applicationData.countryId = countryMap[applicationData.country]
    } else {
      // 如果找不到匹配的国家ID，默认设置为1
      applicationData.countryId = 1
      console.warn(`未找到国家 "${applicationData.country}" 对应的ID，使用默认值1`)
    }

    console.log('发送到后端的申请数据:', applicationData)

    // 最后检查关键字段
    const requiredFields = [
      { field: 'schoolId', name: '学校ID' },
      { field: 'majorId', name: '专业ID' },
      { field: 'studentId', name: '学生ID' },
      { field: 'countryId', name: '国家ID' },
      { field: 'userId', name: '用户ID' }
    ]

    let missingFields = []
    for (const { field, name } of requiredFields) {
      if (!applicationData[field]) {
        missingFields.push(name)
      }
    }

    if (missingFields.length > 0) {
      const errorMessage = `缺少必填字段: ${missingFields.join(', ')}`
      console.error(errorMessage)
      ElMessage.error(errorMessage)
      isLoading.value = false
      return
    }

    // 清理数据，只保留数据库表中的字段
    const cleanedData = {
      // 必要的ID字段
      userId: applicationData.userId,
      schoolId: applicationData.schoolId,
      majorId: applicationData.majorId,
      countryId: applicationData.countryId,
      studentId: applicationData.studentId,

      // 名称字段
      schoolName: applicationData.institution || applicationData.schoolName,
      majorName: applicationData.program || applicationData.majorName,
      country: applicationData.country,

      // 申请相关信息
      degree: applicationData.degree,
      code: applicationData.code || '',
      entryDate: applicationData.entryDate,
      duration: applicationData.duration || '',
      campus: applicationData.campus || '',
      creditExemption: applicationData.creditExemption || '',
      applicationStatus: applicationData.applicationStatus,
      link: applicationData.link || '',
      applicationDeadline: applicationData.applicationDeadline,

      // 提交时间字段
      submitTime: new Date().toISOString()

      // create_time 和 update_time 由后端自动生成
    }

    // 打印清理后的申请数据
    console.log('清理后的申请数据:', cleanedData)

    // 最后检查countryId是否已设置
    if (!cleanedData.countryId) {
      console.error('缺少countryId，将尝试再次设置');

      // 根据country字段重新计算countryId
      const countryMap = {
        '美国': 1,
        '英国': 2,
        '加拿大': 3,
        '澳大利亚': 4,
        '新西兰': 5,
        '中国香港': 6,
        '新加坡': 7
      };

      if (cleanedData.country && countryMap[cleanedData.country]) {
        cleanedData.countryId = countryMap[cleanedData.country];
        console.log('已重设countryId:', cleanedData.countryId);
      } else {
        cleanedData.countryId = 1; // 默认值
        console.warn('无法根据country确定countryId，使用默认值1');
      }
    }

    // 发送请求
    console.log('准备发送 POST 请求到 /api/application-school/submit')
    const response = await axios.post('/api/application-school/submit', cleanedData, {
      headers: {
        'Content-Type': 'application/json'
      }
    })

    console.log('提交申请响应:', response)

    if (response.data && response.data.code === 200) {
      ElMessage.success('申请创建成功')

      // 关闭弹窗
      showAddApplicationModal.value = false;

      // 刷新并切换到新创建的申请
      await refreshAfterSave(applicationData);
    } else {
      ElMessage.error(response.data?.message || '创建申请失败')
    }
  } catch (error) {
    console.error('创建申请时出错:', error)

    // 详细记录错误信息
    if (error.response) {
      console.error('服务器响应错误:', {
        status: error.response.status,
        data: error.response.data,
        headers: error.response.headers
      })
      // 提取并显示更详细的错误信息
      let errorMessage = '创建失败: ';
      if (error.response.data) {
        if (error.response.data.message) {
          errorMessage += error.response.data.message;
        } else if (error.response.data.error) {
          errorMessage += error.response.data.error;
        } else if (typeof error.response.data === 'string') {
          errorMessage += error.response.data;
        } else {
          errorMessage += JSON.stringify(error.response.data);
        }
      } else {
        errorMessage += error.message;
      }
      ElMessage.error(errorMessage);
    } else if (error.request) {
      console.error('请求未收到响应:', error.request)
      ElMessage.error('服务器无响应，请检查网络连接')
    } else {
      console.error('请求配置错误:', error.message)
      ElMessage.error(`请求错误: ${error.message}`)
    }
  } finally {
    isLoading.value = false
  }
}

// 删除申请
const deleteApplication = (application) => {
  ElMessageBox.confirm(
    `确定要删除申请 ${application.id} 吗？此操作不可逆。`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
      try {
        // 调用后端API删除申请
        const res = await request.delete(`/application-school/${application.id}`);

        if (res && (res.code === 200 || res.success === true)) {
          // 清除该学生的缓存
          if (application.student && application.student.id) {
            clearApplicationCache(application.student.id);
          }

          // 删除成功后重新获取数据，或从本地列表中移除
          const index = applications.value.findIndex(item => item.id === application.id);
          if (index !== -1) {
            applications.value.splice(index, 1);
            // 更新统计数据
            updateStatistics();
          }
          ElMessage.success('删除成功');
        } else {
          ElMessage.error(res?.message || '删除失败');
        }
      } catch (error) {
        console.error('删除申请失败:', error);
        ElMessage.error('删除申请失败，请稍后重试');
      }
    })
    .catch(() => {
      // 取消删除
    });
}

// 处理来自 ApplicationResult 的发送签证信息请求
const handleSendVisaInfoRequest = async (application) => {
  console.log(`处理发送签证信息请求，学生: ${application.student.name}, 申请ID: ${application.id}`);

  try {
    // 调用后端API更新签证信息状态
    const res = await request.put(`/application-school/${application.id}/visa-info`, null, {
      params: { visaInfoProvided: true }
    });

    if (res && (res.code === 200 || res.success === true)) {
      // 清除该学生的缓存
      if (application.student && application.student.id) {
        clearApplicationCache(application.student.id);
      }

      // 更新本地数据状态
      const originalApplication = applications.value.find(app => app.id === application.id);
      if (originalApplication) {
        originalApplication.visaInfoProvided = true;
        application.visaInfoProvided = true;
      }
      ElMessage.success(`已为 ${application.student.name} 的申请 (ID: ${application.id}) 标记为已提供签证信息。`);
    } else {
      ElMessage.error(res?.message || '更新签证信息状态失败');
    }
  } catch (error) {
    console.error('更新签证信息状态失败:', error);
    ElMessage.error('更新签证信息状态失败，请稍后重试');
  }
}

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

// 获取材料进度颜色
const getMaterialsProgressColor = (progress) => {
  if (progress < 30) return '#f5222d'
  if (progress < 70) return '#fa8c16'
  return '#52c41a'
}

// 判断截止日期是否紧急
const isDeadlineUrgent = (deadline) => {
  if (!deadline) return false; // Add a guard for null/undefined deadlines
  const now = new Date();
  const deadlineDate = new Date(deadline);
  // Ensure deadlineDate is valid before proceeding
  if (isNaN(deadlineDate.getTime())) return false;
  const diffDays = Math.ceil((deadlineDate - now) / (1000 * 60 * 60 * 24));
  return diffDays >= 0 && diffDays <= 14; // 14 days is the URGENT_THRESHOLD_DAYS
}

// 获取剩余天数
const getRemainingDays = (deadline) => {
  const now = new Date()
  const deadlineDate = new Date(deadline)
  return Math.max(0, Math.ceil((deadlineDate - now) / (1000 * 60 * 60 * 24)))
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '未设置';

  const date = new Date(dateStr);
  if (isNaN(date.getTime())) return '日期无效';

  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');

  return `${year}-${month}-${day}`;
};

// 添加申请模态框
const showAddApplicationModal = ref(false)

// 查看申请详情
const showApplicationDetailModal = ref(false)
const selectedApplication = ref(null)

// 编辑申请
const showEditApplicationModal = ref(false)
const applicationToEdit = ref(null)

// 筛选申请
const filteredApplications = computed(() => {
  let result = [...applications.value]

  // 应用筛选条件
  if (applicationFilters.value.status) {
    result = result.filter(app => app.status === applicationFilters.value.status)
  }

  if (applicationFilters.value.country) {
    result = result.filter(app => app.country === applicationFilters.value.country)
  }

  if (applicationFilters.value.degree) {
    result = result.filter(app => app.degree === applicationFilters.value.degree)
  }

  // 应用搜索查询
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(app =>
      app.id.toLowerCase().includes(query) ||
      app.student.name.toLowerCase().includes(query) ||
      app.institution.toLowerCase().includes(query) ||
      app.program.toLowerCase().includes(query)
    )
  }

  // 应用排序
  if (applicationFilters.value.sort === 'date') {
    result.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
  } else if (applicationFilters.value.sort === 'deadline') {
    result.sort((a, b) => new Date(a.deadline) - new Date(b.deadline))
  } else if (applicationFilters.value.sort === 'student') {
    result.sort((a, b) => a.student.name.localeCompare(b.student.name))
  } else if (applicationFilters.value.sort === 'school') {
    result.sort((a, b) => a.institution.localeCompare(b.institution))
  }

  return result
})

// 分页相关计算属性
const totalPages = computed(() => {
  return Math.ceil(filteredApplications.value.length / pageSize.value) || 1
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

const paginatedApplications = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredApplications.value.slice(start, end)
})

// 全选状态
const allSelected = ref(false)

// 查看申请详情
const viewApplicationDetail = (application) => {
  selectedApplication.value = application
  showApplicationDetailModal.value = true
}

// 编辑申请
const editApplication = (application) => {
  applicationToEdit.value = { ...application }
  showEditApplicationModal.value = true
}

// 保存编辑后的申请信息
const saveEditedApplication = async (application) => {
  try {
    isLoading.value = true

    console.log('准备保存编辑后的申请数据:', application)

    // 确保日期格式正确
    // 创建请求对象
    const applicationData = {
      ...application
    }

    // 处理deadline日期
    if (applicationData.deadline) {
      try {
        if (typeof applicationData.deadline === 'string') {
          const dateObj = new Date(applicationData.deadline)
          applicationData.applicationDeadline = dateObj.toISOString()
        } else if (applicationData.deadline instanceof Date) {
          applicationData.applicationDeadline = applicationData.deadline.toISOString()
        }
      } catch (dateError) {
        console.error('日期格式转换错误:', dateError)
      }
    }

    // 处理entryDate日期
    if (applicationData.entryDate) {
      try {
        if (typeof applicationData.entryDate === 'string') {
          const dateObj = new Date(applicationData.entryDate)
          applicationData.entryDate = dateObj.toISOString()
        } else if (applicationData.entryDate instanceof Date) {
          applicationData.entryDate = applicationData.entryDate.toISOString()
        }
      } catch (dateError) {
        console.error('入学日期格式转换错误:', dateError)
      }
    }

    // 清理数据，只保留数据库表中的字段
    const cleanedData = {
      // 申请ID
      id: applicationData.id,

      // 必要的ID字段
      userId: applicationData.userId,
      schoolId: applicationData.schoolId,
      majorId: applicationData.majorId,
      countryId: applicationData.countryId || (applicationData.country &&
        (['美国', '英国', '加拿大', '澳大利亚', '新西兰', '中国香港', '新加坡'].indexOf(applicationData.country) + 1) || 1),
      studentId: applicationData.studentId,

      // 名称和显示字段
      schoolName: applicationData.institution || applicationData.schoolName,
      majorName: applicationData.program || applicationData.majorName,
      country: applicationData.country,
      major: applicationData.program,

      // 申请相关信息
      degree: applicationData.degree,
      code: applicationData.code || '',
      entryDate: applicationData.entryDate,
      duration: applicationData.duration || '',
      campus: applicationData.campus || '',
      creditExemption: applicationData.creditExemption || '',
      applicationStatus: applicationData.applicationStatus || applicationData.status,
      link: applicationData.link || '',
      applicationDeadline: applicationData.applicationDeadline,

      // 提交时间字段保持原样，不需要更新
    }

    console.log('清理后的编辑申请数据:', cleanedData)

    // 发送请求
    const response = await axios.put(`/api/application-school/${applicationData.id}`, cleanedData, {
      headers: {
        'Content-Type': 'application/json'
      }
    })

    console.log('编辑申请响应:', response)

    if (response.data && response.data.code === 200) {
      ElMessage.success('申请更新成功')
      // 刷新列表
      fetchApplications()
      // 关闭弹窗
      showEditApplicationModal.value = false
    } else {
      ElMessage.error(response.data?.message || '更新申请失败')
    }
  } catch (error) {
    console.error('更新申请时出错:', error)

    // 详细记录错误信息
    if (error.response) {
      console.error('服务器响应错误:', {
        status: error.response.status,
        data: error.response.data,
        headers: error.response.headers
      })
      // 提取并显示更详细的错误信息
      let errorMessage = '更新失败: ';
      if (error.response.data) {
        if (error.response.data.message) {
          errorMessage += error.response.data.message;
        } else if (error.response.data.error) {
          errorMessage += error.response.data.error;
        } else if (typeof error.response.data === 'string') {
          errorMessage += error.response.data;
        } else {
          errorMessage += JSON.stringify(error.response.data);
        }
      } else {
        errorMessage += error.message;
      }
      ElMessage.error(errorMessage);
    } else if (error.request) {
      console.error('请求未收到响应:', error.request)
      ElMessage.error('服务器无响应，请检查网络连接')
    } else {
      console.error('请求配置错误:', error.message)
      ElMessage.error(`请求错误: ${error.message}`)
    }
  } finally {
    isLoading.value = false
  }
}

// 更新统计数据
const updateStatistics = () => {
  applicationStatistics.value.total = applications.value.length
  applicationStatistics.value.completed = applications.value.filter(a =>
    a.status === '录取' || a.status === '已完成'
  ).length
  applicationStatistics.value.processing = applications.value.filter(a =>
    a.status === '已提交' || a.status === '材料审核中' || a.status === '面试中'
  ).length
  applicationStatistics.value.pending = applications.value.filter(a =>
    a.status === '待提交'
  ).length
}

// 重置筛选条件
const resetFilters = () => {
  applicationFilters.value = {
    status: '',
    country: '',
    degree: '',
    sort: 'date'
  }
  searchQuery.value = ''
  // 可以在这里添加重新获取数据的逻辑
}

// 更新筛选条件
const updateFilters = (newFilters) => {
  applicationFilters.value = newFilters
}

// 更新搜索查询
const updateSearchQuery = (newQuery) => {
  searchQuery.value = newQuery
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

// 页码导航
const goToPage = (page) => {
  currentPage.value = page
}

// 搜索处理
const handleSearch = () => {
  // 重置分页
  currentPage.value = 1
}

// 选择申请详情标签页
const selectApplicationTab = (application, tabName) => {
  console.log("选择标签页", application.id, tabName);
  // 找到并更新应用
  const app = applications.value.find(a => a.id === application.id);
  if (app) {
    app.activeTab = tabName;
  }
}

// 应用筛选条件
const applyFilterOptions = (options) => {
  if (options.filters) {
    applicationFilters.value = options.filters;
  }

  if (options.searchQuery !== undefined) {
    searchQuery.value = options.searchQuery;
  }

  // 重置分页
  currentPage.value = 1;
}

// 格式化时间为"几天前"的格式
const formatTimeAgo = (date) => {
  if (!date) return '未知';

  const now = new Date();
  const diffTime = Math.abs(now - new Date(date));
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

  if (diffDays === 0) return '今天';
  if (diffDays === 1) return '昨天';
  if (diffDays < 7) return `${diffDays}天前`;
  if (diffDays < 30) return `${Math.floor(diffDays / 7)}周前`;
  if (diffDays < 365) return `${Math.floor(diffDays / 30)}个月前`;
  return `${Math.floor(diffDays / 365)}年前`;
};

// 需要在保存、删除或更新申请后清除相关缓存
const clearApplicationCache = (studentId) => {
  if (studentId) {
    applicationCache.value.delete(Number(studentId));
  } else {
    // 如果没有指定学生ID，清除所有缓存
    applicationCache.value.clear();
  }
}

onMounted(() => {
  // 加载全部申请数据
  fetchApplications();
});

// 另外，删除之前添加的错误代码块

// 添加一个自动刷新函数，用于保证保存新申请后能显示出来
const refreshAfterSave = async (newApplication) => {
  // 清除所有缓存
  applicationCache.value.clear();

  // 刷新全局申请列表
  await fetchApplications();

  // 如果有学生信息，选择该学生查看申请列表
  if (newApplication.student && newApplication.student.id) {
    // 选择该学生
    selectStudent(newApplication.student);

    // 短暂延迟后刷新学生申请列表
    setTimeout(async () => {
      await fetchStudentApplications(newApplication.student.id);

      // 确保视图更新
      setTimeout(() => {
        ElMessage.success('申请已创建并显示在列表中');
      }, 500);
    }, 200);
  }
}
</script>

<style lang="scss" scoped>
.applications-content {
  width: 100%;
  padding-top: 0;
  margin-top: 0;
}

.applications-table {
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 24px;
  border: 1px solid rgba(0, 0, 0, 0.02);
}

.checkbox-icon {
  width: 18px;
  height: 18px;
  border-radius: 4px;
  border: 1px solid #d9d9d9;
  background-color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  margin-right: 8px;
  transition: all 0.2s;
}

.checkbox-icon.checked {
  background-color: #1890ff;
  border-color: #1890ff;
}

.checkbox-icon .check-icon {
  color: white;
  width: 14px;
  height: 14px;
}

.th-content {
  display: flex;
  align-items: center;
}

.td-content {
  display: flex;
  align-items: center;
}

.application-info {
  display: flex;
  flex-direction: column;
}

.application-id {
  font-weight: 500;
  color: #1890ff;
  margin-bottom: 4px;
}

.application-type {
  font-size: 12px;
  color: #999;
}

.student-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.student-avatar {
  width: 36px;
  height: 36px;
  border-radius: 18px;
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
  font-size: 16px;
  font-weight: 500;
  color: white;
  text-transform: uppercase;
}

.student-name {
  font-weight: 500;
  color: #333;
}

.institution-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.institution-name {
  font-weight: 500;
  color: #333;
}

.program-name {
  font-size: 12px;
  color: #666;
}

.country-tag {
  display: inline-block;
  font-size: 12px;
  padding: 2px 8px;
  background-color: #f5f7fa;
  color: #666;
  border-radius: 12px;
  margin-top: 4px;
  width: fit-content;
}

.materials-progress {
  width: 100%;
}

.progress-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.progress-bar {
  flex: 1;
  height: 6px;
  background-color: #f0f0f0;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 3px;
}

.progress-text {
  font-size: 12px;
  color: #666;
  width: 30px;
  text-align: right;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  display: inline-block;
}

.status-badge.pending {
  background-color: #fff7e6;
  color: #fa8c16;
}

.status-badge.submitted {
  background-color: #e6f7ff;
  color: #1890ff;
}

.status-badge.processing {
  background-color: #f0f5ff;
  color: #2f54eb;
}

.status-badge.interviewing {
  background-color: #f9f0ff;
  color: #722ed1;
}

.status-badge.admitted {
  background-color: #f6ffed;
  color: #52c41a;
}

.status-badge.rejected {
  background-color: #fff1f0;
  color: #f5222d;
}

.status-badge.completed {
  background-color: #f5f5f5;
  color: #595959;
}

.status-badge.visa-processing {
  background-color: #fff0f6;
  color: #eb2f96;
}

.status-badge.visa-approved {
  background-color: #f0f9ff;
  color: #1677ff;
}

.status-badge.visa-rejected {
  background-color: #fff1f0;
  color: #ff4d4f;
}

.status-badge.enrolled {
  background-color: #f6ffed;
  color: #389e0d;
}

.deadline-info {
  color: #333;
}

.deadline-info.urgent {
  color: #f5222d;
  font-weight: 500;
}

.deadline-days {
  background-color: #fff1f0;
  color: #f5222d;
  padding: 1px 6px;
  border-radius: 10px;
  font-size: 12px;
  margin-left: 4px;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.loading-spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #1890ff;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
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
  padding: 40px;
  color: #999;
}

/* 表格样式 */
table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background-color: #fafafa;
}

th {
  padding: 12px 16px;
  text-align: left;
  font-weight: 600;
  color: #666;
  border-bottom: 1px solid #f0f0f0;
}

td {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  vertical-align: middle;
}

tr:hover {
  background-color: #fafafa;
}

/* 筛选栏 */
.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  padding: 16px;
  background-color: #fff;
  border-radius: 12px;
  margin-top: 0;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  align-items: center;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-group label {
  font-size: 14px;
  color: #666;
  white-space: nowrap;
}

.select-wrapper {
  position: relative;
}

.select-wrapper select {
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
  background-color: #fff;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  padding: 8px 32px 8px 12px;
  font-size: 14px;
  color: #333;
  min-width: 120px;
  cursor: pointer;
}

.select-wrapper select:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
}

.select-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none;
  color: #999;
}

.filter-search {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-grow: 1;
}

.search-input-wrapper {
  position: relative;
  flex-grow: 1;
}

.search-input-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
}

.filter-search-input {
  width: 100%;
  padding: 8px 12px 8px 36px;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  font-size: 14px;
}

.filter-search-input:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
}

.filter-actions {
  display: flex;
  gap: 8px;
}

/* 分页 */
.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.table-info {
  font-size: 14px;
  color: #666;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-button {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  background-color: white;
  cursor: pointer;
  transition: all 0.3s;
}

.pagination-button:hover:not(:disabled) {
  border-color: #1890ff;
  color: #1890ff;
}

.pagination-button:disabled {
  cursor: not-allowed;
  color: #d9d9d9;
}

.pagination-number {
  min-width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  background-color: white;
  cursor: pointer;
  transition: all 0.3s;
  padding: 0 8px;
}

.pagination-number:hover:not(.active) {
  border-color: #1890ff;
  color: #1890ff;
}

.pagination-number.active {
  background-color: #1890ff;
  color: white;
  border-color: #1890ff;
}

.pagination-ellipsis {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 32px;
  padding: 0 8px;
  color: #999;
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

.page-size .select-wrapper select {
  min-width: 80px;
}

.btn-icon-sm {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  background-color: transparent;
  border: none;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-icon-sm:hover {
  background-color: #f5f7fa;
  color: #1890ff;
}

.applications-layout {
  display: flex;
  flex-direction: row;
  gap: 24px;
  margin-top: 0;
  padding-top: 0;
}

.applications-split-view {
  display: flex;
  gap: 24px;
  width: 100%;
}

.students-list-panel {
  flex: 0 0 300px;
  /* 固定宽度 */
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(0, 0, 0, 0.02);
  height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
}

.panel-header {
  padding: 16px;
  background-color: #fafafa;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-input {
  width: 100%;
  padding: 8px;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
}

.search-icon {
  color: #999;
}

.students-list {
  padding: 8px;
  overflow-y: auto;
  flex: 1;
}

.student-list-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.2s;
  border-radius: 8px;
  margin-bottom: 4px;
  border-left: 3px solid transparent;
  /* 添加透明边框，保持一致的内边距 */
}

.student-list-item:hover {
  background-color: #f5f7fa;
}

.student-list-item.active {
  background-color: #e6f7ff;
  border-left: 3px solid transparent;
  /* 修改为透明，移除选中色条 */
}

.applications-detail-panel {
  flex: 1;
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(0, 0, 0, 0.02);
  height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
}

.panel-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.applications-accordion {
  padding: 16px;
  overflow-y: auto;
  flex: 1;
  /* 隐藏滚动条 */
  scrollbar-width: none;
  /* Firefox */
  -ms-overflow-style: none;
  /* IE and Edge */
}

.applications-accordion::-webkit-scrollbar {
  display: none;
  /* Chrome, Safari, Opera */
}

.empty-list,
.empty-applications,
.no-student-selected {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #999;
  text-align: center;
  height: 100%;
}

.empty-icon,
.placeholder-icon {
  width: 48px;
  height: 48px;
  color: #d9d9d9;
  margin-bottom: 16px;
}

/* 媒体查询以适应不同屏幕尺寸 */
@media (max-width: 1200px) {
  .applications-split-view {
    flex-direction: column;
  }

  .students-list-panel,
  .applications-detail-panel {
    flex: none;
    height: auto;
    max-height: 50vh;
  }
}

@media (max-width: 768px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }

  .application-card-actions {
    flex-direction: column;
  }

  .btn-outline-sm,
  .btn-primary-sm,
  .btn-danger-sm {
    width: 100%;
  }
}

.panel-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  white-space: nowrap;
}

.panel-title::before {
  display: none;
}

/* 申请材料项目 */
.materials-progress-wrapper {
  margin-bottom: 16px;
}

.progress-header {
  margin-bottom: 16px;
}

.progress-header h4 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 12px;
}

.btn-text-sm {
  display: inline-flex;
  align-items: center;
  height: 28px;
  padding: 0 8px;
  border-radius: 4px;
  font-size: 12px;
  background-color: transparent;
  color: #1890ff;
  border: none;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-text-sm:hover {
  background-color: #e6f7ff;
}

.materials-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.material-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 12px;
  background-color: #fafafa;
  border-radius: 8px;
  transition: box-shadow 0.3s;
}

.material-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.material-status {
  width: 24px;
  height: 24px;
  border-radius: 12px;
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
  width: 14px;
  height: 14px;
}

.material-content {
  flex: 1;
  min-width: 0;
}

.material-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.material-title {
  font-weight: 500;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.material-badge {
  padding: 2px 6px;
  border-radius: 10px;
  font-size: 12px;
  white-space: nowrap;
}

.material-badge.completed {
  background-color: #f6ffed;
  color: #52c41a;
}

.material-badge.processing {
  background-color: #fff7e6;
  color: #fa8c16;
}

.material-badge.pending {
  background-color: #fff1f0;
  color: #f5222d;
}

.material-deadline {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #666;
}

.mini-icon {
  width: 12px;
  height: 12px;
  color: #999;
}

/* 申请进度部分 */
.application-progress-section {
  margin-bottom: 24px;
  border-top: 1px solid #f0f0f0;
  padding-top: 16px;
}

.application-timeline {
  position: relative;
  margin: 30px 0;
  padding: 20px 10px;
}

.timeline-horizontal {
  display: flex;
  align-items: flex-start;
  position: relative;
  min-height: 200px;
}

.timeline-item {
  position: relative;
  flex: 1;
  text-align: center;
  padding: 0 10px;
}

.timeline-status {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  position: relative;
  z-index: 3;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  border: 3px solid #fff;
}

.status-icon {
  width: 20px;
  height: 20px;
  color: #fff;
}

.timeline-status.completed {
  background-color: #52c41a;
}

.timeline-status.current {
  background-color: #1890ff;
}

.timeline-status.pending {
  background-color: #d9d9d9;
}

.timeline-connector {
  position: absolute;
  top: 18px;
  left: 50%;
  width: 100%;
  height: 3px;
  background-color: #f0f0f0;
  z-index: 1;
}

.timeline-item:last-child .timeline-connector {
  display: none;
}

.timeline-connector.completed {
  background-color: #52c41a;
}

.timeline-connector.current {
  background-color: #1890ff;
}

.timeline-content {
  padding-top: 10px;
}

.timeline-title {
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  font-size: 14px;
}

.timeline-date {
  color: #666;
  font-size: 12px;
  margin-bottom: 8px;
  font-weight: 500;
}

.timeline-description {
  color: #666;
  font-size: 13px;
  margin-bottom: 12px;
}

.timeline-details {
  background-color: #f9f9f9;
  padding: 10px;
  border-radius: 8px;
  text-align: left;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.timeline-details .detail-item {
  margin-bottom: 6px;
  font-size: 12px;
  display: flex;
}

.timeline-details .detail-label {
  font-weight: 500;
  color: #666;
  margin-right: 5px;
  flex-shrink: 0;
}

.timeline-details .detail-value {
  color: #333;
  flex: 1;
}

.timeline-item.completed .timeline-title {
  color: #52c41a;
}

.timeline-item.current .timeline-title {
  color: #1890ff;
}

@media (max-width: 768px) {
  .timeline-horizontal {
    flex-direction: column;
    align-items: flex-start;
  }

  .timeline-item {
    width: 100%;
    text-align: left;
    padding: 0 0 20px 40px;
  }

  .timeline-status {
    position: absolute;
    left: 0;
    top: 0;
    margin: 0;
  }

  .timeline-connector {
    position: absolute;
    top: 36px;
    left: 18px;
    width: 3px;
    height: calc(100% - 16px);
  }

  .timeline-title,
  .timeline-date,
  .timeline-description {
    margin-left: 10px;
  }
}

/* 面试结果部分 */
.interview-section {
  margin-bottom: 24px;
  border-top: 1px solid #f0f0f0;
  padding-top: 16px;
}

.interview-result {
  padding: 16px;
  border-radius: 8px;
  background-color: #fafafa;
}

.interview-result.admitted {
  background-color: #f6ffed;
  border: 1px solid #b7eb8f;
}

.interview-result.rejected {
  background-color: #fff1f0;
  border: 1px solid #ffa39e;
}

.interview-result.in-progress {
  background-color: #e6f7ff;
  border: 1px solid #91d5ff;
}

.interview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.interview-status {
  font-weight: 500;
  font-size: 15px;
}

.interview-date {
  font-size: 13px;
  color: #666;
}

.interview-details {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.interview-detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.interview-detail-item.full-width {
  grid-column: 1 / -1;
}

.interview-detail-item label {
  font-size: 12px;
  color: #999;
}

.interview-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

@media (max-width: 768px) {
  .materials-list {
    grid-template-columns: 1fr;
  }

  .interview-details {
    grid-template-columns: 1fr;
  }
}

/* 申请进度步骤指示器 */
.application-steps-section {
  position: relative;
  margin: 20px 0;
  padding: 10px 0;
}

.application-steps {
  display: flex;
  justify-content: space-between;
  position: relative;
  z-index: 2;
}

.step-connector {
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 2px;
  background-color: #e8e8e8;
  transform: translateY(-50%);
  z-index: 1;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  width: 33.33%;
  /* 改为三等分 */
  z-index: 3;
}

.step-number {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background-color: #f0f0f0;
  color: #999;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
  transition: all 0.3s ease;
}

.step-label {
  font-size: 12px;
  color: #999;
  text-align: center;
  transition: color 0.3s ease;
}

.step-item.active .step-number {
  background-color: #1890ff;
  color: white;
}

.step-item.active .step-label {
  color: #1890ff;
  font-weight: 500;
}

.step-item.completed .step-number {
  background-color: #52c41a;
  color: white;
}

.step-item.completed .step-label {
  color: #52c41a;
}

@media (max-width: 768px) {
  .step-label {
    font-size: 10px;
  }

  .step-number {
    width: 24px;
    height: 24px;
    font-size: 12px;
  }
}

.application-meta-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 4px;
}

.application-meta-row.first-row {
  justify-content: space-between;
}

.application-meta {
  margin-top: 4px;
  margin-bottom: 0;
}

.completion-progress {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
  min-width: 120px;
  max-width: 350px;
}

.completion-progress>span {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.progress-bar-wrapper {
  position: relative;
  height: 8px;
  width: 100%;
  border-radius: 4px;
  overflow: hidden;
}

.progress-bar-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #f0f0f0;
}

.progress-bar-fill {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.deadline-info {
  font-size: 14px;
  color: #666;
  min-width: 200px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.deadline-info.urgent {
  color: #f5222d;
}

.deadline-days {
  background-color: #fff1f0;
  color: #f5222d;
  padding: 1px 6px;
  border-radius: 10px;
  font-size: 12px;
  margin-left: 4px;
}

/* 删除之前的mini-progress相关样式 */
.mini-progress {
  display: none;
}

/* 标签式布局样式 */
.application-tabs-section {
  margin: 20px 0;
}

.application-tabs {
  display: flex;
  border-bottom: 1px solid #e8e8e8;
  margin-bottom: 16px;
}

.tab-item {
  padding: 8px 16px;
  cursor: pointer;
  transition: all 0.3s;
  border-bottom: 2px solid transparent;
  font-size: 14px;
  color: #666;
}

.tab-item:hover {
  color: #1890ff;
}

.tab-item.active {
  color: #1890ff;
  border-bottom-color: #1890ff;
  font-weight: 500;
}

.tab-content {
  padding: 8px 0;
}

.tab-pane {
  animation: fadeIn 0.3s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

/* 时间线样式 */
.application-timeline {
  position: relative;
  padding-left: 20px;
}

.timeline-item {
  position: relative;
  padding-bottom: 20px;
}

.timeline-item:last-child {
  padding-bottom: 0;
}

.timeline-status {
  position: absolute;
  left: -20px;
  top: 0;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background-color: #f0f0f0;
  border: 2px solid #fff;
  z-index: 2;
}

.timeline-status.completed {
  background-color: #52c41a;
}

.timeline-status.current {
  background-color: #1890ff;
}

.timeline-status.pending {
  background-color: #d9d9d9;
}

.timeline-connector {
  position: absolute;
  top: 16px;
  left: -12px;
  width: 2px;
  height: calc(100% - 16px);
  background-color: #f0f0f0;
  z-index: 1;
}

.timeline-connector.completed {
  background-color: #52c41a;
}

.timeline-connector.current {
  background-color: #1890ff;
}

.timeline-content {
  padding-left: 10px;
}

.timeline-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.timeline-title {
  font-weight: 500;
  color: #333;
}

.timeline-date {
  color: #999;
  font-size: 12px;
}

.timeline-description {
  color: #666;
  font-size: 13px;
}

/* 面试结果卡片 */
.interview-result {
  padding: 16px;
  border-radius: 8px;
  background-color: #f9f9f9;
  border: 1px solid #e8e8e8;
}

.interview-result.admitted {
  background-color: #f6ffed;
  border-color: #b7eb8f;
}

.interview-result.rejected {
  background-color: #fff1f0;
  border-color: #ffa39e;
}

.interview-result.in-progress {
  background-color: #e6f7ff;
  border-color: #91d5ff;
}

.interview-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.interview-status {
  font-weight: 500;
  color: #333;
}

.interview-date {
  color: #666;
  font-size: 13px;
}

.interview-details {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 16px;
}

.interview-detail-item {
  margin-bottom: 12px;
}

.interview-detail-item label {
  display: block;
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.interview-detail-item.full-width {
  grid-column: 1 / -1;
}

/* 材料列表样式 */
.materials-progress-wrapper {
  margin-bottom: 16px;
}

.progress-header {
  margin-bottom: 8px;
}

.progress-header h5 {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin: 0;
}

.materials-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
}

.material-item {
  display: flex;
  align-items: flex-start;
  padding: 12px;
  background-color: #fafafa;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
}

.material-status {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 8px;
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

.material-content {
  flex: 1;
}

.material-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.material-title {
  font-weight: 500;
  font-size: 14px;
}

.material-badge {
  padding: 2px 6px;
  border-radius: 10px;
  font-size: 12px;
}

.material-badge.completed {
  background-color: #f6ffed;
  color: #52c41a;
}

.material-badge.processing {
  background-color: #fff7e6;
  color: #fa8c16;
}

.material-badge.pending {
  background-color: #fff1f0;
  color: #f5222d;
}

.material-deadline {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #666;
}

.mini-icon {
  width: 12px;
  height: 12px;
  margin-right: 4px;
  color: #999;
}

/* 添加现代化的时间线样式 */
/* 申请进度页面样式 */
.application-progress-wrapper {
  margin: 20px 0;
  padding: 20px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.progress-header {
  margin-bottom: 16px;
}

.progress-header h4 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 12px;
}

.progress-overview {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.progress-percentage {
  font-size: 20px;
  font-weight: 700;
  color: #1890ff;
  min-width: 50px;
}

.progress-bar-wrapper {
  position: relative;
  flex: 1;
  height: 8px;
  border-radius: 4px;
  overflow: hidden;
  background-color: #f0f0f0;
}

.progress-bar-fill {
  position: absolute;
  height: 100%;
  left: 0;
  top: 0;
  background: linear-gradient(90deg, #1890ff, #52c41a);
  border-radius: 4px;
  transition: width 1s ease;
}

.progress-label {
  font-size: 14px;
  font-weight: 500;
  color: #666;
  min-width: 120px;
}

/* 现代化时间线 */
.application-timeline-modern {
  position: relative;
  margin: 20px 0;
}

.timeline-track {
  position: relative;
  margin: 0 30px;
  height: 4px;
}

.track-line {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: #f0f0f0;
  border-radius: 2px;
}

.track-progress {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background: linear-gradient(90deg, #1890ff, #52c41a);
  border-radius: 2px;
  transition: width 1s ease;
}

.timeline-stages {
  position: relative;
  margin-top: 16px;
}

.timeline-stage {
  position: relative;
  margin-bottom: 16px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.timeline-stage:last-child {
  margin-bottom: 0;
}

.stage-node {
  position: absolute;
  left: 30px;
  top: 0;
  transform: translateX(-50%);
  z-index: 3;
}

.node-icon {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  color: #999;
}

.timeline-stage.completed .node-icon {
  background-color: #52c41a;
  color: #fff;
}

.timeline-stage.current .node-icon {
  background-color: #1890ff;
  color: #fff;
}

.stage-content {
  margin-left: 45px;
  padding: 10px 15px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
}

.timeline-stage:hover .stage-content {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.timeline-stage.completed .stage-content {
  border-left: 3px solid #52c41a;
}

.timeline-stage.current .stage-content {
  border-left: 3px solid #1890ff;
}

.stage-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.stage-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.timeline-stage.completed .stage-title {
  color: #52c41a;
}

.timeline-stage.current .stage-title {
  color: #1890ff;
}

.stage-date {
  font-size: 12px;
  color: #999;
  font-weight: 500;
}

.stage-description {
  font-size: 12px;
  color: #666;
  margin-bottom: 8px;
}

.stage-detail {
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.3s ease, opacity 0.3s ease;
  opacity: 0;
}

.stage-detail.expanded {
  max-height: 500px;
  opacity: 1;
}

.stage-info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
  background-color: #f9f9f9;
  padding: 12px;
  border-radius: 8px;
}

.info-item {
  display: flex;
  flex-direction: column;
}

.info-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.info-value {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.stage-actions {
  display: flex;
  gap: 8px;
  margin-top: 16px;
}

.btn-action-sm {
  padding: 6px 12px;
  background-color: #f0f0f0;
  border: none;
  border-radius: 6px;
  color: #666;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-action-sm:hover {
  background-color: #e6f7ff;
  color: #1890ff;
}

/* 里程碑卡片样式 */
.application-milestone-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 10px;
  margin-top: 20px;
}

.milestone-card {
  display: flex;
  background-color: white;
  padding: 12px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
  align-items: center;
  gap: 10px;
}

.milestone-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.milestone-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.milestone-icon.completed {
  background-color: #f6ffed;
  color: #52c41a;
}

.milestone-icon.in-progress {
  background-color: #e6f7ff;
  color: #1890ff;
}

.milestone-icon.success {
  background-color: #f6ffed;
  color: #52c41a;
}

.milestone-icon.rejected {
  background-color: #fff2f0;
  color: #f5222d;
}

.milestone-content {
  flex: 1;
}

.milestone-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.milestone-description {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.milestone-meta {
  font-size: 12px;
  color: #999;
}

@media (max-width: 768px) {
  .application-milestone-cards {
    grid-template-columns: 1fr;
  }

  .stage-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .stage-date {
    margin-top: 4px;
  }

  .progress-overview {
    flex-direction: column;
    align-items: flex-start;
  }

  .progress-bar-wrapper {
    width: 100%;
  }
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #333;
}

.page-subtitle {
  color: #666;
  margin: 0;
  font-size: 14px;
}

.application-status-snapshot {
  margin-top: 24px;
  padding: 16px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  background-color: #fff;
}

.application-status-snapshot h5 {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.status-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.status-label {
  font-size: 12px;
  color: #999;
}

.status-value {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.status-value.urgent {
  color: #f5222d;
}

.snapshot-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  border-top: 1px solid #f0f0f0;
  padding-top: 16px;
}

@media (max-width: 768px) {
  .status-grid {
    grid-template-columns: 1fr;
  }

  .snapshot-actions {
    flex-direction: column;
  }

  .snapshot-actions button {
    width: 100%;
  }
}

// 添加加载状态的CSS样式
.applications-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #666;
  height: 200px;
}

.applications-loading .loading-spinner {
  border: 3px solid #f3f3f3;
  border-top: 3px solid #1890ff;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}
</style>
