<template>
  <div class="sitemap-page">
    <nav-bar></nav-bar>

    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="container">
        <div class="header-content">
          <h1>网站地图</h1>
          <div class="subtitle">SITEMAP</div>
          <div class="intro-text">
            <p>探索新英美留学咨询服务的完整架构，快速定位您所需的信息与资源。</p>
          </div>
        </div>
      </div>
      <!-- 装饰性背景元素 -->
      <div class="bg-pattern"></div>
    </div>

    <div class="content-section">
      <div class="container">
        <div class="sitemap-grid">
          <div v-for="(section, index) in sitemapData" :key="index" class="sitemap-card"
            :style="{ animationDelay: `${index * 0.1}s` }">
            <div class="card-header">
              <div class="icon-wrapper">
                <el-icon :size="24">
                  <component :is="section.icon" />
                </el-icon>
              </div>
              <h2>{{ section.title }}</h2>
            </div>
            <div class="card-divider"></div>
            <ul class="sitemap-links">
              <li v-for="(link, linkIndex) in section.links" :key="linkIndex">
                <router-link :to="link.path" class="link-item">
                  <span class="link-text">{{ link.text }}</span>
                  <el-icon class="link-arrow">
                    <ArrowRight />
                  </el-icon>
                </router-link>
              </li>
            </ul>
          </div>
        </div>

        <div class="sitemap-footer">
          <div class="footer-content">
            <h3>需要更多帮助？</h3>
            <p>如果您在网站上找不到所需的信息，请随时联系我们的专业顾问团队。</p>
            <ul class="contact-info">
              <li>
                <el-icon>
                  <Message />
                </el-icon>
                <span>info@study.com</span>
              </li>
              <li>
                <el-icon>
                  <Phone />
                </el-icon>
                <span>400-151-7451</span>
              </li>
              <li>
                <el-icon>
                  <Timer />
                </el-icon>
                <span>周一至周五 9:30-18:30</span>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <footer-bar></footer-bar>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import NavBar from '../components/NavBar.vue'
import FooterBar from '../components/FooterBar.vue'
import {
  House,
  Service,
  School,
  User,
  Document,
  Setting,
  ArrowRight,
  Message,
  Phone,
  Timer,
  Guide,
  Reading
} from '@element-plus/icons-vue'

const sitemapData = ref([
  {
    title: '核心导航',
    icon: House,
    links: [
      { text: '首页', path: '/' },
      { text: '关于我们', path: '/about' },
      { text: '留学服务', path: '/services' },
      { text: '成功案例', path: '/cases' },
      { text: '留学资讯', path: '/news' },
      { text: '留学国家', path: '/country' },
      { text: '帮助中心', path: '/help-center' },
      { text: '客户服务', path: '/customer-service' }
    ]
  },
  {
    title: '申请流程',
    icon: Guide,
    links: [
      { text: '留学申请流程', path: '/study' },
      { text: '前期准备', path: '/steps/preparation' },
      { text: '材料准备', path: '/steps/materials' },
      { text: '递交申请', path: '/steps/submission' },
      { text: '面试准备', path: '/steps/interview' },
      { text: '签证申请', path: '/steps/visa' },
      { text: '行前准备', path: '/steps/assistant' },
      { text: '申请步骤总览', path: '/application-steps' }
    ]
  },
  {
    title: '院校信息',
    icon: School,
    links: [
      { text: '全部学校', path: '/schoolList/all' },
      { text: '美国学校', path: '/schoolList/us' },
      { text: '英国学校', path: '/schoolList/uk' },
      { text: '加拿大学校', path: '/schoolList/canada' },
      { text: '澳大利亚学校', path: '/schoolList/australia' },
      { text: 'AI选校助手', path: '/aiSelect' }
    ]
  },
  {
    title: '用户中心',
    icon: User,
    links: [
      { text: '登录', path: '/login' },
      { text: '个人中心', path: '/profile' },
      { text: '文件上传', path: '/file-upload' },
      { text: '返利申请', path: '/rebateStart' },
      { text: '自助服务', path: '/self-more' }
    ]
  },
  {
    title: '政策与条款',
    icon: Document,
    links: [
      { text: '隐私政策', path: '/privacy' },
      { text: '服务条款', path: '/terms' }
    ]
  },
  {
    title: '管理入口',
    icon: Setting,
    links: [
      { text: '管理员登录', path: '/admin/login' },
      { text: '管理中心', path: '/admin1' }
    ]
  }
])
</script>

