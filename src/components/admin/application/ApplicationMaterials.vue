<template>
  <div class="tab-pane">
    <div class="materials-progress-wrapper">
      <div class="progress-header">
        <h5>材料准备进度: {{ props.application.materialsProgress }}%</h5>
      </div>
      <div class="progress-bar-wrapper">
        <div class="progress-bar-bg"></div>
        <div class="progress-bar-fill"
          :style="{ width: `${props.application.materialsProgress}%`, backgroundColor: props.getMaterialsProgressColorFn(props.application.materialsProgress) }">
        </div>
      </div>
    </div>

    <!-- 材料清单 -->
    <div class="materials-checklist-wrapper">
      <div class="materials-header">
        <div class="materials-header-content">
          <h3 class="materials-title">材料清单</h3>
          <p class="materials-subtitle">该学生已上传的申请所需材料</p>
        </div>
        <button class="refresh-button" :class="{ loading: loading }" @click="refreshMaterialsStatus"
          :disabled="loading">
          <svg class="refresh-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M3 12a9 9 0 0 1 9-9 9.75 9.75 0 0 1 6.74 2.74L21 8" />
            <path d="M21 3v5h-5" />
            <path d="M21 12a9 9 0 0 1-9 9 9.75 9.75 0 0 1-6.74-2.74L3 16" />
            <path d="M3 21v-5h5" />
          </svg>
          {{ loading ? '刷新中...' : '刷新' }}
        </button>
      </div>

      <!-- 身份证明材料 -->
      <div class="materials-category">
        <div class="category-header" @click="toggleCategory('identity')">
          <span class="category-title">身份证明材料</span>
          <ChevronDown class="category-icon" :class="{ 'expanded': expandedCategories.identity }" />
        </div>
        <div class="category-content" v-show="expandedCategories.identity">
          <div class="material-item" :class="getMaterialStatusClass('护照')">
            <div class="material-check">
              <Check v-if="getMaterialStatus('护照') === 'completed'" class="check-icon" />
              <X v-else-if="getMaterialStatus('护照') === 'error'" class="check-icon error" />
            </div>
            <div class="material-name">护照</div>
            <div class="material-status-tag" :class="getMaterialStatusClass('护照')">
              {{ getMaterialStatusText('护照') }}
            </div>
            <div class="material-actions">
              <button class="btn-action view" @click="viewMaterialDetail('护照')"
                v-if="getMaterialStatus('护照') !== 'pending'">
                <Eye class="action-icon" />
              </button>
              <button class="btn-action upload" @click="uploadMaterial('护照')">
                <Upload class="action-icon" />
              </button>
              <button class="btn-action reject" @click="rejectMaterial('护照')"
                v-if="getMaterialStatus('护照') === 'completed'">
                <RotateCcw class="action-icon" />
              </button>
            </div>
          </div>
          <div class="material-item" :class="getMaterialStatusClass('身份证')">
            <div class="material-check">
              <Check v-if="getMaterialStatus('身份证') === 'completed'" class="check-icon" />
              <X v-else-if="getMaterialStatus('身份证') === 'error'" class="check-icon error" />
            </div>
            <div class="material-name">身份证</div>
            <div class="material-status-tag" :class="getMaterialStatusClass('身份证')">
              {{ getMaterialStatusText('身份证') }}
            </div>
            <div class="material-actions">
              <button class="btn-action view" @click="viewMaterialDetail('身份证')"
                v-if="getMaterialStatus('身份证') !== 'pending'">
                <Eye class="action-icon" />
              </button>
              <button class="btn-action upload" @click="uploadMaterial('身份证')">
                <Upload class="action-icon" />
              </button>
              <button class="btn-action reject" @click="rejectMaterial('身份证')"
                v-if="getMaterialStatus('身份证') === 'completed'">
                <RotateCcw class="action-icon" />
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 学历材料 -->
      <div class="materials-category">
        <div class="category-header" @click="toggleCategory('education')">
          <span class="category-title">学历材料</span>
          <ChevronDown class="category-icon" :class="{ 'expanded': expandedCategories.education }" />
        </div>
        <div class="category-content" v-show="expandedCategories.education">
          <div class="material-item" :class="getMaterialStatusClass('成绩单')">
            <div class="material-check">
              <Check v-if="getMaterialStatus('成绩单') === 'completed'" class="check-icon" />
              <X v-else-if="getMaterialStatus('成绩单') === 'error'" class="check-icon error" />
            </div>
            <div class="material-name">成绩单</div>
            <div class="material-status-tag" :class="getMaterialStatusClass('成绩单')">
              {{ getMaterialStatusText('成绩单') }}
            </div>
            <div class="material-actions">
              <button class="btn-action view" @click="viewMaterialDetail('成绩单')"
                v-if="getMaterialStatus('成绩单') !== 'pending'">
                <Eye class="action-icon" />
              </button>
              <button class="btn-action upload" @click="uploadMaterial('成绩单')">
                <Upload class="action-icon" />
              </button>
              <button class="btn-action reject" @click="rejectMaterial('成绩单')"
                v-if="getMaterialStatus('成绩单') === 'completed'">
                <RotateCcw class="action-icon" />
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 语言成绩 -->
      <div class="materials-category">
        <div class="category-header" @click="toggleCategory('language')">
          <span class="category-title">语言成绩</span>
          <ChevronDown class="category-icon" :class="{ 'expanded': expandedCategories.language }" />
        </div>
        <div class="category-content" v-show="expandedCategories.language">
          <div class="material-item" :class="getMaterialStatusClass('语言成绩')">
            <div class="material-check">
              <Check v-if="getMaterialStatus('语言成绩') === 'completed'" class="check-icon" />
              <X v-else-if="getMaterialStatus('语言成绩') === 'error'" class="check-icon error" />
            </div>
            <div class="material-name">语言成绩</div>
            <div class="material-status-tag" :class="getMaterialStatusClass('语言成绩')">
              {{ getMaterialStatusText('语言成绩') }}
            </div>
            <div class="material-actions">
              <button class="btn-action view" @click="viewMaterialDetail('语言成绩')"
                v-if="getMaterialStatus('语言成绩') !== 'pending'">
                <Eye class="action-icon" />
              </button>
              <button class="btn-action upload" @click="uploadMaterial('语言成绩')">
                <Upload class="action-icon" />
              </button>
              <button class="btn-action reject" @click="rejectMaterial('语言成绩')"
                v-if="getMaterialStatus('语言成绩') === 'completed'">
                <RotateCcw class="action-icon" />
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 文书材料 -->
      <div class="materials-category">
        <div class="category-header" @click="toggleCategory('documents')">
          <span class="category-title">文书材料</span>
          <ChevronDown class="category-icon" :class="{ 'expanded': expandedCategories.documents }" />
        </div>
        <div class="category-content" v-show="expandedCategories.documents">
          <div class="material-item" :class="getMaterialStatusClass('个人陈述')">
            <div class="material-check">
              <Check v-if="getMaterialStatus('个人陈述') === 'completed'" class="check-icon" />
              <X v-else-if="getMaterialStatus('个人陈述') === 'error'" class="check-icon error" />
            </div>
            <div class="material-name">个人陈述</div>
            <div class="material-status-tag" :class="getMaterialStatusClass('个人陈述')">
              {{ getMaterialStatusText('个人陈述') }}
            </div>
            <div class="material-actions">
              <button class="btn-action view" @click="viewMaterialDetail('个人陈述')"
                v-if="getMaterialStatus('个人陈述') !== 'pending'">
                <Eye class="action-icon" />
              </button>
              <button class="btn-action upload" @click="uploadMaterial('个人陈述')">
                <Upload class="action-icon" />
              </button>
              <button class="btn-action reject" @click="rejectMaterial('个人陈述')"
                v-if="getMaterialStatus('个人陈述') === 'completed'">
                <RotateCcw class="action-icon" />
              </button>
            </div>
          </div>
          <div class="material-item" :class="getMaterialStatusClass('推荐信')">
            <div class="material-check">
              <Check v-if="getMaterialStatus('推荐信') === 'completed'" class="check-icon" />
              <X v-else-if="getMaterialStatus('推荐信') === 'error'" class="check-icon error" />
            </div>
            <div class="material-name">推荐信</div>
            <div class="material-status-tag" :class="getMaterialStatusClass('推荐信')">
              {{ getMaterialStatusText('推荐信') }}
            </div>
            <div class="material-actions">
              <button class="btn-action view" @click="viewMaterialDetail('推荐信')"
                v-if="getMaterialStatus('推荐信') !== 'pending'">
                <Eye class="action-icon" />
              </button>
              <button class="btn-action upload" @click="uploadMaterial('推荐信')">
                <Upload class="action-icon" />
              </button>
              <button class="btn-action reject" @click="rejectMaterial('推荐信')"
                v-if="getMaterialStatus('推荐信') === 'completed'">
                <RotateCcw class="action-icon" />
              </button>
            </div>
          </div>
          <div class="material-item" :class="getMaterialStatusClass('简历')">
            <div class="material-check">
              <Check v-if="getMaterialStatus('简历') === 'completed'" class="check-icon" />
              <X v-else-if="getMaterialStatus('简历') === 'error'" class="check-icon error" />
            </div>
            <div class="material-name">简历</div>
            <div class="material-status-tag" :class="getMaterialStatusClass('简历')">
              {{ getMaterialStatusText('简历') }}
            </div>
            <div class="material-actions">
              <button class="btn-action view" @click="viewMaterialDetail('简历')"
                v-if="getMaterialStatus('简历') !== 'pending'">
                <Eye class="action-icon" />
              </button>
              <button class="btn-action upload" @click="uploadMaterial('简历')">
                <Upload class="action-icon" />
              </button>
              <button class="btn-action reject" @click="rejectMaterial('简历')"
                v-if="getMaterialStatus('简历') === 'completed'">
                <RotateCcw class="action-icon" />
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 其他材料 -->
      <div class="materials-category">
        <div class="category-header" @click="toggleCategory('others')">
          <span class="category-title">其他材料</span>
          <ChevronDown class="category-icon" :class="{ 'expanded': expandedCategories.others }" />
        </div>
        <div class="category-content" v-show="expandedCategories.others">
          <div class="material-item" :class="getMaterialStatusClass('学术论文')">
            <div class="material-check">
              <Check v-if="getMaterialStatus('学术论文') === 'completed'" class="check-icon" />
              <X v-else-if="getMaterialStatus('学术论文') === 'error'" class="check-icon error" />
            </div>
            <div class="material-name">学术论文</div>
            <div class="material-status-tag" :class="getMaterialStatusClass('学术论文')">
              {{ getMaterialStatusText('学术论文') }}
            </div>
            <div class="material-actions">
              <button class="btn-action view" @click="viewMaterialDetail('学术论文')"
                v-if="getMaterialStatus('学术论文') !== 'pending'">
                <Eye class="action-icon" />
              </button>
              <button class="btn-action upload" @click="uploadMaterial('学术论文')">
                <Upload class="action-icon" />
              </button>
              <button class="btn-action reject" @click="rejectMaterial('学术论文')"
                v-if="getMaterialStatus('学术论文') === 'completed'">
                <RotateCcw class="action-icon" />
              </button>
            </div>
          </div>
          <div class="material-item" :class="getMaterialStatusClass('其他材料')">
            <div class="material-check">
              <Check v-if="getMaterialStatus('其他材料') === 'completed'" class="check-icon" />
              <X v-else-if="getMaterialStatus('其他材料') === 'error'" class="check-icon error" />
            </div>
            <div class="material-name">其他材料</div>
            <div class="material-status-tag" :class="getMaterialStatusClass('其他材料')">
              {{ getMaterialStatusText('其他材料') }}
            </div>
            <div class="material-actions">
              <button class="btn-action view" @click="viewMaterialDetail('其他材料')"
                v-if="getMaterialStatus('其他材料') !== 'pending'">
                <Eye class="action-icon" />
              </button>
              <button class="btn-action upload" @click="uploadMaterial('其他材料')">
                <Upload class="action-icon" />
              </button>
              <button class="btn-action reject" @click="rejectMaterial('其他材料')"
                v-if="getMaterialStatus('其他材料') === 'completed'">
                <RotateCcw class="action-icon" />
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 材料预览对话框 -->
  <el-dialog v-model="previewDialogVisible" :title="`预览 - ${currentPreviewMaterial?.name || ''}`" width="80%" top="5vh"
    :close-on-click-modal="false" class="material-preview-dialog">
    <div v-if="currentPreviewMaterial" class="preview-container">
      <!-- 文件信息 -->
      <div class="file-info">
        <div class="info-item">
          <span class="label">文件名：</span>
          <span class="value">{{ currentPreviewMaterial.fileName }}</span>
        </div>
        <div class="info-item">
          <span class="label">上传时间：</span>
          <span class="value">{{ currentPreviewMaterial.uploadTime }}</span>
        </div>
        <div class="info-item">
          <span class="label">文件大小：</span>
          <span class="value">{{ currentPreviewMaterial.fileSize }}</span>
        </div>
      </div>

      <!-- 预览内容 -->
      <div class="preview-content">
        <!-- 图片预览 -->
        <div v-if="currentPreviewMaterial.type === 'image'" class="image-preview">
          <img :src="currentPreviewMaterial.url" :alt="currentPreviewMaterial.name" class="preview-image" />
        </div>

        <!-- PDF预览 -->
        <div v-else-if="currentPreviewMaterial.type === 'pdf'" class="pdf-preview">
          <iframe :src="currentPreviewMaterial.url" class="pdf-iframe" frameborder="0"></iframe>
        </div>

        <!-- 其他文件类型 -->
        <div v-else class="unsupported-preview">
          <div class="unsupported-icon">
            <FileText size="64" />
          </div>
          <p>暂不支持预览此文件类型</p>
          <el-button type="primary" @click="downloadFile">下载文件</el-button>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="previewDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="downloadFile">下载</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { Check, ChevronDown, Eye, Upload, X, RotateCcw, FileText } from 'lucide-vue-next';
