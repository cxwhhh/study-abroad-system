<template>
  <div class="home">
    <!-- 导航栏组件 已经弃用-->
    <nav-bar></nav-bar>
    
     <!-- 轮播图部分：展示主要留学国家和宣传信息 -->
    <div class="carousel-container">
      <el-carousel height="800px" :interval="5000" arrow="hover">
        <el-carousel-item v-for="item in carouselItems" :key="item.id">
          <!-- 轮播图文字内容 -->
          <div class="carousel-content">
            <h2>{{ item.title }}</h2>
            <p>{{ item.description }}</p>
            <el-button type="primary" round>了解更多</el-button>
          </div>
          <!-- 轮播图背景图片 -->
          <img :src="item.imageUrl" :alt="item.title" class="carousel-image">
        </el-carousel-item>
      </el-carousel>
    </div>
    
    <!-- 留学申请流程部分：展示申请步骤 -->
    <div class="process-container">
      <div class="process-steps">
        <!-- 步骤列表 -->
        <div class="steps-wrapper">
          <div class="step" 
               v-for="(step, index) in processSteps" 
               :key="index" 
               @click="showStepGuide(step)">
            <div class="step-box" :data-step="index + 1">
              <el-icon><component :is="step.icon" /></el-icon>
            </div>
            <div class="step-content">
              <h3>{{ step.title }}</h3>
              <div class="timing">{{ step.timing }}</div>
              <div class="description">{{ step.description }}</div>
              <div class="step-actions">
                <el-button type="primary" size="small">
                  了解更多
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 热门留学国家部分：展示主要留学目的地 -->
    <div class="countries-container">
      <h2 class="section-title">热门留学国家</h2>
      <div class="countries-grid">
        <div class="countries-row">
          <div v-for="country in countries" :key="country.id" 
               class="country-card" 
               @click="goToSchools(country.name)">
            <div class="country-image-wrapper">
              <img :src="country.imageUrl" class="country-image">
              <div class="country-overlay">
                <h3>{{ country.name }}</h3>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 服务特色部分：展示公司优势 -->
    <div class="features-container">
      <h2 class="section-title">我们的服务特色</h2>
      <div class="features-grid">
        <div v-for="feature in features" :key="feature.id" class="feature-item">
          <div class="feature-icon">
            <component :is="feature.icon" />
          </div>
          <h3>{{ feature.title }}</h3>
          <p>{{ feature.description }}</p>
        </div>
      </div>
    </div>

    <!-- 新闻资讯部分：展示最新留学相关信息 -->
    <div class="news-container">
      <h2 class="section-title">留学资讯</h2>
      <div class="news-grid">
        <el-card v-for="news in newsItems" :key="news.id" class="news-card" shadow="hover">
          <img :src="news.imageUrl" class="news-image">
          <div class="news-info">
            <div class="news-meta">
              <span class="news-date">{{ news.date }}</span>
              <span class="news-category">{{ news.category }}</span>
            </div>
            <h3>{{ news.title }}</h3>
            <p>{{ news.summary }}</p>
            <el-button type="text" class="read-more">
              阅读更多
              <el-icon class="el-icon--right"><ArrowRight /></el-icon>
            </el-button>
          </div>
        </el-card>
      </div>
      <div class="news-more">
        <el-button type="primary" plain>查看更多资讯</el-button>
      </div>
    </div>

    <!-- 页脚组件 -->
    <footer-bar></footer-bar>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { 
  Message, 
  ArrowRight,
  Guide,
  Document,
  Upload,
  ChatDotRound,
  Check,
  Service,
  Money,
  Star,
  Trophy,
  Timer
} from '@element-plus/icons-vue'
import NavBar from '../components/NavBar.vue'
import FooterBar from '../components/FooterBar.vue'
import { useRouter } from 'vue-router'

// 轮播图数据
const carouselItems = ref([
  {
    id: 1,
    imageUrl: '/images/carousel-1.jpg',
    title: '美国留学',
    description: '开启你的美国梦，体验世界顶尖教育'
  },
  {
    id: 2,
    imageUrl: '/images/carousel-2.jpg',
    title: '英国留学',
    description: '探索百年学府，感受英伦文化魅力'
  },
  {
    id: 3,
    imageUrl: '/images/carousel-3.jpg',
    title: '澳洲留学',
    description: '优质教育体系，舒适学习环境'
  }
])

