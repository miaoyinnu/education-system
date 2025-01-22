package com.education.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BackupRecord {
    private Long id;
    private LocalDateTime backupTime;
    private String fileName;
    private Long fileSize;
    private String status;  // success/failed
    private String filePath;
    private String description;
} 