package com.gec.domain.vo;

import lombok.Data;

/**
 * 商品SPU视图对象
 */
@Data
public class GoodsSpuVO {
    
    private Long id;
    private String spuName;
    private String spuDesc;
    private String spuImages;
    private Long categoryId;
    private String categoryName;
    private Long brandId;
    private String brandName;
    private Integer status;
    private String createTime;
    private String updateTime;
}
