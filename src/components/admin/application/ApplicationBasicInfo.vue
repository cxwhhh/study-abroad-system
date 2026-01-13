<template>
  <div class="tab-pane">
    <!-- 申请基本信息容器 -->
    <div class="info-container">
      <h4 class="section-title">申请基本信息</h4>
      <div class="detail-grid">
        <div class="detail-item">
          <div class="detail-label">申请ID</div>
          <div class="detail-value">{{ application.id }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">学位类型</div>
          <div class="detail-value">{{ application.degree || '未设置' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">申请批次</div>
          <div class="detail-value">{{ application.round || '未设置' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">课程代码</div>
          <div class="detail-value">{{ application.code || '未设置' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">入学日期</div>
          <div class="detail-value">{{ application.entryDate || '未设置' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">学习时长</div>
          <div class="detail-value">{{ application.duration || '未设置' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">校区</div>
          <div class="detail-value">{{ application.campus || '未设置' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">学分减免</div>
          <div class="detail-value">{{ application.creditExemption || '未设置' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">负责老师</div>
          <div class="detail-value">{{ application.consultant || '未分配' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">老师邮箱</div>
          <div class="detail-value">{{ application.consultantEmail || '未设置' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">创建时间</div>
          <div class="detail-value">{{ props.formatDateFn(application.createTime) }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">更新时间</div>
          <div class="detail-value">{{ props.formatDateFn(application.updateTime) }}</div>
        </div>
      </div>
    </div>

    <!-- 学校信息容器 -->
    <div class="info-container">
      <h4 class="section-title">学校信息</h4>
      <div class="detail-grid">
        <div class="detail-item">
          <div class="detail-label">学校ID</div>
          <div class="detail-value">{{ application.schoolId || '未知' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">专业ID</div>
          <div class="detail-value">{{ application.majorId || '未知' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">国家ID</div>
          <div class="detail-value">{{ application.countryId || '未知' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">学校名称</div>
          <div class="detail-value">{{ application.institution || '未知' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">专业名称</div>
          <div class="detail-value">{{ application.program || '未知' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">国家</div>
          <div class="detail-value">{{ application.country || '未知' }}</div>
        </div>
        <div class="detail-item full-width">
          <div class="detail-label">申请链接</div>
          <div class="detail-value">
            <a v-if="application.link" :href="application.link" target="_blank" class="link-value">{{ application.link
              }}</a>
            <span v-else>未设置</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 学生信息容器 -->
    <div class="info-container">
      <h4 class="section-title">学生基本信息</h4>
      <div class="detail-grid">
        <div class="detail-item">
          <div class="detail-label">学生ID</div>
          <div class="detail-value">{{ application.student?.id || '未知' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">用户ID</div>
          <div class="detail-value">{{ application.userId || '未知' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">学生姓名</div>
          <div class="detail-value">{{ application.student?.name || '未知' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">英文名</div>
          <div class="detail-value">{{ application.student?.englishName || '未设置' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">性别</div>
          <div class="detail-value">{{ application.student?.gender || '未设置' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">出生日期</div>
          <div class="detail-value">{{ application.student?.birthDate ?
            props.formatDateFn(application.student.birthDate) : '未设置' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">国籍</div>
          <div class="detail-value">{{ application.student?.nationality || '未设置' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">护照号</div>
          <div class="detail-value">{{ application.student?.passportNo || '未设置' }}</div>
        </div>
      </div>

      <h4 class="section-title sub-section">联系方式</h4>
      <div class="detail-grid">
        <div class="detail-item">
          <div class="detail-label">邮箱</div>
          <div class="detail-value">{{ application.student?.email || '未设置' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">电话</div>
          <div class="detail-value">{{ application.student?.phone || '未设置' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">微信</div>
          <div class="detail-value">{{ application.student?.wechat || '未设置' }}</div>
        </div>
      </div>

      <h4 class="section-title sub-section">学术情况</h4>
      <div class="detail-grid">
        <div class="detail-item">
          <div class="detail-label">当前学校</div>
          <div class="detail-value">{{ application.student?.currentSchool || '未设置' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">专业</div>
          <div class="detail-value">{{ application.student?.major || '未设置' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">GPA</div>
          <div class="detail-value">{{ application.student?.gpa || '未设置' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">托福</div>
          <div class="detail-value">{{ application.student?.toefl || '未设置' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">雅思</div>
          <div class="detail-value">{{ application.student?.ielts || '未设置' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">GRE</div>
          <div class="detail-value">{{ application.student?.gre || '未设置' }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">GMAT</div>
          <div class="detail-value">{{ application.student?.gmat || '未设置' }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  application: {
    type: Object,
    required: true
  },
  formatDateFn: {
    type: Function,
    required: true
  }
});
</script>

<style scoped>
.tab-pane {
  padding: 0;
}

.info-container {
  background-color: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 6px;
  padding: 12px 16px;
  margin-bottom: 10px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 0 0 12px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.section-title.sub-section {
  margin-top: 10px;
  font-size: 14px;
  border-bottom-style: dashed;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
  margin-bottom: 12px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-item.full-width {
  grid-column: 1 / -1;
}

.detail-item.highlight {
  background-color: #f6ffed;
  padding: 8px;
  border-radius: 4px;
  border-left: 3px solid #52c41a;
}

.detail-label {
  font-size: 12px;
  color: #999;
}

.detail-value {
  font-size: 14px;
  color: #333;
  word-break: break-word;
}

.link-value {
  color: #1890ff;
  text-decoration: none;
}

.link-value:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>