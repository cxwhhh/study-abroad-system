<template>
  <div class="modal-overlay">
    <div class="modal-container">
      <div class="modal-header">
        <div class="modal-title">
          <Building class="modal-icon" />
          <h3>院校详情</h3>
        </div>
        <button class="btn-icon-sm" @click="$emit('close')">
          <X />
        </button>
      </div>

      <div class="modal-body">
        <!-- 院校基本信息 -->
        <div class="institution-detail-header">
          <div class="institution-logo">
            <img v-if="normalizedInstitution && normalizedInstitution.logoUrl" :src="normalizedInstitution.logoUrl"
              :alt="normalizedInstitution.name" @error="handleLogoError" />
            <div v-else class="avatar-text"
              :style="{ backgroundColor: generateAvatarColor(normalizedInstitution?.name) }">
              {{ normalizedInstitution?.name?.charAt(0) || '院' }}
            </div>
          </div>
          <div class="institution-info">
            <h2>{{ normalizedInstitution?.name }}</h2>
            <div class="institution-tags" v-if="institutionTags.length > 0">
              <span v-for="(tag, index) in institutionTags" :key="index" class="institution-tag">{{ tag }}</span>
            </div>
            <div class="institution-meta">
              <span class="meta-item">
                <Globe class="meta-icon" />
                {{ getCountryName(normalizedInstitution?.country) }}
              </span>
              <span class="meta-item">
                <Building class="meta-icon" />
                {{ getTypeName(normalizedInstitution?.type) }}
              </span>
              <span class="meta-item" v-if="combinedFacultyData.length">
                <BookOpen class="meta-icon" />
                {{ combinedFacultyData.length }} 个学院
              </span>
              <span class="meta-item" v-if="normalizedInstitution?.totalStudentCount">
                <GraduationCap class="meta-icon" />
                {{ normalizedInstitution.totalStudentCount }} 名学生
              </span>
            </div>
          </div>
        </div>

        <!-- 标签页导航 -->
        <div class="detail-tabs">
          <button v-for="tab in tabs" :key="tab.key" class="detail-tab" :class="{ active: activeTab === tab.key }"
            @click="activeTab = tab.key">
            {{ tab.name }}
          </button>
        </div>

        <!-- 基本信息标签页 -->
        <div v-if="activeTab === 'basic'" class="detail-section">
          <div class="info-grid">
            <div class="info-item">
              <h4>学校类型</h4>
              <p>{{ getTypeName(normalizedInstitution?.type) }}</p>
            </div>
            <div class="info-item">
              <h4>所在国家</h4>
              <p>{{ getCountryName(normalizedInstitution?.country) }} ({{ normalizedInstitution?.countryCode || '暂无代码'
                }})</p>
            </div>
            <div class="info-item">
              <h4>所在城市</h4>
              <p>{{ showData(normalizedInstitution?.city) }}{{ normalizedInstitution?.state ? ', ' +
                normalizedInstitution.state : '' }}</p>
            </div>
            <div class="info-item">
              <h4>详细地址</h4>
              <p>{{ showData(normalizedInstitution?.location) }}</p>
            </div>
            <div class="info-item">
              <h4>校区信息</h4>
              <p>{{ showData(normalizedInstitution?.campus) }}</p>
            </div>
            <div class="info-item">
              <h4>办学性质</h4>
              <p>{{ getOwnershipName(normalizedInstitution?.ownership) }}</p>
            </div>
            <div class="info-item">
              <h4>官方网站</h4>
              <p>
                <a v-if="normalizedInstitution?.website" :href="normalizedInstitution.website" target="_blank"
                  class="website-link">
                  {{ normalizedInstitution.website }}
                </a>
                <span v-else>暂无数据</span>
              </p>
            </div>
            <div class="info-item">
              <h4>学费信息</h4>
              <p>{{ showData(normalizedInstitution?.tuitionFee, normalizedInstitution?.currency ? ' ' +
                normalizedInstitution.currency + '/年' : '') }}</p>
            </div>
            <div class="info-item">
              <h4>返点比例</h4>
              <p>{{ showData(normalizedInstitution?.rebatePercentage, '%') }}</p>
            </div>
            <div class="info-item">
              <h4>生活费估计</h4>
              <p>{{ showData(normalizedInstitution?.livingCost, normalizedInstitution?.currency ? ' ' +
                normalizedInstitution.currency + '/年' : '') }}</p>
            </div>
          </div>

          <div class="section-divider"></div>

          <div class="info-section">
            <h4>排名信息</h4>
            <div class="ranking-grid">
              <div class="ranking-item">
                <label>QS世界排名</label>
                <span>{{ showData(normalizedInstitution?.qsRanking) }}</span>
              </div>
              <div class="ranking-item">
                <label>THE泰晤士排名</label>
                <span>{{ showData(normalizedInstitution?.timesRanking) }}</span>
              </div>
              <div class="ranking-item">
                <label>上海软科排名</label>
                <span>{{ showData(normalizedInstitution?.shanghaiRanking) }}</span>
              </div>
              <div class="ranking-item">
                <label>本国排名</label>
                <span>{{ showData(normalizedInstitution?.nationalRanking) }}</span>
              </div>
              <div class="ranking-item">
                <label>世界排名</label>
                <span>{{ showData(normalizedInstitution?.worldRanking) }}</span>
              </div>
            </div>
          </div>

          <div class="section-divider"></div>

          <div class="info-section">
            <h4>学生情况</h4>
            <div class="student-grid">
              <div class="student-item">
                <label>总学生数量</label>
                <span>{{ showData(normalizedInstitution?.totalStudentCount) }}</span>
              </div>
              <div class="student-item">
                <label>中国学生数量</label>
                <span>{{ showData(normalizedInstitution?.chineseStudentCount) }}</span>
              </div>
              <div class="student-item">
                <label>国际学生比例</label>
                <span>{{ showData(normalizedInstitution?.internationalStudentPercentage, '%') }}</span>
              </div>
              <div class="student-item">
                <label>师生比例</label>
                <span>{{ showData(normalizedInstitution?.studentFacultyRatio) }}</span>
              </div>
              <div class="student-item">
                <label>录取率</label>
                <span>{{ showData(normalizedInstitution?.acceptanceRate, '%') }}</span>
              </div>
            </div>
          </div>

          <div class="section-divider"></div>

          <div class="info-section">
            <h4>申请要求</h4>
            <div class="requirement-grid">
              <div class="requirement-item">
                <label>平均GPA</label>
                <span>{{ showData(normalizedInstitution?.averageGpa) }}</span>
              </div>
              <div class="requirement-item">
                <label>托福要求</label>
                <span>{{ showData(normalizedInstitution?.toeflRequirement) }}</span>
              </div>
              <div class="requirement-item">
                <label>雅思要求</label>
                <span>{{ showData(normalizedInstitution?.ieltsRequirement) }}</span>
              </div>
              <div class="requirement-item">
                <label>GRE要求</label>
                <span>{{ showData(normalizedInstitution?.greRequirement) }}</span>
              </div>
              <div class="requirement-item">
                <label>GMAT要求</label>
                <span>{{ showData(normalizedInstitution?.gmatRequirement) }}</span>
              </div>
              <div class="requirement-item">
                <label>SAT要求</label>
                <span>{{ showData(normalizedInstitution?.satRequirement) }}</span>
              </div>
              <div class="requirement-item">
                <label>申请截止日期</label>
                <span>{{ showData(normalizedInstitution?.applicationDeadline) }}</span>
              </div>
            </div>
          </div>

          <div class="section-divider"></div>

          <div class="info-section" v-if="normalizedInstitution?.description">
            <h4>学校简介</h4>
            <p class="description-text">{{ normalizedInstitution.description }}</p>
          </div>

          <div class="info-section" v-if="normalizedInstitution?.notablePrograms">
            <h4>特色专业</h4>
            <p class="description-text">{{ normalizedInstitution.notablePrograms }}</p>
          </div>

          <div class="info-section" v-if="normalizedInstitution?.history">
            <h4>历史沿革</h4>
            <p class="description-text">{{ normalizedInstitution.history }}</p>
          </div>
        </div>

        <!-- 学院与专业标签页 -->
        <div v-if="activeTab === 'colleges'" class="detail-section">
          <!-- 加载状态 -->
          <div v-if="loading" class="loading-state">
            <div class="loading-spinner"></div>
            <p>正在加载学院与专业数据...</p>
          </div>

          <!-- 错误状态 -->
          <div v-else-if="error" class="error-state">
            <p class="error-message">{{ error }}</p>
            <button class="btn btn-primary btn-sm" @click="initializeData">重试</button>
          </div>

          <!-- 空数据状态 -->
          <div v-else-if="!combinedFacultyData.length" class="empty-state">
            <p>暂无学院与专业数据</p>
          </div>

          <!-- 数据展示 -->
          <div v-else>
            <!-- 如果只有专业没有学院 -->
            <div v-if="hasMajorsOnly" class="all-programs">
              <h4>所有专业</h4>
              <div class="program-list">
                <div v-for="program in combinedFacultyData[0].programs" :key="program.id || program._id"
                  class="program-card">
                  <h5 class="program-name">{{ program.name }}</h5>
                  <div class="program-meta">
                    <span class="degree-badge">{{ getDegreeTypeName(program.degreeType || program.degree_type) }}</span>
                    <span v-if="program.duration" class="duration-badge">{{ program.duration }}</span>
                  </div>
                  <p v-if="program.description" class="program-description">{{ program.description }}</p>

                  <div v-if="program.requirements" class="program-requirements">
                    <h6>申请要求</h6>
                    <ul>
                      <li v-if="program.requirements.gpa">平均GPA: {{ program.requirements.gpa }}</li>
                      <li v-if="program.requirements.toefl">托福: {{ program.requirements.toefl }}</li>
                      <li v-if="program.requirements.ielts">雅思: {{ program.requirements.ielts }}</li>
                      <li v-if="program.requirements.gre">GRE: {{ program.requirements.gre }}</li>
                      <li v-if="program.requirements.gmat">GMAT: {{ program.requirements.gmat }}</li>
                    </ul>
                  </div>

                  <div v-if="program.tuitionFee || program.tuition" class="program-tuition">
                    <h6>学费</h6>
                    <p>{{ program.tuitionFee || program.tuition }} {{ program.currency ||
                      normalizedInstitution?.currency || '' }}/年</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- 如果有学院结构 -->
            <div v-else>
              <div v-for="faculty in combinedFacultyData" :key="faculty.id || faculty._id" class="college-item">
                <div class="college-header">
                  <h4 class="college-name">{{ faculty.name }}</h4>
                  <p v-if="faculty.description" class="college-description">{{ faculty.description }}</p>
                </div>

                <div v-if="faculty.programs && faculty.programs.length > 0" class="program-list">
                  <div v-for="program in faculty.programs" :key="program.id || program._id" class="program-card">
                    <h5 class="program-name">{{ program.name }}</h5>
                    <div class="program-meta">
                      <span class="degree-badge">{{ getDegreeTypeName(program.degreeType || program.degree_type)
                        }}</span>
                      <span v-if="program.duration" class="duration-badge">{{ program.duration }}</span>
                    </div>
                    <p v-if="program.description" class="program-description">{{ program.description }}</p>

                    <div v-if="program.requirements" class="program-requirements">
                      <h6>申请要求</h6>
                      <ul>
                        <li v-if="program.requirements.gpa">平均GPA: {{ program.requirements.gpa }}</li>
                        <li v-if="program.requirements.toefl">托福: {{ program.requirements.toefl }}</li>
                        <li v-if="program.requirements.ielts">雅思: {{ program.requirements.ielts }}</li>
                        <li v-if="program.requirements.gre">GRE: {{ program.requirements.gre }}</li>
                        <li v-if="program.requirements.gmat">GMAT: {{ program.requirements.gmat }}</li>
                      </ul>
                    </div>

                    <div v-if="program.tuitionFee || program.tuition" class="program-tuition">
                      <h6>学费</h6>
                      <p>{{ program.tuitionFee || program.tuition }} {{ program.currency ||
                        normalizedInstitution?.currency || '' }}/年</p>
                    </div>
                  </div>
                </div>

                <p v-else class="no-programs-message">该学院暂无专业数据</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 申请要求标签页 -->
        <div v-if="activeTab === 'requirements'" class="detail-section">
          <div class="requirements-container">
            <div class="requirement-group">
              <h4>基本申请要求</h4>
              <div class="requirement-grid">
                <div class="requirement-item">
                  <label>申请类型</label>
                  <div class="requirement-tags">
                    <span v-if="normalizedInstitution.hasUndergraduate" class="tag">本科</span>
                    <span v-if="normalizedInstitution.hasGraduate" class="tag">硕士</span>
                    <span v-if="normalizedInstitution.hasPhd" class="tag">博士</span>
                    <span
                      v-if="!normalizedInstitution.hasUndergraduate && !normalizedInstitution.hasGraduate && !normalizedInstitution.hasPhd"
                      class="tag">暂无信息</span>
                  </div>
                </div>
                <div class="requirement-item">
                  <label>申请截止日期</label>
                  <span>{{ showData(normalizedInstitution?.applicationDeadline) }}</span>
                </div>
                <div class="requirement-item">
                  <label>平均录取率</label>
                  <span>{{ showData(normalizedInstitution?.acceptanceRate, '%') }}</span>
                </div>
                <div class="requirement-item">
                  <label>平均GPA要求</label>
                  <span>{{ showData(normalizedInstitution?.averageGpa) }}</span>
                </div>
              </div>
            </div>

            <div class="section-divider"></div>

            <div class="requirement-group">
              <h4>语言要求</h4>
              <div class="requirement-grid">
                <div class="requirement-item">
                  <label>托福要求</label>
                  <span>{{ showData(normalizedInstitution?.toeflRequirement) }}</span>
                </div>
                <div class="requirement-item">
                  <label>雅思要求</label>
                  <span>{{ showData(normalizedInstitution?.ieltsRequirement) }}</span>
                </div>
              </div>
            </div>

            <div class="section-divider"></div>

            <div class="requirement-group">
              <h4>标准化考试要求</h4>
              <div class="requirement-grid">
                <div class="requirement-item">
                  <label>GRE要求</label>
                  <span>{{ showData(normalizedInstitution?.greRequirement) }}</span>
                </div>
                <div class="requirement-item">
                  <label>GMAT要求</label>
                  <span>{{ showData(normalizedInstitution?.gmatRequirement) }}</span>
                </div>
                <div class="requirement-item">
                  <label>SAT要求</label>
                  <span>{{ showData(normalizedInstitution?.satRequirement) }}</span>
                </div>
              </div>
            </div>

            <div class="section-divider" v-if="normalizedInstitution.entryRequirement"></div>

            <div class="requirement-group" v-if="normalizedInstitution.entryRequirement">
              <h4>入学要求说明</h4>
              <p class="general-requirements">{{ normalizedInstitution.entryRequirement }}</p>
            </div>

            <div class="section-divider"></div>

            <div class="requirement-group" v-if="hasMaterials">
              <h4>申请材料</h4>
              <div class="materials-grid">
                <div v-for="(isRequired, key) in materialsData" :key="key" class="material-item">
                  <div class="material-header">
                    <span class="material-name">{{ getMaterialName(key) }}</span>
                    <span class="material-required" :class="{ required: isRequired }">
                      {{ isRequired ? '必需' : '可选' }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 其他信息标签页 -->
        <div v-if="activeTab === 'others'" class="detail-section">
          <div class="info-grid">
            <div class="info-item">
              <h4>学费信息</h4>
              <p>{{ showData(normalizedInstitution?.tuitionFee, normalizedInstitution?.currency ? ' ' +
                normalizedInstitution.currency + '/年' : '') }}</p>
            </div>
            <div class="info-item">
              <h4>生活费估计</h4>
              <p>{{ showData(normalizedInstitution?.livingCost, normalizedInstitution?.currency ? ' ' +
                normalizedInstitution.currency + '/年' : '') }}</p>
            </div>
            <div class="info-item">
              <h4>返点比例</h4>
              <p>{{ showData(normalizedInstitution?.rebatePercentage, '%') }}</p>
            </div>
          </div>

          <div class="section-divider"></div>

          <div v-if="normalizedInstitution.scholarshipInfo" class="info-section">
            <h4>奖学金信息</h4>
            <div class="service-content">{{ normalizedInstitution.scholarshipInfo }}</div>
          </div>

          <div class="section-divider" v-if="normalizedInstitution.scholarshipInfo"></div>

          <div v-if="normalizedInstitution.studentServices" class="info-section">
            <h4>学生服务</h4>
            <div class="service-content">{{ normalizedInstitution.studentServices }}</div>
          </div>

          <div class="section-divider" v-if="normalizedInstitution.studentServices"></div>

          <div v-if="normalizedInstitution.housingInfo" class="info-section">
            <h4>住宿信息</h4>
            <div class="service-content">{{ normalizedInstitution.housingInfo }}</div>
          </div>

          <div class="section-divider" v-if="normalizedInstitution.housingInfo"></div>

          <div v-if="normalizedInstitution.visaInfo" class="info-section">
            <h4>签证信息</h4>
            <div class="service-content">{{ normalizedInstitution.visaInfo }}</div>
          </div>

          <div class="section-divider" v-if="normalizedInstitution.visaInfo"></div>

          <div v-if="normalizedInstitution.contactEmail || normalizedInstitution.contactPhone" class="info-section">
            <h4>联系方式</h4>
            <div class="contact-info">
              <p v-if="normalizedInstitution.contactEmail"><strong>邮箱:</strong> {{ normalizedInstitution.contactEmail }}
              </p>
              <p v-if="normalizedInstitution.contactPhone"><strong>电话:</strong> {{ normalizedInstitution.contactPhone }}
              </p>
            </div>
          </div>

          <div class="section-divider" v-if="normalizedInstitution.contactEmail || normalizedInstitution.contactPhone">
          </div>

          <div v-if="normalizedInstitution.famousAlumni" class="info-section">
            <h4>知名校友</h4>
            <div class="service-content">{{ normalizedInstitution.famousAlumni }}</div>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button class="btn btn-outline" @click="$emit('close')">关闭</button>
        <button class="btn btn-primary" @click="$emit('edit', normalizedInstitution)">编辑信息</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import {
  Building,
  X,
  Globe,
  BookOpen,
  GraduationCap,
  MapPin,
  Calendar,
  Phone,
  Mail,
  Award,
  Link,
  Users,
  DollarSign,
  BarChart,
  FileText,
  Briefcase
} from 'lucide-vue-next'
import schoolService from '@/api/schoolService'
import facultyService from '@/api/facultyService'
import majorService from '@/api/majorService'

const props = defineProps({
  institution: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['close', 'edit'])

// 标签页
const activeTab = ref('basic')
const tabs = [
  { key: 'basic', name: '基本信息' },
  { key: 'colleges', name: '学院与专业' },
  { key: 'applications', name: '申请情况' },
  { key: 'marketing', name: '营销资料' }
]

// 数据加载状态
const loading = ref(false)
const error = ref(null)

// 学院和专业数据
const facultiesData = ref([])
const combinedFacultyData = ref([])
const hasMajorsOnly = computed(() => {
  return combinedFacultyData.value.length === 1 && !combinedFacultyData.value[0].id
})

// 规范化机构数据
const normalizedInstitution = ref(null)
const institutionTags = computed(() => {
  if (normalizedInstitution.value?.tags && Array.isArray(normalizedInstitution.value.tags)) {
    return normalizedInstitution.value.tags;
  } else if (typeof normalizedInstitution.value?.tags === 'string') {
    return normalizedInstitution.value.tags.split(',').filter(tag => tag.trim() !== '');
  }
  return [];
})

const resolvePublicUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http') || path.startsWith('data:') || path.startsWith('blob:')) return path
  const base = import.meta.env.BASE_URL || '/'
  const normalized = path.startsWith('/') ? path.slice(1) : path
  return `${base}${normalized}`
}

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

onMounted(async () => {
  // 立即规范化传入的数据
  normalizeInstitutionData();
  // 初始化数据加载
  await initializeData();
})

// 规范化从父组件传入的数据格式
const normalizeInstitutionData = () => {
  if (!props.institution) {
    console.error('没有传入院校数据');
    return;
  }

  console.log('传入的院校数据:', props.institution);

  // 处理logo URL
  const logoUrl = normalizeSchoolLogo(props.institution.logo || props.institution.logo_url || '') || '';

  // 先获取直接的院校数据
  normalizedInstitution.value = {
    id: props.institution.id,
    name: props.institution.name,
    englishName: props.institution.englishName || props.institution.english_name || '',
    country: props.institution.country || '',
    countryCode: props.institution.countryCode || props.institution.country_code || props.institution.country || '',
    city: props.institution.location?.city || props.institution.city || '',
    state: props.institution.location?.state || props.institution.state || '',
    location: props.institution.location?.location || props.institution.location || '',
    campus: props.institution.location?.campus || props.institution.campus || '',
    type: props.institution.type || '',
    ownership: props.institution.ownership || '',
    website: props.institution.website || '',
    description: props.institution.description || '',
    history: props.institution.history || '',
    features: props.institution.features || '',

    // 排名信息
    qsRanking: props.institution.rankings?.qs || props.institution.qs_ranking || null,
    timesRanking: props.institution.rankings?.times || props.institution.times_ranking || null,
    shanghaiRanking: props.institution.rankings?.shanghai || props.institution.shanghai_ranking || null,
    worldRanking: props.institution.rankings?.world || props.institution.world_ranking || null,
    nationalRanking: props.institution.rankings?.national || props.institution.national_ranking || null,

    // 费用信息
    tuitionFee: props.institution.tuition?.fee || props.institution.tuition_fee || null,
    currency: props.institution.tuition?.currency || props.institution.currency || 'CNY',
    rebatePercentage: props.institution.tuition?.rebate || props.institution.rebate_percentage || null,
    livingCost: props.institution.livingCost || props.institution.living_cost || null,

    // 学生信息
    totalStudentCount: props.institution.students?.total || props.institution.total_student_count || null,
    chineseStudentCount: props.institution.students?.chinese || props.institution.chinese_student_count || null,
    internationalStudentPercentage: props.institution.students?.international || props.institution.international_student_percentage || null,
    studentFacultyRatio: props.institution.students?.ratio || props.institution.student_faculty_ratio || null,
    acceptanceRate: props.institution.admissions?.acceptance || props.institution.acceptance_rate || null,
    averageGpa: props.institution.admissions?.avgGpa || props.institution.average_gpa || null,

    // 申请要求
    toeflRequirement: props.institution.requirements?.toefl || props.institution.toefl_requirement || '',
    ieltsRequirement: props.institution.requirements?.ielts || props.institution.ielts_requirement || '',
    greRequirement: props.institution.requirements?.gre || props.institution.gre_requirement || '',
    gmatRequirement: props.institution.requirements?.gmat || props.institution.gmat_requirement || '',
    satRequirement: props.institution.requirements?.sat || props.institution.sat_requirement || '',
    applicationDeadline: props.institution.admissions?.deadline || props.institution.application_deadline || '',

    // 服务信息
    studentServices: props.institution.services?.student || props.institution.student_services || '',
    housingInfo: props.institution.services?.housing || props.institution.housing_info || '',
    scholarshipInfo: props.institution.services?.scholarship || props.institution.scholarship_info || '',
    visaInfo: props.institution.services?.visa || props.institution.visa_info || '',

    // 联系信息
    contactEmail: props.institution.contact?.email || props.institution.contact_email || '',
    contactPhone: props.institution.contact?.phone || props.institution.contact_phone || '',

    // 其他信息
    famousAlumni: props.institution.notable?.alumni || props.institution.famous_alumni || '',
    notablePrograms: props.institution.notable?.programs || props.institution.notable_programs || props.institution.featured_programs || '',
    tags: props.institution.tags || [],

    // 媒体资源
    logoUrl: logoUrl,
    imageUrl: props.institution.image || props.institution.image_url || '',
    bannerUrl: props.institution.banner || props.institution.banner_url || ''
  };

  // 确保logo字段正确获取
  if (!normalizedInstitution.value.logoUrl && props.institution.logoUrl) {
    normalizedInstitution.value.logoUrl = normalizeSchoolLogo(props.institution.logoUrl) || props.institution.logoUrl;
  }

  console.log('规范化后的院校数据:', normalizedInstitution.value);
  console.log('规范化后的logo URL:', normalizedInstitution.value.logoUrl);
}

// 初始化数据加载
const initializeData = async () => {
  if (!props.institution || !props.institution.id) {
    error.value = '院校数据无效';
    return;
  }

  loading.value = true;
  error.value = null;

  try {
    // 1. 尝试获取完整的院校数据
    const schoolResponse = await schoolService.getSchoolById(props.institution.id);
    if (schoolResponse && schoolResponse.data) {
      // 更新normalizedInstitution中的缺失数据
      updateNormalizedInstitution(schoolResponse.data);
    }

    // 2. 获取学院数据
    const facultiesResponse = await schoolService.getSchoolFaculties(props.institution.id);
    if (facultiesResponse && Array.isArray(facultiesResponse.data)) {
      facultiesData.value = facultiesResponse.data;
    } else if (facultiesResponse && Array.isArray(facultiesResponse)) {
      facultiesData.value = facultiesResponse;
    } else {
      facultiesData.value = [];
    }

    // 3. 获取各学院的专业数据
    await fetchAllFacultyMajors();

  } catch (err) {
    console.error('加载院校详细数据失败:', err);
    error.value = '加载院校详细数据失败，请稍后重试';
  } finally {
    loading.value = false;
  }
};

// 更新规范化的院校数据
const updateNormalizedInstitution = (schoolData) => {
  if (!schoolData) return;

  console.log('从API获取的学校数据:', schoolData);

  // 处理logo字段
  if (schoolData.logo_url) {
    console.log('从API获取到logo_url:', schoolData.logo_url);
    normalizedInstitution.value.logoUrl = normalizeSchoolLogo(schoolData.logo_url) || schoolData.logo_url;
  }

  // 只更新normalizedInstitution中为空的字段
  Object.keys(normalizedInstitution.value).forEach(key => {
    // 转换字段名（驼峰转下划线）
    const snakeKey = key.replace(/([A-Z])/g, '_$1').toLowerCase();

    if (
      (normalizedInstitution.value[key] === null ||
        normalizedInstitution.value[key] === undefined ||
        normalizedInstitution.value[key] === '') &&
      (schoolData[key] !== undefined || schoolData[snakeKey] !== undefined)
    ) {
      normalizedInstitution.value[key] = schoolData[key] !== undefined ?
        schoolData[key] :
        schoolData[snakeKey];
    }
  });

  // 特别处理排名数据，确保是数字类型
  ['qsRanking', 'timesRanking', 'shanghaiRanking', 'worldRanking', 'nationalRanking'].forEach(rankingKey => {
    if (normalizedInstitution.value[rankingKey]) {
      normalizedInstitution.value[rankingKey] = parseInt(normalizedInstitution.value[rankingKey]) || null;
    }
  });

  console.log('更新后的院校数据:', normalizedInstitution.value);
};

// 获取所有学院的专业
const fetchAllFacultyMajors = async () => {
  try {
    // 首先尝试直接获取学校的所有专业
    const allMajorsResponse = await schoolService.getSchoolMajors(props.institution.id);
    const allMajors = Array.isArray(allMajorsResponse.data) ? allMajorsResponse.data :
      (Array.isArray(allMajorsResponse) ? allMajorsResponse : []);

    // 处理学院数据，为每个学院添加专业
    if (facultiesData.value.length > 0) {
      // 有学院数据，按学院组织专业
      combinedFacultyData.value = await Promise.all(facultiesData.value.map(async (faculty) => {
        let facultyMajors = [];

        try {
          // 尝试获取特定学院的专业
          const majorsResponse = await facultyService.getFacultyMajors(faculty.id);
          facultyMajors = Array.isArray(majorsResponse.data) ? majorsResponse.data :
            (Array.isArray(majorsResponse) ? majorsResponse : []);
        } catch (error) {
          console.warn(`获取学院 ${faculty.id} 的专业失败:`, error);
          // 从全部专业中过滤该学院的专业
          facultyMajors = allMajors.filter(major => major.faculty_id === faculty.id);
        }

        return {
          ...faculty,
          programs: facultyMajors
        };
      }));
    } else if (allMajors.length > 0) {
      // 没有学院数据但有专业，将所有专业归为一类
      combinedFacultyData.value = [{
        name: '所有专业',
        description: '',
        programs: allMajors
      }];
    } else {
      combinedFacultyData.value = [];
    }
  } catch (error) {
    console.error('获取专业数据失败:', error);
    error.value = '获取专业数据失败，请稍后重试';
  }
};

// 辅助函数 - 显示数据，如果为空则显示占位符
const showData = (value, suffix = '') => {
  if (value === null || value === undefined || value === '') {
    return '暂无数据';
  }
  return `${value}${suffix}`;
};

// 辅助函数 - 生成头像背景色
const generateAvatarColor = (name) => {
  if (!name) return '#1890ff';

  let hash = 0;
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash);
  }

  const hue = Math.abs(hash % 360);
  return `hsl(${hue}, 70%, 60%)`;
};

