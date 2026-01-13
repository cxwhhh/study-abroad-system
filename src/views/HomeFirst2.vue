<template>
  <div class="home-container">
    <nav-bar></nav-bar>

    <!-- Hero Section -->
    <header class="hero-section">
      <quantum-hero-scene @navigate="handleNavigate" @hover="handleHover" />

      <!-- Gradient Overlay -->
      <div class="gradient-overlay"></div>

      <div class="hero-content">
        <h1>
          新英美留学 <br />
          <span class="subtitle-text">智能留学规划</span>
        </h1>
        <p class="description-text">
          基于循环 Transformer 神经网络，定制专属留学方案。
        </p>

        <div class="button-group">
          <div class="discover-btn" @click="goToAiSelect">
            <span>开始智能择校</span>
            <div class="circle-icon">
              <el-icon>
                <ArrowRight />
              </el-icon>
            </div>
          </div>
        </div>
      </div>

      <div class="hero-interaction-hint" aria-live="polite">
        <div class="hint-pill" :class="{ 'is-active': hoveredHotspot }">
          <template v-if="hoveredHotspot">
            <span class="hint-kicker">点击进入</span>
            <span class="hint-title">{{ hoveredHotspot.label }}</span>
            <span class="hint-arrow">→</span>
          </template>
          <template v-else>
            <span class="hint-muted">悬停或点击轨道节点，直达院校库/择校等功能</span>
          </template>
        </div>
      </div>
    </header>

    <main>
      <!-- Introduction -->
      <section id="introduction" class="section-white">
        <div class="container grid-layout">
          <div class="col-left">
            <div class="section-label">关于我们</div>
            <h2 class="section-title">打破信息壁垒</h2>
            <div class="gold-separator"></div>
          </div>
          <div class="col-right text-content">
            <p>
              <span class="drop-cap">在</span>传统的留学申请中，信息不对称和流程的复杂性往往成为阻碍学生进入梦校的最大障碍。目前的行业标准虽然提供了基础服务，但在个性化挖掘和精准定位上仍有不足。
            </p>
            <p>
              然而，解读名校录取的深层逻辑——也就是所谓的"解码"——是一项巨大的挑战。复杂的录取偏好和多变的申请趋势往往让普通申请者感到困惑。<strong class="text-dark">新英美留学</strong>
              利用先进的机器学习技术，直接从海量案例中学习这些复杂的录取模式，实现了远超传统人工经验的申请成功率。
            </p>
          </div>
        </div>
      </section>

      <!-- The Science: Surface Code -->
      <section id="science" class="section-white border-top">
        <div class="container">
          <div class="grid-layout-2">
            <div class="text-col">
              <div class="badge-label">
                <el-icon>
                  <Collection />
                </el-icon> 评估体系
              </div>
              <h2 class="section-title-large">全维能力画像</h2>
              <p class="text-lg">
                在我们的评估体系中，"核心数据"承载着学生的学术背景，而穿插其中的"潜力指标"则如同敏锐的观察者。它们通过多维度的交叉验证，在不破坏学生个性的前提下，精准捕捉每一个闪光点。
              </p>
              <p class="text-lg">
                当某个能力维度出现波动，相关的潜力指标就会显现。这种独特的"信号模式"构成了学生的专属画像。我们系统的任务就是解读这些信号，准确预测最适合学生的升学路径。
              </p>
            </div>
            <div class="diagram-col">
              <surface-code-diagram />
            </div>
          </div>
        </div>
      </section>

      <!-- The Science: Transformer Decoder -->
      <section class="section-dark">
        <div class="bg-effects">
          <div class="blur-blob blob-1"></div>
          <div class="blur-blob blob-2"></div>
        </div>

        <div class="container relative-z">
          <div class="grid-layout-2">
            <div class="diagram-col order-2-lg">
              <transformer-decoder-diagram />
            </div>
            <div class="text-col order-1-lg">
              <div class="badge-label dark-badge">
                核心技术
              </div>
              <h2 class="section-title-large text-white">智能选校引擎</h2>
              <p class="text-lg text-light">
                传统的选校往往基于简单、独立的硬性指标。但真实的申请环境更加复杂多变。我们的系统将选校视为一个序列预测问题，采用先进的 <strong>Recurrent Transformer</strong> 架构。
              </p>
              <p class="text-lg text-light">
                它能够摄取历年的录取数据流，利用"软性"的模拟信息——即概率分布而非简单的非黑即白——对录取结果做出高度智能化的预测。
              </p>
            </div>
          </div>
        </div>
      </section>

      <!-- The Science: Results -->
      <section class="section-gray">
        <div class="container">
          <div class="centered-header">
            <h2 class="section-title-large">超越行业标准</h2>
            <p class="text-lg">
              我们的 AI 系统在数万个真实申请案例中进行了验证。它的表现持续优于传统的"人工经验匹配"模式，有效地提升了申请的精准度，让名校的大门为您敞开。
            </p>
          </div>
          <div class="chart-container">
            <performance-metric-diagram />
          </div>
        </div>
      </section>

      <!-- Impact -->
      <section id="impact" class="section-white border-top">
        <div class="container grid-layout">
          <div class="col-left-impact">
            <div class="scene-wrapper">
              <quantum-computer-scene />
              <div class="scene-caption">智能申请环境模拟系统</div>
            </div>
          </div>
          <div class="col-right-impact">
            <div class="section-label">深远影响</div>
            <h2 class="section-title">迈向名校之路</h2>
            <p class="text-lg">
              即使在竞争日益激烈的环境中，我们的系统依然保持着显著优势。它能处理包括复杂的背景提升和文书优化在内的各种现实挑战，这些往往是传统中介难以兼顾的痛点。
            </p>
            <p class="text-lg">
              通过直接从数据中学习，机器学习模型能够适应不同院校独特的招生偏好，从而显著降低了申请名校的不确定性，让每一个梦想都触手可及。
            </p>

            <div class="quote-box">
              <p class="quote-text">
                "我们的工作展示了机器学习超越传统人工规划的能力，通过直接从数据中学习，证明了 AI 是未来留学申请领域强有力的竞争者。"
              </p>
              <span class="quote-author">— 新英美留学团队</span>
            </div>
          </div>
        </div>
      </section>

      <!-- Authors -->
      <section id="authors" class="section-gray border-top">
        <div class="container">
          <div class="centered-header mb-large">
            <div class="section-label">专家团队</div>
            <h2 class="section-title-large">核心顾问</h2>
            <p class="text-gray">汇聚全球顶尖名校导师与 AI 技术专家的跨界合作。</p>
          </div>

          <div class="authors-grid">
            <author-card v-for="(author, index) in authors" :key="index" :name="author.name" :role="author.role"
              :delay="index * 0.1 + 's'" />
          </div>
          <div class="centered-footer">
            <p class="text-italic">以及众多致力于教育咨询、升学规划和技术研发的幕后贡献者。</p>
          </div>
        </div>
      </section>
    </main>

    <footer-bar></footer-bar>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import NavBar from '../components/NavBar.vue';
