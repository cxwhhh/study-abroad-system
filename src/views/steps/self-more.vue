<template>
  <div class="self-more">
    <nav-bar />

    <!-- 主视觉区域 -->
    <section class="hero-section">
      <!-- 添加背景视频 -->
      <div class="video-background">
        <video autoplay loop muted playsinline>
          <source src="/videos/hero-bg.mp4" type="video/mp4">
        </video>
        <!-- 添加遮罩层，确保文字清晰可见 -->
        <div class="video-overlay"></div>
      </div>

      <div class="hero-content">
        <h1>自助申请指南</h1>
        <h2>掌控申请全程，开启留学之路</h2>
      </div>
    </section>

    <!-- 四步流程展示 -->
    <section class="process-section">
      <div class="section-header">
        <h2>四步完成申请</h2>
        <p>专业团队全程指导，让申请更简单</p>
      </div>
      <div class="process-wrapper">
        <div class="process-scroll" ref="scrollContainer">
          <div class="process-container">
            <div class="process-item" v-for="(step, index) in steps" :key="index">
              <div class="item-content">
                <div class="item-image">
                  <img :src="step.image" :alt="step.title">
                </div>
                <div class="item-text">
                  <span class="step-number">{{ index + 1 < 10 ? '0' + (index + 1) : index + 1 }}</span>
                      <h3>{{ step.title }}</h3>
                      <p>{{ step.description }}</p>
                      <ul class="detail-list">
                        <li v-for="(detail, idx) in step.details" :key="idx">
                          <el-icon>
                            <Check />
                          </el-icon>
                          {{ detail }}
                        </li>
                      </ul>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- 将控制区域移到滚动容器外部 -->
        <div class="controls-wrapper">
          <button class="control-btn prev" @click="scrollTo('prev')" :disabled="currentIndex === 0">
            <el-icon>
              <ArrowLeft />
            </el-icon>
          </button>
          <div class="scroll-indicators">
            <span v-for="(_, index) in steps" :key="index"
              :class="['indicator-dot', { active: currentIndex === index }]" @click="scrollToIndex(index)">
            </span>
          </div>
          <button class="control-btn next" @click="scrollTo('next')" :disabled="currentIndex === steps.length - 1">
            <el-icon>
              <ArrowRight />
            </el-icon>
          </button>
        </div>
      </div>
    </section>

    <!-- 特色介绍区域 -->
    <section class="feature-section">
      <div class="section-header">
        <h2>为什么选择自助申请？</h2>
        <p>相比传统中介，自助申请让你更好地把控申请进度</p>
      </div>
      <div class="feature-content">
        <div class="feature-grid">
          <div class="feature-item">
            <div class="feature-icon">
              <el-icon>
                <Timer />
              </el-icon>
            </div>
            <h3>掌控全程</h3>
            <p class="feature-desc">亲自参与每个环节，了解申请细节，提升申请质量</p>
            <ul class="feature-details">
              <li>实时了解申请进度</li>
              <li>随时调整申请策略</li>
              <li>主动把控申请方向</li>
              <li>专属进度追踪系统</li>
            </ul>
          </div>

          <div class="feature-item">
            <div class="feature-icon">
              <el-icon>
                <Money />
              </el-icon>
            </div>
            <h3>成本透明</h3>
            <p class="feature-desc">告别高额中介费，学校奖励全额返还，费用更经济</p>
            <ul class="feature-details">
              <li>服务费用明确公示</li>
              <li>奖励政策透明公开</li>
              <li>无隐藏收费项目</li>
              <li>费用节省40%-60%</li>
            </ul>
          </div>

          <div class="feature-item">
            <div class="feature-icon">
              <el-icon>
                <Document />
              </el-icon>
            </div>
            <h3>材料真实</h3>
            <p class="feature-desc">确保申请材料真实可靠，提升录取成功率</p>
            <ul class="feature-details">
              <li>材料真实性保证</li>
              <li>专业文书指导</li>
              <li>合规性严格把关</li>
              <li>多轮审核把关</li>
            </ul>
          </div>

          <div class="feature-item">
            <div class="feature-icon">
              <el-icon>
                <Service />
              </el-icon>
            </div>
            <h3>专业指导</h3>
            <p class="feature-desc">经验丰富的顾问团队，为你提供专业建议</p>
            <ul class="feature-details">
              <li>一对一咨询服务</li>
              <li>定制申请方案</li>
              <li>全程专家指导</li>
              <li>海外名校经验</li>
            </ul>
          </div>
        </div>
      </div>
    </section>

    <!-- 自主申请数据与优势对比区域 -->
    <section class="data-comparison-section">
      <div class="section-header">
        <h2>助申请数据一览</h2>
        <p>数据说明一切，了解助申请的真实优势</p>
      </div>

      <div class="data-cards-container">
        <div class="data-card">
          <div class="data-icon">
            <el-icon>
              <Timer />
            </el-icon>
          </div>
          <div class="data-number">40%</div>
          <div class="data-title">时间节省</div>
          <div class="data-description">相比传统申请，助申请流程更高效，平均节省40%申请时间</div>
        </div>

        <div class="data-card">
          <div class="data-icon">
            <el-icon>
              <Money />
            </el-icon>
          </div>
          <div class="data-number">60%</div>
          <div class="data-title">费用节省</div>
          <div class="data-description">自主申请服务费用更透明，平均为传统中介的40%</div>
        </div>

        <div class="data-card">
          <div class="data-icon">
            <el-icon>
              <PieChart />
            </el-icon>
          </div>
          <div class="data-number">85%</div>
          <div class="data-title">录取率</div>
          <div class="data-description">我们的助申请服务录取率高达85%，远超行业平均水平</div>
        </div>

        <div class="data-card">
          <div class="data-icon">
            <el-icon>
              <User />
            </el-icon>
          </div>
          <div class="data-number">98%</div>
          <div class="data-title">学生满意度</div>
          <div class="data-description">98%的学生对我们的自主申请服务表示满意或非常满意</div>
        </div>
      </div>

      <div class="comparison-table-container">
        <h3 class="comparison-title">自助申请 vs 传统申请</h3>
        <div class="comparison-table">
          <div class="comparison-header">
            <div class="comparison-cell header-cell">对比项目</div>
            <div class="comparison-cell header-cell">助申请</div>
            <div class="comparison-cell header-cell">传统申请</div>
          </div>

          <div class="comparison-row">
            <div class="comparison-cell">申请透明度</div>
            <div class="comparison-cell highlight">全程透明，实时追踪</div>
            <div class="comparison-cell">流程不透明，难以追踪</div>
          </div>

          <div class="comparison-row">
            <div class="comparison-cell">个性化程度</div>
            <div class="comparison-cell highlight">完全根据学生需求定制</div>
            <div class="comparison-cell">标准化流程，缺乏个性化</div>
          </div>

          <div class="comparison-row">
            <div class="comparison-cell">学校奖励</div>
            <div class="comparison-cell highlight">100%返还给学生</div>
            <div class="comparison-cell">中介留存大部分</div>
          </div>

          <div class="comparison-row">
            <div class="comparison-cell">申请主导权</div>
            <div class="comparison-cell highlight">学生全程参与，掌握主导</div>
            <div class="comparison-cell">中介主导，学生被动接受</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 申请案例展示区域 -->
    <section class="cases-section">
      <div class="section-header">
        <h2>成功案例</h2>
        <p>来看看其他学生的申请历程</p>
      </div>
      <div class="cases-container">
        <div class="case-card">
          <div class="case-header">
            <div class="school-logo">
              <img src="/images/ucl-logo.png" alt="UCL">
            </div>
            <div class="school-info">
              <h3>伦敦大学学院</h3>
              <p>计算机科学 MSc</p>
            </div>
          </div>
          <div class="case-content">
            <div class="student-profile">
              <span class="tag">本科背景：清华大学</span>
              <span class="tag">GPA：3.7/4.0</span>
              <span class="tag">雅思：7.5</span>
            </div>
            <p class="case-desc">从选校定位到最终录取，整个过程仅用时3个月。申请人在我们的指导下，合理规划时间，快速高效地完成了所有申请材料。</p>
          </div>
        </div>

        <div class="case-card">
          <div class="case-header">
            <div class="school-logo">
              <img src="/images/columbia-logo.png" alt="Columbia">
            </div>
            <div class="school-info">
              <h3>哥伦比亚大学</h3>
              <p>金融工程 MS</p>
            </div>
          </div>
          <div class="case-content">
            <div class="student-profile">
              <span class="tag">本科背景：上海交大</span>
              <span class="tag">GPA：3.8/4.0</span>
              <span class="tag">托福：105</span>
            </div>
            <p class="case-desc">申请人通过自主申请，获得了额外的奖学金机会。在材料准备阶段，我们的专业团队提供了针对性的建议。</p>
          </div>
        </div>

        <div class="case-card">
          <div class="case-header">
            <div class="school-logo">
              <img src="/images/imperial-logo.png" alt="Imperial">
            </div>
            <div class="school-info">
              <h3>帝国理工学院</h3>
              <p>机械工程 MSc</p>
            </div>
          </div>
          <div class="case-content">
            <div class="student-profile">
              <span class="tag">本科背景：浙江大学</span>
              <span class="tag">GPA：3.6/4.0</span>
              <span class="tag">雅思：7.0</span>
            </div>
            <p class="case-desc">申请人成功获得了心仪专业的录取，整个申请过程透明且高效。我们的团队全程提供专业的指导和支持。</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 学生反馈区域 -->
    <section class="testimonials-section">
      <div class="section-header">
        <h2>学生反馈</h2>
        <p>听听他们怎么说</p>
      </div>
      <div class="testimonials-container">
        <div class="testimonial-card">
          <div class="quote-icon">
            <el-icon>
              <ChatRound />
            </el-icon>
          </div>
          <p class="testimonial-text">"自助申请让我对整个申请过程有了更清晰的认识，也让我学会了如何更好地展示自己。顾问团队的专业建议给了我很大帮助。"</p>
          <div class="testimonial-author">
            <span class="author-name">张同学</span>
            <span class="author-school">UCL 计算机科学</span>
          </div>
        </div>

        <div class="testimonial-card">
          <div class="quote-icon">
            <el-icon>
              <ChatRound />
            </el-icon>
          </div>
          <p class="testimonial-text">"比传统中介更透明的流程和更实惠的价格是我选择自助申请的主要原因。整个过程我都参与其中，收获很大。"</p>
          <div class="testimonial-author">
            <span class="author-name">李同学</span>
            <span class="author-school">哥伦比亚大学 金融工程</span>
          </div>
        </div>

        <div class="testimonial-card">
          <div class="quote-icon">
            <el-icon>
              <ChatRound />
            </el-icon>
          </div>
          <p class="testimonial-text">"从开始准备到最终录取，每一步都得到了专业的指导。最重要的是，这个过程让我对自己更有信心了。"</p>
          <div class="testimonial-author">
            <span class="author-name">王同学</span>
            <span class="author-school">帝国理工 机械工程</span>
          </div>
        </div>
      </div>
    </section>


    <footer-bar></footer-bar>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Check, ArrowLeft, ArrowRight, VideoPause, VideoPlay, Timer, Money, PieChart, User, Document, Service, ChatRound } from '@element-plus/icons-vue'
