package com.gec.service.impl;

import com.gec.service.StatisticsService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 统计图表服务实现 - 简化演示版本
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Override
    public List<Map<String, Object>> getBehaviorOverview(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> data = new ArrayList<>();
        
        Map<String, Object> view = new HashMap<>();
        view.put("name", "浏览");
        view.put("value", 1500);
        data.add(view);
        
        Map<String, Object> click = new HashMap<>();
        click.put("name", "点击");
        click.put("value", 800);
        data.add(click);
        
        Map<String, Object> cart = new HashMap<>();
        cart.put("name", "加购物车");
        cart.put("value", 300);
        data.add(cart);
        
        Map<String, Object> order = new HashMap<>();
        order.put("name", "下单");
        order.put("value", 120);
        data.add(order);
        
        Map<String, Object> favorite = new HashMap<>();
        favorite.put("name", "收藏");
        favorite.put("value", 200);
        data.add(favorite);
        
        return data;
    }

    @Override
    public List<Map<String, Object>> getGenderDistribution() {
        List<Map<String, Object>> data = new ArrayList<>();
        
        Map<String, Object> male = new HashMap<>();
        male.put("name", "男性");
        male.put("value", 65);
        data.add(male);
        
        Map<String, Object> female = new HashMap<>();
        female.put("name", "女性");
        female.put("value", 35);
        data.add(female);
        
        return data;
    }

    @Override
    public List<Map<String, Object>> getAgeDistribution() {
        List<Map<String, Object>> data = new ArrayList<>();
        
        String[] ageGroups = {"18-25", "26-35", "36-45", "46-55", "55+"};
        int[] values = {25, 35, 20, 15, 5};
        
        for (int i = 0; i < ageGroups.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", ageGroups[i]);
            item.put("value", values[i]);
            data.add(item);
        }
        
        return data;
    }

    @Override
    public List<Map<String, Object>> getDeviceDistribution() {
        List<Map<String, Object>> data = new ArrayList<>();
        
        Map<String, Object> mobile = new HashMap<>();
        mobile.put("name", "手机");
        mobile.put("value", 60);
        data.add(mobile);
        
        Map<String, Object> desktop = new HashMap<>();
        desktop.put("name", "电脑");
        desktop.put("value", 30);
        data.add(desktop);
        
        Map<String, Object> tablet = new HashMap<>();
        tablet.put("name", "平板");
        tablet.put("value", 10);
        data.add(tablet);
        
        return data;
    }

    @Override
    public List<Map<String, Object>> getTimeDistribution() {
        List<Map<String, Object>> data = new ArrayList<>();
        
        String[] timeSlots = {"00:00-06:00", "06:00-12:00", "12:00-18:00", "18:00-24:00"};
        int[] values = {5, 25, 35, 35};
        
        for (int i = 0; i < timeSlots.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", timeSlots[i]);
            item.put("value", values[i]);
            data.add(item);
        }
        
        return data;
    }

    @Override
    public List<Map<String, Object>> getHotGoods() {
        List<Map<String, Object>> data = new ArrayList<>();
        
        String[] goods = {"iPhone 15", "MacBook Pro", "AirPods", "iPad", "Apple Watch"};
        int[] views = {1200, 800, 600, 500, 400};
        
        for (int i = 0; i < goods.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", goods[i]);
            item.put("value", views[i]);
            data.add(item);
        }
        
        return data;
    }

    @Override
    public Map<String, Object> getBehaviorTrend(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> data = new HashMap<>();
        
        List<String> dates = new ArrayList<>();
        List<Integer> views = new ArrayList<>();
        List<Integer> clicks = new ArrayList<>();
        List<Integer> orders = new ArrayList<>();
        
        // 生成最近7天的数据
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));
            views.add(100 + (int)(Math.random() * 200));
            clicks.add(50 + (int)(Math.random() * 100));
            orders.add(10 + (int)(Math.random() * 30));
        }
        
        data.put("dates", dates);
        data.put("views", views);
        data.put("clicks", clicks);
        data.put("orders", orders);
        
        return data;
    }

    @Override
    public List<Map<String, Object>> getConversionFunnel() {
        List<Map<String, Object>> data = new ArrayList<>();
        
        String[] steps = {"浏览", "点击", "加购物车", "下单", "支付"};
        int[] values = {1000, 600, 300, 120, 100};
        
        for (int i = 0; i < steps.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", steps[i]);
            item.put("value", values[i]);
            data.add(item);
        }
        
        return data;
    }

    @Override
    public List<Map<String, Object>> getRegionDistribution() {
        List<Map<String, Object>> data = new ArrayList<>();
        
        String[] regions = {"北京", "上海", "广州", "深圳", "杭州", "成都", "武汉", "西安"};
        int[] values = {25, 20, 15, 12, 10, 8, 6, 4};
        
        for (int i = 0; i < regions.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", regions[i]);
            item.put("value", values[i]);
            data.add(item);
        }
        
        return data;
    }

    @Override
    public void exportCharts(HttpServletResponse response) {
        try {
            response.setContentType("text/csv;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=chart_data.csv");
            
            StringBuilder csv = new StringBuilder();
            csv.append("图表类型,数据项,数值\n");
            
            // 行为概览数据
            List<Map<String, Object>> behaviorData = getBehaviorOverview(null, null);
            for (Map<String, Object> item : behaviorData) {
                csv.append("行为概览,").append(item.get("name")).append(",").append(item.get("value")).append("\n");
            }
            
            // 性别分布数据
            List<Map<String, Object>> genderData = getGenderDistribution();
            for (Map<String, Object> item : genderData) {
                csv.append("性别分布,").append(item.get("name")).append(",").append(item.get("value")).append("\n");
            }
            
            response.getWriter().write(csv.toString());
        } catch (Exception e) {
            throw new RuntimeException("导出图表数据失败", e);
        }
    }
}