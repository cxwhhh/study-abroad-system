<template>
  <div class="application-process">
    <!-- 导航栏 -->
    <nav-bar></nav-bar>

    <!-- 主标题区域 -->
    <section class="hero-section">
      <div class="hero-content">
        <div class="section-label">APPLICATION PROCESS</div>
        <h1 class="hero-title">留学申请流程</h1>
        <div class="gold-separator"></div>
        <p class="hero-subtitle">五步完成您的留学申请</p>
      </div>
    </section>

    <!-- 申请进度指示器 -->
    <section class="progress-section">
      <div class="progress-container">
        <div class="progress-steps" :data-step="currentStep">
          <div v-for="(step, index) in steps.slice(0, 5)" :key="index" class="progress-step" :class="{
            'active': currentStep === index,
            'completed': completedSteps.includes(index),
            'clickable': canNavigateToStep(index)
          }" @click="navigateToStep(index)">
            <div class="step-indicator">
              <span v-if="completedSteps.includes(index)" class="step-check">
                <el-icon>
                  <Check />
                </el-icon>
              </span>
              <span v-else>{{ index + 1 }}</span>
            </div>
            <div class="step-label">{{ step.label }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 申请表单区域 -->
    <section class="form-section">
      <div class="form-container">
        <!-- 登录提示 -->
        <div v-if="!isLoggedIn" class="login-prompt">
          <el-alert title="请先登录" type="warning" description="您需要先登录才能进行留学申请流程。请点击右上角登录按钮进行登录。" show-icon
            :closable="false">
          </el-alert>
        </div>

        <!-- 申请表单内容 -->
        <div v-else>
          <!-- 步骤1: 学生信息 -->
          <div v-if="currentStep === 0" class="form-step student-info-step">
            <!-- 导航按钮 -->
            <div class="step-navigation">
              <el-button v-if="currentStep > 0" @click="prevStep" plain>
                上一步
              </el-button>
              <el-button type="primary" @click="nextStep" :disabled="!canProceed || submittingFiles"
                :loading="submittingFiles">
                {{ submittingFiles ? '正在提交文件请稍等...' : (isLastStep ? '完成申请' : '下一步') }}
              </el-button>
            </div>
            <h2 class="step-title">填写个人信息</h2>
            <p class="step-description">请提供您的个人信息，以便我们为您提供更精准的留学申请服务</p>

            <div class="form-content">
              <!-- 左右两列布局 -->
              <div class="form-columns">
                <!-- 左列 -->
                <div class="form-column">
                  <div class="form-group">
                    <label class="form-label required">姓名</label>
                    <el-input v-model="formData.name" placeholder="输入您的姓名" />
                  </div>

                  <div class="form-group">
                    <label class="form-label">性别</label>
                    <el-select v-model="formData.gender" placeholder="请选择性别">
                      <el-option label="男" value="男" />
                      <el-option label="女" value="女" />
                      <el-option label="其他" value="其他" />
                    </el-select>
                  </div>

                  <div class="form-group">
                    <label class="form-label">国籍</label>
                    <el-input v-model="formData.nationality" placeholder="输入您的国籍" />
                  </div>

                  <div class="form-group">
                    <label class="form-label required">手机</label>
                    <el-input v-model="formData.phone" placeholder="输入您的手机号码" />
                  </div>

                  <div class="form-group">
                    <label class="form-label">微信</label>
                    <el-input v-model="formData.wechat" placeholder="输入您的微信号" />
                  </div>

                  <div class="form-group">
                    <label class="form-label required">当前学校</label>
                    <el-input v-model="formData.currentSchool" placeholder="输入您的当前院校" />
                  </div>
                </div>

                <!-- 右列 -->
                <div class="form-column">
                  <div class="form-group">
                    <label class="form-label">英文名</label>
                    <el-input v-model="formData.englishName" placeholder="输入您的英文名" />
                  </div>

                  <div class="form-group">
                    <label class="form-label required">出生日期</label>
                    <el-date-picker v-model="formData.birthDate" type="date" placeholder="选择日期" style="width: 100%"
                      :clearable="false" />
                  </div>

                  <div class="form-group">
                    <label class="form-label">护照号</label>
                    <el-input v-model="formData.passportNo" placeholder="输入您的护照号" />
                  </div>

                  <div class="form-group">
                    <label class="form-label required">邮箱</label>
                    <el-input v-model="formData.email" placeholder="输入您的电子邮箱" />
                  </div>

                  <div class="form-group">
                    <label class="form-label required">GPA</label>
                    <div class="gpa-input-group">
                      <el-select v-model="formData.gpaScale" placeholder="选择绩点制度" style="width: 120px">
                        <el-option label="100分制" value="100" />
                        <el-option label="4.0分制" value="4.0" />
                        <el-option label="5.0分制" value="5.0" />
                        <el-option label="4.3分制" value="4.3" />
                        <el-option label="4.5分制" value="4.5" />
                        <el-option label="7.0分制" value="7.0" />
                        <el-option label="10.0分制" value="10.0" />
                        <el-option label="20.0分制" value="20.0" />
                        <el-option label="其它（参考格式：绩点/分制）" value="0" />
                      </el-select>
                      <el-input v-model="formData.gpa" placeholder="输入GPA分数" style="width: calc(100% - 120px)">
                        <template #append>
                          分
                        </template>
                      </el-input>
                    </div>
                  </div>

                  <div class="form-group">
                    <label class="form-label required">专业</label>
                    <el-input v-model="formData.currentMajor" placeholder="输入您的当前专业" />
                  </div>
                </div>
              </div>

              <!-- 添加同步数据按钮区域 -->
              <div class="sync-data-section">
                <el-button type="primary" class="sync-data-button" @click="syncStudentData"
                  :loading="loadingStates.syncingStudent" :disabled="loadingStates.syncingStudent">
                  <el-icon v-if="!loadingStates.syncingStudent">
                    <Refresh />
                  </el-icon>
                  <span>{{ loadingStates.syncingStudent ? '正在同步学生信息...' : '从数据库同步学生信息' }}</span>
                </el-button>
                <p class="sync-data-info">
                  {{ loadingStates.syncingStudent ? '正在从数据库获取您的信息，请稍候...' : '点击此按钮可从我们的数据库自动填充您的信息' }}
                </p>
              </div>
            </div>
          </div>

          <!-- 步骤2: 选择学校 -->
          <div v-if="currentStep === 1" class="form-step school-selection-step">
            <!-- 导航按钮 -->
            <div class="step-navigation">
              <el-button v-if="currentStep > 0" @click="prevStep" plain>
                上一步
              </el-button>
              <el-button type="primary" @click="nextStep" :disabled="!canProceed">
                {{ isLastStep ? '完成申请' : '下一步' }}
              </el-button>
            </div>
            <h2 class="step-title">选择申请学校</h2>
            <p class="step-description">浏览并选择您感兴趣的学校和专业</p>

            <!-- 已选择的学校和专业 -->
            <div v-if="selectedSchools.length > 0" class="selected-schools-summary">
              <h3>已选择的学校和专业</h3>
              <div class="selected-schools-list">
                <div v-for="selection in selectedSchools" :key="`${selection.schoolId}-${selection.majorId}`"
                  class="selected-school-item">
                  <div class="school-info">
                    <span class="school-name">{{ selection.schoolName }} - {{ selection.majorName }}</span>
                    <el-tag :type="getStatusTagType(selection.applicationStatus)" size="small" class="status-tag"
                      v-if="selection.statusText">
                      {{ selection.statusText }}
                    </el-tag>
                  </div>
                  <el-button type="danger" size="small" circle @click="removeSelection(selection)"
                    :disabled="selection.applicationStatus !== 'selected'">
                    <el-icon>
                      <Close />
                    </el-icon>
                  </el-button>
                </div>
              </div>
            </div>

            <div class="school-filters">
              <div class="filter-group">
                <label class="filter-label">国家/地区</label>
                <el-select v-model="schoolFilters.country" placeholder="所有国家" class="filter-select">
                  <el-option value="" label="所有国家"></el-option>
                  <el-option v-for="country in countries" :key="country.code" :label="country.name"
                    :value="country.code">
                    <div class="country-option">
                      <span class="country-flag">{{ country.icon }}</span>
                      <span>{{ country.name }}</span>
                    </div>
                  </el-option>
                </el-select>
              </div>

              <div class="filter-group">
                <label class="filter-label">专业方向</label>
                <el-select v-model="schoolFilters.major" placeholder="所有专业" class="filter-select">
                  <el-option value="" label="所有专业"></el-option>
                  <el-option v-for="major in majors" :key="major.code" :label="major.name"
                    :value="major.code"></el-option>
                </el-select>
              </div>

              <div class="filter-group">
                <label class="filter-label">排名范围</label>
                <el-select v-model="schoolFilters.ranking" placeholder="所有排名" class="filter-select">
                  <el-option value="" label="所有排名"></el-option>
                  <el-option label="世界前10" value="top10"></el-option>
                  <el-option label="世界前50" value="top50"></el-option>
                  <el-option label="世界前100" value="top100"></el-option>
                  <el-option label="世界前200" value="top200"></el-option>
                </el-select>
              </div>

              <div class="filter-group">
                <label class="filter-label">学校搜索</label>
                <el-input v-model="schoolSearchKeyword" placeholder="搜索学校名称..." class="filter-select" clearable
                  @input="handleSchoolSearch">
                  <template #prefix>
                    <el-icon>
                      <Search />
                    </el-icon>
                  </template>
                </el-input>
              </div>
            </div>

            <div class="schools-list">
              <!-- 学校基本信息加载状态 -->
              <div v-if="loadingStates.loadingSchools" class="loading-state">
                <div class="loading-message">
                  <el-icon class="is-loading">
                    <Loading />
                  </el-icon>
                  <span>正在加载学校基本信息...</span>
                </div>
                <el-skeleton :rows="3" animated>
                  <template #template>
                    <div style="padding: 14px;">
                      <div style="display: flex; justify-content: space-between;">
                        <el-skeleton-item variant="image" style="width: 80px; height: 80px;" />
                        <el-skeleton-item variant="text" style="width: 60%;" />
                      </div>
                    </div>
                  </template>
                </el-skeleton>
              </div>

              <!-- 学校详细信息加载状态 -->
              <div v-else-if="loadingStates.loadingSchoolDetails" class="loading-details-state">
                <div class="loading-progress">
                  <div class="progress-header">
                    <el-icon class="is-loading">
                      <Loading />
                    </el-icon>
                    <span>正在加载学校详细信息...</span>
                  </div>
                  <el-progress :percentage="loadingStates.schoolDetailsProgress"
                    :format="(percentage) => `${Math.round(percentage)}%`" status="success" :stroke-width="8" />
                  <p class="progress-text">
                    已加载 {{ Math.round(loadingStates.schoolDetailsProgress * loadingStates.totalSchools / 100) }} / {{
                      loadingStates.totalSchools }} 所学校的详细信息
                  </p>
                </div>
              </div>

              <!-- 旧的加载状态（兼容性保留） -->
              <div v-else-if="loading" class="loading-state">
                <el-skeleton :rows="3" animated>
                  <template #template>
                    <div style="padding: 14px;">
                      <div style="display: flex; justify-content: space-between;">
                        <el-skeleton-item variant="image" style="width: 80px; height: 80px;" />
                        <el-skeleton-item variant="text" style="width: 60%;" />
                      </div>
                    </div>
                  </template>
                </el-skeleton>
              </div>

              <!-- 错误提示 -->
              <div v-else-if="error" class="error-state">
                <el-alert :title="error" type="error" show-icon :closable="false" description="请尝试刷新页面或稍后再试" />
              </div>

              <!-- 无数据提示 -->
              <div v-else-if="filteredSchools.length === 0 && !loading" class="empty-state">
                <el-empty description="暂无符合条件的学校" />
              </div>

              <!-- 学校横条卡片 -->
              <div v-else v-for="school in filteredSchools" :key="school.id" class="school-card-horizontal"
                @click="selectSchool(school)">
                <div class="school-logo">
                  <img :src="school.logo || defaultSchoolLogo" :alt="school.name" class="school-logo-img"
                    @error="onSchoolLogoError" />
                </div>
                <div class="school-basic-info">
                  <div class="school-name-section">
                    <div class="school-name-row">
                      <h3 class="school-name">{{ school.name }}</h3>
                      <div class="qs-ranking">
                        <span class="qs-label">QS</span>
                        <span class="qs-number">{{ school.worldRanking ? school.worldRanking.replace('第',
                          '').replace('名',
                            '') :
                          'N/A' }}</span>
                      </div>
                    </div>
                    <p class="school-location">{{ school.location }}</p>
                  </div>
                </div>
                <div class="school-details-horizontal">
                  <div class="detail-item">
                    <span class="detail-label">国家</span>
                    <span class="detail-value">{{ getCountryName(school.country) }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="detail-label">学院数</span>
                    <span class="detail-value">{{ school.facultyCount }}个</span>
                  </div>
                  <div class="detail-item">
                    <span class="detail-label">专业数</span>
                    <span class="detail-value">{{ school.programCount }}个</span>
                  </div>
                </div>
              </div>
            </div>

            <div v-if="filteredSchools.length === 0" class="no-schools-found">
              <el-icon>
                <Search />
              </el-icon>
              <p>没有找到符合条件的学校</p>
              <el-button type="primary" @click="resetSchoolFilters">重置筛选条件</el-button>
            </div>


          </div>

          <!-- 步骤3: 上传材料 -->
          <div v-if="currentStep === 2" class="form-step materials-step">
            <!-- 导航按钮 -->
            <div class="step-navigation">
              <el-button v-if="currentStep > 0" @click="prevStep" plain>
                上一步
              </el-button>
              <el-button type="primary" @click="nextStep" :disabled="!canProceed">
                {{ isLastStep ? '完成申请' : '下一步' }}
              </el-button>
            </div>
            <MaterialsUpload v-model="formData.materials" :applicationData="selectedSchools[0]"
              @completion-change="handleMaterialsCompletionChange" @refresh-files="handleMaterialsRefreshFiles" />

            <!-- 已上传文件列表 -->
            <div v-if="uploadedFiles.length > 0" class="uploaded-files-section">
              <h3>已上传的文件</h3>
              <div class="files-grid">
                <div v-for="file in uploadedFiles" :key="file.id" class="file-item">
                  <div class="file-info">
                    <div class="file-icon">
                      <el-icon>
                        <Document />
                      </el-icon>
                    </div>
                    <div class="file-details">
                      <div class="file-name">{{ file.originalName }}</div>
                      <div class="file-meta">
                        <span class="file-type">{{ getFileTypeLabel(file.description) }}</span>
                        <span class="file-size">{{ formatFileSize(file.fileSize) }}</span>
                        <span class="file-date">{{ formatDate(file.uploadTime) }}</span>
                      </div>
                    </div>
                  </div>
                  <div class="file-actions">
                    <el-button type="primary" size="small" @click="previewFile(file)"
                      :disabled="!isPreviewable(file.fileType)">
                      <el-icon>
                        <View />
                      </el-icon>
                      预览
                    </el-button>
                    <el-button type="success" size="small" @click="downloadFile(file)">
                      <el-icon>
                        <Download />
                      </el-icon>
                      下载
                    </el-button>
                    <el-button type="danger" size="small" @click="deleteFile(file)">
                      <el-icon>
                        <Delete />
                      </el-icon>
                      删除
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 步骤4: 申请进度 (原面试准备) -->
          <div v-if="currentStep === 3" class="form-step application-progress-step">

            <!-- 导航按钮 -->
            <div class="step-navigation">
              <el-button v-if="currentStep > 0" @click="prevStep" plain>
                上一步
              </el-button>
              <el-button type="primary" @click="nextStep" :disabled="!canProceed">
                {{ isLastStep ? '完成申请' : '下一步' }}
              </el-button>
            </div>
            <div class="step-header">
              <div>
                <h2 class="step-title">申请进度</h2>
                <p class="step-description">实时跟踪您的申请状态和待办事项</p>
              </div>
            </div>

            <!-- 使用ApplicationProgress组件 -->
            <ApplicationProgress v-if="formData.userId" :user-id="formData.userId"
              @update:progress="handleProgressUpdate" />
            <div v-else class="loading-placeholder">
              <el-icon class="is-loading">
                <Loading />
              </el-icon>
              <span>正在获取用户信息...</span>
            </div>
          </div>

          <!-- 步骤5: 录取结果 -->
          <div v-if="currentStep === 4" class="form-step admission-results-step">
            <!-- 导航按钮 -->
            <div class="step-navigation">
              <el-button v-if="currentStep > 0" @click="prevStep" plain>
                上一步
              </el-button>
              <el-button type="primary" @click="nextStep" :disabled="!canProceed">
                {{ isLastStep ? '完成申请' : '下一步' }}
              </el-button>
            </div>
            <h2 class="step-title">录取结果</h2>
            <p class="step-description">查看您的录取结果并做出决定</p>

            <!-- 加载状态 -->
            <div v-if="admissionResultsLoading" class="loading-container">
              <el-icon class="loading-icon">
                <Loading />
              </el-icon>
              <span>正在获取录取结果...</span>
            </div>

            <!-- 录取结果列表 -->
            <div v-else-if="admissionResults.length > 0" class="admission-results">
              <div class="section-title">录取结果</div>
              <div class="admission-schools-list">
                <div v-for="result in admissionResults" :key="result.applicationId" class="admission-school-item">
                  <div class="school-info">
                    <div class="school-details">
                      <div class="school-name">{{ result.schoolName }}</div>
                      <div class="school-major">{{ result.majorName }}</div>
                    </div>
                    <el-tag type="success" size="small" class="status-tag">
                      已录取
                    </el-tag>
                  </div>
                </div>
              </div>
            </div>

            <!-- 无录取结果 -->
            <div v-else class="no-results">
              <p>暂无录取结果</p>
            </div>
          </div>
        </div> <!-- 结束 v-else 申请表单内容 -->

      </div>
    </section>
    <!-- 对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="50%" :before-close="closeDialog">
      <div v-html="dialogContent"></div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="handleDialogConfirm" v-if="showDialogConfirm">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改学院和专业选择对话框 -->
    <el-dialog v-model="showFacultySelection" :title="`选择${selectedSchool?.name || ''}的学院和专业`" width="85%"
      class="faculty-selection-dialog">
      <div class="faculty-selection">
        <!-- 左侧学院列表 -->
        <div class="faculty-list">
          <div v-for="faculty in selectedSchool?.faculties" :key="faculty.id" class="faculty-item"
            :class="{ active: selectedFaculty?.id === faculty.id }" @click="selectFaculty(faculty)">
            <div class="faculty-icon">
              <el-icon>
                <component :is="faculty.icon" />
              </el-icon>
            </div>
            <div class="faculty-info">
              <h4 class="faculty-name">{{ faculty.name }}</h4>
            </div>
          </div>
        </div>

        <!-- 右侧专业列表 -->
        <div class="majors-container" v-if="selectedFaculty">
          <h3 class="majors-title">{{ selectedFaculty.name }}专业列表</h3>
          <div class="majors-grid">
            <div v-for="major in selectedFaculty.majors" :key="major.id" class="major-card">
              <div class="major-content">
                <div class="major-header">
                  <h4 class="major-name">{{ major.name }}</h4>
                </div>
                <p class="major-description">{{ major.description }}</p>

                <div class="major-actions">
                  <el-button size="small" type="primary" plain @click="viewMajorDetail(major)">
                    了解详细
                  </el-button>
                  <el-button size="small" type="primary" @click="applyMajor(major)">
                    申请该专业
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="no-faculty-selected" v-else>
          <el-empty description="请选择左侧学院以查看专业列表" />
        </div>
      </div>
    </el-dialog>

    <!-- 专业详情弹窗 -->
    <MajorDetailModal v-model:visible="showMajorDetail" :major-detail="selectedMajorDetail"
      :school-info="selectedSchool" :faculty-info="selectedFaculty" @apply="handleMajorApply"
      @consult="handleMajorConsult" @collect="handleMajorCollect" />

    <!-- 页脚 -->
    <footer-bar></footer-bar>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
  Check,
  Close,
  Search,
  Document,
  Refresh,
  Loading,
  View,
  Download,
  Delete
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import NavBar from '../../components/NavBar.vue'
import FooterBar from '../../components/FooterBar.vue'
import ApplicationProgress from './ApplicationProgress.vue'
import axios from 'axios'
import schoolService from '@/api/schoolService'
import MaterialsUpload from './MaterialsUpload.vue'
import MajorDetailModal from './MajorDetailModal.vue'
import sessionManager from '../../utils/sessionManager.js'

const router = useRouter()

const resolvePublicUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http') || path.startsWith('data:') || path.startsWith('blob:')) return path
  const base = import.meta.env.BASE_URL || '/'
  const normalized = path.startsWith('/') ? path.slice(1) : path
  return `${base}${normalized}`
}

const defaultSchoolLogo = resolvePublicUrl('images/schools/default-school.jpg')

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

const onSchoolLogoError = (event) => {
  const img = event?.target
  if (!img) return
  if (img.dataset.fallbackDone === '1') return
  img.dataset.fallbackDone = '1'
  img.onerror = null
  img.src = defaultSchoolLogo
}

// 申请步骤
const steps = [
  { label: '个人信息' },
  { label: '选择学校' },
  { label: '上传材料' },
  { label: '申请进度' },
  { label: '录取结果' }
]

const currentStep = ref(0)
const completedSteps = ref([])
const submittingFiles = ref(false)

// 登录状态
const isLoggedIn = ref(false)
const userInfo = ref(null)

// 表单数据
const formData = reactive({
  // 个人信息
  name: '',
  englishName: '',
  gender: '',
  birthDate: '',
  nationality: '',
  passportNo: '',
  phone: '',
  email: '',
  wechat: '',
  currentEducation: '',
  currentSchool: '',
  currentMajor: '',
  gpaScale: '', // 新增 GPA 制度字段
  gpa: '',
  ielts: '',
  toefl: '',
  gre: '',
  gmat: '',
  studentId: null, // 添加学生ID字段
  userId: null, // 添加用户ID字段

  // 申请材料
  materials: {
    personalStatement: null, // 必填
    transcript: null, // 必填
    recommendationLetters: [], // 必填
    resume: null, // 必填
    languageScores: null, // 必填
    papers: [], // 新增：论文（选填）
    others: [], // 新增：其他材料（选填）
    portfolio: [] // 作品集（选填）
  }
})

// 国家数据
const countries = [
  { name: '美国', code: 'us', icon: '🇺🇸' },
  { name: '英国', code: 'uk', icon: '🇬🇧' },
  { name: '澳大利亚', code: 'au', icon: '🇦🇺' },
  { name: '加拿大', code: 'ca', icon: '🇨🇦' },
  { name: '新加坡', code: 'sg', icon: '🇸🇬' },
  { name: '日本', code: 'jp', icon: '🇯🇵' },
  { name: '德国', code: 'de', icon: '🇩🇪' },
  { name: '法国', code: 'fr', icon: '🇫🇷' },
  { name: '荷兰', code: 'nl', icon: '🇳🇱' },
  { name: '瑞士', code: 'ch', icon: '🇨🇭' }
]

// 专业数据
const majors = [
  { name: '计算机科学', code: 'cs' },
  { name: '商业管理', code: 'business' },
  { name: '工程学', code: 'engineering' },
  { name: '医学', code: 'medicine' },
  { name: '法学', code: 'law' },
  { name: '艺术与设计', code: 'art' },
  { name: '社会科学', code: 'social' },
  { name: '自然科学', code: 'science' },
  { name: '教育学', code: 'education' },
  { name: '传媒与通信', code: 'media' }
]

// 学校筛选
const schoolFilters = ref({
  country: 'uk', // 默认选择英国
  major: '',
  degree: '',
  ranking: ''
})

// 学校搜索关键词
const schoolSearchKeyword = ref('')

// 处理学校搜索
const handleSchoolSearch = (keyword) => {
  schoolSearchKeyword.value = keyword
}

// 重置学校筛选
const resetSchoolFilters = () => {
  schoolFilters.value = {
    country: 'uk', // 重置时也默认选择英国
    major: '',
    degree: '',
    ranking: ''
  }
  schoolSearchKeyword.value = ''
}

// 学校数据
const schools = ref([])
const loading = ref(false)
const error = ref(null)

// 详细的加载状态
const loadingStates = reactive({
  syncingStudent: false,
  loadingSchools: false,
  loadingSchoolDetails: false,
  schoolDetailsProgress: 0,
  totalSchools: 0
})

// 已上传文件列表
const uploadedFiles = ref([])

// 当前材料完成度
const currentMaterialsCompletion = ref(0)

// 录取结果相关变量
const admissionResults = ref([])
const admissionResultsLoading = ref(false)
const admissionStats = ref({
  totalOffers: 0,
  totalSchools: 0,
  nearestDeadline: ''
})

// 获取学校数据的方法 - 优化版本
const fetchSchoolData = async () => {
  try {
    const cached = schoolService.peekAllSchools()
    loading.value = !Array.isArray(cached)
    loadingStates.loadingSchools = !Array.isArray(cached)
    error.value = null

    console.log('开始获取学校数据...')
    const response = await schoolService.getAllSchoolsCached({
      maxAgeMs: 1000 * 60 * 60 * 12,
      revalidate: true
    })

    console.log('学校数据响应:', response)

    // 检查返回的数据
    if (!response || !response.data) {
      console.error('获取学校数据失败: 返回数据格式不正确', response)
      throw new Error('获取学校数据失败: 返回数据格式不正确')
    }

    // 确保数据是数组
    if (!Array.isArray(response.data)) {
      console.error('获取学校数据失败: 返回数据不是数组', response.data)
      throw new Error('获取学校数据失败: 返回数据不是数组')
    }

    if (response.status === 200 && response.data) {
      // 处理API返回的学校数据
      const processedSchools = response.data.map(school => {
        // 处理标签和学位信息
        const tags = []
        if (school.type) tags.push(school.type)
        if (school.ownership) tags.push(school.ownership)

        // 从tags字段拆分标签
        if (school.tags) {
          school.tags.split(',').forEach(tag => {
            if (tag.trim()) {
              tags.push(tag.trim())
            }
          })
        }

        // 处理学位信息
        const degrees = []
        if (school.hasUndergraduate) degrees.push('bachelor')
        if (school.hasGraduate) degrees.push('master')
        if (school.hasPhd) degrees.push('phd')

        // 处理地址信息
        let location = school.city || ''
        if (school.state && school.state !== school.city) {
          location = school.state + (location ? ' ' + location : '')
        }

        // 处理学费
        let tuition = '待定'
        if (school.tuitionFee) {
          tuition = `¥${school.tuitionFee}/年`
        }

        // 使用世界排名或国家排名
        const ranking = school.worldRanking ||
          school.nationalRanking ||
          school.qsRanking ||
          school.timesRanking ||
          999

        // 构建学校对象
        return {
          id: school.id.toString(),
          name: school.name,
          image: school.imageUrl || '/images/schools/default-school.jpg',
          location: location || '未知',
          ranking: `世界前${Math.ceil(ranking / 10) * 10}`,
          worldRanking: `第${ranking}名`,
          country: school.countryCode?.toLowerCase() ||
            mapCountryCode(school.country),
          programCount: 0, // 将在获取学院专业信息后重新计算
          facultyCount: 0, // 学院数量，将在获取学院信息后设置
          tuition: tuition,
          tags: tags.length > 0 ? tags : ['综合'],
          degrees: degrees.length > 0 ? degrees : ['bachelor', 'master', 'phd'],
          logo: normalizeSchoolLogo(school.logoUrl) || null,
          faculties: [], // 学院信息会在后续请求中填充
          detailsLoaded: false // 标记详细信息是否已加载
        }
      })

      // 立即更新学校基本数据，让用户先看到学校列表
      schools.value = processedSchools
      loadingStates.loadingSchools = false
      loadingStates.totalSchools = processedSchools.length
      console.log('学校基本信息加载完成，开始异步加载详细信息...')

      // 异步并行加载所有学校的详细信息
      loadingStates.loadingSchoolDetails = true
      await loadSchoolDetails(processedSchools)

    } else {
      throw new Error('获取学校数据失败')
    }
  } catch (error) {
    console.error('获取学校数据失败:', error)
    error.value = error.message || '获取学校数据失败'
  } finally {
    loading.value = false
    loadingStates.loadingSchools = false
    loadingStates.loadingSchoolDetails = false
  }
}

// 并行加载学校详细信息
const loadSchoolDetails = async (schoolList) => {
  const icons = ['Notebook', 'Briefcase', 'SetUp', 'FirstAidKit', 'Reading', 'House', 'Monitor']

  const extractDetailsArray = (payload) => {
    if (Array.isArray(payload)) return payload
    if (!payload) return []
    if (typeof payload === 'string') {
      try {
        return extractDetailsArray(JSON.parse(payload))
      } catch {
        return []
      }
    }
    if (typeof payload === 'object') {
      if (Array.isArray(payload.data)) return payload.data
      if (Array.isArray(payload.result)) return payload.result
      if (Array.isArray(payload.rows)) return payload.rows
      if (typeof payload.data === 'string') {
        try {
          return extractDetailsArray(JSON.parse(payload.data))
        } catch {
          return []
        }
      }
    }
    return []
  }

  const applyDetailToSchool = (school, detail, detailIndex) => {
    const facultiesRaw = Array.isArray(detail?.faculties) ? detail.faculties : []
    const faculties = facultiesRaw.map((faculty, idx) => {
      const majorsRaw = Array.isArray(faculty?.majors) ? faculty.majors : []
      const majors = majorsRaw.map((major) => ({
        ...major,
        degree: major.degree || '本科',
        duration: major.duration || '4年',
        tuition: major.tuitionFee ? `¥${major.tuitionFee}/年` : school.tuition,
        description: major.description || `${major.name}专业提供全面的理论和实践教育。`,
        requirements: major.requirements || `托福90+/雅思6.5+，GPA 3.0+`,
        features: major.features ? major.features.split(',') : ['综合教育', '实践机会', '研究项目']
      }))

      return {
        ...faculty,
        icon: icons[(detailIndex + idx) % icons.length],
        majors
      }
    })

    const totalMajorCount = Number.isFinite(detail?.totalMajorCount)
      ? detail.totalMajorCount
      : faculties.reduce((total, faculty) => total + (faculty.majors?.length || 0), 0)

    school.faculties = faculties
    school.facultyCount = faculties.length
    school.programCount = totalMajorCount
    school.detailsLoaded = true
  }

  try {
    loadingStates.schoolDetailsProgress = 0
    loadingStates.totalSchools = Array.isArray(schoolList) ? schoolList.length : 0

    const schoolIds = Array.isArray(schoolList)
      ? schoolList.map((school) => Number(school.id)).filter((id) => Number.isFinite(id))
      : []

    if (!schoolIds.length) {
      loadingStates.schoolDetailsProgress = 100
      return
    }

    const headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    }

    let details = []
    try {
      const bulkResponse = await axios.get('/api/schools/bulk-details', {
        params: { schoolIds: schoolIds.join(',') },
        withCredentials: true,
        timeout: 15000,
        headers
      })
      details = extractDetailsArray(bulkResponse?.data)
    } catch {
      details = []
    }

    if (!Array.isArray(details) || details.length === 0) {
      throw new Error('bulk-details unavailable')
    }

    let processedCount = 0

    details.forEach((detail, detailIndex) => {
      const schoolId = String(detail?.schoolId)
      const school = schoolList.find((s) => String(s.id) === schoolId)
      if (!school) return
      applyDetailToSchool(school, detail, detailIndex)
      processedCount += 1
      loadingStates.schoolDetailsProgress = Math.round((processedCount / schoolList.length) * 100)
    })

    schoolList.forEach((school) => {
      if (!school.detailsLoaded) {
        school.faculties = []
        school.facultyCount = 0
        school.programCount = 0
        school.detailsLoaded = true
        processedCount += 1
        loadingStates.schoolDetailsProgress = Math.round((processedCount / schoolList.length) * 100)
      }
    })

    loadingStates.schoolDetailsProgress = 100
  } catch (error) {
    try {
      const headers = {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }

      const fallbackSchoolIds = Array.isArray(schoolList)
        ? schoolList.map((school) => Number(school.id)).filter((id) => Number.isFinite(id))
        : []

      let nextIndex = 0
      const concurrency = 5
      let processedCount = 0

      const worker = async () => {
        while (nextIndex < fallbackSchoolIds.length) {
          const schoolId = fallbackSchoolIds[nextIndex++]
          const school = schoolList.find((s) => String(s.id) === String(schoolId))
          if (!school) continue

          try {
            const facultiesResponse = await axios.get(`/api/schools/${schoolId}/faculties`, {
              withCredentials: true,
              timeout: 15000,
              headers
            })

            const faculties = extractDetailsArray(facultiesResponse?.data)
            const totalMajorCount = faculties.reduce((total, faculty) => {
              const majorsRaw = Array.isArray(faculty?.majors) ? faculty.majors : []
              return total + majorsRaw.length
            }, 0)

            applyDetailToSchool(school, { schoolId, faculties, totalMajorCount }, processedCount)
          } catch {
            school.faculties = []
            school.facultyCount = 0
            school.programCount = 0
            school.detailsLoaded = true
          } finally {
            processedCount += 1
            loadingStates.schoolDetailsProgress = Math.round((processedCount / fallbackSchoolIds.length) * 100)
          }
        }
      }

      await Promise.all(Array.from({ length: Math.min(concurrency, fallbackSchoolIds.length) }, () => worker()))
      loadingStates.schoolDetailsProgress = 100
      return
    } catch (fallbackError) {
      console.error('加载学校详细信息时发生错误:', fallbackError)
    }

    console.error('加载学校详细信息时发生错误:', error)
  } finally {
    loadingStates.loadingSchoolDetails = false;
  }
}

