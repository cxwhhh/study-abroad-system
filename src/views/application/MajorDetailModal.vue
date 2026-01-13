<template>
  <el-dialog v-model="visible" width="900px" :before-close="handleClose" class="major-detail-dialog" :show-close="false"
    destroy-on-close append-to-body>
    <!-- 自定义头部 -->
    <template #header>
      <div class="dialog-header">
        <div class="header-left">
          <div class="school-logo">
            <img :src="getSchoolLogo(schoolInfo?.logoUrl || schoolInfo?.image)" :alt="schoolInfo?.name" @error="onSchoolLogoError" />
          </div>
          <div class="header-title">
            <div class="school-name">{{ schoolInfo?.name }}</div>
            <h1 class="major-name">{{ majorDetail?.name }}</h1>
            <div class="major-en-name">{{ majorDetail?.englishName }}</div>
          </div>
        </div>
        <div class="header-right">
          <button class="close-btn" @click="handleClose">
            <el-icon>
              <Close />
            </el-icon>
          </button>
        </div>
      </div>
    </template>

    <div class="dialog-body" v-if="majorDetail">
      <!-- 顶部关键标签 -->
      <div class="tags-bar">
        <el-tag type="primary" effect="dark" class="main-tag">{{ majorDetail.degreeType || '硕士' }}</el-tag>
        <el-tag type="success" effect="plain" v-if="majorDetail.isPopular">热门专业</el-tag>
        <el-tag type="warning" effect="plain" v-if="majorDetail.scholarshipAvailable">提供奖学金</el-tag>
        <span class="faculty-label" v-if="facultyInfo">
          <el-icon>
            <School />
          </el-icon> 所属学院：{{ facultyInfo.name }}
        </span>
      </div>

      <div class="content-layout">
        <!-- 左侧主要内容 -->
        <div class="main-column">
          <!-- 导航锚点 (可选，或者直接使用 Tabs) -->
          <el-tabs v-model="activeTab" class="content-tabs">
            <el-tab-pane label="专业概况" name="overview">
              <div class="section-block">
                <h3 class="section-title">专业介绍</h3>
                <div class="text-content" v-if="majorDetail.description">
                  {{ majorDetail.description }}
                </div>
                <el-empty v-else description="暂无介绍" image-size="60"></el-empty>
              </div>

              <div class="section-block">
                <h3 class="section-title">培养目标</h3>
                <div class="text-content" v-if="majorDetail.objective">
                  {{ majorDetail.objective }}
                </div>
                <div class="text-content text-gray" v-else>暂无培养目标描述</div>
              </div>

              <div class="section-block">
                <h3 class="section-title">核心课程</h3>
                <div v-if="coreCoursesList.length > 0" class="course-grid">
                  <div v-for="(course, index) in coreCoursesList" :key="index" class="course-item">
                    <el-icon>
                      <Notebook />
                    </el-icon>
                    <span>{{ course }}</span>
                  </div>
                </div>
                <div v-else class="text-content" v-html="formatRichText(majorDetail.curriculum || '暂无课程信息')"></div>
              </div>
            </el-tab-pane>

            <el-tab-pane label="申请要求" name="requirements">
              <div class="section-block">
                <h3 class="section-title">语言要求</h3>
                <div class="requirements-cards">
                  <div class="req-card ielts">
                    <div class="req-label">IELTS (雅思)</div>
                    <div class="req-value">{{ majorDetail.ieltsRequirement || 'N/A' }}</div>
                  </div>
                  <div class="req-card toefl">
                    <div class="req-label">TOEFL (托福)</div>
                    <div class="req-value">{{ majorDetail.toeflRequirement || 'N/A' }}</div>
                  </div>
                </div>
              </div>

              <div class="section-block">
                <h3 class="section-title">学术要求 (GPA/标化)</h3>
                <div class="info-list">
                  <div class="info-row">
                    <span class="label">平均GPA：</span>
                    <span class="value">{{ majorDetail.averageGpa ? `${majorDetail.averageGpa}+` : '详询顾问' }}</span>
                  </div>
                  <div class="info-row">
                    <span class="label">GRE要求：</span>
                    <span class="value">{{ majorDetail.greRequirement || '无强制要求' }}</span>
                  </div>
                  <div class="info-row">
                    <span class="label">GMAT要求：</span>
                    <span class="value">{{ majorDetail.gmatRequirement || '无强制要求' }}</span>
                  </div>
                  <div class="info-row">
                    <span class="label">背景要求：</span>
                    <span class="value">{{ majorDetail.admissionRequirements || '相关专业背景优先' }}</span>
                  </div>
                  <div class="info-row">
                    <span class="label">工作经验：</span>
                    <span class="value">{{ majorDetail.workExperienceRequired || '不强制' }}</span>
                  </div>
                </div>
              </div>
            </el-tab-pane>

            <el-tab-pane label="就业与发展" name="career">
              <div class="section-block">
                <h3 class="section-title">就业前景</h3>
                <div class="text-content" v-if="majorDetail.careerProspects">
                  {{ majorDetail.careerProspects }}
                </div>
                <el-empty v-else description="暂无就业数据" image-size="60"></el-empty>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>

        <!-- 右侧侧边栏信息 -->
        <div class="side-column">
          <div class="info-card highlight-card">
            <div class="card-title">关键信息</div>
            <div class="kv-item">
              <span class="k"><el-icon>
                  <Timer />
                </el-icon> 学制</span>
              <span class="v">{{ majorDetail.duration || '1年' }}</span>
            </div>
            <div class="kv-item">
              <span class="k"><el-icon>
                  <Calendar />
                </el-icon> 开学时间</span>
              <span class="v">{{ majorDetail.startDate || '9月' }}</span>
            </div>
            <div class="kv-item">
              <span class="k"><el-icon>
                  <Money />
                </el-icon> 学费</span>
              <span class="v price">{{ formatCurrency(majorDetail.tuitionFee, majorDetail.feeCurrency) }}</span>
            </div>
            <div class="kv-item">
              <span class="k"><el-icon>
                  <Place />
                </el-icon> 校区</span>
              <span class="v">{{ schoolInfo?.campus || '主校区' }}</span>
            </div>
          </div>

          <div class="info-card">
            <div class="card-title">申请信息</div>
            <div class="kv-item">
              <span class="k">截止日期</span>
              <span class="v text-danger">{{ majorDetail.applicationDeadline || '滚动录取' }}</span>
            </div>
            <div class="kv-item">
              <span class="k">专业代码</span>
              <span class="v">{{ majorDetail.code || '--' }}</span>
            </div>
            <div class="kv-item">
              <span class="k">返点比例</span>
              <span class="v highlight-text">{{ majorDetail.rebatePercentage ? majorDetail.rebatePercentage + '%' : '0%'
                }}</span>
            </div>
          </div>

          <div class="info-card links-card" v-if="majorDetail.website || majorDetail.brochureUrl">
            <div class="card-title">相关资源</div>
            <a v-if="majorDetail.website" :href="majorDetail.website" target="_blank" class="resource-link">
              <el-icon>
                <Link />
              </el-icon> 官方网站
            </a>
            <a v-if="majorDetail.brochureUrl" :href="majorDetail.brochureUrl" target="_blank" class="resource-link">
              <el-icon>
                <Document />
              </el-icon> 下载简章
            </a>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="loading-wrapper">
      <el-skeleton :rows="10" animated />
    </div>

    <!-- 底部按钮 -->
    <template #footer>
      <div class="dialog-footer">
        <div class="footer-left">
          <el-button class="collect-btn" @click="handleCollect" text>
            <el-icon>
              <Star />
            </el-icon> 收藏
          </el-button>
        </div>
        <div class="footer-right">
          <el-button @click="handleConsult">咨询顾问</el-button>
          <el-button type="primary" @click="handleApply" size="large" color="#4F46E5">
            立即申请该专业
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import {
  Close, School, Notebook, Timer, Calendar, Money, Place, Link, Document, Star
} from '@element-plus/icons-vue'

