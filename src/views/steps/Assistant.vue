<template>
  <div class="step-page">
    <nav-bar></nav-bar>
    
    <!-- 步骤流程图 已经弃用-->
    <div class="steps-progress">
      <div class="steps-wrapper">
        <el-steps :active="5" finish-status="success" align-center>
          <el-step 
            v-for="(step, index) in applicationSteps" 
            :key="index"
            :title="step.title"
            :description="step.timing">
            <template #icon>
              <el-icon><component :is="step.icon" /></el-icon>
            </template>
          </el-step>
        </el-steps>
      </div>
    </div>
    
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>学校助手</h1>
      <div class="timing">入学前1-3个月</div>
    </div>

    <!-- 主要内容区域 -->
    <div class="page-content">
      <!-- 服务内容 -->
      <div class="content-section">
        <h2>服务内容</h2>
        <div class="services-grid">
          <div class="service-item" v-for="(service, index) in services" :key="index">
            <div class="service-icon">
              <el-icon><component :is="service.icon" /></el-icon>
            </div>
            <div class="service-content">
              <h3>{{ service.title }}</h3>
              <p>{{ service.description }}</p>
              <ul class="service-features">
                <li v-for="(feature, featureIndex) in service.features" :key="featureIndex">
                  {{ feature }}
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      <!-- 服务流程 -->
      <div class="content-section">
        <h2>服务流程</h2>
        <div class="process-steps">
          <el-steps :active="4" finish-status="success" direction="vertical">
            <el-step
              v-for="(step, index) in serviceProcess"
              :key="index"
              :title="step.title"
              :description="step.description">
              <template #icon>
                <el-icon><component :is="step.icon" /></el-icon>
              </template>
            </el-step>
          </el-steps>
        </div>
      </div>

      <!-- 联系方式 -->
      <div class="content-section">
        <h2>联系方式</h2>
        <div class="contact-grid">
          <div class="contact-item" v-for="(contact, index) in contactMethods" :key="index">
            <div class="contact-icon">
              <el-icon><component :is="contact.icon" /></el-icon>
            </div>
            <div class="contact-content">
              <h3>{{ contact.title }}</h3>
              <p>{{ contact.value }}</p>
              <el-button v-if="contact.action" type="primary" @click="contact.handler">
                {{ contact.action }}
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 咨询部分 -->
      <div class="consultation-section">
        <h2>需要帮助？</h2>
        <p>我们的留学顾问随时为您提供专业的咨询服务</p>
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
import { 
  House, 
  School, 
  User, 
  Message, 
  Phone, 
  Location,
  Document,
  Service,
  ChatLineRound,
  Connection,
  Guide,
  Upload,
  Check,
  Money
} from '@element-plus/icons-vue'

// 服务内容数据
const services = ref([
  {
    icon: 'House',
    title: '住宿安排',
    description: '帮助学生解决住宿问题,提供全方位住宿服务',
    features: [
      '校内宿舍申请指导',
      '校外租房建议',
      '临时住宿安排',
      '室友匹配服务'
    ]
  },
  {
    icon: 'School',
    title: '选课指导',
    description: '提供专业的选课建议和指导',
    features: [
      '课程体系解读',
      '选课策略建议',
      '课程难度分析',
      '学分规划指导'
    ]
  },
  {
    icon: 'User',
    title: '生活适应',
    description: '帮助学生快速适应海外生活',
    features: [
      '文化差异指导',
      '日常生活建议',
      '社交圈子建立',
      '心理调适支持'
    ]
  },
  {
    icon: 'Service',
    title: '学习支持',
    description: '提供全面的学习支持服务',
    features: [
      '学习方法指导',
      '资源获取途径',
      '作业辅导建议',
      '考试准备支持'
    ]
  }
])

// 服务流程数据
const serviceProcess = ref([
  {
    icon: 'ChatLineRound',
    title: '需求沟通',
    description: '了解学生具体需求和困难'
  },
  {
    icon: 'Document',
    title: '方案制定',
    description: '根据需求制定个性化解决方案'
  },
  {
    icon: 'Connection',
    title: '资源对接',
    description: '连接相关资源,提供具体帮助'
  },
  {
    icon: 'Service',
    title: '持续跟进',
    description: '定期跟进进展,及时调整方案'
  }
])

// 联系方式数据
const contactMethods = ref([
  {
    icon: 'Phone',
    title: '电话咨询',
    value: '+86 400-123-4567',
    action: '立即拨打',
    handler: () => {
      window.location.href = 'tel:+86 400-123-4567'
    }
  },
  {
    icon: 'Message',
    title: '在线咨询',
    value: '工作时间: 9:00-21:00',
    action: '开始聊天',
    handler: () => {
      // 实现在线聊天功能
    }
  },
  {
    icon: 'Location',
    title: '线下咨询',
    value: '北京市朝阳区xxx大厦',
    action: '查看地图',
    handler: () => {
      // 实现地图查看功能
    }
  }
])

// 显示咨询弹窗
const showConsultation = () => {
  // 实现咨询功能
}

// 申请流程步骤数据
const applicationSteps = ref([
  {
    icon: 'Guide',
    title: '前期准备',
    timing: '12-18个月'
  },
  {
    icon: 'Document',
    title: '材料准备',
    timing: '6-12个月'
  },
  {
    icon: 'Upload',
    title: '申请提交',
    timing: '申请季'
  },
  {
    icon: 'Check',
    title: '签证准备',
    timing: '2-3个月'
  },
  {
    icon: 'Service',
    title: '学校助手',
    timing: '1-3个月'
  },
  {
    icon: 'Money',
    title: '入学奖励',
    timing: '入学后'
  }
])
</script>

<style scoped>
/* 步骤流程图样式 */
.steps-progress {
  background: #fff;
  padding: 40px 20px;
  border-bottom: 1px solid #eee;
}

.steps-wrapper {
  max-width: 1200px;
  margin: 0 auto;
}

:deep(.el-steps) {
  --el-step-icon-size: 34px;
}

:deep(.el-step__title) {
  font-size: 15px;
  color: #666;
}

:deep(.el-step__head.is-process) {
  color: #409EFF;
  border-color: #409EFF;
}

:deep(.el-step__title.is-process) {
  color: #409EFF;
  font-weight: 600;
}

:deep(.el-step__description) {
  font-size: 13px;
}

:deep(.el-step__line) {
  background-color: #dcdfe6;
}

:deep(.el-step.is-horizontal .el-step__line) {
  height: 2px;
  top: 16px;
  left: -1px;
  right: -1px;
}

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

.services-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 30px;
}

.service-item {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.service-icon {
  background: #409EFF;
  color: white;
  width: 50px;
  height: 50px;
  border-radius: 25px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-bottom: 20px;
}

.service-content h3 {
  margin: 0 0 10px;
  font-size: 20px;
  color: #333;
}

.service-content p {
  margin: 0 0 15px;
  color: #666;
  line-height: 1.6;
}

.service-features {
  margin: 0;
  padding-left: 20px;
  color: #666;
}

.service-features li {
  margin-bottom: 8px;
  line-height: 1.6;
}

.process-steps {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.contact-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
  padding: 20px;
}

.contact-item {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.contact-icon {
  background: #409EFF;
  color: white;
  width: 60px;
  height: 60px;
  border-radius: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  margin: 0 auto 20px;
}

.contact-content h3 {
  margin: 0 0 10px;
  font-size: 20px;
  color: #333;
}

.contact-content p {
  margin: 0 0 20px;
  color: #666;
  line-height: 1.6;
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
  .services-grid,
  .contact-grid {
    grid-template-columns: 1fr;
  }
}
</style>