<template>
  <div class="materials-step">
    <h2 class="step-title">上传申请材料</h2>
    <p class="step-description">上传您的申请材料，确保申请顺利进行</p>

    <!-- 已申请的学校 -->
    <div class="schools-selection-section" v-if="allApplications.length > 0">
      <h3 class="section-title">已申请的学校</h3>

      <div class="selected-schools-list">
        <div v-for="application in allApplications" :key="application.id" class="selected-school-item"
          :class="{ 'active': currentUserApplication && currentUserApplication.id === application.id }"
          @click="selectApplication(application)">
          <div class="school-info">
            <div class="school-details">
              <div class="school-name">{{ application.schoolName }}</div>
              <div class="school-major">{{ application.majorName }}</div>
            </div>
            <el-tag :type="getStatusTagType(application.applicationStatus)" size="small" class="status-tag">
              {{ getStatusText(application.applicationStatus) }}
            </el-tag>
          </div>
        </div>
      </div>
    </div>

    <div class="no-school-warning" v-if="allApplications.length === 0">
      <el-alert title="未找到学校信息" type="warning" description="请先在申请流程中选择学校，再来上传申请材料。" show-icon :closable="false" />
    </div>

    <!-- 专业选择部分 -->
    <div class="major-selection-section" v-if="selectedSchool && selectedMajor">
      <div class="major-header">
        <h3 class="section-title">已选择专业</h3>
        <div class="major-value">
          <span>{{ selectedMajor.name }}</span>
        </div>
      </div>
    </div>

    <div class="major-selection-section" v-else-if="selectedSchool && availableMajors.length > 0">
      <div class="major-header" @click="toggleMajorSelector">
        <h3 class="section-title">选择专业</h3>
        <div class="major-value">
          <span v-if="selectedMajor">{{ selectedMajor.name }}</span>
          <span v-else class="no-major">请选择专业</span>
          <el-icon class="expand-icon" :class="{ 'is-active': showMajorSelector }">
            <arrow-down />
          </el-icon>
        </div>
      </div>

      <div class="major-dropdown" v-if="showMajorSelector">
        <ul class="major-list">
          <li v-for="major in availableMajors" :key="major.id" class="major-item"
            :class="{ 'selected': selectedMajor && selectedMajor.id === major.id }" @click="selectMajor(major)">
            {{ major.name }}
          </li>
        </ul>
      </div>
    </div>

    <div class="materials-grid">
      <!-- 护照扫描件 - 必填 -->
      <div class="material-card required">
        <div class="material-header">
          <h3>护照扫描件</h3>
          <div class="material-status" :class="{ 'completed': materials.passport, 'required': !materials.passport }">
            {{ materials.passport ? '已上传' : '必填' }}
          </div>
        </div>
        <p class="material-description">
          请上传护照扫描件，确保信息清晰可见。这些文件用于身份验证和申请材料审核。
        </p>

        <!-- 显示已上传的护照文件 -->
        <div v-if="materials.passport" class="uploaded-file">
          <div class="file-info">
            <el-icon class="file-icon">
              <Document />
            </el-icon>
            <span class="file-name">{{ materials.passport.name }}</span>
            <div class="file-actions">
              <el-button type="text" @click="downloadFile(materials.passport)" class="download-btn">
                <el-icon>
                  <Download />
                </el-icon>
              </el-button>
              <el-button type="danger" size="small" @click="removeFile('passport')">
                删除
              </el-button>
            </div>
          </div>
        </div>

        <!-- 护照上传按钮容器 -->
        <div class="upload-container">
          <div class="upload-buttons">
            <el-upload class="material-upload" action="#" :auto-upload="false" :show-file-list="false"
              :on-change="(file, fileList) => handleMaterialUpload('passport', file, fileList)"
              :file-list="getFileList('passport')">
              <template #trigger>
                <el-button type="primary">
                  {{ materials.passport ? '重新上传' : '选择护照文件' }}
                </el-button>
              </template>
            </el-upload>
          </div>
          <div class="upload-tip">请上传PDF、JPG或PNG格式文件，大小不超过5MB</div>
        </div>
      </div>

      <!-- 身份证扫描件 - 必填 -->
      <div class="material-card required">
        <div class="material-header">
          <h3>身份证扫描件</h3>
          <div class="material-status" :class="{ 'completed': materials.idCard, 'required': !materials.idCard }">
            {{ materials.idCard ? '已上传' : '必填' }}
          </div>
        </div>
        <p class="material-description">
          请上传身份证扫描件，确保信息清晰可见。这些文件用于身份验证和申请材料审核。
        </p>

        <!-- 显示已上传的身份证文件 -->
        <div v-if="materials.idCard" class="uploaded-file">
          <div class="file-info">
            <el-icon class="file-icon">
              <Document />
            </el-icon>
            <span class="file-name">{{ materials.idCard.name }}</span>
            <div class="file-actions">
              <el-button type="text" @click="downloadFile(materials.idCard)" class="download-btn">
                <el-icon>
                  <Download />
                </el-icon>
              </el-button>
              <el-button type="danger" size="small" @click="removeFile('idCard')">
                删除
              </el-button>
            </div>
          </div>
        </div>

        <!-- 身份证上传按钮容器 -->
        <div class="upload-container">
          <div class="upload-buttons">
            <el-upload class="material-upload" action="#" :auto-upload="false" :show-file-list="false"
              :on-change="(file, fileList) => handleMaterialUpload('idCard', file, fileList)"
              :file-list="getFileList('idCard')">
              <template #trigger>
                <el-button type="primary">
                  {{ materials.idCard ? '重新上传' : '选择身份证文件' }}
                </el-button>
              </template>
            </el-upload>
          </div>
          <div class="upload-tip">请上传PDF、JPG或PNG格式文件，大小不超过5MB</div>
        </div>
      </div>

      <!-- 个人陈述 - 必填 -->
      <div class="material-card required">
        <div class="material-header">
          <h3>个人陈述</h3>
          <div class="material-status"
            :class="{ 'completed': materials.personalStatement, 'required': !materials.personalStatement }">
            {{ materials.personalStatement ? '已上传' : '必填' }}
          </div>
        </div>
        <p class="material-description">
          个人陈述是您向招生官展示自己的重要文件，应包括您的学术背景、职业目标和为什么选择该学校/专业的原因。
        </p>

        <!-- 显示已上传的文件 -->
        <div v-if="materials.personalStatement" class="uploaded-file">
          <div class="file-info">
            <el-icon class="file-icon">
              <Document />
            </el-icon>
            <span class="file-name">{{ materials.personalStatement.name }}</span>
            <div class="file-actions">
              <el-button type="text" @click="downloadFile(materials.personalStatement)" class="download-btn">
                <el-icon>
                  <Download />
                </el-icon>
              </el-button>
              <el-button type="danger" size="small" @click="removeFile('personalStatement')">
                删除
              </el-button>
            </div>
          </div>
        </div>

        <!-- 上传按钮容器 -->
        <div class="upload-container">
          <div class="upload-buttons">
            <!-- 必填材料：有文件时显示重新上传，无文件时显示选择文件 -->
            <el-upload class="material-upload" action="#" :auto-upload="false" :show-file-list="false"
              :on-change="(file, fileList) => handleMaterialUpload('personalStatement', file, fileList)"
              :file-list="getFileList('personalStatement')">
              <template #trigger>
                <el-button type="primary">
                  {{ materials.personalStatement ? '重新上传' : '选择文件' }}
                </el-button>
              </template>
            </el-upload>
          </div>
          <div class="upload-tip">请上传PDF/DOC/DOCX格式文件，大小不超过5MB</div>
        </div>
      </div>

      <!-- 成绩单 - 必填 -->
      <div class="material-card required">
        <div class="material-header">
          <h3>成绩单</h3>
          <div class="material-status"
            :class="{ 'completed': materials.transcript, 'required': !materials.transcript }">
            {{ materials.transcript ? '已上传' : '必填' }}
          </div>
        </div>
        <p class="material-description">
          请上传您的官方成绩单，包括所有已完成课程的成绩。如有需要，请提供成绩单的公证翻译件。
        </p>

        <!-- 显示已上传的文件 -->
        <div v-if="materials.transcript" class="uploaded-file">
          <div class="file-info">
            <el-icon class="file-icon">
              <Document />
            </el-icon>
            <span class="file-name">{{ materials.transcript.name }}</span>
            <div class="file-actions">
              <el-button type="text" @click="downloadFile(materials.transcript)" class="download-btn">
                <el-icon>
                  <Download />
                </el-icon>
              </el-button>
              <el-button type="danger" size="small" @click="removeFile('transcript')">
                删除
              </el-button>
            </div>
          </div>
        </div>

        <!-- 上传按钮容器 -->
        <div class="upload-container">
          <div class="upload-buttons">
            <!-- 必填材料：有文件时显示重新上传，无文件时显示选择文件 -->
            <el-upload class="material-upload" action="#" :auto-upload="false" :show-file-list="false"
              :on-change="(file, fileList) => handleMaterialUpload('transcript', file, fileList)"
              :file-list="getFileList('transcript')">
              <template #trigger>
                <el-button type="primary">
                  {{ materials.transcript ? '重新上传' : '选择文件' }}
                </el-button>
              </template>
            </el-upload>
          </div>
          <div class="upload-tip">请上传PDF格式文件，大小不超过5MB</div>
        </div>
      </div>

      <!-- 推荐信 - 必填 -->
      <div class="material-card required">
        <div class="material-header">
          <h3>推荐信</h3>
          <div class="material-status"
            :class="{ 'completed': materials.recommendationLetters.length > 0, 'required': !materials.recommendationLetters.length }">
            {{ materials.recommendationLetters.length > 0 ? `已上传 ${materials.recommendationLetters.length} 封` : '必填' }}
          </div>
        </div>
        <p class="material-description">
          大多数学校要求2-3封推荐信。请确保推荐人了解您的学术或专业能力，并能提供具体例子。
        </p>

        <!-- 显示已上传的文件列表 -->
        <div v-if="materials.recommendationLetters && materials.recommendationLetters.length > 0"
          class="uploaded-files-list">
          <div v-for="(file, index) in materials.recommendationLetters" :key="index" class="uploaded-file">
            <div class="file-info">
              <el-icon class="file-icon">
                <Document />
              </el-icon>
              <span class="file-name">{{ file.name }}</span>
              <div class="file-actions">
                <el-button type="text" @click="downloadFile(file)" class="download-btn">
                  <el-icon>
                    <Download />
                  </el-icon>
                </el-button>
                <el-button type="danger" size="small" @click="removeFileFromList('recommendationLetters', index)">
                  删除
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 上传按钮容器 -->
        <div class="upload-container">
          <div class="upload-buttons">
            <!-- 必填材料：有文件时显示重新上传，无文件时显示选择文件 -->
            <el-upload v-show="!materials.recommendationLetters || materials.recommendationLetters.length < 3"
              class="material-upload" action="#" :auto-upload="false" :show-file-list="false" :limit="3"
              :on-change="(file, fileList) => handleMaterialUpload('recommendationLetters', file, fileList)"
              :file-list="getFileList('recommendationLetters')" multiple>
              <template #trigger>
                <el-button type="primary">
                  {{ materials.recommendationLetters && materials.recommendationLetters.length > 0 ? '重新上传' : '选择文件' }}
                </el-button>
              </template>
            </el-upload>
          </div>
          <div class="upload-tip">请上传PDF格式文件，大小不超过5MB，最多3封</div>
        </div>
      </div>

      <!-- 简历/CV - 必填 -->
      <div class="material-card required">
        <div class="material-header">
          <h3>简历/CV</h3>
          <div class="material-status" :class="{ 'completed': materials.resume, 'required': !materials.resume }">
            {{ materials.resume ? '已上传' : '必填' }}
          </div>
        </div>
        <p class="material-description">
          请上传您的最新简历，包括教育背景、工作经验、技能和成就等信息。
        </p>

        <!-- 显示已上传的文件 -->
        <div v-if="materials.resume" class="uploaded-file">
          <div class="file-info">
            <el-icon class="file-icon">
              <Document />
            </el-icon>
            <span class="file-name">{{ materials.resume.name }}</span>
            <div class="file-actions">
              <el-button type="text" @click="downloadFile(materials.resume)" class="download-btn">
                <el-icon>
                  <Download />
                </el-icon>
              </el-button>
              <el-button type="danger" size="small" @click="removeFile('resume')">
                删除
              </el-button>
            </div>
          </div>
        </div>

        <!-- 上传按钮容器 -->
        <div class="upload-container">
          <div class="upload-buttons">
            <!-- 必填材料：有文件时显示重新上传，无文件时显示选择文件 -->
            <el-upload class="material-upload" action="#" :auto-upload="false" :show-file-list="false"
              :on-change="(file, fileList) => handleMaterialUpload('resume', file, fileList)"
              :file-list="getFileList('resume')">
              <template #trigger>
                <el-button type="primary">
                  {{ materials.resume ? '重新上传' : '选择文件' }}
                </el-button>
              </template>
            </el-upload>
          </div>
          <div class="upload-tip">请上传PDF格式文件，大小不超过5MB</div>
        </div>
      </div>

      <!-- 语言成绩证明 - 必填 -->
      <div class="material-card required">
        <div class="material-header">
          <h3>语言成绩证明</h3>
          <div class="material-status"
            :class="{ 'completed': materials.languageScores, 'required': !materials.languageScores }">
            {{ materials.languageScores ? '已上传' : '必填' }}
          </div>
        </div>
        <p class="material-description">
          请上传您的语言考试成绩单，如托福、雅思、GRE或GMAT等。
        </p>

        <!-- 显示已上传的文件 -->
        <div v-if="materials.languageScores" class="uploaded-file">
          <div class="file-info">
            <el-icon class="file-icon">
              <Document />
            </el-icon>
            <span class="file-name">{{ materials.languageScores.name }}</span>
            <div class="file-actions">
              <el-button type="text" @click="downloadFile(materials.languageScores)" class="download-btn">
                <el-icon>
                  <Download />
                </el-icon>
              </el-button>
              <el-button type="danger" size="small" @click="removeFile('languageScores')">
                删除
              </el-button>
            </div>
          </div>
        </div>

        <!-- 上传按钮容器 -->
        <div class="upload-container">
          <div class="upload-buttons">
            <!-- 必填材料：有文件时显示重新上传，无文件时显示选择文件 -->
            <el-upload class="material-upload" action="#" :auto-upload="false" :show-file-list="false"
              :on-change="(file, fileList) => handleMaterialUpload('languageScores', file, fileList)"
              :file-list="getFileList('languageScores')">
              <template #trigger>
                <el-button type="primary">
                  {{ materials.languageScores ? '重新上传' : '选择文件' }}
                </el-button>
              </template>
            </el-upload>
          </div>
          <div class="upload-tip">请上传PDF格式文件，大小不超过5MB</div>
        </div>
      </div>

      <!-- 论文 - 非必填 -->
      <div class="material-card">
        <div class="material-header">
          <h3>论文</h3>
          <div class="material-status" :class="{ 'completed': materials.papers?.length > 0 }">
            {{ materials.papers?.length > 0 ? `已上传 ${materials.papers.length} 篇` : '选填' }}
          </div>
        </div>
        <p class="material-description">
          如果您有已发表的学术论文或研究成果，请在此上传。这将有助于展示您的研究能力和学术潜力。
        </p>

        <!-- 显示已上传的文件列表 -->
        <div v-if="materials.papers && materials.papers.length > 0" class="uploaded-files-list">
          <div v-for="(file, index) in materials.papers" :key="index" class="uploaded-file">
            <div class="file-info">
              <el-icon class="file-icon">
                <Document />
              </el-icon>
              <span class="file-name">{{ file.name }}</span>
              <div class="file-actions">
                <el-button type="text" @click="downloadFile(file)" class="download-btn">
                  <el-icon>
                    <Download />
                  </el-icon>
                </el-button>
                <el-button type="danger" size="small" @click="removeFileFromList('papers', index)">
                  删除
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 上传按钮容器 -->
        <div class="upload-container">
          <div class="upload-buttons">
            <!-- 可选材料：始终显示选择文件按钮 -->
            <el-upload v-show="!materials.papers || materials.papers.length < 5" class="material-upload" action="#"
              :auto-upload="false" :show-file-list="false" :limit="5"
              :on-change="(file, fileList) => handleMaterialUpload('papers', file, fileList)"
              :file-list="getFileList('papers')" multiple>
              <template #trigger>
                <el-button type="primary">选择文件</el-button>
              </template>
            </el-upload>
          </div>
          <div class="upload-tip">请上传PDF格式文件，每个文件大小不超过10MB，最多5篇</div>
        </div>
      </div>

      <!-- 其他材料 - 非必填 -->
      <div class="material-card">
        <div class="material-header">
          <h3>其他材料</h3>
          <div class="material-status" :class="{ 'completed': materials.others?.length > 0 }">
            {{ materials.others?.length > 0 ? `已上传 ${materials.others.length} 个` : '选填' }}
          </div>
        </div>
        <p class="material-description">
          上传任何其他可能对您的申请有帮助的补充材料，如获奖证书、实习证明、专利等。
        </p>

        <!-- 显示已上传的文件列表 -->
        <div v-if="materials.others && materials.others.length > 0" class="uploaded-files-list">
          <div v-for="(file, index) in materials.others" :key="index" class="uploaded-file">
            <div class="file-info">
              <el-icon class="file-icon">
                <Document />
              </el-icon>
              <span class="file-name">{{ file.name }}</span>
              <div class="file-actions">
                <el-button type="text" @click="downloadFile(file)" class="download-btn">
                  <el-icon>
                    <Download />
                  </el-icon>
                </el-button>
                <el-button type="danger" size="small" @click="removeFileFromList('others', index)">
                  删除
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 上传按钮容器 -->
        <div class="upload-container">
          <div class="upload-buttons">
            <!-- 可选材料：始终显示选择文件按钮 -->
            <el-upload v-show="!materials.others || materials.others.length < 10" class="material-upload" action="#"
              :auto-upload="false" :show-file-list="false" :limit="10"
              :on-change="(file, fileList) => handleMaterialUpload('others', file, fileList)"
              :file-list="getFileList('others')" multiple>
              <template #trigger>
                <el-button type="primary">选择文件</el-button>
              </template>
            </el-upload>
          </div>
          <div class="upload-tip">支持PDF、JPG、PNG格式，每个文件大小不超过5MB，最多10个文件</div>
        </div>
      </div>
    </div>

    <div class="materials-progress">
      <div class="progress-header">
        <h3>申请材料完成度</h3>
        <div class="progress-percentage">{{ materialsCompletionPercentage }}%</div>
      </div>
      <el-progress :percentage="materialsCompletionPercentage" :stroke-width="20" :show-text="false"></el-progress>
      <p class="progress-tip">
        {{ getMaterialsProgressTip() }}
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { ArrowDown, Document, Download } from '@element-plus/icons-vue';
import axios from 'axios';
import { useRoute } from 'vue-router';

