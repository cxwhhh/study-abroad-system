<template>
  <div class="institutions-content">
    <div class="page-header">
      <div>
        <h2 class="page-title">院校管理</h2>
        <p class="page-subtitle">管理所有合作院校、学院和专业信息</p>
      </div>
      <div class="page-actions">
        <el-button class="custom-button" type="default" @click="handleImportData">
          <Upload class="button-icon" />
          导入数据
        </el-button>
        <el-button class="custom-button" type="primary" @click="showAddModal">
          <Plus class="button-icon" />
          添加院校
        </el-button>
      </div>
    </div>

    <!-- 过滤栏 -->
    <div class="filter-bar">
      <div class="filter-group">
        <label>国家/地区</label>
        <div class="select-wrapper">
          <select v-model="filters.country">
            <option value="">全部</option>
            <option v-for="(name, code) in COUNTRIES" :key="code" :value="code">{{ name }}</option>
          </select>
          <ChevronDown class="select-icon" />
        </div>
      </div>
      <div class="filter-group">
        <label>院校类型</label>
        <div class="select-wrapper">
          <select v-model="filters.type">
            <option value="">全部</option>
            <option v-for="(name, code) in SCHOOL_TYPES" :key="code" :value="code">{{ name }}</option>
          </select>
          <ChevronDown class="select-icon" />
        </div>
      </div>
      <div class="filter-group">
        <label>排序方式</label>
        <div class="select-wrapper">
          <select v-model="filters.sort">
            <option value="name">名称</option>
            <option value="rank">排名</option>
            <option value="applications">申请数量</option>
          </select>
          <ChevronDown class="select-icon" />
        </div>
      </div>
      <div class="filter-search">
        <label>搜索</label>
        <div class="search-input-wrapper">
          <Search class="search-input-icon" />
          <input 
            type="text" 
            v-model="filters.search" 
            placeholder="搜索院校名称..." 
            class="filter-search-input" 
          />
        </div>
      </div>
      <div class="filter-actions">
        <el-button class="custom-button" type="default" @click="applyFilters">
          <Filter class="button-icon" />
          筛选
        </el-button>
        <el-button class="custom-button" type="default" @click="resetFilters">
          <RefreshCw class="button-icon" />
          重置
        </el-button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="isLoading" class="loading-container">
      <div class="loading-spinner"></div>
      <div>正在加载院校数据，请稍候...</div>
    </div>
    
    <!-- 错误状态 -->
    <div v-else-if="hasError" class="error-container">
      <div class="error-icon">
        <i class="el-icon-warning"></i>
      </div>
      <div class="error-message">{{ errorMessage }}</div>
      <el-button class="retry-button" type="primary" @click="retryLoading">
        <i class="el-icon-refresh"></i>
        重试
      </el-button>
    </div>
    
    <!-- 空状态 -->
    <div v-else-if="filteredInstitutions.length === 0" class="empty-container">
      <div class="empty-message">未找到符合条件的院校</div>
    </div>
    
    <!-- 数据内容 -->
    <div v-else class="institution-list">
      <!-- 院校表格 -->
      <div class="institutions-table">
        <table>
          <thead>
            <tr>
              <th>
                <div class="th-content">
                  <input type="checkbox" class="table-checkbox" v-model="selectAll" @change="toggleSelectAll" />
                  <span>院校名称</span>
                </div>
              </th>
              <th>国家/地区</th>
              <th>类型</th>
              <th>学院数量</th>
              <th>专业数量</th>
              <th>申请数量</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="institution in paginatedInstitutions" :key="institution.id">
              <td>
                <div class="td-content">
                  <input 
                    type="checkbox" 
                    class="table-checkbox" 
                    v-model="institution.selected" 
                    @change="handleSelect"
                  />
                  <div class="institution-name">
                    <div class="institution-logo">
                      <img 
                        v-if="institution.logo" 
                        :src="institution.logo" 
                        :alt="institution.name" 
                      />
                      <div 
                        v-else 
                        class="avatar-text" 
                        :style="{ backgroundColor: generateAvatarColor(institution.name) }"
                      >
                        {{ institution.name?.charAt(0) || '院' }}
                      </div>
                    </div>
                    <span>{{ institution.name }}</span>
                  </div>
                </div>
              </td>
              <td>
                <div class="country-badge">
                  <span>{{ getCountryName(institution.country || institution.countryCode) }}</span>
                </div>
              </td>
              <td>{{ getTypeName(institution.type) }}</td>
              <td>
                <div class="count-badge">{{ institution.collegesCount || 0 }}</div>
              </td>
              <td>
                <div class="count-badge">{{ institution.majorsCount || 0 }}</div>
              </td>
              <td>
                <div class="count-badge">{{ institution.applicationsCount || 0 }}</div>
              </td>
              <td>
                <div class="action-buttons">
                  <button class="btn-icon-sm" @click="showViewModal(institution)" title="查看详情">
                    <Eye />
                  </button>
                  <button class="btn-icon-sm" @click="showEditModal(institution)" title="编辑">
                    <Edit />
                  </button>
                  <button class="btn-icon-sm" @click="showDeleteModal(institution)" title="删除">
                    <Trash2 />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div class="table-footer" v-if="!isLoading && filteredInstitutions.length > 0">
        <div class="table-info">
          显示 {{ paginationInfo.start }} 至 {{ paginationInfo.end }} 条，共 {{ totalInstitutions }} 条
        </div>
        <div class="pagination">
          <button 
            class="pagination-button" 
            :disabled="currentPage === 1"
            @click="changePage(1)"
          >
            <ChevronsLeft />
          </button>
          <button 
            class="pagination-button" 
            :disabled="currentPage === 1"
            @click="changePage(currentPage - 1)"
          >
            <ChevronLeft />
          </button>
          <button 
            v-for="page in displayedPages" 
            :key="page"
            class="pagination-number"
            :class="{ active: currentPage === page }"
            @click="changePage(page)"
          >
            {{ page }}
          </button>
          <span v-if="showEndEllipsis" class="pagination-ellipsis">...</span>
          <button 
            v-if="showEndPage"
            class="pagination-number"
            :class="{ active: currentPage === totalPages }"
            @click="changePage(totalPages)"
          >
            {{ totalPages }}
          </button>
          <button 
            class="pagination-button" 
            :disabled="currentPage === totalPages"
            @click="changePage(currentPage + 1)"
          >
            <ChevronRight />
          </button>
          <button 
            class="pagination-button" 
            :disabled="currentPage === totalPages"
            @click="changePage(totalPages)"
          >
            <ChevronsRight />
          </button>
        </div>
        <div class="page-size">
          <span>每页显示:</span>
          <div class="select-wrapper">
            <select v-model="pageSize" @change="handlePageSizeChange">
              <option :value="10">10</option>
              <option :value="20">20</option>
              <option :value="50">50</option>
              <option :value="100">100</option>
            </select>
            <ChevronDown class="select-icon" />
          </div>
        </div>
      </div>
    </div>

    <!-- 添加院校模态框 -->
    <AddInstitutionModal
      v-if="showAddInstitutionModal"
      @close="closeAddModal"
      @save="handleSave"
    />

    <!-- 查看院校详情模态框 -->
    <ViewInstitutionModal
      v-if="showViewInstitutionModal"
      :institution="selectedInstitution"
      @close="closeViewModal"
      @edit="showEditModal"
    />

    <!-- 编辑院校模态框 -->
    <EditInstitutionModal
      v-if="showEditInstitutionModal"
      :institution="selectedInstitution"
      @close="closeEditModal"
      @save="handleUpdate"
    />

    <!-- 删除确认模态框 -->
    <DeleteConfirmModal
      v-if="showDeleteConfirmModal"
      :institution="selectedInstitution"
      @close="closeDeleteModal"
      @confirm="handleDelete"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElLoading } from 'element-plus'
