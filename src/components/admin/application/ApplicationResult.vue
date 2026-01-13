<template>
  <div class="tab-pane">
    <div v-if="application.status === '录取' || application.status === '拒绝'" class="interview-result"
      :class="getInterviewResultClass(application)">
      <div class="interview-header">
        <div class="interview-status">{{ getInterviewStatusText(application) }}</div>
        <div class="interview-date" v-if="application.interviewDetails && application.interviewDetails.date">面试日期: {{ props.formatDateFn(application.interviewDetails.date) }}</div>
      </div>

      <div class="result-content">
        <div class="result-section">
          <h4>结果详情</h4>
          <div class="info-grid">
            <div class="info-item" v-if="application.status === '录取'">
              <div class="info-label">录取项目</div>
              <div class="info-value">{{ application.program }}</div>
            </div>
            <div class="info-item" v-if="application.status === '录取' && application.offerReceiveTime">
              <div class="info-label">录取通知日期</div>
              <div class="info-value">{{ props.formatDateFn(application.offerReceiveTime) }}</div>
            </div>
            <div class="info-item" v-if="application.status === '录取' && application.visaDeadline">
              <div class="info-label">签证办理截止日期</div>
              <div class="info-value" :class="{ 'warning-text': props.isDeadlineUrgentFn(application.visaDeadline) }">
                {{ props.formatDateFn(application.visaDeadline) }}
                <span v-if="props.isDeadlineUrgentFn(application.visaDeadline)" class="badge warning">
                  剩余{{ props.getRemainingDaysFn(application.visaDeadline) }}天
                </span>
              </div>
            </div>
            <div class="info-item" v-if="application.status === '拒绝' && application.rejectTime">
              <div class="info-label">拒绝通知日期</div>
              <div class="info-value">{{ props.formatDateFn(application.rejectTime) }}</div>
            </div>
            <div class="info-item" v-if="application.status === '拒绝' && application.rejectReason">
              <div class="info-label">拒绝原因</div>
              <div class="info-value">{{ application.rejectReason }}</div>
            </div>
          </div>
        </div>

        <div class="result-section" v-if="application.interviewDetails">
          <h4>面试信息</h4>
          <div class="info-grid">
            <div class="info-item" v-if="application.interviewDetails.interviewer">
              <div class="info-label">面试官</div>
              <div class="info-value">{{ application.interviewDetails.interviewer }}</div>
            </div>
            <div class="info-item" v-if="application.interviewDetails.type">
              <div class="info-label">面试类型</div>
              <div class="info-value">{{ application.interviewDetails.type }}</div>
            </div>
            <div class="info-item" v-if="application.interviewDetails.duration">
              <div class="info-label">面试时长</div>
              <div class="info-value">{{ application.interviewDetails.duration }}</div>
            </div>
            <div class="info-item full-width" v-if="application.interviewDetails.feedback">
              <div class="info-label">面试反馈</div>
              <div class="info-value">{{ application.interviewDetails.feedback }}</div>
            </div>
          </div>
        </div>

        <div class="result-actions">
          <button class="btn-primary" v-if="application.status === '录取'" @click="viewOfferDetails(application)">
            查看录取通知
          </button>
          <button class="btn-outline" v-if="application.status === '录取' && !application.visaInfoProvided"
            @click="sendVisaInfo(application)">
            发送签证指导
          </button>
          <button class="btn-outline" @click="viewInterviewDetails(application)">
            查看面试详情
          </button>
          <button class="btn-edit" @click="editInterviewDetails(application)">
            <Edit class="btn-icon" />
            编辑信息
          </button>
        </div>
      </div>
    </div>

    <div v-else-if="application.status === '面试中'" class="interview-pending">
      <div class="pending-icon">
        <Clock />
      </div>
      <h3>面试进行中</h3>
      <p class="pending-desc">学生目前正在面试阶段，等待面试结果</p>

      <div v-if="application.interviewDate" class="interview-info">
        <div class="info-item">
          <div class="info-label">面试日期</div>
          <div class="info-value">{{ props.formatDateFn(application.interviewDate) }}</div>
        </div>
      </div>

      <div class="result-actions">
        <button class="btn-primary" @click="updateInterviewStatus(application)">
          更新面试状态
        </button>
        <button class="btn-outline" @click="viewInterviewDetails(application)">
          查看面试详情
        </button>
      </div>
    </div>

    <div v-else class="no-result">
      <div class="no-result-icon">
        <AlertCircle />
      </div>
      <h3>暂无录取结果</h3>
      <p class="no-result-desc">
        {{ getNoResultMessage(application) }}
      </p>

      <div class="result-actions">
        <button class="btn-primary" @click="updateApplicationStatus(application)">
          更新申请状态
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Clock, AlertCircle, Edit } from 'lucide-vue-next';
import { ElMessage } from 'element-plus';

const props = defineProps({
  application: {
    type: Object,
    required: true
  },
  formatDateFn: {
    type: Function,
    required: true
  },
  getRemainingDaysFn: {
    type: Function,
    required: true
  },
  isDeadlineUrgentFn: {
    type: Function,
    required: true
  }
});

const emit = defineEmits(['request-send-visa-info']);

