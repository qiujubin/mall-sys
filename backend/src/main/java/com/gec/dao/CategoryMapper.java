package com.gec.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gec.domain.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品类别Mapper接口
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
