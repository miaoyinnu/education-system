package com.education.dto;

import lombok.Data;
import java.util.Date;

@Data
public class SystemSettingsDTO {
    private String settingKey;
    private String settingValue;
    private String description;
    private Date createdAt;
    private Date updatedAt;
} 