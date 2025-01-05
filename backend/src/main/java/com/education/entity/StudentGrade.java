package com.education.entity;

import lombok.Data;
import java.util.Date;

@Data
public class StudentGrade {
    private Long studentId;
    private String studentName;
    private String className;
    private Double score;
    private Date updateTime;
} 