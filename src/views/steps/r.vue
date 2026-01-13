<template>
  <div class="r-container">
    <!-- 导航栏 -->
    <nav-bar></nav-bar>

    <!-- 主横幅 -->
    <div class="hero-section">
      <div class="hero-content">
        <div class="section-label">REBATE PROGRAM</div>
        <h1 class="hero-title" ref="heroTitle">入学奖励</h1>
        <div class="gold-separator"></div>
        <p class="hero-tagline" ref="heroTagline">透明、高效、为您争取最大权益</p>
        <div class="hero-stats" ref="heroStats">
          <div class="stat-item">
            <span class="stat-number">15%</span>
            <span class="stat-label">平均奖励率</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">¥35,000</span>
            <span class="stat-label">平均奖励金额</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">3,000+</span>
            <span class="stat-label">成功案例</span>
          </div>
        </div>
        <button @click="scrollToRebateInfo" class="hero-button" ref="heroButton">
          了解热门学校奖励信息
        </button>
      </div>
    </div>

    <!-- 奖励介绍 -->
    <div class="intro-section">
      <div class="container">
        <div class="intro-card">
          <div class="intro-header">
            <h2 class="intro-title" ref="introTitle">💰 什么是入学奖励？</h2>
            <p class="intro-subtitle" ref="introText">简单来说，就是学校给平台的佣金，平台返还给你的钱</p>
          </div>

          <div class="intro-content">
            <div class="intro-explanation">
              <div class="explanation-item">
                <div class="step-number">1</div>
                <div class="step-content">
                  <h4>你通过平台申请学校</h4>
                  <p>选择我们的留学服务</p>
                </div>
              </div>
              <div class="flow-arrow">→</div>
              <div class="explanation-item">
                <div class="step-number">2</div>
                <div class="step-content">
                  <h4>学校给平台佣金</h4>
                  <p>通常是学费的10-20%</p>
                </div>
              </div>
              <div class="flow-arrow">→</div>
              <div class="explanation-item">
                <div class="step-number">3</div>
                <div class="step-content">
                  <h4>我们返还给你</h4>
                  <p>最高可返还15%学费</p>
                </div>
              </div>
            </div>

            <div class="intro-benefits">
              <div class="benefit-tag">✅ 完全合法合规</div>
              <div class="benefit-tag">✅ 不影响申请结果</div>
              <div class="benefit-tag">✅ 节省留学成本</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 申请流程 -->
    <div class="process-section">
      <div class="container">
        <h2 class="section-title">🚀 如何获得入学奖励？只需5步</h2>
        <div class="process-grid">
          <div v-for="(step, index) in processSteps" :key="index" class="process-card" ref="processStepsRefs">
            <div class="process-number">{{ index + 1 }}</div>
            <div class="process-icon">
              <span v-if="step.icon === 'UserGroupIcon'">👥</span>
              <span v-else-if="step.icon === 'DocumentTextIcon'">📝</span>
              <span v-else-if="step.icon === 'AcademicCapIcon'">🎓</span>
              <span v-else-if="step.icon === 'CreditCardIcon'">💳</span>
              <span v-else-if="step.icon === 'BanknotesIcon'">💰</span>
            </div>
            <h3 class="process-title">{{ step.title }}</h3>
            <p class="process-desc">{{ getSimpleDescription(step.title) }}</p>
            <div class="process-time">{{ getProcessTime(index) }}</div>
          </div>
        </div>

        <div class="process-note">
          <div class="note-icon">💡</div>
          <div class="note-content">
            <strong>温馨提示：</strong>整个流程完全免费，我们承诺在学费到账后7个工作日内返还奖励金额
          </div>
        </div>
      </div>
    </div>

    <!-- 热门奖励信息 -->
    <div class="rebate-section" ref="rebateInfoSection">
      <div class="container">
        <div class="rebate-header">
          <h2 class="section-title">热门奖励信息</h2>
          <div class="rebate-filters">
            <button v-for="(tab, index) in rebateTabs" :key="index" @click="activeTabIndex = index" class="filter-btn"
              :class="{ 'active': activeTabIndex === index }">
              {{ tab }}
            </button>
          </div>
        </div>

        <div class="rebate-grid">
          <div v-for="(rebate, index) in filteredRebates" :key="index" class="rebate-card" ref="rebateCards">
            <div class="card-header">
              <div class="school-info">
                <h3 class="school-name">{{ rebate.school }}</h3>
                <span class="school-country">{{ rebate.country }}</span>
              </div>
              <div class="rebate-badge">{{ rebate.rebateRate }}</div>
            </div>

            <div class="card-body">
              <div class="rebate-metrics">
                <div class="metric">
                  <span class="metric-label">学历</span>
                  <span class="metric-value">{{ rebate.degree }}</span>
                </div>
                <div class="metric">
                  <span class="metric-label">学费</span>
                  <span class="metric-value">{{ rebate.tuition }}</span>
                </div>
              </div>

              <div class="rebate-amount">
                <span class="amount-label">预计奖励</span>
                <span class="amount-value">{{ rebate.rebateAmount }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 服务优势 -->
    <div class="advantages-section">
      <div class="container">
        <h2 class="section-title" ref="benefitsTitle">为什么选择我们</h2>
        <div class="advantages-grid" ref="benefitsGrid">
          <div class="advantage-card" v-for="(benefit, index) in benefits" :key="index">
            <div class="advantage-icon" :class="benefit.iconClass"></div>
            <div class="advantage-content">
              <h3 class="advantage-title">{{ benefit.title }}</h3>
              <p class="advantage-description">{{ benefit.description }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 常见问题 -->
    <div class="faq-section">
      <div class="faq-container">
        <div class="faq-header">
          <h2 class="section-title">常见问题</h2>
          <p class="section-desc">关于入学奖励，这里为您解答</p>
        </div>
        <div class="faq-list">
          <div v-for="(faq, index) in faqs" :key="index" class="faq-item" :class="{ 'is-active': faq.isOpen }"
            ref="faqItems">
            <button class="faq-trigger" @click="toggleFaq(index)" :aria-expanded="faq.isOpen">
              <div class="faq-question-wrapper">
                <span class="faq-number">{{ String(index + 1).padStart(2, '0') }}</span>
                <span class="faq-question">{{ faq.question }}</span>
              </div>
              <span class="faq-icon"></span>
            </button>
            <div class="faq-content" :style="{ maxHeight: faq.isOpen ? faq.height + 'px' : '0px' }">
              <div class="faq-answer" ref="faqAnswers">
                {{ faq.answer }}
                <div v-if="faq.tips" class="faq-tips">
                  <span class="tips-icon">💡</span>
                  <span class="tips-text">{{ faq.tips }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="faq-footer">
          <p>还有其他问题？</p>
          <button class="contact-btn">联系我们获取更多帮助</button>
        </div>
      </div>
    </div>


    <!-- 页脚 -->
    <footer-bar></footer-bar>
  </div>
</template>

<script setup>
import { ref, nextTick, computed, onMounted, watch } from 'vue'
import NavBar from '../../components/NavBar.vue'
import FooterBar from '../../components/FooterBar.vue'


// 入学奖励流程步骤
const processSteps = [
  {
    title: '选择平台',
    description: '根据奖励政策、服务质量和口碑选择合适的留学申请平台。我们提供透明的奖励比例和专业的咨询服务。',
    icon: 'UserGroupIcon'
  },
  {
    title: '签订协议',
    description: '与平台签订明确的奖励协议，确保奖励比例、返还时间和条件等细节清晰。我们提供标准化的协议文本，保障您的权益。',
    icon: 'DocumentTextIcon'
  },
  {
    title: '申请学校',
    description: '平台协助您申请目标学校，提供专业的申请指导和材料准备。我们的顾问团队将全程跟进您的申请进度。',
    icon: 'AcademicCapIcon'
  },
  {
    title: '缴纳学费',
    description: '收到录取通知后，按照学校要求缴纳学费。我们将协助您完成学费支付流程，确保资金安全。',
    icon: 'CreditCardIcon'
  },
  {
    title: '获取奖励',
    description: '根据协议约定的时间和方式，获取相应比例的学费奖励。我们承诺准时返还，让您安心无忧。',
    icon: 'BanknotesIcon'
  }
]

// 奖励标签页
const rebateTabs = ['全部', '美国', '英国', '澳大利亚', '加拿大', '其他'];
const activeTabIndex = ref(0);



// 获取简化的流程描述
const getSimpleDescription = (title) => {
  const descriptions = {
    '选择平台': '联系我们，了解奖励政策',
    '签订协议': '签署奖励协议，保障权益',
    '申请学校': '您自己自主申请心仪学校',
    '缴纳学费': '收到offer后缴纳学费',
    '获取奖励': '学费到账后返还给您'
  }
  return descriptions[title] || '详细咨询客服'
}

// 获取流程时间
const getProcessTime = (index) => {
  const times = ['1天', '1天', '2-6个月', '1天', '7个工作日']
  return times[index] || ''
}

// 监听标签变化，重置卡片动画
watch(activeTabIndex, async () => {
  await nextTick();
  const cards = document.querySelectorAll('.rebate-card');
  cards.forEach((card, index) => {
    card.style.opacity = '0';
    card.style.transform = 'translateY(20px)';
    setTimeout(() => {
      card.style.opacity = '1';
      card.style.transform = 'translateY(0)';
    }, index * 100);
  });
});

// 奖励信息数据
const rebateInfo = ref([
  {
    country: '美国',
    school: '哈佛大学',
    degree: '硕士',
    tuition: '¥350,000/年',
    rebateRate: '15%',
    rebateAmount: '¥52,500'
  },
  {
    country: '美国',
    school: '斯坦福大学',
    degree: '本科',
    tuition: '¥320,000/年',
    rebateRate: '12%',
    rebateAmount: '¥38,400'
  },
  {
    country: '英国',
    school: '牛津大学',
    degree: '本科',
    tuition: '¥280,000/年',
    rebateRate: '12%',
    rebateAmount: '¥33,600'
  },
  {
    country: '英国',
    school: '剑桥大学',
    degree: '硕士',
    tuition: '¥290,000/年',
    rebateRate: '14%',
    rebateAmount: '¥40,600'
  },
  {
    country: '澳大利亚',
    school: '墨尔本大学',
    degree: '硕士',
    tuition: '¥220,000/年',
    rebateRate: '18%',
    rebateAmount: '¥39,600'
  },
  {
    country: '澳大利亚',
    school: '悉尼大学',
    degree: '本科',
    tuition: '¥210,000/年',
    rebateRate: '16%',
    rebateAmount: '¥33,600'
  },
  {
    country: '加拿大',
    school: '多伦多大学',
    degree: '本科',
    tuition: '¥240,000/年',
    rebateRate: '10%',
    rebateAmount: '¥24,000'
  },
  {
    country: '加拿大',
    school: '麦吉尔大学',
    degree: '硕士',
    tuition: '¥230,000/年',
    rebateRate: '11%',
    rebateAmount: '¥25,300'
  },
  {
    country: '日本',
    school: '东京大学',
    degree: '硕士',
    tuition: '¥180,000/年',
    rebateRate: '8%',
    rebateAmount: '¥14,400'
  },
  {
    country: '新加坡',
    school: '新加坡国立大学',
    degree: '本科',
    tuition: '¥200,000/年',
    rebateRate: '15%',
    rebateAmount: '¥30,000'
  }
]);

// 过滤奖励信息
const filteredRebates = computed(() => {
  const selectedTab = rebateTabs[activeTabIndex.value];
  if (selectedTab === '全部') {
    return rebateInfo.value;
  }
  if (selectedTab === '其他') {
    const mainCountries = ['美国', '英国', '澳大利亚', '加拿大'];
    return rebateInfo.value.filter(item => !mainCountries.includes(item.country));
  }
  return rebateInfo.value.filter(item => item.country === selectedTab);
});

// 奖励优势
const benefits = [
  {
    title: '透明政策',
    description: '我们提供完全透明的奖励政策，让您清楚了解每一笔费用的去向。',
    iconClass: 'icon-transparency'
  },
  {
    title: '最高奖励',
    description: '我们与众多知名院校建立了长期合作关系，确保您能获得市场上最高的奖励比例。',
    iconClass: 'icon-percentage'
  },
  {
    title: '快速到账',
    description: '简化的奖励流程，确保您的奖励金额能够在最短时间内到账。',
    iconClass: 'icon-speed'
  },
  {
    title: '专业服务',
    description: '我们的留学顾问均有海外留学背景，能够提供专业的申请指导。',
    iconClass: 'icon-service'
  }
];

// FAQ数据
const faqs = ref([
  {
    question: '什么是入学奖励？',
    answer: '入学奖励是指留学平台从学校获得的佣金中返还给学生的部分金额，通常以学费的百分比计算。',
    isOpen: false,
    height: 0
  },
  {
    question: '如何获取入学奖励？',
    answer: '要获取入学奖励，您需要通过提供奖励服务的留学平台申请学校。在申请前，您可以通过我们的平台了解各个平台的奖励政策，选择最适合您的方案。申请成功并缴纳学费后，平台会按照约定的比例和时间将奖励返还给您。',
    isOpen: false,
    height: 0
  },
  {
    question: '奖励一般是多少？',
    answer: '奖励比例因国家、学校和专业而异，一般在学费的5%-20%之间。知名大学和热门专业的奖励通常较高，而一些公立大学或特殊专业的奖励可能较低。',
    isOpen: false,
    height: 0
  },
  {
    question: '奖励会影响申请结果吗？',
    answer: '正规的奖励不会影响您的申请结果。学校与平台的佣金合作是公开透明的商业行为，您获取部分奖励是您与平台之间的协议，不会影响学校对您申请的评估。',
    isOpen: false,
    height: 0
  },
  {
    question: '什么时候可以收到奖励？',
    answer: '奖励发放时间因中介而异，一般有以下几种情况：1. 学生缴纳学费后立即返还；2. 学生成功入学后返还；3. 学生完成第一学期或第一学年后分期返还。具体时间请查看各中介的政策说明。',
    isOpen: false,
    height: 0
  }
]);

// 引用元素
const heroTitle = ref(null);
const heroTagline = ref(null);
const heroStats = ref(null);
const heroButton = ref(null);
const introTitle = ref(null);
const introText = ref(null);
const processStepsRefs = ref([]);
const rebateCards = ref([]);
const benefitsTitle = ref(null);
const benefitsGrid = ref(null);
const faqItems = ref([]);
const rebateInfoSection = ref(null);
const faqAnswers = ref([])

// 方法
const scrollToRebateInfo = () => {
  rebateInfoSection.value.scrollIntoView({ behavior: 'smooth' });
};

const toggleFaq = (index) => {
  const faq = faqs.value[index]
  if (!faq.height) {
    // 计算内容高度
    faq.height = faqAnswers.value[index].offsetHeight
  }
  faq.isOpen = !faq.isOpen
}

// 动画相关
onMounted(async () => {
  // 等待DOM更新
  await nextTick();

  // 添加初始动画类
  if (heroTitle.value) heroTitle.value.classList.add('animate-fade-in');
  if (heroTagline.value) {
    setTimeout(() => {
      heroTagline.value.classList.add('animate-fade-in');
    }, 200);
  }
  if (heroStats.value) {
    setTimeout(() => {
      heroStats.value.classList.add('animate-fade-in');
    }, 400);
  }
  if (heroButton.value) {
    setTimeout(() => {
      heroButton.value.classList.add('animate-fade-in');
    }, 600);
  }

  // 设置滚动监听
  const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -100px 0px'
  };

  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('animate-fade-in');
        observer.unobserve(entry.target);
      }
    });
  }, observerOptions);

  // 观察需要动画的元素
  [introTitle.value, introText.value, benefitsTitle.value, benefitsGrid.value].forEach(el => {
    if (el) observer.observe(el);
  });

  // 添加对 processSteps 的观察
  if (processStepsRefs.value) {
    processStepsRefs.value.forEach((step, index) => {
      if (step) {
        step.style.transitionDelay = `${index * 100}ms`;
        observer.observe(step);
      }
    });
  }

  // 处理多个元素的引用
  if (rebateCards.value) {
    rebateCards.value.forEach((card, index) => {
      if (card) {
        card.style.transitionDelay = `${index * 100}ms`;
        observer.observe(card);
      }
    });
  }

  if (faqItems.value) {
    faqItems.value.forEach((item, index) => {
      if (item) {
        item.style.transitionDelay = `${index * 100}ms`;
        observer.observe(item);
      }
    });
  }
});
</script>