// Props
const props = defineProps({
  visible: { type: Boolean, default: false },
  majorDetail: { type: Object, default: null },
  schoolInfo: { type: Object, default: null },
  facultyInfo: { type: Object, default: null }
})

// Emits
const emit = defineEmits(['update:visible', 'apply', 'consult', 'collect'])

// State
const activeTab = ref('overview')

// Computed
const visible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value)
})

// 解析核心课程字符串（如果后端存的是逗号分隔字符串）
const coreCoursesList = computed(() => {
  if (!props.majorDetail?.coreCourses) return [];
  // 如果已经是数组
  if (Array.isArray(props.majorDetail.coreCourses)) return props.majorDetail.coreCourses;
  // 如果是字符串
  return props.majorDetail.coreCourses.split(/[,，、\n]/).filter(item => item.trim());
});

// Methods
const handleClose = () => {
  visible.value = false
}

const handleApply = () => {
  emit('apply', props.majorDetail)
}

const handleConsult = () => {
  emit('consult', props.majorDetail)
}

const handleCollect = () => {
  emit('collect', props.majorDetail)
}

const formatCurrency = (amount, currency = 'CNY') => {
  if (!amount) return '待咨询';
  // 简单的格式化
  const symbol = currency === 'USD' ? '$' : (currency === 'GBP' ? '£' : '¥');
  return `${symbol}${Number(amount).toLocaleString()}`;
}

