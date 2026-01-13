/**
 * 学校服务模块
 * 
 * 功能说明：
 * - 提供学校相关的API接口调用服务
 * - 包含学校的增删改查操作
 * - 支持按国家、类型筛选学校
 * - 支持学校搜索功能
 * - 提供学校下属学院、专业、申请的查询
 * - 统一处理API请求错误和异常情况
 * - 包含前后端数据格式映射功能
 * 
 * 作者：cxw
 * 创建时间：2025年
 */

import axios from 'axios';

// 设置API基础URL - 学校相关接口的基础路径
const API_URL = '/api/schools';

// 用于避免重复记录同样的404错误的集合
const logged404Endpoints = new Set();

let allSchoolsCache = null;
let allSchoolsPromise = null;
let cacheVersion = 0;
let hydrated = false;

const schoolFacultiesCache = new Map();
const schoolFacultiesPromise = new Map();
const schoolMajorsCache = new Map();
const schoolMajorsPromise = new Map();
const schoolApplicationsCache = new Map();
const schoolApplicationsPromise = new Map();
const schoolCountsCache = new Map();

const STORAGE_KEYS = {
  cacheVersion: 'schoolService:cacheVersion',
  allSchools: 'schoolService:allSchools',
  schoolCountsPrefix: 'schoolService:schoolCounts:',
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
    // ignore quota / serialization errors
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

const hydrateAllSchools = () => {
  const persisted = readStorageJSON(STORAGE_KEYS.allSchools);
  if (persisted && persisted.version === cacheVersion && Array.isArray(persisted.data)) {
    allSchoolsCache = persisted.data;
  }
};

const ensureHydrated = () => {
  if (hydrated) return;
  hydrated = true;
  try {
    hydrateCacheVersion();
    hydrateAllSchools();
  } catch {
    // ignore
  }
};

const persistAllSchools = (data) => {
  writeStorageJSON(STORAGE_KEYS.allSchools, {
    version: cacheVersion,
    updatedAt: nowMs(),
    data: Array.isArray(data) ? data : [],
  });
};

const persistCacheVersion = () => {
  writeStorageJSON(STORAGE_KEYS.cacheVersion, cacheVersion);
};

const schoolCountsKey = (schoolId) => `${STORAGE_KEYS.schoolCountsPrefix}${String(schoolId)}`;

const hydrateSchoolCounts = (schoolId, maxAgeMs) => {
  const persisted = readStorageJSON(schoolCountsKey(schoolId));
  if (!persisted || persisted.version !== cacheVersion) return null;
  if (!isFresh(persisted, maxAgeMs)) return null;
  if (!persisted.data || typeof persisted.data !== 'object') return null;
  return persisted.data;
};

const persistSchoolCounts = (schoolId, counts) => {
  writeStorageJSON(schoolCountsKey(schoolId), {
    version: cacheVersion,
    updatedAt: nowMs(),
    data: counts,
  });
};

const clearPerSchoolCaches = () => {
  schoolFacultiesCache.clear();
  schoolFacultiesPromise.clear();
  schoolMajorsCache.clear();
  schoolMajorsPromise.clear();
  schoolApplicationsCache.clear();
  schoolApplicationsPromise.clear();
  schoolCountsCache.clear();
};

const invalidateAllSchoolsCache = () => {
  allSchoolsCache = null;
  allSchoolsPromise = null;
  clearPerSchoolCaches();
  cacheVersion += 1;
  persistCacheVersion();
  removeStorage(STORAGE_KEYS.allSchools);
};

/**
 * 日志404错误但只记录一次
 * 避免相同的404错误重复输出到控制台
 * @param {string} endpoint - API端点URL
 * @returns {Promise} 返回拒绝的Promise对象
 */
const logOnce404Error = (endpoint) => {
  if (!logged404Endpoints.has(endpoint)) {
    console.warn(`API端点未实现: ${endpoint}，这可能是正常的开发状态。`);
    logged404Endpoints.add(endpoint);
  }
  return Promise.reject({ response: { status: 404 } });
};

/**
 * 创建安全请求函数
 * 统一处理HTTP请求，包含完善的错误处理和异常捕获机制
 * @param {string} method - HTTP请求方法 (get, post, put, delete)
 * @param {string} url - 请求URL
 * @param {Object|null} data - 请求数据 (POST/PUT请求时使用)
 * @returns {Promise} 返回axios响应对象或处理后的错误响应
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
      // 对于特定的子资源请求，返回空数组而不是错误
      if (url.includes('/faculties') || url.includes('/majors') || url.includes('/applications')) {
        console.info(`${url} 返回空数据，这是正常的`);
        return { data: [] };
      } else {
        // 对于主资源请求，返回适当的错误信息
        console.warn(`资源不存在: ${url}`);
        return {
          data: method === 'get' ? [] : { success: false, message: '资源不存在' }
        };
      }
    }

    // 处理其他HTTP错误 - 记录详细错误信息并重新抛出
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
 * 前端字段转后端字段映射函数
 * 将前端使用的下划线命名转换为后端使用的驼峰命名
 * 确保前后端数据格式的兼容性
 * @param {Object} data - 需要映射的数据对象
 * @returns {Object} 映射后的数据对象
 */
const mapFrontendToBackend = (data) => {
  const result = { ...data };

  // 定义前端字段到后端字段的映射关系
  const fieldMappings = {
    'country_code': 'countryCode',
    'english_name': 'englishName',
    'world_ranking': 'worldRanking',
    'national_ranking': 'nationalRanking',
    'qs_ranking': 'qsRanking',
    'times_ranking': 'timesRanking',
    'shanghai_ranking': 'shanghaiRanking',
    'tuition_fee': 'tuitionFee',
    'rebate_percentage': 'rebatePercentage',
    'living_cost': 'livingCost',
    'fee_currency': 'feeCurrency',
    'international_student_percentage': 'internationalStudentPercentage',
    'chinese_student_count': 'chineseStudentCount',
    'total_student_count': 'totalStudentCount',
    'student_faculty_ratio': 'studentFacultyRatio',
    'acceptance_rate': 'acceptanceRate',
    'average_gpa': 'averageGpa',
    'toefl_requirement': 'toeflRequirement',
    'ielts_requirement': 'ieltsRequirement',
    'gre_requirement': 'greRequirement',
    'gmat_requirement': 'gmatRequirement',
    'sat_requirement': 'satRequirement',
    'application_deadline': 'applicationDeadline',
    'has_undergraduate': 'hasUndergraduate',
    'has_graduate': 'hasGraduate',
    'has_phd': 'hasPhd',
    'logo_url': 'logoUrl',
    'image_url': 'imageUrl',
    'banner_url': 'bannerUrl',
    'contact_email': 'contactEmail',
    'contact_phone': 'contactPhone',
    'student_services': 'studentServices',
    'housing_info': 'housingInfo',
    'famous_alumni': 'famousAlumni',
    'entry_requirement': 'entryRequirement',
    'scholarship_info': 'scholarshipInfo',
    'visa_info': 'visaInfo',
    'is_deleted': 'isDeleted',
    'featured_programs': 'featuredPrograms',
    'notable_programs': 'notablePrograms',
    'created_time': 'createdTime',
    'updated_time': 'updatedTime'
  };

  // 遍历映射关系，应用字段转换
  Object.entries(fieldMappings).forEach(([frontendField, backendField]) => {
    if (result[frontendField] !== undefined) {
      // 将前端字段的值赋给对应的后端字段
      result[backendField] = result[frontendField];

      // 对特殊字段进行类型转换处理
      if (frontendField === 'times_ranking' && result[frontendField] !== null) {
        // 确保泰晤士排名字段是数值类型
        result[backendField] = Number(result[frontendField]);
        console.log(`将 ${frontendField} 设置为数值类型:`, result[backendField]);
      }

      // 保留原字段以维持向后兼容性
      // delete result[frontendField];
    }
  });

  // 特殊处理英文名称字段的双向兼容
  if (result.englishName !== undefined && result.english_name === undefined) {
    result.english_name = result.englishName;
  } else if (result.english_name !== undefined && result.englishName === undefined) {
    result.englishName = result.english_name;
  }

  // 特殊处理timesRanking和times_ranking字段 - 确保双向同步
  if (result.timesRanking !== undefined && result.timesRanking !== null) {
    // 确保是数字类型
    result.timesRanking = Number(result.timesRanking);
    result.times_ranking = result.timesRanking;
    console.log('从timesRanking同步到times_ranking字段:', result.timesRanking);
  } else if (result.times_ranking !== undefined && result.times_ranking !== null) {
    // 确保是数字类型
    result.times_ranking = Number(result.times_ranking);
    result.timesRanking = result.times_ranking;
    console.log('从times_ranking同步到timesRanking字段:', result.times_ranking);
  }

  // 最后再检查一次，确保timesRanking和times_ranking字段都有值（如果有一个有值）
  if ((result.timesRanking !== undefined && result.timesRanking !== null) ||
    (result.times_ranking !== undefined && result.times_ranking !== null)) {
    const value = result.timesRanking !== undefined && result.timesRanking !== null ?
      result.timesRanking : result.times_ranking;
    result.timesRanking = Number(value);
    result.times_ranking = Number(value);
    console.log('最终确认排名字段同步:', {
      timesRanking: result.timesRanking,
      times_ranking: result.times_ranking
    });
  }

  // 记录字段映射情况
  console.log('字段映射结果:', {
    englishName: result.englishName,
    english_name: result.english_name,
    timesRanking: result.timesRanking,
    times_ranking: result.times_ranking
  });

  return result;
};

/**
 * 院校服务对象
 * 提供院校相关的所有API接口方法
 */
export default {
  /**
   * 获取所有院校列表
   * @returns {Promise} 返回包含所有院校信息的Promise对象
   */
  async getAllSchoolsCached(options = {}) {
    ensureHydrated();
    const { force = false, maxAgeMs = 1000 * 60 * 60 * 12, revalidate = true } = options || {};

    if (!force && !Array.isArray(allSchoolsCache)) {
      const persisted = readStorageJSON(STORAGE_KEYS.allSchools);
      if (persisted && persisted.version === cacheVersion && Array.isArray(persisted.data) && isFresh(persisted, maxAgeMs)) {
        allSchoolsCache = persisted.data;
      }
    }

    if (!force && Array.isArray(allSchoolsCache)) {
      const persisted = readStorageJSON(STORAGE_KEYS.allSchools);
      if (revalidate && persisted && persisted.version === cacheVersion && Array.isArray(persisted.data) && !isFresh(persisted, maxAgeMs)) {
        safeRequest('get', API_URL)
          .then((response) => {
            const data = Array.isArray(response?.data) ? response.data : [];
            allSchoolsCache = data;
            persistAllSchools(data);
          })
          .catch(() => { })
          .finally(() => {
            allSchoolsPromise = null;
          });
      }
      return { status: 200, data: allSchoolsCache };
    }

    if (!force && allSchoolsPromise) {
      return allSchoolsPromise;
    }

    allSchoolsPromise = safeRequest('get', API_URL)
      .then((response) => {
        const data = Array.isArray(response?.data) ? response.data : [];
        allSchoolsCache = data;
        persistAllSchools(data);
        return { ...response, data };
      })
      .catch((err) => {
        if (!Array.isArray(allSchoolsCache)) {
          allSchoolsCache = null;
        }
        throw err;
      })
      .finally(() => {
        allSchoolsPromise = null;
      });

    return allSchoolsPromise;
  },

  getAllSchools(options = {}) {
    return this.getAllSchoolsCached(options);
  },

  peekAllSchools() {
    ensureHydrated();
    return allSchoolsCache;
  },

  /**
   * 根据院校ID获取院校详细信息
   * @param {number|string} id - 院校ID
   * @returns {Promise} 返回包含院校详细信息的Promise对象
   */
  getSchoolById(id) {
    return safeRequest('get', `${API_URL}/${id}`);
  },

  /**
   * 根据国家筛选院校
   * @param {string} country - 国家名称或代码
   * @returns {Promise} 返回包含指定国家院校列表的Promise对象
   */
  getSchoolsByCountry(country) {
    return safeRequest('get', `${API_URL}/filter/country/${country}`);
  },

  /**
   * 根据院校类型筛选院校
   * @param {string} type - 院校类型 (如：公立、私立等)
   * @returns {Promise} 返回包含指定类型院校列表的Promise对象
   */
  getSchoolsByType(type) {
    return safeRequest('get', `${API_URL}/filter/type/${type}`);
  },

  /**
   * 搜索院校
   * @param {string} keyword - 搜索关键词
   * @returns {Promise} 返回包含搜索结果的Promise对象
   */
  searchSchools(keyword) {
    return safeRequest('get', `${API_URL}/search?keyword=${encodeURIComponent(keyword)}`);
  },

  /**
   * 添加新院校
   * 包含数据验证和格式化处理
   * @param {Object} school - 院校信息对象
   * @returns {Promise} 返回添加结果的Promise对象
   */
  addSchool(school) {
    // 创建院校数据副本，避免修改原始对象
    const schoolData = { ...school };

    // 验证并设置国家代码字段的默认值
    if (!schoolData.country_code || schoolData.country_code.trim() === '') {
      console.warn('添加院校时发现country_code为空，设置默认值');
      schoolData.country_code = schoolData.country ?
        (schoolData.country.toLowerCase().substring(0, 2) || 'us') : 'us';
    }

    // 验证并设置国家字段的默认值
    if (!schoolData.country || schoolData.country.trim() === '') {
      console.warn('添加院校时发现country为空，设置默认值');
      schoolData.country = 'US';
    }

    // 确保英文名称字段的双向兼容性（下划线和驼峰命名）
    if (schoolData.englishName && !schoolData.english_name) {
      schoolData.english_name = schoolData.englishName;
      console.log('添加english_name字段:', schoolData.english_name);
    } else if (schoolData.english_name && !schoolData.englishName) {
      schoolData.englishName = schoolData.english_name;
      console.log('添加englishName字段:', schoolData.englishName);
    }

    // 定义需要转换为数字类型的字段列表
    const numericFields = [
      'worldRanking', 'world_ranking',
      'nationalRanking', 'national_ranking',
      'qsRanking', 'qs_ranking',
      'timesRanking', 'times_ranking',
      'shanghaiRanking', 'shanghai_ranking',
      'tuitionFee', 'tuition_fee',
      'rebatePercentage', 'rebate_percentage',
      'internationalStudentPercentage', 'international_student_percentage',
      'chineseStudentCount', 'chinese_student_count',
      'totalStudentCount', 'total_student_count',
      'acceptanceRate', 'acceptance_rate',
      'averageGpa', 'average_gpa'
    ];

    // 遍历数字字段列表，将字符串转换为数字类型
    numericFields.forEach(field => {
      if (schoolData[field] !== undefined && schoolData[field] !== null && schoolData[field] !== '') {
        schoolData[field] = Number(schoolData[field]);
        console.log(`字段 ${field} 转换为数字类型:`, schoolData[field]);
      }
    });

    // 特别处理泰晤士排名字段的双向同步
    if ((schoolData.timesRanking !== undefined && schoolData.timesRanking !== null) ||
      (schoolData.times_ranking !== undefined && schoolData.times_ranking !== null)) {

      // 获取有效的排名值（优先使用驼峰命名，其次使用下划线命名）
      const validValue = schoolData.timesRanking !== undefined && schoolData.timesRanking !== null
        ? schoolData.timesRanking
        : schoolData.times_ranking;

      // 确保数值类型并同步两个字段，保持数据一致性
      schoolData.timesRanking = Number(validValue);
      schoolData.times_ranking = Number(validValue);

      console.log('泰晤士排名字段同步完成:', {
        timesRanking: schoolData.timesRanking,
        times_ranking: schoolData.times_ranking
      });
    }

    // 应用前端到后端的字段映射转换
    const mappedData = mapFrontendToBackend(schoolData);

    // 详细日志，便于调试
    console.log('===============================================');
    console.log('添加院校 - 提交数据详情:');
    console.log('原始数据:', {
      name: schoolData.name,
      country: schoolData.country,
      country_code: schoolData.country_code,
      englishName: schoolData.englishName,
      english_name: schoolData.english_name,
      timesRanking: schoolData.timesRanking,
      times_ranking: schoolData.times_ranking
    });
    console.log('映射后数据:', {
      name: mappedData.name,
      country: mappedData.country,
      countryCode: mappedData.countryCode,
      englishName: mappedData.englishName,
      english_name: mappedData.english_name,
      timesRanking: mappedData.timesRanking,
      times_ranking: mappedData.times_ranking
    });
    console.log('完整映射后数据:', mappedData);
    console.log('===============================================');

    return safeRequest('post', API_URL, mappedData).finally(() => {
      invalidateAllSchoolsCache();
    });
  },

  // 更新院校信息
  /**
   * 更新院校信息
   * @description 根据院校ID更新院校的详细信息，包括数据验证、字段映射和类型转换
   * @param {string|number} id - 院校ID
   * @param {Object} school - 院校信息对象
   * @param {string} school.name - 院校名称
   * @param {string} school.country - 国家名称
   * @param {string} [school.country_code] - 国家代码
   * @param {string} [school.englishName] - 英文名称（驼峰命名）
   * @param {string} [school.english_name] - 英文名称（下划线命名）
   * @param {number} [school.worldRanking] - 世界排名
   * @param {number} [school.timesRanking] - 泰晤士排名
   * @param {number} [school.tuitionFee] - 学费
   * @param {number} [school.acceptanceRate] - 录取率
   * @returns {Promise} 返回更新结果的Promise对象
   */
  updateSchool(id, school) {
    // 创建院校数据副本，避免修改原始对象
    const schoolData = { ...school };
    if (!schoolData.country_code || schoolData.country_code.trim() === '') {
      console.warn('更新院校时发现country_code为空，设置默认值');
      schoolData.country_code = schoolData.country ?
        (schoolData.country.toLowerCase().substring(0, 2) || 'us') : 'us';
    }

    // 确保英文名称字段同时有下划线和驼峰形式
    if (schoolData.englishName && !schoolData.english_name) {
      schoolData.english_name = schoolData.englishName;
      console.log('添加english_name字段:', schoolData.english_name);
    } else if (schoolData.english_name && !schoolData.englishName) {
      schoolData.englishName = schoolData.english_name;
      console.log('添加englishName字段:', schoolData.englishName);
    }

    // 确保数字类型字段正确转换
    const numericFields = [
      'worldRanking', 'world_ranking',
      'nationalRanking', 'national_ranking',
      'qsRanking', 'qs_ranking',
      'timesRanking', 'times_ranking',
      'shanghaiRanking', 'shanghai_ranking',
      'tuitionFee', 'tuition_fee',
      'rebatePercentage', 'rebate_percentage',
      'internationalStudentPercentage', 'international_student_percentage',
      'chineseStudentCount', 'chinese_student_count',
      'totalStudentCount', 'total_student_count',
      'acceptanceRate', 'acceptance_rate',
      'averageGpa', 'average_gpa'
    ];

    numericFields.forEach(field => {
      if (schoolData[field] !== undefined && schoolData[field] !== null && schoolData[field] !== '') {
        schoolData[field] = Number(schoolData[field]);
        console.log(`字段 ${field} 转换为数字类型:`, schoolData[field]);
      }
    });

    // 特别处理 times_ranking 字段
    if ((schoolData.timesRanking !== undefined && schoolData.timesRanking !== null) ||
      (schoolData.times_ranking !== undefined && schoolData.times_ranking !== null)) {

      // 获取有效的值（优先使用timesRanking，其次使用times_ranking）
      const validValue = schoolData.timesRanking !== undefined && schoolData.timesRanking !== null
        ? schoolData.timesRanking
        : schoolData.times_ranking;

      // 确保数值类型并同步两个字段
      schoolData.timesRanking = Number(validValue);
      schoolData.times_ranking = Number(validValue);

      console.log('泰晤士排名字段同步完成:', {
        timesRanking: schoolData.timesRanking,
        times_ranking: schoolData.times_ranking
      });
    }

    // 前端到后端的字段映射
    const mappedData = mapFrontendToBackend(schoolData);

    // 详细日志，便于调试
    console.log('===============================================');
    console.log('更新院校 - 提交数据详情:');
    console.log('院校ID:', id);
    console.log('原始数据:', {
      name: schoolData.name,
      country: schoolData.country,
      country_code: schoolData.country_code,
      englishName: schoolData.englishName,
      english_name: schoolData.english_name,
      timesRanking: schoolData.timesRanking,
      times_ranking: schoolData.times_ranking
    });
    console.log('映射后数据:', {
      name: mappedData.name,
      country: mappedData.country,
      countryCode: mappedData.countryCode,
      englishName: mappedData.englishName,
      english_name: mappedData.english_name,
      timesRanking: mappedData.timesRanking,
      times_ranking: mappedData.times_ranking
    });
    console.log('===============================================');

    return safeRequest('put', `${API_URL}/${id}`, mappedData).finally(() => {
      invalidateAllSchoolsCache();
    });
  },

  // 删除院校
  deleteSchool(id) {
    return safeRequest('delete', `${API_URL}/${id}`).finally(() => {
      invalidateAllSchoolsCache();
    });
  },

  // 获取院校学院列表
  async getSchoolFaculties(schoolId, options = {}) {
    const { force = false } = options || {};
    const key = String(schoolId);

    if (!force && schoolFacultiesCache.has(key)) {
      return schoolFacultiesCache.get(key);
    }

    if (!force && schoolFacultiesPromise.has(key)) {
      return schoolFacultiesPromise.get(key);
    }

    const promise = safeRequest('get', `${API_URL}/${schoolId}/faculties`)
      .then((response) => {
        const data = Array.isArray(response?.data) ? response.data : [];
        schoolFacultiesCache.set(key, data);
        return data;
      })
      .catch(() => {
        schoolFacultiesCache.set(key, []);
        return [];
      })
      .finally(() => {
        schoolFacultiesPromise.delete(key);
      });

    schoolFacultiesPromise.set(key, promise);
    return promise;
  },

  // 获取学院专业列表
  getFacultyMajors(facultyId) {
    return safeRequest('get', `/api/faculties/${facultyId}/majors`).then(response => {
      console.log(`获取学院 ${facultyId} 的专业数据:`, response);
      if (response && response.data) {
        return Array.isArray(response.data) ? response.data : [];
      }
      return [];
    }).catch(error => {
      console.error(`获取学院 ${facultyId} 的专业数据失败:`, error);
      return [];
    });
  },

  // 获取院校专业列表
  async getSchoolMajors(schoolId, options = {}) {
    const { force = false } = options || {};
    const key = String(schoolId);

    if (!force && schoolMajorsCache.has(key)) {
      return schoolMajorsCache.get(key);
    }

    if (!force && schoolMajorsPromise.has(key)) {
      return schoolMajorsPromise.get(key);
    }

    const promise = safeRequest('get', `${API_URL}/${schoolId}/majors`)
      .then((response) => {
        const data = Array.isArray(response?.data) ? response.data : [];
        schoolMajorsCache.set(key, data);
        return data;
      })
      .catch(() => {
        schoolMajorsCache.set(key, []);
        return [];
      })
      .finally(() => {
        schoolMajorsPromise.delete(key);
      });

    schoolMajorsPromise.set(key, promise);
    return promise;
  },

  // 获取院校申请数据
  async getSchoolApplications(schoolId, options = {}) {
    const { force = false } = options || {};
    const key = String(schoolId);

    if (!force && schoolApplicationsCache.has(key)) {
      return schoolApplicationsCache.get(key);
    }

    if (!force && schoolApplicationsPromise.has(key)) {
      return schoolApplicationsPromise.get(key);
    }

    const promise = safeRequest('get', `${API_URL}/${schoolId}/applications`)
      .then((response) => {
        const data = Array.isArray(response?.data) ? response.data : [];
        schoolApplicationsCache.set(key, data);
        return data;
      })
      .catch(() => {
        schoolApplicationsCache.set(key, []);
        return [];
      })
      .finally(() => {
        schoolApplicationsPromise.delete(key);
      });

    schoolApplicationsPromise.set(key, promise);
    return promise;
  }
  ,
  invalidateCache() {
    ensureHydrated();
    invalidateAllSchoolsCache();
  },
  getCacheVersion() {
    ensureHydrated();
    return cacheVersion;
  },
  peekSchoolCounts(schoolId) {
    const key = String(schoolId);
    if (schoolCountsCache.has(key)) return schoolCountsCache.get(key);
    const persisted = hydrateSchoolCounts(key, 1000 * 60 * 60 * 24 * 30);
    if (persisted) {
      schoolCountsCache.set(key, persisted);
      return persisted;
    }
    return null;
  },
  async getSchoolCounts(schoolId, options = {}) {
    const { force = false, maxAgeMs = 1000 * 60 * 60 * 24 * 30 } = options || {};
    const key = String(schoolId);

    if (!force && schoolCountsCache.has(key)) {
      return schoolCountsCache.get(key);
    }

    if (!force) {
      const persisted = hydrateSchoolCounts(key, maxAgeMs);
      if (persisted) {
        schoolCountsCache.set(key, persisted);
        return persisted;
      }
    }

    const faculties = await this.getSchoolFaculties(schoolId, options);
    const facultyList = Array.isArray(faculties) ? faculties : [];

    let majorCount = 0;
    let majorsCountedFromFaculties = true;
    for (const faculty of facultyList) {
      if (!faculty || !Array.isArray(faculty.majors)) {
        majorsCountedFromFaculties = false;
        break;
      }
      majorCount += faculty.majors.length;
    }

    if (!majorsCountedFromFaculties) {
      const majors = await this.getSchoolMajors(schoolId, options);
      majorCount = Array.isArray(majors) ? majors.length : 0;
    }

    const counts = {
      facultyCount: facultyList.length,
      majorCount,
    };

    schoolCountsCache.set(key, counts);
    persistSchoolCounts(key, counts);
    return counts;
  }
};
