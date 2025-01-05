package com.education.service.impl;

import com.education.mapper.*;
import com.education.service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private ClassroomMapper classroomMapper;

    @Autowired
    private GradeMapper gradeMapper;

    @Autowired
    private SystemLogMapper systemLogMapper;

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        // 获取基础统计数据
        overview.put("studentCount", studentMapper.countTotal());
        overview.put("courseCount", courseMapper.countTotal());
        overview.put("classroomCount", classroomMapper.countTotal());
        
        // 获取成绩统计
        Map<String, Object> gradeStats = gradeMapper.getOverallStats();
        overview.put("averageScore", gradeStats.get("avgScore"));
        overview.put("passRate", gradeStats.get("passRate"));
        
        // 获取系统使用统计
        overview.put("todayOperations", systemLogMapper.getHourlyOperationStats());
        overview.put("operationsByType", systemLogMapper.getOperationStatsByType());
        
        return overview;
    }

    @Override
    public Map<String, Object> getCourseSelectionTrend() {
        Map<String, Object> trend = new HashMap<>();
        trend.put("daily", courseMapper.getDailySelectionStats());
        trend.put("weekly", courseMapper.getWeeklySelectionStats());
        trend.put("monthly", courseMapper.getMonthlySelectionStats());
        return trend;
    }

    @Override
    public Map<String, Object> getClassroomUsage() {
        Map<String, Object> usage = new HashMap<>();
        usage.put("current", classroomMapper.getCurrentUsage());
        usage.put("daily", classroomMapper.getDailyUsageStats());
        usage.put("utilization", classroomMapper.getUtilizationRate());
        return usage;
    }

    @Override
    public Map<String, Object> getRecentAlerts() {
        Map<String, Object> alerts = new HashMap<>();
        alerts.put("gradeAlerts", gradeMapper.getRecentAlerts());
        alerts.put("systemAlerts", systemLogMapper.findRecent(10));
        return alerts;
    }
} 