<style scoped>
.r-container {
  --primary-color: #C5A059;
  --primary-hover: #B08D45;
  --primary-soft: #f6e9dd;
  --primary-soft-border: #ebd8bc;
  --dark-color: #1c1917;
  --light-color: #F9F8F4;
  --muted-color: #86868b;
  --gray-color: #78716c;
  --border-color: rgba(0, 0, 0, 0.08);
  --shadow-light: 0 1px 3px rgba(0, 0, 0, 0.04);
  --shadow-medium: 0 8px 24px rgba(0, 0, 0, 0.08);
  --border-radius: 12px;
  --transition: all 0.25s ease;

  min-height: 100vh;
  background-color: var(--light-color);
  color: var(--dark-color);
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.section-title {
  font-family: serif;
  font-size: 2.25rem;
  font-weight: 600;
  color: var(--dark-color);
  text-align: center;
  margin-bottom: 3rem;
  letter-spacing: -0.02em;
  line-height: 1.1;
}

/* 动画类 */
.animate-fade-in {
  animation: fadeIn 0.8s ease forwards;
  opacity: 0;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 淡入淡出过渡 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s, max-height 0.5s ease;
  max-height: 500px;
  overflow: hidden;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  max-height: 0;
}

/* hero区域样式 */
.hero-section {
  background-color: var(--light-color);
  color: var(--dark-color);
  padding: 6rem 1rem 4rem;
  text-align: center;
  position: relative;
}

.hero-section::before {
  content: '';
  position: absolute;
  inset: 0;
  pointer-events: none;
  background: radial-gradient(circle at center, rgba(249, 248, 244, 0.92) 0%, rgba(249, 248, 244, 0.6) 50%, rgba(249, 248, 244, 0.3) 100%);
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.section-label {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--muted-color);
  letter-spacing: 0.05em;
  text-transform: uppercase;
  margin-bottom: 0.5rem;
}

.gold-separator {
  width: 60px;
  height: 4px;
  background-color: var(--primary-color);
  margin: 0 auto 2rem;
  border-radius: 2px;
}

.hero-title {
  font-family: serif;
  font-size: 3rem;
  font-weight: 600;
  margin: 0 0 1.5rem;
  color: var(--dark-color);
  letter-spacing: -0.02em;
  line-height: 1.1;
  opacity: 0;
  transform: translateY(20px);
}

.hero-tagline {
  font-size: 1.25rem;
  margin-bottom: 3rem;
  color: var(--muted-color);
  line-height: 1.6;
  opacity: 0;
  transform: translateY(20px);
}

.hero-stats {
  display: flex;
  justify-content: center;
  gap: 3rem;
  margin-bottom: 3rem;
  opacity: 0;
  transform: translateY(20px);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-number {
  font-size: 2.5rem;
  font-weight: 700;
  color: var(--primary-color);
}

.stat-label {
  font-size: 0.95rem;
  color: var(--muted-color);
}

.hero-button {
  background-color: var(--primary-color);
  color: white;
  border: none;
  padding: 1rem 2rem;
  border-radius: 30px;
  font-size: 1.125rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  opacity: 0;
  transform: translateY(20px);
}

.hero-button:hover {
  background-color: var(--primary-hover);
  transform: scale(1.05);
}

/* 介绍部分样式 */
.intro-section {
  padding: 3rem 0;
  background: var(--light-color);
}

.intro-card {
  background: white;
  border-radius: 20px;
  padding: 2rem;
  box-shadow: var(--shadow-light);
  border: 1px solid var(--border-color);
}

.intro-header {
  text-align: center;
  margin-bottom: 2rem;
}

.intro-title {
  font-size: 1.8rem;
  font-weight: 700;
  color: var(--dark-color);
  margin-bottom: 0.5rem;
  letter-spacing: -0.5px;
}

.intro-subtitle {
  font-size: 1rem;
  color: var(--gray-color);
  font-weight: 500;
}

.intro-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.intro-explanation {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.explanation-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  flex: 1;
}

.step-number {
  width: 32px;
  height: 32px;
  background: var(--primary-color);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
}

.step-content h4 {
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--dark-color);
  margin: 0 0 0.25rem 0;
}

.step-content p {
  font-size: 0.8rem;
  color: var(--gray-color);
  margin: 0;
}

.flow-arrow {
  font-size: 1.5rem;
  color: var(--primary-color);
  flex-shrink: 0;
}

.intro-benefits {
  display: flex;
  justify-content: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.benefit-tag {
  background: white;
  border-radius: 20px;
  padding: 0.5rem 1rem;
  font-size: 0.8rem;
  font-weight: 500;
  color: var(--dark-color);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

/* 流程部分样式 */
.process-section {
  padding: 3rem 0;
  background: var(--light-color);
}

.process-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
}

.process-card {
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  text-align: center;
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-light);
  transition: var(--transition);
  position: relative;
}

.process-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-medium);
  border-color: var(--primary-color);
}

