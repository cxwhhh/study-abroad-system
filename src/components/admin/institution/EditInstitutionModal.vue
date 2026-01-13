<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-container">
      <div class="modal-header">
        <div class="modal-title">
          <Edit class="modal-icon" />
          <h3>编辑院校</h3>
        </div>
        <button class="btn-icon-sm" @click="$emit('close')">
          <X />
        </button>
      </div>

      <div class="modal-body">
        <div class="modal-tabs">
          <button 
            v-for="tab in tabs" 
            :key="tab.key"
            class="modal-tab"
            :class="{ active: activeTab === tab.key }"
            @click.stop="switchTab(tab.key)"
          >
            {{ tab.name }}
          </button>
        </div>

        <!-- 基本信息标签页 -->
        <div v-if="activeTab === 'basic'" class="form-section">
          <h4 class="form-section-title">院校基本信息</h4>
          <div class="form-group">
            <label>院校名称 <span class="required">*</span></label>
            <input type="text" v-model="formData.name" placeholder="请输入院校名称" class="form-input" required />
          </div>
          <div class="form-group">
            <label>院校英文名称</label>
            <input type="text" v-model="formData.english_name" placeholder="请输入院校英文名称" class="form-input" />
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>国家/地区 <span class="required">*</span></label>
              <div class="select-wrapper">
                <select v-model="formData.country" class="form-select" required>
                  <option v-for="(name, code) in COUNTRIES" :key="code" :value="code.toUpperCase()">
                    {{ name }}
                  </option>
                </select>
                <ChevronDown class="select-icon" />
              </div>
            </div>
            <div class="form-group">
              <label>国家代码 <span class="required">*</span></label>
              <input type="text" v-model="formData.country_code" placeholder="例如：us, uk, ca" class="form-input" required />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>院校类型</label>
              <div class="select-wrapper">
                <select v-model="formData.type" class="form-select">
                  <option v-for="(name, code) in SCHOOL_TYPES" :key="code" :value="code">
                    {{ name }}
                  </option>
                </select>
                <ChevronDown class="select-icon" />
              </div>
            </div>
            <div class="form-group">
              <label>公立/私立</label>
              <div class="select-wrapper">
                <select v-model="formData.ownership" class="form-select">
                  <option v-for="(name, code) in OWNERSHIP_TYPES" :key="code" :value="code">
                    {{ name }}
                  </option>
                </select>
                <ChevronDown class="select-icon" />
              </div>
            </div>
          </div>
          <div class="form-group">
            <label>学校简介</label>
            <textarea v-model="formData.description" placeholder="请输入院校简介" class="form-textarea"></textarea>
          </div>
          <div class="form-group">
            <label>学校历史</label>
            <textarea v-model="formData.history" placeholder="请输入院校历史" class="form-textarea"></textarea>
                </div>
          <div class="form-group">
            <label>学校特色</label>
            <textarea v-model="formData.features" placeholder="请输入院校特色" class="form-textarea"></textarea>
                </div>
          <div class="form-group">
            <label>教育层次</label>
            <div class="checkbox-group">
              <label class="checkbox-label">
                <input type="checkbox" v-model="formData.has_undergraduate" />
                <span>本科教育</span>
              </label>
              <label class="checkbox-label">
                <input type="checkbox" v-model="formData.has_graduate" />
                <span>研究生教育</span>
              </label>
              <label class="checkbox-label">
                <input type="checkbox" v-model="formData.has_phd" />
                <span>博士教育</span>
              </label>
            </div>
          </div>
          <div class="form-group">
            <label>特色专业</label>
            <textarea v-model="formData.featured_programs" placeholder="请输入特色专业" class="form-textarea"></textarea>
          </div>
          <div class="form-group">
            <label>知名专业</label>
            <textarea v-model="formData.notable_programs" placeholder="请输入知名专业" class="form-textarea"></textarea>
          </div>
          <div class="form-group">
            <label>院校标签</label>
            <input type="text" v-model="formData.tags" placeholder="标签之间用逗号分隔" class="form-input" />
          </div>
          <div class="form-group">
            <label>状态</label>
            <div class="select-wrapper">
              <select v-model="formData.status" class="form-select">
                <option value="1">启用</option>
                <option value="0">禁用</option>
              </select>
              <ChevronDown class="select-icon" />
            </div>
          </div>
        </div>

        <!-- 添加学院与专业标签页 -->
        <div v-if="activeTab === 'colleges'" class="form-section">
          <h4 class="form-section-title">学院信息</h4>
          <div v-if="!institutionAdded" class="note-message">
            <p>请先保存院校基本信息，然后才能添加学院和专业。</p>
          </div>
          <div v-else>
          <div class="section-actions">
              <el-button type="primary" class="action-button" @click="showAddFacultyModal">
              <Plus class="btn-icon" />
              添加学院
            </el-button>
          </div>
          
            <div v-if="faculties.length === 0" class="empty-data-message">
              <p>暂无学院数据，请点击上方按钮添加学院。</p>
              </div>
            
            <div v-else class="faculties-list">
              <div v-for="faculty in faculties" :key="faculty.id" class="faculty-item">
                <div class="faculty-header">
                  <div class="faculty-title">
                    <BookOpen class="faculty-icon" />
                    <h5>{{ faculty.name }}</h5>
                  </div>
                  <div class="faculty-actions">
                    <button class="btn-icon-sm btn-add" @click="showAddMajorModal(faculty)">
                      <PlusCircle />
                    </button>
                    <button class="btn-icon-sm btn-delete" @click="deleteFaculty(faculty.id)">
                  <Trash2 />
                </button>
              </div>
            </div>
                <div class="faculty-body">
                  <p v-if="faculty.description" class="faculty-description">{{ faculty.description }}</p>
                  
            <div class="majors-section">
              <div class="majors-header">
                      <h6 class="majors-title">专业列表</h6>
              </div>
                    
                    <div v-if="faculty.majors && faculty.majors.length > 0" class="majors-list">
                      <div v-for="major in faculty.majors" :key="major.id" class="major-item">
                        <div class="major-info">
                          <span class="major-name">{{ major.name }}</span>
                          <span class="major-degree">{{ getDegreeLabel(major.degree_type) }}</span>
                        </div>
                        <button class="btn-icon-sm" @click="deleteMajor(major.id)">
                    <X />
                  </button>
                </div>
              </div>
                    <div v-else class="empty-majors-message">
                      <p>暂无专业数据，请点击添加按钮添加专业。</p>
            </div>
          </div>
        </div>
                </div>
                </div>
              </div>
            </div>

        <!-- 学生信息标签页 -->
        <div v-if="activeTab === 'students'" class="form-section">
          <h4 class="form-section-title">学生信息</h4>
            <div class="form-row">
              <div class="form-group">
              <label>学生总数</label>
              <input type="number" v-model="formData.total_student_count" placeholder="请输入学生总数" class="form-input" />
              </div>
              <div class="form-group">
              <label>中国学生数量</label>
              <input type="number" v-model="formData.chinese_student_count" placeholder="请输入中国学生数量" class="form-input" />
              </div>
            </div>
          <div class="form-row">
                  <div class="form-group">
              <label>国际学生比例(%)</label>
              <input type="number" v-model="formData.international_student_percentage" step="0.01" placeholder="请输入国际学生比例" class="form-input" />
                  </div>
                  <div class="form-group">
              <label>师生比例</label>
              <input type="text" v-model="formData.student_faculty_ratio" placeholder="例如：1:15" class="form-input" />
                  </div>
                  </div>
          <div class="form-row">
                  <div class="form-group">
              <label>录取率(%)</label>
              <input type="number" v-model="formData.acceptance_rate" step="0.01" placeholder="请输入录取率" class="form-input" />
                  </div>
                  <div class="form-group">
              <label>平均录取GPA</label>
              <input type="number" v-model="formData.average_gpa" step="0.01" placeholder="请输入平均录取GPA" class="form-input" />
                  </div>
                </div>
                  <div class="form-group">
            <label>学生服务</label>
            <textarea v-model="formData.student_services" placeholder="请输入学生服务信息" class="form-textarea"></textarea>
                  </div>
                  <div class="form-group">
            <label>住宿信息</label>
            <textarea v-model="formData.housing_info" placeholder="请输入住宿信息" class="form-textarea"></textarea>
                  </div>
                  <div class="form-group">
            <label>著名校友</label>
            <textarea v-model="formData.famous_alumni" placeholder="请输入著名校友信息" class="form-textarea"></textarea>
                  </div>
        </div>

        <!-- 申请要求标签页 -->
        <div v-if="activeTab === 'requirements'" class="form-section">
          <h4 class="form-section-title">申请要求</h4>
                  <div class="form-group">
            <label>申请截止日期</label>
            <input type="text" v-model="formData.application_deadline" placeholder="请输入申请截止日期" class="form-input" />
                  </div>
                  <div class="form-group">
            <label>入学要求</label>
            <textarea v-model="formData.entry_requirement" placeholder="请输入入学要求" class="form-textarea"></textarea>
                  </div>
            <div class="form-row">
              <div class="form-group">
              <label>TOEFL要求</label>
              <input type="text" v-model="formData.toefl_requirement" placeholder="请输入TOEFL要求" class="form-input" />
              </div>
              <div class="form-group">
              <label>IELTS要求</label>
              <input type="text" v-model="formData.ielts_requirement" placeholder="请输入IELTS要求" class="form-input" />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>GRE要求</label>
              <input type="text" v-model="formData.gre_requirement" placeholder="请输入GRE要求" class="form-input" />
              </div>
              <div class="form-group">
                <label>GMAT要求</label>
              <input type="text" v-model="formData.gmat_requirement" placeholder="请输入GMAT要求" class="form-input" />
              </div>
            </div>
          <div class="form-group">
            <label>SAT要求</label>
            <input type="text" v-model="formData.sat_requirement" placeholder="请输入SAT要求" class="form-input" />
          </div>
          <div class="form-group">
            <label>签证信息</label>
            <textarea v-model="formData.visa_info" placeholder="请输入签证信息" class="form-textarea"></textarea>
                  </div>
                  </div>

        <!-- 联系信息标签页 -->
        <div v-if="activeTab === 'contact'" class="form-section">
          <h4 class="form-section-title">联系信息</h4>
          <div class="form-group">
            <label>官方网站</label>
            <input type="url" v-model="formData.website" placeholder="请输入官方网站地址" class="form-input" />
                </div>
          <div class="form-row">
            <div class="form-group">
              <label>联系邮箱</label>
              <input type="email" v-model="formData.contact_email" placeholder="请输入联系邮箱" class="form-input" />
                  </div>
            <div class="form-group">
              <label>联系电话</label>
              <input type="text" v-model="formData.contact_phone" placeholder="请输入联系电话" class="form-input" />
                  </div>
                </div>
              </div>
              
        <!-- 图片信息标签页 -->
        <div v-if="activeTab === 'images'" class="form-section">
          <h4 class="form-section-title">图片信息</h4>
          <div class="form-group">
            <label>Logo图片URL</label>
            <input type="text" v-model="formData.logo_url" placeholder="请输入Logo图片URL" class="form-input" />
                  </div>
          <div class="form-group">
            <label>学校图片URL</label>
            <input type="text" v-model="formData.image_url" placeholder="请输入学校图片URL" class="form-input" />
                  </div>
          <div class="form-group">
            <label>横幅图片URL</label>
            <input type="text" v-model="formData.banner_url" placeholder="请输入横幅图片URL" class="form-input" />
                </div>
                  </div>

        <!-- 位置信息标签页 -->
        <div v-if="activeTab === 'location'" class="form-section">
          <h4 class="form-section-title">位置信息</h4>
          <div class="form-row">
            <div class="form-group">
              <label>所在城市</label>
              <input type="text" v-model="formData.city" placeholder="请输入城市" class="form-input" />
                  </div>
            <div class="form-group">
              <label>所在州/省</label>
              <input type="text" v-model="formData.state" placeholder="请输入州/省" class="form-input" />
            </div>
          </div>
          <div class="form-group">
            <label>详细地址</label>
            <input type="text" v-model="formData.location" placeholder="请输入详细地址" class="form-input" />
          </div>
          <div class="form-group">
            <label>校区信息</label>
            <textarea v-model="formData.campus" placeholder="请输入校区信息" class="form-textarea"></textarea>
                </div>
              </div>

        <!-- 排名信息标签页 -->
        <div v-if="activeTab === 'rankings'" class="form-section">
          <h4 class="form-section-title">排名信息</h4>
          <div class="form-row">
            <div class="form-group">
              <label>世界排名</label>
              <input type="number" v-model="formData.world_ranking" placeholder="请输入世界排名" class="form-input" />
                  </div>
            <div class="form-group">
              <label>本国排名</label>
              <input type="number" v-model="formData.national_ranking" placeholder="请输入本国排名" class="form-input" />
                  </div>
                </div>
          <div class="form-row">
            <div class="form-group">
              <label>QS排名</label>
              <input type="number" v-model="formData.qs_ranking" placeholder="请输入QS排名" class="form-input" />
                  </div>
            <div class="form-group">
              <label>THE泰晤士排名</label>
              <input type="number" v-model="formData.times_ranking" placeholder="请输入泰晤士排名" class="form-input" />
                  </div>
                </div>
          <div class="form-group">
            <label>上海交大排名</label>
            <input type="number" v-model="formData.shanghai_ranking" placeholder="请输入上海交大排名" class="form-input" />
                </div>
              </div>

        <!-- 费用信息标签页 -->
        <div v-if="activeTab === 'costs'" class="form-section">
          <h4 class="form-section-title">费用信息</h4>
          <div class="form-row">
            <div class="form-group">
              <label>学费(人民币/年)</label>
              <input type="number" v-model="formData.tuition_fee" step="0.01" placeholder="请输入学费" class="form-input" />
                  </div>
            <div class="form-group">
              <label>生活费估算(人民币/年)</label>
              <input type="number" v-model="formData.living_cost" step="0.01" placeholder="请输入生活费" class="form-input" />
                  </div>
                </div>
          <div class="form-row">
            <div class="form-group">
              <label>货币单位</label>
              <input type="text" v-model="formData.currency" placeholder="例如：CNY, USD" class="form-input" />
              </div>
            <div class="form-group">
              <label>返点比例(%)</label>
              <input type="number" v-model="formData.rebate_percentage" step="0.01" placeholder="请输入返点比例" class="form-input" />
            </div>
          </div>
          <div class="form-group">
            <label>奖学金信息</label>
            <textarea v-model="formData.scholarship_info" placeholder="请输入奖学金信息" class="form-textarea"></textarea>
          </div>
        </div>

        <!-- 其他信息标签页 -->
        <div v-if="activeTab === 'others'" class="form-section">
          <h4 class="form-section-title">其他信息</h4>

          <div class="form-group">
            <label>QS世界排名</label>
            <input type="number" v-model="formData.qs_ranking" class="form-input" placeholder="请输入排名" />
          </div>

          <div class="form-group">
            <label>地理位置</label>
            <div class="location-inputs">
              <input type="text" v-model="formData.city" class="form-input" placeholder="城市" />
              <input type="text" v-model="formData.state" class="form-input" placeholder="州/省" />
            </div>
          </div>

          <div class="form-group">
            <label>学校特色标签</label>
            <div class="tags-input">
              <div class="tags-container">
                <span v-for="(tag, index) in formData.tags" :key="index" class="tag">
                  {{ tag }}
                  <button class="tag-remove" @click="removeTag(index)">×</button>
                </span>
              </div>
              <input type="text" 
                     v-model="newTagInput"
                     @keyup.enter="addTag"
                     class="form-input" 
                     placeholder="输入标签后按回车添加" />
            </div>
          </div>

          <div class="form-group">
            <label>补充说明</label>
            <textarea v-model="formData.notes" 
                      class="form-textarea" 
                      placeholder="请输入任何需要补充的信息"></textarea>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button class="btn btn-outline" @click="$emit('close')">取消</button>
        <button class="btn btn-primary" @click="handleSubmit">保存</button>
      </div>
    </div>
  </div>

  <!-- 添加学院模态框 -->
  <AddFacultyModal 
    v-if="showFacultyModal" 
    :schoolId="savedSchoolId" 
    @close="closeFacultyModal"
    @refresh="refreshFaculties" 
  />

  <!-- 添加专业模态框 -->
  <AddMajorModal 
    v-if="showMajorModal" 
    :schoolId="savedSchoolId"
    :facultyId="selectedFacultyId"
    @close="closeMajorModal"
    @refresh="refreshMajors" 
  />
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { 
  Building, ChevronDown, Upload, X, Edit, Plus, PlusCircle, 
  Trash2, BookOpen
} from 'lucide-vue-next'
import { ElMessage, ElLoading, ElMessageBox } from 'element-plus'
import schoolService from '@/api/schoolService'
import facultyService from '@/api/facultyService'
import majorService from '@/api/majorService'
import AddFacultyModal from './AddFacultyModal.vue'
import AddMajorModal from './AddMajorModal.vue'
import { COUNTRIES, SCHOOL_TYPES, OWNERSHIP_TYPES } from './constants'
import institution from '.'

