package com.education.service.impl;

import com.education.dto.SystemSettingsDTO;
import com.education.entity.SystemSettings;
import com.education.mapper.SystemSettingsMapper;
import com.education.service.SystemSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SystemSettingsServiceImpl implements SystemSettingsService {

    @Autowired
    private SystemSettingsMapper systemSettingsMapper;

    @Override
    @Transactional
    public SystemSettingsDTO updateSettings(SystemSettingsDTO settingsDTO) {
        SystemSettings settings = new SystemSettings();
        settings.setKey(settingsDTO.getKey());
        settings.setValue(settingsDTO.getValue());
        settings.setDescription(settingsDTO.getDescription());
        systemSettingsMapper.updateByKey(settings);
        return settingsDTO;
    }

    @Override
    public List<SystemSettingsDTO> getSettings() {
        List<SystemSettings> settings = systemSettingsMapper.findAllSettings();
        return settings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SystemSettingsDTO getSettingByKey(String key) {
        SystemSettings settings = systemSettingsMapper.getByKey(key);
        return settings != null ? convertToDTO(settings) : null;
    }

    @Override
    @Transactional
    public void updateGradeAlertThreshold(Integer threshold) {
        SystemSettings settings = new SystemSettings();
        settings.setKey("grade_alert_threshold");
        settings.setValue(String.valueOf(threshold));
        settings.setDescription("成绩预警阈值");
        systemSettingsMapper.updateByKey(settings);
    }

    @Override
    public Integer getGradeAlertThreshold() {
        SystemSettings settings = systemSettingsMapper.getByKey("grade_alert_threshold");
        return settings != null ? Integer.parseInt(settings.getValue()) : 60;
    }

    private SystemSettingsDTO convertToDTO(SystemSettings settings) {
        SystemSettingsDTO dto = new SystemSettingsDTO();
        dto.setKey(settings.getKey());
        dto.setValue(settings.getValue());
        dto.setDescription(settings.getDescription());
        return dto;
    }
} 