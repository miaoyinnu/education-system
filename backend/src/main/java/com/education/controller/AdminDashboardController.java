package com.education.controller;

import com.education.common.Result;
import com.education.service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService dashboardService;

    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        return Result.success(dashboardService.getOverview());
    }

    @GetMapping("/course-selection-trend")
    public Result<Map<String, Object>> getCourseSelectionTrend() {
        return Result.success(dashboardService.getCourseSelectionTrend());
    }

    @GetMapping("/classroom-usage")
    public Result<Map<String, Object>> getClassroomUsage() {
        return Result.success(dashboardService.getClassroomUsage());
    }

    @GetMapping("/recent-alerts")
    public Result<Map<String, Object>> getRecentAlerts() {
        return Result.success(dashboardService.getRecentAlerts());
    }
} 