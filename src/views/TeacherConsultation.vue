<template>
  <div class="teacher-consultation">
    <nav-bar></nav-bar>

    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="container">
        <h1>新英美留学</h1>
        <div class="intro-text">
          <p>我们拥有一支经验丰富、专业资深的留学顾问团队，每位顾问都具备多年的留学申请经验和深厚的专业背景。</p>
          <p>他们不仅熟悉各国教育体系和申请流程，更能为您提供个性化的留学规划和专业指导。</p>
          <p>无论您是想申请本科、硕士还是博士项目，我们的专业团队都将为您提供最优质的咨询服务。</p>
        </div>
      </div>
    </div>

    <!-- 老师介绍区域 -->
    <div class="teachers-section">
      <div class="container">
        <div class="teachers-grid">
          <!-- 老师展示 -->
          <div v-for="teacher in teachers" :key="teacher.id" class="teacher-card-wrapper">
            <div class="teacher-card">
              <div class="teacher-image">
                <img :src="teacher.image" :alt="teacher.name" />
              </div>
              <div class="teacher-info">
                <h3>{{ teacher.name }}</h3>
                <p class="teacher-title">{{ teacher.title }}</p>
                <p class="teacher-description">{{ teacher.description }}</p>
                <div class="consult-btn-wrapper">
                  <el-button class="consult-btn" round>立即咨询</el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <footer-bar></footer-bar>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import NavBar from '../components/NavBar.vue'
import FooterBar from '../components/FooterBar.vue'

// 老师数据
const teachers = ref([
  {
    id: 1,
    name: 'YY老师',
    title: '资深留学顾问',
    description: 'YY老师拥有8年丰富的留学咨询经验，专注于英美名校申请指导。她曾帮助多名学生成功获得理想院校的录取通知书，包括世界顶尖学府。YY老师以其细致入微的服务态度和专业的申请策略，深受学生和家长的信赖。',
    image: '/images/teachers/y.jpg'
  },
  {
    id: 2,
    name: '大毛老师',
    title: '澳洲留学专家',
    description: '毛老师拥有10年留学行业从业经验。他对澳洲教育体系有着深入的了解，特别擅长澳洲TOP30院校的申请规划。毛老师曾成功指导数名学生进入顶尖学府，在理工科专业申请方面有着独到的见解。',
    image: '/images/teachers/mao.jpg'
  },
  {
    id: 3,
    name: '田田老师',
    title: '文书写作专家',
    description: '田田老师拥有英语文学硕士学位，专业从事留学文书写作指导工作。她擅长帮助学生挖掘个人亮点，打造独具特色的申请材料。田田老师的文书指导不仅注重语言表达的准确性，更关注内容的深度和个性化，帮助学生在众多申请者中脱颖而出。',
    image: '/images/teachers/tian.jpg'
  },
  {
    id: 4,
    name: '轩轩老师',
    title: '留学咨询专家',
    description: '轩轩老师具有留学专业背景，专注于留学申请指导。对各国留学院校的申请要求和作品集制作标准有着深入的研究，曾帮助众多学生成功申请到世界顶尖学府，在留学领域享有很高声誉。',
    image: '/images/teachers/xuan.jpg'
  },
  {
    id: 5,
    name: 'Rita老师',
    title: '申请专家',
    description: 'Rita老师拥有丰富的海外留学申请经验，专注于为学生提供个性化的申请策略和专业指导。她深谙各国名校的录取标准和申请流程，擅长挖掘学生的独特优势，制定最适合的申请方案。Rita老师以其专业的服务和卓越的成果，帮助众多学生实现了留学梦想。',
    image: '/images/teachers/rita.jpg'
  },
  {
    id: 6,
    name: '藤子老师',
    title: '项目负责人 · 文书专家',
    description: '藤子老师是经验丰富的项目负责人和文书写作专家，拥有深厚的教育背景和丰富的留学申请指导经验。她擅长统筹管理整个申请流程，同时在文书创作方面有着独到的见解。藤子老师能够帮助学生打造具有说服力的个人陈述和推荐信，确保申请材料的质量和完整性。',
    image: '/images/teachers/teng.jpg'
  },
  {
    id: 7,
    name: '丹丹老师',
    title: '材料投递专家',
    description: '丹丹老师对金融、管理、市场营销等商科专业有着深入的了解，熟悉各大商学院的录取偏好和申请流程。丹老师曾成功帮助学生申请到沃顿商学院、伦敦商学院等顶尖商学院，在商科留学领域经验丰富。',
    image: '/images/teachers/dan.jpg'
  },
  {
    id: 8,
    name: '果子老师',
    title: '英国申请专家',
    description: '果子老师对计算机、软件、金融等专业有着深入的了解，熟悉各大英国学院的录取偏好和申请流程。果子老师曾成功帮助学生申请到UCL、LSE等顶尖学院，在英国留学领域经验丰富',
    image: '/images/teachers/guo1.jpg'
  },
  {
    id: 9,
    name: '朔雪老师',
    title: '语言培训专家',
    description: '朔雪老师是资深的雅思托福培训讲师，拥有多年语言教学经验。她擅长根据学生的基础和目标制定个性化的学习计划，帮助学生快速提升语言成绩。赵老师的学生中有很多在短期内取得了显著的成绩提升，为后续的留学申请奠定了坚实的语言基础。',
    image: '/images/teachers/xue.jpg'
  },
])


</script>

