package com.education.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SystemSettings {
    private Long id;
    private String settingKey;
    private String settingValue;
    private String description;
    private LocalDateTime updatedAt;
} 