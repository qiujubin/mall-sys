package com.gec.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.common.PageQuery;
import com.gec.common.R;
import com.gec.domain.entity.GoodsAttr;
import com.gec.dao.GoodsAttrMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 销售属性控制器
 */
@RestController
@RequestMapping("/sale-attr")
@CrossOrigin
public class SaleAttrController {
    
    @Autowired
    private GoodsAttrMapper attrMapper;
    
    /**
     * 分页查询销售属性列表
     */
    @PostMapping("/page")
    public R<Page<GoodsAttr>> page(@RequestBody PageQuery pageQuery) {
        Page<GoodsAttr> page = new Page<>(pageQuery.getCurrent(), pageQuery.getSize());
        QueryWrapper<GoodsAttr> queryWrapper = new QueryWrapper<>();
        
        if (pageQuery.getKeyword() != null && !pageQuery.getKeyword().trim().isEmpty()) {
            queryWrapper.like("attr_name", pageQuery.getKeyword());
        }
        
        if (pageQuery.getCategoryId() != null) {
            queryWrapper.eq("category_id", pageQuery.getCategoryId());
        }
        
        queryWrapper.eq("attr_type", 2) // 销售属性
                   .orderByDesc("create_time");
        Page<GoodsAttr> result = attrMapper.selectPage(page, queryWrapper);
        return R.success(result);
    }
    
    /**
     * 获取销售属性列表
     */
    @GetMapping("/list")
    public R<List<GoodsAttr>> list(@RequestParam(required = false) Long categoryId) {
        QueryWrapper<GoodsAttr> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1)
                   .eq("attr_type", 2); // 销售属性
        
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }
        
        queryWrapper.orderByAsc("id");
        List<GoodsAttr> list = attrMapper.selectList(queryWrapper);
        return R.success(list);
    }
    
    /**
     * 根据ID查询销售属性
     */
    @GetMapping("/{id}")
    public R<GoodsAttr> getById(@PathVariable Long id) {
        GoodsAttr attr = attrMapper.selectById(id);
        return R.success(attr);
    }
    
    /**
     * 新增销售属性
     */
    @PostMapping
    public R<String> save(@RequestBody GoodsAttr attr) {
        attr.setAttrType(2); // 销售属性
        int result = attrMapper.insert(attr);
        return result > 0 ? R.success("新增成功") : R.error("新增失败");
    }
    
    /**
     * 修改销售属性
     */
    @PutMapping
    public R<String> update(@RequestBody GoodsAttr attr) {
        int result = attrMapper.updateById(attr);
        return result > 0 ? R.success("修改成功") : R.error("修改失败");
    }
    
    /**
     * 删除销售属性
     */
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        int result = attrMapper.deleteById(id);
        return result > 0 ? R.success("删除成功") : R.error("删除失败");
    }
}
