package com.education.mapper;

import com.education.dto.SystemSettingsDTO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface SystemSettingsMapper {
    void updateSettings(SystemSettingsDTO settingsDTO);
    List<SystemSettingsDTO> findAll();
    SystemSettingsDTO findByKey(String key);
} 