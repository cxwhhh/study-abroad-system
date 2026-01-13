<template>
  <div class="tab-pane">
    <div class="progress-management-container">
      <!-- 状态监控面板 -->
      <div class="status-monitor-panel">
        <h5 class="section-title">
          <Activity class="section-icon" />
          申请状态监控
          <span class="status-summary" v-if="hasAlerts(application)">{{ getAlertCount(application) }}项需要关注</span>
        </h5>

        <div class="status-cards">
          <!-- 材料投递监控 -->
          <div class="status-card"
            :class="{ 'alert': application.materialsProgress < 100 && props.isDeadlineUrgentFn(application.deadline) }">
            <div class="card-header">
              <Upload class="card-icon" />
              <span class="card-title">材料投递</span>
              <div v-if="application.materialsProgress < 100 && props.isDeadlineUrgentFn(application.deadline)"
                class="status-badge urgent">
                未完成投递
              </div>
              <div v-else-if="application.materialsProgress < 100" class="status-badge warning">
                进行中
              </div>
              <div v-else class="status-badge success">
                已完成
              </div>
            </div>
            <div class="card-content">
              <div class="progress-info">
                <span>完成度: {{ application.materialsProgress }}%</span>
                <span v-if="props.isDeadlineUrgentFn(application.deadline)" class="deadline-alert">
                  剩余 {{ props.getRemainingDaysFn(application.deadline) }} 天
                </span>
              </div>
              <div class="progress-detail" v-if="application.materialsProgress < 100">
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: `${application.materialsProgress}%` }"></div>
                </div>
                <div class="missing-materials">
                  <div class="material-status-title">未提交材料:</div>
                  <div class="materials-tags">
                    <span class="material-item-tag" v-for="material in getMissingMaterials(application)"
                      :key="material">
                      {{ material }}
                    </span>
                  </div>
                </div>
              </div>
              <div v-if="getErrorMaterials(application).length > 0" class="error-materials">
                <div class="material-status-title">存在问题:</div>
                <div class="materials-tags">
                  <span class="material-item-tag error" v-for="material in getErrorMaterials(application)"
                    :key="material">
                    {{ material }}
                  </span>
                </div>
              </div>
              <div class="card-actions" v-if="application.materialsProgress < 100">
                <button class="action-btn primary" @click="sendDeadlineReminder(application)">
                  提醒学生
                </button>
                <button class="action-btn secondary" @click="checkMaterialsStatus(application)">
                  查看详情
                </button>
              </div>
            </div>
          </div>

          <!-- Offer状态监控 -->
          <div class="status-card" :class="{ 'alert': isOfferDelayed(application) }">
            <div class="card-header">
              <Mail class="card-icon" />
              <span class="card-title">Offer状态</span>
              <div v-if="isOfferDelayed(application)" class="status-badge urgent">
                等待超时
              </div>
              <div v-else-if="application.status === '录取'" class="status-badge success">
                已收到
              </div>
              <div v-else-if="application.status === '材料审核中'" class="status-badge warning">
                等待中
              </div>
            </div>
            <div class="card-content">
              <div class="progress-info">
                <span>当前状态: {{ getOfferStatusText(application) }}</span>
                <span v-if="isOfferDelayed(application)" class="time-info">
                  等待时间: {{ getWaitingDays(application) }} 天
                </span>
              </div>
              <div class="offer-status-detail">
                <div class="status-box" :class="getOfferStatusClass(application)">
                  <div class="status-icon">
                    <Check v-if="application.status === '录取'" />
                    <X v-else-if="application.status === '拒绝'" />
                    <Clock v-else-if="isOfferDelayed(application)" />
                    <FileText v-else />
                  </div>
                  <div class="status-info">
                    <div class="status-stage">{{ getOfferStageText(application) }}</div>
                    <div class="status-date" v-if="application.lastUpdateTime">
                      {{ props.formatDateFn(application.lastUpdateTime) }}
                    </div>
                  </div>
                </div>
                <div class="expected-date" v-if="isOfferDelayed(application)">
                  <Slash class="date-icon" />
                  <span>预期回复: {{ props.formatDateFn(getExpectedResponseDate(application)) }}</span>
                </div>
              </div>
              <div class="card-actions" v-if="isOfferDelayed(application)">
                <button class="action-btn primary" @click="checkSchoolStatus(application)">
                  联系学校
                </button>
                <button class="action-btn secondary" @click="updateOfferStatus(application)">
                  更新状态
                </button>
              </div>
            </div>
          </div>

          <!-- 签证信息监控 -->
          <div class="status-card" :class="{ 'alert': needsVisaInfo(application) }">
            <div class="card-header">
              <Globe class="card-icon" />
              <span class="card-title">签证信息</span>
              <div v-if="needsVisaInfo(application)" class="status-badge urgent">
                需提供信息
              </div>
              <div v-else-if="application.visaInfoProvided" class="status-badge success">
                已提供
              </div>
              <div v-else class="status-badge default">
                未开始
              </div>
            </div>
            <div class="card-content">
              <div class="progress-info">
                <span>状态: {{ getVisaStatusText(application) }}</span>
                <span v-if="needsVisaInfo(application)" class="time-info">
                  录取后: {{ getDaysSinceAdmission(application) }} 天
                </span>
              </div>
              <div class="visa-status-detail">
                <div class="visa-current-step">
                  <div class="step-indicator" :class="getVisaStepClass(application)"></div>
                  <div class="step-text">
                    <div class="step-name">{{ getVisaCurrentStep(application) }}</div>
                    <div class="step-description">{{ getVisaStepDescription(application) }}</div>
                  </div>
                </div>
                <div class="visa-info" v-if="application.status === '录取'">
                  <div class="visa-detail-item" v-if="application.visaDeadline">
                    <Calendar class="detail-icon" />
                    <span>截止日期: {{ props.formatDateFn(application.visaDeadline) }}</span>
                  </div>
                  <div class="visa-detail-item" v-if="application.visaType">
                    <Bookmark class="detail-icon" />
                    <span>签证类型: {{ application.visaType }}</span>
                  </div>
                </div>
              </div>
              <div class="card-actions" v-if="needsVisaInfo(application)">
                <button class="action-btn primary" @click="sendVisaInfo(application)">
                  发送签证信息
                </button>
                <button class="action-btn secondary" @click="scheduleVisaConsultation(application)">
                  安排咨询
                </button>
              </div>
            </div>
          </div>

          <!-- 学生活跃度监控 -->
          <div class="status-card" :class="{ 'alert': isInactiveApplication(application) }">
            <div class="card-header">
              <UserCheck class="card-icon" />
              <span class="card-title">学生互动</span>
              <div v-if="isInactiveApplication(application)" class="status-badge urgent">
                长期未活动
              </div>
              <div v-else class="status-badge success">
                正常
              </div>
            </div>
            <div class="card-content">
              <div class="progress-info">
                <span>最近活动: {{ props.formatTimeAgoFn(getLastActivityTime(application)) }}</span>
              </div>
              <div class="student-interaction">
                <div v-if="getLastActivity(application)" class="last-message">
                  <div class="message-meta">
                    <div class="message-type" :class="getLastActivity(application).type">
                      {{ getLastActivity(application).typeText }}
                    </div>
                    <div class="message-time">{{ props.formatDateFn(getLastActivity(application).time) }}</div>
                  </div>
                  <div class="message-content">
                    {{ truncateText(getLastActivity(application).content, 120) }}
                  </div>
                </div>
                <div v-else class="no-interaction">
                  <MessageSquare class="no-message-icon" />
                  <span>暂无互动记录</span>
                </div>
              </div>
              <div class="card-actions" v-if="isInactiveApplication(application)">
                <button class="action-btn primary" @click="contactStudent(application)">
                  联系学生
                </button>
                <button class="action-btn secondary" @click="sendReminderToStudent(application)">
                  发送提醒
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 学生申请问题处理 -->
      <div class="issue-management-panel">
        <h5 class="section-title">
          <AlertTriangle class="section-icon" />
          申请问题处理
        </h5>

        <div v-if="hasApplicationIssues(application)" class="issues-list">
          <div v-if="application.hasDispute" class="issue-item dispute">
            <div class="issue-icon">
              <AlertTriangle />
            </div>
            <div class="issue-content">
              <div class="issue-header">
                <span class="issue-title">学生申诉</span>
                <span class="issue-time">{{ props.formatTimeAgoFn(application.disputeTime) }}</span>
              </div>
              <p class="issue-description">{{ application.disputeMessage }}</p>
              <div class="issue-actions">
                <button class="action-btn primary" @click="reviewDispute(application)">
                  处理申诉
                </button>
                <button class="action-btn secondary" @click="contactSchool(application)">
                  联系学校
                </button>
              </div>
            </div>
          </div>

          <div v-if="application.hasReminder" class="issue-item reminder">
            <div class="issue-icon">
              <Bell />
            </div>
            <div class="issue-content">
              <div class="issue-header">
                <span class="issue-title">学生催促</span>
                <span class="issue-time">{{ props.formatTimeAgoFn(application.reminderTime) }}</span>
              </div>
              <p class="issue-description">{{ application.reminderMessage }}</p>
              <div class="issue-actions">
                <button class="action-btn primary" @click="respondToReminder(application)">
                  回复学生
                </button>
                <button class="action-btn secondary" @click="updateProgressStatus(application)">
                  更新状态
                </button>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="no-issues">
          <CheckCircle class="no-issues-icon" />
          <p class="no-issues-text">当前无待处理问题</p>
        </div>
      </div>

      <!-- 快速操作区域 -->
      <div class="quick-actions-section">
        <h5 class="section-title">
          <Edit class="section-icon" />
          快速操作
        </h5>

        <div class="quick-actions-grid">
          <button class="quick-action-btn" @click="updateApplicationStatus(application)">
            <Edit class="action-icon" />
            <span>更新状态</span>
          </button>
          <button class="quick-action-btn" @click="sendStudentNotification(application)">
            <Bell class="action-icon" />
            <span>通知学生</span>
          </button>
          <button class="quick-action-btn" @click="scheduleFollowUp(application)">
            <Clock class="action-icon" />
            <span>安排跟进</span>
          </button>
          <button class="quick-action-btn" @click="completeApplication(application)"
            :disabled="!canCompleteApplication(application)"
            :class="{ 'disabled': !canCompleteApplication(application) }">
            <CheckSquare class="action-icon" />
            <span>完成申请</span>
          </button>
          <button class="quick-action-btn" @click="exportProgressReport(application)">
            <FileText class="action-icon" />
            <span>导出报告</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 消息发送模态框 -->
    <div v-if="showMessageModal" class="modal-overlay">
      <div class="modal-container message-modal">
        <div class="modal-header">
          <h3 class="modal-title">发送消息给学生</h3>
          <button class="close-btn" @click="closeMessageModal">
            <X />
          </button>
        </div>
        <div class="modal-body">
          <div class="student-info">
            <div class="student-avatar">
              {{ application.student.name.charAt(0) }}
            </div>
            <div class="student-details">
              <h4>{{ application.student.name }}</h4>
              <p>{{ application.student.email }}</p>
              <span class="application-info">申请: {{ application.institution }} - {{ application.major }}</span>
            </div>
          </div>

          <div class="form-group">
            <label>消息类型 <span class="required">*</span></label>
            <div class="select-wrapper">
              <select v-model="messageForm.type" class="form-select">
                <option value="application_update">申请状态更新</option>
                <option value="material_reminder">材料提醒</option>
                <option value="deadline_notice">截止日期通知</option>
                <option value="general_message">一般消息</option>
              </select>
            </div>
          </div>

          <div class="form-group">
            <label>优先级 <span class="required">*</span></label>
            <div class="select-wrapper">
              <select v-model="messageForm.priority" class="form-select">
                <option value="normal">普通</option>
                <option value="important">重要</option>
                <option value="urgent">紧急</option>
              </select>
            </div>
          </div>

          <div class="form-group">
            <label>消息标题 <span class="required">*</span></label>
            <input type="text" v-model="messageForm.title" class="form-input" placeholder="请输入消息标题" maxlength="100" />
          </div>

          <div class="form-group">
            <label>消息内容 <span class="required">*</span></label>
            <textarea v-model="messageForm.content" class="form-textarea" placeholder="请输入消息内容" rows="6"
              maxlength="1000"></textarea>
            <div class="char-count">{{ messageForm.content.length }}/1000</div>
          </div>

          <div class="form-options">
            <label class="checkbox-container">
              <input type="checkbox" v-model="messageForm.emailNotification">
              <span class="checkmark"></span>
              同时发送邮件通知
            </label>
            <label class="checkbox-container">
              <input type="checkbox" v-model="messageForm.readReceipt">
              <span class="checkmark"></span>
              需要学生确认回执
            </label>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="closeMessageModal">取消</button>
          <button class="btn-send" @click="sendMessage" :disabled="!isFormValid || sending">
            <span v-if="sending">发送中...</span>
            <span v-else>发送消息</span>
          </button>
        </div>
      </div>
    </div>
  </div>

  <!-- 状态更新弹窗 -->
  <el-dialog v-model="statusUpdateDialog.visible" title="更新申请状态" width="400px" :before-close="handleStatusDialogClose">
    <div class="status-update-container">
      <label class="status-update-label">请选择新状态：</label>
      <select v-model="statusUpdateDialog.selectedStatus" class="status-update-select">
        <option v-for="option in statusUpdateDialog.options" :key="option" :value="option">
          {{ option }}
        </option>
      </select>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleStatusDialogClose">取消</el-button>
        <el-button type="primary" @click="confirmStatusUpdate">
          确认更新
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, ref, reactive } from 'vue';
import { Activity, Upload, Mail, Globe, UserCheck, AlertTriangle, Bell, CheckCircle, Edit, Clock, FileText, Check, X, Slash, Calendar, Bookmark, CheckSquare } from 'lucide-vue-next';
import { ElMessage, ElMessageBox, ElSelect, ElOption } from 'element-plus';
import { h } from 'vue';
import request from '@/utils/request';


