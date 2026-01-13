package com.study.service.log;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;

/**
 * 日志服务实现类
 */
@Service
@Slf4j
public class LogServiceImpl implements LogService {

    @Autowired
    private LogRepository logRepository;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveLog(Log logEntry) {
        try {
            // 设置操作时间
            if (logEntry.getOperTime() == null) {
                logEntry.setOperTime(new Date());
            }

            logRepository.save(logEntry);
            return true;
        } catch (Exception e) {
            log.error("保存日志失败", e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getLogsList(LogQueryParams params) {
        try {
            // 创建分页对象
            Pageable pageable = createPageable(params);

            // 创建查询条件
            Specification<Log> spec = createSpecification(params);

            // 执行分页查询
            Page<Log> page = logRepository.findAll(spec, pageable);

            return createResultMap(page);
        } catch (Exception e) {
            log.error("查询日志列表失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("rows", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Log getLogDetail(Long id) {
        try {
            return logRepository.findById(id).orElse(null);
        } catch (Exception e) {
            log.error("查询日志详情失败", e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getStudentLogs(LogQueryParams params) {
        try {
            // 创建分页对象
            Pageable pageable = createPageable(params);

            // 解析日期
            Date beginTime = parseDate(params.getBeginTime());
            Date endTime = parseDate(params.getEndTime());
            if (endTime != null) {
                // 将结束时间设置为当天的23:59:59
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(endTime);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                endTime = calendar.getTime();
            }

            // 执行学生日志查询
            Page<Log> page = logRepository.findStudentLogs(
                    params.getOperType(),
                    params.getOperName(),
                    params.getStatus(),
                    params.getTitle(),
                    params.getStudentId(),
                    beginTime,
                    endTime,
                    pageable);

            return createResultMap(page);
        } catch (Exception e) {
            log.error("查询学生日志失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("rows", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getHomepageLogs(LogQueryParams params) {
        try {
            // 创建分页对象
            Pageable pageable = createPageable(params);

            // 解析日期
            Date beginTime = parseDate(params.getBeginTime());
            Date endTime = parseDate(params.getEndTime());
            if (endTime != null) {
                // 将结束时间设置为当天的23:59:59
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(endTime);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                endTime = calendar.getTime();
            }

            // 执行主页日志查询
            Page<Log> page = logRepository.findHomepageLogs(
                    params.getOperType(),
                    params.getOperName(),
                    params.getStatus(),
                    params.getPageModule(),
                    beginTime,
                    endTime,
                    pageable);

            return createResultMap(page);
        } catch (Exception e) {
            log.error("查询主页日志失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("rows", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cleanLogs(int days) {
        try {
            // 计算保留日期
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -days);
            Date retentionDate = calendar.getTime();

            // 创建查询条件：查找早于保留日期的日志
            Specification<Log> spec = (root, query, cb) -> cb.lessThan(root.get("operTime"), retentionDate);

            // 查询并删除符合条件的日志
            List<Log> logsToDelete = logRepository.findAll(spec);
            logRepository.deleteAll(logsToDelete);

            log.info("清理过期日志成功，共删除{}条日志", logsToDelete.size());
            return true;
        } catch (Exception e) {
            log.error("清理过期日志失败", e);
            return false;
        }
    }

    @Override
    public byte[] exportLogs(LogQueryParams params) {
        try {
            List<Log> logs;

            // 根据日志类型决定查询方式
            if ("student".equals(params.getLogType())) {
                logs = getLogsForExport(getStudentLogs(params));
            } else if ("homepage".equals(params.getLogType())) {
                logs = getLogsForExport(getHomepageLogs(params));
            } else {
                logs = getLogsForExport(getLogsList(params));
            }

            // 创建Excel工作簿
            try (Workbook workbook = new XSSFWorkbook()) {
                // 创建工作表
                Sheet sheet = workbook.createSheet("系统日志");

                // 创建标题样式
                CellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);

                // 创建表头
                Row headerRow = sheet.createRow(0);
                String[] headers = { "序号", "操作模块", "操作类型", "操作人员", "IP地址", "操作地点", "操作状态", "操作时间" };

                // 根据日志类型添加特定列
                if ("student".equals(params.getLogType())) {
                    headers = new String[] { "序号", "操作模块", "操作类型", "操作人员", "学生ID", "学生姓名", "IP地址", "操作状态", "操作时间" };
                } else if ("homepage".equals(params.getLogType())) {
                    headers = new String[] { "序号", "操作模块", "操作类型", "操作人员", "页面模块", "IP地址", "操作状态", "操作时间" };
                }

                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                    sheet.autoSizeColumn(i);
                }

                // 写入数据
                int rowNum = 1;
                for (Log log : logs) {
                    Row row = sheet.createRow(rowNum++);

                    int colNum = 0;
                    row.createCell(colNum++).setCellValue(rowNum - 1);
                    row.createCell(colNum++).setCellValue(log.getTitle());

                    // 翻译操作类型
                    String operTypeName = getOperTypeName(log.getOperType());
                    row.createCell(colNum++).setCellValue(operTypeName);

                    row.createCell(colNum++).setCellValue(log.getOperName());

                    // 根据日志类型添加特定数据
                    if ("student".equals(params.getLogType())) {
                        row.createCell(colNum++)
                                .setCellValue(log.getStudentId() != null ? log.getStudentId().toString() : "");
                        row.createCell(colNum++).setCellValue(log.getStudentName());
                    } else if ("homepage".equals(params.getLogType())) {
                        row.createCell(colNum++).setCellValue(log.getPageModule());
                    }

                    row.createCell(colNum++).setCellValue(log.getOperIp());

                    // 翻译状态
                    String status = "0".equals(log.getStatus()) ? "成功" : "失败";
                    row.createCell(colNum++).setCellValue(status);

                    // 格式化日期
                    String operTime = log.getOperTime() != null ? DATE_FORMAT.format(log.getOperTime()) : "";
                    row.createCell(colNum++).setCellValue(operTime);
                }

                // 自动调整列宽
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                // 写入输出流
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            log.error("导出日志失败", e);
            return new byte[0];
        }
    }

    @Override
    public List<Map<String, String>> getLogTypes() {
        List<Map<String, String>> types = new ArrayList<>();

        // 日志类型定义
        Map<String, String> type1 = new HashMap<>();
        type1.put("value", "1");
        type1.put("label", "登录日志");
        types.add(type1);

        Map<String, String> type2 = new HashMap<>();
        type2.put("value", "2");
        type2.put("label", "操作日志");
        types.add(type2);

        Map<String, String> type3 = new HashMap<>();
        type3.put("value", "3");
        type3.put("label", "系统日志");
        types.add(type3);

        Map<String, String> type4 = new HashMap<>();
        type4.put("value", "4");
        type4.put("label", "错误日志");
        types.add(type4);

        Map<String, String> type5 = new HashMap<>();
        type5.put("value", "5");
        type5.put("label", "其他日志");
        types.add(type5);

        return types;
    }

    @Override
    public List<Map<String, String>> getModules() {
        List<Map<String, String>> modules = new ArrayList<>();

        // 系统模块定义
        addModule(modules, "user", "用户管理");
        addModule(modules, "student", "学生管理");
        addModule(modules, "application", "申请管理");
        addModule(modules, "institution", "院校管理");
        addModule(modules, "system", "系统设置");
        addModule(modules, "dashboard", "控制台");
        addModule(modules, "finance", "财务管理");
        addModule(modules, "notification", "通知中心");

        return modules;
    }

    /**
     * 创建分页对象
     */
    private Pageable createPageable(LogQueryParams params) {
        // 默认按操作时间倒序排序
        Sort sort = Sort.by(Direction.DESC, "operTime");

        // 页码从0开始
        int pageNum = (params.getPageNum() != null && params.getPageNum() > 0) ? params.getPageNum() - 1 : 0;
        int pageSize = (params.getPageSize() != null && params.getPageSize() > 0) ? params.getPageSize() : 10;

        return PageRequest.of(pageNum, pageSize, sort);
    }

    /**
     * 创建查询条件
     */
    private Specification<Log> createSpecification(LogQueryParams params) {
        return new Specification<Log>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(@NonNull Root<Log> root, @Nullable CriteriaQuery<?> query,
                    @NonNull CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                // 操作类型条件
                if (StringUtils.hasText(params.getOperType())) {
                    predicates.add(cb.equal(root.get("operType"), params.getOperType()));
                }

                // 操作人员条件
                if (StringUtils.hasText(params.getOperName())) {
                    predicates.add(cb.like(root.get("operName"), "%" + params.getOperName() + "%"));
                }

                // 操作模块条件
                if (StringUtils.hasText(params.getTitle())) {
                    predicates.add(cb.equal(root.get("title"), params.getTitle()));
                }

                // 状态条件
                if (StringUtils.hasText(params.getStatus())) {
                    predicates.add(cb.equal(root.get("status"), params.getStatus()));
                }

                // 学生ID条件
                if (params.getStudentId() != null) {
                    predicates.add(cb.equal(root.get("studentId"), params.getStudentId()));
                }

                // 页面模块条件
                if (StringUtils.hasText(params.getPageModule())) {
                    predicates.add(cb.equal(root.get("pageModule"), params.getPageModule()));
                }

                // 日期范围条件
                if (StringUtils.hasText(params.getBeginTime()) && StringUtils.hasText(params.getEndTime())) {
                    try {
                        Date beginTime = DATE_FORMAT.parse(params.getBeginTime() + " 00:00:00");
                        Date endTime = DATE_FORMAT.parse(params.getEndTime() + " 23:59:59");
                        predicates.add(cb.between(root.get("operTime"), beginTime, endTime));
                    } catch (ParseException e) {
                        log.error("日期解析失败", e);
                    }
                } else if (StringUtils.hasText(params.getBeginTime())) {
                    try {
                        Date beginTime = DATE_FORMAT.parse(params.getBeginTime() + " 00:00:00");
                        predicates.add(cb.greaterThanOrEqualTo(root.get("operTime"), beginTime));
                    } catch (ParseException e) {
                        log.error("开始日期解析失败", e);
                    }
                } else if (StringUtils.hasText(params.getEndTime())) {
                    try {
                        Date endTime = DATE_FORMAT.parse(params.getEndTime() + " 23:59:59");
                        predicates.add(cb.lessThanOrEqualTo(root.get("operTime"), endTime));
                    } catch (ParseException e) {
                        log.error("结束日期解析失败", e);
                    }
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
    }

    /**
     * 创建结果Map
     */
    private Map<String, Object> createResultMap(Page<Log> page) {
        Map<String, Object> result = new HashMap<>();
        result.put("rows", page.getContent());
        result.put("total", page.getTotalElements());
        return result;
    }

    /**
     * 解析日期字符串
     */
    private Date parseDate(String dateStr) {
        if (!StringUtils.hasText(dateStr)) {
            return null;
        }

        try {
            return DATE_FORMAT.parse(dateStr + (dateStr.length() <= 10 ? " 00:00:00" : ""));
        } catch (ParseException e) {
            log.error("日期解析失败: {}", dateStr, e);
            return null;
        }
    }

    /**
     * 从分页结果中获取日志列表用于导出
     */
    @SuppressWarnings("unchecked")
    private List<Log> getLogsForExport(Map<String, Object> pageResult) {
        return (List<Log>) pageResult.get("rows");
    }

    /**
     * 获取操作类型名称
     */
    private String getOperTypeName(String operType) {
        switch (operType) {
            case "1":
                return "登录日志";
            case "2":
                return "操作日志";
            case "3":
                return "系统日志";
            case "4":
                return "错误日志";
            case "5":
                return "其他日志";
            default:
                return operType;
        }
    }

    /**
     * 添加模块到列表
     */
    private void addModule(List<Map<String, String>> modules, String value, String label) {
        Map<String, String> module = new HashMap<>();
        module.put("value", value);
        module.put("label", label);
        modules.add(module);
    }
}