import NavBar from '../../components/NavBar.vue'
import FooterBar from '../../components/FooterBar.vue'

const router = useRouter()

const steps = ref([
  {
    title: '前期规划',
    description: '科学规划申请时间，合理安排各项准备工作',
    details: ['选校定位分析', '时间规划制定', '背景提升建议', '语言考试规划'],
    image: '/images/preparation.jpg'
  },
  {
    title: '材料准备',
    description: '专业指导准备各类申请材料，确保材料质量',
    details: ['文书写作指导', '推荐信准备', '成绩单公证', '作品集辅导'],
    image: '/images/materials.jpg'
  },
  {
    title: '递交申请',
    description: '专业团队指导申请递交，把控申请细节',
    details: ['网申系统指导', '材料上传核查', '申请费支付', '追踪申请状态'],
    image: '/images/submission.jpg'
  },
  {
    title: '录取服务',
    description: '全程跟进申请进度，提供录取相关咨询',
    details: ['Offer评估', '奖学金申请', '住宿安排', '签证指导'],
    image: '/images/admission.jpg'
  }
])


const scrollContainer = ref(null)
const currentIndex = ref(0)
let autoScrollInterval = null

// 修改滚动到指定方向的方法
const scrollTo = (direction) => {
  if (!scrollContainer.value) return

  // 如果已经到达边界，则不再滚动
  if (direction === 'next' && currentIndex.value >= steps.value.length - 1) {
    return
  }
  if (direction === 'prev' && currentIndex.value <= 0) {
    return
  }
  // 更新当前索引
  if (direction === 'next') {
    currentIndex.value++
  } else {
    currentIndex.value--
  }

  const container = scrollContainer.value
  const itemWidth = container.querySelector('.process-item').offsetWidth
  const scrollAmount = direction === 'next' ? itemWidth : -itemWidth

  container.scrollBy({
    left: scrollAmount,
    behavior: 'smooth'
  })

}

