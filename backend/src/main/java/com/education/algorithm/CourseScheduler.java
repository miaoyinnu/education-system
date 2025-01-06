package com.education.algorithm;

import com.education.dto.CourseDTO;
import com.education.dto.TeacherDTO;
import com.education.dto.ClassroomDTO;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CourseScheduler {
    private Map<Long, Set<String>> teacherTimeSlots;  // 教师已占用的时间段
    private Map<Long, Set<String>> classroomTimeSlots;  // 教室已占用的时间段
    
    private static final String[] TIME_SLOTS = {
        "周一-1", "周一-2", "周一-3", "周一-4",
        "周二-1", "周二-2", "周二-3", "周二-4",
        "周三-1", "周三-2", "周三-3", "周三-4",
        "周四-1", "周四-2", "周四-3", "周四-4",
        "周五-1", "周五-2", "周五-3", "周五-4"
    };

    public CourseScheduler() {
        this.teacherTimeSlots = new HashMap<>();
        this.classroomTimeSlots = new HashMap<>();
    }

    public List<CourseDTO> schedule(List<CourseDTO> courses, List<TeacherDTO> teachers, List<ClassroomDTO> classrooms) {
        // 初始化教师和教室的时间槽
        teacherTimeSlots.clear();
        classroomTimeSlots.clear();
        
        for (TeacherDTO teacher : teachers) {
            teacherTimeSlots.put(teacher.getId(), new HashSet<>());
        }
        for (ClassroomDTO classroom : classrooms) {
            classroomTimeSlots.put(classroom.getId(), new HashSet<>());
        }

        // 按照优先级对课程进行排序
        courses.sort((c1, c2) -> {
            // 可以根据需要添加更复杂的优先级规则
            return c2.getCapacity().compareTo(c1.getCapacity());
        });

        // 获取可用的教室列表
        List<ClassroomDTO> availableClassrooms = classrooms.stream()
            .filter(c -> "可用".equals(c.getStatus()))
            .collect(Collectors.toList());

        List<CourseDTO> scheduledCourses = new ArrayList<>();
        for (CourseDTO course : courses) {
            boolean scheduled = false;
            Long teacherId = course.getTeacherId();

            // 遍历所有时间槽
            for (String timeSlot : TIME_SLOTS) {
                // 检查教师在这个时间段是否可用
                if (teacherTimeSlots.get(teacherId).contains(timeSlot)) {
                    continue;
                }

                // 寻找合适的教室
                for (ClassroomDTO classroom : availableClassrooms) {
                    if (classroom.getCapacity() >= course.getCapacity() &&
                        !classroomTimeSlots.get(classroom.getId()).contains(timeSlot)) {
                        
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
                        break;
                    }
                }
                
                if (scheduled) {
                    break;
                }
            }
        }

        return scheduledCourses;
    }
} 