// 国家代码映射函数
const mapCountryCode = (countryName) => {
  const countryMap = {
    '美国': 'us',
    '英国': 'uk',
    '澳大利亚': 'au',
    '加拿大': 'ca',
    '新加坡': 'sg',
    '日本': 'jp',
    '德国': 'de',
    '法国': 'fr',
    '荷兰': 'nl',
    '瑞士': 'ch'
  }

  return countryMap[countryName] || 'us'
}

// 筛选后的学校列表
const filteredSchools = computed(() => {
  // 开始筛选前检查数据是否已加载
  if (loading.value || schools.value.length === 0) {
    return []
  }

  return schools.value.filter(school => {
    // 搜索关键词筛选
    if (schoolSearchKeyword.value) {
      const keyword = schoolSearchKeyword.value.toLowerCase()
      const schoolName = school.name.toLowerCase()
      if (!schoolName.includes(keyword)) {
        return false
      }
    }

    // 国家筛选
    if (schoolFilters.value.country && school.country !== schoolFilters.value.country) {
      return false
    }

    // 专业方向筛选 - 需要实现
    if (schoolFilters.value.major) {
      // 未实现的专业筛选逻辑
      return true
    }

    // 学位筛选
    if (schoolFilters.value.degree && !school.degrees.includes(schoolFilters.value.degree)) {
      return false
    }

    // 排名筛选
    if (schoolFilters.value.ranking) {
      const rank = parseInt(school.worldRanking?.replace(/[^0-9]/g, '') || 999)
      if (schoolFilters.value.ranking === 'top10' && rank > 10) return false
      if (schoolFilters.value.ranking === 'top50' && rank > 50) return false
      if (schoolFilters.value.ranking === 'top100' && rank > 100) return false
      if (schoolFilters.value.ranking === 'top200' && rank > 200) return false
    }

    return true
  })
})