// 申请流程步骤数据
const processSteps = ref([
  {
    icon: 'Guide',
    title: '前期准备',
    description: '确定目标和准备考试',
    timing: '申请前12-18个月'
  },
  {
    icon: 'Document',
    title: '材料准备',
    description: '准备申请材料',
    timing: '申请前6-12个月'
  },
  {
    icon: 'Upload',
    title: '申请提交',
    description: '提交院校申请',
    timing: '申请季'
  },
  {
    icon: 'ChatDotRound',
    title: '面试录取',
    description: '准备面试和确认录取',
    timing: '申请后2-3个月'
  },
  {
    icon: 'Check',
    title: '签证准备',
    description: '签证行前准备',
    timing: '入学前2-3个月'
  },
  {
    icon: 'Service',
    title: '学校助手',
    description: '留学生活指导',
    timing: '留学期间'
  },
  {
    icon: 'Money',
    title: '入学返点',
    description: '入学奖励计划',
    timing: '入学后'
  }
])

// 国家数据
const countries = ref([
  {
    id: 1,
    name: '英国',
    imageUrl: '/images/uk.jpg'
  },
  {
    id: 2,
    name: '美国',
    imageUrl: '/images/usa.jpg'
  },
  {
    id: 3,
    name: '澳大利亚',
    imageUrl: '/images/australia.jpg'
  },
  {
    id: 4,
    name: '加拿大',
    imageUrl: '/images/canada.jpg'
  }
])

// 服务特色数据
const features = ref([
  {
    id: 1,
    icon: Star,
    title: '专业顾问团队',
    description: '经验丰富的留学顾问为您提供一对一咨询服务'
  },
  {
    id: 2,
    icon: Trophy,
    title: '高录取率',
    description: '优秀的申请方案，确保较高的名校录取率'
  },
  {
    id: 3,
    icon: Timer,
    title: '高效服务',
    description: '快速响应，高效处理申请事务'
  },
  {
    id: 4,
    icon: Service,
    title: '全程指导',
    description: '从选校到入学，提供全方位指导服务'
  }
])

// 新闻资讯数据
const newsItems = ref([
  {
    id: 1,
    imageUrl: '/images/news-1.jpg',
    date: '2024-01-15',
    category: '留学资讯',
    title: '2024年美国大学申请变化解析',
    summary: '最新的申请政策变化及应对策略分析'
  },
  {
    id: 2,
    imageUrl: '/images/news-2.jpg',
    date: '2024-01-10',
    category: '留学指南',
    title: '如何准备一份出色的留学文书',
    summary: '专业文书写作技巧及案例分析'
  },
  {
    id: 3,
    imageUrl: '/images/news-3.jpg',
    date: '2024-01-05',
    category: '留学生活',
    title: '海外生活适应指南',
    summary: '帮助新生快速适应海外学习生活'
  }
])

const router = useRouter()

// 页面跳转方法
const showStepGuide = (step) => {
  const stepIndex = processSteps.value.findIndex(s => s.title === step.title)
  if (stepIndex >= 0) {
    const paths = ['preparation', 'materials', 'submission', 'interview', 'visa', 'assistant', 'rebate']
    router.push(`/steps/${paths[stepIndex]}`)
  }
}

const goToSchools = (country) => {
  router.push(`/schools/${country.toLowerCase()}`)
}

// 添加 learnMore 方法
const learnMore = (item) => {
  router.push(`/countries/${item.title.replace('留学', '').toLowerCase()}`)
}
</script>

<style scoped>
.home {
  min-height: 100vh;
}

/* 轮播图样式 */
.carousel-container {
  position: relative;
  overflow: hidden;
}

.carousel-container :deep(.el-carousel__container) {
  position: relative;
}

.carousel-container :deep(.el-carousel__item) {
  overflow: hidden;
}

.carousel-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transform: scale(1.2);
  transition: transform 6s ease-in-out;
}

.el-carousel__item.is-active .carousel-image {
  transform: scale(1);
}

.carousel-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1;
  width: 100%;
  max-width: 1200px;
  padding: 0 40px;
  color: #fff;
  text-align: center;
  opacity: 0;
  transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1) 0.3s;
}

.el-carousel__item.is-active .carousel-content {
  opacity: 1;
}

