package com.education.mapper;

import com.education.dto.ClassroomDTO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface ClassroomMapper {
    void insert(ClassroomDTO classroom);
    void update(ClassroomDTO classroom);
    void delete(Long id);
    List<ClassroomDTO> findAll();
    Map<String, Double> getUsageRates();
    Map<String, Double> getCapacityUtilization();
    
    int countTotal();
    List<Map<String, Object>> getCurrentUsage();
    List<Map<String, Object>> getDailyUsageStats();
    Double getUtilizationRate();
} 