import FooterBar from '../components/FooterBar.vue';
import QuantumHeroScene from '../components/homefirst2/QuantumHeroScene.vue';
import SurfaceCodeDiagram from '../components/homefirst2/SurfaceCodeDiagram.vue';
import TransformerDecoderDiagram from '../components/homefirst2/TransformerDecoderDiagram.vue';
import PerformanceMetricDiagram from '../components/homefirst2/PerformanceMetricDiagram.vue';
import QuantumComputerScene from '../components/homefirst2/QuantumComputerScene.vue';
import AuthorCard from '../components/homefirst2/AuthorCard.vue';
import { ArrowRight } from '@element-plus/icons-vue';

const authors = [
  { name: "Johannes Bausch", role: "Google DeepMind" },
  { name: "Andrew W. Senior", role: "Google DeepMind" },
  { name: "Francisco J. H. Heras", role: "Google DeepMind" },
  { name: "Thomas Edlich", role: "Google DeepMind" },
  { name: "Alex Davies", role: "Google DeepMind" },
  { name: "Michael Newman", role: "Google Quantum AI" }
];

const router = useRouter();
const hoveredHotspot = ref(null);

const handleNavigate = (path) => {
  if (!path) return;
  router.push(path);
};

const handleHover = (payload) => {
  hoveredHotspot.value = payload;
};

const goToAiSelect = () => {
  router.push('/matching');
};
</script>

<style scoped>
.home-container {
  background-color: #F9F8F4;
  color: #292524;
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
}

/* Hero Section */
.hero-section {
  position: relative;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background-color: #F9F8F4;
}

.hero-interaction-hint {
  position: absolute;
  bottom: 2.25rem;
  left: 50%;
  transform: translateX(-50%);
  z-index: 12;
  pointer-events: none;
}