// 选中的学校
const selectedSchools = ref([])

// 获取国家名称
const getCountryName = (countryCode) => {
  const country = countries.find(c => c.code === countryCode)
  return country ? country.name : countryCode
}

// 处理时间线操作


// ApplicationProgress组件会自己管理申请进度相关的数据
// 这里不再需要重复定义这些变量

// ApplicationProgress组件会自己管理申请进度数据的获取
// ApplicationProgress组件会自己处理学校选择和数据获取
// 这个函数现在不再需要，因为ApplicationProgress组件有自己的选择逻辑

// ApplicationProgress组件会自己处理状态操作、通知操作和任务完成等逻辑

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const dialogContent = ref('')
const showDialogConfirm = ref(false)

// 关闭对话框
const closeDialog = () => {
  dialogVisible.value = false
  showDialogConfirm.value = false
}

// 处理对话框确认
const handleDialogConfirm = () => {
  ElMessage.success('预约成功！我们的顾问将尽快与您联系')
  dialogVisible.value = false
}

// 处理申请进度更新
const handleProgressUpdate = (progressData) => {
  console.log('申请进度更新:', progressData)
  // 可以在这里处理进度更新的逻辑，比如刷新其他相关数据
}

// 从数据库同步学生信息
const syncStudentData = async () => {
  try {
    loadingStates.syncingStudent = true
    ElMessage({
      type: 'info',
      message: '正在获取用户信息...'
    });

    // 直接获取当前用户信息
    const userResponse = await axios.get('/api/user/current');
    console.log('当前用户信息API响应:', userResponse.data);

    if (userResponse.data.code !== 200 || !userResponse.data.data) {
      ElMessage.warning('获取用户信息失败，请确保已登录');
      return;
    }

    const userData = userResponse.data.data;
    const userId = userData.id;

    if (!userId) {
      ElMessage.warning('无法获取用户ID，请确保已登录');
      return;
    }
    formData.userId = userId

    // 预先填充用户信息（邮箱、电话等基本字段）
    formData.email = userData.email || '';
    formData.phone = userData.phone || '';

    ElMessage({
      type: 'info',
      message: `正在同步ID为 ${userId} 的学生信息...`
    });

    // 使用获取到的用户ID查询students表
    const studentResponse = await axios.get(`/api/student/info/${userId}`);
    console.log('学生信息API响应:', studentResponse.data);

    if (studentResponse.data.code === 200 && studentResponse.data.data) {
      // 数据库中存在学生信息，更新表单
      const studentData = studentResponse.data.data;

      // 更新表单数据
      formData.name = studentData.name || '';
      formData.englishName = studentData.englishName || '';
      formData.gender = studentData.gender || '';
      formData.birthDate = studentData.birthDate ? new Date(studentData.birthDate) : '';
      formData.nationality = studentData.nationality || '';
      formData.passportNo = studentData.passportNo || '';
      formData.phone = studentData.phone || formData.phone || '';
      formData.email = studentData.email || formData.email || '';
      formData.wechat = studentData.wechat || '';
      formData.currentSchool = studentData.currentSchool || '';
      formData.currentMajor = studentData.major || '';

      // 获取GPA信息
      try {
        const gpaResponse = await axios.get(`/api/student/gpa/${userId}`);
        if (gpaResponse.data.code === 200 && gpaResponse.data.data) {
          const gpaData = gpaResponse.data.data;
          formData.gpa = gpaData.gpaScore ? gpaData.gpaScore.toString() : '';
          formData.gpaScale = gpaData.gpaScale || studentData.gpaScale || '4.0';
        } else {
          // 如果没有专门的GPA数据，则使用student表中的数据
          formData.gpa = studentData.gpa ? studentData.gpa.toString() : '';
          formData.gpaScale = studentData.gpaScale || '4.0';
        }
      } catch (error) {
        console.error('获取GPA信息失败:', error);
        // 使用student表中的默认GPA数据
        formData.gpa = studentData.gpa ? studentData.gpa.toString() : '';
        formData.gpaScale = studentData.gpaScale || '4.0';
      }

      ElMessage.success('学生信息同步成功！');
    } else {
      // 如果没有找到学生信息但有用户信息
      ElMessage.warning('未找到学生信息，已同步基本用户信息，请补充填写其他信息');
    }
  } catch (error) {
    console.error('同步数据失败:', error);
    console.error('错误详情:', {
      message: error.message,
      response: error.response?.data
    });
    ElMessage.error('同步学生信息失败，请稍后重试');
  } finally {
    loadingStates.syncingStudent = false;
  }
};

