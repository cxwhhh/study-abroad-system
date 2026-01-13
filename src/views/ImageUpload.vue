<template>
  <div class="image-upload-page">
    <nav-bar></nav-bar>

    <div class="upload-container">
      <h2>图片上传</h2>

      <el-upload v-bind="uploadConfig" class="upload-demo" :on-success="handleUploadSuccess"
        :on-error="handleUploadError" :before-upload="beforeUpload" name="file" multiple drag>
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          拖拽文件到此处或 <em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            只能上传jpg/png文件，且不超过10MB
          </div>
        </template>
      </el-upload>

      <!-- 已上传图片列表 -->
      <div class="image-list" v-if="imageList.length > 0">
        <h3>已上传图片</h3>
        <el-row :gutter="20">
          <el-col :span="6" v-for="(image, index) in imageList" :key="index">
            <el-card :body-style="{ padding: '0px' }" v-if="!image.loadError">
              <img :src="getImageUrl(image.filePath)" 
                   class="image" 
                   @error="handleImageError($event, image)"
                   @load="handleImageLoad(image)">
              <div class="image-info">
                <span>{{ image.originalName }}</span>
                <div class="bottom">
                  <time>{{ formatDate(image.uploadTime) }}</time>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>

    <footer-bar></footer-bar>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import NavBar from '../components/NavBar.vue'
import FooterBar from '../components/FooterBar.vue'
import axios from 'axios'

const imageList = ref([])
const headers = {
  'Accept': 'application/json',
  'X-Requested-With': 'XMLHttpRequest'
}

// 获取已上传的图片列表
const fetchImages = async () => {
  try {
    const response = await axios.get('/api/images', {
      headers: headers,
      withCredentials: true,
      timeout: 5000
    });
    console.log('获取图片列表响应:', response);
    imageList.value = response.data.map(image => ({
      ...image,
      loadError: false
    }));
  } catch (error) {
    console.error('获取图片列表失败:', error.response || error);
    ElMessage.error(`获取图片列表失败: ${error.response?.data?.message || error.message}`);
  }
}

// 上传成功处理
const handleUploadSuccess = (response) => {
  console.log('上传成功响应:', response)
  ElMessage.success('上传成功')
  fetchImages()  // 刷新图片列表
}

// 上传失败处理
const handleUploadError = (error) => {
  console.error('上传失败:', error)
  const errorMessage = error.response?.data?.message || error.message || '未知错误'
  ElMessage.error('上传失败: ' + errorMessage)
}

// 上传前的验证
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!')
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

// 修改上传组件的配置
const uploadConfig = {
  headers: headers,
  action: 'http://localhost:8080/api/images/upload',
  data: () => {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    return {
      userId: userInfo.id  // 从localStorage中获取用户ID
    };
  },
  withCredentials: true,
  multiple: true,
  name: 'file',
  accept: 'image/*'
}

// 处理图片URL
const getImageUrl = (filePath) => {
  if (!filePath) return '';
  // 如果路径已经是完整的URL，直接返回
  if (filePath.startsWith('http')) return filePath;
  // 否则，拼接基础URL
  return `http://localhost:8080${filePath}`;
}

// 处理图片加载错误
const handleImageError = (e, image) => {
  console.error('图片加载失败:', image.filePath);
  image.loadError = true;  // 标记图片加载失败
  // 从列表中移除加载失败的图片
  imageList.value = imageList.value.filter(img => !img.loadError);
}

// 处理图片加载成功
const handleImageLoad = (image) => {
  image.loadError = false;
}

onMounted(() => {
  fetchImages()
})
</script>

<style scoped>
.image-upload-page {
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

.image-list {
  margin-top: 40px;
}

.image-list h3 {
  margin-bottom: 20px;
  color: #333;
}

.image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.image-info {
  padding: 14px;
}

.image-info span {
  font-size: 14px;
  color: #333;
  word-break: break-all;
}

.bottom {
  margin-top: 8px;
  font-size: 12px;
  color: #999;
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

.el-col {
  margin-bottom: 20px;
}
</style>