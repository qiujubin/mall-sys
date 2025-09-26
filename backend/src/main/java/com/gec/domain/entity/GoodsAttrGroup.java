package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 属性分组实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tbl_goods_attr_group")
public class GoodsAttrGroup extends BaseEntity {
    
    private String groupName;
    private Long categoryId;
    private String icon;
    private Integer sortOrder;
    private Integer searchable;
    private Integer status;
}
