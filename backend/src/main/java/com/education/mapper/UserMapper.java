package com.education.mapper;

import com.education.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User findByUsername(@Param("username") String username);
    User findById(@Param("id") Long id);
    Long findIdByUsername(@Param("username") String username);
    Long findStudentIdByUsername(@Param("username") String username);
} 