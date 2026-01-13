<template>
  <section class="hero-section">
    <div class="hero-background">
      <div class="gradient-sphere sphere-1"></div>
      <div class="gradient-sphere sphere-2"></div>
      <div class="gradient-sphere sphere-3"></div>
    </div>
    <div class="container">
      <div class="hero-content">
        <span class="badge">智能科技</span>
        <h1>AI智能选校</h1>
        <p class="subtitle">人工智能驱动的精准匹配</p>
        <p class="description">
          我们的智能算法基于<span class="highlight">深度学习技术</span>，分析全球<span class="highlight">10,000+</span>院校数据，
          为您提供<span class="highlight">95%</span>准确率的院校匹配，让留学选择更加科学、精准。
        </p>
        <div class="accuracy-indicator">
          <div class="accuracy-bar">
            <div class="accuracy-fill"></div>
          </div>
          <div class="accuracy-label">95% 匹配精度</div>
        </div>
        <div class="hero-buttons">
          <el-button type="primary" class="primary-button" @click="startMatching">
            <el-icon>
              <Connection />
            </el-icon>
            开始智能匹配
          </el-button>
          <el-button class="secondary-button" @click="showTechDetails">
            <el-icon>
              <DataAnalysis />
            </el-icon>
            了解技术原理
          </el-button>
        </div>
      </div>

      <div class="hero-visual">
        <div class="device-mockup">
          <div class="device-frame">
            <div class="device-screen">
              <div class="screen-content">
                <div class="ai-animation">
                  <div class="ai-particles"></div>
                  <div class="ai-brain">
                    <div class="brain-center"></div>
                    <div class="brain-connections"></div>
                  </div>
                  <div class="matching-visualization">
                    <div class="student-profile"></div>
                    <div class="matching-lines">
                      <div class="match-line l1"></div>
                      <div class="match-line l2"></div>
                      <div class="match-line l3"></div>
                    </div>
                    <div class="university-results">
                      <div class="result-item r1"></div>
                      <div class="result-item r2"></div>
                      <div class="result-item r3"></div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="floating-stats">
            <div class="stat-card stat-1">
              <div class="stat-value">95<span>%</span></div>
              <div class="stat-label">匹配精度</div>
            </div>
            <div class="stat-card stat-2">
              <div class="stat-value">10k<span>+</span></div>
              <div class="stat-label">全球院校</div>
            </div>
            <div class="stat-card stat-3">
              <div class="stat-value">3<span>×</span></div>
              <div class="stat-label">录取率提升</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { Connection, DataAnalysis } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()

const startMatching = () => {
  ElMessage.success('正在启动AI智能匹配系统...')
  setTimeout(() => {
    router.push('/matching')
  }, 1500)
}

const showTechDetails = () => {
  // 滚动到技术展示部分
  const technologySection = document.querySelector('.technology-section')
  if (technologySection) {
    technologySection.scrollIntoView({
      behavior: 'smooth',
      block: 'start'
    })
    ElMessage.success('为您展示AI技术原理')
  } else {
    ElMessage.info('技术详情请查看下方内容')
  }
}
</script>

<style scoped>
/* Hero Section */
.hero-section {
  background: linear-gradient(135deg, #2c3e50 0%, #34495e 50%, #2c3e50 100%);
  color: #ecf0f1;
  position: relative;
  overflow: hidden;
  padding: 40px 0 60px 0;
}

.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.gradient-sphere {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.sphere-1 {
  width: 600px;
  height: 600px;
  background: linear-gradient(135deg, rgba(52, 152, 219, 0.15) 0%, rgba(155, 89, 182, 0.08) 100%);
  top: -200px;
  left: -100px;
  animation: float-slow 20s infinite alternate ease-in-out;
}

.sphere-2 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, rgba(155, 89, 182, 0.08) 0%, rgba(52, 152, 219, 0.05) 100%);
  bottom: -100px;
  right: -50px;
  animation: float-slow 15s infinite alternate-reverse ease-in-out;
}

.sphere-3 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, rgba(52, 152, 219, 0.08) 0%, rgba(155, 89, 182, 0.05) 100%);
  top: 40%;
  right: 20%;
  animation: float-slow 18s infinite alternate ease-in-out;
}

@keyframes float-slow {
  0% {
    transform: translateY(0px) rotate(0deg);
  }

  100% {
    transform: translateY(-20px) rotate(10deg);
  }
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: center;
  position: relative;
  z-index: 1;
}

.hero-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.badge {
  display: inline-block;
  padding: 8px 16px;
  background: rgba(52, 152, 219, 0.1);
  color: #3498db;
  border: 1px solid rgba(52, 152, 219, 0.3);
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  backdrop-filter: blur(10px);
  width: fit-content;
}

