package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 部门实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tbl_dept")
public class Dept extends BaseEntity {
    
    private String deptName;
    private Long parentId;
    private Integer level;
    private Integer sortOrder;
    private Integer status;
    
    // 子部门列表，用于树形结构
    @TableField(exist = false)
    private List<Dept> children;
}
