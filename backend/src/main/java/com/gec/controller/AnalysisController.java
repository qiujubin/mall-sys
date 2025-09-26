package com.gec.controller;

import com.gec.common.R;
import com.gec.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 数据分析控制器
 */
@RestController
@RequestMapping("/analysis")
@CrossOrigin
public class AnalysisController {

    @Autowired
    private AnalysisService analysisService;

    /**
     * 获取数据质量报告
     */
    @GetMapping("/data-quality")
    public R<Map<String, Object>> getDataQualityReport() {
        Map<String, Object> report = analysisService.getDataQualityReport();
        return R.success(report);
    }

    /**
     * 获取用户画像统计
     */
    @GetMapping("/user-profile-stats")
    public R<Map<String, Object>> getUserProfileStats() {
        Map<String, Object> stats = analysisService.getUserProfileStats();
        return R.success(stats);
    }

    /**
     * 获取行为分析统计
     */
    @GetMapping("/behavior-stats")
    public R<Map<String, Object>> getBehaviorStats() {
        Map<String, Object> stats = analysisService.getBehaviorStats();
        return R.success(stats);
    }
}