// 是否可以进入下一步
const canProceed = computed(() => {
  if (currentStep.value === 2) { // 材料上传步骤
    // 只检查材料完成度是否达到100%
    return currentMaterialsCompletion.value >= 100
  }
  if (currentStep.value === 0) {
    // 个人信息步骤
    return formData.name && formData.phone && formData.email && formData.currentSchool
  } else if (currentStep.value === 1) {
    // 选择学校步骤
    return selectedSchools.value.length > 0
  } else if (currentStep.value === 3) {
    // 面试准备步骤
    return true
  } else if (currentStep.value === 4) {
    // 录取结果步骤
    return true
  }

  return true
})

// 是否是最后一步
const isLastStep = computed(() => {
  return currentStep.value === steps.length - 1
})



// 下一步
const nextStep = async () => {
  if (currentStep.value < steps.length - 1) {
    // 如果是第一步（个人信息）完成，保存数据到数据库
    if (currentStep.value === 0) {
      // 显示保存中的加载状态
      const loadingMessage = ElMessage({
        message: '正在保存个人信息，请稍候...',
        type: 'info',
        duration: 0, // 不自动关闭
        showClose: false
      });

      try {
        let userId = formData.userId;
        if (!userId) {
          const userResponse = await axios.get('/api/user/current');
          if (userResponse.data.code !== 200 || !userResponse.data.data) {
            loadingMessage.close();
            ElMessage.error('获取用户信息失败，请确保已登录');
            return;
          }
          userId = userResponse.data.data.id;
          formData.userId = userId;
        }

        if (!userId) {
          loadingMessage.close();
          ElMessage.error('无法获取用户ID，请确保已登录');
          return;
        }

        // 确保日期格式正确
        let formattedBirthDate = null;
        if (formData.birthDate instanceof Date) {
          const year = formData.birthDate.getFullYear();
          const month = String(formData.birthDate.getMonth() + 1).padStart(2, '0');
          const day = String(formData.birthDate.getDate()).padStart(2, '0');
          const formattedDateString = `${year}-${month}-${day}`;

          formattedBirthDate = `${formattedDateString} 00:00:00`;
        }

        // 构建学生数据
        const studentData = {
          userId: userId,
          name: formData.name,
          englishName: formData.englishName,
          gender: formData.gender,
          birthDate: formattedBirthDate,
          nationality: formData.nationality,
          passportNo: formData.passportNo,
          phone: formData.phone,
          email: formData.email,
          wechat: formData.wechat,
          currentSchool: formData.currentSchool,
          major: formData.currentMajor,
          gpa: formData.gpa ? parseFloat(formData.gpa) : null,
          gpaScale: formData.gpaScale
        };

        console.log('准备发送的数据:', studentData);

        const syncResponse = await axios.post('/api/student/sync', studentData);
        console.log('同步学生信息响应:', syncResponse.data);

        if (syncResponse.data.code !== 200) {
          throw new Error(syncResponse.data.message || '保存失败');
        }

        loadingMessage.close();
        ElMessage.success('个人信息保存成功！正在进入学校选择页面...');
        await new Promise(resolve => setTimeout(resolve, 300));
      } catch (error) {
        console.error('保存个人信息失败:', error);
        // 关闭加载消息
        loadingMessage.close();

        let errorMsg = '未知错误';
        if (error.response && error.response.data) {
          errorMsg = error.response.data.message || '服务器返回错误: ' + error.response.status;
        } else if (error.message) {
          errorMsg = error.message;
        }
        ElMessage.error('保存个人信息失败: ' + errorMsg);
        return; // 如果保存失败，不进入下一步
      }
    }
    // 如果是选择学校步骤，确保选择的学校和专业已保存到数据库
    else if (currentStep.value === 1) {
      // 检查是否选择了学校
      if (selectedSchools.value.length === 0) {
        ElMessage.warning('请至少选择一所学校和专业后再继续');
        return;
      }

      // 检查是否所有选择都已成功保存到数据库
      const unsavedSelections = selectedSchools.value.filter(selection => !selection.applicationId);

      if (unsavedSelections.length > 0) {
        ElMessage.warning('有学校和专业尚未保存到申请表中，请等待保存完成或重新选择');

        // 尝试重新保存未保存的选择
        const savePromises = unsavedSelections.map(selection => saveApplicationToDatabase(selection));

        try {
          // 等待所有保存操作完成
          const results = await Promise.all(savePromises);

          // 检查是否所有保存操作都成功
          if (results.some(result => !result)) {
            ElMessage.error('部分学校和专业未能成功保存，请重试');
            return;
          }

          ElMessage.success('所有学校和专业已成功保存');
        } catch (error) {
          console.error('保存学校和专业时出错:', error);
          ElMessage.error('保存学校和专业失败，请重试');
          return;
        }
      }
    }
    // 如果是材料上传步骤，检查材料完成度
    else if (currentStep.value === 2) {
      // 检查是否有非selected状态的申请，如果有则直接跳转到申请进度页面
      const nonSelectedApplications = selectedSchools.value.filter(school =>
        school.applicationStatus !== 'selected'
      );

      if (nonSelectedApplications.length > 0) {
        // 有非selected状态的申请，直接跳转到申请进度页面
        ElMessage.info('检测到已处理的申请，正在跳转到申请进度页面...');
        // 将当前步骤添加到已完成步骤中
        if (!completedSteps.value.includes(currentStep.value)) {
          completedSteps.value.push(currentStep.value);
        }
        currentStep.value++;
        // 滚动到顶部
        window.scrollTo({ top: 0, behavior: 'smooth' });
        return;
      }

      // 检查材料完成度
      if (currentMaterialsCompletion.value < 100) {
        ElMessage.warning(`材料完成度仅为 ${currentMaterialsCompletion.value}%，请先完成所有必需材料的上传`);
        return;
      }

      // 材料已100%完成，显示确认对话框
      try {
        await ElMessageBox.confirm(
          '材料已完成上传，确认提交申请吗？提交后将无法修改材料。',
          '提交申请确认',
          {
            confirmButtonText: '确认提交',
            cancelButtonText: '取消',
            type: 'warning'
          }
        );

        // 用户确认后，开始提交流程
        submittingFiles.value = true;

        try {
          // 显示提交中的消息
          const loadingMessage = ElMessage({
            message: '正在提交文件请稍等...',
            type: 'info',
            duration: 0, // 不自动关闭
            showClose: false
          });

          // 获取所有selected状态的申请
          const selectedApplications = selectedSchools.value.filter(school =>
            school.applicationStatus === 'selected'
          );

          console.log('选中的申请:', selectedApplications);
          console.log('开始更新申请状态...');

          if (selectedApplications.length > 0) {
            // 更新所有selected状态的申请为submitted
            const updatePromises = selectedApplications.map(async (application) => {
              try {
                console.log(`正在更新申请 ${application.applicationId} 的状态...`);
                if (!application.applicationId) {
                  return false;
                }
                const response = await axios.put(`/api/application-school/${application.applicationId}/status?status=submitted`);
                console.log(`申请 ${application.applicationId} API响应:`, response.data);
                if (response.data.code === 200) {
                  // 更新本地状态
                  application.applicationStatus = 'submitted';
                  console.log(`申请 ${application.applicationId} 状态已更新为 submitted`);
                  return true;
                } else {
                  console.error(`申请 ${application.applicationId} 更新失败，响应码:`, response.data.code);
                  return false;
                }
              } catch (error) {
                console.error(`更新申请 ${application.applicationId} 状态失败:`, error);
                return false;
              }
            });

            const results = await Promise.all(updatePromises);
            const successCount = results.filter(result => result).length;

            // 关闭loading消息
            loadingMessage.close();

            if (successCount === selectedApplications.length) {
              ElMessage.success(`申请已成功提交！共提交了 ${successCount} 个申请`);
            } else {
              ElMessage.warning(`部分申请提交成功，成功提交 ${successCount}/${selectedApplications.length} 个申请`);
            }
          } else {
            // 关闭loading消息
            loadingMessage.close();
            ElMessage.success('申请已成功提交！');
          }

          // 状态更新完成后，才进入下一步
          // 将当前步骤添加到已完成步骤中
          if (!completedSteps.value.includes(currentStep.value)) {
            completedSteps.value.push(currentStep.value);
          }
          currentStep.value++;

          // 滚动到顶部
          window.scrollTo({ top: 0, behavior: 'smooth' });

        } catch (error) {
          console.error('提交申请时出错:', error);
          ElMessage.error('提交申请失败，请重试');
          return;
        } finally {
          submittingFiles.value = false;
        }

      } catch (error) {
        // 用户取消了提交
        if (error === 'cancel') {
          return;
        }
        console.error('提交申请时出错:', error);
        ElMessage.error('提交申请失败，请重试');
        return;
      }

      // 如果是step2且状态更新成功，直接返回，不执行后面的步骤跳转逻辑
      return;
    }

    // 将当前步骤添加到已完成步骤中
    if (!completedSteps.value.includes(currentStep.value)) {
      completedSteps.value.push(currentStep.value);
    }
    currentStep.value++;

    // 滚动到顶部
    window.scrollTo({ top: 0, behavior: 'smooth' });
  } else {
    // 最后一步，完成申请
    ElMessageBox.confirm(
      '您确定要完成申请流程吗？',
      '完成申请',
      {
        confirmButtonText: '确认完成',
        cancelButtonText: '取消',
        type: 'info'
      }
    ).then(() => {
      ElMessage.success('恭喜您完成了整个申请流程！');
      router.push('/');
    }).catch(() => { });
  }
};

