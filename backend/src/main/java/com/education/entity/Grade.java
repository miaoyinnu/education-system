package com.education.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Grade {
    private Long id;
    private Long studentId;
    private Long courseId;
    private Double score;
    private LocalDateTime createdAt;
    
    // 关联对象
    private Student student;
    private Course course;
} 