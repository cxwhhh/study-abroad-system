<template>
  <div class="logs-container">
    <div class="page-header">
      <div>
        <h2 class="page-title">系统日志</h2>
        <p class="page-subtitle">查看系统操作日志、学生信息和主页操作记录</p>
      </div>
      <div class="page-actions">
        <el-button class="custom-button" type="default" @click="refreshLogs">
          <RefreshCw class="btn-icon" />
          <span>刷新</span>
        </el-button>
        <el-button class="custom-button" type="primary" @click="handleExport">
          <DownloadCloud class="btn-icon" />
          <span>导出日志</span>
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="filter-card" shadow="hover">
      <el-tabs v-model="activeTab" @tab-click="handleTabChange">
        <el-tab-pane label="全部日志" name="all"></el-tab-pane>
        <el-tab-pane label="学生信息日志" name="student"></el-tab-pane>
        <el-tab-pane label="主页操作日志" name="homepage"></el-tab-pane>
      </el-tabs>
      
      <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="80px">
        <el-form-item label="日志类型">
          <el-select v-model="queryParams.operType" placeholder="选择日志类型" clearable>
            <el-option 
              v-for="dict in logTypeOptions" 
              :key="dict.value" 
              :label="dict.label" 
              :value="dict.value" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="操作人员">
          <el-input v-model="queryParams.operName" placeholder="请输入操作人员" clearable />
        </el-form-item>
        
        <!-- 学生ID筛选，仅在学生信息日志选项卡显示 -->
        <el-form-item label="学生ID" v-if="activeTab === 'student'">
          <el-input v-model="queryParams.studentId" placeholder="请输入学生ID" clearable />
        </el-form-item>
        
        <el-form-item label="操作模块">
          <el-select v-model="queryParams.title" placeholder="选择操作模块" clearable>
            <el-option 
              v-for="dict in moduleOptions" 
              :key="dict.value" 
              :label="dict.label" 
              :value="dict.value" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="操作状态">
          <el-select v-model="queryParams.status" placeholder="选择状态" clearable>
            <el-option label="成功" value="0" />
            <el-option label="失败" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <Search class="btn-icon" />
            <span>搜索</span>
          </el-button>
          <el-button @click="resetQuery">
            <RefreshCw class="btn-icon" />
            <span>重置</span>
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 日志表格 -->
    <el-card class="table-card" shadow="hover">
      <el-table
        v-loading="loading"
        :data="logsList"
        border
        style="width: 100%"
        @row-click="handleRowClick"
      >
        <el-table-column type="index" width="50" label="#" />
        <el-table-column prop="operType" label="日志类型" width="100" show-overflow-tooltip>
          <template #default="scope">
            <el-tag :type="getLogTypeTag(scope.row.operType)">
              {{ scope.row.operTypeName || scope.row.operType }}
            </el-tag>
          </template>
        </el-table-column>
        
        <!-- 根据选项卡类型显示不同的列 -->
        <el-table-column prop="title" label="操作模块" width="120" show-overflow-tooltip />
        <el-table-column prop="operName" label="操作人员" width="120" show-overflow-tooltip />
        
        <!-- 学生信息选项卡特有列 -->
        <el-table-column prop="studentId" label="学生ID" width="120" v-if="activeTab === 'student'" show-overflow-tooltip />
        <el-table-column prop="studentName" label="学生姓名" width="120" v-if="activeTab === 'student'" show-overflow-tooltip />
        
        <!-- 主页操作日志特有列 -->
        <el-table-column prop="pageModule" label="页面模块" width="120" v-if="activeTab === 'homepage'" show-overflow-tooltip />
        
        <el-table-column prop="operIp" label="IP地址" width="120" show-overflow-tooltip />
        <el-table-column prop="operLocation" label="操作地点" width="150" show-overflow-tooltip />
        <el-table-column prop="method" label="请求方法" min-width="150" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
              {{ scope.row.status === '0' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operTime" label="操作时间" width="170" sortable />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button
              type="text"
              size="small"
              @click.stop="handleDetail(scope.row)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          background
          :current-page="queryParams.pageNum"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="queryParams.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 日志详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="日志详情"
      width="700px"
    >
      <template v-if="logDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="操作模块" :span="2">{{ logDetail.title }}</el-descriptions-item>
          <el-descriptions-item label="操作类型">
            <el-tag :type="getLogTypeTag(logDetail.operType)">
              {{ logDetail.operTypeName || logDetail.operType }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="操作状态">
            <el-tag :type="logDetail.status === '0' ? 'success' : 'danger'">
              {{ logDetail.status === '0' ? '成功' : '失败' }}
            </el-tag>
          </el-descriptions-item>
          
          <!-- 根据当前选项卡显示特定详情 -->
          <template v-if="activeTab === 'student'">
            <el-descriptions-item label="学生ID">{{ logDetail.studentId }}</el-descriptions-item>
            <el-descriptions-item label="学生姓名">{{ logDetail.studentName }}</el-descriptions-item>
          </template>
          
          <template v-if="activeTab === 'homepage'">
            <el-descriptions-item label="页面模块">{{ logDetail.pageModule }}</el-descriptions-item>
            <el-descriptions-item label="操作类型">{{ logDetail.actionType }}</el-descriptions-item>
          </template>
          
          <el-descriptions-item label="操作人员">{{ logDetail.operName }}</el-descriptions-item>
          <el-descriptions-item label="操作时间">{{ logDetail.operTime }}</el-descriptions-item>
          <el-descriptions-item label="IP地址">{{ logDetail.operIp }}</el-descriptions-item>
          <el-descriptions-item label="操作地点">{{ logDetail.operLocation }}</el-descriptions-item>
          <el-descriptions-item label="请求方法" :span="2">{{ logDetail.method }}</el-descriptions-item>
          <el-descriptions-item label="请求地址" :span="2">{{ logDetail.operUrl }}</el-descriptions-item>
          <el-descriptions-item label="请求参数" :span="2">
            <div class="param-content">
              <pre>{{ formatJson(logDetail.operParam) }}</pre>
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="返回结果" :span="2" v-if="logDetail.jsonResult">
            <div class="param-content">
              <pre>{{ formatJson(logDetail.jsonResult) }}</pre>
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="错误消息" :span="2" v-if="logDetail.errorMsg">
            <div class="error-msg">{{ logDetail.errorMsg }}</div>
          </el-descriptions-item>
        </el-descriptions>
      </template>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="exportSingleLog" v-if="logDetail">
            <DownloadCloud class="btn-icon" />
            导出此日志
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, RefreshCw, DownloadCloud } from 'lucide-vue-next';
import { 
  getLogsList, 
  getLogDetail, 
  exportLogs, 
  getLogTypeOptions, 
  getModuleOptions,
  getStudentLogs,
  getHomepageLogs
} from '@/api/logs';

// 状态及响应式变量
const loading = ref(false);
const dialogVisible = ref(false);
const logsList = ref([]);
const logDetail = ref(null);
const total = ref(0);
const dateRange = ref([]);
const logTypeOptions = ref([]);
const moduleOptions = ref([]);
const activeTab = ref('all'); // 默认显示全部日志选项卡

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  operType: undefined,
  operName: undefined,
  title: undefined,
  status: undefined,
  beginTime: undefined,
  endTime: undefined,
  studentId: undefined, // 学生ID，用于学生信息日志
  pageModule: undefined  // 页面模块，用于主页操作日志
});

