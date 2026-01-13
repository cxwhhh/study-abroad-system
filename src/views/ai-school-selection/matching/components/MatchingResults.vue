<!-- START OF FILE src/views/ai-school-selection/matching/components/MatchingResults.vue -->
<template>
  <div class="result-card" v-if="isMatching || hasMatchingResults || isLoadingSchools">
    <!-- 加载状态 -->
    <LoadingOverlay v-if="isLoadingSchools || isMatching" />

    <!-- 错误状态 -->
    <div v-else-if="errorMessage && !hasMatchingResults" class="error-container">
      <el-icon class="error-icon">
        <Warning />
      </el-icon>
      <p class="error-text">{{ errorMessage }}</p>
      <el-button type="primary" @click="fetchMatchingResults">重新匹配</el-button>
    </div>

    <div id="pdf-content" v-else>
      <h2 class="result-title">
        <el-icon class="icon">
          <Document />
        </el-icon>
        AI智能选校报告
      </h2>

      <!-- 顶部选择统计栏 -->
      <div class="selection-bar" v-if="hasMatchingResults">
        <div class="left">
          <span>已选择 <span class="highlight-num">{{ selectedCount }}</span> 个专业方案</span>
        </div>
        <div class="right">
          <el-button link type="primary" @click="toggleSelectAll(true)">全选所有</el-button>
          <el-button link type="info" @click="toggleSelectAll(false)">清空选择</el-button>
          <el-button type="primary" size="default" @click="exportToPDF" class="export-button"
            :disabled="selectedCount === 0">
            <el-icon>
              <Download />
            </el-icon>
            {{ selectedCount > 0 ? `导出选中的 ${selectedCount} 个专业方案` : '请先选择专业' }}
          </el-button>
        </div>
      </div>

      <div class="student-summary">
        <p>
          根据 <strong>{{ studentInfo.name || '学生' }}</strong> 同学的背景和偏好，我们为您生成了以下选校建议：
        </p>
      </div>

      <!-- 学校列表 -->
      <div class="school-list">
        <div v-for="(group, index) in groupedSchools" :key="group.schoolId" class="school-group-item">

          <!-- 学校概览卡片 -->
          <div class="school-card-header" @click.stop="toggleExpand(group.schoolId)">
            <!-- 学校层级复选框 -->
            <div class="header-checkbox" @click.stop>
              <el-checkbox v-model="group.isAllSelected" :indeterminate="group.isIndeterminate"
                @change="(val) => handleSchoolSelection(val, group)" />
            </div>

            <div class="school-logo">
              <img :src="getSchoolLogo(group.logo)" :data-logo="group.logo" :alt="group.schoolName"
                @error="handleImageError" />
            </div>

            <div class="school-main-info">
              <h3 class="school-name">{{ group.schoolName }}</h3>
              <p class="school-english-name">{{ group.englishName }}</p>
              <div class="qs-ranking">
                <span class="qs-label">QS排名</span>
                <span class="qs-number">{{ group.rank || 'N/A' }}</span>
              </div>
            </div>

            <div class="school-group-stats">
              <div class="stat-tag">
                <span class="label">匹配专业</span>
                <span class="value">{{ group.majors.length }}个</span>
              </div>
              <div class="stat-tag">
                <span class="label">最高匹配度</span>
                <span class="value highlight">{{ formatMaxScore(group.maxMatchingScore) }}</span>
              </div>
            </div>

            <div class="school-actions">
              <el-button :type="group.isExpanded ? 'default' : 'primary'" size="default" class="toggle-btn">
                {{ group.isExpanded ? '收起专业' : '查看专业' }}
                <el-icon class="el-icon--right">
                  <component :is="group.isExpanded ? 'ArrowUp' : 'ArrowDown'" />
                </el-icon>
              </el-button>
            </div>
          </div>

          <div class="expand-wrapper">
            <transition name="expand-fade">
              <div v-if="group.isExpanded" class="expand-inner">
                <div class="major-list-container">
                  <div v-for="(major, mIndex) in group.majors" :key="mIndex" class="major-item"
                    :class="{ 'is-selected': isSelected(major) }" @click="toggleMajorSelection(major)">

                    <div class="major-info-row">
                      <div class="major-checkbox">
                        <el-checkbox :model-value="isSelected(major)" @change="toggleMajorSelection(major)" />
                      </div>

                      <div class="major-name-col">
                        <span class="detail-label">推荐专业</span>
                        <span class="detail-value major-name">{{ major.major }}</span>
                      </div>

                      <div class="major-stat-col">
                        <span class="detail-label">匹配分数</span>
                        <span class="detail-value match-score">{{ formatMatchScore(major.matchingScore) }}</span>
                      </div>

                      <div class="major-stat-col">
                        <span class="detail-label">匹配等级</span>
                        <span class="detail-value" :class="getMatchLevelClass(major.matchLevel)">
                          {{ major.matchLevel || '适中' }}
                        </span>
                      </div>

                      <div class="major-stat-col">
                        <span class="detail-label">录取概率</span>
                        <span class="detail-value">{{ formatAdmissionRate(major.admissionRate) }}</span>
                      </div>
                    </div>

                    <div class="major-reason" v-if="major.analysis">
                      <p><el-icon>
                          <InfoFilled />
                        </el-icon> <strong>AI分析：</strong> {{ major.analysis }}</p>
                    </div>
                  </div>
                </div>
              </div>
            </transition>
          </div>

        </div>
      </div>
    </div>
  </div>
  <div class="placeholder" v-else>
    <el-empty description="请填写信息后开始匹配" />
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue';
import { Download, Document, Loading, Warning, ArrowDown, ArrowUp, InfoFilled } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import matchingService from '@/api/matchingService';
import LoadingOverlay from './LoadingOverlay.vue';

