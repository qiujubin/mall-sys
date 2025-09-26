package com.gec.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.common.PageQuery;
import com.gec.common.R;
import com.gec.domain.dto.GoodsAttrDTO;
import com.gec.domain.entity.GoodsAttr;
import com.gec.dao.GoodsAttrMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 规格参数控制器
 */
@RestController
@RequestMapping("/attr")
@CrossOrigin
public class AttrController {
    
    @Autowired
    private GoodsAttrMapper attrMapper;
    
    /**
     * 分页查询规格参数列表
     */
    @PostMapping("/page")
    public R<Page<GoodsAttrDTO>> page(@RequestBody PageQuery pageQuery) {
        Page<GoodsAttrDTO> page = new Page<>(pageQuery.getCurrent(), pageQuery.getSize());
        Page<GoodsAttrDTO> result = attrMapper.selectPageWithGroupName(page, pageQuery.getKeyword(), pageQuery.getCategoryId(), 1);
        return R.success(result);
    }
    
    /**
     * 获取规格参数列表
     */
    @GetMapping("/list")
    public R<List<GoodsAttr>> list(@RequestParam(required = false) Long categoryId) {
        QueryWrapper<GoodsAttr> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1)
                   .eq("attr_type", 1); // 基本属性
        
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }
        
        queryWrapper.orderByAsc("id");
        List<GoodsAttr> list = attrMapper.selectList(queryWrapper);
        return R.success(list);
    }
    
    /**
     * 根据ID查询规格参数
     */
    @GetMapping("/{id}")
    public R<GoodsAttr> getById(@PathVariable Long id) {
        GoodsAttr attr = attrMapper.selectById(id);
        return R.success(attr);
    }
    
    /**
     * 新增规格参数
     */
    @PostMapping
    public R<String> save(@RequestBody GoodsAttr attr) {
        attr.setAttrType(1); // 基本属性
        int result = attrMapper.insert(attr);
        return result > 0 ? R.success("新增成功") : R.error("新增失败");
    }
    
    /**
     * 修改规格参数
     */
    @PutMapping
    public R<String> update(@RequestBody GoodsAttr attr) {
        int result = attrMapper.updateById(attr);
        return result > 0 ? R.success("修改成功") : R.error("修改失败");
    }
    
    /**
     * 删除规格参数
     */
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        int result = attrMapper.deleteById(id);
        return result > 0 ? R.success("删除成功") : R.error("删除失败");
    }
}
