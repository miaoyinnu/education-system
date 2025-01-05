package com.education.controller;

import com.education.dto.CourseDTO;
import com.education.dto.GradeDTO;
import com.education.dto.StudentStatsDTO;
import com.education.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/student")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class StudentController {
    
    @Autowired
    private StudentService studentService;
    
    @GetMapping("/semesters")
    public ResponseEntity<List<String>> getSemesters() {
        return ResponseEntity.ok(studentService.getSemesters());
    }
    
    @GetMapping("/grades")
    public ResponseEntity<List<GradeDTO>> getGrades(@RequestParam(required = false) String semester) {
        return ResponseEntity.ok(studentService.getGrades(semester));
    }
    
    @GetMapping("/courses")
    public ResponseEntity<List<CourseDTO>> getCourses() {
        return ResponseEntity.ok(studentService.getCourses());
    }
    
    @GetMapping("/available-courses")
    public ResponseEntity<List<CourseDTO>> getAvailableCourses(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(studentService.getAvailableCourses(search));
    }
    
    @PostMapping("/select-course")
    public ResponseEntity<?> selectCourse(@RequestBody Map<String, Long> request) {
        Long courseId = request.get("courseId");
        studentService.selectCourse(courseId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/stats")
    public ResponseEntity<StudentStatsDTO> getStats() {
        StudentStatsDTO stats = studentService.getStats();
        return ResponseEntity.ok(stats);
    }
} 