<template>
  <div class="application-progress">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading">
        <Loading />
      </el-icon>
      <span>正在获取申请进度...</span>
    </div>

    <div v-else>
      <!-- 已申请的学校 -->
      <div class="schools-selection-section" v-if="availableSchools.length > 0">
        <h3 class="section-title">已申请的学校</h3>

        <div class="selected-schools-list">
          <div v-for="school in availableSchools" :key="school.applicationId" class="selected-school-item"
            :class="{ 'active': selectedApplication && String(selectedApplication) === String(school.applicationId) }"
            @click="handleApplicationChange(school.applicationId)">
            <div class="school-info">
              <div class="school-details">
                <div class="school-name">{{ school.schoolName }}</div>
                <div class="school-major">{{ school.majorName }}</div>
              </div>
              <el-tag :type="getStatusTagType(school.applicationStatus)" size="small" class="status-tag">
                {{ getStatusText(school.applicationStatus) }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 提示信息 -->
      <div v-else class="no-applications-hint">
        <el-icon class="hint-icon">
          <Document />
        </el-icon>
        <p class="hint-text">您还没有提交任何申请</p>
        <p class="hint-description">请先在"材料上传"步骤中提交申请后再查看进度</p>
      </div>

      <!-- 申请进度条 -->
      <div class="progress-section">
        <h3 v-if="selectedApplication && selectedSchoolName">{{ selectedSchoolName }} - 申请进度</h3>
        <h3 v-else>申请进度</h3>

        <!-- 空状态 -->
        <div v-if="!selectedApplication || progressSteps.length === 0" class="empty-state">
          <el-icon class="empty-icon">
            <Document />
          </el-icon>
          <p class="empty-text">暂无申请进度数据</p>
          <p class="empty-description">
            {{ availableSchools.length === 0 ? '您还没有提交任何申请，请先提交申请后再查看进度' : '该申请的进度信息正在更新中' }}
          </p>
        </div>

        <!-- 快递式进度条 -->
        <div v-else class="delivery-progress">
          <div class="progress-track">
            <div class="progress-line" :style="{ width: `${overallProgress}%` }"></div>
          </div>

          <div class="progress-steps">
            <div v-for="(step, index) in progressSteps" :key="index" class="progress-step" :class="{
              'completed': step.status === 'completed',
              'current': step.status === 'current',
              'pending': step.status === 'pending'
            }">
              <div class="step-icon">
                <el-icon v-if="step.status === 'completed'">
                  <Check />
                </el-icon>
                <el-icon v-else-if="step.status === 'current'">
                  <Loading />
                </el-icon>
                <span v-else class="step-number">{{ index + 1 }}</span>
              </div>
              <div class="step-content">
                <div class="step-title">{{ step.title }}</div>
                <div class="step-date" v-if="step.date">{{ step.date }}</div>
                <div class="step-description" v-if="step.description">{{ step.description }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 双栏布局容器 -->
      <div class="dual-column-container">
        <!-- 消息通知框 -->
        <div class="message-notification-box">
          <div class="message-header">
            <h3>
              <el-icon>
                <Message />
              </el-icon>
              消息通知
            </h3>
            <el-button v-if="selectedApplication" type="primary" size="small" @click="refreshMessages"
              :loading="messagesLoading">
              <el-icon>
                <Refresh />
              </el-icon>
              刷新
            </el-button>
          </div>

          <div class="message-content">
            <!-- 空状态 -->
            <div v-if="!selectedApplication || teacherMessages.length === 0" class="empty-messages">
              <el-icon class="empty-icon">
                <Service />
              </el-icon>
              <p class="empty-text">{{ !selectedApplication ? '请先选择申请学校' : '暂无消息' }}</p>
              <p class="empty-description">{{ !selectedApplication ? '选择学校后可查看相关消息' : '老师发送的消息将在这里显示' }}</p>
            </div>

            <!-- 消息列表 -->
            <div v-else class="messages-list">
              <div v-for="(message, index) in teacherMessages" :key="index" class="message-item"
                :class="{ unread: !message.read }">
                <div class="message-avatar">
                  <el-avatar :size="40">
                    <el-icon>
                      <Service />
                    </el-icon>
                  </el-avatar>
                </div>
                <div class="message-body">
                  <div class="message-info">
                    <span class="sender-name">{{ message.senderName || '系统通知' }}</span>
                    <span class="message-time">{{ message.time }}</span>
                    <el-tag v-if="!message.read" type="danger" size="small">未读</el-tag>
                  </div>
                  <div class="message-title" v-if="message.title">{{ message.title }}</div>
                  <div class="message-actions-row">
                    <el-button v-if="!message.read" type="success" size="small" @click="markAsRead(message)"
                      class="read-btn">
                      标记已读
                    </el-button>
                    <el-button type="primary" size="small" @click="showMessageDetail(message)" class="detail-btn">
                      查看详细
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 代办事项框 -->
        <div class="todo-notification-box">
          <div class="todo-header">
            <h3>
              <el-icon>
                <List />
              </el-icon>
              代办事项
            </h3>
            <el-button type="primary" size="small" :icon="Plus" @click="showAddTodoDialog = true">
              添加
            </el-button>
          </div>

          <div class="todo-content">
            <!-- 空状态 -->
            <div v-if="todoList.length === 0" class="empty-todos">
              <el-icon class="empty-icon">
                <List />
              </el-icon>
              <p class="empty-text">暂无代办事项</p>
              <p class="empty-description">点击上方"添加"按钮创建新的代办事项</p>
            </div>

            <!-- 代办事项列表 -->
            <div v-else class="todos-list">
              <div v-for="todo in todoList" :key="todo.id" class="todo-item" :class="{ completed: todo.completed }">
                <div class="todo-main-content">
                  <div class="todo-header-info">
                    <el-checkbox v-model="todo.completed" @change="toggleTodoComplete(todo.id)" class="todo-checkbox" />
                    <div class="todo-title" :class="{ 'completed-text': todo.completed }">
                      {{ todo.title }}
                    </div>
                  </div>
                  <div v-if="todo.description" class="todo-description">
                    {{ todo.description }}
                  </div>
                </div>
                <div class="todo-meta">
                  <div class="todo-info">
                    <el-tag :color="getPriorityColor(todo.priority)" size="small" effect="light">
                      {{ getPriorityText(todo.priority) }}
                    </el-tag>
                    <span v-if="todo.dueDate" class="todo-date">
                      截止: {{ formatDate(todo.dueDate) }}
                    </span>
                  </div>
                  <div class="todo-actions">
                    <el-button type="text" size="small" :icon="Edit" @click="editTodo(todo.id)" class="edit-btn">
                      编辑
                    </el-button>
                    <el-button type="text" size="small" :icon="Delete" @click="deleteTodo(todo.id)" class="delete-btn">
                      删除
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>




    </div>
  </div>

  <!-- 添加代办事项对话框 -->
  <el-dialog v-model="showAddTodoDialog" title="添加代办事项" width="500px">
    <el-form :model="newTodo" label-width="80px">
      <el-form-item label="任务标题" required>
        <el-input v-model="newTodo.title" placeholder="请输入任务标题" />
      </el-form-item>
      <el-form-item label="任务描述">
        <el-input v-model="newTodo.description" type="textarea" :rows="3" placeholder="请输入任务描述（可选）" />
      </el-form-item>
      <el-form-item label="优先级">
        <el-select v-model="newTodo.priority" placeholder="选择优先级">
          <el-option label="高优先级" value="high" />
          <el-option label="中优先级" value="medium" />
          <el-option label="低优先级" value="low" />
        </el-select>
      </el-form-item>
      <el-form-item label="截止日期">
        <el-date-picker v-model="newTodo.dueDate" type="date" placeholder="选择截止日期" format="YYYY-MM-DD"
          value-format="YYYY-MM-DD" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showAddTodoDialog = false">取消</el-button>
        <el-button type="primary" @click="addTodo">添加</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 消息详细弹窗 -->
  <el-dialog v-model="showMessageDetailDialog" title="消息详细信息" width="600px">
    <div v-if="selectedMessageDetail" class="message-detail-content">
      <div class="detail-header">
        <div class="detail-sender">
          <el-avatar :size="50">
            <el-icon>
              <Service />
            </el-icon>
          </el-avatar>
          <div class="sender-info">
            <div class="sender-name">{{ selectedMessageDetail.senderName || '系统通知' }}</div>
            <div class="send-time">{{ selectedMessageDetail.time }}</div>
          </div>
          <el-tag :type="selectedMessageDetail.read ? 'success' : 'danger'" size="small">
            {{ selectedMessageDetail.read ? '已读' : '未读' }}
          </el-tag>
        </div>
      </div>

      <div class="detail-body">
        <div v-if="selectedMessageDetail.title" class="detail-title">
          {{ selectedMessageDetail.title }}
        </div>
        <div class="detail-content">
          {{ selectedMessageDetail.content }}
        </div>
        <div v-if="selectedMessageDetail.attachments" class="detail-attachments">
          <h4>附件</h4>
          <div v-for="attachment in selectedMessageDetail.attachments" :key="attachment.id" class="attachment-item">
            <el-icon>
              <Document />
            </el-icon>
            <span>{{ attachment.name }}</span>
            <el-button type="text" size="small">下载</el-button>
          </div>
        </div>
      </div>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showMessageDetailDialog = false">关闭</el-button>
        <el-button v-if="selectedMessageDetail && !selectedMessageDetail.read" type="success"
          @click="markAsRead(selectedMessageDetail)">
          标记已读
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Check,
  Loading,
  Warning,
  Timer,
  Clock,
  Document,
  Service,
  InfoFilled,
  Plus,
  Edit,
  Delete,
  List
} from '@element-plus/icons-vue'
import axios from 'axios'

