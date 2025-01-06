package com.education.dto;

import lombok.Data;

@Data
public class CourseDTO {
    private Long id;
    private String name;
    private String teacher;
    private Long teacherId;
    private String teacherName;
    private String time;
    private String location;
    private String semester;
    private Integer capacity;
    private Boolean selected;
    private Long classroomId;
    private String classroomName;
    private Integer maxStudents;
    private Integer currentStudents;
    private Double credit;
    private Long timeSlotId;
} 