import { ElMessage, ElMessageBox, ElDialog, ElButton } from 'element-plus';

const props = defineProps({
  application: {
    type: Object,
    required: true
  },
  getMaterialsProgressColorFn: {
    type: Function,
    required: true
  }
});

const applicationId = computed(() => props.application.id);

// 控制分类的展开/折叠状态
const expandedCategories = reactive({
  identity: true,
  education: true,
  language: true,
  documents: true,
  others: true
});

// 预览对话框相关状态
const previewDialogVisible = ref(false);
const currentPreviewMaterial = ref(null);

// 切换分类的展开/折叠状态
const toggleCategory = (category) => {
  expandedCategories[category] = !expandedCategories[category];
};

// 材料状态数据
const materialStatus = reactive({});
const loading = ref(false);

// 材料类型映射 - 与学生端保持一致
const materialTypeMapping = {
  'personalStatement': '个人陈述',
  'transcript': '成绩单',
  'recommendationLetters': '推荐信',
  'resume': '简历',
  'passport': '护照',
  'idCard': '身份证',
  'languageScores': '语言成绩',
  'papers': '学术论文',
  'others': '其他材料'
};

// 获取学生材料状态
const fetchMaterialsStatus = async () => {
  if (!props.application?.studentId || !props.application?.id) {
    console.warn('缺少学生ID或申请ID');
    return;
  }

  loading.value = true;
  try {
    const response = await fetch(`/api/files/admin/materials/student/${props.application.studentId}/application/${props.application.id}`);
    const result = await response.json();

    if (result.success) {
      // 清空现有状态
      Object.keys(materialStatus).forEach(key => {
        delete materialStatus[key];
      });

      // 后端返回的键名到前端键名的映射
      const backendToFrontendMapping = {
        'passport': 'passport',
        'id_card': 'idCard',
        'transcript': 'transcript', 
        'personal_statement': 'personalStatement',
        'recommendation_letter': 'recommendationLetters',
        'resume': 'resume',
        'language_scores': 'languageScores',
        'research_papers': 'papers',
        'other': 'others'
      };

      // 更新材料状态
      Object.entries(result.data).forEach(([backendKey, status]) => {
        const frontendKey = backendToFrontendMapping[backendKey] || backendKey;
        const materialName = materialTypeMapping[frontendKey] || frontendKey;
        materialStatus[materialName] = {
          status: status.uploaded ? 'completed' : 'pending',
          uploaded: status.uploaded,
          fileName: status.fileName,
          uploadTime: status.uploadTime,
          fileSize: status.fileSize,
          fileId: status.fileId,
          filePath: status.filePath
        };
      });
    } else {
      ElMessage.error('获取材料状态失败：' + result.message);
    }
  } catch (error) {
    console.error('获取材料状态失败:', error);
    ElMessage.error('获取材料状态失败');
  } finally {
    loading.value = false;
  }
};