// 使用路由而不是props
const route = useRoute();

// 学校和专业信息
const selectedSchool = ref(null);
const selectedMajor = ref(null);
const availableMajors = ref([]);
const showMajorSelector = ref(false);

// 申请数据
const currentUserApplication = ref(null);
const studentId = ref(null);
const applicationId = ref(null);
const allApplications = ref([]); // 存储所有申请

// 接收的属性
const props = defineProps({
  modelValue: {
    type: Object,
    required: true
  }
});

// 发出的事件
const emit = defineEmits(['update:modelValue', 'completion-change', 'refresh-files']);

// 内部材料数据，基于modelValue
const materials = ref(props.modelValue);

// 获取当前用户的申请数据
const fetchCurrentUserApplication = async () => {
  try {
    console.log('开始从API获取申请数据...');

    // 1. 获取当前用户信息
    const userResponse = await axios.get('/api/user/current');
    if (userResponse.data.code !== 200 || !userResponse.data.data) {
      console.error('获取当前用户信息失败');
      ElMessage.warning('无法获取用户信息，请确保已登录');
      return false;
    }

    const userId = userResponse.data.data.id;
    console.log('获取到当前用户ID:', userId);

    // 2. 获取学生信息
    const studentResponse = await axios.get(`/api/student/info/${userId}`);
    if (studentResponse.data.code === 200 && studentResponse.data.data) {
      studentId.value = studentResponse.data.data.id;
      console.log('获取到学生ID:', studentId.value);
    } else {
      console.warn('未找到学生信息');
      if (process.env.NODE_ENV !== 'production') {
        studentId.value = 1; // 开发环境使用临时ID
        console.log('使用临时学生ID:', studentId.value);
      } else {
        ElMessage.warning('未找到您的学生信息，请先完善个人资料');
        return false;
      }
    }

    // 3. 获取所有申请信息
    if (studentId.value) {
      const applicationsResponse = await axios.get(`/api/application-school/list?studentId=${studentId.value}`);
      if (applicationsResponse.data.code === 200 &&
        applicationsResponse.data.data &&
        applicationsResponse.data.data.length > 0) {

        // 保存所有申请
        allApplications.value = applicationsResponse.data.data;
        console.log(`获取到${allApplications.value.length}个申请`);

        // 默认选择第一个申请
        selectApplication(allApplications.value[0]);

        return true;
      } else {
        console.warn('未找到申请数据');
        ElMessage.warning('未找到您的申请信息，请先选择学校');
        return false;
      }
    }

    return false;
  } catch (error) {
    console.error('获取申请数据失败:', error);
    ElMessage.error('加载申请数据失败，请刷新页面重试');
    return false;
  }
};