// 本地辅助函数
const getInterviewResultClass = (application) => {
  if (!application || !application.status) return '';
  if (application.status === '录取') return 'admitted';
  if (application.status === '拒绝') return 'rejected';
  if (['面试中', '已提交', '材料审核中', '待提交'].includes(application.status)) return 'in-progress';
  return ''; 
};

const getInterviewStatusText = (application) => {
  if (!application || !application.status) return '状态未知';
  if (application.status === '录取') return '面试通过，已获录取';
  if (application.status === '拒绝') return '面试未通过';
  if (application.status === '面试中') return '面试进行中';
  if (application.status === '已提交') return '申请已提交';
  if (application.status === '材料审核中') return '材料审核中';
  if (application.status === '待提交') return '申请待提交';
  return '状态未知';
};

const getNoResultMessage = (application) => {
  if (!application || !application.status) return '申请正在处理中，暂无明确结果信息。';
  if (application.status === '待提交') return '申请尚未提交，请先完成材料准备并提交申请。';
  if (application.status === '已提交') return '申请已提交，等待学校审核。';
  if (application.status === '材料审核中') return '学校正在审核申请材料，请耐心等待。';
  // 对于已经是录取、拒绝、面试中的状态，这个函数通常不应该被调用来显示主要信息，
  // 但可以作为一种备用文本或在特定逻辑下使用。
  // 如果这些状态下不应显示此消息，则可以在调用处进行逻辑控制。
  return '申请正在处理中，请耐心等待结果。'; 
};

// 事件处理方法
const viewOfferDetails = (application) => {
  ElMessage.info(`查看申请 ${application.id} 的录取通知`);
};

const sendVisaInfo = (application) => {
  // 触发父组件中定义的事件处理器
  emit('request-send-visa-info', application);
};

const viewInterviewDetails = (application) => {
  ElMessage.info(`查看申请 ${application.id} 的面试详情`);
};

const editInterviewDetails = (application) => {
  ElMessage.info(`编辑申请 ${application.id} 的面试信息`);
};

const updateInterviewStatus = (application) => {
  ElMessage.info(`更新申请 ${application.id} 的面试状态`);
};

const updateApplicationStatus = (application) => {
  ElMessage.info(`更新申请 ${application.id} 的状态`);
};
</script>

<style scoped>
.interview-result {
  background-color: #f9f9f9;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 24px;
}

.interview-result.admitted {
  border-top: 4px solid #52c41a;
}

.interview-result.rejected {
  border-top: 4px solid #f5222d;
}

.interview-result.in-progress {
  border-top: 4px solid #1890ff;
}

.interview-header {
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: rgba(0, 0, 0, 0.02);
}

.interview-status {
  font-size: 18px;
  font-weight: 600;
}

.admitted .interview-status {
  color: #52c41a;
}

.rejected .interview-status {
  color: #f5222d;
}

.interview-date {
  font-size: 14px;
  color: #666;
}

.result-content {
  padding: 24px;
}

.result-section {
  margin-bottom: 24px;
}

.result-section h4 {
  font-size: 16px;
  font-weight: 600;
  margin-top: 0;
  margin-bottom: 16px;
  color: #333;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-label {
  font-size: 12px;
  color: #999;
}

.info-value {
  font-size: 14px;
  color: #333;
}

.warning-text {
  color: #fa8c16;
}

.badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  margin-left: 8px;
}

.badge.warning {
  background-color: #fff7e6;
  color: #fa8c16;
}

.result-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 24px;
}

/* 面试进行中 */
.interview-pending {
  background-color: #f9f9f9;
  border-radius: 12px;
  padding: 32px 24px;
  text-align: center;
  margin-bottom: 24px;
  border-top: 4px solid #1890ff;
}

.pending-icon {
  width: 64px;
  height: 64px;
  background-color: #e6f7ff;
  border-radius: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}

.pending-icon svg {
  width: 32px;
  height: 32px;
  color: #1890ff;
}

.interview-pending h3 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #1890ff;
}

.pending-desc {
  color: #666;
  margin-bottom: 24px;
}

.interview-info {
  max-width: 300px;
  margin: 0 auto 24px;
  text-align: left;
}

/* 无结果 */
.no-result {
  background-color: #f9f9f9;
  border-radius: 12px;
  padding: 32px 24px;
  text-align: center;
  margin-bottom: 24px;
  border-top: 4px solid #d9d9d9;
}

.no-result-icon {
  width: 64px;
  height: 64px;
  background-color: #f5f5f5;
  border-radius: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}

.no-result-icon svg {
  width: 32px;
  height: 32px;
  color: #999;
}

.no-result h3 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #666;
}

.no-result-desc {
  color: #666;
  margin-bottom: 24px;
}

/* 按钮样式 */
.btn-primary {
  background-color: #1890ff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary:hover {
  background-color: #40a9ff;
}

.btn-outline {
  background-color: transparent;
  color: #1890ff;
  border: 1px solid #1890ff;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-outline:hover {
  background-color: #e6f7ff;
}

.btn-edit {
  background-color: transparent;
  color: #666;
  border: 1px solid #d9d9d9;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-edit:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.btn-icon {
  width: 14px;
  height: 14px;
}

@media (max-width: 768px) {
  .result-actions {
    flex-direction: column;
  }

  .result-actions button {
    width: 100%;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>