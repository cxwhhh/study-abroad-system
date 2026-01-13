<template>
  <section class="process-section">
    <div class="container">
      <div class="section-header">
        <span class="section-badge">使用流程</span>
        <h2>三步完成智能选校</h2>
        <p>简单三步，即可获得专业的AI选校建议，让留学申请更加高效精准</p>
      </div>

      <div class="process-grid">
        <div class="step-card">
          <div class="step-number">1</div>
          <div class="step-content">
            <h3>填写信息</h3>
            <p>输入学术背景</p>
            <div class="step-demo">
              <span class="demo-tag">GPA: 3.8</span>
              <span class="demo-tag">TOEFL: 105</span>
            </div>
          </div>
        </div>

        <div class="step-card">
          <div class="step-number">2</div>
          <div class="step-content">
            <h3>AI分析</h3>
            <p>智能匹配计算</p>
            <div class="step-demo">
              <div class="progress-mini">
                <div class="progress-fill" style="width: 85%"></div>
                <span>分析中 85%</span>
              </div>
            </div>
          </div>
        </div>

        <div class="step-card">
          <div class="step-number">3</div>
          <div class="step-content">
            <h3>获得结果</h3>
            <p>院校推荐列表</p>
            <div class="step-demo">
              <div class="result-mini">
                <span class="result-tag">哈佛 96%</span>
                <span class="result-tag">斯坦福 94%</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="process-cta">
        <h3>准备开始您的智能选校之旅吗？</h3>
        <p>只需3分钟，即可获得专业的AI选校建议</p>
        <el-button type="primary" size="large" class="cta-button" @click="startProcess">
          <el-icon>
            <Connection />
          </el-icon>
          立即开始
        </el-button>
      </div>
    </div>
  </section>
</template>

<script setup>
import {
  Edit,
  Cpu,
  List,
  Document,
  Trophy,
  Star,
  DataAnalysis,
  Connection,
  Timer,
  PieChart,
  TrendCharts,
  Briefcase
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()

const startProcess = () => {
  ElMessage.success('正在启动智能选校流程...')
  setTimeout(() => {
    router.push('/matching')
  }, 1500)
}
</script>

<style scoped>
.process-section {
  background: linear-gradient(135deg, #ecf0f1 0%, #bdc3c7 50%, #95a5a6 100%);
  padding: 40px 0;
  position: relative;
  overflow: hidden;
  color: #2c3e50;
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
  font-size: 16px;
  font-weight: 600;
  color: #0066cc;
  background-color: rgba(0, 102, 204, 0.1);
  padding: 6px 12px;
  border-radius: 20px;
  margin-bottom: 16px;
  letter-spacing: 0.5px;
}

.section-header h2 {
  font-size: 48px;
  font-weight: 700;
  margin: 0 0 16px;
  color: #1d1d1f;
  line-height: 1.1;
}

.section-header p {
  font-size: 20px;
  line-height: 1.5;
  color: #6c757d;
}

.process-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 40px;
}

@media (max-width: 768px) {
  .process-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
}

.timeline-line {
  position: absolute;
  left: 50%;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(180deg, #0066cc 0%, #5ac8fa 100%);
  transform: translateX(-50%);
  z-index: 1;
}

.step-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  position: relative;
}

.step-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
}

.step-number {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #4A90E2 0%, #7BB3F0 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
  margin-bottom: 12px;
}

.step-content h3 {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 4px 0;
  color: #1d1d1f;
}

.step-content p {
  font-size: 13px;
  color: #6c757d;
  margin: 0 0 12px 0;
  line-height: 1.4;
}

.step-demo {
  margin-top: 8px;
}

.demo-tag,
.result-tag {
  display: inline-block;
  background: #f8f9fa;
  color: #495057;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 11px;
  margin: 2px;
  border: 1px solid #e9ecef;
}

.progress-mini {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 8px;
  position: relative;
  overflow: hidden;
}

.progress-fill {
  height: 4px;
  background: linear-gradient(90deg, #4A90E2, #7BB3F0);
  border-radius: 2px;
  transition: width 0.3s ease;
}

.progress-mini span {
  font-size: 11px;
  color: #6c757d;
  margin-top: 4px;
  display: block;
}

.result-mini {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.step-indicator {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.step-number {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #0066cc 0%, #5ac8fa 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 700;
  box-shadow: 0 8px 24px rgba(0, 102, 204, 0.3);
}

.step-icon {
  width: 48px;
  height: 48px;
  background: white;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #0066cc;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(0, 102, 204, 0.2);
}

.step-content {
  flex: 1;
  background: white;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.step-content h3 {
  font-size: 28px;
  font-weight: 700;
  color: #1d1d1f;
  margin: 0 0 16px;
  line-height: 1.2;
}

.step-content p {
  font-size: 16px;
  line-height: 1.6;
  color: #4a5568;
  margin-bottom: 24px;
}

.step-features {
  display: flex;
  gap: 12px;
  margin-bottom: 32px;
  flex-wrap: wrap;
}

.feature-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: rgba(0, 102, 204, 0.1);
  color: #0066cc;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  border: 1px solid rgba(0, 102, 204, 0.2);
}

.step-demo {
  background: linear-gradient(135deg, rgba(0, 102, 204, 0.02), rgba(90, 200, 250, 0.02));
  border-radius: 12px;
  padding: 20px;
  border: 1px solid rgba(0, 102, 204, 0.1);
}

.demo-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-field {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: white;
  border-radius: 8px;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.field-label {
  font-size: 14px;
  color: #6c757d;
  font-weight: 500;
}

.field-value {
  font-size: 14px;
  color: #1d1d1f;
  font-weight: 600;
}

.analysis-visual {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.progress-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.progress-label {
  font-size: 14px;
  color: #6c757d;
  min-width: 80px;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background: rgba(0, 0, 0, 0.1);
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #0066cc, #5ac8fa);
  border-radius: 4px;
  animation: progress-fill 2s ease-out;
}

@keyframes progress-fill {
  from {
    width: 0;
  }
}

.results-preview {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.result-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: white;
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.university-logo {
  font-size: 24px;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 102, 204, 0.1);
  border-radius: 12px;
}

.university-info {
  flex: 1;
}

.university-name {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 4px;
}

.match-score {
  font-size: 14px;
  color: #0066cc;
  font-weight: 500;
}

.admission-rate {
  font-size: 12px;
  color: #6c757d;
}

.process-cta {
  text-align: center;
  padding: 32px 24px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.process-cta h3 {
  font-size: 32px;
  font-weight: 700;
  color: #1d1d1f;
  margin: 0 0 16px;
  line-height: 1.2;
}

.process-cta p {
  font-size: 18px;
  color: #6c757d;
  margin: 0 0 32px;
}

.cta-button {
  padding: 16px 32px;
  font-size: 18px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #0066cc 0%, #5ac8fa 100%);
  border: none;
  transition: all 0.3s ease;
}

.cta-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 102, 204, 0.3);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .process-step {
    flex-direction: column !important;
    text-align: center;
    gap: 30px;
  }

  .timeline-line {
    display: none;
  }

  .step-indicator {
    flex-direction: row;
  }
}

@media (max-width: 768px) {
  .section-header h2 {
    font-size: 36px;
  }

  .step-content {
    padding: 24px;
  }

  .step-content h3 {
    font-size: 24px;
  }

  .step-features {
    justify-content: center;
  }

  .process-cta {
    padding: 40px 24px;
  }

  .process-cta h3 {
    font-size: 24px;
  }
}
</style>