const props = defineProps({
  isMatching: { type: Boolean, default: false },
  formData: { type: Object, default: () => ({}) },
  studentInfo: { type: Object, default: () => ({}) },
  matchedSchools: { type: Array, default: () => [] }
});

const emit = defineEmits(['update:isMatching', 'update-matched-schools', 'export-to-pdf']);

const resolvePublicUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http') || path.startsWith('data:') || path.startsWith('blob:')) return path
  const base = import.meta.env.BASE_URL || '/'
  const normalized = path.startsWith('/') ? path.slice(1) : path
  return `${base}${normalized}`
}

const defaultSchoolLogo = resolvePublicUrl('images/schools/default-school.jpg')
const failedSchoolLogos = ref(new Set())

const normalizeSchoolLogo = (raw) => {
  if (!raw || typeof raw !== 'string') return ''
  const cleanPath = raw.trim().replace(/\r?\n/g, '').replace(/\\/g, '/')
  if (!cleanPath || cleanPath === 'null' || cleanPath === 'undefined') return ''
  if (cleanPath.startsWith('http') || cleanPath.startsWith('data:') || cleanPath.startsWith('blob:')) {
    return encodeURI(cleanPath)
  }
  if (cleanPath.startsWith('/')) return encodeURI(resolvePublicUrl(cleanPath))
  if (!cleanPath.includes('/')) return encodeURI(resolvePublicUrl(`images/schools/${cleanPath}`))
  return encodeURI(resolvePublicUrl(cleanPath))
}

const getSchoolLogo = (logo) => {
  if (!logo) return defaultSchoolLogo
  return failedSchoolLogos.value.has(logo) ? defaultSchoolLogo : logo
}

const localMatchedSchools = ref([]);
const isLoadingSchools = ref(false);
const hasMatchingResults = ref(false);
const errorMessage = ref('');
const expandedState = ref({});

// 存储选中专业的唯一ID集合
const selectedKeys = ref(new Set());

// 生成唯一Key的辅助函数
const getUniqueKey = (item) => `${item.id}_${item.major}`;

// 判断是否选中
const isSelected = (item) => selectedKeys.value.has(getUniqueKey(item));

// 切换单个专业选择
const toggleMajorSelection = (item) => {
  const key = getUniqueKey(item);
  if (selectedKeys.value.has(key)) {
    selectedKeys.value.delete(key);
  } else {
    selectedKeys.value.add(key);
  }
};

// 全选/清空
const toggleSelectAll = (select) => {
  if (select) {
    localMatchedSchools.value.forEach(item => selectedKeys.value.add(getUniqueKey(item)));
  } else {
    selectedKeys.value.clear();
  }
};

// 选中的总数
const selectedCount = computed(() => selectedKeys.value.size);

// 处理学校层级的全选
const handleSchoolSelection = (val, group) => {
  group.majors.forEach(major => {
    const key = getUniqueKey(major);
    if (val) {
      selectedKeys.value.add(key);
    } else {
      selectedKeys.value.delete(key);
    }
  });
};