// Props
const props = defineProps({
  userId: {
    type: [String, Number],
    required: true
  }
})

// 响应式数据
const loading = ref(false)
const selectedApplication = ref('')
const selectedSchoolName = ref('')
const showProgress = ref(false)
const availableSchools = ref([])
const applicationStatus = ref([])

// 进度条相关数据
const progressSteps = ref([])
const overallProgress = ref(0)

// 消息通知相关数据
const teacherMessages = ref([])
const messagesLoading = ref(false)

// 自动刷新相关数据
const autoRefreshTimer = ref(null)
const autoRefreshInterval = 30000 // 30秒自动刷新一次
const lastMessageCount = ref(0)

// 代办事项相关数据
const todoList = ref([
  {
    id: 1,
    title: '准备推荐信',
    description: '联系教授获取推荐信，需要至少2封学术推荐信',
    completed: false,
    priority: 'high',
    dueDate: '2025-01-15',
    createdAt: '2025-10-22'
  },
  {
    id: 2,
    title: '完善个人陈述',
    description: '根据学校要求修改和完善个人陈述内容',
    completed: false,
    priority: 'medium',
    dueDate: '2025-01-10',
    createdAt: '2025-10-21'
  },
  {
    id: 3,
    title: '提交语言成绩',
    description: '上传托福或雅思成绩单到申请系统',
    completed: true,
    priority: 'high',
    dueDate: '2025-10-20',
    createdAt: '2025-10-15'
  }
])
const showAddTodoDialog = ref(false)
const newTodo = reactive({
  title: '',
  description: '',
  priority: 'medium',
  dueDate: ''
})

// 消息详细弹窗相关数据
const showMessageDetailDialog = ref(false)
const selectedMessageDetail = ref(null)



