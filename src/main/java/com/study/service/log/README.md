# 系统日志模块

本模块提供系统操作日志记录、查询和导出功能，用于跟踪用户操作、系统事件和异常情况。

## 功能特性

- 自动记录用户操作日志
- 支持多种日志类型（登录日志、操作日志、系统日志、错误日志）
- 支持学生信息相关日志和主页操作日志的专门记录
- 提供日志查询、过滤和导出功能
- 支持日志清理功能

## 模块结构

```
com.study.service.log
├── Log.java                 // 日志实体
├── LogQueryParams.java      // 日志查询参数
├── LogOperation.java        // 日志操作注解
├── LogAspect.java           // 日志记录切面
├── LogRepository.java       // 日志数据访问接口
├── LogService.java          // 日志服务接口
├── LogServiceImpl.java      // 日志服务实现
├── LogController.java       // 日志API接口
├── IpUtils.java             // IP地址工具类
└── README.md                // 说明文档
```

## API接口

| 接口                  | 方法 | 描述                  |
|---------------------|-----|---------------------|
| /api/log/list       | GET | 获取所有日志列表          |
| /api/log/student    | GET | 获取学生相关日志          |
| /api/log/homepage   | GET | 获取主页操作日志          |
| /api/log/detail/{id} | GET | 获取日志详情            |
| /api/log/types      | GET | 获取日志类型选项          |
| /api/log/modules    | GET | 获取系统模块选项          |
| /api/log/export     | GET | 导出日志               |
| /api/log/clean/{days} | GET | 清理指定天数前的日志       |

## 使用示例

### 1. 添加日志记录注解

在需要记录日志的方法上添加`@LogOperation`注解：

```java
@LogOperation(module = "用户管理", description = "创建用户", operType = "2")
@PostMapping("/user")
public ResponseEntity<Map<String, Object>> createUser(@RequestBody User user) {
    // 方法实现
}
```

### 2. 查询日志

```java
// 获取系统日志列表
@GetMapping("/logs")
public String logs(Model model) {
    LogQueryParams params = new LogQueryParams();
    params.setPageNum(1);
    params.setPageSize(10);
    Map<String, Object> result = logService.getLogsList(params);
    model.addAttribute("logs", result.get("rows"));
    model.addAttribute("total", result.get("total"));
    return "logs/list";
}
```

### 3. 导出日志

```java
// 导出日志
@GetMapping("/export")
public void exportLogs(HttpServletResponse response) {
    LogQueryParams params = new LogQueryParams();
    params.setPageSize(1000); // 设置较大的数量
    byte[] data = logService.exportLogs(params);
    // 设置响应头
    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Content-Disposition", "attachment;filename=logs.xlsx");
    try (ServletOutputStream out = response.getOutputStream()) {
        out.write(data);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

## 配置说明

在`application.properties`中可以添加以下配置：

```properties
# 日志清理配置（默认保留30天）
log.retention.days=30
```

## 依赖要求

- Spring Boot 3.x
- Spring Data JPA
- Spring AOP
- Apache POI（用于Excel导出）
- MySQL/H2数据库
- Java 17+ 