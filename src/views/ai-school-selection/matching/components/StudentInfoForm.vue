<template>
  <div class="input-card" :class="{ active: isMatching }">
    <el-form ref="studentFormRef" :model="studentInfo" :rules="formRules" label-width="80px" class="student-form">
      <div class="form-section">
        <h3 class="section-title">
          <el-icon class="section-icon">
            <User />
          </el-icon>
          基本信息
        </h3>
        <div class="form-grid basic-info-grid">
          <el-form-item label="学生姓名" prop="name">
            <el-input v-model="studentInfo.name" placeholder="选填" />
          </el-form-item>
          <el-form-item label="本科学校" prop="undergraduateSchool">
            <el-input v-model="studentInfo.undergraduateSchool" placeholder="请输入本科学校名称" />
          </el-form-item>
          <el-form-item label="本科专业" prop="undergraduateMajor">
            <el-input v-model="studentInfo.undergraduateMajor" placeholder="请输入本科专业" />
          </el-form-item>
          <el-form-item label="意向学位" prop="degreeType">
            <el-select v-model="studentInfo.degreeType" placeholder="选择学位">
              <el-option label="本科学士" value="bachelor" />
              <el-option label="硕士学位" value="master" />
              <el-option label="博士学位" value="phd" />
            </el-select>
          </el-form-item>
        </div>
      </div>
      <div class="form-section">
        <h3 class="section-title">
          <el-icon class="section-icon">
            <DataAnalysis />
          </el-icon>
          学术成绩
        </h3>
        <div class="form-grid gpa-grid">
          <el-form-item label="均分(%)" prop="gpa">
            <el-input-number v-model="studentInfo.gpa" :min="0" :max="100" :step="1" :precision="0"
              placeholder="请输入百分制均分" controls-position="right" class="gpa-input-number" @change="onAvgChange" />
          </el-form-item>
        </div>
      </div>
      <div class="form-section">
        <h3 class="section-title">
          <el-icon class="section-icon">
            <EditPen />
          </el-icon>
          语言成绩（选填）
        </h3>
        <div class="form-grid language-grid">
          <el-form-item label="雅思成绩" prop="ieltsScore">
            <el-input-number v-model="studentInfo.ieltsScore" :min="0" :max="9" :step="0.5" :precision="1"
              placeholder="0-9" controls-position="right" class="language-input-number" />
          </el-form-item>
          <el-form-item label="托福成绩" prop="toeflScore">
            <el-input-number v-model="studentInfo.toeflScore" :min="0" :max="120" :step="1" :precision="0"
              placeholder="0-120" controls-position="right" class="language-input-number" />
          </el-form-item>
          <el-form-item label="PTE成绩" prop="pteScore">
            <el-input-number v-model="studentInfo.pteScore" :min="0" :max="90" :step="1" :precision="0"
              placeholder="0-90" controls-position="right" class="language-input-number" />
          </el-form-item>
        </div>
      </div>
      <div class="form-section">
        <h3 class="section-title">
          <el-icon class="section-icon">
            <Collection />
          </el-icon>
          专业偏好
        </h3>
        <el-form-item label="意向专业" prop="major">
          <div class="major-picker">
            <el-input class="major-input" :model-value="majorInputText" placeholder="点击选择意向专业（最多 6 个）" readonly
              clearable @click="openMajorDialog" @clear="clearMajors" />
            <div class="selected-majors" :class="{ empty: !hasSelectedMajors }">
              <template v-if="hasSelectedMajors">
                <el-tag v-for="m in studentInfo.major" :key="m" closable type="info" class="selected-major-tag"
                  @close="removeMajor(m)">
                  {{ getMajorLabel(m) }}
                </el-tag>
              </template>
              <div v-else class="selected-majors-placeholder">已选：暂无</div>
            </div>
          </div>
        </el-form-item>
      </div>
      <div class="form-section">
        <h3 class="section-title">
          <el-icon class="section-icon">
            <Location />
          </el-icon>
          地区偏好
        </h3>
        <el-form-item label="意向国家" prop="preferredCountries">
          <el-select v-model="studentInfo.preferredCountries" placeholder="请选择意向国家（可多选）" style="width: 100%" multiple>
            <el-option label="美国" value="usa" />
            <el-option label="英国" value="uk" />
            <el-option label="加拿大" value="canada" />
            <el-option label="澳大利亚" value="australia" />
            <el-option label="新加坡" value="singapore" />
            <el-option label="香港" value="hongkong" />
            <el-option label="德国" value="germany" />
            <el-option label="法国" value="france" />
            <el-option label="日本" value="japan" />
            <el-option label="韩国" value="korea" />
          </el-select>
        </el-form-item>
        <el-form-item label="预算范围" prop="budget">
          <el-select v-model="studentInfo.budget" placeholder="请选择预算范围" style="width: 100%">
            <el-option label="20万以下/年" value="under-20w" />
            <el-option label="20-40万/年" value="20w-40w" />
            <el-option label="40-60万/年" value="40w-60w" />
            <el-option label="60万以上/年" value="over-60w" />
            <el-option label="预算充足" value="unlimited" />
          </el-select>
        </el-form-item>
      </div>
      <div class="form-actions">
        <el-button type="primary" size="large" @click="startMatching" :loading="isMatching" class="match-button">
          <el-icon>
            <Connection />
          </el-icon>
          {{ isMatching ? "正在匹配中..." : "开始AI智能匹配" }}
        </el-button>
        <el-button size="large" @click="resetForm">
          <el-icon>
            <Refresh />
          </el-icon>
          重置表单
        </el-button>
      </div>
    </el-form>
  </div>

  <el-dialog v-model="majorDialogVisible" title="选择意向专业" width="720px" class="major-dialog">
    <div class="major-dialog-header">
      <div class="major-dialog-hint">最多选择 {{ maxMajorSelection }} 个（已选 {{ draftMajors.length }} 个）</div>
      <div class="major-dialog-tags" :class="{ empty: !draftMajors.length }">
        <template v-if="draftMajors.length">
          <el-tag v-for="m in draftMajors" :key="m" closable type="info" class="selected-major-tag"
            @close="removeMajorDraft(m)">
            {{ getMajorLabel(m) }}
          </el-tag>
        </template>
        <div v-else class="major-dialog-tags-placeholder">已选：暂无</div>
      </div>
    </div>

    <div class="major-dialog-body">
      <div class="major-dialog-categories">
        <button v-for="(category, key) in majorCategories" :key="key" type="button" class="major-dialog-category"
          :class="{ active: String(key) === activeCategoryKey }" @click="activeCategoryKey = String(key)">
          <span class="major-dialog-category-title">{{ category.label }}</span>
          <el-icon class="major-dialog-category-arrow">
            <ArrowRight />
          </el-icon>
        </button>
      </div>

      <div class="major-dialog-majors">
        <div class="major-dialog-majors-title">
          {{ activeCategory?.label || "请选择分类" }}
        </div>
        <div class="major-dialog-majors-list">
          <el-checkbox-group :model-value="draftMajors" @change="onDraftMajorsChange">
            <el-checkbox v-for="m in (activeCategory?.majors || [])" :key="m.value" :label="m.value"
              :disabled="isMajorDisabledInDraft(m.value)" class="major-dialog-major">
              {{ m.label }}
            </el-checkbox>
          </el-checkbox-group>
        </div>
      </div>
    </div>

    <template #footer>
      <el-button @click="majorDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="confirmMajors">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed } from "vue";