// 定义属性
const props = defineProps({
  institution: {
    type: Object,
    required: true
  }
})

// 定义事件
const emit = defineEmits(['close', 'save', 'refresh'])

// 定义标签页
const tabs = [
  { key: 'basic', name: '基本信息' },
  { key: 'location', name: '位置信息' },
  { key: 'rankings', name: '排名信息' },
  { key: 'costs', name: '费用信息' },
  { key: 'students', name: '学生信息' },
  { key: 'requirements', name: '申请要求' },
  { key: 'contact', name: '联系信息' },
  { key: 'images', name: '图片信息' },
  { key: 'colleges', name: '学院与专业' }
]

// 当前激活的标签页
const activeTab = ref('basic')

// 安全切换标签页
const switchTab = (tabKey) => {
  try {
    activeTab.value = tabKey
    // 记录到控制台，帮助调试
    console.log('切换到标签页:', tabKey)
    
    // 如果切换到学院标签页，尝试加载学院数据
    if (tabKey === 'colleges' && institutionAdded.value) {
      loadFaculties()
    }
  } catch (error) {
    console.error('切换标签页时出错:', error)
    ElMessage.error('切换标签页时出错，请重试')
  }
}

// 已保存的院校ID
const savedSchoolId = ref(null)
const institutionAdded = ref(false)

