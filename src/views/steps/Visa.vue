<template>
  <div class="step-page">
    <nav-bar></nav-bar>
    
    <!-- 页面标题 已经弃用-->
    <div class="page-header">
      <h1>签证准备</h1>
      <div class="timing">录取后1-2个月</div>
    </div>

    <!-- 主要内容区域 -->
    <div class="page-content">
      <!-- 签证材料 -->
      <div class="content-section">
        <h2>签证材料</h2>
        <div class="materials-grid">
          <div class="material-item" v-for="(item, index) in visaMaterials" :key="index">
            <div class="material-icon">
              <el-icon><component :is="item.icon" /></el-icon>
            </div>
            <div class="material-content">
              <h3>{{ item.title }}</h3>
              <p>{{ item.description }}</p>
              <div class="material-note" v-if="item.note">
                <strong>注意：</strong>
                <p>{{ item.note }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 申请流程 -->
      <div class="content-section">
        <h2>申请流程</h2>
        <div class="process-timeline">
          <el-timeline>
            <el-timeline-item
              v-for="(step, index) in visaProcess"
              :key="index"
              :type="step.type"
              :color="step.color"
              :size="step.size"
              :timestamp="step.timestamp">
              <h3>{{ step.title }}</h3>
              <p>{{ step.content }}</p>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>

      <!-- 注意事项 -->
      <div class="content-section">
        <h2>注意事项</h2>
        <div class="notes-grid">
          <el-card v-for="(note, index) in visaNotes" :key="index" class="note-card">
            <template #header>
              <div class="note-header">
                <el-icon><Warning /></el-icon>
                <span>{{ note.title }}</span>
              </div>
            </template>
            <div class="note-content">
              <p>{{ note.content }}</p>
              <ul v-if="note.details">
                <li v-for="(detail, detailIndex) in note.details" :key="detailIndex">
                  {{ detail }}
                </li>
              </ul>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 咨询部分 -->
      <div class="consultation-section">
        <h2>需要帮助？</h2>
        <p>我们的签证顾问可以为您提供专业的签证申请指导</p>
        <el-button type="primary" size="large" @click="showConsultation">
          立即咨询
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import NavBar from '../../components/NavBar.vue'
import { Document, Money, Tickets, Calendar, Warning } from '@element-plus/icons-vue'

// 签证材料数据
const visaMaterials = ref([
  {
    icon: Tickets,
    title: '护照',
    description: '有效期需超过6个月的护照原件',
    note: '确保护照信息页清晰完整,无破损'
  },
  {
    icon: Document,
    title: '录取通知书',
    description: '学校正式录取通知书原件',
    note: '需要原件,不接受复印件'
  },
  {
    icon: Money,
    title: '资金证明',
    description: '足够支付学习和生活费用的存款证明',
    note: '金额需满足使馆要求,存期通常要求3-6个月'
  },
  {
    icon: Document,
    title: '学习计划',
    description: '详细的学习和职业规划',
    note: '需要清晰说明学习目标和回国计划'
  }
])

// 申请流程数据
const visaProcess = ref([
  {
    timestamp: '第一步',
    title: '准备材料',
    content: '收集并准备所有必需的签证申请材料',
    type: 'primary'
  },
  {
    timestamp: '第二步',
    title: '填写申请',
    content: '完整填写签证申请表格',
    type: 'primary'
  },
  {
    timestamp: '第三步',
    title: '预约面签',
    content: '在使馆官网预约面签时间',
    type: 'primary'
  },
  {
    timestamp: '第四步',
    title: '面签',
    content: '按时参加签证面试',
    type: 'primary'
  },
  {
    timestamp: '第五步',
    title: '等待审批',
    content: '等待签证审批结果',
    type: 'success'
  }
])

// 注意事项数据
const visaNotes = ref([
  {
    title: '材料真实性',
    content: '所有提供的材料必须真实有效,不得提供虚假材料',
    details: [
      '确保所有文件都是真实的',
      '不要提供虚假的资金证明',
      '如实回答签证官的问题'
    ]
  },
  {
    title: '时间规划',
    content: '合理安排签证申请时间,预留充足的处理时间',
    details: [
      '提前3-4个月开始准备材料',
      '预留足够的签证处理时间',
      '考虑可能的补充材料时间'
    ]
  },
  {
    title: '面签准备',
    content: '充分准备面签,提高签证通过率',
    details: [
      '准备常见问题的回答',
      '整理材料便于查找',
      '保持良好的精神状态'
    ]
  }
])

// 显示咨询弹窗
const showConsultation = () => {
  // 实现咨询功能
}
</script>

<style scoped>
.step-page {
  padding-top: 60px;
}

.page-header {
  background: linear-gradient(135deg, #409EFF, #36D1DC);
  color: white;
  padding: 60px 20px;
  text-align: center;
}

.page-header h1 {
  font-size: 36px;
  margin: 0 0 10px;
}

.timing {
  font-size: 18px;
  opacity: 0.9;
}

.page-content {
  max-width: 1200px;
  margin: 40px auto;
  padding: 0 20px;
}

.content-section {
  margin-bottom: 60px;
}

.content-section h2 {
  font-size: 28px;
  color: #333;
  margin-bottom: 30px;
  text-align: center;
}

.materials-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 30px;
}

.material-item {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.material-icon {
  background: #409EFF;
  color: white;
  width: 50px;
  height: 50px;
  border-radius: 25px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-bottom: 20px;
}

.material-content h3 {
  margin: 0 0 10px;
  font-size: 20px;
  color: #333;
}

.material-content p {
  margin: 0 0 15px;
  color: #666;
  line-height: 1.6;
}

.material-note {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 8px;
  margin-top: 15px;
}

.material-note strong {
  color: #f56c6c;
  display: block;
  margin-bottom: 5px;
}

.material-note p {
  margin: 0;
  font-size: 14px;
}

.process-timeline {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.notes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
  padding: 20px;
}

.note-card {
  border-radius: 12px;
  overflow: hidden;
}

.note-header {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #f56c6c;
}

.note-header .el-icon {
  font-size: 20px;
}

.note-content {
  color: #666;
}

.note-content p {
  margin: 0 0 15px;
  line-height: 1.6;
}

.note-content ul {
  margin: 0;
  padding-left: 20px;
}

.note-content li {
  margin-bottom: 8px;
  line-height: 1.6;
}

.consultation-section {
  text-align: center;
  background: #f5f7fa;
  padding: 60px 20px;
  border-radius: 12px;
}

.consultation-section h2 {
  margin-bottom: 20px;
}

.consultation-section p {
  color: #666;
  margin-bottom: 30px;
}

@media (max-width: 768px) {
  .materials-grid,
  .notes-grid {
    grid-template-columns: 1fr;
  }
}
</style> 