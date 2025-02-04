package com.education.service;

import com.education.dto.SystemSettingsDTO;
import java.util.List;

public interface SystemSettingsService {
    SystemSettingsDTO updateSettings(SystemSettingsDTO settingsDTO);
    List<SystemSettingsDTO> getSettings();
    SystemSettingsDTO getSettingByKey(String key);
    void updateGradeAlertThreshold(Integer threshold);
    Integer getGradeAlertThreshold();
} 