// 学院相关
const faculties = ref([])
const showFacultyModal = ref(false)

// 专业相关
const showMajorModal = ref(false)
const selectedFacultyId = ref(null)

// 表单提交中状态
const submitting = ref(false)

// 表单数据
const formData = ref({
  // 基本信息
  name: '',                     // 院校名称 *
  english_name: '',             // 院校英文名称
  country: '',                // 国家/地区 *
  country_code: '',           // 国家代码 *
  type: '综合大学',             // 院校类型
  ownership: 'public',          // 公立/私立
  description: '',              // 学校简介
  history: '',                  // 学校历史
  features: '',                 // 学校特色
  has_undergraduate: true,      // 本科教育
  has_graduate: true,           // 研究生教育
  has_phd: true,                // 博士教育
  featured_programs: '',        // 特色专业
  notable_programs: '',         // 知名专业
  tags: '',                     // 院校标签
  status: 1,                    // 状态

  // 位置信息
  city: '',                     // 所在城市
  state: '',                    // 所在州/省
  location: '',                 // 详细地址
  campus: '',                   // 校区信息

  // 排名信息
  world_ranking: null,          // 世界排名
  national_ranking: null,       // 本国排名
  qs_ranking: null,             // QS排名
  times_ranking: null,          // 泰晤士排名
  shanghai_ranking: null,       // 上海交大排名

  // 费用信息
  tuition_fee: null,            // 学费
  living_cost: null,            // 生活费估算
  currency: 'CNY',              // 货币单位
  rebate_percentage: null,      // 返点比例
  scholarship_info: '',         // 奖学金信息

  // 学生信息
  total_student_count: null,    // 学生总数
  chinese_student_count: null,  // 中国学生数量
  international_student_percentage: null, // 国际学生比例
  student_faculty_ratio: '',    // 师生比例
  acceptance_rate: null,        // 录取率
  average_gpa: null,            // 平均录取GPA
  student_services: '',         // 学生服务
  housing_info: '',             // 住宿信息
  famous_alumni: '',            // 著名校友

  // 申请要求
  application_deadline: '',     // 申请截止日期
  entry_requirement: '',        // 入学要求
  toefl_requirement: '',        // TOEFL要求
  ielts_requirement: '',        // IELTS要求
  gre_requirement: '',          // GRE要求
  gmat_requirement: '',         // GMAT要求
  sat_requirement: '',          // SAT要求
  visa_info: '',                // 签证信息

  // 联系信息
  website: '',                  // 官方网站
  contact_email: '',            // 联系邮箱
  contact_phone: '',            // 联系电话

  // 图片信息
  logo_url: '',                 // Logo图片URL
  image_url: '',                // 学校图片URL
  banner_url: '',               // 横幅图片URL

  // 系统字段
  is_deleted: 0,                // 是否删除(0-未删除，1-已删除)
})

