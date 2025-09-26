package com.gec.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.common.PageQuery;
import com.gec.common.R;
import com.gec.domain.entity.Brand;
import com.gec.domain.entity.Category;
import com.gec.domain.entity.GoodsSpu;
import com.gec.dao.BrandMapper;
import com.gec.dao.CategoryMapper;
import com.gec.dao.GoodsSpuMapper;
import com.gec.dao.BrandCategoryMapper;
import com.gec.domain.entity.BrandCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 品牌控制器
 */
@RestController
@RequestMapping("/brand")
@CrossOrigin
public class BrandController {
    
    @Autowired
    private BrandMapper brandMapper;
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Autowired
    private GoodsSpuMapper goodsSpuMapper;
    
    @Autowired
    private BrandCategoryMapper brandCategoryMapper;
    
    /**
     * 分页查询品牌列表
     */
    @PostMapping("/page")
    public R<Page<Brand>> page(@RequestBody PageQuery pageQuery) {
        Page<Brand> page = new Page<>(pageQuery.getCurrent(), pageQuery.getSize());
        QueryWrapper<Brand> queryWrapper = new QueryWrapper<>();
        
        if (pageQuery.getKeyword() != null && !pageQuery.getKeyword().trim().isEmpty()) {
            queryWrapper.like("brand_name", pageQuery.getKeyword());
        }
        
        queryWrapper.orderByDesc("create_time");
        Page<Brand> result = brandMapper.selectPage(page, queryWrapper);
        return R.success(result);
    }
    
    /**
     * 获取品牌列表
     */
    @GetMapping("/list")
    public R<List<Brand>> list() {
        QueryWrapper<Brand> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1)
                   .orderByAsc("brand_name");
        List<Brand> list = brandMapper.selectList(queryWrapper);
        return R.success(list);
    }
    
    /**
     * 根据ID查询品牌
     */
    @GetMapping("/{id}")
    public R<Brand> getById(@PathVariable Long id) {
        Brand brand = brandMapper.selectById(id);
        return R.success(brand);
    }
    
    /**
     * 新增品牌
     */
    @PostMapping
    public R<String> save(@RequestBody Brand brand) {
        int result = brandMapper.insert(brand);
        return result > 0 ? R.success("新增成功") : R.error("新增失败");
    }
    
    /**
     * 修改品牌
     */
    @PutMapping
    public R<String> update(@RequestBody Brand brand) {
        int result = brandMapper.updateById(brand);
        return result > 0 ? R.success("修改成功") : R.error("修改失败");
    }
    
    /**
     * 删除品牌
     */
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        // 检查是否有关联的类别
        QueryWrapper<BrandCategory> brandCategoryWrapper = new QueryWrapper<>();
        brandCategoryWrapper.eq("brand_id", id);
        Long categoryCount = brandCategoryMapper.selectCount(brandCategoryWrapper);
        if (categoryCount > 0) {
            return R.error("该品牌下有关联数据，不能删除");
        }
        
        // 检查是否有关联的商品
        QueryWrapper<GoodsSpu> goodsWrapper = new QueryWrapper<>();
        goodsWrapper.eq("brand_id", id);
        Long goodsCount = goodsSpuMapper.selectCount(goodsWrapper);
        if (goodsCount > 0) {
            return R.error("该品牌下有关联数据，不能删除");
        }
        
        int result = brandMapper.deleteById(id);
        return result > 0 ? R.success("删除成功") : R.error("删除失败");
    }
}
