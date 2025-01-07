package com.education.mapper;

import com.education.entity.Course;
import com.education.entity.Teacher;
import com.education.dto.TeacherDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TeacherMapper {
    List<Course> getTeacherCourses(Long teacherId);
    
    // 管理员相关方法
    List<TeacherDTO> findAll();
    void insert(Teacher teacher);
    void update(TeacherDTO teacher);
    void delete(Long id);
    boolean hasOngoingCourses(Long id);
    void deleteByUserId(@Param("userId") Long userId);
    
    @Select("SELECT name FROM teacher WHERE id = #{id}")
    String findNameById(Long id);

    @Select("SELECT COUNT(*) FROM teacher")
    int countTotal();
} 