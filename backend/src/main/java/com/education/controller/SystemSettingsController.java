package com.education.controller;

import com.education.common.Result;
import com.education.dto.SystemSettingsDTO;
import com.education.service.SystemSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings")
public class SystemSettingsController {
    
    @Autowired
    private SystemSettingsService settingsService;

    @GetMapping
    public Result<List<SystemSettingsDTO>> getSettings() {
        return Result.success(settingsService.getSettings());
    }

    @GetMapping("/{key}")
    public Result<SystemSettingsDTO> getSettingByKey(@PathVariable String key) {
        return Result.success(settingsService.getSettingByKey(key));
    }

    @PutMapping
    public Result<SystemSettingsDTO> updateSettings(@RequestBody SystemSettingsDTO settingsDTO) {
        return Result.success(settingsService.updateSettings(settingsDTO));
    }

    @GetMapping("/grade-alert/threshold")
    public Result<Integer> getGradeAlertThreshold() {
        return Result.success(settingsService.getGradeAlertThreshold());
    }

    @PutMapping("/grade-alert/threshold")
    public Result<Void> updateGradeAlertThreshold(@RequestParam Integer threshold) {
        settingsService.updateGradeAlertThreshold(threshold);
        return Result.success();
    }
} 