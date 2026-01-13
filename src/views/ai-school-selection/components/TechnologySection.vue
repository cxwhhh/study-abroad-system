<template>
  <section class="technology-section">
    <div class="container">
      <div class="section-header">
        <span class="section-badge">智能核心</span>
        <h2>先进算法驱动</h2>
        <p>我们的AI选校系统采用多层神经网络和自然语言处理技术，通过持续学习和优化，实现95%以上的匹配精度。</p>
      </div>

      <div class="tech-grid">
        <div class="tech-visual">
          <div class="visual-container">
            <div class="neural-network">
              <div class="network-layer layer-1">
                <div class="node n1-1"></div>
                <div class="node n1-2"></div>
                <div class="node n1-3"></div>
                <div class="node n1-4"></div>
              </div>
              <div class="network-layer layer-2">
                <div class="node n2-1"></div>
                <div class="node n2-2"></div>
                <div class="node n2-3"></div>
              </div>
              <div class="network-layer layer-3">
                <div class="node n3-1"></div>
                <div class="node n3-2"></div>
                <div class="node n3-3"></div>
                <div class="node n3-4"></div>
                <div class="node n3-5"></div>
              </div>
              <div class="connections">
                <svg width="100%" height="100%" class="connections-svg">
                  <!-- SVG connections will be drawn with JavaScript -->
                </svg>
              </div>
            </div>
          </div>
        </div>

        <div class="tech-features">
          <div class="feature-card">
            <div class="feature-icon">
              <el-icon>
                <DataAnalysis />
              </el-icon>
            </div>
            <div class="feature-content">
              <h3>深度学习算法</h3>
              <p>基于数百万条历史录取数据训练的神经网络，能够精确预测申请结果，准确率高达95%</p>
            </div>
          </div>

          <div class="feature-card">
            <div class="feature-icon">
              <el-icon>
                <Connection />
              </el-icon>
            </div>
            <div class="feature-content">
              <h3>自然语言处理</h3>
              <p>智能分析申请文书和个人陈述，评估与目标院校的匹配度，提供个性化建议</p>
            </div>
          </div>

          <div class="feature-card">
            <div class="feature-icon">
              <el-icon>
                <TrendCharts />
              </el-icon>
            </div>
            <div class="feature-content">
              <h3>精准预测模型</h3>
              <p>综合考虑学术成绩、语言能力、专业背景等因素，提供精准录取概率和选校建议</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { DataAnalysis, Connection, TrendCharts } from '@element-plus/icons-vue'
import { onMounted } from 'vue'

onMounted(() => {
  // 创建神经网络连接线
  setTimeout(() => {
    createNeuralConnections()
  }, 100)
})

const createNeuralConnections = () => {
  const svg = document.querySelector('.connections-svg')
  if (!svg) return

  // 清除现有元素
  while (svg.firstChild) {
    svg.removeChild(svg.firstChild)
  }

  const networkContainer = document.querySelector('.neural-network')
  if (!networkContainer) return

  const containerRect = networkContainer.getBoundingClientRect()

  // 第一层到第二层的连接
  const layer1Nodes = document.querySelectorAll('.layer-1 .node')
  const layer2Nodes = document.querySelectorAll('.layer-2 .node')

  layer1Nodes.forEach((node1, i1) => {
    const node1Rect = node1.getBoundingClientRect()
    const x1 = node1Rect.left - containerRect.left + node1Rect.width / 2
    const y1 = node1Rect.top - containerRect.top + node1Rect.height / 2

    layer2Nodes.forEach((node2, i2) => {
      const node2Rect = node2.getBoundingClientRect()
      const x2 = node2Rect.left - containerRect.left + node2Rect.width / 2
      const y2 = node2Rect.top - containerRect.top + node2Rect.height / 2

      const delay = (i1 * layer2Nodes.length + i2) * 0.15

      const line = document.createElementNS('http://www.w3.org/2000/svg', 'line')
      line.setAttribute('x1', x1)
      line.setAttribute('y1', y1)
      line.setAttribute('x2', x2)
      line.setAttribute('y2', y2)
      line.setAttribute('stroke', 'rgba(52, 152, 219, 0.4)')
      line.setAttribute('stroke-width', '1.5')
      line.setAttribute('stroke-dasharray', '5,5')
      line.style.animation = `connection-pulse 2s ease-in-out infinite ${delay}s`

      svg.appendChild(line)
    })
  })

  // 第二层到第三层的连接
  const layer3Nodes = document.querySelectorAll('.layer-3 .node')

  layer2Nodes.forEach((node2, i2) => {
    const node2Rect = node2.getBoundingClientRect()
    const x2 = node2Rect.left - containerRect.left + node2Rect.width / 2
    const y2 = node2Rect.top - containerRect.top + node2Rect.height / 2

    layer3Nodes.forEach((node3, i3) => {
      const node3Rect = node3.getBoundingClientRect()
      const x3 = node3Rect.left - containerRect.left + node3Rect.width / 2
      const y3 = node3Rect.top - containerRect.top + node3Rect.height / 2

      const delay = (i2 * layer3Nodes.length + i3) * 0.15 + 1

      const line = document.createElementNS('http://www.w3.org/2000/svg', 'line')
      line.setAttribute('x1', x2)
      line.setAttribute('y1', y2)
      line.setAttribute('x2', x3)
      line.setAttribute('y2', y3)
      line.setAttribute('stroke', 'rgba(52, 152, 219, 0.4)')
      line.setAttribute('stroke-width', '1.5')
      line.setAttribute('stroke-dasharray', '5,5')
      line.style.animation = `connection-pulse 2s ease-in-out infinite ${delay}s`

      svg.appendChild(line)
    })
  })
}
</script>

