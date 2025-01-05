package com.education.dto;

import lombok.Data;

@Data
public class GradeDTO {
    private Long id;
    private String courseName;
    private String teacherName;
    private Double score;
    private String semester;
    private Integer credit;
} 