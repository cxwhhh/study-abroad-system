<template>
  <div class="company-about">
    <nav-bar></nav-bar>

    <header class="hero">
      <div class="hero-visual" aria-hidden="true">
        <quantum-hero-scene />
        <div class="hero-veil"></div>
      </div>
      <div class="container hero-content">
        <div class="hero-kicker">XINYINGMEI · STUDY ABROAD</div>
        <h1 class="hero-title">关于新英美留学</h1>
        <p class="hero-subtitle">专业、可靠、值得信赖的留学服务机构</p>
        <div class="hero-metrics">
          <div class="hero-metric">
            <div class="metric-value">2015</div>
            <div class="metric-label">成立年份</div>
          </div>
          <div class="hero-metric">
            <div class="metric-value">3000+</div>
            <div class="metric-label">成功案例</div>
          </div>
          <div class="hero-metric">
            <div class="metric-value">Top 50</div>
            <div class="metric-label">全球名校覆盖</div>
          </div>
        </div>
      </div>
    </header>

    <div class="main-content">
      <div class="container">

        <!-- 公司简介 -->
        <section class="company-intro">
          <div class="intro-layout">
            <div class="intro-content">
              <h2>我们的故事</h2>
              <p>新英美留学成立于2015年，是一家专注于英美留学申请的专业咨询机构。我们致力于为中国学生提供最优质的留学申请服务，帮助他们实现海外求学梦想。</p>
              <p>经过多年的发展，我们已经成功帮助超过3000名学生获得理想院校的录取通知书，其中包括牛津、剑桥、哈佛、斯坦福等世界顶尖学府。</p>
            </div>
            <div class="intro-image">
              <img src="/images/campus.jpg" alt="校园环境" />
            </div>
          </div>
        </section>

        <section class="company-timeline">
          <h2>发展历程</h2>
          <div class="timeline">
            <div class="timeline-line" aria-hidden="true"></div>
            <div class="timeline-item timeline-item-left">
              <div class="timeline-card">
                <div class="timeline-year">2015</div>
                <h3 class="timeline-title">公司成立</h3>
                <p class="timeline-text">新英美留学正式成立，开始为中国学生提供专业的留学咨询服务</p>
              </div>
              <div class="timeline-dot" aria-hidden="true"></div>
            </div>
            <div class="timeline-item timeline-item-right">
              <div class="timeline-card">
                <div class="timeline-year">2017</div>
                <h3 class="timeline-title">业务扩展</h3>
                <p class="timeline-text">成功帮助首批100名学生获得理想院校录取，建立了完善的服务体系</p>
              </div>
              <div class="timeline-dot" aria-hidden="true"></div>
            </div>
            <div class="timeline-item timeline-item-left">
              <div class="timeline-card">
                <div class="timeline-year">2019</div>
                <h3 class="timeline-title">团队壮大</h3>
                <p class="timeline-text">专业顾问团队扩展至20人，服务范围覆盖更多专业领域</p>
              </div>
              <div class="timeline-dot" aria-hidden="true"></div>
            </div>
            <div class="timeline-item timeline-item-right">
              <div class="timeline-card">
                <div class="timeline-year">2022</div>
                <h3 class="timeline-title">数字化转型</h3>
                <p class="timeline-text">推出在线申请平台，为学生提供更便捷的服务体验</p>
              </div>
              <div class="timeline-dot" aria-hidden="true"></div>
            </div>
            <div class="timeline-item timeline-item-left">
              <div class="timeline-card">
                <div class="timeline-year">2024</div>
                <h3 class="timeline-title">AI智能服务</h3>
                <p class="timeline-text">引入AI智能选校系统，为学生提供更精准的院校匹配服务</p>
              </div>
              <div class="timeline-dot" aria-hidden="true"></div>
            </div>
          </div>
        </section>

        <!-- 核心价值 -->
        <section class="company-values">
          <div class="values-header">
            <div class="values-kicker">CORE VALUES</div>
            <h2>核心价值观</h2>
            <p class="values-subtitle">以结果为导向、以学生为中心，用专业与责任兑现承诺。</p>
          </div>

          <div class="values-cards">
            <div class="value-card">
              <div class="value-card-top">
                <div class="value-badge">01</div>
                <h3 class="value-title">专业</h3>
              </div>
              <p class="value-desc">拥有资深的留学顾问团队，深度了解各国教育体系和申请要求。</p>
            </div>
            <div class="value-card">
              <div class="value-card-top">
                <div class="value-badge">02</div>
                <h3 class="value-title">诚信</h3>
              </div>
              <p class="value-desc">以诚待人，为每位学生提供真实可靠的留学信息和建议。</p>
            </div>
            <div class="value-card">
              <div class="value-card-top">
                <div class="value-badge">03</div>
                <h3 class="value-title">创新</h3>
              </div>
              <p class="value-desc">不断引入新技术和服务模式，为学生提供更优质的体验。</p>
            </div>
            <div class="value-card">
              <div class="value-card-top">
                <div class="value-badge">04</div>
                <h3 class="value-title">责任</h3>
              </div>
              <p class="value-desc">对每位学生的未来负责，全程陪伴留学申请的每一个环节。</p>
            </div>
          </div>

          <div class="values-banner" aria-hidden="true">
            <img src="/images/oxford.jpg" alt="" />
            <div class="values-banner-veil"></div>
          </div>
        </section>



      </div>
    </div>

    <footer-bar></footer-bar>
  </div>
