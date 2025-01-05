package com.education.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Backup {
    private Long id;
    private String fileName;
    private Long fileSize;
    private String description;
    private LocalDateTime createdAt;
} 