const props = defineProps({
  application: {
    type: Object,
    required: true
  },
  isDeadlineUrgentFn: {
    type: Function,
    required: true
  },
  getRemainingDaysFn: {
    type: Function,
    required: true
  },
  formatDateFn: {
    type: Function,
    required: true
  },
  formatTimeAgoFn: {
    type: Function,
    required: true
  }
});

const emit = defineEmits(['status-updated']);

// 消息发送相关的响应式数据
const showMessageModal = ref(false);
const sending = ref(false);
const messageForm = reactive({
  type: 'application_update',
  priority: 'normal',
  title: '',
  content: '',
  emailNotification: false,
  readReceipt: false
});

// 状态更新弹窗相关的响应式数据
const statusUpdateDialog = reactive({
  visible: false,
  selectedStatus: '',
  options: [
    '已选择学校',
    '已提交材料',
    '材料审核中',
    '面试阶段',
    '已录取',
    '被拒绝',
    '签证申请中',
    '签证已批准',
    '签证被拒',
    '已入学'
  ],
  currentApplication: null,
  resolve: null,
  reject: null
});

// 表单验证
const isFormValid = computed(() => {
  return messageForm.title.trim().length > 0 && messageForm.content.trim().length > 0;
});

// 判断是否应该显示offer提醒
const shouldShowOfferAlert = (application) => {
  return application.offerReceived && application.status === '已录取';
};



