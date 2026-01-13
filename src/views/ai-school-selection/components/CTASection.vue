<template>
  <section class="cta-section">
    <div class="cta-background">
      <div class="gradient-overlay"></div>
      <div class="floating-elements">
        <div class="element element-1"></div>
        <div class="element element-2"></div>
        <div class="element element-3"></div>
        <div class="element element-4"></div>
      </div>
    </div>

    <div class="container">
      <div class="cta-content">
        <div class="cta-header">
          <h2>准备开始您的智能选校之旅吗？</h2>
          <p>只需3分钟，AI为您精准匹配全球顶尖院校，让留学申请更加科学高效</p>
        </div>

        <div class="cta-features">
          <div class="feature-item">
            <div class="feature-icon">
              <el-icon>
                <Timer />
              </el-icon>
            </div>
            <div class="feature-text">
              <h4>3分钟快速匹配</h4>
              <p>简单填写信息即可获得专业建议</p>
            </div>
          </div>

          <div class="feature-item">
            <div class="feature-icon">
              <el-icon>
                <Lock />
              </el-icon>
            </div>
            <div class="feature-text">
              <h4>数据安全保护</h4>
              <p>严格保护您的个人隐私信息</p>
            </div>
          </div>

          <div class="feature-item">
            <div class="feature-icon">
              <el-icon>
                <Star />
              </el-icon>
            </div>
            <div class="feature-text">
              <h4>95%匹配精度</h4>
              <p>基于AI算法的精准推荐</p>
            </div>
          </div>

          <div class="feature-item">
            <div class="feature-icon">
              <el-icon>
                <Trophy />
              </el-icon>
            </div>
            <div class="feature-text">
              <h4>50万+成功案例</h4>
              <p>丰富经验助力您的申请</p>
            </div>
          </div>
        </div>

        <div class="cta-actions">
          <el-button type="primary" size="large" class="primary-cta" @click="startMatching">
            <el-icon>
              <Connection />
            </el-icon>
            立即开始智能匹配
          </el-button>

          <el-button size="large" class="secondary-cta" @click="learnMore">
            <el-icon>
              <Document />
            </el-icon>
            了解更多详情
          </el-button>
        </div>

        <div class="cta-stats">
          <div class="stat-item">
            <div class="stat-icon">👥</div>
            <div class="stat-content">
              <div class="stat-number">50万+</div>
              <div class="stat-label">用户信赖</div>
            </div>
          </div>

          <div class="stat-item">
            <div class="stat-icon">⭐</div>
            <div class="stat-content">
              <div class="stat-number">4.9分</div>
              <div class="stat-label">用户评分</div>
            </div>
          </div>

          <div class="stat-item">
            <div class="stat-icon">🏆</div>
            <div class="stat-content">
              <div class="stat-number">95%</div>
              <div class="stat-label">成功率</div>
            </div>
          </div>

          <div class="stat-item">
            <div class="stat-icon">🌍</div>
            <div class="stat-content">
              <div class="stat-number">50+</div>
              <div class="stat-label">国家覆盖</div>
            </div>
          </div>
        </div>

        <div class="trust-indicators">
          <div class="trust-item">
            <el-icon>
              <Lock />
            </el-icon>
            <span>SSL安全加密</span>
          </div>
          <div class="trust-item">
            <el-icon>
              <Checked />
            </el-icon>
            <span>权威认证</span>
          </div>
          <div class="trust-item">
            <el-icon>
              <Clock />
            </el-icon>
            <span>24/7客服支持</span>
          </div>
          <div class="trust-item">
            <el-icon>
              <Money />
            </el-icon>
            <span>免费使用</span>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import {
  Timer,
  Star,
  Trophy,
  Connection,
  Document,
  Lock,
  Checked,
  Clock,
  Money
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()

const startMatching = () => {
  ElMessage.success('正在启动AI智能匹配系统...')
  setTimeout(() => {
    router.push('/matching')
  }, 1500)
}

const learnMore = () => {
  // 滚动到技术展示部分
  const technologySection = document.querySelector('.technology-section')
  if (technologySection) {
    technologySection.scrollIntoView({
      behavior: 'smooth',
      block: 'start'
    })
    ElMessage.success('为您展示更多技术详情')
  } else {
    // 如果找不到技术部分，则跳转到页面顶部
    window.scrollTo({ top: 0, behavior: 'smooth' })
    ElMessage.info('查看页面详细信息')
  }
}
</script>

<style scoped>
.cta-section {
  background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
  color: white;
  position: relative;
  overflow: hidden;
  padding: 40px 0;
}

.cta-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.gradient-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background:
    radial-gradient(circle at 20% 20%, rgba(44, 62, 80, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(52, 73, 94, 0.05) 0%, transparent 50%);
}

.floating-elements {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.element {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float-element 8s ease-in-out infinite;
}

.element-1 {
  width: 120px;
  height: 120px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.element-2 {
  width: 80px;
  height: 80px;
  top: 20%;
  right: 15%;
  animation-delay: 2s;
}

.element-3 {
  width: 100px;
  height: 100px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

.element-4 {
  width: 60px;
  height: 60px;
  bottom: 30%;
  right: 25%;
  animation-delay: 6s;
}

@keyframes float-element {

  0%,
  100% {
    transform: translateY(0px) rotate(0deg);
  }

  50% {
    transform: translateY(-30px) rotate(180deg);
  }
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  position: relative;
  z-index: 1;
}

.cta-content {
  text-align: center;
  max-width: 900px;
  margin: 0 auto;
}

.cta-header {
  margin-bottom: 32px;
}

.cta-header h2 {
  font-size: 48px;
  font-weight: 700;
  margin: 0 0 24px;
  line-height: 1.2;
}

.cta-header p {
  font-size: 20px;
  line-height: 1.6;
  opacity: 0.9;
  margin: 0;
}

.cta-features {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 32px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  text-align: left;
}

.feature-icon {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}

.feature-text h4 {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 4px;
  color: white;
}

.feature-text p {
  font-size: 14px;
  margin: 0;
  opacity: 0.8;
}

.cta-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-bottom: 32px;
}

.primary-cta,
.secondary-cta {
  padding: 18px 32px;
  font-size: 18px;
  font-weight: 600;
  border-radius: 12px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.primary-cta {
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
  color: #1a202c;
  border: none;
}

.primary-cta:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 30px rgba(255, 215, 0, 0.4);
}

.secondary-cta {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.secondary-cta:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-3px);
}

.cta-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
  padding: 24px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.stat-icon {
  font-size: 32px;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
}

.stat-content {
  text-align: center;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: white;
  margin-bottom: 4px;
  display: block;
}

.stat-label {
  font-size: 14px;
  opacity: 0.8;
}

.trust-indicators {
  display: flex;
  justify-content: center;
  gap: 32px;
  flex-wrap: wrap;
}

.trust-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  opacity: 0.9;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .cta-features {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .cta-stats {
    grid-template-columns: repeat(2, 1fr);
    gap: 24px;
  }
}

@media (max-width: 768px) {
  .cta-header h2 {
    font-size: 36px;
  }

  .cta-header p {
    font-size: 18px;
  }

  .cta-actions {
    flex-direction: column;
    align-items: center;
  }

  .primary-cta,
  .secondary-cta {
    width: 100%;
    max-width: 300px;
  }

  .cta-stats {
    grid-template-columns: 1fr;
    gap: 16px;
    padding: 24px;
  }

  .trust-indicators {
    gap: 16px;
  }

  .trust-item {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .cta-section {
    padding: 32px 0;
  }

  .cta-header h2 {
    font-size: 28px;
  }

  .feature-item {
    padding: 12px;
  }

  .feature-icon {
    width: 40px;
    height: 40px;
    font-size: 20px;
  }
}
</style>