.carousel-content h2 {
  font-size: 48px;
  font-weight: 600;
  margin-bottom: 20px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.carousel-content p {
  font-size: 24px;
  margin: 0 auto 30px;
  max-width: 800px;
  line-height: 1.6;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.carousel-content .el-button {
  padding: 12px 30px;
  font-size: 16px;
  transition: all 0.3s ease;
}

.carousel-content .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.carousel-container :deep(.el-carousel__arrow) {
  background-color: rgba(255, 255, 255, 0.8);
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  width: 44px;
  height: 44px;
  top: 50%;
  transform: none;
  transition: transform 0.3s ease;
}

.carousel-container :deep(.el-carousel__arrow:hover) {
  background-color: rgba(255, 255, 255, 0.95);
  transform: scale(1.1);
}

.carousel-container :deep(.el-carousel__arrow i) {
  color: #333;
}

.carousel-container :deep(.el-carousel__button) {
  width: 30px;
  height: 3px;
  border-radius: 2px;
  background-color: rgba(255, 255, 255, 0.4);
  transition: all 0.3s ease;
}

.carousel-container :deep(.el-carousel__indicator.is-active .el-carousel__button) {
  background-color: #fff;
}

/* 申请流程样式 */
.process-container {
  background: #fff;
  padding: 0;
  margin: 0;
}

.process-steps {
  padding: 60px 0;
  max-width: 1400px;
  margin: 0 auto;
}

.steps-wrapper {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 30px;
  padding: 0 40px;
}

.step {
  flex: 0 0 160px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.step:hover {
  transform: translateY(-5px);
}

.step:hover .step-actions {
  opacity: 1;
  transform: translateY(0);
}

.step-box {
  width: 90px;
  height: 90px;
  margin: 0 auto 20px;
  background: #7abaf5;
  border-radius: 25px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  color: white;
  box-shadow: 0 4px 12px #2880ea4c;
  position: relative;
}

.step-box::after {
  content: attr(data-step);
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 36px;
  color: white;
}

.step-content h3 {
  font-size: 18px;
  color: #333;
  margin: 0 0 10px;
}

.timing {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.description {
  font-size: 14px;
  color: #666;
  margin-bottom: 15px;
}

.step-actions {
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.3s ease;
}

/* 通用标题样式 */
.section-title {
  text-align: center;
  font-size: 36px;
  color: #333;
  margin-bottom: 40px;
  position: relative;
  padding-bottom: 16px;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background: #409EFF;
  border-radius: 2px;
}

/* 国家展示样式 */
.countries-container {
  padding: 10px 20px;
  background: hsla(230, 23%, 95%, 0.637);
}

.countries-grid {
  max-width: 1200px;
  margin: 0 auto;
}

.countries-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30px;
  padding: 0 20px;
}

.country-card {
  position: relative;
  cursor: pointer;
  transition: transform 0.3s ease;
  border-radius: 12px;
  overflow: hidden;
  aspect-ratio: 16/9;
}

.country-image-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.country-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.country-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 30px;
  background: linear-gradient(to top, rgba(0,0,0,0.7), transparent);
  color: white;
  transform: translateY(0);
  transition: transform 0.3s ease;
}

.country-overlay h3 {
  margin: 0;
  font-size: 24px;
  font-weight: 500;
}

.country-card:hover .country-image {
  transform: scale(1.05);
}

/* 服务特色样式 */
.features-container {
  padding: 80px 20px;
  background: white;
}

.features-grid {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 30px;
}

.feature-item {
  text-align: center;
  padding: 30px;
}

.feature-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  background: #409EFF;
  color: white;
  border-radius: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
}

.feature-item h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 15px;
}

.feature-item p {
  color: #666;
  line-height: 1.6;
}

/* 新闻资讯样式 */
.news-container {
  padding: 80px 20px;
  background: #f5f7fa;
}

.news-grid {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 30px;
}

.news-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.news-card:hover {
  transform: translateY(-5px);
}

.news-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.news-info {
  padding: 20px;
}

.news-meta {
  display: flex;
  justify-content: space-between;
  color: #999;
  font-size: 14px;
  margin-bottom: 10px;
}

.news-info h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 10px;
}

.news-info p {
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
}

.news-more {
  text-align: center;
  margin-top: 40px;
}

@media (max-width: 768px) {
  .carousel-content h2 {
    font-size: 36px;
  }
  
  .carousel-content p {
    font-size: 18px;
  }
  
  .section-title {
    font-size: 28px;
  }
  
  .step {
    flex: 0 0 140px;
  }
  
  .step-box {
    width: 70px;
    height: 70px;
    font-size: 28px;
  }
  
  .step-box::after {
    font-size: 28px;
  }
  
  .step-actions {
    opacity: 1;
    transform: none;
  }
  
  .countries-row {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 0 10px;
  }
  
  .country-overlay h3 {
    font-size: 20px;
  }
}

/* 修改轮播图容器大小 */
.el-carousel {
  width: 100%;
  height: 700px;  /* 你可以调整这个高度值 */
}

/* 修改轮播图项的大小 */
.el-carousel__item {
  width: 100%;
  height: 100%;
}

/* 修改轮播图图片的大小和展示方式 */
.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;  /* 保持图片比例并填充容器 */
}
</style> 