// 获取可申请学校列表
const fetchAvailableSchools = async (options = {}) => {
  const { silent = false } = options
  try {
    console.log('正在获取可申请学校列表...')
    const response = await axios.get('/api/application-school/available', {
      params: { userId: props.userId }
    })

    console.log('可申请学校API响应:', response.data)

    // 后端返回的数据结构是 { code: 200, message: "success", data: [...] }
    if (response.data && response.data.code === 200 && response.data.data && response.data.data.length > 0) {
      availableSchools.value = response.data.data.map(school => ({
        applicationId: school.id,
        schoolName: school.schoolName,
        majorName: school.programName, // 将programName映射为majorName以匹配模板
        programName: school.programName,
        displayName: `${school.schoolName} - ${school.programName}`,
        applicationStatus: school.applicationStatus || 'selected' // 添加申请状态信息
      }))
      console.log('获取到可申请学校:', availableSchools.value)
      if (selectedApplication.value) {
        const selectedSchool = availableSchools.value.find(school => String(school.applicationId) === String(selectedApplication.value))
        if (selectedSchool) {
          selectedSchoolName.value = selectedSchool.displayName
        }
      }
    } else {
      console.log('没有找到可申请学校')
      availableSchools.value = []
      if (!silent && response.data && response.data.code === 200) {
        ElMessage.info('您还没有提交任何申请，请先提交申请后再查看可申请学校')
      }
    }
  } catch (error) {
    console.error('获取可申请学校失败:', error)
    if (!silent && error.response && error.response.status === 405) {
      ElMessage.error('API端点不支持该请求方法，请联系管理员')
    } else if (!silent) {
      ElMessage.error('获取可申请学校失败，请稍后重试')
    }
    availableSchools.value = []
  }
}

// 获取申请进度数据
const fetchApplicationProgress = async (applicationId = null, options = {}) => {
  const { silent = false } = options
  if (!silent) {
    loading.value = true
  }
  try {
    console.log('正在获取申请进度数据...', { userId: props.userId, applicationId })

    const params = { userId: props.userId }
    // 如果指定了applicationId，则查询特定申请的进度
    if (applicationId && applicationId !== 'all' && applicationId !== '') {
      params.applicationId = applicationId
    }

    const response = await axios.get('/api/application-school/progress', { params })
    console.log('申请进度API响应:', response.data)

    // 后端返回的数据结构是 { code: 200, message: "success", data: {...} }
    if (response.data && response.data.code === 200) {
      const progressData = response.data.data || {}

      // 处理申请状态时间线
      if (progressData.applicationStatus && Array.isArray(progressData.applicationStatus) && progressData.applicationStatus.length > 0) {
        applicationStatus.value = progressData.applicationStatus.map(item => ({
          type: mapStatusType(item.type),
          text: item.text || '未知状态',
          date: item.date || '',
          description: item.description || '',
          progress: item.progress,
          subStatus: item.subStatus,
          deadline: item.deadline,
          action: item.action
        }))
        console.log('处理后的申请状态:', applicationStatus.value)

        // 转换为进度条数据
        convertToProgressSteps(applicationStatus.value)
      } else {
        console.log('没有申请进度数据，创建默认状态')
        // 创建默认的申请状态，显示友好的空状态
        applicationStatus.value = []
        progressSteps.value = []
        overallProgress.value = 0
      }

      // 如果所有数据都为空，显示提示信息
      if (applicationStatus.value.length === 0) {
        console.log('所有数据为空，用户可能还没有申请')
      }
    } else {
      console.log('API返回错误或空数据:', response.data)
      // 设置空状态
      applicationStatus.value = []
      progressSteps.value = []
      overallProgress.value = 0
    }
  } catch (error) {
    console.error('获取申请进度失败:', error)

    // 设置空状态
    applicationStatus.value = []
    progressSteps.value = []
    overallProgress.value = 0

    // 根据错误类型显示不同的提示
    if (error.response) {
      console.error('错误响应:', error.response.data)
      if (error.response.status === 404) {
        console.log('申请进度数据不存在，可能用户还没有申请')
      } else {
        if (!silent) {
          ElMessage.error(`获取申请进度失败: ${error.response.data.message || '服务器错误'}`)
        }
      }
    } else if (error.request) {
      console.error('网络错误:', error.request)
      if (!silent) {
        ElMessage.error('网络连接失败，请检查网络设置')
      }
    } else {
      console.error('请求配置错误:', error.message)
      if (!silent) {
        ElMessage.error('请求配置错误')
      }
    }
  } finally {
    if (!silent) {
      loading.value = false
    }
  }
}

// 映射状态类型
const mapStatusType = (statusType) => {
  const typeMap = {
    'completed': 'success',
    'current': 'processing',
    'pending': 'upcoming',
    'warning': 'warning',
    'error': 'error',
    'rejected': 'error',
    // 兼容旧的状态类型
    'success': 'success',
    'processing': 'processing',
    'upcoming': 'upcoming'
  }
  return typeMap[statusType] || 'upcoming'
}

