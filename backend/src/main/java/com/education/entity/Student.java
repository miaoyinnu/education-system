package com.education.entity;

import lombok.Data;

@Data
public class Student {
    private Long id;
    private String name;
    private Long userId;
    private Long classId;
    
    // 关联对象
    private User user;
} 