// 选择一个申请
const selectApplication = (application) => {
  currentUserApplication.value = application;
  applicationId.value = application.id;

  console.log('选择申请ID:', applicationId.value);
  console.log('选择学校ID:', application.schoolId);
  console.log('选择学校名称:', application.schoolName);

  // 设置学校信息
  fetchSchoolInformation();

  // 加载该申请的材料
  if (studentId.value && applicationId.value) {
    loadExistingMaterials();
    emit('refresh-files', { studentId: studentId.value, applicationId: applicationId.value });
  }
};

// 监听props.modelValue的变化
watch(() => props.modelValue, (newValue) => {
  materials.value = newValue;
}, { deep: true });

// 监听materials的变化，更新父组件的值
watch(materials, (newValue) => {
  emit('update:modelValue', newValue);
  emit('completion-change', materialsCompletionPercentage.value);
}, { deep: true });

// 监听路由变化，重新获取申请数据
watch(
  () => route.path,
  async () => {
    console.log('路由变化，重新获取申请数据');
    await fetchCurrentUserApplication();
    if (currentUserApplication.value) {
      await fetchSchoolInformation();
    }
  }
);

// 处理材料上传
const handleMaterialUpload = async (type, file, uploadFiles = null) => {
  try {
    console.log('开始处理材料上传:', type, file.name);

    const removeFromUploadFiles = () => {
      if (!Array.isArray(uploadFiles) || !file) return;
      const uid = file.uid;
      const index = uploadFiles.findIndex(f => f && f.uid === uid);
      if (index > -1) {
        uploadFiles.splice(index, 1);
      }
    };

    // 避免重复上传
    if (file.status === 'success') {
      return;
    }

    const rawFile = file?.raw;
    const fileName = rawFile?.name || file?.name || '';
    const fileSize = rawFile?.size ?? file?.size ?? 0;
    const fileType = rawFile?.type || '';
    const fileExtension = (fileName.split('.').pop() || '').toLowerCase();

    // 检查文件大小
    const maxSize = type === 'papers' ? 10 * 1024 * 1024 : 5 * 1024 * 1024; // 论文10MB，其他5MB
    if (fileSize > maxSize) {
      ElMessage.error(`文件大小超过限制: ${file.name}`);
      removeFromUploadFiles();
      return;
    }

    // 检查文件类型
    const allowedTypes = ['application/pdf'];
    const allowedExtensions = new Set(['pdf']);
    if (type === 'resume') {
      allowedTypes.push('application/msword');
      allowedTypes.push('application/vnd.openxmlformats-officedocument.wordprocessingml.document');
      allowedExtensions.add('doc');
      allowedExtensions.add('docx');
    }
    if (type === 'others' || type === 'passport' || type === 'idCard') {
      allowedTypes.push('image/jpeg', 'image/png');
      allowedExtensions.add('jpg');
      allowedExtensions.add('jpeg');
      allowedExtensions.add('png');
    }
    const isAllowedByMime = !!fileType && allowedTypes.includes(fileType);
    const isAllowedByExt = !!fileExtension && allowedExtensions.has(fileExtension);
    if (!isAllowedByMime && !isAllowedByExt) {
      const tip = type === 'resume' ? '请上传 PDF/DOC/DOCX 格式文件' : '请上传 PDF 格式文件';
      ElMessage.error(`${tip}: ${fileName || file.name}`);
      removeFromUploadFiles();
      return;
    }

    // 检查是否有必要的参数
    console.log('上传前检查 - 学生ID:', studentId.value, '申请ID:', applicationId.value);

    // 如果没有studentId或applicationId，尝试重新获取
    if (!studentId.value || !applicationId.value) {
      console.warn('缺少学生ID或申请ID，尝试重新获取数据...');
      const success = await fetchCurrentUserApplication();

      console.log('重新获取数据结果:', success);
      console.log('重新获取后 - 学生ID:', studentId.value, '申请ID:', applicationId.value);

      if (!studentId.value) {
        console.error('无法获取学生ID，无法上传文件');
        ElMessage.error('无法识别当前学生信息，请确保您已正确登录');
        removeFromUploadFiles();
        return;
      }

      if (!applicationId.value) {
        console.error('无法获取申请ID，无法上传文件');
        ElMessage.error('无法识别当前申请信息，请确保您已选择学校');
        removeFromUploadFiles();
        return;
      }
    }

    console.log('文件上传参数:', {
      studentId: studentId.value,
      applicationId: applicationId.value,
      fileName: file.name,
      fileType: type
    });

    // 先将文件添加到本地状态
    if (Array.isArray(materials.value[type])) {
      // 如果是数组类型的材料（如推荐信、作品集）
      // 确保材料数组已初始化
      if (!materials.value[type]) {
        materials.value[type] = [];
      }

      const existingIndex = materials.value[type].findIndex(f => f.uid === file.uid);
      if (existingIndex !== -1) {
        materials.value[type].splice(existingIndex, 1, file);
      } else {
        materials.value[type].push(file);
      }
    } else {
      // 如果是单个文件类型的材料
      materials.value[type] = file;
    }

    // 更新文件状态为上传中
    file.status = 'uploading';
    ElMessage.info(`正在上传: ${file.name}`);

    // 使用原生fetch API上传文件
    const formData = new FormData();
    formData.append('file', file.raw, file.name);
    formData.append('type', type);

    const url = `/api/files/application/materials/student/${studentId.value}/application-school/${applicationId.value}`;
    console.log('发送请求:', { url });

    // 使用原生fetch API，让浏览器自动处理multipart/form-data
    const response = await fetch(url, {
      method: 'POST',
      body: formData,
      credentials: 'include'
    });

    // 检查响应
    if (!response.ok) {
      let errorData = null;
      try {
        errorData = await response.clone().json();
      } catch (e) {
        try {
          const text = await response.text();
          errorData = { message: text };
        } catch (e2) {
          errorData = null;
        }
      }
      console.error('上传失败:', errorData);
      file.status = 'error';
      ElMessage.error(`上传失败: ${errorData?.message || response.statusText}`);
      return;
    }

    // 处理成功响应
    const responseData = await response.json();
    if (responseData.success) {
      file.status = 'success';
      const uploadedFilename = String(responseData.filePath || '').split('/').pop();
      file.url = `/api/files/download/${studentId.value}/${applicationId.value}/${uploadedFilename}?download=true`;
      // 设置文件ID，用于后续删除操作
      file.id = responseData.fileId;
      console.log('文件上传成功，设置文件ID:', responseData.fileId);
      ElMessage.success(`上传成功: ${file.name}`);

      // 主动触发进度条更新
      emit('completion-change', materialsCompletionPercentage.value);
      emit('refresh-files', { studentId: studentId.value, applicationId: applicationId.value });
    } else {
      file.status = 'error';
      ElMessage.error(`上传失败: ${responseData?.message || '未知错误'}`);
      console.error('上传响应:', responseData);
    }
  } catch (error) {
    // 处理异常
    console.error('文件上传出错:', error);
    // 显示更详细的错误信息
    ElMessage.error(`上传出错: ${error.message || '未知错误'}`);
    if (Array.isArray(uploadFiles) && file?.status !== 'success') {
      const uid = file?.uid;
      const index = uploadFiles.findIndex(f => f && f.uid === uid);
      if (index > -1) {
        uploadFiles.splice(index, 1);
      }
    }
  }
};

