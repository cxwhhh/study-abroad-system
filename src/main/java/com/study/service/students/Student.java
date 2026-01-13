package com.study.service.students;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

@Data
public class Student {
    private Long id;
    private Long userId;
    private String name;
    private String englishName;
    private String gender;
    @JsonDeserialize(using = Student.MultiFormatDateDeserializer.class)
    private Date birthDate;
    private String nationality;
    private String passportNo;
    private String phone;
    private String email;
    private String wechat;
    private String currentSchool;
    private String major;
    private Double gpa;
    private String gpaScale;
    private Double toefl;
    private Double ielts;
    private Integer gre;
    private Integer gmat;
    private String applicationStatus;
    private Integer applicationProgress;
    private String currentStep;
    private String targetCountries;
    private Long advisorId;
    private String notes;
    private Date createTime;
    private Date updateTime;

    static class MultiFormatDateDeserializer extends JsonDeserializer<Date> {
        private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        private static final DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonToken t = p.currentToken();
            if (t == JsonToken.VALUE_NULL) {
                return null;
            }
            if (t == JsonToken.VALUE_NUMBER_INT) {
                long v = p.getLongValue();
                if (v > 0 && v < 100000000000L) {
                    v = v * 1000L;
                }
                return new Date(v);
            }
            if (t == JsonToken.VALUE_STRING) {
                String s = p.getText();
                if (s == null) {
                    return null;
                }
                s = s.trim();
                if (s.isEmpty()) {
                    return null;
                }
                if (s.matches("^\\d{10}$")) {
                    long sec = Long.parseLong(s);
                    return new Date(sec * 1000L);
                }
                if (s.matches("^\\d{13}$")) {
                    long ms = Long.parseLong(s);
                    return new Date(ms);
                }
                try {
                    Instant instant = Instant.parse(s);
                    return Date.from(instant);
                } catch (DateTimeParseException ignored) {
                }
                ZoneId zone = ZoneId.systemDefault();
                try {
                    LocalDateTime ldt = LocalDateTime.parse(s, DATETIME);
                    return Date.from(ldt.atZone(zone).toInstant());
                } catch (DateTimeParseException ignored) {
                }
                try {
                    LocalDate ld = LocalDate.parse(s.length() >= 10 ? s.substring(0, 10) : s, DATE);
                    return Date.from(ld.atStartOfDay(zone).toInstant());
                } catch (DateTimeParseException ignored) {
                }
                return null;
            }
            return (Date) ctxt.handleUnexpectedToken(Date.class, p);
        }
    }
}
