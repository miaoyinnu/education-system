package com.education.service;

import com.education.entity.Course;
import com.education.entity.Grade;
import com.education.entity.StudentGrade;
import com.education.vo.TeacherStatsVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface TeacherService {
    
    TeacherStatsVO getTeacherStats();
    
    List<Course> getTeacherCourses();
    
    List<Map<String, Object>> getCourseStudents(Long courseId);
    
    void updateGrade(Long courseId, Grade grade);
    
    void importGrades(Long courseId, MultipartFile file);
    
    void publishGrades(Long courseId);
    
    List<Course> getCourses();
    
    List<StudentGrade> getCourseGrades(Long courseId);
    
    void saveGrades(Long courseId, List<StudentGrade> grades);
} 