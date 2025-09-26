package com.gec.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户画像实体
 */
@Data
@TableName("tbl_user_profile")
public class UserProfile {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String sessionId;
    
    private String gender;
    
    private String ageGroup;
    
    private String preferredCategories;
    
    private String preferredBrands;
    
    private String priceRange;
    
    private String browseTimePreference;
    
    private String purchaseFrequency;
    
    private Integer totalViews;
    
    private Integer totalClicks;
    
    private Integer totalCollections;
    
    private Integer totalCartAdds;
    
    private Integer totalOrders;
    
    private BigDecimal totalAmount;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastVisitTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