// 转换申请状态为进度条数据
const convertToProgressSteps = (statusList) => {
  // 获取当前选中申请的状态
  const selectedSchool = availableSchools.value.find(school => school.applicationId === selectedApplication.value)
  const currentStatus = selectedSchool ? selectedSchool.applicationStatus : 'selected'

  console.log('当前申请状态:', currentStatus)

  // 定义标准的申请进度步骤（根据状态映射）
  const standardSteps = [
    { key: 'selected', title: '已选择学校', description: '您已选择学校和专业' },
    { key: 'submitted', title: '已提交材料', description: '您的申请材料已成功提交' },
    { key: 'under_review', title: '材料审核中', description: '学校正在审核您的申请材料' },
    { key: 'interview', title: '面试阶段', description: '参加学校安排的面试或考试' },
    { key: 'admitted', title: '已录取', description: '恭喜！您已被学校录取' },
    { key: 'visa_processing', title: '签证申请', description: '正在申请学生签证' },
    { key: 'enrolled', title: '已入学', description: '恭喜！您已成功入学' }
  ]

  // 状态到步骤索引的映射
  const statusToStepIndex = {
    'selected': 0,
    'submitted': 1,
    'under_review': 2,
    'interview': 3,
    'admitted': 4,
    'visa_processing': 5,
    'visa_approved': 5, // 签证批准后仍在签证申请步骤，但状态为完成
    'visa_rejected': 5, // 签证被拒也在签证申请步骤，但状态为error
    'enrolled': 6,
    'rejected': 4 // rejected状态也显示到第4步，但状态为error
  }

  // 获取当前状态对应的步骤索引
  const currentStepIndex = statusToStepIndex[currentStatus] || 0

  // 生成进度步骤
  const steps = standardSteps.map((standardStep, index) => {
    let stepStatus = 'pending'
    let stepDate = ''
    let stepDescription = standardStep.description

    if (currentStatus === 'rejected') {
      // 处理rejected状态的特殊逻辑
      if (index < currentStepIndex) {
        stepStatus = 'completed'
      } else if (index === currentStepIndex) {
        stepStatus = 'error'
        stepDescription = '很遗憾，您的申请未能通过'
      } else {
        stepStatus = 'pending'
      }
    } else if (currentStatus === 'visa_rejected') {
      // 处理签证被拒的特殊逻辑
      if (index < currentStepIndex) {
        stepStatus = 'completed'
      } else if (index === currentStepIndex) {
        stepStatus = 'error'
        stepDescription = '很遗憾，您的签证申请被拒绝'
      } else {
        stepStatus = 'pending'
      }
    } else if (currentStatus === 'visa_approved') {
      // 处理签证批准的特殊逻辑
      if (index < currentStepIndex) {
        stepStatus = 'completed'
      } else if (index === currentStepIndex) {
        stepStatus = 'completed'
        stepDescription = '恭喜！您的签证申请已获批准'
      } else {
        stepStatus = 'pending'
      }
    } else {
      // 正常状态的处理逻辑
      if (index < currentStepIndex) {
        stepStatus = 'completed'
      } else if (index === currentStepIndex) {
        stepStatus = 'current'
      } else {
        stepStatus = 'pending'
      }
    }

    // 从statusList中获取对应的日期和描述信息
    if (statusList && statusList.length > 0) {
      const matchingStatus = statusList.find(status =>
        status.text && (
          status.text.includes(standardStep.title) ||
          standardStep.title.includes(status.text)
        )
      )
      if (matchingStatus) {
        stepDate = matchingStatus.date || ''
        if (matchingStatus.description) {
          stepDescription = matchingStatus.description
        }
      }
    }

    return {
      title: standardStep.title,
      description: stepDescription,
      date: stepDate,
      status: stepStatus
    }
  })

  // 计算整体进度
  let completedSteps = 0
  let currentSteps = 0

  if (currentStatus === 'rejected') {
    // rejected状态的进度计算
    completedSteps = currentStepIndex
    overallProgress.value = Math.round((completedSteps / steps.length) * 100)
  } else {
    // 正常状态的进度计算
    completedSteps = steps.filter(step => step.status === 'completed').length
    currentSteps = steps.filter(step => step.status === 'current').length
    overallProgress.value = Math.round(((completedSteps + currentSteps * 0.5) / steps.length) * 100)
  }

  progressSteps.value = steps
  console.log('生成的进度步骤:', steps)
  console.log('整体进度:', overallProgress.value)
}

// 处理学校选择变化
const handleApplicationChange = (value) => {
  console.log('学校选择变化:', value)

  // 停止当前的自动刷新
  stopAutoRefresh()

  if (!value) {
    // 清空选择
    selectedApplication.value = ''
    selectedSchoolName.value = ''
    showProgress.value = false
    applicationStatus.value = []
    lastMessageCount.value = 0
    return
  }

  // 更新选中的申请ID
  selectedApplication.value = value

  // 找到选中的学校信息
  const selectedSchool = availableSchools.value.find(school => school.applicationId === value)
  if (selectedSchool) {
    selectedSchoolName.value = selectedSchool.displayName
    showProgress.value = true
    fetchApplicationProgress(value)
    fetchTeacherMessages()

    // 启动新的自动刷新
    startAutoRefresh()
  }
}

// 处理状态操作
const handleStatusAction = (action) => {
  console.log('状态操作:', action)
  if (action.url) {
    window.open(action.url, '_blank')
  } else if (action.callback) {
    action.callback()
  }
}



// 获取截止日期样式类
const getDeadlineClass = (task) => {
  if (!task.deadline) return ''

  const deadline = new Date(task.deadline)
  const now = new Date()
  const diffDays = Math.ceil((deadline - now) / (1000 * 60 * 60 * 24))

  if (diffDays < 0) return 'overdue'
  if (diffDays <= 3) return 'urgent'
  if (diffDays <= 7) return 'warning'
  return 'normal'
}

// 获取录取状态样式类
const getAdmissionStatusClass = (status) => {
  const statusMap = {
    '已录取': 'admitted',
    '未录取': 'rejected',
    '等待结果': 'pending',
    '条件录取': 'conditional'
  }
  return statusMap[status] || 'pending'
}



// 获取老师消息
const fetchTeacherMessages = async () => {
  if (!selectedApplication.value) return

  messagesLoading.value = true
  try {
    console.log('正在获取老师消息...', {
      userId: props.userId,
      applicationId: selectedApplication.value
    })

    const response = await axios.get('/api/messages/teacher', {
      params: {
        userId: props.userId,
        applicationId: selectedApplication.value
      }
    })

    console.log('老师消息API响应:', response.data)

    if (response.data && response.data.code === 200 && response.data.data) {
      const newMessages = response.data.data.map(message => ({
        id: message.id,
        senderName: message.senderName || '系统通知',
        title: message.title,
        content: message.content,
        time: message.createdAt || message.time,
        read: message.isRead || message.read || false,
        messageType: message.messageType || 'notification',
        priority: message.priority || 'normal',
        requiresReply: message.requiresReply || false,
        replyDeadline: message.replyDeadline,
        attachmentUrl: message.attachmentUrl,
        actions: message.requiresReply ? [
          { text: '回复', type: 'primary' },
          { text: '标记已读', type: 'default' }
        ] : [
          { text: '标记已读', type: 'default' },
          { text: '查看详情', type: 'info' }
        ]
      }))

      // 检测新消息
      const currentMessageCount = newMessages.length
      const hasNewMessages = currentMessageCount > lastMessageCount.value && lastMessageCount.value > 0

      // 如果有新消息，显示提示
      if (hasNewMessages) {
        const newMessageCount = currentMessageCount - lastMessageCount.value
        ElMessage.success(`收到 ${newMessageCount} 条新消息`)
      }

      teacherMessages.value = newMessages
      lastMessageCount.value = currentMessageCount
    } else {
      teacherMessages.value = []
      lastMessageCount.value = 0
    }
  } catch (error) {
    console.error('获取老师消息失败:', error)
    // 清空消息数据，等待连接老师端
    teacherMessages.value = []
  } finally {
    messagesLoading.value = false
  }
}

