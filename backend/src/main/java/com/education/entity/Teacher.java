package com.education.entity;

import lombok.Data;

@Data
public class Teacher {
    private Long id;
    private String name;
    private Long userId;
    
    // 关联对象
    private User user;
} 