package com.education.dto;

import lombok.Data;

@Data
public class ClassroomDTO {
    private Long id;
    private String name;
    private Integer capacity;
    private String status;  // 可用、维修中、已预约
    private String building;
    private String floor;
    private String type;    // 普通教室、实验室、多媒体教室等
    private String equipment;  // 教室配备的设备
    private String remark;
} 