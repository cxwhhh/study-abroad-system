<template>
  <div class="file-upload-page">
    <nav-bar></nav-bar>

    <div class="upload-container">
      <h2>文件上传</h2>

      <el-upload v-bind="uploadConfig" class="upload-demo" :on-success="handleUploadSuccess"
        :on-error="handleUploadError" :before-upload="beforeUpload" :on-remove="handleRemove" name="file" multiple drag>
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          拖拽文件到此处或 <em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持各种类型的文件，单个文件不超过10MB
          </div>
        </template>
      </el-upload>

      <!-- 文件描述输入框 -->
      <el-input v-model="fileDescription" type="textarea" :rows="2" placeholder="请输入文件描述（可选）"
        class="description-input" />

      <!-- 已上传文件列表 -->
      <div class="file-list">
        <h3>已上传文件</h3>
        <el-table :data="fileList.filter(file => !file.loadError)" style="width: 100%" v-loading="loading"
          element-loading-text="加载中..." :empty-text="emptyText">
          <el-table-column prop="originalName" label="文件名" width="250">
            <template #default="scope">
              <el-link type="primary" :underline="false" @click="previewFile(scope.row)"
                :disabled="!isPreviewable(scope.row.fileType)">
                {{ scope.row.originalName }}
              </el-link>
            </template>
          </el-table-column>
          <el-table-column prop="fileType" label="类型" width="100">
            <template #default="scope">
              <el-tag :type="getFileTypeTagType(scope.row.fileType)" size="small">
                {{ scope.row.fileType.toUpperCase() }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="fileSize" label="大小" width="120">
            <template #default="scope">
              {{ formatFileSize(scope.row.fileSize) }}
            </template>
          </el-table-column>
          <el-table-column prop="description" label="描述" show-overflow-tooltip />
          <el-table-column prop="uploadTime" label="上传时间" width="180">
            <template #default="scope">
              {{ formatDate(scope.row.uploadTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="scope">
              <el-button-group>
                <el-tooltip content="预览文件" placement="top" :disabled="!isPreviewable(scope.row.fileType)">
                  <el-button type="primary" :icon="View" @click="previewFile(scope.row)"
                    :disabled="!isPreviewable(scope.row.fileType)" size="small">
                    预览
                  </el-button>
                </el-tooltip>
                <el-tooltip content="下载文件" placement="top">
                  <el-button type="success" :icon="Download" @click="downloadFile(scope.row)" size="small">
                    下载
                  </el-button>
                </el-tooltip>
              </el-button-group>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <footer-bar></footer-bar>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { View, Download } from '@element-plus/icons-vue'
import NavBar from '../components/NavBar.vue'
import FooterBar from '../components/FooterBar.vue'
import axios from 'axios'

const router = useRouter();

const fileList = ref([])
const fileDescription = ref('')
const loading = ref(false)
const emptyText = ref('暂无数据')
const headers = {
  'Accept': 'application/json'
}

// 获取已上传的文件列表
const fetchFiles = async () => {
  loading.value = true;
  emptyText.value = '加载中...';
  try {
    console.log('开始获取文件列表...');
    const response = await axios.get('http://localhost:8080/api/files', {
      headers: headers,
      withCredentials: true,
      timeout: 5000
    })
    console.log('获取文件列表响应:', response);

    if (response.data && Array.isArray(response.data)) {
      // 初始化文件加载状态
      fileList.value = response.data.map(file => ({
        ...file,
        loadError: false
      }));
      console.log('更新文件列表:', fileList.value);
      if (fileList.value.length === 0) {
        console.log('文件列表为空');
        emptyText.value = '暂无上传的文件';
      }
    } else {
      console.error('文件列表数据格式不正确:', response.data);
      ElMessage.error('获取文件列表失败: 数据格式不正确');
      emptyText.value = '数据格式不正确';
    }
  } catch (error) {
    console.error('获取文件列表失败:', error);
    if (error.response) {
      console.error('错误响应:', error.response.data);
      console.error('状态码:', error.response.status);
      console.error('响应头:', error.response.headers);
      emptyText.value = `获取数据失败: ${error.response.status}`;
    } else if (error.request) {
      console.error('请求未收到响应:', error.request);
      emptyText.value = '服务器未响应';
    } else {
      console.error('请求配置错误:', error.message);
      emptyText.value = '请求失败';
    }
    ElMessage.error(`获取文件列表失败: ${error.response?.data?.message || error.message}`);
  } finally {
    loading.value = false;
  }
}
// 获取当前用户ID的函数
const getCurrentUserId = () => {
  // 这里可以根据你的安全框架获取当前用户的ID
  return 1; // 示例返回值，实际应根据当前用户返回
}

// 上传成功处理
const handleUploadSuccess = (response, uploadFile) => {
  console.log('上传成功响应:', response);
  console.log('上传的文件信息:', uploadFile);
  ElMessage.success('上传成功');
  fileDescription.value = ''; // 清空描述
  fetchFiles();  // 刷新文件列表
}

// 上传失败处理
const handleUploadError = (error, uploadFile) => {
  console.error('上传失败:', error);
  console.error('上传失败的文件:', uploadFile);
  const errorMessage = error.response?.data?.message || error.message || '未知错误';
  if (errorMessage.includes('用户未登录')) {
    ElMessage.warning('请先登录');
    router.push('/login');
  } else {
    ElMessage.error('上传失败: ' + errorMessage);
  }
}

// 上传前的验证
const beforeUpload = (file) => {
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isLt10M) {
    ElMessage.error('文件大小不能超过 10MB!')
    return false
  }
  return true
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 格式化文件大小
const formatFileSize = (size) => {
  if (!size) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  let index = 0
  let fileSize = size

  while (fileSize >= 1024 && index < units.length - 1) {
    fileSize /= 1024
    index++
  }

  return `${fileSize.toFixed(2)} ${units[index]}`
}

// 判断文件是否可预览
const isPreviewable = (fileType) => {
  const previewableTypes = [
    'pdf', 'jpg', 'jpeg', 'png', 'gif', 'svg',
    'mp4', 'webm', 'mp3', 'wav', 'txt'
  ];
  return previewableTypes.includes(fileType.toLowerCase());
}

// 预览文件
const previewFile = async (file) => {
  try {
    const response = await axios.head(`http://localhost:8080${file.filePath}`, {
      headers: headers,
      withCredentials: true,
      timeout: 5000
    });

    if (response.status === 200) {
      // 如果文件存在，则在新窗口中预览
      window.open(`http://localhost:8080${file.filePath}`, '_blank');
    } else {
      throw new Error('文件不存在或无法访问');
    }
  } catch (error) {
    console.error('文件访问失败:', error);
    file.loadError = true;
    if (error.response) {
      ElMessage.error(`文件访问失败: ${error.response.status === 404 ? '文件不存在' : '服务器错误'}`);
    } else if (error.request) {
      ElMessage.error('服务器未响应，请检查网络连接');
    } else {
      ElMessage.error(error.message || '文件访问失败');
    }
    // 从列表中移除不可访问的文件
    fileList.value = fileList.value.filter(f => !f.loadError);
  }
}

// 下载文件
const downloadFile = async (file) => {
  try {
    const response = await axios.head(`http://localhost:8080${file.filePath}`, {
      headers: headers,
      withCredentials: true,
      timeout: 5000
    });

    if (response.status === 200) {
      // 如果文件存在，则下载
      window.open(`http://localhost:8080${file.filePath}?download=true`, '_blank');
    } else {
      throw new Error('文件不存在或无法访问');
    }
  } catch (error) {
    console.error('文件访问失败:', error);
    file.loadError = true;
    if (error.response) {
      ElMessage.error(`文件访问失败: ${error.response.status === 404 ? '文件不存在' : '服务器错误'}`);
    } else if (error.request) {
      ElMessage.error('服务器未响应，请检查网络连接');
    } else {
      ElMessage.error(error.message || '文件访问失败');
    }
    // 从列表中移除不可访问的文件
    fileList.value = fileList.value.filter(f => !f.loadError);
  }
}

// 获取文件类型的标签样式
const getFileTypeTagType = (fileType) => {
  const typeMap = {
    'pdf': 'danger',
    'doc': 'primary',
    'docx': 'primary',
    'xls': 'success',
    'xlsx': 'success',
    'txt': 'info',
    'jpg': 'warning',
    'jpeg': 'warning',
    'png': 'warning',
    'gif': 'warning'
  };
  return typeMap[fileType.toLowerCase()] || '';
}

// 处理文件移除
const handleRemove = (file) => {
  ElMessageBox.confirm(
    '确定要移除这个文件吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    // 这里可以添加删除文件的 API 调用
    ElMessage.success('文件已移除');
  }).catch(() => {
    // 用户取消删除
  });
}

// 修改上传组件的配置
const uploadConfig = {
  headers: Object.assign({}, headers, {
    'X-Requested-With': 'XMLHttpRequest'
  }),
  action: '/api/files/upload',
  data: () => ({
    description: fileDescription.value
    // 不需要在前端传递 userId，后端会从 session 中获取
  }),
  withCredentials: true,
  showFileList: true,
  autoUpload: true,
  multiple: true,
  limit: 10, // 最多同时上传10个文件
  onExceed: (files) => {
    ElMessage.warning(`最多同时上传 10 个文件，本次选择了 ${files.length} 个文件`);
  }
}

onMounted(async () => {
  // 检查登录状态
  try {
    const response = await fetch('/api/files/check-login', {
      credentials: 'include'
    });
    const data = await response.json();
    if (!data.loggedIn) {
      ElMessage.warning('请先登录');
      router.push('/login');
      return;
    }
    // 获取文件列表
  } catch (error) {
    console.error('检查登录状态时发生错误:', error);
    ElMessage.error('检查登录状态失败，请稍后重试');
    router.push('/login');
    return;
  }
  fetchFiles(); // 如果已登录，获取文件列表
});
</script>

<style scoped>
.file-upload-page {
  padding-top: 44px;
  min-height: 100vh;
  background: #f5f7fa;
}

.upload-container {
  max-width: 1200px;
  margin: 40px auto;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.upload-container h2 {
  margin-bottom: 20px;
  color: #333;
}

.description-input {
  margin: 20px 0;
  max-width: 100%;
}

.file-list {
  margin-top: 40px;
}

.file-list h3 {
  margin-bottom: 20px;
  color: #333;
  font-size: 18px;
  font-weight: 500;
}

:deep(.el-table) {
  margin-top: 20px;
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #333;
  font-weight: 500;
}

:deep(.el-table__row) {
  cursor: pointer;
  transition: all 0.3s;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

:deep(.el-tag) {
  text-transform: uppercase;
}

:deep(.el-button-group) {
  display: flex;
  gap: 5px;
}

:deep(.el-tooltip__trigger) {
  display: inline-flex;
}

:deep(.el-upload) {
  width: 100%;
}

:deep(.el-upload-dragger) {
  width: 100%;
  height: 200px;
}

:deep(.el-icon--upload) {
  font-size: 48px;
  color: #409EFF;
  margin-bottom: 10px;
}

:deep(.el-upload__text) {
  font-size: 16px;
}

:deep(.el-upload__tip) {
  font-size: 12px;
  color: #999;
  margin-top: 10px;
}

:deep(.el-upload-dragger:hover) {
  border-color: #409EFF;
  background-color: #f5f7fa;
}
</style>