// 辅助函数 - 获取国家名称
const getCountryName = (code) => {
  if (!code) return '未知';

  const countries = {
    'US': '美国',
    'UK': '英国',
    'CA': '加拿大',
    'AU': '澳大利亚',
    'CN': '中国',
    'NZ': '新西兰',
    'SG': '新加坡',
    'JP': '日本',
    'KR': '韩国',
    'HK': '中国香港'
  };
  return countries[code] || code;
};

// 辅助函数 - 获取学校类型名称
const getTypeName = (code) => {
  if (!code) return '未知';

  const types = {
    'university': '大学',
    'college': '学院',
    'highschool': '高中',
    '综合大学': '综合大学',
    '理工大学': '理工大学',
    '文理学院': '文理学院',
    '艺术院校': '艺术院校',
    '医学院校': '医学院校',
    '商学院': '商学院',
    '法学院': '法学院'
  };
  return types[code] || code;
};

// 辅助函数 - 获取办学性质名称
const getOwnershipName = (code) => {
  if (!code) return '未知';

  const ownership = {
    'public': '公立',
    'private': '私立'
  };
  return ownership[code] || code;
};

// 辅助函数 - 获取学位类型名称
const getDegreeTypeName = (code) => {
  if (!code) return '未知';

  const degreeTypes = {
    'bachelor': '学士学位',
    'master': '硕士学位',
    'phd': '博士学位',
    'postdoc': '博士后',
    'bachelor_of_arts': '文学学士',
    'bachelor_of_science': '理学学士',
    'master_of_arts': '文学硕士',
    'master_of_science': '理学硕士',
    'mba': 'MBA',
    'emba': 'EMBA'
  };
  return degreeTypes[code] || code;
};