import {
  Search,
  ChevronDown,
  Eye,
  Edit,
  Trash2,
  Upload,
  ChevronLeft,
  ChevronRight,
  ChevronsLeft,
  ChevronsRight,
  Plus,
  Filter,
  RefreshCw
} from 'lucide-vue-next'

import {
  AddInstitutionModal,
  EditInstitutionModal,
  ViewInstitutionModal,
  DeleteConfirmModal
} from '.'

import schoolService from '@/api/schoolService'
import { COUNTRIES, SCHOOL_TYPES } from './constants'

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

// 状态管理
const institutions = ref([])
const isLoading = ref(false)
const hasError = ref(false)
const errorMessage = ref('')

// 筛选条件
const filters = ref({
  country: '',
  type: '',
  sort: 'name',
  search: ''
})

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const totalInstitutions = computed(() => filteredInstitutions.value.length)
const totalPages = computed(() => Math.ceil(totalInstitutions.value / pageSize.value))

// 模态框显示状态
const showAddInstitutionModal = ref(false)
const showViewInstitutionModal = ref(false)
const showEditInstitutionModal = ref(false)
const showDeleteConfirmModal = ref(false)
const selectedInstitution = ref(null)

// 全选状态
const selectAll = ref(false)

// 监听pageSize的变化
watch(pageSize, (newSize) => {
  // 确保pageSize是数值类型
  pageSize.value = parseInt(newSize)
  // 重置到第一页
  currentPage.value = 1
})

// 生命周期钩子
onMounted(() => {
  fetchInstitutions()
})