.hint-pill {
  display: inline-flex;
  align-items: center;
  gap: 0.6rem;
  padding: 0.75rem 1.05rem;
  border-radius: 9999px;
  background: rgba(255, 255, 255, 0.66);
  border: 1px solid rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(10px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.08);
  color: #44403c;
  font-size: 0.875rem;
  line-height: 1.2;
  max-width: min(640px, calc(100vw - 2.5rem));
  justify-content: center;
}

.hint-pill.is-active {
  border-color: rgba(197, 160, 89, 0.4);
  box-shadow: 0 14px 36px rgba(197, 160, 89, 0.16);
}

.hint-kicker {
  font-size: 0.75rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #78716c;
  white-space: nowrap;
}

.hint-title {
  font-family: serif;
  font-weight: 600;
  color: #1c1917;
  white-space: nowrap;
}

.hint-arrow {
  color: #C5A059;
  font-weight: 600;
}

.hint-muted {
  color: #78716c;
  font-weight: 500;
  text-align: center;
}

.gradient-overlay {
  position: absolute;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  background: radial-gradient(circle at center, rgba(249, 248, 244, 0.92) 0%, rgba(249, 248, 244, 0.6) 50%, rgba(249, 248, 244, 0.3) 100%);
}

.hero-content {
  position: relative;
  z-index: 10;
  text-align: center;
  padding: 0 1.5rem;
  max-width: 80rem;
  margin: 0 auto;
  pointer-events: none;
  /* Let clicks pass through to the canvas */
}

.hero-content>* {
  pointer-events: auto;
  /* Re-enable pointer events for actual content elements */
}

h1 {
  font-family: serif;
  font-size: 3rem;
  line-height: 1.3;
  margin-bottom: 2rem;
  color: #1c1917;
  font-weight: 500;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

@media (min-width: 768px) {
  h1 {
    font-size: 4.5rem;
    line-height: 1.1;
  }
}

@media (min-width: 1024px) {
  h1 {
    font-size: 6rem;
  }
}

.subtitle-text {
  font-style: italic;
  font-weight: normal;
  color: #57534e;
  font-size: 1.5rem;
  display: block;
  margin-top: 1rem;
}

@media (min-width: 768px) {
  .subtitle-text {
    font-size: 2.5rem;
  }
}

.description-text {
  max-width: 42rem;
  margin: 0 auto 3rem;
  font-size: 1.125rem;
  color: #44403c;
  font-weight: 300;
  line-height: 1.8;
}

@media (min-width: 768px) {
  .description-text {
    font-size: 1.25rem;
  }
}

.button-group {
  display: flex;
  justify-content: center;
}

.discover-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.75rem;
  font-size: 1.5rem;
  font-weight: 600;
  color: #78716c;
  cursor: pointer;
  transition: color 0.3s;
}

@media (min-width: 768px) {
  .discover-btn {
    font-size: 1.875rem;
  }
}

.discover-btn:hover {
  color: #1c1917;
}

.circle-icon {
  padding: 0.75rem;
  border: 1px solid #d6d3d1;
  border-radius: 9999px;
  background-color: rgba(255, 255, 255, 0.5);
  transition: border-color 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
}

.discover-btn:hover .circle-icon {
  border-color: #1c1917;
}

/* Common Section Styles */
.section-white {
  background-color: white;
  padding: 6rem 0;
}

.section-gray {
  background-color: #F9F8F4;
  padding: 6rem 0;
}

.section-dark {
  background-color: #1c1917;
  color: #f5f5f4;
  padding: 6rem 0;
  position: relative;
  overflow: hidden;
}

.border-top {
  border-top: 1px solid #e7e5e4;
}

.container {
  max-width: 80rem;
  margin: 0 auto;
  padding: 0 1.5rem;
}

.grid-layout {
  display: grid;
  grid-template-columns: 1fr;
  gap: 3rem;
  align-items: start;
}

@media (min-width: 768px) {
  .grid-layout {
    grid-template-columns: repeat(12, 1fr);
  }
}

.col-left {
  grid-column: span 12;
}

@media (min-width: 768px) {
  .col-left {
    grid-column: span 4;
  }
}

.col-right {
  grid-column: span 12;
}

@media (min-width: 768px) {
  .col-right {
    grid-column: span 8;
  }
}

.section-label {
  display: inline-block;
  margin-bottom: 0.75rem;
  font-size: 0.75rem;
  font-weight: bold;
  letter-spacing: 0.1em;
  color: #78716c;
  text-transform: uppercase;
}

.section-title {
  font-family: serif;
  font-size: 2.25rem;
  margin-bottom: 1.5rem;
  line-height: 1.2;
  color: #1c1917;
}

.col-left .section-title {
  margin-left: 0;
}