// 处理争议
const handleDispute = (application, action) => {
  if (action === 'review') {
    ElMessage.info(`正在审核申请 ${application.id} 的争议`);
  } else if (action === 'contact') {
    ElMessage.info(`已联系 ${application.institution} 学校确认申请情况`);
  } else if (action === 'dismiss') {
    // Assuming application object can be mutated directly or via a more robust state management if needed
    if (application && typeof application === 'object') {
      application.hasDispute = false;
    }
    ElMessage.warning('已驳回学生争议');
  }
};

// 检查申请是否有待处理的Offer
const isOfferDelayed = (application) => {
  if (application.status !== '材料审核中' && application.status !== '已提交') return false;

  // 如果提交申请超过45天仍未收到结果，视为延迟
  const submitDate = new Date(application.lastUpdateTime || application.createTime);
  const now = new Date();
  const diffDays = Math.ceil((now - submitDate) / (1000 * 60 * 60 * 24));

  return diffDays > 45;
};

// 获取offer状态文本
const getOfferStatusText = (application) => {
  if (application.status === '已录取') return '已收到Offer';
  if (application.status === '被拒绝') return '申请被拒';
  if (application.status === '材料审核中') return '等待学校审核';
  if (application.status === '面试阶段') return '面试阶段';
  if (application.status === '已提交材料') return '已提交申请';
  return '尚未提交';
};

// 检查是否需要提供签证信息
const needsVisaInfo = (application) => {
  return application.status === '已录取' && !application.visaInfoProvided;
};

// 获取签证状态文本
const getVisaStatusText = (application) => {
  if (application.status !== '已录取') return '尚未录取';
  if (application.visaInfoProvided) return '已提供签证信息';
  return '需提供签证指导';
};

// 获取等待天数
const getWaitingDays = (application) => {
  const submitDate = new Date(application.lastUpdateTime || application.createTime);
  const now = new Date();
  return Math.ceil((now - submitDate) / (1000 * 60 * 60 * 24));
};

// 获取录取后天数
const getDaysSinceAdmission = (application) => {
  if (application.status !== '已录取') return 0;

  const admissionDate = new Date(application.updateTime);
  const now = new Date();
  return Math.ceil((now - admissionDate) / (1000 * 60 * 60 * 24));
};

// 检查申请是否长时间无动态
const isInactiveApplication = (application) => {
  if (!application.lastUpdateTime) return false;

  const now = new Date();
  const lastUpdate = new Date(application.lastUpdateTime);
  const diffDays = Math.ceil((now - lastUpdate) / (1000 * 60 * 60 * 24));

  // 如果申请状态不是已完成且超过30天没有更新，视为不活跃
  return application.status !== '已完成' && application.status !== '已录取' && diffDays > 30;
};

// 获取最后活动时间
const getLastActivityTime = (application) => {
  if (!application.studentActivities || application.studentActivities.length === 0) {
    return application.updateTime || application.createTime;
  }

  // 找出最近的活动时间
  const activities = [...application.studentActivities];
  activities.sort((a, b) => new Date(b.time) - new Date(a.time));
  return activities[0].time;
};

// 检查申请是否有问题需要处理
const hasApplicationIssues = (application) => {
  return application.hasDispute || application.hasReminder;
};

// 状态更新弹窗相关方法
const handleStatusDialogClose = () => {
  statusUpdateDialog.visible = false;
  if (statusUpdateDialog.reject) {
    statusUpdateDialog.reject(new Error('取消操作'));
  }
  resetStatusDialog();
};

const confirmStatusUpdate = () => {
  if (statusUpdateDialog.resolve) {
    statusUpdateDialog.resolve(statusUpdateDialog.selectedStatus);
  }
  statusUpdateDialog.visible = false;
  resetStatusDialog();
};

const resetStatusDialog = () => {
  statusUpdateDialog.selectedStatus = '';
  statusUpdateDialog.currentApplication = null;
  statusUpdateDialog.resolve = null;
  statusUpdateDialog.reject = null;
};

const showStatusUpdateDialog = (application) => {
  return new Promise((resolve, reject) => {
    // 获取当前状态的中文显示
    const reverseStatusMapping = {
      'selected': '已选择学校',
      'submitted': '已提交材料',
      'under_review': '材料审核中',
      'interview': '面试阶段',
      'admitted': '已录取',
      'rejected': '被拒绝',
      'visa_processing': '签证申请中',
      'visa_approved': '签证已批准',
      'visa_rejected': '签证被拒',
      'enrolled': '已入学'
    };

    const currentChineseStatus = application.applicationStatusDisplay ||
      reverseStatusMapping[application.status] ||
      application.status;

    statusUpdateDialog.currentApplication = application;
    statusUpdateDialog.selectedStatus = currentChineseStatus;
    statusUpdateDialog.resolve = resolve;
    statusUpdateDialog.reject = reject;
    statusUpdateDialog.visible = true;
  });
};

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

// 事件处理方法
const sendDeadlineReminder = (application) => {
  ElMessage.success(`已向学生 ${application.student.name} 发送截止日期提醒`);
};

const checkMaterialsStatus = (application) => {
  ElMessage.info(`正在查看申请 ${application.id} 的材料状态`);
};

const checkSchoolStatus = (application) => {
  ElMessage.info(`正在查询 ${application.institution} 的申请状态`);
};

const updateOfferStatus = async (application) => {
  try {
    // 显示状态选择对话框
    const { value: newStatus } = await ElMessageBox.prompt('请选择新的Offer状态', '更新Offer状态', {
      confirmButtonText: '确认更新',
      cancelButtonText: '取消',
      inputType: 'select',
      inputOptions: {
        '已提交': '已提交',
        '材料审核中': '材料审核中',
        '面试中': '面试中',
        '录取': '录取',
        '拒绝': '拒绝',
        '已完成': '已完成'
      },
      inputValue: application.status
    });

    // 更新本地状态
    const oldStatus = application.status;
    application.status = newStatus;
    application.lastUpdateTime = new Date().toISOString();

    // 同步到后端和学生端
    await syncApplicationStatusToStudent(application, newStatus, oldStatus);

    ElMessage.success(`申请 ${application.id} 的状态已更新为：${newStatus}`);
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新Offer状态失败:', error);
      ElMessage.error('更新状态失败，请重试');
    }
  }
};

const sendVisaInfo = async (application) => {
  try {
    // 更新签证信息提供状态
    application.visaInfoProvided = true;

    // 同步到学生端
    await syncVisaInfoToStudent(application);

    ElMessage.success(`已向学生 ${application.student.name} 发送签证办理信息`);
  } catch (error) {
    console.error('发送签证信息失败:', error);
    ElMessage.error('发送签证信息失败，请重试');
    application.visaInfoProvided = false;
  }
};

const scheduleVisaConsultation = async (application) => {
  try {
    const { value: consultationDate } = await ElMessageBox.prompt('请选择咨询时间', '安排签证咨询', {
      confirmButtonText: '确认安排',
      cancelButtonText: '取消',
      inputType: 'date'
    });

    // 同步到学生端
    await syncVisaConsultationToStudent(application, consultationDate);

    ElMessage.success(`已为学生 ${application.student.name} 安排 ${consultationDate} 的签证咨询`);
  } catch (error) {
    if (error !== 'cancel') {
      console.error('安排签证咨询失败:', error);
      ElMessage.error('安排签证咨询失败，请重试');
    }
  }
};

const contactStudent = async (application) => {
  try {
    const { value: message } = await ElMessageBox.prompt('请输入联系内容', '联系学生', {
      confirmButtonText: '发送消息',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入要发送给学生的消息内容',
      inputValidator: (value) => {
        if (!value || value.trim().length < 5) {
          return '消息内容不能少于5个字符';
        }
        return true;
      }
    });

    // 发送消息到学生端
    await sendMessageToStudent(application, message);

    ElMessage.success(`已向学生 ${application.student.name} 发送消息`);
  } catch (error) {
    if (error !== 'cancel') {
      console.error('联系学生失败:', error);
      ElMessage.error('发送消息失败，请重试');
    }
  }
};