import { ElMessage } from "element-plus";
import {
  Connection,
  Refresh,
  ArrowRight,
  User,
  DataAnalysis,
  Collection,
  Location,
  EditPen,
} from "@element-plus/icons-vue";

const props = defineProps({
  isMatching: Boolean,
  studentInfo: Object,
  majorCategories: Object,
  expandedCategories: Object,
});

const emit = defineEmits([
  "start-matching",
  "reset-form",
  "update:studentInfo",
  "toggle-category",
  "handle-major-change",
]);

const studentFormRef = ref();

const getMaxGpa = () => 100;

const startMatching = () => {
  studentFormRef.value.validate((valid) => {
    if (valid) {
      emit("start-matching");
    }
  });
};

const resetForm = () => {
  studentFormRef.value.resetFields();
  emit("reset-form");
};

const handleMajorChange = (value) => {
  emit("handle-major-change", value);
};

const toggleCategory = (category) => {
  emit("toggle-category", category);
};

const maxMajorSelection = 6;
const majorDialogVisible = ref(false);
const activeCategoryKey = ref("");
const draftMajors = ref([]);
const lastDraftMajors = ref([]);

const categoryKeys = computed(() => Object.keys(props.majorCategories || {}));

const activeCategory = computed(() => {
  const key = activeCategoryKey.value;
  return (props.majorCategories || {})[key];
});

const getMajorLabel = (value) => {
  for (const key in props.majorCategories || {}) {
    const category = props.majorCategories[key];
    const found = (category?.majors || []).find(m => m.value === value);
    if (found) return found.label;
  }
  return value;
};

const majorInputText = computed(() => {
  const majors = Array.isArray(props.studentInfo.major) ? props.studentInfo.major : [];
  if (!majors.length) return "";
  const labels = majors.map(getMajorLabel).filter(Boolean);
  if (labels.length <= 2) return labels.join("、");
  return `${labels.slice(0, 2).join("、")} 等${labels.length}个`;
});

