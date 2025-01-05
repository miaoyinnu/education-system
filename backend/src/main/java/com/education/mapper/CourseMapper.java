package com.education.mapper;

import com.education.entity.Course;
import com.education.dto.CourseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface CourseMapper {
    // 基础CRUD
    List<Course> findAll();
    Course findById(Long id);
    void insert(CourseDTO course);
    void update(CourseDTO course);
    void delete(Long id);
    
    // 学生相关
    List<String> findAllSemesters();
    List<Course> findByStudentId(Long studentId);
    List<Course> findAvailableCourses(@Param("studentId") Long studentId, @Param("search") String search);
    boolean isStudentSelected(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
    void insertCourseSelection(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
    void incrementCurrentStudents(Long courseId);
    
    // 教师相关
    List<Course> findByTeacherId(Long teacherId);
    boolean isTeacherOfCourse(@Param("teacherId") Long teacherId, @Param("courseId") Long courseId);
    
    // 管理员相关
    boolean hasStudents(Long courseId);
    boolean hasTeacherTimeConflict(CourseDTO course);
    boolean hasClassroomTimeConflict(CourseDTO course);
    void updateSchedule(CourseDTO course);
    List<CourseDTO> findUnscheduledCourses();

    // 统计相关
    int countTotal();
    List<Map<String, Object>> getDailySelectionStats();
    List<Map<String, Object>> getWeeklySelectionStats();
    List<Map<String, Object>> getMonthlySelectionStats();
} 