// 获取院校数据
const fetchInstitutions = async () => {
  isLoading.value = true;
  hasError.value = false;
  errorMessage.value = '';
  
  try {
    const response = await schoolService.getAllSchools();
    
    if (response.data) {
      institutions.value = response.data.map(school => {
        console.log(`处理学校数据: ${school.name}, 国家代码:${school.countryCode}, logo_url:${school.logoUrl || '无'}`);
        
        return {
          id: school.id,
          name: school.name,
          englishName: school.englishName || '',
          logo: normalizeSchoolLogo(school.logoUrl),
          country: school.country ,
          countryCode: school.countryCode ,
          type: mapSchoolType(school.type),
          location: {
            city: school.city || '',
            state: school.state || '',
            campus: school.campus || '',
            location: school.location || ''
          },
          rankings: {
            qs: parseInt(school.qsRanking) || null,
            times: parseInt(school.timesRanking) || null,
            world: parseInt(school.worldRanking) || null,
            national: parseInt(school.nationalRanking) || null,
            shanghai: parseInt(school.shanghaiRanking) || null
          },
          tuition: {
            fee: school.tuitionFee,
            currency: school.currency || 'CNY',
            rebate: school.rebatePercentage
          },
          livingCost: school.livingCost,
          website: school.website || '',
          description: school.description || '',
          services: {
            student: school.studentServices || '',
            housing: school.housingInfo || '',
            scholarship: school.scholarshipInfo || '',
            visa: school.visaInfo || ''
          },
          contact: {
            email: school.contactEmail || '',
            phone: school.contactPhone || ''
          },
          students: {
            international: school.internationalStudentPercentage,
            chinese: school.chineseStudentCount,
            total: school.totalStudentCount,
            ratio: school.studentFacultyRatio
          },
          admissions: {
            acceptance: school.acceptanceRate,
            avgGpa: school.averageGpa,
            deadline: school.applicationDeadline || ''
          },
          requirements: {
            toefl: school.toeflRequirement || '',
            ielts: school.ieltsRequirement || '',
            gre: school.greRequirement || '',
            gmat: school.gmatRequirement || '',
            sat: school.satRequirement || ''
          },
          notable: {
            alumni: school.famousAlumni || '',
            programs: school.featuredPrograms || ''
          },
          tags: school.tags ? school.tags.split(',') : [],
          image: school.imageUrl || '',
          banner: school.bannerUrl || '',
          collegesCount: 0,
          majorsCount: 0,
          applicationsCount: 0,
          selected: false
        };
      });
      
      console.log('处理后的院校数据:', institutions.value.map(inst => ({
        id: inst.id,
        name: inst.name, 
        country: inst.country,
        countryCode: inst.countryCode
      })));
      
      // 获取每个院校的学院和专业数量
      await Promise.all(institutions.value.map(async (institution) => {
        try {
          // 获取学院数据
          const facultiesResponse = await schoolService.getSchoolFaculties(institution.id);
          if (Array.isArray(facultiesResponse.data)) {
            institution.collegesCount = facultiesResponse.data.length;
            console.log(`学校 ${institution.name} 的学院数量: ${institution.collegesCount}`);
          } else if (Array.isArray(facultiesResponse)) {
            institution.collegesCount = facultiesResponse.length;
            console.log(`学校 ${institution.name} 的学院数量: ${institution.collegesCount}`);
          } else {
            console.warn(`获取学校 ${institution.name} 的学院数据格式不正确:`, facultiesResponse);
            institution.collegesCount = 0;
          }
          
          // 获取专业数据
          const majorsResponse = await schoolService.getSchoolMajors(institution.id);
          if (Array.isArray(majorsResponse.data)) {
            institution.majorsCount = majorsResponse.data.length;
            console.log(`学校 ${institution.name} 的专业数量: ${institution.majorsCount}`);
          } else if (Array.isArray(majorsResponse)) {
            institution.majorsCount = majorsResponse.length;
            console.log(`学校 ${institution.name} 的专业数量: ${institution.majorsCount}`);
          } else {
            console.warn(`获取学校 ${institution.name} 的专业数据格式不正确:`, majorsResponse);
            institution.majorsCount = 0;
          }
          
          // 获取申请数据
          const applicationsResponse = await schoolService.getSchoolApplications(institution.id);
          if (Array.isArray(applicationsResponse.data)) {
            institution.applicationsCount = applicationsResponse.data.length;
            console.log(`学校 ${institution.name} 的申请数量: ${institution.applicationsCount}`);
          } else if (Array.isArray(applicationsResponse)) {
            institution.applicationsCount = applicationsResponse.length;
            console.log(`学校 ${institution.name} 的申请数量: ${institution.applicationsCount}`);
          } else {
            console.warn(`获取学校 ${institution.name} 的申请数据格式不正确:`, applicationsResponse);
            institution.applicationsCount = 0;
          }
        } catch (error) {
          console.error(`获取学校 ${institution.name} (ID: ${institution.id}) 的相关数据失败:`, error);
          // 设置默认值
          institution.collegesCount = 0;
          institution.majorsCount = 0;
          institution.applicationsCount = 0;
        }
      }));
    }
  } catch (error) {
    console.error('获取院校数据失败:', error);
    hasError.value = true;
    errorMessage.value = '获取院校数据失败，请稍后重试';
    ElMessage.error('获取院校数据失败，请稍后重试');
  } finally {
    isLoading.value = false;
  }
};

// 重试加载数据
const retryLoading = () => {
  fetchInstitutions()
}

// 解析语言要求字符串
const parseRequirement = (reqStr) => {
  if (!reqStr) return null
  
  // 尝试解析格式如 "100 (R:25, L:25, S:25, W:25)" 的字符串
  const totalMatch = reqStr.match(/^(\d+)/)
  const readingMatch = reqStr.match(/R:(\d+)/)
  const listeningMatch = reqStr.match(/L:(\d+)/)
  const speakingMatch = reqStr.match(/S:(\d+)/)
  const writingMatch = reqStr.match(/W:(\d+)/)
  
  return {
    total: totalMatch ? parseInt(totalMatch[1]) : null,
    reading: readingMatch ? parseInt(readingMatch[1]) : null,
    listening: listeningMatch ? parseInt(listeningMatch[1]) : null,
    speaking: speakingMatch ? parseInt(speakingMatch[1]) : null,
    writing: writingMatch ? parseInt(writingMatch[1]) : null
  }
}

// 映射院校类型
const mapSchoolType = (type) => {
  switch (type) {
    case 'UNIVERSITY':
      return 'university'
    case 'COLLEGE':
      return 'college'
    default:
      return type ? type.toLowerCase() : 'university'
  }
}

