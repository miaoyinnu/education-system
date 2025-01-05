package com.education.controller;

import com.education.common.Result;
import com.education.service.AdminService;
import com.education.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;

    // 课程管理
    @PostMapping("/course")
    public Result<CourseDTO> addCourse(@RequestBody CourseDTO courseDTO) {
        return Result.success(adminService.addCourse(courseDTO));
    }

    @PutMapping("/course/{id}")
    public Result<CourseDTO> updateCourse(@PathVariable Long id, @RequestBody CourseDTO courseDTO) {
        courseDTO.setId(id);
        return Result.success(adminService.updateCourse(courseDTO));
    }

    @DeleteMapping("/course/{id}")
    public Result<Void> deleteCourse(@PathVariable Long id) {
        adminService.deleteCourse(id);
        return Result.success();
    }

    // 教师管理
    @PostMapping("/teacher")
    public Result<TeacherDTO> addTeacher(@RequestBody TeacherDTO teacherDTO) {
        return Result.success(adminService.addTeacher(teacherDTO));
    }

    @PutMapping("/teacher/{id}")
    public Result<TeacherDTO> updateTeacher(@PathVariable Long id, @RequestBody TeacherDTO teacherDTO) {
        teacherDTO.setId(id);
        return Result.success(adminService.updateTeacher(teacherDTO));
    }

    // 教室管理
    @PostMapping("/classroom")
    public Result<ClassroomDTO> addClassroom(@RequestBody ClassroomDTO classroomDTO) {
        return Result.success(adminService.addClassroom(classroomDTO));
    }

    @PutMapping("/classroom/{id}")
    public Result<ClassroomDTO> updateClassroom(@PathVariable Long id, @RequestBody ClassroomDTO classroomDTO) {
        classroomDTO.setId(id);
        return Result.success(adminService.updateClassroom(classroomDTO));
    }

    // 自动排课
    @PostMapping("/schedule/auto")
    public Result<List<CourseDTO>> autoScheduleCourses() {
        return Result.success(adminService.autoScheduleCourses());
    }

    // 系统设置
    @PutMapping("/settings")
    public Result<SystemSettingsDTO> updateSettings(@RequestBody SystemSettingsDTO settingsDTO) {
        return Result.success(adminService.updateSettings(settingsDTO));
    }

    @GetMapping("/settings")
    public Result<List<SystemSettingsDTO>> getSettings() {
        return Result.success(adminService.getSettings());
    }

    // 统计分析
    @GetMapping("/statistics/grade")
    public Result<Map<String, Object>> getGradeStatistics() {
        return Result.success(adminService.getGradeStatistics());
    }

    @GetMapping("/statistics/classroom")
    public Result<Map<String, Object>> getClassroomUsageStatistics() {
        return Result.success(adminService.getClassroomUsageStatistics());
    }

    // 成绩预警
    @PutMapping("/grade-alert")
    public Result<Void> setGradeAlertThreshold(@RequestBody GradeAlertSettingDTO settingDTO) {
        adminService.setGradeAlertThreshold(settingDTO);
        return Result.success();
    }

    @GetMapping("/grade-alert")
    public Result<List<Map<String, Object>>> getGradeAlerts() {
        return Result.success(adminService.getGradeAlerts());
    }
} 