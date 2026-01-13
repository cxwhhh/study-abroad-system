<template>
  <div class="school-container">
    <!-- 导航栏 -->
    <nav-bar></nav-bar>

    <!-- 学校头部信息 -->
    <div class="school-header">
      <div class="school-header-content">
        <div class="school-header-top">
          <div class="school-header-left">
            <div class="school-logo-card">
              <img v-if="schoolLogoUrl" class="school-logo" :src="schoolLogoUrl" :alt="school.name" />
              <div v-else class="school-logo-fallback">{{ (school.name || '').slice(0, 1) }}</div>
            </div>

            <div class="school-header-info">
              <h1 class="school-name">{{ school.name }}</h1>
              <p class="school-location">{{ school.location }}</p>

              <div class="school-tags">
                <span v-for="(tag, index) in school.tags" :key="index" class="school-tag">{{ tag }}</span>
              </div>
            </div>
          </div>

          <div class="school-header-right">
            <div class="ranking-card qs-ranking">
              <div class="ranking-value" :class="{ 'no-data': !school.qsRanking }">
                QS排名 {{ school.qsRanking || '暂无数据' }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>



    <!-- 学院和专业设置 -->
    <div class="majors-section">
      <div class="majors-container">
        <div class="section-header">
          <!-- 添加返回按钮 -->
          <button class="back-button" @click="goBack">
            <el-icon>
              <ArrowLeft />
            </el-icon>
            <span>返回院校库</span>
          </button>
          <h2 class="section-title">学院与专业</h2>
        </div>

        <!-- 学院列表 -->
        <div class="faculties-grid">
          <div v-for="faculty in faculties" :key="faculty.id" class="faculty-card"
            :class="{ active: selectedFaculty?.id === faculty.id }" @click="selectFaculty(faculty)">
            <h3 class="faculty-name">{{ faculty.name }}</h3>
          </div>
        </div>

        <!-- 专业列表（选择学院后显示） -->
        <div v-if="selectedFaculty" class="programs-section">
          <div class="programs-header">
            <h3 class="selected-faculty-name">{{ selectedFaculty.name }} - 专业列表</h3>
          </div>

          <!-- 专业筛选 -->
          <div class="filter-container">
            <div class="filter-row">
              <div class="filter-group">
                <span class="filter-label">学位类型:</span>
                <el-select v-model="selectedDegree" placeholder="全部" size="large">
                  <el-option label="全部" value="all"></el-option>
                  <el-option label="本科" value="bachelor"></el-option>
                  <el-option label="硕士" value="master"></el-option>
                  <el-option label="博士" value="phd"></el-option>
                </el-select>
              </div>

              <div class="filter-group">
                <span class="filter-label">排序方式:</span>
                <el-select v-model="sortBy" placeholder="综合排序" size="large">
                  <el-option label="综合排序" value="comprehensive"></el-option>
                  <el-option label="返点比例" value="rebate"></el-option>
                  <el-option label="学费" value="tuition"></el-option>
                  <el-option label="热门程度" value="popularity"></el-option>
                </el-select>
              </div>
            </div>
          </div>

          <!-- 专业无结果提示 -->
          <div v-if="filteredFacultyPrograms.length === 0" class="no-results">
            <div class="no-results-icon">
              <el-icon>
                <Search />
              </el-icon>
            </div>
            <h3 class="no-results-title">未找到相关专业</h3>
            <p class="no-results-text">请尝试调整筛选条件</p>
            <button class="reset-filters" @click="resetFilters">
              重置筛选条件
              <el-icon>
                <Refresh />
              </el-icon>
            </button>
          </div>

          <!-- 专业列表 -->
          <div class="programs-container">
            <!-- 展开的专业详情弹窗 -->
            <transition name="program-modal">
              <div v-if="expandedProgram && getExpandedProgram.id" class="program-modal-overlay"
                @click="closeExpandedProgram">
                <div class="program-modal" @click.stop>

                  <div class="program-modal-content">
                    <button class="modal-close-button" @click="closeExpandedProgram">
                      <el-icon>
                        <Close />
                      </el-icon>
                    </button>

                    <div class="program-modal-header">
                      <h3 class="program-modal-title">{{ getExpandedProgram.name }}</h3>
                      <div class="program-modal-subtitle">{{ getExpandedProgram.college }}</div>
                    </div>

                    <div class="program-modal-body">
                      <div class="program-modal-info">
                        <div class="modal-info-item">
                          <div class="modal-info-label">学位</div>
                          <div class="modal-info-value">{{ getExpandedProgram.degree }}</div>
                        </div>
                        <div class="modal-info-item">
                          <div class="modal-info-label">学制</div>
                          <div class="modal-info-value">{{ getExpandedProgram.duration }}</div>
                        </div>
                        <div class="modal-info-item">
                          <div class="modal-info-label">学费</div>
                          <div class="modal-info-value">{{ getExpandedProgram.tuition }}</div>
                        </div>
                        <div class="modal-info-item">
                          <div class="modal-info-label">返点</div>
                          <div class="modal-info-value highlight">{{ getExpandedProgram.rebate }}</div>
                        </div>
                      </div>

                      <div class="modal-section">
                        <h4 class="modal-section-title">课程设置</h4>
                        <ul class="modal-courses">
                          <li v-for="(course, index) in getCoursesForProgram(getExpandedProgram.id)" :key="index">
                            {{ course }}
                          </li>
                        </ul>
                      </div>

                      <div class="modal-section">
                        <h4 class="modal-section-title">就业前景</h4>
                        <p class="modal-career">{{ getCareerForProgram(getExpandedProgram.id) }}</p>
                      </div>

                      <div class="modal-section">
                        <h4 class="modal-section-title">申请要求</h4>
                        <div class="modal-requirements">
                          <div class="requirement-item">
                            <span class="requirement-label">GPA要求:</span>
                            <span class="requirement-value">{{ getRequirementsForProgram(getExpandedProgram.id).gpa
                            }}</span>
                          </div>
                          <div class="requirement-item">
                            <span class="requirement-label">语言要求:</span>
                            <span class="requirement-value">{{ getRequirementsForProgram(getExpandedProgram.id).language
                            }}</span>
                          </div>
                          <div class="requirement-item">
                            <span class="requirement-label">其他要求:</span>
                            <span class="requirement-value">{{ getRequirementsForProgram(getExpandedProgram.id).other
                            }}</span>
                          </div>
                        </div>
                      </div>
                    </div>

                    <div class="program-modal-footer">
                      <button class="modal-apply-button">
                        申请咨询
                        <el-icon>
                          <ArrowRight />
                        </el-icon>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </transition>

            <!-- 专业卡片列表 -->
            <transition-group name="program-list" tag="div" class="programs-grid" :duration="200">
              <div v-for="(program, index) in filteredFacultyPrograms" :key="`${program.id}-${index}`"
                class="major-card" @click="goToMajorDetail(program.id)" :style="getCardAnimationDelay(index)">
                <div class="major-header">
                  <h3 class="major-name">{{ program.name }}</h3>
                  <div class="major-degree">{{ program.degree }}</div>
                </div>
                <div class="major-info">
                  <div class="major-duration">学制: {{ program.duration }}</div>
                  <div class="major-tuition">{{ program.tuition }}</div>
                </div>
                <div class="major-tags">
                  <span v-if="program.isPopular" class="major-tag popular">热门</span>
                  <span v-if="program.hasScholarship" class="major-tag scholarship">奖学金</span>
                </div>
              </div>
            </transition-group>
          </div>
        </div>
      </div>
    </div>



    <!-- 页脚 -->
    <footer-bar></footer-bar>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowRight, ArrowDown, Location, Calendar, Trophy, School, User, Close, ArrowLeft } from '@element-plus/icons-vue'
