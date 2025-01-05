package com.education.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class StudentAnalysisDTO {
    private String studentId;
    private String studentName;
    private Double averageScore;
    private Integer totalCourses;
    private Integer passedCourses;
    private Double attendanceRate;
    private Double totalCredits;
    private List<Map<String, Object>> scoreHistory;
    private List<Map<String, Object>> courseScores;
    private Map<String, Integer> gradeDistribution;
} 