<template>
  <div class="materials-progress-test">
    <h2>材料进度测试</h2>
    
    <div class="test-section">
      <h3>测试参数</h3>
      <div class="input-group">
        <label>学生ID:</label>
        <input v-model="studentId" type="number" />
      </div>
      <div class="input-group">
        <label>申请ID:</label>
        <input v-model="applicationId" type="number" />
      </div>
      <button @click="testMaterialsProgress" :disabled="loading">
        {{ loading ? '计算中...' : '测试材料进度' }}
      </button>
    </div>

    <div class="results-section" v-if="result !== null">
      <h3>测试结果</h3>
      <div class="progress-display">
        <div class="progress-text">材料进度: {{ result }}%</div>
        <div class="progress-bar">
          <div class="progress-fill" :style="{ width: `${result}%` }"></div>
        </div>
      </div>
    </div>

    <div class="api-response-section" v-if="apiResponse">
      <h3>API响应数据</h3>
      <pre>{{ JSON.stringify(apiResponse, null, 2) }}</pre>
    </div>

    <div class="logs-section">
      <h3>日志</h3>
      <div class="log-item" v-for="(log, index) in logs" :key="index">
        {{ log }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const studentId = ref(1)
const applicationId = ref(1)
const loading = ref(false)
const result = ref(null)
const apiResponse = ref(null)
const logs = ref([])

const addLog = (message) => {
  logs.value.push(`${new Date().toLocaleTimeString()}: ${message}`)
}

const calculateMaterialsProgress = async (studentId, applicationId) => {
  try {
    addLog(`🔍 开始计算材料进度 - 学生ID: ${studentId}, 申请ID: ${applicationId}`)
    
    // 如果没有学生ID或申请ID，返回0
    if (!studentId || !applicationId) {
      addLog('❌ 缺少学生ID或申请ID')
      return 0
    }

    // 获取学生的材料状态
    const apiUrl = `/api/files/admin/materials/student/${studentId}/application/${applicationId}`
    addLog(`📡 调用API: ${apiUrl}`)
    
    const response = await fetch(apiUrl)
    addLog(`📡 API响应状态: ${response.status} ${response.statusText}`)
    
    if (!response.ok) {
      addLog(`⚠️ API响应失败，状态码: ${response.status}`)
      return 0
    }

    const result = await response.json()
    addLog(`📊 API返回数据: ${JSON.stringify(result, null, 2)}`)
    apiResponse.value = result
    
    if (!result.success || !result.data) {
      addLog(`❌ API返回数据无效 - success: ${result.success}, data: ${result.data}`)
      return 0
    }

    // 定义必需的材料类型
    const requiredMaterials = [
      'passport',           // 护照
      'id_card',           // 身份证  
      'transcript',        // 成绩单
      'language_scores',   // 语言成绩
      'personal_statement', // 个人陈述
      'recommendation_letter', // 推荐信
      'resume'             // 简历
    ]

    addLog(`📋 检查的必需材料: ${requiredMaterials.join(', ')}`)
    addLog(`📊 材料数据结构: ${Object.keys(result.data).join(', ')}`)

    // 计算已上传的必需材料数量
    let uploadedCount = 0
    requiredMaterials.forEach(materialType => {
      const materialData = result.data[materialType]
      addLog(`📄 ${materialType}: ${JSON.stringify(materialData)}`)
      
      if (materialData && materialData.uploaded) {
        uploadedCount++
        addLog(`✅ ${materialType} 已上传`)
      } else {
        addLog(`❌ ${materialType} 未上传`)
      }
    })

    addLog(`📊 已上传材料数量: ${uploadedCount}/${requiredMaterials.length}`)

    // 计算进度
    let progress
    if (uploadedCount === requiredMaterials.length) {
      progress = 100
      addLog('🎉 所有材料已上传，进度100%')
    } else {
      progress = Math.round((uploadedCount / requiredMaterials.length) * 100)
      addLog(`📊 计算进度: ${progress}%`)
    }
    
    return progress
  } catch (error) {
    addLog(`💥 计算材料进度失败: ${error.message}`)
    console.error('计算材料进度失败:', error)
    return 0
  }
}

const testMaterialsProgress = async () => {
  loading.value = true
  result.value = null
  apiResponse.value = null
  logs.value = []
  
  try {
    const progress = await calculateMaterialsProgress(studentId.value, applicationId.value)
    result.value = progress
    addLog(`🎯 最终结果: ${progress}%`)
  } catch (error) {
    addLog(`💥 测试失败: ${error.message}`)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.materials-progress-test {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.test-section, .results-section, .api-response-section, .logs-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.input-group {
  margin-bottom: 10px;
}

.input-group label {
  display: inline-block;
  width: 80px;
  margin-right: 10px;
}

.input-group input {
  padding: 5px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.progress-display {
  margin-bottom: 20px;
}

.progress-text {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
}

.progress-bar {
  width: 100%;
  height: 20px;
  background-color: #f0f0f0;
  border-radius: 10px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background-color: #28a745;
  transition: width 0.3s ease;
}

pre {
  background-color: #f8f9fa;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
  font-size: 12px;
}

.log-item {
  padding: 5px;
  border-bottom: 1px solid #eee;
  font-family: monospace;
  font-size: 12px;
}

.log-item:last-child {
  border-bottom: none;
}
</style>