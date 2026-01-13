<template>
  <div v-if="isLoading" class="loading">
    <el-loading></el-loading>
  </div>
  <div v-else class="step-page">
    <nav-bar></nav-bar>
    
    <!-- 步骤流程图 已经弃用 -->
    <div class="steps-progress">
      <div class="steps-wrapper">
        <div 
          class="step-card" 
          v-for="(step, index) in applicationSteps" 
          :key="index"
          :class="{
            'active': index === 0,
            'finished': index < 0
          }"
          @click="selectStep(index)">
          <div class="first-content">
            <div class="step-icon">
              <el-icon><component :is="step.icon" /></el-icon>
            </div>
            <div class="step-title">{{ step.title }}</div>
          </div>
          <div class="second-content">
            <div class="step-status">
              {{ index === 0 ? '进行中' : (index < 0 ? '已完成' : '未开始') }}
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>前期准备</h1>
      <div class="timing">申请前12-18个月</div>
    </div>

    <!-- 主要内容区域 -->
    <div class="page-content">
      <!-- 准备事项 -->
      <div class="content-section">
        <h2>准备事项</h2>
        <div class="preparation-items">
          <div class="prep-item" 
               v-for="(item, index) in preparationItems" 
               :key="index"
               @click="showItemDetail(item)">
            <div class="item-icon">
              <el-icon><component :is="item.icon" /></el-icon>
            </div>
            <div class="item-content">
              <h3>{{ item.title }}</h3>
              <p>{{ item.description }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 时间规划 -->
      <div class="content-section">
        <h2>时间规划</h2>
        <el-timeline>
          <el-timeline-item
            v-for="(activity, index) in timeline"
            :key="index"
            :timestamp="activity.time"
            :type="activity.type">
            {{ activity.content }}
          </el-timeline-item>
        </el-timeline>
      </div>

      <!-- 常见问题 -->
      <div class="content-section">
        <h2>常见问题</h2>
        <div class="faq-container">
          <div class="faq-item" v-for="(faq, index) in faqs" :key="index">
            <h3>{{ faq.question }}</h3>
            <p>{{ faq.answer }}</p>
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

    <!-- 添加弹窗组件 -->
    <div class="detail-dialog" v-if="showDialog" @click="closeDialog">
      <div class="dialog-content" @click.stop>
        <div class="dialog-close" @click="closeDialog">
          <el-icon><Close /></el-icon>
        </div>
        <div class="dialog-header">
          <div class="dialog-subtitle">{{ selectedItem?.category || '留学准备' }}</div>
          <h2 class="dialog-title">{{ selectedItem?.title }}</h2>
        </div>
        <div class="dialog-body">
          <div class="image-container">
            <div class="image-description">
              {{ selectedItem?.category }} · {{ selectedItem?.title }}
            </div>
            <div class="dialog-image">
              <img :src="selectedItem?.image" :alt="selectedItem?.title">
            </div>
          </div>
          <div class="dialog-text">
            <p class="dialog-description">{{ selectedItem?.description }}</p>
            <div class="dialog-details">
              <h3>详细内容</h3>
              <ul>
                <li v-for="(detail, index) in selectedItem?.details" :key="index">
                  {{ detail }}
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import NavBar from '../../components/NavBar.vue'
import { 
  School, 
  Calendar, 
  DocumentChecked, 
  Trophy,
  Guide,
  Document,
  Upload,
  Check,
  Service,
  Money,
  Close
} from '@element-plus/icons-vue'

const isLoading = ref(true)

onMounted(() => {
  // 模拟数据加载
  setTimeout(() => {
    isLoading.value = false
  }, 100)
})

// 申请流程步骤数据
const applicationSteps = ref([
  {
    icon: 'Guide',
    title: '前期规划',
    timing: '12-18个月',
    description: '开始规划留学目标，确定目标国家和专业方向'
  },
  {
    icon: 'Document',
    title: '材料准备',
    timing: '6-12个月',
    description: '收集和准备成绩单、学位证书等基础材料'
  },
  {
    icon: 'Upload',
    title: '申请提交',
    timing: '申请季',
    description: '确定目标院校列表，开始准备文书材料'
  },
  {
    icon: 'Check',
    title: '签证准备',
    timing: '2-3个月',
    description: '规划并准备语言考试（IELTS/TOEFL）和标准化考试（GRE/GMAT）'
  },
  {
    icon: 'Service',
    title: '学校助手',
    timing: '1-3个月',
    description: '规划和参与实习、科研、竞赛等活动，提升个人背景'
  },
  {
    icon: 'Money',
    title: '入学返点',
    timing: '入学后',
    description: '准备并参加标准化考试，收集整理申请材料'
  }
])

// 添加弹窗相关的状态
const showDialog = ref(false)
const selectedItem = ref(null)

// 准备事项数据
const preparationItems = ref([
  {
    icon: 'School',
    title: '选校定位',
    category: '留学规划',
    description: '根据个人情况和目标确定申请院校范围，包括目标院校、安全院校和保底院校',
    image: '/images/school-selection.jpg',
    details: [
      '分析个人学术背景和申请优势',
      '研究目标院校的录取要求和录取率',
      '考虑专业排名和就业前景',
      '评估学费和生活成本',
      '制定合理的申请院校列表'
    ]
  },
  {
    icon: 'Calendar',
    title: '考试规划',
    category: '考试准备',
    description: '规划并准备语言考试（IELTS/TOEFL）和标准化考试（GRE/GMAT）',
    image: '/images/exam-prep.jpg',
    details: [
      '制定语言考试和标准化考试时间表',
      '选择合适的备考方式和资源',
      '进行模拟测试和评估',
      '针对性提高薄弱环节',
      '合理安排考试时间确保最佳状态'
    ]
  },
  {
    icon: 'DocumentChecked',
    title: '材料准备',
    category: '申请材料',
    description: '收集和准备成绩单、学位证书等基础材料',
    image: '/images/documents.jpg',
    details: [
      '准备成绩单和学位证书',
      '撰写个人陈述和文书材料',
      '获取推荐信',
      '准备作品集（如需要）',
      '整理实习和工作经验证明'
    ]
  },
  {
    icon: 'Trophy',
    title: '背景提升',
    category: '个人提升',
    description: '规划和参与实习、科研、竞赛等活动，提升个人背景',
    image: '/images/background.jpg',
    details: [
      '参与相关领域的实习项目',
      '加入科研项目或实验室',
      '参加学术竞赛和比赛',
      '获取专业证书和资格认证',
      '培养领导力和团队协作能力'
    ]
  }
])

// 时间线数据
const timeline = ref([
  {
    time: '18个月前',
    content: '开始规划留学目标，确定目标国家和专业方向',
    type: 'primary'
  },
  {
    time: '15个月前',
    content: '准备并参加语言考试，制定提升计划',
    type: 'primary'
  },
  {
    time: '12个月前',
    content: '准备并参加标准化考试，收集整理申请材料',
    type: 'primary'
  },
  {
    time: '10个月前',
    content: '确定目标院校列表，开始准备文书材料',
    type: 'success'
  }
])

// 常见问题数据
const faqs = ref([
  {
    question: '什么时候开始准备留学最合适？',
    answer: '建议在计划入学前18-24个月开始准备。这样有充足的时间完成语言考试、标准化考试、准备申请材料等各项任务，也能从容地进行背景提升和院校选择。'
  },
  {
    question: '如何选择目标院校和专业？',
    answer: '选择目标院校和专业需要综合考虑以下因素：个人学术背景、职业规划、院校排名、专业优势、地理位置、学费成本等。建议将院校分为冲刺、目标和保底三个档次，确保申请策略的合理性。'
  },
  {
    question: '语言考试和标准化考试如何安排？',
    answer: '建议先准备语言考试（TOEFL/IELTS），达到理想分数后再准备标准化考试（GRE/GMAT）。考试时间要预留充足的准备期和重考机会，建议在提交申请前4-6个月完成所有考试。'
  },
  {
    question: '如何提升个人背景？',
    answer: '可以通过以下方式提升个人背景：参与相关领域的实习、加入研究项目、参加学术竞赛、获取专业证书、参与志愿服务等。重要的是要与目标专业相关，并且能够展现你的能力和潜力。'
  }
])

// 显示咨询弹窗
const showConsultation = () => {
  // 实现咨询功能
}

// 选择步骤
const selectStep = (index) => {
  // 实现步骤选择逻辑
}

// 显示详情弹窗
const showItemDetail = (item) => {
  selectedItem.value = item
  showDialog.value = true
  document.body.style.overflow = 'hidden'
}

// 关闭弹窗
const closeDialog = () => {
  showDialog.value = false
  document.body.style.overflow = ''
}
</script>

<style scoped>
.step-page {
  padding-top: 60px;
}

/* 步骤流程图样式 */
.steps-progress {
  background: #fff;
  padding: 10px 0 30px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
}

.steps-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: center;
  gap: 60px;
  flex-wrap: wrap;
  padding: 10px;
}