.gold-separator {
  width: 4rem;
  height: 0.25rem;
  background-color: #C5A059;
  margin-bottom: 1.5rem;
}

.text-content p {
  font-size: 1.125rem;
  color: #57534e;
  line-height: 1.625;
  margin-bottom: 1.5rem;
}

.drop-cap {
  font-size: 3rem;
  float: left;
  margin-right: 0.75rem;
  margin-top: -0.5rem;
  font-family: serif;
  color: #C5A059;
}

.text-dark {
  color: #1c1917;
  font-weight: 500;
}

/* Science Section */
.grid-layout-2 {
  display: grid;
  grid-template-columns: 1fr;
  gap: 4rem;
  align-items: center;
}

@media (min-width: 1024px) {
  .grid-layout-2 {
    grid-template-columns: 1fr 1fr;
  }
}

.text-col {
  display: flex;
  flex-direction: column;
}

.badge-label {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.25rem 0.75rem;
  background-color: #f5f5f4;
  color: #57534e;
  font-size: 0.75rem;
  font-weight: bold;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  border-radius: 9999px;
  margin-bottom: 1.5rem;
  border: 1px solid #e7e5e4;
  width: fit-content;
}

.badge-label.dark-badge {
  background-color: #292524;
  color: #C5A059;
  border-color: #44403c;
}

.section-title-large {
  font-family: serif;
  font-size: 2.25rem;
  margin-bottom: 1.5rem;
  color: #1c1917;
}

@media (min-width: 768px) {
  .section-title-large {
    font-size: 3rem;
  }
}

.section-title-large.text-white {
  color: white;
}

.text-lg {
  font-size: 1.125rem;
  color: #57534e;
  margin-bottom: 1.5rem;
  line-height: 1.625;
}

.text-light {
  color: #a8a29e;
}

/* Dark Section Effects */
.bg-effects {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0.1;
  pointer-events: none;
}

.blur-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
}

.blob-1 {
  width: 24rem;
  height: 24rem;
  background-color: #57534e;
  top: -6rem;
  left: -6rem;
}

.blob-2 {
  width: 24rem;
  height: 24rem;
  background-color: #C5A059;
  bottom: -6rem;
  right: -6rem;
}

.relative-z {
  position: relative;
  z-index: 10;
}

@media (min-width: 1024px) {
  .order-2-lg {
    order: 2;
  }

  .order-1-lg {
    order: 1;
  }
}

/* Results Section */
.centered-header {
  max-width: 56rem;
  margin: 0 auto 3rem;
  text-align: center;
}

.chart-container {
  max-width: 48rem;
  margin: 0 auto;
}

/* Impact Section */
.col-left-impact {
  grid-column: span 12;
  position: relative;
}

@media (min-width: 768px) {
  .col-left-impact {
    grid-column: span 5;
  }
}

.col-right-impact {
  grid-column: span 12;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

@media (min-width: 768px) {
  .col-right-impact {
    grid-column: span 7;
  }
}

.scene-wrapper {
  aspect-ratio: 1 / 1;
  background-color: #F5F4F0;
  border-radius: 0.75rem;
  overflow: hidden;
  position: relative;
  border: 1px solid #e7e5e4;
  box-shadow: inset 0 2px 4px 0 rgba(0, 0, 0, 0.05);
}

.scene-caption {
  position: absolute;
  bottom: 1rem;
  left: 0;
  right: 0;
  text-align: center;
  font-size: 0.75rem;
  color: #a8a29e;
  font-family: serif;
  font-style: italic;
  pointer-events: none;
}

.quote-box {
  padding: 1.5rem;
  background-color: #F9F8F4;
  border: 1px solid #e7e5e4;
  border-radius: 0.5rem;
  border-left: 4px solid #C5A059;
  margin-top: 1rem;
}

.quote-text {
  font-family: serif;
  font-style: italic;
  font-size: 1.25rem;
  color: #292524;
  margin-bottom: 1rem;
}

.quote-author {
  font-size: 0.875rem;
  font-weight: bold;
  color: #78716c;
  letter-spacing: 0.05em;
  text-transform: uppercase;
}

/* Authors Section */
.mb-large {
  margin-bottom: 4rem;
}

.text-gray {
  color: #78716c;
  max-width: 42rem;
  margin: 0 auto;
}

.authors-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 2rem;
  justify-content: center;
  align-items: center;
}

.centered-footer {
  text-align: center;
  margin-top: 3rem;
}

.text-italic {
  font-style: italic;
  color: #78716c;
}
</style>