// 监听选项卡变化，重置查询参数并重新加载数据
watch(activeTab, (newVal) => {
  // 重置分页
  queryParams.pageNum = 1;
  
  // 根据选项卡类型，清除不相关的参数
  if (newVal === 'all') {
    queryParams.studentId = undefined;
    queryParams.pageModule = undefined;
  } else if (newVal === 'student') {
    queryParams.pageModule = undefined;
  } else if (newVal === 'homepage') {
    queryParams.studentId = undefined;
  }
  
  // 重新加载数据
  getList();
});

// 获取日志类型选项
const fetchLogTypeOptions = async () => {
  try {
    // 尝试从后端获取日志类型选项
    const response = await getLogTypeOptions();
    if (response.code === 200) {
      logTypeOptions.value = response.data || [];
    } else {
      console.warn('获取日志类型选项返回非200状态:', response);
      // 设置默认选项
      setDefaultLogTypeOptions();
    }
  } catch (error) {
    console.error('获取日志类型选项失败:', error);
    // API不可用时设置默认选项
    setDefaultLogTypeOptions();
  }
};

// 设置默认的日志类型选项
const setDefaultLogTypeOptions = () => {
  logTypeOptions.value = [
    { value: '1', label: '登录日志' },
    { value: '2', label: '操作日志' },
    { value: '3', label: '系统日志' },
    { value: '4', label: '错误日志' },
    { value: '5', label: '其他日志' }
  ];
  ElMessage.warning('无法获取日志类型数据，已使用默认值');
};

