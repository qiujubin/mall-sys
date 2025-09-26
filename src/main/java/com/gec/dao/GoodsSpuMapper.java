package com.gec.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.domain.entity.GoodsSpu;
import com.gec.domain.vo.GoodsSpuVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品SPU Mapper接口
 */
@Mapper
public interface GoodsSpuMapper extends BaseMapper<GoodsSpu> {
    
    /**
     * 分页查询商品列表（包含品牌和分类名称）
     */
    Page<GoodsSpuVO> selectPageWithNames(Page<GoodsSpuVO> page, 
                                        @Param("keyword") String keyword,
                                        @Param("brandId") Long brandId,
                                        @Param("categoryId") Long categoryId);
}