// 获取文件列表
const getFileList = (type) => {
  if (!materials.value) return [];

  if (Array.isArray(materials.value[type])) {
    return materials.value[type] || [];
  } else if (materials.value[type]) {
    return [materials.value[type]];
  }
  return [];
};

// 计算材料完成度百分比
const materialsCompletionPercentage = computed(() => {
  let completed = 0;
  let total = 7; // 必需的材料数量：护照、身份证、个人陈述、成绩单、推荐信、简历、语言成绩

  if (materials.value.passport) completed++;
  if (materials.value.idCard) completed++;
  if (materials.value.personalStatement) completed++;
  if (materials.value.transcript) completed++;
  if (materials.value.recommendationLetters.length > 0) completed++;
  if (materials.value.resume) completed++;
  if (materials.value.languageScores) completed++;

  // 计算百分比
  return Math.round((completed / total) * 100);
});

// 获取材料进度提示
const getMaterialsProgressTip = () => {
  const percentage = materialsCompletionPercentage.value;

  if (percentage === 0) return '您还没有上传任何材料，请开始上传必要的申请材料';
  if (percentage < 50) return '您已上传部分材料，请继续上传其余必要材料';
  if (percentage < 100) return '您已上传大部分材料，请完成剩余材料的上传';
  return '恭喜！您已完成所有材料的上传';
}

