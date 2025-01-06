package com.education.service.impl;

import com.education.dto.*;
import com.education.entity.*;
import com.education.mapper.*;
import com.education.service.AdminService;
import com.education.algorithm.CourseScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private SystemSettingsMapper systemSettingsMapper;
    @Autowired
    private GradeMapper gradeMapper;
    @Autowired
    private CourseScheduler courseScheduler;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StudentMapper studentMapper;

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
        SystemSettings settings = new SystemSettings();
        settings.setKey(settingsDTO.getKey());
        settings.setValue(settingsDTO.getValue());
        settings.setDescription(settingsDTO.getDescription());
        systemSettingsMapper.updateByKey(settings);
        return settingsDTO;
    }

    @Override
    public List<SystemSettingsDTO> getSettings() {
        // TODO: 实现从 SystemSettings 到 SystemSettingsDTO 的转换
        return Collections.emptyList();
    }

    // 统计分析
    @Override
    public Map<String, Object> getGradeStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取所有课程的平均分、最高分、最低分、及格率
        List<Map<String, Object>> courseStats = gradeMapper.getCourseStatistics();
        stats.put("courseStats", courseStats);
        
        // 获取成绩分布
        Map<String, Integer> gradeDistribution = gradeMapper.getGradeDistribution();
        stats.put("gradeDistribution", gradeDistribution);
        
        // 获取学生成绩统计
        List<Map<String, Object>> studentStats = gradeMapper.getStudentStatistics();
        stats.put("studentStats", studentStats);
        
        return stats;
    }

    @Override
    public Map<String, Object> getClassroomUsageStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取教室使用率
        List<Map<String, Object>> usageStats = classroomMapper.getUsageStatistics();
        stats.put("usageStats", usageStats);
        
        // 获取教室容量利用率
        List<Map<String, Object>> capacityStats = classroomMapper.getCapacityStatistics();
        stats.put("capacityStats", capacityStats);
        
        return stats;
    }

    // 成绩预警
    @Override
    @Transactional
    public void setGradeAlertThreshold(GradeAlertSettingDTO settingDTO) {
        SystemSettings settings = new SystemSettings();
        settings.setKey("grade_alert_threshold");
        settings.setValue(String.valueOf(settingDTO.getThreshold()));
        systemSettingsMapper.updateByKey(settings);
    }

    @Override
    public GradeAlertSettingDTO getGradeAlertThreshold() {
        SystemSettings settings = systemSettingsMapper.getByKey("grade_alert_threshold");
        GradeAlertSettingDTO dto = new GradeAlertSettingDTO();
        dto.setThreshold(settings != null ? Integer.parseInt(settings.getValue()) : 60);
        return dto;
    }

    @Override
    public List<GradeAlertDTO> getGradeAlerts() {
        GradeAlertSettingDTO threshold = getGradeAlertThreshold();
        return gradeMapper.findGradeAlerts(threshold.getThreshold());
    }

    @Override
    @Transactional
    public void sendGradeAlert(Long studentId, Long courseId) {
        // 获取学生信息
        Student student = studentMapper.findById(studentId);
        // 获取课程信息
        Course course = courseMapper.findById(courseId);
        
        if (student == null || course == null) {
            throw new IllegalArgumentException("学生或课程不存在");
        }
        
        // 保存一条系统通知记录
        SystemNotification notification = new SystemNotification();
        notification.setUserId(studentId);
        notification.setTitle("成绩预警通知");
        notification.setContent(String.format("您在课程《%s》中的成绩未达到预期，请及时改进。", course.getName()));
        notification.setType("GRADE_ALERT");
        notification.setStatus("UNREAD");
        notification.setCreatedAt(LocalDateTime.now());
        
        systemSettingsMapper.insertNotification(notification);
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

    // 用户管理
    @Override
    public Map<String, Object> getUsers(int page, int size, String search) {
        int offset = (page - 1) * size;
        List<User> users = userMapper.findByPage(offset, size, search);
        int total = userMapper.count(search);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", users);
        result.put("total", total);
        return result;
    }

    @Override
    @Transactional
    public User addUser(User user) {
        // 检查用户名是否已存在
        if (userMapper.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        // 设置创建时间
        user.setCreatedAt(LocalDateTime.now());
        userMapper.insert(user);
        
        // 根据角色在对应表中创建记录
        if ("STUDENT".equals(user.getRole())) {
            Student student = new Student();
            student.setUserId(user.getId());
            student.setName(user.getUsername()); // 默认使用用户名作为学生姓名
            studentMapper.insert(student);
        } else if ("TEACHER".equals(user.getRole())) {
            Teacher teacher = new Teacher();
            teacher.setUserId(user.getId());
            teacher.setName(user.getUsername()); // 默认使用用户名作为教师姓名
            teacherMapper.insert(teacher);
        }
        
        return user;
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        // 检查用户是否存在
        User existingUser = userMapper.findById(user.getId());
        if (existingUser == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 如果修改了用户名，检查新用户名是否已存在
        if (!existingUser.getUsername().equals(user.getUsername()) 
            && userMapper.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        // 如果角色发生变化，需要处理关联表
        if (!existingUser.getRole().equals(user.getRole())) {
            // 删除原有角色的关联记录
            if ("STUDENT".equals(existingUser.getRole())) {
                studentMapper.deleteByUserId(user.getId());
            } else if ("TEACHER".equals(existingUser.getRole())) {
                teacherMapper.deleteByUserId(user.getId());
            }
            
            // 创建新角色的关联记录
            if ("STUDENT".equals(user.getRole())) {
                Student student = new Student();
                student.setUserId(user.getId());
                student.setName(user.getUsername());
                studentMapper.insert(student);
            } else if ("TEACHER".equals(user.getRole())) {
                Teacher teacher = new Teacher();
                teacher.setUserId(user.getId());
                teacher.setName(user.getUsername());
                teacherMapper.insert(teacher);
            }
        }
        
        userMapper.update(user);
        return user;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        // 检查用户是否存在
        User user = userMapper.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 检查用户是否有关联数据
        if (user.getRole().equals("TEACHER") && teacherMapper.hasOngoingCourses(id)) {
            throw new IllegalStateException("该用户是教师且有正在进行的课程，无法删除");
        }
        
        userMapper.delete(id);
    }

    @Override
    public List<CourseDTO> getScheduledCourses() {
        return courseMapper.findScheduledCourses();
    }

    @Override
    public List<CourseDTO> getUnscheduledCourses() {
        return courseMapper.findUnscheduledCourses();
    }
} 