const hasSelectedMajors = computed(() => {
  return Array.isArray(props.studentInfo.major) && props.studentInfo.major.length > 0;
});

const openMajorDialog = () => {
  const current = Array.isArray(props.studentInfo.major) ? props.studentInfo.major : [];
  draftMajors.value = [...current];
  lastDraftMajors.value = [...current];

  const keys = categoryKeys.value;
  const tryPickKey = (() => {
    const first = current[0];
    if (!first) return "";
    for (const key of keys) {
      const category = (props.majorCategories || {})[key];
      if ((category?.majors || []).some(m => m.value === first)) return key;
    }
    return "";
  })();

  activeCategoryKey.value = tryPickKey || keys[0] || "";
  majorDialogVisible.value = true;
};

const setMajors = (majors) => {
  const next = Array.isArray(majors) ? majors : [];
  emit("update:studentInfo", { ...props.studentInfo, major: next });
  emit("handle-major-change", next);
};

const clearMajors = () => {
  setMajors([]);
};

const removeMajor = (value) => {
  const current = Array.isArray(props.studentInfo.major) ? props.studentInfo.major : [];
  const next = current.filter(v => v !== value);
  setMajors(next);
};

const removeMajorDraft = (value) => {
  const next = (draftMajors.value || []).filter(v => v !== value);
  draftMajors.value = next;
  lastDraftMajors.value = [...next];
};

const onDraftMajorsChange = (value) => {
  const next = Array.isArray(value) ? value : [];
  if (next.length > maxMajorSelection) {
    draftMajors.value = [...lastDraftMajors.value];
    ElMessage.warning(`最多只能选择 ${maxMajorSelection} 个专业`);
    return;
  }
  draftMajors.value = next;
  lastDraftMajors.value = [...next];
};

const isMajorDisabledInDraft = (value) => {
  const current = Array.isArray(draftMajors.value) ? draftMajors.value : [];
  if (current.includes(value)) return false;
  return current.length >= maxMajorSelection;
};

const confirmMajors = () => {
  setMajors(draftMajors.value);
  majorDialogVisible.value = false;
};

// 绩点转换相关方法
const getPercentageScore = () => {
  if (!props.studentInfo.gpa || !props.studentInfo.gpaScale) {
    return "--";
  }

  const gpa = parseFloat(props.studentInfo.gpa);
  const scale = props.studentInfo.gpaScale;

  // 如果GPA为负数或无效值，返回0
  if (gpa < 0 || isNaN(gpa)) {
    return 0;
  }

  let percentage = 0;

  if (scale === "4") {
    // 4分制转百分制的换算公式
    // 常用换算：GPA 4.0 = 100分，GPA 3.0 = 90分，GPA 2.0 = 80分
    // 使用线性换算：percentage = (gpa / 4.0) * 40 + 60
    percentage = Math.round((gpa / 4.0) * 40 + 60);
  } else if (scale === "5") {
    // 5分制转百分制的换算公式
    // 常用换算：GPA 5.0 = 100分，GPA 4.0 = 92分，GPA 3.0 = 84分
    // 使用线性换算：percentage = (gpa / 5.0) * 40 + 60
    percentage = Math.round((gpa / 5.0) * 40 + 60);
  }

  // 确保百分制成绩在合理范围内（0-100分）
  percentage = Math.max(0, Math.min(100, percentage));

  return percentage;
};

const onAvgChange = () => {
  emit("update:studentInfo", { ...props.studentInfo, gpaScale: "100" });
};

const formRules = {
  name: [],
  undergraduateSchool: [
    { required: true, message: "请输入本科学校名称", trigger: "blur" },
  ],
  degreeType: [{ required: true, message: "请选择意向学位", trigger: "change" }],
  gpa: [
    { required: true, message: "请输入均分(%)", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error("请输入均分(%)"));
          return;
        }
        if (value > 100) {
          callback(new Error("均分不能超过100"));
          return;
        }
        if (value < 0) {
          callback(new Error("均分不能小于0"));
          return;
        }
        if (value < 60) {
          console.warn(`均分较低，可能影响申请成功率。当前均分: ${value}%`);
        }
        callback();
      },
      trigger: "blur",
    },
  ],
  undergraduateMajor: [
    { required: true, message: "请输入本科专业", trigger: "blur" },
  ],
  major: [
    {
      required: true,
      message: "请至少选择一个意向专业",
      trigger: "change",
    },
  ],
  preferredCountries: [
    {
      required: true,
      message: "请至少选择一个意向国家",
      trigger: "change",
    },
  ],
  budget: [{ required: true, message: "请选择预算范围", trigger: "change" }],
};
</script>