// 获取学校和专业信息
const fetchSchoolInformation = async () => {
  try {
    // 使用currentUserApplication中的数据设置学校和专业信息
    if (currentUserApplication.value) {
      // 创建学校对象
      selectedSchool.value = {
        id: currentUserApplication.value.schoolId,
        name: currentUserApplication.value.schoolName,
        country: currentUserApplication.value.country || '',
        location: currentUserApplication.value.location || '',
      };

      // 如果有专业信息
      if (currentUserApplication.value.majorName) {
        selectedMajor.value = {
          id: currentUserApplication.value.majorId,
          name: currentUserApplication.value.majorName
        };
      }

      console.log('从申请数据获取的学校信息:', selectedSchool.value);
      console.log('从申请数据获取的专业信息:', selectedMajor.value);

      // 如果需要，可以尝试获取更多专业选项
      if (selectedSchool.value.id) {
        try {
          const majorsResponse = await axios.get(`/api/schools/${selectedSchool.value.id}/majors`);
          if (majorsResponse.data && majorsResponse.data.success) {
            availableMajors.value = majorsResponse.data.data || [];
          }
        } catch (error) {
          console.error('获取专业列表失败:', error);
        }
      }

      return; // 已经处理完毕，不需要继续请求API
    }

    // 如果没有currentUserApplication，但有applicationId，尝试通过API获取
    if (applicationId.value) {
      const response = await axios.get(`/api/application-school/detail/${applicationId.value}`);
      if (response.data.code === 200 && response.data.data) {
        const application = response.data.data;

        // 设置学校信息
        selectedSchool.value = {
          id: application.schoolId,
          name: application.schoolName,
          country: application.country || '',
          location: application.location || '',
        };

        // 设置专业信息
        if (application.majorName) {
          selectedMajor.value = {
            id: application.majorId,
            name: application.majorName
          };
        }

        // 获取专业列表
        if (selectedSchool.value.id) {
          const majorsResponse = await axios.get(`/api/schools/${selectedSchool.value.id}/majors`);
          if (majorsResponse.data && majorsResponse.data.success) {
            availableMajors.value = majorsResponse.data.data || [];
          }
        }
      } else {
        console.error('获取申请详情失败:', response.data);
        ElMessage.warning('未能获取申请的学校信息');
      }
    } else {
      console.error('无法获取学校信息：缺少applicationId');
    }
  } catch (error) {
    console.error('获取学校信息出错:', error);
    ElMessage.error('加载学校信息失败，请稍后重试');
  }
};