<style scoped>
/* Technology Section */
.technology-section {
  background: linear-gradient(135deg, #ecf0f1 0%, #bdc3c7 50%, #95a5a6 100%);
  position: relative;
  overflow: hidden;
  padding: 40px 0;
  color: #2c3e50;
}

.technology-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image:
    radial-gradient(circle at 25% 25%, rgba(52, 152, 219, 0.05) 0%, transparent 50%),
    radial-gradient(circle at 75% 75%, rgba(52, 152, 219, 0.05) 0%, transparent 50%);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  position: relative;
  z-index: 2;
}

.section-header {
  text-align: center;
  max-width: 800px;
  margin: 0 auto 60px;
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

.tech-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 80px;
  align-items: center;
}

.tech-visual {
  position: relative;
}

.visual-container {
  position: relative;
  width: 100%;
  height: 400px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8) 0%, rgba(236, 240, 241, 0.6) 100%);
  border-radius: 20px;
  border: 1px solid rgba(189, 195, 199, 0.3);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  backdrop-filter: blur(10px);
}

/* Neural Network Visualization */
.neural-network {
  position: relative;
  width: 300px;
  height: 200px;
}

.network-layer {
  position: absolute;
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-items: center;
}

.layer-1 {
  left: 0;
  top: 50%;
  transform: translateY(-50%);
}

.layer-2 {
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
}

.layer-3 {
  right: 0;
  top: 50%;
  transform: translateY(-50%);
}

.node {
  width: 12px;
  height: 12px;
  background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
  border-radius: 50%;
  position: relative;
  animation: node-pulse 2s ease-in-out infinite;
}

.node::before {
  content: '';
  position: absolute;
  top: -4px;
  left: -4px;
  right: -4px;
  bottom: -4px;
  background: linear-gradient(135deg, rgba(52, 152, 219, 0.3) 0%, rgba(41, 128, 185, 0.1) 100%);
  border-radius: 50%;
  z-index: -1;
}

@keyframes node-pulse {

  0%,
  100% {
    transform: scale(1);
  }

  50% {
    transform: scale(1.2);
  }
}

@keyframes node-glow {

  0%,
  100% {
    opacity: 0.3;
    transform: scale(1);
  }

  50% {
    opacity: 0.8;
    transform: scale(1.5);
  }
}

/* 错开动画 */
.n1-2,
.n2-2,
.n3-2 {
  animation-delay: 0.5s;
}

.n1-3,
.n2-3,
.n3-3 {
  animation-delay: 1s;
}

.n1-4,
.n3-4 {
  animation-delay: 1.5s;
}

.n3-5 {
  animation-delay: 2s;
}

.connections-svg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

@keyframes connection-pulse {

  0%,
  100% {
    stroke-dashoffset: 10;
    opacity: 0.4;
  }

  50% {
    stroke-dashoffset: 0;
    opacity: 1;
  }
}

/* Tech Features */
.tech-features {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.feature-card {
  display: flex;
  gap: 20px;
  align-items: flex-start;
  padding: 32px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(44, 62, 80, 0.1);
  border: 1px solid rgba(189, 195, 199, 0.3);
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 60px rgba(44, 62, 80, 0.15);
  background: rgba(255, 255, 255, 0.95);
  border-color: rgba(52, 152, 219, 0.4);
}

.feature-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #3498db, #2980b9);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-size: 24px;
  flex-shrink: 0;
}

.feature-content h3 {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 8px 0;
  line-height: 1.3;
}

.feature-content p {
  font-size: 14px;
  line-height: 1.5;
  color: #34495e;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .tech-grid {
    grid-template-columns: 1fr;
    gap: 40px;
  }
}

@media (max-width: 768px) {
  .section-header h2 {
    font-size: 36px;
  }

  .feature-card {
    flex-direction: column;
    text-align: center;
  }

  .neural-network {
    width: 250px;
    height: 150px;
  }
}
</style>
