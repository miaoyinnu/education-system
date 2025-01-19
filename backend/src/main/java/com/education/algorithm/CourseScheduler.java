package com.education.algorithm;

import com.education.dto.CourseDTO;
import com.education.dto.TeacherDTO;
import com.education.dto.ClassroomDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CourseScheduler {
    private static final Logger logger = LoggerFactory.getLogger(CourseScheduler.class);
    
    private Map<Long, Set<String>> teacherTimeSlots;  // 教师已占用的时间段
    private Map<Long, Set<String>> classroomTimeSlots;  // 教室已占用的时间段
    private Set<String> occupiedTimeSlots;  // 所有已占用的时间段
    
    private static final String[] TIME_SLOTS = {
        "周一 1-2节", "周一 3-4节", "周一 5-6节", "周一 7-8节",
        "周二 1-2节", "周二 3-4节", "周二 5-6节", "周二 7-8节",
        "周三 1-2节", "周三 3-4节", "周三 5-6节", "周三 7-8节",
        "周四 1-2节", "周四 3-4节", "周四 5-6节", "周四 7-8节",
        "周五 1-2节", "周五 3-4节", "周五 5-6节", "周五 7-8节"
    };

    public CourseScheduler() {
        this.teacherTimeSlots = new HashMap<>();
        this.classroomTimeSlots = new HashMap<>();
        this.occupiedTimeSlots = new HashSet<>();
    }

    public List<CourseDTO> schedule(List<CourseDTO> courses, List<TeacherDTO> teachers, List<ClassroomDTO> classrooms) {
        logger.debug("开始排课，待排课程数：{}，教师数：{}，教室数：{}", 
            courses.size(), teachers.size(), classrooms.size());
        
        // 初始化教师和教室的时间槽
        teacherTimeSlots.clear();
        classroomTimeSlots.clear();
        occupiedTimeSlots.clear();
        
        // 检查是否有可用的教师和教室
        if (teachers.isEmpty() || classrooms.isEmpty()) {
            logger.warn("没有可用的教师或教室");
            return new ArrayList<>();
        }
        
        // 为每个教师和教室初始化时间槽集合
        teachers.forEach(teacher -> 
            teacherTimeSlots.put(teacher.getId(), new HashSet<>()));
        classrooms.forEach(classroom -> 
            classroomTimeSlots.put(classroom.getId(), new HashSet<>()));

        // 初始化已排课程的时间槽
        logger.debug("初始化已排课程时间槽：");
        for (CourseDTO course : courses) {
            if (course.getTime() != null && course.getTeacherId() != null && course.getClassroomId() != null) {
                logger.debug("已排课程：{} - {} - {} - {}", 
                    course.getName(), course.getTeacherId(), course.getClassroomId(), course.getTime());
                    
                // 检查教师时间槽映射是否存在
                Set<String> teacherSlots = teacherTimeSlots.get(course.getTeacherId());
                if (teacherSlots != null) {
                    teacherSlots.add(course.getTime());
                }
                
                // 检查教室时间槽映射是否存在
                Set<String> classroomSlots = classroomTimeSlots.get(course.getClassroomId());
                if (classroomSlots != null) {
                    classroomSlots.add(course.getTime());
                }
                
                // 添加到全局已占用时间槽
                occupiedTimeSlots.add(course.getTime());
            }
        }

        // 打印初始化后的占用情况
        logger.debug("初始化后教师时间槽占用情况：");
        teacherTimeSlots.forEach((teacherId, slots) -> 
            logger.debug("教师 {} 已占用时间槽: {}", teacherId, slots));
            
        logger.debug("初始化后教室时间槽占用情况：");
        classroomTimeSlots.forEach((classroomId, slots) -> 
            logger.debug("教室 {} 已占用时间槽: {}", classroomId, slots));
            
        logger.debug("初始化后全局已占用时间槽: {}", occupiedTimeSlots);

        // 过滤出未排课的课程
        List<CourseDTO> unscheduledCourses = courses.stream()
            .filter(c -> c.getTime() == null)
            .collect(Collectors.toList());
        
        logger.debug("未排课程数量：{}", unscheduledCourses.size());

        // 按照优先级对未排课程进行排序（按最大学生数降序）
        unscheduledCourses.sort((c1, c2) -> {
            if (c2.getMaxStudents() == null || c1.getMaxStudents() == null) {
                logger.warn("课程最大学生数为空：{}, {}", 
                    c1.getName(), c2.getName());
                return 0;
            }
            return c2.getMaxStudents().compareTo(c1.getMaxStudents());
        });

        // 获取可用的教室列表
        logger.debug("所有教室状态：");
        for (ClassroomDTO classroom : classrooms) {
            logger.debug("教室 {} 状态: {}", classroom.getName(), classroom.getStatus());
        }

        List<ClassroomDTO> availableClassrooms = classrooms.stream()
            .filter(c -> c.getStatus() == null || !"禁用".equals(c.getStatus()))  // 只要不是禁用状态的教室都可以使用
            .collect(Collectors.toList());

        logger.debug("可用教室数量：{}", availableClassrooms.size());

        // 如果没有可用教室，直接返回空列表
        if (availableClassrooms.isEmpty()) {
            logger.warn("没有状态为可用的教室");
            return new ArrayList<>();
        }

        List<CourseDTO> scheduledCourses = new ArrayList<>();
        for (CourseDTO course : unscheduledCourses) {
            logger.debug("开始为课程 {} 安排时间和教室", course.getName());
            
            boolean scheduled = false;
            Long teacherId = course.getTeacherId();
            
            // 检查课程是否有教师
            if (teacherId == null) {
                logger.warn("课程 {} 没有指定教师", course.getName());
                continue;
            }

            // 遍历所有时间槽
            for (String timeSlot : TIME_SLOTS) {
                // 检查该时间段是否已被占用
                if (occupiedTimeSlots.contains(timeSlot)) {
                    logger.debug("时间段 {} 已被其他课程占用", timeSlot);
                    continue;
                }

                // 检查教师在这个时间段是否可用
                if (teacherTimeSlots.get(teacherId) != null && 
                    teacherTimeSlots.get(teacherId).contains(timeSlot)) {
                    logger.debug("教师在时间段 {} 已被占用", timeSlot);
                    continue;
                }

                // 寻找合适的教室
                for (ClassroomDTO classroom : availableClassrooms) {
                    // 检查教室在该时间段是否已被占用
                    Set<String> classroomSlots = classroomTimeSlots.get(classroom.getId());
                    if (classroomSlots != null && classroomSlots.contains(timeSlot)) {
                        logger.debug("教室 {} 在时间段 {} 已被占用", classroom.getName(), timeSlot);
                        continue;
                    }

                    // 检查教室容量是否满足要求
                    if (classroom.getCapacity() >= course.getMaxStudents()) {
                        logger.debug("找到合适的教室 {}，时间段 {}", 
                            classroom.getName(), timeSlot);
                        
                        // 分配时间和教室
                        course.setTime(timeSlot);
                        course.setTimeSlotId(Arrays.asList(TIME_SLOTS).indexOf(timeSlot) + 1L);
                        course.setLocation(classroom.getName());
                        course.setClassroomId(classroom.getId());
                        course.setClassroomName(classroom.getName());
                        
                        // 更新已占用时间槽
                        teacherTimeSlots.get(teacherId).add(timeSlot);
                        classroomTimeSlots.get(classroom.getId()).add(timeSlot);
                        occupiedTimeSlots.add(timeSlot);
                        
                        scheduled = true;
                        scheduledCourses.add(course);
                        logger.debug("课程 {} 排课成功", course.getName());
                        break;
                    } else {
                        logger.debug("教室 {} 容量不足：容量={}, 所需容量={}", 
                            classroom.getName(), 
                            classroom.getCapacity(), 
                            course.getMaxStudents());
                    }
                }
                
                if (scheduled) {
                    break;
                }
            }
            
            if (!scheduled) {
                logger.warn("课程 {} 排课失败", course.getName());
            }
        }

        logger.debug("排课完成，成功排课数量：{}", scheduledCourses.size());
        return scheduledCourses;
    }
} 