// 获取模块选项
const fetchModuleOptions = async () => {
  try {
    // 尝试从后端获取模块选项
    const response = await getModuleOptions();
    if (response.code === 200) {
      moduleOptions.value = response.data || [];
    } else {
      console.warn('获取模块选项返回非200状态:', response);
      // 设置默认模块
      setDefaultModuleOptions();
    }
  } catch (error) {
    console.error('获取模块选项失败:', error);
    // API不可用时设置默认模块
    setDefaultModuleOptions();
  }
};

// 设置默认的模块选项
const setDefaultModuleOptions = () => {
  moduleOptions.value = [
    { value: 'user', label: '用户管理' },
    { value: 'student', label: '学生管理' },
    { value: 'application', label: '申请管理' },
    { value: 'institution', label: '院校管理' },
    { value: 'system', label: '系统设置' }
  ];
  ElMessage.warning('无法获取模块选项数据，已使用默认值');
};

// 处理选项卡切换
const handleTabChange = () => {
  // 切换选项卡时重置查询结果
  logsList.value = [];
  total.value = 0;
  // 重新加载数据（由watch监听器自动触发）
};

// 获取日志列表
const getList = async () => {
  loading.value = true;
  try {
    // 将日期范围转换为后端需要的格式
    if (dateRange.value && dateRange.value.length === 2) {
      queryParams.beginTime = dateRange.value[0];
      queryParams.endTime = dateRange.value[1];
    } else {
      queryParams.beginTime = undefined;
      queryParams.endTime = undefined;
    }

    let response;
    
    try {
      // 根据当前选项卡类型调用不同的API
      if (activeTab.value === 'student') {
        response = await getStudentLogs(queryParams);
      } else if (activeTab.value === 'homepage') {
        response = await getHomepageLogs(queryParams);
      } else {
        // 默认获取所有日志
        response = await getLogsList(queryParams);
      }
      
      if (response.code === 200) {
        // 兼容不同的后端返回格式
        logsList.value = response.rows || response.data || [];
        total.value = response.total || (response.data ? response.data.total : 0) || 0;
      } else {
        ElMessage.warning(response.msg || '获取日志列表失败，显示模拟数据');
        // API返回错误时使用模拟数据
        setMockLogData();
      }
    } catch (error) {
      console.error('API调用失败:', error);
      // API调用失败时使用模拟数据
      setMockLogData();
    }
  } catch (error) {
    console.error('获取日志列表出错:', error);
    // 发生其他错误时也使用模拟数据
    setMockLogData();
  } finally {
    loading.value = false;
  }
};

