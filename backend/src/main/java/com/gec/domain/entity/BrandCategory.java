package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 品牌类别关联实体类
 */
@Data
@TableName("tbl_brand_category")
public class BrandCategory {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long brandId;
    private String brandName;
    private Long categoryId;
    private String categoryName;
    
    private LocalDateTime createTime;
}