// 国家代码映射
const countryCodeMap = {
  'US': 'us',
  'UK': 'gb',
  'CA': 'ca',
  'AU': 'au',
  'NZ': 'nz',
  'SG': 'sg',
  'JP': 'jp',
  'KR': 'kr',
  'HK': 'hk',
  'CN': 'cn'
}

// 监听国家变化，自动更新国家代码
watch(() => formData.value.country, (newCountry) => {
  if (newCountry) {
    formData.value.country_code = countryCodeMap[newCountry] || newCountry.toLowerCase().substring(0, 2) || 'us';
    console.log(`国家变更为: ${newCountry}, 自动设置国家代码为: ${formData.value.country_code}`);
  } else {
    // 即使country为空，也要确保country_code有值
    formData.value.country_code = 'us';
    console.log('国家为空，默认设置country_code为:', formData.value.country_code);
  }
})

// 初始化表单数据
const initFormData = (institutionData) => {  
  if (!institutionData) return
  
  console.log('原始院校数据:', institutionData)
  
  // 创建一个副本以避免修改原始数据
  const processedData = JSON.parse(JSON.stringify(institutionData));
  
  // 处理logo_url字段可能存在的换行符问题
  if (processedData.logo_url) {
    processedData.logo_url = processedData.logo_url.trim();
  } else if (processedData.logoUrl) {
    processedData.logo_url = processedData.logoUrl.trim();
    delete processedData.logoUrl;
  }
  
  if (processedData.logo) {
    processedData.logo = processedData.logo.trim();
  }
  
  // 处理国家信息 - 确保country和country_code都存在且正确
  if (processedData.country) {
    // 如果提供了country，确保它的格式正确(大写国家代码，如US、UK)
    processedData.country = processedData.country.toUpperCase();
  } else if (processedData.countryCode) {
    // 如果提供了countryCode，使用它设置country
    processedData.country = processedData.countryCode.toUpperCase();
  }
  
  // 确保country_code字段有值
  if (!processedData.country_code || processedData.country_code.trim() === '') {
    if (processedData.countryCode) {
      processedData.country_code = processedData.countryCode.toLowerCase();
      delete processedData.countryCode;
    } else {
      processedData.country_code = countryCodeMap[processedData.country] || 
                                 processedData.country?.toLowerCase().substring(0, 2) || 
                                 'us';
    }
    console.log('初始化数据时设置country_code:', processedData.country_code);
  }
  
  // 统一处理字段名 - 将驼峰命名转换为下划线命名
  // 英文名称
  if (processedData.englishName) {
    processedData.english_name = processedData.englishName;
    delete processedData.englishName;
  }
  
  // 图片URL
  if (processedData.logo) {
    processedData.logo_url = processedData.logo;
  }
  
  if (processedData.image) {
    processedData.image_url = processedData.image;
  }
  
  if (processedData.banner) {
    processedData.banner_url = processedData.banner;
  }
  
  // 处理标签
  if (typeof processedData.tags === 'string') {
    processedData.tags = processedData.tags.split(',').filter(tag => tag.trim() !== '');
  }
  
  // 处理rankings对象中的数据（如果存在）
  if (processedData.rankings) {
    if (processedData.rankings.world !== undefined) 
      processedData.world_ranking = processedData.rankings.world;
    
    if (processedData.rankings.national !== undefined) 
      processedData.national_ranking = processedData.rankings.national;
    
    if (processedData.rankings.qs !== undefined) 
      processedData.qs_ranking = processedData.rankings.qs;
    
    if (processedData.rankings.times !== undefined) 
      processedData.times_ranking = processedData.rankings.times;
    
    if (processedData.rankings.shanghai !== undefined) 
      processedData.shanghai_ranking = processedData.rankings.shanghai;
    
    // 删除rankings对象，我们已经提取了所有信息
    delete processedData.rankings;
  }
  //处理requirments数据
  if (processedData.requirements) {
    if (processedData.requirements.toefl !== undefined) {
      processedData.toefl_requirement = processedData.requirements.toefl;
    }
    if(processedData.requirements.ielts !== undefined) {
      processedData.ielts_requirement = processedData.requirements.ielts;
    }
    if(processedData.requirements.gre !== undefined) {
      processedData.gre_requirement = processedData.requirements.gre;
    }
    if(processedData.requirements.gmat !== undefined) {
      processedData.gmat_requirement = processedData.requirements.gmat;
    }
    if(processedData.requirements.sat !== undefined) {
      processedData.sat_requirement = processedData.requirements.sat;
    }
    delete processedData.requirements
  }
  //处理students数据
  if(processedData.students){
    if(processedData.students.total !== undefined){
      processedData.total_student_count = processedData.students.total;
    }
    if(processedData.students.chinese !== undefined){
      processedData.chinese_student_count = processedData.students.chinese;
    }
    if(processedData.students.international !== undefined){
      processedData.international_student_percentage = processedData.students.international;
    }
    if(processedData.students.ratio !== undefined){
      processedData.student_faculty_ratio = processedData.students.ratio;
    }
    if(processedData.students.acceptance !== undefined){
      processedData.acceptance_rate = processedData.students.acceptance;
    }
    delete processedData.students
  }
  //处理status
  if(institutionData.status !== undefined){
    processedData.status = institutionData.status;
  }

  //处理admissions数据
  if(processedData.admissions){
    if(processedData.admissions.acceptance !== undefined){
      processedData.acceptance_rate = processedData.admissions.acceptance;
    }
    if(processedData.admissions.avgGpa !== undefined){
      processedData.average_gpa = processedData.admissions.avgGpa;
    }
    if(processedData.admissions.deadline !== undefined){
      processedData.application_deadline = processedData.admissions.deadline;
    }
    delete processedData.admissions
  }
  
  //处理入学要求entryRequirement
  if (processedData.entryRequirement !== undefined) {
    processedData.entry_requirement = processedData.entryRequirement;
    delete processedData.entryRequirement;
  }
  
  // 处理位置信息
  if (processedData.location && typeof processedData.location === 'object') {
    if (processedData.location.city) processedData.city = processedData.location.city;
    if (processedData.location.state) processedData.state = processedData.location.state;
    if (processedData.location.location) processedData.location = processedData.location.location;
    if(processedData.location.campus)processedData.campus = processedData.location.campus;
  }

  //单独处理ownership
  if(institutionData.ownership !== undefined){
    processedData.ownership = institutionData.ownership;
  }

  //单独处理website
  if(institutionData.website !== undefined){
    processedData.website = institutionData.website;
  }

  //单独处理history
  if(institutionData.history !== undefined){
    processedData.history = institutionData.history;
  }
  
  //单独处理features
  if(institutionData.features !== undefined){
    processedData.features = institutionData.features;
  }

  // 修改处理学费数据的代码，确保类型一致性
  if(processedData.tuition){
    if(processedData.tuition.fee !== undefined){
      processedData.tuition_fee = processedData.tuition.fee;
    }
    if(processedData.tuition.rebate !== undefined){
      processedData.rebate_percentage = processedData.tuition.rebate;
    }
    if(processedData.tuition.currency !== undefined){
      processedData.currency = processedData.tuition.currency;
    }
    delete processedData.tuition
  }
  //单独处理livingcost信息
  if (processedData.livingCost !== undefined) {
    processedData.living_cost = processedData.livingCost;
    delete processedData.livingCost;
  }
  // 处理联系信息
  if(processedData.contact){
    if(processedData.contact.email !== undefined){
      processedData.contact_email = processedData.contact.email;
    }
    if(processedData.contact.phone !== undefined){
      processedData.contact_phone = processedData.contact.phone;
    }
    delete processedData.contact
  } 
  //处理服务信息
  if(processedData.services){
    if(processedData.services.student !== undefined){
      processedData.student_services = processedData.services.student;
    }
    if(processedData.services.housing !== undefined){
      processedData.housing_info = processedData.services.housing;
    }
    if(processedData.services.scholarship !== undefined){
      processedData.scholarship_info = processedData.services.scholarship;
    }
    if(processedData.services.visa !== undefined){
      processedData.visa_info = processedData.services.visa;
    }
    delete processedData.services
  }
  
  // 统一处理布尔值字段 - 转换为下划线格式
  if (processedData.hasUndergraduate !== undefined) {
    processedData.has_undergraduate = processedData.hasUndergraduate === 1 || processedData.hasUndergraduate === true;
    delete processedData.hasUndergraduate;
  }
  
  if (processedData.hasGraduate !== undefined) {
    processedData.has_graduate = processedData.hasGraduate === 1 || processedData.hasGraduate === true;
    delete processedData.hasGraduate;
  }
  
  if (processedData.hasPhd !== undefined) {
    processedData.has_phd = processedData.hasPhd === 1 || processedData.hasPhd === true;
    delete processedData.hasPhd;
  }

  //处理noble信息
  if(processedData.notable){
    if(processedData.notable.alumni !== undefined){
      processedData.famous_alumni = processedData.notable.alumni;
    }
    if(processedData.notable.programs !== undefined){
      processedData.notable_programs = processedData.notable.programs;
    }
    delete processedData.notable
  }
  //处理featured_programs信息
  if(processedData.featuredPrograms !== undefined){
    processedData.featured_programs = processedData.featuredPrograms;
    delete processedData.featuredPrograms
  }
  
  // 输出处理后的排名数据进行验证
  console.log('处理后排名数据:', {
    world_ranking: processedData.world_ranking,
    national_ranking: processedData.national_ranking,
    qs_ranking: processedData.qs_ranking,
    times_ranking: processedData.times_ranking,
    shanghai_ranking: processedData.shanghai_ranking,
    has_undergraduate: processedData.has_undergraduate,
    has_graduate: processedData.has_graduate,
    has_phd: processedData.has_phd,
    is_deleted: processedData.is_deleted,
    total_student_count: processedData.total_student_count,
    chinese_student_count: processedData.chinese_student_count,
    international_student_percentage: processedData.international_student_percentage,
    student_faculty_ratio: processedData.student_faculty_ratio,
    acceptance_rate: processedData.acceptance_rate,
    average_gpa: processedData.average_gpa,
    tuition_fee: processedData.tuition_fee,
    rebate_percentage: processedData.rebate_percentage,
    living_cost: processedData.living_cost,
    // 添加详细的字段类型信息，帮助调试
    tuition_fee_type: typeof processedData.tuition_fee,
    living_cost_type: typeof processedData.living_cost,
    rebate_percentage_type: typeof processedData.rebate_percentage,
    total_student_count_type: typeof processedData.total_student_count,
    contact_email: processedData.contact_email,
    contact_phone: processedData.contact_phone,
    student_services: processedData.student_services,
    housing_info: processedData.housing_info,
    scholarship_info: processedData.scholarship_info,
    visa_info: processedData.visa_info,
    is_deleted: processedData.is_deleted,
    featured_programs: processedData.featured_programs,
    notable_programs: processedData.notable_programs,
    created_time: processedData.created_time,
    updated_time: processedData.updated_time,
    entry_requirement: processedData.entry_requirement,
    toefl_requirement: processedData.toefl_requirement,
    ielts_requirement: processedData.ielts_requirement,
    gre_requirement: processedData.gre_requirement,
    gmat_requirement: processedData.gmat_requirement,
    sat_requirement: processedData.sat_requirement,
    application_deadline: processedData.application_deadline
  });
  
  // 将数据赋值到表单
  formData.value = processedData
  
  console.log('处理后的表单数据:', formData.value)
  
  // 设置已保存院校ID
  savedSchoolId.value = processedData.id
  institutionAdded.value = true
  
  // 如果当前标签页是学院标签页，加载学院数据
  if (activeTab.value === 'colleges') {
    loadFaculties()
  }
}

