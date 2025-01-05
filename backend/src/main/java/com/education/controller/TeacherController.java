package com.education.controller;

import com.education.entity.Course;
import com.education.entity.Grade;
import com.education.entity.StudentGrade;
import com.education.service.TeacherService;
import com.education.vo.TeacherStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @GetMapping("/semesters")
    public ResponseEntity<List<String>> getSemesters() {
        return ResponseEntity.ok(teacherService.getSemesters());
    }

    @GetMapping("/timetable")
    public ResponseEntity<List<Course>> getTimetable(@RequestParam(required = false) String semester) {
        return ResponseEntity.ok(teacherService.getTeacherCourses(semester));
    }

    @GetMapping("/stats")
    public ResponseEntity<TeacherStatsVO> getStats() {
        return ResponseEntity.ok(teacherService.getTeacherStats());
    }

    @GetMapping("/courses")
    public List<Course> getCourses() {
        return teacherService.getCourses();
    }

    @GetMapping("/courses/{courseId}/students")
    public ResponseEntity<List<Map<String, Object>>> getCourseStudents(@PathVariable Long courseId) {
        return ResponseEntity.ok(teacherService.getCourseStudents(courseId));
    }

    @PutMapping("/courses/{courseId}/grades")
    public ResponseEntity<Void> updateGrade(
            @PathVariable Long courseId,
            @RequestBody Grade grade
    ) {
        teacherService.updateGrade(courseId, grade);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/courses/{courseId}/grades/import")
    public ResponseEntity<Void> importGrades(
            @PathVariable Long courseId,
            @RequestParam("file") MultipartFile file
    ) {
        teacherService.importGrades(courseId, file);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/course/{courseId}/grades/publish")
    public ResponseEntity<Void> publishGrades(@PathVariable Long courseId) {
        teacherService.publishGrades(courseId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/course/{courseId}/grades")
    public List<StudentGrade> getCourseGrades(@PathVariable Long courseId) {
        return teacherService.getCourseGrades(courseId);
    }

    @PostMapping("/course/{courseId}/grades")
    public void saveGrades(@PathVariable Long courseId, @RequestBody List<StudentGrade> grades) {
        teacherService.saveGrades(courseId, grades);
    }
} 