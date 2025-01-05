package com.education.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class CourseAnalysisDTO {
    private Long courseId;
    private String courseName;
    private Integer totalStudents;
    private Double averageScore;
    private Double passRate;
    private Double minScore;
    private Double maxScore;
    private Map<String, Integer> gradeDistribution;
    private List<Map<String, Object>> scoreDistribution;
    private List<Map<String, Object>> attendanceRate;
} 