/* 页面标题容器样式 */
.page-header {
  max-width: 1200px;          /* 限制最大宽度,保持页面布局一致性 */
  margin: 0 auto;             /* 水平居中对齐 */
  padding: 40px 0 20px 0;     /* 上下添加内边距,营造空间感 */
  text-align: left;           /* 文本左对齐 */
}

/* 主标题样式 */
.page-header h1 {
  font-size: 48px;            /* 设置大号字体,突出标题 */
  color: #1d1d1f;            /* 使用深色提高可读性 */
  margin: -20px 0 0 -200px;    /* 使用负边距微调位置,创造视觉张力 */
  font-weight: 600;           /* 设置字重,增强视觉层级 */
  letter-spacing: -0.5px;     /* 调整字间距,优化大字号显示效果 */
  display: inline-block;      /* 设置为行内块元素,便于控制位置 */
  position: relative;         /* 开启相对定位,为可能的装饰元素做准备 */
}

@media (max-width: 768px) {
  .page-header {
    padding: 30px 20px 20px 20px;
  }
  
  .page-header h1 {
    font-size: 36px;
    margin: -10px 0 0 -10px;
  }
}

.timing {
  font-size: 20px;
  color: #86868b;
  margin-top: 10px;
  margin-left: -198px;
  padding-left: 0;
}

