package com.education.mapper;

import com.education.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User findByUsername(String username);
    Long findIdByUsername(String username);
    Long findStudentIdByUsername(String username);
} 