.process-number {
  position: absolute;
  top: -12px;
  left: 50%;
  transform: translateX(-50%);
  width: 24px;
  height: 24px;
  background: var(--primary-color);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  font-weight: 600;
  box-shadow: 0 2px 4px rgba(197, 160, 89, 0.3);
}

.process-icon {
  font-size: 2rem;
  margin: 0.5rem 0;
}

.process-title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--dark-color);
  margin: 0.5rem 0;
}

.process-desc {
  font-size: 0.8rem;
  color: var(--gray-color);
  margin: 0.5rem 0;
  line-height: 1.4;
}

.process-time {
  background: var(--light-color);
  border-radius: 12px;
  padding: 0.25rem 0.75rem;
  font-size: 0.7rem;
  font-weight: 600;
  color: var(--primary-color);
  display: inline-block;
  margin-top: 0.5rem;
}

.process-note {
  background: var(--primary-soft);
  border: 1px solid var(--primary-soft-border);
  border-radius: 12px;
  padding: 1rem;
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  margin-top: 2rem;
}

.note-icon {
  font-size: 1.2rem;
  flex-shrink: 0;
}

.note-content {
  font-size: 0.9rem;
  line-height: 1.4;
  color: #292524;
}

.note-content strong {
  color: #1c1917;
}

