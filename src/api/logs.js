import request from '@/utils/request'

/**
 * 获取日志列表
 * @param {Object} query 查询参数
 * @returns {Promise} 返回Promise对象
 */
export function getLogsList(query) {
  return request({
    url: '/log/list',
    method: 'get',
    params: query
  })
}

/**
 * 获取日志详情
 * @param {Number} id 日志ID
 * @returns {Promise} 返回Promise对象
 */
export function getLogDetail(id) {
  return request({
    url: `/log/detail/${id}`,
    method: 'get'
  })
}

/**
 * 获取学生信息日志
 * @param {Object} query 查询参数
 * @returns {Promise} 返回Promise对象
 */
export function getStudentLogs(query) {
  return request({
    url: '/log/student',
    method: 'get',
    params: query
  })
}

/**
 * 获取主页操作日志
 * @param {Object} query 查询参数
 * @returns {Promise} 返回Promise对象
 */
export function getHomepageLogs(query) {
  return request({
    url: '/log/homepage',
    method: 'get',
    params: query
  })
}

/**
 * 导出日志
 * @param {Object} query 查询参数
 * @returns {Promise} 返回Promise对象
 */
export function exportLogs(query) {
  return request({
    url: '/log/export',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}

/**
 * 获取日志类型选项
 * @returns {Promise} 返回Promise对象
 */
export function getLogTypeOptions() {
  return request({
    url: '/log/types',
    method: 'get'
  })
}

/**
 * 获取系统模块选项
 * @returns {Promise} 返回Promise对象
 */
export function getModuleOptions() {
  return request({
    url: '/log/modules',
    method: 'get'
  })
} 