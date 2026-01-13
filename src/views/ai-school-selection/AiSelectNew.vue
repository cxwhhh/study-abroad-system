<template>
  <div class="ai-select-page">
    <NavBar />
    <!-- 浮动粒子 -->
    <div class="floating-particles">
      <div class="particle" v-for="n in 20" :key="n" :style="getParticleStyle(n)"></div>
    </div>
    
    <!-- 网格背景 -->
    <div class="grid-background"></div>
    
    <!-- 光晕效果 -->
    <div class="glow-effects">
      <div class="glow glow-1"></div>
      <div class="glow glow-2"></div>
      <div class="glow glow-3"></div>
    </div>

    <main>
      <!-- Hero Section -->
      <section class="hero-section">
        <video class="hero-video" autoplay muted loop playsinline>
          <source src="/videos/hero-bg2.mp4" type="video/mp4">
        </video>
        <div class="hero-overlay"></div>
        <div class="hero-background">
          <div class="hero-gradient"></div>
          <div class="hero-particles"></div>
        </div>
        <div class="hero-content">
          <div class="hero-badge">
            <svg class="badge-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/>
            </svg>
            <span>AI智能选校系统</span>
          </div>
          <h1 class="hero-title">
            <span class="title-highlight">智能匹配</span>
            <br>你的理想院校
          </h1>
          <p class="hero-description">
            基于大数据分析和AI算法，为你精准推荐最适合的海外院校
            <br>让留学申请更科学、更高效、更成功
          </p>
          <div class="hero-actions">
            <button class="btn-primary">
              <span>开始智能选校</span>
              <svg class="btn-icon" viewBox="0 0 24 24" fill="none">
                <path d="M5 12h14m-7-7l7 7-7 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
            <button class="btn-secondary">
              <svg class="btn-icon" viewBox="0 0 24 24" fill="none">
                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                <polygon points="10,8 16,12 10,16" fill="currentColor"/>
              </svg>
              <span>观看演示</span>
            </button>
          </div>
          <div class="hero-stats">
            <div class="stat-item">
              <div class="stat-number">50,000+</div>
              <div class="stat-label">成功案例</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">98%</div>
              <div class="stat-label">匹配准确率</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">1000+</div>
              <div class="stat-label">合作院校</div>
            </div>
          </div>
        </div>
      </section>

      <!-- Features Section -->
      <section class="features-section">
        <div class="container">
          <div class="section-header">
            <h2 class="section-title">为什么选择我们的AI选校系统</h2>
            <p class="section-subtitle">先进的技术，专业的服务，让你的留学之路更加顺畅</p>
          </div>
          <div class="features-grid">
            <div class="feature-card">
              <div class="feature-icon">
                <svg viewBox="0 0 24 24" fill="none">
                  <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <h3 class="feature-title">大数据分析</h3>
              <p class="feature-description">整合全球院校数据，深度分析录取趋势和要求变化</p>
            </div>
            <div class="feature-card">
              <div class="feature-icon">
                <svg viewBox="0 0 24 24" fill="none">
                  <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
                  <path d="M12 1v6m0 6v6m11-7h-6m-6 0H1" stroke="currentColor" stroke-width="2"/>
                </svg>
              </div>
              <h3 class="feature-title">精准匹配</h3>
              <p class="feature-description">AI算法分析个人背景，匹配最适合的院校和专业</p>
            </div>
            <div class="feature-card">
              <div class="feature-icon">
                <svg viewBox="0 0 24 24" fill="none">
                  <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <h3 class="feature-title">实时更新</h3>
              <p class="feature-description">院校信息实时同步，确保推荐结果的准确性和时效性</p>
            </div>
            <div class="feature-card">
              <div class="feature-icon">
                <svg viewBox="0 0 24 24" fill="none">
                  <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <h3 class="feature-title">专业指导</h3>
              <p class="feature-description">资深顾问团队提供一对一专业指导和申请建议</p>
            </div>
          </div>
        </div>
      </section>

      <!-- Process Section -->
      <section class="process-section">
        <div class="container">
          <div class="section-header">
            <h2 class="section-title">智能选校流程</h2>
            <p class="section-subtitle">简单四步，找到你的理想院校</p>
          </div>
          <div class="process-timeline">
            <div class="process-step">
              <div class="step-number">01</div>
              <div class="step-content">
                <h3 class="step-title">填写个人信息</h3>
                <p class="step-description">详细填写学术背景、语言成绩、兴趣爱好等信息</p>
              </div>
            </div>
            <div class="process-step">
              <div class="step-number">02</div>
              <div class="step-content">
                <h3 class="step-title">AI智能分析</h3>
                <p class="step-description">系统分析你的条件，匹配最适合的院校和专业</p>
              </div>
            </div>
            <div class="process-step">
              <div class="step-number">03</div>
              <div class="step-content">
                <h3 class="step-title">获取推荐结果</h3>
                <p class="step-description">查看详细的院校推荐报告和申请建议</p>
              </div>
            </div>
            <div class="process-step">
              <div class="step-number">04</div>
              <div class="step-content">
                <h3 class="step-title">专业指导</h3>
                <p class="step-description">获得专业顾问的一对一申请指导服务</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Data Statistics Section -->
      <section class="stats-section">
        <div class="container">
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-icon">🎓</div>
              <div class="stat-value">50,000+</div>
              <div class="stat-label">成功录取学生</div>
              <div class="stat-trend">+15% 今年增长</div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">🏫</div>
              <div class="stat-value">1,200+</div>
              <div class="stat-label">合作院校</div>
              <div class="stat-trend">覆盖30+国家</div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">⭐</div>
              <div class="stat-value">98.5%</div>
              <div class="stat-label">匹配准确率</div>
              <div class="stat-trend">AI算法优化</div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">💰</div>
              <div class="stat-value">$2.5M+</div>
              <div class="stat-label">奖学金获得</div>
              <div class="stat-trend">累计金额</div>
            </div>
          </div>
        </div>
      </section>

      <!-- Success Stories Section -->
      <section class="success-section">
        <div class="container">
          <div class="section-header">
            <h2 class="section-title">成功案例</h2>
            <p class="section-subtitle">看看其他学生是如何通过我们的AI系统实现留学梦想的</p>
            <div class="section-actions">
              <router-link to="/cases" class="btn-view-more">
                查看更多案例
                <i class="arrow-icon">→</i>
              </router-link>
            </div>
          </div>
          <div class="success-grid">
            <div class="success-card">
              <div class="success-avatar">
                <img src="/images/avatar1.jpg" alt="学生头像" />
              </div>
              <div class="success-content">
                <h4 class="success-name">张同学</h4>
                <div class="success-school">哈佛大学 · 计算机科学</div>
                <p class="success-quote">"AI系统精准匹配了我的背景，帮我找到了最适合的专业方向，最终成功录取哈佛！"</p>
                <div class="success-tags">
                  <span class="tag">GPA 3.8</span>
                  <span class="tag">TOEFL 110</span>
                  <span class="tag">全额奖学金</span>
                </div>
              </div>
            </div>
            <div class="success-card">
              <div class="success-avatar">
                <img src="/images/avatar2.jpg" alt="学生头像" />
              </div>
              <div class="success-content">
                <h4 class="success-name">李同学</h4>
                <div class="success-school">牛津大学 · 金融学</div>
                <p class="success-quote">"系统分析了我的兴趣和能力，推荐的学校完全符合我的期望，申请过程非常顺利。"</p>
                <div class="success-tags">
                  <span class="tag">GPA 3.9</span>
                  <span class="tag">IELTS 8.0</span>
                  <span class="tag">早录取</span>
                </div>
              </div>
            </div>
            <div class="success-card">
              <div class="success-avatar">
                <img src="/images/avatar3.jpg" alt="学生头像" />
              </div>
              <div class="success-content">
                <h4 class="success-name">王同学</h4>
                <div class="success-school">斯坦福大学 · 人工智能</div>
                <p class="success-quote">"通过AI分析发现了我之前没有考虑过的专业，现在在斯坦福学习AI，非常感谢！"</p>
                <div class="success-tags">
                  <span class="tag">GPA 3.7</span>
                  <span class="tag">GRE 330</span>
                  <span class="tag">研究助理</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Partners Section -->
      <section class="partners-section">
        <div class="container">
          <div class="section-header">
            <h2 class="section-title">合作伙伴</h2>
            <p class="section-subtitle">与全球顶尖院校建立深度合作关系</p>
          </div>
          <div class="partners-grid">
            <div class="partner-category">
              <h3 class="category-title">美国顶尖大学</h3>
              <div class="partner-logos">
                <div class="partner-logo">Harvard</div>
                <div class="partner-logo">MIT</div>
                <div class="partner-logo">Stanford</div>
                <div class="partner-logo">Yale</div>
              </div>
            </div>
            <div class="partner-category">
              <h3 class="category-title">英国知名院校</h3>
              <div class="partner-logos">
                <div class="partner-logo">Oxford</div>
                <div class="partner-logo">Cambridge</div>
                <div class="partner-logo">Imperial</div>
                <div class="partner-logo">UCL</div>
              </div>
            </div>
            <div class="partner-category">
              <h3 class="category-title">其他国家</h3>
              <div class="partner-logos">
                <div class="partner-logo">Toronto</div>
                <div class="partner-logo">Melbourne</div>
                <div class="partner-logo">ETH</div>
                <div class="partner-logo">NUS</div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Advanced Features Section -->
      <section class="advanced-features-section">
        <div class="container">
          <div class="section-header">
            <h2 class="section-title">高级功能</h2>
            <p class="section-subtitle">更多智能工具，助力你的留学申请</p>
          </div>
          <div class="advanced-grid">
            <div class="advanced-card">
              <div class="advanced-icon">📊</div>
              <h3 class="advanced-title">申请成功率预测</h3>
              <p class="advanced-description">基于历史数据和AI算法，预测你在目标院校的录取概率</p>
              <div class="advanced-features">
                <span class="feature-tag">实时分析</span>
                <span class="feature-tag">概率计算</span>
                <span class="feature-tag">风险评估</span>
              </div>
            </div>
            <div class="advanced-card">
              <div class="advanced-icon">📝</div>
              <h3 class="advanced-title">个人陈述优化</h3>
              <p class="advanced-description">AI辅助优化个人陈述，提升申请材料质量和竞争力</p>
              <div class="advanced-features">
                <span class="feature-tag">语言优化</span>
                <span class="feature-tag">结构调整</span>
                <span class="feature-tag">亮点突出</span>
              </div>
            </div>
            <div class="advanced-card">
              <div class="advanced-icon">💼</div>
              <h3 class="advanced-title">职业规划建议</h3>
              <p class="advanced-description">结合专业特点和市场需求，提供个性化职业发展建议</p>
              <div class="advanced-features">
                <span class="feature-tag">行业分析</span>
                <span class="feature-tag">薪资预测</span>
                <span class="feature-tag">发展路径</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- CTA Section -->
      <section class="cta-section">
        <div class="container">
          <div class="cta-content">
            <h2 class="cta-title">开启你的留学之旅</h2>
            <p class="cta-description">立即体验AI智能选校，找到最适合你的理想院校</p>
            <div class="cta-buttons">
              <button class="cta-button primary">
                <span>免费开始选校</span>
                <svg class="btn-icon" viewBox="0 0 24 24" fill="none">
                  <path d="M5 12h14m-7-7l7 7-7 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>
              <router-link to="/teacher-consultation" class="cta-button secondary">
                <span>预约专家咨询</span>
                <svg class="btn-icon" viewBox="0 0 24 24" fill="none">
                  <path d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </router-link>
            </div>
            <div class="cta-guarantee">
              <span class="guarantee-icon">🛡️</span>
              <span>30天满意保证 · 不满意全额退款</span>
            </div>
          </div>
        </div>
      </section>
    </main>

    <FooterBar />
  </div>