// 选择专业
const selectMajor = async (major) => {
  try {
    if (!applicationId.value) {
      console.error('未找到申请ID，无法保存专业选择');
      ElMessage.error('无法识别当前申请信息，请确保您已选择学校');
      return;
    }

    // 保存选择的专业
    const response = await axios.post(`/api/applications/${applicationId.value}/select-major`, {
      majorId: major.id
    });

    if (response.data && response.data.success) {
      selectedMajor.value = major;
      showMajorSelector.value = false;
      ElMessage.success(`已选择专业: ${major.name}`);

      // 重新加载材料列表，可能根据专业有所不同
      await loadMaterials();
    } else {
      ElMessage.error('保存专业选择失败');
    }
  } catch (error) {
    console.error('选择专业出错:', error);
    ElMessage.error('选择专业失败，请稍后重试');
  }
};

// 展开或关闭专业选择器
const toggleMajorSelector = () => {
  showMajorSelector.value = !showMajorSelector.value;
};

// 组件挂载时加载已有的材料
onMounted(async () => {
  console.log('MaterialsUpload组件已挂载，开始初始化...');

  // 初始化材料状态
  if (!materials.value || Object.keys(materials.value).length === 0) {
    materials.value = {
      passport: null,
      idCard: null,
      personalStatement: null,
      transcript: null,
      recommendationLetters: [],
      resume: null,
      languageScores: null,
      papers: [],
      others: []
    };
  }

  // 从API获取申请数据
  const success = await fetchCurrentUserApplication();

  // 记录当前的学生ID和申请ID
  console.log('当前学生ID:', studentId.value);
  console.log('当前申请ID:', applicationId.value);

  // 加载申请相关的学校信息
  if (success) {
    await fetchSchoolInformation();
  }

  // 如果有学生ID和申请ID，加载已有的材料
  if (studentId.value && applicationId.value) {
    await loadExistingMaterials();
  } else if (studentId.value) {
    // 如果只有学生ID没有申请ID，尝试加载该学生的所有材料
    await loadAllStudentMaterials();
  } else {
    console.warn('未找到学生ID或申请ID，无法加载申请材料');
    ElMessage.warning('无法加载申请材料，请确保已登录并选择了学校');
  }
});

// 加载特定学校和专业的材料要求
const loadMaterials = async () => {
  try {
    if (!applicationId.value) {
      console.error('无法加载材料要求：缺少申请ID');
      return;
    }

    // 构造查询参数
    let url = `/api/applications/${applicationId.value}/materials-requirements`;

    // 如果已有学校信息
    if (selectedSchool.value) {
      url += `?schoolId=${selectedSchool.value.id}`;
    }
    // 否则尝试从currentUserApplication获取
    else if (currentUserApplication.value?.schoolId) {
      url += `?schoolId=${currentUserApplication.value.schoolId}`;
    }

    // 如果已有专业信息
    if (selectedMajor.value) {
      url += url.includes('?') ? `&majorId=${selectedMajor.value.id}` : `?majorId=${selectedMajor.value.id}`;
    }
    // 否则尝试从currentUserApplication获取
    else if (currentUserApplication.value?.majorId) {
      url += url.includes('?') ? `&majorId=${currentUserApplication.value.majorId}` : `?majorId=${currentUserApplication.value.majorId}`;
    }

    console.log('请求材料要求的URL:', url);

    const response = await axios.get(url);
    if (response.data && response.data.success) {
      // 这里可以根据学校/专业特殊要求调整材料列表
      console.log('获取到的材料要求:', response.data);

      // 如果后端返回了特定的材料列表，可以在这里更新
      if (response.data.requiredMaterials) {
        // 根据后端返回的信息设置哪些材料是必需的
        console.log('特定要求的材料:', response.data.requiredMaterials);
      }
    }
  } catch (error) {
    console.error('加载材料要求失败:', error);
  }
};

// 加载特定申请的材料
const loadExistingMaterials = async () => {
  try {
    // 首先重置材料状态为初始值
    resetMaterials();

    const url = `/api/files/application/materials/student/${studentId.value}/application-school/${applicationId.value}`;
    console.log('请求特定申请材料的URL:', url);
    const response = await axios.get(url);

    // 处理获取到的材料
    if (response.data) {
      const materialsByType = response.data;
      console.log('获取到的材料数据:', materialsByType);

      // 将获取到的材料按类型整合到materials中
      for (const [type, files] of Object.entries(materialsByType)) {
        if (Array.isArray(materials.value[type])) {
          // 如果是数组类型，如推荐信、论文
          materials.value[type] = files.length > 0 ? files.map(file => ({
            name: file.originalName,
            url: `/api/files/download/${studentId.value}/${applicationId.value}/${file.fileName}?download=true`,
            status: 'success',
            uid: file.id.toString(),
            id: file.id
          })) : [];
        } else if (files.length > 0) {
          // 如果是单个文件类型，如个人陈述
          materials.value[type] = {
            name: files[0].originalName,
            url: `/api/files/download/${studentId.value}/${applicationId.value}/${files[0].fileName}?download=true`,
            status: 'success',
            uid: files[0].id.toString(),
            id: files[0].id
          };
        } else {
          // 如果该类型没有文件，设置为null
          materials.value[type] = null;
        }
      }

      console.log('材料加载完成:', materials.value);
      // 计算材料完成度
      emit('completion-change', materialsCompletionPercentage.value);
    }
  } catch (error) {
    console.error('加载特定申请材料失败:', error);
    ElMessage.warning('加载已有申请材料失败，请刷新页面重试');
  }
};

