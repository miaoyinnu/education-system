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
    List<CourseDTO> findAll();
    Course findById(Long id);
    CourseDTO findCourseById(Long id);
    void insert(CourseDTO course);
    void update(CourseDTO course);
    void delete(Long id);
    
    // 学生相关
    List<String> findAllSemesters();
    List<CourseDTO> findByStudentId(Long studentId);
    List<CourseDTO> findAvailableCourses(@Param("studentId") Long studentId, @Param("search") String search);
    boolean isStudentSelected(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
    void insertCourseSelection(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
    void incrementCurrentStudents(Long courseId);
    
    // 教师相关
    List<CourseDTO> findByTeacherId(Long teacherId);
    boolean isTeacherOfCourse(@Param("teacherId") Long teacherId, @Param("courseId") Long courseId);
    
    // 管理员相关
    boolean hasStudents(Long courseId);
    boolean hasTeacherTimeConflict(CourseDTO course);
    boolean hasClassroomTimeConflict(CourseDTO course);
    void updateSchedule(CourseDTO course);
    List<CourseDTO> findUnscheduledCourses();
    List<CourseDTO> findScheduledCourses();
    boolean isTimeSlotOccupied(@Param("timeSlotId") Long timeSlotId, @Param("classroomId") Long classroomId, @Param("courseId") Long courseId);

    // 统计相关
    int countTotal();
    int countUnscheduledCourses();
    List<Map<String, Object>> getDailySelectionStats();
    List<Map<String, Object>> getWeeklySelectionStats();
    List<Map<String, Object>> getMonthlySelectionStats();

    List<CourseDTO> findByPage(@Param("offset") int offset, @Param("size") int size, @Param("search") String search);
    int count(@Param("search") String search);

    // 检查教室是否有课程
    boolean hasClassroomCourses(Long classroomId);
} 