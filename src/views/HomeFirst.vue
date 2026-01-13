<template>
  <div class="homefirst">
    <!-- 添加加载遮罩 -->
    <div class="loading-overlay" v-if="isLoading">
      <el-loading></el-loading>
    </div>
    <!-- 导航栏组件 -->
    <nav-bar></nav-bar>

    <!-- 主要内容区域 -->
    <div class="content-container">
      <!-- 智能择校部分 -->
      <section class="hero-section">
        <div class="hero-content">
          <h1>AI驱动 · 留学新篇章</h1>
          <p class="subtitle">让每一个梦想都能抵达彼岸，用科技重新定义留学申请。</p>
          <div class="button-group">
            <el-button type="primary" round @click="goToR">了解更多</el-button>
            <el-button class="secondary-button" round @click="goToRebateStart">现在开始</el-button>
          </div>
        </div>
        <!-- 添加背景图片容器 -->
        <div class="hero-image">
          <img src="/images/optimized/hero-bg2.jpg" alt="入学奖励" loading="eager" decoding="async" @load="handleImageLoad">
        </div>
      </section>

      <!-- 下半部分两列布局 -->
      <div class="features-grid">
        <!-- 自主申请 -->
        <section class="feature-card">
          <div class="feature-content">
            <h2>自助申请</h2>
            <p class="subtitle">专业的留学申请指导</p>
            <div class="button-group">
              <el-button type="primary" round @click="goToSelfMore">了解更多</el-button>
              <el-button class="secondary-button" round @click="goTostudy">现在开始</el-button>
            </div>
          </div>
          <div class="feature-image">
            <img src="/images/optimized/self-apply1.jpg" alt="自助申请" loading="lazy" decoding="async"
              @load="handleImageLoad">
          </div>
        </section>

        <!-- 智能择校 -->
        <section class="feature-card">
          <div class="feature-content">
            <h2>智能择校</h2>
            <p>Hello, AI Intelligence.</p>
            <div class="button-group">
              <el-button type="primary" round @click="goToAiselect">了解更多</el-button>
              <el-button class="secondary-button" round @click="goToAiMatching">现在开始</el-button>
            </div>
          </div>
          <div class="feature-image">
            <img src="/images/optimized/rebate1.jpg" alt="入校返点" loading="lazy" decoding="async" @load="handleImageLoad">
          </div>
        </section>

        <!-- 热门留学 -->
        <section class="feature-card">
          <div class="feature-content">
            <h2>院校库</h2>
            <p class="subtitle">探索全球顶尖学府</p>
            <div class="button-group">
              <el-button type="primary" round @click="goToSelfSchools">院校库</el-button>
              <el-button class="secondary-button" round @click="goToMajorIndex">专业库</el-button>
            </div>
          </div>
          <div class="feature-image">
            <img src="/images/optimized/popular-study1.jpg" alt="热门留学" loading="lazy" decoding="async"
              @load="handleImageLoad">
          </div>
        </section>

        <!-- 客服咨询 -->
        <section class="feature-card">
          <div class="feature-content">
            <h2>关于我们</h2>
            <p class="subtitle">新时代留学助手</p>
            <div class="button-group">
              <el-button type="primary" round @click="goToCustomerService">了解更多</el-button>
              <el-button class="secondary-button" round @click="goToTeacherConsultation">留学团队</el-button>
            </div>
          </div>
          <div class="feature-image">
            <img src="/images/optimized/customer-service1.jpg" alt="客服咨询" loading="lazy" decoding="async"
              @load="handleImageLoad">
          </div>
        </section>
      </div>
    </div>


    <!-- 添加底部导航栏 -->
    <footer-bar></footer-bar>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import FooterBar from '../components/FooterBar.vue'
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const imagesLoaded = ref(0)
const totalImages = ref(5) // 总图片数量



const goTostudy = () => {
  // 添加页面切换前的延迟
  setTimeout(() => {
    router.push('/study')
  }, 300)
}

const isLoading = ref(true)

// 图片加载完成处理
const handleImageLoad = () => {
  imagesLoaded.value++
  if (imagesLoaded.value >= totalImages.value) {
    setTimeout(() => {
      isLoading.value = false
    }, 300)
  }
}

// 页面加载完成后隐藏遮罩
onMounted(async () => {
  await nextTick()

  // 预加载关键图片
  const criticalImages = ['/images/optimized/hero-bg.jpg']
  const promises = criticalImages.map(src => {
    return new Promise((resolve) => {
      const img = new Image()
      img.onload = resolve
      img.onerror = resolve
      img.src = src
    })
  })

  await Promise.all(promises)

  // 如果图片加载超时，强制显示页面
  setTimeout(() => {
    if (isLoading.value) {
      isLoading.value = false
    }
  }, 2000)
})

