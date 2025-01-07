package com.education.mapper;

import com.education.dto.ClassroomDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface ClassroomMapper {
    List<ClassroomDTO> findAll();
    void insert(ClassroomDTO classroomDTO);
    void update(ClassroomDTO classroomDTO);
    void delete(Long id);
    
    // 统计分析
    List<Map<String, Object>> getUsageStatistics();
    List<Map<String, Object>> getCapacityStatistics();
    int countTotal();

    // 仪表盘相关
    List<Map<String, Object>> getCurrentUsage();
    List<Map<String, Object>> getDailyUsageStats();
    Double getUtilizationRate();
} 