@media (max-width: 768px) {
  .timing {
    margin-left: -10px;
  }
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
  font-size: 30px;
  color: #333;
  margin-bottom: 30px;
  text-align: left;
  margin-left: -197px;
}

/* 准备事项卡片容器样式 */
.preparation-items {
  display: flex;              /* 使用弹性布局 */
  flex-direction: row;        /* 水平排列子元素 */
  justify-content: center;    /* 子元素在主轴上居中对齐 */
  gap: 24px;                 /* 设置子元素之间的间距 */
  padding: 0;             /* 容器内边距 */
  margin-left: -320px;            /* 容器左边距 */
  max-width: 2000px;         /* 限制容器最大宽度,避免在超宽屏幕上过度拉伸 */
}

.prep-item {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  text-align: center;
  height: 400px;
  width: 200px;
  border-radius: 16px;
  color: white;
  cursor: pointer;
  transition: 400ms;
  padding: 50px;
  position: relative;
  overflow: hidden;
}

.prep-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom,
    rgba(0, 0, 0, 0.3) 0%,
    rgba(0, 0, 0, 0.6) 100%);
  z-index: 1;
}

.prep-item::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-size: cover;
  background-position: center;
  transition: transform 0.4s ease;
  z-index: 0;
}

.prep-item:nth-child(1)::after {
  background-image: url('/images/school-selection.jpg');
}