// 加载学生的所有材料
const loadAllStudentMaterials = async () => {
  try {
    // 首先重置材料状态
    resetMaterials();

    const url = `/api/files/application/materials/student/${studentId.value}`;
    console.log('请求所有材料的URL:', url);
    const response = await axios.get(url);

    // 处理获取到的材料
    if (response.data) {
      const materialsByType = response.data;
      console.log('获取到的所有材料数据:', materialsByType);

      // 将获取到的材料按类型整合到materials中
      for (const [type, files] of Object.entries(materialsByType)) {
        if (Array.isArray(materials.value[type])) {
          // 如果是数组类型，如推荐信、论文
          materials.value[type] = files.length > 0 ? files.map(file => ({
            name: file.originalName,
            url: `/api/files/download/${studentId.value}/${file.applicationId || applicationId.value}/${file.fileName}?download=true`,
            status: 'success',
            uid: file.id.toString(),
            id: file.id
          })) : [];
        } else if (files.length > 0) {
          // 如果是单个文件类型，如个人陈述
          materials.value[type] = {
            name: files[0].originalName,
            url: `/api/files/download/${studentId.value}/${files[0].applicationId || applicationId.value}/${files[0].fileName}?download=true`,
            status: 'success',
            uid: files[0].id.toString(),
            id: files[0].id
          };
        } else {
          // 如果该类型没有文件，设置为null
          materials.value[type] = null;
        }
      }

      console.log('所有材料加载完成:', materials.value);
      // 计算材料完成度
      emit('completion-change', materialsCompletionPercentage.value);
    }
  } catch (error) {
    console.error('加载学生所有材料失败:', error);
    ElMessage.warning('加载已有申请材料失败，请刷新页面重试');
  }
};

// 获取申请状态对应的CSS类名
const getStatusClass = (status) => {
  if (!status) return 'applied';

  switch (status) {
    case '已录取':
    case '录取':
    case 'Accepted':
      return 'accepted';
    case '已拒绝':
    case '拒绝':
    case 'Rejected':
      return 'rejected';
    case '已提交':
    case '提交':
    case 'Submitted':
      return 'submitted';
    case '已申请':
    case '申请':
    case 'Applied':
      return 'applied';
    default:
      return 'applied';
  }
};

// 获取状态文本
const getStatusText = (status) => {
  if (!status) return '未知';

  switch (status) {
    case 'selected':
      return '已选择';
    case 'submitted':
      return '已提交';
    case 'under_review':
      return '审核中';
    case 'interview':
      return '面试中';
    case 'accepted':
    case 'admitted':
      return '已录取';
    case 'rejected':
      return '已拒绝';
    case 'visa_processing':
      return '签证申请中';
    case 'visa_approved':
      return '签证已批准';
    case 'visa_rejected':
      return '签证被拒';
    case 'enrolled':
      return '已入学';
    default:
      return status;
  }
};

// 获取状态标签类型
const getStatusTagType = (status) => {
  if (!status) return 'info';

  switch (status) {
    case 'selected':
      return 'info';
    case 'submitted':
      return 'warning';
    case 'under_review':
      return 'primary';
    case 'interview':
      return 'primary';
    case 'accepted':
    case 'admitted':
      return 'success';
    case 'rejected':
      return 'danger';
    case 'visa_processing':
      return 'primary';
    case 'visa_approved':
      return 'success';
    case 'visa_rejected':
      return 'danger';
    case 'enrolled':
      return 'success';
    default:
      return 'info';
  }
};

// 检查是否有selected状态的学校
const hasSelectedSchools = computed(() => {
  return allApplications.value.some(app => app.applicationStatus === 'selected');
});

// 获取selected状态的学校列表
const getSelectedSchools = () => {
  return allApplications.value.filter(app => app.applicationStatus === 'selected');
};

// 重置材料状态为初始值
const resetMaterials = () => {
  // 这里需要根据实际情况重置材料状态
  // 例如，清空所有材料数组，重置所有文件状态等
  // 这里只是一个示例，具体实现需要根据实际情况调整
  materials.value = {
    passport: null,
    idCard: null,
    personalStatement: null,
    transcript: null,
    recommendationLetters: [],
    resume: null,
    languageScores: null,
    papers: [],
    others: []
  };
};



