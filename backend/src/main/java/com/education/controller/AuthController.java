package com.education.controller;

import com.education.dto.LoginRequest;
import com.education.dto.LoginResponse;
import com.education.dto.UserInfoResponse;
import com.education.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            logger.info("Login request received: {}", request.getUsername());
            
            if (request.getUsername() == null || request.getPassword() == null) {
                return ResponseEntity.badRequest().body("用户名和密码不能为空");
            }
            
            LoginResponse response = authService.login(request);
            logger.info("Login successful for user: {}", request.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Login failed for user: " + request.getUsername(), e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo() {
        try {
            UserInfoResponse userInfo = authService.getUserInfo();
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            logger.error("Failed to get user info", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
} 