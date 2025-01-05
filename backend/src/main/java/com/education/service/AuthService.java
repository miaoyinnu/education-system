package com.education.service;

import com.education.dto.LoginRequest;
import com.education.dto.LoginResponse;
import com.education.dto.UserInfoResponse;
import com.education.entity.User;
import com.education.mapper.UserMapper;
import com.education.util.JwtUtil;
import com.education.util.UserContext;
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
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        logger.debug("Found user: {}", user);
        
        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        String token = jwtUtil.generateToken(user.getUsername());
        logger.info("Generated token for user: {}", user.getUsername());
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setRole(user.getRole());
        response.setUsername(user.getUsername());
        
        logger.info("Login successful for user: {}", user.getUsername());
        return response;
    }

    public UserInfoResponse getUserInfo() {
        String username = UserContext.getCurrentUsername();
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        UserInfoResponse response = new UserInfoResponse();
        response.setName(user.getUsername());
        response.setRole(user.getRole());
        return response;
    }
} 