// 辅助函数 - 处理logo加载失败
const handleLogoError = (e) => {
  console.error('Logo 加载失败:', e);
  // 设置为默认占位图像
  e.target.style.display = 'none';
  // 显示文字占位
  const parent = e.target.parentElement;
  if (!parent.querySelector('.avatar-text')) {
    const placeholder = document.createElement('div');
    placeholder.className = 'avatar-text';
    placeholder.style.backgroundColor = generateAvatarColor(normalizedInstitution.value?.name);
    placeholder.textContent = normalizedInstitution.value?.name?.charAt(0) || '院';
    parent.appendChild(placeholder);
  }
};

// 计算属性 - 是否有申请材料要求
const hasMaterials = computed(() => {
  if (!normalizedInstitution.value) return false;

  // 检查各种可能的材料数据路径
  return Boolean(
    (normalizedInstitution.value.requirements && normalizedInstitution.value.requirements.materials &&
      Object.keys(normalizedInstitution.value.requirements.materials).length > 0) ||
    (normalizedInstitution.value.materials && Object.keys(normalizedInstitution.value.materials).length > 0) ||
    (normalizedInstitution.value.application_materials && Object.keys(normalizedInstitution.value.application_materials).length > 0) ||
    (normalizedInstitution.value.applicationMaterials && Object.keys(normalizedInstitution.value.applicationMaterials).length > 0)
  );
});

