<template>
  <section class="core-advantages-section">
    <div class="container">
      <div class="section-header">
        <span class="section-badge">核心优势</span>
        <h2>智能精准匹配</h2>
        <p>基于先进的AI技术和海量数据分析，为每位学生提供个性化的院校推荐方案</p>
      </div>

      <div class="advantages-grid">
        <div class="advantage-card smart-matching">
          <div class="card-header">
            <div class="card-icon">
              <el-icon>
                <Compass />
              </el-icon>
            </div>
            <div class="card-title-area">
              <h3>智能匹配算法</h3>
            </div>
            <div class="card-badge">核心技术</div>
          </div>

          <div class="card-content">
            <p>采用深度学习神经网络，分析学生的学术背景、兴趣爱好、职业规划等多维度信息，与全球院校数据进行智能匹配。</p>

            <div class="matching-visual">
              <div class="visual-header">
                <span class="visual-title">实时匹配演示</span>
                <span class="visual-status">
                  <span class="status-dot"></span>
                  正在分析
                </span>
              </div>

              <div class="matching-flow">
                <div class="profile-section">
                  <div class="profile-avatar">👤</div>
                  <div class="profile-data">
                    <div class="data-point">GPA: 3.8</div>
                    <div class="data-point">TOEFL: 105</div>
                    <div class="data-point">专业: CS</div>
                  </div>
                </div>

                <div class="analysis-center">
                  <div class="ai-processor">
                    <div class="processor-core">🧠</div>
                    <div class="processing-rings">
                      <div class="ring ring-1"></div>
                      <div class="ring ring-2"></div>
                      <div class="ring ring-3"></div>
                    </div>
                  </div>
                </div>

                <div class="results-section">
                  <div class="result-card">
                    <div class="university-icon">🏛️</div>
                    <div class="university-name">哈佛大学</div>
                    <div class="match-percentage">96%</div>
                  </div>
                  <div class="result-card">
                    <div class="university-icon">🎓</div>
                    <div class="university-name">斯坦福大学</div>
                    <div class="match-percentage">94%</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="advantage-card accuracy-rate">
          <div class="card-header">
            <div class="card-icon">
              <el-icon>
                <PieChart />
              </el-icon>
            </div>
            <div class="card-title-area">
              <h3>95%准确率保障</h3>
            </div>
            <div class="card-badge">数据驱动</div>
          </div>

          <div class="card-content">
            <p>基于50万+成功案例的大数据分析，我们的AI系统能够提供95%以上的匹配准确率，让您的选校决策更加科学可靠。</p>

            <div class="accuracy-visualization">
              <div class="viz-header">
                <span class="viz-title">准确率统计</span>
                <span class="viz-status">
                  <span class="status-indicator"></span>
                  实时更新
                </span>
              </div>

              <div class="accuracy-dashboard">
                <div class="main-chart">
                  <div class="circular-chart">
                    <div class="chart-center">
                      <div class="center-value">95%</div>
                      <div class="center-label">匹配准确率</div>
                    </div>
                  </div>
                </div>

                <div class="sub-charts">
                  <div class="mini-chart">
                    <div class="mini-value">98%</div>
                    <div class="mini-label">录取预测</div>
                    <div class="mini-bar">
                      <div class="bar-fill" style="width: 98%"></div>
                    </div>
                  </div>
                  <div class="mini-chart">
                    <div class="mini-value">92%</div>
                    <div class="mini-label">专业匹配</div>
                    <div class="mini-bar">
                      <div class="bar-fill" style="width: 92%"></div>
                    </div>
                  </div>
                  <div class="mini-chart">
                    <div class="mini-value">96%</div>
                    <div class="mini-label">地区偏好</div>
                    <div class="mini-bar">
                      <div class="bar-fill" style="width: 96%"></div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="advantage-card personalized">
          <div class="card-header">
            <div class="card-icon">
              <el-icon>
                <User />
              </el-icon>
            </div>
            <div class="card-title-area">
              <h3>个性化定制方案</h3>
            </div>
            <div class="card-badge">量身定制</div>
          </div>

          <div class="card-content">
            <p>不是千篇一律的推荐，而是基于您的独特背景和目标，为您量身定制的个性化选校方案。</p>

            <div class="personalization-features">
              <div class="feature-item">
                <div class="feature-icon">🎯</div>
                <div class="feature-content">
                  <h4>目标导向</h4>
                  <p>根据职业规划定制</p>
                </div>
              </div>
              <div class="feature-item">
                <div class="feature-icon">📊</div>
                <div class="feature-content">
                  <h4>数据分析</h4>
                  <p>多维度综合评估</p>
                </div>
              </div>
              <div class="feature-item">
                <div class="feature-icon">🔄</div>
                <div class="feature-content">
                  <h4>动态调整</h4>
                  <p>实时优化推荐</p>
                </div>
              </div>
              <div class="feature-item">
                <div class="feature-icon">💡</div>
                <div class="feature-content">
                  <h4>智能建议</h4>
                  <p>专业提升建议</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="advantage-card global-coverage">
          <div class="card-header">
            <div class="card-icon">
              <el-icon>
                <School />
              </el-icon>
            </div>
            <div class="card-title-area">
              <h3>全球院校覆盖</h3>
            </div>
            <div class="card-badge">海量数据</div>
          </div>

          <div class="card-content">
            <p>覆盖全球50+国家和地区的10,000+所知名院校，从常春藤盟校到新兴优质大学，应有尽有。</p>

            <div class="coverage-stats">
              <div class="stat-grid">
                <div class="stat-item">
                  <div class="stat-number">10k+</div>
                  <div class="stat-label">全球院校</div>
                  <div class="stat-bar">
                    <div class="stat-fill" style="width: 100%"></div>
                  </div>
                </div>
                <div class="stat-item">
                  <div class="stat-number">50+</div>
                  <div class="stat-label">国家地区</div>
                  <div class="stat-bar">
                    <div class="stat-fill" style="width: 85%"></div>
                  </div>
                </div>
                <div class="stat-item">
                  <div class="stat-number">500+</div>
                  <div class="stat-label">专业领域</div>
                  <div class="stat-bar">
                    <div class="stat-fill" style="width: 90%"></div>
                  </div>
                </div>
                <div class="stat-item">
                  <div class="stat-number">24/7</div>
                  <div class="stat-label">实时更新</div>
                  <div class="stat-bar">
                    <div class="stat-fill" style="width: 100%"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { Compass, PieChart, User, School } from '@element-plus/icons-vue'