// 计算属性
const filteredInstitutions = computed(() => {
  let result = [...institutions.value]

  // 应用筛选
  if (filters.value.country) {
    console.log(`按国家筛选: ${filters.value.country}`);
    result = result.filter(item => {
      const match = item.country === filters.value.country || item.countryCode === filters.value.country;
      console.log(`院校 ${item.name}: country=${item.country}, countryCode=${item.countryCode}, 匹配=${match}`);
      return match;
    });
  }
  if (filters.value.type) {
    result = result.filter(item => item.type === filters.value.type)
  }
  if (filters.value.search) {
    const searchLower = filters.value.search.toLowerCase()
    result = result.filter(item => 
      item.name.toLowerCase().includes(searchLower)
    )
  }

  // 应用排序
  result.sort((a, b) => {
    switch (filters.value.sort) {
      case 'name':
        return a.name.localeCompare(b.name)
      case 'rank':
        return (a.rankings?.qs || 9999) - (b.rankings?.qs || 9999)
      case 'applications':
        return b.applicationsCount - a.applicationsCount
      default:
        return 0
    }
  })

  return result
})

// 分页信息
const paginationInfo = computed(() => {
  const start = totalInstitutions.value === 0 ? 0 : (currentPage.value - 1) * pageSize.value + 1
  const end = Math.min(start + pageSize.value - 1, totalInstitutions.value)
  return { start, end }
})

// 显示的页码
const displayedPages = computed(() => {
  const pages = []
  const maxDisplayed = 5
  let start = Math.max(1, currentPage.value - 2)
  let end = Math.min(start + maxDisplayed - 1, totalPages.value)

  if (end - start + 1 < maxDisplayed) {
    start = Math.max(1, end - maxDisplayed + 1)
  }

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }

  return pages
})

const showEndEllipsis = computed(() => {
  return totalPages.value > Math.max(...displayedPages.value) + 1
})

const showEndPage = computed(() => {
  return totalPages.value > Math.max(...displayedPages.value) && !displayedPages.value.includes(totalPages.value)
})

// 添加计算属性，在showEndPage计算属性下方添加
const paginatedInstitutions = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredInstitutions.value.slice(start, end)
})

// 方法
const generateAvatarColor = (name) => {
  if (!name) return '#1890ff'
  
  let hash = 0
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash)
  }
  
  const hue = Math.abs(hash % 360)
  return `hsl(${hue}, 70%, 60%)`
}

const getCountryName = (code) => {
  if (!code) {
    console.warn('国家代码为空');
    return '未知';
  }
  
  console.log(`获取国家名称: 代码=${code}`);
  
  const result = COUNTRIES[code] || code;
  console.log(`国家名称结果: ${result}`);
  return result;
}

const getTypeName = (code) => {
  if (!code) return '未知';
  return SCHOOL_TYPES[code] || code;
}

// 筛选方法
const applyFilters = async () => {
  currentPage.value = 1
  
  if (filters.value.country || filters.value.type || filters.value.search) {
    isLoading.value = true
    try {
      let response
      
      // 根据筛选条件调用相应的API
      if (filters.value.search) {
        response = await schoolService.searchSchools(filters.value.search)
      } else if (filters.value.country) {
        response = await schoolService.getSchoolsByCountry(filters.value.country)
      } else if (filters.value.type) {
        response = await schoolService.getSchoolsByType(
          filters.value.type.toUpperCase()
        )
      }
      
      if (response) {
        // 更新数据
        institutions.value = response.data.map(school => {
          console.log(`筛选后处理学校: ${school.name}, 国家代码:`, school.country_code);
          
          return {
            id: school.id,
          name: school.name,
          englishName: school.englishName || '',
          logo: normalizeSchoolLogo(school.logoUrl),
          country: school.country ,
          countryCode: school.countryCode ,
          type: mapSchoolType(school.type),
          location: {
            city: school.city || '',
            state: school.state || '',
            campus: school.campus || '',
            location: school.location || ''
          },
          rankings: {
            qs: parseInt(school.qsRanking) || null,
            times: parseInt(school.timesRanking) || null,
            world: parseInt(school.worldRanking) || null,
            national: parseInt(school.nationalRanking) || null,
            shanghai: parseInt(school.shanghaiRanking) || null
          },
          tuition: {
            fee: school.tuitionFee,
            currency: school.currency || 'CNY',
            rebate: school.rebatePercentage
          },
          livingCost: school.livingCost,
          website: school.website || '',
          description: school.description || '',
          services: {
            student: school.studentServices || '',
            housing: school.housingInfo || '',
            scholarship: school.scholarshipInfo || '',
            visa: school.visaInfo || ''
          },
          contact: {
            email: school.contactEmail || '',
            phone: school.contactPhone || ''
          },
          students: {
            international: school.internationalStudentPercentage,
            chinese: school.chineseStudentCount,
            total: school.totalStudentCount,
            ratio: school.studentFacultyRatio
          },
          admissions: {
            acceptance: school.acceptanceRate,
            avgGpa: school.averageGpa,
            deadline: school.applicationDeadline || ''
          },
          requirements: {
            toefl: school.toeflRequirement || '',
            ielts: school.ieltsRequirement || '',
            gre: school.greRequirement || '',
            gmat: school.gmatRequirement || '',
            sat: school.satRequirement || ''
          },
          notable: {
            alumni: school.famousAlumni || '',
            programs: school.featuredPrograms || ''
          },
          tags: school.tags ? school.tags.split(',') : [],
          image: school.imageUrl || '',
          banner: school.bannerUrl || '',
          collegesCount: 0,
          majorsCount: 0,
          applicationsCount: 0,
          selected: false
          };
        });
        
        console.log('筛选后的院校数据:', institutions.value.map(inst => ({
          id: inst.id,
          name: inst.name, 
          country: inst.country,
          countryCode: inst.countryCode
        })));
      
      // 获取每个院校的学院和专业数量
      await Promise.allSettled(institutions.value.map(async (institution) => {
          // 获取学院数据
          try {
            const facultiesResponse = await schoolService.getSchoolFaculties(institution.id)
            if (facultiesResponse?.data) {
              institution.collegesCount = facultiesResponse.data.length
            }
          } catch (error) {
            console.warn(`获取院校 ${institution.id} 的学院数据失败:`, error.message)
            // 404错误是正常的，API可能尚未实现
            if (error.response && error.response.status !== 404) {
              console.error('非404错误:', error)
            }
          }
          
          // 获取专业数据
          try {
            const majorsResponse = await schoolService.getSchoolMajors(institution.id)
            if (majorsResponse?.data) {
              institution.majorsCount = majorsResponse.data.length
            }
          } catch (error) {
            console.warn(`获取院校 ${institution.id} 的专业数据失败:`, error.message)
            // 404错误是正常的，API可能尚未实现
            if (error.response && error.response.status !== 404) {
              console.error('非404错误:', error)
            }
          }
          
          // 获取申请数据
          try {
            const applicationsResponse = await schoolService.getSchoolApplications(institution.id)
            if (applicationsResponse?.data) {
              institution.applicationsCount = applicationsResponse.data.length
            }
          } catch (error) {
            console.warn(`获取院校 ${institution.id} 的申请数据失败:`, error.message)
            // 404错误是正常的，API可能尚未实现
            if (error.response && error.response.status !== 404) {
              console.error('非404错误:', error)
            }
          }
        }))
      }
    } catch (error) {
      console.error('筛选院校数据失败:', error)
      ElMessage.error('筛选院校数据失败，请稍后重试')
    } finally {
      isLoading.value = false
    }
  }
}