</template>

<script>
import NavBar from '../../components/NavBar.vue'
import FooterBar from '../../components/FooterBar.vue'

export default {
  name: 'AiSelectNew',
  components: {
    NavBar,
    FooterBar
  },
  methods: {
    getParticleStyle(index) {
      const size = Math.random() * 4 + 2;
      const delay = Math.random() * 5;
      const duration = Math.random() * 10 + 10;
      const left = Math.random() * 100;
      const opacity = Math.random() * 0.5 + 0.1;
      
      return {
        width: `${size}px`,
        height: `${size}px`,
        left: `${left}%`,
        animationDelay: `${delay}s`,
        animationDuration: `${duration}s`,
        opacity: opacity
      };
    }
  }
}
</script>

<style scoped>
/* 全局变量 - 黑白主题 */
:root {
  --primary-color: #000000;
  --primary-hover: #333333;
  --secondary-color: #666666;
  --dark-color: #000000;
  --light-color: #ffffff;
  --gray-color: #888888;
  --accent-color: #f0f0f0;
  --border-color: #e0e0e0;
  --shadow-color: rgba(0, 0, 0, 0.1);
  --border-radius: 12px;
  --transition-speed: 0.3s;
}



/* 浮动粒子 */
.floating-particles {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
  overflow: hidden;
}