// 组件挂载时初始化数据
onMounted(() => {
  if (props.institution) {
    initFormData(props.institution)
  }
})

// 处理标志上传
const handleLogoUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    // 这里可以添加文件类型和大小的验证
    formData.value.logo = file
  }
}

// 处理表单提交
const handleSubmit = async () => {
  // 避免重复提交
  if (submitting.value) return
  
  // 验证必填字段
  if (!formData.value.name || !formData.value.country) {
    ElMessage.warning('请填写必要的院校信息(院校名称、国家/地区)')
    return
  }

  try {
    // 在提交前强制检查和设置country_code
    if (!formData.value.country_code || formData.value.country_code.trim() === '') {
      formData.value.country_code = countryCodeMap[formData.value.country] || 
                                  formData.value.country.toLowerCase().substring(0, 2) || 
                                  'us';
      console.log('提交前检查并设置country_code:', formData.value.country_code);
    }
    
    submitting.value = true
    
    // 显示加载提示
    const loading = ElLoading.service({
      lock: true,
      text: '正在保存院校信息...',
      background: 'rgba(0, 0, 0, 0.7)'
    })
    
    // 首先打印原始数据结构，帮助调试
    console.log('原始数据结构:', JSON.stringify(formData.value, null, 2))
    
    // 将数据转换为适合后端API的格式，使用深拷贝避免修改原数据
    const submitData = JSON.parse(JSON.stringify(formData.value))
    
    // 确保country_code字段有值 - 这是最重要的处理，放在最前面优先处理
    if (!submitData.country_code || submitData.country_code.trim() === '') {
      // 根据country设置默认值
      submitData.country_code = countryCodeMap[submitData.country] || submitData.country.toLowerCase().substring(0, 2)
      console.log(`自动设置country_code: ${submitData.country_code}，基于国家: ${submitData.country}`)
    }

    // 再次确认country_code不为空，强制设置一个有效值，确保不会为null
    submitData.country_code = submitData.country_code || 'us';
    submitData.country = submitData.country || 'US';
    
    // 1. 处理可能存在的嵌套对象
    if (typeof submitData.location === 'object') {
      submitData.city = submitData.location.city || submitData.city;
      submitData.state = submitData.location.state || submitData.state;
      submitData.location = typeof submitData.location.location === 'string' 
        ? submitData.location.location 
        : (submitData.location || '');
    }
    
    // 2. 处理英文名称 - 确保正确映射
    if (submitData.english_name !== undefined) {
      submitData.englishName = submitData.english_name;
      console.log('映射english_name到englishName:', submitData.englishName);
    }
    
    // 3. 处理排名信息 - 映射到驼峰命名格式并正确处理times_ranking字段
    // 先使用下划线格式的排名字段（表单中使用的格式）
    submitData.worldRanking = submitData.world_ranking || null;
    submitData.nationalRanking = submitData.national_ranking || null;
    submitData.qsRanking = submitData.qs_ranking || null;
    
    // 处理times_ranking
    if (submitData.times_ranking !== undefined) {
      submitData.timesRanking = submitData.times_ranking;
      console.log('映射times_ranking到timesRanking:', submitData.timesRanking);
    }
    
    submitData.shanghaiRanking = submitData.shanghai_ranking || null;
    
    console.log('排名字段映射后:', {
      worldRanking: submitData.worldRanking,
      nationalRanking: submitData.nationalRanking,
      qsRanking: submitData.qsRanking,
      timesRanking: submitData.timesRanking,
      shanghaiRanking: submitData.shanghaiRanking
    });
    
    // 4. 如果有rankings对象，使用其中的数据
    if (submitData.rankings) {
      // 尝试从rankings对象中获取最新数据
      if (submitData.rankings.world !== undefined) submitData.worldRanking = submitData.rankings.world;
      if (submitData.rankings.national !== undefined) submitData.nationalRanking = submitData.rankings.national;
      if (submitData.rankings.qs !== undefined) submitData.qsRanking = submitData.rankings.qs;
      if (submitData.rankings.times !== undefined) submitData.timesRanking = submitData.rankings.times;
      if (submitData.rankings.shanghai !== undefined) submitData.shanghaiRanking = submitData.rankings.shanghai;
      
      console.log('从rankings对象中更新数据后:', {
        worldRanking: submitData.worldRanking,
        nationalRanking: submitData.nationalRanking,
        qsRanking: submitData.qsRanking,
        timesRanking: submitData.timesRanking,
        shanghaiRanking: submitData.shanghaiRanking
      });
      
      // 删除嵌套对象，避免序列化问题
      delete submitData.rankings;
    }
    
    // 5. 处理tags字段 - 确保它是字符串类型
    if (Array.isArray(submitData.tags)) {
      submitData.tags = submitData.tags.join(',');
      console.log('转换tags数组为字符串:', submitData.tags);
    } else if (typeof submitData.tags === 'object' && submitData.tags !== null) {
      // 如果是对象但不是数组，尝试转换为字符串
      submitData.tags = String(submitData.tags);
      console.log('转换tags对象为字符串:', submitData.tags);
    }
    
    // 6. 检查其他可能需要字符串化的字段
    const stringFields = ['description', 'website', 'logoUrl', 'imageUrl', 'location'];
    stringFields.forEach(field => {
      if (submitData[field] && typeof submitData[field] === 'object') {
        console.log(`字段${field}是对象类型，转换为字符串`);
        submitData[field] = JSON.stringify(submitData[field]);
      }
    });
    
    // 7. 将字符串数字转换为数字类型 - 使用下划线格式的字段名
    const numberFields = [
      'status', 'is_deleted', 
      // 排名字段
      'world_ranking', 'national_ranking', 'qs_ranking', 'times_ranking', 'shanghai_ranking',
      // 学生相关字段
      'total_student_count', 'chinese_student_count', 
      // 费用相关字段
      'tuition_fee', 'living_cost', 'rebate_percentage',
      // 申请相关字段
      'international_student_percentage', 'acceptance_rate', 'average_gpa',
      // 其他字段
      'toefl_requirement', 'ielts_requirement', 'gre_requirement', 'gmat_requirement', 'sat_requirement', 'application_deadline',
      'has_undergraduate', 'has_graduate', 'has_phd',
      'featured_programs', 'notable_programs', 'created_time', 'updated_time',
      'entry_requirement', 'scholarship_info', 'visa_info', 'student_services', 'housing_info', 'famous_alumni',
      'is_deleted', 'featured_programs', 'notable_programs', 'created_time', 'updated_time'
    ]

    // 强制转换排名字段为数字类型，确保0也被正确处理而不是转为null
    const rankingFields = [
      'world_ranking', 'national_ranking', 'qs_ranking', 'times_ranking', 'shanghai_ranking'
    ];
    rankingFields.forEach(field => {
      // 如果是空字符串或null/undefined，设置为null而不是0
      if (submitData[field] === '' || submitData[field] === null || submitData[field] === undefined) {
        submitData[field] = null;
      } 
      // 否则确保转换为数字
      else if (submitData[field] !== null && submitData[field] !== undefined) {
        const numVal = Number(submitData[field]);
        submitData[field] = isNaN(numVal) ? null : numVal;
        console.log(`将排名字段${field}转换为数字:`, submitData[field]);
      }
    });

    numberFields.forEach(field => {
      if (submitData[field] !== null && submitData[field] !== undefined && submitData[field] !== '') {
        submitData[field] = Number(submitData[field])
        console.log(`将${field}转换为数字:`, submitData[field]);
      }
    })
    
    // 8. 将布尔字段转换为0/1
    const booleanFields = ['has_undergraduate', 'has_graduate', 'has_phd', 'is_deleted']
    booleanFields.forEach(field => {
      if (typeof submitData[field] === 'boolean') {
        submitData[field] = submitData[field] ? 1 : 0
        console.log(`将${field}转换为数字:`, submitData[field]);
      }
    })
    
    // 9. 确保ID为数字类型
    if (submitData.id && typeof submitData.id === 'string') {
      submitData.id = parseInt(submitData.id, 10);
      console.log('将ID转换为数字:', submitData.id);
    }
    
    // 10. 检查缺失的字段
    const requiredFields = ['name', 'country', 'country_code'];
    requiredFields.forEach(field => {
      if (!submitData[field]) {
        console.warn(`缺少必填字段: ${field}`);
      }
    });
    
    // 11. 保留关键字段，避免删除导致数据丢失
    // 输出处理后的完整提交数据到控制台，便于排查问题
    console.log('处理后的提交数据结构:', JSON.stringify(submitData, null, 2));
    console.log('重要字段检查:', {
      id: submitData.id,
      name: submitData.name,
      englishName: submitData.englishName,
      english_name: submitData.english_name,
      country: submitData.country,
      countryCode: submitData.countryCode,
      country_code: submitData.country_code,
      worldRanking: submitData.worldRanking,
      world_ranking: submitData.world_ranking,
      nationalRanking: submitData.nationalRanking,
      national_ranking: submitData.national_ranking,
      qsRanking: submitData.qsRanking,
      qs_ranking: submitData.qs_ranking,
      timesRanking: submitData.timesRanking,
      times_ranking: submitData.times_ranking,
      shanghaiRanking: submitData.shanghaiRanking,
      shanghai_ranking: submitData.shanghai_ranking
    });
    
    // 调用API更新数据库中的院校信息
    const response = await schoolService.updateSchool(submitData.id, submitData)
    
    loading.close()
    submitting.value = false
    
    console.log('提交响应:', response)
    
    if (response && (response.data || response.success || response.status === 200)) {
      ElMessage.success('院校信息更新成功')
      
      // 如果当前在学院标签页，刷新学院数据
      if (activeTab.value === 'colleges') {
        loadFaculties()
      }
      
      // 触发刷新事件
      emit('refresh')
    } else {
      ElMessage.error(response?.message || '院校信息更新失败')
    }
  } catch (error) {
    console.error('更新院校错误:', error);
    
    // 增强错误日志，帮助排查问题
    if (error.response) {
      console.error('服务器响应:', {
        status: error.response.status,
        data: error.response.data,
        headers: error.response.headers
      });
      
      // 特别检查400错误
      if (error.response.status === 400) {
        console.error('400错误详情:', error.response.data);
        // 尝试解析错误信息
        let errorMessage = '请求格式错误';
        if (typeof error.response.data === 'string' && error.response.data.includes('Cannot deserialize')) {
          errorMessage = '数据类型错误: ' + error.response.data;
        } else if (error.response.data.message) {
          errorMessage = error.response.data.message;
        }
        ElMessage.error(`提交失败: ${errorMessage}`);
        submitting.value = false;
        return;
      }
    }
    
    submitting.value = false;
    ElMessage.error(`院校信息更新失败: ${error.message || '服务器错误'}`);
  }
}