// 刷新消息
const refreshMessages = async () => {
  await fetchTeacherMessages()
  ElMessage.success('消息已刷新')
}

// 启动自动刷新
const startAutoRefresh = () => {
  // 清除现有定时器
  if (autoRefreshTimer.value) {
    clearInterval(autoRefreshTimer.value)
  }

  // 设置新的定时器
  autoRefreshTimer.value = setInterval(async () => {
    if (selectedApplication.value) {
      await fetchAvailableSchools({ silent: true })
      await fetchApplicationProgress(selectedApplication.value, { silent: true })
      await fetchTeacherMessages()
    }
  }, autoRefreshInterval)

  console.log('消息自动刷新已启动，间隔:', autoRefreshInterval / 1000, '秒')
}

// 停止自动刷新
const stopAutoRefresh = () => {
  if (autoRefreshTimer.value) {
    clearInterval(autoRefreshTimer.value)
    autoRefreshTimer.value = null
    console.log('消息自动刷新已停止')
  }
}

// 页面可见性变化处理
const handleVisibilityChange = () => {
  if (document.hidden) {
    // 页面隐藏时停止自动刷新
    stopAutoRefresh()
  } else {
    // 页面显示时重新启动自动刷新并立即刷新一次
    if (selectedApplication.value) {
      fetchAvailableSchools({ silent: true })
      fetchApplicationProgress(selectedApplication.value, { silent: true })
      fetchTeacherMessages()
      startAutoRefresh()
    }
  }
}

// 处理消息操作
const handleMessageAction = async (message, action) => {
  console.log('消息操作:', { message, action })

  if (action.text === '标记已读') {
    // 调用统一的markAsRead方法
    await markAsRead(message)
  } else if (action.text === '查看详情') {
    // 调用查看详细方法
    showMessageDetail(message)
  } else if (action.text === '回复') {
    ElMessage.info('回复功能待实现')
  }
}

// 状态代码转换为中文显示文本
const getStatusText = (status) => {
  const statusMap = {
    'selected': '已选择',
    'submitted': '已提交',
    'under_review': '审核中',
    'interview': '面试中',
    'admitted': '已录取',
    'rejected': '已拒绝',
    'visa_processing': '签证申请中',
    'visa_approved': '签证已批准',
    'visa_rejected': '签证被拒',
    'enrolled': '已入学'
  }
  return statusMap[status] || '未知状态'
}

// 根据状态返回标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    'selected': 'info',
    'submitted': 'warning',
    'under_review': 'primary',
    'interview': 'primary',
    'admitted': 'success',
    'rejected': 'danger',
    'visa_processing': 'warning',
    'visa_approved': 'success',
    'visa_rejected': 'danger',
    'enrolled': 'success'
  }
  return typeMap[status] || 'info'
}

// 代办事项相关方法
const toggleTodoComplete = (todoId) => {
  const todo = todoList.value.find(item => item.id === todoId)
  if (todo) {
    todo.completed = !todo.completed
    ElMessage.success(todo.completed ? '任务已完成' : '任务已标记为未完成')
  }
}

const addTodo = () => {
  if (!newTodo.title.trim()) {
    ElMessage.warning('请输入任务标题')
    return
  }

  const todo = {
    id: Date.now(),
    title: newTodo.title,
    description: newTodo.description,
    completed: false,
    priority: newTodo.priority,
    dueDate: newTodo.dueDate,
    createdAt: new Date().toISOString().split('T')[0]
  }

  todoList.value.unshift(todo)

  // 重置表单
  newTodo.title = ''
  newTodo.description = ''
  newTodo.priority = 'medium'
  newTodo.dueDate = ''
  showAddTodoDialog.value = false

  ElMessage.success('任务添加成功')
}

const editTodo = (todoId) => {
  ElMessage.info('编辑功能开发中...')
}

const deleteTodo = (todoId) => {
  const index = todoList.value.findIndex(item => item.id === todoId)
  if (index > -1) {
    todoList.value.splice(index, 1)
    ElMessage.success('任务删除成功')
  }
}

const getPriorityColor = (priority) => {
  const colorMap = {
    'high': '#f56565',
    'medium': '#ed8936',
    'low': '#48bb78'
  }
  return colorMap[priority] || '#718096'
}

const getPriorityText = (priority) => {
  const textMap = {
    'high': '高优先级',
    'medium': '中优先级',
    'low': '低优先级'
  }
  return textMap[priority] || '未知'
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

// 消息详细弹窗相关方法
const showMessageDetail = (message) => {
  selectedMessageDetail.value = message
  showMessageDetailDialog.value = true

  // 如果消息未读，标记为已读
  if (!message.read) {
    message.read = true
    ElMessage.success('消息已标记为已读')
  }
}

const markAsRead = async (message) => {
  if (!message.read) {
    try {
      const response = await axios.put(`/api/messages/${message.id}/read`)
      if (response.data && response.data.code === 200) {
        message.read = true
        ElMessage.success('消息已标记为已读')

        // 如果详细弹窗中显示的是同一条消息，也更新弹窗中的状态
        if (showMessageDetailDialog.value && selectedMessageDetail.value && selectedMessageDetail.value.id === message.id) {
          selectedMessageDetail.value.read = true
        }
      } else {
        ElMessage.error('标记已读失败: ' + (response.data?.message || '未知错误'))
      }
    } catch (error) {
      console.error('标记已读失败:', error)
      ElMessage.error('标记已读失败: ' + (error.response?.data?.message || error.message || '网络错误'))
    }
  }
}

// 组件挂载时获取数据
onMounted(async () => {
  console.log('ApplicationProgress组件挂载，用户ID:', props.userId)
  if (props.userId) {
    await fetchAvailableSchools()
    // 如果有可用学校，自动选择第一个并获取进度数据
    if (availableSchools.value.length > 0) {
      const firstSchool = availableSchools.value[0]
      selectedApplication.value = firstSchool.applicationId
      selectedSchoolName.value = firstSchool.displayName
      showProgress.value = true
      await fetchApplicationProgress(firstSchool.applicationId)

      // 启动消息自动刷新
      startAutoRefresh()
    } else {
      // 如果没有学校，直接获取用户的申请进度（不传schoolId参数）
      console.log('没有可用学校，获取用户的所有申请进度')
      showProgress.value = true
      await fetchApplicationProgress()
    }
  }

  // 监听页面可见性变化
  document.addEventListener('visibilitychange', handleVisibilityChange)
})

// 组件卸载时清理资源
onUnmounted(() => {
  // 停止自动刷新
  stopAutoRefresh()

  // 移除页面可见性监听器
  document.removeEventListener('visibilitychange', handleVisibilityChange)
})

// 监听用户ID变化
watch(() => props.userId, async (newUserId) => {
  console.log('用户ID变化:', newUserId)

  // 停止当前的自动刷新
  stopAutoRefresh()

  if (newUserId) {
    await fetchAvailableSchools()
    // 重置选择状态
    selectedApplication.value = ''
    selectedSchoolName.value = ''
    showProgress.value = false
    applicationStatus.value = []
    lastMessageCount.value = 0

    // 如果有可用学校，重新启动自动刷新
    if (availableSchools.value.length > 0) {
      startAutoRefresh()
    }
  }
}, { immediate: false })
</script>

<style scoped>
.application-progress {
  padding: 10px 20px 20px 20px;
  min-height: 100vh;
}

/* 加载状态样式 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #666;
  font-size: 16px;
}

.loading-container .el-icon {
  font-size: 32px;
  margin-bottom: 16px;
  color: var(--primary-color);
}

/* 空状态样式 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  color: #c0c4cc;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 18px;
  color: #606266;
  margin: 0 0 8px 0;
  font-weight: 500;
}

.empty-description {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

/* 学校选择器样式 */
.school-selector {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px -20px;
  padding-bottom: 8px;
  text-align: left;
}

/* 学校列表样式 */
.schools-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  /* 一行显示2个卡片 */
  gap: 16px;
  margin-bottom: 30px;
}

