package com.study.service.applicationschool;

import com.study.common.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*", methods = {
        RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS
})
@RestController
@RequestMapping("/api/application-school")
public class ApplicationSchoolController {

    private static final Logger log = LoggerFactory.getLogger(ApplicationSchoolController.class);

    // 状态映射：英文状态 -> 中文显示
    private static final Map<String, String> STATUS_MAPPING = new HashMap<>();
    static {
        STATUS_MAPPING.put("selected", "已选择学校");
        STATUS_MAPPING.put("submitted", "已提交材料");
        STATUS_MAPPING.put("under_review", "材料审核中");
        STATUS_MAPPING.put("interview", "面试阶段");
        STATUS_MAPPING.put("admitted", "已录取");
        STATUS_MAPPING.put("rejected", "被拒绝");
        STATUS_MAPPING.put("visa_processing", "签证申请中");
        STATUS_MAPPING.put("visa_approved", "签证已批准");
        STATUS_MAPPING.put("visa_rejected", "签证被拒");
        STATUS_MAPPING.put("enrolled", "已入学");
    }

    // 中文状态 -> 英文状态的反向映射
    private static final Map<String, String> REVERSE_STATUS_MAPPING = new HashMap<>();
    static {
        REVERSE_STATUS_MAPPING.put("已选择学校", "selected");
        REVERSE_STATUS_MAPPING.put("已提交材料", "submitted");
        REVERSE_STATUS_MAPPING.put("材料审核中", "under_review");
        REVERSE_STATUS_MAPPING.put("面试阶段", "interview");
        REVERSE_STATUS_MAPPING.put("已录取", "admitted");
        REVERSE_STATUS_MAPPING.put("被拒绝", "rejected");
        REVERSE_STATUS_MAPPING.put("签证申请中", "visa_processing");
        REVERSE_STATUS_MAPPING.put("签证已批准", "visa_approved");
        REVERSE_STATUS_MAPPING.put("签证被拒", "visa_rejected");
        REVERSE_STATUS_MAPPING.put("已入学", "enrolled");
    }

    /**
     * 将英文状态转换为中文显示
     */
    private String getChineseStatus(String englishStatus) {
        return STATUS_MAPPING.getOrDefault(englishStatus, englishStatus);
    }

    /**
     * 将中文状态转换为英文存储
     */
    private String getEnglishStatus(String chineseStatus) {
        return REVERSE_STATUS_MAPPING.getOrDefault(chineseStatus, chineseStatus);
    }

    @Autowired
    private ApplicationSchoolService applicationSchoolService;