const resetFilters = () => {
  filters.value = {
    country: '',
    type: '',
    sort: 'name',
    search: ''
  }
  currentPage.value = 1
  fetchInstitutions() // 重新获取所有数据
}

// 分页方法
const changePage = (page) => {
  currentPage.value = page
}

const handlePageSizeChange = () => {
  // 确保pageSize是数字类型
  pageSize.value = parseInt(pageSize.value)
  // 重置到第一页
  currentPage.value = 1
}

// 选择方法
const toggleSelectAll = () => {
  const newValue = selectAll.value
  institutions.value.forEach(item => {
    item.selected = newValue
  })
}

const handleSelect = () => {
  selectAll.value = institutions.value.every(item => item.selected)
}

// 模态框方法
const showAddModal = () => {
  showAddInstitutionModal.value = true
}

const showViewModal = (institution) => {
  selectedInstitution.value = institution
  showViewInstitutionModal.value = true
}

const showEditModal = (institution) => {
  selectedInstitution.value = institution
  showEditInstitutionModal.value = true
}

const showDeleteModal = (institution) => {
  selectedInstitution.value = institution
  showDeleteConfirmModal.value = true
}

const closeAddModal = () => {
  showAddInstitutionModal.value = false
}

const closeViewModal = () => {
  showViewInstitutionModal.value = false
  selectedInstitution.value = null
}

const closeEditModal = () => {
  showEditInstitutionModal.value = false
  selectedInstitution.value = null
}

const closeDeleteModal = () => {
  showDeleteConfirmModal.value = false
  selectedInstitution.value = null
}

