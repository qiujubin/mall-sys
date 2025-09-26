package com.gec.service;

import java.util.Map;

/**
 * 数据分析服务接口
 */
public interface AnalysisService {
    
    /**
     * 获取数据质量报告
     */
    Map<String, Object> getDataQualityReport();
    
    /**
     * 获取用户画像统计
     */
    Map<String, Object> getUserProfileStats();
    
    /**
     * 获取行为分析统计
     */
    Map<String, Object> getBehaviorStats();
}