// 获取材料状态
const getMaterialStatus = (materialName) => {
  const material = materialStatus[materialName];
  return material ? material.status : 'pending';
};

// 获取材料状态对应的类名
const getMaterialStatusClass = (materialName) => {
  const status = getMaterialStatus(materialName);
  return status;
};

// 获取材料状态对应的文本
const getMaterialStatusText = (materialName) => {
  const status = getMaterialStatus(materialName);
  const statusMap = {
    'completed': '已上传',
    'pending': '未上传',
    'error': '文件错误'
  };
  return statusMap[status] || '未上传';
};

// 查看材料详细
const viewMaterialDetail = (materialName) => {
  const material = materialStatus[materialName];

  // 如果材料未上传，提示用户
  if (!material || !material.uploaded) {
    ElMessage.warning(`${materialName}尚未上传`);
    return;
  }

  // 构建文件预览URL
  const fileUrl = `/api/files/download/${props.application.studentId}/${props.application.id}/${material.fileName}`;

  // 确定文件类型
  const fileExtension = material.fileName.split('.').pop().toLowerCase();
  let fileType = 'other';
  if (['jpg', 'jpeg', 'png', 'gif'].includes(fileExtension)) {
    fileType = 'image';
  } else if (fileExtension === 'pdf') {
    fileType = 'pdf';
  }

  // 打开预览对话框
  previewDialogVisible.value = true;
  currentPreviewMaterial.value = {
    name: materialName,
    type: fileType,
    url: fileUrl,
    fileName: material.fileName,
    uploadTime: material.uploadTime,
    fileSize: formatFileSize(material.fileSize)
  };

  ElMessage.success(`正在加载${materialName}预览`);
};

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (!bytes) return '未知';
  if (bytes < 1024) return bytes + ' B';
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
};