// 数据操作方法
const handleSave = async (newInstitution) => {
  // 添加新院校
  try {
    // 转换为后端需要的格式
    const schoolData = {
      name: newInstitution.name,
      english_name: newInstitution.englishName || '',
      country: newInstitution.country || '',
      country_code: newInstitution.country_code || newInstitution.country?.toLowerCase() || 'us',
      city: newInstitution.location?.city || '',
      state: newInstitution.location?.state || '',
      location: newInstitution.location?.location || '',
      campus: newInstitution.location?.campus || '',
      qs_ranking: newInstitution.rankings?.qs || null,
      times_ranking: newInstitution.rankings?.times || null,
      world_ranking: newInstitution.rankings?.world || null,
      national_ranking: newInstitution.rankings?.national || null,
      shanghai_ranking: newInstitution.rankings?.shanghai || null,
      type: newInstitution.type ? newInstitution.type.toUpperCase() : 'UNIVERSITY',
      ownership: newInstitution.ownership || '',
      tuition_fee: newInstitution.tuition?.fee || null,
      currency: newInstitution.tuition?.currency || 'CNY',
      fee_currency: newInstitution.tuition?.currency || 'CNY',
      rebate_percentage: newInstitution.tuition?.rebate || null,
      living_cost: newInstitution.livingCost || null,
      website: newInstitution.website || '',
      description: newInstitution.description || '',
      history: newInstitution.history || '',
      features: newInstitution.features || '',
      has_undergraduate: newInstitution.has?.undergraduate ? 1 : 0,
      has_graduate: newInstitution.has?.graduate ? 1 : 0,
      has_phd: newInstitution.has?.phd ? 1 : 0,
      international_student_percentage: newInstitution.students?.international || null,
      chinese_student_count: newInstitution.students?.chinese || null,
      total_student_count: newInstitution.students?.total || null,
      student_faculty_ratio: newInstitution.students?.ratio || null,
      acceptance_rate: newInstitution.admissions?.acceptance || null,
      average_gpa: newInstitution.admissions?.avgGpa || null,
      application_deadline: newInstitution.admissions?.deadline || '',
      toefl_requirement: formatRequirement(newInstitution.requirements?.toefl),
      ielts_requirement: formatRequirement(newInstitution.requirements?.ielts),
      gre_requirement: newInstitution.requirements?.gre || '',
      gmat_requirement: newInstitution.requirements?.gmat || '',
      sat_requirement: newInstitution.requirements?.sat || '',
      student_services: newInstitution.services?.student || '',
      housing_info: newInstitution.services?.housing || '',
      scholarship_info: newInstitution.services?.scholarship || '',
      visa_info: newInstitution.services?.visa || '',
      contact_email: newInstitution.contact?.email || '',
      contact_phone: newInstitution.contact?.phone || '',
      famous_alumni: newInstitution.notable?.alumni || '',
      notable_programs: newInstitution.notable?.programs || '',
      featured_programs: newInstitution.notable?.programs || '',
      tags: newInstitution.tags ? newInstitution.tags.join(',') : '',
      logo_url: newInstitution.logo || '',
      image_url: newInstitution.image || '',
      banner_url: newInstitution.banner || '',
      status: 1
    }
    
    const response = await schoolService.addSchool(schoolData)
    
    if (response.status === 201) {
      // 将新添加的院校添加到列表中
      const addedSchool = response.data
      institutions.value.push({
        id: addedSchool.id,
        school_id: addedSchool.school_id,
        name: addedSchool.name,
        english_name: addedSchool.english_name,
        code: addedSchool.code,
        description: addedSchool.description,
        history: addedSchool.history,
        ranking: addedSchool.ranking,
        dean_name: addedSchool.dean_name,
        faculty_count: addedSchool.faculty_count,
        student_count: addedSchool.student_count,
        website: addedSchool.website,
        image_url: addedSchool.image_url,
        featured_programs: addedSchool.featured_programs,
        research_areas: addedSchool.research_areas,
        contact_email: addedSchool.contact_email,
        contact_phone: addedSchool.contact_phone,
        created_time: addedSchool.created_time,
        updated_time: addedSchool.updated_time,
        is_deleted: addedSchool.is_deleted,
        status: addedSchool.status,
        created_at: addedSchool.created_at,
        updated_at: addedSchool.updated_at,
        country: addedSchool.country_code,
        country_code: addedSchool.country_code,
        city: addedSchool.city,
        state: addedSchool.state,
        location: addedSchool.location,
        campus: addedSchool.campus,
        type: addedSchool.type,
        ownership: addedSchool.ownership,
        world_ranking: addedSchool.world_ranking,
        national_ranking: addedSchool.national_ranking,
        qs_ranking: addedSchool.qs_ranking,
        times_ranking: addedSchool.times_ranking,
        shanghai_ranking: addedSchool.shanghai_ranking,
        tuition_fee: addedSchool.tuition_fee,
        rebate_percentage: addedSchool.rebate_percentage,
        living_cost: addedSchool.living_cost,
        currency: addedSchool.currency,
        international_student_percentage: addedSchool.international_student_percentage,
        chinese_student_count: addedSchool.chinese_student_count,
        total_student_count: addedSchool.total_student_count,
        student_faculty_ratio: addedSchool.student_faculty_ratio,
        acceptance_rate: addedSchool.acceptance_rate,
        average_gpa: addedSchool.average_gpa,
        toefl_requirement: parseRequirement(addedSchool.toefl_requirement),
        ielts_requirement: parseRequirement(addedSchool.ielts_requirement),
        gre_requirement: addedSchool.gre_requirement,
        gmat_requirement: addedSchool.gmat_requirement,
        sat_requirement: addedSchool.sat_requirement,
        application_deadline: addedSchool.application_deadline,
        logo_url: addedSchool.logo_url,
        banner_url: addedSchool.banner_url,
        has_undergraduate: addedSchool.has_undergraduate === 1,
        has_graduate: addedSchool.has_graduate === 1,
        has_phd: addedSchool.has_phd === 1,
        tags: addedSchool.tags ? addedSchool.tags.split(',') : [],
        student_services: addedSchool.student_services,
        housing_info: addedSchool.housing_info,
        famous_alumni: addedSchool.famous_alumni,
        notable_programs: addedSchool.notable_programs,
        entry_requirement: addedSchool.entry_requirement,
        scholarship_info: addedSchool.scholarship_info,
        visa_info: addedSchool.visa_info,
        selected: false,
        collegesCount: 0,
        majorsCount: 0,
        applicationsCount: 0
      })
      
      ElMessage.success('院校添加成功')
    }
  } catch (error) {
    console.error('添加院校失败:', error)
    ElMessage.error('添加院校失败，请稍后重试')
  }
  closeAddModal()
}

// 格式化语言要求为字符串
const formatRequirement = (req) => {
  if (!req || !req.total) return ''
  
  const parts = []
  if (req.reading) parts.push(`R:${req.reading}`)
  if (req.listening) parts.push(`L:${req.listening}`)
  if (req.speaking) parts.push(`S:${req.speaking}`)
  if (req.writing) parts.push(`W:${req.writing}`)
  
  if (parts.length > 0) {
    return `${req.total} (${parts.join(', ')})`
  } else {
    return `${req.total}`
  }
}

