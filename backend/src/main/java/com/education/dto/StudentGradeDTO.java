package com.education.dto;

import lombok.Data;

@Data
public class StudentGradeDTO {
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseName;
    private Double score;
    private String semester;
} 