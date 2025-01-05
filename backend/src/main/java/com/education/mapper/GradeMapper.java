package com.education.mapper;

import com.education.entity.Grade;
import com.education.entity.StudentGrade;
import com.education.dto.StudentGradeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
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
} 