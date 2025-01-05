package com.education.mapper;

import com.education.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface StudentMapper {
    @Select("SELECT * FROM student WHERE id = #{id}")
    Student findById(Long id);

    @Select("SELECT * FROM student")
    List<Student> findAll();

    @Select("SELECT COUNT(*) FROM student")
    int countTotal();

    @Select("SELECT AVG(score) as avgScore, COUNT(*) as total FROM grade WHERE student_id = #{studentId}")
    Map<String, Object> getStudentScoreStats(Long studentId);

    @Select("SELECT course_id, score FROM grade WHERE student_id = #{studentId}")
    List<Map<String, Object>> getStudentScores(Long studentId);

    @Select("SELECT COUNT(*) FROM grade WHERE student_id = #{studentId} AND score >= 60")
    int countPassedCourses(Long studentId);
} 