// 上一步
const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

// 是否可以导航到指定步骤
const canNavigateToStep = (stepIndex) => {
  // 允许导航到当前步骤或已完成的步骤
  return stepIndex <= currentStep.value || completedSteps.value.includes(stepIndex)
}

const navigateToStep = (stepIndex) => {
  // 只允许导航到已完成的步骤或当前步骤
  if (canNavigateToStep(stepIndex)) {
    currentStep.value = stepIndex
  }
}

// 监听步骤变化，更新URL
watch(currentStep, (newStep) => {
  const url = new URL(window.location.href)
  url.searchParams.set('step', newStep)
  window.history.replaceState({}, '', url)

  // 当切换到"选择学校"步骤时，如果已有已加载的学校和专业，显示提示
  if (newStep === 1 && selectedSchools.value.length > 0) {
    ElMessage.success(`已加载${selectedSchools.value.length}个已保存的学校和专业`)
  }
})

// 初始化
onMounted(async () => {
  // 首先检查登录状态
  if (!checkLoginStatus()) {
    ElMessage.warning('请先登录后再进行留学申请')
    // 可以选择跳转到登录页面或显示登录提示
    // router.push('/login')
    return
  }

  // 从URL获取步骤
  const urlParams = new URLSearchParams(window.location.search)
  const stepParam = urlParams.get('step')

  if (stepParam !== null) {
    const step = parseInt(stepParam)
    if (!isNaN(step) && step >= 0 && step < steps.length) {
      currentStep.value = step

      // 将之前的步骤标记为已完成
      for (let i = 0; i < step; i++) {
        if (!completedSteps.value.includes(i)) {
          completedSteps.value.push(i)
        }
      }
    }
  }

  // 自动同步学生信息
  await syncStudentData()

  // 获取学校数据
  await fetchSchoolData()

  // 获取学生ID
  await getStudentId()

  // 加载用户已保存的申请数据
  await loadSavedApplications()

  // 检测申请状态并决定是否跳转
  await checkApplicationStatusAndRedirect()

  // 如果当前在材料上传步骤，获取文件列表
  if (currentStep.value === 2) {
    await fetchUploadedFiles()
  }

  // ApplicationProgress组件会自己管理申请进度数据
})

// 在 script setup 中修改和添加状态
const selectedSchool = ref(null)
const showFacultySelection = ref(false) // 控制学院选择对话框
const selectedFaculty = ref(null) // 当前选中的学院
const showMajorDetail = ref(false) // 控制专业详情对话框
const selectedMajorDetail = ref(null) // 当前查看详情的专业

// 学校选择方法
const selectSchool = (school) => {
  selectedSchool.value = school
  showFacultySelection.value = true
}

// 选择学院方法
const selectFaculty = (faculty) => {
  selectedFaculty.value = faculty
}

// 添加防止重复选择的方法
const canSelectMajor = (schoolId, majorId) => {
  return !selectedSchools.value.some(
    selection => selection.schoolId === schoolId && selection.majorId === majorId
  )
}

// 查看专业详细信息
const viewMajorDetail = (major) => {
  // 确保数据结构完整
  selectedMajorDetail.value = {
    ...major,
    degree: major.degree || '本科',
    duration: major.duration || '4年',
    tuitionFee: major.tuitionFee || major.tuition,
    requirements: major.requirements || '托福90+/雅思6.5+，GPA 3.0+'
  }
  showMajorDetail.value = true
}

// 申请该专业
const applyMajor = async (major) => {
  if (selectedSchool.value && selectedFaculty.value) {
    // 检查是否已经选择过该专业
    if (!canSelectMajor(selectedSchool.value.id, major.id)) {
      ElMessage.warning('您已经选择过该专业了')
      return
    }

    const selection = {
      schoolId: selectedSchool.value.id,
      schoolName: selectedSchool.value.name,
      facultyId: selectedFaculty.value.id,
      facultyName: selectedFaculty.value.name,
      majorId: major.id,
      majorName: major.name,
      degree: major.degree,
      duration: major.duration,
      tuition: major.tuition,
      country: getCountryName(selectedSchool.value.country),
      applicationStatus: 'selected',
      statusText: '已选择'
    }

    // 添加到已选择列表
    selectedSchools.value.push(selection)

    // 保存到数据库并等待结果
    const saveResult = await saveApplicationToDatabase(selection)

    if (!saveResult) {
      // 如果保存失败，从选择列表中移除
      const index = selectedSchools.value.indexOf(selection)
      if (index !== -1) {
        selectedSchools.value.splice(index, 1)
      }
      ElMessage.error('专业申请失败，请重试')
      return
    }

    showFacultySelection.value = false
    selectedSchool.value = null
    selectedFaculty.value = null

    ElMessage.success('专业申请成功')
  }
}

// 处理专业申请事件
const handleMajorApply = async (majorData) => {
  if (majorData) {
    await applyMajor(majorData)
    showMajorDetail.value = false
  }
}

// 处理专业咨询事件
const handleMajorConsult = (majorData) => {
  // 这里可以添加咨询逻辑，比如跳转到咨询页面或打开咨询对话框
  console.log('咨询专业:', majorData)
  ElMessage.info('咨询功能即将上线')
}

// 处理专业收藏事件
const handleMajorCollect = (majorData) => {
  // 这里可以添加收藏逻辑
  console.log('收藏专业:', majorData)
  ElMessage.success('收藏成功')
}

// 移除选中的学校
const removeSelection = async (selection) => {
  try {
    // 显示删除确认对话框
    await ElMessageBox.confirm(
      `确定要删除申请 "${selection.schoolName} - ${selection.majorName}" 吗？此操作不可逆。`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    // 如果有申请ID，先从数据库删除
    if (selection.applicationId) {
      console.log('正在删除申请记录，ID:', selection.applicationId)

      const response = await axios.delete(`/api/application-school/${selection.applicationId}`)

      if (response.data && response.data.code === 200) {
        console.log('申请记录删除成功')

        // 从已保存的申请ID列表中移除
        const savedIndex = savedApplicationIds.value.indexOf(selection.applicationId)
        if (savedIndex !== -1) {
          savedApplicationIds.value.splice(savedIndex, 1)
        }

        ElMessage.success('申请记录删除成功')
      } else {
        console.error('删除申请记录失败:', response.data)
        ElMessage.error('删除申请记录失败: ' + (response.data?.message || '未知错误'))
        return // 如果数据库删除失败，不继续删除前端数据
      }
    }

    // 从前端列表中移除
    const index = selectedSchools.value.indexOf(selection)
    if (index !== -1) {
      selectedSchools.value.splice(index, 1)
      console.log('已从前端列表中移除选择项')
    }

  } catch (error) {
    if (error === 'cancel') {
      // 用户取消删除
      console.log('用户取消删除操作')
    } else {
      console.error('删除申请时出错:', error)
      ElMessage.error('删除申请失败，请稍后重试')
    }
  }
}

// 添加保存申请到数据库的函数
const saveApplicationToDatabase = async (selection) => {
  try {
    let userId = formData.userId
    if (!userId) {
      const userResponse = await axios.get('/api/user/current')
      if (userResponse.data.code !== 200 || !userResponse.data.data) {
        ElMessage.warning('获取用户信息失败，无法保存申请记录')
        return false
      }
      userId = userResponse.data.data.id
      formData.userId = userId
    }

    // 从国家名称获取国家ID
    const countryId = getCountryId(selection.country)

    // 构建申请数据
    const applicationData = {
      userId: userId,
      schoolId: selection.schoolId,
      majorId: selection.majorId,
      countryId: countryId, // 添加countryId字段
      schoolName: selection.schoolName,
      majorName: selection.majorName,
      country: selection.country,
      major: selection.majorName,
      degree: selection.degree,
      duration: selection.duration,
      applicationStatus: 'selected',
      submitTime: new Date(),
      // 如果有学生ID，则添加
      studentId: formData.studentId || null
    }

    console.log('准备保存申请记录:', applicationData)

    // 调用API保存数据
    const response = await axios.post('/api/application-school/submit', applicationData)

    if (response.data && response.data.code === 200) {
      console.log('申请记录已保存:', response.data)

      // 为选择项添加ID，便于后续操作
      if (response.data.data && response.data.data.id) {
        const applicationId = response.data.data.id
        selection.applicationId = applicationId
        // 记录已保存的申请ID
        savedApplicationIds.value.push(applicationId)
      }

      ElMessage.success('申请记录已保存')
      return true
    } else {
      console.error('保存申请记录失败:', response.data)
      ElMessage.error('保存申请记录失败: ' + (response.data?.message || '未知错误'))
      return false
    }
  } catch (error) {
    console.error('保存申请记录出错:', error)
    ElMessage.error('保存申请记录出错: ' + (error.message || '未知错误'))
    return false
  }
}

// 检查登录状态
const checkLoginStatus = () => {
  isLoggedIn.value = sessionManager.isLoggedIn()
  userInfo.value = sessionManager.getUserInfo()

  if (!isLoggedIn.value) {
    console.log('用户未登录')
    return false
  }

  console.log('用户已登录:', userInfo.value)
  return true
}

// 添加获取学生ID的函数(在组件挂载时获取)
const getStudentId = async () => {
  try {
    // 首先检查登录状态
    if (!checkLoginStatus()) {
      console.log('用户未登录，无法获取学生ID')
      return null
    }

    const userResponse = await axios.get('/api/user/current')
    if (userResponse.data.code === 200 && userResponse.data.data) {
      const userId = userResponse.data.data.id

      // 设置用户ID
      formData.userId = userId
      console.log('已获取用户ID:', formData.userId)

      // 获取该用户关联的学生信息
      const studentResponse = await axios.get(`/api/student/info/${userId}`)
      if (studentResponse.data.code === 200 && studentResponse.data.data) {
        formData.studentId = studentResponse.data.data.id
        console.log('已获取学生ID:', formData.studentId)
        return formData.studentId // 返回学生ID
      }
    }
    return null // 如果获取失败，返回null
  } catch (error) {
    console.error('获取用户ID和学生ID失败:', error)
    return null // 如果出错，返回null
  }
}

// 添加一个新的响应式变量，用于跟踪选择的学校是否已保存到数据库
const savedApplicationIds = ref([])

// 添加国家ID映射函数
const getCountryId = (countryName) => {
  // 国家名称到ID的映射表
  const countryMap = {
    '美国': 1,
    '英国': 2,
    '澳大利亚': 3,
    '加拿大': 4,
    '新加坡': 5,
    '日本': 6,
    '德国': 7,
    '法国': 8,
    '荷兰': 9,
    '瑞士': 10
  };

  return countryMap[countryName] || 1; // 如果找不到映射，默认返回1(美国)
}

// 状态代码转换为中文显示文本
const getStatusText = (status) => {
  const statusMap = {
    'selected': '已选择',
    'submitted': '已提交',
    'under_review': '审核中',
    'interview': '面试中',
    'admitted': '已录取',
    'rejected': '已拒绝',
    'visa_processing': '签证申请中',
    'visa_approved': '签证已批准',
    'visa_rejected': '签证被拒',
    'enrolled': '已入学'
  }
  return statusMap[status] || '未知状态'
}

// 根据状态返回标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    'selected': 'info',
    'submitted': 'warning',
    'under_review': 'primary',
    'interview': 'primary',
    'admitted': 'success',
    'rejected': 'danger',
    'visa_processing': 'primary',
    'visa_approved': 'success',
    'visa_rejected': 'danger',
    'enrolled': 'success'
  }
  return typeMap[status] || 'info'
}

