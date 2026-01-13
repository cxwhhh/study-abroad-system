/**
 * 专业服务模块
 * 
 * 功能说明：
 * - 提供专业相关的API接口调用服务
 * - 包含专业的增删改查操作
 * - 支持按学校、学院筛选专业
 * - 统一处理API请求错误和异常情况
 * 
 * 作者：cxw
 * 创建时间：2025年
 */

import axios from 'axios';
import schoolService from './schoolService';

// 设置API基础URL - 专业相关接口的基础路径
const API_URL = '/api/majors';

let allMajorsCache = null;
let allMajorsPromise = null;
let cacheVersion = 0;
let hydrated = false;

const STORAGE_KEYS = {
  cacheVersion: 'majorService:cacheVersion',
  allMajors: 'majorService:allMajors',
};

const readStorageJSON = (key) => {
  try {
    const raw = localStorage.getItem(key);
    if (!raw) return null;
    return JSON.parse(raw);
  } catch {
    return null;
  }
};

const writeStorageJSON = (key, value) => {
  try {
    localStorage.setItem(key, JSON.stringify(value));
  } catch {
    // ignore
  }
};

const removeStorage = (key) => {
  try {
    localStorage.removeItem(key);
  } catch {
    // ignore
  }
};

const nowMs = () => Date.now();

const isFresh = (entry, maxAgeMs) => {
  if (!entry || typeof entry !== 'object') return false;
  if (typeof entry.updatedAt !== 'number') return false;
  if (!Number.isFinite(maxAgeMs)) return true;
  return nowMs() - entry.updatedAt <= maxAgeMs;
};

const hydrateCacheVersion = () => {
  const persisted = readStorageJSON(STORAGE_KEYS.cacheVersion);
  if (typeof persisted === 'number' && Number.isFinite(persisted)) {
    cacheVersion = persisted;
  }
};

const hydrateAllMajors = () => {
  const persisted = readStorageJSON(STORAGE_KEYS.allMajors);
  if (persisted && persisted.version === cacheVersion && Array.isArray(persisted.data)) {
    allMajorsCache = persisted.data;
  }
};

const ensureHydrated = () => {
  if (hydrated) return;
  hydrated = true;
  try {
    hydrateCacheVersion();
    hydrateAllMajors();
  } catch {
    // ignore
  }
};

const persistAllMajors = (data) => {
  writeStorageJSON(STORAGE_KEYS.allMajors, {
    version: cacheVersion,
    updatedAt: nowMs(),
    data: Array.isArray(data) ? data : [],
  });
};

const persistCacheVersion = () => {
  writeStorageJSON(STORAGE_KEYS.cacheVersion, cacheVersion);
};

const invalidateAllMajorsCache = () => {
  allMajorsCache = null;
  allMajorsPromise = null;
  cacheVersion += 1;
  persistCacheVersion();
  removeStorage(STORAGE_KEYS.allMajors);
};

/**
 * 创建安全请求函数
 * 统一处理HTTP请求，包含错误处理和异常捕获
 * @param {string} method - HTTP请求方法 (get, post, put, delete)
 * @param {string} url - 请求URL
 * @param {Object|null} data - 请求数据 (POST/PUT请求时使用)
 * @returns {Promise} 返回axios响应对象
 */
const safeRequest = async (method, url, data = null) => {
  try {
    let response;
    // 根据请求方法执行相应的HTTP请求
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
    // 处理404错误 - 资源不存在的情况
    if (error.response && error.response.status === 404) {
      console.warn(`资源不存在: ${url}`);
      return {
        data: method === 'get' ? [] : { success: false, message: '资源不存在' }
      };
    }

    // 处理其他HTTP错误 - 记录详细错误信息
    console.error('请求失败:', {
      url,
      method,
      status: error.response?.status,
      message: error.message
    });
    throw error;
  }
};

/**
 * 专业服务对象
 * 提供专业相关的所有API接口方法
 */