const formatRichText = (text) => {
  if (!text) return '';
  // 简单的换行处理，实际项目可能需要更复杂的HTML清洗
  return text.replace(/\n/g, '<br>');
}

const resolvePublicUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http') || path.startsWith('data:') || path.startsWith('blob:')) return path
  const base = import.meta.env.BASE_URL || '/'
  const normalized = path.startsWith('/') ? path.slice(1) : path
  return `${base}${normalized}`
}

const defaultSchoolLogo = resolvePublicUrl('images/schools/default-school.jpg')

const getSchoolLogo = (url) => {
  if (!url || typeof url !== 'string') return defaultSchoolLogo
  const cleanPath = url.trim().replace(/\r?\n/g, '').replace(/\\/g, '/')
  if (!cleanPath || cleanPath === 'null' || cleanPath === 'undefined') return defaultSchoolLogo
  if (cleanPath.startsWith('http') || cleanPath.startsWith('data:') || cleanPath.startsWith('blob:')) return encodeURI(cleanPath)
  if (cleanPath.startsWith('/')) return encodeURI(resolvePublicUrl(cleanPath))
  if (!cleanPath.includes('/')) return encodeURI(resolvePublicUrl(`images/schools/${cleanPath}`))
  return encodeURI(resolvePublicUrl(cleanPath))
}

const onSchoolLogoError = (event) => {
  const img = event?.target
  if (!img) return
  if (img.dataset.fallbackDone === '1') return
  img.dataset.fallbackDone = '1'
  img.onerror = null
  img.src = defaultSchoolLogo
}

// Watchers
watch(() => props.visible, (newVal) => {
  if (newVal) {
    activeTab.value = 'overview'
  }
})
</script>

<style scoped lang="scss">
/* 重写 Element Plus Dialog 样式 */
:deep(.major-detail-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);

  .el-dialog__header {
    padding: 0;
    margin: 0;
  }

  .el-dialog__body {
    padding: 0;
    background-color: #F9FAFB;
    /* 浅灰背景 */
    min-height: 400px;
  }

  .el-dialog__footer {
    padding: 0;
    margin: 0;
  }
}

/* Header 样式 */
.dialog-header {
  background: linear-gradient(135deg, #4F46E5 0%, #7C3AED 100%);
  /* 现代化渐变紫 */
  padding: 24px 32px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  color: white;
}

.header-left {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.school-logo {
  width: 72px;
  height: 72px;
  background: white;
  border-radius: 12px;
  padding: 4px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);

  img {
    width: 100%;
    height: 100%;
    object-fit: contain;
    border-radius: 8px;
  }
}

.header-title {
  .school-name {
    font-size: 14px;
    opacity: 0.9;
    margin-bottom: 4px;
  }

  .major-name {
    font-size: 24px;
    font-weight: 700;
    margin: 0 0 4px 0;
    line-height: 1.3;
  }

  .major-en-name {
    font-size: 15px;
    font-weight: 400;
    opacity: 0.8;
  }
}

.close-btn {
  background: rgba(255, 255, 255, 0.15);
  border: none;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: rgba(255, 255, 255, 0.3);
  }
}

/* Body 布局 */
.dialog-body {
  padding: 24px 32px;
}

.tags-bar {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 24px;

  .main-tag {
    font-size: 13px;
    font-weight: 600;
  }

  .faculty-label {
    margin-left: auto;
    color: #6B7280;
    font-size: 13px;
    display: flex;
    align-items: center;
    gap: 4px;
  }
}

.content-layout {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 24px;
  align-items: start;
}

/* 左侧主要内容 */
.main-column {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  min-height: 400px;
}

