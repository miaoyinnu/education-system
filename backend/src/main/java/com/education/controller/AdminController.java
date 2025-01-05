package com.education.controller;

import com.education.dto.*;
import com.education.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    // 课程管理
    @PostMapping("/course")
    public void createCourse(@RequestBody CourseDTO course) {
        adminService.createCourse(course);
    }
    
    @PutMapping("/course/{id}")
    public void updateCourse(@PathVariable Long id, @RequestBody CourseDTO course) {
        course.setId(id);
        adminService.updateCourse(course);
    }
    
    @DeleteMapping("/course/{id}")
    public void deleteCourse(@PathVariable Long id) {
        adminService.deleteCourse(id);
    }
    
    @GetMapping("/courses")
    public List<CourseDTO> getAllCourses() {
        return adminService.getAllCourses();
    }
    
    // 教师管理
    @PostMapping("/teacher")
    public void createTeacher(@RequestBody TeacherDTO teacher) {
        adminService.createTeacher(teacher);
    }
    
    @PutMapping("/teacher/{id}")
    public void updateTeacher(@PathVariable Long id, @RequestBody TeacherDTO teacher) {
        teacher.setId(id);
        adminService.updateTeacher(teacher);
    }
    
    @DeleteMapping("/teacher/{id}")
    public void deleteTeacher(@PathVariable Long id) {
        adminService.deleteTeacher(id);
    }
    
    @GetMapping("/teachers")
    public List<TeacherDTO> getAllTeachers() {
        return adminService.getAllTeachers();
    }
    
    // 教室管理
    @PostMapping("/classroom")
    public void createClassroom(@RequestBody ClassroomDTO classroom) {
        adminService.createClassroom(classroom);
    }
    
    @PutMapping("/classroom/{id}")
    public void updateClassroom(@PathVariable Long id, @RequestBody ClassroomDTO classroom) {
        classroom.setId(id);
        adminService.updateClassroom(classroom);
    }
    
    @DeleteMapping("/classroom/{id}")
    public void deleteClassroom(@PathVariable Long id) {
        adminService.deleteClassroom(id);
    }
    
    @GetMapping("/classrooms")
    public List<ClassroomDTO> getAllClassrooms() {
        return adminService.getAllClassrooms();
    }
    
    // 排课管理
    @PostMapping("/schedule")
    public void scheduleClass(@RequestBody CourseDTO course) {
        adminService.scheduleClass(course);
    }
    
    @GetMapping("/schedule/conflicts")
    public List<String> checkScheduleConflicts(@RequestBody CourseDTO course) {
        return adminService.checkScheduleConflicts(course);
    }
} 