/* 奖励信息部分样式 */
.rebate-section {
  padding: 4rem 0;
  background: var(--light-color);
}

.rebate-header {
  text-align: center;
  margin-bottom: 3rem;
}

.rebate-filters {
  display: inline-flex;
  background: var(--light-color);
  border-radius: 12px;
  padding: 4px;
  gap: 2px;
  margin-top: 1rem;
}

.filter-btn {
  background: none;
  border: none;
  padding: 8px 16px;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  border-radius: 8px;
  transition: var(--transition);
  color: var(--gray-color);
  white-space: nowrap;
}

.filter-btn:hover {
  color: var(--dark-color);
  background: rgba(255, 255, 255, 0.5);
}

.filter-btn.active {
  background: var(--primary-color);
  color: white;
  box-shadow: var(--shadow-light);
}

.rebate-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 1.5rem;
}

.rebate-card {
  background: white;
  border-radius: var(--border-radius);
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-light);
  overflow: hidden;
  transition: var(--transition);
}

.rebate-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-medium);
  border-color: var(--primary-color);
}

.card-header {
  padding: 1.5rem;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.school-info {
  flex: 1;
}

.school-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--dark-color);
  margin: 0 0 4px 0;
  letter-spacing: -0.2px;
}

.school-country {
  font-size: 0.8rem;
  color: var(--gray-color);
  font-weight: 500;
}

