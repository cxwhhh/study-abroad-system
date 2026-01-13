<template>
  <NavBar />
  <div class="school-matching-container">
    <div class="main-content">
      <div class="matching-panel">
        <div class="content-grid">
          <StudentInfoForm :is-matching="isMatching" :student-info="studentInfo" :major-categories="majorCategories"
            :expanded-categories="expandedCategories" @start-matching="startMatching" @reset-form="resetForm"
            @update:studentInfo="updateStudentInfo" @toggle-category="toggleCategory"
            @handle-major-change="handleMajorChange" />
          <MatchingResults :is-matching="isMatching" :matched-schools="matchedSchools" :student-info="studentInfo"
            @export-to-pdf="exportToPDF" @update-matched-schools="updateMatchedSchools" />
        </div>
      </div>
    </div>
  </div>
  <FooterBar />
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElLoading } from "element-plus";
import StudentInfoForm from "./components/StudentInfoForm.vue";
import MatchingResults from "./components/MatchingResults.vue";
import { useMajorCategories } from "@/composables/useMajorCategories";
import { exportSchoolReport } from '@/utils/pdfExporter';
import NavBar from "@/components/NavBar.vue";
import FooterBar from "@/components/FooterBar.vue";
import matchingService from '@/api/matchingService';
import sessionManager from '@/utils/sessionManager';

const { majorCategories, expandedCategories, toggleCategory } = useMajorCategories();

const studentInfo = reactive({
  name: "",
  undergraduateSchool: "",
  undergraduateMajor: "",
  gpaScale: "100",
  gpa: null,
  major: [],
  degreeType: "master",
  preferredCountries: [],
  budget: "",
  ieltsScore: null,
  toeflScore: null,
  pteScore: null,
});

const isMatching = ref(false);
const matchedSchools = ref([]);

const updateStudentInfo = (newInfo) => {
  Object.assign(studentInfo, newInfo);
};

const handleMajorChange = (value) => {
  studentInfo.major = value;
};

// 处理从MatchingResults组件传来的学校数据更新
const updateMatchedSchools = (schools) => {
  matchedSchools.value = schools;
  console.log('父组件接收到学校数据更新:', schools);
};