// 计算属性：分组数据
const groupedSchools = computed(() => {
  const groups = {};

  localMatchedSchools.value.forEach(item => {
    const id = item.id;
    if (!groups[id]) {
      groups[id] = {
        schoolId: item.id,
        schoolName: item.name,
        englishName: item.englishName,
        logo: item.logo,
        rank: item.rank,
        worldRank: item.worldRank,
        country: item.country,
        majors: [],
        maxMatchingScore: 0,
        isExpanded: expandedState.value[id] || false
      };
    }
    groups[id].majors.push(item);
    const current = Number(item.matchingScore);
    if (Number.isFinite(current)) {
      groups[id].maxMatchingScore = Math.max(groups[id].maxMatchingScore, current);
    }
  });

  // 转换为数组并计算选中状态
  const groupArray = Object.values(groups).map(group => {
    const totalMajors = group.majors.length;
    const selectedMajorsCount = group.majors.filter(m => selectedKeys.value.has(getUniqueKey(m))).length;

    group.isAllSelected = totalMajors > 0 && totalMajors === selectedMajorsCount;
    group.isIndeterminate = selectedMajorsCount > 0 && selectedMajorsCount < totalMajors;

    return group;
  });

  // 排序
  return groupArray.sort((a, b) => {
    const rankA = parseInt(a.rank) || 9999;
    const rankB = parseInt(b.rank) || 9999;
    return rankA - rankB;
  });
});

const toggleExpand = (schoolId) => {
  expandedState.value[schoolId] = !expandedState.value[schoolId];
};

const handleImageError = (event) => {
  const img = event?.target
  if (!img) return
  const original = img?.dataset?.logo
  if (original) {
    failedSchoolLogos.value = new Set([...failedSchoolLogos.value, original])
  }
  img.onerror = null
  img.src = defaultSchoolLogo
};

const formatMatchScore = (score) => {
  if (!score && score !== 0) return 'N/A';
  return `${score.toFixed(1)}分`;
};

const formatMaxScore = (score) => {
  if (!score && score !== 0) return 'N/A';
  return `${Number(score).toFixed(1)}分`;
};

const formatAdmissionRate = (rate) => {
  if (!rate && rate !== 0) return '待查询';
  if (typeof rate === 'number') {
    return `${(rate * 100).toFixed(1)}%`;
  }
  return rate;
};

const getMatchLevelClass = (level) => {
  if (!level) return 'match-level-default';
  switch (level) {
    case '保底':
    case '符合要求': return 'match-level-safe';
    case '稳妥':
    case '适中': return 'match-level-moderate';
    case '冲刺': return 'match-level-sprint';
    case '不建议':
    case '不符合要求':
    case '不符合申请资格': return 'match-level-unqualified';
    default: return 'match-level-default';
  }
};

const isQualifiedMajor = (item) => {
  const level = item?.matchLevel;
  if (level === '不建议' || level === '不符合要求' || level === '不符合申请资格') {
    return false;
  }
  const score = Number(item?.matchingScore);
  if (Number.isFinite(score) && score < 60) {
    return false;
  }
  return true;
};

const exportToPDF = () => {
  if (selectedKeys.value.size === 0) {
    ElMessage.warning("请至少选择一个专业进行导出");
    return;
  }

  const exportData = localMatchedSchools.value.filter(item =>
    selectedKeys.value.has(getUniqueKey(item))
  );

  emit("export-to-pdf", exportData);
};

