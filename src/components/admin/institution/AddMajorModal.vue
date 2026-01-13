<template>
  <div class="modal-overlay">
    <div class="modal-container">
      <div class="modal-header">
        <div class="modal-title">
          <BookmarkPlus class="modal-icon" />
          <h3>添加专业</h3>
        </div>
        <button class="btn-icon-sm" @click="$emit('close')">
          <X />
        </button>
      </div>

      <div class="modal-body">
        <div class="form-grid">
          <!-- 基本信息 -->
          <div class="form-group">
            <label>专业名称 <span class="required">*</span></label>
            <input type="text" v-model="formData.name" placeholder="请输入专业名称" class="form-input" required />
          </div>
          
          <div class="form-group">
            <label>英文名称</label>
            <input type="text" v-model="formData.englishName" placeholder="请输入英文名称" class="form-input" />
          </div>
          
          <div class="form-group">
            <label>专业代码</label>
            <input type="text" v-model="formData.code" placeholder="请输入专业代码" class="form-input" />
          </div>
          
          <div class="form-group">
            <label>学位类型</label>
            <select v-model="formData.degreeType" class="form-input">
              <option value="本科">本科</option>
              <option value="硕士">硕士</option>
              <option value="博士">博士</option>
              <option value="其他">其他</option>
            </select>
          </div>
          
          <div class="form-group">
            <label>学习年限</label>
            <input type="number" v-model="formData.studyYears" placeholder="请输入学习年限" class="form-input" />
          </div>
          
          <div class="form-group">
            <label>专业主任</label>
            <input type="text" v-model="formData.director" placeholder="请输入专业主任姓名" class="form-input" />
          </div>
          
          <div class="form-group">
            <label>学生数量</label>
            <input type="number" v-model="formData.studentCount" placeholder="请输入学生数量" class="form-input" />
          </div>
          
          <div class="form-group">
            <label>开设年份</label>
            <input type="number" v-model="formData.establishedYear" placeholder="请输入开设年份" class="form-input" />
          </div>
          
          <div class="form-group">
            <label>图片URL</label>
            <input type="url" v-model="formData.imageUrl" placeholder="请输入专业图片URL" class="form-input" />
          </div>
        </div>
        
        <!-- 详细信息 -->
        <div class="form-section">
          <h4 class="form-section-title">详细信息</h4>
          
          <div class="form-group">
            <label>专业简介</label>
            <textarea v-model="formData.description" placeholder="请输入专业简介" class="form-input form-textarea"></textarea>
          </div>
          
          <div class="form-group">
            <label>培养目标</label>
            <textarea v-model="formData.objective" placeholder="请输入培养目标" class="form-input form-textarea"></textarea>
          </div>
          
          <div class="form-group">
            <label>核心课程</label>
            <textarea v-model="formData.coreCourses" placeholder="请输入核心课程，多个课程用英文逗号分隔" class="form-input form-textarea"></textarea>
          </div>
          
          <div class="form-group">
            <label>就业方向</label>
            <textarea v-model="formData.careerProspects" placeholder="请输入就业方向" class="form-input form-textarea"></textarea>
          </div>
          
          <div class="form-group">
            <label>入学要求</label>
            <textarea v-model="formData.admissionRequirements" placeholder="请输入入学要求" class="form-input form-textarea"></textarea>
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
import { BookmarkPlus, X } from 'lucide-vue-next'
import { ElMessage, ElLoading } from 'element-plus'
import majorService from '@/api/majorService'

// 定义属性
const props = defineProps({
  facultyId: {
    type: [Number, String],
    required: true
  },
  schoolId: {
    type: [Number, String],
    required: true
  }
})

// 表单数据
const formData = ref({
  name: '',
  englishName: '',
  facultyId: props.facultyId,
  schoolId: props.schoolId,
  code: '',
  degreeType: '本科',
  studyYears: 4,
  description: '',
  objective: '',
  director: '',
  studentCount: null,
  coreCourses: '',
  careerProspects: '',
  establishedYear: null,
  admissionRequirements: '',
  imageUrl: '',
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
    ElMessage.warning('请填写专业名称')
    return
  }

  try {
    submitting.value = true
    
    // 显示加载提示
    const loading = ElLoading.service({
      lock: true,
      text: '正在保存专业信息...',
      background: 'rgba(0, 0, 0, 0.7)'
    })
    
    // 将数据转换为适合后端API的格式
    const submitData = {...formData.value}
    
    // 将字符串数字转换为数字类型
    const numberFields = ['facultyId', 'schoolId', 'studyYears', 'studentCount', 'establishedYear', 'status', 'isDeleted']
    
    numberFields.forEach(field => {
      if (submitData[field] !== null && submitData[field] !== undefined && submitData[field] !== '') {
        submitData[field] = Number(submitData[field])
      }
    })
    
    console.log('准备提交数据:', submitData)
    
    // 调用API将数据保存到数据库
    const response = await majorService.addMajor(submitData)
    
    loading.close()
    submitting.value = false
    
    console.log('提交响应:', response)
    
    if (response && (response.data || response.success)) {
      ElMessage.success('专业添加成功')
      emit('refresh', response.data || {})
      emit('close')
    } else {
      ElMessage.error(response?.message || '专业添加失败')
    }
  } catch (error) {
    console.error('添加专业错误:', error)
    submitting.value = false
    ElMessage.error(`专业添加失败: ${error.message || '服务器错误'}`)
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