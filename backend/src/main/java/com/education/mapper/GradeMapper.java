package com.education.mapper;

import com.education.entity.Grade;
import com.education.entity.StudentGrade;
import com.education.dto.StudentGradeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;
import java.util.Date;

@Mapper
public interface GradeMapper {
    // 学生相关
    List<Grade> findByStudentId(@Param("studentId") Long studentId, @Param("semester") String semester);
    
    // 教师相关
    List<StudentGradeDTO> findStudentsByCourseId(Long courseId);
    void updateGrade(@Param("studentId") Long studentId, 
                    @Param("courseId") Long courseId, 
                    @Param("score") Double score);
    void updateGradeWithExam(@Param("courseId") Long courseId,
                            @Param("studentId") Long studentId,
                            @Param("score") Double score,
                            @Param("updateTime") Date updateTime);
    void batchUpdateGrades(List<Grade> grades);
    
    // 基础操作
    void insert(Grade grade);
    void delete(Long id);
    
    List<StudentGrade> getCourseGrades(Long courseId);
    void publishGrades(Long courseId);

    // 统计分析相关
    @Select("SELECT " +
            "AVG(score) as avgScore, " +
            "COUNT(CASE WHEN score >= 60 THEN 1 END) * 100.0 / COUNT(*) as passRate " +
            "FROM grade " +
            "WHERE is_published = true")
    Map<String, Object> getOverallStats();

    @Select("SELECT s.name as studentName, c.name as courseName, " +
            "g.score, gas.threshold " +
            "FROM grade g " +
            "JOIN student s ON g.student_id = s.id " +
            "JOIN course c ON g.course_id = c.id " +
            "JOIN grade_alert_settings gas ON g.course_id = gas.course_id " +
            "WHERE g.score < gas.threshold " +
            "AND g.is_published = true " +
            "ORDER BY g.score ASC " +
            "LIMIT 10")
    List<Map<String, Object>> getRecentAlerts();

    List<Map<String, Object>> getCourseAverageGrades();
    Map<String, Integer> getGradeDistribution();
    Map<String, Double> getCoursePassRates();
    List<Map<String, Object>> findGradeAlerts();
} 