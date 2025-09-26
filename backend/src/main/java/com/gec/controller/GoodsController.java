package com.gec.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.common.PageQuery;
import com.gec.common.R;
import com.gec.domain.entity.GoodsSpu;
import com.gec.domain.vo.GoodsSpuVO;
import com.gec.dao.GoodsSpuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/goods")
@CrossOrigin
public class GoodsController {
    
    @Autowired
    private GoodsSpuMapper goodsSpuMapper;
    
    /**
     * 分页查询商品列表
     */
    @PostMapping("/page")
    public R<Page<GoodsSpuVO>> page(@RequestBody PageQuery pageQuery) {
        Page<GoodsSpuVO> page = new Page<>(pageQuery.getCurrent(), pageQuery.getSize());
        Page<GoodsSpuVO> result = goodsSpuMapper.selectPageWithNames(page, 
            pageQuery.getKeyword(), 
            pageQuery.getBrandId(), 
            pageQuery.getCategoryId());
        return R.success(result);
    }
    
    /**
     * 获取商品列表
     */
    @GetMapping("/list")
    public R<List<GoodsSpu>> list() {
        QueryWrapper<GoodsSpu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1)
                   .orderByDesc("create_time");
        List<GoodsSpu> list = goodsSpuMapper.selectList(queryWrapper);
        return R.success(list);
    }
    
    /**
     * 根据ID查询商品
     */
    @GetMapping("/{id}")
    public R<GoodsSpu> getById(@PathVariable Long id) {
        GoodsSpu goods = goodsSpuMapper.selectById(id);
        return R.success(goods);
    }
    
    /**
     * 新增商品
     */
    @PostMapping
    public R<String> save(@RequestBody GoodsSpu goods) {
        int result = goodsSpuMapper.insert(goods);
        return result > 0 ? R.success("新增成功") : R.error("新增失败");
    }
    
    /**
     * 修改商品
     */
    @PutMapping
    public R<String> update(@RequestBody GoodsSpu goods) {
        int result = goodsSpuMapper.updateById(goods);
        return result > 0 ? R.success("修改成功") : R.error("修改失败");
    }
    
    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        int result = goodsSpuMapper.deleteById(id);
        return result > 0 ? R.success("删除成功") : R.error("删除失败");
    }
    
    /**
     * 发布商品
     */
    @PutMapping("/publish/{id}")
    public R<String> publish(@PathVariable Long id) {
        GoodsSpu goods = goodsSpuMapper.selectById(id);
        if (goods != null) {
            goods.setStatus(1); // 启用状态
            int result = goodsSpuMapper.updateById(goods);
            return result > 0 ? R.success("发布成功") : R.error("发布失败");
        }
        return R.error("商品不存在");
    }
    
    /**
     * 下架商品
     */
    @PutMapping("/offline/{id}")
    public R<String> offline(@PathVariable Long id) {
        GoodsSpu goods = goodsSpuMapper.selectById(id);
        if (goods != null) {
            goods.setStatus(0); // 禁用状态
            int result = goodsSpuMapper.updateById(goods);
            return result > 0 ? R.success("下架成功") : R.error("下架失败");
        }
        return R.error("商品不存在");
    }
}
