<template>
  <div class="step-page">
    <nav-bar></nav-bar>
    
    <!-- 页面标题 已经弃用 -->
    <div class="page-header">
      <h1>入学奖励</h1>
      <div class="timing">入学后1个月内</div>
    </div>

    <!-- 主要内容区域 -->
    <div class="page-content">
      <!-- 奖励政策 -->
      <div class="content-section">
        <h2>奖励政策</h2>
        <div class="policy-grid">
          <div class="policy-item" v-for="(policy, index) in policies" :key="index">
            <div class="policy-icon">
              <el-icon><component :is="policy.icon" /></el-icon>
            </div>
            <div class="policy-content">
              <h3>{{ policy.title }}</h3>
              <p>{{ policy.description }}</p>
              <div class="policy-details">
                <strong>具体政策：</strong>
                <ul>
                  <li v-for="(detail, detailIndex) in policy.details" :key="detailIndex">
                    {{ detail }}
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 申请流程 -->
      <div class="content-section">
        <h2>申请流程</h2>
        <div class="process-timeline">
          <el-steps :active="4" finish-status="success">
            <el-step
              v-for="(step, index) in applyProcess"
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

      <!-- 常见问题 -->
      <div class="content-section">
        <h2>常见问题</h2>
        <div class="faq-section">
          <el-collapse v-model="activeNames">
            <el-collapse-item
              v-for="(faq, index) in faqs"
              :key="index"
              :title="faq.question"
              :name="index">
              <div class="faq-answer">
                <p>{{ faq.answer }}</p>
                <ul v-if="faq.points">
                  <li v-for="(point, pointIndex) in faq.points" :key="pointIndex">
                    {{ point }}
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
        <p>我们的顾问可以为您提供详细的奖励政策说明</p>
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
  Money, 
  Document, 
  School, 
  Calendar,
  Upload,
  Check,
  ChatLineRound
} from '@element-plus/icons-vue'

// 奖励政策数据
const policies = ref([
  {
    icon: 'Money',
    title: '学费奖励',
    description: '根据学费金额提供相应比例的奖励',
    details: [
      '本科学费奖励比例：3%-5%',
      '研究生学费奖励比例：4%-6%',
      '博士学费奖励比例：5%-8%',
      '奖励金额将在确认入学后发放'
    ]
  },
  {
    icon: 'School',
    title: '住宿补贴',
    description: '为学生提供住宿费用补贴',
    details: [
      '校内宿舍补贴：每学期1000元',
      '校外住宿补贴：每学期800元',
      '补贴将分期发放',
      '需提供有效住宿证明'
    ]
  },
  {
    icon: 'Document',
    title: '生活津贴',
    description: '提供日常生活补助',
    details: [
      '基础生活津贴：每月300元',
      '优秀学生额外补贴：每月200元',
      '节假日特别补贴',
      '根据实际情况调整补贴金额'
    ]
  }
])

// 申请流程数据
const applyProcess = ref([
  {
    icon: 'Document',
    title: '提交申请',
    description: '填写奖励申请表格并提交相关证明'
  },
  {
    icon: 'Upload',
    title: '材料审核',
    description: '工作人员审核申请材料'
  },
  {
    icon: 'Check',
    title: '确认资格',
    description: '确认申请人奖励资格'
  },
  {
    icon: 'Money',
    title: '奖励发放',
    description: '按期发放奖励金额'
  }
])

// 常见问题数据
const faqs = ref([
  {
    question: '什么时候可以申请奖励？',
    answer: '学生在确认入学并完成注册后即可申请奖励',
    points: [
      '需要提供入学证明',
      '需要提供学生证复印件',
      '需要提供银行账户信息'
    ]
  },
  {
    question: '奖励如何发放？',
    answer: '奖励将分期通过银行转账方式发放',
    points: [
      '首期奖励在确认入学后30天内发放',
      '后续奖励将按学期发放',
      '特殊情况可协商发放方式'
    ]
  },
  {
    question: '如何提高奖励比例？',
    answer: '可以通过以下方式提高奖励比例：',
    points: [
      '成绩优异可获得额外奖励',
      '推荐其他学生可获得推荐奖励',
      '参与学校活动可获得活动奖励'
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

.policy-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
}

.policy-item {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.policy-icon {
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

.policy-content h3 {
  margin: 0 0 10px;
  font-size: 20px;
  color: #333;
}

.policy-content p {
  margin: 0 0 15px;
  color: #666;
  line-height: 1.6;
}

.policy-details {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 8px;
}

.policy-details strong {
  color: #409EFF;
  display: block;
  margin-bottom: 10px;
}

.policy-details ul {
  margin: 0;
  padding-left: 20px;
}

.policy-details li {
  color: #666;
  margin-bottom: 8px;
  line-height: 1.6;
}

.process-timeline {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.faq-section {
  max-width: 800px;
  margin: 0 auto;
}

.faq-answer {
  color: #666;
  line-height: 1.6;
}

.faq-answer p {
  margin: 0 0 15px;
}

.faq-answer ul {
  margin: 0;
  padding-left: 20px;
}

.faq-answer li {
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
  .policy-grid {
    grid-template-columns: 1fr;
  }
}
</style>