const goToSelfMore = async () => {
  try {
    isLoading.value = true
    await router.push('/self-more')
    await new Promise(resolve => setTimeout(resolve, 300))
  } catch (error) {
    console.error('Navigation failed:', error)
  } finally {
    isLoading.value = false
  }
}

const goToR = async () => {
  try {
    isLoading.value = true
    await router.push('/r')
    await new Promise(resolve => setTimeout(resolve, 300))
  } catch (error) {
    console.error('Navigation failed:', error)
  } finally {
    isLoading.value = false
  }
}

const goToAiselect = async () => {
  console.log('goToAiselect function called')
  try {
    isLoading.value = true
    console.log('Navigating to /aiselectNew')
    await router.push('/aiselectNew')
    await new Promise(resolve => setTimeout(resolve, 300))
    console.log('Navigation successful')
  } catch (error) {
    console.error('Navigation failed:', error)
  } finally {
    isLoading.value = false
  }
}

const goToAiMatching = async () => {
  try {
    isLoading.value = true
    ElMessage.success('正在进入智能择校匹配系统...')
    await router.push('/matching')
    await new Promise(resolve => setTimeout(resolve, 300))
  } catch (error) {
    console.error('Navigation failed:', error)
    ElMessage.error('页面跳转失败，请重试')
  } finally {
    isLoading.value = false
  }
}

const goToRebateStart = async () => {
  try {
    isLoading.value = true
    await router.push('/rebateStart')
    await new Promise(resolve => setTimeout(resolve, 300))
  } catch (error) {
    console.error('Navigation failed:', error)
  } finally {
    isLoading.value = false
  }
}


const goToSelfSchools = async () => {
  try {
    isLoading.value = true
    await router.push('/country')
    await new Promise(resolve => setTimeout(resolve, 300))
  } catch (error) {
    console.error('Navigation failed:', error)
  } finally {
    isLoading.value = false
  }
}

const goToMajorIndex = async () => {
  try {
    isLoading.value = true
    ElMessage.success('正在进入专业索引页面...')
    await router.push('/schools/major-index')
    await new Promise(resolve => setTimeout(resolve, 300))
  } catch (error) {
    console.error('Navigation failed:', error)
    ElMessage.error('页面跳转失败，请重试')
  } finally {
    isLoading.value = false
  }
}

const goToCustomerService = async () => {
  try {
    isLoading.value = true
    await router.push('/customer-service')
    await new Promise(resolve => setTimeout(resolve, 300))
  } catch (error) {
    console.error('Navigation failed:', error)
    ElMessage.error('页面跳转失败，请重试')
  } finally {
    isLoading.value = false
  }
}

const goToTeacherConsultation = async () => {
  try {
    isLoading.value = true
    ElMessage.success('正在进入老师咨询页面...')
    await router.push('/teacher-consultation')
    await new Promise(resolve => setTimeout(resolve, 300))
  } catch (error) {
    console.error('Navigation failed:', error)
    ElMessage.error('页面跳转失败，请重试')
  } finally {
    isLoading.value = false
  }
}



</script>

<style scoped>
.homefirst {
  background: #fff;
  color: #fff;
  display: flex;
  flex-direction: column;
  min-height: 200vh;
  transition: opacity 0.3s ease;
}

.homefirst.loading {
  opacity: 0;
}

.content-container {
  width: 100%;
  flex: 1;
}

/* 优化动画曲线 - 简化动画以提升性能 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes scaleIn {
  from {
    transform: scale(0.98);
    opacity: 0;
  }

  to {
    transform: scale(1);
    opacity: 1;
  }
}

/* 智能择校部分样式 */
.hero-section {
  position: relative;
  height: 850px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  background: #000;
  overflow: hidden;
  margin: 0;
  animation: fadeInUp 0.6s ease-out;
}

.hero-content {
  position: relative;
  z-index: 2;
  padding: 0 20px;
  max-width: 800px;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 0;
}

.hero-content h1 {
  font-size: 56px;
  line-height: 1;
  margin-bottom: 8px;
  font-weight: 600;
  letter-spacing: -0.002em;
  animation: fadeInUp 0.6s ease-out 0.2s;
  animation-fill-mode: both;
}

.hero-content .subtitle {
  font-size: 28px;
  line-height: 1;
  margin-bottom: 24px;
  animation: fadeInUp 0.6s ease-out 0.3s;
  animation-fill-mode: both;
}

