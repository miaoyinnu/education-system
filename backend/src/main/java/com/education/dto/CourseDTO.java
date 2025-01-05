package com.education.dto;

import lombok.Data;

@Data
public class CourseDTO {
    private Long id;
    private String name;
    private String teacher;
    private String time;
    private String location;
    private String semester;
    private Integer capacity;
    private Boolean selected;
    private Long classroomId;
    private Integer maxStudents;
} 