package com.education.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TeacherDTO {
    private Long id;
    private String name;
    private String title;      // 职称
    private String department; // 所属院系
    private String email;
    private String phone;
    private String status;     // 在职状态
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 