// 计算属性 - 获取材料数据
const materialsData = computed(() => {
  if (!normalizedInstitution.value) return {};

  // 按优先级尝试获取材料数据
  if (normalizedInstitution.value.requirements?.materials &&
    Object.keys(normalizedInstitution.value.requirements.materials).length > 0) {
    return normalizedInstitution.value.requirements.materials;
  }

  if (normalizedInstitution.value.materials &&
    Object.keys(normalizedInstitution.value.materials).length > 0) {
    return normalizedInstitution.value.materials;
  }

  if (normalizedInstitution.value.application_materials &&
    Object.keys(normalizedInstitution.value.application_materials).length > 0) {
    return normalizedInstitution.value.application_materials;
  }

  if (normalizedInstitution.value.applicationMaterials &&
    Object.keys(normalizedInstitution.value.applicationMaterials).length > 0) {
    return normalizedInstitution.value.applicationMaterials;
  }

  return {};
});

// 计算属性 - 是否有语言要求
const hasLanguageRequirements = computed(() => {
  if (!normalizedInstitution.value) return false;

  // 检查各种可能的语言要求路径
  return Boolean(
    normalizedInstitution.value.toeflRequirement ||
    normalizedInstitution.value.ieltsRequirement ||
    normalizedInstitution.value.duolingoRequirement ||
    normalizedInstitution.value.greRequirement ||
    normalizedInstitution.value.gmatRequirement ||
    normalizedInstitution.value.satRequirement ||
    normalizedInstitution.value.actRequirement ||
    (normalizedInstitution.value.requirements?.toefl) ||
    (normalizedInstitution.value.requirements?.ielts) ||
    (normalizedInstitution.value.requirements?.duolingo) ||
    (normalizedInstitution.value.requirements?.gre) ||
    (normalizedInstitution.value.requirements?.gmat) ||
    (normalizedInstitution.value.requirements?.sat) ||
    (normalizedInstitution.value.requirements?.act)
  );
});
</script>

