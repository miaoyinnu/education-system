package com.education.controller;

import com.education.common.Result;
import com.education.entity.User;
import com.education.service.AdminService;
import com.education.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;

    // 课程管理
    @GetMapping("/courses")
    public Result<Map<String, Object>> getCourses(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search
    ) {
        return Result.success(adminService.getCourses(page, size, search));
    }

    @PostMapping("/courses")
    public Result<CourseDTO> addCourse(@RequestBody CourseDTO courseDTO) {
        return Result.success(adminService.addCourse(courseDTO));
    }

    @PutMapping("/courses/{id}")
    public Result<CourseDTO> updateCourse(@PathVariable Long id, @RequestBody CourseDTO courseDTO) {
        courseDTO.setId(id);
        return Result.success(adminService.updateCourse(courseDTO));
    }

    @DeleteMapping("/courses/{id}")
    public Result<Void> deleteCourse(@PathVariable Long id) {
        adminService.deleteCourse(id);
        return Result.success();
    }

    @PostMapping("/courses/{id}/cancel-schedule")
    public Result<Void> cancelSchedule(@PathVariable Long id) {
        adminService.cancelSchedule(id);
        return Result.success();
    }

    @PostMapping("/courses/schedule")
    public Result<Void> scheduleCourse(@RequestBody CourseDTO courseDTO) {
        adminService.scheduleCourse(courseDTO);
        return Result.success();
    }

    // 教师管理
    @GetMapping("/teachers")
    public Result<List<TeacherDTO>> getTeachers() {
        return Result.success(adminService.getTeachers());
    }

    @PostMapping("/teachers")
    public Result<TeacherDTO> addTeacher(@RequestBody TeacherDTO teacherDTO) {
        return Result.success(adminService.addTeacher(teacherDTO));
    }

    @PutMapping("/teachers/{id}")
    public Result<TeacherDTO> updateTeacher(@PathVariable Long id, @RequestBody TeacherDTO teacherDTO) {
        teacherDTO.setId(id);
        return Result.success(adminService.updateTeacher(teacherDTO));
    }

    @DeleteMapping("/teachers/{id}")
    public Result<Void> deleteTeacher(@PathVariable Long id) {
        adminService.deleteTeacher(id);
        return Result.success();
    }

    // 教室管理
    @GetMapping("/classrooms")
    public Result<List<ClassroomDTO>> getClassrooms() {
        return Result.success(adminService.getClassrooms());
    }

    @PostMapping("/classrooms")
    public Result<ClassroomDTO> addClassroom(@RequestBody ClassroomDTO classroomDTO) {
        return Result.success(adminService.addClassroom(classroomDTO));
    }

    @PutMapping("/classrooms/{id}")
    public Result<ClassroomDTO> updateClassroom(@PathVariable Long id, @RequestBody ClassroomDTO classroomDTO) {
        classroomDTO.setId(id);
        return Result.success(adminService.updateClassroom(classroomDTO));
    }

    @DeleteMapping("/classrooms/{id}")
    public Result<Void> deleteClassroom(@PathVariable Long id) {
        adminService.deleteClassroom(id);
        return Result.success();
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
    @GetMapping("/grade-alerts")
    public Result<List<GradeAlertDTO>> getGradeAlerts() {
        return Result.success(adminService.getGradeAlerts());
    }

    @PostMapping("/grade-alerts/notify")
    public Result<Void> sendGradeAlert(@RequestBody Map<String, Long> params) {
        adminService.sendGradeAlert(params.get("studentId"), params.get("courseId"));
        return Result.success();
    }

    // 仪表盘相关
    @GetMapping("/todo-list")
    public Result<List<Map<String, Object>>> getTodoList() {
        return Result.success(adminService.getTodoList());
    }

    @GetMapping("/system-status")
    public Result<Map<String, Object>> getSystemStatus() {
        return Result.success(adminService.getSystemStatus());
    }

    // 用户管理
    @GetMapping("/users")
    public Result<Map<String, Object>> getUsers(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search
    ) {
        return Result.success(adminService.getUsers(page, size, search));
    }

    @PostMapping("/users")
    public Result<User> addUser(@RequestBody User user) {
        return Result.success(adminService.addUser(user));
    }

    @PutMapping("/users/{id}")
    public Result<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return Result.success(adminService.updateUser(user));
    }

    @DeleteMapping("/users/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return Result.success();
    }

    // 排课相关
    @GetMapping("/courses/scheduled")
    public Result<List<CourseDTO>> getScheduledCourses() {
        return Result.success(adminService.getScheduledCourses());
    }

    @GetMapping("/courses/unscheduled")
    public Result<List<CourseDTO>> getUnscheduledCourses() {
        return Result.success(adminService.getUnscheduledCourses());
    }
} 