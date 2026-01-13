<template>
  <section class="data-section">
    <div class="container">
      <div class="section-header">
        <span class="section-badge">数据实力</span>
        <h2>用数据说话</h2>
        <p>基于真实数据的AI选校系统，让每一个推荐都有据可依</p>
      </div>

      <div class="stats-grid">
        <!-- 核心数据 -->
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon>
              <Trophy />
            </el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">500K+</div>
            <div class="stat-label">成功案例</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">
            <el-icon>
              <TrendCharts />
            </el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">95%</div>
            <div class="stat-label">录取成功率</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">
            <el-icon>
              <School />
            </el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">2K+</div>
            <div class="stat-label">合作院校</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">
            <el-icon>
              <User />
            </el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">50</div>
            <div class="stat-label">专业顾问</div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { Trophy, TrendCharts, School, User } from '@element-plus/icons-vue'
</script>

<style scoped>
.data-section {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 50%, #e9ecef 100%);
  padding: 40px 0;
  position: relative;
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
  line-height: 1.5;
  color: #6c757d;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }
}

@media (max-width: 480px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px 20px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #4A90E2 0%, #7BB3F0 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  color: white;
  font-size: 20px;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 8px;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #6c757d;
  font-weight: 500;
}
</style>
