package com.education.mapper;

import com.education.dto.ClassroomDTO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ClassroomMapper {
    List<ClassroomDTO> findAll();
    void insert(ClassroomDTO classroom);
    void update(ClassroomDTO classroom);
    void delete(Long id);
    boolean hasScheduledCourses(Long id);
    ClassroomDTO findById(Long id);
} 