const sendReminderToStudent = async (application) => {
  try {
    const { value: reminderType } = await ElMessageBox.prompt('请选择提醒类型', '发送提醒', {
      confirmButtonText: '发送提醒',
      cancelButtonText: '取消',
      inputType: 'select',
      inputOptions: {
        'deadline': '截止日期提醒',
        'materials': '材料补充提醒',
        'status': '申请状态更新',
        'interview': '面试安排提醒',
        'offer': 'Offer回复提醒'
      }
    });

    // 发送提醒到学生端
    await sendReminderToStudentAPI(application, reminderType);

    ElMessage.success(`已向学生 ${application.student.name} 发送${reminderType}提醒`);
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发送提醒失败:', error);
      ElMessage.error('发送提醒失败，请重试');
    }
  }
};

const reviewDispute = async (application) => {
  try {
    const { value: action } = await ElMessageBox.prompt('请选择处理方式', '处理申诉', {
      confirmButtonText: '确认处理',
      cancelButtonText: '取消',
      inputType: 'select',
      inputOptions: {
        'approve': '同意申诉',
        'reject': '驳回申诉',
        'investigate': '进一步调查'
      }
    });

    // 处理申诉并同步到学生端
    await processDisputeToStudent(application, action);

    if (action === 'approve') {
      application.hasDispute = false;
      ElMessage.success(`已同意学生 ${application.student.name} 的申诉`);
    } else if (action === 'reject') {
      application.hasDispute = false;
      ElMessage.warning(`已驳回学生 ${application.student.name} 的申诉`);
    } else {
      ElMessage.info(`正在进一步调查学生 ${application.student.name} 的申诉`);
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('处理申诉失败:', error);
      ElMessage.error('处理申诉失败，请重试');
    }
  }
};

const contactSchool = async (application) => {
  try {
    const { value: inquiry } = await ElMessageBox.prompt('请输入询问内容', '联系学校', {
      confirmButtonText: '发送询问',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入要向学校询问的内容',
      inputValidator: (value) => {
        if (!value || value.trim().length < 10) {
          return '询问内容不能少于10个字符';
        }
        return true;
      }
    });

    // 记录联系学校的操作
    await recordSchoolContact(application, inquiry);

    ElMessage.success(`已向 ${application.institution} 发送询问，并记录到申请日志中`);
  } catch (error) {
    if (error !== 'cancel') {
      console.error('联系学校失败:', error);
      ElMessage.error('联系学校失败，请重试');
    }
  }
};

const respondToReminder = async (application) => {
  try {
    const { value: response } = await ElMessageBox.prompt('请输入回复内容', '回复催促', {
      confirmButtonText: '发送回复',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入对学生催促的回复内容',
      inputValidator: (value) => {
        if (!value || value.trim().length < 5) {
          return '回复内容不能少于5个字符';
        }
        return true;
      }
    });

    // 回复学生催促并更新状态
    await respondToStudentReminder(application, response);
    application.hasReminder = false;

    ElMessage.success(`已回复学生 ${application.student.name} 的催促`);
  } catch (error) {
    if (error !== 'cancel') {
      console.error('回复催促失败:', error);
      ElMessage.error('回复催促失败，请重试');
    }
  }
};

const updateProgressStatus = async (application) => {
  try {
    const { value: progressUpdate } = await ElMessageBox.prompt('请输入进度更新', '更新申请进度', {
      confirmButtonText: '确认更新',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入申请进度的最新情况',
      inputValidator: (value) => {
        if (!value || value.trim().length < 5) {
          return '进度更新不能少于5个字符';
        }
        return true;
      }
    });

    // 更新进度并同步到学生端
    await syncProgressUpdateToStudent(application, progressUpdate);
    application.lastUpdateTime = new Date().toISOString();

    ElMessage.success(`申请 ${application.id} 的进度已更新`);
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新进度失败:', error);
      ElMessage.error('更新进度失败，请重试');
    }
  }
};

const updateApplicationStatus = async (application) => {
  try {
    // 中文到英文的状态映射（与后端保持一致）
    const statusMapping = {
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
    };

    // 英文到中文的状态映射（用于显示当前状态）
    const reverseStatusMapping = {
      'selected': '已选择学校',
      'submitted': '已提交材料',
      'under_review': '材料审核中',
      'interview': '面试阶段',
      'admitted': '已录取',
      'rejected': '被拒绝',
      'visa_processing': '签证申请中',
      'visa_approved': '签证已批准',
      'visa_rejected': '签证被拒',
      'enrolled': '已入学'
    };

    // 获取当前状态的中文显示
    const currentChineseStatus = application.applicationStatusDisplay ||
      reverseStatusMapping[application.status] ||
      application.status;

    // 使用Vue组件弹窗选择状态
    const chineseStatus = await showStatusUpdateDialog(application);

    // 将中文状态转换为英文状态
    const englishStatus = statusMapping[chineseStatus];
    if (!englishStatus) {
      throw new Error('无效的状态选择');
    }

    const oldStatus = application.applicationStatusDisplay || application.status;

    // 调用后端API更新状态（提交英文状态）
    const response = await request.put(`/application-school/${application.id}/status`, null, {
      params: {
        status: englishStatus
      }
    });

    // 检查响应是否成功
    if (response?.code === 200 && response.data === true) {
      // 更新本地状态显示（保存中文显示）
      application.applicationStatusDisplay = chineseStatus;
      application.applicationStatus = englishStatus;
      application.status = chineseStatus;
      application.lastUpdateTime = new Date().toISOString();

      emit('status-updated', { applicationId: application.id, studentId: application.studentId, status: englishStatus, oldStatus });

      ElMessage.success(`申请 ${application.id} 的状态已更新为：${chineseStatus}`);
    } else {
      throw new Error(response?.message || '更新失败');
    }
  } catch (error) {
    if (error.message !== '取消操作') {
      console.error('更新申请状态失败:', error);
      ElMessage.error('更新申请状态失败，请重试');
    }
  }
};

// API同步函数
const syncApplicationStatusToStudent = async (application, newStatus, oldStatus) => {
  console.log(`同步申请状态到学生端：${application.id} ${oldStatus} -> ${newStatus}`);

  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (Math.random() > 0.1) {
        resolve({ success: true });
      } else {
        reject(new Error('同步失败'));
      }
    }, 500);
  });
};

const syncVisaInfoToStudent = async (application) => {
  console.log(`同步签证信息到学生端：${application.id}`);

  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (Math.random() > 0.1) {
        resolve({ success: true });
      } else {
        reject(new Error('同步失败'));
      }
    }, 500);
  });
};

const syncVisaConsultationToStudent = async (application, date) => {
  console.log(`同步签证咨询安排到学生端：${application.id} - ${date}`);

  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (Math.random() > 0.1) {
        resolve({ success: true });
      } else {
        reject(new Error('同步失败'));
      }
    }, 500);
  });
};

const sendMessageToStudent = async (application, message) => {
  console.log(`发送消息到学生端：${application.id} - ${message}`);

  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (Math.random() > 0.1) {
        resolve({ success: true });
      } else {
        reject(new Error('发送失败'));
      }
    }, 500);
  });
};

const sendReminderToStudentAPI = async (application, reminderType) => {
  console.log(`发送提醒到学生端：${application.id} - ${reminderType}`);

  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (Math.random() > 0.1) {
        resolve({ success: true });
      } else {
        reject(new Error('发送失败'));
      }
    }, 500);
  });
};