.hero-content h1 {
  font-size: 48px;
  font-weight: 700;
  margin: 0;
  line-height: 1.1;
}

.subtitle {
  font-size: 24px;
  margin: 0;
  opacity: 0.9;
}

.description {
  font-size: 18px;
  line-height: 1.6;
  margin: 0;
  opacity: 0.8;
}

.highlight {
  color: #3498db;
  background: linear-gradient(135deg, rgba(52, 152, 219, 0.1), rgba(155, 89, 182, 0.05));
  padding: 2px 8px;
  border-radius: 6px;
  font-weight: 700;
}

.accuracy-indicator {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.accuracy-bar {
  width: 200px;
  height: 6px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
  overflow: hidden;
}

.accuracy-fill {
  width: 95%;
  height: 100%;
  background: linear-gradient(90deg, #ffd700, #ffed4e);
  border-radius: 3px;
  animation: fill-bar 2s ease-out;
}

@keyframes fill-bar {
  from {
    width: 0;
  }
}

.accuracy-label {
  font-size: 14px;
  opacity: 0.8;
}

.hero-buttons {
  display: flex;
  gap: 16px;
}

.hero-buttons .el-button {
  padding: 16px 24px;
  font-size: 16px;
  border-radius: 12px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.primary-button {
  background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
  color: #ffffff;
  border: 2px solid rgba(52, 152, 219, 0.3);
  box-shadow: 0 8px 32px rgba(52, 152, 219, 0.2);
  backdrop-filter: blur(10px);
}

.primary-button:hover {
  background: linear-gradient(135deg, #2980b9 0%, #1f5f8b 100%);
  transform: translateY(-2px);
  box-shadow: 0 12px 40px rgba(52, 152, 219, 0.3);
  border-color: rgba(52, 152, 219, 0.5);
}

.secondary-button {
  background: rgba(236, 240, 241, 0.1);
  color: #ecf0f1;
  border: 2px solid rgba(236, 240, 241, 0.3);
  backdrop-filter: blur(10px);
}

.secondary-button:hover {
  background: rgba(236, 240, 241, 0.15);
  border-color: rgba(236, 240, 241, 0.4);
  transform: translateY(-2px);
}

/* Hero Visual */
.hero-visual {
  position: relative;
}

.device-mockup {
  position: relative;
  width: 100%;
  max-width: 400px;
  margin: 0 auto;
}

.device-frame {
  background: linear-gradient(135deg, rgba(236, 240, 241, 0.1), rgba(189, 195, 199, 0.05));
  border: 2px solid rgba(189, 195, 199, 0.2);
  border-radius: 20px;
  padding: 20px;
  backdrop-filter: blur(15px);
  box-shadow: 0 20px 60px rgba(44, 62, 80, 0.2);
}

.device-screen {
  background: linear-gradient(135deg, rgba(236, 240, 241, 0.08), rgba(189, 195, 199, 0.03));
  border-radius: 12px;
  padding: 16px;
  border: 1px solid rgba(189, 195, 199, 0.15);
  min-height: 300px;
  position: relative;
  overflow: hidden;
}

.screen-content {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ai-animation {
  position: relative;
  width: 200px;
  height: 200px;
}

.ai-brain {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #ffffff;
  box-shadow: 0 8px 32px rgba(52, 152, 219, 0.3), inset 0 2px 8px rgba(52, 152, 219, 0.2);
  animation: brain-glow 3s ease-in-out infinite;
}

@keyframes pulse {

  0%,
  100% {
    transform: translate(-50%, -50%) scale(1);
  }

  50% {
    transform: translate(-50%, -50%) scale(1.1);
  }
}

.floating-stats {
  position: absolute;
  top: -20px;
  right: -20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.stat-card {
  background: rgba(236, 240, 241, 0.08);
  border: 1px solid rgba(189, 195, 199, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  text-align: center;
  box-shadow: 0 8px 32px rgba(44, 62, 80, 0.1);
  transition: all 0.3s ease;
  animation: float-card 3s ease-in-out infinite;
}

.stat-card:nth-child(2) {
  animation-delay: 1s;
}

.stat-card:nth-child(3) {
  animation-delay: 2s;
}

@keyframes float-card {

  0%,
  100% {
    transform: translateY(0);
  }

  50% {
    transform: translateY(-10px);
  }
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #ecf0f1;
  margin-bottom: 8px;
}

.stat-value span {
  color: #3498db;
  font-size: 24px;
}

.stat-label {
  font-size: 14px;
  color: #95a5a6;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .container {
    grid-template-columns: 1fr;
    gap: 40px;
    text-align: center;
  }

  .hero-content h1 {
    font-size: 36px;
  }

  .subtitle {
    font-size: 20px;
  }

  .hero-buttons {
    justify-content: center;
  }
}
</style>
