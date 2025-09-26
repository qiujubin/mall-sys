package com.gec.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gec.domain.entity.BrandCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BrandCategoryMapper extends BaseMapper<BrandCategory> {
    
    /**
     * 根据类别ID删除关联关系
     */
    int deleteByCategoryId(@Param("categoryId") Long categoryId);
    
    /**
     * 根据品牌ID删除关联关系
     */
    int deleteByBrandId(@Param("brandId") Long brandId);
    
    /**
     * 批量插入关联关系
     */
    int batchInsert(@Param("list") List<BrandCategory> list);
}
