package com.gec.controller;

import com.gec.service.StatisticsService;
import com.gec.common.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 统计图表控制器
 */
@RestController
@RequestMapping("/statistics")
@CrossOrigin
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取行为概览数据
     */
    @GetMapping("/behavior-overview")
    public R getBehaviorOverview(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            List<Map<String, Object>> data = statisticsService.getBehaviorOverview(startDate, endDate);
            return R.success(data);
        } catch (Exception e) {
            return R.error("获取行为概览失败: " + e.getMessage());
        }
    }

    /**
     * 获取性别分布数据
     */
    @GetMapping("/gender-distribution")
    public R getGenderDistribution() {
        try {
            List<Map<String, Object>> data = statisticsService.getGenderDistribution();
            return R.success(data);
        } catch (Exception e) {
            return R.error("获取性别分布失败: " + e.getMessage());
        }
    }

    /**
     * 获取年龄段分布数据
     */
    @GetMapping("/age-distribution")
    public R getAgeDistribution() {
        try {
            List<Map<String, Object>> data = statisticsService.getAgeDistribution();
            return R.success(data);
        } catch (Exception e) {
            return R.error("获取年龄段分布失败: " + e.getMessage());
        }
    }

    /**
     * 获取设备类型分布数据
     */
    @GetMapping("/device-distribution")
    public R getDeviceDistribution() {
        try {
            List<Map<String, Object>> data = statisticsService.getDeviceDistribution();
            return R.success(data);
        } catch (Exception e) {
            return R.error("获取设备类型分布失败: " + e.getMessage());
        }
    }

    /**
     * 获取浏览时间分布数据
     */
    @GetMapping("/time-distribution")
    public R getTimeDistribution() {
        try {
            List<Map<String, Object>> data = statisticsService.getTimeDistribution();
            return R.success(data);
        } catch (Exception e) {
            return R.error("获取浏览时间分布失败: " + e.getMessage());
        }
    }

    /**
     * 获取热门商品数据
     */
    @GetMapping("/hot-goods")
    public R getHotGoods() {
        try {
            List<Map<String, Object>> data = statisticsService.getHotGoods();
            return R.success(data);
        } catch (Exception e) {
            return R.error("获取热门商品失败: " + e.getMessage());
        }
    }

    /**
     * 获取行为趋势数据
     */
    @GetMapping("/behavior-trend")
    public R getBehaviorTrend(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            Map<String, Object> data = statisticsService.getBehaviorTrend(startDate, endDate);
            return R.success(data);
        } catch (Exception e) {
            return R.error("获取行为趋势失败: " + e.getMessage());
        }
    }

    /**
     * 获取转化漏斗数据
     */
    @GetMapping("/conversion-funnel")
    public R getConversionFunnel() {
        try {
            List<Map<String, Object>> data = statisticsService.getConversionFunnel();
            return R.success(data);
        } catch (Exception e) {
            return R.error("获取转化漏斗失败: " + e.getMessage());
        }
    }

    /**
     * 获取地域分布数据
     */
    @GetMapping("/region-distribution")
    public R getRegionDistribution() {
        try {
            List<Map<String, Object>> data = statisticsService.getRegionDistribution();
            return R.success(data);
        } catch (Exception e) {
            return R.error("获取地域分布失败: " + e.getMessage());
        }
    }

    /**
     * 导出图表数据
     */
    @GetMapping("/export-charts")
    public void exportCharts(HttpServletResponse response) {
        try {
            statisticsService.exportCharts(response);
        } catch (Exception e) {
            throw new RuntimeException("导出图表失败: " + e.getMessage());
        }
    }
}