const processDisputeToStudent = async (application, action) => {
  console.log(`处理申诉并同步到学生端：${application.id} - ${action}`);

  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (Math.random() > 0.1) {
        resolve({ success: true });
      } else {
        reject(new Error('处理失败'));
      }
    }, 500);
  });
};

const recordSchoolContact = async (application, inquiry) => {
  console.log(`记录学校联系：${application.id} - ${inquiry}`);

  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (Math.random() > 0.1) {
        resolve({ success: true });
      } else {
        reject(new Error('记录失败'));
      }
    }, 500);
  });
};

const respondToStudentReminder = async (application, response) => {
  console.log(`回复学生催促：${application.id} - ${response}`);

  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (Math.random() > 0.1) {
        resolve({ success: true });
      } else {
        reject(new Error('回复失败'));
      }
    }, 500);
  });
};

const syncProgressUpdateToStudent = async (application, progressUpdate) => {
  console.log(`同步进度更新到学生端：${application.id} - ${progressUpdate}`);

  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (Math.random() > 0.1) {
        resolve({ success: true });
      } else {
        reject(new Error('同步失败'));
      }
    }, 500);
  });
};

const sendStudentNotification = (application) => {
  showMessageModal.value = true;
  // 根据申请状态预设消息内容
  const statusMessages = {
    '已提交': '您的申请已成功提交，我们正在为您准备材料。',
    '材料审核中': '您的申请材料正在审核中，请耐心等待。',
    '面试中': '恭喜！您已进入面试阶段，请准备相关面试材料。',
    '录取': '恭喜！您已被录取，请及时查看录取通知书。',
    '拒绝': '很遗憾，您的申请未能通过，我们会为您提供其他选择。'
  };

  messageForm.title = `关于您的${application.institution}申请状态更新`;
  messageForm.content = statusMessages[application.status] || '关于您的申请有重要更新，请查看详情。';
};

// 关闭消息模态框
const closeMessageModal = () => {
  showMessageModal.value = false;
  // 重置表单
  Object.assign(messageForm, {
    type: 'application_update',
    priority: 'normal',
    title: '',
    content: '',
    emailNotification: false,
    readReceipt: false
  });
};

// 发送消息
const sendMessage = async () => {
  console.log('开始发送消息...');

  if (!messageForm.title.trim() || !messageForm.content.trim()) {
    ElMessage.warning('请填写消息标题和内容');
    return;
  }

  if (sending.value) {
    return; // 防止重复提交
  }

  sending.value = true;
  console.log('当前申请数据:', props.application);
  console.log('学生信息:', props.application.student);

  try {
    // 从localStorage获取当前管理员信息
    let senderId = 1;
    let senderName = '系统管理员';

    try {
      const adminInfo = localStorage.getItem('adminInfo');
      if (adminInfo) {
        const currentUser = JSON.parse(adminInfo);
        senderId = currentUser.id;
        senderName = currentUser.nickname || currentUser.username || '系统管理员';
        console.log('获取到当前管理员信息:', { senderId, senderName });
      } else {
        console.warn('localStorage中没有管理员信息，使用默认值');
      }
    } catch (error) {
      console.error('解析管理员信息失败:', error);
      console.warn('无法获取当前管理员信息，使用默认值');
    }

    const receiverId = props.application?.userId;
    if (!receiverId) {
      ElMessage.error('申请信息中未找到有效用户ID，无法发送消息');
      return;
    }
    const messageData = {
      senderId: senderId,
      senderName: senderName,
      senderType: 'teacher',
      receiverId,
      applicationId: props.application.id,
      title: messageForm.title,
      content: messageForm.content,
      messageType: messageForm.type,
      priority: messageForm.priority,
      requiresReply: messageForm.readReceipt,
      status: 'active'
    };

    console.log('准备发送的消息数据:', messageData);

    const response = await request.post('/messages/teacher', messageData);

    console.log('服务器响应:', response);

    if (response?.code === 200 || response?.success === true) {
      ElMessage.success('消息发送成功');
      closeMessageModal();
    } else {
      console.error('服务器返回错误:', response);
      ElMessage.error('消息发送失败：' + (response.message || '未知错误'));
    }
  } catch (error) {
    console.error('发送消息失败:', error);
    console.error('错误详情:', error.response?.data);
    ElMessage.error('消息发送失败：' + (error.response?.data?.message || error.message || '请稍后重试'));
  } finally {
    sending.value = false;
  }
};

const scheduleFollowUp = (application) => {
  ElMessage.success(`已为申请 ${application.id} 安排跟进提醒`);
};

const exportProgressReport = (application) => {
  ElMessage.success(`正在导出申请 ${application.id} 的进度报告`);
};

// 检查是否可以完成申请（只有已入学状态才能完成）
const canCompleteApplication = (application) => {
  return application.applicationStatus === 'enrolled' ||
    application.applicationStatusDisplay === '已入学' ||
    application.status === '已入学';
};

// 完成申请功能
const completeApplication = async (application) => {
  if (!canCompleteApplication(application)) {
    ElMessage.warning('只有已入学状态的申请才能完成');
    return;
  }

  try {
    await ElMessageBox.confirm(
      `确认完成申请 ${application.id}？这将把学生的入学信息插入到入学表中。`,
      '确认完成申请',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );

    let createdBy = 1;
    try {
      const adminInfo = localStorage.getItem('adminInfo');
      if (adminInfo) {
        const currentUser = JSON.parse(adminInfo);
        if (currentUser?.id != null) {
          createdBy = currentUser.id;
        }
      }
    } catch (e) {
      createdBy = 1;
    }

    const response = await request.post('/admin/applications/complete', {
      applicationId: application.id,
      programId: application.programId || application.majorId,
      enrollmentDate: new Date().toISOString(),
      createdBy
    });

    if (!(response?.success === true || response?.code === 200)) {
      throw new Error(response?.message || '完成申请失败');
    }

    ElMessage.success('申请已完成，入学信息已记录');

    // 刷新数据或触发父组件更新
    if (props.onApplicationCompleted) {
      props.onApplicationCompleted(application.id);
    }

  } catch (error) {
    if (error !== 'cancel') {
      console.error('完成申请失败:', error);
      ElMessage.error('完成申请失败：' + (error.message || '请稍后重试'));
    }
  }
};

// 检查是否有需要关注的项目
const hasAlerts = (application) => {
  return (application.materialsProgress < 100 && props.isDeadlineUrgentFn(application.deadline)) ||
    isOfferDelayed(application) ||
    needsVisaInfo(application) ||
    isInactiveApplication(application) ||
    hasApplicationIssues(application);
};

// 获取需要关注的项目数量
const getAlertCount = (application) => {
  let count = 0;

  if (application.materialsProgress < 100 && props.isDeadlineUrgentFn(application.deadline)) count++;
  if (isOfferDelayed(application)) count++;
  if (needsVisaInfo(application)) count++;
  if (isInactiveApplication(application)) count++;
  if (application.hasDispute) count++;
  if (application.hasReminder) count++;

  return count;
};

// 获取预期的回复日期
const getExpectedResponseDate = (application) => {
  const submitDate = new Date(application.lastUpdateTime || application.createTime);
  // 通常学校会在45天内回复
  submitDate.setDate(submitDate.getDate() + 45);
  return submitDate;
};



// 获取最新的学生活动
const getLastActivity = (application) => {
  if (!application.studentActivities || application.studentActivities.length === 0) {
    return null;
  }

  const activities = [...application.studentActivities];
  activities.sort((a, b) => new Date(b.time) - new Date(a.time));
  return activities[0];
};

// 截断文本并添加省略号



const truncateText = (text, maxLength) => {
  if (!text) return '';
  if (text.length <= maxLength) return text;

  return text.substring(0, maxLength) + '...';
};