.rebate-badge {
  background: var(--primary-color);
  color: white;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.card-body {
  padding: 1.5rem;
}

.rebate-metrics {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.metric {
  text-align: center;
  padding: 0.75rem;
  background: var(--light-color);
  border-radius: 8px;
}

.metric-label {
  display: block;
  font-size: 0.7rem;
  color: var(--gray-color);
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 4px;
}

.metric-value {
  display: block;
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--dark-color);
}

.rebate-amount {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 1rem;
  border-top: 1px solid var(--border-color);
}

.amount-label {
  font-size: 0.8rem;
  color: var(--gray-color);
  font-weight: 500;
}

.amount-value {
  font-size: 1.2rem;
  font-weight: 700;
  color: var(--primary-color);
}

/* 优势部分样式 */
.advantages-section {
  padding: 4rem 0;
  background: var(--light-color);
}

.advantages-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.5rem;
}

.advantage-card {
  background: white;
  border-radius: var(--border-radius);
  padding: 1.5rem;
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-light);
  transition: var(--transition);
  display: flex;
  align-items: flex-start;
  gap: 1rem;
}

.advantage-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-medium);
  border-color: var(--primary-color);
}

.advantage-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: var(--light-color);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--border-color);
  flex-shrink: 0;
}

.icon-transparency::before {
  content: "🔍";
  font-size: 1.2rem;
}