<style scoped>
/* 模态框基本样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-container {
  background-color: #fff;
  border-radius: 16px;
  width: 1000px;
  max-width: 95%;
  max-height: 95vh;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.modal-icon {
  width: 24px;
  height: 24px;
  color: #1890ff;
}

.modal-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #111;
  margin: 0;
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
  max-height: calc(95vh - 130px);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #f0f0f0;
}

/* 无数据提示样式 */
.no-data-message {
  padding: 24px;
  text-align: center;
  color: #999;
  font-size: 14px;
  background-color: #fafafa;
  border-radius: 8px;
  margin: 16px 0;
}

/* 院校详情样式 */
.institution-detail-header {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.institution-logo {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid #f0f0f0;
}

.institution-logo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-text {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 500;
  color: white;
  text-transform: uppercase;
  border-radius: 4px;
}

.institution-info {
  flex: 1;
}

.institution-info h2 {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 12px 0;
  color: #111;
}

.institution-tags {
  margin-bottom: 12px;
}

.institution-tag {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  background-color: #f0f7ff;
  color: #1890ff;
  margin-right: 8px;
}

.institution-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
}

.meta-icon {
  width: 16px;
  height: 16px;
  color: #999;
}

/* 标签页样式 */
.detail-tabs {
  display: flex;
  gap: 4px;
  margin-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 16px;
}