// 删除单个文件
const removeFile = async (type) => {
  try {
    const file = materials.value[type];
    if (!file) return;

    // 确认删除
    await ElMessageBox.confirm(
      `确定要删除文件 "${file.name}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );

    // 调用后端删除API - 使用正确的文件ID字段
    const fileId = file.id || file.uid;
    if (fileId) {
      console.log('删除文件ID:', fileId);
      await axios.delete(`/api/files/delete/${fileId}`);
    }

    // 从本地状态中移除
    materials.value[type] = null;

    ElMessage.success('文件删除成功');
    emit('completion-change', materialsCompletionPercentage.value);
    emit('refresh-files', { studentId: studentId.value, applicationId: applicationId.value });
  } catch (error) {
    if (error === 'cancel') {
      return; // 用户取消删除
    }
    console.error('删除文件失败:', error);
    ElMessage.error('删除文件失败，请重试');
  }
};

// 从列表中删除文件
const removeFileFromList = async (type, index) => {
  try {
    const fileList = materials.value[type];
    if (!fileList || !fileList[index]) return;

    const file = fileList[index];

    // 确认删除
    await ElMessageBox.confirm(
      `确定要删除文件 "${file.name}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );

    // 调用后端删除API - 使用正确的文件ID字段
    const fileId = file.id || file.uid;
    if (fileId) {
      console.log('删除列表文件ID:', fileId);
      await axios.delete(`/api/files/delete/${fileId}`);
    }

    // 从本地状态中移除
    fileList.splice(index, 1);

    ElMessage.success('文件删除成功');
    emit('completion-change', materialsCompletionPercentage.value);
    emit('refresh-files', { studentId: studentId.value, applicationId: applicationId.value });
  } catch (error) {
    if (error === 'cancel') {
      return; // 用户取消删除
    }
    console.error('删除文件失败:', error);
    ElMessage.error('删除文件失败，请重试');
  }
};

// 下载文件
const downloadFile = (file) => {
  if (file.url) {
    // 创建一个临时链接来下载文件
    const link = document.createElement('a');
    link.href = file.url;
    link.download = file.name;
    link.target = '_blank';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  } else {
    ElMessage.error('文件链接不可用');
  }
};


</script>

<style scoped>
/* 材料上传步骤样式 */
.materials-step {
  margin-bottom: 20px;
}

.step-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 0.25rem;
  color: var(--dark-color, #1d1d1f);
}

.step-description {
  font-size: 0.875rem;
  color: var(--gray-color, #86868b);
  margin-bottom: 1.5rem;
}

/* 已上传文件样式 */
.uploaded-file {
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.uploaded-files-list {
  margin-bottom: 16px;
}

.uploaded-files-list .uploaded-file {
  margin-bottom: 12px;
}

.uploaded-files-list .uploaded-file:last-child {
  margin-bottom: 0;
}

.file-info {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.file-icon {
  color: var(--primary-color);
  margin-right: 8px;
  font-size: 18px;
}

.file-name {
  flex: 1;
  font-size: 14px;
  color: #333;
  margin-right: 8px;
}

.download-btn {
  color: var(--primary-color);
  padding: 4px;
}

.download-btn:hover {
  background-color: var(--el-color-primary-light-9);
}

/* 文件操作按钮样式 */
.file-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.file-actions .el-button {
  height: 32px;
  padding: 0 12px;
  font-size: 12px;
}

/* 上传组件和按钮容器 */
.upload-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 上传按钮和添加更多按钮的容器 */
.upload-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: nowrap;
}

.upload-buttons .el-button {
  height: 36px;
  padding: 0 16px;
  white-space: nowrap;
}

/* 确保上传组件和按钮在同一行 */
.upload-buttons .material-upload {
  display: inline-block;
}

.upload-buttons .material-upload .el-upload {
  display: inline-block;
}

/* 学校信息部分样式 - 精简版 */
.schools-selection-section {
  margin-bottom: 16px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 20px;
  text-align: left;
}

/* 已选择学校列表样式 */
.selected-schools-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.selected-school-item {
  display: inline-flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  background-color: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  width: fit-content;
  min-width: 300px;
  max-width: 100%;
  min-height: 56px;
}

.selected-school-item:hover {
  background-color: #e9ecef;
  transform: translateY(-1px);
}

.selected-school-item.active {
  border-color: var(--primary-color);
  background-color: var(--el-color-primary-light-9);
}

.school-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.school-details {
  flex: 1;
  min-width: 0;
}

.school-name {
  font-size: 14px;
  font-weight: 600;
  color: #1d1d1f;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.2;
}

.school-major {
  font-size: 12px;
  font-weight: 400;
  color: #6b7280;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-top: 2px;
  line-height: 1.2;
}

.status-tag {
  margin-left: 12px;
  flex-shrink: 0;
}

.school-header {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 15px;
}

.school-meta {
  display: flex;
  align-items: center;
}

.school-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.school-logo {
  width: 80px;
  height: 80px;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  overflow: hidden;
  background-color: #f5f5f7;
}

.school-logo img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.school-logo-placeholder {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  background-color: #f5f5f7;
  font-size: 24px;
  font-weight: 600;
  color: var(--primary-color);
}



.school-details {
  flex: 1;
  width: 100%;
}

.school-name {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 5px;
}

.school-location {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 5px;
}

.school-major {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 10px;
}

.application-status {
  font-size: 14px;
  color: #6b7280;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 8px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
  margin-right: 8px;
}

.status-applied {
  background-color: var(--el-color-primary-light-5);
}

.status-submitted {
  background-color: var(--el-color-primary-light-5);
}

.status-accepted {
  background-color: #34d399;
}

.status-rejected {
  background-color: #f87171;
}

.no-school-warning {
  margin-bottom: 30px;
}

.major-selection-section {
  margin-top: 30px;
  margin-bottom: 30px;
}

.major-header {
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.major-header h3 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.major-value {
  display: flex;
  align-items: center;
}

.no-major {
  color: #9ca3af;
}

.expand-icon {
  margin-left: 8px;
  transition: transform 0.3s;
}

.expand-icon.is-active {
  transform: rotate(180deg);
}

.major-dropdown {
  margin-top: 15px;
  background-color: #f9f9fb;
  border-radius: 8px;
  padding: 8px 0;
}

.major-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.major-item {
  padding: 10px 16px;
  cursor: pointer;
  transition: background-color 0.2s;
  font-size: 14px;
}

.major-item:hover {
  background-color: #f3f4f6;
}

.major-item.selected {
  background-color: var(--el-color-primary-light-9);
  color: var(--primary-color);
  font-weight: 500;
}

/* 材料网格样式 - 精简版 */
.materials-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.material-card {
  background-color: white;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid #eee;
  position: relative;
  /* 添加定位以支持必填标记 */
}

/* 添加必填标记样式 - 精简版 */
.material-card.required::before {
  content: '*';
  position: absolute;
  top: 16px;
  right: 16px;
  color: #f56c6c;
  font-size: 16px;
}

.material-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.material-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
}

.material-status {
  font-size: 12px;
  padding: 3px 8px;
  border-radius: 16px;
  background-color: #f3f4f6;
  color: #6b7280;
  display: flex;
  align-items: center;
  gap: 4px;
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
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 12px;
  line-height: 1.4;
}

.sub-material {
  margin-bottom: 16px;
  padding: 12px;
  background-color: #f9f9fb;
  border-radius: 6px;
  border-left: 3px solid var(--primary-color);
}

.sub-material:last-child {
  margin-bottom: 0;
}

.sub-material h4 {
  font-size: 14px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 8px 0;
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

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.progress-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
}

.progress-percentage {
  font-size: 18px;
  font-weight: 600;
  color: var(--primary-color);
}

.progress-tip {
  font-size: 14px;
  color: #6b7280;
  margin-top: 15px;
}

@media (max-width: 768px) {
  .materials-grid {
    grid-template-columns: 1fr;
  }
}
</style>