</template>

<script setup>
import NavBar from '../components/NavBar.vue'
import FooterBar from '../components/FooterBar.vue'
import QuantumHeroScene from '../components/homefirst2/QuantumHeroScene.vue'
</script>

<style scoped>
.company-about {
  min-height: 100vh;
  background: #F9F8F4;
  font-family: sans-serif;
  color: #292524;
}

.container {
  max-width: 1080px;
  margin: 0 auto;
  padding: 0 24px;
}

.hero {
  position: relative;
  padding: 120px 0 68px;
  overflow: hidden;
}

.hero-visual {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.hero-veil {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(800px 420px at 20% 30%, rgba(197, 160, 89, 0.18), transparent 60%),
    radial-gradient(720px 360px at 80% 20%, rgba(79, 70, 229, 0.12), transparent 60%),
    linear-gradient(180deg, rgba(249, 248, 244, 0.9), rgba(249, 248, 244, 0.98));
}

.hero-content {
  position: relative;
  z-index: 1;
  text-align: left;
}

.hero-kicker {
  font-size: 13px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: rgba(41, 37, 36, 0.62);
  font-weight: 800;
}

.hero-title {
  margin: 16px 0 0;
  font-family: "Times New Roman", Times, serif;
  font-size: 56px;
  font-weight: 600;
  letter-spacing: -0.02em;
}

.hero-subtitle {
  margin: 16px 0 0;
  font-size: 18px;
  color: rgba(87, 83, 78, 0.9);
  line-height: 1.7;
  max-width: 52ch;
}

.hero-metrics {
  margin-top: 28px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  max-width: 820px;
}

.hero-metric {
  border-radius: 16px;
  border: 1px solid rgba(41, 37, 36, 0.08);
  background: rgba(255, 255, 255, 0.72);
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.06);
  padding: 14px 16px;
}

.metric-value {
  font-weight: 900;
  letter-spacing: -0.02em;
  font-size: 18px;
  color: rgba(41, 37, 36, 0.92);
}

.metric-label {
  margin-top: 4px;
  font-size: 12px;
  color: rgba(87, 83, 78, 0.72);
}

/* Main Content */
.main-content {
  padding: 80px 0;
}

section {
  margin-bottom: 120px;
}

section:last-child {
  margin-bottom: 0;
}

section h2 {
  font-family: "Times New Roman", Times, serif;
  font-size: 42px;
  font-weight: 400;
  color: #292524;
  margin: 0 0 60px;
  text-align: center;
}

/* Intro */
.intro-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 80px;
  align-items: center;
}

.intro-content h2 {
  text-align: left;
  margin-bottom: 32px;
}

.intro-content p {
  font-size: 17px;
  line-height: 1.8;
  color: #57534e;
  margin: 0 0 24px;
}

.intro-image {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.08);
}

.intro-image img {
  width: 100%;
  height: 400px;
  object-fit: cover;
  transition: transform 0.8s cubic-bezier(0.165, 0.84, 0.44, 1);
}

.intro-image:hover img {
  transform: scale(1.05);
}

/* Timeline */
.timeline {
  max-width: 980px;
  margin: 0 auto;
  position: relative;
  padding: 20px 0 10px;
}

.timeline-line {
  position: absolute;
  left: 50%;
  top: 0;
  bottom: 0;
  width: 2px;
  transform: translateX(-50%);
  background: linear-gradient(180deg, transparent, rgba(197, 160, 89, 0.55), transparent);
}

