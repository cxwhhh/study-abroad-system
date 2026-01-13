<template>
  <div class="home-container">
    <nav-bar></nav-bar>

    <!-- 轮播图 Section -->
    <div class="carousel-section">
      <el-carousel trigger="click" height="450px" :interval="5000" arrow="always" class="main-carousel">
        <el-carousel-item v-for="(item, index) in carouselItems" :key="index">
          <div class="carousel-item-content" :style="{ backgroundImage: `url(${item.image})` }">
            <div class="carousel-overlay"></div>
            <div class="carousel-text container">
              <div class="hero-content">
                <h1 class="animate-up">{{ item.title }}</h1>
                <p class="animate-up delay-1">{{ item.subtitle }}</p>
                <el-button type="primary" size="large" class="animate-up delay-2 action-btn"
                  @click="handleCarouselClick(item.link)">
                  {{ item.buttonText }}
                </el-button>
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>

      <!-- 悬浮功能条 -->
      <div class="features-bar-wrapper">
        <div class="features-bar">
          <!-- 院校库 -->
          <div class="feature-item" @click="router.push('/country')">
            <div class="feature-icon-box">
              <el-icon>
                <School />
              </el-icon>
            </div>
            <div class="feature-info">
              <h3>全球院校库</h3>
              <p>覆盖英美主流名校</p>
            </div>
          </div>

          <!-- 专业库 -->
          <div class="feature-item" @click="router.push('/schools/major-index')">
            <div class="feature-icon-box">
              <el-icon>
                <Reading />
              </el-icon>
            </div>
            <div class="feature-info">
              <h3>专业数据库</h3>
              <p>海量专业详细解析</p>
            </div>
          </div>


          <!-- 自主申请 -->
          <div class="feature-item" @click="router.push('/study')">
            <div class="feature-icon-box">
              <el-icon>
                <Edit />
              </el-icon>
            </div>
            <div class="feature-info">
              <h3>自主申请</h3>
              <p>DIY留学申请指导</p>
            </div>
          </div>

          <!-- 智能择校 -->
          <div class="feature-item highlight" @click="router.push('/matching')">
            <div class="feature-icon-box">
              <el-icon>
                <Cpu />
              </el-icon>
            </div>
            <div class="feature-info">
              <h3>AI 智能择校</h3>
              <p>定制专属留学方案</p>
            </div>
          </div>

          <!-- 入学奖励 -->
          <div class="feature-item" @click="router.push('/r')">
            <div class="feature-icon-box">
              <el-icon>
                <Trophy />
              </el-icon>
            </div>
            <div class="feature-info">
              <h3>入学奖励</h3>
              <p>丰厚奖学金计划</p>
            </div>
          </div>

          <!-- 成功案例 -->
          <div class="feature-item" @click="router.push('/cases')">
            <div class="feature-icon-box">
              <el-icon>
                <Trophy />
              </el-icon>
            </div>
            <div class="feature-info">
              <h3>成功案例</h3>
              <p>名校录取真实案例</p>
            </div>
          </div>

          <!-- 老师团队 -->
          <div class="feature-item" @click="router.push('/teacher-consultation')">
            <div class="feature-icon-box">
              <el-icon>
                <UserFilled />
              </el-icon>
            </div>
            <div class="feature-info">
              <h3>名师团队</h3>
              <p>全球顶尖名校导师</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 资讯与推荐 Section -->
    <div class="info-section container">
      <div class="info-grid">
        <!-- 左侧：最新资讯列表 -->
        <div class="news-column">
          <div class="section-header">
            <h2>新闻资讯</h2>
            <a @click="router.push('/news')" class="view-more">
              查看更多 <el-icon>
                <ArrowRight />
              </el-icon>
            </a>
          </div>
          <div class="news-list">
            <div v-for="item in newsList.slice(0, 10)" :key="item.id" class="news-item" @click="openNewsDetail(item)">
              <div class="news-title">{{ item.title }}</div>
              <div class="news-meta">
                <span class="news-category">{{ item.category }}</span>
                <span class="news-date">{{ item.date }}</span>
              </div>
            </div>
            <div v-if="newsList.length === 0" class="news-empty">暂无资讯</div>
          </div>
          <div class="view-more-wrapper">
            <button class="load-more-btn" @click="router.push('/news')">
              查看更多 <el-icon>
                <ArrowRight />
              </el-icon>
            </button>
          </div>
        </div>

        <!-- 右侧：热门推荐卡片 -->
        <div class="cards-column">
          <div class="section-header">
            <h2>热门推荐</h2>
          </div>
          <div class="hot-cards-grid">
            <div v-for="card in hotCards" :key="card.id" class="hot-card" @click="router.push(card.link)"
              :style="{ backgroundImage: `url(${card.image})` }">
              <div class="card-overlay">
                <div class="card-tag" v-if="card.tag">{{ card.tag }}</div>
                <h3>{{ card.title }}</h3>
                <p>{{ card.subtitle }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 短视频 Section -->
    <div class="video-section container">
      <div class="section-header">
        <h2>精选短视频</h2>
        <a class="view-more">
          查看更多 <el-icon>
            <ArrowRight />
          </el-icon>
        </a>
      </div>
      <div class="video-grid">
        <div v-for="video in videoList" :key="video.id" class="video-card" @click="openVideoPlayer(video)">
          <div class="video-thumbnail" :style="{ backgroundImage: `url(${video.image})` }">
            <div class="play-overlay">
              <el-icon class="play-icon">
                <VideoPlay />
              </el-icon>
            </div>
            <span class="video-duration">{{ video.duration }}</span>
          </div>
          <div class="video-info">
            <h3 class="video-title">{{ video.title }}</h3>
            <div class="video-meta">
              <span class="meta-item"><el-icon>
                  <View />
                </el-icon> {{ video.views }}</span>
              <span class="meta-item">{{ video.likes }} 赞</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="isVideoPlayerOpen" class="video-player-modal" @click.self="closeVideoPlayer">
      <div class="video-player-panel">
        <div class="video-player-header">
          <div class="video-player-title">{{ activeVideo ? activeVideo.title : '' }}</div>
          <button class="video-player-close" type="button" @click="closeVideoPlayer">×</button>
        </div>
        <div class="video-player-body">
          <video v-if="activeVideoSrc" class="video-player" controls autoplay playsinline
            :poster="activeVideo ? activeVideo.image : ''">
            <source :src="activeVideoSrc" />
          </video>
          <div v-else class="video-player-empty">未配置视频地址</div>
        </div>
      </div>
    </div>

    <footer-bar></footer-bar>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import FooterBar from '../components/FooterBar.vue'
import { School, Reading, Cpu, Trophy, UserFilled, ArrowRight, Edit, ArrowDown, VideoPlay, View } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()

const activeVideo = ref(null)
const isVideoPlayerOpen = ref(false)

const openNewsDetail = (item) => {
  if (!item || item.id == null) return
  router.push(`/news/${item.id}`)
}

const activeVideoSrc = computed(() => {
  const src = activeVideo.value && activeVideo.value.src
  return src ? String(src) : ''
})

// 轮播图数据
// 使用 public/images 下的图片资源
const resolvePublicUrl = (path) => {
  return path.startsWith('/') ? path : `/${path}`
}

const carouselItems = ref([
  {
    image: resolvePublicUrl('images/hero-bg.jpg'),
    title: '高端留学 专属定制',
    subtitle: '藤校/G5/Top30名校申请专家，圆您名校梦',
    buttonText: '免费评估',
    link: '/matching'
  },
  {
    image: resolvePublicUrl('images/carousel-6.jpg'), // 假设存在
    title: '海量院校 智能匹配',
    subtitle: '全球院校数据库，大数据精准定位您的目标',
    buttonText: '查阅院校',
    link: '/country'
  },
  {
    image: resolvePublicUrl('images/cases-bg.jpg'),
    title: '名校录取 真实见证',
    subtitle: '数千份成功案例，不仅是结果，更是实力的证明',
    buttonText: '查看案例',
    link: '/cases'
  },
  {
    image: resolvePublicUrl('images/schools/harvard.jpg'), // 使用哈佛图片作为背景
    title: '顶尖导师 全程护航',
    subtitle: '海外名校前招生官与外籍文书导师强强联手',
    buttonText: '预约咨询',
    link: '/teacher-consultation'
  }
])

const newsList = ref([])

const normalizeHomeDate = (value) => {
  if (value == null) return ''
  const str = String(value).trim()
  if (!str) return ''
  if (/^\d{4}-\d{2}-\d{2}/.test(str)) return str.slice(0, 10)
  const date = new Date(str.includes(' ') && !str.includes('T') ? str.replace(' ', 'T') : str)
  if (Number.isNaN(date.getTime())) return str
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

const normalizeHomeCategory = (value) => {
  const raw = value == null ? '' : String(value).trim()
  if (!raw) return '留学资讯'
  const map = {
    policy: '政策解读',
    application: '申请指南',
    life: '留学生活',
    career: '就业前景'
  }
  return map[raw] || raw
}

const fetchHomeNews = async () => {
  try {
    const res = await request.get('/news')
    const list = Array.isArray(res?.data) ? res.data : []
    newsList.value = list
      .filter((n) => n && n.id != null)
      .map((n) => ({
        id: n.id,
        title: n.title || '',
        date: normalizeHomeDate(n.createTime),
        category: normalizeHomeCategory(n.category)
      }))
  } catch (e) {
    newsList.value = []
  }
}

// 热门推荐数据 - 改为新闻资讯卡片
const hotCards = ref([
  {
    id: 1,
    title: '2026留学趋势报告发布',
    subtitle: '数字化转型下的全球教育新格局',
    image: resolvePublicUrl('images/schools/harvard.jpg'),
    link: '/news',
    tag: '行业重磅'
  },
  {
    id: 2,
    title: '高薪潜力专业TOP10',
    subtitle: '未来十年最具发展前景的留学专业',
    image: resolvePublicUrl('images/schools/oxford.jpg'),
    link: '/news',
    tag: '专业盘点'
  },
  {
    id: 3,
    title: '名校招生官面对面',
    subtitle: '揭秘藤校录取背后的隐形标准',
    image: resolvePublicUrl('images/carousel-6.jpg'),
    link: '/news',
    tag: '独家专访'
  }
])

// 短视频数据
const videoList = ref([
  {
    id: 1,
    title: '哈佛学姐带你逛校园，真实留学生活大公开',
    image: resolvePublicUrl('images/schools/harvard.jpg'),
    src: resolvePublicUrl('videos/hero-bg.mp4'),
    views: '1.2w',
    likes: '328',
    duration: '0:45'
  },
  {
    id: 2,
    title: '雅思口语7分现场模拟，考官最爱这些表达',
    image: resolvePublicUrl('images/schools/oxford.jpg'),
    src: resolvePublicUrl('videos/hero-bg2.mp4'),
    views: '8.5k',
    likes: '156',
    duration: '1:20'
  },
  {
    id: 3,
    title: '英国留学省钱攻略，一个月生活费只要...',
    image: resolvePublicUrl('images/carousel-6.jpg'),
    src: resolvePublicUrl('videos/Gen-3_Alpha_Turbo_2298329671,_I_want_to_make_a_web,_carousel-3jpg,_M_5.mp4'),
    views: '2.3w',
    likes: '520',
    duration: '0:58'
  },
  {
    id: 4,
    title: '计算机专业就业前景分析，大厂Offer拿到手软',
    image: resolvePublicUrl('images/hero-bg.jpg'),
    src: resolvePublicUrl('videos/hero-bg.mp4'),
    views: '1.5w',
    likes: '210',
    duration: '1:15'
  },
  {
    id: 5,
    title: '英国G5申请材料清单，一次讲清楚',
    image: resolvePublicUrl('images/schools/oxford.jpg'),
    src: resolvePublicUrl('videos/hero-bg2.mp4'),
    views: '9.1k',
    likes: '187',
    duration: '0:52'
  },
  {
    id: 6,
    title: '美国签证面试高频问题与回答思路',
    image: resolvePublicUrl('images/hero-bg.jpg'),
    src: resolvePublicUrl('videos/hero-bg.mp4'),
    views: '1.8w',
    likes: '402',
    duration: '1:08'
  },
  {
    id: 7,
    title: '如何选专业？3分钟搞懂职业路径匹配',
    image: resolvePublicUrl('images/carousel-6.jpg'),
    src: resolvePublicUrl('videos/Gen-3_Alpha_Turbo_2298329671,_I_want_to_make_a_web,_carousel-3jpg,_M_5.mp4'),
    views: '2.6w',
    likes: '638',
    duration: '1:12'
  },
  {
    id: 8,
    title: '留学行前必备：住宿、银行卡、手机卡',
    image: resolvePublicUrl('images/schools/harvard.jpg'),
    src: resolvePublicUrl('videos/hero-bg2.mp4'),
    views: '7.4k',
    likes: '146',
    duration: '0:59'
  }
])

const handleCarouselClick = (link) => {
  if (link) {
    router.push(link)
  }
}

const openVideoPlayer = (video) => {
  if (!video) return
  activeVideo.value = video
  isVideoPlayerOpen.value = true
}

const closeVideoPlayer = () => {
  isVideoPlayerOpen.value = false
  activeVideo.value = null
}

onMounted(() => {
  fetchHomeNews()
})
</script>

<style scoped>
.home-container {
  background-color: #F9F8F4;
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
}

/* 容器通用样式 */
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1.5rem;
}

.text-center {
  text-align: center;
}

.mx-auto {
  margin-left: auto;
  margin-right: auto;
}

/* 轮播图样式 */
.carousel-section {
  position: relative;
  width: 100%;
  margin-top: 70px;
  border-radius: 0;
  box-shadow: none;
  margin-bottom: 120px;
  /* 为悬浮条留出空间 */
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
}

.main-carousel {
  border-radius: 0;
  overflow: hidden;
}

.carousel-item-content {
  height: 100%;
  width: 100%;
  background-size: cover;
  background-position: center;
  position: relative;
  display: flex;
  align-items: center;
}

.carousel-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, rgba(0, 0, 0, 0.6) 0%, rgba(0, 0, 0, 0.3) 60%, rgba(0, 0, 0, 0) 100%);
  z-index: 1;
}

