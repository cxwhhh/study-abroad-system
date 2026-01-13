<template>
  <div class="cases-page">
    <nav-bar></nav-bar>

    <!-- 英雄区域 -->
    <div class="country-hero">
      <div class="country-hero-content">
        <div class="section-label">SUCCESS STORIES</div>
        <h1 class="country-title">成功案例</h1>
        <div class="gold-separator"></div>
        <p class="country-subtitle">见证每一个梦想的实现，分享真实的留学故事</p>
      </div>
    </div>

    <!-- 筛选区域 (Moved out of hero) -->
    <div class="country-selector-section">
      <div class="container">
        <div class="filter-tabs">
          <button v-for="category in categories" :key="category.id"
            :class="['filter-tab', { active: activeCategory === category.id }]" @click="activeCategory = category.id">
            <span class="tab-text">{{ category.name }}</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 案例展示区域 -->
    <div class="cases-section">
      <div class="container">
        <div class="cases-grid">
          <div v-for="case_ in filteredCases" :key="case_.id" class="case-card">
            <div class="case-header">
              <div class="student-info">
                <div class="student-avatar">
                  <img :src="case_.avatar" :alt="case_.name" loading="lazy" decoding="async" />
                </div>
                <div class="student-details">
                  <h3 class="student-name">{{ case_.name }}</h3>
                  <div class="admission-info">
                    <span class="university">{{ case_.university }}</span>
                    <span class="major">{{ case_.major }}</span>
                  </div>
                  <div class="result-badge">{{ case_.result }}</div>
                </div>
              </div>
            </div>

            <div class="case-content">
              <blockquote class="testimonial">
                "{{ case_.testimonial }}"
              </blockquote>

              <div class="case-body">
                <div class="case-details">
                  <div class="detail-row">
                    <span class="detail-label">学术背景</span>
                    <span class="detail-value" :title="case_.background">{{ case_.background }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="detail-label">申请时间线</span>
                    <span class="detail-value" :title="case_.timeline">{{ case_.timeline }}</span>
                  </div>
                </div>

                <div class="case-metrics">
                  <div class="metric-item">
                    <span class="metric-label">GPA</span>
                    <span class="metric-value">{{ case_.gpa }}</span>
                  </div>
                  <div class="metric-item">
                    <span class="metric-label">{{ case_.testType }}</span>
                    <span class="metric-value">{{ case_.testScore }}</span>
                  </div>
                </div>
              </div>

              <div class="case-tags">
                <span v-for="tag in case_.tags.slice(0, 3)" :key="tag" class="tag">{{ tag }}</span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="hasMore" class="load-more">
          <button class="load-more-button" @click="loadMore">加载更多</button>
          <div class="load-more-hint">已加载 {{ visibleCount }} / {{ ALL_CASES.length }}</div>
        </div>
      </div>
    </div>

    <div class="stats-section">
      <div class="container">
        <div class="hero-stats">
          <div class="stat-item">
            <span class="stat-number">1000+</span>
            <span class="stat-label">成功案例</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">98%</span>
            <span class="stat-label">录取成功率</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">50+</span>
            <span class="stat-label">合作院校</span>
          </div>
        </div>
      </div>
    </div>

    <footer-bar></footer-bar>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import NavBar from '../components/NavBar.vue'
import FooterBar from '../components/FooterBar.vue'

// 筛选分类
const categories = ref([
  { id: 'all', name: '全部案例', icon: '📚' },
  { id: 'undergraduate', name: '本科申请', icon: '🎓' },
  { id: 'graduate', name: '硕士申请', icon: '📖' },
  { id: 'phd', name: '博士申请', icon: '🔬' },
  { id: 'elite', name: '精英计划', icon: '🌟' }
])

const activeCategory = ref('all')

const ALL_CASES = [
  {
    id: 1,
    name: '张雨萱',
    avatar: '/images/avatar1.jpg',
    university: '哈佛大学',
    major: '计算机科学',
    category: 'graduate',
    testimonial: '从双非院校到哈佛CS硕士，这段路走得并不容易。感谢新英美团队的专业指导，他们不仅帮我制定了详细的背景提升计划，还在文书写作中挖掘出了我独特的技术创新经历。最终获得全额奖学金的那一刻，我知道所有的努力都值得了。',
    gpa: '3.82',
    testType: 'TOEFL',
    testScore: '112',
    result: '全额奖学金',
    tags: ['全额奖学金', '双非逆袭', 'FAANG实习'],
    background: '双非院校，3段实习经历，2篇论文发表',
    timeline: '2023年9月开始规划，2024年4月收到录取'
  },
  {
    id: 2,
    name: '李明轩',
    avatar: '/images/avatar2.jpg',
    university: '牛津大学',
    major: '金融经济学',
    category: 'graduate',
    testimonial: '作为一名理工科背景的学生，跨专业申请金融一直是我的梦想。新英美的顾问老师深度分析了我的数学和编程优势，帮我包装成量化金融方向的候选人。牛津的面试准备尤其关键，模拟面试让我在真正面试时游刃有余。',
    gpa: '3.91',
    testType: 'IELTS',
    testScore: '8.5',
    result: '直接录取',
    tags: ['跨专业申请', '量化金融', '面试满分'],
    background: '985工科背景，CFA Level 2，量化实习',
    timeline: '2023年6月开始准备，2024年1月收到offer'
  },
  {
    id: 3,
    name: '王思涵',
    avatar: '/images/avatar3.jpg',
    university: '斯坦福大学',
    major: '人工智能',
    category: 'phd',
    testimonial: '博士申请是一场马拉松，不仅要有学术实力，更要找到合适的导师。新英美的学术顾问帮我精准匹配了研究方向，并协助我与多位教授建立联系。最终不仅获得了斯坦福的录取，还拿到了Google Research的联合培养机会。',
    gpa: '3.89',
    testType: 'GRE',
    testScore: '334',
    result: 'RA全奖',
    tags: ['博士全奖', 'AI顶会', '产学合作'],
    background: '清华本硕，3篇顶会论文，Google实习',
    timeline: '2022年10月开始套磁，2023年12月确认录取'
  },
  {
    id: 4,
    name: '陈嘉怡',
    avatar: '/images/avatar1.jpg',
    university: '麻省理工学院',
    major: '机械工程',
    category: 'undergraduate',
    testimonial: '高中时期就对机器人技术充满热情，但作为国际学生申请MIT本科竞争异常激烈。新英美团队帮我系统规划了课外活动，从机器人竞赛到科研项目，每一步都精心设计。最终我的创新项目获得了招生官的高度认可。',
    gpa: '4.0',
    testType: 'SAT',
    testScore: '1590',
    result: '直接录取',
    tags: ['本科直录', '机器人竞赛', '创新项目'],
    background: '国际高中，机器人世界冠军，专利发明',
    timeline: '2022年9月开始规划，2023年3月收到录取'
  },
  {
    id: 5,
    name: '刘子墨',
    avatar: '/images/avatar2.jpg',
    university: '剑桥大学',
    major: '数学',
    category: 'undergraduate',
    testimonial: '剑桥数学系的申请不仅要求极高的学术水平，还有严格的入学考试和面试。新英美的学术导师本身就是剑桥校友，他们的专业指导让我在STEP考试中取得了优异成绩，面试时也能从容应对各种数学难题。',
    gpa: '95/100',
    testType: 'A-Level',
    testScore: 'A*A*A*A*',
    result: '无条件录取',
    tags: ['无条件录取', 'STEP满分', '数学竞赛'],
    background: 'A-Level学生，IMO银牌，数学竞赛获奖',
    timeline: '2022年6月开始准备，2023年1月收到offer'
  },
  {
    id: 6,
    name: '赵雅琪',
    avatar: '/images/avatar3.jpg',
    university: '沃顿商学院',
    major: 'MBA',
    category: 'graduate',
    testimonial: '工作五年后决定申请MBA，沃顿一直是我的梦校。新英美的MBA顾问团队不仅帮我梳理了职业发展轨迹，还在文书中突出了我在金融科技领域的独特见解。面试培训更是让我充满自信地展现了领导力和创新思维。',
    gpa: '3.7',
    testType: 'GMAT',
    testScore: '750',
    result: '奖学金录取',
    tags: ['MBA奖学金', '金融科技', '领导力'],
    background: '投行5年经验，创业经历，CFA持证',
    timeline: '2023年3月开始准备，2024年1月收到录取'
  },
  {
    id: 7,
    category: 'elite',
    year: 2021,
    student: 'W同学',
    background: '美高',
    university: '杜克大学',
    gpa: '3.95',
    testimonial: '在美高激烈的竞争中，新英美团队的精准定位和文书指导，是助我圆梦杜克的关键。'
  },
  {
    id: 8,
    category: 'elite',
    year: 2021,
    student: 'P同学',
    background: '美高',
    university: '约翰霍普金斯大学',
    gpa: '3.98',
    testimonial: '感谢新英美，让我的科研热情与JHU的学术氛围完美契合，最终收获理想offer。'
  },
  {
    id: 9,
    category: 'elite',
    year: 2021,
    student: 'S同学',
    background: '国际高中',
    university: '范德堡大学',
    gpa: '3.92',
    testimonial: '从活动规划到文书打磨，新英美的老师们全程陪伴，让我充满自信地展示了最好的自己。'
  },
  {
    id: 10,
    category: 'elite',
    year: 2021,
    student: 'C同学',
    background: '国际高中',
    university: '圣路易斯华盛顿大学',
    gpa: '3.88',
    testimonial: '新英美团队的专业指导，让我在众多申请者中脱颖而出，最终获得了WUSTL的青睐。'
  },
  {
    id: 11,
    category: 'elite',
    year: 2021,
    student: 'S同学',
    background: '国际高中',
    university: '康奈尔大学',
    gpa: '3.96',
    testimonial: '新英美的帮助让我成功进入梦校康奈尔，开启了全新的学术旅程。'
  },
  {
    id: 12,
    category: 'elite',
    year: 2021,
    student: 'L同学',
    background: '美高',
    university: '加州大学伯克利分校',
    gpa: '4.0',
    testimonial: 'UCB的录取离不开新英美团队的精心规划，让我的学术和课外活动大放异彩。'
  },
  {
    id: 13,
    category: 'elite',
    year: 2021,
    student: 'W同学',
    background: '国际高中',
    university: '乔治城大学',
    gpa: '3.85',
    testimonial: '在文书和面试中，新英美的老师帮我充分展现了对国际关系的热情，成功打动了招生官。'
  },
  {
    id: 14,
    category: 'elite',
    year: 2021,
    student: 'D同学',
    background: '国际高中',
    university: '乔治城大学',
    gpa: '3.87',
    testimonial: '感谢新英美，让我在申请季的每一步都走得踏实而坚定，最终圆梦乔治城。'
  },
  {
    id: 15,
    category: 'elite',
    year: 2021,
    student: 'M同学',
    background: '国际高中',
    university: '密歇根大学安娜堡',
    gpa: '3.90',
    testimonial: '新英美团队的专业指导，让我的工程背景和领导力在申请中得到了完美呈现。'
  },
  {
    id: 16,
    category: 'elite',
    year: 2021,
    student: 'X同学',
    background: '国际高中',
    university: '卡内基梅隆大学',
    gpa: '3.94',
    testimonial: 'CMU的CS项目竞争激烈，新英美的老师们帮我挖掘了独特的项目经历，让我成功突围。'
  },
  {
    id: 17,
    category: 'elite',
    year: 2021,
    student: 'L同学',
    background: '国际高中',
    university: '北卡罗来纳大学教堂山分校',
    gpa: '3.89',
    testimonial: 'UNC的录取让我倍感荣幸，感谢新英美团队的全程支持与鼓励。'
  },
  {
    id: 18,
    category: 'elite',
    year: 2021,
    student: 'Y同学',
    background: '国际高中',
    university: '北卡罗来纳大学教堂山分校',
    gpa: '3.91',
    testimonial: '新英美的老师们非常专业，让我的申请过程事半功倍，最终如愿进入梦校。'
  },
  {
    id: 19,
    category: 'elite',
    year: 2021,
    student: 'L同学',
    background: '国际高中',
    university: '纽约大学',
    gpa: '3.80',
    testimonial: '在繁华的纽约开启我的大学生活，是新英美帮我实现的梦想。'
  },
  {
    id: 20,
    category: 'elite',
    year: 2021,
    student: 'L同学',
    background: '国际高中',
    university: '加州大学圣塔芭芭拉分校',
    gpa: '3.85',
    testimonial: 'UCSB的阳光和沙滩在向我招手，感谢新英美的一路相伴。'
  },
  {
    id: 21,
    category: 'elite',
    year: 2021,
    student: 'C同学',
    background: '美高',
    university: '加州大学圣塔芭芭拉分校',
    gpa: '3.88',
    testimonial: '新英美团队的专业指导，让我的美高背景在申请中发挥了最大优势。'
  },
  {
    id: 22,
    category: 'elite',
    year: 2021,
    student: 'L同学',
    background: '国际高中',
    university: '加州大学圣地亚哥分校',
    gpa: '3.86',
    testimonial: 'UCSD的科研氛围正是我所向往的，感谢新英美帮我实现了这个目标。'
  },
  {
    id: 23,
    category: 'elite',
    year: 2021,
    student: 'L同学',
    background: '国际高中',
    university: '加州大学圣迭戈分校',
    gpa: '3.87',
    testimonial: '新英美的老师们非常耐心，解答了我所有关于申请的困惑。'
  },
  {
    id: 24,
    category: 'elite',
    year: 2021,
    student: 'W同学',
    background: '国际高中',
    university: '罗切斯特大学',
    gpa: '3.82',
    testimonial: '感谢新英美，让我在申请季收获了满意的offer。'
  },
  {
    id: 25,
    category: 'elite',
    year: 2021,
    student: 'Z同学',
    background: '国际高中',
    university: '波士顿学院',
    gpa: '3.88',
    testimonial: 'BC的录取让我对未来的学习生活充满期待，感谢新英美的帮助。'
  },
  {
    id: 26,
    category: 'elite',
    year: 2021,
    student: 'Q同学',
    background: '国际高中',
    university: '波士顿大学',
    gpa: '3.81',
    testimonial: '新英美团队的专业和高效，让我的申请之路异常顺利。'
  },
  {
    id: 27,
    category: 'elite',
    year: 2021,
    student: 'Z同学',
    background: '美高',
    university: '加州大学尔湾分校',
    gpa: '3.84',
    testimonial: 'UCI的录取离不开新英美老师的悉心指导，非常感谢他们。'
  },
  {
    id: 28,
    category: 'elite',
    year: 2021,
    student: 'D同学',
    background: '国际高中',
    university: '加州大学尔湾分校',
    gpa: '3.83',
    testimonial: '新英美，让我对留学申请有了更清晰的认识和规划。'
  },
  {
    id: 29,
    category: 'elite',
    year: 2021,
    student: 'G同学',
    background: '国际高中',
    university: '加州大学尔湾分校',
    gpa: '3.85',
    testimonial: '感谢新英美，让我在众多申请者中展现了自己的独特优势。'
  },
  {
    id: 30,
    category: 'elite',
    year: 2021,
    student: 'T同学',
    background: '国际高中',
    university: '波士顿大学',
    gpa: '3.82',
    testimonial: 'BU的多元文化氛围一直吸引着我，感谢新英美帮我圆梦。'
  },
  {
    id: 31,
    category: 'elite',
    year: 2021,
    student: 'J同学',
    background: '美高',
    university: '波士顿大学',
    gpa: '3.86',
    testimonial: '新英美的老师们非常了解美国大学的招生偏好，给了我很多有价值的建议。'
  },
  {
    id: 32,
    category: 'elite',
    year: 2021,
    student: 'Y同学',
    background: '国际高中',
    university: '波士顿大学',
    gpa: '3.84',
    testimonial: '在申请过程中，我遇到了很多挑战，但新英美的老师们总能给我最及时的帮助。'
  },
  {
    id: 33,
    category: 'elite',
    year: 2021,
    student: 'B同学',
    background: '国际高中',
    university: '布兰迪斯大学',
    gpa: '3.80',
    testimonial: '感谢新英美，让我的文书在众多申请材料中脱颖而出。'
  },
  {
    id: 34,
    category: 'elite',
    year: 2021,
    student: 'W同学',
    background: '国际高中',
    university: '伊利诺伊大学香槟分校',
    gpa: '3.88',
    testimonial: 'UIUC的工程学院是我的梦想，新英美帮我实现了它。'
  },
  {
    id: 35,
    category: 'elite',
    year: 2021,
    student: 'L同学',
    background: '国际高中',
    university: '伊利诺伊大学香槟分校',
    gpa: '3.89',
    testimonial: '新英美的模拟面试让我对真正的面试不再恐惧，最终表现出色。'
  },
  {
    id: 36,
    category: 'elite',
    year: 2021,
    student: 'C同学',
    background: '国际高中',
    university: '伊利诺伊大学香槟分校',
    gpa: '3.87',
    testimonial: '感谢新英美团队的头脑风暴，让我的文书充满了独特的创意和思考。'
  },
  {
    id: 37,
    category: 'elite',
    year: 2021,
    student: 'Y同学',
    background: '国际高中',
    university: '伊利诺伊大学香槟分校',
    gpa: '3.90',
    testimonial: '在标化成绩不占优势的情况下，新英美帮我通过背景提升成功逆袭。'
  },
  {
    id: 38,
    category: 'elite',
    year: 2021,
    student: 'P同学',
    background: '国际高中',
    university: '伊利诺伊大学香槟分校',
    gpa: '3.86',
    testimonial: '新英美的老师们对我的申请进度了如指掌，让我感到非常安心。'
  },
  {
    id: 39,
    category: 'elite',
    year: 2021,
    student: 'O同学',
    background: '国际高中',
    university: '剑桥大学',
    gpa: '3.99',
    testimonial: '能够进入剑桥深造，是我从未想过的，感谢新英美让梦想照进现实。'
  },
  {
    id: 40,
    category: 'elite',
    year: 2021,
    student: 'H同学',
    background: '国际高中',
    university: '剑桥大学',
    gpa: '4.0',
    testimonial: '新英美的导师给了我专业的学术指导，让我在剑桥的面试中充满自信。'
  },
  {
    id: 41,
    category: 'elite',
    year: 2021,
    student: 'L同学',
    background: '国际高中',
    university: '帝国理工学院',
    gpa: '3.97',
    testimonial: 'IC的录取是对我努力的最好回报，也离不开新英美团队的专业护航。'
  },
  {
    id: 42,
    category: 'elite',
    year: 2021,
    student: 'Z同学',
    background: '国际高中',
    university: '伦敦政治经济学院',
    gpa: '3.95',
    testimonial: 'LSE的学术氛围正是我所追求的，感谢新英美帮我叩开这所顶尖学府的大门。'
  },
  {
    id: 43,
    category: 'elite',
    year: 2021,
    student: 'Q同学',
    background: '国际高中',
    university: '伦敦大学学院',
    gpa: '3.91',
    testimonial: 'UCL的录取让我对未来充满期待，感谢新英美的一路支持。'
  },
  {
    id: 44,
    category: 'elite',
    year: 2021,
    student: 'P同学',
    background: '国际高中',
    university: '伦敦大学学院',
    gpa: '3.92',
    testimonial: '新英美的文书老师真的非常棒，让我的个人陈述充满了真情实感。'
  },
  {
    id: 45,
    category: 'elite',
    year: 2021,
    student: 'M同学',
    background: '国际高中',
    university: '爱丁堡大学',
    gpa: '3.85',
    testimonial: '爱丁堡大学的录取，让我离我的梦想又近了一步。'
  },
  {
    id: 46,
    category: 'elite',
    year: 2021,
    student: 'L同学',
    background: '国际高中',
    university: '爱丁堡大学',
    gpa: '3.86',
    testimonial: '感谢新英美，让我的申请季画上了圆满的句号。'
  },
  {
    id: 47,
    category: 'elite',
    year: 2021,
    student: 'Q同学',
    background: '国际高中',
    university: '爱丁堡大学',
    gpa: '3.84',
    testimonial: '新英美的老师们总是能在我最需要的时候给予我最专业的建议。'
  },
  {
    id: 48,
    category: 'elite',
    year: 2021,
    student: 'X同学',
    background: '国际高中',
    university: '爱丁堡大学',
    gpa: '3.87',
    testimonial: '在文书写作中，新英美的老师帮我找到了最能打动招生官的切入点。'
  },
  {
    id: 49,
    category: 'elite',
    year: 2021,
    student: 'L同学',
    background: '普高',
    university: '曼彻斯特大学',
    gpa: '3.75',
    testimonial: '从普高到世界名校，新英美帮我实现了看似不可能完成的任务。'
  },
  {
    id: 50,
    category: 'elite',
    year: 2021,
    student: 'D同学',
    background: '国际高中',
    university: '曼彻斯特大学',
    gpa: '3.80',
    testimonial: '感谢新英美，让我的留学之路有了一个完美的开始。'
  },
  {
    id: 51,
    category: 'elite',
    year: 2021,
    student: 'Q同学',
    background: '普高',
    university: '曼彻斯特大学',
    gpa: '3.78',
    testimonial: '新英美的老师们非常负责，让我的申请过程没有任何疏漏。'
  },
  {
    id: 52,
    category: 'elite',
    year: 2021,
    student: 'Y同学',
    background: '国际高中',
    university: '曼彻斯特大学',
    gpa: '3.82',
    testimonial: '在选校定位上，新英美给了我非常精准的建议，最终收获了理想的offer。'
  },
  {
    id: 53,
    category: 'elite',
    year: 2021,
    student: 'F同学',
    background: '普高',
    university: '曼彻斯特大学',
    gpa: '3.76',
    testimonial: '感谢新英美，让我的努力没有白费，最终进入了心仪的大学。'
  },
  {
    id: 54,
    category: 'elite',
    year: 2021,
    student: 'K同学',
    background: '国际高中',
    university: '曼彻斯特大学',
    gpa: '3.81',
    testimonial: '新英美的老师们对英国大学的申请流程非常熟悉，给了我很多帮助。'
  },
  {
    id: 55,
    category: 'elite',
    year: 2021,
    student: 'T同学',
    background: '普高',
    university: '曼彻斯特大学',
    gpa: '3.77',
    testimonial: '在背景提升方面，新英美为我量身定制了方案，让我的简历更具竞争力。'
  },
  {
    id: 56,
    category: 'elite',
    year: 2021,
    student: 'X同学',
    background: '普高',
    university: '伦敦国王学院',
    gpa: '3.79',
    testimonial: 'KCL的录取让我非常惊喜，感谢新英美团队的专业指导。'
  },
  {
    id: 57,
    category: 'elite',
    year: 2021,
    student: 'S同学',
    background: '国际高中',
    university: '伦敦国王学院',
    gpa: '3.83',
    testimonial: '新英美的老师们非常友善，让我在申请过程中感受到了家一般的温暖。'
  },
  { id: 58, category: 'elite', year: 2021, student: 'T同学', background: '国际高中', university: '伦敦国王学院', gpa: '3.84', testimonial: '感谢新英美，让我在激烈的竞争中成功拿到了KCL的offer。' },
  { id: 59, category: 'elite', year: 2021, student: 'M同学', background: '国际高中', university: '伦敦国王学院', gpa: '3.85', testimonial: '新英美的老师们对我的文书进行了多次修改，最终呈现出最完美的效果。' },
  { id: 60, category: 'elite', year: 2021, student: 'D同学', background: '国际高中', university: '华威大学', gpa: '3.88', testimonial: '华威大学的商学院是我的梦想，新英美帮我实现了它。' },
  { id: 61, category: 'elite', year: 2021, student: 'F同学', background: '国际高中', university: '华威大学', gpa: '3.89', testimonial: '新英美的老师们非常专业，让我的申请过程事半功倍。' },
  { id: 62, category: 'elite', year: 2021, student: 'Y同学', background: '国际高中', university: '华威大学', gpa: '3.87', testimonial: '感谢新英美，让我在申请季收获了满意的offer。' },
  { id: 63, category: 'elite', year: 2021, student: 'W同学', background: '国际高中', university: '华威大学', gpa: '3.90', testimonial: '在文书和面试中，新英美的老师帮我充分展现了我的优势，成功打动了招生官。' },
  { id: 64, category: 'elite', year: 2021, student: 'M同学', background: '国际高中', university: '布里斯托大学', gpa: '3.82', testimonial: '布里斯托大学的录取让我对未来的学习生活充满期待，感谢新英美的帮助。' },
  { id: 65, category: 'elite', year: 2021, student: 'G同学', background: '国际高中', university: '布里斯托大学', gpa: '3.83', testimonial: '新英美团队的专业和高效，让我的申请之路异常顺利。' },
  { id: 66, category: 'elite', year: 2021, student: 'L同学', background: '国际高中', university: '布里斯托大学', gpa: '3.81', testimonial: '感谢新英美，让我的努力没有白费，最终进入了心仪的大学。' },
  { id: 67, category: 'elite', year: 2021, student: 'S同学', background: '国际高中', university: '布里斯托大学', gpa: '3.84', testimonial: '新英美的老师们对英国大学的申请流程非常熟悉，给了我很多帮助。' },
  { id: 68, category: 'elite', year: 2021, student: 'T同学', background: '普高', university: '布里斯托大学', gpa: '3.75', testimonial: '在背景提升方面，新英美为我量身定制了方案，让我的简历更具竞争力。' },
  { id: 69, category: 'elite', year: 2021, student: 'H同学', background: '普高', university: '布里斯托大学', gpa: '3.76', testimonial: 'KCL的录取让我非常惊喜，感谢新英美团队的专业指导。' },
  { id: 70, category: 'elite', year: 2021, student: 'D同学', background: '国际高中', university: '布里斯托大学', gpa: '3.85', testimonial: '新英美的老师们非常友善，让我在申请过程中感受到了家一般的温暖。' },
  { id: 71, category: 'elite', year: 2021, student: 'P同学', background: '国际高中', university: '布里斯托大学', gpa: '3.86', testimonial: '感谢新英美，让我在激烈的竞争中成功拿到了布里斯托大学的offer。' },
  { id: 72, category: 'elite', year: 2021, student: 'S同学', background: '国际高中', university: '伦敦皇后玛丽学院', gpa: '3.78', testimonial: '新英美的老师们对我的文书进行了多次修改，最终呈现出最完美的效果。' },
  { id: 73, category: 'elite', year: 2021, student: 'M同学', background: '国际高中', university: '多伦多大学', gpa: '3.90', testimonial: '多伦多大学的录取，让我离我的梦想又近了一步。' },
  { id: 74, category: 'elite', year: 2021, student: 'F同学', background: '国际高中', university: '多伦多大学', gpa: '3.91', testimonial: '感谢新英美，让我的申请季画上了圆满的句号。' },
  { id: 75, category: 'elite', year: 2021, student: 'R同学', background: '国际高中', university: '多伦多大学', gpa: '3.89', testimonial: '新英美的老师们总是能在我最需要的时候给予我最专业的建议。' },
  { id: 76, category: 'elite', year: 2021, student: 'T同学', background: '国际高中', university: '多伦多大学', gpa: '3.92', testimonial: '在文书写作中，新英美的老师帮我找到了最能打动招生官的切入点。' },
  { id: 77, category: 'elite', year: 2021, student: 'C同学', background: '国际高中', university: '多伦多大学', gpa: '3.88', testimonial: '从普高到世界名校，新英美帮我实现了看似不可能完成的任务。' },
  { id: 78, category: 'elite', year: 2021, student: 'L同学', background: '国际高中', university: '多伦多大学', gpa: '3.87', testimonial: '感谢新英美，让我的留学之路有了一个完美的开始。' },
  { id: 79, category: 'elite', year: 2021, student: 'S同学', background: '国际高中', university: '多伦多大学', gpa: '3.89', testimonial: '新英美的老师们非常负责，让我的申请过程没有任何疏漏。' },
  { id: 80, category: 'elite', year: 2021, student: 'G同学', background: '普高', university: '多伦多大学', gpa: '3.80', testimonial: '在选校定位上，新英美给了我非常精准的建议，最终收获了理想的offer。' },
  { id: 81, category: 'elite', year: 2021, student: 'Y同学', background: '普高', university: '多伦多大学', gpa: '3.81', testimonial: '感谢新英美，让我的努力没有白费，最终进入了心仪的大学。' },
  { id: 82, category: 'elite', year: 2021, student: 'R同学', background: '国际高中', university: '麦吉尔大学', gpa: '3.93', testimonial: '麦吉尔大学的录取让我对未来的学习生活充满期待，感谢新英美的帮助。' },
  { id: 83, category: 'elite', year: 2021, student: 'K同学', background: '国际高中', university: '英属哥伦比亚大学', gpa: '3.88', testimonial: 'UBC的录取离不开新英美老师的悉心指导，非常感谢他们。' },
  { id: 84, category: 'elite', year: 2021, student: 'L同学', background: '国际高中', university: '英属哥伦比亚大学', gpa: '3.89', testimonial: '新英美，让我对留学申请有了更清晰的认识和规划。' },
  { id: 85, category: 'elite', year: 2021, student: 'S同学', background: '国际高中', university: '英属哥伦比亚大学', gpa: '3.87', testimonial: '感谢新英美，让我在众多申请者中展现了自己的独特优势。' },
  { id: 86, category: 'elite', year: 2021, student: 'K同学', background: '国际高中', university: '新加坡国立大学', gpa: '3.95', testimonial: 'NUS的录取让我倍感荣幸，感谢新英美团队的全程支持与鼓励。' },
  { id: 87, category: 'elite', year: 2021, student: 'D同学', background: '国际高中', university: '南洋理工大学', gpa: '3.94', testimonial: 'NTU的录取让我对未来充满期待，感谢新英美的一路支持。' },
  { id: 88, category: 'elite', year: 2021, student: 'S同学', background: '美高', university: '南洋理工大学', gpa: '3.96', testimonial: '新英美的老师们非常专业，让我的申请过程事半功倍，最终如愿进入梦校。' },
  { id: 89, category: 'elite', year: 2021, student: 'S同学', background: '国际高中', university: '南洋理工大学', gpa: '3.93', testimonial: '在繁华的新加坡开启我的大学生活，是新英美帮我实现的梦想。' },
  { id: 90, category: 'elite', year: 2021, student: 'W同学', background: '普高', university: '雪城大学', gpa: '3.70', testimonial: '雪城大学的录取离不开新英美老师的悉心指导，非常感谢他们。' },
  { id: 91, category: 'elite', year: 2021, student: 'L同学', background: '国际高中', university: '宾州州立大学', gpa: '3.80', testimonial: '新英美，让我对留学申请有了更清晰的认识和规划。' },
  { id: 92, category: 'elite', year: 2021, student: 'J同学', background: '国际高中', university: '宾州州立大学', gpa: '3.81', testimonial: '感谢新英美，让我在众多申请者中展现了自己的独特优势。' },
  { id: 93, category: 'elite', year: 2021, student: 'G同学', background: '国际高中', university: '宾州州立大学', gpa: '3.82', testimonial: 'PSU的录取让我倍感荣幸，感谢新英美团队的全程支持与鼓励。' },
  { id: 94, category: 'elite', year: 2021, student: 'X同学', background: '国际高中', university: '康涅狄格大学', gpa: '3.78', testimonial: 'UConn的录取让我对未来充满期待，感谢新英美的一路支持。' },
  { id: 95, category: 'elite', year: 2021, student: 'Y同学', background: '国际高中', university: '康涅狄格大学', gpa: '3.79', testimonial: '新英美的老师们非常专业，让我的申请过程事半功倍，最终如愿进入梦校。' },
  { id: 96, category: 'elite', year: 2021, student: 'H同学', background: '国际高中', university: '康涅狄格大学', gpa: '3.77', testimonial: '在康涅狄格开启我的大学生活，是新英美帮我实现的梦想。' },
  { id: 97, category: 'elite', year: 2021, student: 'Z同学', background: '国际高中', university: '爱默生学院', gpa: '3.85', testimonial: '爱默生学院的传媒专业是我的梦想，新英美帮我实现了它。' },
  { id: 98, category: 'elite', year: 2021, student: 'A同学', background: '国际高中', university: '爱默生学院', gpa: '3.86', testimonial: '新英美的文书老师真的非常棒，让我的个人陈述充满了真情实感。' },
  { id: 99, category: 'elite', year: 2021, student: 'P同学', background: '国际高中', university: '杜伦大学', gpa: '3.88', testimonial: '杜伦大学的录取，让我离我的梦想又近了一步。' },
  { id: 100, category: 'elite', year: 2021, student: 'D同学', background: '国际高中', university: '杜伦大学', gpa: '3.89', testimonial: '感谢新英美，让我的申请季画上了圆满的句号。' },
  { id: 101, category: 'elite', year: 2021, student: 'T同学', background: '国际高中', university: '杜伦大学', gpa: '3.87', testimonial: '新英美的老师们总是能在我最需要的时候给予我最专业的建议。' },
  { id: 102, category: 'elite', year: 2021, student: 'L同学', background: '国际高中', university: '杜伦大学', gpa: '3.90', testimonial: '在文书写作中，新英美的老师帮我找到了最能打动招生官的切入点。' },
  { id: 103, category: 'elite', year: 2021, student: 'C同学', background: '国际高中', university: '杜伦大学', gpa: '3.86', testimonial: '从普高到世界名校，新英美帮我实现了看似不可能完成的任务。' },
  { id: 104, category: 'elite', year: 2021, student: 'Y同学', background: '国际高中', university: '澳洲国立大学', gpa: '3.92', testimonial: 'ANU的录取让我对未来的学习生活充满期待，感谢新英美的帮助。' },
  { id: 105, category: 'elite', year: 2021, student: 'J同学', background: '国际高中', university: '墨尔本大学', gpa: '3.90', testimonial: '墨尔本大学的录取离不开新英美老师的悉心指导，非常感谢他们。' },
  { id: 106, category: 'elite', year: 2021, student: 'P同学', background: '国际高中', university: '墨尔本大学', gpa: '3.91', testimonial: '新英美，让我对留学申请有了更清晰的认识和规划。' },
  { id: 107, category: 'elite', year: 2021, student: 'B同学', background: '普高', university: '悉尼大学', gpa: '3.82', testimonial: '感谢新英美，让我在众多申请者中展现了自己的独特优势。' },
  { id: 108, category: 'elite', year: 2021, student: 'X同学', background: '国际高中', university: '悉尼大学', gpa: '3.85', testimonial: '悉尼大学的录取让我倍感荣幸，感谢新英美团队的全程支持与鼓励。' },
  { id: 109, category: 'elite', year: 2021, student: 'D同学', background: '国际高中', university: '悉尼大学', gpa: '3.86', testimonial: '悉尼大学的录取让我对未来充满期待，感谢新英美的一路支持。' },
  { id: 110, category: 'elite', year: 2021, student: 'Q同学', background: '国际高中', university: '悉尼大学', gpa: '3.84', testimonial: '新英美的老师们非常专业，让我的申请过程事半功倍，最终如愿进入梦校。' },
  { id: 111, category: 'elite', year: 2021, student: 'M同学', background: '国际高中', university: '悉尼大学', gpa: '3.87', testimonial: '在悉尼开启我的大学生活，是新英美帮我实现的梦想。' },
  { id: 112, category: 'elite', year: 2021, student: 'F同学', background: '普高', university: '悉尼大学', gpa: '3.81', testimonial: '悉尼大学的录取离不开新英美老师的悉心指导，非常感谢他们。' },
  { id: 113, category: 'elite', year: 2021, student: 'Z同学', background: '国际高中', university: '悉尼大学', gpa: '3.88', testimonial: '新英美，让我对留学申请有了更清晰的认识和规划。' },
  { id: 114, category: 'elite', year: 2021, student: 'S同学', background: '国际高中', university: '新南威尔士大学', gpa: '3.89', testimonial: '感谢新英美，让我在众多申请者中展现了自己的独特优势。' },
  { id: 115, category: 'elite', year: 2021, student: 'X同学', background: '国际高中', university: '新南威尔士大学', gpa: '3.90', testimonial: 'UNSW的录取让我倍感荣幸，感谢新英美团队的全程支持与鼓励。' },
  { id: 116, category: 'elite', year: 2021, student: 'T同学', background: '国际高中', university: '昆士兰大学', gpa: '3.83', testimonial: '昆士兰大学的录取让我对未来充满期待，感谢新英美的一路支持。' },
  { id: 117, category: 'elite', year: 2021, student: 'M同学', background: '国际高中', university: '昆士兰大学', gpa: '3.84', testimonial: '新英美的老师们非常专业，让我的申请过程事半功倍，最终如愿进入梦校。' },
  { id: 118, category: 'elite', year: 2021, student: 'M同学', background: '国际高中', university: '昆士兰大学', gpa: '3.85', testimonial: '在昆士兰开启我的大学生活，是新英美帮我实现的梦想。' },
  { id: 119, category: 'elite', year: 2021, student: 'P同学', background: '国际高中', university: '昆士兰大学', gpa: '3.86', testimonial: '昆士兰大学的录取离不开新英美老师的悉心指导，非常感谢他们。' },
  { id: 120, category: 'elite', year: 2021, student: 'D同学', background: '国际高中', university: '昆士兰大学', gpa: '3.87', testimonial: '新英美，让我对留学申请有了更清晰的认识和规划。' },
  { id: 121, category: 'elite', year: 2021, student: 'S同学', background: '普高', university: '昆士兰大学', gpa: '3.78', testimonial: '感谢新英美，让我在众多申请者中展现了自己的独特优势。' },
  { id: 122, category: 'elite', year: 2021, student: 'H同学', background: '国际高中', university: '昆士兰大学', gpa: '3.88', testimonial: 'UQ的录取让我倍感荣幸，感谢新英美团队的全程支持与鼓励。' },
  { id: 123, category: 'elite', year: 2021, student: 'R同学', background: '国际高中', university: '莫纳什大学', gpa: '3.80', testimonial: '莫纳什大学的录取让我对未来充满期待，感谢新英美的一路支持。' },
  { id: 124, category: 'elite', year: 2021, student: 'Q同学', background: '国际高中', university: '莫纳什大学', gpa: '3.81', testimonial: '新英美的老师们非常专业，让我的申请过程事半功倍，最终如愿进入梦校。' },
  { id: 125, category: 'elite', year: 2021, student: 'H同学', background: '国际高中', university: '莫纳什大学', gpa: '3.82', testimonial: '在莫纳什开启我的大学生活，是新英美帮我实现的梦想。' },
  { id: 126, category: 'elite', year: 2021, student: 'J同学', background: '国际高中', university: '莫纳什大学', gpa: '3.83', testimonial: '莫纳什大学的录取离不开新英美老师的悉心指导，非常感谢他们。' },
  { id: 127, category: 'elite', year: 2021, student: 'K同学', background: '普高', university: '莫纳什大学', gpa: '3.75', testimonial: '新英美，让我对留学申请有了更清晰的认识和规划。' },
  { id: 128, category: 'elite', year: 2021, student: 'H同学', background: '国际高中', university: '莫纳什大学', gpa: '3.84', testimonial: '感谢新英美，让我在众多申请者中展现了自己的独特优势。' },
  { id: 129, category: 'elite', year: 2021, student: 'W同学', background: '国际高中', university: '莫纳什大学', gpa: '3.85', testimonial: '莫纳什大学的录取让我倍感荣幸，感谢新英美团队的全程支持与鼓励。' },
  { id: 130, category: 'elite', year: 2021, student: 'L同学', background: '国际高中', university: '莫纳什大学', gpa: '3.86', testimonial: '莫纳什大学的录取让我对未来充满期待，感谢新英美的一路支持。' }
]

const INITIAL_COUNT = 16
const LOAD_MORE_STEP = 16

const cases = ref(ALL_CASES.slice(0, INITIAL_COUNT))
const visibleCount = ref(INITIAL_COUNT)

const hasMore = computed(() => visibleCount.value < ALL_CASES.length)

const loadMore = () => {
  const nextCount = Math.min(visibleCount.value + LOAD_MORE_STEP, ALL_CASES.length)
  if (nextCount > visibleCount.value) {
    cases.value = ALL_CASES.slice(0, nextCount)
    visibleCount.value = nextCount
  }
}

const getDefaultAvatar = (id) => {
  const avatars = ['/images/avatar1.jpg', '/images/avatar2.jpg', '/images/avatar3.jpg']
  const index = Number(id || 0) % avatars.length
  return avatars[index]
}

const normalizeCase = (case_) => {
  const categoryNames = {
    all: '全部案例',
    undergraduate: '本科申请',
    graduate: '硕士申请',
    phd: '博士申请',
    elite: '精英计划'
  }

  const name = case_.name || case_.student || '匿名同学'
  const avatar = case_.avatar || getDefaultAvatar(case_.id)
  const university = case_.university || case_.school || '未填写院校'
  const major = case_.major || case_.program || categoryNames[case_.category] || '未填写专业'
  const testimonial = case_.testimonial || '在新英美的帮助下，顺利获得理想录取。'
  const gpa = case_.gpa || '—'
  const testType = case_.testType || 'IELTS'
  const testScore = case_.testScore || '—'
  const result = case_.result || '成功录取'
  const background = case_.background || '—'
  const timeline = case_.timeline || (case_.year ? `${case_.year}年录取` : '—')
  const tags = Array.isArray(case_.tags) && case_.tags.length
    ? case_.tags
    : [categoryNames[case_.category] || '案例', result]

  return {
    ...case_,
    name,
    avatar,
    university,
    major,
    testimonial,
    gpa,
    testType,
    testScore,
    result,
    background,
    timeline,
    tags
  }
}

const filteredCases = computed(() => {
  if (activeCategory.value === 'all') {
    return cases.value.map(normalizeCase)
  }
  return cases.value
    .filter(case_ => case_.category === activeCategory.value)
    .map(normalizeCase)
})

// 添加新的硕士案例
const newCases = [
  {
    id: 131,
    name: 'L同学',
    avatar: '/images/avatar1.jpg',
    university: '哈佛大学',
    major: '教育学',
    category: 'graduate',
    testimonial: '在新英美的帮助下，我成功从国内本科背景跃升至哈佛大学，开启了梦寐以求的教育学研究生涯。他们的专业指导和文书润色至关重要。',
    gpa: '3.85',
    testType: 'TOEFL',
    testScore: '110',
    result: '直接录取',
    tags: ['名校录取', '文科申请'],
    background: '国内本科',
    timeline: '2023年8月启动，2024年3月录取'
  },
  {
    id: 132,
    name: 'W同学',
    avatar: '/images/avatar2.jpg',
    university: '斯坦福大学',
    major: '计算机科学',
    category: 'graduate',
    testimonial: '斯坦福的CS项目是我的梦想，新英美团队通过深入的背景挖掘和项目指导，让我的申请材料在众多竞争者中脱颖而出。',
    gpa: '3.92',
    testType: 'GRE',
    testScore: '335',
    result: '直接录取',
    tags: ['顶尖CS', '科研背景'],
    background: '国内顶尖大学',
    timeline: '2023年9月启动，2024年3月录取'
  },
  {
    id: 133,
    name: 'Z同学',
    avatar: '/images/avatar3.jpg',
    university: '麻省理工学院',
    major: '金融学',
    category: 'graduate',
    testimonial: 'MIT的金融硕士项目对申请者的数理能力要求极高。新英美为我量身定制了背景提升方案，并指导我完成了高质量的量化研究项目。',
    gpa: '3.95',
    testType: 'GMAT',
    testScore: '760',
    result: '直接录取',
    tags: ['量化金融', '顶尖名校'],
    background: '国内顶尖大学',
    timeline: '2023年7月启动，2024年2月录取'
  },
  {
    id: 134,
    name: 'L同学',
    avatar: '/images/avatar1.jpg',
    university: '牛津大学',
    major: '经济学',
    category: 'graduate',
    testimonial: '牛津大学的经济学项目历史悠久，学术氛围浓厚。新英美的导师帮助我深入理解了英国的教育体系和申请要求，让我的申请过程事半功倍。',
    gpa: '3.88',
    testType: 'IELTS',
    testScore: '8.0',
    result: '直接录取',
    tags: ['G5精英', '经济学'],
    background: '国内985院校',
    timeline: '2023年8月启动，2024年1月录取'
  },
  {
    id: 135,
    name: 'C同学',
    avatar: '/images/avatar2.jpg',
    university: '剑桥大学',
    major: '工程学',
    category: 'graduate',
    testimonial: '剑桥大学的工程学项目享誉全球。在新英美的帮助下，我成功展示了自己在科研和实践方面的优势，获得了评审委员会的青睐。',
    gpa: '3.90',
    testType: 'IELTS',
    testScore: '7.5',
    result: '直接录取',
    tags: ['G5精英', '工程学'],
    background: '国内985院校',
    timeline: '2023年9月启动，2024年1月录取'
  },
  {
    id: 136,
    name: 'S同学',
    avatar: '/images/avatar3.jpg',
    university: '苏黎世联邦理工学院',
    major: '化学',
    category: 'graduate',
    testimonial: '作为欧洲顶尖的理工科大学，ETH对申请者的学术能力要求非常严格。新英美的导师为我提供了专业的学术指导和面试培训，让我在申请过程中充满自信。',
    gpa: '3.92',
    testType: 'TOEFL',
    testScore: '108',
    result: '直接录取',
    tags: ['欧陆名校', '化学'],
    background: '国内顶尖大学',
    timeline: '2023年8月启动，2024年2月录取'
  },
  {
    id: 137,
    name: 'W同学',
    avatar: '/images/avatar1.jpg',
    university: '新加坡国立大学',
    major: '市场营销',
    category: 'graduate',
    testimonial: '新加坡国立大学的市场营销项目非常注重实践和创新。在新英美的指导下，我成功地将自己的实习经历和市场洞察融入到文书和面试中，给招生官留下了深刻印象。',
    gpa: '3.80',
    testType: 'TOEFL',
    testScore: '105',
    result: '直接录取',
    tags: ['亚洲顶尖', '市场营销'],
    background: '国内211院校',
    timeline: '2023年9月启动，2024年3月录取'
  },
  {
    id: 138,
    name: 'L同学',
    avatar: '/images/avatar2.jpg',
    university: '哥伦比亚大学',
    major: '新闻学',
    category: 'graduate',
    testimonial: '哥伦比亚大学的新闻学院是所有新闻人心中的圣殿。新英美的导师帮助我精心打磨了我的作品集和个人陈述，让我的新闻理想和专业素养得到了充分展示。',
    gpa: '3.85',
    testType: 'TOEFL',
    testScore: '115',
    result: '直接录取',
    tags: ['常春藤', '新闻学'],
    background: '国内顶尖大学',
    timeline: '2023年8月启动，2024年3月录取'
  },
  {
    id: 139,
    name: 'M同学',
    avatar: '/images/avatar3.jpg',
    university: '芝加哥大学',
    major: '公共政策',
    category: 'graduate',
    testimonial: '芝加哥大学的公共政策项目以其严谨的学术训练和丰富的实践机会而闻名。在新英美的帮助下，我成功地将自己的社会实践和政策思考融入到申请中，获得了招生官的认可。',
    gpa: '3.88',
    testType: 'GRE',
    testScore: '330',
    result: '直接录取',
    tags: ['顶尖名校', '公共政策'],
    background: '国内985院校',
    timeline: '2023年9月启动，2024年2月录取'
  },
  {
    id: 140,
    name: 'Y同学',
    avatar: '/images/avatar1.jpg',
    university: '宾夕法尼亚大学',
    major: '法学',
    category: 'graduate',
    testimonial: '宾夕法尼亚大学的法学院是美国最顶尖的法学院之一。新英美的导师为我提供了专业的法律文书写作指导和面试培训，让我在激烈的竞争中脱颖而出。',
    gpa: '3.90',
    testType: 'TOEFL',
    testScore: '112',
    result: '直接录取',
    tags: ['常春藤', '法学'],
    background: '国内顶尖大学',
    timeline: '2023年8月启动，2024年3月录取'
  },
  {
    id: 141,
    name: 'S同学',
    avatar: '/images/avatar2.jpg',
    university: '约翰霍普金斯大学',
    major: '国际关系',
    category: 'graduate',
    testimonial: '约翰霍普金斯大学的SAIS学院是国际关系领域的顶尖学府。在新英美的指导下，我成功地展示了自己对国际事务的深刻见解和跨文化交流能力。',
    gpa: '3.85',
    testType: 'GRE',
    testScore: '328',
    result: '直接录取',
    tags: ['顶尖名校', '国际关系'],
    background: '国内985院校',
    timeline: '2023年9月启动，2024年2月录取'
  },
  {
    id: 142,
    name: 'L同学',
    avatar: '/images/avatar3.jpg',
    university: '西北大学',
    major: '整合营销传播',
    category: 'graduate',
    testimonial: '西北大学的整合营销传播项目是该领域的开创者和领导者。新英美的导师帮助我深入理解了IMC的理论框架，并指导我完成了高质量的案例分析。',
    gpa: '3.82',
    testType: 'TOEFL',
    testScore: '108',
    result: '直接录取',
    tags: ['顶尖名校', '整合营销'],
    background: '国内211院校',
    timeline: '2023年8月启动，2024年3月录取'
  },
  {
    id: 143,
    name: 'W同学',
    avatar: '/images/avatar1.jpg',
    university: '杜克大学',
    major: '环境管理',
    category: 'graduate',
    testimonial: '杜克大学的环境管理项目非常注重跨学科研究和实践能力。在新英美的帮助下，我成功地将自己的环保热情和专业知识结合起来，给招生官留下了深刻印象。',
    gpa: '3.80',
    testType: 'GRE',
    testScore: '325',
    result: '直接录取',
    tags: ['顶尖名校', '环境管理'],
    background: '国内985院校',
    timeline: '2023年9月启动，2024年2月录取'
  },
  {
    id: 144,
    name: 'Z同学',
    avatar: '/images/avatar2.jpg',
    university: '康奈尔大学',
    major: '酒店管理',
    category: 'graduate',
    testimonial: '康奈尔大学的酒店管理学院是全球最好的酒店管理学院之一。新英美的导师帮助我深入挖掘了自己在酒店行业的实习经历和管理潜力。',
    gpa: '3.78',
    testType: 'TOEFL',
    testScore: '106',
    result: '直接录取',
    tags: ['常春藤', '酒店管理'],
    background: '国内211院校',
    timeline: '2023年8月启动，2024年3月录取'
  },
  {
    id: 145,
    name: 'C同学',
    avatar: '/images/avatar3.jpg',
    university: '布朗大学',
    major: '计算机科学',
    category: 'graduate',
    testimonial: '布朗大学的CS项目以其开放的课程设置和紧密的师生关系而闻名。在新英美的指导下，我成功地展示了我的学术好奇心和独立研究能力。',
    gpa: '3.88',
    testType: 'GRE',
    testScore: '332',
    result: '直接录取',
    tags: ['常春藤', '计算机科学'],
    background: '国内985院校',
    timeline: '2023年9月启动，2024年2月录取'
  },
  {
    id: 146,
    name: 'L同学',
    avatar: '/images/avatar1.jpg',
    university: '莱斯大学',
    major: '建筑学',
    category: 'graduate',
    testimonial: '莱斯大学的建筑学院以其精湛的教学和前沿的设计理念而著称。新英美的导师帮助我精心准备了我的作品集，并指导我如何在面试中展示我的设计才华。',
    gpa: '3.82',
    testType: 'TOEFL',
    testScore: '107',
    result: '直接录取',
    tags: ['顶尖名校', '建筑学'],
    background: '国内顶尖建筑院校',
    timeline: '2023年8月启动，2024年3月录取'
  },
  {
    id: 147,
    name: 'S同学',
    avatar: '/images/avatar2.jpg',
    university: '圣母大学',
    major: '会计',
    category: 'graduate',
    testimonial: '圣母大学的会计硕士项目是美国最好的会计项目之一。在新英美的帮助下，我成功地通过了严格的面试，并获得了宝贵的录取机会。',
    gpa: '3.85',
    testType: 'GMAT',
    testScore: '740',
    result: '直接录取',
    tags: ['顶尖名校', '会计'],
    background: '国内985院校',
    timeline: '2023年9月启动，2024年2月录取'
  },
  {
    id: 148,
    name: 'W同学',
    avatar: '/images/avatar3.jpg',
    university: '范德堡大学',
    major: '教育学',
    category: 'graduate',
    testimonial: '范德堡大学的皮博迪教育学院是全美顶尖的教育学院之一。在新英美的指导下，我成功地将自己的教学实践和教育理想到融入到申请中。',
    gpa: '3.80',
    testType: 'GRE',
    testScore: '326',
    result: '直接录取',
    tags: ['顶尖名校', '教育学'],
    background: '国内师范大学',
    timeline: '2023年8月启动，2024年3月录取'
  },
  {
    id: 149,
    name: 'L同学',
    avatar: '/images/avatar1.jpg',
    university: '加州大学洛杉矶分校',
    major: '电影制作',
    category: 'graduate',
    testimonial: 'UCLA的电影学院是所有电影人心中的梦想之地。新英美的导师帮助我精心打磨了我的作品集和剧本，让我的创意和才华得到了充分展示。',
    gpa: '3.75',
    testType: 'TOEFL',
    testScore: '105',
    result: '直接录取',
    tags: ['顶尖名校', '电影制作'],
    background: '国内艺术院校',
    timeline: '2023年9月启动，2024年3月录取'
  },
  {
    id: 150,
    name: 'M同学',
    avatar: '/images/avatar2.jpg',
    university: '加州大学伯克利分校',
    major: '信息管理与系统',
    category: 'graduate',
    testimonial: '伯克利的信息学院是信息科学领域的领导者。在新英美的帮助下，我成功地展示了我在数据分析和系统设计方面的优势。',
    gpa: '3.88',
    testType: 'GRE',
    testScore: '330',
    result: '直接录取',
    tags: ['顶尖名校', '信息管理'],
    background: '国内985院校',
    timeline: '2023年8月启动，2024年2月录取'
  },
  {
    id: 151,
    name: 'Y同学',
    avatar: '/images/avatar3.jpg',
    university: '南加州大学',
    major: '游戏设计',
    category: 'graduate',
    testimonial: '南加州大学的游戏设计项目是全球最好的游戏设计项目之一。新英美的导师帮助我深入挖掘了我的游戏创意和设计能力。',
    gpa: '3.78',
    testType: 'TOEFL',
    testScore: '104',
    result: '直接录取',
    tags: ['顶尖名校', '游戏设计'],
    background: '国内艺术院校',
    timeline: '2023年9月启动，2024年3月录取'
  },
  {
    id: 152,
    name: 'S同学',
    avatar: '/images/avatar1.jpg',
    university: '卡内基梅隆大学',
    major: '人机交互',
    category: 'graduate',
    testimonial: '卡内基梅隆大学的人机交互项目是该领域的顶尖项目。在新英美的指导下，我成功地完成了多个高质量的HCI项目，并在面试中表现出色。',
    gpa: '3.90',
    testType: 'GRE',
    testScore: '332',
    result: '直接录取',
    tags: ['顶尖名校', '人机交互'],
    background: '国内985院校',
    timeline: '2023年8月启动，2024年2月录取'
  },
  {
    id: 153,
    name: 'L同学',
    avatar: '/images/avatar2.jpg',
    university: '弗吉尼亚大学',
    major: '商业分析',
    category: 'graduate',
    testimonial: '弗吉尼亚大学的商业分析项目非常注重实践和案例教学。在新英美的帮助下，我成功地将自己的数据分析能力和商业洞察力结合起来。',
    gpa: '3.82',
    testType: 'GMAT',
    testScore: '730',
    result: '直接录取',
    tags: ['顶尖名校', '商业分析'],
    background: '国内211院校',
    timeline: '2023年9月启动，2024年3月录取'
  },
  {
    id: 154,
    name: 'W同学',
    avatar: '/images/avatar3.jpg',
    university: '密歇根大学安娜堡分校',
    major: '公共卫生',
    category: 'graduate',
    testimonial: '密歇根大学的公共卫生学院是全美顶尖的公共卫生学院之一。在新英美的指导下，我成功地将自己的医学背景和公共卫生理想融入到申请中。',
    gpa: '3.85',
    testType: 'GRE',
    testScore: '328',
    result: '直接录取',
    tags: ['顶尖名校', '公共卫生'],
    background: '国内医科大学',
    timeline: '2023年8月启动，2024年2月录取'
  },
  {
    id: 155,
    name: 'Z同学',
    avatar: '/images/avatar1.jpg',
    university: '纽约大学',
    major: '整合营销',
    category: 'graduate',
    testimonial: '纽约大学的整合营销项目地处纽约市中心，拥有无与伦比的行业资源和实践机会。在新英美的帮助下，我成功地获得了宝贵的录取机会。',
    gpa: '3.78',
    testType: 'TOEFL',
    testScore: '107',
    result: '直接录取',
    tags: ['顶尖名校', '整合营销'],
    background: '国内211院校',
    timeline: '2023年9月启动，2024年3月录取'
  },
  {
    id: 156,
    name: 'C同学',
    avatar: '/images/avatar2.jpg',
    university: '塔夫茨大学',
    major: '国际法',
    category: 'graduate',
    testimonial: '塔夫茨大学的弗莱彻法律与外交学院是国际法领域的顶尖学府。在新英美的指导下，我成功地展示了自己对国际法的深刻理解和浓厚兴趣。',
    gpa: '3.88',
    testType: 'TOEFL',
    testScore: '110',
    result: '直接录取',
    tags: ['顶尖名校', '国际法'],
    background: '国内顶尖大学',
    timeline: '2023年8月启动，2024年2月录取'
  },
  {
    id: 157,
    name: 'L同学',
    avatar: '/images/avatar3.jpg',
    university: '加州大学圣地亚哥分校',
    major: '金融',
    category: 'graduate',
    testimonial: '加州大学圣地亚哥分校的金融硕士项目以其严谨的量化训练和优越的地理位置而闻名。在新英美的帮助下，我成功地通过了面试，并获得了录取。',
    gpa: '3.85',
    testType: 'GMAT',
    testScore: '720',
    result: '直接录取',
    tags: ['顶尖名校', '金融'],
    background: '国内985院校',
    timeline: '2023年9月启动，2024年3月录取'
  },
  {
    id: 158,
    name: 'S同学',
    avatar: '/images/avatar1.jpg',
    university: '伊利诺伊大学香槟分校',
    major: '会计',
    category: 'graduate',
    testimonial: 'UIUC的会计硕士项目是全美最好的会计项目之一。在新英美的指导下，我成功地展示了我的专业能力和职业规划。',
    gpa: '3.82',
    testType: 'GMAT',
    testScore: '710',
    result: '直接录取',
    tags: ['顶尖名校', '会计'],
    background: '国内211院校',
    timeline: '2023年8月启动，2024年2月录取'
  },
  {
    id: 159,
    name: 'W同学',
    avatar: '/images/avatar2.jpg',
    university: '威斯康星大学麦迪逊分校',
    major: '市场营销',
    category: 'graduate',
    testimonial: '威斯康星大学麦迪逊分校的市场营销项目非常注重研究和数据分析。在新英美的帮助下，我成功地完成了多个高质量的研究项目。',
    gpa: '3.80',
    testType: 'GRE',
    testScore: '325',
    result: '直接录取',
    tags: ['顶尖名校', '市场营销'],
    background: '国内985院校',
    timeline: '2023年9月启动，2024年3月录取'
  },
  {
    id: 160,
    name: 'L同学',
    avatar: '/images/avatar3.jpg',
    university: '华盛顿大学',
    major: '信息管理',
    category: 'graduate',
    testimonial: '华盛顿大学的信息学院是信息科学领域的顶尖学院之一。在新英美的指导下，我成功地展示了我在信息管理和数据科学方面的潜力。',
    gpa: '3.85',
    testType: 'GRE',
    testScore: '328',
    result: '直接录取',
    tags: ['顶尖名校', '信息管理'],
    background: '国内985院校',
    timeline: '2023年8月启动，2024年2月录取'
  },
  {
    id: 161,
    name: 'M同学',
    avatar: '/images/avatar1.jpg',
    university: '宾州州立大学',
    major: '供应链管理',
    category: 'graduate',
    testimonial: '宾州州立大学的供应链管理项目是全美最好的供应链管理项目之一。在新英美的帮助下，我成功地将自己的实习经历和职业规划融入到申请中。',
    gpa: '3.78',
    testType: 'GMAT',
    testScore: '700',
    result: '直接录取',
    tags: ['顶尖名校', '供应链管理'],
    background: '国内211院校',
    timeline: '2023年9月启动，2024年3月录取'
  },
  {
    id: 162,
    name: 'Y同学',
    avatar: '/images/avatar2.jpg',
    university: '佛罗里达大学',
    major: '市场营销',
    category: 'graduate',
    testimonial: '佛罗里达大学的市场营销项目非常注重实践和创新。在新英美的指导下，我成功地展示了我的市场洞察力和创新思维。',
    gpa: '3.75',
    testType: 'GMAT',
    testScore: '690',
    result: '直接录取',
    tags: ['顶尖名校', '市场营销'],
    background: '国内211院校',
    timeline: '2023年8月启动，2024年2月录取'
  },
  {
    id: 163,
    name: 'S同学',
    avatar: '/images/avatar3.jpg',
    university: '德州农工大学',
    major: '石油工程',
    category: 'graduate',
    testimonial: '德州农工大学的石油工程项目是全球最好的石油工程项目之一。在新英美的帮助下，我成功地展示了我的专业知识和研究潜力。',
    gpa: '3.80',
    testType: 'GRE',
    testScore: '322',
    result: '直接录取',
    tags: ['顶尖名校', '石油工程'],
    background: '国内石油大学',
    timeline: '2023年9月启动，2024年3月录取'
  },
  {
    id: 164,
    name: 'L同学',
    avatar: '/images/avatar1.jpg',
    university: '俄亥俄州立大学',
    major: '教育学',
    category: 'graduate',
    testimonial: '俄亥俄州立大学的教育学院是全美顶尖的教育学院之一。在新英美的指导下，我成功地将自己的教学实践和教育理想到融入到申请中。',
    gpa: '3.78',
    testType: 'GRE',
    testScore: '320',
    result: '直接录取',
    tags: ['顶尖名校', '教育学'],
    background: '国内师范大学',
    timeline: '2023年8月启动，2024年2月录取'
  },
  {
    id: 165,
    name: 'W同学',
    avatar: '/images/avatar2.jpg',
    university: '普渡大学',
    major: '计算机科学',
    category: 'graduate',
    testimonial: '普渡大学的CS项目以其严谨的学术训练和丰富的实践机会而闻名。在新英美的帮助下，我成功地展示了我的编程能力和项目经验。',
    gpa: '3.85',
    testType: 'GRE',
    testScore: '330',
    result: '直接录取',
    tags: ['顶尖名校', '计算机科学'],
    background: '国内985院校',
    timeline: '2023年9月启动，2024年3月录取'
  },
  {
    id: 166,
    name: 'Z同学',
    avatar: '/images/avatar3.jpg',
    university: '马里兰大学',
    major: '信息系统',
    category: 'graduate',
    testimonial: '马里兰大学的信息系统项目非常注重实践和技术应用。在新英美的指导下，我成功地将自己的技术背景和职业规划结合起来。',
    gpa: '3.80',
    testType: 'GRE',
    testScore: '325',
    result: '直接录取',
    tags: ['顶尖名校', '信息系统'],
    background: '国内211院校',
    timeline: '2023年8月启动，2024年2月录取'
  },
  {
    id: 167,
    name: 'C同学',
    avatar: '/images/avatar1.jpg',
    university: '佐治亚理工学院',
    major: '工业工程',
    category: 'graduate',
    testimonial: '佐治亚理工学院的工业工程项目是全美最好的工业工程项目之一。在新英美的帮助下，我成功地展示了我的分析能力和解决问题的能力。',
    gpa: '3.88',
    testType: 'GRE',
    testScore: '332',
    result: '直接录取',
    tags: ['顶尖名校', '工业工程'],
    background: '国内985院校',
    timeline: '2023年9月启动，2024年3月录取'
  },
  {
    id: 168,
    name: 'L同学',
    avatar: '/images/avatar2.jpg',
    university: '明尼苏达大学双城分校',
    major: '化学工程',
    category: 'graduate',
    testimonial: '明尼苏达大学双城分校的化学工程项目是全美顶尖的化学工程项目之一。在新英美的指导下，我成功地展示了我的科研能力和学术潜力。',
    gpa: '3.85',
    testType: 'GRE',
    testScore: '328',
    result: '直接录取',
    tags: ['顶尖名校', '化学工程'],
    background: '国内985院校',
    timeline: '2023年8月启动，2024年2月录取'
  },
  {
    id: 169,
    name: 'S同学',
    avatar: '/images/avatar3.jpg',
    university: '加州大学戴维斯分校',
    major: '农业与环境科学',
    category: 'graduate',
    testimonial: '加州大学戴维斯分校的农业与环境科学项目是全球最好的相关项目之一。在新英美的帮助下，我成功地将自己的研究兴趣和职业规划融入到申请中。',
    gpa: '3.82',
    testType: 'TOEFL',
    testScore: '105',
    result: '直接录取',
    tags: ['顶尖名校', '农业科学'],
    background: '国内农业大学',
    timeline: '2023年9月启动，2024年3月录取'
  },
  {
    id: 170,
    name: 'W同学',
    avatar: '/images/avatar1.jpg',
    university: '德克萨斯大学奥斯汀分校',
    major: '会计',
    category: 'graduate',
    testimonial: '德克萨斯大学奥斯汀分校的会计硕士项目是全美最好的会计项目之一。在新英美的指导下，我成功地通过了严格的面试，并获得了宝贵的录取机会。',
    gpa: '3.88',
    testType: 'GMAT',
    testScore: '740',
    result: '直接录取',
    tags: ['顶尖名校', '会计'],
    background: '国内985院校',
    timeline: '2023年8月启动，2024年2月录取'
  }
];
ALL_CASES.push(...newCases);
</script>

<style scoped>
.cases-page {
  min-height: 100vh;
  background: #F9F8F4;
  font-family: sans-serif;
  color: #292524;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

/* 英雄区域样式 (Updated to match Country.vue) */
.country-hero {
  background-color: #F9F8F4;
  color: #1d1d1f;
  padding: 4rem 1rem 2rem;
  text-align: center;
  margin-top: 0;
  width: 100%;
  box-sizing: border-box;
}

.country-hero-content {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.section-label {
  font-size: 0.875rem;
  font-weight: 600;
  color: #86868b;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  margin-bottom: 0.5rem;
}

.country-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 1.5rem;
  letter-spacing: -0.02em;
  line-height: 1.1;
}

.gold-separator {
  width: 60px;
  height: 4px;
  background-color: #C5A059;
  margin: 0 auto 2rem;
  border-radius: 2px;
}

.country-subtitle {
  font-size: 1.25rem;
  max-width: 600px;
  margin: 0 auto;
  color: #86868b;
  line-height: 1.6;
}

.country-selector-section {
  background-color: white;
  padding: 1rem 1rem;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.filter-tabs {
  display: flex;
  justify-content: center;
  gap: 1.5rem;
  flex-wrap: wrap;
}

.filter-tab {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 120px;
  height: 44px;
  padding: 0;
  cursor: pointer;
  border-radius: 12px;
  transition: all 0.3s ease;
  background-color: #F9F8F4;
  border: 1px solid transparent;
  color: #57534E;
  font-family: sans-serif;
  font-size: 14px;
  font-weight: 500;
  box-shadow: none;
}

.filter-tab:hover {
  background-color: white;
  border-color: #C5A059;
  color: #C5A059;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.filter-tab.active {
  background: #C5A059;
  color: white;
  box-shadow: 0 4px 12px rgba(197, 160, 89, 0.3);
  border-color: #C5A059;
  transform: translateY(-2px);
}

.tab-text {
  font-weight: 500;
  letter-spacing: 0.5px;
}

/* 统计数据区域 */
.stats-section {
  padding: 1.5rem 0 3rem;
  background-color: #F9F8F4;
  /* border-bottom: 1px solid rgba(197, 160, 89, 0.3); */
}

.hero-stats {
  display: flex;
  justify-content: center;
  gap: 64px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-family: "Times New Roman", Times, serif;
  font-size: 48px;
  font-weight: 400;
  color: #C5A059;
  margin-bottom: 8px;
  line-height: 1;
}

.stat-label {
  font-family: sans-serif;
  font-size: 14px;
  color: #86868b;
  font-weight: 500;
  letter-spacing: 1px;
  text-transform: uppercase;
}

/* 筛选区域样式 (Removed old styles) */
/* .filter-section {
  padding: 40px 0;
  background: #F9F8F4;
  position: sticky;
  top: 64px;
  z-index: 10;
  border-bottom: 1px solid rgba(41, 37, 36, 0.05);
  backdrop-filter: blur(10px);
  background: rgba(249, 248, 244, 0.95);
} */

/* .filter-tabs {
  display: flex;
  justify-content: center;
  gap: 16px;
  flex-wrap: wrap;
}

.filter-tab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 28px;
  border: 1px solid transparent;
  border-radius: 30px;
  background: #FFFFFF;
  color: #57534E;
  font-family: sans-serif;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.165, 0.84, 0.44, 1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
}

.filter-tab:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  color: #1c1917;
}

.filter-tab.active {
  background: #1c1917;
  color: #F9F8F4;
  box-shadow: 0 8px 20px rgba(28, 25, 23, 0.2);
}

.tab-icon {
  font-size: 16px;
}

.tab-text {
  font-weight: 500;
  letter-spacing: 0.5px;
} */

/* 案例展示区域样式 */
.cases-section {
  padding: 40px 0 80px;
  background: #F9F8F4;
}

.cases-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 20px;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
}

.load-more {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  margin-top: 24px;
}

.load-more-button {
  background: #C5A059;
  color: #FFFFFF;
  border: none;
  border-radius: 12px;
  padding: 10px 18px;
  cursor: pointer;
  font-weight: 600;
  transition: filter 0.2s ease;
}

.load-more-button:hover {
  filter: brightness(0.95);
}

.load-more-hint {
  font-size: 12px;
  color: #86868b;
}

.case-card {
  background: #FFFFFF;
  border-radius: 16px;
  border: none;
  padding: 20px;
  transition: all 0.5s cubic-bezier(0.165, 0.84, 0.44, 1);
  display: flex;
  flex-direction: column;
  position: relative;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.02);
}

.case-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.06);
}

