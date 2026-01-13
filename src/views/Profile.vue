<template>
  <div class="profile-page">
    <nav-bar></nav-bar>

    <!-- 加载中 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="10" animated />
    </div>

    <!-- 错误提示 -->
    <div v-else-if="error" class="error-container">
      <el-result icon="error" :title="error" sub-title="请刷新页面或联系管理员">
        <template #extra>
          <el-button type="primary" @click="() => window.location.reload()">刷新页面</el-button>
        </template>
      </el-result>
    </div>

    <!-- 主要内容 -->
    <div v-else>
      <div class="profile-header-section">
        <div class="profile-container">
          <div class="profile-header">
            <div class="user-info">
              <div class="avatar-container">
                <el-avatar :size="100" :src="userInfo.avatar || getRandomAvatar(userInfo.id)"></el-avatar>
                <div class="avatar-overlay" @click="showAvatarSelector">
                  <el-icon class="camera-icon">
                    <Camera />
                  </el-icon>
                  <span class="change-text">更换头像</span>
                </div>
              </div>
              <div class="user-details">
                <h2>{{ studentInfo.name || userInfo.username }}</h2>
                <div class="user-meta">
                  <span>ID: {{ userInfo.id }}</span>
                  <span class="divider">|</span>
                  <span>加入时间：{{ formatDate(userInfo.createTime) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="profile-container">
        <div class="profile-content">
          <div class="info-card">
            <div class="section-header">
              <h3>基本信息</h3>
              <el-button class="edit-btn" type="primary" @click="editStudentInfo">
                {{ isEditing ? '保存' : '编辑' }}
              </el-button>
            </div>
            <el-form ref="studentForm" :model="studentInfo" :rules="rules" :disabled="!isEditing" label-width="100px"
              class="student-form">
              <div class="form-grid">
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="姓名" prop="name">
                      <el-input v-model="studentInfo.name" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="英文名" prop="englishName">
                      <el-input v-model="studentInfo.englishName" />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="性别" prop="gender">
                      <el-select v-model="studentInfo.gender" style="width: 100%">
                        <el-option label="男" value="男" />
                        <el-option label="女" value="女" />
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="出生日期" prop="birthDate">
                      <el-date-picker v-model="studentInfo.birthDate" type="date" style="width: 100%" />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="国籍" prop="nationality">
                      <el-input v-model="studentInfo.nationality" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="护照号" prop="passportNo">
                      <el-input v-model="studentInfo.passportNo" />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="手机" prop="phone">
                      <el-input v-model="studentInfo.phone" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="邮箱" prop="email">
                      <el-input v-model="studentInfo.email" />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="微信" prop="wechat">
                      <el-input v-model="studentInfo.wechat" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="GPA" prop="gpa">
                      <div style="display: flex; gap: 12px; align-items: center;">
                        <el-select v-model="studentInfo.gpaScale" placeholder="分制" style="width: 120px">
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
                        <el-input v-model="studentInfo.gpa" />
                      </div>
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="当前学校" prop="currentSchool">
                      <el-input v-model="studentInfo.currentSchool" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="专业" prop="major">
                      <el-input v-model="studentInfo.major" />
                    </el-form-item>
                  </el-col>
                </el-row>
              </div>
            </el-form>
          </div>
        </div>
      </div>
    </div>

    <!-- 头像选择对话框 -->
    <el-dialog v-model="avatarDialogVisible" title="选择头像" width="600px" :before-close="handleAvatarDialogClose">
      <div class="avatar-selection">
        <div class="avatar-options">
          <h4>预设头像</h4>
          <div class="preset-avatars">
            <div v-for="(avatar, index) in presetAvatars" :key="index" class="avatar-option"
              :class="{ selected: selectedAvatar === avatar }" @click="selectAvatar(avatar)">
              <el-avatar :size="60" :src="avatar"></el-avatar>
            </div>
          </div>
        </div>

        <div class="upload-section">
          <h4>上传自定义头像</h4>
          <el-upload class="avatar-uploader" action="#" :show-file-list="false" :before-upload="beforeAvatarUpload"
            :http-request="handleAvatarUpload" accept="image/*">
            <div class="upload-area">
              <el-icon class="upload-icon">
                <Plus />
              </el-icon>
              <div class="upload-text">点击上传图片</div>
              <div class="upload-hint">支持 JPG、PNG 格式，大小不超过 2MB</div>
            </div>
          </el-upload>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="avatarDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmAvatarChange" :disabled="!selectedAvatar">确认更换</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Camera, Plus } from '@element-plus/icons-vue'
import NavBar from '../components/NavBar.vue'
import request from '../utils/request'
import sessionManager from '../utils/sessionManager'

const router = useRouter()

// 初始化用户信息
const userInfo = ref({
  id: '',
  username: '',
  createTime: '',
  avatar: '',
  email: '',
  phone: '',
  status: 1
})

// 基于用户ID生成固定的随机头像
const getRandomAvatar = (userId) => {
  const avatars = [
    '/images/avatar1.jpg',
    '/images/avatar2.jpg',
    '/images/avatar3.jpg',
    '/images/1.jpg',
    '/images/2.jpg',
    '/images/3.jpg',
    '/images/4.jpg',
    '/images/5.jpg',
    '/images/6.jpg',
    '/images/7.jpg',
    '/images/8.jpg'
  ]
  // 使用用户ID作为种子生成固定的随机索引
  const seed = userId || 1
  const index = seed % avatars.length
  return avatars[index]
}

// 初始化页面状态
const loading = ref(true)
const error = ref(null)
const timeout = ref(null)

// 设置超时处理
const setupTimeout = () => {
  clearTimeout(timeout.value)
  timeout.value = setTimeout(() => {
    if (loading.value) {
      loading.value = false
      error.value = '加载超时，请刷新页面重试'
      ElMessage.error('加载超时')
    }
  }, 5000)
}

// 清除超时处理
const clearPageTimeout = () => {
  if (timeout.value) {
    clearTimeout(timeout.value)
    timeout.value = null
  }
}

// 初始化学生信息
const studentInfo = ref({
  id: null,
  name: '',
  englishName: '',
  gender: '',
  birthDate: '',
  nationality: '',
  passportNo: '',
  phone: '',
  email: '',
  wechat: '',
  currentSchool: '',
  major: '',
  gpa: '',
  gpaScale: '4.0',
  userId: null
})
const isEditing = ref(false)
const studentForm = ref(null)

// 头像相关状态
const avatarDialogVisible = ref(false)
const selectedAvatar = ref('')
const presetAvatars = ref([
  '/images/avatar1.jpg',
  '/images/avatar2.jpg',
  '/images/avatar3.jpg',
  '/images/1.jpg',
  '/images/2.jpg',
  '/images/3.jpg',
  '/images/4.jpg',
  '/images/5.jpg',
  '/images/6.jpg',
  '/images/7.jpg',
  '/images/8.jpg'
])

// 显示头像选择器
const showAvatarSelector = () => {
  selectedAvatar.value = userInfo.value.avatar || ''
  avatarDialogVisible.value = true
}

// 选择头像
const selectAvatar = (avatar) => {
  selectedAvatar.value = avatar
}

// 处理对话框关闭
const handleAvatarDialogClose = (done) => {
  selectedAvatar.value = ''
  done()
}

// 上传前验证
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片格式的文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 处理头像上传
const handleAvatarUpload = (options) => {
  const file = options.file
  const reader = new FileReader()

  reader.onload = (e) => {
    selectedAvatar.value = e.target.result
    ElMessage.success('图片上传成功!')
  }

  reader.readAsDataURL(file)
}

// 确认更换头像
const confirmAvatarChange = async () => {
  try {
    userInfo.value.avatar = selectedAvatar.value
    sessionManager.updateUserInfo({ avatar: selectedAvatar.value })
    ElMessage.success('头像更换成功!')
    avatarDialogVisible.value = false
    selectedAvatar.value = ''
  } catch (error) {
    console.error('更换头像失败:', error)
    ElMessage.error('更换头像失败，请重试')
  }
}

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  currentSchool: [
    { required: true, messgae: '请输入当前学校', trigger: 'blur' },
  ],
  major: [
    { required: true, messgae: '请输入当前专业', trigger: 'blur' },
  ]
}

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const controller = new AbortController();
    loading.value = true;
    error.value = null;

    setupTimeout();
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
    if (!isLoggedIn) {
      throw new Error('用户未登录');
    }
    const response = await request.get('/user/current');

    if (response.code === 200 && response.data) {
      userInfo.value = response.data;
      sessionManager.updateUserInfo(response.data)
      if (userInfo.value.id) {
        await fetchStudentInfo(userInfo.value.id);
      }
    } else {
      throw new Error(response.message || '获取用户信息失败');
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
    if (error.message === '用户未登录') {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('userInfo');
      router.push('/login');
      return;
    }
    error.value = error.message || '获取用户信息失败';
    ElMessage.error(error.value);
  } finally {
    loading.value = false;
    clearPageTimeout();
  }
};

