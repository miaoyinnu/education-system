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

    // 课程管理
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
    @Transactional
    public TeacherDTO addTeacher(TeacherDTO teacherDTO) {
        validateTeacher(teacherDTO);
        teacherMapper.insert(teacherDTO);
        return teacherDTO;
    }

    @Override
    @Transactional
    public TeacherDTO updateTeacher(TeacherDTO teacherDTO) {
        validateTeacher(teacherDTO);
        teacherMapper.update(teacherDTO);
        return teacherDTO;
    }

    // 教室管理
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