.carousel-text {
  position: relative;
  z-index: 2;
  color: white;
  width: 100%;
  padding: 0 1.5rem;
  display: flex;
  align-items: center;
  padding-bottom: 80px;
  /* 向上移动内容 */
}

.hero-content {
  max-width: 650px;
}

.carousel-text h1 {
  font-size: 3.5rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
  letter-spacing: 1px;
  line-height: 1.2;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  font-family: 'Times New Roman', serif;
}

.carousel-text p {
  font-size: 1.25rem;
  margin-bottom: 2.5rem;
  font-weight: 300;
  opacity: 0.95;
  text-shadow: 0 1px 5px rgba(0, 0, 0, 0.3);
}

.main-carousel :deep(.el-carousel__item.is-active .carousel-item-content) {
  animation: carouselZoom 5s ease-out both;
}

.main-carousel :deep(.el-carousel__item.is-active .carousel-overlay) {
  animation: overlayFade 900ms ease-out both;
}

.main-carousel :deep(.el-carousel__item.is-active .animate-up) {
  opacity: 0;
  transform: translateY(22px);
  animation: textRise 900ms cubic-bezier(0.21, 1, 0.32, 1) both;
}

.main-carousel :deep(.el-carousel__item.is-active .delay-1) {
  animation-delay: 160ms;
}

