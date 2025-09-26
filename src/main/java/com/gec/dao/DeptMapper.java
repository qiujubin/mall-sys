package com.gec.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gec.domain.entity.Dept;
import org.apache.ibatis.annotations.Mapper;

/**
 * 部门Mapper接口
 */
@Mapper
public interface DeptMapper extends BaseMapper<Dept> {
}
