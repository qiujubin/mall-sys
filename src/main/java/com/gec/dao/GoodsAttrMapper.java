package com.gec.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.domain.dto.GoodsAttrDTO;
import com.gec.domain.entity.GoodsAttr;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 规格参数Mapper接口
 */
@Mapper
public interface GoodsAttrMapper extends BaseMapper<GoodsAttr> {
    
    /**
     * 分页查询规格参数列表，包含分组名称
     */
    Page<GoodsAttrDTO> selectPageWithGroupName(Page<GoodsAttrDTO> page, @Param("keyword") String keyword, @Param("categoryId") Long categoryId, @Param("attrType") Integer attrType);
}