.detail-tab {
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  background-color: transparent;
  border: none;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.detail-tab:hover {
  background-color: #f5f7fa;
}

.detail-tab.active {
  background-color: #e6f7ff;
  color: #1890ff;
  font-weight: 500;
}

/* 详情内容样式 */
.detail-section {
  margin-bottom: 24px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item label {
  font-size: 12px;
  color: #999;
}

.info-item span,
.info-item a {
  font-size: 14px;
  color: #333;
}

.link {
  color: #1890ff;
  text-decoration: none;
}

.link:hover {
  text-decoration: underline;
}

.description-section {
  margin-top: 24px;
}

.description-section label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
}

.description-section p {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0;
}

/* 学院和专业样式 */
.college-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.college-item {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 24px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s;
}

.college-item:hover {
  background-color: #f0f0f0;
  border-color: #e6e6e6;
}

.college-header {
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.college-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.college-description {
  font-size: 14px;
  color: #666;
  margin: 0;
  line-height: 1.5;
}

.program-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.program-card {
  background-color: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  height: 100%;
  display: flex;
  flex-direction: column;
}

.program-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #e6e6e6;
}

.program-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.program-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.program-description {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  margin: 0 0 12px 0;
  flex-grow: 1;
}

.program-requirements {
  margin-top: auto;
  border-top: 1px dashed #f0f0f0;
  padding-top: 12px;
}

.program-requirements h6,
.program-tuition h6 {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin: 0 0 8px 0;
}

.program-requirements ul {
  padding-left: 16px;
  margin: 0;
}

.program-requirements li {
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
}

.program-tuition {
  margin-top: 12px;
  border-top: 1px dashed #f0f0f0;
  padding-top: 12px;
}

.program-tuition p {
  font-size: 13px;
  color: #666;
  margin: 0;
}

.no-programs-message {
  padding: 24px;
  text-align: center;
  color: #999;
  font-size: 14px;
  background-color: #fafafa;
  border-radius: 8px;
  margin: 16px 0;
}

/* 申请要求样式 */
.requirements-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.requirement-group {
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.requirement-group:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.requirement-group h4 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #333;
  position: relative;
  padding-left: 16px;
}

.requirement-group h4::before {
  content: "";
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 16px;
  background-color: #1890ff;
  border-radius: 2px;
}

.requirement-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.requirement-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  background-color: #f0f7ff;
  color: #1890ff;
}

