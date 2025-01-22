package com.education.service;

import com.education.dto.CourseDTO;
import com.education.dto.GradeDTO;
import com.education.dto.GradeAlertDTO;
import com.education.dto.StudentStatsDTO;
import com.education.entity.Course;
import com.education.entity.Grade;
import com.education.exception.BusinessException;
import com.education.mapper.CourseMapper;
import com.education.mapper.GradeMapper;
import com.education.mapper.TeacherMapper;
import com.education.util.UserContext;
import com.education.util.UserIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private GradeMapper gradeMapper;
    
    @Autowired
    private TeacherMapper teacherMapper;
    
    public List<String> getSemesters() {
        return courseMapper.findAllSemesters();
    }
    
    public List<GradeDTO> getGrades(String semester) {
        String username = UserContext.getCurrentUsername();
        Long studentId = UserIdCache.getStudentIdByUsername(username);
        if (studentId == null) {
            throw new BusinessException("学生信息不存在");
        }
        
        return gradeMapper.findByStudentId(studentId, semester);
    }
    
    public List<CourseDTO> getCourses() {
        String username = UserContext.getCurrentUsername();
        Long studentId = UserIdCache.getStudentIdByUsername(username);
        if (studentId == null) {
            throw new BusinessException("学生信息不存在");
        }
        
        return courseMapper.findByStudentId(studentId);
    }
    
    public List<CourseDTO> getAvailableCourses(String search) {
        String username = UserContext.getCurrentUsername();
        Long studentId = UserIdCache.getStudentIdByUsername(username);
        if (studentId == null) {
            throw new BusinessException("学生信息不存在");
        }
        
        return courseMapper.findAvailableCourses(studentId, search);
    }
    
    @Transactional
    public void selectCourse(Long courseId) {
        String username = UserContext.getCurrentUsername();
        Long studentId = UserIdCache.getStudentIdByUsername(username);
        if (studentId == null) {
            throw new BusinessException("学生信息不存在");
        }
        
        CourseDTO course = courseMapper.findCourseById(courseId);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        
        int currentStudents = course.getCurrentStudents() != null ? course.getCurrentStudents() : 0;
        if (currentStudents >= course.getMaxStudents()) {
            throw new BusinessException("课程已满");
        }
        
        if (courseMapper.isStudentSelected(studentId, courseId)) {
            throw new BusinessException("已经选择过该课程");
        }
        
        courseMapper.insertCourseSelection(studentId, courseId);
        courseMapper.incrementCurrentStudents(courseId);
    }

    public StudentStatsDTO getStats() {
        String username = UserContext.getCurrentUsername();
        Long studentId = UserIdCache.getStudentIdByUsername(username);
        if (studentId == null) {
            throw new BusinessException("学生信息不存在");
        }

        StudentStatsDTO stats = new StudentStatsDTO();
        
        // 获取总课程数
        List<CourseDTO> courses = courseMapper.findByStudentId(studentId);
        stats.setTotalCourses(courses.size());
        
        // 获取成绩信息
        List<GradeDTO> grades = gradeMapper.findByStudentId(studentId, null);
        
        // 计算总学分
        double totalCredits = grades.stream()
            .filter(grade -> grade.getScore() != null)  // 只计算有成绩的课程的学分
            .map(GradeDTO::getCredit)
            .filter(credit -> credit != null)
            .mapToDouble(Integer::doubleValue)
            .sum();
        stats.setTotalCredits((int) totalCredits);
        
        // 计算加权平均成绩
        double weightedScoreSum = grades.stream()
            .filter(grade -> grade.getScore() != null && grade.getCredit() != null)
            .mapToDouble(grade -> grade.getScore() * grade.getCredit())
            .sum();
            
        double averageScore = totalCredits > 0 ? weightedScoreSum / totalCredits : 0.0;
        stats.setAverageScore(averageScore);
        
        return stats;
    }

    public List<GradeAlertDTO> getGradeAlerts() {
        String username = UserContext.getCurrentUsername();
        Long studentId = UserIdCache.getStudentIdByUsername(username);
        if (studentId == null) {
            throw new BusinessException("学生信息不存在");
        }
        
        return gradeMapper.findGradeAlertsByStudentId(studentId);
    }
} 