// 获取学生信息
const fetchStudentInfo = async (userId) => {
  try {
    if (!userId) {
      return;
    }

    const response = await request.get(`/student/info/${userId}`);

    if (response.code === 200 && response.data) {
      const studentData = response.data;
      if (studentData.birthDate) {
        if (typeof studentData.birthDate === 'number') {
          studentData.birthDate = new Date(studentData.birthDate);
        } else if (typeof studentData.birthDate === 'string') {
          studentData.birthDate = new Date(studentData.birthDate);
        }
      }

      studentInfo.value = {
        ...studentInfo.value,
        ...studentData,
        userId: userId
      };
    } else {
      studentInfo.value = {
        ...studentInfo.value,
        userId: userId
      };
    }
  } catch (error) {
    console.error('获取学生信息失败:', error);
    ElMessage.error('获取学生信息失败，请刷新页面重试');
  }
};

// 编辑/保存学生信息
const editStudentInfo = async () => {
  if (isEditing.value) {
    try {
      await studentForm.value.validate()
      const userInfo = JSON.parse(localStorage.getItem('userInfo'))

      const formatBirthDateForSubmit = (v) => {
        if (!v) return null
        const d = v instanceof Date ? v : new Date(v)
        if (isNaN(d.getTime())) return null
        const year = d.getFullYear()
        const month = String(d.getMonth() + 1).padStart(2, '0')
        const day = String(d.getDate()).padStart(2, '0')
        return `${year}-${month}-${day} 00:00:00`
      }

      const submitData = {
        ...studentInfo.value,
        userId: userInfo.id,
        birthDate: formatBirthDateForSubmit(studentInfo.value.birthDate)
      };

      let response;
      if (submitData.id) {
        response = await request.put('/student/update', submitData)
      } else {
        response = await request.post('/student/sync', submitData)
      }

      if (response.code === 200) {
        ElMessage.success('保存成功')
        isEditing.value = false
        await fetchStudentInfo(userInfo.id)
      } else {
        ElMessage.error(response.message || '保存失败')
      }
    } catch (error) {
      console.error('保存失败:', error)
      ElMessage.error('保存失败: ' + error.message)
    }
  } else {
    isEditing.value = true
  }
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  if (isNaN(d.getTime())) return ''
  return d.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 页面加载时的初始化
const initializePage = async () => {
  try {
    loading.value = true
    error.value = null
    setupTimeout()

    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
    if (!isLoggedIn) {
      router.push('/login')
      return
    }

    await fetchUserInfo()
  } catch (e) {
    console.error('Profile组件加载失败:', e)
    error.value = e.message || '页面加载失败'
  } finally {
    clearPageTimeout()
    loading.value = false
  }
}

// 组件挂载
onMounted(async () => {
  if (!localStorage.getItem('isLoggedIn')) {
    localStorage.setItem('isLoggedIn', 'true')
    localStorage.setItem('userId', '1')
  }

  await initializePage()
})

onUnmounted(() => {
  clearPageTimeout()
})
</script>

<style scoped>
.loading-container,
.error-container {
  padding: 2rem;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.profile-page {
  min-height: 100vh;
  background: #F9F8F4;
  padding-top: 0;
}

.profile-header-section {
  padding: 80px 0 40px;
  background: #F9F8F4;
  color: #292524;
}

.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.profile-header {
  display: flex;
  justify-content: center;
  align-items: center;
}

.user-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
  text-align: center;
}

.avatar-container {
  position: relative;
  cursor: pointer;
  border-radius: 50%;
  padding: 4px;
  background: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  display: flex;
  justify-content: center;
  align-items: center;
  width: fit-content;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s;
  color: white;
}

.avatar-container:hover .avatar-overlay {
  opacity: 1;
}

.user-details h2 {
  margin: 0 0 8px;
  font-family: "Times New Roman", serif;
  font-size: 32px;
  font-weight: 500;
  color: #1c1917;
}

.user-meta {
  font-size: 14px;
  color: #57534e;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.user-meta .divider {
  color: #d6d3d1;
}

.profile-content {
  margin-top: 0;
  padding-bottom: 60px;
}

.info-card {
  background: #fff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0, 0, 0, 0.02);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.section-header h3 {
  margin: 0;
  font-family: "Times New Roman", serif;
  font-size: 24px;
  color: #292524;
}

.edit-btn {
  background-color: #C5A059;
  border-color: #C5A059;
  color: white;
  padding: 10px 24px;
  border-radius: 30px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.edit-btn:hover {
  background-color: #b08d4b;
  border-color: #b08d4b;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(197, 160, 89, 0.2);
}

.student-form {
  padding: 0;
}

.form-grid {
  display: grid;
  gap: 24px;
}

:deep(.el-form-item) {
  display: flex;
  align-items: center;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #57534e;
  padding-right: 12px;
  line-height: normal;
  /* 重置行高，确保 flex 对齐生效 */
  display: flex;
  align-items: center;
  height: 100%;
  justify-content: flex-end;
  /* 保持标签右对齐 */
}

:deep(.el-form-item__content) {
  margin-left: 0 !important;
  /* flex 布局下不需要 margin-left */
}

:deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #e7e5e4 inset;
  background-color: #fafaf9;
  transition: all 0.3s ease;
  padding: 8px 12px;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #C5A059 inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #C5A059 inset !important;
}

:deep(.el-select .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #C5A059 inset !important;
}

/* 头像选择弹窗样式 */
.avatar-selection {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.avatar-options h4,
.upload-section h4 {
  margin: 0 0 16px;
  font-size: 16px;
  color: #1c1917;
  font-weight: 500;
}

.preset-avatars {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(70px, 1fr));
  gap: 16px;
}

.avatar-option {
  cursor: pointer;
  border-radius: 50%;
  padding: 2px;
  border: 2px solid transparent;
  transition: all 0.2s;
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar-option:hover {
  border-color: #e7e5e4;
  transform: scale(1.05);
}

.avatar-option.selected {
  border-color: #C5A059;
}

.upload-area {
  border: 2px dashed #e7e5e4;
  border-radius: 12px;
  padding: 32px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  background: #fafaf9;
}

.upload-area:hover {
  border-color: #C5A059;
  background: #fff;
}

.upload-icon {
  font-size: 32px;
  color: #a8a29e;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 14px;
  color: #57534e;
  margin-bottom: 4px;
}

.upload-hint {
  font-size: 12px;
  color: #a8a29e;
}
</style>