const handleUpdate = async (updatedInstitution) => {
  // 更新院校信息
  try {
    // 转换为后端需要的格式
    const schoolData = {
      id: updatedInstitution.id,
      name: updatedInstitution.name,
      english_name: updatedInstitution.englishName || '',
      country: updatedInstitution.country || '',
      country_code: updatedInstitution.country_code || updatedInstitution.country?.toLowerCase() || 'us',
      city: updatedInstitution.location?.city || '',
      state: updatedInstitution.location?.state || '',
      location: updatedInstitution.location?.location || '',
      campus: updatedInstitution.location?.campus || '',
      qs_ranking: updatedInstitution.rankings?.qs || null,
      times_ranking: updatedInstitution.rankings?.times || null,
      world_ranking: updatedInstitution.rankings?.world || null,
      national_ranking: updatedInstitution.rankings?.national || null,
      shanghai_ranking: updatedInstitution.rankings?.shanghai || null,
      type: updatedInstitution.type ? updatedInstitution.type.toUpperCase() : 'UNIVERSITY',
      ownership: updatedInstitution.ownership || '',
      tuition_fee: updatedInstitution.tuition?.fee || null,
      currency: updatedInstitution.tuition?.currency || 'CNY',
      fee_currency: updatedInstitution.tuition?.currency || 'CNY',
      rebate_percentage: updatedInstitution.tuition?.rebate || null,
      living_cost: updatedInstitution.livingCost || null,
      website: updatedInstitution.website || '',
      description: updatedInstitution.description || '',
      history: updatedInstitution.history || '',
      features: updatedInstitution.features || '',
      has_undergraduate: updatedInstitution.has?.undergraduate ? 1 : 0,
      has_graduate: updatedInstitution.has?.graduate ? 1 : 0,
      has_phd: updatedInstitution.has?.phd ? 1 : 0,
      international_student_percentage: updatedInstitution.students?.international || null,
      chinese_student_count: updatedInstitution.students?.chinese || null,
      total_student_count: updatedInstitution.students?.total || null,
      student_faculty_ratio: updatedInstitution.students?.ratio || null,
      acceptance_rate: updatedInstitution.admissions?.acceptance || null,
      average_gpa: updatedInstitution.admissions?.avgGpa || null,
      application_deadline: updatedInstitution.admissions?.deadline || '',
      toefl_requirement: formatRequirement(updatedInstitution.requirements?.toefl),
      ielts_requirement: formatRequirement(updatedInstitution.requirements?.ielts),
      gre_requirement: updatedInstitution.requirements?.gre || '',
      gmat_requirement: updatedInstitution.requirements?.gmat || '',
      sat_requirement: updatedInstitution.requirements?.sat || '',
      student_services: updatedInstitution.services?.student || '',
      housing_info: updatedInstitution.services?.housing || '',
      scholarship_info: updatedInstitution.services?.scholarship || '',
      visa_info: updatedInstitution.services?.visa || '',
      contact_email: updatedInstitution.contact?.email || '',
      contact_phone: updatedInstitution.contact?.phone || '',
      famous_alumni: updatedInstitution.notable?.alumni || '',
      notable_programs: updatedInstitution.notable?.programs || '',
      featured_programs: updatedInstitution.notable?.programs || '',
      tags: updatedInstitution.tags ? updatedInstitution.tags.join(',') : '',
      logo_url: updatedInstitution.logo || '',
      image_url: updatedInstitution.image || '',
      banner_url: updatedInstitution.banner || '',
      status: updatedInstitution.status ? 1 : 0
    }
    
    const response = await schoolService.updateSchool(updatedInstitution.id, schoolData)
    
    if (response.data.success) {
      // 更新本地数据
      const index = institutions.value.findIndex(item => item.id === updatedInstitution.id)
      if (index !== -1) {
        institutions.value[index] = {
          ...institutions.value[index],
          ...updatedInstitution
        }
      }
      
      ElMessage.success('院校信息已更新')
    } else {
      ElMessage.error(response.data.message || '更新失败')
    }
  } catch (error) {
    console.error('更新院校失败:', error)
    ElMessage.error('更新院校失败，请稍后重试')
  }
  closeEditModal()
}

const handleDelete = async () => {
  // 删除院校
  if (!selectedInstitution.value || !selectedInstitution.value.id) {
    ElMessage.error('无效的院校ID')
    return
  }
  
  try {
    const response = await schoolService.deleteSchool(selectedInstitution.value.id)
    
    if (response.data.success) {
      // 从列表中移除
      const index = institutions.value.findIndex(item => item.id === selectedInstitution.value.id)
      if (index !== -1) {
        institutions.value.splice(index, 1)
      }
      
      ElMessage.success('院校已删除')
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    console.error('删除院校失败:', error)
    ElMessage.error('删除院校失败，请稍后重试')
  }
  closeDeleteModal()
}

const handleImportData = () => {
  // 导入数据的逻辑
  ElMessage.info('导入数据功能开发中')
}

const handleLogoError = (event) => {
  // 隐藏图片元素
  event.target.style.display = 'none';
  
  // 获取父元素
  const logoContainer = event.target.parentElement;
  
  // 创建文字占位符
  if (!logoContainer.querySelector('.avatar-text')) {
    const institution = institutions.value.find(inst => 
      inst.name === event.target.alt
    );
    
    const avatarText = document.createElement('div');
    avatarText.className = 'avatar-text';
    avatarText.style.backgroundColor = generateAvatarColor(institution?.name || '');
    avatarText.textContent = (institution?.name?.charAt(0) || '院');
    
    // 添加到父元素
    logoContainer.appendChild(avatarText);
  }
}
</script>

<style scoped>
/* 院校管理页面样式 */
.institutions-content {
  padding: 0px;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 4px;
  color: #111;
}

.page-subtitle {
  color: #666;
  font-size: 14px;
}

.page-actions {
  display: flex;
  gap: 12px;
}

/* 过滤栏 */
.filter-bar {
  display: flex;
  align-items: flex-end;
  gap: 16px;
  background-color: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(0, 0, 0, 0.02);
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-group label {
  font-size: 12px;
  color: #666;
  font-weight: 500;
}

.select-wrapper {
  position: relative;
}

.select-wrapper select {
  padding: 8px 12px;
  border-radius: 8px;
  border: 1px solid #d9d9d9;
  background-color: #fff;
  min-width: 140px;
  appearance: none;
  font-size: 14px;
  transition: all 0.3s;
}

.select-wrapper select:focus {
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
  outline: none;
}

.select-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  width: 16px;
  height: 16px;
  color: #999;
  pointer-events: none;
}

.filter-search {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}

.search-input-wrapper {
  position: relative;
}

.search-input-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  width: 16px;
  height: 16px;
  color: #999;
}