// 加载学院数据
const loadFaculties = async () => {
  if (!savedSchoolId.value) return
  
  try {
    const response = await facultyService.getFacultiesBySchoolId(savedSchoolId.value)
    if (response && response.data) {
      faculties.value = Array.isArray(response.data) ? response.data : []
      
      // 加载每个学院的专业
      for (const faculty of faculties.value) {
        const majorsResponse = await facultyService.getFacultyMajors(faculty.id)
        faculty.majors = majorsResponse || []
      }
    }
  } catch (error) {
    console.error('加载学院数据失败:', error)
    ElMessage.error('加载学院数据失败')
  }
}

// 显示添加学院模态框
const showAddFacultyModal = () => {
  if (!savedSchoolId.value) {
    ElMessage.warning('请先保存院校基本信息')
    return
  }
  showFacultyModal.value = true
}

// 关闭添加学院模态框
const closeFacultyModal = () => {
  showFacultyModal.value = false
}

// 刷新学院列表
const refreshFaculties = (newFaculty) => {
  loadFaculties()
  closeFacultyModal()
}

// 删除学院
const deleteFaculty = async (facultyId) => {
  try {
    const confirmed = await ElMessageBox.confirm(
      '确定要删除这个学院吗？这将同时删除该学院下的所有专业。',
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    if (confirmed) {
      const response = await facultyService.deleteFaculty(facultyId)
      if (response && (response.data?.success || response.success)) {
        ElMessage.success('学院删除成功')
        loadFaculties() // 重新加载学院列表
      } else {
        ElMessage.error(response?.message || '学院删除失败')
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除学院失败:', error)
      ElMessage.error('删除学院失败')
    }
  }
}

// 显示添加专业模态框
const showAddMajorModal = (faculty) => {
  if (!savedSchoolId.value) {
    ElMessage.warning('请先保存院校基本信息')
    return
  }
  selectedFacultyId.value = faculty.id
  showMajorModal.value = true
}

// 关闭添加专业模态框
const closeMajorModal = () => {
  showMajorModal.value = false
  selectedFacultyId.value = null
}

// 刷新专业列表
const refreshMajors = () => {
  loadFaculties() // 重新加载所有学院及其专业
  closeMajorModal()
}

// 删除专业
const deleteMajor = async (majorId) => {
  try {
    const confirmed = await ElMessageBox.confirm(
      '确定要删除这个专业吗？',
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    if (confirmed) {
      const response = await majorService.deleteMajor(majorId)
      if (response && (response.data?.success || response.success)) {
        ElMessage.success('专业删除成功')
        loadFaculties() // 重新加载学院列表及专业
      } else {
        ElMessage.error(response?.message || '专业删除失败')
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除专业失败:', error)
      ElMessage.error('删除专业失败')
    }
  }
}

// 获取学位类型标签
const getDegreeLabel = (degreeType) => {
  const degreeMap = {
    'bachelor': '本科',
    'master': '硕士',
    'phd': '博士',
    'all': '本硕博'
  }
  return degreeMap[degreeType] || degreeType
}

// 监听标签页变化
watch(activeTab, (newVal) => {
  if (newVal === 'colleges' && institutionAdded.value) {
    loadFaculties()
  }
})
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
  width: 800px;
  max-width: 90%;
  max-height: 90vh;
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
  max-height: calc(90vh - 130px);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #f0f0f0;
}

/* 表单样式 */
.form-section {
  margin-bottom: 32px;
}

.form-section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #111;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.required {
  color: #f5222d;
  margin-left: 4px;
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid #d9d9d9;
  font-size: 14px;
  transition: all 0.3s;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
  outline: none;
}

.form-textarea {
  min-height: 120px;
  resize: vertical;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

/* 文件上传样式 */
.file-upload {
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  padding: 24px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.file-upload:hover {
  border-color: #1890ff;
}

.file-input {
  display: none;
}

.file-label {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  cursor: pointer;
}

.file-icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 24px;
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.file-icon {
  width: 24px;
  height: 24px;
  color: #999;
}

.file-info {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.file-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.file-subtitle {
  font-size: 12px;
  color: #999;
}

/* 标签页样式 */
.modal-tabs {
  display: flex;
  gap: 4px;
  margin-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 16px;
}

.modal-tab {
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  background-color: transparent;
  border: none;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.modal-tab:hover {
  background-color: #f5f7fa;
}

.modal-tab.active {
  background-color: #e6f7ff;
  color: #1890ff;
  font-weight: 500;
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

/* 学院与专业样式 */
.faculty-item {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s;
}

.faculty-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border-color: #e6e6e6;
}

.faculty-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.faculty-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.faculty-icon {
  width: 20px;
  height: 20px;
  color: #1890ff;
}

.faculty-title h5 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.faculty-actions {
  display: flex;
  gap: 8px;
}

.faculty-description {
  color: #666;
  font-size: 14px;
  margin: 0 0 16px 0;
  line-height: 1.5;
}

.btn-add {
  color: #52c41a;
}

.btn-add:hover {
  background-color: rgba(82, 196, 26, 0.1);
}

.btn-delete {
  color: #ff4d4f;
}

.btn-delete:hover {
  background-color: rgba(255, 77, 79, 0.1);
}

.majors-section {
  background-color: white;
  border-radius: 8px;
  padding: 16px;
  margin-top: 16px;
}

.majors-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.majors-title {
  margin: 0;
  font-size: 14px;
  font-weight: 500;
  color: #666;
}

.majors-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.major-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background-color: #f5f7fa;
  border-radius: 6px;
  transition: all 0.3s;
}

.major-item:hover {
  background-color: #e6f7ff;
}

.major-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.major-name {
  font-weight: 500;
  color: #333;
}

.major-degree {
  font-size: 12px;
  color: #666;
  background-color: #f0f0f0;
  border-radius: 4px;
  padding: 2px 6px;
}

.empty-data-message,
.empty-majors-message,
.note-message {
  padding: 24px;
  text-align: center;
  color: #999;
  background-color: #fafafa;
  border-radius: 8px;
  margin-top: 16px;
}

.note-message {
  background-color: #fffbe6;
  border: 1px solid #ffe58f;
  color: #d48806;
}

.section-actions {
  margin-bottom: 20px;
}

.action-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.btn-icon {
  width: 16px;
  height: 16px;
}

/* 复选框样式 */
.checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-top: 8px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  width: 16px;
  height: 16px;
  accent-color: #1890ff;
}

.select-wrapper {
  position: relative;
}

.select-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none;
  width: 16px;
  height: 16px;
  color: #999;
}
</style> 