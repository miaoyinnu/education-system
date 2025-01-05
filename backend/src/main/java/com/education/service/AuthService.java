package com.education.service;

import com.education.dto.LoginRequest;
import com.education.dto.LoginResponse;
import com.education.entity.User;
import com.education.exception.BusinessException;
import com.education.mapper.UserMapper;
import com.education.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public LoginResponse login(LoginRequest request) {
        logger.info("Processing login request for user: {}", request.getUsername());
        
        User user = userMapper.findByUsername(request.getUsername());
        logger.debug("Found user: {}", user);
        
        if (user == null) {
            logger.warn("User not found: {}", request.getUsername());
            throw new BusinessException("用户名或密码错误");
        }
        
        // 暂时使用明文密码比较
        if (!request.getPassword().equals(user.getPassword())) {
            logger.warn("Password mismatch for user: {}", request.getUsername());
            throw new BusinessException("用户名或密码错误");
        }
        
        String token = jwtUtil.generateToken(user.getUsername());
        logger.info("Generated token for user: {}", request.getUsername());
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        
        logger.info("Login successful for user: {}", request.getUsername());
        return response;
    }
} 