.main-carousel :deep(.el-carousel__item.is-active .delay-2) {
  animation-delay: 320ms;
}

@keyframes carouselZoom {
  from {
    transform: scale(1);
  }

  to {
    transform: scale(1.06);
  }
}

@keyframes overlayFade {
  from {
    opacity: 0.65;
  }

  to {
    opacity: 1;
  }
}

@keyframes textRise {
  from {
    opacity: 0;
    transform: translateY(22px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 悬浮功能条样式 */
.features-bar-wrapper {
  position: absolute;
  bottom: 0;
  left: 0;
  transform: translateY(50%);
  width: 100%;
  z-index: 10;
  padding: 0;
}

.features-bar {
  display: flex;
  background: white;
  border-radius: 0;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  width: 100%;
}

.feature-item {
  flex: 1;
  padding: 1rem 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 0;
  cursor: pointer;
  transition: all 0.3s ease;
  border-right: 1px solid #f0f0f0;
  position: relative;
  height: 100px;
  justify-content: center;
  opacity: 0;
  animation: slideUpFade 0.6s ease-out forwards;
}

.feature-item:nth-child(1) {
  animation-delay: 0.1s;
}

.feature-item:nth-child(2) {
  animation-delay: 0.2s;
}

.feature-item:nth-child(3) {
  animation-delay: 0.3s;
}

.feature-item:nth-child(4) {
  animation-delay: 0.4s;
}

.feature-item:nth-child(5) {
  animation-delay: 0.5s;
}

.feature-item:nth-child(6) {
  animation-delay: 0.6s;
}

.feature-item:nth-child(7) {
  animation-delay: 0.7s;
}

@keyframes slideUpFade {
  from {
    opacity: 0;
    transform: translateY(30px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.feature-item:last-child {
  border-right: none;
}

.feature-item:hover {
  background-color: #fcfcfc;
}

.feature-item.highlight {
  background: white;
}

/* 高亮项顶部金条 */
.feature-item.highlight::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background-color: #C5A059;
}

.feature-icon-box {
  width: auto;
  height: auto;
  background-color: transparent;
  color: #C5A059;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  /* 图标缩小 */
  line-height: 1;
  transition: all 0.3s ease;
  margin-bottom: 0;
}

.feature-item:hover .feature-icon-box {
  background-color: transparent;
  transform: translateY(-3px);
  color: #b08d4a;
}

.feature-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: -2px;
}

.feature-info h3 {
  font-size: 1.1rem;
  font-weight: bold;
  font-family: 'Times New Roman', serif;
  color: #333;
  margin-bottom: 0;
  transition: color 0.3s ease;
  letter-spacing: 0.5px;
  line-height: 1.2;
}

.feature-item:hover .feature-info h3 {
  color: #C5A059;
}

.feature-info p {
  color: #999;
  font-size: 0.7rem;
  margin: 0;
  display: none;
  /* 根据截图风格，可能只需要标题，或者描述很小。这里先隐藏描述，或者只在桌面端显示 */
}

@media (min-width: 1200px) {
  .feature-info p {
    display: block;
  }
}

.action-btn {
  font-weight: 600;
  padding: 1.5rem 3rem;
  font-size: 1.125rem;
  background-color: #C5A059;
  border-color: #C5A059;
  transition: all 0.3s ease;
  border-radius: 4px;
}

/* 资讯与推荐样式 */
.info-section {
  padding: 3rem 2rem;
  background-color: white;
  border-radius: 20px;
  margin-top: 2rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.info-grid {
  display: grid;
  grid-template-columns: 2.2fr 1fr;
  gap: 3rem;
}

.section-header {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 1.5rem;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 1rem;
}

.section-header h2 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #333;
  margin: 0;
  position: relative;
  font-family: 'Times New Roman', serif;
}

.section-header h2::after {
  content: '';
  position: absolute;
  bottom: -1.1rem;
  /* Aligns with border-bottom padding + border width */
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background-color: #C5A059;
}

.view-more {
  position: absolute;
  right: 0;
  bottom: 1rem;
  font-size: 0.9rem;
  color: #999;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.3s;
}

.view-more:hover {
  color: #C5A059;
}

/* 新闻列表 */
.news-list {
  display: flex;
  flex-direction: column;
}

.news-empty {
  padding: 18px 0;
  color: #999;
  font-size: 14px;
}

.news-item {
  padding: 1rem 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s;
}

.news-item:last-child {
  border-bottom: none;
}

.news-item:hover {
  background-color: #fafafa;
  padding-left: 1rem;
  padding-right: 1rem;
  border-radius: 8px;
}

.news-item:hover .news-title {
  color: #C5A059;
}

.news-meta {
  display: flex;
  justify-content: space-between;
  font-size: 0.85rem;
  color: #999;
  margin-top: 0.5rem;
}

.news-category {
  color: #C5A059;
  background-color: rgba(197, 160, 89, 0.1);
  padding: 2px 8px;
  border-radius: 4px;
}

.news-title {
  font-size: 1.15rem;
  color: #333;
  font-weight: 600;
  line-height: 1.5;
  transition: color 0.3s;
}

/* 加载更多按钮 */
.view-more-wrapper {
  margin-top: 2rem;
  display: flex;
  justify-content: center;
}

.load-more-btn {
  background-color: transparent;
  color: #C5A059;
  border: 1px solid #C5A059;
  padding: 0.75rem 2rem;
  font-size: 0.95rem;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s;
  font-family: inherit;
}

.load-more-btn:hover {
  background-color: #C5A059;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(197, 160, 89, 0.2);
}

/* 热门卡片 */
.hot-cards-grid {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

/* 短视频样式 */
.video-section {
  padding: 3rem 2rem;
  background-color: white;
  border-radius: 20px;
  margin-top: 2rem;
  margin-bottom: 2rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
}

.video-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.video-card:hover {
  transform: translateY(-5px);
}

.video-thumbnail {
  height: 280px;
  background-size: cover;
  background-position: center;
  border-radius: 12px;
  position: relative;
  overflow: hidden;
  margin-bottom: 0.8rem;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.play-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.video-card:hover .play-overlay {
  opacity: 1;
}

.play-icon {
  font-size: 3rem;
  color: white;
  filter: drop-shadow(0 2px 5px rgba(0, 0, 0, 0.5));
}

.video-duration {
  position: absolute;
  bottom: 10px;
  right: 10px;
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.75rem;
}

.video-title {
  font-size: 1rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 0.5rem;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.video-meta {
  display: flex;
  justify-content: space-between;
  font-size: 0.8rem;
  color: #999;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.video-player-modal {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.55);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  z-index: 2000;
}

.video-player-panel {
  width: min(980px, 100%);
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
}

.video-player-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 16px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
}

.video-player-title {
  font-size: 14px;
  font-weight: 700;
  color: #1d1d1f;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.video-player-close {
  appearance: none;
  border: none;
  background: transparent;
  font-size: 22px;
  line-height: 1;
  padding: 4px 8px;
  cursor: pointer;
  color: #666;
}

.video-player-close:hover {
  color: #111;
}

.video-player-body {
  padding: 16px;
  background: #0b0b0c;
}

.video-player {
  width: 100%;
  height: auto;
  display: block;
  border-radius: 12px;
  background: #000;
}

.video-player-empty {
  color: rgba(255, 255, 255, 0.85);
  font-size: 14px;
  text-align: center;
  padding: 36px 16px;
}

.hot-card {
  height: 220px;
  border-radius: 12px;
  background-size: cover;
  background-position: center;
  background-color: #e0e0e0;
  /* Fallback color */
  position: relative;
  cursor: pointer;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
}

.hot-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
}

.card-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  padding: 1.5rem;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8), transparent);
  color: white;
}

.card-tag {
  display: inline-block;
  background-color: #C5A059;
  color: white;
  font-size: 0.75rem;
  padding: 2px 8px;
  border-radius: 4px;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.card-overlay h3 {
  font-size: 1.25rem;
  font-weight: 700;
  margin-bottom: 0.25rem;
}

.card-overlay p {
  font-size: 0.9rem;
  opacity: 0.9;
  margin: 0;
}

/* 响应式调整 */
@media (max-width: 992px) {
  .features-bar {
    flex-wrap: wrap;
  }

  .info-section {
    padding: 2rem 1rem;
  }

  .feature-item {
    flex: 0 0 50%;
    /* 两列布局 */
    border-bottom: 1px solid #f0f0f0;
  }

  .feature-item:nth-child(even) {
    border-right: none;
  }

  .feature-item:last-child {
    flex: 0 0 100%;
    /* 最后一个占满 */
    border-bottom: none;
  }

  .info-grid {
    grid-template-columns: 1fr;
    gap: 3rem;
  }

  .video-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .carousel-text h1 {
    font-size: 2rem;
  }

  .carousel-text p {
    font-size: 1rem;
  }

  .feature-item {
    flex: 0 0 100%;
    /* 单列布局 */
    border-right: none;
  }

  .action-btn {
    padding: 1rem 2rem;
    font-size: 1rem;
  }
}
</style>
