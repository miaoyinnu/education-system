package com.education.dto;

import lombok.Data;

@Data
public class GradeAlertDTO {
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseName;
    private String teacherName;
    private Integer score;
} 