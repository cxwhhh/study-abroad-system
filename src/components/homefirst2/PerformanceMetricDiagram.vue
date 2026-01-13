<template>
  <div class="performance-container">
    <div class="content-side">
      <h3 class="chart-title">Performance vs Standard</h3>
      <p class="chart-description">
        AlphaQubit consistently achieves lower logical error rates (LER) than the standard Minimum-Weight Perfect
        Matching (MWPM) decoder.
      </p>

      <div class="button-group">
        <button v-for="d in [3, 5, 11]" :key="d" class="distance-btn" :class="{ 'active': distance === d }"
          @click="distance = d">
          Distance {{ d }}
        </button>
      </div>

      <div class="metric-label">
        <el-icon class="chart-icon">
          <TrendCharts />
        </el-icon>
        <span>LOGICAL ERROR RATE (LOWER IS BETTER)</span>
      </div>
    </div>

    <div class="chart-side">
      <!-- Background Grid Lines -->
      <div class="grid-lines">
        <div class="h-line"></div>
        <div class="h-line"></div>
        <div class="h-line"></div>
        <div class="h-line"></div>
      </div>

      <!-- MWPM Bar -->
      <div class="bar-column">
        <div class="bar-wrapper">
          <div class="value-tag standard">{{ formatValue(currentData.mwpm) }}</div>
          <div class="bar standard-bar" :style="{ height: (currentData.mwpm / maxVal) * 100 + '%' }"></div>
        </div>
        <div class="bar-label">Standard</div>
      </div>

      <!-- AlphaQubit Bar -->
      <div class="bar-column">
        <div class="bar-wrapper">
          <div class="value-tag alpha">{{ formatValue(currentData.alpha) }}</div>
          <div class="bar alpha-bar" :style="{ height: Math.max(1, (currentData.alpha / maxVal) * 100) + '%' }">
            <div class="shine"></div>
          </div>
        </div>
        <div class="bar-label alpha-label">AlphaQubit</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { TrendCharts } from '@element-plus/icons-vue';

const distance = ref(5);

const data = {
  3: { mwpm: 3.5, alpha: 2.9 },
  5: { mwpm: 3.6, alpha: 2.75 },
  11: { mwpm: 0.0041, alpha: 0.0009 }
};

const currentData = computed(() => data[distance.value]);

const maxVal = computed(() => {
  return Math.max(currentData.value.mwpm, currentData.value.alpha) * 1.25;
});

const formatValue = (val) => {
  if (val < 0.01) return val.toFixed(4) + '%';
  return val.toFixed(2) + '%';
};
</script>

<style scoped>
.performance-container {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  padding: 2rem;
  background-color: #1c1917;
  color: #f5f5f4;
  border-radius: 0.75rem;
  margin: 2rem 0;
  border: 1px solid #292524;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
  font-family: serif;
}

@media (min-width: 768px) {
  .performance-container {
    flex-direction: row;
    align-items: center;
  }
}

.content-side {
  flex: 1;
  min-width: 240px;
}

.chart-title {
  font-size: 1.25rem;
  margin-bottom: 0.5rem;
  color: #C5A059;
}

.chart-description {
  color: #a8a29e;
  font-size: 0.875rem;
  margin-bottom: 1rem;
  line-height: 1.625;
  font-family: sans-serif;
}

.button-group {
  display: flex;
  gap: 0.5rem;
  margin-top: 1.5rem;
}

.distance-btn {
  padding: 0.375rem 0.75rem;
  border-radius: 0.25rem;
  font-size: 0.875rem;
  font-weight: 500;
  transition: all 0.2s;
  border: 1px solid #44403c;
  background-color: transparent;
  color: #a8a29e;
  cursor: pointer;
  font-family: sans-serif;
}

.distance-btn:hover {
  border-color: #78716c;
  color: #e7e5e4;
}

.distance-btn.active {
  background-color: #C5A059;
  color: #1c1917;
  border-color: #C5A059;
}

.metric-label {
  margin-top: 1.5rem;
  font-family: monospace;
  font-size: 0.75rem;
  color: #78716c;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.chart-icon {
  color: #C5A059;
  font-size: 0.875rem;
}

.chart-side {
  position: relative;
  width: 16rem;
  height: 18rem;
  background-color: rgba(41, 37, 36, 0.5);
  border-radius: 0.75rem;
  border: 1px solid rgba(68, 64, 60, 0.5);
  padding: 1.5rem;
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
}

.grid-lines {
  position: absolute;
  inset: 1.5rem;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  pointer-events: none;
  opacity: 0.1;
}

.h-line {
  width: 100%;
  height: 1px;
  background-color: #a8a29e;
}

.bar-column {
  width: 5rem;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  align-items: center;
  z-index: 10;
}

.bar-wrapper {
  flex: 1;
  width: 100%;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  position: relative;
  margin-bottom: 0.75rem;
}

.value-tag {
  position: absolute;
  top: -1.25rem;
  width: 100%;
  text-align: center;
  font-size: 0.875rem;
  font-family: monospace;
  font-weight: bold;
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
  backdrop-filter: blur(4px);
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.value-tag.standard {
  color: #a8a29e;
  background-color: rgba(28, 25, 23, 0.9);
  border: 1px solid rgba(68, 64, 60, 0.5);
}

.value-tag.alpha {
  color: #C5A059;
  background-color: rgba(28, 25, 23, 0.9);
  border: 1px solid rgba(197, 160, 89, 0.3);
}

.bar {
  width: 100%;
  border-top-left-radius: 0.375rem;
  border-top-right-radius: 0.375rem;
  transition: height 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.standard-bar {
  background-color: #57534e;
  border-top: 1px solid rgba(120, 113, 108, 0.3);
  border-left: 1px solid rgba(120, 113, 108, 0.3);
  border-right: 1px solid rgba(120, 113, 108, 0.3);
}

.alpha-bar {
  background-color: #C5A059;
  box-shadow: 0 0 20px rgba(197, 160, 89, 0.25);
  position: relative;
  overflow: hidden;
}

.shine {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top right, transparent, rgba(255, 255, 255, 0.2));
}

.bar-label {
  height: 1.5rem;
  display: flex;
  align-items: center;
  font-size: 0.75rem;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #78716c;
  font-family: sans-serif;
}

.alpha-label {
  color: #C5A059;
}
</style>
