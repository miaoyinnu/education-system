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
    }

    public List<CourseDTO> schedule(List<CourseDTO> courses, List<TeacherDTO> teachers, List<ClassroomDTO> classrooms) {
        logger.debug("开始排课，待排课程数：{}，教师数：{}，教室数：{}", 
            courses.size(), teachers.size(), classrooms.size());
        
        // 初始化教师和教室的时间槽
        teacherTimeSlots.clear();
        classroomTimeSlots.clear();
        
        // 检查是否有可用的教师和教室
        if (teachers.isEmpty() || classrooms.isEmpty()) {
            logger.warn("没有可用的教师或教室");
            return new ArrayList<>();
        }
        
        for (TeacherDTO teacher : teachers) {
            teacherTimeSlots.put(teacher.getId(), new HashSet<>());
        }
        for (ClassroomDTO classroom : classrooms) {
            classroomTimeSlots.put(classroom.getId(), new HashSet<>());
        }

        // 按照优先级对课程进行排序（按最大学生数降序）
        courses.sort((c1, c2) -> {
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
        for (CourseDTO course : courses) {
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
                // 检查教师在这个时间段是否可用
                if (teacherTimeSlots.get(teacherId) != null && 
                    teacherTimeSlots.get(teacherId).contains(timeSlot)) {
                    logger.debug("教师在时间段 {} 已被占用", timeSlot);
                    continue;
                }

                // 寻找合适的教室
                for (ClassroomDTO classroom : availableClassrooms) {
                    // 检查教室容量和时间槽占用情况
                    if (classroom.getCapacity() >= course.getMaxStudents() &&
                        !classroomTimeSlots.get(classroom.getId()).contains(timeSlot)) {
                        
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
                        
                        scheduled = true;
                        scheduledCourses.add(course);
                        logger.debug("课程 {} 排课成功", course.getName());
                        break;
                    } else {
                        logger.debug("教室 {} 不满足要求：容量={}, 所需容量={}, 时间段是否被占用={}", 
                            classroom.getName(), 
                            classroom.getCapacity(), 
                            course.getMaxStudents(),
                            classroomTimeSlots.get(classroom.getId()).contains(timeSlot));
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