.filter-search-input {
  padding: 8px 12px 8px 36px;
  border-radius: 8px;
  border: 1px solid #d9d9d9;
  width: 100%;
  font-size: 14px;
  transition: all 0.3s;
}

.filter-search-input:focus {
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
  outline: none;
}

.filter-actions {
  display: flex;
  gap: 8px;
}

/* 院校表格 */
.institutions-table {
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 24px;
  border: 1px solid rgba(0, 0, 0, 0.02);
  min-height: 200px; /* 确保空状态下也有一定高度 */
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 50px 0;
  color: #666;
  font-size: 14px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(24, 144, 255, 0.1);
  border-radius: 50%;
  border-top-color: #1890ff;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background-color: #fafafa;
}

th {
  padding: 12px 16px;
  text-align: left;
  font-weight: 600;
  color: #666;
  border-bottom: 1px solid #f0f0f0;
}

td {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.th-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.td-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.table-checkbox {
  width: 16px;
  height: 16px;
  border-radius: 4px;
}

.institution-name {
  display: flex;
  align-items: center;
  gap: 12px;
}

.institution-logo {
  width: 52px; /* 从40px增加到52px */
  height: 52px; /* 从40px增加到52px */
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid #f0f0f0;
  transition: all 0.3s;
  background-color: #f8f9fa; /* 添加背景色 */
  padding: 2px; /* 添加内边距给logo留出空间 */
  box-sizing: border-box;
}

.institution-logo img {
  width: 100%;
  height: 100%;
  object-fit: contain; /* 改为contain确保logo完整显示 */
  object-position: center; /* 居中显示 */
  background-color: white; /* 为logo添加白色背景 */
  border-radius: 4px; /* 给图片本身添加圆角 */
}

.avatar-text {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px; /* 从18px增加到20px */
  font-weight: 500;
  color: white;
  text-transform: uppercase;
  border-radius: 4px;
}

.country-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  background-color: #f5f7fa;
  color: #666;
  display: inline-block;
}

.count-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  background-color: #f5f7fa;
  color: #666;
  display: inline-block;
  min-width: 40px;
  text-align: center;
}

.action-buttons {
  display: flex;
  gap: 8px;
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

/* 分页 */
.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background-color: #fff;
  border-radius: 12px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(0, 0, 0, 0.02);
}

.button-icon {
  width: 16px;
  height: 16px;
  margin-right: 6px;
}

.table-info {
  font-size: 14px;
  color: #666;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-button {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  background-color: transparent;
  border: 1px solid #d9d9d9;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.pagination-button:hover:not(:disabled) {
  border-color: #1890ff;
  color: #1890ff;
}

.pagination-button:disabled {
  color: #d9d9d9;
  cursor: not-allowed;
}

.pagination-number {
  min-width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  background-color: transparent;
  border: 1px solid #d9d9d9;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.pagination-number:hover:not(.active) {
  border-color: #1890ff;
  color: #1890ff;
}

.pagination-number.active {
  background-color: #1890ff;
  color: white;
  border-color: #1890ff;
}

.pagination-ellipsis {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  color: #666;
}

.page-size {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
}

.page-size .select-wrapper select {
  min-width: 80px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .filter-bar {
    flex-wrap: wrap;
  }
  
  .filter-group {
    flex: 1 0 calc(33.33% - 16px);
  }
  
  .filter-search {
    flex: 1 0 100%;
    order: -1;
    margin-bottom: 16px;
  }
}

@media (max-width: 768px) {
  .filter-group {
    flex: 1 0 calc(50% - 8px);
  }
  
  .filter-actions {
    flex: 1 0 100%;
    justify-content: flex-end;
    margin-top: 16px;
  }
  
  .table-footer {
    flex-direction: column;
    gap: 16px;
    align-items: center;
  }
  
  .pagination {
    order: -1;
  }
}

@media (max-width: 576px) {
  .filter-group {
    flex: 1 0 100%;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .page-actions {
    width: 100%;
    justify-content: flex-end;
  }
  
  .institutions-table {
    overflow-x: auto;
  }
  
  .table-footer {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .pagination {
    order: -1;
    width: 100%;
    justify-content: center;
  }
}
</style> 