.particle {
  position: absolute;
  background: var(--primary-color);
  border-radius: 50%;
  animation: particle-float linear infinite;
}

@keyframes particle-float {
  0% {
    transform: translateY(100vh) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100px) rotate(360deg);
    opacity: 0;
  }
}

/* 网格背景 */
.grid-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  opacity: 0.03;
  background-image: 
    linear-gradient(var(--primary-color) 1px, transparent 1px),
    linear-gradient(90deg, var(--primary-color) 1px, transparent 1px);
  background-size: 50px 50px;
  animation: grid-move 20s linear infinite;
}

@keyframes grid-move {
  0% { transform: translate(0, 0); }
  100% { transform: translate(50px, 50px); }
}

/* 光晕效果 */
.glow-effects {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(40px);
  opacity: 0.1;
}

.glow-1 {
  width: 300px;
  height: 300px;
  background: var(--primary-color);
  top: 20%;
  left: 10%;
  animation: glow-pulse-1 8s ease-in-out infinite;
}

.glow-2 {
  width: 200px;
  height: 200px;
  background: var(--primary-color);
  top: 60%;
  right: 15%;
  animation: glow-pulse-2 6s ease-in-out infinite;
}

.glow-3 {
  width: 250px;
  height: 250px;
  background: var(--primary-color);
  bottom: 10%;
  left: 50%;
  transform: translateX(-50%);
  animation: glow-pulse-3 10s ease-in-out infinite;
}

