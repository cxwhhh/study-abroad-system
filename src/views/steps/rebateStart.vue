<template>
  <div class="r-start-container">
    <!-- 导航栏 -->
    <nav-bar></nav-bar>

    <!-- 主标题区域 -->
    <section class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">申请留学入学奖励</h1>
        <p class="hero-subtitle">验证您的申请信息，获取奖励</p>
      </div>
    </section>

    <!-- 申请进度指示器 -->
    <section class="progress-section">
      <div class="progress-container">
        <div class="progress-steps" :data-step="currentStep">
          <div v-for="(step, index) in steps" :key="index" class="progress-step"
            :class="{ 'active': currentStep === index, 'completed': currentStep > index }">
            <div class="step-indicator">{{ index + 1 }}</div>
            <div class="step-label">{{ step.title }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 申请表单区域 -->
    <section class="form-section">
      <div class="form-container">
        <!-- 步骤1: 申请信息验证 -->
        <div v-if="currentStep === 0" class="form-step application-verification-step">
          <h2 class="step-title">申请信息验证</h2>
          <p class="step-description">请输入您的申请ID和学校专业信息进行验证</p>

          <div class="form-group">
            <label class="form-label">申请ID</label>
            <el-input v-model="formData.applicationId" placeholder="请输入您的申请ID" class="form-input" :disabled="loading" />
          </div>

          <div class="form-group">
            <label class="form-label">申请的学校</label>
            <el-input v-model="formData.selectedSchoolName" placeholder="请输入学校名称" class="form-input"
              :disabled="loading" />
          </div>

          <div class="form-group">
            <label class="form-label">申请的专业</label>
            <el-input v-model="formData.selectedMajorName" placeholder="请输入专业名称" class="form-input"
              :disabled="loading" />
          </div>

          <div class="form-group">
            <el-button type="primary" @click="verifyApplicationInfo" :loading="loading"
              :disabled="!formData.applicationId || !formData.selectedSchoolName || !formData.selectedMajorName"
              class="verify-button">
              验证申请信息
            </el-button>
          </div>

          <!-- 验证成功后显示信息 -->
          <div v-if="applicationVerified" class="verification-success">
            <div class="success-header">
              <el-icon class="success-icon">
                <SuccessFilled />
              </el-icon>
              <h3>验证成功！</h3>
            </div>
          </div>
        </div>

        <!-- 步骤2: 资料上传 -->
        <div v-if="currentStep === 1" class="form-step upload-step">
          <h2 class="step-title">资料上传</h2>
          <p class="step-description">请上传入学证明和录取通知书</p>

          <div class="upload-section">
            <div class="upload-item">
              <div class="upload-label">
                <span class="required-mark">*</span>
                录取验证
              </div>
              <el-upload class="upload-component" action="#" :auto-upload="false" :limit="1"
                :on-change="handleAdmissionLetterUpload">
                <template #trigger>
                  <el-button type="primary" plain>选择文件</el-button>
                </template>
                <template #tip>
                  <div class="upload-tip">请上传学校发放的录取通知书 (PDF格式)</div>
                </template>
              </el-upload>
            </div>

            <div class="upload-item">
              <div class="upload-label">
                <span class="required-mark">*</span>
                入学验证
              </div>
              <el-upload class="upload-component" action="#" :auto-upload="false" :limit="1"
                :on-change="handleEnrollmentProofUpload">
                <template #trigger>
                  <el-button type="primary" plain>选择文件</el-button>
                </template>
                <template #tip>
                  <div class="upload-tip">请上传入学证明文件 (PDF格式)</div>
                </template>
              </el-upload>
            </div>
          </div>
        </div>

        <!-- 步骤3: 确认提交 -->
        <div v-if="currentStep === 2" class="form-step confirm-step">
          <h2 class="step-title">确认提交</h2>
          <p class="step-description">请确认您的申请信息，并同意相关条款</p>

          <div class="summary-section">
            <h3>申请信息摘要</h3>
            <div class="summary-content">
              <div class="summary-row">
                <span class="summary-label">申请ID:</span>
                <span class="summary-value">{{ formData.applicationId }}</span>
              </div>
              <div class="summary-row">
                <span class="summary-label">学校:</span>
                <span class="summary-value">{{ formData.selectedSchoolName }}</span>
              </div>
              <div class="summary-row">
                <span class="summary-label">专业:</span>
                <span class="summary-value">{{ formData.selectedMajorName }}</span>
              </div>
              <div class="summary-row">
                <span class="summary-label">支付方式:</span>
                <span class="summary-value">{{paymentMethods.find(m => m.value === formData.paymentMethod)?.label
                  }}</span>
              </div>
              <div class="summary-row highlight">
                <span class="summary-label">预计奖励金额:</span>
                <span class="summary-value">¥{{ estimatedRebateAmount.toLocaleString() }}</span>
              </div>
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">备注 (选填)</label>
            <el-input v-model="formData.notes" type="textarea" :rows="3" placeholder="请输入备注信息" class="form-input" />
          </div>

          <div class="agreement-section">
            <el-checkbox v-model="formData.agreementAccepted">
              我已阅读并同意 <a href="#" @click="showTerms">《奖励申请条款》</a>
            </el-checkbox>
          </div>
        </div>

        <!-- 步骤4: 支付信息 -->
        <div v-if="currentStep === 3" class="form-step payment-info-step">
          <h2 class="step-title">支付信息</h2>
          <p class="step-description">请填写奖励支付信息，我们将通过您选择的方式发放奖励</p>

          <div class="form-group">
            <label class="form-label">支付方式</label>
            <el-radio-group v-model="formData.paymentMethod" class="payment-methods">
              <el-radio v-for="method in paymentMethods" :key="method.value" :label="method.value">
                {{ method.label }}
              </el-radio>
            </el-radio-group>
          </div>

          <div v-if="formData.paymentMethod === 'bank_transfer'" class="form-group">
            <label class="form-label">银行账号</label>
            <el-input v-model="formData.accountNumber" placeholder="请输入银行账号" class="form-input" />
          </div>

          <div v-if="formData.paymentMethod === 'alipay'" class="form-group">
            <label class="form-label">支付宝账号</label>
            <el-input v-model="formData.digitalAccount" placeholder="请输入支付宝账号" class="form-input" />
          </div>

          <div v-if="formData.paymentMethod === 'wechat'" class="form-group">
            <label class="form-label">微信号</label>
            <el-input v-model="formData.digitalAccount" placeholder="请输入微信号" class="form-input" />
          </div>
        </div>

        <!-- 步骤5: 申请成功 -->
        <div v-if="currentStep === 4" class="form-step success-step">
          <div class="success-content">
            <el-icon class="success-icon-large">
              <SuccessFilled />
            </el-icon>
            <h2>申请提交成功！</h2>
            <p>您的奖励申请已成功提交，我们将在3-5个工作日内审核您的申请。</p>
            <div class="success-actions">
              <el-button type="primary" @click="goToApplications">查看申请状态</el-button>
              <el-button @click="goToHome">返回首页</el-button>
            </div>
          </div>
        </div>

        <!-- 表单底部按钮 -->
        <div class="form-actions" v-if="currentStep < 4">
          <el-button v-if="currentStep > 0" @click="prevStep" plain>
            上一步
          </el-button>
          <el-button type="primary" @click="nextStep" :disabled="!canProceed" :loading="loading">
            {{ currentStep === 3 ? '提交申请' : '下一步' }}
          </el-button>
        </div>
      </div>
    </section>

    <!-- 服务条款对话框 -->
    <el-dialog v-model="termsDialogVisible" title="奖励申请服务条款" width="50%">
      <div class="terms-content">
        <h3>一、服务说明</h3>
        <p>1.1 本服务是指我们为您提供的留学入学奖励申请服务，帮助您获取学校奖励。</p>
        <p>1.2 我们将根据您提供的申请信息，为您提供最高奖励方案。</p>

        <h3>二、申请条件</h3>
        <p>2.1 申请人必须是已获得学校录取通知书的学生。</p>
        <p>2.2 申请人必须已缴纳学费，并能提供相关证明材料。</p>

        <h3>三、奖励规则</h3>
        <p>3.1 奖励金额根据学校、专业和学费金额计算，具体奖励比例以实际审核为准。</p>
        <p>3.2 奖励金额将在申请审核通过后3-5个工作日内发放。</p>

        <h3>四、申请材料</h3>
        <p>4.1 申请人需提供真实有效的申请材料，包括但不限于录取通知书、学费缴纳证明等。</p>
        <p>4.2 如发现申请人提供虚假材料，我们有权取消其奖励资格。</p>

        <h3>五、隐私保护</h3>
        <p>5.1 我们将严格保护申请人的个人信息和申请材料，不会泄露给无关第三方。</p>
        <p>5.2 我们可能会使用申请人的信息进行奖励服务相关的联系和通知。</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="termsDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="agreeTerms">
            同意并关闭
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { SuccessFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import NavBar from '../../components/NavBar.vue'
import axios from 'axios'

const router = useRouter()

// 申请步骤
const steps = [
  { title: '申请信息验证' },
  { title: '资料上传' },
  { title: '确认提交' },
  { title: '支付信息' }
]

const currentStep = ref(0)
const termsDialogVisible = ref(false)
const loading = ref(false)
const applicationVerified = ref(false)
const enrollmentData = ref(null)
const estimatedRebateAmount = ref(0)

// 支付方式选项
const paymentMethods = [
  { value: 'bank_transfer', label: '银行转账' },
  { value: 'alipay', label: '支付宝' },
  { value: 'wechat', label: '微信支付' }
]

// 表单数据
const formData = reactive({
  // 第一步：申请ID验证
  applicationId: '',

  // 第二步：学校专业确认
  selectedSchoolName: '',
  selectedMajorName: '',

  // 第三步：支付信息
  paymentMethod: 'bank_transfer',
  accountNumber: '',
  digitalAccount: '',

  // 第四步：资料上传
  admissionLetter: null,
  enrollmentProof: null,

  // 第五步：确认提交
  notes: '',
  agreementAccepted: false
})

// 验证申请信息（申请ID、学校、专业）
const verifyApplicationInfo = async () => {
  if (!formData.applicationId) {
    ElMessage.warning('请输入申请ID')
    return
  }

  if (!formData.selectedSchoolName) {
    ElMessage.warning('请输入申请的学校')
    return
  }

  if (!formData.selectedMajorName) {
    ElMessage.warning('请输入申请的专业')
    return
  }

  loading.value = true
  try {
    const response = await axios.post('/api/rebate/validate-application', {
      applicationId: formData.applicationId,
      schoolName: formData.selectedSchoolName,
      majorName: formData.selectedMajorName
    })

    if (response.data.success) {
      applicationVerified.value = true
      enrollmentData.value = response.data.enrollment

      ElMessage.success('申请信息验证成功！')
    } else {
      ElMessage.error(response.data.message || '申请信息验证失败，请检查输入的信息是否正确')
    }
  } catch (error) {
    console.error('验证申请信息失败:', error)
    ElMessage.error('验证失败，请检查输入的信息是否正确')
  } finally {
    loading.value = false
  }
}

// 保留原有的验证申请ID函数（如果其他地方需要使用）
const verifyApplicationId = async () => {
  if (!formData.applicationId) {
    ElMessage.warning('请输入申请ID')
    return
  }

  loading.value = true
  try {
    const response = await axios.post('/api/rebate/validate-application', {
      applicationId: formData.applicationId
    })

    if (response.data.success) {
      applicationVerified.value = true
      enrollmentData.value = response.data.enrollment

      ElMessage.success('申请ID验证成功！')
    } else {
      ElMessage.error(response.data.message || '申请ID验证失败')
    }
  } catch (error) {
    console.error('验证申请ID失败:', error)
    ElMessage.error('验证失败，请检查申请ID是否正确')
  } finally {
    loading.value = false
  }
}



// 提交奖励申请
const submitRebateApplication = async () => {
  if (!formData.agreementAccepted) {
    ElMessage.warning('请先同意奖励申请条款')
    return
  }

  loading.value = true
  try {
    const requestData = {
      applicationId: formData.applicationId,
      schoolName: formData.selectedSchoolName,
      majorName: formData.selectedMajorName,
      paymentMethod: formData.paymentMethod,
      accountNumber: formData.accountNumber,
      digitalAccount: formData.digitalAccount,
      notes: formData.notes,
      agreementAccepted: formData.agreementAccepted
    }

    const response = await axios.post('/api/rebate/submit-application', requestData)

    if (response.data.success) {
      ElMessage.success('奖励申请提交成功！')
      currentStep.value = 4 // 跳转到成功页面
    } else {
      ElMessage.error(response.data.message || '提交失败')
    }
  } catch (error) {
    console.error('提交奖励申请失败:', error)
    ElMessage.error('提交失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 文件上传处理
const handleAdmissionLetterUpload = async (file) => {
  formData.admissionLetter = file.raw
  await uploadRebateFile(file.raw, 'admissionLetter')
}

const handleEnrollmentProofUpload = async (file) => {
  formData.enrollmentProof = file.raw
  await uploadRebateFile(file.raw, 'enrollmentProof')
}

// 上传奖励申请文件
const uploadRebateFile = async (file, fileType) => {
  if (!file) {
    ElMessage.error('请选择要上传的文件')
    return
  }

  if (!enrollmentData.value || !enrollmentData.value.applicationId) {
    ElMessage.error('缺少申请ID信息')
    return
  }

  const uploadFormData = new FormData()
  uploadFormData.append('file', file)
  uploadFormData.append('fileType', fileType)
  uploadFormData.append('applicationId', enrollmentData.value.applicationId)

  try {
    const response = await fetch('/api/rebate/upload-file', {
      method: 'POST',
      body: uploadFormData
    })

    const result = await response.json()

    if (result.success) {
      ElMessage.success(`${result.materialType}上传成功`)

      // 更新对应的文件状态
      if (fileType === 'admissionLetter') {
        formData.admissionLetter = {
          name: file.name,
          fileId: result.fileId,
          filePath: result.filePath
        }
      } else if (fileType === 'enrollmentProof') {
        formData.enrollmentProof = {
          name: file.name,
          fileId: result.fileId,
          filePath: result.filePath
        }
      }
    } else {
      ElMessage.error(result.message || '文件上传失败')
    }
  } catch (error) {
    console.error('文件上传错误:', error)
    ElMessage.error('文件上传失败，请重试')
  }
}

// 步骤导航
const nextStep = async () => {
  if (currentStep.value === 3) {
    // 最后一步，提交申请
    await submitRebateApplication()
  } else {
    currentStep.value++
  }
}

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

// 检查是否可以进行下一步
const canProceed = computed(() => {
  switch (currentStep.value) {
    case 0:
      return applicationVerified.value && formData.selectedSchoolName && formData.selectedMajorName
    case 1:
      return formData.admissionLetter && formData.enrollmentProof
    case 2:
      return formData.agreementAccepted
    case 3:
      return formData.paymentMethod &&
        (formData.paymentMethod === 'bank_transfer' ? formData.accountNumber : formData.digitalAccount)
    default:
      return true
  }
})

// 显示条款
const showTerms = () => {
  termsDialogVisible.value = true
}

// 同意条款
const agreeTerms = () => {
  formData.agreementAccepted = true
  termsDialogVisible.value = false
  ElMessage.success('已同意奖励申请条款')
}

// 页面跳转
const goToApplications = () => {
  router.push('/applications')
}

const goToHome = () => {
  router.push('/')
}

// 页面加载时的初始化
onMounted(() => {
  // 从URL参数获取申请ID（如果有的话）
  const urlParams = new URLSearchParams(window.location.search)
  const applicationId = urlParams.get('applicationId')
  if (applicationId) {
    formData.applicationId = applicationId
  }
})
</script>

<style scoped>
.r-start-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #000 0%, #1a1a1a 50%, #333 100%);
}

.hero-section {
  background: linear-gradient(135deg, #000 0%, #1a1a1a 50%, #333 100%);
  color: white;
  padding: 0 0 60px;
  text-align: center;
}

.hero-content {
  max-width: 700px;
  margin: 0 auto;
  padding: 0 20px;
}

.hero-title {
  font-size: 4.5rem;
  font-weight: 800;
  margin-bottom: 20px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  letter-spacing: -1px;
}

.hero-subtitle {
  font-size: 1.8rem;
  opacity: 0.9;
  margin-bottom: 0;
  font-weight: 500;
}

.progress-section {
  background: white;
  padding: 30px 0;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.progress-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.progress-steps {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  gap: 10px;
}

.progress-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  position: relative;
}

.step-indicator {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  background: #e0e0e0;
  color: #999;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
  margin-bottom: 8px;
  transition: all 0.3s ease;
}

.progress-step.active .step-indicator {
  background: #667eea;
  color: white;
}

.progress-step.completed .step-indicator {
  background: #4CAF50;
  color: white;
}

.step-label {
  font-size: 14px;
  color: #666;
  text-align: center;
  font-weight: 500;
}

.progress-step.active .step-label {
  color: #667eea;
  font-weight: 600;
}

.form-section {
  background: #f8f9fa;
  padding: 40px 0;
  min-height: 500px;
}

.form-container {
  max-width: 700px;
  margin: 0 auto;
  padding: 0 20px;
}

.form-step {
  background: white;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.step-title {
  font-size: 2rem;
  color: #333;
  margin-bottom: 10px;
  font-weight: 600;
}

.step-description {
  color: #666;
  margin-bottom: 30px;
  font-size: 1.1rem;
}

.form-group {
  margin-bottom: 25px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #333;
}

.form-input {
  width: 100%;
}

.input-with-button {
  display: flex;
  gap: 10px;
}

.input-with-button .form-input {
  flex: 1;
}

.verification-success {
  margin-top: 20px;
  padding: 20px;
  background: #f0f9ff;
  border: 1px solid #0ea5e9;
  border-radius: 8px;
}

.success-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.success-icon {
  color: #10b981;
  font-size: 24px;
}

.enrollment-info {
  display: grid;
  gap: 10px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #e5e7eb;
}

.info-row.highlight {
  background: #fef3c7;
  padding: 12px;
  border-radius: 6px;
  border: none;
  font-weight: 600;
}

.info-label {
  color: #666;
}

.info-value {
  color: #333;
  font-weight: 500;
}

.schools-list {
  margin-bottom: 30px;
}

.school-cards {
  display: grid;
  gap: 15px;
  margin-top: 15px;
}

.school-card {
  padding: 20px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #f9fafb;
}

.school-info h4 {
  margin: 0 0 10px 0;
  color: #333;
}

.school-info p {
  margin: 5px 0;
  color: #666;
}

.payment-methods {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.upload-section {
  display: grid;
  gap: 25px;
}

.upload-item {
  padding: 20px;
  border: 2px dashed #d1d5db;
  border-radius: 8px;
  text-align: center;
}

.upload-label {
  margin-bottom: 15px;
  font-weight: 600;
  color: #333;
}

.required-mark {
  color: #ef4444;
  margin-right: 5px;
}

.upload-tip {
  color: #666;
  font-size: 14px;
  margin-top: 10px;
}

.summary-section {
  margin-bottom: 30px;
}

.summary-content {
  background: #f9fafb;
  padding: 20px;
  border-radius: 8px;
  margin-top: 15px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #e5e7eb;
}

.summary-row.highlight {
  background: #fef3c7;
  padding: 15px;
  border-radius: 6px;
  border: none;
  font-weight: 600;
  margin-top: 10px;
}

.summary-label {
  color: #666;
}

.summary-value {
  color: #333;
  font-weight: 500;
}

.agreement-section {
  margin-top: 20px;
  padding: 20px;
  background: #f0f9ff;
  border-radius: 8px;
}

.success-step {
  text-align: center;
  padding: 60px 40px;
}

.success-icon-large {
  font-size: 80px;
  color: #10b981;
  margin-bottom: 20px;
}

.success-actions {
  margin-top: 30px;
  display: flex;
  gap: 15px;
  justify-content: center;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 30px;
  padding: 20px 0;
}

.terms-content {
  max-height: 400px;
  overflow-y: auto;
  padding: 20px;
}

.terms-content h3 {
  color: #333;
  margin-top: 20px;
  margin-bottom: 10px;
}

.terms-content p {
  color: #666;
  line-height: 1.6;
  margin-bottom: 10px;
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 3rem;
  }

  .hero-subtitle {
    font-size: 1.4rem;
  }

  .form-step {
    padding: 20px;
  }

  .progress-steps {
    flex-direction: column;
    gap: 20px;
  }

  .input-with-button {
    flex-direction: column;
  }

  .success-actions {
    flex-direction: column;
  }
}
</style>