<style scoped>
.teacher-consultation {
  min-height: 100vh;
  background: #ffffff;
  display: flex;
  flex-direction: column;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

/* 页面标题区域 */
.page-header {
  background: linear-gradient(to bottom, #F9F8F4, #FFFFFF);
  color: #1c1917;
  padding: 80px 0 60px;
  margin-top: 0;
  text-align: center;
  position: relative;
  border-bottom: 1px solid rgba(197, 160, 89, 0.3);
}

.page-header::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 50%;
  transform: translateX(-50%);
  width: 100px;
  height: 3px;
  background-color: #C5A059;
}

.page-header h1 {
  font-family: "Times New Roman", Times, serif;
  font-size: 56px;
  font-weight: normal;
  margin-bottom: 24px;
  letter-spacing: 1px;
  color: #1c1917;
}

.intro-text {
  max-width: 800px;
  margin: 0 auto;
}

.intro-text p {
  font-size: 18px;
  line-height: 1.8;
  margin-bottom: 12px;
  color: #57534e;
  font-weight: 300;
}

.intro-text p:last-child {
  margin-bottom: 0;
}

/* 老师介绍区域 */
.teachers-section {
  flex: 1;
  padding: 40px 0;
  background: #FFFFFF;
}

.teachers-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40px;
}

/* Teacher Card Wrapper handles entrance animation */
.teacher-card-wrapper {
  animation: fadeUp 0.8s ease-out forwards;
  opacity: 0;
  transform: translateY(30px);
  display: flex;
  flex-direction: column;
}

.teacher-card-wrapper:nth-child(1) {
  animation-delay: 0.1s;
}

.teacher-card-wrapper:nth-child(2) {
  animation-delay: 0.2s;
}

.teacher-card-wrapper:nth-child(3) {
  animation-delay: 0.3s;
}

.teacher-card-wrapper:nth-child(4) {
  animation-delay: 0.4s;
}

.teacher-card-wrapper:nth-child(5) {
  animation-delay: 0.5s;
}

.teacher-card-wrapper:nth-child(6) {
  animation-delay: 0.6s;
}

.teacher-card-wrapper:nth-child(7) {
  animation-delay: 0.7s;
}

.teacher-card-wrapper:nth-child(8) {
  animation-delay: 0.8s;
}

.teacher-card-wrapper:nth-child(9) {
  animation-delay: 0.9s;
}

@keyframes fadeUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Teacher Card handles hover effects and visual style */
.teacher-card {
  background: #FFFFFF;
  padding: 40px 30px;
  text-align: center;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(0, 0, 0, 0.03);
  position: relative;
  overflow: hidden;
  transition: transform 0.5s cubic-bezier(0.34, 1.56, 0.64, 1),
    box-shadow 0.5s cubic-bezier(0.34, 1.56, 0.64, 1),
    border-color 0.5s ease;
  will-change: transform;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.teacher-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.teacher-description {
  flex: 1;
}

.teacher-card:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 20px 40px rgba(197, 160, 89, 0.15);
  border-color: rgba(197, 160, 89, 0.2);
}

.teacher-image {
  width: 140px;
  height: 140px;
  margin: 0 auto 14px;
  border-radius: 50%;
  padding: 4px;
  border: 2px solid rgba(197, 160, 89, 0.2);
  transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.teacher-card:hover .teacher-image {
  border-color: #C5A059;
  transform: scale(1.05);
}

.teacher-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.teacher-info h3 {
  font-family: "Times New Roman", Times, serif;
  font-size: 24px;
  font-weight: 600;
  color: #1c1917;
  margin: 0 0 8px;
}

.teacher-title {
  font-size: 13px;
  color: #C5A059;
  margin-bottom: 20px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.teacher-description {
  font-size: 15px;
  color: #57534e;
  line-height: 1.7;
  margin-bottom: 0;
  text-align: justify;
  text-justify: inter-ideograph;
}

.consult-btn-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 18px;
}

.consult-btn {
  height: 40px;
  padding: 0 22px;
  border: 1px solid rgba(197, 160, 89, 0.35);
  background: linear-gradient(135deg, #C5A059, #D8B56C);
  color: #ffffff;
  font-weight: 600;
  letter-spacing: 0.5px;
  box-shadow: 0 10px 18px rgba(197, 160, 89, 0.22);
  position: relative;
  overflow: hidden;
  transition: transform 0.18s ease, box-shadow 0.18s ease, filter 0.18s ease;
}

.consult-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -45%;
  width: 45%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.38), transparent);
  transform: skewX(-18deg);
  transition: left 0.55s ease;
}

.consult-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 14px 26px rgba(197, 160, 89, 0.28);
  filter: brightness(1.03);
}

.consult-btn:hover::before {
  left: 120%;
}

.consult-btn:active {
  transform: translateY(0);
  box-shadow: 0 8px 16px rgba(197, 160, 89, 0.2);
  filter: brightness(0.98);
}

.consult-btn:focus-visible {
  outline: none;
  box-shadow: 0 0 0 3px rgba(197, 160, 89, 0.22), 0 12px 22px rgba(197, 160, 89, 0.24);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .teachers-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 30px;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 100px 0 40px;
  }

  .page-header h1 {
    font-size: 36px;
  }

  .intro-text p {
    font-size: 16px;
  }

  .teachers-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .teachers-section {
    padding: 40px 0;
  }

  .teacher-card {
    padding: 30px 20px;
  }
}
</style>