import NavBar from '../components/NavBar.vue'
import FooterBar from '../components/FooterBar.vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import schoolService from '@/api/schoolService'
import gsap from 'gsap'

const route = useRoute()
const router = useRouter()

// 返回上一级
const goBack = () => {
  router.back()
}

// 学位选项卡
const degreeTabs = [
  { label: '全部', value: 'all' },
  { label: '本科', value: 'bachelor' },
  { label: '硕士', value: 'master' },
  { label: '博士', value: 'phd' }
]

// 筛选状态
const sortBy = ref('comprehensive')
const selectedDegree = ref('all')
const selectedCollege = ref('all')
const expandedProgram = ref(null)
const isAnimating = ref(false)

// 学院相关状态
const selectedFaculty = ref(null)
const faculties = ref([])

// 学校特色
const schoolFeatures = [
  {
    icon: 'Trophy',
    title: '世界排名',
    description: '在全球高等教育排名中名列前茅，拥有卓越的学术声誉和研究成果。'
  },
  {
    icon: 'School',
    title: '师资力量',
    description: '拥有众多诺贝尔奖得主和顶尖学者，提供高质量的教学和研究指导。'
  },
  {
    icon: 'User',
    title: '校友网络',
    description: '强大的全球校友网络，为学生提供丰富的职业发展和社交机会。'
  },
  {
    icon: 'Location',
    title: '地理位置',
    description: '位于充满活力的城市中心，周边环境优美，交通便利，文化氛围浓厚。'
  }
]



// 学校数据 (综合不同国家的学校)
const allSchools = {
  // 美国
  harvard: {
    id: 'harvard',
    name: '哈佛大学',
    location: '马萨诸塞州 剑桥',
    ranking: 1,
    rebate: '15%',
    tuition: '¥350,000/年',
    intlStudents: '24%',
    tags: ['综合大学', '私立', '常春藤'],
    description: '哈佛大学是美国历史最悠久的高等学府，也是世界上最负盛名的大学之一。自1636年创立以来，哈佛一直致力于培养引领各行各业的杰出人才。学校拥有世界一流的教学设施和研究资源，提供广泛的学术项目和丰富的校园生活。'
  }
}

// 根据URL参数获取学校ID
const schoolId = computed(() => route.params.id || 'harvard')
const school = ref({})

// 获取学校图片的方法
const getSchoolImage = (schoolId) => {
  // 检查是否有实际的图片URL
  const schoolData = school.value || {}
  if (schoolData.imageUrl && !schoolData.imageUrl.includes('null') && !schoolData.imageUrl.includes('undefined')) {
    return schoolData.imageUrl.startsWith('http') || schoolData.imageUrl.startsWith('/')
      ? schoolData.imageUrl
      : `/${schoolData.imageUrl}`
  }

  // 如果没有图片或路径无效，使用默认图片
  return `/images/schools/${schoolId}-header.jpg`
}

