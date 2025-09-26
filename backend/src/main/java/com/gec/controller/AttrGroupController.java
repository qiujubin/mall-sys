package com.gec.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.common.PageQuery;
import com.gec.common.R;
import com.gec.domain.entity.GoodsAttrGroup;
import com.gec.dao.GoodsAttrGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 属性分组控制器
 */
@RestController
@RequestMapping("/attr-group")
@CrossOrigin
public class AttrGroupController {
    
    @Autowired
    private GoodsAttrGroupMapper attrGroupMapper;
    
    /**
     * 分页查询属性分组列表
     */
    @PostMapping("/page")
    public R<Page<GoodsAttrGroup>> page(@RequestBody PageQuery pageQuery) {
        Page<GoodsAttrGroup> page = new Page<>(pageQuery.getCurrent(), pageQuery.getSize());
        QueryWrapper<GoodsAttrGroup> queryWrapper = new QueryWrapper<>();
        
        if (pageQuery.getKeyword() != null && !pageQuery.getKeyword().trim().isEmpty()) {
            queryWrapper.like("group_name", pageQuery.getKeyword());
        }
        
        if (pageQuery.getCategoryId() != null) {
            queryWrapper.eq("category_id", pageQuery.getCategoryId());
        }
        
        queryWrapper.orderByDesc("create_time");
        Page<GoodsAttrGroup> result = attrGroupMapper.selectPage(page, queryWrapper);
        return R.success(result);
    }
    
    /**
     * 获取属性分组列表
     */
    @GetMapping("/list")
    public R<List<GoodsAttrGroup>> list(@RequestParam(required = false) Long categoryId) {
        QueryWrapper<GoodsAttrGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }
        
        queryWrapper.orderByAsc("sort_order", "id");
        List<GoodsAttrGroup> list = attrGroupMapper.selectList(queryWrapper);
        return R.success(list);
    }
    
    /**
     * 根据ID查询属性分组
     */
    @GetMapping("/{id}")
    public R<GoodsAttrGroup> getById(@PathVariable Long id) {
        GoodsAttrGroup attrGroup = attrGroupMapper.selectById(id);
        return R.success(attrGroup);
    }
    
    /**
     * 新增属性分组
     */
    @PostMapping
    public R<String> save(@RequestBody GoodsAttrGroup attrGroup) {
        int result = attrGroupMapper.insert(attrGroup);
        return result > 0 ? R.success("新增成功") : R.error("新增失败");
    }
    
    /**
     * 修改属性分组
     */
    @PutMapping
    public R<String> update(@RequestBody GoodsAttrGroup attrGroup) {
        int result = attrGroupMapper.updateById(attrGroup);
        return result > 0 ? R.success("修改成功") : R.error("修改失败");
    }
    
    /**
     * 删除属性分组
     */
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        int result = attrGroupMapper.deleteById(id);
        return result > 0 ? R.success("删除成功") : R.error("删除失败");
    }
}