@keyframes glow-pulse-1 {
  0%, 100% { transform: scale(1) translateY(0); opacity: 0.1; }
  50% { transform: scale(1.2) translateY(-20px); opacity: 0.2; }
}

@keyframes glow-pulse-2 {
  0%, 100% { transform: scale(1) translateX(0); opacity: 0.1; }
  50% { transform: scale(1.3) translateX(20px); opacity: 0.15; }
}

@keyframes glow-pulse-3 {
  0%, 100% { transform: translateX(-50%) scale(1); opacity: 0.1; }
  50% { transform: translateX(-50%) scale(1.1); opacity: 0.2; }
}

/* 页面容器 */
.ai-select-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  overflow-x: hidden;
  position: relative;
}

.ai-select-page::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at 20% 80%, rgba(0, 0, 0, 0.05) 0%, transparent 50%),
              radial-gradient(circle at 80% 20%, rgba(0, 0, 0, 0.05) 0%, transparent 50%);
  pointer-events: none;
  z-index: 1;
}

main {
  padding-top: 44px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

/* Hero Section */
.hero-section {
  position: relative;
  min-height: 35vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 30%, #e2e8f0 100%);
}

.hero-video {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 0;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.1);
  z-index: 1;
}

.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
}

.hero-gradient {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 50% 80%, rgba(0, 0, 0, 0.03) 0%, transparent 60%);
}

.hero-particles {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    radial-gradient(circle at 20% 80%, rgba(0, 0, 0, 0.02) 0%, transparent 40%),
    radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.05) 0%, transparent 40%),
    radial-gradient(circle at 40% 40%, rgba(0, 0, 0, 0.01) 0%, transparent 30%);
  animation: float 20s ease-in-out infinite;
}

.hero-particles::before {
  content: '';
  position: absolute;
  top: 10%;
  left: 10%;
  width: 200px;
  height: 200px;
  border: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 50%;
  animation: rotate 30s linear infinite;
}