</script>

<style scoped>
.core-advantages-section {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 50%, #e2e8f0 100%);
  padding: 32px 0;
  position: relative;
  overflow: hidden;
  color: #1e293b;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.section-header {
  text-align: center;
  max-width: 800px;
  margin: 0 auto 40px;
}

.section-badge {
  display: inline-block;
  font-size: 14px;
  font-weight: 600;
  color: #3498db;
  background-color: rgba(52, 152, 219, 0.1);
  padding: 6px 16px;
  border-radius: 20px;
  margin-bottom: 12px;
  letter-spacing: 0.5px;
  border: 1px solid rgba(52, 152, 219, 0.3);
}

.section-header h2 {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 12px;
  color: #2c3e50;
  line-height: 1.2;
}

.section-header p {
  font-size: 16px;
  line-height: 1.6;
  color: #34495e;
  max-width: 600px;
  margin: 0 auto;
}

.advantages-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  align-items: stretch;
}

.advantage-card {
  background: linear-gradient(145deg, rgba(255, 255, 255, 0.98) 0%, rgba(248, 250, 252, 0.95) 100%);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 8px 32px rgba(30, 41, 59, 0.08), 0 2px 8px rgba(30, 41, 59, 0.04);
  border: 1px solid rgba(148, 163, 184, 0.12);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(20px);
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-height: 320px;
}

