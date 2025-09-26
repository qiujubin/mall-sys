package com.gec.controller;

import com.gec.entity.CustomerBehavior;
import com.gec.service.BehaviorService;
import com.gec.common.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 客户行为追踪控制器
 */
@RestController
@RequestMapping("/behavior")
@CrossOrigin
public class BehaviorController {

    @Autowired
    private BehaviorService behaviorService;

    /**
     * 追踪客户行为
     */
    @PostMapping("/track")
    public R trackBehavior(@RequestBody CustomerBehavior behavior) {
        try {
            behaviorService.trackBehavior(behavior);
            return R.success("行为追踪成功");
        } catch (Exception e) {
            return R.error("行为追踪失败: " + e.getMessage());
        }
    }

    /**
     * 获取行为概览数据
     */
    @GetMapping("/overview")
    public R getOverview() {
        try {
            Map<String, Object> overview = behaviorService.getOverview();
            return R.success(overview);
        } catch (Exception e) {
            return R.error("获取概览数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取行为数据列表
     */
    @GetMapping("/list")
    public R getBehaviorList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String behaviorType,
            @RequestParam(required = false) String deviceType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        try {
            Map<String, Object> result = behaviorService.getBehaviorList(page, size, behaviorType, deviceType, startTime, endTime);
            return R.success(result);
        } catch (Exception e) {
            return R.error("获取行为数据失败: " + e.getMessage());
        }
    }

    /**
     * 导出行为数据
     */
    @GetMapping("/export")
    public void exportBehaviorData(
            @RequestParam(required = false) String behaviorType,
            @RequestParam(required = false) String deviceType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            javax.servlet.http.HttpServletResponse response) {
        try {
            behaviorService.exportBehaviorData(behaviorType, deviceType, startTime, endTime, response);
        } catch (Exception e) {
            throw new RuntimeException("导出数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取行为详情
     */
    @GetMapping("/{id}")
    public R getBehaviorDetail(@PathVariable Long id) {
        try {
            CustomerBehavior behavior = behaviorService.getBehaviorById(id);
            return R.success(behavior);
        } catch (Exception e) {
            return R.error("获取行为详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取实时行为统计
     */
    @GetMapping("/realtime")
    public R getRealtimeStats() {
        try {
            Map<String, Object> stats = behaviorService.getRealtimeStats();
            return R.success(stats);
        } catch (Exception e) {
            return R.error("获取实时统计失败: " + e.getMessage());
        }
    }
}