.prep-item:nth-child(2)::after {
  background-image: url('/images/exam-prep.jpg');
}

.prep-item:nth-child(3)::after {
  background-image: url('/images/documents.jpg');
}

.prep-item:nth-child(4)::after {
  background-image: url('/images/background.jpg');
}

.item-icon {
  font-size: 56px;
  margin-bottom: 30px;
  color: white;
  position: relative;
  z-index: 2;
}

.item-content {
  position: relative;
  z-index: 2;
}

.item-content h3 {
  font-size: 1.6em;
  font-weight: 700;
  margin-bottom: 24px;
  color: white;
}

.item-content p {
  font-size: 1.1em;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.95);
}

.prep-item:hover {
  transform: scale(1.1, 1.1);
}

.prep-item:hover::after {
  transform: scale(1.2);
}

.preparation-items:hover > .prep-item:not(:hover) {
  filter: blur(10px);
  transform: scale(0.9, 0.9);
}

@media (max-width: 2000px) {
  .preparation-items {
    flex-wrap: wrap;
  }
  
  .prep-item {
    width: calc(50% - 24px);
    min-width: 420px;
  }
}

@media (max-width: 768px) {
  .preparation-items {
    flex-direction: column;
    align-items: center;
    padding: 10px;
  }
  
  .prep-item {
    width: 100%;
    max-width: 460px;
    height: 420px;
    padding: 40px;
  }
}

.consultation-section {
  text-align: center;
  background: #f5f7fa;
  padding: 60px 20px;
  border-radius: 12px;
}

.consultation-section h2 {
  text-align: center;
  margin-left: 0;
}

.consultation-section p {
  color: #666;
  margin-bottom: 30px;
}

