package com.education.dto;

import lombok.Data;

@Data
public class GradeAlertSettingDTO {
    private Long courseId;
    private Double alertThreshold;
} 