export default {
  /**
   * 获取所有专业列表
   * @returns {Promise} 返回包含所有专业信息的Promise对象
   */
  async getAllMajorsCached(options = {}) {
    ensureHydrated();
    const { force = false, maxAgeMs = 1000 * 60 * 60 * 12, revalidate = true } = options || {};

    if (!force && !Array.isArray(allMajorsCache)) {
      const persisted = readStorageJSON(STORAGE_KEYS.allMajors);
      if (persisted && persisted.version === cacheVersion && Array.isArray(persisted.data) && isFresh(persisted, maxAgeMs)) {
        allMajorsCache = persisted.data;
      }
    }

    if (!force && Array.isArray(allMajorsCache)) {
      const persisted = readStorageJSON(STORAGE_KEYS.allMajors);
      if (revalidate && persisted && persisted.version === cacheVersion && Array.isArray(persisted.data) && !isFresh(persisted, maxAgeMs)) {
        safeRequest('get', API_URL)
          .then((response) => {
            const data = Array.isArray(response?.data) ? response.data : [];
            allMajorsCache = data;
            persistAllMajors(data);
          })
          .catch(() => { })
          .finally(() => {
            allMajorsPromise = null;
          });
      }
      return { status: 200, data: allMajorsCache };
    }

    if (!force && allMajorsPromise) {
      return allMajorsPromise;
    }

    allMajorsPromise = safeRequest('get', API_URL)
      .then((response) => {
        const data = Array.isArray(response?.data) ? response.data : [];
        allMajorsCache = data;
        persistAllMajors(data);
        return { ...response, data };
      })
      .catch((err) => {
        if (!Array.isArray(allMajorsCache)) {
          allMajorsCache = null;
        }
        throw err;
      })
      .finally(() => {
        allMajorsPromise = null;
      });

    return allMajorsPromise;
  },

  getAllMajors(options = {}) {
    return this.getAllMajorsCached(options);
  },

  peekAllMajors() {
    ensureHydrated();
    return allMajorsCache;
  },

  invalidateCache() {
    ensureHydrated();
    invalidateAllMajorsCache();
  },

  getCacheVersion() {
    ensureHydrated();
    return cacheVersion;
  },

  /**
   * 根据专业ID获取专业详细信息
   * @param {number|string} id - 专业ID
   * @returns {Promise} 返回包含专业详细信息的Promise对象
   */
  getMajorById(id) {
    return safeRequest('get', `${API_URL}/${id}`);
  },

  /**
   * 根据学校ID获取该学校的所有专业
   * @param {number|string} schoolId - 学校ID
   * @returns {Promise} 返回包含学校专业列表的Promise对象
   */
  getMajorsBySchoolId(schoolId) {
    return safeRequest('get', `/api/schools/${schoolId}/majors`);
  },

  /**
   * 根据学院ID获取该学院的所有专业
   * @param {number|string} facultyId - 学院ID
   * @returns {Promise} 返回包含学院专业列表的Promise对象
   */
  getMajorsByFacultyId(facultyId) {
    return safeRequest('get', `/api/faculties/${facultyId}/majors`);
  },

  /**
   * 添加新专业
   * @param {Object} major - 专业信息对象
   * @param {string} major.name - 专业名称
   * @param {string} major.description - 专业描述
   * @param {number} major.schoolId - 所属学校ID
   * @param {number} major.facultyId - 所属学院ID
   * @returns {Promise} 返回添加结果的Promise对象
   */
  addMajor(major) {
    return safeRequest('post', API_URL, major).finally(() => {
      invalidateAllMajorsCache();
      schoolService.invalidateCache();
    });
  },

  /**
   * 更新专业信息
   * @param {number|string} id - 专业ID
   * @param {Object} major - 更新的专业信息对象
   * @returns {Promise} 返回更新结果的Promise对象
   */
  updateMajor(id, major) {
    return safeRequest('put', `${API_URL}/${id}`, major).finally(() => {
      invalidateAllMajorsCache();
      schoolService.invalidateCache();
    });
  },

  /**
   * 删除专业
   * @param {number|string} id - 要删除的专业ID
   * @returns {Promise} 返回删除结果的Promise对象
   */
  deleteMajor(id) {
    return safeRequest('delete', `${API_URL}/${id}`).finally(() => {
      invalidateAllMajorsCache();
      schoolService.invalidateCache();
    });
  },

  /**
   * 获取专业的核心课程列表
   * @param {number|string} majorId - 专业ID
   * @returns {Promise} 返回包含核心课程列表的Promise对象
   */
  getCoreCoursesByMajorId(majorId) {
    return safeRequest('get', `${API_URL}/${majorId}/core-courses`);
  },

  /**
   * 根据课程类型获取专业的核心课程
   * @param {number|string} majorId - 专业ID
   * @param {string} courseType - 课程类型（必修/选修）
   * @returns {Promise} 返回包含核心课程列表的Promise对象
   */
  getCoreCoursesByMajorIdAndType(majorId, courseType) {
    return safeRequest('get', `${API_URL}/${majorId}/core-courses/type/${courseType}`);
  },

  /**
   * 根据学期获取专业的核心课程
   * @param {number|string} majorId - 专业ID
   * @param {number} semester - 学期
   * @returns {Promise} 返回包含核心课程列表的Promise对象
   */
  getCoreCoursesByMajorIdAndSemester(majorId, semester) {
    return safeRequest('get', `${API_URL}/${majorId}/core-courses/semester/${semester}`);
  },

  /**
   * 统计专业的核心课程数量
   * @param {number|string} majorId - 专业ID
   * @returns {Promise} 返回包含课程数量的Promise对象
   */
  getCoreCourseCountByMajorId(majorId) {
    return safeRequest('get', `${API_URL}/${majorId}/core-courses/count`);
  }
};