<style scoped>
.input-card {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid #e5e5e5;
  max-width: 300px;
  /* 250px (input) + 30px (padding) + buffer */
}

.major-picker {
  width: 100%;
}

.major-input {
  width: 100%;
  margin-bottom: 10px;
}

.selected-majors {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 6px;
  min-height: 34px;
  padding: 6px 8px;
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  background: #fff;
  box-sizing: border-box;
  align-items: center;
}

.selected-majors.empty {
  color: #909399;
}

.selected-majors-placeholder {
  font-size: 12px;
  color: #909399;
}

.selected-major-tag {
  max-width: 100%;
}

.input-card :deep(.el-input__wrapper),
.input-card :deep(.el-textarea__inner) {
  box-shadow: 0 0 0 1px #dcdfe6 inset !important;
  transition: box-shadow 0.15s ease, border-color 0.15s ease !important;
}

.input-card :deep(.el-input__inner) {
  padding-right: 30px !important;
}

.input-card :deep(.el-input__suffix) {
  min-width: 26px;
}

.student-form :deep(.el-form-item__content) {
  position: relative;
}

.student-form :deep(.el-form-item__error) {
  top: calc(100% - 6px);
  left: 0;
  padding: 3px 8px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.98);
  border: 1px solid rgba(245, 108, 108, 0.45);
  box-shadow: 0 6px 14px rgba(0, 0, 0, 0.08);
  z-index: 5;
  line-height: 1.4;
  font-size: 12px;
  max-width: 100%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  pointer-events: none;
}

.input-card :deep(.el-input__wrapper:hover),
.input-card :deep(.el-input__wrapper.is-focus),
.input-card :deep(.el-textarea__inner:hover),
.input-card :deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 1px #c5a059 inset !important;
}

.major-dialog-header {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 12px;
}

.major-dialog-hint {
  font-size: 13px;
  color: #606266;
}

.major-dialog-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  height: 72px;
  padding: 6px 8px;
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  background: #fff;
  box-sizing: border-box;
  overflow: auto;
  align-content: flex-start;
}

.major-dialog-tags.empty {
  color: #909399;
}

.major-dialog-tags-placeholder {
  font-size: 12px;
  color: #909399;
}

.major-dialog-body {
  display: grid;
  grid-template-columns: 200px 1fr;
  border: 1px solid #e5e5e5;
  border-radius: 10px;
  overflow: hidden;
  background: #fff;
  min-height: 360px;
}

.major-dialog-categories {
  border-right: 1px solid #eaeaea;
  background: #fafafa;
  overflow: auto;
}

.major-dialog-category {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 12px;
  border: none;
  background: transparent;
  cursor: pointer;
  text-align: left;
  color: #333;
}

.major-dialog-category.active {
  background: #ffffff;
  font-weight: 600;
}

.major-dialog-category-title {
  font-size: 13px;
}

.major-dialog-category-arrow {
  color: #909399;
}

.major-dialog-majors {
  padding: 12px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.major-dialog-majors-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.major-dialog-majors-list {
  overflow: auto;
  padding-right: 6px;
}

.major-dialog-major {
  display: block;
  margin: 10px 0;
}

.major-categories {
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
}

.major-category+.major-category {
  border-top: 1px solid #eaeaea;
}

.category-header {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  background: #fafafa;
  border: none;
  cursor: pointer;
  text-align: left;
}

.category-title {
  font-size: 13px;
  font-weight: 600;
  color: #333;
}

.category-arrow {
  color: #909399;
}

.category-body {
  padding: 10px 12px;
}

.major-checkbox {
  display: block;
  margin: 8px 0;
}

.language-grid {
  grid-template-columns: 1fr;
}

.language-input-number {
  width: 100%;
}

.student-form {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.form-section {
  border: 1px solid #f0f0f0;
  border-radius: 6px;
  padding: 10px;
  background-color: #fcfcfc;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.section-icon {
  font-size: 18px;
  color: #606266;
}

.form-grid {
  display: grid;
  gap: 5px 10px;
}

.basic-info-grid {
  grid-template-columns: 1fr;
}

.gpa-grid {
  grid-template-columns: 1fr;
  gap: 15px;
}

.gpa-input-number {
  width: 100%;
}

.form-actions {
  display: flex;
  justify-content: flex-start;
  gap: 10px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

:deep(.el-form-item) {
  margin-bottom: 5px;
}

:deep(.el-form-item__label) {
  font-size: 13px;
}

:deep(.el-input__inner) {
  font-size: 13px;
  height: 32px;
}

:deep(.el-select .el-input__inner) {
  height: 32px;
}

:deep(.el-input),
:deep(.el-select),
:deep(.el-input-number) {
  max-width: 250px;
}

:deep(.el-input-number) {
  width: 100%;
}
</style>
