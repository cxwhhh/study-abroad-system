import request from '@/utils/request'

/**
 * 智能选校匹配服务 API
 */

// 1. 智能一键择校 (对应后端 /api/matching/match)
export function performMatching(data) {
  return request({
    url: '/matching/match', // 【关键修改】修正为新的后端接口地址
    method: 'post',
    data: data,
    timeout: 60000
  })
}

// 2. 单独检测某所学校 (对应后端 /api/matching/check)
// 如果您的前端有"测试该学校"的功能，请调用此方法
export function checkSpecificSchool(data, schoolName) {
  return request({
    url: '/matching/check',
    method: 'post',
    data: data,
    timeout: 60000,
    params: {
      schoolName: schoolName
    }
  })
}

// 3. 获取历史结果 (对应后端 /api/matching/results/{userId})
export function getMatchingResults(userId, page = 0, size = 20) {
  return request({
    url: `/matching/results/${userId}`,
    method: 'get',
    params: {
      page,
      size
    }
  })
}

// 4. 获取用户统计信息 (对应后端 /api/matching/statistics/{userId})
export function getMatchingStatistics(userId) {
  return request({
    url: `/matching/statistics/${userId}`,
    method: 'get'
  })
}

// 5. 获取支持的地区 (对应后端 /api/matching/regions)
export function getSupportedRegions() {
  return request({
    url: '/matching/regions',
    method: 'get'
  })
}

/**
 * 统一导出默认对象，兼容不同的导入方式
 */
export default {
  performMatching,
  checkSpecificSchool,
  getMatchingResults,
  getMatchingStatistics,
  getSupportedRegions
}
