package com.education.common;

public interface ResultCode {
    // 成功
    int SUCCESS = 200;
    
    // 失败
    int ERROR = 500;
    
    // 未授权
    int UNAUTHORIZED = 401;
    
    // 禁止访问
    int FORBIDDEN = 403;
    
    // 参数错误
    int PARAM_ERROR = 400;
    
    // 不存在
    int NOT_FOUND = 404;
    
    // 冲突
    int CONFLICT = 409;
} 