// 加载已保存申请数据的函数
const loadSavedApplications = async () => {
  try {
    console.log('正在加载已保存的申请数据...')
    // 获取当前用户信息
    const userResponse = await axios.get('/api/user/current')
    if (userResponse.data.code !== 200 || !userResponse.data.data) {
      console.error('获取用户信息失败，无法加载已保存的申请')
      return
    }

    const userId = userResponse.data.data.id
    // 从API获取用户的申请记录
    const response = await axios.get(`/api/application-school/list?userId=${userId}`)

    if (response.data.code === 200 && response.data.data) {
      // 处理返回的申请记录
      const applications = response.data.data
      console.log('获取到已保存的申请记录:', applications)

      // 清空当前选择列表，防止重复
      selectedSchools.value = []

      // 将已保存的申请记录添加到选择列表中
      applications.forEach(app => {
        // 显示所有状态的申请记录
        const selection = {
          applicationId: app.id,  // 保存申请ID，方便后续操作
          schoolId: app.schoolId,
          schoolName: app.schoolName,
          majorId: app.majorId,
          majorName: app.majorName,
          country: app.country,
          degree: app.degree,
          duration: app.duration,
          // 其他可能需要的字段
          facultyId: app.facultyId || 0,
          facultyName: app.facultyName || '未知学院',
          tuition: app.tuition || '未知',
          // 添加申请状态信息
          applicationStatus: app.applicationStatus,
          statusText: getStatusText(app.applicationStatus)
        }

        selectedSchools.value.push(selection)
        // 将申请ID添加到已保存列表
        if (!savedApplicationIds.value.includes(app.id)) {
          savedApplicationIds.value.push(app.id)
        }
      })

      console.log('已加载选择的学校和专业:', selectedSchools.value)

      // 仅当用户处于"选择学校"步骤时才显示消息
      if (selectedSchools.value.length > 0 && currentStep.value === 1) {
        ElMessage.success(`已加载${selectedSchools.value.length}个已保存的学校和专业`)
      }
    }
  } catch (error) {
    console.error('加载已保存的申请数据失败:', error)
  }
}

// 检测申请状态并决定是否跳转
const checkApplicationStatusAndRedirect = async () => {
  try {
    console.log('正在检测申请状态...')

    // 首先检查登录状态
    if (!checkLoginStatus()) {
      console.log('用户未登录，跳过申请状态检测')
      return
    }

    // 获取当前用户信息
    const userResponse = await axios.get('/api/user/current')
    if (userResponse.data.code !== 200 || !userResponse.data.data) {
      console.error('获取用户信息失败，无法检测申请状态')
      ElMessage.warning('获取用户信息失败，请确保已登录')
      return
    }

    const userId = userResponse.data.data.id
    // 从API获取用户的所有申请记录
    const response = await axios.get(`/api/application-school/list?userId=${userId}`)

    if (response.data.code === 200 && response.data.data) {
      const applications = response.data.data
      console.log('检测到的申请记录:', applications)

      if (applications.length === 0) {
        // 没有申请记录，保持在当前页面
        return
      }

      // 统计不同状态的申请
      const submittedApps = applications.filter(app => app.applicationStatus === 'submitted')
      const otherStatusApps = applications.filter(app => app.applicationStatus !== 'submitted')

      console.log('状态统计:', {
        submitted: submittedApps.length,
        other: otherStatusApps.length
      })

      // 如果所有申请都是submitted状态，直接跳转到申请进度页面
      if (submittedApps.length > 0 && otherStatusApps.length === 0) {
        console.log('所有申请都已提交，跳转到申请进度页面')
        ElMessage.info('您的所有申请都已提交，正在跳转到申请进度页面...')
        // 跳转到申请进度步骤（步骤3，索引为3）
        currentStep.value = 3
        // 标记前面的步骤为已完成
        completedSteps.value = [0, 1, 2]
        // 更新URL
        const url = new URL(window.location)
        url.searchParams.set('step', '3')
        window.history.pushState({}, '', url)
        return
      }

    }
  } catch (error) {
    console.error('检测申请状态失败:', error)
  }
}

const getSelectedApplicationId = () => {
  // 这里应该返回当前选择的申请ID
  // 你可以使用selectedSchools.value中的某个属性来获取这个ID
  // 例如，selectedSchools.value[0]?.applicationId
  return selectedSchools.value[0]?.applicationId || null;
}

// 获取已上传文件列表
const fetchUploadedFiles = async (options = {}) => {
  try {
    const overrideStudentId = options?.studentId ?? null
    const overrideApplicationId = options?.applicationId ?? null

    const studentId = overrideStudentId ?? await getStudentId()
    const applicationId = overrideApplicationId ?? getSelectedApplicationId()

    if (!studentId || !applicationId) {
      console.log('缺少学生ID或申请ID，无法获取文件列表')
      return
    }

    const response = await axios.get(`/api/files/application/materials/student/${studentId}/application-school/${applicationId}`)

    const payload = response?.data?.data ?? response?.data
    const files = Array.isArray(payload)
      ? payload
      : payload && typeof payload === 'object'
        ? Object.values(payload).flat().filter(Boolean)
        : []

    uploadedFiles.value = files
    console.log('获取到已上传文件:', uploadedFiles.value)
  } catch (error) {
    console.error('获取文件列表出错:', error)
  }
}

const handleMaterialsRefreshFiles = async (payload) => {
  await fetchUploadedFiles({
    studentId: payload?.studentId,
    applicationId: payload?.applicationId
  })
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取文件类型标签
const getFileTypeLabel = (description) => {
  const typeMap = {
    passport: '护照扫描件',
    idCard: '身份证扫描件',
    personalStatement: '个人陈述',
    transcript: '成绩单',
    recommendationLetters: '推荐信',
    resume: '简历',
    languageScores: '语言成绩',
    portfolio: '作品集',
    papers: '研究论文',
    others: '其他材料',
    personal_statement: '个人陈述',
    recommendation_letter: '推荐信',
    language_scores: '语言成绩',
    id_card: '身份证扫描件',
    other: '其他材料'
  }

  const raw = String(description || '').trim()
  const normalized = raw.startsWith('申请材料:') ? raw.slice('申请材料:'.length).trim() : raw
  return typeMap[normalized] || normalized || '未知类型'
}

// 判断文件是否可预览
const isPreviewable = (fileType) => {
  const previewableTypes = ['.pdf', '.jpg', '.jpeg', '.png', '.gif', '.txt']
  const raw = String(fileType || '').toLowerCase()
  const ext = raw && raw.startsWith('.') ? raw : raw ? `.${raw}` : ''
  return previewableTypes.includes(ext)
}

// 预览文件
const previewFile = async (file) => {
  try {
    const studentId = await getStudentId()
    const applicationId = getSelectedApplicationId()

    if (!studentId || !applicationId) {
      ElMessage.error('缺少必要参数，无法预览文件')
      return
    }

    const previewUrl = `/api/files/download/${studentId}/${applicationId}/${file.fileName}?download=false`

    // 在新窗口中打开预览
    window.open(previewUrl, '_blank')
  } catch (error) {
    console.error('预览文件失败:', error)
    ElMessage.error('预览文件失败')
  }
}

// 下载文件
const downloadFile = async (file) => {
  try {
    const studentId = await getStudentId()
    const applicationId = getSelectedApplicationId()

    if (!studentId || !applicationId) {
      ElMessage.error('缺少必要参数，无法下载文件')
      return
    }

    const downloadUrl = `/api/files/download/${studentId}/${applicationId}/${file.fileName}?download=true`

    // 创建下载链接
    const link = document.createElement('a')
    link.href = downloadUrl
    link.download = file.originalName
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)

    ElMessage.success('文件下载开始')
  } catch (error) {
    console.error('下载文件失败:', error)
    ElMessage.error('下载文件失败')
  }
}

// 删除文件
const deleteFile = async (file) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除文件 "${file.originalName}" 吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    const studentId = await getStudentId()
    const applicationId = getSelectedApplicationId()

    if (!studentId || !applicationId) {
      ElMessage.error('缺少必要参数，无法删除文件')
      return
    }

    const response = await axios.delete(`/api/files/delete/${file.id}`)

    if (response.data.code === 200) {
      ElMessage.success('文件删除成功')
      // 重新获取文件列表
      await fetchUploadedFiles()
    } else {
      ElMessage.error(response.data.message || '删除文件失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除文件失败:', error)
      ElMessage.error('删除文件失败')
    }
  }
}

// 监听材料完成度变化，重新获取文件列表
const handleMaterialsCompletionChange = async (newCompletion) => {
  console.log('材料完成度变化:', newCompletion)
  // 更新当前材料完成度
  currentMaterialsCompletion.value = newCompletion
  // 重新获取文件列表
  await fetchUploadedFiles()
}