/* 横条格式学校卡片样式 */
.school-card-horizontal {
  background-color: white;
  border-radius: 8px;
  /* 缩小圆角 */
  padding: 8px 12px;
  /* 进一步缩小内边距，减少上下空白 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 2px solid #e0e0e0;
  /* 加强默认边框对比度 */
  display: flex;
  align-items: center;
  gap: 10px;
  /* 进一步缩小间距 */
  min-height: 60px;
  /* 进一步缩小最小高度 */
}

.school-card-horizontal:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border-color: #b0b0b0;
  /* 加强悬停时边框对比度 */
}

.school-card-horizontal.selected {
  border-color: var(--primary-color);
  background-color: var(--el-color-primary-light-9);
}

.school-logo {
  width: 60px;
  /* 缩小logo尺寸 */
  height: 60px;
  /* 缩小logo尺寸 */
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f8f9fa;
  border-radius: 6px;
  /* 缩小圆角 */
  overflow: hidden;
}

.school-logo-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 6px;
  /* 缩小圆角 */
}

.school-basic-info {
  flex: 0 0 200px;
  /* 缩小基本信息区域宽度 */
  display: flex;
  flex-direction: column;
  gap: 6px;
  /* 缩小间距 */
}

.school-name-section {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.school-name-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  /* 学校名在左，QS排名在右 */
  gap: 8px;
  min-height: 24px;
  /* 固定高度确保对齐 */
}

.school-name {
  font-size: 16px;
  /* 缩小字体 */
  font-weight: 600;
  color: #333;
  margin: 0;
  line-height: 1.3;
  flex: 1;
  /* 占据剩余空间 */
  min-width: 0;
  /* 允许文本截断 */
  white-space: nowrap;
  /* 强制不换行 */
  overflow: hidden;
  /* 隐藏溢出内容 */
  text-overflow: ellipsis;
  /* 显示省略号 */
}

.school-location {
  font-size: 12px;
  /* 缩小字体 */
  color: #666;
  margin: 0;
  line-height: 1.4;
}

.qs-ranking {
  display: inline-flex;
  align-items: center;
  background-color: #ffd700;
  color: #333;
  padding: 3px 8px;
  /* 缩小内边距 */
  border-radius: 12px;
  /* 缩小圆角 */
  font-weight: 600;
  font-size: 11px;
  /* 缩小字体 */
  gap: 3px;
  /* 缩小间距 */
  width: fit-content;
  flex-shrink: 0;
  /* 防止QS排名被压缩 */
  min-width: 50px;
  /* 确保最小宽度，保持对齐 */
  justify-content: center;
  /* 内容居中 */
}

.qs-label {
  font-size: 10px;
  /* 缩小字体 */
}

.qs-number {
  font-size: 12px;
  /* 缩小字体 */
  font-weight: 700;
}

.school-details-horizontal {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  min-width: 0;
}

.school-program-info {
  flex: 1;
  min-width: 0;
}

.program-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.application-status {
  display: flex;
  align-items: center;
}

.status-tag {
  font-size: 12px;
}

.school-actions {
  flex-shrink: 0;
}

/* 无申请提示样式 */
.no-applications-hint {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
  margin-top: 20px;
  border: 2px dashed #e4e7ed;
  border-radius: 8px;
  background-color: #fafafa;
}

.hint-icon {
  font-size: 48px;
  color: #c0c4cc;
  margin-bottom: 12px;
}

.hint-text {
  font-size: 16px;
  color: #606266;
  margin: 0 0 6px 0;
  font-weight: 500;
}

.hint-description {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

/* 状态时间轴样式 */
.status-timeline {
  background: white;
  border-radius: 8px;
  padding: 12px 24px 24px 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.status-timeline h3 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 18px;
  font-weight: 600;
}

.timeline {
  position: relative;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 20px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #e4e7ed;
}

.timeline-item {
  position: relative;
  padding-left: 45px;
  margin-bottom: 16px;
}

.timeline-item:last-child {
  margin-bottom: 0;
}

.timeline-icon {
  position: absolute;
  left: 6px;
  top: 2px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  z-index: 1;
}

.timeline-item.success .timeline-icon {
  background: #67c23a;
  color: white;
}

.timeline-item.processing .timeline-icon {
  background: var(--primary-color);
  color: white;
}

.timeline-item.warning .timeline-icon {
  background: #e6a23c;
  color: white;
}

.timeline-item.upcoming .timeline-icon {
  background: #909399;
  color: white;
}

.timeline-item.error .timeline-icon {
  background: #f56c6c;
  color: white;
}

.timeline-content {
  background: #f8f9fa;
  border-radius: 6px;
  padding: 12px;
  border-left: 3px solid #e4e7ed;
}

.timeline-item.success .timeline-content {
  border-left-color: #67c23a;
}

.timeline-item.processing .timeline-content {
  border-left-color: var(--primary-color);
}

.timeline-item.warning .timeline-content {
  border-left-color: #e6a23c;
}

.timeline-item.error .timeline-content {
  border-left-color: #f56c6c;
}

.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 6px;
}