// 修改滚动到指定索引的方法
const scrollToIndex = (index) => {
  const container = scrollContainer.value
  if (container) {
    const items = container.querySelectorAll('.process-item')
    if (items[index]) {
      const scrollLeft = items[index].offsetLeft - (container.offsetWidth - items[index].offsetWidth) / 2
      container.scrollTo({
        left: scrollLeft,
        behavior: 'smooth'
      })
      currentIndex.value = index
    }
  }
}

// 修改滚动事件处理
const handleScroll = () => {
  if (!scrollContainer.value) return

  const container = scrollContainer.value
  const items = container.querySelectorAll('.process-item')
  const scrollLeft = container.scrollLeft
  const containerWidth = container.offsetWidth

  let minDistance = Infinity
  let nearestIndex = 0

  items.forEach((item, index) => {
    const itemCenter = item.offsetLeft + item.offsetWidth / 2
    const scrollCenter = scrollLeft + containerWidth / 2
    const distance = Math.abs(itemCenter - scrollCenter)

    if (distance < minDistance) {
      minDistance = distance
      nearestIndex = index
    }
  })

  // 限制索引范围
  nearestIndex = Math.max(0, Math.min(nearestIndex, steps.value.length - 1))
  currentIndex.value = nearestIndex
}

// 修改自动滚动逻辑
const startAutoScroll = () => {
  autoScrollInterval = setInterval(() => {
    // 如果到达最后一张，则回到第一张
    if (currentIndex.value >= steps.value.length - 1) {
      currentIndex.value = 0
      scrollToIndex(0)
    } else {
      scrollTo('next')
    }
  }, 20000)
}

