package com.gec.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户行为记录实体
 */
@Data
@TableName("tbl_customer_behavior")
public class CustomerBehavior {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String sessionId;
    
    private String behaviorType;
    
    private String targetType;
    
    private Long targetId;
    
    private String targetName;
    
    private String pageUrl;
    
    private String referrerUrl;
    
    private String userAgent;
    
    private String ipAddress;
    
    private String deviceType;
    
    private String browser;
    
    private String os;
    
    private String extraData;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