.advantage-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #3498db, #2980b9, #3498db);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.advantage-card:hover {
  transform: translateY(-6px) scale(1.01);
  box-shadow: 0 16px 48px rgba(30, 41, 59, 0.12), 0 4px 16px rgba(30, 41, 59, 0.08);
  background: rgba(255, 255, 255, 0.98);
  border-color: rgba(52, 152, 219, 0.25);
}

.advantage-card:hover::before {
  opacity: 1;
}

.card-header {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  margin-bottom: 18px;
  position: relative;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.08);
}

.card-icon {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  flex-shrink: 0;
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.25);
  position: relative;
  overflow: hidden;
}

.card-icon::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.2) 0%, transparent 50%);
  border-radius: 12px;
}

.card-title-area {
  flex: 1;
  margin-left: 12px;
}

.card-header h3 {
  font-size: 17px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 4px;
  line-height: 1.3;
  letter-spacing: -0.01em;
}

.card-badge {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.12) 0%, rgba(59, 130, 246, 0.06) 100%);
  color: #3b82f6;
  font-size: 11px;
  font-weight: 600;
  padding: 5px 10px;
  border-radius: 14px;
  border: 1px solid rgba(59, 130, 246, 0.18);
  white-space: nowrap;
  align-self: flex-start;
  backdrop-filter: blur(8px);
}

.card-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card-content p {
  font-size: 14px;
  line-height: 1.6;
  color: #475569;
  margin: 0;
  font-weight: 400;
  letter-spacing: -0.005em;
  flex-shrink: 0;
}

/* 匹配可视化样式 */
.matching-visual,
.accuracy-visualization {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.03) 0%, rgba(59, 130, 246, 0.01) 100%);
  border-radius: 12px;
  padding: 16px;
  border: 1px solid rgba(59, 130, 246, 0.1);
  margin-top: auto;
  flex: 1;
  backdrop-filter: blur(8px);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.1);
}

.visual-header,
.viz-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(59, 130, 246, 0.12);
}

.visual-title,
.viz-title {
  font-size: 12px;
  font-weight: 600;
  color: #1e293b;
  letter-spacing: -0.01em;
}

.visual-status,
.viz-status {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 10px;
  color: #3b82f6;
  font-weight: 500;
}

.status-dot,
.status-indicator {
  width: 5px;
  height: 5px;
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
  border-radius: 50%;
  animation: pulse 2s infinite;
  box-shadow: 0 0 4px rgba(59, 130, 246, 0.4);
}

@keyframes pulse {

  0%,
  100% {
    opacity: 1;
    transform: scale(1);
  }

  50% {
    opacity: 0.5;
    transform: scale(1.2);
  }
}

/* 其他样式继续... */
.matching-flow {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.profile-section,
.results-section {
  flex: 1;
  font-size: 10px;
}

.analysis-center {
  flex-shrink: 0;
}

.ai-processor {
  position: relative;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.processor-core {
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, #3498db 0%, #5ac8fa 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: white;
  animation: core-pulse 2s ease-in-out infinite;
}

.profile-data .data-point,
.result-card {
  font-size: 9px;
  margin-bottom: 2px;
}

.university-name {
  font-size: 8px;
  font-weight: 600;
}

.match-percentage {
  font-size: 10px;
  font-weight: 700;
  color: #3498db;
}

@keyframes core-pulse {

  0%,
  100% {
    transform: scale(1);
  }

  50% {
    transform: scale(1.1);
  }
}

/* 个性化内容区域样式 */
.personalization-content {
  display: flex;
  gap: 18px;
  margin-top: auto;
  flex: 1;
  min-height: 140px;
}

.features-grid {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  align-content: start;
}

.feature-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 12px 10px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.04) 0%, rgba(59, 130, 246, 0.02) 100%);
  border-radius: 10px;
  border: 1px solid rgba(59, 130, 246, 0.12);
  transition: all 0.25s ease;
  backdrop-filter: blur(8px);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.feature-item:hover {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.08) 0%, rgba(59, 130, 246, 0.04) 100%);
  border-color: rgba(59, 130, 246, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
}