.case-header {
  margin-bottom: 12px;
}

.student-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.student-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  border: 2px solid rgba(197, 160, 89, 0.2);
  padding: 2px;
  background: white;
}

.student-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.student-details {
  flex: 1;
}

.student-name {
  font-family: "Times New Roman", Times, serif;
  font-size: 18px;
  font-weight: 500;
  color: #1c1917;
  margin-bottom: 2px;
}

.admission-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  margin-bottom: 6px;
}

.university {
  font-family: "Times New Roman", Times, serif;
  font-size: 15px;
  font-weight: 500;
  color: #C5A059;
  letter-spacing: 0.5px;
}

.major {
  font-family: sans-serif;
  font-size: 12px;
  color: #86868b;
  font-weight: 400;
}

.result-badge {
  display: inline-flex;
  padding: 3px 8px;
  background: rgba(197, 160, 89, 0.1);
  color: #C5A059;
  font-size: 10px;
  font-weight: 600;
  border-radius: 20px;
  letter-spacing: 1px;
  text-transform: uppercase;
}

.case-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex: 1;
}

.testimonial {
  font-family: "Times New Roman", Times, serif;
  font-size: 14px;
  line-height: 1.5;
  color: #44403c;
  margin: 0;
  padding: 0;
  background: transparent;
  border: none;
  font-style: italic;
  flex: 1;
  position: relative;
}