.status-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
}

.status-text {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}

.status-date {
  color: #909399;
  font-size: 14px;
  white-space: nowrap;
  margin-left: 16px;
}

.status-description {
  color: #606266;
  margin: 0 0 6px 0;
  line-height: 1.4;
  font-size: 13px;
}

.status-sub {
  color: #909399;
  font-size: 13px;
  margin: 0 0 6px 0;
}

.status-deadline {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #e6a23c;
  font-size: 13px;
  margin: 0 0 6px 0;
}

.status-progress {
  display: flex;
  align-items: center;
  gap: 8px;
}

.progress-bar {
  width: 100px;
  height: 4px;
  background: #e4e7ed;
  border-radius: 2px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 2px;
  transition: width 0.3s ease;
}

.progress-fill.success {
  background: #67c23a;
}

.progress-fill.processing {
  background: var(--primary-color);
}

.progress-fill.warning {
  background: #e6a23c;
}

.progress-text {
  font-size: 12px;
  color: #909399;
  font-weight: 500;
}

.status-action {
  margin-top: 8px;
}



/* 响应式设计 */
@media (max-width: 768px) {
  .application-progress {
    padding: 12px;
  }

  .selector-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .timeline-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .status-date {
    margin-left: 0;
  }


}

/* 录取决策指南样式 */
.admission-guidance {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-top: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.admission-guidance h3 {
  color: #1a1a1a;
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.guidance-content p {
  color: #666;
  line-height: 1.6;
  margin-bottom: 16px;
}

.guidance-content ul {
  list-style: none;
  padding: 0;
  margin: 16px 0;
}

.guidance-content li {
  color: #555;
  line-height: 1.6;
  margin-bottom: 8px;
  padding-left: 20px;
  position: relative;
}

.guidance-content li::before {
  content: '•';
  color: var(--primary-color);
  font-weight: bold;
  position: absolute;
  left: 0;
}

.guidance-content .el-button {
  margin-top: 16px;
}

/* 录取统计样式 */
.admission-stats {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  border: 1px solid #e9ecef;
}

.admission-stats h4 {
  color: #1a1a1a;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.stat-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  border: 1px solid #e9ecef;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: var(--primary-color);
  margin-bottom: 8px;
}

.stat-label {
  color: #666;
  font-size: 14px;
  font-weight: 500;
}



/* 快递式进度条样式 */
.progress-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.delivery-progress {
  position: relative;
  padding: 20px 0;
}

.progress-track {
  position: absolute;
  top: 50px;
  left: 0;
  right: 0;
  height: 4px;
  background: #f0f0f0;
  border-radius: 2px;
  z-index: 1;
}

.progress-line {
  height: 100%;
  background: linear-gradient(90deg, var(--primary-color), #52c41a);
  border-radius: 2px;
  transition: width 0.8s ease;
}

.progress-steps {
  display: flex;
  justify-content: space-between;
  position: relative;
  z-index: 2;
}

.progress-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  max-width: 200px;
}

.step-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  font-size: 20px;
  font-weight: bold;
  transition: all 0.3s ease;
  position: relative;
  z-index: 3;
}

.progress-step.completed .step-icon {
  background: #52c41a;
  color: white;
  box-shadow: 0 4px 12px rgba(82, 196, 26, 0.3);
}

.progress-step.current .step-icon {
  background: var(--primary-color);
  color: white;
  box-shadow: 0 4px 12px rgba(65, 113, 247, 0.3);
  animation: pulse 2s infinite;
}

.progress-step.pending .step-icon {
  background: #f0f0f0;
  color: #999;
  border: 2px solid #e0e0e0;
}

.progress-step.visa_processing .step-icon {
  background: #eb2f96;
  color: white;
  box-shadow: 0 4px 12px rgba(235, 47, 150, 0.3);
}

.progress-step.visa_approved .step-icon {
  background: #1677ff;
  color: white;
  box-shadow: 0 4px 12px rgba(22, 119, 255, 0.3);
}

.progress-step.visa_rejected .step-icon {
  background: #ff4d4f;
  color: white;
  box-shadow: 0 4px 12px rgba(255, 77, 79, 0.3);
}

.progress-step.enrolled .step-icon {
  background: #389e0d;
  color: white;
  box-shadow: 0 4px 12px rgba(56, 158, 13, 0.3);
}

.step-number {
  font-size: 16px;
  font-weight: 600;
}

.step-content {
  text-align: center;
  margin-top: 8px;
}

.step-title {
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
  font-size: 14px;
}

.step-date {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.step-description {
  font-size: 12px;
  color: #999;
  line-height: 1.4;
}

@keyframes pulse {
  0% {
    box-shadow: 0 4px 12px rgba(65, 113, 247, 0.3);
  }

  50% {
    box-shadow: 0 4px 20px rgba(65, 113, 247, 0.5);
  }

  100% {
    box-shadow: 0 4px 12px rgba(65, 113, 247, 0.3);
  }
}

/* 双栏布局容器 */
.dual-column-container {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
}

/* 消息通知框样式 */
.message-notification-box {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid #f0f0f0;
  flex: 1;
  min-width: 0;
  /* 允许flex项目收缩 */
}

/* 代办事项框样式 */
.todo-notification-box {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid #f0f0f0;
  flex: 1;
  min-width: 0;
  /* 允许flex项目收缩 */
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.message-header h3 {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #333;
  font-size: 18px;
}

.message-content {
  max-height: 400px;
  overflow-y: auto;
}

.empty-messages {
  text-align: center;
  padding: 40px 20px;
  color: #999;
}

.empty-messages .empty-icon {
  font-size: 48px;
  color: #ddd;
  margin-bottom: 16px;
}

.empty-messages .empty-text {
  font-size: 16px;
  margin-bottom: 8px;
  color: #666;
}

.empty-messages .empty-description {
  font-size: 14px;
  color: #999;
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.message-item:hover {
  border-color: var(--primary-color);
  box-shadow: 0 2px 8px rgba(65, 113, 247, 0.1);
}

.message-item.unread {
  background: #f6f8ff;
  border-color: var(--primary-color);
}

.message-avatar {
  flex-shrink: 0;
}

.message-body {
  flex: 1;
  min-width: 0;
}

.message-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.sender-name {
  font-weight: 600;
  color: #333;
}

.message-time {
  font-size: 12px;
  color: #999;
}

.message-title {
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  font-size: 14px;
}

.message-text {
  color: #666;
  line-height: 1.5;
  margin-bottom: 12px;
  font-size: 14px;
}

.message-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

/* 申请进度页面样式 */
.progress-dashboard {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-top: 24px;
}

/* 学校选择器样式 */
.selector-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.selector-label {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  white-space: nowrap;
}

@media (max-width: 1024px) {
  .progress-dashboard {
    grid-template-columns: 1fr;
  }
}

/* 学校选择部分样式 - 来自MaterialsUpload.vue */
.schools-selection-section {
  margin-bottom: 16px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 20px;
  text-align: left;
}

/* 已选择学校列表样式 */
.selected-schools-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.selected-school-item {
  display: inline-flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  background-color: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  width: fit-content;
  min-width: 300px;
  max-width: 100%;
  min-height: 56px;
}

.selected-school-item:hover {
  background-color: #e9ecef;
  transform: translateY(-1px);
}

.selected-school-item.active {
  border-color: var(--primary-color);
  background-color: #f0f4ff;
}

.school-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.school-details {
  flex: 1;
  min-width: 0;
}

.school-name {
  font-size: 14px;
  font-weight: 600;
  color: #1d1d1f;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.2;
}

.school-major {
  font-size: 12px;
  font-weight: 400;
  color: #6b7280;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-top: 2px;
  line-height: 1.2;
}

.status-tag {
  margin-left: 12px;
  flex-shrink: 0;
}

/* 新的紧凑型消息卡片样式 */
.message-item-new {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 8px;
  transition: all 0.3s ease;
  min-height: 50px;
  display: flex;
  flex-direction: column;
  width: 100%;
}

.message-item-new:hover {
  background: #e9ecef;
  border-color: #007bff;
}

.message-item-new.unread {
  border-left: 4px solid #007bff;
  background: #f0f8ff;
}

.message-main-content {
  flex: 1;
  margin-bottom: 8px;
  min-width: 0;
  /* 允许内容收缩 */
}

.message-header-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
  gap: 8px;
}

.sender-name {
  font-weight: 600;
  color: #333;
  font-size: 14px;
  flex-shrink: 0;
}

.message-time {
  font-size: 12px;
  color: #666;
  flex-shrink: 0;
}

.message-title {
  font-size: 13px;
  color: #555;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 100%;
}

.message-actions-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.unread-tag {
  font-size: 12px;
  flex-shrink: 0;
}

.detail-btn {
  font-size: 12px;
  padding: 4px 12px;
  height: 24px;
  flex-shrink: 0;
}

/* 代办事项相关样式 */
.todo-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.todo-header h3 {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #333;
  font-size: 18px;
}

.todo-content {
  max-height: 400px;
  overflow-y: auto;
}

.empty-todos {
  text-align: center;
  padding: 40px 20px;
  color: #999;
}

.empty-todos .empty-icon {
  font-size: 48px;
  color: #ddd;
  margin-bottom: 16px;
}

.empty-todos .empty-text {
  font-size: 16px;
  margin-bottom: 8px;
  color: #666;
}

.empty-todos .empty-description {
  font-size: 14px;
  color: #999;
}

.todos-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.todo-item {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 12px;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}

.todo-item:hover {
  background: #e9ecef;
  border-color: #28a745;
}

.todo-item.completed {
  background: #f0f8f0;
  border-color: #28a745;
}

.todo-main-content {
  flex: 1;
  margin-bottom: 8px;
}

.todo-header-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.todo-checkbox {
  flex-shrink: 0;
}

.todo-title {
  font-weight: 500;
  color: #333;
  font-size: 14px;
  flex: 1;
}

.todo-title.completed-text {
  text-decoration: line-through;
  color: #666;
}

.todo-description {
  font-size: 12px;
  color: #666;
  margin-bottom: 8px;
  line-height: 1.4;
}

.todo-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.todo-date {
  font-size: 12px;
  color: #666;
}

.todo-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.edit-btn,
.delete-btn {
  font-size: 12px;
  padding: 2px 8px;
}

.delete-btn {
  color: #dc3545;
}

.delete-btn:hover {
  color: #c82333;
}

/* 消息详细弹窗样式 */
.message-detail-content {
  padding: 0;
}

.detail-header {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-sender {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sender-info {
  flex: 1;
}

.sender-info .sender-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.sender-info .send-time {
  font-size: 14px;
  color: #666;
}

.detail-body {
  line-height: 1.6;
}

.detail-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f5f5f5;
}

.detail-content {
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.detail-attachments {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.detail-attachments h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #333;
}

.attachment-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 6px;
  margin-bottom: 8px;
}

.attachment-item:last-child {
  margin-bottom: 0;
}

.attachment-item span {
  flex: 1;
  font-size: 14px;
  color: #333;
}

.read-btn {
  margin-right: 8px;
}

@media (max-width: 768px) {
  .dual-column-container {
    flex-direction: column;
    gap: 16px;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .progress-steps {
    flex-direction: column;
    gap: 24px;
  }

  .progress-step {
    flex-direction: row;
    text-align: left;
    max-width: none;
  }

  .step-content {
    margin-top: 0;
    margin-left: 16px;
    text-align: left;
  }

  .progress-track {
    display: none;
  }

  .message-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .message-item {
    flex-direction: column;
    gap: 12px;
  }

  .message-info {
    flex-wrap: wrap;
  }
}
</style>
