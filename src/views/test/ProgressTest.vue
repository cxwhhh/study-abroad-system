<template>
  <div class="progress-test">
    <h2>申请进度状态测试</h2>
    
    <div class="status-selector">
      <h3>选择测试状态：</h3>
      <el-radio-group v-model="testStatus" @change="updateProgress">
        <el-radio value="selected">已选择学校</el-radio>
        <el-radio value="submitted">已提交材料</el-radio>
        <el-radio value="under_review">材料审核中</el-radio>
        <el-radio value="interview">面试阶段</el-radio>
        <el-radio value="admitted">已录取</el-radio>
        <el-radio value="rejected">被拒绝</el-radio>
      </el-radio-group>
    </div>

    <div class="progress-display">
      <h3>当前状态：{{ testStatus }}</h3>
      <div class="progress-steps">
        <div 
          v-for="(step, index) in progressSteps" 
          :key="index"
          class="step-item"
          :class="step.status"
        >
          <div class="step-number">{{ index + 1 }}</div>
          <div class="step-content">
            <div class="step-title">{{ step.title }}</div>
            <div class="step-description">{{ step.description }}</div>
            <div class="step-date" v-if="step.date">{{ step.date }}</div>
          </div>
        </div>
      </div>
      
      <div class="overall-progress">
        <h4>整体进度：{{ overallProgress }}%</h4>
        <el-progress :percentage="overallProgress" :color="getProgressColor()" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const testStatus = ref('selected')
const progressSteps = ref([])
const overallProgress = ref(0)

// 定义标准的申请进度步骤（根据状态映射）
const standardSteps = [
  { key: 'selected', title: '已选择学校', description: '您已选择学校和专业' },
  { key: 'submitted', title: '已提交材料', description: '您的申请材料已成功提交' },
  { key: 'under_review', title: '材料审核中', description: '学校正在审核您的申请材料' },
  { key: 'interview', title: '面试阶段', description: '参加学校安排的面试或考试' },
  { key: 'admitted', title: '已录取', description: '恭喜！您已被学校录取' }
]

// 状态到步骤索引的映射
const statusToStepIndex = {
  'selected': 0,
  'submitted': 1,
  'under_review': 2,
  'interview': 3,
  'admitted': 4,
  'rejected': 4 // rejected状态也显示到第4步，但状态为error
}

// 转换申请状态为进度条数据
const convertToProgressSteps = (currentStatus) => {
  console.log('当前申请状态:', currentStatus)
  
  // 获取当前状态对应的步骤索引
  const currentStepIndex = statusToStepIndex[currentStatus] || 0
  
  // 生成进度步骤
  const steps = standardSteps.map((standardStep, index) => {
    let stepStatus = 'pending'
    let stepDate = ''
    let stepDescription = standardStep.description
    
    if (currentStatus === 'rejected') {
      // 处理rejected状态的特殊逻辑
      if (index < currentStepIndex) {
        stepStatus = 'completed'
      } else if (index === currentStepIndex) {
        stepStatus = 'error'
        stepDescription = '很遗憾，您的申请未能通过'
      } else {
        stepStatus = 'pending'
      }
    } else {
      // 正常状态的处理逻辑
      if (index < currentStepIndex) {
        stepStatus = 'completed'
      } else if (index === currentStepIndex) {
        stepStatus = 'current'
      } else {
        stepStatus = 'pending'
      }
    }
    
    return {
      title: standardStep.title,
      description: stepDescription,
      date: stepDate,
      status: stepStatus
    }
  })
  
  // 计算整体进度
  let completedSteps = 0
  let currentSteps = 0
  
  if (currentStatus === 'rejected') {
    // rejected状态的进度计算
    completedSteps = currentStepIndex
    overallProgress.value = Math.round((completedSteps / steps.length) * 100)
  } else {
    // 正常状态的进度计算
    completedSteps = steps.filter(step => step.status === 'completed').length
    currentSteps = steps.filter(step => step.status === 'current').length
    overallProgress.value = Math.round(((completedSteps + currentSteps * 0.5) / steps.length) * 100)
  }
  
  progressSteps.value = steps
  console.log('生成的进度步骤:', steps)
  console.log('整体进度:', overallProgress.value)
}

// 更新进度显示
const updateProgress = () => {
  convertToProgressSteps(testStatus.value)
}

// 获取进度条颜色
const getProgressColor = () => {
  if (testStatus.value === 'rejected') {
    return '#f56c6c'
  } else if (testStatus.value === 'admitted') {
    return '#67c23a'
  } else {
    return '#409eff'
  }
}

onMounted(() => {
  updateProgress()
})
</script>

<style scoped>
.progress-test {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.status-selector {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.progress-display {
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.progress-steps {
  margin: 20px 0;
}

.step-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
  padding: 15px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.step-item.completed {
  background-color: #f0f9ff;
  border-left: 4px solid #67c23a;
}

.step-item.current {
  background-color: #fff7e6;
  border-left: 4px solid #409eff;
}

.step-item.error {
  background-color: #fef0f0;
  border-left: 4px solid #f56c6c;
}

.step-item.pending {
  background-color: #f9f9f9;
  border-left: 4px solid #ddd;
}

.step-number {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-right: 15px;
  flex-shrink: 0;
}

.step-item.completed .step-number {
  background-color: #67c23a;
  color: white;
}

.step-item.current .step-number {
  background-color: #409eff;
  color: white;
}

.step-item.error .step-number {
  background-color: #f56c6c;
  color: white;
}

.step-item.pending .step-number {
  background-color: #ddd;
  color: #666;
}

.step-content {
  flex: 1;
}

.step-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
}

.step-description {
  color: #666;
  font-size: 14px;
  margin-bottom: 5px;
}

.step-date {
  color: #999;
  font-size: 12px;
}

.overall-progress {
  margin-top: 30px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
}
</style>