.testimonial::before {
  content: '“';
  font-family: serif;
  font-size: 32px;
  color: rgba(197, 160, 89, 0.2);
  position: absolute;
  top: -16px;
  left: -6px;
  line-height: 1;
}

.case-details {
  margin-bottom: 0;
  padding: 12px;
  background: #F9F8F4;
  border-radius: 12px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
  gap: 8px;
  padding-bottom: 6px;
  border-bottom: 1px solid rgba(41, 37, 36, 0.06);
}

.detail-row:last-child {
  border-bottom: none;
  padding-bottom: 0;
  margin-bottom: 0;
}

.detail-label {
  font-family: sans-serif;
  font-size: 11px;
  color: #86868b;
  font-weight: 600;
  flex-shrink: 0;
  min-width: auto;
  margin-right: 4px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.detail-value {
  font-family: sans-serif;
  font-size: 12px;
  color: #292524;
  text-align: right;
  flex: 1;
  line-height: 1.4;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.case-metrics {
  display: flex;
  gap: 10px;
  margin-top: auto;
  padding-top: 4px;
  border-top: none;
}

.metric-item {
  text-align: center;
  flex: 1;
  padding: 8px;
  background: white;
  border-radius: 8px;
  border: 1px solid rgba(41, 37, 36, 0.05);
}

.metric-label {
  display: block;
  font-family: sans-serif;
  font-size: 10px;
  color: #86868b;
  margin-bottom: 2px;
  text-transform: uppercase;
  letter-spacing: 1px;
  font-weight: 600;
}

.metric-value {
  display: block;
  font-family: "Times New Roman", Times, serif;
  font-size: 18px;
  font-weight: 500;
  color: #C5A059;
}

.case-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 12px;
}

.tag {
  padding: 3px 8px;
  background: white;
  color: #86868b;
  font-family: sans-serif;
  font-size: 11px;
  font-weight: 500;
  border-radius: 20px;
  transition: all 0.3s ease;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.tag:hover {
  background: #1c1917;
  color: #F9F8F4;
  border-color: #1c1917;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-stats {
    flex-direction: column;
    gap: 32px;
  }

  .stat-number {
    font-size: 42px;
  }

  .filter-tabs {
    flex-wrap: nowrap;
    overflow-x: auto;
    justify-content: flex-start;
    padding: 0 20px;
    -webkit-overflow-scrolling: touch;
  }

  .filter-tab {
    flex-shrink: 0;
  }

  .cases-grid {
    grid-template-columns: 1fr;
    gap: 16px;
    padding: 0 16px;
  }

  .case-metrics {
    gap: 16px;
  }
}

@media (max-width: 480px) {
  .case-card {
    padding: 16px;
  }

  .student-info {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }

  .admission-info {
    align-items: center;
  }

  .case-metrics {
    flex-direction: column;
    gap: 16px;
  }

  .metric-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .metric-label {
    margin-bottom: 0;
  }
}
</style>