const schoolLogoUrl = computed(() => {
  const schoolData = school.value || {}
  const logoUrl = schoolData.logoUrl
  if (!logoUrl || String(logoUrl).includes('null') || String(logoUrl).includes('undefined')) {
    return ''
  }
  return logoUrl.startsWith('http') || logoUrl.startsWith('/') ? logoUrl : `/${logoUrl}`
})



// 获取课程数据
const getCoursesForProgram = (programId) => {
  const program = programsData.value[schoolId.value]?.find(p => p.id === programId)
  if (program && program.curriculum) {
    return program.curriculum.split('。').filter(Boolean).map(item => item.trim())
  }
  return [
    '专业核心课程1',
    '专业核心课程2',
    '专业核心课程3',
    '专业选修课程1',
    '专业选修课程2',
    '实践/实习课程'
  ]
}

// 获取就业前景数据
const getCareerForProgram = (programId) => {
  const program = programsData.value[schoolId.value]?.find(p => p.id === programId)
  return program?.careerProspects || '该专业毕业生就业前景广阔，可在相关行业的知名企业、研究机构和政府部门就职。毕业生通常具备扎实的专业知识和实践能力，在就业市场上具有较强的竞争力。'
}

// 获取申请要求数据
const getRequirementsForProgram = (programId) => {
  const program = programsData.value[schoolId.value]?.find(p => p.id === programId)
  return program?.requirements || {
    gpa: '本科GPA 3.5+/4.0',
    language: 'TOEFL 90+或IELTS 6.5+',
    other: '标准化考试成绩，推荐信，个人陈述'
  }
}

// 学院数据
const colleges = ref([])

// 专业数据
const programsData = ref({})

// 加载状态
const loading = ref(true)
const majorsLoading = ref(false)
const facultiesLoading = ref(false)
const error = ref(null)

// 从API获取学院数据
const fetchFaculties = async (schoolId) => {
  try {
    facultiesLoading.value = true
    console.log('开始获取学院数据，学校ID:', schoolId)

    const response = await schoolService.getSchoolFaculties(schoolId)
    if (response && Array.isArray(response)) {
      colleges.value = response.map(faculty => ({
        id: faculty.id,
        name: faculty.name,
        englishName: faculty.englishName,
        code: faculty.code,
        description: faculty.description
      }))

      // 同时设置faculties数据，包含专业数量统计
      faculties.value = response.map(faculty => ({
        id: faculty.id,
        name: faculty.name,
        description: faculty.description || '该学院致力于提供优质的教育和研究环境',
        programCount: 0, // 将在获取专业数据后更新
        ranking: Math.floor(Math.random() * 20) + 1 // 临时排名，实际应从API获取
      }))

      console.log('获取到学院数据:', colleges.value.length, '个学院')

      // 默认选择第一个学院
      if (faculties.value.length > 0) {
        selectedFaculty.value = faculties.value[0]
      }
    } else {
      console.warn('未获取到学院数据或数据格式不正确')
      colleges.value = []
      // 使用默认学院数据
      faculties.value = getDefaultFaculties()

      // 默认选择第一个学院
      if (faculties.value.length > 0) {
        selectedFaculty.value = faculties.value[0]
      }
    }
  } catch (err) {
    console.error('获取学院数据失败:', err)
    colleges.value = []
    // 使用默认学院数据
    faculties.value = getDefaultFaculties()

    // 默认选择第一个学院
    if (faculties.value.length > 0) {
      selectedFaculty.value = faculties.value[0]
    }
  } finally {
    facultiesLoading.value = false
  }
}

// 获取默认学院数据
const getDefaultFaculties = () => {
  return [
    {
      id: 'humanities',
      name: '人文学院',
      description: '涵盖文学、历史、哲学、语言学等人文学科，培养具有深厚人文素养的人才',
      programCount: 12,
      ranking: 3
    },
    {
      id: 'sciences',
      name: '理学院',
      description: '包含数学、物理、化学、生物等基础科学学科，注重理论研究与实践应用',
      programCount: 15,
      ranking: 2
    },
    {
      id: 'engineering',
      name: '工程学院',
      description: '提供各类工程技术专业，培养具有创新能力的工程技术人才',
      programCount: 18,
      ranking: 1
    },
    {
      id: 'business',
      name: '商学院',
      description: '商业管理、经济学、金融等专业，培养商业领袖和管理人才',
      programCount: 10,
      ranking: 4
    },
    {
      id: 'medicine',
      name: '医学院',
      description: '医学、护理、药学等健康科学专业，培养医疗健康领域的专业人才',
      programCount: 8,
      ranking: 5
    },
    {
      id: 'law',
      name: '法学院',
      description: '法学、国际法、商法等专业，培养法律专业人才',
      programCount: 6,
      ranking: 6
    }
  ]
}

// 学院选择相关方法
const selectFaculty = (faculty) => {
  selectedFaculty.value = faculty
  // 重置筛选条件
  selectedDegree.value = 'all'
  sortBy.value = 'comprehensive'
}



