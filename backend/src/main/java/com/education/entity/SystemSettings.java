package com.education.entity;

import lombok.Data;

@Data
public class SystemSettings {
    private Long id;
    private String key;
    private String value;
    private String description;
} 