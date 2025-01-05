package com.education.service;

import java.util.Map;

public interface AdminDashboardService {
    Map<String, Object> getOverview();
    Map<String, Object> getCourseSelectionTrend();
    Map<String, Object> getClassroomUsage();
    Map<String, Object> getRecentAlerts();
} 