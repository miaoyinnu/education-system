package com.education.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GradeAlertDTO {
    private Long id;
    private Long courseId;
    private String courseName;
    private String studentId;
    private String studentName;
    private Double score;
    private Double threshold;
    private String alertLevel;
    private String alertType;
    private String semester;
    private LocalDateTime alertTime;
    private String status;
    private String remark;
} 