.icon-percentage::before {
  content: "💰";
  font-size: 1.2rem;
}

.icon-speed::before {
  content: "⚡";
  font-size: 1.2rem;
}

.icon-service::before {
  content: "👨‍💼";
  font-size: 1.2rem;
}

.advantage-content {
  flex: 1;
}

.advantage-title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--dark-color);
  margin: 0 0 0.5rem 0;
  letter-spacing: -0.2px;
}

.advantage-description {
  font-size: 0.85rem;
  line-height: 1.4;
  color: var(--gray-color);
  margin: 0;
}

/* FAQ部分样式 - 苹果风格简洁设计 */
.faq-section {
  padding: 60px 0 120px 0;
  background: var(--light-color);
}

.faq-container {
  max-width: 980px;
  margin: 0 auto;
  padding: 0 22px;
}

.faq-header {
  text-align: center;
  margin-bottom: 64px;
}



.section-desc {
  font-size: 1.25rem;
  color: var(--muted-color);
  font-weight: 400;
  line-height: 1.381;
}

.faq-list {
  background: rgba(255, 255, 255, 0.7);
  border-radius: 18px;
  padding: 8px;
  border: 1px solid var(--border-color);
}

.faq-item {
  background: #fff;
  margin-bottom: 8px;
  border-radius: 14px;
  overflow: hidden;
  transition: all 0.3s ease;
  border-bottom: 1px solid #d2d2d7;
}