onMounted(() => {
  if (scrollContainer.value) {
    scrollContainer.value.addEventListener('scroll', handleScroll)
    startAutoScroll()
  }
})

onUnmounted(() => {
  if (scrollContainer.value) {
    scrollContainer.value.removeEventListener('scroll', handleScroll)
  }
  if (autoScrollInterval) {
    clearInterval(autoScrollInterval)
  }
})
</script>

<style scoped>
/* 修改整体背景色 */
.self-more {
  background: #fff;
  color: #1d1d1f;
}

/* 主视觉区域 */
.hero-section {
  min-height: 80vh;
  /* 增加视频区域高度 */
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 0;
  /* 移除内边距让视频充满 */
  position: relative;
  overflow: hidden;
}

/* 视频背景容器 */
.video-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

/* 视频样式 */
.video-background video {
  width: 100%;
  height: 100%;
  object-fit: cover;
  position: absolute;
  top: 0;
  left: 0;
}

/* 视频遮罩层 - 完全移除遮罩效果 */
.video-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.1);
  /* 只保留极轻微的暗化效果，确保文字可读 */
  backdrop-filter: none;
  -webkit-backdrop-filter: none;
}

/* 内容样式调整 */
.hero-content {
  position: relative;
  z-index: 1;
  padding: 0 20px;
}

