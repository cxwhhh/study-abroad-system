<template>
  <div class="modal-overlay">
    <div class="modal-container">
      <div class="modal-header">
        <div class="modal-title">
          <BookOpen class="modal-icon" />
          <h3>添加学院</h3>
        </div>
        <button class="btn-icon-sm" @click="$emit('close')">
          <X />
        </button>
      </div>

      <div class="modal-body">
        <div class="form-grid">
          <!-- 基本信息 -->
          <div class="form-group">
            <label>学院名称 <span class="required">*</span></label>
            <input type="text" v-model="formData.name" placeholder="请输入学院名称" class="form-input" required />
          </div>
          
          <div class="form-group">
            <label>英文名称</label>
            <input type="text" v-model="formData.englishName" placeholder="请输入英文名称" class="form-input" />
          </div>
          
          <div class="form-group">
            <label>学院代码</label>
            <input type="text" v-model="formData.code" placeholder="请输入学院代码" class="form-input" />
          </div>
          
          <div class="form-group">
            <label>学院排名</label>
            <input type="number" v-model="formData.ranking" placeholder="请输入学院排名" class="form-input" />
          </div>
          
          <div class="form-group">
            <label>院长姓名</label>
            <input type="text" v-model="formData.deanName" placeholder="请输入院长姓名" class="form-input" />
          </div>
          
          <div class="form-group">
            <label>教师数量</label>
            <input type="number" v-model="formData.facultyCount" placeholder="请输入教师数量" class="form-input" />
          </div>
          
          <div class="form-group">
            <label>学生数量</label>
            <input type="number" v-model="formData.studentCount" placeholder="请输入学生数量" class="form-input" />
          </div>
          
          <div class="form-group">
            <label>学院网站</label>
            <input type="url" v-model="formData.website" placeholder="请输入学院网站URL" class="form-input" />
          </div>
          
          <div class="form-group">
            <label>图片URL</label>
            <input type="url" v-model="formData.imageUrl" placeholder="请输入学院图片URL" class="form-input" />
          </div>
          
          <div class="form-group">
            <label>联系邮箱</label>
            <input type="email" v-model="formData.contactEmail" placeholder="请输入联系邮箱" class="form-input" />
          </div>
          
          <div class="form-group">
            <label>联系电话</label>
            <input type="tel" v-model="formData.contactPhone" placeholder="请输入联系电话" class="form-input" />
          </div>
        </div>
        
        <!-- 详细信息 -->
        <div class="form-section">
          <h4 class="form-section-title">详细信息</h4>
          
          <div class="form-group">
            <label>学院简介</label>
            <textarea v-model="formData.description" placeholder="请输入学院简介" class="form-input form-textarea"></textarea>
          </div>
          
          <div class="form-group">
            <label>学院历史</label>
            <textarea v-model="formData.history" placeholder="请输入学院历史" class="form-input form-textarea"></textarea>
          </div>
          
          <div class="form-group">
            <label>特色专业</label>
            <textarea v-model="formData.featuredPrograms" placeholder="请输入特色专业，多个专业用英文逗号分隔" class="form-input form-textarea"></textarea>
          </div>
          
          <div class="form-group">
            <label>研究领域</label>
            <textarea v-model="formData.researchAreas" placeholder="请输入研究领域，多个领域用英文逗号分隔" class="form-input form-textarea"></textarea>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button class="btn btn-outline" @click="$emit('close')">取消</button>
        <button class="btn btn-primary" @click="handleSubmit" :disabled="submitting">保存</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { BookOpen, X } from 'lucide-vue-next'
import { ElMessage, ElLoading } from 'element-plus'
import facultyService from '@/api/facultyService'

// 定义属性
const props = defineProps({
  schoolId: {
    type: [Number, String],
    required: true
  }
})

// 表单数据
const formData = ref({
  name: '',
  englishName: '',
  schoolId: props.schoolId,
  code: '',
  description: '',
  history: '',
  ranking: null,
  deanName: '',
  facultyCount: null,
  studentCount: null,
  website: '',
  imageUrl: '',
  featuredPrograms: '',
  researchAreas: '',
  contactEmail: '',
  contactPhone: '',
  status: 1,
  isDeleted: 0
})

// 表单提交中状态
const submitting = ref(false)

// 处理表单提交
const handleSubmit = async () => {
  // 避免重复提交
  if (submitting.value) return
  
  // 验证必填字段
  if (!formData.value.name) {
    ElMessage.warning('请填写学院名称')
    return
  }

  try {
    submitting.value = true
    
    // 显示加载提示
    const loading = ElLoading.service({
      lock: true,
      text: '正在保存学院信息...',
      background: 'rgba(0, 0, 0, 0.7)'
    })
    
    // 将数据转换为适合后端API的格式
    const submitData = {...formData.value}
    
    // 将字符串数字转换为数字类型
    const numberFields = ['schoolId', 'ranking', 'facultyCount', 'studentCount', 'status', 'isDeleted']
    
    numberFields.forEach(field => {
      if (submitData[field] !== null && submitData[field] !== undefined && submitData[field] !== '') {
        submitData[field] = Number(submitData[field])
      }
    })
    
    console.log('准备提交数据:', submitData)
    
    // 调用API将数据保存到数据库
    const response = await facultyService.addFaculty(submitData)
    
    loading.close()
    submitting.value = false
    
    console.log('提交响应:', response)
    
    if (response && (response.data || response.success)) {
      ElMessage.success('学院添加成功')
      emit('refresh', response.data || {})
    } else {
      ElMessage.error(response?.message || '学院添加失败')
    }
  } catch (error) {
    console.error('添加学院错误:', error)
    submitting.value = false
    ElMessage.error(`学院添加失败: ${error.message || '服务器错误'}`)
  }
}

// 定义事件
const emit = defineEmits(['close', 'refresh'])
</script>

<style scoped>
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
  width: 680px;
  max-width: 90%;
  max-height: 90vh;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
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
  flex: 1;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #f0f0f0;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.form-section {
  margin-bottom: 24px;
}

.form-section-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  color: #666;
}

.required {
  color: #f5222d;
}

.form-input {
  height: 40px;
  padding: 0 12px;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  font-size: 14px;
  color: #333;
  transition: all 0.3s;
}

.form-input:focus {
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.15);
  outline: none;
}

.form-textarea {
  height: auto;
  min-height: 100px;
  padding: 12px;
  resize: vertical;
}

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

.btn-outline {
  background-color: white;
  border: 1px solid #d9d9d9;
  color: #666;
}

.btn-outline:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.btn-primary {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  color: white;
  border: none;
}

.btn-primary:hover {
  opacity: 0.9;
}

.btn-primary:disabled {
  background: #b7e3ff;
  cursor: not-allowed;
  opacity: 0.6;
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

@media (max-width: 767px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style> 