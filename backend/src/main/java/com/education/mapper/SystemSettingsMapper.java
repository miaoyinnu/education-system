package com.education.mapper;

import com.education.entity.SystemSettings;
import com.education.entity.SystemNotification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SystemSettingsMapper {
    SystemSettings getByKey(@Param("key") String key);
    void updateByKey(SystemSettings settings);
    void insertNotification(SystemNotification notification);
    List<SystemSettings> findAllSettings();
} 