package com.education.service.impl;

import com.education.dto.SystemSettingsDTO;
import com.education.mapper.SystemSettingsMapper;
import com.education.service.SystemSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class SystemSettingsServiceImpl implements SystemSettingsService {

    @Autowired
    private SystemSettingsMapper settingsMapper;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public SystemSettingsDTO updateSettings(SystemSettingsDTO settingsDTO) {
        settingsMapper.updateSettings(settingsDTO);
        return settingsDTO;
    }

    @Override
    public List<SystemSettingsDTO> getSettings() {
        return settingsMapper.findAll();
    }

    @Override
    public SystemSettingsDTO getSettingByKey(String key) {
        return settingsMapper.findByKey(key);
    }

    @Override
    @Transactional
    public void resetToDefault() {
        // 定义默认设置
        Map<String, String> defaultSettings = new HashMap<>();
        defaultSettings.put("grade_alert_threshold", "60");
        defaultSettings.put("max_course_selection", "5");
        defaultSettings.put("course_selection_enabled", "true");
        
        // 更新每个设置到默认值
        defaultSettings.forEach((key, value) -> {
            SystemSettingsDTO setting = new SystemSettingsDTO();
            setting.setSettingKey(key);
            setting.setSettingValue(value);
            setting.setDescription("Default setting");
            settingsMapper.updateSettings(setting);
        });
    }

    @Override
    @Transactional
    public void importSettings(String filePath) {
        try {
            // 从JSON文件读取设置
            List<SystemSettingsDTO> settings = objectMapper.readValue(
                new File(filePath),
                objectMapper.getTypeFactory().constructCollectionType(List.class, SystemSettingsDTO.class)
            );
            
            // 更新每个设置
            for (SystemSettingsDTO setting : settings) {
                settingsMapper.updateSettings(setting);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to import settings: " + e.getMessage(), e);
        }
    }

    @Override
    public void exportSettings(String filePath) {
        try {
            // 获取所有设置
            List<SystemSettingsDTO> settings = settingsMapper.findAll();
            
            // 写入JSON文件
            objectMapper.writeValue(new File(filePath), settings);
        } catch (IOException e) {
            throw new RuntimeException("Failed to export settings: " + e.getMessage(), e);
        }
    }
} 