.nav-container {
  position: fixed;
  top: 50%;
  transform: translateY(-50%);
  left: 0;
  right: 0;
  z-index: 2000;
  background-color: rgba(251, 251, 253, 0.8);
  backdrop-filter: saturate(180%) blur(20px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.search-overlay {
  position: fixed;
  top: 50%;
  transform: translateY(-50%);
  left: 0;
  right: 0;
  bottom: auto;
  height: calc(100vh - 44px);
  background-color: rgba(0, 0, 0, 0.2);
  z-index: 2001;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.search-dialog {
  margin-top: 44px;
}

.step-card {
  width: 90px;
  height: 90px;
  background: rgb(103, 225, 255);
  transition: all 0.4s;
  border-radius: 10px;
  box-shadow: 0px 0px 10px 5px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
}

.step-card:hover {
  border-radius: 15px;
  cursor: pointer;
  transform: scale(1.15);
  box-shadow: 0px 0px 15px 5px rgba(0, 0, 0, 0.15);
  background: rgb(103, 151, 255);
}

.first-content {
  height: 100%;
  width: 100%;
  transition: all 0.4s;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  opacity: 1;
  text-align: center;
  position: relative;
}

.step-card:hover .first-content {
  height: 0px;
  opacity: 0;
  transform: rotate(10deg) scale(0.8);
}

.second-content {
  height: 0%;
  width: 100%;
  opacity: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  transition: all 0.4s;
  padding: 0;
  text-align: center;
  transform: rotate(90deg) scale(-1);
}

.step-card:hover .second-content {
  opacity: 1;
  height: 100%;
  transform: rotate(0deg) scale(1);
}

.step-icon {
  font-size: 28px;
  color: #fff;
  margin-bottom: 8px;
}

.step-title {
  font-size: 14px;
  font-weight: 700;
  color: #fff;
  line-height: 1.2;
  padding: 0 5px;
  text-align: center;
}

.step-status {
  font-size: 18px;
  color: #fff;
  font-weight: 700;
  line-height: 1.2;
  letter-spacing: 1px;
}

.step-card.active {
  background: #409EFF;
  transform: scale(1.05);
  box-shadow: 0px 0px 20px rgba(64, 158, 255, 0.3);
}

.step-card.finished {
  background: #67C23A;
}

/* 弹窗样式 */
.detail-dialog {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(10px);
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 80px 20px 20px;
  overflow-y: auto;
}

.dialog-content {
  background: #fff;
  border-radius: 20px;
  max-width: 1000px;
  width: 90%;
  height: auto;
  position: relative;
  animation: dialogSlideIn 0.3s ease;
  margin-top: 20px;
}

.dialog-close {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 1;
}

.dialog-close:hover {
  background: rgba(0, 0, 0, 0.2);
}

.dialog-header {
  padding: 40px 40px 20px;
  text-align: left;
}

.dialog-subtitle {
  font-size: 20px;
  color: #86868b;
  margin-bottom: 8px;
}

.dialog-title {
  font-size: 48px;
  color: #1d1d1f;
  margin: 0;
  font-weight: 600;
}

.dialog-body {
  display: flex;
  flex-direction: column;
  gap: 30px;
  padding: 0 40px 40px;
  overflow: visible;
}

.image-container {
  background: #f5f5f7;
  border-radius: 20px;
  padding: 25px;
  margin: 0 auto;
  width: 95%;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .image-container {
    width: 90%;        /* 移动端稍微放宽一点 */
    padding: 16px;
  }
}

.image-description {
  font-size: 14px;
  color: #86868b;
  margin-bottom: 16px;
  text-align: center;
}

.dialog-image {
  width: 70%;
  margin: 0 auto;
  border-radius: 8px;
  overflow: hidden;
}

.dialog-image img {
  width: 100%;
  height: auto;
  display: block;
}

.dialog-text {
  color: #1d1d1f;
}

.dialog-description {
  font-size: 20px;
  line-height: 1.5;
  margin-bottom: 30px;
}

.dialog-details h3 {
  font-size: 24px;
  margin-bottom: 20px;
}

.dialog-details ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.dialog-details li {
  font-size: 18px;
  color: #424245;
  margin-bottom: 15px;
  padding-left: 24px;
  position: relative;
}

.dialog-details li::before {
  content: '•';
  position: absolute;
  left: 0;
  color: #0066cc;
}

@keyframes dialogFadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes dialogSlideIn {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

@media (max-width: 768px) {
  .detail-dialog {
    padding: 60px 0 0;
  }
  
  .dialog-content {
    margin-top: 0;
    border-radius: 20px 20px 0 0;
  }
  
  .dialog-header {
    padding: 30px 20px 15px;
  }
  
  .dialog-title {
    font-size: 32px;
  }
  
  .dialog-body {
    padding: 0 20px 30px;
  }
  
  .dialog-description {
    font-size: 18px;
  }
  
  .dialog-details li {
    font-size: 16px;
  }
  
  .image-container {
    padding: 16px;
  }
  
  .dialog-image {
    width: 85%;
  }
}

.faq-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.faq-item {
  background: #fff;
  border: 1px solid #e5e5e5;
  border-radius: 12px;
  padding: 24px 32px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.faq-item:hover {
  transform: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border-color: #d5d5d5;
}

.faq-item h3 {
  font-size: 18px;
  color: #1d1d1f;
  margin-bottom: 12px;
  font-weight: 500;
  position: relative;
  padding-left: 24px;
}

.faq-item h3::before {
  content: 'Q:';
  position: absolute;
  left: 0;
  color: #0066cc;
  font-weight: 600;
}

.faq-item p {
  font-size: 15px;
  color: #424245;
  line-height: 1.5;
  position: relative;
  padding-left: 24px;
}

.faq-item p::before {
  content: 'A:';
  position: absolute;
  left: 0;
  color: #666;
  font-weight: 600;
}

@media (max-width: 768px) {
  .faq-container {
    padding: 0 16px;
  }
  
  .faq-item {
    padding: 20px 24px;
  }
  
  .faq-item h3 {
    font-size: 16px;
  }
  
  .faq-item p {
    font-size: 14px;
  }
}

.loading {
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style> 