.timeline-item {
  position: relative;
  display: grid;
  grid-template-columns: 1fr 40px 1fr;
  align-items: center;
  padding: 18px 0;
}

.timeline-item-left .timeline-card {
  grid-column: 1;
  justify-self: end;
}

.timeline-item-right .timeline-card {
  grid-column: 3;
  justify-self: start;
}

.timeline-dot {
  grid-column: 2;
  width: 14px;
  height: 14px;
  border-radius: 999px;
  background: #C5A059;
  border: 3px solid #F9F8F4;
  box-shadow: 0 0 0 6px rgba(197, 160, 89, 0.16);
  justify-self: center;
}

.timeline-card {
  width: min(420px, 100%);
  padding: 22px 22px;
  border-radius: 18px;
  border: 1px solid rgba(41, 37, 36, 0.08);
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.06);
}

.timeline-year {
  font-family: "Times New Roman", Times, serif;
  font-size: 26px;
  color: #C5A059;
  font-weight: 700;
  letter-spacing: -0.01em;
}

.timeline-title {
  margin: 10px 0 0;
  font-size: 20px;
  font-weight: 800;
  color: rgba(41, 37, 36, 0.92);
}

.timeline-text {
  margin: 12px 0 0;
  font-size: 15px;
  line-height: 1.7;
  color: rgba(87, 83, 78, 0.9);
}

/* Values */
.company-values {
  position: relative;
}

.values-header {
  max-width: 760px;
  margin: 0 auto 42px;
  text-align: center;
}

.values-kicker {
  font-size: 12px;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  font-weight: 900;
  color: rgba(41, 37, 36, 0.55);
}

.company-values h2 {
  margin: 16px 0 0;
}

.values-subtitle {
  margin: 14px auto 0;
  font-size: 16px;
  line-height: 1.75;
  color: rgba(87, 83, 78, 0.9);
  max-width: 60ch;
}

.values-cards {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.value-card {
  text-align: left;
  padding: 22px 22px;
  border-radius: 18px;
  border: 1px solid rgba(41, 37, 36, 0.08);
  background: rgba(255, 255, 255, 0.76);
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.04);
  transition: transform 0.25s ease, box-shadow 0.25s ease, border-color 0.25s ease;
}

.value-card:hover {
  transform: translateY(-2px);
  border-color: rgba(197, 160, 89, 0.4);
  box-shadow: 0 22px 48px rgba(0, 0, 0, 0.06);
}

.value-card-top {
  display: flex;
  align-items: center;
  gap: 12px;
}

.value-badge {
  width: 38px;
  height: 38px;
  border-radius: 14px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: rgba(197, 160, 89, 0.14);
  color: #C5A059;
  font-weight: 900;
  letter-spacing: 0.04em;
  font-size: 12px;
}

.value-title {
  margin: 0;
  font-size: 22px;
  font-weight: 900;
  letter-spacing: -0.01em;
  color: rgba(41, 37, 36, 0.92);
}

.value-desc {
  margin: 14px 0 0;
  font-size: 15px;
  line-height: 1.7;
  color: rgba(87, 83, 78, 0.9);
}

.values-banner {
  margin-top: 26px;
  border-radius: 22px;
  overflow: hidden;
  height: 260px;
  position: relative;
  border: 1px solid rgba(41, 37, 36, 0.08);
  box-shadow: 0 18px 46px rgba(0, 0, 0, 0.06);
}

.values-banner img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transform: scale(1.02);
}

.values-banner-veil {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(520px 240px at 18% 35%, rgba(197, 160, 89, 0.28), transparent 60%),
    linear-gradient(180deg, rgba(249, 248, 244, 0.08), rgba(249, 248, 244, 0.6));
}

/* Responsive */
@media (max-width: 768px) {
  .hero {
    padding: 110px 0 56px;
  }

  .hero-title {
    font-size: 40px;
  }

  .intro-layout,
  .values-cards {
    grid-template-columns: 1fr;
    gap: 40px;
  }

  .hero-metrics {
    grid-template-columns: 1fr;
  }

  .timeline-line {
    left: 16px;
    transform: none;
  }

  .timeline-item {
    grid-template-columns: 24px 1fr;
    gap: 14px;
  }

  .timeline-item-left .timeline-card,
  .timeline-item-right .timeline-card {
    grid-column: 2;
    justify-self: stretch;
  }

  .timeline-dot {
    grid-column: 1;
  }

  .values-header {
    margin-bottom: 34px;
  }
}
</style>