.feature-icon {
  font-size: 16px;
  flex-shrink: 0;
  margin-top: 1px;
  color: #3b82f6;
}

.feature-content h4 {
  font-size: 12px;
  font-weight: 600;
  margin: 0 0 3px 0;
  color: #1e293b;
  letter-spacing: -0.01em;
}

.feature-content p {
  font-size: 10px;
  margin: 0;
  color: #64748b;
  line-height: 1.4;
  font-weight: 400;
}

.customization-preview {
  flex: 1;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.04) 0%, rgba(59, 130, 246, 0.02) 100%);
  border-radius: 12px;
  padding: 16px;
  border: 1px solid rgba(59, 130, 246, 0.1);
  backdrop-filter: blur(8px);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.1);
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(59, 130, 246, 0.12);
}

.preview-title {
  font-size: 12px;
  font-weight: 600;
  color: #1e293b;
  letter-spacing: -0.01em;
}

.preview-status {
  font-size: 10px;
  color: #3b82f6;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.12) 0%, rgba(59, 130, 246, 0.08) 100%);
  padding: 4px 8px;
  border-radius: 12px;
  font-weight: 500;
  border: 1px solid rgba(59, 130, 246, 0.15);
}

.profile-match {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.match-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 8px;
}

.match-label {
  font-size: 10px;
  color: #475569;
  flex: 1;
  font-weight: 500;
}

.match-bar {
  flex: 1;
  height: 4px;
  background: rgba(148, 163, 184, 0.2);
  border-radius: 3px;
  position: relative;
  overflow: hidden;
}

.match-fill {
  height: 100%;
  background: linear-gradient(90deg, #3b82f6 0%, #1d4ed8 100%);
  border-radius: 3px;
  transition: width 0.4s ease;
  box-shadow: 0 0 4px rgba(59, 130, 246, 0.3);
}

.match-value {
  font-size: 9px;
  font-weight: 600;
  color: #3b82f6;
  min-width: 26px;
  text-align: right;
}

/* 统计区域样式 */
.coverage-stats {
  margin-top: auto;
  flex: 1;
}

.feature-item:last-child {
  border-bottom: none;
}

.feature-icon {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  background: rgba(52, 152, 219, 0.06);
  border-radius: 6px;
  flex-shrink: 0;
}

.feature-content h4 {
  font-size: 12px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 2px;
}

.feature-content p {
  font-size: 10px;
  color: #7f8c8d;
  margin: 0;
  line-height: 1.3;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.stat-item {
  text-align: center;
  padding: 14px 10px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8) 0%, rgba(255, 255, 255, 0.6) 100%);
  border-radius: 10px;
  border: 1px solid rgba(148, 163, 184, 0.1);
  backdrop-filter: blur(8px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.25s ease;
}

.stat-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.1);
  border-color: rgba(59, 130, 246, 0.15);
}

.stat-number {
  font-size: 18px;
  font-weight: 700;
  color: #3b82f6;
  margin-bottom: 3px;
  letter-spacing: -0.02em;
}

.stat-label {
  font-size: 10px;
  color: #64748b;
  margin-bottom: 8px;
  font-weight: 500;
}

.stat-bar {
  height: 2px;
  background: rgba(189, 195, 199, 0.15);
  border-radius: 1px;
  overflow: hidden;
}

.stat-fill {
  height: 100%;
  background: linear-gradient(90deg, #3498db, #2980b9);
  border-radius: 1px;
  transition: width 0.8s ease;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .advantages-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .advantage-card {
    min-height: auto;
  }
}

@media (max-width: 768px) {
  .advantage-card {
    padding: 20px;
  }

  .section-header h2 {
    font-size: 32px;
  }

  .matching-flow {
    flex-direction: column;
    gap: 12px;
  }

  .stat-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }
}
</style>
