<template>
  <div class="step-page">
    <nav-bar></nav-bar>
    
    <!-- 页面标题 已经弃用-->
    <div class="page-header">
      <h1>申请提交</h1>
      <div class="timing">申请前3-6个月</div>
    </div>

    <!-- 主要内容区域 -->
    <div class="page-content">
      <!-- 申请流程 -->
      <div class="content-section">
        <h2>申请流程</h2>
        <div class="process-timeline">
          <el-timeline>
            <el-timeline-item
              v-for="(activity, index) in submissionProcess"
              :key="index"
              :type="activity.type"
              :color="activity.color"
              :size="activity.size"
              :icon="activity.icon"
              :timestamp="activity.timestamp">
              <h3>{{ activity.title }}</h3>
              <p>{{ activity.content }}</p>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>

      <!-- 注意事项 -->
      <div class="content-section">
        <h2>注意事项</h2>
        <div class="notes-grid">
          <div class="note-item" v-for="(note, index) in importantNotes" :key="index">
            <div class="note-icon">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="note-content">
              <h3>{{ note.title }}</h3>
              <p>{{ note.description }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 提交指南 -->
      <div class="content-section">
        <h2>提交指南</h2>
        <div class="submission-guide">
          <el-collapse v-model="activeNames">
            <el-collapse-item
              v-for="(guide, index) in submissionGuides"
              :key="index"
              :title="guide.title"
              :name="index">
              <div class="guide-content">
                <p>{{ guide.content }}</p>
                <ul v-if="guide.steps">
                  <li v-for="(step, stepIndex) in guide.steps" :key="stepIndex">
                    {{ step }}
                  </li>
                </ul>
              </div>
            </el-collapse-item>
          </el-collapse>
        </div>
      </div>

      <!-- 咨询部分 -->
      <div class="consultation-section">
        <h2>需要帮助？</h2>
        <p>我们的留学顾问可以为您提供申请提交全程指导</p>
        <el-button type="primary" size="large" @click="showConsultation">
          立即咨询
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import NavBar from '../../components/NavBar.vue'
import { Warning, Document, Upload, Check, Calendar } from '@element-plus/icons-vue'

// 申请流程数据
const submissionProcess = ref([
  {
    timestamp: '第一步',
    title: '创建申请账号',
    content: '在目标院校官网或统一申请平台创建申请账号',
    type: 'primary',
    size: 'large',
    icon: 'User'
  },
  {
    timestamp: '第二步',
    title: '填写基本信息',
    content: '完整填写个人信息、教育背景、工作经历等',
    type: 'primary',
    size: 'large',
    icon: 'Document'
  },
  {
    timestamp: '第三步',
    title: '上传申请材料',
    content: '按要求上传所有必需的申请材料',
    type: 'primary',
    size: 'large',
    icon: 'Upload'
  },
  {
    timestamp: '第四步',
    title: '支付申请费',
    content: '确认并支付申请费用',
    type: 'primary',
    size: 'large',
    icon: 'Money'
  },
  {
    timestamp: '第五步',
    title: '提交申请',
    content: '最终检查并提交申请',
    type: 'success',
    size: 'large',
    icon: 'Check'
  }
])

// 注意事项数据
const importantNotes = ref([
  {
    title: '截止日期',
    description: '务必在申请截止日期前完成所有材料的提交,建议预留3-5天的缓冲时间'
  },
  {
    title: '材料完整性',
    description: '确保所有必需材料都已准备齐全,并符合学校要求的格式和规范'
  },
  {
    title: '信息准确性',
    description: '仔细核对所有填写的信息,确保准确无误,避免因信息错误影响申请'
  },
  {
    title: '付款确认',
    description: '确保申请费支付成功,并保存付款凭证'
  }
])

// 提交指南数据
const submissionGuides = ref([
  {
    title: '如何创建申请账号',
    content: '大多数院校都有自己的申请系统,需要创建专门的申请账号',
    steps: [
      '访问院校官网找到申请入口',
      '填写基本信息创建账号',
      '激活邮箱完成注册',
      '保存好账号密码'
    ]
  },
  {
    title: '材料上传要求',
    content: '不同院校对材料的格式和大小有不同要求',
    steps: [
      '检查支持的文件格式（通常为PDF）',
      '确认文件大小限制',
      '使用规范的文件命名',
      '保证扫描件清晰完整'
    ]
  },
  {
    title: '申请费支付说明',
    content: '了解支付方式和注意事项',
    steps: [
      '确认支付金额和支付方式',
      '准备信用卡或其他支付工具',
      '注意货币单位和汇率',
      '保存支付凭证'
    ]
  }
])

const activeNames = ref([0])

// 显示咨询弹窗
const showConsultation = () => {
  // 实现咨询功能
}
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
  margin: 40px auto;
  padding: 0 20px;
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

.process-timeline {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.notes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
  padding: 20px;
}

.note-item {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: flex-start;
  gap: 20px;
}

.note-icon {
  background: #f56c6c;
  color: white;
  width: 40px;
  height: 40px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.note-content h3 {
  margin: 0 0 10px;
  font-size: 18px;
  color: #333;
}

.note-content p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.submission-guide {
  max-width: 800px;
  margin: 0 auto;
}

.guide-content {
  padding: 10px 0;
}

.guide-content p {
  margin: 0 0 15px;
  color: #666;
  line-height: 1.6;
}

.guide-content ul {
  margin: 0;
  padding-left: 20px;
}

.guide-content li {
  color: #666;
  line-height: 1.8;
  margin-bottom: 8px;
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

@media (max-width: 768px) {
  .notes-grid {
    grid-template-columns: 1fr;
  }
}
</style> 