package com.gec.service;

import com.gec.entity.CustomerBehavior;
import org.springframework.format.annotation.DateTimeFormat;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 客户行为服务接口
 */
public interface BehaviorService {
    
    /**
     * 追踪客户行为
     */
    void trackBehavior(CustomerBehavior behavior);
    
    /**
     * 获取行为概览数据
     */
    Map<String, Object> getOverview();
    
    /**
     * 获取行为数据列表
     */
    Map<String, Object> getBehaviorList(Integer page, Integer size, String behaviorType, 
                                       String deviceType, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 导出行为数据
     */
    void exportBehaviorData(String behaviorType, String deviceType, 
                           LocalDateTime startTime, LocalDateTime endTime, HttpServletResponse response);
    
    /**
     * 根据ID获取行为详情
     */
    CustomerBehavior getBehaviorById(Long id);
    
    /**
     * 获取实时行为统计
     */
    Map<String, Object> getRealtimeStats();
}