.hero-particles::after {
  content: '';
  position: absolute;
  bottom: 20%;
  right: 15%;
  width: 150px;
  height: 150px;
  border: 1px solid rgba(0, 0, 0, 0.03);
  transform: rotate(45deg);
  animation: pulse 4s ease-in-out infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes pulse {
  0%, 100% { opacity: 0.3; transform: rotate(45deg) scale(1); }
  50% { opacity: 0.6; transform: rotate(45deg) scale(1.1); }
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  33% { transform: translateY(-20px) rotate(1deg); }
  66% { transform: translateY(10px) rotate(-1deg); }
}

.hero-content {
  position: relative;
  z-index: 3;
  text-align: center;
  max-width: 800px;
  padding: 0 24px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  background: linear-gradient(135deg, #000 0%, #333 100%);
  border: 1px solid #444;
  border-radius: 50px;
  padding: 12px 28px;
  margin-bottom: 32px;
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  position: relative;
  overflow: hidden;
}

.hero-badge::before {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  background: linear-gradient(45deg, transparent, rgba(0, 0, 0, 0.1), transparent);
  border-radius: 50px;
  z-index: -1;
  animation: shimmer 3s ease-in-out infinite;
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.badge-icon {
  width: 22px;
  height: 22px;
  color: #fff;
  stroke-width: 2;
}

.hero-title {
  font-size: clamp(2rem, 4vw, 3.2rem);
  font-weight: 700;
  line-height: 1.2;
  margin-bottom: 20px;
  color: #000;
  letter-spacing: -0.02em;
}

.title-highlight {
  background: linear-gradient(135deg, #000 0%, #333 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  position: relative;
}

.title-highlight::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, transparent, #000, transparent);
  animation: underline-expand 2s ease-in-out infinite;
}

@keyframes underline-expand {
  0%, 100% { transform: scaleX(0.3); opacity: 0.5; }
  50% { transform: scaleX(1); opacity: 1; }
}

.hero-description {
  font-size: 1.25rem;
  line-height: 1.6;
  color: #666;
  margin-bottom: 40px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.hero-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-bottom: 60px;
  flex-wrap: wrap;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: var(--primary-color);
  color: white;
  border: 2px solid var(--primary-color);
  border-radius: var(--border-radius);
  padding: 16px 32px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-speed) ease;
  box-shadow: 0 4px 20px var(--shadow-color);
  position: relative;
  overflow: hidden;
}

.btn-primary::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.btn-primary:hover::before {
  left: 100%;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(24, 144, 255, 0.4);
}

.btn-secondary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: transparent;
  backdrop-filter: blur(10px);
  color: var(--primary-color);
  border: 2px solid var(--primary-color);
  border-radius: var(--border-radius);
  padding: 16px 32px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-speed) ease;
  position: relative;
  overflow: hidden;
}

.btn-secondary::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 0;
  height: 100%;
  background: var(--primary-color);
  transition: width var(--transition-speed) ease;
  z-index: -1;
}

.btn-secondary:hover::before {
  width: 100%;
}

.btn-secondary:hover {
  color: white;
}

.btn-secondary:hover {
  background: rgba(24, 144, 255, 0.1);
  border-color: var(--primary-color);
  transform: translateY(-2px);
}

.btn-icon {
  width: 20px;
  height: 20px;
}

.hero-stats {
  display: flex;
  justify-content: center;
  gap: 60px;
  flex-wrap: wrap;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 2.5rem;
  font-weight: 700;
  color: var(--primary-color);
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: var(--gray-color);
  font-weight: 500;
}

/* Features Section */
.features-section {
  padding: 80px 0;
  background: white;
}

.section-header {
  text-align: center;
  margin-bottom: 60px;
}

.section-title {
  font-size: 2rem;
  font-weight: 700;
  color: var(--dark-color);
  margin-bottom: 12px;
  letter-spacing: -0.02em;
}

.section-subtitle {
  font-size: 1.25rem;
  color: var(--gray-color);
  line-height: 1.6;
  max-width: 600px;
  margin: 0 auto;
}

.section-actions {
  margin-top: 20px;
}

.btn-view-more {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #000 0%, #333 100%);
  color: white;
  text-decoration: none;
  border-radius: 25px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

.btn-view-more:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.4);
  color: white;
}

.arrow-icon {
  font-style: normal;
  transition: transform 0.3s ease;
}

.btn-view-more:hover .arrow-icon {
  transform: translateX(3px);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 32px;
}

.feature-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(15px);
  border: 2px solid transparent;
  border-radius: 20px;
  padding: 40px 32px;
  text-align: center;
  transition: all var(--transition-speed) ease;
  position: relative;
  overflow: hidden;
  box-shadow: 0 8px 32px var(--shadow-color);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, transparent, rgba(0, 0, 0, 0.05), transparent);
  opacity: 0;
  transition: opacity var(--transition-speed) ease;
}