/* 调整文字样式以适应无遮罩的视频背景 */
.hero-content h1 {
  color: #fff;
  font-size: 72px;
  font-weight: 700;
  margin-bottom: 24px;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.5);
  /* 增强文字阴影 */
  letter-spacing: -0.02em;
}

.hero-content h2 {
  color: rgba(255, 255, 255, 0.95);
  font-size: 32px;
  font-weight: 500;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
  /* 增强文字阴影 */
  letter-spacing: -0.01em;
}

/* 流程展示区域保持浅灰背景 */
.process-section {
  padding: 60px 0;
  /* 减小上下内边距 */
  background: #f5f5f7;
  color: #000;
  overflow: hidden;
}

.section-header {
  text-align: center;
  max-width: 800px;
  margin: 0 auto 40px;
  /* 减小下边距 */
  padding: 0 20px;
}

.section-header h2 {
  font-size: 42px;
  /* 减小标题字体从56px改为42px */
  margin-bottom: 12px;
  /* 减小下边距从16px改为12px */
  font-weight: 700;
}

.section-header p {
  font-size: 20px;
  /* 减小描述文字从28px改为20px */
  color: #86868b;
}

.process-wrapper {
  position: relative;
  width: 100%;
  margin: 0 auto;
  overflow: hidden;
}

.process-scroll {
  width: 100%;
  overflow-x: auto;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
  -ms-overflow-style: none;
  padding: 20px 0;
  max-width: 100vw;
  scroll-snap-type: x mandatory;
}

.process-container {
  display: flex;
  gap: 40px;
  width: max-content;
  /* 修改为max-content确保容器宽度适应内容 */
  padding: 0 calc((100vw - 1200px) / 2);
  /* 使用calc计算padding */
}

.process-item {
  flex: 0 0 min(1200px, calc(100vw - 80px));
  background: #fff;
  border-radius: 30px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease;
  scroll-snap-align: center;
}

/* 移除原来的margin设置 */
.process-item:first-child,
.process-item:last-child {
  margin: 0;
}

.item-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  padding: 40px;
  height: 100%;
  align-items: start;
  background: #fff;
}

.item-text {
  display: flex;
  flex-direction: column;
  gap: 20px;
  position: relative;
  padding-top: 80px;
  /* Make room for the step number */
}

.step-number {
  position: absolute;
  top: 0;
  left: 0;
  color: #0066cc;
  font-size: 48px;
  font-weight: 700;
  /* 重置所有样式 */
  all: unset;
  /* 仅保留必要的样式 */
  position: absolute;
  top: 0;
  left: 0;
  color: #0066cc;
  font-size: 48px;
  font-weight: 700;
  /* 确保没有背景 */
  background: none !important;
  /* 移除所有边框和轮廓 */
  border: 0 !important;
  outline: 0 !important;
  /* 移除所有圆角 */
  border-radius: 0 !important;
  /* 移除所有阴影 */
  box-shadow: none !important;
  /* 移除所有变换和过渡 */
  transform: none !important;
  transition: none !important;
}

/* 移除所有伪元素 */
.step-number::before,
.step-number::after {
  display: none !important;
  content: none !important;
  background: none !important;
  border: none !important;
  box-shadow: none !important;
  border-radius: 0 !important;
}