// 获取材料文件信息（从实际数据中获取）
const getMaterialFileInfo = (materialName) => {
  if (!materialsStatus.value || !materialsStatus.value.data) {
    return null;
  }

  // 查找对应的材料数据
  const materialData = materialsStatus.value.data.find(material => {
    const displayName = materialTypeMapping[material.materialType] || material.materialType;
    return displayName === materialName;
  });

  if (!materialData || !materialData.fileInfo) {
    return null;
  }

  // 返回实际的文件信息
  return {
    type: materialData.fileInfo.contentType?.startsWith('image/') ? 'image' : 'pdf',
    url: materialData.fileInfo.downloadUrl || materialData.fileInfo.url,
    fileName: materialData.fileInfo.originalName || materialData.fileInfo.fileName,
    uploadTime: materialData.uploadTime || materialData.fileInfo.uploadTime,
    fileSize: formatBytes(materialData.fileInfo.size || 0)
  };
};

// 下载文件
const downloadFile = () => {
  if (!currentPreviewMaterial.value) {
    ElMessage.error('无法下载文件');
    return;
  }

  // 构建下载URL，添加download参数
  const downloadUrl = `${currentPreviewMaterial.value.url}?download=true`;

  // 创建下载链接
  const link = document.createElement('a');
  link.href = downloadUrl;
  link.download = currentPreviewMaterial.value.fileName;
  link.target = '_blank';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);

  ElMessage.success(`正在下载 ${currentPreviewMaterial.value.fileName}`);
};

