package com.study.service.students;

import com.study.common.Result;
import com.study.service.user.User;
import com.study.service.user.UserMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

@RestController
@RequestMapping("/api/student")
@Slf4j
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/info/{userId}")
    public Result<Student> getStudentInfo(@PathVariable Long userId) {
        try {
            log.info("获取用户ID为 {} 的学生信息", userId);
            Student student = studentService.getStudentByUserId(userId);
            if (student == null) {
                return Result.success(null, "未找到学生信息");
            }
            return Result.success(student, "获取学生信息成功");
        } catch (Exception e) {
            log.error("获取学生信息失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<Void> updateStudent(@RequestBody Student student) {
        try {
            log.info("更新学生信息: {}", student);
            studentService.updateStudent(student);
            return Result.success(null, "更新学生信息成功");
        } catch (Exception e) {
            log.error("更新学生信息失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/sync")
    public Result<Student> syncStudentInfo(@RequestBody Student student) {
        try {
            log.info("同步学生信息: {}", student);
            Student syncedStudent = studentService.syncStudentInfo(student);
            return Result.success(syncedStudent, "同步学生信息成功");
        } catch (Exception e) {
            log.error("同步学生信息失败", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/gpa/{userId}")
    public Result<StudentGPA> getStudentGPA(@PathVariable Long userId) {
        try {
            log.info("获取用户ID为 {} 的GPA信息", userId);
            StudentGPA gpa = studentService.getStudentGPA(userId);
            if (gpa == null) {
                return Result.success(null, "未找到GPA信息");
            }
            return Result.success(gpa, "获取GPA信息成功");
        } catch (Exception e) {
            log.error("获取GPA信息失败", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadStudentTemplate() throws IOException {
        XWPFDocument document = new XWPFDocument();

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = title.createRun();
        titleRun.setBold(true);
        titleRun.setFontSize(18);
        titleRun.setText("学生信息采集模板（请勿删除 @字段名 前缀）");

        XWPFParagraph tip = document.createParagraph();
        XWPFRun tipRun = tip.createRun();
        tipRun.setFontSize(11);
        tipRun.setText("填写说明：每行以 @字段名 开头，冒号后填写内容；留空表示暂无。系统会根据邮箱/手机号自动创建或匹配账号并分配用户ID。国家用逗号分隔。日期格式：YYYY-MM-DD。");

        String[] lines = new String[] {
                "@name(中文姓名): ",
                "@englishName(英文名): ",
                "@gender(性别 男/女): ",
                "@birthDate(出生日期 YYYY-MM-DD): ",
                "@nationality(国籍): ",
                "@passportNo(护照号): ",
                "@phone(手机号): ",
                "@email(邮箱): ",
                "@wechat(微信): ",
                "@currentSchool(当前学校): ",
                "@major(当前专业): ",
                "@gpa(GPA 如 3.85/4.0 或 87/100): ",
                "@gpaScale(GPA满分 如 4.0/100): ",
                "@toefl(托福): ",
                "@ielts(雅思): ",
                "@gre(GRE): ",
                "@gmat(GMAT): ",
                "@targetCountries(目标国家 用逗号分隔): ",
                "@notes(备注/补充): "
        };

        for (String line : lines) {
            XWPFParagraph p = document.createParagraph();
            XWPFRun r = p.createRun();
            r.setFontSize(12);
            r.setText(line);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.write(baos);
        document.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"student_profile_template.docx\"");

        return ResponseEntity.ok().headers(headers).body(baos.toByteArray());
    }

    @PostMapping(value = "/template/parse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, Object>> parseStudentTemplate(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Long userId) {
        if (file == null || file.isEmpty()) {
            return Result.error("文件为空");
        }

        Map<String, String> raw = new LinkedHashMap<>();
        Pattern fieldPattern = Pattern.compile("^@([a-zA-Z0-9_]+)(?:\\([^)]*\\))?\\s*[:：]\\s*(.*)$");

        try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
            String currentKey = null;
            for (XWPFParagraph p : document.getParagraphs()) {
                String text = p.getText();
                if (text == null)
                    continue;
                text = text.trim();
                if (text.isEmpty())
                    continue;

                Matcher m = fieldPattern.matcher(text);
                if (m.matches()) {
                    currentKey = m.group(1);
                    raw.put(currentKey, m.group(2) == null ? "" : m.group(2).trim());
                } else if (currentKey != null) {
                    String prev = raw.getOrDefault(currentKey, "");
                    String merged = prev.isEmpty() ? text : (prev + "\n" + text);
                    raw.put(currentKey, merged.trim());
                }
            }
        } catch (Exception e) {
            log.error("解析学生模板失败", e);
            return Result.error("解析失败：" + e.getMessage());
        }

        Map<String, Object> result = new LinkedHashMap<>();

        Long resolvedUserId = userId;
        if (resolvedUserId == null) {
            String userIdRaw = raw.get("userId");
            if (userIdRaw != null && !userIdRaw.isBlank()) {
                try {
                    resolvedUserId = Long.parseLong(userIdRaw.trim());
                } catch (NumberFormatException ignored) {
                }
            }
        }
        if (resolvedUserId == null) {
            resolvedUserId = resolveOrCreateUserId(raw);
        }
        if (resolvedUserId == null) {
            return Result.error("缺少用户标识：请至少填写邮箱或手机号，用于自动创建/匹配账号");
        }
        result.put("userId", resolvedUserId);

        putIfNotBlank(result, "name", raw.get("name"));
        putIfNotBlank(result, "englishName", raw.get("englishName"));
        putIfNotBlank(result, "gender", raw.get("gender"));
        putIfNotBlank(result, "birthDate", raw.get("birthDate"));
        putIfNotBlank(result, "nationality", raw.get("nationality"));
        putIfNotBlank(result, "passportNo", raw.get("passportNo"));
        putIfNotBlank(result, "phone", raw.get("phone"));
        putIfNotBlank(result, "email", raw.get("email"));
        putIfNotBlank(result, "wechat", raw.get("wechat"));
        putIfNotBlank(result, "currentSchool", raw.get("currentSchool"));
        putIfNotBlank(result, "major", raw.get("major"));

        String gpaRaw = raw.get("gpa");
        if (gpaRaw != null && !gpaRaw.isBlank()) {
            String cleaned = gpaRaw.trim();
            String scale = null;
            if (cleaned.contains("/")) {
                String[] parts = cleaned.split("/");
                if (parts.length >= 2) {
                    cleaned = parts[0].trim();
                    scale = parts[1].trim();
                }
            }
            Double gpa = parseDouble(cleaned);
            if (gpa != null)
                result.put("gpa", gpa);
            if (scale != null && !scale.isBlank() && result.get("gpaScale") == null) {
                result.put("gpaScale", scale);
            }
        }

        String gpaScaleRaw = raw.get("gpaScale");
        if (gpaScaleRaw != null && !gpaScaleRaw.isBlank()) {
            result.put("gpaScale", gpaScaleRaw.trim());
        }

        Double toefl = parseDouble(raw.get("toefl"));
        if (toefl != null)
            result.put("toefl", toefl);
        Double ielts = parseDouble(raw.get("ielts"));
        if (ielts != null)
            result.put("ielts", ielts);
        Integer gre = parseInteger(raw.get("gre"));
        if (gre != null)
            result.put("gre", gre);
        Integer gmat = parseInteger(raw.get("gmat"));
        if (gmat != null)
            result.put("gmat", gmat);

        String targetCountries = raw.get("targetCountries");
        if (targetCountries != null && !targetCountries.isBlank()) {
            result.put("targetCountries", targetCountries.trim());
        }

        String notes = raw.get("notes");
        if (notes != null && !notes.isBlank()) {
            result.put("notes", notes.trim());
        }

        return Result.success(result, "解析成功");
    }

    private Long resolveOrCreateUserId(Map<String, String> raw) {
        String email = raw.get("email");
        if (email != null) {
            email = email.trim();
        }

        if (email != null && !email.isBlank()) {
            User existing = userMapper.findByEmail(email);
            if (existing != null) {
                return existing.getId();
            }
        }

        String phone = raw.get("phone");
        if (phone != null) {
            phone = phone.trim();
        }

        if (phone != null && !phone.isBlank()) {
            User existing = userMapper.findByPhone(phone);
            if (existing != null) {
                return existing.getId();
            }
        }

        if ((email == null || email.isBlank()) && (phone == null || phone.isBlank())) {
            return null;
        }

        String name = raw.get("name");
        if (name != null) {
            name = name.trim();
        }

        User user = new User();
        user.setEmail((email == null || email.isBlank()) ? null : email);
        user.setPhone((phone == null || phone.isBlank()) ? null : phone);
        user.setName((name == null || name.isBlank()) ? null : name);
        user.setRole("student");
        user.setStatus(1);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        user.setUsername(generateUniqueUsername(email, phone, name));
        String rawPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        user.setPassword(passwordEncoder.encode(rawPassword));

        try {
            userMapper.insert(user);
        } catch (Exception e) {
            if ((user.getEmail() == null || user.getEmail().isBlank()) && user.getPhone() != null
                    && !user.getPhone().isBlank()) {
                String digits = user.getPhone().replaceAll("[^0-9]", "");
                if (!digits.isBlank()) {
                    user.setEmail(digits + "@auto.local");
                    userMapper.insert(user);
                    return user.getId();
                }
            }
            throw e;
        }
        return user.getId();
    }

    private String generateUniqueUsername(String email, String phone, String name) {
        String base = null;
        if (email != null && !email.isBlank() && email.contains("@")) {
            base = email.substring(0, email.indexOf('@'));
        } else if (phone != null && !phone.isBlank()) {
            String p = phone.replaceAll("[^0-9]", "");
            base = p.length() > 6 ? ("stu" + p.substring(p.length() - 6)) : ("stu" + p);
        } else if (name != null && !name.isBlank()) {
            base = name;
        } else {
            base = "stu";
        }

        base = base.toLowerCase().replaceAll("[^a-z0-9_]", "_");
        base = base.replaceAll("_+", "_");
        base = base.replaceAll("^_+", "").replaceAll("_+$", "");
        if (base.isBlank()) {
            base = "stu";
        }
        if (base.length() > 30) {
            base = base.substring(0, 30);
        }

        String candidate = base;
        for (int i = 0; i < 10; i++) {
            User exists = userMapper.findByUsername(candidate);
            if (exists == null) {
                return candidate;
            }
            String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
            String head = base.length() > 23 ? base.substring(0, 23) : base;
            candidate = head + "_" + suffix;
        }
        return "stu_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    private static void putIfNotBlank(Map<String, Object> out, String key, String value) {
        if (value == null)
            return;
        String v = value.trim();
        if (!v.isBlank())
            out.put(key, v);
    }

    private static Double parseDouble(String input) {
        if (input == null)
            return null;
        String s = input.trim();
        if (s.isEmpty())
            return null;
        s = s.replaceAll("[^0-9.\\-]", "");
        if (s.isEmpty() || s.equals(".") || s.equals("-") || s.equals("-."))
            return null;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Integer parseInteger(String input) {
        if (input == null)
            return null;
        String s = input.trim();
        if (s.isEmpty())
            return null;
        s = s.replaceAll("[^0-9\\-]", "");
        if (s.isEmpty() || s.equals("-"))
            return null;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 管理员API：获取所有学生列表
     */
    @GetMapping("/admin/students")
    public Result<List<Student>> getAllStudents() {
        try {
            log.info("获取所有学生列表");
            List<Student> students = studentService.getAllStudents();
            return Result.success(students, "获取学生列表成功");
        } catch (Exception e) {
            log.error("获取学生列表失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员API：添加学生
     */
    @PostMapping("/admin/students")
    public Result<Student> addStudent(@RequestBody Student student) {
        try {
            // 验证必要字段
            if (student.getUserId() == null) {
                log.error("添加学生失败：用户ID不能为空");
                return Result.error("用户ID不能为空");
            }

            // 强制设置ID为null，确保走添加逻辑而不是更新逻辑
            if (student.getId() != null) {
                log.info("添加学生：收到ID为 {} 的请求，强制设置为null以确保走添加逻辑", student.getId());
                student.setId(null);
            }

            log.info("添加学生: {}", student);
            Student addedStudent = studentService.addStudent(student);
            log.info("添加学生成功，新ID: {}", addedStudent.getId());
            return Result.success(addedStudent, "添加学生成功");
        } catch (Exception e) {
            log.error("添加学生失败", e);
            // 直接返回错误消息，不带前缀
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员API：添加新学生（专用API）
     * 此API专门用于添加学生模态框，与更新学生API完全分离
     */
    @PostMapping("/admin/students/add")
    public Result<Student> addNewStudent(@RequestBody Student student) {
        try {
            // 验证必要字段
            if (student.getUserId() == null) {
                log.error("添加新学生失败：用户ID不能为空");
                return Result.error("用户ID不能为空");
            }

            // 强制设置ID为null，确保是新增学生
            student.setId(null);

            log.info("通过专用API添加新学生: {}", student);
            Student addedStudent = studentService.addStudent(student);
            log.info("添加新学生成功，新ID: {}", addedStudent.getId());
            return Result.success(addedStudent, "添加学生成功");
        } catch (Exception e) {
            log.error("添加新学生失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员API：通过POST方法更新学生信息
     */
    @PostMapping("/admin/students/update")
    public Result<Student> updateStudentByAdminPost(@RequestBody Student student) {
        try {
            log.info("管理员通过POST方法更新学生信息: {}", student);

            // 确保学生ID存在
            if (student.getId() == null) {
                return Result.error("学生ID不能为空");
            }

            // 直接更新，无需验证学生是否存在
            boolean updated = studentService.updateStudent(student);
            if (updated) {
                return Result.success(student, "更新学生信息成功");
            } else {
                return Result.error("更新学生信息失败，可能学生不存在");
            }
        } catch (Exception e) {
            log.error("更新学生信息失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员API：删除学生
     */
    @DeleteMapping("/admin/students/delete{id}")
    public Result<Void> deleteStudent(@PathVariable Long id) {
        try {
            log.info("删除学生: ID={}", id);
            studentService.deleteStudent(id);
            return Result.success(null, "删除学生成功");
        } catch (Exception e) {
            log.error("删除学生失败", e);
            return Result.error(e.getMessage());
        }
    }

}