// 获取录取结果数据
const fetchAdmissionResults = async () => {
  try {
    admissionResultsLoading.value = true
    console.log('正在获取录取结果...')

    // 获取当前用户信息
    const userResponse = await axios.get('/api/user/current')
    if (userResponse.data.code !== 200 || !userResponse.data.data) {
      console.error('获取用户信息失败，无法获取录取结果')
      return
    }

    const userId = userResponse.data.data.id

    // 获取用户的申请记录，筛选enrolled状态
    const response = await axios.get('/api/application-school/available', {
      params: { userId: userId }
    })

    console.log('录取结果API响应:', response.data)

    if (response.data && response.data.code === 200 && response.data.data) {
      // 筛选出enrolled状态的申请
      const enrolledApplications = response.data.data.filter(app =>
        app.applicationStatus === 'enrolled'
      )

      // 转换数据格式以匹配模板
      admissionResults.value = enrolledApplications.map(app => ({
        applicationId: app.id,
        schoolName: app.schoolName,
        majorName: app.programName,
        applicationStatus: app.applicationStatus
      }))

      // 更新统计信息
      admissionStats.value = {
        totalOffers: enrolledApplications.length,
        totalSchools: new Set(enrolledApplications.map(app => app.schoolName)).size,
        nearestDeadline: '待确定'
      }

      console.log('获取到录取结果:', admissionResults.value)

      if (enrolledApplications.length > 0) {
        ElMessage.success(`获取到 ${enrolledApplications.length} 个录取结果`)
      }
    } else {
      console.log('没有找到录取结果')
      admissionResults.value = []
      admissionStats.value = {
        totalOffers: 0,
        totalSchools: 0,
        nearestDeadline: ''
      }
    }
  } catch (error) {
    console.error('获取录取结果失败:', error)
    ElMessage.error('获取录取结果失败，请稍后重试')
    admissionResults.value = []
  } finally {
    admissionResultsLoading.value = false
  }
}

// 监听步骤变化，在进入材料上传步骤时获取文件列表，在进入录取结果步骤时获取录取数据
watch(currentStep, async (newStep) => {
  if (newStep === 2) {
    await fetchUploadedFiles()
  } else if (newStep === 4) {
    // 进入录取结果步骤时获取录取数据
    await fetchAdmissionResults()
  }
})
</script>

<style scoped>
/* 全局样式变量 */
.application-process {
  --primary-color: #C5A059;
  --primary-hover: #B08D45;
  --secondary-color: #F9F8F4;
  --dark-color: #1c1917;
  --light-color: #F9F8F4;
  --gray-color: #78716c;
  --success-color: #4cd964;
  --warning-color: #ff9500;
  --danger-color: #ff3b30;
  --border-radius: 12px;
  --transition-speed: 0.3s;

  /* Element Plus Override Variables */
  --el-color-primary: var(--primary-color);
  --el-color-primary-light-3: #d6b87a;
  --el-color-primary-light-5: #e1c89b;
  --el-color-primary-light-7: #ebd8bc;
  --el-color-primary-light-9: #f6e9dd;
  --el-color-primary-dark-2: #9e8047;
}

/* 容器样式 */
.application-process {
  min-height: 100vh;
  /* 移除 padding-top，让 hero 区域顶到顶部，NavBar 会浮在上面 */
  padding-top: 0;
  background-color: #F9F8F4;
  color: var(--dark-color);
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
}

/* 主标题区域 */
.hero-section {
  background: #F9F8F4;
  background-image: none;
  color: #1d1d1f;
  padding: 5rem 1rem 1rem;
  /* 增加顶部 padding 以避开 NavBar */
  text-align: center;
  position: relative;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.section-label {
  font-size: 0.875rem;
  font-weight: 600;
  color: #86868b;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  margin-bottom: 0.5rem;
}

.hero-title {
  font-family: serif;
  font-size: 3rem;
  font-weight: 500;
  margin-bottom: 1.5rem;
  color: #1d1d1f;
  line-height: 1.2;
}

.gold-separator {
  width: 60px;
  height: 4px;
  background-color: #C5A059;
  margin: 0 auto 2rem;
  border-radius: 2px;
}

.hero-subtitle {
  font-size: 1.25rem;
  color: #86868b;
  font-weight: 400;
  max-width: 600px;
  margin: 0 auto;
  font-style: normal;
  /* Country.vue 没有 italic */
  line-height: 1.6;
}

/* 进度指示器 - 全新设计 */
.progress-section {
  background-color: white;
  padding: 1.5rem 1rem;
  border-bottom: 1px solid #f0f0f0;
  position: sticky;
  top: 80px;
  /* 调整为 80px 以适应 NavBar 高度 */
  z-index: 10;
}

.progress-container {
  max-width: 1200px;
  margin: 0 auto;
}

.progress-steps {
  display: flex;
  justify-content: space-between;
  position: relative;
  margin: 0 auto;
  padding: 0 40px;
}

/* Background line */
.progress-steps::before {
  content: '';
  position: absolute;
  top: 20px;
  left: 40px;
  /* 修改从50px到40px */
  right: 40px;
  /* 修改从50px到40px */
  height: 2px;
  background-color: #E5E7EB;
  z-index: 1;
}

/* Active progress line */
.progress-steps::after {
  content: '';
  position: absolute;
  top: 20px;
  left: 40px;
  /* 修改从50px到40px */
  height: 2px;
  background-color: var(--primary-color);
  z-index: 2;
  transition: width 0.5s ease;
}

.progress-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  z-index: 3;
  flex: 1;
}

.step-indicator {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  background-color: #E5E7EB;
  color: #9CA3AF;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.progress-step.active .step-indicator {
  background-color: var(--primary-color);
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(197, 160, 89, 0.3);
}

.progress-step.completed .step-indicator {
  background-color: var(--primary-color);
  color: white;
  cursor: pointer;
}

.progress-step.completed:hover .step-indicator {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(197, 160, 89, 0.3);
}

.step-check {
  color: white;
}

.step-label {
  font-size: 14px;
  color: #6B7280;
  transition: all 0.3s ease;
  text-align: center;
  margin-top: 4px;
  font-weight: 500;
}

.progress-step.active .step-label {
  color: var(--primary-color);
  font-weight: 600;
}

.progress-step.clickable {
  cursor: pointer;
}

.progress-step.clickable:hover .step-indicator {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(197, 160, 89, 0.2);
}

/* Progress line widths */
.progress-steps[data-step="0"]::after {
  width: 0;
}

.progress-steps[data-step="1"]::after {
  width: calc((100% - 80px) * 0.25);
}

/* 5步进度条计算方式 */
.progress-steps[data-step="2"]::after {
  width: calc((100% - 80px) * 0.5);
}

/* 5步进度条计算方式 */
.progress-steps[data-step="3"]::after {
  width: calc((100% - 80px) * 0.75);
}

/* 5步进度条计算方式 */
.progress-steps[data-step="4"]::after {
  width: calc(100% - 80px);
}

/* 5步进度条计算方式 */

/* 表单区域 */
.form-section {
  padding: 2rem 1rem 4rem;
}

.form-container {
  max-width: 1200px;
  margin: 0 auto;
  background-color: white;
  border-radius: var(--border-radius);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  padding: 2rem;
}

.form-step {
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.step-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 2rem;
}

.step-title {
  font-size: 1.75rem;
  font-weight: 600;
  margin-top: -0.5rem;
  margin-bottom: 0.5rem;
  color: var(--dark-color);
}

.step-description {
  font-size: 1rem;
  color: var(--gray-color);
  margin-bottom: 0;
}

/* 个人信息表单样式 */
.form-content {
  padding: 24px;
  background-color: #fff;
  border-radius: 8px;
}

.form-columns {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
}

.form-column {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  color: #606266;
}

.form-label.required::before {
  content: '*';
  color: #f56c6c;
  margin-right: 4px;
}

/* 输入框统一样式 */
:deep(.el-input__inner) {
  height: 40px;
  border-radius: 4px;
}

:deep(.el-select) {
  width: 100%;
}

:deep(.el-date-picker) {
  width: 100%;
}

.gender-options {
  display: flex;
  gap: 24px;
  padding-top: 6px;
}

.language-scores {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 16px;
  margin-top: 8px;
}

.language-score-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.language-label {
  font-size: 14px;
  color: #666;
  width: 50px;
}

/* 同步数据区域样式 */
.sync-data-section {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
  text-align: center;
}

.sync-data-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  font-size: 14px;
  background-color: var(--primary-color);
  border-color: var(--primary-color);
}

.sync-data-button:hover {
  background-color: var(--primary-hover);
  border-color: var(--primary-hover);
}

.sync-data-button .el-icon {
  font-size: 16px;
}

.sync-data-info {
  margin-top: 8px;
  font-size: 12px;
  color: #6b7280;
}



/* 学校筛选样式 */
.school-filters {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
  background-color: #f9f9f9;
  border-radius: 12px;
  padding: 20px;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.filter-select {
  width: 100%;
}

/* 确保搜索框与其他筛选器宽度一致 */
.filter-group .el-input.filter-select {
  width: 100%;
}

.filter-group .el-input.filter-select .el-input__wrapper {
  width: 100%;
}

.country-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.country-flag {
  font-size: 18px;
}

/* 学校列表样式 */
.schools-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  /* 一行显示2个卡片 */
  gap: 16px;
  margin-bottom: 30px;
}

/* 横条格式学校卡片样式 */
.school-card-horizontal {
  background-color: white;
  border-radius: 8px;
  /* 缩小圆角 */
  padding: 8px 12px;
  /* 进一步缩小内边距，减少上下空白 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 2px solid #e0e0e0;
  /* 加强默认边框对比度 */
  display: flex;
  align-items: center;
  gap: 10px;
  /* 进一步缩小间距 */
  min-height: 60px;
  /* 进一步缩小最小高度 */
}

.school-card-horizontal:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border-color: #b0b0b0;
  /* 加强悬停时边框对比度 */
}

.school-card-horizontal.selected {
  border-color: var(--primary-color);
  background-color: #f6e9dd;
}

.school-logo {
  width: 60px;
  /* 缩小logo尺寸 */
  height: 60px;
  /* 缩小logo尺寸 */
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f8f9fa;
  border-radius: 6px;
  /* 缩小圆角 */
  overflow: hidden;
}

.school-logo-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 6px;
  /* 缩小圆角 */
}

.school-basic-info {
  flex: 0 0 200px;
  /* 缩小基本信息区域宽度 */
  display: flex;
  flex-direction: column;
  gap: 6px;
  /* 缩小间距 */
}

.school-name-section {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.school-name-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  /* 学校名在左，QS排名在右 */
  gap: 8px;
  min-height: 24px;
  /* 固定高度确保对齐 */
}

.school-name {
  font-size: 16px;
  /* 缩小字体 */
  font-weight: 600;
  color: #333;
  margin: 0;
  line-height: 1.3;
  flex: 1;
  /* 占据剩余空间 */
  min-width: 0;
  /* 允许文本截断 */
  white-space: nowrap;
  /* 强制不换行 */
  overflow: hidden;
  /* 隐藏溢出内容 */
  text-overflow: ellipsis;
  /* 显示省略号 */
}

.school-location {
  font-size: 12px;
  /* 缩小字体 */
  color: #666;
  margin: 0;
  line-height: 1.4;
}

.qs-ranking {
  display: inline-flex;
  align-items: center;
  background-color: #ffd700;
  color: #333;
  padding: 3px 8px;
  /* 缩小内边距 */
  border-radius: 12px;
  /* 缩小圆角 */
  font-weight: 600;
  font-size: 11px;
  /* 缩小字体 */
  gap: 3px;
  /* 缩小间距 */
  width: fit-content;
  flex-shrink: 0;
  /* 防止QS排名被压缩 */
  min-width: 50px;
  /* 确保最小宽度，保持对齐 */
  justify-content: center;
  /* 内容居中 */
}

.qs-label {
  font-size: 10px;
  /* 缩小字体 */
}

.qs-number {
  font-size: 12px;
  /* 缩小字体 */
  font-weight: 700;
}

