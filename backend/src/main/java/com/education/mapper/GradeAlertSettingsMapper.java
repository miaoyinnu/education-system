package com.education.mapper;

import com.education.dto.GradeAlertSettingDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;

@Mapper
public interface GradeAlertSettingsMapper {
    
    @Select("SELECT * FROM grade_alert_settings WHERE course_id = #{courseId}")
    GradeAlertSettingDTO findByCourseId(Long courseId);
    
    @Update("UPDATE grade_alert_settings SET alert_threshold = #{alertThreshold} " +
            "WHERE course_id = #{courseId}")
    void updateThreshold(GradeAlertSettingDTO setting);
    
    @Insert("INSERT INTO grade_alert_settings(course_id, alert_threshold) " +
            "VALUES(#{courseId}, #{alertThreshold})")
    void insert(GradeAlertSettingDTO setting);
} 