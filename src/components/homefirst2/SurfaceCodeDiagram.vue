<template>
  <div class="diagram-container">
    <h3 class="diagram-title">Interactive: Surface Code Detection</h3>
    <p class="diagram-description">
      Click the grey <strong>Data Qubits</strong> to inject errors. Watch the colored <strong>Stabilizers</strong> light
      up when they detect an odd number of errors.
    </p>

    <div class="grid-container">
      <!-- Grid Lines -->
      <div class="grid-lines">
        <div class="inner-box"></div>
        <div class="h-line"></div>
        <div class="v-line"></div>
      </div>

      <!-- Stabilizers -->
      <div v-for="stab in stabilizers" :key="'stab-' + stab.id" class="stabilizer" :class="[
        stab.type === 'Z' ? 'bg-blue' : 'bg-red',
        activeStabilizers.includes(stab.id) ? 'active' : 'inactive'
      ]" :style="{ left: stab.x, top: stab.y }">
        {{ stab.type }}
      </div>

      <!-- Data Qubits -->
      <button v-for="q in dataQubits" :key="'data-' + q.id" class="data-qubit"
        :class="{ 'active': errors.includes(q.id) }" :style="{ left: q.x, top: q.y }" @click="toggleError(q.id)">
        <el-icon v-if="errors.includes(q.id)">
          <Warning />
        </el-icon>
      </button>
    </div>

    <div class="legend">
      <div class="legend-item">
        <div class="dot error"></div> Error
      </div>
      <div class="legend-item">
        <div class="dot z-check"></div> Z-Check
      </div>
      <div class="legend-item">
        <div class="dot x-check"></div> X-Check
      </div>
    </div>

    <div class="status-text">
      {{ errors.length === 0 ? "System is stable." : `Detected ${activeStabilizers.length} parity violations.` }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { Warning } from '@element-plus/icons-vue';

const errors = ref([]);

const stabilizers = [
  { id: 0, x: '50%', y: '20%', type: 'Z' },
  { id: 1, x: '20%', y: '50%', type: 'X' },
  { id: 2, x: '80%', y: '50%', type: 'X' },
  { id: 3, x: '50%', y: '80%', type: 'Z' },
];

const dataQubits = [
  { id: 0, x: '20%', y: '20%' }, { id: 1, x: '80%', y: '20%' },
  { id: 4, x: '50%', y: '50%' }, // Center
  { id: 2, x: '20%', y: '80%' }, { id: 3, x: '80%', y: '80%' },
];

const adjacency = {
  0: [0, 1],
  1: [0, 2],
  2: [1, 3],
  3: [2, 3],
  4: [0, 1, 2, 3],
};

const toggleError = (id) => {
  if (errors.value.includes(id)) {
    errors.value = errors.value.filter(e => e !== id);
  } else {
    errors.value.push(id);
  }
};

const activeStabilizers = computed(() => {
  return [0, 1, 2, 3].filter(stabId => {
    let errorCount = 0;
    Object.entries(adjacency).forEach(([dataId, stabs]) => {
      if (errors.value.includes(parseInt(dataId)) && stabs.includes(stabId)) {
        errorCount++;
      }
    });
    return errorCount % 2 !== 0;
  });
});
</script>

<style scoped>
.diagram-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
  background-color: white;
  border-radius: 0.75rem;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  border: 1px solid #e7e5e4;
  margin: 2rem 0;
  font-family: serif;
}

.diagram-title {
  font-size: 1.25rem;
  margin-bottom: 1rem;
  color: #292524;
}

.diagram-description {
  font-size: 0.875rem;
  color: #78716c;
  margin-bottom: 1.5rem;
  text-align: center;
  max-width: 28rem;
  font-family: sans-serif;
}

.grid-container {
  position: relative;
  width: 16rem;
  height: 16rem;
  background-color: #F5F4F0;
  border-radius: 0.5rem;
  border: 1px solid #e7e5e4;
  padding: 1rem;
}

.grid-lines {
  position: absolute;
  inset: 0;
  pointer-events: none;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0.2;
}

.inner-box {
  width: 66.66%;
  height: 66.66%;
  border: 1px solid #a8a29e;
}

.h-line {
  position: absolute;
  width: 100%;
  height: 1px;
  background-color: #a8a29e;
}

.v-line {
  position: absolute;
  height: 100%;
  width: 1px;
  background-color: #a8a29e;
}

.stabilizer {
  position: absolute;
  width: 2.5rem;
  height: 2.5rem;
  margin-left: -1.25rem;
  margin-top: -1.25rem;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 0.75rem;
  font-weight: bold;
  border-radius: 0.125rem;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
  font-family: sans-serif;
}

.stabilizer.bg-blue {
  background-color: #3b82f6;
}

.stabilizer.bg-red {
  background-color: #ef4444;
}

.stabilizer.inactive {
  background-color: #d6d3d1;
  opacity: 0.4;
}

.stabilizer.active {
  opacity: 1;
  transform: scale(1.1);
  box-shadow: 0 0 0 4px #e7e5e4;
}

.data-qubit {
  position: absolute;
  width: 2rem;
  height: 2rem;
  margin-left: -1rem;
  margin-top: -1rem;
  border-radius: 50%;
  border: 2px solid #d6d3d1;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  z-index: 10;
  cursor: pointer;
  background-color: white;
}

.data-qubit:hover {
  border-color: #78716c;
}

.data-qubit.active {
  background-color: #292524;
  border-color: #1c1917;
  color: #C5A059;
}

.legend {
  margin-top: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  font-size: 0.75rem;
  font-family: monospace;
  color: #78716c;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.dot {
  width: 0.75rem;
  height: 0.75rem;
}

.dot.error {
  background-color: #292524;
  border-radius: 50%;
}

.dot.z-check {
  background-color: #3b82f6;
  border-radius: 0.125rem;
}

.dot.x-check {
  background-color: #ef4444;
  border-radius: 0.125rem;
}

.status-text {
  margin-top: 1rem;
  height: 1.5rem;
  font-size: 0.875rem;
  font-family: serif;
  font-style: italic;
  color: #57534e;
}
</style>
