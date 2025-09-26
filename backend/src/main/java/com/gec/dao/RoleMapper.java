package com.gec.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gec.domain.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色Mapper接口
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}
