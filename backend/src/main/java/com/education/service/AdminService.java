package com.education.service;

import com.education.dto.*;
import com.education.entity.User;

import java.util.List;
import java.util.Map;

public interface AdminService {
    // 课程管理
    Map<String, Object> getCourses(int page, int size, String search);
    CourseDTO addCourse(CourseDTO courseDTO);
    CourseDTO updateCourse(CourseDTO courseDTO);
    void deleteCourse(Long id);

    // 教师管理
    List<TeacherDTO> getTeachers();
    TeacherDTO addTeacher(TeacherDTO teacherDTO);
    TeacherDTO updateTeacher(TeacherDTO teacherDTO);
    void deleteTeacher(Long id);

    // 教室管理
    List<ClassroomDTO> getClassrooms();
    ClassroomDTO addClassroom(ClassroomDTO classroomDTO);
    ClassroomDTO updateClassroom(ClassroomDTO classroomDTO);
    void deleteClassroom(Long id);

    // 自动排课
    List<CourseDTO> autoScheduleCourses();

    // 系统设置
    SystemSettingsDTO updateSettings(SystemSettingsDTO settingsDTO);
    List<SystemSettingsDTO> getSettings();

    // 统计分析
    Map<String, Object> getGradeStatistics();
    Map<String, Object> getClassroomUsageStatistics();

    // 成绩预警
    void setGradeAlertThreshold(GradeAlertSettingDTO settingDTO);
    GradeAlertSettingDTO getGradeAlertThreshold();
    List<GradeAlertDTO> getGradeAlerts();
    void sendGradeAlert(Long studentId, Long courseId);

    // 仪表盘相关
    List<Map<String, Object>> getTodoList();
    Map<String, Object> getSystemStatus();

    // 用户管理
    Map<String, Object> getUsers(int page, int size, String search);
    User addUser(User user);
    User updateUser(User user);
    void deleteUser(Long id);

    // 排课相关
    List<CourseDTO> getScheduledCourses();
    List<CourseDTO> getUnscheduledCourses();
} 