.item-image {
  position: relative;
  overflow: hidden;
  border-radius: 20px;
  height: 400px;
  background: #f5f5f7;
  /* 添加占位背景色 */
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.item-text h3 {
  font-size: 32px;
  font-weight: 600;
  margin: 0;
  line-height: 1.2;
}

.item-text p {
  font-size: 18px;
  color: #666;
  line-height: 1.6;
  margin: 0;
  margin-bottom: 20px;
}

.detail-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-list li {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #666;
  font-size: 16px;
  line-height: 1.4;
}

.detail-list .el-icon {
  color: #0066cc;
  font-size: 20px;
  flex-shrink: 0;
}

/* 特色介绍区域样式 */
.feature-section {
  padding: 60px 0;
  /* 减小上下内边距从100px改为60px */
  background: linear-gradient(to bottom, #fff 0%, #f8f9ff 100%);
}

.feature-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  /* 减小间距从30px改为20px */
  margin-top: 40px;
  /* 减小上边距从60px改为40px */
}

.feature-item {
  text-align: center;
  padding: 30px 20px;
  /* 减小内边距从40px 30px改为30px 20px */
  background: #fff;
  border-radius: 24px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.feature-item:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(0, 102, 204, 0.1);
}

.feature-icon {
  width: 70px;
  /* 减小图标容器从88px改为70px */
  height: 70px;
  margin: 0 auto 20px;
  /* 减小下边距从28px改为20px */
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 18px;
  /* 相应调整圆角从22px改为18px */
  background: linear-gradient(135deg, #f0f4ff 0%, #e6ebff 100%);
  transition: all 0.3s ease;
}

.feature-item:hover .feature-icon {
  background: linear-gradient(135deg, #0066cc 0%, #0052a3 100%);
}

.feature-icon .el-icon {
  font-size: 28px;
  /* 减小图标字体从36px改为28px */
  color: #0066cc;
  transition: all 0.3s ease;
}

.feature-item:hover .feature-icon .el-icon {
  color: #fff;
}

.feature-item h3 {
  font-size: 20px;
  /* 减小标题字体从24px改为20px */
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 12px;
  /* 减小下边距从16px改为12px */
}

.feature-desc {
  font-size: 14px;
  /* 减小描述字体从16px改为14px */
  line-height: 1.6;
  color: #666;
  margin-bottom: 18px;
  /* 减小下边距从24px改为18px */
}

.feature-details {
  text-align: left;
  list-style: none;
  padding: 0;
  margin: 0;
}

.feature-details li {
  font-size: 13px;
  /* 减小详细列表字体从15px改为13px */
  color: #666;
  margin-bottom: 8px;
  /* 减小下边距从12px改为8px */
  padding-left: 20px;
  /* 减小左内边距从24px改为20px */
  position: relative;
}

.feature-details li::before {
  content: '';
  position: absolute;
  left: 0;
  top: 8px;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #0066cc;
}

/* 响应式调整 */
@media (max-width: 1024px) {
  .feature-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 24px;
  }
}

@media (max-width: 768px) {
  .feature-section {
    padding: 60px 0;
  }

  .feature-grid {
    grid-template-columns: 1fr;
    gap: 20px;
    margin-top: 40px;
  }

  .feature-item {
    padding: 30px 24px;
  }

  .feature-icon {
    width: 72px;
    height: 72px;
    margin-bottom: 20px;
  }

  .feature-icon .el-icon {
    font-size: 32px;
  }

  .feature-item h3 {
    font-size: 20px;
    margin-bottom: 12px;
  }

  .feature-desc {
    font-size: 15px;
    margin-bottom: 20px;
  }
}



/* 控制区域样式 */
.controls-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 20px;
}

.control-btn {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.05);
  border: none;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.control-btn:hover {
  background: rgba(0, 0, 0, 0.1);
  color: #333;
}

/* 指示器样式 */
.scroll-indicators {
  display: flex;
  gap: 10px;
  align-items: center;
}

.indicator-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #ccc;
  cursor: pointer;
  transition: all 0.3s ease;
}