// 从API获取专业数据
const fetchMajors = async (schoolId) => {
  try {
    majorsLoading.value = true
    console.log('开始获取专业数据，学校ID:', schoolId)

    const response = await schoolService.getSchoolMajors(schoolId)
    if (response && Array.isArray(response)) {
      // 将API返回的专业数据保存到programsData中
      programsData.value = {
        [schoolId]: response.map(major => ({
          id: major.id,
          name: major.name,
          college: major.facultyId ? colleges.value.find(c => c.id === major.facultyId)?.name || '未知学院' : '未知学院',
          collegeId: major.facultyId,
          degree: major.degreeType || '未知',
          degreeType: getDegreeType(major.degreeType),
          duration: major.duration || '未知',
          tuition: major.tuitionFee ? `¥${major.tuitionFee}/年` : '未知',
          rebate: major.rebatePercentage ? `${major.rebatePercentage}%` : '未知',
          isPopular: major.isPopular || false,
          description: major.description || '暂无描述',
          curriculum: major.curriculum || '暂无课程信息',
          careerProspects: major.careerProspects || '暂无就业前景信息',
          requirements: {
            gpa: major.averageGpa ? `${major.averageGpa}+/4.0` : '未知',
            language: getLanguageRequirement(major),
            other: getOtherRequirements(major)
          }
        }))
      }
      console.log('获取到专业数据:', programsData.value[schoolId]?.length, '个专业')
    } else {
      console.warn('未获取到专业数据或数据格式不正确')
      programsData.value = { [schoolId]: [] }
    }
  } catch (err) {
    console.error('获取专业数据失败:', err)
    programsData.value = { [schoolId]: [] }
  } finally {
    majorsLoading.value = false
  }
}

// 获取学位类型
const getDegreeType = (degreeType) => {
  if (!degreeType) return 'all'

  if (degreeType.includes('本科')) return 'bachelor'
  if (degreeType.includes('硕士')) return 'master'
  if (degreeType.includes('博士')) return 'phd'

  // 英文匹配
  const lowerDegree = degreeType.toLowerCase()
  if (lowerDegree.includes('bachelor')) return 'bachelor'
  if (lowerDegree.includes('master')) return 'master'
  if (lowerDegree.includes('phd') || lowerDegree.includes('doctor')) return 'phd'

  return 'all'
}

// 获取语言要求
const getLanguageRequirement = (major) => {
  const requirements = []

  if (major.toeflRequirement) {
    requirements.push(`TOEFL ${major.toeflRequirement}+`)
  }

  if (major.ieltsRequirement) {
    requirements.push(`IELTS ${major.ieltsRequirement}+`)
  }

  return requirements.length > 0 ? requirements.join(' 或 ') : '未知'
}

// 获取其他要求
const getOtherRequirements = (major) => {
  const requirements = []

  if (major.gmatRequirement && major.gmatRequirement !== '不要求') {
    requirements.push(`GMAT ${major.gmatRequirement}`)
  }

  if (major.greRequirement && major.greRequirement !== '不要求') {
    requirements.push(`GRE ${major.greRequirement}`)
  }

  if (major.workExperienceRequired && major.workExperienceRequired !== '不要求') {
    requirements.push(`工作经验 ${major.workExperienceRequired}`)
  }

  return requirements.length > 0 ? requirements.join('，') : '无特殊要求'
}

// 从API获取学校数据
const fetchSchoolData = async () => {
  loading.value = true
  error.value = null

  try {
    const response = await schoolService.getSchoolById(schoolId.value)
    if (response && response.data) {
      school.value = response.data
      console.log('获取的学校数据:', school.value)

      // 处理标签
      if (school.value.tags && typeof school.value.tags === 'string') {
        school.value.tags = school.value.tags.split(',').map(tag => tag.trim()).filter(Boolean)
      } else if (!Array.isArray(school.value.tags)) {
        school.value.tags = []
      }

      // 加载学院和专业数据
      await fetchFaculties(schoolId.value)
      await fetchMajors(schoolId.value)
    } else {
      // 如果API暂未实现，使用本地数据
      school.value = allSchools[schoolId.value] || {}
      console.log('使用本地学校数据:', school.value)
    }
  } catch (err) {
    console.error('获取学校数据失败:', err)
    error.value = '获取学校数据失败'
    // 如果API暂未实现，使用本地数据
    school.value = allSchools[schoolId.value] || {}
  } finally {
    loading.value = false
  }
}

// 在组件挂载时获取数据
onMounted(() => {
  fetchSchoolData()
})

// 获取展开的专业详情
const getExpandedProgram = computed(() => {
  try {
    if (!expandedProgram.value) {
      return {};
    }

    const allPrograms = programsData.value[schoolId.value] || [];
    const program = allPrograms.find(p => p.id === expandedProgram.value);

    console.log('Finding expanded program:', expandedProgram.value);
    console.log('School ID:', schoolId.value);
    console.log('Available programs:', allPrograms.length);
    console.log('Found program:', program ? program.name : 'Not found');

    return program || {};
  } catch (error) {
    console.error('Error in getExpandedProgram:', error);
    return {};
  }
})

// 选择学院方法
const selectCollege = (collegeId) => {
  if (selectedCollege.value === collegeId) return

  selectedCollege.value = collegeId // Update immediately

  // Reset animation state
  isAnimating.value = true

  // Trigger immediate re-render
  nextTick(() => {
    animateCards()
    isAnimating.value = false
  })
}

