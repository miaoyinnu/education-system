package com.education.util;

import com.education.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserIdCache {
    private static UserMapper userMapper;
    private static final Map<String, Long> cache = new ConcurrentHashMap<>();
    private static final Map<String, Long> studentCache = new ConcurrentHashMap<>();

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        UserIdCache.userMapper = userMapper;
    }

    public static Long getIdByUsername(String username) {
        return cache.computeIfAbsent(username, k -> {
            if (userMapper != null) {
                return userMapper.findIdByUsername(username);
            }
            return null;
        });
    }

    public static Long getStudentIdByUsername(String username) {
        return studentCache.computeIfAbsent(username, k -> {
            if (userMapper != null) {
                return userMapper.findStudentIdByUsername(username);
            }
            return null;
        });
    }

    public static void clearCache() {
        cache.clear();
        studentCache.clear();
    }
} 