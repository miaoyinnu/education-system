package com.education.entity;

import lombok.Data;

@Data
public class Classroom {
    private Long id;
    private String name;
    private Integer capacity;
    private String building;
    private String type;
    private String status;
} 