// 选择学位方法
const selectDegree = (degree) => {
  if (selectedDegree.value === degree) return

  isAnimating.value = true

  // 先淡出当前专业卡片
  const cards = document.querySelectorAll('.program-card')
  const timeline = gsap.timeline({
    onComplete: () => {
      selectedDegree.value = degree

      // 在下一个渲染周期后，为新的卡片添加动画
      nextTick(() => {
        animateCards()
        isAnimating.value = false
      })
    }
  })

  cards.forEach(card => {
    timeline.to(card, {
      opacity: 0,
      y: 20,
      scale: 0.95,
      duration: 0.2,
      ease: 'power1.out',
      stagger: 0.05
    }, 0)
  })
}

// 获取学院专业数量
const getCollegeProgramCount = (collegeId) => {
  if (collegeId === 'all') return programsData.value[schoolId.value]?.length || 0
  return programsData.value[schoolId.value]?.filter(p => p.collegeId === parseInt(collegeId)).length || 0
}

// 获取学位专业数量
const getDegreeCount = (degree) => {
  if (degree === 'all') return programsData.value[schoolId.value]?.length || 0
  return programsData.value[schoolId.value]?.filter(p => p.degreeType === degree).length || 0
}

// 重置筛选条件
const resetFilters = () => {
  selectedCollege.value = 'all'
  selectedDegree.value = 'all'
  sortBy.value = 'comprehensive'
}

// 获取卡片动画延迟
const getCardAnimationDelay = (index) => {
  return {
    '--animation-delay': `${index * 0.05}s`
  }
}

// 展开专业详情
const expandProgram = (programId) => {
  try {
    console.log('Expanding program:', programId);

    // 禁用页面滚动
    document.body.style.overflow = 'hidden';

    // 设置被展开的程序ID
    expandedProgram.value = programId;

    // 验证程序是否存在
    const programs = programsData.value[schoolId.value] || [];
    const program = programs.find(p => p.id === programId);

    if (!program) {
      console.error('Program not found:', programId);
      return;
    }

    console.log('Program found:', program.name);
  } catch (error) {
    console.error('Error in expandProgram:', error);
  }
}

// 关闭展开的专业详情
const closeExpandedProgram = () => {
  console.log('Closing expanded program');

  // 恢复页面滚动
  document.body.style.overflow = '';

  // 清除展开的程序ID
  expandedProgram.value = null;
}

// 跳转到专业详情页面
const goToMajorDetail = (majorId) => {
  router.push(`/schools/${schoolId.value}/major/${majorId}`)
}

// 根据选中的筛选条件过滤专业
const filteredPrograms = computed(() => {
  const programs = programsData.value[schoolId.value] || []
  console.log('筛选专业，当前专业总数:', programs.length)
  console.log('筛选条件:', { college: selectedCollege.value, degree: selectedDegree.value, sortBy: sortBy.value })

  return programs.filter(program => {
    // College filter
    if (selectedCollege.value !== 'all' && program.collegeId !== parseInt(selectedCollege.value)) {
      return false
    }

    // Degree filter
    if (selectedDegree.value !== 'all') {
      if (selectedDegree.value === 'bachelor' && program.degreeType !== 'bachelor') return false
      if (selectedDegree.value === 'master' && program.degreeType !== 'master') return false
      if (selectedDegree.value === 'phd' && program.degreeType !== 'phd') return false
    }

    return true
  }).sort((a, b) => {
    switch (sortBy.value) {
      case 'rebate':
        const rebateA = parseInt(a.rebate) || 0
        const rebateB = parseInt(b.rebate) || 0
        return rebateB - rebateA
      case 'tuition':
        const tuitionA = parseInt(a.tuition.replace(/[^0-9]/g, '')) || 0
        const tuitionB = parseInt(b.tuition.replace(/[^0-9]/g, '')) || 0
        return tuitionA - tuitionB
      case 'popularity':
        return b.isPopular ? 1 : -1
      default:
        if (a.isPopular !== b.isPopular) return b.isPopular ? 1 : -1
        return 0
    }
  })
})

// 根据选中学院筛选专业
const filteredFacultyPrograms = computed(() => {
  if (!selectedFaculty.value) return []

  const programs = programsData.value[schoolId.value] || []
  console.log('筛选学院专业，学院:', selectedFaculty.value.name, '专业总数:', programs.length)

  return programs.filter(program => {
    // 学院筛选 - 根据学院名称或ID匹配
    const facultyMatch = program.college === selectedFaculty.value.name ||
      program.collegeId === selectedFaculty.value.id ||
      program.facultyId === selectedFaculty.value.id

    if (!facultyMatch) return false

    // 学位类型筛选
    if (selectedDegree.value !== 'all') {
      if (selectedDegree.value === 'bachelor' && program.degreeType !== 'bachelor') return false
      if (selectedDegree.value === 'master' && program.degreeType !== 'master') return false
      if (selectedDegree.value === 'phd' && program.degreeType !== 'phd') return false
    }

    return true
  }).sort((a, b) => {
    switch (sortBy.value) {
      case 'rebate':
        const rebateA = parseInt(a.rebate) || 0
        const rebateB = parseInt(b.rebate) || 0
        return rebateB - rebateA
      case 'tuition':
        const tuitionA = parseInt(a.tuition.replace(/[^0-9]/g, '')) || 0
        const tuitionB = parseInt(b.tuition.replace(/[^0-9]/g, '')) || 0
        return tuitionA - tuitionB
      case 'popularity':
        return b.isPopular ? 1 : -1
      default:
        if (a.isPopular !== b.isPopular) return b.isPopular ? 1 : -1
        return 0
    }
  })
})