// 刷新材料状态
const refreshMaterialsStatus = async () => {
  await fetchMaterialsStatus();
  ElMessage.success('材料状态已刷新');
};

// 上传材料（管理员代为上传）
const uploadMaterial = async (materialName) => {
  try {
    ElMessage.info(`正在代为上传${materialName}...`);
    
    // 创建文件上传输入元素
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = materialName.includes('护照') || materialName.includes('身份证') ? 'image/*' : '.pdf,.doc,.docx';
    
    input.onchange = async (event) => {
      const file = event.target.files[0];
      if (!file) return;
      
      try {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('materialType', getMaterialTypeByName(materialName));
        
        // 调用上传API
        const response = await fetch(`/api/files/admin/materials/student/${props.application.studentId}/application/${props.application.id}`, {
          method: 'POST',
          body: formData
        });
        
        if (!response.ok) {
          throw new Error('上传失败');
        }
        
        const result = await response.json();
        if (result.success) {
          // 刷新材料状态
          await refreshMaterialsStatus();
          ElMessage.success(`${materialName}上传成功`);
        } else {
          throw new Error(result.message || '上传失败');
        }
      } catch (error) {
        console.error('材料上传失败:', error);
        ElMessage.error(`${materialName}上传失败：${error.message}`);
      }
    };
    
    input.click();
  } catch (error) {
    console.error('上传材料失败:', error);
    ElMessage.error(`上传${materialName}失败，请重试`);
  }
};

