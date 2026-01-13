import axios from 'axios';
import schoolService from './schoolService';

// 设置API基础URL
const API_URL = '/api/faculties';

// 创建安全请求函数
const safeRequest = async (method, url, data = null) => {
  try {
    let response;
    if (method === 'get') {
      response = await axios.get(url);
    } else if (method === 'post') {
      response = await axios.post(url, data);
    } else if (method === 'put') {
      response = await axios.put(url, data);
    } else if (method === 'delete') {
      response = await axios.delete(url);
    }
    return response;
  } catch (error) {
    // 处理404错误
    if (error.response && error.response.status === 404) {
      if (url.includes('/majors')) {
        console.info(`${url} 返回空数据，这是正常的`);
        return { data: [] };
      } else {
        console.warn(`资源不存在: ${url}`);
        return {
          data: method === 'get' ? [] : { success: false, message: '资源不存在' }
        };
      }
    }

    // 处理其他错误
    console.error('请求失败:', {
      url,
      method,
      status: error.response?.status,
      message: error.message
    });
    throw error;
  }
};

// 学院服务
export default {
  // 获取所有学院
  getAllFaculties() {
    return safeRequest('get', API_URL);
  },

  // 根据ID获取学院
  getFacultyById(id) {
    return safeRequest('get', `${API_URL}/${id}`);
  },

  // 根据学校ID获取学院
  getFacultiesBySchoolId(schoolId) {
    return safeRequest('get', `/api/schools/${schoolId}/faculties`);
  },

  // 添加学院
  addFaculty(faculty) {
    return safeRequest('post', API_URL, faculty).finally(() => {
      schoolService.invalidateCache();
    });
  },

  // 更新学院信息
  updateFaculty(id, faculty) {
    return safeRequest('put', `${API_URL}/${id}`, faculty).finally(() => {
      schoolService.invalidateCache();
    });
  },

  // 删除学院
  deleteFaculty(id) {
    return safeRequest('delete', `${API_URL}/${id}`).finally(() => {
      schoolService.invalidateCache();
    });
  },

  // 获取学院专业列表
  getFacultyMajors(facultyId) {
    return safeRequest('get', `${API_URL}/${facultyId}/majors`).then(response => {
      console.log(`获取学院 ${facultyId} 的专业数据:`, response);
      if (response && response.data) {
        return Array.isArray(response.data) ? response.data : [];
      }
      return [];
    }).catch(error => {
      console.error(`获取学院 ${facultyId} 的专业数据失败:`, error);
      return [];
    });
  }
}; 
