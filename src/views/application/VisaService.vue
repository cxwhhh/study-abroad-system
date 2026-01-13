<template>
  <div class="application-process">
    <!-- 导航栏 -->
    <nav-bar></nav-bar>

    <!-- 主标题区域 -->
    <section class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">签证服务流程</h1>
        <p class="hero-subtitle">五步完成您的签证申请</p>
      </div>
    </section>

    <!-- 申请进度指示器 -->
    <section class="progress-section">
      <div class="progress-container">
        <div class="progress-steps" :data-step="currentStep">
          <div v-for="(step, index) in steps.slice(0, 5)" :key="index" class="progress-step" :class="{
            'active': currentStep === index,
            'completed': completedSteps.includes(index),
            'clickable': canNavigateToStep(index)
          }" @click="navigateToStep(index)">
            <div class="step-indicator">
              <span v-if="completedSteps.includes(index)" class="step-check">
                <el-icon>
                  <Check />
                </el-icon>
              </span>
              <span v-else>{{ index + 1 }}</span>
            </div>
            <div class="step-label">{{ step.label }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 申请表单区域 -->
    <section class="form-section">
      <div class="form-container">
        <!-- 登录提示 -->
        <div v-if="!isLoggedIn" class="login-prompt-card">
          <el-icon><Lock /></el-icon>
          <h3>请先登录以继续</h3>
          <p>登录后即可开始您的签证申请流程，享受我们专业、高效的服务。</p>
          <el-button type="primary" size="large" @click="goToLogin">立即登录</el-button>
        </div>
        
        <!-- 申请表单内容 -->
        <div v-else>
          <transition :name="transitionName" mode="out-in">
            <!-- 步骤1: 个人信息 -->
            <div v-if="currentStep === 0" class="form-step" key="0">
              <div class="step-navigation">
                <el-button v-if="currentStep > 0" @click="prevStep" plain>上一步</el-button>
                <el-button type="primary" @click="nextStep">下一步</el-button>
              </div>
              <h2 class="step-title">填写个人信息</h2>
              <p class="step-description">请提供您的个人信息，以便我们为您办理签证。</p>
              <div class="form-content">
                <el-form ref="formRef" :model="formData" :rules="formRules" class="form-columns">
                  <div class="form-column">
                    <el-form-item label="姓名" prop="name">
                      <el-input v-model="formData.name" placeholder="输入您的姓名" />
                    </el-form-item>
                    <el-form-item label="手机" prop="phone">
                      <el-input v-model="formData.phone" placeholder="输入您的手机号码" />
                    </el-form-item>
                  </div>
                  <div class="form-column">
                    <el-form-item label="护照号" prop="passportNo">
                      <el-input v-model="formData.passportNo" placeholder="输入您的护照号" />
                    </el-form-item>
                    <el-form-item label="邮箱" prop="email">
                      <el-input v-model="formData.email" placeholder="输入您的电子邮箱" />
                    </el-form-item>
                  </div>
                </el-form>
              </div>
            </div>

            <!-- 步骤2: 选择签证类型 -->
            <div v-else-if="currentStep === 1" class="form-step" key="1">
              <div class="step-navigation">
                <el-button v-if="currentStep > 0" @click="prevStep" plain>上一步</el-button>
                <el-button type="primary" @click="nextStep">下一步</el-button>
              </div>
              <h2 class="step-title">选择签证类型</h2>
              <p class="step-description">请选择您计划申请的签证类型。</p>
              <div class="form-content">
                <div class="form-group">
                  <label class="form-label required">签证类型</label>
                  <el-select v-model="formData.visaType" placeholder="请选择签证类型" style="width: 100%;">
                    <el-option value="F-1">
                      <span style="font-weight: bold;">F-1 学生签证</span>
                      <span style="display: block; color: #999; font-size: 0.9rem;">适用于前往美国进行学术学习的学生</span>
                    </el-option>
                    <el-option value="B-2">
                      <span style="font-weight: bold;">B-2 旅游签证</span>
                      <span style="display: block; color: #999; font-size: 0.9rem;">适用于赴美旅游、探亲或医疗</span>
                    </el-option>
                    <el-option value="J-1">
                      <span style="font-weight: bold;">J-1 交流访问学者签证</span>
                      <span style="display: block; color: #999; font-size: 0.9rem;">适用于参与交流访问项目的学者、学生</span>
                    </el-option>
                    <el-option value="H-1B">
                      <span style="font-weight: bold;">H-1B 工作签证</span>
                      <span style="display: block; color: #999; font-size: 0.9rem;">适用于在美国从事专业技术工作的外籍员工</span>
                    </el-option>
                    <el-option value="Other">
                      <span style="font-weight: bold;">其他</span>
                      <span style="display: block; color: #999; font-size: 0.9rem;">如需申请其他类型签证，请联系我们</span>
                    </el-option>
                  </el-select>
                </div>
              </div>
            </div>

            <!-- 步骤3: 上传所需材料 -->
            <div v-else-if="currentStep === 2" class="form-step" key="2">
              <div class="step-navigation">
                <el-button v-if="currentStep > 0" @click="prevStep" plain>上一步</el-button>
                <el-button type="primary" @click="nextStep">下一步</el-button>
              </div>
              <h2 class="step-title">上传所需材料</h2>
              <p class="step-description">请上传您的签证申请所需文件。我们建议您准备以下材料的扫描件或清晰照片：</p>
              <ul class="materials-list">
                <li>有效护照（首页、签名页及所有签证页）</li>
                <li>近期拍摄的签证照片（白底，尺寸51x51mm）</li>
                <li>学校录取通知书（I-20、CAS等）</li>
                <li>资金证明（银行存款证明、奖学金证明等）</li>
                <li>学术材料（成绩单、毕业证、学位证）</li>
                <li>语言成绩单（托福、雅思等）</li>
              </ul>
              <div class="form-content">
                <el-upload
                  v-model:file-list="formData.materials"
                  class="upload-demo"
                  drag
                  action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15" 
                  multiple
                >
                  <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                  <div class="el-upload__text">
                    将文件拖到此处，或 <em>点击上传</em>
                  </div>
                  <template #tip>
                    <div class="el-upload__tip">
                      请上传PDF、JPG或PNG格式的文件，大小不超过10MB。
                    </div>
                  </template>
                </el-upload>
              </div>
            </div>

            <!-- 步骤4: 支付服务费用 -->
            <div v-else-if="currentStep === 3" class="form-step" key="3">
              <div class="step-navigation">
                <el-button v-if="currentStep > 0" @click="prevStep" plain>上一步</el-button>
                <el-button type="primary" @click="nextStep">确认支付</el-button>
              </div>
              <h2 class="step-title">支付服务费用</h2>
              <p class="step-description">请确认您的服务费用并选择支付方式。</p>
              <div class="form-content payment-section">
                <div class="payment-summary">
                  <div class="fee-item">
                    <span class="fee-label">签证服务费</span>
                    <span class="fee-amount">¥ 1,500.00</span>
                  </div>
                  <div class="fee-item total-fee">
                    <span class="fee-label">总计</span>
                    <span class="fee-amount">¥ 1,500.00</span>
                  </div>
                </div>
                <div class="payment-methods">
                  <h3 class="payment-methods-title">选择支付方式</h3>
                  <el-radio-group v-model="formData.paymentMethod" class="payment-options">
                    <el-radio label="alipay" border>支付宝</el-radio>
                    <el-radio label="wechatpay" border>微信支付</el-radio>
                    <el-radio label="card" border>银行卡</el-radio>
                  </el-radio-group>
                </div>
              </div>
            </div>

            <!-- 步骤5: 追踪签证进度 -->
            <div v-else-if="currentStep === 4" class="form-step" key="4">
              <div class="step-navigation">
                <el-button v-if="currentStep > 0" @click="prevStep" plain>上一步</el-button>
                <el-button type="success" @click="finishApplication">完成</el-button>
              </div>
              <h2 class="step-title">追踪签证进度</h2>
              <p class="step-description">您可以在这里查看签证申请的实时状态。</p>
              <div class="form-content">
                <el-timeline>
                  <el-timeline-item timestamp="2025/11/18" placement="top" type="primary">
                    <h4>1. 申请已提交</h4>
                    <p>您的签证申请已成功提交，正在等待初步审核。</p>
                  </el-timeline-item>
                  <el-timeline-item timestamp="2025/11/19" placement="top">
                    <h4>2. 材料审核中</h4>
                    <p>我们的签证专员正在审核您的申请材料，确保所有文件齐全、准确。</p>
                  </el-timeline-item>
                  <el-timeline-item timestamp="" placement="top">
                    <h4>3. 已递交至领事馆</h4>
                    <p>您的申请材料已正式递交至相关国家领事馆。</p>
                  </el-timeline-item>
                  <el-timeline-item timestamp="" placement="top">
                    <h4>4. 领事馆审核中</h4>
                    <p>您的申请正在领事馆审核中，请耐心等待。通常需要5-15个工作日。</p>
                  </el-timeline-item>
                  <el-timeline-item timestamp="" placement="top">
                    <h4>5. 签证已批准</h4>
                    <p>恭喜！您的签证已获批准，我们将尽快为您领取并寄送护照。</p>
                  </el-timeline-item>
                </el-timeline>
              </div>
            </div>
          </transition>
        </div>
      </div>
    </section>

    <!-- 常见问题 -->
    <section class="faq-section">
      <div class="faq-container">
        <h2 class="section-title">常见问题</h2>
        <el-collapse v-model="activeFaq" accordion>
          <el-collapse-item title="我需要准备哪些核心签证材料？" name="1">
            <p>通常需要准备有效护照、签证照片、录取通知书（如适用）、资金证明、以及完整的签证申请表。具体材料会因签证类型和目标国家而异。</p>
          </el-collapse-item>
          <el-collapse-item title="签证申请流程大概需要多长时间？" name="2">
            <p>签证处理时间差异很大，取决于国家、签证类型和申请季节。一般建议提前3-6个月开始准备和申请，以免耽误行程。</p>
          </el-collapse-item>
          <el-collapse-item title="如果我的签证申请被拒绝了怎么办？" name="3">
            <p>如果签证被拒，首先要了解拒签原因。我们专业的签证顾问会帮您分析拒签信，找出问题所在，并协助您准备申诉或重新申请。</p>
          </el-collapse-item>
          <el-collapse-item title="我需要自己去领事馆面试吗？" name="4">
            <p>某些国家和签证类型要求申请人进行面试。我们会为您提供全面的面试培训，包括常见问题、着装建议和材料准备，以确保您顺利通过面试。</p>
          </el-collapse-item>
        </el-collapse>
      </div>
    </section>

    <!-- 免费咨询 -->
    <section class="consultation-section">
      <div class="consultation-container">
        <h2>仍然有疑问？</h2>
        <p>我们的签证专家随时准备为您提供一对一的免费咨询服务。</p>
        <el-button type="primary" size="large" @click="openConsultation">立即免费咨询</el-button>
      </div>
    </section>

    <!-- 联系我们浮动按钮 -->
    <el-button type="primary" :icon="ChatDotRound" circle class="contact-fab" @click="openConsultation" />

    <!-- 页脚 -->
    <footer-bar></footer-bar>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElButton, ElIcon, ElInput, ElSelect, ElOption, ElUpload, ElRadioGroup, ElRadio, ElTimeline, ElTimelineItem, ElCollapse, ElCollapseItem, ElForm, ElFormItem } from 'element-plus';