.language-requirements {
  margin-top: 16px;
}

.toefl-section,
.ielts-section,
.other-test-section {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  border: 1px solid #f0f0f0;
}

.toefl-section h5,
.ielts-section h5,
.other-test-section h5 {
  margin: 0 0 12px 0;
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.scores-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 12px;
}

.score-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.score-item label {
  font-size: 12px;
  color: #999;
}

.score-item span {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

/* 其他信息样式 */
.tags-section,
.notes-section {
  margin-top: 24px;
}

.tags-section h4,
.notes-section h4 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #333;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.feature-tag {
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 13px;
  background-color: #f0f7ff;
  color: #1890ff;
  display: inline-flex;
  align-items: center;
  transition: all 0.2s;
}

.feature-tag:hover {
  background-color: #e6f7ff;
  transform: translateY(-2px);
  box-shadow: 0 2px 6px rgba(24, 144, 255, 0.1);
}

.service-section,
.notes-section {
  margin-top: 24px;
}

.service-section h4,
.notes-section h4 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #333;
}

.service-section p,
.notes-section p {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0;
  padding: 12px;
  background-color: #f9f9f9;
  border-radius: 8px;
  border-left: 4px solid #1890ff;
}

/* 按钮样式 */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0 16px;
  height: 40px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  min-width: 80px;
}

