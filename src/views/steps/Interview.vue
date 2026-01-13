<template>
  <div class="step-page">
    <nav-bar></nav-bar>
    
    <!-- 页面标题 已经弃用-->
    <div class="page-header">
      <h1>面试录取</h1>
      <div class="timing">申请后1-3个月</div>
    </div>

    <!-- 主要内容区域 -->
    <div class="page-content">
      <!-- 面试准备 -->
      <div class="content-section">
        <h2>面试准备</h2>
        <div class="prep-grid">
          <div class="prep-item" v-for="(item, index) in interviewPrep" :key="index">
            <div class="prep-icon">
              <el-icon><component :is="item.icon" /></el-icon>
            </div>
            <div class="prep-content">
              <h3>{{ item.title }}</h3>
              <p>{{ item.description }}</p>
              <ul class="prep-tips">
                <li v-for="(tip, tipIndex) in item.tips" :key="tipIndex">
                  {{ tip }}
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      <!-- 常见问题 -->
      <div class="content-section">
        <h2>常见问题</h2>
        <div class="faq-section">
          <el-collapse v-model="activeNames">
            <el-collapse-item
              v-for="(faq, index) in commonQuestions"
              :key="index"
              :title="faq.question"
              :name="index">
              <div class="faq-answer">
                <p>{{ faq.answer }}</p>
                <div class="answer-tips" v-if="faq.tips">
                  <strong>回答技巧：</strong>
                  <ul>
                    <li v-for="(tip, tipIndex) in faq.tips" :key="tipIndex">
                      {{ tip }}
                    </li>
                  </ul>
                </div>
              </div>
            </el-collapse-item>
          </el-collapse>
        </div>
      </div>

      <!-- 录取流程 -->
      <div class="content-section">
        <h2>录取流程</h2>
        <div class="admission-process">
          <el-steps :active="4" finish-status="success">
            <el-step
              v-for="(step, index) in admissionSteps"
              :key="index"
              :title="step.title"
              :description="step.description" />
          </el-steps>
        </div>
      </div>

      <!-- 咨询部分 -->
      <div class="consultation-section">
        <h2>需要帮助？</h2>
        <p>我们的留学顾问可以为您提供专业的面试辅导</p>
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
import { Document, Timer, Message, User, Calendar } from '@element-plus/icons-vue'

// 面试准备数据
const interviewPrep = ref([
  {
    icon: 'Document',
    title: '材料准备',
    description: '准备面试所需的各项材料和文件',
    tips: [
      '准备个人简历的打印件',
      '准备作品集或研究成果展示',
      '准备相关证书原件',
      '准备笔记本和笔'
    ]
  },
  {
    icon: 'Timer',
    title: '时间管理',
    description: '合理安排面试时间,提前做好准备',
    tips: [
      '确认面试时区差异',
      '提前15-30分钟进入等候室',
      '测试设备和网络',
      '准备备用设备'
    ]
  },
  {
    icon: 'Message',
    title: '语言准备',
    description: '加强口语练习,提高表达能力',
    tips: [
      '练习专业术语的发音',
      '准备自我介绍',
      '练习常见问题回答',
      '提高口语流畅度'
    ]
  },
  {
    icon: 'User',
    title: '仪表礼仪',
    description: '注意着装和礼仪细节',
    tips: [
      '着装正式得体',
      '保持微笑和眼神交流',
      '注意坐姿和手势',
      '营造专业形象'
    ]
  }
])

// 常见问题数据
const commonQuestions = ref([
  {
    question: '为什么选择我们学校？',
    answer: '这个问题考察你对学校的了解程度和申请动机',
    tips: [
      '提前研究学校特色和优势',
      '结合个人发展目标',
      '突出学校独特之处',
      '表达真诚的兴趣'
    ]
  },
  {
    question: '介绍一下你的研究计划',
    answer: '这个问题考察你的学术规划和研究能力',
    tips: [
      '清晰表达研究方向',
      '突出创新点和可行性',
      '展示前期准备工作',
      '与导师研究方向结合'
    ]
  },
  {
    question: '你的优势是什么？',
    answer: '这个问题考察你的自我认知和竞争力',
    tips: [
      '结合具体事例',
      '突出个人特色',
      '展示成长经历',
      '强调与专业相关的能力'
    ]
  }
])

// 录取流程数据
const admissionSteps = ref([
  {
    title: '面试通知',
    description: '收到学校面试邀请'
  },
  {
    title: '面试准备',
    description: '准备材料和模拟练习'
  },
  {
    title: '参加面试',
    description: '按时参加面试并展现最佳状态'
  },
  {
    title: '等待结果',
    description: '耐心等待面试结果'
  },
  {
    title: '录取通知',
    description: '收到正式录取通知'
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

.prep-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 30px;
}

.prep-item {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.prep-icon {
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

.prep-content h3 {
  margin: 0 0 10px;
  font-size: 20px;
  color: #333;
}

.prep-content p {
  margin: 0 0 15px;
  color: #666;
  line-height: 1.6;
}

.prep-tips {
  margin: 0;
  padding-left: 20px;
  color: #666;
}

.prep-tips li {
  margin-bottom: 8px;
  line-height: 1.6;
}

.faq-section {
  max-width: 800px;
  margin: 0 auto;
}

.faq-answer {
  color: #666;
  line-height: 1.6;
}

.answer-tips {
  margin-top: 15px;
  background: #f5f7fa;
  padding: 15px;
  border-radius: 8px;
}

.answer-tips strong {
  color: #409EFF;
  display: block;
  margin-bottom: 10px;
}

.answer-tips ul {
  margin: 0;
  padding-left: 20px;
}

.answer-tips li {
  margin-bottom: 8px;
  line-height: 1.6;
}

.admission-process {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
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
  .prep-grid {
    grid-template-columns: 1fr;
  }
}
</style> 