/* 添加顶部图片样式 */
.hero-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.hero-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0.5;
  /* 调暗hero区域图片 */
  animation: scaleIn 0.8s ease-out 0.1s, fadeInScale 1.2s ease-out;
  transition: transform 0.3s ease-out, opacity 0.3s ease-out;
}

/* hero区域悬停效果 */
.hero-section:hover .hero-image img {
  transform: scale(1.02);
  opacity: 0.6;
}

.hero-image img::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: 0;
  box-shadow:
    inset 0 0 20px rgba(255, 255, 255, 0.1),
    0 0 30px rgba(0, 113, 227, 0.1);
  pointer-events: none;
}

/* 下半部分布局样式 */
.features-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  padding: 10px;
  background: #fff;
  margin-top: 5px;
}

.feature-card {
  position: relative;
  height: 600px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  background: transparent;
  overflow: hidden;
  animation: fadeInUp 0.6s ease-out;
}

.feature-content {
  position: relative;
  z-index: 2;
  padding: 0 20px;
  max-width: 600px;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 0;
}

.features-grid section .feature-content h2 {
  font-size: 44px;
  line-height: 1.1;
  margin-bottom: 12px;
  font-weight: 600;
  letter-spacing: -0.002em;
  margin-top: 0;
  animation: fadeInUp 0.6s ease-out 0.2s;
  animation-fill-mode: both;
}

.features-grid section .feature-content .subtitle,
.features-grid section .feature-content p {
  font-size: 22px;
  line-height: 1.2;
  margin-bottom: 20px;
  margin-top: 0;
  animation: fadeInUp 0.6s ease-out 0.3s;
  animation-fill-mode: both;
}

.feature-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.feature-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 1;
  animation: scaleIn 0.8s ease-out 0.1s;
  transition: transform 0.3s ease-out;
}

.feature-image img::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  box-shadow:
    inset 0 0 20px rgba(255, 255, 255, 0.1),
    0 0 30px rgba(0, 113, 227, 0.1);
  pointer-events: none;
}

/* 统一按钮组样式 */
.button-group {
  display: flex;
  gap: 20px;
  /* 统一按钮间距 */
  justify-content: center;
  margin-top: 2em;
  padding: 0 20px;
  /* 添加内边距 */
}

/* 智能择校按钮组特殊调整 */
.hero-section .button-group {
  margin-top: 2em;
  /* 顶部大图的按钮间距更大 */
}

/* 卡片按钮组位置优化 */
.feature-content .button-group {
  margin-top: 1.2em;
  /* 卡片按钮间距稍小 */
}

/* 确保所有按钮对齐方式一致 */
.features-grid section .button-group {
  justify-content: center;
}

/* 响应式优化 */
@media (max-width: 768px) {
  .button-group {
    flex-direction: column;
    gap: 12px;
    margin-top: 1.2em;
  }

  .hero-section .button-group {
    margin-top: 1.5em;
  }
}

/* 通用按钮样式优化 - 灰白风格 */
:deep(.el-button) {
  border-radius: 50px;
  font-weight: 500;
  letter-spacing: 0.5px;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  animation: fadeInUp 0.6s ease-out 0.4s;
  animation-fill-mode: both;
  position: relative;
  overflow: hidden;
}

.el-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

/* 主按钮通用样式 - 深灰风格 */
:deep(.el-button--primary) {
  background: linear-gradient(135deg, #495057 0%, #343a40 100%) !important;
  border: 1px solid #343a40 !important;
  color: #ffffff !important;
  padding: 12px 32px;
  font-size: 18px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(52, 58, 64, 0.2);
}

:deep(.el-button--primary)::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(120deg,
      transparent,
      rgba(255, 255, 255, 0.6),
      transparent);
  transition: 0.6s ease;
}

:deep(.el-button--primary)::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
  transform: translate(-50%, -50%);
  transition: all 0.4s ease;
  border-radius: 50%;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #6c757d 0%, #495057 100%) !important;
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(52, 58, 64, 0.3);
  border-color: #495057 !important;
}

:deep(.el-button--primary:hover)::before {
  left: 100%;
}

:deep(.el-button--primary:hover)::after {
  width: 120px;
  height: 120px;
}

/* 次要按钮通用样式 - 浅灰边框风格 */
.secondary-button {
  background: transparent !important;
  border: 2px solid rgba(206, 212, 218, 0.8) !important;
  color: #6c757d !important;
  padding: 12px 32px;
  font-size: 18px;
  backdrop-filter: blur(10px);
  position: relative;
  overflow: hidden;
}

