package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 规格参数实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tbl_goods_attr")
public class GoodsAttr extends BaseEntity {
    
    private String attrName;
    private Long categoryId;
    private Long attrGroupId;
    private Integer attrType;
    private Integer valueType;
    private String attrValue;
    private Integer searchable;
    private Integer status;
}
