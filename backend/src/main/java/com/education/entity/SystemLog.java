package com.education.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SystemLog {
    private Long id;
    private String operation;     // 操作类型
    private String description;   // 操作描述
    private String operator;      // 操作人
    private String operatorType;  // 操作人类型（学生、教师、管理员）
    private String ipAddress;     // IP地址
    private String method;        // 请求方法
    private String params;        // 请求参数
    private String result;        // 操作结果
    private Integer status;       // 状态（0：成功，1：失败）
    private String errorMsg;      // 错误信息
    private LocalDateTime createTime; // 创建时间
    private Long duration;        // 执行时长（毫秒）
} 