/* hero-section 中的次要按钮特殊样式 - 白色文字更清晰 */
.hero-section .secondary-button {
  background: rgba(255, 255, 255, 0.1) !important;
  border: 2px solid rgba(255, 255, 255, 0.6) !important;
  color: #ffffff !important;
  backdrop-filter: blur(10px);
}

.hero-section .secondary-button:hover {
  background: rgba(255, 255, 255, 0.2) !important;
  border-color: rgba(255, 255, 255, 0.8) !important;
  color: #ffffff !important;
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
}

.secondary-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(120deg,
      transparent,
      rgba(255, 255, 255, 0.4),
      transparent);
  transition: 0.6s ease;
}

.secondary-button::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, #adb5bd, transparent);
  transform: translateX(-50%);
  transition: all 0.4s ease;
}

.secondary-button:hover {
  background: rgba(248, 249, 250, 0.8) !important;
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  border-color: #495057 !important;
  color: #343a40 !important;
}

.secondary-button:hover::before {
  left: 100%;
}

.secondary-button:hover::after {
  width: 80%;
}

/* 右边卡片按钮特殊样式 - 深灰风格 */
.features-grid section:last-child .el-button--primary {
  background: linear-gradient(135deg, #495057 0%, #343a40 100%) !important;
  border: 1px solid #343a40 !important;
  color: #ffffff !important;
}

.features-grid section:last-child .secondary-button {
  background: rgba(248, 249, 250, 0.8) !important;
  border: 1px solid rgba(206, 212, 218, 0.6) !important;
  color: #6c757d !important;
}

.features-grid section:last-child .secondary-button:hover {
  background: rgba(255, 255, 255, 0.9) !important;
  border-color: rgba(173, 181, 189, 0.8) !important;
  color: #495057 !important;
}

/* 左边卡片文字位置 */
.features-grid section:first-child .feature-content {
  align-items: center;
  text-align: center;
  margin-top: 0;
  padding: 0 20px;
}

/* 右边卡片文字位置 */
.features-grid section:last-child .feature-content {
  align-items: center;
  text-align: center;
  margin-top: 0;
  padding: 0 20px;
}

/* 右边卡片背景色调整 */
.features-grid section:last-child {
  background: hsl(240, 18%, 90%);
}

/* 右边卡片图片透明度微调 */
.features-grid section:last-child .feature-image img {
  opacity: 0.95;
}

/* 左边卡片样式优化 - 自主申请 */
.features-grid section:first-child .feature-content h2 {
  color: #ffffff !important;
  text-shadow: 3px 3px 6px rgba(0, 0, 0, 0.8), 1px 1px 2px rgba(0, 0, 0, 0.9);
  font-weight: 700;
  letter-spacing: 1px;
}

.features-grid section:first-child .feature-content .subtitle,
.features-grid section:first-child .feature-content p {
  color: #f8f9fa !important;
  text-shadow: 2px 2px 5px rgba(0, 0, 0, 0.7), 1px 1px 2px rgba(0, 0, 0, 0.8);
  font-weight: 500;
  letter-spacing: 0.5px;
}

/* 左边卡片按钮样式 - 深灰风格 */
.features-grid section:first-child .el-button--primary {
  background: linear-gradient(135deg, #495057 0%, #343a40 100%) !important;
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
  color: #ffffff !important;
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.features-grid section:first-child .secondary-button {
  background: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid rgba(255, 255, 255, 0.6) !important;
  color: #f8f9fa !important;
  backdrop-filter: blur(10px);
}

.features-grid section:first-child .el-button--primary:hover {
  background: linear-gradient(135deg, #6c757d 0%, #495057 100%) !important;
  border-color: rgba(255, 255, 255, 0.5) !important;
  color: #ffffff !important;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.4);
}

.features-grid section:first-child .secondary-button:hover {
  background: rgba(255, 255, 255, 0.2) !important;
  border-color: rgba(255, 255, 255, 0.8) !important;
  color: #ffffff !important;
}

/* 智能择校卡片按钮样式 */
.features-grid section:nth-child(2) .secondary-button {
  background: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid rgba(255, 255, 255, 0.6) !important;
  color: #ffffff !important;
  backdrop-filter: blur(10px);
}

.features-grid section:nth-child(2) .secondary-button:hover {
  background: rgba(255, 255, 255, 0.2) !important;
  border-color: rgba(255, 255, 255, 0.8) !important;
  color: #ffffff !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

/* 院校库卡片按钮样式 */
.features-grid section:nth-child(3) .secondary-button {
  background: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid rgba(255, 255, 255, 0.6) !important;
  color: #ffffff !important;
  backdrop-filter: blur(10px);
}

.features-grid section:nth-child(3) .secondary-button:hover {
  background: rgba(255, 255, 255, 0.2) !important;
  border-color: rgba(255, 255, 255, 0.8) !important;
  color: #ffffff !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

/* 关于我们卡片文字样式 */
.features-grid section:nth-child(4) .feature-content h2 {
  color: #ffffff !important;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.features-grid section:nth-child(4) .feature-content p,
.features-grid section:nth-child(4) .feature-content .subtitle {
  color: rgba(255, 255, 255, 0.9) !important;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

/* 关于我们卡片按钮样式 */
.features-grid section:nth-child(4) .secondary-button {
  background: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid rgba(255, 255, 255, 0.6) !important;
  color: #ffffff !important;
  backdrop-filter: blur(10px);
}

.features-grid section:nth-child(4) .secondary-button:hover {
  background: rgba(255, 255, 255, 0.2) !important;
  border-color: rgba(255, 255, 255, 0.8) !important;
  color: #ffffff !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

/* 右边卡片样式优化 */
.features-grid section:last-child .feature-content h2 {
  color: #1d1d1f;
}

.features-grid section:last-child .feature-content p {
  color: #1d1d1f;
}

/* 响应式布局调整 */
@media (max-width: 1024px) {
  .features-grid section .feature-content h2 {
    font-size: 38px;
  }

  .features-grid section .feature-content .subtitle,
  .features-grid section .feature-content p {
    font-size: 20px;
  }
}

@media (max-width: 768px) {
  .features-grid section .feature-content h2 {
    font-size: 34px;
  }

  .features-grid section .feature-content .subtitle,
  .features-grid section .feature-content p {
    font-size: 18px;
  }
}

/* 自主申请按钮组优化 */
.features-grid section:first-child .button-group {
  gap: 20px;
  /* 增大按钮间距 */
  margin-top: 1.2em;
  /* 增加上边距 */
}

/* 响应式优化 */
@media (max-width: 768px) {
  .features-grid section:first-child .button-group {
    flex-direction: column;
    gap: 12px;
  }

  :deep(.el-button) {
    padding: 10px 24px;
    font-size: 16px;
  }
}

/* 响应式调整 */
@media (max-width: 768px) {

  .hero-section,
  .feature-card {
    animation: none;
  }

  .hero-content h1,
  .hero-content .subtitle,
  .feature-content h2,
  .feature-content .subtitle {
    animation: fadeInUp 0.6s ease-out;
  }
}

/* 简化视差效果 */
.hero-section:hover .hero-image img {
  transform: scale(1.02);
}

/* 优化底部图片动画 */
.feature-image img {
  animation: scaleIn 0.8s ease-out 0.1s;
  transition: transform 0.3s ease-out;
}

/* 简化视差效果 */
.feature-card:hover .feature-image img {
  transform: scale(1.02);
}

/* 确保动画触发 */
.feature-card {
  overflow: hidden;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .feature-image img {
    animation: scaleIn 0.8s ease-out;
  }

  .feature-card:hover .feature-image img {
    transform: none;
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .hero-content h1 {
    font-size: 40px;
  }

  .hero-content .subtitle {
    font-size: 22px;
  }
}

/* 为客服咨询图片添加特殊效果 */
.features-grid section:last-child .feature-image img::after {
  border-radius: 12px;
  box-shadow:
    inset 0 0 15px rgba(0, 0, 0, 0.1),
    0 8px 25px rgba(0, 0, 0, 0.15);
}

/* 添加悬停动态效果 */
.hero-image:hover img::after,
.feature-image:hover img::after {
  box-shadow:
    inset 0 0 25px rgba(255, 255, 255, 0.15),
    0 0 40px rgba(0, 113, 227, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
}





/* 添加加载遮罩样式 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
}

/* 骨架屏样式 */
.skeleton {
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
}

@keyframes loading {
  0% {
    background-position: 200% 0;
  }

  100% {
    background-position: -200% 0;
  }
}

.skeleton-text {
  height: 20px;
  margin: 10px 0;
  border-radius: 4px;
}

.skeleton-title {
  height: 40px;
  margin: 20px 0;
  border-radius: 8px;
}

.skeleton-button {
  height: 45px;
  width: 120px;
  margin: 10px;
  border-radius: 25px;
}

/* 添加页面过渡效果 */
.homefirst {
  transition: opacity 0.3s ease;
}

.homefirst.loading {
  opacity: 0;
}
</style>