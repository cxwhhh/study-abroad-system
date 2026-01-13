<template>
  <div class="diagram-container">
    <h3 class="diagram-title">AlphaQubit Architecture</h3>
    <p class="diagram-description">
      The model processes syndrome history using a recurrent transformer, attending to spatial and temporal
      correlations.
    </p>

    <div class="process-flow">
      <!-- Input Stage -->
      <div class="stage">
        <div class="stage-box input-box" :class="{ 'active': step === 0 }">
          <div class="grid-3x3">
            <div v-for="i in 9" :key="i" class="dot" :class="{ 'dark': Math.random() > 0.7 }"></div>
          </div>
        </div>
        <span class="stage-label">Syndrome</span>
      </div>

      <!-- Arrow 1 -->
      <div class="arrow" :class="{ 'visible': step >= 1 }">→</div>

      <!-- Transformer Stage -->
      <div class="stage">
        <div class="stage-box transformer-box" :class="{ 'active': step === 1 || step === 2 }">
          <el-icon class="cpu-icon" :class="{ 'pulse': step === 1 || step === 2 }">
            <Cpu />
          </el-icon>
          <div v-if="step === 1" class="ping-container">
            <div class="ping-line top"></div>
            <div class="ping-line bottom"></div>
          </div>
        </div>
        <span class="stage-label">Transformer</span>
      </div>

      <!-- Arrow 2 -->
      <div class="arrow" :class="{ 'visible': step >= 3 }">→</div>

      <!-- Output Stage -->
      <div class="stage">
        <div class="stage-box output-box" :class="{ 'active': step === 3 }">
          <span v-if="step === 3" class="result-text success">X</span>
          <span v-else class="result-text placeholder">?</span>
        </div>
        <span class="stage-label">Correction</span>
      </div>
    </div>

    <div class="progress-bar">
      <div v-for="s in [0, 1, 2, 3]" :key="s" class="progress-step" :class="{ 'active': step === s }"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { Cpu } from '@element-plus/icons-vue';

const step = ref(0);
let intervalId;

onMounted(() => {
  intervalId = setInterval(() => {
    step.value = (step.value + 1) % 4;
  }, 2000);
});

onUnmounted(() => {
  clearInterval(intervalId);
});
</script>

<style scoped>
.diagram-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
  background-color: #F5F4F0;
  border-radius: 0.75rem;
  border: 1px solid #e7e5e4;
  margin: 2rem 0;
  font-family: serif;
}

.diagram-title {
  font-size: 1.25rem;
  margin-bottom: 1rem;
  color: #1c1917;
}

.diagram-description {
  font-size: 0.875rem;
  color: #57534e;
  margin-bottom: 1.5rem;
  text-align: center;
  max-width: 28rem;
  font-family: sans-serif;
}

.process-flow {
  position: relative;
  width: 100%;
  max-width: 32rem;
  height: 14rem;
  background-color: white;
  border-radius: 0.5rem;
  box-shadow: inset 0 2px 4px 0 rgba(0, 0, 0, 0.05);
  margin-bottom: 1.5rem;
  border: 1px solid #e7e5e4;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 2rem;
  padding: 1rem;
}

.stage {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
}

.stage-box {
  border-radius: 0.5rem;
  border: 2px solid #e7e5e4;
  background-color: #fafaf9;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transition: all 0.5s;
}

.input-box {
  width: 4rem;
  height: 4rem;
}

.input-box.active {
  border-color: #C5A059;
  background-color: rgba(197, 160, 89, 0.1);
}

.transformer-box {
  width: 6rem;
  height: 6rem;
  position: relative;
  overflow: hidden;
}

.transformer-box.active {
  border-color: #292524;
  background-color: #1c1917;
  color: white;
}

.output-box {
  width: 4rem;
  height: 4rem;
}

.output-box.active {
  border-color: #22c55e;
  background-color: #f0fdf4;
}

.stage-label {
  font-size: 0.625rem;
  text-transform: uppercase;
  font-weight: bold;
  letter-spacing: 0.05em;
  color: #78716c;
  font-family: sans-serif;
}

.grid-3x3 {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 0.25rem;
}

.dot {
  width: 0.5rem;
  height: 0.5rem;
  border-radius: 50%;
  background-color: #d6d3d1;
}

.dot.dark {
  background-color: #292524;
}

.cpu-icon {
  font-size: 1.5rem;
  color: #d6d3d1;
  transition: color 0.5s;
}

.cpu-icon.pulse {
  color: #C5A059;
  animation: pulse 2s infinite;
}

.arrow {
  font-size: 1.5rem;
  color: #292524;
  transition: all 0.5s;
  opacity: 0.3;
  transform: translateX(-5px);
}

.arrow.visible {
  opacity: 1;
  transform: translateX(0);
}

.result-text {
  font-size: 1.5rem;
  font-family: serif;
}

.result-text.success {
  color: #16a34a;
}

.result-text.placeholder {
  color: #d6d3d1;
}

.ping-container {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ping-line {
  position: absolute;
  width: 100%;
  height: 1px;
  background-color: #C5A059;
  animation: ping 1s cubic-bezier(0, 0, 0.2, 1) infinite;
}

.ping-line.top {
  top: 33%;
}

.ping-line.bottom {
  top: 66%;
  animation-delay: 0.075s;
}

.progress-bar {
  display: flex;
  gap: 0.5rem;
}

.progress-step {
  height: 0.25rem;
  border-radius: 9999px;
  background-color: #d6d3d1;
  width: 0.5rem;
  transition: all 0.3s;
}

.progress-step.active {
  width: 2rem;
  background-color: #C5A059;
}

@keyframes pulse {

  0%,
  100% {
    opacity: 1;
  }

  50% {
    opacity: .5;
  }
}

@keyframes ping {

  75%,
  100% {
    transform: scale(2);
    opacity: 0;
  }
}
</style>