const fetchMatchingResults = async () => {
  if (!props.studentInfo || Object.keys(props.studentInfo).length === 0) return;
  if (isLoadingSchools.value) return;

  try {
    isLoadingSchools.value = true;
    errorMessage.value = '';
    expandedState.value = {};

    const regionMapping = {
      'uk': '英国', 'us': '美国', 'usa': '美国',
      'australia': '澳洲', 'singapore': '新加坡',
      'hongkong': '香港', 'hong-kong': '香港'
    };

    const rawRegion = Array.isArray(props.studentInfo.preferredCountries)
      ? props.studentInfo.preferredCountries[0]
      : props.studentInfo.preferredCountries;
    const mappedRegion = regionMapping[rawRegion?.toLowerCase()] || rawRegion || '英国';

    const matchingRequest = {
      region: mappedRegion,
      studentInfo: {
        userId: props.studentInfo.userId || 1,
        undergraduateSchool: props.studentInfo.undergraduateSchool || '',
        gpa: props.studentInfo.gpa ? parseFloat(props.studentInfo.gpa) : null,
        gpaScale: props.studentInfo.gpaScale || '4.0',
        targetMajor: Array.isArray(props.studentInfo.major)
          ? props.studentInfo.major.join(',')
          : props.studentInfo.major || '',
        targetDegree: props.studentInfo.targetDegree || '硕士',
        languageTest: props.studentInfo.languageTest || 'IELTS',
        languageScore: props.studentInfo.languageScore ? parseFloat(props.studentInfo.languageScore) : null,
        workExperience: props.studentInfo.workExperience || '无',
        researchExperience: props.studentInfo.researchExperience || '无',
        awards: props.studentInfo.awards || '',
        publications: props.studentInfo.publications || ''
      }
    };

    const response = await matchingService.performMatching(matchingRequest);

    if (response && response.success && response.data) {
      const processedSchools = response.data.map(result => {
        const logoUrl = normalizeSchoolLogo(result.schoolLogo) || defaultSchoolLogo
        // 解析课程 JSON
        let coreCoursesList = [];
        try {
          if (result.majorCoreCourses) {
            coreCoursesList = JSON.parse(result.majorCoreCourses);
          }
        } catch (e) {
          console.error("解析课程数据失败", e);
        }


        return {
          id: result.schoolId,
          name: result.schoolName,
          englishName: result.schoolEnglishName,
          logo: logoUrl,
          rank: result.schoolQsRanking || 'N/A',
          worldRank: result.schoolQsRanking || 'N/A',
          recommendedMajor: result.majorName || '未指定专业',
          major: result.majorName || '未指定专业',
          country: result.region || '英国',
          // 1. AI 匹配分析 (用于展示匹配理由)
          analysis: result.matchReason || result.riskAnalysis || 'AI匹配分析生成中...',
          // 2. 数据库真实描述 (用于展示专业详情和PDF导出)
          description: result.majorDescription || '暂无专业描述', // 对应后端新增字段
          // 3. 其他详情
          curriculum: result.majorCurriculum || '暂无课程信息',
          // [新增] 结构化的课程数组
          coreCourses: coreCoursesList,
          careerProspects: result.majorCareerProspects || '暂无就业信息',
          duration: result.duration || '1年',
          tuition: result.estimatedTuition ? `£${result.estimatedTuition}` : '待查询',
          matchingScore: result.matchScore || 0,
          matchLevel: result.matchLevel || '适中',
          matchReason: result.matchReason || '',
          admissionRate: result.admissionProbability || 0,
        };
      });

      const qualified = processedSchools.filter(isQualifiedMajor);
      localMatchedSchools.value = qualified;
      hasMatchingResults.value = qualified.length > 0;

      selectedKeys.value.clear();

      // 默认不展开任何学校，根据用户反馈优化
      // const uniqueSchoolIds = [...new Set(qualified.map(item => item.id))];
      // uniqueSchoolIds.slice(0, 3).forEach(id => expandedState.value[id] = true);

      emit('update-matched-schools', qualified);
      if (qualified.length === 0) {
        ElMessage.warning('未找到匹配的学校，请尝试调整筛选条件');
      } else {
        ElMessage.success(`成功匹配到 ${qualified.length} 个推荐专业`);
      }
    } else {
      const errorMsg = response?.message || '未找到匹配的学校';
      errorMessage.value = errorMsg;
      ElMessage.warning(errorMsg);
    }
  } catch (error) {
    const errorMsg = error.message || '获取学校匹配结果失败';
    errorMessage.value = errorMsg;
    console.error(error);
    ElMessage.error(errorMsg);
  } finally {
    isLoadingSchools.value = false;
  }
};

watch(() => props.isMatching, (newIsMatching) => {
  if (newIsMatching) fetchMatchingResults();
});
</script>

<style scoped>
.selection-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #FBF7EF;
  padding: 10px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  border: 1px solid #E7D4A8;
  color: #57534e;
}

.selection-bar .right :deep(.el-button.is-link) {
  color: #A88442;
}