/* Element Tabs 自定义 */
:deep(.content-tabs) {
  .el-tabs__header {
    margin-bottom: 0;
    padding: 0 20px;
    border-bottom: 1px solid #E5E7EB;
  }

  .el-tabs__item {
    font-size: 15px;
    height: 50px;
    line-height: 50px;
    color: #6B7280;

    &.is-active {
      color: #4F46E5;
      font-weight: 600;
    }
  }

  .el-tabs__content {
    padding: 24px;
  }
}

.section-block {
  margin-bottom: 32px;

  &:last-child {
    margin-bottom: 0;
  }
}

.section-title {
  font-size: 16px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 12px 0;
  padding-left: 12px;
  border-left: 4px solid #4F46E5;
  line-height: 1.2;
}

.text-content {
  font-size: 14px;
  line-height: 1.7;
  color: #374151;
  white-space: pre-wrap;
}

.text-gray {
  color: #9CA3AF;
}

/* 核心课程网格 */
.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;

  .course-item {
    background: #F3F4F6;
    padding: 10px 14px;
    border-radius: 8px;
    font-size: 13px;
    color: #374151;
    display: flex;
    align-items: center;
    gap: 8px;

    .el-icon {
      color: #4F46E5;
    }
  }
}

/* 申请要求卡片 */
.requirements-cards {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;

  .req-card {
    flex: 1;
    padding: 16px;
    border-radius: 8px;
    text-align: center;

    &.ielts {
      background: #ECFDF5;
      color: #059669;

      .req-value {
        color: #059669;
      }
    }

    &.toefl {
      background: #EFF6FF;
      color: #2563EB;

      .req-value {
        color: #2563EB;
      }
    }

    .req-label {
      font-size: 12px;
      font-weight: 600;
      margin-bottom: 4px;
      opacity: 0.8;
    }

    .req-value {
      font-size: 20px;
      font-weight: 700;
    }
  }
}

.info-list {
  .info-row {
    display: flex;
    padding: 12px 0;
    border-bottom: 1px dashed #E5E7EB;
    font-size: 14px;

    &:last-child {
      border-bottom: none;
    }

    .label {
      color: #6B7280;
      width: 100px;
      flex-shrink: 0;
    }

    .value {
      color: #111827;
      font-weight: 500;
    }
  }
}

/* 右侧侧边栏 */
.side-column {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #E5E7EB;

  &.highlight-card {
    border-top: 4px solid #4F46E5;
  }

  .card-title {
    font-size: 14px;
    font-weight: 600;
    color: #111827;
    margin-bottom: 16px;
    text-transform: uppercase;
    letter-spacing: 0.5px;
  }
}

.kv-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;

  &:last-child {
    margin-bottom: 0;
  }

  .k {
    color: #6B7280;
    display: flex;
    align-items: center;
    gap: 6px;

    .el-icon {
      font-size: 16px;
    }
  }

  .v {
    color: #111827;
    font-weight: 500;
    text-align: right;

    &.price {
      color: #DC2626;
      font-weight: 700;
      font-size: 16px;
    }

    &.text-danger {
      color: #DC2626;
    }

    &.highlight-text {
      color: #4F46E5;
      font-weight: 700;
    }
  }
}

.resource-link {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #4B5563;
  text-decoration: none;
  padding: 8px 12px;
  border-radius: 6px;
  background: #F3F4F6;
  margin-bottom: 8px;
  font-size: 14px;
  transition: all 0.2s;

  &:hover {
    background: #E5E7EB;
    color: #4F46E5;
  }

  &:last-child {
    margin-bottom: 0;
  }
}

/* Footer */
.dialog-footer {
  padding: 16px 32px;
  background: white;
  border-top: 1px solid #E5E7EB;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-right {
  display: flex;
  gap: 12px;
}

.loading-wrapper {
  padding: 40px;
}

/* 响应式 */
@media (max-width: 768px) {
  .content-layout {
    grid-template-columns: 1fr;
  }

  .dialog-header {
    flex-direction: column;
    gap: 16px;
  }

  .header-right {
    position: absolute;
    top: 16px;
    right: 16px;
  }

  .dialog-footer {
    flex-direction: column;
    gap: 16px;

    .footer-right {
      width: 100%;
      display: flex;

      button {
        flex: 1;
      }
    }
  }
}
</style>