    @PostMapping("/submit")
    public Result<ApplicationSchool> submit(@RequestBody ApplicationSchool applicationSchool) {
        try {
            // 打印接收到的数据
            System.out.println("Received application data: " + applicationSchool);
            ApplicationSchool submitted = applicationSchoolService.submit(applicationSchool);
            return Result.success(submitted);
        } catch (Exception e) {
            e.printStackTrace();
            // 返回更详细的错误信息
            return Result.error("提交申请失败：" + e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getSubmittedApplications(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "studentId", required = false) Long studentId,
            @RequestParam(value = "country", required = false) String country) {
        try {
            log.info("Received request for applications with userId: {}, studentId: {}, country: {}", userId, studentId,
                    country);

            // 获取申请列表
            List<ApplicationSchool> applications = applicationSchoolService.getSubmittedApplications(userId, studentId,
                    country);

            // 检查结果
            if (applications == null) {
                log.warn("No applications found or error occurred");
                return Result.error("获取申请列表失败");
            }

            // 为每个申请添加中文状态显示
            List<Map<String, Object>> applicationsWithChineseStatus = new ArrayList<>();
            for (ApplicationSchool app : applications) {
                Map<String, Object> appMap = new HashMap<>();
                appMap.put("id", app.getId());
                appMap.put("userId", app.getUserId());
                appMap.put("studentId", app.getStudentId());
                appMap.put("schoolName", app.getSchoolName());
                appMap.put("majorName", app.getMajorName());
                appMap.put("country", app.getCountry());
                appMap.put("applicationDeadline", app.getApplicationDeadline());
                appMap.put("applicationStatus", app.getApplicationStatus()); // 英文状态
                appMap.put("applicationStatusDisplay", getChineseStatus(app.getApplicationStatus())); // 中文状态显示
                appMap.put("visaInfoProvided", app.getVisaInfoProvided());
                appMap.put("createTime", app.getCreateTime());
                appMap.put("updateTime", app.getUpdateTime());

                // 添加学生相关信息
                appMap.put("studentName", app.getStudentName());
                appMap.put("studentEmail", app.getStudentEmail());
                appMap.put("userName", app.getUserName());
                appMap.put("userEmail", app.getUserEmail());
                appMap.put("phone", app.getPhone());

                applicationsWithChineseStatus.add(appMap);
            }

            log.info("Found {} applications", applications.size());
            return Result.success(applicationsWithChineseStatus);
        } catch (Exception e) {
            log.error("Error getting applications", e);
            return Result.error("获取申请列表时发生错误: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            // 如果传入的是中文状态，转换为英文状态存储
            String englishStatus = getEnglishStatus(status);
            if (englishStatus == null) {
                // 如果不是中文状态，直接使用传入的状态（可能已经是英文）
                englishStatus = status;
            }

            boolean updated = applicationSchoolService.updateApplicationStatus(id, englishStatus);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.error("更新状态失败：" + e.getMessage());
        }
    }

    @GetMapping("/check-join")
    public Result<String> checkJoin(@RequestParam Long userId) {
        try {
            boolean joined = applicationSchoolService.checkTablesJoined(userId);
            if (joined) {
                return Result.success("三表关联正常");
            } else {
                return Result.error("未找到关联数据");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("检查关联失败：" + e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> getDetail(@PathVariable Long id) {
        try {
            ApplicationSchool detail = applicationSchoolService.getApplicationDetail(id);
            if (detail != null) {
                // 为详情添加中文状态显示
                Map<String, Object> detailMap = new HashMap<>();
                detailMap.put("id", detail.getId());
                detailMap.put("userId", detail.getUserId());
                detailMap.put("studentId", detail.getStudentId());
                detailMap.put("schoolName", detail.getSchoolName());
                detailMap.put("majorName", detail.getMajorName());
                detailMap.put("country", detail.getCountry());
                detailMap.put("applicationDeadline", detail.getApplicationDeadline());
                detailMap.put("applicationStatus", detail.getApplicationStatus()); // 英文状态
                detailMap.put("applicationStatusDisplay", getChineseStatus(detail.getApplicationStatus())); // 中文状态显示
                detailMap.put("visaInfoProvided", detail.getVisaInfoProvided());
                detailMap.put("createTime", detail.getCreateTime());
                detailMap.put("updateTime", detail.getUpdateTime());

                return Result.success(detailMap);
            } else {
                return Result.error("未找到申请详情");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取详情失败：" + e.getMessage());
        }
    }

    @GetMapping("/test-join")
    public Result<Map<String, Object>> testJoinTables() {
        try {
            List<ApplicationSchool> applications = applicationSchoolService.getSubmittedApplications(1L, null, null);

            Map<String, Object> result = new HashMap<>();
            result.put("applicationsCount", applications.size());
            result.put("status", "success");

            if (applications.size() > 0) {
                // 获取第一个申请对象的信息
                ApplicationSchool application = applications.get(0);
                result.put("firstApplicationId", application.getId());
                result.put("studentName", application.getStudentName());
                result.put("studentEmail", application.getStudentEmail());
                result.put("userName", application.getUserName());
                result.put("userEmail", application.getUserEmail());
            }

            return Result.success(result);
        } catch (Exception e) {
            log.error("Error testing join tables", e);
            return Result.error("测试连接表时发生错误: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/visa-info")
    public Result<Boolean> updateVisaInfo(
            @PathVariable Long id,
            @RequestParam Boolean visaInfoProvided) {
        try {
            log.info("Updating visa info for application: {}, visaInfoProvided: {}", id, visaInfoProvided);
            boolean success = applicationSchoolService.updateVisaInfoProvided(id, visaInfoProvided);
            if (success) {
                return Result.success(true);
            } else {
                return Result.error("更新签证信息失败");
            }
        } catch (Exception e) {
            log.error("Error updating visa info", e);
            return Result.error("更新签证信息时发生错误: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteApplication(@PathVariable Long id) {
        try {
            log.info("Deleting application with id: {}", id);
            boolean success = applicationSchoolService.deleteApplication(id);
            if (success) {
                return Result.success(true);
            } else {
                return Result.error("删除申请失败");
            }
        } catch (Exception e) {
            log.error("Error deleting application", e);
            return Result.error("删除申请时发生错误: " + e.getMessage());
        }
    }

    /**
     * 获取可申请的学校列表
     * 
     * @param userId 用户ID
     * @return 可申请的学校列表
     */
    @GetMapping("/available")
    public Result<List<Map<String, Object>>> getAvailableSchools(
            @RequestParam(value = "userId", required = false) Long userId) {
        try {
            log.info("Getting available schools for userId: {}", userId);

            // 获取用户已申请的学校列表
            List<ApplicationSchool> applications = applicationSchoolService.getSubmittedApplications(userId, null,
                    null);

            if (applications == null) {
                applications = new ArrayList<>();
            }

            // 构建可申请学校列表（这里返回已申请的学校作为示例）
            List<Map<String, Object>> availableSchools = new ArrayList<>();

            for (ApplicationSchool app : applications) {
                if (app.getSchoolId() != null && app.getMajorId() != null) {
                    Map<String, Object> school = new HashMap<>();
                    school.put("id", app.getId());
                    school.put("schoolName", app.getSchoolName());
                    school.put("programName", app.getMajorName());
                    school.put("country", app.getCountry());
                    school.put("applicationStatus", app.getApplicationStatus());
                    availableSchools.add(school);
                }
            }

            log.info("Found {} available schools for user", availableSchools.size());
            return Result.success(availableSchools);
        } catch (Exception e) {
            log.error("Error getting available schools", e);
            return Result.error("获取可申请学校时发生错误: " + e.getMessage());
        }
    }

    /**
     * 获取申请进度详情
     * 
     * @param userId    用户ID
     * @param studentId 学生ID（可选）
     * @return 申请进度详情
     */
    /**
     * 获取用户申请的学校列表
     */
    @GetMapping("/schools")
    public Result<List<Map<String, Object>>> getApplicationSchools(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "studentId", required = false) Long studentId) {
        try {
            log.info("Getting application schools for userId: {}, studentId: {}", userId, studentId);

            // 获取申请列表
            List<ApplicationSchool> applications = applicationSchoolService.getSubmittedApplications(userId, studentId,
                    null);

            if (applications == null) {
                return Result.error("获取申请学校失败");
            }

            // 构建学校+专业组合列表，不去重，显示每个申请的学校+专业
            List<Map<String, Object>> schoolPrograms = new ArrayList<>();
            Set<String> addedCombinations = new HashSet<>();

            for (ApplicationSchool app : applications) {
                if (app.getSchoolId() != null && app.getMajorId() != null) {
                    // 创建学校+专业的唯一标识
                    String combination = app.getSchoolId() + "_" + app.getMajorId();

                    if (!addedCombinations.contains(combination)) {
                        Map<String, Object> schoolProgram = new HashMap<>();
                        schoolProgram.put("schoolId", app.getSchoolId());
                        schoolProgram.put("majorId", app.getMajorId());
                        schoolProgram.put("schoolName", app.getSchoolName());
                        schoolProgram.put("majorName", app.getMajorName());
                        schoolProgram.put("displayName", app.getSchoolName() + " - " + app.getMajorName());
                        schoolProgram.put("country", app.getCountry());
                        schoolProgram.put("applicationId", app.getId());
                        schoolPrograms.add(schoolProgram);
                        addedCombinations.add(combination);
                    }
                }
            }

            log.info("Found {} unique school-program combinations for user", schoolPrograms.size());
            return Result.success(schoolPrograms);
        } catch (Exception e) {
            log.error("Error getting application schools", e);
            return Result.error("获取申请学校时发生错误: " + e.getMessage());
        }
    }

    @GetMapping("/progress")
    public Result<Map<String, Object>> getApplicationProgress(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "studentId", required = false) Long studentId,
            @RequestParam(value = "schoolId", required = false) Long schoolId,
            @RequestParam(value = "applicationId", required = false) Long applicationId) {
        try {
            log.info("Getting application progress for userId: {}, studentId: {}, schoolId: {}",
                    userId, studentId, schoolId);
            log.info("ApplicationId parameter: {}", applicationId);

            List<ApplicationSchool> applications;

            // 如果指定了applicationId，直接查询该申请记录
            if (applicationId != null) {
                ApplicationSchool app = applicationSchoolService.getApplicationDetail(applicationId);
                if (app != null && (userId == null || userId.equals(app.getUserId()))) {
                    applications = java.util.Arrays.asList(app);
                    log.info("Found application by applicationId {}: {}", applicationId, app.getSchoolName());
                } else {
                    applications = new ArrayList<>();
                    log.info("No application found for applicationId: {}", applicationId);
                }
            } else {
                // 获取申请列表
                applications = applicationSchoolService.getSubmittedApplications(userId, studentId, null);

                // 如果指定了schoolId，则过滤出该学校的申请
                if (schoolId != null) {
                    applications = applications.stream()
                            .filter(app -> schoolId.equals(app.getSchoolId()))
                            .collect(java.util.stream.Collectors.toList());
                    log.info("Filtered applications by schoolId {}: {} applications found", schoolId,
                            applications.size());
                }
            }

            if (applications == null) {
                return Result.error("获取申请进度失败");
            }

            // 构建进度数据
            Map<String, Object> progressData = new HashMap<>();

            // 申请状态时间线
            List<Map<String, Object>> statusTimeline = new ArrayList<>();
            List<Map<String, Object>> notifications = new ArrayList<>();
            List<Map<String, Object>> tasks = new ArrayList<>();

            // 如果查询单个申请，只返回该申请的进度
            if (applicationId != null && !applications.isEmpty()) {
                ApplicationSchool app = applications.get(0);
                log.info("Processing single application: id={}, schoolName={}, majorName={}, status={}",
                        app.getId(), app.getSchoolName(), app.getMajorName(), app.getApplicationStatus());
                statusTimeline = generateStatusTimeline(app);
                notifications = generateNotifications(app);
                tasks = generateTasks(app);
                log.info("Generated {} tasks for single application", tasks.size());
            } else {
                // 查询多个申请时，合并所有申请的进度
                for (ApplicationSchool app : applications) {
                    // 根据申请状态生成时间线
                    List<Map<String, Object>> appTimeline = generateStatusTimeline(app);
                    statusTimeline.addAll(appTimeline);

                    // 生成通知
                    List<Map<String, Object>> appNotifications = generateNotifications(app);
                    notifications.addAll(appNotifications);

                    // 生成待办事项
                    List<Map<String, Object>> appTasks = generateTasks(app);
                    tasks.addAll(appTasks);
                }
            }

            progressData.put("applicationStatus", statusTimeline);
            progressData.put("notifications", notifications);
            progressData.put("tasks", tasks);
            progressData.put("applicationsCount", applications.size());

            return Result.success(progressData);
        } catch (Exception e) {
            log.error("Error getting application progress", e);
            return Result.error("获取申请进度时发生错误: " + e.getMessage());
        }
    }

    /**
     * 根据申请状态生成时间线
     */
    private List<Map<String, Object>> generateStatusTimeline(ApplicationSchool app) {
        List<Map<String, Object>> timeline = new ArrayList<>();

        // 根据申请状态生成不同的时间线项
        String status = app.getApplicationStatus();
        if (status == null)
            status = "selected";

        String schoolName = app.getSchoolName();
        String majorName = app.getMajorName();
        String submitTimeStr = resolveEventDate(app.getSubmitTime(), app.getCreateTime(), app.getUpdateTime());

        switch (status) {
            case "selected":
                // 刚选择学校，还未提交材料
                timeline.add(createTimelineItem("completed", "选择学校专业",
                        submitTimeStr, "您已选择" + schoolName + " " + majorName + "专业", 20));

                // 当前需要做的步骤
                timeline.add(createTimelineItem("current", "准备申请材料",
                        "进行中", "请准备个人陈述、成绩单、推荐信等申请材料", 20));

                // 未来步骤
                timeline.add(createTimelineItem("pending", "提交申请",
                        "待完成", "在学校官网完成在线申请表格填写并提交", 0));
                timeline.add(createTimelineItem("pending", "材料审核",
                        "待提交后", "学校将对您的申请材料进行审核", 0));
                timeline.add(createTimelineItem("pending", "录取决定",
                        "预计8-12周", "学校将发布最终录取结果", 0));
                break;

            case "submitted":
                // 已提交材料
                timeline.add(createTimelineItem("completed", "选择学校专业",
                        submitTimeStr, "您已选择" + schoolName + " " + majorName + "专业", 20));
                timeline.add(createTimelineItem("completed", "申请材料已提交",
                        submitTimeStr, "您的申请材料已成功提交至" + schoolName + " " + majorName + "专业", 20));

                // 当前进行中的步骤
                timeline.add(createTimelineItem("current", "材料初审中",
                        "进行中", "招生办公室正在进行材料完整性检查", 20));

                // 未来步骤
                timeline.add(createTimelineItem("pending", "院系审核",
                        "预计2-3周", "院系将对您的学术背景进行详细评估", 0));
                timeline.add(createTimelineItem("pending", "录取决定",
                        "预计8-12周", "学校将发布最终录取结果", 0));
                break;

            case "under_review":
                // 材料审核中
                timeline.add(createTimelineItem("completed", "选择学校专业",
                        submitTimeStr, "您已选择" + schoolName + " " + majorName + "专业", 20));
                timeline.add(createTimelineItem("completed", "申请材料已提交",
                        submitTimeStr, "您的申请材料已成功提交至" + schoolName + " " + majorName + "专业", 20));
                timeline.add(createTimelineItem("completed", "材料初审通过",
                        "提交后1天", "您的申请材料已通过初步审核，材料完整", 20));

                // 当前进行中的步骤
                timeline.add(createTimelineItem("current", "院系学术审核",
                        "进行中", "院系正在评估您的学术背景和研究潜力", 20));

                // 未来步骤
                timeline.add(createTimelineItem("pending", "面试邀请",
                        "预计2-4周", "通过学术审核后可能安排面试", 0));
                timeline.add(createTimelineItem("pending", "录取决定",
                        "预计6-8周", "学校将发布最终录取结果", 0));
                break;

            case "interview":
                // 面试阶段
                timeline.add(createTimelineItem("completed", "选择学校专业",
                        submitTimeStr, "您已选择" + schoolName + " " + majorName + "专业", 16));
                timeline.add(createTimelineItem("completed", "申请材料已提交",
                        submitTimeStr, "您的申请材料已成功提交", 16));
                timeline.add(createTimelineItem("completed", "材料审核通过",
                        "提交后1天", "您的申请材料已通过全面审核", 16));
                timeline.add(createTimelineItem("completed", "面试邀请",
                        "提交后2-4周", "恭喜！您已收到面试邀请", 16));

                // 当前步骤
                timeline.add(createTimelineItem("current", "面试准备",
                        "进行中", "请准备面试，展示您的学术能力和研究兴趣", 16));

                // 未来步骤
                timeline.add(createTimelineItem("pending", "录取决定",
                        "面试后2-4周", "学校将基于面试表现做出最终决定", 0));
                break;

            case "admitted":
                // 已录取
                timeline.add(createTimelineItem("completed", "选择学校专业",
                        submitTimeStr, "您已选择" + schoolName + " " + majorName + "专业", 16));
                timeline.add(createTimelineItem("completed", "申请材料已提交",
                        submitTimeStr, "您的申请材料已成功提交", 16));
                timeline.add(createTimelineItem("completed", "材料审核通过",
                        "提交后1天", "您的申请材料已通过全面审核", 16));
                timeline.add(createTimelineItem("completed", "面试完成",
                        "提交后4-6周", "面试表现优秀，获得认可", 16));
                timeline.add(createTimelineItem("completed", "获得录取",
                        "提交后8-12周", "恭喜！您已被" + schoolName + " " + majorName + "专业录取", 16));

                // 当前步骤
                timeline.add(createTimelineItem("current", "确认入学",
                        "待确认", "请确认是否接受录取并完成入学手续", 20));
                break;

            case "rejected":
                // 被拒绝
                timeline.add(createTimelineItem("completed", "选择学校专业",
                        submitTimeStr, "您已选择" + schoolName + " " + majorName + "专业", 25));
                timeline.add(createTimelineItem("completed", "申请材料已提交",
                        submitTimeStr, "您的申请材料已成功提交", 25));
                timeline.add(createTimelineItem("completed", "材料审核完成",
                        "提交后1天", "您的申请材料已完成审核", 25));
                timeline.add(createTimelineItem("rejected", "申请未通过",
                        "提交后8-12周", "很遗憾，您的申请未能通过" + schoolName + "的选拔", 25));
                break;

            default:
                timeline.add(createTimelineItem("pending", "开始申请",
                        "待定", "请开始选择学校和专业", 0));
                break;
        }

        for (Map<String, Object> item : timeline) {
            if (!item.containsKey("applicationId")) {
                item.put("applicationId", app.getId());
            }
        }

        return timeline;
    }

    private String resolveEventDate(Object primary, Object fallback1, Object fallback2) {
        String v = tryFormatDate(primary);
        if (v != null) {
            return v;
        }
        v = tryFormatDate(fallback1);
        if (v != null) {
            return v;
        }
        v = tryFormatDate(fallback2);
        if (v != null) {
            return v;
        }
        return "今天";
    }

    private String tryFormatDate(Object value) {
        if (value == null) {
            return null;
        }
        String s = value.toString();
        if (s == null) {
            return null;
        }
        s = s.trim();
        if (s.isEmpty()) {
            return null;
        }
        return s.length() >= 10 ? s.substring(0, 10) : s;
    }

    /**
     * 创建时间线项
     */
    private Map<String, Object> createTimelineItem(String type, String text, String date,
            String description, int progress) {
        Map<String, Object> item = new HashMap<>();
        item.put("type", type);
        item.put("text", text);
        item.put("date", date);
        item.put("description", description);
        if (progress >= 0) {
            item.put("progress", progress);
        }
        return item;
    }

    /**
     * 生成通知
     */
    private List<Map<String, Object>> generateNotifications(ApplicationSchool app) {
        List<Map<String, Object>> notifications = new ArrayList<>();

        String status = app.getApplicationStatus();
        if (status == null)
            status = "selected";
        String schoolName = app.getSchoolName();
        String majorName = app.getMajorName();

        switch (status) {

            case "submitted":
                Map<String, Object> confirmNotification = new HashMap<>();
                confirmNotification.put("type", "email");
                confirmNotification.put("title", "申请确认邮件");
                confirmNotification.put("time", "2小时前");
                confirmNotification.put("text", schoolName + "已确认收到您的申请材料，申请编号：APP" + app.getId());
                confirmNotification.put("read", false);
                confirmNotification.put("action", "查看详情");
                notifications.add(confirmNotification);
                break;

            case "under_review":
                Map<String, Object> reviewNotification = new HashMap<>();
                reviewNotification.put("type", "email");
                reviewNotification.put("title", "院系邮件");
                reviewNotification.put("time", "1小时前");
                reviewNotification.put("text", schoolName + "需要您补充部分申请材料，请查看详细要求");
                reviewNotification.put("read", false);
                reviewNotification.put("action", "查看详情");
                notifications.add(reviewNotification);

                Map<String, Object> statusUpdate = new HashMap<>();
                statusUpdate.put("type", "update");
                statusUpdate.put("title", "状态更新");
                statusUpdate.put("time", "昨天");
                statusUpdate.put("text", "您的申请已进入院系学术审核阶段");
                statusUpdate.put("read", true);
                statusUpdate.put("action", "查看进度");
                notifications.add(statusUpdate);
                break;

            case "interview":
                Map<String, Object> interviewNotification = new HashMap<>();
                interviewNotification.put("type", "email");
                interviewNotification.put("title", "面试邀请");
                interviewNotification.put("time", "3小时前");
                interviewNotification.put("text", "恭喜！" + schoolName + "邀请您参加" + majorName + "专业的面试");
                interviewNotification.put("read", false);
                interviewNotification.put("action", "安排面试");
                notifications.add(interviewNotification);

                Map<String, Object> prepNotification = new HashMap<>();
                prepNotification.put("type", "alert");
                prepNotification.put("title", "面试准备提醒");
                prepNotification.put("time", "今天");
                prepNotification.put("text", "请准备面试材料，包括研究计划和学术作品集");
                prepNotification.put("read", false);
                prepNotification.put("action", "查看要求");
                notifications.add(prepNotification);
                break;

            case "selected":
                Map<String, Object> materialPrepNotification = new HashMap<>();
                materialPrepNotification.put("type", "alert");
                materialPrepNotification.put("title", "材料准备提醒");
                materialPrepNotification.put("time", "今天");
                materialPrepNotification.put("text", "请准备个人陈述、成绩单、推荐信等申请材料");
                materialPrepNotification.put("read", false);
                materialPrepNotification.put("action", "上传材料");
                notifications.add(materialPrepNotification);

                Map<String, Object> submissionNotification = new HashMap<>();
                submissionNotification.put("type", "update");
                submissionNotification.put("title", "提交申请提醒");
                submissionNotification.put("time", "今天");
                submissionNotification.put("text", "材料准备完成后，请在" + schoolName + "官网完成网申并提交");
                submissionNotification.put("read", true);
                submissionNotification.put("action", "查看进度");
                notifications.add(submissionNotification);
                break;

            case "admitted":
                Map<String, Object> admittedNotification = new HashMap<>();
                admittedNotification.put("type", "email");
                admittedNotification.put("title", "录取通知");
                admittedNotification.put("time", "2小时前");
                admittedNotification.put("text", "恭喜！您已被" + schoolName + " " + majorName + "专业录取");
                admittedNotification.put("read", false);
                admittedNotification.put("action", "查看详情");
                notifications.add(admittedNotification);

                Map<String, Object> enrollmentReminder = new HashMap<>();
                enrollmentReminder.put("type", "alert");
                enrollmentReminder.put("title", "入学手续提醒");
                enrollmentReminder.put("time", "今天");
                enrollmentReminder.put("text", "请在规定时间内确认入学意向并完成相关手续");
                enrollmentReminder.put("read", false);
                enrollmentReminder.put("action", "查看要求");
                notifications.add(enrollmentReminder);
                break;

            case "rejected":
                Map<String, Object> rejectionNotification = new HashMap<>();
                rejectionNotification.put("type", "email");
                rejectionNotification.put("title", "申请结果通知");
                rejectionNotification.put("time", "1天前");
                rejectionNotification.put("text", "很遗憾，" + schoolName + " " + majorName + "专业未能录取您的申请");
                rejectionNotification.put("read", false);
                rejectionNotification.put("action", "查看反馈");
                notifications.add(rejectionNotification);

                Map<String, Object> encourageNotification = new HashMap<>();
                encourageNotification.put("type", "alert");
                encourageNotification.put("title", "申请建议");
                encourageNotification.put("time", "今天");
                encourageNotification.put("text", "建议您考虑申请其他合适的学校和专业");
                encourageNotification.put("read", false);
                encourageNotification.put("action", "查看建议");
                notifications.add(encourageNotification);
                break;
        }

        for (Map<String, Object> item : notifications) {
            if (!item.containsKey("applicationId")) {
                item.put("applicationId", app.getId());
            }
        }

        return notifications;
    }

    /**
     * 生成待办事项
     */
    private List<Map<String, Object>> generateTasks(ApplicationSchool app) {
        List<Map<String, Object>> tasks = new ArrayList<>();

        String status = app.getApplicationStatus();
        if (status == null)
            status = "selected";
        String schoolName = app.getSchoolName();
        String majorName = app.getMajorName();

        switch (status) {

            case "submitted":
                Map<String, Object> trackTask = new HashMap<>();
                trackTask.put("title", "跟踪申请状态");
                trackTask.put("description", "定期查看" + schoolName + "申请状态更新");
                trackTask.put("deadline", "持续关注");
                trackTask.put("completed", false);
                trackTask.put("priority", "low");
                tasks.add(trackTask);
                break;

            case "under_review":
                Map<String, Object> supplementTask = new HashMap<>();
                supplementTask.put("title", "补充申请材料");
                supplementTask.put("description", "根据" + schoolName + "要求补充额外的学术材料");
                supplementTask.put("deadline", "尽快");
                supplementTask.put("completed", false);
                supplementTask.put("priority", "high");
                tasks.add(supplementTask);

                Map<String, Object> contactTask = new HashMap<>();
                contactTask.put("title", "联系招生办");
                contactTask.put("description", "如有疑问，及时联系" + schoolName + "招生办公室");
                contactTask.put("deadline", "随时");
                contactTask.put("completed", false);
                contactTask.put("priority", "medium");
                tasks.add(contactTask);
                break;

            case "interview":
                Map<String, Object> prepInterviewTask = new HashMap<>();
                prepInterviewTask.put("title", "准备面试材料");
                prepInterviewTask.put("description", "准备研究计划、学术作品集和面试问题");
                prepInterviewTask.put("deadline", "面试前1周");
                prepInterviewTask.put("completed", false);
                prepInterviewTask.put("priority", "high");
                tasks.add(prepInterviewTask);

                Map<String, Object> scheduleTask = new HashMap<>();
                scheduleTask.put("title", "安排面试时间");
                scheduleTask.put("description", "与" + schoolName + "协调确定面试时间");
                scheduleTask.put("deadline", "尽快");
                scheduleTask.put("completed", false);
                scheduleTask.put("priority", "high");
                tasks.add(scheduleTask);

                Map<String, Object> practiceTask = new HashMap<>();
                practiceTask.put("title", "模拟面试练习");
                practiceTask.put("description", "进行模拟面试，提高表达能力和自信心");
                practiceTask.put("deadline", "面试前");
                practiceTask.put("completed", false);
                practiceTask.put("priority", "medium");
                tasks.add(practiceTask);
                break;

            case "selected":
                Map<String, Object> prepareMaterialsTask = new HashMap<>();
                prepareMaterialsTask.put("title", "准备申请材料");
                prepareMaterialsTask.put("description", "准备个人陈述、成绩单、推荐信等申请材料");
                prepareMaterialsTask.put("deadline", "尽快");
                prepareMaterialsTask.put("completed", false);
                prepareMaterialsTask.put("priority", "high");
                tasks.add(prepareMaterialsTask);

                Map<String, Object> fillFormTask = new HashMap<>();
                fillFormTask.put("title", "完成网申并提交");
                fillFormTask.put("description", "在" + schoolName + "官网完成在线申请表格填写并提交");
                fillFormTask.put("deadline", "申请截止前");
                fillFormTask.put("completed", false);
                fillFormTask.put("priority", "high");
                tasks.add(fillFormTask);

                Map<String, Object> checkRequirementsTask = new HashMap<>();
                checkRequirementsTask.put("title", "核对材料清单");
                checkRequirementsTask.put("description", "核对" + schoolName + " " + majorName + "专业的材料要求与格式");
                checkRequirementsTask.put("deadline", "尽快");
                checkRequirementsTask.put("completed", false);
                checkRequirementsTask.put("priority", "medium");
                tasks.add(checkRequirementsTask);
                break;

            case "admitted":
                Map<String, Object> confirmTask = new HashMap<>();
                confirmTask.put("title", "确认入学意向 - " + majorName);
                confirmTask.put("description", "确认是否接受" + schoolName + " " + majorName + "专业的录取");
                confirmTask.put("deadline", "按录取信截止日期");
                confirmTask.put("completed", false);
                confirmTask.put("priority", "high");
                tasks.add(confirmTask);

                Map<String, Object> depositTask = new HashMap<>();
                depositTask.put("title", "支付入学押金 - " + schoolName);
                depositTask.put("description", "支付" + schoolName + " " + majorName + "专业要求的入学押金");
                depositTask.put("deadline", "按录取信要求");
                depositTask.put("completed", false);
                depositTask.put("priority", "high");
                tasks.add(depositTask);

                Map<String, Object> visaTask = new HashMap<>();
                visaTask.put("title", "准备签证申请 - " + majorName);
                visaTask.put("description", "为" + schoolName + " " + majorName + "专业准备学生签证申请材料");
                visaTask.put("deadline", "开学前");
                visaTask.put("completed", false);
                visaTask.put("priority", "medium");
                tasks.add(visaTask);

                Map<String, Object> housingTask = new HashMap<>();
                housingTask.put("title", "申请学生宿舍 - " + schoolName);
                housingTask.put("description", "申请" + schoolName + "的学生宿舍或寻找校外住房");
                housingTask.put("deadline", "开学前");
                housingTask.put("completed", false);
                housingTask.put("priority", "medium");
                tasks.add(housingTask);
                break;

            case "rejected":
                Map<String, Object> reviewTask = new HashMap<>();
                reviewTask.put("title", "分析申请反馈");
                reviewTask.put("description", "仔细分析" + schoolName + "的拒绝原因和改进建议");
                reviewTask.put("deadline", "尽快");
                reviewTask.put("completed", false);
                reviewTask.put("priority", "medium");
                tasks.add(reviewTask);

                Map<String, Object> alternativeTask = new HashMap<>();
                alternativeTask.put("title", "寻找替代方案");
                alternativeTask.put("description", "考虑申请其他合适的学校和专业");
                alternativeTask.put("deadline", "下一申请季");
                alternativeTask.put("completed", false);
                alternativeTask.put("priority", "high");
                tasks.add(alternativeTask);
                break;
        }

        for (Map<String, Object> item : tasks) {
            if (!item.containsKey("applicationId")) {
                item.put("applicationId", app.getId());
            }
        }

        return tasks;
    }
}