// 获取缺少的材料
const getMissingMaterials = (application) => {
  // 模拟获取缺少的材料
  const allMaterials = ['个人陈述', '推荐信', '成绩单', '简历', '语言成绩'];
  const completedMaterials = application.completedMaterials || [];

  return allMaterials.filter(material => !completedMaterials.includes(material));
};

// 获取有问题的材料
const getErrorMaterials = (application) => {
  // 模拟获取有问题的材料
  return application.errorMaterials || [];
};

// 获取Offer状态的CSS类名
const getOfferStatusClass = (application) => {
  if (application.status === '已录取') return 'success';
  if (application.status === '被拒绝') return 'error';
  if (isOfferDelayed(application)) return 'warning';
  return 'default';
};

// 获取Offer阶段文本
const getOfferStageText = (application) => {
  switch (application.status) {
    case '待提交': return '申请准备中';
    case '已提交材料': return '已提交给院校';
    case '材料审核中': return '院校审核中';
    case '面试阶段': return '面试阶段';
    case '已录取': return '已录取';
    case '被拒绝': return '申请被拒';
    default: return '未知状态';
  }
};

// 获取签证步骤CSS类名
const getVisaStepClass = (application) => {
  if (application.status !== '已录取') return 'disabled';
  if (application.visaApproved) return 'completed';
  if (application.visaSubmitted) return 'processing';
  if (application.visaInfoProvided) return 'preparing';
  return 'initial';
};

// 获取签证当前步骤
const getVisaCurrentStep = (application) => {
  if (application.status !== '已录取') return '尚未录取';
  if (application.visaApproved) return '签证已批准';
  if (application.visaSubmitted) return '签证申请中';
  if (application.visaInfoProvided) return '准备材料中';
  return '等待提供签证信息';
};

// 获取签证步骤描述
const getVisaStepDescription = (application) => {
  if (application.status !== '已录取') return '学生需要先获得录取';
  if (application.visaApproved) return '学生可以前往目标国家';
  if (application.visaSubmitted) return `预计处理时间: ${application.visaProcessingTime || '2-4周'}`;
  if (application.visaInfoProvided) return '学生正在准备签证材料';
  return '需要向学生提供签证指导';
};
</script>

<style scoped>
.progress-management-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 状态监控面板样式 */
.status-monitor-panel {
  background-color: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 16px;
}

.status-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 12px;
  margin-top: 12px;
}

.status-card {
  border-radius: 12px;
  background: #f9fafb;
  border: 1px solid #f0f0f0;
  padding: 16px;
  transition: all 0.3s ease;
}

.status-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
}