.indicator-dot.active {
  background: #0066cc;
  transform: scale(1.2);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .process-item {
    flex: 0 0 calc(100vw - 80px);
  }

  .process-item:first-child {
    margin-left: 40px;
  }

  .process-item:last-child {
    margin-right: 40px;
  }

  .feature-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 30px;
  }
}

@media (max-width: 1024px) {
  .process-item {
    flex: 0 0 calc(100vw - 80px);
  }

  .process-item:first-child {
    margin-left: 40px;
  }

  .process-item:last-child {
    margin-right: 40px;
  }

  .feature-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 30px;
  }
}

@media (max-width: 768px) {

  /* Update step number styling */
  .step-number {
    font-size: 32px;
    width: 50px;
    height: 50px;
  }

  .process-container {
    padding: 0 20px;
    gap: 20px;
  }

  .process-item {
    flex: 0 0 calc(100vw - 40px);
  }

  .item-content {
    padding: 30px;
    gap: 30px;
  }

  .item-text {
    gap: 15px;
    padding-top: 15px;
  }

  .step-number {
    font-size: 32px;
  }

  .item-text h3 {
    font-size: 28px;
  }

  .item-text p {
    font-size: 16px;
  }

  .detail-list {
    gap: 12px;
  }

  .detail-list li {
    font-size: 14px;
  }

  .feature-section {
    padding: 80px 0;
  }

  .section-header h2 {
    font-size: 40px;
  }

  .section-header p {
    font-size: 20px;
  }

  .feature-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .feature-item {
    padding: 30px 20px;
  }

  .feature-icon {
    width: 72px;
    height: 72px;
    margin-bottom: 20px;
  }

  .feature-icon .el-icon {
    font-size: 32px;
  }

  .feature-item h3 {
    font-size: 20px;
    margin-bottom: 12px;
  }

  .feature-desc {
    font-size: 14px;
    margin-bottom: 20px;
  }

  .cta-content h2 {
    font-size: 40px;
  }

  .cta-content p {
    font-size: 20px;
  }
}

/* 当到达最后一张时禁用下一步按钮 */
.control-btn.next[disabled] {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 当到达第一张时禁用上一步按钮 */
.control-btn.prev[disabled] {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 数据与对比区域样式 */
.data-comparison-section {
  padding: 10px 0;
  background: #f9f9fb;
  color: #1d1d1f;
}

.data-cards-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 30px;
  max-width: 1200px;
  margin: 0 auto 60px;
  padding: 0 20px;
}

.data-card {
  background: #fff;
  border-radius: 20px;
  padding: 30px;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.data-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.1);
}

.data-icon {
  margin-bottom: 20px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.data-icon .el-icon {
  font-size: 40px;
  color: #0066cc;
}

.data-number {
  font-size: 48px;
  font-weight: 700;
  color: #0066cc;
  margin-bottom: 10px;
  line-height: 1;
}

.data-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 15px;
  color: #1d1d1f;
}

