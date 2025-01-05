package com.education.dto;

import lombok.Data;

@Data
public class StudentStatsDTO {
    private Integer totalCourses;
    private Integer totalCredits;
    private Double averageScore;
} 