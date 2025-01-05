package com.education.mapper;

import com.education.entity.Course;
import com.education.dto.TeacherDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface TeacherMapper {
    List<Course> getTeacherCourses(Long teacherId);
    
    // 管理员相关方法
    List<TeacherDTO> findAll();
    void insert(TeacherDTO teacher);
    void update(TeacherDTO teacher);
    void delete(Long id);
    boolean hasOngoingCourses(Long id);
    
    @Select("SELECT name FROM teacher WHERE id = #{id}")
    String findNameById(Long id);
} 