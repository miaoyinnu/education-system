package com.education.service;

import com.education.dto.CourseDTO;
import com.education.dto.GradeDTO;
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
        
        List<Grade> grades = gradeMapper.findByStudentId(studentId, semester);
        return grades.stream().map(grade -> {
            GradeDTO dto = new GradeDTO();
            dto.setId(grade.getId());
            dto.setCourseName(grade.getCourse().getName());
            dto.setTeacherName(teacherMapper.findNameById(grade.getCourse().getTeacherId()));
            dto.setScore(grade.getScore());
            dto.setSemester(grade.getCourse().getSemester());
            Double credit = grade.getCourse().getCredit();
            dto.setCredit(credit != null ? credit.intValue() : null);
            return dto;
        }).collect(Collectors.toList());
    }
    
    public List<CourseDTO> getCourses() {
        String username = UserContext.getCurrentUsername();
        Long studentId = UserIdCache.getStudentIdByUsername(username);
        if (studentId == null) {
            throw new BusinessException("学生信息不存在");
        }
        
        List<Course> courses = courseMapper.findByStudentId(studentId);
        
        return courses.stream().map(course -> {
            CourseDTO dto = new CourseDTO();
            dto.setId(course.getId());
            dto.setName(course.getName());
            dto.setTeacher(course.getTeacherId() != null ? 
                teacherMapper.findNameById(course.getTeacherId()) : null);
            dto.setTime(course.getCourseTime());
            dto.setLocation(course.getClassroomName());
            dto.setSemester(course.getSemester());
            return dto;
        }).collect(Collectors.toList());
    }
    
    public List<CourseDTO> getAvailableCourses(String search) {
        String username = UserContext.getCurrentUsername();
        Long studentId = UserIdCache.getStudentIdByUsername(username);
        List<Course> courses = courseMapper.findAvailableCourses(studentId, search);
        
        return courses.stream().map(course -> {
            CourseDTO dto = new CourseDTO();
            dto.setId(course.getId());
            dto.setName(course.getName());
            dto.setTeacher(teacherMapper.findNameById(course.getTeacherId()));
            dto.setTime(course.getCourseTime());
            dto.setLocation(course.getClassroomName());
            dto.setSemester(course.getSemester());
            int currentStudents = course.getCurrentStudents() != null ? course.getCurrentStudents() : 0;
            dto.setCapacity(course.getMaxStudents() - currentStudents);
            dto.setSelected(false);
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Transactional
    public void selectCourse(Long courseId) {
        String username = UserContext.getCurrentUsername();
        Long studentId = UserIdCache.getStudentIdByUsername(username);
        if (studentId == null) {
            throw new BusinessException("学生信息不存在");
        }
        
        Course course = courseMapper.findById(courseId);
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
        List<Course> courses = courseMapper.findByStudentId(studentId);
        stats.setTotalCourses(courses.size());
        
        // 获取成绩信息
        List<Grade> grades = gradeMapper.findByStudentId(studentId, null);
        if (!grades.isEmpty()) {
            // 计算总学分
            double totalCredits = grades.stream()
                .map(grade -> grade.getCourse().getCredit())
                .filter(credit -> credit != null)
                .mapToDouble(Double::doubleValue)
                .sum();
            stats.setTotalCredits((int) totalCredits);
            
            // 计算平均成绩
            double averageScore = grades.stream()
                .filter(grade -> grade.getScore() != null)
                .mapToDouble(Grade::getScore)
                .average()
                .orElse(0.0);
            stats.setAverageScore(averageScore);
        }
        
        return stats;
    }
} 