package com.education.controller;

import com.education.common.Result;
import com.education.dto.SystemSettingsDTO;
import com.education.service.SystemSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/system/settings")
public class SystemSettingsController {

    @Autowired
    private SystemSettingsService settingsService;

    @GetMapping
    public Result<List<SystemSettingsDTO>> getSettings() {
        return Result.success(settingsService.getSettings());
    }

    @PutMapping
    public Result<SystemSettingsDTO> updateSettings(@RequestBody SystemSettingsDTO settingsDTO) {
        return Result.success(settingsService.updateSettings(settingsDTO));
    }

    @PostMapping("/reset")
    public Result<Void> resetToDefault() {
        settingsService.resetToDefault();
        return Result.success();
    }

    @PostMapping("/import")
    public Result<Void> importSettings(@RequestBody String content) {
        settingsService.importSettings(content);
        return Result.success();
    }

    @GetMapping("/export")
    public Result<Void> exportSettings(@RequestParam String format, HttpServletResponse response) {
        settingsService.exportSettings(format);
        return Result.success();
    }
} 