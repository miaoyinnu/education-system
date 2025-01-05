package com.education.mapper;

import com.education.entity.SystemLog;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface SystemLogMapper {
    @Insert("INSERT INTO system_log(operation, description, operator, operator_type, ip_address, method, params, result, status, error_msg, create_time, duration) " +
            "VALUES(#{operation}, #{description}, #{operator}, #{operatorType}, #{ipAddress}, #{method}, #{params}, #{result}, #{status}, #{errorMsg}, #{createTime}, #{duration})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(SystemLog log);

    @Select("SELECT * FROM system_log WHERE id = #{id}")
    SystemLog findById(Long id);

    @Select("SELECT * FROM system_log ORDER BY create_time DESC LIMIT #{limit}")
    List<SystemLog> findRecent(int limit);

    @Select("SELECT * FROM system_log WHERE operator = #{operator} ORDER BY create_time DESC LIMIT #{limit}")
    List<SystemLog> findByOperator(String operator, int limit);

    @Select("SELECT COUNT(*) FROM system_log WHERE status = #{status}")
    int countByStatus(Integer status);

    @Select("SELECT operator_type, COUNT(*) as count FROM system_log " +
            "WHERE create_time >= DATE_SUB(NOW(), INTERVAL 24 HOUR) " +
            "GROUP BY operator_type")
    List<Map<String, Object>> getOperationStatsByType();

    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m-%d %H:00:00') as hour, COUNT(*) as count " +
            "FROM system_log " +
            "WHERE create_time >= DATE_SUB(NOW(), INTERVAL 24 HOUR) " +
            "GROUP BY hour " +
            "ORDER BY hour")
    List<Map<String, Object>> getHourlyOperationStats();

    @Delete("DELETE FROM system_log WHERE create_time < DATE_SUB(NOW(), INTERVAL #{days} DAY)")
    void deleteOldLogs(int days);
} 