.feature-card::after {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  background: linear-gradient(45deg, var(--primary-color), var(--primary-hover), var(--primary-color));
  border-radius: 20px;
  z-index: -1;
  opacity: 0;
  transition: opacity var(--transition-speed) ease;
}

.feature-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 16px 48px var(--shadow-color);
}

.feature-card:hover::before {
  opacity: 1;
}

.feature-card:hover::after {
  opacity: 1;
}

.feature-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 24px;
  background: var(--primary-color);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  position: relative;
  overflow: hidden;
  box-shadow: 0 8px 24px var(--shadow-color);
}

.feature-icon::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transform: rotate(45deg);
  animation: icon-shine 3s ease-in-out infinite;
}

@keyframes icon-shine {
  0% { transform: translateX(-100%) translateY(-100%) rotate(45deg); }
  50% { transform: translateX(0) translateY(0) rotate(45deg); }
  100% { transform: translateX(100%) translateY(100%) rotate(45deg); }
}

.feature-icon svg {
  width: 32px;
  height: 32px;
}

.feature-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--dark-color);
  margin-bottom: 16px;
  text-align: center;
  width: 100%;
}

.feature-description {
  font-size: 16px;
  color: var(--gray-color);
  line-height: 1.6;
  text-align: center;
  width: 100%;
}

/* Process Section */
.process-section {
  padding: 80px 0;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

.process-timeline {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 32px;
  position: relative;
  max-width: 1200px;
  margin: 0 auto;
}

.process-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  position: relative;
  background: #ffffff;
  border: none;
  border-radius: 16px;
  padding: 40px 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all var(--transition-speed) ease;
  overflow: hidden;
}

.process-step:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
}

.step-number {
  width: 50px;
  height: 50px;
  background: #000000;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  font-weight: 700;
  margin: 0 auto 24px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  position: relative;
}



.step-content {
  width: 100%;
  max-width: none;
}

.step-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #000000;
  margin-bottom: 12px;
}

.step-description {
  font-size: 16px;
  color: #666666;
  line-height: 1.6;
}

/* Data Statistics Section */
.stats-section {
  padding: 60px 0;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  position: relative;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  max-width: 1000px;
  margin: 0 auto;
}

.stat-card {
  background: #fff;
  padding: 1.5rem;
  border-radius: 16px;
  text-align: center;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  border: 1px solid #e0e0e0;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #000 0%, #666 100%);
}

.stat-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  font-size: 2.5rem;
  margin-bottom: 0.8rem;
  display: block;
}

.stat-value {
  font-size: 2rem;
  font-weight: 700;
  color: #000;
  margin-bottom: 0.4rem;
}

.stat-label {
  font-size: 1.1rem;
  color: #666;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.stat-trend {
  font-size: 0.9rem;
  color: #28a745;
  font-weight: 600;
  background: rgba(40, 167, 69, 0.1);
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  display: inline-block;
}

/* Success Stories Section */
.success-section {
  padding: 80px 0;
  background: #fff;
}

.success-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 2rem;
  margin-top: 3rem;
}

.success-card {
  background: #f8f9fa;
  padding: 2rem;
  border-radius: 20px;
  border: 1px solid #e0e0e0;
  transition: all 0.3s ease;
  position: relative;
}

.success-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  border-color: #000;
}

.success-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  margin-bottom: 1rem;
  border: 3px solid #000;
}

.success-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  background: linear-gradient(135deg, #ddd 0%, #bbb 100%);
}

.success-name {
  font-size: 1.2rem;
  font-weight: 600;
  color: #000;
  margin-bottom: 0.5rem;
}

.success-school {
  font-size: 1rem;
  color: #666;
  margin-bottom: 1rem;
  font-weight: 500;
}

.success-quote {
  font-style: italic;
  color: #333;
  margin-bottom: 1rem;
  line-height: 1.6;
}

.success-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.success-tags .tag {
  background: #000;
  color: #fff;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
}

/* Partners Section */
.partners-section {
  padding: 80px 0;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

.partners-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 3rem;
  margin-top: 3rem;
}

.partner-category {
  text-align: center;
}