// 动画卡片方法
const animateCards = () => {
  nextTick(() => {
    const cards = document.querySelectorAll('.program-card');
    if (cards.length === 0) return;

    console.log('Animating cards:', cards.length); // 添加调试日志

    gsap.fromTo(cards,
      {
        opacity: 0,
        y: 30,
        scale: 0.95
      },
      {
        opacity: 1,
        y: 0,
        scale: 1,
        duration: 0.5,
        stagger: 0.05,
        ease: 'back.out(1.2)',
        clearProps: 'all'
      }
    );
  });
}

// 监听筛选条件变化，重置展开状态
watch([selectedCollege, selectedDegree, sortBy], () => {
  expandedProgram.value = null
})

// 页面加载时执行
onMounted(() => {
  // 如果学校不存在，可以重定向到学校列表页
  if (!allSchools[schoolId.value]) {
    console.warn(`学校ID ${schoolId.value} 不存在`);
    // 实际应用中可以选择重定向
    // router.push('/country');
  }

  // 添加页面滚动动画
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('animate-in');
      }
    });
  }, { threshold: 0.1 });

  // 观察需要动画的元素
  document.querySelectorAll('.overview-content, .filter-container, .gallery-item').forEach(el => {
    observer.observe(el);
  });

  // 初始化卡片动画
  setTimeout(() => {
    animateCards();
  }, 100);

  // 添加鼠标移动效果
  const onMouseMove = (e) => {
    const cards = document.querySelectorAll('.program-card');
    cards.forEach(card => {
      const rect = card.getBoundingClientRect();
      const x = e.clientX - rect.left;
      const y = e.clientY - rect.top;

      if (x > 0 && x < rect.width && y > 0 && y < rect.height) {
        card.style.setProperty('--mouse-x', `${x}px`);
        card.style.setProperty('--mouse-y', `${y}px`);
      }
    });
  };
  document.addEventListener('mousemove', onMouseMove);

  onUnmounted(() => {
    observer.disconnect()
    document.removeEventListener('mousemove', onMouseMove)
  })
})

const getColleges = computed(() => {
  // 确保返回API获取的学院数据，格式化为下拉选项
  const apiColleges = colleges.value.map(college => ({
    label: college.name,
    value: college.id.toString()
  }))

  // 添加"全部"选项在首位
  return [
    { label: '全部学院', value: 'all' },
    ...apiColleges
  ]
})
</script>

<style scoped>
/* 容器样式 */
.school-container {
  --primary-color: #0071e3;
  --secondary-color: #86b7fe;
  --dark-color: #1d1d1f;
  --light-color: #F9F8F4;
  --gray-color: #86868b;
  --success-color: #4cd964;
  --warning-color: #ff9500;
  --danger-color: #ff3b30;
  --border-radius: 12px;
  --transition-speed: 0.3s;

  min-height: 100vh;
  padding-top: 44px;
  /* 导航栏高度 */
  background-color: var(--light-color);
  color: var(--dark-color);
}

/* 学校头部 */
.school-header {
  position: relative;
  background-color: var(--light-color);
  color: var(--dark-color);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.school-header-content {
  position: relative;
  max-width: 1200px;
  margin: 0 auto;
  padding: 1.25rem 1rem;
}

.school-header-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  width: 100%;
}

.school-header-left {
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 0;
  flex: 1 1 auto;
}

.school-header-right {
  display: flex;
  align-items: center;
  flex: 0 0 auto;
}

.school-logo-card {
  width: 84px;
  height: 84px;
  background: white;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.10);
  border: 1px solid rgba(0, 0, 0, 0.06);
  flex: 0 0 auto;
}

.school-logo {
  width: 80%;
  height: 80%;
  object-fit: contain;
}

.school-logo-fallback {
  font-size: 28px;
  font-weight: 700;
  color: var(--dark-color);
}

.school-header-info {
  margin-bottom: 0;
  animation: fade-up 1s ease-out;
  min-width: 0;
}

@keyframes fade-up {
  from {
    opacity: 0;
    transform: translateY(30px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.school-header .school-name {
  font-size: 1.6rem;
  font-weight: 700;
  margin-bottom: 0.35rem;
  margin-left: 0;
  color: var(--dark-color);
  letter-spacing: -0.02em;
  line-height: 1.1;
}

.school-header .school-location {
  font-size: 0.95rem;
  margin-bottom: 0.6rem;
  color: var(--gray-color);
  margin-left: 0;
}

.school-header .school-ranking {
  display: inline-block;
  background-color: rgba(255, 255, 255, 0.85);
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 1rem;
  backdrop-filter: blur(10px);
}

.school-header .ranking-card {
  padding: 8px 14px;
  min-width: 110px;
}

.school-header .ranking-card.qs-ranking {
  border: 1px solid rgba(0, 0, 0, 0.06);
}

.school-header .ranking-card.qs-ranking .ranking-value {
  font-size: 15px;
}

.school-header .school-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-left: 0;
}

.school-header .school-tag {
  background-color: rgba(0, 0, 0, 0.06);
  color: var(--dark-color);
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.875rem;
}





/* 专业部分 */
.majors-section {
  padding: 1rem 1rem;
  background-color: var(--light-color);
}

.majors-container {
  max-width: 1200px;
  margin: 0 auto;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  width: 100%;
  margin-bottom: 1rem;
}

.section-title {
  font-size: 2.5rem;
  font-weight: 600;
  margin-bottom: 1rem;
  text-align: center;
  letter-spacing: -0.02em;
}

.back-button {
  position: absolute;
  left: 0;
  display: flex;
  align-items: center;
  gap: 6px;
  background: white;
  border: 1px solid rgba(0, 0, 0, 0.08);
  padding: 0 20px;
  height: 44px;
  border-radius: 22px;
  font-size: 15px;
  color: var(--dark-color);
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
}

.back-button:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 113, 227, 0.15);
}