.status-card.alert {
  border-left: 4px solid #f5222d;
  background: linear-gradient(135deg, #fff1f0 0%, #ffffff 100%);
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.card-icon {
  width: 20px;
  height: 20px;
  color: #1890ff;
  margin-right: 12px;
}

.card-title {
  font-weight: 600;
  font-size: 16px;
  flex: 1;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.urgent {
  background-color: #f5222d;
  color: white;
}

.status-badge.warning {
  background-color: #fa8c16;
  color: white;
}

.status-badge.success {
  background-color: #52c41a;
  color: white;
}

.status-badge.default {
  background-color: #d9d9d9;
  color: #666;
}

.card-content {
  padding: 0 4px;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  font-size: 14px;
  color: #666;
}

.deadline-alert {
  color: #f5222d;
  font-weight: 500;
}

.time-info {
  color: #666;
}

.card-actions {
  display: flex;
  gap: 8px;
}

/* 问题处理面板样式 */
.issue-management-panel {
  background-color: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 16px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.section-icon {
  width: 20px;
  height: 20px;
  color: #1890ff;
}

.issues-list {
  margin-top: 12px;
}

.issue-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  margin-bottom: 16px;
  border-radius: 12px;
  background: #f9fafb;
  border: 1px solid #f0f0f0;
}

.issue-item.dispute {
  border-left: 1px solid #f0f0f0;
  background: linear-gradient(135deg, #f9f0ff 0%, #ffffff 100%);
}

.issue-item.reminder {
  border-left: 1px solid #f0f0f0;
  background: linear-gradient(135deg, #fff7e6 0%, #ffffff 100%);
}

.issue-icon {
  width: 40px;
  height: 40px;
  border-radius: 20px;
  background: rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.issue-icon svg {
  width: 20px;
  height: 20px;
  color: #666;
}

.issue-content {
  flex: 1;
}

.issue-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.issue-title {
  font-weight: 600;
  font-size: 16px;
}

.issue-time {
  color: #999;
  font-size: 14px;
}

.issue-description {
  color: #666;
  margin-bottom: 16px;
  line-height: 1.6;
}

.issue-actions {
  display: flex;
  gap: 8px;
}

.no-issues {
  text-align: center;
  padding: 32px 20px;
}

.no-issues-icon {
  width: 48px;
  height: 48px;
  color: #52c41a;
  margin: 0 auto 16px;
}

.no-issues-text {
  color: #666;
}

/* 快速操作区域样式 */
.quick-actions-section {
  background: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 12px;
  margin-top: 12px;
}

.quick-action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  color: #333;
  font-size: 13px;
}

.quick-action-btn:hover {
  background: #e6f7ff;
  border-color: #1890ff;
  color: #1890ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.15);
}

.quick-action-btn.disabled {
  background: #f5f5f5;
  border-color: #d9d9d9;
  color: #bfbfbf;
  cursor: not-allowed;
  opacity: 0.6;
}

.quick-action-btn.disabled:hover {
  background: #f5f5f5;
  border-color: #d9d9d9;
  color: #bfbfbf;
  transform: none;
  box-shadow: none;
}

.action-icon {
  width: 18px;
  height: 18px;
}

.action-btn {
  padding: 8px 16px;
  border-radius: 8px;
  border: none;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-btn.primary {
  background: #1890ff;
  color: white;
}

.action-btn.primary:hover {
  background: #40a9ff;
  transform: translateY(-1px);
}

.action-btn.secondary {
  background: #f5f5f5;
  color: #666;
}

.action-btn.secondary:hover {
  background: #e6f7ff;
  color: #1890ff;
}

.status-summary {
  font-size: 13px;
  font-weight: 400;
  margin-left: 12px;
  background-color: #ff4d4f;
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
}

.progress-detail {
  margin-bottom: 16px;
  font-size: 13px;
}

.progress-bar {
  height: 6px;
  background-color: #f0f0f0;
  border-radius: 3px;
  margin-bottom: 8px;
}

.progress-fill {
  height: 100%;
  background-color: #1890ff;
  border-radius: 3px;
}

.missing-materials {
  font-size: 12px;
  color: #666;
}

.material-item-tag {
  display: inline-block;
  padding: 2px 6px;
  background-color: #fff2e8;
  color: #fa8c16;
  border-radius: 4px;
  margin-right: 4px;
  font-size: 12px;
}

.material-item-tag.error {
  background-color: #fff1f0;
  color: #f5222d;
}

.error-materials {
  margin-top: 8px;
}

.material-status-title {
  font-size: 13px;
  font-weight: 500;
  color: #666;
  margin-bottom: 6px;
}

.materials-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
}

.timeline-marker {
  position: relative;
  padding-left: 24px;
  margin: 8px 0;
}

.marker-dot {
  position: absolute;
  left: 0;
  top: 6px;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: #f5222d;
}

.marker-line {
  position: absolute;
  left: 5px;
  top: 6px;
  width: 1px;
  height: calc(100% - 6px);
  background-color: #f5222d;
}

.marker-date {
  font-size: 12px;
  color: #f5222d;
  font-weight: 500;
}

.marker-label {
  font-size: 12px;
  color: #666;
}

@media (max-width: 768px) {
  .status-cards {
    grid-template-columns: 1fr;
  }

  .card-actions {
    flex-direction: column;
  }

  .action-btn {
    width: 100%;
  }

  .issue-item {
    flex-direction: column;
  }

  .issue-icon {
    margin-bottom: 8px;
  }

  .quick-actions-grid {
    grid-template-columns: 1fr;
  }
}

/* 进度步骤样式 */
.step-progress {
  display: flex;
  margin: 16px 0;
  position: relative;
}

.step-progress::before {
  content: '';
  position: absolute;
  top: 8px;
  left: 16px;
  right: 16px;
  height: 1px;
  background-color: #e8e8e8;
  z-index: 0;
}

.step-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  z-index: 1;
}

.step-dot {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background-color: #f5f5f5;
  border: 1px solid #d9d9d9;
  margin-bottom: 8px;
}

.step-item.completed .step-dot {
  background-color: #52c41a;
  border-color: #52c41a;
}

.step-item.current .step-dot {
  background-color: #1890ff;
  border-color: #1890ff;
  box-shadow: 0 0 0 4px rgba(24, 144, 255, 0.2);
}

.step-item.disabled .step-dot {
  background-color: #f5f5f5;
  border-color: #d9d9d9;
  opacity: 0.5;
}

.step-label {
  font-size: 12px;
  color: #666;
  text-align: center;
  width: 100%;
}

.step-item.completed .step-label {
  color: #52c41a;
}

.step-item.current .step-label {
  color: #1890ff;
  font-weight: 500;
}

.step-item.disabled .step-label {
  color: #999;
}

/* 签证详情样式 */
.visa-details {
  margin-top: 12px;
  font-size: 13px;
  background-color: #f9f9f9;
  border-radius: 4px;
  padding: 8px;
}

.detail-item {
  margin-bottom: 4px;
  display: flex;
}

.detail-item .label {
  color: #666;
  margin-right: 8px;
}

.detail-item .value {
  color: #333;
  font-weight: 500;
}

/* 学生互动时间线样式 */
.interaction-timeline {
  margin: 16px 0;
}

.timeline-item {
  display: flex;
  margin-bottom: 16px;
}

.timeline-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: #d9d9d9;
  margin-right: 8px;
  margin-top: 6px;
  flex-shrink: 0;
}

.timeline-dot.recent {
  background-color: #1890ff;
  box-shadow: 0 0 0 3px rgba(24, 144, 255, 0.2);
}

.timeline-content {
  flex: 1;
}

.timeline-date {
  font-size: 11px;
  color: #999;
  margin-bottom: 2px;
}

.timeline-text {
  font-size: 13px;
  color: #333;
  margin-bottom: 4px;
}

.timeline-tag {
  display: inline-block;
  font-size: 11px;
  padding: 1px 6px;
  border-radius: 10px;
  background-color: #f5f5f5;
  color: #666;
}

.timeline-tag.message {
  background-color: #e6f7ff;
  color: #1890ff;
}

.timeline-tag.meeting {
  background-color: #f9f0ff;
  color: #722ed1;
}

.timeline-tag.note {
  background-color: #f6ffed;
  color: #52c41a;
}

.timeline-tag.system {
  background-color: #f5f5f5;
  color: #999;
}

.timeline-item.empty .timeline-text {
  color: #999;
  font-style: italic;
}

/* Offer 状态样式 */
.offer-status-detail {
  margin: 12px 0 16px;
}

.status-box {
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  background-color: #f5f5f5;
  margin-bottom: 8px;
}

.status-box.success {
  background-color: #f6ffed;
}

.status-box.error {
  background-color: #fff1f0;
}

.status-box.warning {
  background-color: #fff7e6;
}

.status-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.status-box.success .status-icon {
  background-color: #d9f7be;
  color: #52c41a;
}

.status-box.error .status-icon {
  background-color: #ffccc7;
  color: #f5222d;
}

.status-box.warning .status-icon {
  background-color: #ffe7ba;
  color: #fa8c16;
}

.status-info {
  flex: 1;
}

.status-stage {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.status-date {
  font-size: 12px;
  color: #999;
}

.expected-date {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #f5222d;
}

.date-icon {
  width: 14px;
  height: 14px;
  margin-right: 6px;
  color: #f5222d;
}

/* 签证状态样式 */
.visa-status-detail {
  margin: 12px 0 16px;
}

.visa-current-step {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12px;
}

.step-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 12px;
  margin-top: 6px;
  position: relative;
}

.step-indicator::after {
  content: '';
  position: absolute;
  top: -4px;
  left: -4px;
  right: -4px;
  bottom: -4px;
  border-radius: 50%;
  border: 1px solid;
  opacity: 0.3;
}

.step-indicator.initial {
  background-color: #d9d9d9;
}

.step-indicator.initial::after {
  border-color: #d9d9d9;
}

.step-indicator.preparing {
  background-color: #1890ff;
}

.step-indicator.preparing::after {
  border-color: #1890ff;
}

.step-indicator.processing {
  background-color: #fa8c16;
}

.step-indicator.processing::after {
  border-color: #fa8c16;
}

.step-indicator.completed {
  background-color: #52c41a;
}

.step-indicator.completed::after {
  border-color: #52c41a;
}

.step-indicator.disabled {
  background-color: #d9d9d9;
  opacity: 0.5;
}

.step-text {
  flex: 1;
}

.step-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.step-description {
  font-size: 12px;
  color: #666;
}

.visa-info {
  background-color: #f9f9f9;
  padding: 10px;
  border-radius: 6px;
}

.visa-detail-item {
  display: flex;
  align-items: center;
  margin-bottom: 6px;
  font-size: 12px;
  color: #666;
}

.visa-detail-item:last-child {
  margin-bottom: 0;
}

.detail-icon {
  width: 14px;
  height: 14px;
  margin-right: 6px;
  color: #999;
}

/* 学生互动样式 */
.student-interaction {
  margin: 12px 0 16px;
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 12px;
}

.last-message {
  display: flex;
  flex-direction: column;
}

.message-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.message-type {
  font-size: 12px;
  padding: 1px 6px;
  border-radius: 4px;
  background-color: #f5f5f5;
  color: #666;
}

.message-type.message {
  background-color: #e6f7ff;
  color: #1890ff;
}

.message-type.meeting {
  background-color: #f9f0ff;
  color: #722ed1;
}

.message-type.note {
  background-color: #f6ffed;
  color: #52c41a;
}

.message-time {
  font-size: 12px;
  color: #999;
}

.message-content {
  font-size: 13px;
  line-height: 1.5;
  color: #333;
}

.no-interaction {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 12px;
  color: #999;
  font-size: 13px;
}

.no-message-icon {
  width: 24px;
  height: 24px;
  color: #d9d9d9;
  margin-bottom: 8px;
}

/* 移除表格行和卡片选中时的边缘色条 */
.application-table tbody tr {
  border-left: 3px solid transparent;
  transition: all 0.3s;
}

.application-table tbody tr:hover {
  background-color: #f5f7fa;
}

.application-table tbody tr.selected {
  background-color: #f0f5ff;
  border-left: 3px solid transparent;
  /* 修改为透明，移除选中色条 */
}

/* 确保卡片没有选中色条 */
.monitor-card {
  border-left: 3px solid transparent;
  transition: all 0.3s;
}

.monitor-card.selected {
  background-color: #f0f5ff;
  border-left: 3px solid transparent;
  /* 修改为透明，移除选中色条 */
}

/* 新的进度管理样式 */
.progress-management-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.progress-overview-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 24px;
  color: white;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
}

.overview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.overview-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-icon {
  width: 24px;
  height: 24px;
}

.overview-title h4 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.progress-status {
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
}

.progress-status.success {
  background: rgba(82, 196, 26, 0.2);
}

.progress-status.warning {
  background: rgba(250, 140, 22, 0.2);
}

.progress-status.error {
  background: rgba(245, 34, 45, 0.2);
}

.progress-timeline {
  position: relative;
}

.timeline-track {
  height: 4px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 2px;
  margin-bottom: 24px;
  overflow: hidden;
}

.timeline-progress {
  height: 100%;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 2px;
  transition: width 0.6s ease;
}

.timeline-steps {
  display: flex;
  justify-content: space-between;
  position: relative;
}

.timeline-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  position: relative;
}

.step-marker {
  width: 48px;
  height: 48px;
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  transition: all 0.3s ease;
}

.timeline-step.completed .step-marker {
  background: rgba(255, 255, 255, 0.9);
  color: #667eea;
  box-shadow: 0 4px 12px rgba(255, 255, 255, 0.3);
}

.timeline-step.current .step-marker {
  background: #52c41a;
  color: white;
  box-shadow: 0 4px 12px rgba(82, 196, 26, 0.4);
  animation: pulse 2s infinite;
}

.timeline-step.pending .step-marker {
  background: rgba(255, 255, 255, 0.3);
  color: rgba(255, 255, 255, 0.7);
}

@keyframes pulse {
  0% {
    box-shadow: 0 4px 12px rgba(82, 196, 26, 0.4);
  }

  50% {
    box-shadow: 0 4px 20px rgba(82, 196, 26, 0.6);
  }

  100% {
    box-shadow: 0 4px 12px rgba(82, 196, 26, 0.4);
  }
}

.step-icon {
  width: 24px;
  height: 24px;
}

.step-content {
  text-align: center;
}

.step-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
}

