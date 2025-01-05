package com.education.service;

import com.education.dto.*;
import com.education.entity.Course;
import com.education.exception.BusinessException;
import com.education.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class AdminService {
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private TeacherMapper teacherMapper;
    
    @Autowired
    private ClassroomMapper classroomMapper;
    
    // 课程管理
    @Transactional
    public void createCourse(CourseDTO course) {
        validateCourse(course);
        courseMapper.insert(course);
    }
    
    @Transactional
    public void updateCourse(CourseDTO course) {
        validateCourse(course);
        courseMapper.update(course);
    }
    
    @Transactional
    public void deleteCourse(Long id) {
        // 检查是否有学生已选课
        if (courseMapper.hasStudents(id)) {
            throw new BusinessException("该课程已有学生选课,不能删除");
        }
        courseMapper.delete(id);
    }
    
    public List<CourseDTO> getAllCourses() {
        List<Course> courses = courseMapper.findAll();
        return courses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private CourseDTO convertToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setName(course.getName());
        dto.setTeacher(course.getTeacherId() != null ? 
            teacherMapper.findNameById(course.getTeacherId()) : null);
        dto.setTime(course.getCourseTime());
        dto.setLocation(course.getClassroomName());
        dto.setSemester(course.getSemester());
        dto.setCapacity(course.getMaxStudents());
        dto.setSelected(course.getCurrentStudents() > 0);
        dto.setClassroomId(course.getClassroomId());
        dto.setMaxStudents(course.getMaxStudents());
        return dto;
    }
    
    // 教师管理
    @Transactional
    public void createTeacher(TeacherDTO teacher) {
        validateTeacher(teacher);
        teacherMapper.insert(teacher);
    }
    
    @Transactional
    public void updateTeacher(TeacherDTO teacher) {
        validateTeacher(teacher);
        teacherMapper.update(teacher);
    }
    
    @Transactional
    public void deleteTeacher(Long id) {
        // 检查是否有正在进行的课程
        if (teacherMapper.hasOngoingCourses(id)) {
            throw new BusinessException("该教师有正在进行的课程,不能删除");
        }
        teacherMapper.delete(id);
    }
    
    public List<TeacherDTO> getAllTeachers() {
        return teacherMapper.findAll();
    }
    
    // 教室管理
    @Transactional
    public void createClassroom(ClassroomDTO classroom) {
        validateClassroom(classroom);
        classroomMapper.insert(classroom);
    }
    
    @Transactional
    public void updateClassroom(ClassroomDTO classroom) {
        validateClassroom(classroom);
        classroomMapper.update(classroom);
    }
    
    @Transactional
    public void deleteClassroom(Long id) {
        // 检查是否有正在使用的课程
        if (classroomMapper.hasScheduledCourses(id)) {
            throw new BusinessException("该教室有已安排的课程,不能删除");
        }
        classroomMapper.delete(id);
    }
    
    public List<ClassroomDTO> getAllClassrooms() {
        return classroomMapper.findAll();
    }
    
    // 排课管理
    @Transactional
    public void scheduleClass(CourseDTO course) {
        List<String> conflicts = checkScheduleConflicts(course);
        if (!conflicts.isEmpty()) {
            throw new BusinessException("存在课程冲突: " + String.join(", ", conflicts));
        }
        courseMapper.updateSchedule(course);
    }
    
    public List<String> checkScheduleConflicts(CourseDTO course) {
        List<String> conflicts = new ArrayList<>();
        
        // 检查教师时间冲突
        if (courseMapper.hasTeacherTimeConflict(course)) {
            conflicts.add("教师时间冲突");
        }
        
        // 检查教室时间冲突
        if (courseMapper.hasClassroomTimeConflict(course)) {
            conflicts.add("教室时间冲突");
        }
        
        // 检查教室容量
        ClassroomDTO classroom = classroomMapper.findById(course.getClassroomId());
        if (classroom.getCapacity() < course.getMaxStudents()) {
            conflicts.add("教室容量不足");
        }
        
        return conflicts;
    }
    
    // 验证方法
    private void validateCourse(CourseDTO course) {
        if (course.getName() == null || course.getName().trim().isEmpty()) {
            throw new BusinessException("课程名称不能为空");
        }
        if (course.getMaxStudents() <= 0) {
            throw new BusinessException("课程最大人数必须大于0");
        }
    }
    
    private void validateTeacher(TeacherDTO teacher) {
        if (teacher.getName() == null || teacher.getName().trim().isEmpty()) {
            throw new BusinessException("教师姓名不能为空");
        }
        if (teacher.getUsername() == null || teacher.getUsername().trim().isEmpty()) {
            throw new BusinessException("用户名不能为空");
        }
    }
    
    private void validateClassroom(ClassroomDTO classroom) {
        if (classroom.getName() == null || classroom.getName().trim().isEmpty()) {
            throw new BusinessException("教室名称不能为空");
        }
        if (classroom.getCapacity() <= 0) {
            throw new BusinessException("教室容量必须大于0");
        }
    }
} 