const startMatching = async () => {
  const userId = sessionManager.getUserId();
  if (!userId) {
    ElMessage.error('请先登录再进行匹配');
    return;
  }
  // 验证必填字段
  if (!studentInfo.undergraduateSchool || !studentInfo.gpa) {
    ElMessage.error('请填写完整的学生信息');
    return;
  }

  // 验证目标国家
  if (!studentInfo.preferredCountries || studentInfo.preferredCountries.length === 0) {
    ElMessage.error('请选择目标国家');
    return;
  }

  isMatching.value = true;
  matchedSchools.value = [];

  try {
    console.log('开始匹配，学生信息:', studentInfo);

    // 地区参数映射：将前端的英文代码转换为后端期望的中文
    const regionMapping = {
      'uk': '英国',
      'us': '美国',
      'usa': '美国',
      'australia': '澳洲',
      'singapore': '新加坡',
      'hongkong': '香港',
      'hong-kong': '香港'
    };

    const rawRegion = Array.isArray(studentInfo.preferredCountries) ? studentInfo.preferredCountries[0] : studentInfo.preferredCountries;
    const mappedRegion = regionMapping[rawRegion?.toLowerCase()] || rawRegion || '英国';

    console.log('原始地区参数:', rawRegion, '映射后地区参数:', mappedRegion);

    const languagePayload = (() => {
      const ielts = studentInfo.ieltsScore != null && studentInfo.ieltsScore !== '' ? parseFloat(studentInfo.ieltsScore) : null;
      const toefl = studentInfo.toeflScore != null && studentInfo.toeflScore !== '' ? parseFloat(studentInfo.toeflScore) : null;
      const pte = studentInfo.pteScore != null && studentInfo.pteScore !== '' ? parseFloat(studentInfo.pteScore) : null;

      if (Number.isFinite(ielts)) return { test: 'IELTS', score: ielts };
      if (Number.isFinite(toefl)) return { test: 'TOEFL', score: toefl };
      if (Number.isFinite(pte)) return { test: 'PTE', score: pte };
      return { test: null, score: null };
    })();

    // 构建匹配请求
    const matchingRequest = {
      region: mappedRegion,
      targetMajors: Array.isArray(studentInfo.major) ? studentInfo.major : (studentInfo.major ? [studentInfo.major] : []),
      studentInfo: {
        userId: sessionManager.getUserId(),
        name: studentInfo.name || '',
        undergraduateSchool: studentInfo.undergraduateSchool,
        undergraduateMajor: studentInfo.undergraduateMajor,
        gpa: studentInfo.gpa ? parseFloat(studentInfo.gpa) : null,
        gpaScale: studentInfo.gpaScale,
        targetMajor: Array.isArray(studentInfo.major) ? (studentInfo.major[0] || '') : (studentInfo.major || ''),
        targetDegree: studentInfo.degreeType || 'master',
        languageTest: languagePayload.test,
        languageScore: languagePayload.score,
        workExperience: studentInfo.workExperience || '',
        researchExperience: studentInfo.researchExperience || '',
        awards: studentInfo.awards || '',
        publications: studentInfo.publications || ''
      }
    };

    // 调用新的后端API获取匹配结果
    const response = await matchingService.performMatching(matchingRequest);

    if (response && response.success && response.data) {
      // 处理API返回的匹配结果数据
      const processedSchools = response.data.map(result => ({
        id: result.schoolId,
        name: result.schoolName,
        englishName: result.schoolName, // 如果有英文名可以从学校信息中获取
        logo: '/images/schools/default-school.jpg',
        worldRank: result.schoolRank || 0,
        rank: result.schoolRank || 0,
        recommendedMajor: result.majorName || '未指定专业',
        major: result.majorName || '未指定专业',
        duration: '1年', // 默认值，可以从专业信息中获取
        tuition: '待查询', // 默认值，可以从学校信息中获取
        country: result.region,
        matchingScore: Math.round(result.matchScore * 100) / 100,
        acceptanceRate: result.admissionProbability || 0,
        tier: result.matchLevel,
        analysis: result.matchReason || '根据您的背景进行的智能匹配分析',
        matchLevel: result.matchLevel,
        recommendationIndex: result.recommendationIndex || 0
      }));

      matchedSchools.value = processedSchools;
      console.log('匹配结果:', processedSchools);
      ElMessage.success(`匹配成功！找到 ${processedSchools.length} 所合适的学校`);

    } else {
      // 如果API没有返回数据
      console.log('API返回空数据或失败:', response);
      ElMessage.warning(response?.message || '未找到匹配的学校，请尝试调整筛选条件');
      matchedSchools.value = [];
    }

  } catch (error) {
    console.error('匹配失败:', error);
    ElMessage.error(error.message || '匹配失败，请稍后重试');
    matchedSchools.value = [];
  } finally {
    isMatching.value = false;
  }
};

// 模拟数据已移除，现在只使用真实的API数据

const resetForm = () => {
  Object.assign(studentInfo, {
    name: "",
    undergraduateSchool: "",
    undergraduateMajor: "",
    gpaScale: "100",
    gpa: null,
    major: [],
    degreeType: "master",
    preferredCountries: [],
    budget: "",
    ieltsScore: null,
    toeflScore: null,
    pteScore: null,
  });
  matchedSchools.value = [];
  ElMessage.info("表单已重置");
};

// [修改] 导出PDF的方法
const exportToPDF = (filteredData) => {
  // 检查是否接收到了过滤后的数据
  // 如果没有(比如直接调用)，则回退到使用全部 matchedSchools
  const dataToExport = filteredData && filteredData.length > 0
    ? filteredData
    : matchedSchools.value;

  if (dataToExport && dataToExport.length > 0) {
    ElMessage.success(`正在生成包含 ${dataToExport.length} 个专业的选校报告...`);
    // 调用工具函数导出 PDF
    exportSchoolReport(studentInfo, dataToExport);
  } else {
    ElMessage.warning('没有可导出的匹配结果，请先进行匹配并勾选专业');
  }
};
</script>

<style scoped>
.school-matching-container {
  padding: 96px 10px 40px;
  background-color: #F9F8F4;
  min-height: 100vh;
  width: 100%;
  --el-color-primary: #C5A059;
  --el-color-primary-light-3: #D8BE87;
  --el-color-primary-light-5: #E7D4A8;
  --el-color-primary-light-7: #F1E7CF;
  --el-color-primary-light-8: #F7F0E2;
  --el-color-primary-light-9: #FBF7EF;
  --el-color-primary-dark-2: #A88442;
}

.main-content {
  max-width: 1360px;
  margin: 0 auto;
}

.matching-panel {
  background-color: #ffffff;
  border: 1px solid #e7e5e4;
  border-radius: 16px;
  padding: 18px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.05);
}

.content-grid {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 24px;
  align-items: start;
}

@media (max-width: 1024px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
}

:deep(.footer-bar) {
  border-top: 1px solid #e5e5e5;
  background-color: #ffffff;
}
</style>
