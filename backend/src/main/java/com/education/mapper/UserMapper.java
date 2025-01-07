package com.education.mapper;

import com.education.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {
    User findByUsername(@Param("username") String username);
    User findById(@Param("id") Long id);
    Long findIdByUsername(@Param("username") String username);
    Long findStudentIdByUsername(@Param("username") String username);
    
    // 用户管理
    List<User> findByPage(@Param("offset") int offset, @Param("size") int size, @Param("search") String search);
    int count(@Param("search") String search);
    void insert(User user);
    void update(User user);
    void delete(Long id);
    boolean existsByUsername(String username);
} 