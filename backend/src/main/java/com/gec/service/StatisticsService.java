package com.gec.service;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 统计图表服务接口
 */
public interface StatisticsService {
    
    /**
     * 获取行为概览数据
     */
    List<Map<String, Object>> getBehaviorOverview(LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取性别分布数据
     */
    List<Map<String, Object>> getGenderDistribution();
    
    /**
     * 获取年龄段分布数据
     */
    List<Map<String, Object>> getAgeDistribution();
    
    /**
     * 获取设备类型分布数据
     */
    List<Map<String, Object>> getDeviceDistribution();
    
    /**
     * 获取浏览时间分布数据
     */
    List<Map<String, Object>> getTimeDistribution();
    
    /**
     * 获取热门商品数据
     */
    List<Map<String, Object>> getHotGoods();
    
    /**
     * 获取行为趋势数据
     */
    Map<String, Object> getBehaviorTrend(LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取转化漏斗数据
     */
    List<Map<String, Object>> getConversionFunnel();
    
    /**
     * 获取地域分布数据
     */
    List<Map<String, Object>> getRegionDistribution();
    
    /**
     * 导出图表数据
     */
    void exportCharts(HttpServletResponse response);
}
