package com.gec.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tbl_user")
public class User extends BaseEntity {
    
    private String account;
    private String password;
    private String nickname;
    private String phone;
    private String gender;
    private String employeeId;
    private Long deptId;
    private Long roleId;
    private Integer status;
}