// 打回材料
const rejectMaterial = (materialName) => {
  // 如果材料未上传，不能打回
  if (getMaterialStatus(materialName) === 'pending') {
    ElMessage.warning(`${materialName}尚未上传，无法打回`);
    return;
  }

  // 使用确认对话框获取打回原因
  ElMessageBox.prompt('请输入打回原因', `打回${materialName}`, {
    confirmButtonText: '确认打回',
    cancelButtonText: '取消',
    inputPlaceholder: '请详细说明打回原因，以便学生重新上传',
    inputValidator: (value) => {
      if (!value || value.trim().length < 5) {
        return '打回原因不能少于5个字符';
      }
      return true;
    }
  }).then(async ({ value: reason }) => {
    try {
      // 同步到后端和学生端，包含打回原因
      await syncMaterialStatusToStudent(materialName, 'error', reason);

      // 刷新材料状态以获取最新数据
      await refreshMaterialsStatus();

      // 更新申请进度
      updateApplicationProgress();

      ElMessage.success(`${materialName}已被打回，学生将收到通知`);
      console.log(`打回材料：${materialName}，原因：${reason}`);
    } catch (error) {
      console.error('材料打回同步失败:', error);
      ElMessage.error(`${materialName}打回失败，请重试`);
      // 恢复原状态
      materialStatus[materialName] = 'completed';
    }
  }).catch(() => {
    ElMessage.info('已取消打回操作');
  });
};

// 根据材料名称获取材料类型
const getMaterialTypeByName = (materialName) => {
  const nameToTypeMap = {
    '护照': 'passport',
    '身份证': 'idCard', 
    '成绩单': 'transcript',
    '语言成绩': 'languageScores',
    '个人陈述': 'personalStatement',
    '推荐信': 'recommendationLetters',
    '简历': 'resume',
    '学术论文': 'papers',
    '其他材料': 'others'
  };
  return nameToTypeMap[materialName] || 'others';
};

// 同步材料状态到学生端
const syncMaterialStatusToStudent = async (materialName, status, reason = '') => {
  try {
    const materialType = getMaterialTypeByName(materialName);
    
    const response = await fetch(`/api/files/admin/materials/student/${props.application.studentId}/application/${props.application.id}/status`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        materialType,
        status,
        reason
      })
    });
    
    if (!response.ok) {
      throw new Error('同步失败');
    }
    
    const result = await response.json();
    if (!result.success) {
      throw new Error(result.message || '同步失败');
    }
    
    return result;
  } catch (error) {
    console.error('同步材料状态失败:', error);
    throw error;
  }
};

// 更新申请进度
const updateApplicationProgress = () => {
  const totalMaterials = Object.keys(materialStatus).length;
  const completedMaterials = Object.values(materialStatus).filter(status => status === 'completed').length;
  const progress = Math.round((completedMaterials / totalMaterials) * 100);

  // 更新申请对象的进度
  if (props.application) {
    props.application.materialsProgress = progress;
  }

  console.log(`申请进度已更新：${progress}%`);
};

// 初始化
onMounted(async () => {
  console.log('材料清单组件已加载', applicationId.value);

  // 获取真实的材料状态数据
  await fetchMaterialsStatus();
});
</script>

<style scoped>
.materials-progress-wrapper {
  margin-bottom: 20px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.progress-header h5 {
  font-size: 16px;
  font-weight: 500;
  margin: 0;
  color: #333;
}

.progress-bar-wrapper {
  position: relative;
  height: 8px;
  background-color: #f5f5f5;
  border-radius: 4px;
  overflow: hidden;
}

.progress-bar-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #f0f0f0;
}

.progress-bar-fill {
  position: absolute;
  top: 0;
  left: 0;
  bottom: 0;
  height: 100%;
  transition: width 0.3s ease;
}