.faq-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.faq-item:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transform: translateY(-1px);
}

.faq-trigger {
  width: 100%;
  background: none;
  border: none;
  padding: 24px 32px;
  text-align: left;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.3s ease;
}

.faq-question-wrapper {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
}

.faq-number {
  font-size: 14px;
  font-weight: 600;
  color: var(--primary-color);
  background: rgba(197, 160, 89, 0.15);
  padding: 6px 12px;
  border-radius: 8px;
  min-width: 40px;
  text-align: center;
}

.faq-question {
  font-size: 19px;
  font-weight: 600;
  color: #1d1d1f;
  line-height: 1.21;
}

.faq-icon {
  width: 14px;
  height: 14px;
  position: relative;
  flex-shrink: 0;
}

.faq-icon::before,
.faq-icon::after {
  content: '';
  position: absolute;
  background: #1d1d1f;
  border-radius: 1px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.faq-icon::before {
  width: 14px;
  height: 2px;
  top: 6px;
  left: 0;
}

.faq-icon::after {
  width: 2px;
  height: 14px;
  left: 6px;
  top: 0;
}

.is-active .faq-icon::after {
  transform: scaleY(0);
}

.faq-content {
  overflow: hidden;
  transition: max-height 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.faq-answer {
  padding: 0 32px 32px;
  font-size: 17px;
  color: #515154;
  line-height: 1.47;
}

.faq-tips {
  margin-top: 16px;
  padding: 16px;
  background: var(--primary-soft);
  border-radius: 12px;
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.tips-icon {
  font-size: 16px;
  flex-shrink: 0;
}

.tips-text {
  font-size: 15px;
  color: #515154;
  line-height: 1.4;
}

.faq-footer {
  text-align: center;
  margin-top: 48px;
  padding-top: 32px;
  border-top: 1px solid #d2d2d7;
}

.faq-footer p {
  font-size: 16px;
  color: #86868b;
  margin-bottom: 16px;
}

.contact-btn {
  background: var(--primary-color);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 980px;
  font-size: 17px;
  font-weight: 400;
  cursor: pointer;
  transition: all 0.3s ease;
}

.contact-btn:hover {
  background: var(--primary-hover);
  transform: scale(1.02);
}

/* 悬停效果优化 */
.faq-trigger:hover .faq-question {
  color: var(--primary-color);
}

.faq-trigger:hover .faq-icon::before,
.faq-trigger:hover .faq-icon::after {
  background: var(--primary-color);
}

.faq-item:hover {
  transform: translateY(-1px);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .faq-section {
    padding: 40px 0 80px 0;
  }

  .section-desc {
    font-size: 17px;
  }

  .faq-list {
    margin-top: 40px;
  }

  .faq-question {
    font-size: 16px;
  }

  .faq-answer {
    font-size: 15px;
  }
}



/* 响应式设计 */
@media (max-width: 768px) {
  .container {
    padding: 0 16px;
  }

  .section-title {
    font-size: 1.5rem;
    margin-bottom: 2rem;
  }

  .hero-title {
    font-size: 2.5rem;
  }

  .hero-tagline {
    font-size: 1.25rem;
  }

  .hero-stats {
    flex-direction: column;
    gap: 1.5rem;
  }

  /* 介绍部分响应式 */
  .intro-card {
    padding: 1.5rem;
  }

  .intro-title {
    font-size: 1.5rem;
  }

  .intro-explanation {
    flex-direction: column;
    gap: 1rem;
    padding: 1rem;
  }

  .flow-arrow {
    transform: rotate(90deg);
    font-size: 1.2rem;
  }

  .intro-benefits {
    gap: 0.5rem;
  }

  .benefit-tag {
    font-size: 0.75rem;
    padding: 0.4rem 0.8rem;
  }

  /* 流程部分响应式 */
  .process-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 0.75rem;
  }

  .process-card {
    padding: 1rem;
  }

  .process-icon {
    font-size: 1.5rem;
  }

  .process-title {
    font-size: 0.9rem;
  }

  .process-desc {
    font-size: 0.75rem;
  }

  .process-note {
    padding: 0.75rem;
    gap: 0.5rem;
  }

  .note-content {
    font-size: 0.8rem;
  }

  /* 奖励信息响应式 */
  .rebate-filters {
    flex-wrap: wrap;
    gap: 4px;
  }

  .filter-btn {
    font-size: 0.8rem;
    padding: 6px 12px;
  }

  .rebate-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  /* 优势部分响应式 */
  .advantages-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .advantage-card {
    padding: 1.25rem;
  }
}

@media (max-width: 480px) {

  .intro-section,
  .process-section,
  .rebate-section,
  .advantages-section {
    padding: 2rem 0;
  }

  .section-title {
    font-size: 1.25rem;
  }

  .intro-card {
    padding: 1rem;
  }

  .intro-title {
    font-size: 1.25rem;
  }

  .intro-subtitle {
    font-size: 0.9rem;
  }

  .intro-explanation {
    padding: 0.75rem;
  }

  .step-number {
    width: 28px;
    height: 28px;
    font-size: 0.8rem;
  }

  .step-content h4 {
    font-size: 0.8rem;
  }

  .step-content p {
    font-size: 0.75rem;
  }

  .process-grid {
    grid-template-columns: 1fr;
    gap: 0.5rem;
  }

  .process-card {
    padding: 0.75rem;
  }

  .process-number {
    width: 20px;
    height: 20px;
    font-size: 0.7rem;
    top: -10px;
  }

  .process-icon {
    font-size: 1.25rem;
  }

  .process-title {
    font-size: 0.8rem;
  }

  .process-desc {
    font-size: 0.7rem;
  }

  .process-time {
    font-size: 0.65rem;
    padding: 0.2rem 0.5rem;
  }

  .card-header,
  .card-body {
    padding: 1rem;
  }

  .advantage-card {
    padding: 1rem;
    gap: 0.75rem;
  }

  .advantage-icon {
    width: 40px;
    height: 40px;
  }
}
</style>