.selection-bar .right {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.highlight-num {
  font-weight: bold;
  color: #C5A059;
  font-size: 18px;
  margin: 0 4px;
}

.school-card-header {
  padding: 20px 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  background-color: #fff;
  cursor: pointer;
}

.header-checkbox {
  display: flex;
  align-items: center;
}

.major-info-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.major-item {
  background-color: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  margin-top: 12px;
  padding: 16px;
  transition: all 0.2s ease;
  cursor: pointer;
}

.major-item.is-selected {
  background-color: #FBF7EF;
  border-color: #E7D4A8;
  box-shadow: 0 2px 10px rgba(197, 160, 89, 0.12);
}

.major-item:hover {
  border-color: #C5A059;
  box-shadow: 0 2px 10px rgba(197, 160, 89, 0.12);
}

.major-checkbox {
  margin-right: 8px;
}

.result-card {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid #e7e5e4;
  min-height: 400px;
  position: relative;
}

.result-title {
  font-size: 22px;
  font-weight: 600;
  color: #333;
  text-align: center;
  margin-bottom: 25px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.student-summary {
  text-align: center;
  margin-bottom: 20px;
  font-size: 14px;
  color: #57534e;
  background-color: #F9F8F4;
  border: 1px solid #e7e5e4;
  padding: 10px;
  border-radius: 8px;
}

.school-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.school-group-item {
  border: 1px solid #e8e8e8;
  border-radius: 16px;
  background-color: #ffffff;
  transition: all 0.3s ease;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.school-group-item:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  border-color: #E7D4A8;
}

.school-logo {
  width: 60px;
  height: 60px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  padding: 4px;
}

.school-logo img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.school-main-info {
  flex: 0 0 240px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.school-name {
  font-size: 18px;
  font-weight: 700;
  color: #333;
  margin: 0;
  line-height: 1.3;
}

.school-english-name {
  font-size: 13px;
  color: #666;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.qs-ranking {
  display: inline-flex;
  align-items: center;
  background-color: #FBF7EF;
  color: #A88442;
  border: 1px solid #E7D4A8;
  padding: 2px 8px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 12px;
  gap: 6px;
  width: fit-content;
}

.qs-number {
  font-weight: 800;
  font-size: 13px;
}

.school-group-stats {
  flex: 1;
  display: flex;
  gap: 24px;
  padding-left: 20px;
  border-left: 1px solid #f0f0f0;
}

.stat-tag {
  display: flex;
  flex-direction: column;
}

.stat-tag .label {
  font-size: 12px;
  color: #909399;
}

.stat-tag .value {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.stat-tag .value.highlight {
  color: #C5A059;
}

.school-actions {
  flex-shrink: 0;
  margin-left: auto;
}

.toggle-btn {
  min-width: 110px;
}

.major-list-container {
  background-color: #F9F8F4;
  border-top: 1px solid #f0f0f0;
  padding: 0 24px 24px 24px;
}

.major-name-col {
  flex: 2;
  min-width: 180px;
  display: flex;
  flex-direction: column;
}

.major-stat-col {
  flex: 1;
  min-width: 90px;
  display: flex;
  flex-direction: column;
}

.detail-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.detail-value {
  font-size: 14px;
  color: #303133;
  font-weight: 600;
}

.major-name {
  font-size: 15px;
  font-weight: 700;
  color: #303133;
}

.match-score {
  color: #A88442;
}

.match-level-safe {
  color: #3f6212;
}

.match-level-moderate {
  color: #A88442;
}

.match-level-sprint {
  color: #b45309;
}

.match-level-unqualified {
  color: #b91c1c;
}

.match-level-default {
  color: #909399;
}

.major-reason {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px dashed #eee;
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
}

.major-reason .el-icon {
  vertical-align: middle;
  margin-right: 4px;
  color: #C5A059;
}

.result-actions {
  margin-top: 30px;
  text-align: center;
}

.loading-container,
.error-container,
.placeholder {
  padding: 60px 20px;
  text-align: center;
}

.loading-container .el-icon {
  font-size: 40px;
  color: #C5A059;
  margin-bottom: 15px;
}

.error-icon {
  font-size: 40px;
  color: #f56c6c;
  margin-bottom: 15px;
}

@media (max-width: 768px) {
  .school-card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .school-group-stats {
    border-left: none;
    padding-left: 0;
    width: 100%;
    justify-content: space-between;
    background: #f5f7fa;
    padding: 10px;
    border-radius: 8px;
  }

  .school-actions {
    width: 100%;
  }

  .toggle-btn {
    width: 100%;
  }

  .major-info-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .major-stat-col,
  .major-name-col {
    width: 100%;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #f0f0f0;
    padding-bottom: 4px;
  }
}

/* 优化动画性能的样式 */
.expand-wrapper {
  width: 100%;
}

.expand-inner {
  overflow: hidden;
  contain: layout paint;
  will-change: opacity, transform;
}

.major-list-container {
  content-visibility: auto;
  contain-intrinsic-size: 1px 640px;
}

.expand-fade-enter-active,
.expand-fade-leave-active {
  transition: opacity 0.18s ease, transform 0.18s ease;
}

.expand-fade-enter-from,
.expand-fade-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

.expand-fade-enter-to,
.expand-fade-leave-from {
  opacity: 1;
  transform: translateY(0);
}
</style>
