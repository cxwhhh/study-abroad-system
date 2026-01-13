<template>
  <footer class="footer">
    <!-- Apple-style footer content -->
    <div class="footer-content">
      <!-- Services Section -->
      <div class="footer-section">
        <h3>服务</h3>
        <ul class="footer-links">
          <li><a href="/study">留学申请</a></li>
          <li><a href="/visa-service">签证服务</a></li>
          <li><a href="/schools">院校推荐</a></li>
          <li><a href="/cases">成功案例</a></li>
        </ul>
      </div>

      <!-- Programs Section -->
      <div class="footer-section">
        <h3>项目</h3>
        <ul class="footer-links">
          <li><a href="#" @click.prevent="showNotAvailable">美国留学</a></li>
          <li><a href="/country/">英国留学</a></li>
          <li><a href="#" @click.prevent="showNotAvailable">加拿大留学</a></li>
          <li><a href="#" @click.prevent="showNotAvailable">澳洲留学</a></li>
        </ul>
      </div>

      <!-- Resources Section -->
      <div class="footer-section">
        <h3>资源</h3>
        <ul class="footer-links">
          <li><a href="/news">留学资讯</a></li>
          <li><a href="#" @click.prevent="showNotAvailable">申请流程</a></li>
          <li><a href="/profile">个人中心</a></li>
          <li><a href="/help">帮助中心</a></li>
        </ul>
      </div>

      <!-- Support Section -->
      <div class="footer-section">
        <h3>支持</h3>
        <ul class="footer-links">
          <li><a href="#" @click.prevent="showNotAvailable">联系我们</a></li>
          <li><a href="/terms">服务条款</a></li>
          <li><a href="/privacy">隐私政策</a></li>
          <li><a href="/sitemap">网站地图</a></li>
        </ul>
      </div>

      <!-- Company Section -->
      <div class="footer-section">
        <h3>新英美</h3>
        <ul class="footer-links">
          <li><a href="/about">关于我们</a></li>
          <li><a href="/teacher-consultation">老师团队</a></li>
          <li><a href="/news">新闻动态</a></li>
          <li><a href="#" @click.prevent="showNotAvailable">联系方式</a></li>
        </ul>
      </div>
    </div>

    <!-- Bottom section -->
    <div class="footer-bottom">
      <div class="footer-bottom-content">
        <div class="footer-info">
          <p>更多方式来了解新英美留学</p>
          <div class="contact-methods">
            <span>咨询热线：400-151-7451</span>
            <span>邮箱：xinyingmei@study.com</span>
            <span>工作时间：周一至周五 9:30-18:30</span>
          </div>
        </div>
        <div class="footer-legal">
          <p>Copyright © 2025 新英美留学服务. 保留所有权利.</p>
          <div class="legal-links">
            <a href="/privacy">隐私政策</a>
            <a href="/terms">使用条款</a>
            <a href="/sitemap">网站地图</a>
          </div>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { Location, Phone, Message } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 显示功能暂未开放的提示
const showNotAvailable = () => {
  ElMessage({
    message: '功能暂未开放',
    type: 'info',
    duration: 2000
  })
}
</script>

<style scoped>
/* Apple-style Footer - Dark Theme */
.footer {
  background-color: #1a1918;
  /* Dark brown/black to match image */
  color: #f5f5f7;
  position: relative;
  margin-top: 0;
  padding: 0;
  border-top: 1px solid #333;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 60px 22px 60px;
  /* Increased padding */
  border-bottom: 1px solid #2c2c2e;
  gap: 80px;
  /* More space between logo and links */
}

.footer-logo-section {
  flex-shrink: 0;
  max-width: 300px;
}

.nav-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  margin-bottom: 16px;
}

.logo-circle {
  width: 36px;
  height: 36px;
  background-color: #C5A059;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 20px;
  font-family: serif;
}

.logo-text {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.logo-main {
  font-weight: bold;
  font-size: 20px;
  color: #f5f5f7;
  /* White for dark theme */
  letter-spacing: 0.05em;
  font-family: serif;
}

.logo-year {
  color: #86868b;
  /* Gray for dark theme */
  font-size: 14px;
}

.footer-links-container {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 40px;
  flex-grow: 1;
  justify-content: flex-end;
}

.footer-section {
  padding: 0;
  display: flex;
  flex-direction: column;
}

.footer-section h3 {
  font-size: 14px;
  font-weight: 600;
  color: #C5A059;
  /* Gold color for headings */
  margin: 0 0 16px 0;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  line-height: 1.5;
  font-family: 'Times New Roman', serif;
  /* Serif font */
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-links li {
  margin: 0 0 12px 0;
}

.footer-links a {
  font-size: 13px;
  line-height: 1.5;
  font-weight: 400;
  color: #a1a1a6;
  /* Light gray text */
  text-decoration: none;
  transition: color 0.2s ease-in-out;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.footer-links a:hover {
  color: #fff;
  text-decoration: none;
}

/* Bottom section */
.footer-bottom {
  background-color: #1a1918;
  padding: 24px 0 32px;
}

.footer-bottom-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 22px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.footer-info {
  flex: 1;
  min-width: 300px;
}

.footer-info p {
  font-size: 12px;
  line-height: 1.5;
  font-weight: 400;
  color: #6e6e73;
  margin: 0 0 8px 0;
}

.contact-methods {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.contact-methods span {
  font-size: 12px;
  line-height: 1.5;
  font-weight: 400;
  color: #6e6e73;
  white-space: nowrap;
}

.footer-legal {
  text-align: right;
  flex-shrink: 0;
}

.footer-legal p {
  font-size: 12px;
  line-height: 1.5;
  font-weight: 400;
  color: #6e6e73;
  margin: 0 0 8px 0;
}

.legal-links {
  display: flex;
  gap: 20px;
  justify-content: flex-end;
}

.legal-links a {
  font-size: 12px;
  line-height: 1.5;
  font-weight: 400;
  color: #6e6e73;
  text-decoration: none;
  transition: color 0.2s ease-in-out;
}

.legal-links a:hover {
  color: #f5f5f7;
  text-decoration: none;
}

/* Responsive design */
@media (max-width: 1024px) {
  .footer-content {
    flex-direction: column;
    gap: 40px;
    padding: 40px 22px;
  }

  .footer-links-container {
    width: 100%;
    grid-template-columns: repeat(3, 1fr);
    gap: 30px;
    justify-content: flex-start;
  }

  .footer-section:nth-child(4),
  .footer-section:nth-child(5) {
    grid-column: span 1;
  }
}

@media (max-width: 768px) {
  .footer-links-container {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .footer-links-container {
    grid-template-columns: 1fr;
  }

  .footer-bottom-content {
    flex-direction: column;
    align-items: flex-start;
  }

  .footer-legal {
    text-align: left;
  }

  .legal-links {
    justify-content: flex-start;
  }

  .contact-methods {
    flex-direction: column;
    gap: 8px;
  }
}
</style>