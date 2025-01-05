package com.education.dto;

import lombok.Data;

@Data
public class ClassroomDTO {
    private Long id;
    private String name;
    private Integer capacity;
    private String building; // 教学楼
    private String type;     // 教室类型(普通/多媒体/实验室)
    private String status;   // 使用状态
} 