.data-description {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.comparison-table-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.comparison-title {
  text-align: center;
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 30px;
  color: #1d1d1f;
}

.comparison-table {
  background: #fff;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
}

.comparison-header {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  background: #0066cc;
  color: #fff;
}

.comparison-row {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  border-bottom: 1px solid #eee;
}

.comparison-row:last-child {
  border-bottom: none;
}

.comparison-cell {
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  font-size: 16px;
}

.header-cell {
  font-weight: 600;
  font-size: 18px;
}

.highlight {
  color: #0066cc;
  font-weight: 500;
}

/* 响应式调整 */
@media (max-width: 1024px) {
  .data-cards-container {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .data-cards-container {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .comparison-cell {
    padding: 15px 10px;
    font-size: 14px;
  }

  .header-cell {
    font-size: 16px;
  }

  .data-comparison-section {
    padding: 60px 0;
  }

  .comparison-title {
    font-size: 24px;
  }
}

/* 申请案例展示区域 */
.cases-section {
  padding: 10px 0;
  background: #fff;
}

.cases-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
  margin-top: 60px;
}

.case-card {
  background: #fff;
  border-radius: 24px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.case-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 40px rgba(0, 102, 204, 0.1);
}

.case-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
}

.school-logo {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  overflow: hidden;
  background: #f5f7ff;
}

.school-logo img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.school-info h3 {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 4px;
  color: #1d1d1f;
}

.school-info p {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.student-profile {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.tag {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 100px;
  background: #f0f4ff;
  color: #0066cc;
}

.case-desc {
  font-size: 14px;
  line-height: 1.6;
  color: #666;
  margin: 0;
}

/* 学生反馈区域 */
.testimonials-section {
  padding: 10px 0;
  background: linear-gradient(to bottom, #f8f9ff 0%, #fff 100%);
}

.testimonials-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
  margin-top: 60px;
}

.testimonial-card {
  background: #fff;
  border-radius: 24px;
  padding: 40px 30px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.testimonial-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 40px rgba(0, 102, 204, 0.1);
}

.quote-icon {
  width: 48px;
  height: 48px;
  margin: 0 auto 24px;
  border-radius: 50%;
  background: linear-gradient(135deg, #0066cc 0%, #0052a3 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.quote-icon .el-icon {
  font-size: 24px;
  color: #fff;
}

.testimonial-text {
  font-size: 16px;
  line-height: 1.8;
  color: #1d1d1f;
  margin: 0 0 24px;
  font-style: italic;
}

.testimonial-author {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.author-name {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
}

.author-school {
  font-size: 14px;
  color: #666;
}

/* 响应式调整 */
@media (max-width: 1024px) {

  .cases-container,
  .testimonials-container {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {

  .cases-container,
  .testimonials-container {
    grid-template-columns: 1fr;
  }

  .cases-section,
  .testimonials-section {
    padding: 60px 0;
  }

  .case-card,
  .testimonial-card {
    padding: 24px;
  }
}

/* 新增样式 */
.feature-progress {
  margin: 20px 0;
  padding: 0 10px;
}

.progress-item {
  margin-bottom: 12px;
}

.progress-label {
  display: block;
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.progress-bar {
  height: 6px;
  background: #f0f4ff;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #0066cc, #0052a3);
  border-radius: 3px;
  transition: width 0.3s ease;
}

.cost-comparison {
  margin: 20px 0;
  padding: 15px;
  background: #f8f9ff;
  border-radius: 12px;
}

.cost-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.cost-item:last-child {
  border-bottom: none;
}

.cost-item.highlight {
  color: #0066cc;
  font-weight: 600;
}

.cost-label {
  font-size: 14px;
}

.cost-value {
  font-size: 16px;
  font-weight: 600;
}

.quality-badges {
  display: flex;
  gap: 12px;
  margin: 20px 0;
  flex-wrap: wrap;
}

.badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #f0f4ff;
  border-radius: 100px;
  font-size: 14px;
  color: #0066cc;
}

.advisor-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  margin: 20px 0;
}

.stat-item {
  text-align: center;
  padding: 15px;
  background: #f8f9ff;
  border-radius: 12px;
}

.stat-number {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #0066cc;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  color: #666;
}

.feature-highlight {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  text-align: center;
}

.highlight-number {
  display: block;
  font-size: 32px;
  font-weight: 700;
  color: #0066cc;
  margin-bottom: 4px;
}

.highlight-text {
  font-size: 14px;
  color: #666;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .quality-badges {
    justify-content: center;
  }

  .advisor-stats {
    grid-template-columns: 1fr;
  }

  .feature-highlight {
    margin-top: 15px;
    padding-top: 15px;
  }

  .highlight-number {
    font-size: 28px;
  }
}
</style>