.school-details-horizontal {
  flex: 1;
  display: flex;
  gap: 4px;
  /* 再次缩小间距 */
  align-items: center;
  min-width: 0;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 1px;
  /* 再次缩小间距 */
  min-width: 35px;
  /* 再次缩小最小宽度 */
  flex: 1;
}

.detail-label {
  color: #666;
  font-weight: 500;
  font-size: 10px;
  /* 再次缩小字体 */
}

.detail-value {
  color: #333;
  font-weight: 600;
  font-size: 11px;
  /* 再次缩小字体 */
}

.school-image-container {
  height: 180px;
  position: relative;
  overflow: hidden;
}

.school-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.school-card:hover .school-image {
  transform: scale(1.05);
}

.school-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 15px;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8), transparent);
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.school-ranking {
  color: white;
  font-size: 14px;
  font-weight: 600;
  padding: 4px 10px;
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 20px;
}

.school-country {
  color: white;
  font-size: 14px;
  font-weight: 500;
}

.school-content {
  padding: 20px;
}

.school-name {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 4px;
}

.school-location {
  font-size: 14px;
  color: #86868b;
  margin-bottom: 15px;
}

.school-stats {
  margin-bottom: 15px;
}

.school-stat {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 14px;
  color: #666;
}

.school-stat .el-icon {
  color: var(--primary-color);
  font-size: 16px;
}

.school-selection-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px 0;
  background-color: var(--el-color-primary-light-9);
  border-radius: 8px;
  color: var(--primary-color);
  font-weight: 500;
  transition: all 0.3s ease;
}

.selected .school-selection-indicator {
  background-color: var(--primary-color);
  color: white;
}

.no-schools-found {
  text-align: center;
  padding: 40px;
  background-color: #f9f9f9;
  border-radius: 12px;
  margin-bottom: 30px;
}

.no-schools-found .el-icon {
  font-size: 48px;
  color: #d1d5db;
  margin-bottom: 16px;
}

.selected-schools-summary {
  background-color: #f6e9dd;
  border: 1px solid #ebd8bc;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
}

.selected-schools-summary h3 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 15px;
  color: #1d1d1f;
}

.selected-schools-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.selected-school-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  background-color: var(--el-color-primary-light-9);
  border-radius: 20px;
  padding: 6px 12px;
  font-size: 14px;
  color: var(--primary-color);
}

.school-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.school-name {
  flex: 1;
}

.status-tag {
  margin-left: 8px;
}

/* 材料上传样式 */
.materials-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 24px;
  margin-bottom: 30px;
}

.material-card {
  background-color: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  border: 1px solid #eee;
  position: relative;
  /* 添加定位以支持必填标记 */
}

/* 添加必填标记样式 */
.material-card.required::before {
  content: '*';
  position: absolute;
  top: 24px;
  right: 24px;
  color: #f56c6c;
  font-size: 20px;
}

.material-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.material-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
}

.material-status {
  font-size: 14px;
  padding: 4px 10px;
  border-radius: 20px;
  background-color: #f3f4f6;
  color: #6b7280;
  display: flex;
  align-items: center;
  gap: 6px;
}

.material-status.completed {
  background-color: #d1fae5;
  color: #065f46;
}

.material-status.required {
  background-color: #fef2f2;
  color: #dc2626;
}

.material-description {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 20px;
  line-height: 1.5;
}

.upload-tip {
  font-size: 12px;
  color: #6b7280;
  margin-top: 8px;
}

.materials-progress {
  background-color: #f9f9f9;
  border-radius: 12px;
  padding: 24px;
  margin-top: 40px;
}





.resource-card {
  background-color: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  border: 1px solid #eee;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

/* 资源卡片样式 */
.resource-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background-color: #f6e9dd;
  display: flex;
  align-items: center;
  justify-content: center;
}

.resource-icon .el-icon {
  font-size: 24px;
  color: var(--primary-color);
}

.resource-content h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 8px;
}

.resource-content p {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 15px;
  line-height: 1.5;
}

.step-navigation {
  display: flex;
  justify-content: space-between;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #eee;
}

.step-navigation .el-button {
  min-width: 100px;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid #eee;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .form-container {
    padding: 1.5rem;
  }

  .form-row {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .school-filters {
    grid-template-columns: 1fr;
  }

  .schools-grid {
    grid-template-columns: 1fr;
  }

  .materials-grid {
    grid-template-columns: 1fr;
  }

  .resources-grid {
    grid-template-columns: 1fr;
  }

  .status-table-header,
  .status-table-row {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .status-table-header {
    display: none;
  }

  .status-cell {
    padding: 4px 0;
  }

  .admission-stats {
    grid-template-columns: 1fr 1fr;
  }

  .admissions-grid {
    grid-template-columns: 1fr;
  }

  .eligibility-requirements {
    grid-template-columns: 1fr;
  }

  .step-label {
    font-size: 0.75rem;
  }

  .progress-step {
    padding: 0 0.25rem;
  }
}

/* 专业选择对话框样式 */
.faculty-selection {
  display: grid;
  grid-template-columns: 350px 1fr;
  height: 70vh;
  background-color: #f8fafc;
}

.faculty-list {
  background-color: #fff;
  border-right: 1px solid #e5e7eb;
  overflow-y: auto;
  padding: 20px;
}

.faculty-item {
  display: flex;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 8px;
  background-color: #fff;
  border: 1px solid #e5e7eb;
}

.faculty-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border-color: var(--primary-color);
}

.faculty-item.active {
  background-color: #eef2ff;
  border-color: var(--primary-color);
}

.faculty-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background-color: #f0f7ff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary-color);
  flex-shrink: 0;
}

.faculty-icon .el-icon {
  font-size: 18px;
}

.faculty-info {
  flex: 1;
  display: flex;
  align-items: center;
}

.faculty-name {
  font-size: 14px;
  font-weight: 500;
  color: #1d1d1f;
  margin: 0;
}

.faculty-description {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.5;
  margin-bottom: 8px;
}

.faculty-major-count {
  font-size: 12px;
  color: var(--primary-color);
  background-color: #eef2ff;
  padding: 2px 8px;
  border-radius: 12px;
}

.majors-container {
  padding: 24px;
  overflow-y: auto;
}

.majors-title {
  font-size: 20px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.majors-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.major-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.3s ease;
  border: 1px solid #e5e7eb;
}

.major-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border-color: var(--primary-color);
}

.major-content {
  padding: 0;
}

.major-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.major-name {
  font-size: 15px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0;
}

.major-description {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.4;
  margin: 0 0 16px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.major-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.major-details {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.major-detail {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #6b7280;
  font-size: 14px;
}

.no-faculty-selected {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  background-color: #fff;
}

.major-features {
  margin: 16px 0;
}

.features-title {
  font-size: 14px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 8px;
}

.features-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.feature-tag {
  background-color: #f0f7ff;
  border-color: #e1ecfe;
}

.major-requirements {
  margin: 16px 0;
  padding: 12px;
  background-color: #f9fafb;
  border-radius: 8px;
}

.requirements-title {
  font-size: 14px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 8px;
}

.requirements-text {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.5;
}

/* 当专业已被选择时的样式 */
.major-card.selected {
  opacity: 0.7;
  cursor: not-allowed;
  background-color: #f6e9dd;
  /* 添加淡背景色作为选中标识 */
  border-color: rgba(65, 113, 247, 0.2);
  /* 降低边框颜色透明度 */
}

.major-card.selected:hover {
  transform: none;
  box-shadow: none;
}

/* GPA 输入组样式 */
.gpa-input-group {
  display: flex;
  gap: 12px;
  align-items: center;
}

.gpa-input-group .el-select {
  flex-shrink: 0;
}

.gpa-input-group .el-input {
  flex: 1;
}

/* 必填项样式 */
.form-label.required::before {
  content: '*';
  color: #f56c6c;
  margin-right: 4px;
}

/* 加载状态、错误状态和空状态样式 */
.loading-state {
  min-height: 400px;
  padding: 2rem;
  background-color: rgba(255, 255, 255, 0.5);
  border-radius: var(--border-radius);
}

.loading-state .el-skeleton {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 2rem;
}

.error-state {
  min-height: 200px;
  padding: 2rem;
  display: flex;
  justify-content: center;
  align-items: center;
}

.error-state .el-alert {
  width: 100%;
  max-width: 600px;
}

.empty-state {
  min-height: 300px;
  padding: 2rem;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgba(245, 247, 250, 0.5);
  border-radius: var(--border-radius);
}

/* 已上传文件列表样式 */
.uploaded-files-section {
  margin-top: 32px;
  padding: 24px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.uploaded-files-section h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  display: flex;
  align-items: center;
  gap: 8px;
}

.uploaded-files-section h3::before {
  content: '';
  width: 4px;
  height: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

.files-grid {
  display: grid;
  gap: 16px;
}

.file-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.file-item:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.file-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.file-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
}

.file-details {
  flex: 1;
}

.file-name {
  font-weight: 500;
  color: #1f2937;
  margin-bottom: 4px;
  word-break: break-all;
}

.file-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #6b7280;
}

.file-type {
  background: #e0e7ff;
  color: #3730a3;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 500;
}

.file-size {
  color: #059669;
}

.file-date {
  color: #6b7280;
}

.file-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.file-actions .el-button {
  padding: 6px 12px;
  font-size: 12px;
  border-radius: 6px;
}

.file-actions .el-button .el-icon {
  margin-right: 4px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .file-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .file-actions {
    width: 100%;
    justify-content: flex-end;
  }

  .file-meta {
    flex-wrap: wrap;
  }
}

/* 加载状态样式 */
.loading-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem 1rem;
  color: var(--gray-color);
  font-size: 1rem;
}

.loading-placeholder .el-icon {
  font-size: 2rem;
  margin-bottom: 1rem;
  color: var(--primary-color);
}

/* 新的加载状态样式 */
.loading-message {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 20px;
  font-size: 16px;
  color: #606266;
}

.loading-message .el-icon {
  font-size: 20px;
  color: var(--primary-color);
}

.loading-details-state {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 20px;
}

.loading-progress {
  max-width: 600px;
  margin: 0 auto;
}

.progress-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 20px;
  font-size: 16px;
  color: #606266;
}

.progress-header .el-icon {
  font-size: 20px;
  color: var(--primary-color);
}

.progress-text {
  text-align: center;
  margin-top: 12px;
  font-size: 14px;
  color: #909399;
}

/* 录取结果样式 */
.admission-results {
  margin-bottom: 16px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 20px;
  text-align: left;
}

/* 录取学校列表样式 */
.admission-schools-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.admission-school-item {
  display: inline-flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 20px;
  background-color: #f0f9ff;
  border-radius: 12px;
  transition: all 0.3s ease;
  border: 2px solid #e0f2fe;
  width: fit-content;
  min-width: 350px;
  max-width: 100%;
  min-height: 68px;
}

.admission-school-item:hover {
  background-color: #e0f2fe;
  transform: translateY(-1px);
  border-color: #0ea5e9;
}

.admission-school-item .school-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.admission-school-item .school-details {
  flex: 1;
  min-width: 0;
}

.admission-school-item .school-name {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.3;
}

.admission-school-item .school-major {
  font-size: 14px;
  font-weight: 400;
  color: #6b7280;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-top: 4px;
  line-height: 1.3;
}

.admission-school-item .status-tag {
  margin-left: 12px;
  flex-shrink: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .schools-list {
    grid-template-columns: 1fr;
    /* 小屏幕时一行显示1个卡片 */
  }

  .school-card-horizontal {
    flex-direction: column;
    /* 小屏幕时垂直布局 */
    text-align: center;
    gap: 8px;
    min-height: auto;
  }

  .school-basic-info {
    flex: none;
    align-items: center;
  }

  .school-details-horizontal {
    justify-content: center;
    gap: 12px;
  }
}
</style>
