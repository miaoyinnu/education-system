package com.education.service;

import com.education.dto.CourseAnalysisDTO;
import com.education.dto.GradeAlertDTO;
import com.education.dto.StudentAnalysisDTO;
import com.education.exception.BusinessException;
import com.education.mapper.CourseMapper;
import com.education.mapper.GradeMapper;
import com.education.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class AdminAnalysisService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private GradeMapper gradeMapper;
    
    @Autowired
    private StudentMapper studentMapper;

    // 获取课程成绩统计
    public List<CourseAnalysisDTO> getCourseAnalysis(String semester) {
        String sql = "SELECT c.id, c.name, " +
            "COUNT(g.id) as total_students, " +
            "AVG(g.score) as avg_score, " +
            "MIN(g.score) as min_score, " +
            "MAX(g.score) as max_score, " +
            "COUNT(CASE WHEN g.score >= 90 THEN 1 END) as a_count, " +
            "COUNT(CASE WHEN g.score >= 80 AND g.score < 90 THEN 1 END) as b_count, " +
            "COUNT(CASE WHEN g.score >= 70 AND g.score < 80 THEN 1 END) as c_count, " +
            "COUNT(CASE WHEN g.score >= 60 AND g.score < 70 THEN 1 END) as d_count, " +
            "COUNT(CASE WHEN g.score < 60 THEN 1 END) as f_count " +
            "FROM course c " +
            "LEFT JOIN grade g ON c.id = g.course_id " +
            (semester != null ? "WHERE c.semester = ? " : "") +
            "GROUP BY c.id, c.name";

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> {
                CourseAnalysisDTO analysis = new CourseAnalysisDTO();
                analysis.setCourseId(rs.getLong("id"));
                analysis.setCourseName(rs.getString("name"));
                analysis.setTotalStudents(rs.getInt("total_students"));
                analysis.setAverageScore(rs.getDouble("avg_score"));
                analysis.setMinScore(rs.getDouble("min_score"));
                analysis.setMaxScore(rs.getDouble("max_score"));
                Map<String, Integer> gradeDistribution = new HashMap<>();
                gradeDistribution.put("A", rs.getInt("a_count"));
                gradeDistribution.put("B", rs.getInt("b_count"));
                gradeDistribution.put("C", rs.getInt("c_count"));
                gradeDistribution.put("D", rs.getInt("d_count"));
                gradeDistribution.put("F", rs.getInt("f_count"));
                analysis.setGradeDistribution(gradeDistribution);
                return analysis;
            },
            semester != null ? new Object[]{semester} : new Object[]{}
        );
    }

    // 获取学生成绩统计
    public List<StudentAnalysisDTO> getStudentAnalysis() {
        return jdbcTemplate.query(
            "SELECT s.id, s.name, " +
            "COUNT(g.id) as total_courses, " +
            "AVG(g.score) as avg_score, " +
            "SUM(c.credit) as total_credits " +
            "FROM student s " +
            "LEFT JOIN grade g ON s.id = g.student_id " +
            "LEFT JOIN course c ON g.course_id = c.id " +
            "GROUP BY s.id, s.name",
            (rs, rowNum) -> {
                StudentAnalysisDTO analysis = new StudentAnalysisDTO();
                analysis.setStudentId(String.valueOf(rs.getLong("id")));
                analysis.setStudentName(rs.getString("name"));
                analysis.setTotalCourses(rs.getInt("total_courses"));
                analysis.setAverageScore(rs.getDouble("avg_score"));
                analysis.setTotalCredits(rs.getDouble("total_credits"));
                return analysis;
            }
        );
    }

    // 获取成绩预警信息
    public List<GradeAlertDTO> getGradeAlerts(double threshold) {
        if (threshold < 0 || threshold > 100) {
            throw new BusinessException("阈值必须在0-100之间");
        }

        return jdbcTemplate.query(
            "SELECT s.id as student_id, s.name as student_name, " +
            "c.id as course_id, c.name as course_name, " +
            "t.name as teacher_name, " +
            "g.score " +
            "FROM grade g " +
            "JOIN student s ON g.student_id = s.id " +
            "JOIN course c ON g.course_id = c.id " +
            "JOIN teacher t ON c.teacher_id = t.id " +
            "WHERE g.score < ? " +
            "ORDER BY g.score ASC",
            (rs, rowNum) -> {
                GradeAlertDTO alert = new GradeAlertDTO();
                alert.setStudentId(rs.getLong("student_id"));
                alert.setStudentName(rs.getString("student_name"));
                alert.setCourseId(rs.getLong("course_id"));
                alert.setCourseName(rs.getString("course_name"));
                alert.setTeacherName(rs.getString("teacher_name"));
                alert.setScore((int) rs.getDouble("score"));
                return alert;
            },
            threshold
        );
    }

    // 获取教室使用情况统计
    public List<Map<String, Object>> getClassroomUsage(String semester) {
        String sql = "SELECT cr.id, cr.name, " +
            "COUNT(c.id) as total_courses, " +
            "SUM(c.current_students) as total_students, " +
            "cr.capacity, " +
            "ROUND(AVG(c.current_students * 100.0 / cr.capacity), 2) as usage_rate " +
            "FROM classroom cr " +
            "LEFT JOIN course c ON cr.id = c.classroom_id " +
            (semester != null ? "AND c.semester = ? " : "") +
            "GROUP BY cr.id, cr.name, cr.capacity";

        return jdbcTemplate.queryForList(
            sql,
            semester != null ? new Object[]{semester} : new Object[]{}
        );
    }

    private List<GradeAlertDTO> convertToGradeAlertDTOs(List<Map<String, Object>> alerts) {
        return alerts.stream().map(alert -> {
            GradeAlertDTO dto = new GradeAlertDTO();
            dto.setStudentId(Long.parseLong(alert.get("student_id").toString()));
            dto.setStudentName((String) alert.get("student_name"));
            dto.setCourseId(Long.parseLong(alert.get("course_id").toString()));
            dto.setCourseName((String) alert.get("course_name"));
            dto.setTeacherName((String) alert.get("teacher_name"));
            dto.setScore(((Number) alert.get("score")).intValue());
            return dto;
        }).collect(Collectors.toList());
    }
} 