.step-date {
  font-size: 12px;
  opacity: 0.8;
}

.critical-alerts-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 20px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.section-icon {
  width: 20px;
  height: 20px;
  color: #1890ff;
}

.alert-item {
  display: flex;
  padding: 16px;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border-left: 4px solid #fa8c16;
}

.alert-item.reminder {
  border-left-color: #fa8c16;
  background-color: #fff7e6;
}

.alert-item.reminder .alert-icon {
  color: #fa8c16;
}

.alert-item.dispute {
  border-left-color: #f5222d;
  background-color: #fff1f0;
}

.alert-item.dispute .alert-icon {
  color: #f5222d;
}

.alert-item.offer {
  border-left-color: #52c41a;
  background-color: #f6ffed;
}

.alert-item.offer .alert-icon {
  color: #52c41a;
}

.alert-item.inactive {
  border-left-color: #1890ff;
  background-color: #e6f7ff;
}

.alert-item.inactive .alert-icon {
  color: #1890ff;
}

.alert-icon {
  width: 24px;
  height: 24px;
  margin-right: 12px;
  flex-shrink: 0;
}

.alert-content {
  flex: 1;
}

.alert-title {
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.alert-time {
  font-size: 12px;
  color: #666;
  margin-bottom: 8px;
}

.alert-message {
  font-size: 14px;
  color: #333;
  margin-bottom: 12px;
  line-height: 1.5;
}

.visa-alert {
  display: inline-block;
  background-color: #fff1f0;
  color: #f5222d;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  margin-left: 8px;
}

.alert-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 8px;
}

.student-activity-timeline {
  margin-top: 24px;
}

.student-activity-timeline h5 {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 0 0 12px 0;
}

.activity-list {
  padding: 16px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  background-color: #fff;
  max-height: 300px;
  overflow-y: auto;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px dashed #f0f0f0;
}

.activity-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.activity-icon {
  width: 28px;
  height: 28px;
  border-radius: 14px;
  margin-right: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #e6f7ff;
  color: #1890ff;
  flex-shrink: 0;
}

.activity-icon.viewed {
  background-color: #e6f7ff;
  color: #1890ff;
}

.activity-icon.updated {
  background-color: #f6ffed;
  color: #52c41a;
}

.activity-icon.contacted {
  background-color: #fff7e6;
  color: #fa8c16;
}

.activity-icon.submitted {
  background-color: #f9f0ff;
  color: #722ed1;
}

.activity-icon.reminder {
  background-color: #fff1f0;
  color: #f5222d;
}

.activity-content {
  flex: 1;
}

.activity-time {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.activity-description {
  font-size: 14px;
  color: #333;
  line-height: 1.5;
}

.no-activities {
  text-align: center;
  padding: 24px;
  color: #999;
  font-size: 14px;
}

@media (max-width: 768px) {
  .alert-item {
    flex-direction: column;
  }

  .alert-icon {
    margin-right: 0;
    margin-bottom: 12px;
  }

  .alert-actions {
    flex-direction: column;
  }

  .btn-action-sm {
    width: 100%;
  }
}

/* 消息模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.message-modal {
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  animation: modalSlideIn 0.3s ease-out;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }

  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  padding: 8px;
  cursor: pointer;
  border-radius: 6px;
  color: #666;
  transition: all 0.2s;
}

.close-btn:hover {
  background-color: #f5f5f5;
  color: #333;
}

.modal-body {
  padding: 24px;
}

.student-info {
  display: flex;
  align-items: center;
  padding: 16px;
  background: linear-gradient(135deg, #f6f9fc 0%, #e9f4ff 100%);
  border-radius: 12px;
  margin-bottom: 24px;
  border: 1px solid #e1f0ff;
}

.student-avatar {
  width: 48px;
  height: 48px;
  border-radius: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 18px;
  margin-right: 16px;
  flex-shrink: 0;
}

.student-details h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.student-details p {
  margin: 0 0 6px 0;
  font-size: 14px;
  color: #666;
}

.application-info {
  font-size: 12px;
  color: #1890ff;
  background: rgba(24, 144, 255, 0.1);
  padding: 2px 8px;
  border-radius: 4px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.required {
  color: #ff4d4f;
}

.select-wrapper {
  position: relative;
}

.form-select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  font-size: 14px;
  background-color: white;
  transition: border-color 0.2s;
}

.form-select:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-input:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.form-textarea {
  width: 100%;
  min-height: 100px;
  padding: 10px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  font-size: 14px;
  resize: vertical;
  font-family: inherit;
  transition: border-color 0.2s;
}

.form-textarea:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.checkbox-item {
  display: flex;
  align-items: center;
}

.checkbox-item input[type="checkbox"] {
  margin-right: 8px;
  width: 16px;
  height: 16px;
}

.checkbox-item label {
  margin: 0;
  font-size: 14px;
  color: #666;
  cursor: pointer;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
  border-radius: 0 0 12px 12px;
}

.btn-cancel {
  padding: 8px 16px;
  border: 1px solid #d9d9d9;
  background: white;
  color: #666;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.btn-cancel:hover {
  border-color: #40a9ff;
  color: #40a9ff;
}

.btn-send {
  padding: 8px 20px;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-send:hover {
  background: linear-gradient(135deg, #40a9ff 0%, #1890ff 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

.btn-send:active {
  transform: translateY(0);
}

/* 状态更新弹窗样式 */
.status-update-container {
  margin: 15px 0;
  text-align: left;
}

.status-update-label {
  margin-bottom: 12px;
  color: #303133;
  font-size: 14px;
  font-weight: 500;
  display: block;
}

.status-update-select {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  font-size: 14px;
  color: #606266;
  background-color: #fff;
  outline: none;
  transition: border-color 0.2s ease;
  cursor: pointer;
  box-sizing: border-box;
}

.status-update-select:hover {
  border-color: #409eff;
}

.status-update-select:focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.status-update-select option {
  padding: 8px 12px;
  color: #606266;
}

@media (max-width: 768px) {
  .message-modal {
    width: 95%;
    margin: 20px;
  }

  .modal-body {
    padding: 20px;
  }

  .student-info {
    flex-direction: column;
    text-align: center;
  }

  .student-avatar {
    margin-right: 0;
    margin-bottom: 12px;
  }

  .status-update-select {
    padding: 10px 14px;
    font-size: 16px;
    /* 防止iOS缩放 */
  }
}
</style>