.btn-primary {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  color: white;
  border: none;
}

.btn-primary:hover {
  opacity: 0.9;
}

.btn-outline {
  background-color: white;
  border: 1px solid #d9d9d9;
  color: #666;
}

.btn-outline:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.btn-icon-sm {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  background-color: transparent;
  border: none;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-icon-sm:hover {
  background-color: #f5f7fa;
  color: #1890ff;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .institution-detail-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .institution-meta {
    justify-content: center;
  }

  .info-grid,
  .scores-grid {
    grid-template-columns: 1fr;
  }

  .scores-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .modal-container {
    width: 95%;
    max-width: none;
  }

  .modal-body {
    padding: 16px;
  }

  .institution-logo {
    width: 60px;
    height: 60px;
  }

  .institution-info h2 {
    font-size: 20px;
  }

  .detail-tabs {
    flex-wrap: wrap;
  }

  .program-list {
    grid-template-columns: 1fr;
  }
}

.education-levels {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.level-badge {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  background-color: #f0f7ff;
  color: #1890ff;
}

/* 申请材料样式 */
.materials-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.material-item {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 12px;
  border: 1px solid #f0f0f0;
  transition: all 0.2s ease;
}

.material-item:hover {
  background-color: #f0f0f0;
  border-color: #e6e6e6;
}

.material-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.material-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.material-required {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  background-color: #f5f7fa;
  color: #999;
}

.material-required.required {
  background-color: #fff2e8;
  color: #fa8c16;
}

.material-desc {
  font-size: 12px;
  color: #666;
  margin: 0;
  line-height: 1.5;
}

/* 服务内容样式 */
.service-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 8px 0 0 0;
  padding: 12px;
  background-color: #f9f9f9;
  border-radius: 8px;
  border-left: 4px solid #1890ff;
}

.general-requirements {
  font-size: 14px;
  color: #666;
  margin-top: 8px;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 6px;
  line-height: 1.5;
}

.section-divider {
  border-top: 1px solid #f0f0f0;
  margin: 24px 0;
}

.info-section {
  margin-bottom: 24px;
}

.info-section h4 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
}

.ranking-grid,
.student-grid,
.requirement-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.ranking-item,
.student-item,
.requirement-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  background-color: #f9f9f9;
  padding: 12px;
  border-radius: 8px;
}

.ranking-item label,
.student-item label,
.requirement-item label {
  font-size: 14px;
  color: #666;
}

.ranking-item span,
.student-item span,
.requirement-item span {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.description-text {
  line-height: 1.6;
  color: #333;
  white-space: pre-line;
}

.website-link {
  color: #1890ff;
  text-decoration: none;
}

.website-link:hover {
  text-decoration: underline;
}

.requirement-section {
  margin-top: 16px;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.requirement-section h6 {
  font-size: 16px;
  margin: 0 0 12px 0;
  color: #333;
}

.requirement-details {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
}

.program-links {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.program-links a {
  color: #1890ff;
  text-decoration: none;
}

.program-links a:hover {
  text-decoration: underline;
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 0;
  text-align: center;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top-color: #1890ff;
  animation: spin 1s ease-in-out infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 错误状态 */
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px;
  background-color: #fff2f0;
  border-radius: 8px;
  border: 1px solid #ffccc7;
  margin: 16px 0;
}

.error-message {
  color: #f5222d;
  margin-bottom: 16px;
}

/* 空数据状态 */
.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
  background-color: #fafafa;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  margin: 16px 0;
}

.empty-state p {
  color: #999;
  font-size: 16px;
}

/* 学位类型和学时徽章 */
.degree-badge {
  background-color: #e6f7ff;
  color: #1890ff;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  margin-right: 4px;
  display: inline-block;
}

.duration-badge {
  background-color: #f6ffed;
  color: #52c41a;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  display: inline-block;
}

.all-programs {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  margin-bottom: 24px;
}

.all-programs h4 {
  margin: 0 0 16px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 12px;
}

.no-programs-message {
  text-align: center;
  padding: 20px;
  color: #999;
  background-color: #fafafa;
  border-radius: 6px;
  font-size: 14px;
}
</style>
