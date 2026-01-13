<template>
  <div class="filter-bar">
    <div class="filter-group">
      <label>申请状态</label>
      <div class="select-wrapper">
        <select v-model="filters.status">
          <option value="">全部</option>
          <option value="待提交">待提交</option>
          <option value="已提交">已提交</option>
          <option value="材料审核中">材料审核中</option>
          <option value="面试中">面试中</option>
          <option value="录取">录取</option>
          <option value="拒绝">拒绝</option>
          <option value="已完成">已完成</option>
        </select>
        <ChevronDown class="select-icon" />
      </div>
    </div>
    <div class="filter-group">
      <label>国家/地区</label>
      <div class="select-wrapper">
        <select v-model="filters.country">
          <option value="">全部</option>
          <option value="美国">美国</option>
          <option value="英国">英国</option>
          <option value="加拿大">加拿大</option>
          <option value="澳大利亚">澳大利亚</option>
          <option value="新西兰">新西兰</option>
          <option value="中国香港">中国香港</option>
          <option value="新加坡">新加坡</option>
        </select>
        <ChevronDown class="select-icon" />
      </div>
    </div>
    <div class="filter-group">
      <label>申请学位</label>
      <div class="select-wrapper">
        <select v-model="filters.degree">
          <option value="">全部</option>
          <option value="本科">本科</option>
          <option value="硕士">硕士</option>
          <option value="博士">博士</option>
        </select>
        <ChevronDown class="select-icon" />
      </div>
    </div>
    <div class="filter-group">
      <label>排序方式</label>
      <div class="select-wrapper">
        <select v-model="filters.sort">
          <option value="date">申请日期</option>
          <option value="deadline">截止日期</option>
          <option value="student">学生姓名</option>
          <option value="school">学校名称</option>
        </select>
        <ChevronDown class="select-icon" />
      </div>
    </div>
    <div class="filter-search">
      <label>搜索</label>
      <div class="search-input-wrapper">
        <Search class="search-input-icon" />
        <input type="text" v-model="searchQuery" placeholder="搜索申请、学生或学校..." class="filter-search-input"
          @input="handleSearch" />
      </div>
    </div>
    <div class="filter-actions">
      <el-button class="custom-button" type="default" icon="Filter" @click="applyFilters">
        筛选
      </el-button>
      <el-button class="custom-button" type="default" icon="RefreshRight" @click="resetFilters">
        重置
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, defineEmits, defineProps, onMounted } from 'vue';
import { ChevronDown, Search } from 'lucide-vue-next';

const props = defineProps({
  initialFilters: {
    type: Object,
    default: () => ({
      status: '',
      country: '',
      degree: '',
      sort: 'date'
    })
  },
  initialSearchQuery: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['update:filters', 'update:search', 'reset', 'search', 'apply-filters']);

const filters = ref({ ...props.initialFilters });
const searchQuery = ref(props.initialSearchQuery);

// 监听筛选条件变化
watch(filters, (newFilters) => {
  emit('update:filters', newFilters);

  // 本地保存筛选条件
  try {
    localStorage.setItem('applicationFilters', JSON.stringify(newFilters));
  } catch (e) {
    console.error('保存筛选条件失败:', e);
  }
}, { deep: true });

// 监听搜索词变化
watch(searchQuery, (newQuery) => {
  emit('update:search', newQuery);

  // 本地保存搜索词
  try {
    localStorage.setItem('applicationSearchQuery', newQuery);
  } catch (e) {
    console.error('保存搜索词失败:', e);
  }
});

// 重置筛选条件
const resetFilters = () => {
  filters.value = {
    status: '',
    country: '',
    degree: '',
    sort: 'date'
  };
  searchQuery.value = '';
  emit('reset');

  // 清除本地存储
  try {
    localStorage.removeItem('applicationFilters');
    localStorage.removeItem('applicationSearchQuery');
  } catch (e) {
    console.error('清除筛选条件失败:', e);
  }
};

// 处理搜索
const handleSearch = () => {
  emit('search', searchQuery.value);
};

// 应用筛选条件
const applyFilters = () => {
  // 触发筛选应用事件
  emit('apply-filters', {
    filters: filters.value,
    searchQuery: searchQuery.value
  });
};

// 组件挂载时尝试恢复之前保存的筛选条件
onMounted(() => {
  // 尝试从本地存储恢复筛选条件
  try {
    const savedFilters = localStorage.getItem('applicationFilters');
    const savedSearchQuery = localStorage.getItem('applicationSearchQuery');

    if (savedFilters) {
      const parsedFilters = JSON.parse(savedFilters);
      filters.value = { ...filters.value, ...parsedFilters };
    }

    if (savedSearchQuery) {
      searchQuery.value = savedSearchQuery;
    }
  } catch (e) {
    console.error('恢复筛选条件失败:', e);
  }
});
</script>

<style scoped>
.filter-bar {
  background-color: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-end;
  margin-bottom: 24px;
  margin-top: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.filter-group {
  display: flex;
  flex-direction: column;
  min-width: 180px;
}

.filter-search {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 250px;
}

.filter-group label,
.filter-search label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.select-wrapper {
  position: relative;
}

.select-wrapper select {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  font-size: 14px;
  color: #333;
  appearance: none;
  background-color: #fff;
  cursor: pointer;
}

.select-wrapper select:focus {
  outline: none;
  border-color: #40a9ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.select-icon {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  pointer-events: none;
  width: 16px;
  height: 16px;
}

.search-input-wrapper {
  position: relative;
}

.search-input-icon {
  position: absolute;
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  width: 16px;
  height: 16px;
}

.filter-search-input {
  width: 100%;
  padding: 10px 14px 10px 36px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  font-size: 14px;
  color: #333;
  background-color: #fff;
}

.filter-search-input:focus {
  outline: none;
  border-color: #40a9ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.filter-actions {
  display: flex;
  gap: 8px;
}

.custom-button {
  height: 38px !important;
}

@media (max-width: 768px) {
  .filter-bar {
    flex-direction: column;
  }

  .filter-group,
  .filter-search {
    width: 100%;
  }

  .filter-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