<style scoped>
.sitemap-page {
  min-height: 100vh;
  background: #F9F8F4;
  /* 温暖的米色背景，更有质感 */
  display: flex;
  flex-direction: column;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

/* 页面标题区域 */
.page-header {
  background: linear-gradient(135deg, #FFFFFF 0%, #F0F0F0 100%);
  color: #1c1917;
  padding: 140px 0 80px;
  text-align: center;
  position: relative;
  overflow: hidden;
  border-bottom: 1px solid rgba(197, 160, 89, 0.1);
}

.bg-pattern {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: radial-gradient(#C5A059 1px, transparent 1px);
  background-size: 40px 40px;
  opacity: 0.05;
  pointer-events: none;
}

.header-content {
  position: relative;
  z-index: 1;
}

.page-header h1 {
  font-family: "Times New Roman", Times, serif;
  font-size: 48px;
  font-weight: normal;
  margin-bottom: 8px;
  color: #1c1917;
  letter-spacing: 1px;
}

.subtitle {
  font-size: 14px;
  letter-spacing: 4px;
  color: #C5A059;
  text-transform: uppercase;
  margin-bottom: 24px;
  font-weight: 600;
}

.intro-text p {
  font-size: 16px;
  line-height: 1.8;
  color: #57534e;
  font-weight: 300;
  max-width: 600px;
  margin: 0 auto;
}

/* 内容区域 */
.content-section {
  flex: 1;
  padding: 60px 0 100px;
}

.sitemap-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
  margin-bottom: 60px;
}

/* 卡片样式 */
.sitemap-card {
  background: #FFFFFF;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0, 0, 0, 0.02);
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  display: flex;
  flex-direction: column;
  animation: fadeUp 0.8s ease-out forwards;
  opacity: 0;
  transform: translateY(30px);
  position: relative;
  overflow: hidden;
}

.sitemap-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, #C5A059, #E6D5B8);
  opacity: 0;
  transition: opacity 0.4s ease;
}

.sitemap-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(197, 160, 89, 0.1);
}

.sitemap-card:hover::before {
  opacity: 1;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: rgba(197, 160, 89, 0.1);
  color: #C5A059;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.4s ease;
}

.sitemap-card:hover .icon-wrapper {
  background: #C5A059;
  color: #FFFFFF;
}

.card-header h2 {
  font-family: "Times New Roman", Times, serif;
  font-size: 22px;
  font-weight: 600;
  color: #1c1917;
  margin: 0;
}

.card-divider {
  height: 1px;
  background: rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
  width: 100%;
}

/* 链接列表 */
.sitemap-links {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sitemap-links li {
  margin-bottom: 12px;
}

.sitemap-links li:last-child {
  margin-bottom: 0;
}

.link-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  text-decoration: none;
  color: #57534e;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
  background: transparent;
}

.link-text {
  font-size: 15px;
  font-weight: 400;
}

.link-arrow {
  font-size: 14px;
  opacity: 0;
  transform: translateX(-10px);
  transition: all 0.3s ease;
  color: #C5A059;
}

.link-item:hover {
  background: rgba(197, 160, 89, 0.05);
  color: #C5A059;
  padding-left: 16px;
}

.link-item:hover .link-arrow {
  opacity: 1;
  transform: translateX(0);
}

/* 底部联系区域 */
.sitemap-footer {
  margin-top: 40px;
  background: #FFFFFF;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0, 0, 0, 0.02);
  text-align: center;
  animation: fadeUp 0.8s ease-out forwards;
  animation-delay: 0.6s;
  opacity: 0;
  transform: translateY(30px);
}

.footer-content h3 {
  font-family: "Times New Roman", Times, serif;
  font-size: 24px;
  color: #1c1917;
  margin-bottom: 12px;
}

.footer-content p {
  color: #57534e;
  margin-bottom: 30px;
  font-weight: 300;
}

.contact-info {
  display: flex;
  justify-content: center;
  gap: 40px;
  list-style: none;
  padding: 0;
  margin: 0;
  flex-wrap: wrap;
}

.contact-info li {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #1c1917;
  font-size: 15px;
  font-weight: 500;
}

.contact-info .el-icon {
  color: #C5A059;
  font-size: 18px;
}

/* 动画定义 */
@keyframes fadeUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .sitemap-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 100px 0 60px;
  }

  .page-header h1 {
    font-size: 36px;
  }

  .sitemap-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .contact-info {
    flex-direction: column;
    gap: 16px;
    align-items: center;
  }
}
</style>
