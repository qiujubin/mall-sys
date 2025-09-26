package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品SPU实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tbl_goods_spu")
public class GoodsSpu extends BaseEntity {
    
    private String spuName;
    private String spuDesc;
    private Long categoryId;
    private Long brandId;
    private Integer status;
    private String spuImages;
}
