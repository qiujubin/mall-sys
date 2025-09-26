package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 商品类别实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tbl_goods_category")
public class Category extends BaseEntity {
    
    private String categoryName;
    private Long parentId;
    private Integer level;
    private Integer sortOrder;
    private Integer status;
    
    // 子类别列表，用于树形结构
    @TableField(exist = false)
    private List<Category> children;
}
