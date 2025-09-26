package com.gec.service.impl;

import com.gec.entity.CustomerBehavior;
import com.gec.service.BehaviorService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 客户行为服务实现 - 简化演示版本
 */
@Service
public class BehaviorServiceImpl implements BehaviorService {

    // 模拟数据存储
    private static List<CustomerBehavior> mockData = new ArrayList<>();
    private static long nextId = 1;

    static {
        // 初始化一些模拟数据
        initMockData();
    }

    private static void initMockData() {
        String[] behaviorTypes = {"view", "click", "cart", "order", "favorite"};
        String[] deviceTypes = {"desktop", "mobile", "tablet"};
        String[] browsers = {"Chrome", "Firefox", "Safari", "Edge"};
        String[] os = {"Windows", "macOS", "iOS", "Android"};
        
        for (int i = 0; i < 50; i++) {
            CustomerBehavior behavior = new CustomerBehavior();
            behavior.setId(nextId++);
            behavior.setUserId((long) (Math.random() * 20 + 1));
            behavior.setSessionId("session_" + (i + 1));
            behavior.setBehaviorType(behaviorTypes[(int) (Math.random() * behaviorTypes.length)]);
            behavior.setTargetType("goods");
            behavior.setTargetId((long) (Math.random() * 100 + 1));
            behavior.setTargetName("商品" + (i + 1));
            behavior.setPageUrl("/goods/detail/" + (i + 1));
            behavior.setReferrerUrl("/goods/list");
            behavior.setUserAgent("Mozilla/5.0...");
            behavior.setIpAddress("192.168.1." + (i % 255 + 1));
            behavior.setDeviceType(deviceTypes[(int) (Math.random() * deviceTypes.length)]);
            behavior.setBrowser(browsers[(int) (Math.random() * browsers.length)]);
            behavior.setOs(os[(int) (Math.random() * os.length)]);
            behavior.setExtraData("{\"source\":\"search\"}");
            behavior.setCreateTime(LocalDateTime.now().minusHours((long) (Math.random() * 24)));
            mockData.add(behavior);
        }
    }

    @Override
    public void trackBehavior(CustomerBehavior behavior) {
        behavior.setId(nextId++);
        behavior.setCreateTime(LocalDateTime.now());
        mockData.add(behavior);
    }

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        // 总浏览量
        long totalViews = mockData.stream()
            .filter(b -> "view".equals(b.getBehaviorType()))
            .count();
        overview.put("totalViews", totalViews);
        
        // 总点击量
        long totalClicks = mockData.stream()
            .filter(b -> "click".equals(b.getBehaviorType()))
            .count();
        overview.put("totalClicks", totalClicks);
        
        // 总加购物车量
        long totalCarts = mockData.stream()
            .filter(b -> "cart".equals(b.getBehaviorType()))
            .count();
        overview.put("totalCarts", totalCarts);
        
        // 总订单量
        long totalOrders = mockData.stream()
            .filter(b -> "order".equals(b.getBehaviorType()))
            .count();
        overview.put("totalOrders", totalOrders);
        
        // 总收藏量
        long totalFavorites = mockData.stream()
            .filter(b -> "favorite".equals(b.getBehaviorType()))
            .count();
        overview.put("totalFavorites", totalFavorites);
        
        // 总用户数
        long totalUsers = mockData.stream()
            .map(CustomerBehavior::getUserId)
            .distinct()
            .count();
        overview.put("totalUsers", totalUsers);
        
        // 今日新增
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        long todayNew = mockData.stream()
            .filter(b -> b.getCreateTime().isAfter(today))
            .count();
        overview.put("todayNew", todayNew);
        
        return overview;
    }

    @Override
    public Map<String, Object> getBehaviorList(Integer page, Integer size, String behaviorType, 
                                             String deviceType, LocalDateTime startTime, LocalDateTime endTime) {
        List<CustomerBehavior> filteredData = new ArrayList<>(mockData);
        
        // 过滤条件
        if (behaviorType != null && !behaviorType.isEmpty()) {
            filteredData = filteredData.stream()
                .filter(b -> behaviorType.equals(b.getBehaviorType()))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }
        
        if (deviceType != null && !deviceType.isEmpty()) {
            filteredData = filteredData.stream()
                .filter(b -> deviceType.equals(b.getDeviceType()))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }
        
        if (startTime != null) {
            filteredData = filteredData.stream()
                .filter(b -> b.getCreateTime().isAfter(startTime))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }
        
        if (endTime != null) {
            filteredData = filteredData.stream()
                .filter(b -> b.getCreateTime().isBefore(endTime))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }
        
        // 按时间倒序排序
        filteredData.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));
        
        // 分页
        int total = filteredData.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        
        List<CustomerBehavior> pageData = new ArrayList<>();
        if (start < total) {
            pageData = filteredData.subList(start, end);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", pageData);
        result.put("total", total);
        result.put("current", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size);
        
        return result;
    }

    @Override
    public void exportBehaviorData(String behaviorType, String deviceType, 
                                 LocalDateTime startTime, LocalDateTime endTime, 
                                 HttpServletResponse response) {
        // 简化的导出实现
        try {
            response.setContentType("text/csv;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=behavior_data.csv");
            
            StringBuilder csv = new StringBuilder();
            csv.append("ID,用户ID,行为类型,目标名称,设备类型,创建时间\n");
            
            List<CustomerBehavior> filteredData = new ArrayList<>(mockData);
            if (behaviorType != null && !behaviorType.isEmpty()) {
                filteredData = filteredData.stream()
                    .filter(b -> behaviorType.equals(b.getBehaviorType()))
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            }
            
            for (CustomerBehavior behavior : filteredData) {
                csv.append(behavior.getId()).append(",")
                   .append(behavior.getUserId()).append(",")
                   .append(behavior.getBehaviorType()).append(",")
                   .append(behavior.getTargetName()).append(",")
                   .append(behavior.getDeviceType()).append(",")
                   .append(behavior.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                   .append("\n");
            }
            
            response.getWriter().write(csv.toString());
        } catch (Exception e) {
            throw new RuntimeException("导出失败", e);
        }
    }

    @Override
    public CustomerBehavior getBehaviorById(Long id) {
        return mockData.stream()
            .filter(b -> id.equals(b.getId()))
            .findFirst()
            .orElse(null);
    }

    @Override
    public Map<String, Object> getRealtimeStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 最近1小时的数据
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long recentViews = mockData.stream()
            .filter(b -> b.getCreateTime().isAfter(oneHourAgo) && "view".equals(b.getBehaviorType()))
            .count();
        stats.put("recentViews", recentViews);
        
        long recentClicks = mockData.stream()
            .filter(b -> b.getCreateTime().isAfter(oneHourAgo) && "click".equals(b.getBehaviorType()))
            .count();
        stats.put("recentClicks", recentClicks);
        
        long recentCarts = mockData.stream()
            .filter(b -> b.getCreateTime().isAfter(oneHourAgo) && "cart".equals(b.getBehaviorType()))
            .count();
        stats.put("recentCarts", recentCarts);
        
        long recentOrders = mockData.stream()
            .filter(b -> b.getCreateTime().isAfter(oneHourAgo) && "order".equals(b.getBehaviorType()))
            .count();
        stats.put("recentOrders", recentOrders);
        
        return stats;
    }
}