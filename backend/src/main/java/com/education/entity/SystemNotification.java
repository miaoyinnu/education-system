package com.education.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SystemNotification {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String type;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
} 