/* 筛选容器样式 */
.filter-container {
  margin-bottom: 2rem;
  background-color: white;
  border-radius: var(--border-radius);
  padding: 1.5rem;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
}

.filter-row {
  display: flex;
  gap: 2rem;
  align-items: center;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.filter-label {
  font-size: 0.875rem;
  color: var(--gray-color);
  white-space: nowrap;
}

/* Element Plus Select 样式覆盖 */
:deep(.el-select) {
  min-width: 140px;
}

:deep(.el-input__wrapper) {
  background-color: var(--light-color);
  box-shadow: none !important;
}

:deep(.el-input__wrapper:hover) {
  background-color: #e8e8e8;
}

/* 无结果样式 */
.no-results {
  text-align: center;
  padding: 4rem 1rem;
  background-color: white;
  border-radius: var(--border-radius);
  margin-bottom: 2rem;
  animation: fade-in 0.5s ease-out;
}

.no-results-icon {
  width: 64px;
  height: 64px;
  background-color: var(--light-color);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1.5rem;
}

.no-results-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: var(--dark-color);
}

.no-results-text {
  color: var(--gray-color);
  margin-bottom: 1.5rem;
}

.reset-filters {
  background: none;
  border: 1px solid var(--primary-color);
  color: var(--primary-color);
  padding: 0.5rem 1.5rem;
  border-radius: 20px;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all var(--transition-speed) ease;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.reset-filters:hover {
  background-color: var(--primary-color);
  color: white;
  transform: translateY(-1px);
}

/* 专业容器 */
.programs-container {
  position: relative;
}

/* 专业卡片网格 */
.programs-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

/* 专业卡片 - 简洁设计 */
.major-card {
  padding: 20px;
  background: white;
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.04);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.02);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.major-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent 0%, rgba(0, 0, 0, 0.1) 50%, transparent 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.major-card:hover::before {
  opacity: 1;
}

.major-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  border-color: rgba(0, 0, 0, 0.08);
}

.major-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.major-name {
  font-size: 18px;
  font-weight: 600;
  color: #000;
  margin: 0;
  line-height: 1.3;
  letter-spacing: -0.2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
  margin-right: 12px;
}

.major-degree {
  font-size: 13px;
  color: #666;
  background: #f8f9fa;
  padding: 4px 8px;
  border-radius: 4px;
  font-weight: 500;
  flex-shrink: 0;
}

.major-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.major-duration {
  font-size: 14px;
  color: #666;
}

.major-tuition {
  font-size: 14px;
  color: #409eff;
  font-weight: 600;
}

.major-tags {
  display: flex;
  gap: 6px;
}

.major-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.major-tag.popular {
  background-color: #ff4757;
  color: white;
}

.major-tag.scholarship {
  background-color: #2ed573;
  color: white;
}

/* 专业详情弹窗 */
.program-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(10px);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 3rem;
  overflow-y: auto;
}

.program-modal {
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  width: 100%;
  max-width: 800px;
  max-height: calc(100vh - 6rem);
  margin: auto;
  display: flex;
  flex-direction: column;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25),
    0 0 0 1px rgba(0, 0, 0, 0.05);
  position: relative;
  animation: modal-appear 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

.modal-close-button {
  position: absolute;
  top: 1.25rem;
  right: 1.25rem;
  width: 30px;
  height: 30px;
  border-radius: 15px;
  background-color: rgba(0, 0, 0, 0.05);
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  z-index: 10;
}

.modal-close-button:hover {
  background-color: rgba(0, 0, 0, 0.1);
  transform: scale(1.1);
}

.modal-close-button .el-icon {
  font-size: 16px;
  color: rgba(0, 0, 0, 0.5);
}

.program-modal-content {
  padding: 2.5rem;
  overflow-y: scroll;
  /* 改为 scroll 而不是 auto */
  height: 100%;
  /* 添加明确的高度 */
  max-height: calc(100vh - 6rem);
  flex: 1;
}

.program-modal-header {
  margin-bottom: 2.5rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
}

.program-modal-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 0.75rem;
  color: var(--dark-color);
  letter-spacing: -0.02em;
  line-height: 1.2;
}

.program-modal-subtitle {
  font-size: 1.25rem;
  color: var(--gray-color);
  font-weight: 500;
}

.program-modal-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 2rem;
  margin-bottom: 2.5rem;
  background-color: rgba(0, 0, 0, 0.02);
  padding: 2rem;
  border-radius: 16px;
}

.modal-info-item {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.modal-info-label {
  font-size: 0.875rem;
  color: var(--gray-color);
  font-weight: 500;
  letter-spacing: 0.02em;
}

.modal-info-value {
  font-size: 1.5rem;
  font-weight: 600;
  letter-spacing: -0.01em;
}

.modal-section {
  background-color: rgba(0, 0, 0, 0.02);
  border-radius: 16px;
  padding: 2rem;
  margin-bottom: 2rem;
}

.modal-section-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  color: var(--dark-color);
  letter-spacing: -0.01em;
}