import { Check, UploadFilled, Lock, ChatDotRound } from '@element-plus/icons-vue';
import NavBar from '@/components/NavBar.vue';
import FooterBar from '@/components/FooterBar.vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

const currentStep = ref(0);
const completedSteps = ref([]);
const isLoggedIn = ref(true); // 假设用户已登录，后续会替换为真实状态
const transitionName = ref('slide-right');

const formData = ref({
  name: '',
  passportNo: '',
  phone: '',
  email: '',
  visaType: '',
  materials: [],
  paymentMethod: 'alipay'
});

const formRef = ref(null);

const formRules = ref({
  name: [{ required: true, message: '请输入您的姓名', trigger: 'blur' }],
  passportNo: [{ required: true, message: '请输入您的护照号', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入您的手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入您的电子邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的电子邮箱', trigger: ['blur', 'change'] }
  ]
});

const steps = ref([
  { label: '填写个人信息' },
  { label: '选择签证类型' },
  { label: '上传所需材料' },
  { label: '支付服务费用' },
  { label: '追踪签证进度' }
]);

const canNavigateToStep = (index) => {
  // 允许导航到已完成的步骤或当前步骤
  return completedSteps.value.includes(index) || index === currentStep.value;
};

const navigateToStep = (index) => {
  if (canNavigateToStep(index)) {
    if (index < currentStep.value) {
      transitionName.value = 'slide-left';
    } else {
      transitionName.value = 'slide-right';
    }
    currentStep.value = index;
  }
};

const prevStep = () => {
  if (currentStep.value > 0) {
    transitionName.value = 'slide-left';
    currentStep.value--;
  }
};

const nextStep = () => {
  transitionName.value = 'slide-right';
  if (currentStep.value === 0) {
    formRef.value.validate((valid) => {
      if (valid) {
        if (currentStep.value < steps.value.length - 1) {
          completedSteps.value.push(currentStep.value);
          currentStep.value++;
        }
      } else {
        ElMessage.error('请检查并修正表单中的错误');
        return false;
      }
    });
  } else {
    if (currentStep.value < steps.value.length - 1) {
      completedSteps.value.push(currentStep.value);
      currentStep.value++;
    }
  }
};

const activeFaq = ref('1');

const router = useRouter();

const goToLogin = () => {
  router.push('/login');
};

const openConsultation = () => {
  router.push('/customer-service');
};

const finishApplication = () => {
  ElMessage.success('恭喜您，签证申请流程已全部完成！');
  router.push('/');
};
</script>

<style scoped>
/* 动画：Hero Section 淡入 */
.hero-section {
  animation: hero-fade-in 1s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards;
}

@keyframes hero-fade-in {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 动画：步骤切换 */
.slide-left-enter-active,
.slide-left-leave-active,
.slide-right-enter-active,
.slide-right-leave-active {
  transition: all 0.5s cubic-bezier(0.55, 0, 0.1, 1);
}

.slide-left-enter-from,
.slide-right-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

.slide-left-leave-to,
.slide-right-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

/* 动画：进度指示器 */
.progress-step.active .step-indicator,
.progress-step.completed .step-indicator {
  transform: scale(1.1);
}

/* 样式与 study.vue 保持一致 */
.application-process {
  background-color: #f8f9fa;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}

.hero-section {
  background: #000;
  color: white;
  padding: 2rem 1rem;
  text-align: center;
  opacity: 0; /* 动画初始状态 */
}

.hero-title {
  font-size: 3.5rem;
  font-weight: bold;
  margin-bottom: 1rem;
}

.hero-subtitle {
  font-size: 1.5rem; /* 调整字体大小 */
}

.progress-section {
  padding: 2rem 0;
  background-color: #ffffff;
  border-bottom: 1px solid #e0e0e0;
}

.progress-container {
  max-width: 1200px;
  margin: 0 auto;
}

.progress-steps {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  position: relative;
}

.progress-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  flex: 1;
  text-align: center;
}

.step-indicator {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #e0e0e0;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 1.2rem;
  font-weight: bold;
  border: 3px solid #e0e0e0;
  transition: all 0.3s ease;
}

.progress-step.active .step-indicator {
  background-color: #4a90e2;
  border-color: #4a90e2;
}

.progress-step.completed .step-indicator {
  background-color: #50e3c2;
  border-color: #50e3c2;
}

.step-label {
  margin-top: 0.5rem;
  font-size: 1rem;
  color: #333;
}

.form-section {
  padding: 3rem 2rem;
}

.form-container {
  max-width: 1000px;
  margin: 0 auto;
  background-color: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden; /* 确保子元素动画不会溢出 */
}

.login-prompt {
  margin-bottom: 2rem;
}

.form-step {
  /* 移除旧的 fadeIn 动画，由 transition 组件处理 */
}

.step-navigation {
  display: flex;
  justify-content: space-between;
  margin-bottom: 2rem;
}

.step-title {
  font-size: 1.8rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
}

.step-description {
  font-size: 1rem;
  color: #666;
  margin-bottom: 2rem;
}

.form-columns {
  display: flex;
  gap: 2rem;
}

.form-column {
  flex: 1;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #333;
}

.form-label.required::after {
  content: ' *';
  color: #f56c6c;
}

.payment-section {
  padding: 1rem;
}

.payment-summary {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 1.5rem;
  margin-bottom: 2rem;
}

.fee-item {
  display: flex;
  justify-content: space-between;
  font-size: 1rem;
  color: #333;
  margin-bottom: 1rem;
}

.fee-item.total-fee {
  font-weight: bold;
  font-size: 1.2rem;
  border-top: 1px solid #e0e0e0;
  padding-top: 1rem;
}

.payment-methods-title {
  font-size: 1.2rem;
  font-weight: 600;
  margin-bottom: 1rem;
}

.payment-options {
  display: flex;
  gap: 1rem;
}

.section-title {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 3rem;
  color: #333;
}

.faq-section {
  padding: 3rem 2rem;
}

.faq-container {
  max-width: 1000px;
  margin: 0 auto;
}

.el-collapse-item__header {
  font-size: 1.2rem;
  font-weight: 600;
}

.el-collapse-item__content p {
  font-size: 1rem;
  color: #666;
  line-height: 1.6;
}

.materials-list {
  list-style-type: disc;
  padding-left: 2rem;
  margin-bottom: 2rem;
  color: #666;
}

.materials-list li {
  margin-bottom: 0.5rem;
}

.consultation-section {
  background-color: #f8f9fa;
  padding: 4rem 2rem;
  text-align: center;
}

.consultation-container h2 {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 1rem;
}

.consultation-container p {
  font-size: 1.2rem;
  color: #666;
  margin-bottom: 2rem;
}

.login-prompt-card {
  text-align: center;
  padding: 3rem;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.login-prompt-card .el-icon {
  font-size: 4rem;
  color: #4a90e2;
  margin-bottom: 1.5rem;
}

.login-prompt-card h3 {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 1rem;
}

.login-prompt-card p {
  font-size: 1.1rem;
  color: #666;
  margin-bottom: 2rem;
}

.contact-fab {
  position: fixed;
  right: 2rem;
  bottom: 8rem;
  width: 60px; /* 增加尺寸 */
  height: 60px; /* 增加尺寸 */
  font-size: 24px; /* 调整图标大小 */
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }

  .hero-subtitle {
    font-size: 1.2rem; /* 调整移动端字体大小 */
  }

  .progress-steps {
    flex-direction: column;
    align-items: center;
  }

  .progress-step {
    flex-direction: row;
    align-items: center;
    margin-bottom: 1rem;
  }

  .step-label {
    margin-top: 0;
    margin-left: 1rem;
  }

  .form-columns {
    flex-direction: column;
    gap: 0;
  }

  .form-section {
    padding: 2rem 1rem;
  }

  .consultation-container h2 {
    font-size: 2rem;
  }
}
</style>