.category-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #000;
  margin-bottom: 2rem;
}

.partner-logos {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.partner-logo {
  background: #fff;
  padding: 1.5rem;
  border-radius: 15px;
  border: 2px solid #e0e0e0;
  font-weight: 600;
  color: #000;
  transition: all 0.3s ease;
  cursor: pointer;
}

.partner-logo:hover {
  border-color: #000;
  transform: translateY(-3px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

/* Advanced Features Section */
.advanced-features-section {
  padding: 80px 0;
  background: #fff;
}

.advanced-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 2rem;
  margin-top: 3rem;
}

.advanced-card {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 2.5rem;
  border-radius: 20px;
  border: 1px solid #e0e0e0;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.advanced-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #000 0%, #666 100%);
}

.advanced-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  border-color: #000;
}

.advanced-icon {
  font-size: 3rem;
  margin-bottom: 1.5rem;
  display: block;
}

.advanced-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #000;
  margin-bottom: 1rem;
}

.advanced-description {
  color: #666;
  line-height: 1.6;
  margin-bottom: 1.5rem;
}

.advanced-features {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.feature-tag {
  background: #000;
  color: #fff;
  padding: 0.4rem 1rem;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
}

/* CTA Section */
.cta-section {
  padding: 80px 0;
  background: linear-gradient(135deg, #000 0%, #333 100%);
  position: relative;
  overflow: hidden;
}

.cta-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 30% 70%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
  pointer-events: none;
}

.cta-content {
  text-align: center;
  position: relative;
  z-index: 2;
}

.cta-title {
  font-size: 3rem;
  font-weight: 700;
  color: #fff;
  margin-bottom: 1rem;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.cta-description {
  font-size: 1.25rem;
  color: #ccc;
  margin-bottom: 2rem;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.cta-buttons {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.cta-button {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem 2rem;
  border: none;
  border-radius: 50px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  position: relative;
  overflow: hidden;
}

.cta-button.primary {
  background: linear-gradient(135deg, #fff 0%, #f0f0f0 100%);
  color: #000;
  box-shadow: 0 4px 15px rgba(255, 255, 255, 0.2);
}

.cta-button.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(255, 255, 255, 0.3);
  background: linear-gradient(135deg, #f0f0f0 0%, #e0e0e0 100%);
}

.cta-button.secondary {
  background: transparent;
  color: #fff;
  border: 2px solid #fff;
}

.cta-button.secondary:hover {
  background: #fff;
  color: #000;
  transform: translateY(-2px);
}

.cta-button .btn-icon {
  width: 20px;
  height: 20px;
  transition: transform 0.3s ease;
}

.cta-button:hover .btn-icon {
  transform: translateX(4px);
}

.cta-guarantee {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  color: #ccc;
  font-size: 0.9rem;
}

.guarantee-icon {
  font-size: 1.2rem;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .process-timeline {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }
  
  .hero-description {
    font-size: 1.125rem;
  }
  
  .hero-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .hero-stats {
    gap: 40px;
  }
  
  .section-title {
    font-size: 2rem;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .process-timeline {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 1rem;
  }
  
  .stat-card {
    padding: 1.5rem;
  }
  
  .stat-value {
    font-size: 2rem;
  }
  
  .success-grid {
    grid-template-columns: 1fr;
  }
  
  .success-card {
    padding: 1.5rem;
  }
  
  .partners-grid {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
  
  .partner-logos {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .advanced-grid {
    grid-template-columns: 1fr;
  }
  
  .advanced-card {
    padding: 2rem;
  }
  
  .cta-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .cta-button {
    width: 100%;
    max-width: 300px;
    justify-content: center;
  }
  
  .floating-particles {
    display: none;
  }
  
  .grid-background {
    opacity: 0.3;
  }
  
  .container {
    padding: 0 16px;
  }
}

@media (max-width: 480px) {
  .hero-title {
    font-size: 2rem;
  }
  
  .section-title {
    font-size: 1.75rem;
  }
  
  .cta-title {
    font-size: 2rem;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .hero-stats {
    grid-template-columns: 1fr;
  }
  
  .partner-logos {
    grid-template-columns: 1fr;
  }
  
  .success-tags {
    justify-content: center;
  }
  
  .advanced-features {
    justify-content: center;
  }
}
</style>
