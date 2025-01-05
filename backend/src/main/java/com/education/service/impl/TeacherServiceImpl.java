package com.education.service.impl;

import com.education.mapper.GradeMapper;
import com.education.mapper.TeacherMapper;
import com.education.entity.Course;
import com.education.entity.Grade;
import com.education.entity.StudentGrade;
import com.education.service.TeacherService;
import com.education.util.UserContext;
import com.education.vo.TeacherStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private GradeMapper gradeMapper;

    @Override
    public TeacherStatsVO getTeacherStats() {
        Long userId = UserContext.getCurrentUserId();
        TeacherStatsVO stats = new TeacherStatsVO();

        // 获取课程总数
        Integer totalCourses = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM course c " +
            "JOIN teacher t ON c.teacher_id = t.id " +
            "WHERE t.user_id = ?",
            Integer.class,
            userId
        );
        stats.setTotalCourses(totalCourses);

        // 获取学生总数
        Integer totalStudents = jdbcTemplate.queryForObject(
            "SELECT COUNT(DISTINCT s.id) FROM student s " +
            "JOIN course_selection cs ON s.id = cs.student_id " +
            "JOIN course c ON cs.course_id = c.id " +
            "JOIN teacher t ON c.teacher_id = t.id " +
            "WHERE t.user_id = ?",
            Integer.class,
            userId
        );
        stats.setTotalStudents(totalStudents);

        // 获取平均分数
        Double averageScore = jdbcTemplate.queryForObject(
            "SELECT AVG(g.score) FROM grade g " +
            "JOIN course c ON g.course_id = c.id " +
            "JOIN teacher t ON c.teacher_id = t.id " +
            "WHERE t.user_id = ? AND g.score IS NOT NULL",
            Double.class,
            userId
        );
        stats.setAverageScore(averageScore != null ? averageScore : 0.0);

        return stats;
    }

    @Override
    public List<Course> getTeacherCourses() {
        Long userId = UserContext.getCurrentUserId();
        return jdbcTemplate.query(
            "SELECT c.id, c.name, c.semester, c.course_time as courseTime, " +
            "cl.name as classroomName, c.max_students, c.current_students " +
            "FROM course c " +
            "JOIN teacher t ON c.teacher_id = t.id " +
            "LEFT JOIN classroom cl ON c.classroom_id = cl.id " +
            "WHERE t.user_id = ?",
            (rs, rowNum) -> {
                Course course = new Course();
                course.setId(rs.getLong("id"));
                course.setName(rs.getString("name"));
                course.setSemester(rs.getString("semester"));
                course.setCourseTime(rs.getString("courseTime"));
                course.setClassroomName(rs.getString("classroomName"));
                course.setCurrentStudents(rs.getInt("current_students"));
                course.setMaxStudents(rs.getInt("max_students"));
                return course;
            },
            userId
        );
    }

    @Override
    public List<Map<String, Object>> getCourseStudents(Long courseId) {
        validateTeacherPermission(courseId);
        return jdbcTemplate.queryForList(
            "SELECT s.id as student_id, s.name, g.score, g.update_time " +
            "FROM student s " +
            "JOIN course_selection cs ON s.id = cs.student_id " +
            "LEFT JOIN grade g ON s.id = g.student_id AND g.course_id = ? " +
            "WHERE cs.course_id = ?",
            courseId, courseId
        );
    }

    @Override
    @Transactional
    public void updateGrade(Long courseId, Grade grade) {
        validateTeacherPermission(courseId);
        validateGrade(grade);
        
        jdbcTemplate.update(
            "INSERT INTO grade (student_id, course_id, score, update_time) " +
            "VALUES (?, ?, ?, NOW()) " +
            "ON DUPLICATE KEY UPDATE score = ?, update_time = NOW()",
            grade.getStudentId(), courseId, grade.getScore(),
            grade.getScore()
        );
    }

    @Override
    @Transactional
    public void importGrades(Long courseId, MultipartFile file) {
        validateTeacherPermission(courseId);
        // TODO: 实现Excel导入功能
    }

    @Override
    @Transactional
    public void publishGrades(Long courseId) {
        validateTeacherPermission(courseId);
        jdbcTemplate.update(
            "UPDATE grade SET is_published = TRUE WHERE course_id = ?",
            courseId
        );
    }

    @Override
    public List<Course> getCourses() {
        Long teacherId = UserContext.getCurrentUserId();
        return teacherMapper.getTeacherCourses(teacherId);
    }

    @Override
    public List<StudentGrade> getCourseGrades(Long courseId) {
        return gradeMapper.getCourseGrades(courseId);
    }

    @Override
    @Transactional
    public void saveGrades(Long courseId, List<StudentGrade> grades) {
        validateTeacherPermission(courseId);
        
        for (StudentGrade grade : grades) {
            // 检查是否存在记录
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM grade WHERE course_id = ? AND student_id = ?",
                Integer.class,
                courseId, grade.getStudentId()
            );
            
            if (count != null && count > 0) {
                // 如果存在记录，则更新
                jdbcTemplate.update(
                    "UPDATE grade SET score = ?, created_at = NOW() " +
                    "WHERE course_id = ? AND student_id = ?",
                    grade.getScore(), courseId, grade.getStudentId()
                );
            } else {
                // 如果不存在记录，则插入
                jdbcTemplate.update(
                    "INSERT INTO grade (course_id, student_id, score, created_at) " +
                    "VALUES (?, ?, ?, NOW())",
                    courseId, grade.getStudentId(), grade.getScore()
                );
            }
        }
    }

    private void validateTeacherPermission(Long courseId) {
        Long teacherId = UserContext.getCurrentUserId();
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM course c " +
            "JOIN teacher t ON c.teacher_id = t.id " +
            "WHERE c.id = ? AND t.user_id = ?",
            Integer.class,
            courseId, teacherId
        );
        if (count == null || count == 0) {
            throw new RuntimeException("您不是该课程的教师");
        }
    }

    private void validateGrade(Grade grade) {
        if (grade.getScore() < 0 || grade.getScore() > 100) {
            throw new RuntimeException("成绩必须在0-100之间");
        }
    }
} 