.modal-courses {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1rem;
  padding-left: 1.5rem;
  font-size: 0.9375rem;
  color: var(--gray-color);
  line-height: 1.6;
}

.program-modal-footer {
  padding-top: 2rem;
  margin-top: 2rem;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: flex-end;
}

.modal-apply-button {
  background-color: var(--primary-color);
  color: white;
  border: none;
  padding: 0.75rem 2rem;
  border-radius: 25px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  letter-spacing: 0.01em;
}

.modal-apply-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 113, 227, 0.2);
}

@keyframes modal-appear {
  from {
    opacity: 0;
    transform: scale(0.98) translateY(10px);
  }

  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

/* 修改滚动条样式 */
.program-modal-content::-webkit-scrollbar {
  width: 10px;
  /* 增加宽度使其更明显 */
  display: block;
}

.program-modal-content::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 4px;
  margin: 5px 0;
  /* 添加上下边距 */
}

.program-modal-content::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.3);
  /* 加深颜色使其更明显 */
  border-radius: 4px;
  border: 2px solid rgba(255, 255, 255, 0.95);
  /* 添加边框 */
}

.program-modal-content::-webkit-scrollbar-thumb:hover {
  background-color: rgba(0, 0, 0, 0.5);
}

/* Firefox 滚动条样式 */
.program-modal-content {
  scrollbar-width: thin;
  scrollbar-color: rgba(0, 0, 0, 0.3) rgba(0, 0, 0, 0.05);
}



/* 专业列表动画 */
.program-list-enter-active,
.program-list-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.program-modal-enter-active,
.program-modal-leave-active {
  transition: opacity 0.3s ease;
}

.program-modal-enter-from,
.program-modal-leave-to {
  opacity: 0;
}

/* 响应式调整 */
@media (max-width: 992px) {
  .overview-content {
    grid-template-columns: 1fr;
  }

  .overview-features {
    grid-template-columns: 1fr;
  }

  .program-modal-info {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 768px) {
  .school-header {
    height: auto;
    min-height: 280px;
  }

  .school-header .school-name {
    font-size: 2rem;
  }

  .school-rankings {
    gap: 6px;
    margin: 8px 0;
  }

  .ranking-card {
    min-width: 80px;
    padding: 4px 8px;
  }

  .ranking-title {
    font-size: 10px;
  }

  .ranking-value {
    font-size: 12px;
  }

  .school-header-stats {
    flex-direction: column;
    gap: 0.75rem;
  }

  .filter-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }

  .college-tabs,
  .degree-tabs {
    width: 100%;
    justify-content: flex-start;
  }

  .programs-grid {
    grid-template-columns: 1fr;
  }

  .section-title {
    font-size: 2rem;
  }

  .campus-gallery {
    grid-template-columns: 1fr;
  }

  .program-modal-content {
    padding: 1.5rem;
  }

  .program-modal-title {
    font-size: 1.5rem;
  }

  .program-modal-subtitle {
    font-size: 1rem;
  }

  .modal-courses {
    grid-template-columns: 1fr;
  }
}

@media (max-height: 700px) {
  .program-modal-overlay {
    padding: 2rem;
  }

  .program-modal {
    max-height: calc(100vh - 4rem);
  }
}

@media (max-width: 640px) {
  .program-modal-overlay {
    padding: 1.5rem;
  }

  .program-modal {
    max-height: calc(100vh - 3rem);
  }
}

@keyframes fade-in {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

/* 排名卡片样式 */
.school-rankings {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 12px 0;
}

.ranking-card {
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 8px;
  padding: 8px 16px;
  min-width: 120px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  text-align: center;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.ranking-card.qs-ranking {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.9), rgba(37, 99, 235, 0.9));
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.ranking-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.ranking-title {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
  font-weight: 500;
}

.ranking-card.qs-ranking .ranking-title {
  color: rgba(255, 255, 255, 0.9);
}

.ranking-value {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.ranking-card.qs-ranking .ranking-value {
  color: white;
  font-size: 18px;
}

/* 暂无数据样式 */
.ranking-card .ranking-value.no-data {
  color: #999;
  font-size: 12px;
  font-weight: normal;
}

/* 学院卡片样式 */
.faculties-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 2rem;
}

.faculty-card {
  background-color: white;
  border-radius: 8px;
  padding: 0.75rem 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e5e7eb;
  min-width: 120px;
  text-align: center;
}

.faculty-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border-color: var(--primary-color);
}

.faculty-card.active {
  background-color: rgba(0, 113, 227, 0.08);
  border-color: var(--primary-color);
  color: var(--dark-color);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.faculty-card.active .faculty-name {
  color: var(--dark-color);
}

.faculty-name {
  font-size: 0.95rem;
  font-weight: 500;
  margin: 0;
  color: var(--dark-color);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}





/* 学院视图特定样式 */
.faculty-view .section-title {
  margin-bottom: 1rem;
}

.faculty-view .filter-section {
  margin-bottom: 1.5rem;
}

/* 响应式设计 - 学院卡片 */
@media (max-width: 768px) {
  .faculties-grid {
    gap: 0.75rem;
  }

  .faculty-card {
    min-width: 100px;
    padding: 0.5rem 1rem;
  }

  .faculty-name {
    font-size: 0.875rem;
  }
}
</style>