/* Materials.vue styles */
.materials-checklist-wrapper {
  width: 100%;
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.materials-header {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.materials-header-content {
  flex: 1;
}

.materials-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.materials-subtitle {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.refresh-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  color: #64748b;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  min-width: 80px;
  justify-content: center;
}

.refresh-button:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
  color: #475569;
}

.refresh-button:active {
  background: #e2e8f0;
  transform: translateY(1px);
}

.refresh-button.loading {
  cursor: not-allowed;
  opacity: 0.7;
}

.refresh-icon {
  width: 16px;
  height: 16px;
  transition: transform 0.3s ease;
}

.refresh-button.loading .refresh-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

.materials-category {
  border-bottom: 1px solid #f0f0f0;
}

.materials-category:last-child {
  border-bottom: none;
}

.category-header {
  padding: 16px 20px;
  background-color: #f5f7fa;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  transition: background-color 0.2s;
}

.category-header:hover {
  background-color: #e6f7ff;
}

.category-title {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.category-icon {
  color: #999;
  width: 18px;
  height: 18px;
  transition: transform 0.3s;
}

.category-icon.expanded {
  transform: rotate(180deg);
}

.category-content {
  transition: max-height 0.3s ease;
}

.material-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  border-bottom: 1px solid #f9f9f9;
  transition: background-color 0.2s;
}

.material-item:last-child {
  border-bottom: none;
}

.material-item:hover {
  background-color: #fafafa;
}

.material-item.completed {
  background-color: #f6ffed;
}

.material-item.error {
  background-color: #fff2f0;
}

.material-item.pending {
  background-color: #f9f9f9;
}

.material-check {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 1px solid #d9d9d9;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  flex-shrink: 0;
}

.material-item.completed .material-check {
  border-color: #52c41a;
  background-color: #52c41a;
}

.material-item.error .material-check {
  border-color: #f5222d;
  background-color: #f5222d;
}

.check-icon {
  color: #fff;
  width: 14px;
  height: 14px;
}

.check-icon.error {
  color: #fff;
}

.material-name {
  flex: 1;
  font-size: 14px;
  color: #333;
  margin-right: 12px;
}

.material-status-tag {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  margin-right: 16px;
  flex-shrink: 0;
}

.material-status-tag.completed {
  background-color: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.material-status-tag.error {
  background-color: #fff2f0;
  color: #f5222d;
  border: 1px solid #ffccc7;
}

.material-status-tag.pending {
  background-color: #f5f5f5;
  color: #999;
  border: 1px solid #d9d9d9;
}

.material-actions {
  display: flex;
  gap: 8px;
}

.btn-action {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 4px;
  background-color: transparent;
  border: 1px solid transparent;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-action.view {
  border-color: #1890ff;
  color: #1890ff;
}

.btn-action.view:hover {
  background-color: #e6f7ff;
}

.btn-action.upload {
  border-color: #52c41a;
  color: #52c41a;
}

.btn-action.upload:hover {
  background-color: #f6ffed;
}

.btn-action.reject {
  border-color: #f5222d;
  color: #f5222d;
}

.btn-action.reject:hover {
  background-color: #fff2f0;
}

.action-icon {
  width: 16px;
  height: 16px;
}

/* 响应式调整 */
@media (max-width: 992px) {
  .material-actions {
    flex-direction: column;
  }

  .btn-action {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .material-item {
    flex-wrap: wrap;
    padding: 16px;
  }

  .material-status-tag {
    margin-left: 32px;
    margin-top: 4px;
    margin-bottom: 8px;
  }

  .material-actions {
    flex-direction: row;
    width: 100%;
    margin-top: 8px;
    margin-left: 32px;
  }

  .btn-action {
    flex: 1;
    justify-content: center;
  }

  .category-header {
    padding: 14px 16px;
  }

  .materials-header {
    padding: 16px;
  }
}

/* 预览对话框样式 */
.material-preview-dialog {
  .preview-container {
    max-height: 70vh;
    overflow: hidden;
    display: flex;
    flex-direction: column;
  }

  .file-info {
    background: #f8fafc;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 16px;
    display: flex;
    gap: 24px;
    flex-wrap: wrap;

    .info-item {
      display: flex;
      align-items: center;
      gap: 8px;

      .label {
        font-weight: 500;
        color: #64748b;
        min-width: 80px;
      }

      .value {
        color: #1e293b;
        font-weight: 500;
      }
    }
  }

  .preview-content {
    flex: 1;
    min-height: 500px;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    overflow: hidden;
    background: #ffffff;
  }

  .image-preview {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px;

    .preview-image {
      max-width: 100%;
      max-height: 100%;
      object-fit: contain;
      border-radius: 4px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
  }

  .pdf-preview {
    width: 100%;
    height: 100%;

    .pdf-iframe {
      width: 100%;
      height: 500px;
      border: none;
    }
  }

  .unsupported-preview {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #64748b;
    gap: 16px;

    .unsupported-icon {
      color: #94a3b8;
    }

    p {
      margin: 0;
      font-size: 16px;
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
}
</style>