// 设置模拟日志数据
const setMockLogData = () => {
  // 生成当前时间
  const now = new Date();
  const formatTime = (date) => {
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`;
  };
  
  // 创建模拟数据
  const mockLogs = [];
  for (let i = 0; i < queryParams.pageSize; i++) {
    const timestamp = new Date(now.getTime() - i * 3600000);
    let mockLog = {
      operId: i + 1,
      title: ['用户管理', '学生管理', '申请管理', '院校管理', '系统设置'][i % 5],
      operType: String((i % 5) + 1),
      operTypeName: ['登录日志', '操作日志', '系统日志', '错误日志', '其他日志'][i % 5],
      operName: `管理员${i % 3 + 1}`,
      operIp: `192.168.1.${i + 1}`,
      operLocation: '本地网络',
      method: 'GET /api/data',
      status: i % 5 === 0 ? '1' : '0', // 每5条数据出现一次失败状态
      operTime: formatTime(timestamp)
    };
    
    // 根据当前选项卡添加特定字段
    if (activeTab.value === 'student') {
      mockLog.studentId = `STU${10000 + i}`;
      mockLog.studentName = `学生${i + 1}`;
    } else if (activeTab.value === 'homepage') {
      mockLog.pageModule = ['首页', '学生中心', '申请管理', '资源库', '设置'][i % 5];
      mockLog.actionType = ['查看', '编辑', '删除', '添加', '导出'][i % 5];
    }
    
    mockLogs.push(mockLog);
  }
  
  // 设置模拟数据和总数
  logsList.value = mockLogs;
  total.value = 100; // 假设总共有100条数据
  
  ElMessage.warning('后端API不可用，显示模拟数据');
};

// 根据日志类型返回标签类型
const getLogTypeTag = (type) => {
  const map = {
    '1': 'primary',   // 登录日志
    '2': 'success',   // 操作日志
    '3': 'warning',   // 系统日志
    '4': 'danger',    // 错误日志
    '5': 'info'       // 其他日志
  };
  return map[type] || 'info';
};

// 格式化JSON字符串
const formatJson = (jsonString) => {
  if (!jsonString) return '';
  try {
    const json = JSON.parse(jsonString);
    return JSON.stringify(json, null, 2);
  } catch (e) {
    return jsonString;
  }
};

// 处理查询
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};

// 重置查询
const resetQuery = () => {
  dateRange.value = [];
  // 保留当前选项卡相关的参数
  const tabType = activeTab.value;
  
  // 重置所有查询参数
  Object.keys(queryParams).forEach(key => {
    if (key !== 'pageNum' && key !== 'pageSize') {
      queryParams[key] = undefined;
    }
  });
  
  queryParams.pageNum = 1;
  getList();
};

// 刷新日志
const refreshLogs = () => {
  getList();
  ElMessage.success('刷新成功');
};

// 导出日志
const handleExport = () => {
  ElMessageBox.confirm(
    '是否确认导出所有日志数据?',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    try {
      // 处理日期范围
      const exportParams = { ...queryParams };
      if (dateRange.value && dateRange.value.length === 2) {
        exportParams.beginTime = dateRange.value[0];
        exportParams.endTime = dateRange.value[1];
      }
      
      // 添加当前选项卡类型
      exportParams.logType = activeTab.value;
      
      exportLogs(exportParams).then(response => {
        // 创建blob对象
        const blob = new Blob([response.data], { type: 'application/vnd.ms-excel' });
        const fileName = '系统日志_' + activeTab.value + '_' + new Date().getTime() + '.xlsx';
        
        // 创建下载链接
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = fileName;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        
        ElMessage.success('导出成功');
      }).catch((error) => {
        console.error('导出失败:', error);
        ElMessage.error('导出失败，API可能不可用');
      });
    } catch (error) {
      console.error('导出操作错误:', error);
      ElMessage.error('导出操作失败');
    }
  });
};

// 查看日志详情
const handleDetail = async (row) => {
  dialogVisible.value = true;
  
  try {
    // 获取日志ID，根据后端API调整字段名
    const logId = row.operId || row.id || row.logId;
    
    if (!logId) {
      ElMessage.warning('日志ID不存在');
      return;
    }
    
    try {
      const response = await getLogDetail(logId);
      if (response.code === 200) {
        logDetail.value = response.data || response;
      } else {
        // API返回错误时使用行数据作为详情
        setMockLogDetail(row);
      }
    } catch (error) {
      console.error('获取日志详情API调用失败:', error);
      // API调用失败时使用行数据作为详情
      setMockLogDetail(row);
    }
  } catch (error) {
    console.error('获取日志详情出错:', error);
    // 发生其他错误时也使用行数据作为详情
    setMockLogDetail(row);
  }
};

// 设置模拟日志详情
const setMockLogDetail = (row) => {
  // 使用行数据作为基础，添加额外详情信息
  logDetail.value = {
    ...row,
    operParam: JSON.stringify({ query: 'example', pageSize: 10, pageNum: 1 }),
    operUrl: `/api/${row.title.toLowerCase().replace(/\s/g, '')}`,
    jsonResult: row.status === '0' ? JSON.stringify({ code: 200, msg: '操作成功', data: [] }) : null,
    errorMsg: row.status === '1' ? '操作失败：资源不存在或权限不足' : null
  };
  
  ElMessage.warning('无法获取详细日志数据，显示简略信息');
};

// 导出单条日志
const exportSingleLog = () => {
  if (!logDetail.value) return;
  
  // 创建只包含当前日志ID的查询参数
  const exportParams = {
    operId: logDetail.value.operId || logDetail.value.id,
    logType: activeTab.value // 添加当前选项卡类型
  };
  
  ElMessage.info('正在导出单条日志...');
  
  try {
    exportLogs(exportParams).then(response => {
      // 创建blob对象
      const blob = new Blob([response.data], { type: 'application/vnd.ms-excel' });
      const fileName = '系统日志_' + (logDetail.value.operId || logDetail.value.id) + '_' + new Date().getTime() + '.xlsx';
      
      // 创建下载链接
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = fileName;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      
      ElMessage.success('导出成功');
    }).catch((error) => {
      console.error('导出单条日志失败:', error);
      ElMessage.error('导出失败，API可能不可用');
    });
  } catch (error) {
    console.error('导出单条日志操作错误:', error);
    ElMessage.error('导出操作失败');
  }
};

// 点击行展示详情
const handleRowClick = (row) => {
  handleDetail(row);
};

// 页面大小改变
const handleSizeChange = (val) => {
  queryParams.pageSize = val;
  getList();
};

// 页码改变
const handleCurrentChange = (val) => {
  queryParams.pageNum = val;
  getList();
};

// 组件挂载时执行
onMounted(async () => {
  try {
    loading.value = true;
    
    // 并行获取选项和日志列表
    await Promise.all([
      fetchLogTypeOptions(),
      fetchModuleOptions(),
      getList()
    ]);
    
  } catch (error) {
    console.error('初始化日志页面失败:', error);
    ElMessage.error('加载日志数据失败，请刷新页面重试');
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.logs-container {
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #333;
}

.page-subtitle {
  font-size: 14px;
  color: #666;
  margin: 0;
  margin-top: 4px;
}

.page-actions {
  display: flex;
  gap: 12px;
}

.btn-icon {
  width: 14px;
  height: 14px;
  margin-right: 4px;
}

.filter-card {
  margin-bottom: 24px;
  border-radius: 8px;
}

.table-card {
  border-radius: 8px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding: 0 20px;
}

.param-content {
  max-height: 200px;
  overflow-y: auto;
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  font-family: monospace;
}

.param-content pre {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
}

.error-msg {
  color: #f56c6c;
  background-color: #fef0f0;
  padding: 10px;
  border-radius: 4px;
  font-family: monospace;
  white-space: pre-wrap;
  word-break: break-all;
}

:deep(.el-descriptions__label) {
  width: 120px;
}
</style>

/**
 * 系统日志组件
 * 
 * 说明：
 * 1. 此组件通过API调用后端获取真实的系统日志数据
 * 2. 支持按不同条件筛选、分页显示、查看详情、导出日志等功能
 * 3. 字段名根据后端API进行了适配，如有不一致请调整
 * 4. 特别关注学生信息操作和主页操作的日志
 */