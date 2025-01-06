package com.education.service.impl;

import com.education.dto.*;
import com.education.entity.*;
import com.education.mapper.*;
import com.education.service.AdminService;
import com.education.algorithm.CourseScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private ClassroomMapper classroomMapper;
    @Autowired
    private SystemSettingsMapper settingsMapper;
    @Autowired
    private GradeMapper gradeMapper;
    @Autowired
    private GradeAlertSettingsMapper alertSettingsMapper;
    @Autowired
    private CourseScheduler courseScheduler;
    @Autowired
    private UserMapper userMapper;

    // 课程管理
    @Override
    public Map<String, Object> getCourses(int page, int size, String search) {
        Map<String, Object> result = new HashMap<>();
        // 计算偏移量
        int offset = (page - 1) * size;
        // 获取分页数据
        List<CourseDTO> courses = courseMapper.findByPage(offset, size, search);
        // 获取总数
        int total = courseMapper.count(search);
        result.put("data", courses);
        result.put("total", total);
        return result;
    }

    @Override
    @Transactional
    public CourseDTO addCourse(CourseDTO courseDTO) {
        validateCourse(courseDTO);
        courseMapper.insert(courseDTO);
        return courseDTO;
    }

    @Override
    @Transactional
    public CourseDTO updateCourse(CourseDTO courseDTO) {
        validateCourse(courseDTO);
        courseMapper.update(courseDTO);
        return courseDTO;
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        courseMapper.delete(id);
    }

    // 教师管理
    @Override
    public List<TeacherDTO> getTeachers() {
        return teacherMapper.findAll();
    }

    @Override
    @Transactional
    public TeacherDTO addTeacher(TeacherDTO teacherDTO) {
        validateTeacher(teacherDTO);
        
        // 检查用户是否存在
        User user = userMapper.findById(teacherDTO.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("用户ID不存在，请先创建用户");
        }
        
        // 转换DTO为实体
        Teacher teacher = new Teacher();
        teacher.setName(teacherDTO.getName());
        teacher.setUserId(teacherDTO.getUserId());
        
        teacherMapper.insert(teacher);
        // 设置返回的DTO的ID
        teacherDTO.setId(teacher.getId());
        return teacherDTO;
    }

    @Override
    @Transactional
    public TeacherDTO updateTeacher(TeacherDTO teacherDTO) {
        validateTeacher(teacherDTO);
        teacherMapper.update(teacherDTO);
        return teacherDTO;
    }

    @Override
    @Transactional
    public void deleteTeacher(Long id) {
        // 检查教师是否有正在进行的课程
        if (teacherMapper.hasOngoingCourses(id)) {
            throw new IllegalStateException("该教师有正在进行的课程，无法删除");
        }
        teacherMapper.delete(id);
    }

    // 教室管理
    @Override
    public List<ClassroomDTO> getClassrooms() {
        return classroomMapper.findAll();
    }

    @Override
    @Transactional
    public ClassroomDTO addClassroom(ClassroomDTO classroomDTO) {
        validateClassroom(classroomDTO);
        classroomMapper.insert(classroomDTO);
        return classroomDTO;
    }

    @Override
    @Transactional
    public ClassroomDTO updateClassroom(ClassroomDTO classroomDTO) {
        validateClassroom(classroomDTO);
        classroomMapper.update(classroomDTO);
        return classroomDTO;
    }

    @Override
    @Transactional
    public void deleteClassroom(Long id) {
        // 检查教室是否有正在进行的课程
        if (courseMapper.hasClassroomCourses(id)) {
            throw new IllegalStateException("该教室有正在进行的课程，无法删除");
        }
        classroomMapper.delete(id);
    }

    // 自动排课
    @Override
    @Transactional
    public List<CourseDTO> autoScheduleCourses() {
        List<CourseDTO> unscheduledCourses = courseMapper.findUnscheduledCourses();
        List<TeacherDTO> teachers = teacherMapper.findAll();
        List<ClassroomDTO> classrooms = classroomMapper.findAll();
        
        // 使用排课算法进行自动排课
        List<CourseDTO> scheduledCourses = courseScheduler.schedule(
            unscheduledCourses, teachers, classrooms);
        
        // 保存排课结果
        for (CourseDTO course : scheduledCourses) {
            courseMapper.updateSchedule(course);
        }
        
        return scheduledCourses;
    }

    // 系统设置
    @Override
    @Transactional
    public SystemSettingsDTO updateSettings(SystemSettingsDTO settingsDTO) {
        settingsMapper.updateSettings(settingsDTO);
        return settingsDTO;
    }

    @Override
    public List<SystemSettingsDTO> getSettings() {
        return settingsMapper.findAll();
    }

    // 统计分析
    @Override
    public Map<String, Object> getGradeStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取所有课程的平均分
        List<Map<String, Object>> courseAvgGrades = gradeMapper.getCourseAverageGrades();
        stats.put("courseAverages", courseAvgGrades);
        
        // 获取成绩分布
        Map<String, Integer> gradeDistribution = gradeMapper.getGradeDistribution();
        stats.put("gradeDistribution", gradeDistribution);
        
        // 获取及格率
        Map<String, Double> passRates = gradeMapper.getCoursePassRates();
        stats.put("passRates", passRates);
        
        return stats;
    }

    @Override
    public Map<String, Object> getClassroomUsageStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取教室使用率
        Map<String, Double> usageRates = classroomMapper.getUsageRates();
        stats.put("usageRates", usageRates);
        
        // 获取教室容量利用率
        Map<String, Double> capacityUtilization = classroomMapper.getCapacityUtilization();
        stats.put("capacityUtilization", capacityUtilization);
        
        return stats;
    }

    // 成绩预警
    @Override
    @Transactional
    public void setGradeAlertThreshold(GradeAlertSettingDTO settingDTO) {
        alertSettingsMapper.updateThreshold(settingDTO);
    }

    @Override
    public List<Map<String, Object>> getGradeAlerts() {
        return gradeMapper.findGradeAlerts();
    }

    // 仪表盘相关
    @Override
    public List<Map<String, Object>> getTodoList() {
        List<Map<String, Object>> todoList = new ArrayList<>();
        
        // 获取未排课的课程数量
        int unscheduledCourses = courseMapper.countUnscheduledCourses();
        if (unscheduledCourses > 0) {
            Map<String, Object> todo = new HashMap<>();
            todo.put("type", "course");
            todo.put("message", "有 " + unscheduledCourses + " 门课程待排课");
            todo.put("count", unscheduledCourses);
            todoList.add(todo);
        }

        // 获取成绩预警数量
        int gradeAlerts = gradeMapper.countGradeAlerts();
        if (gradeAlerts > 0) {
            Map<String, Object> todo = new HashMap<>();
            todo.put("type", "grade");
            todo.put("message", "有 " + gradeAlerts + " 个成绩预警待处理");
            todo.put("count", gradeAlerts);
            todoList.add(todo);
        }

        return todoList;
    }

    @Override
    public Map<String, Object> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        
        // 获取总课程数
        int totalCourses = courseMapper.countTotal();
        status.put("totalCourses", totalCourses);
        
        // 获取总教师数
        int totalTeachers = teacherMapper.countTotal();
        status.put("totalTeachers", totalTeachers);
        
        // 获取总教室数
        int totalClassrooms = classroomMapper.countTotal();
        status.put("totalClassrooms", totalClassrooms);
        
        // 获取选课统计
        Map<String, Object> courseSelectionStats = new HashMap<>();
        courseSelectionStats.put("daily", courseMapper.getDailySelectionStats());
        courseSelectionStats.put("weekly", courseMapper.getWeeklySelectionStats());
        courseSelectionStats.put("monthly", courseMapper.getMonthlySelectionStats());
        status.put("courseSelectionStats", courseSelectionStats);

        return status;
    }

    // 验证方法
    private void validateCourse(CourseDTO course) {
        if (course.getName() == null || course.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("课程名称不能为空");
        }
        if (course.getMaxStudents() <= 0) {
            throw new IllegalArgumentException("课程最大人数必须大于0");
        }
    }

    private void validateTeacher(TeacherDTO teacher) {
        if (teacher.getName() == null || teacher.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("教师姓名不能为空");
        }
    }

    private void validateClassroom(ClassroomDTO classroom) {
        if (classroom.getName() == null || classroom.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("教室名称不能为空");
        }
        if (classroom.getCapacity() <= 0) {
            throw new IllegalArgumentException("教室容量必须大于0");
        }
    }
} 