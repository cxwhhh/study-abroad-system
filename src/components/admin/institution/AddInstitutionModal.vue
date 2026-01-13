<template>
  <div class="modal-overlay">
    <div class="modal-container">
      <div class="modal-header">
        <div class="modal-title">
          <Building class="modal-icon" />
          <h3>添加院校</h3>
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
            @click="activeTab = tab.key"
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
                  <option value="US">美国</option>
                  <option value="UK">英国</option>
                  <option value="CA">加拿大</option>
                  <option value="AU">澳大利亚</option>
                  <option value="NZ">新西兰</option>
                  <option value="SG">新加坡</option>
                  <option value="JP">日本</option>
                  <option value="KR">韩国</option>
                  <option value="HK">中国香港</option>
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
                  <option value="综合大学">综合大学</option>
                  <option value="理工大学">理工大学</option>
                  <option value="文理学院">文理学院</option>
                  <option value="艺术院校">艺术院校</option>
                  <option value="医学院校">医学院校</option>
                  <option value="商学院">商学院</option>
                  <option value="法学院">法学院</option>
                </select>
                <ChevronDown class="select-icon" />
              </div>
            </div>
            <div class="form-group">
              <label>公立/私立</label>
              <div class="select-wrapper">
                <select v-model="formData.ownership" class="form-select">
                  <option value="public">公立</option>
                  <option value="private">私立</option>
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
              <label>泰晤士排名</label>
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
  Building, ChevronDown, Upload, X, Plus, PlusCircle, 
  Trash2, BookOpen
} from 'lucide-vue-next'
import { ElMessage, ElLoading } from 'element-plus'
import schoolService from '@/api/schoolService'
import facultyService from '@/api/facultyService'
import majorService from '@/api/majorService'
import AddFacultyModal from './AddFacultyModal.vue'
import AddMajorModal from './AddMajorModal.vue'

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

// 表单数据
const formData = ref({
  // 基本信息
  name: '',                     // 院校名称 *
  english_name: '',             // 院校英文名称
  country: 'US',                // 国家/地区 *
  country_code: 'us',           // 国家代码 *
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
  times_ranking: null,          // 泰晤士排名 (改为times_ranking)
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

// 组件挂载时确保country_code有默认值
onMounted(() => {
  // 确保country_code有初始值
  if (!formData.value.country_code) {
    formData.value.country_code = 'us';
    console.log('组件挂载时设置country_code默认值:', formData.value.country_code);
  }
})

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
    submitting.value = true
    
    // 显示加载提示
    const loading = ElLoading.service({
      lock: true,
      text: '正在保存院校信息...',
      background: 'rgba(0, 0, 0, 0.7)'
    })
    
    // 在转换前强制检查和设置country_code
    if (!formData.value.country_code || formData.value.country_code.trim() === '') {
      formData.value.country_code = countryCodeMap[formData.value.country] || formData.value.country.toLowerCase().substring(0, 2) || 'us';
      console.log('提交前已设置country_code:', formData.value.country_code);
    }
    
    // 将数据转换为适合后端API的格式
    const submitData = JSON.parse(JSON.stringify(formData.value));
    
    // 再次检查特殊字段
    if (!submitData.country_code || submitData.country_code.trim() === '') {
      submitData.country_code = 'us';
      console.log('在JSON转换后检测到country_code为空，已设置为默认值');
    }
    
    // 将字符串数字转换为数字类型
    const numberFields = [
      'status', 'is_deleted', 'world_ranking', 'national_ranking', 
      'qs_ranking', 'times_ranking', 'shanghai_ranking', 'total_student_count', 
      'chinese_student_count', 'tuition_fee', 'living_cost', 'rebate_percentage',
      'international_student_percentage', 'acceptance_rate', 'average_gpa'
    ]
    
    // 特殊处理：确保使用times_ranking字段
    // 不需要再做映射了，直接使用times_ranking
    
    numberFields.forEach(field => {
      if (submitData[field] !== null && submitData[field] !== undefined && submitData[field] !== '') {
        submitData[field] = Number(submitData[field])
      }
    })
    
    // 将布尔字段转换为0/1
    const booleanFields = ['has_undergraduate', 'has_graduate', 'has_phd', 'is_deleted']
    booleanFields.forEach(field => {
      if (typeof submitData[field] === 'boolean') {
        submitData[field] = submitData[field] ? 1 : 0
      }
    })
    
    // 添加创建时间和更新时间
    submitData.created_time = new Date()
    submitData.updated_time = new Date()
    
    // 最后检查确认
    submitData.country_code = submitData.country_code || 'us';
    submitData.country = submitData.country || 'US';
    
    // 输出完整提交数据到控制台，便于排查问题
    console.log('完整提交数据:', submitData);
    console.log('重要字段检查:', {
      name: submitData.name,
      country: submitData.country,
      country_code: submitData.country_code,
      type: submitData.type
    });
    
    // 调用API将数据保存到数据库
    const response = await schoolService.addSchool(submitData)
    
    loading.close()
    submitting.value = false
    
    console.log('提交响应:', response)
    
    if (response && (response.data || response.success || response.status === 201)) {
      // 保存返回的院校ID，用于后续添加学院
      savedSchoolId.value = response.data?.id || response.id
      institutionAdded.value = true
      
      ElMessage.success('院校添加成功')
      
      if (activeTab.value === 'colleges') {
        // 如果当前在学院标签页，加载学院数据
        loadFaculties()
      }
      
      // 不关闭模态框，允许用户继续添加学院和专业
    } else {
      ElMessage.error(response?.message || '院校添加失败')
    }
  } catch (error) {
    console.error('添加院校错误:', error)
    submitting.value = false
    ElMessage.error(`院校添加失败: ${error.message || '服务器错误'}`)
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

// 定义事件
const emit = defineEmits(['close', 'save', 'refresh'])
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
</style> 