package com.education.entity;

import lombok.Data;

@Data
public class Course {
    private Long id;
    private String name;
    private String semester;
    private String courseTime;  // 课程时间
    private Long classroomId;  // 教室ID
    private String classroomName;  // 教室名称
    private Long teacherId;  // 教师ID
    private String teacherName;  // 教师姓名
    private Integer maxStudents;  // 最大学生数
    private Integer currentStudents;  // 当前学生数
    private Double credit;  // 学分
} 