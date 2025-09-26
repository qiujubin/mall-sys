package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tbl_role")
public class Role extends BaseEntity {
    
    private String roleName;
    private String roleDesc;
    private Integer status;
}
