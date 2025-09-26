package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 品牌实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tbl_brand")
public class Brand extends BaseEntity {
    
    private String brandName;
    private String brandLogo;
    private String brandDesc;
    private Integer status;
}
