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
├── annotation        // 注解
│   └── LogOperation.java    // 日志操作注解
├── aspect            // 切面
│   └── LogAspect.java        // 日志记录切面
├── controller        // 控制器
│   └── LogController.java    // 日志API接口
├── entity            // 实体类
│   ├── Log.java            // 日志实体
│   └── LogQueryParams.java  // 日志查询参数
├── repository        // 数据访问
│   └── LogRepository.java    // 日志数据访问接口
├── service           // 服务层
│   ├── LogService.java      // 日志服务接口
│   └── impl
│       └── LogServiceImpl.java // 日志服务实现
└── util              // 工具类
    └── IpUtils.java          // IP地址工具类
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

## 开发者指南

1. 扩展日志类型：修改`LogOperation`注解和相关常量
2. 添加自定义日志字段：修改`Log`实体类和相关查询方法
3. 增强日志查询功能：扩展`LogRepository`接口和`LogService`实现 

## 2025-11-11
-更新pdfexport.js
-更新schoolmatching.vue
-更新aiselectnew.vue文件
-main.js文件import Particles from "particles.vue3";
-两个uk文件都进行更改
-studentinfo.vue文件进行更改
-UKmatchingalgorithm.java文件更名UKBirminghamMatchingAlgorithm.java
-更新MatchingAlogorithmManger.java文件
-新加入ukgenericmatchingalgorithm文件
-更新matchingresults.vue文件
-更新pdfExport.js

## 2025-11-17
-更新auth文件夹下的wechat文件，需要同步到服务器上
-前端界面下加入了wechatlogin页面
-前端login界面新加入代码，需要修改

## 2025-11-18
-更新VisaService.vue文件
-更新index.js文件
-更新footerbar.vue文件
-更新cases.vue文件
-password下的exception文件需要加入

## 2025-11-20
-更新UKDurham算法文件
-更新MatchingAlgorithmManager.java文件
-matching算法全部修改，pdf文件进行修改，Majordetailmodal.vue文件进行修改

## 2026-1-1
-request.js修改导致的bug需要继续修复

## 2026-1-4 
-更新cases news companyabout TeacherConsultation文件
-Country界面修改 Profile界面修改
-majorindex界面修改

## 2026-1-5
-更新Country MajorIndex News Cases CompanyAbout majorService.js schoolService.js NavBar MajorDetail
-更新News界面
-R界面 study界面 更新

## 2026-1-6
-更新mapper和算法文件夹
-schools.vue

# 2026-1-8
-更新homefirst3.vue文件 index.js文件 TeacherConsulation文件  Nabvbar文件
-更新news和index文件，数据库更新 matchingvue 和loading
-app.vue文件更新
