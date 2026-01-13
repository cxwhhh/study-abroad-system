<template>
  <div class="loading-overlay">
    <div class="loading-content">
      <!-- 核心动画区域 -->
      <div class="animation-core">
        <!-- 旋转光环 -->
        <div class="ring ring-outer"></div>
        <div class="ring ring-inner"></div>
        
        <!-- 中心Logo -->
        <div class="logo-container">
          <img src="/images/ai-avatar1.png" alt="AI Logo" class="ai-logo" />
          <div class="pulse-effect"></div>
        </div>
        
        <!-- 粒子效果 (使用CSS模拟) -->
        <div class="particles">
          <span v-for="n in 8" :key="n" class="particle" :style="{ '--i': n }"></span>
        </div>
      </div>

      <!-- 文字提示区域 -->
      <div class="text-container">
        <h3 class="main-title">AI 智能选校引擎正在运行</h3>
        <div class="typing-container">
          <span class="typing-text">{{ currentStatusText }}</span>
          <span class="cursor">|</span>
        </div>
      </div>

      <!-- 进度条 -->
      <div class="progress-container">
        <div class="progress-bar">
          <div class="progress-fill"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

const statusMessages = [
  "正在分析您的学术背景...",
  "正在检索全球院校数据库...",
  "正在比对历年录取数据...",
  "正在计算录取概率...",
  "正在生成个性化推荐方案..."
];

const currentStatusText = ref(statusMessages[0]);
let messageIndex = 0;
let intervalId = null;

onMounted(() => {
  intervalId = setInterval(() => {
    messageIndex = (messageIndex + 1) % statusMessages.length;
    currentStatusText.value = statusMessages[messageIndex];
  }, 2000);
});

onUnmounted(() => {
  if (intervalId) clearInterval(intervalId);
});
</script>

<style scoped>
.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  min-height: 400px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 100;
  border-radius: 16px;
}

.loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 32px;
}

/* 核心动画区域 */
.animation-core {
  position: relative;
  width: 160px;
  height: 160px;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 旋转光环 */
.ring {
  position: absolute;
  border-radius: 50%;
  border: 2px solid transparent;
}

.ring-outer {
  width: 100%;
  height: 100%;
  border-top-color: #C5A059;
  border-right-color: rgba(197, 160, 89, 0.3);
  animation: spin 3s linear infinite;
}

.ring-inner {
  width: 70%;
  height: 70%;
  border-bottom-color: #A88442;
  border-left-color: rgba(168, 132, 66, 0.3);
  animation: spin 2s linear infinite reverse;
}

/* 中心Logo */
.logo-container {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 0 20px rgba(197, 160, 89, 0.2);
  z-index: 2;
}

.ai-logo {
  width: 50px;
  height: 50px;
  object-fit: contain;
  animation: breathe 3s ease-in-out infinite;
}

.pulse-effect {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: rgba(197, 160, 89, 0.2);
  animation: pulse 2s ease-out infinite;
  z-index: -1;
}

/* 粒子效果 */
.particles {
  position: absolute;
  width: 100%;
  height: 100%;
}

.particle {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 6px;
  height: 6px;
  background: #C5A059;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  animation: particleMove 2s ease-out infinite;
  animation-delay: calc(var(--i) * 0.2s);
  opacity: 0;
}

/* 文字区域 */
.text-container {
  text-align: center;
}

.main-title {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 8px 0;
  letter-spacing: 1px;
}

.typing-container {
  font-size: 14px;
  color: #666;
  min-height: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.typing-text {
  animation: fadeIn 0.5s ease-out;
}

.cursor {
  display: inline-block;
  margin-left: 2px;
  animation: blink 1s step-end infinite;
  color: #C5A059;
  font-weight: bold;
}

/* 进度条 */
.progress-container {
  width: 240px;
  height: 4px;
  background: #f0f0f0;
  border-radius: 2px;
  overflow: hidden;
  position: relative;
}

.progress-bar {
  width: 100%;
  height: 100%;
}

.progress-fill {
  width: 30%;
  height: 100%;
  background: linear-gradient(90deg, #C5A059, #F1E7CF, #C5A059);
  background-size: 200% 100%;
  animation: progressMove 1.5s ease-in-out infinite;
  border-radius: 2px;
}

/* 动画定义 */
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes breathe {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.1); opacity: 0.8; }
}

@keyframes pulse {
  0% { transform: translate(-50%, -50%) scale(1); opacity: 0.6; }
  100% { transform: translate(-50%, -50%) scale(1.5); opacity: 0; }
}

@keyframes particleMove {
  0% {
    transform: translate(-50%, -50%) rotate(calc(var(--i) * 45deg)) translateY(0);
    opacity: 0;
  }
  20% {
    opacity: 1;
  }
  100% {
    transform: translate(-50%, -50%) rotate(calc(var(--i) * 45deg)) translateY(80px);
    opacity: 0;
  }
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

@keyframes progressMove {
  0% { transform: translateX(-100%); background-position: 100% 0; }
  50% { background-position: 0 0; }
  100% { transform: translateX(400%); background-position: -100% 0; }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(5px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
