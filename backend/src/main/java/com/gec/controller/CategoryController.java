package com.gec.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gec.common.R;
import com.gec.domain.entity.Category;
import com.gec.domain.entity.Brand;
import com.gec.domain.entity.BrandCategory;
import com.gec.domain.entity.GoodsAttrGroup;
import com.gec.domain.entity.GoodsAttr;
import com.gec.domain.entity.GoodsSpu;
import com.gec.dao.CategoryMapper;
import com.gec.dao.BrandCategoryMapper;
import com.gec.dao.BrandMapper;
import com.gec.dao.GoodsAttrGroupMapper;
import com.gec.dao.GoodsAttrMapper;
import com.gec.dao.GoodsSpuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

/**
 * 商品类别控制器
 */
@RestController
@RequestMapping("/category")
@CrossOrigin
public class CategoryController {
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Autowired
    private BrandCategoryMapper brandCategoryMapper;
    
    @Autowired
    private BrandMapper brandMapper;
    
    @Autowired
    private GoodsAttrGroupMapper goodsAttrGroupMapper;
    
    @Autowired
    private GoodsAttrMapper goodsAttrMapper;
    
    @Autowired
    private GoodsSpuMapper goodsSpuMapper;
    
    /**
     * 获取类别树形结构
     */
    @GetMapping("/tree")
    public R<List<Category>> getCategoryTree() {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1)
                   .orderByAsc("sort_order", "id");
        List<Category> categoryList = categoryMapper.selectList(queryWrapper);
        
        // 构建树形结构
        List<Category> tree = buildTree(categoryList, 0L);
        return R.success(tree);
    }
    
    /**
     * 获取类别列表
     */
    @GetMapping("/list")
    public R<List<Category>> getCategoryList() {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1)
                   .orderByAsc("sort_order", "id");
        List<Category> list = categoryMapper.selectList(queryWrapper);
        return R.success(list);
    }
    
    /**
     * 根据ID查询类别
     */
    @GetMapping("/{id}")
    public R<Category> getById(@PathVariable Long id) {
        Category category = categoryMapper.selectById(id);
        return R.success(category);
    }
    
    /**
     * 新增类别
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        // 设置层级
        if (category.getParentId() == null || category.getParentId() == 0) {
            category.setLevel(1);
        } else {
            Category parentCategory = categoryMapper.selectById(category.getParentId());
            if (parentCategory == null) {
                return R.error("父类别不存在");
            }
            // 检查是否超过3级
            if (parentCategory.getLevel() >= 3) {
                return R.error("类别最多只能有3级");
            }
            category.setLevel(parentCategory.getLevel() + 1);
        }
        
        int result = categoryMapper.insert(category);
        return result > 0 ? R.success("新增成功") : R.error("新增失败");
    }
    
    /**
     * 修改类别
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        // 验证父节点ID关联
        if (category.getParentId() != null && category.getParentId() != 0) {
            // 不能将自己设为自己的父节点
            if (category.getId().equals(category.getParentId())) {
                return R.error("不能将自己设为父节点");
            }
            
            // 检查是否会导致循环引用
            if (isCircularReference(category.getId(), category.getParentId())) {
                return R.error("不能将子节点设为父节点，会导致循环引用");
            }
            
            // 检查是否超过3级
            Category parentCategory = categoryMapper.selectById(category.getParentId());
            if (parentCategory != null && parentCategory.getLevel() >= 3) {
                return R.error("类别最多只能有3级");
            }
        }
        
        int result = categoryMapper.updateById(category);
        return result > 0 ? R.success("修改成功") : R.error("修改失败");
    }
    
    /**
     * 删除类别
     */
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        // 检查是否有子类别
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        Long count = categoryMapper.selectCount(queryWrapper);
        if (count > 0) {
            return R.error("该类别下有子类别，不能删除");
        }
        
        // 检查是否有关联的品牌
        QueryWrapper<BrandCategory> brandQueryWrapper = new QueryWrapper<>();
        brandQueryWrapper.eq("category_id", id);
        Long brandCount = brandCategoryMapper.selectCount(brandQueryWrapper);
        if (brandCount > 0) {
            return R.error("该类别下有关联的品牌数据，不能删除");
        }
        
        // 检查是否有关联的属性分组
        QueryWrapper<GoodsAttrGroup> attrGroupQueryWrapper = new QueryWrapper<>();
        attrGroupQueryWrapper.eq("category_id", id);
        Long attrGroupCount = goodsAttrGroupMapper.selectCount(attrGroupQueryWrapper);
        if (attrGroupCount > 0) {
            return R.error("该类别下有关联的属性分组数据，不能删除");
        }
        
        // 检查是否有关联的规格参数
        QueryWrapper<GoodsAttr> attrQueryWrapper = new QueryWrapper<>();
        attrQueryWrapper.eq("category_id", id);
        Long attrCount = goodsAttrMapper.selectCount(attrQueryWrapper);
        if (attrCount > 0) {
            return R.error("该类别下有关联的规格参数数据，不能删除");
        }
        
        // 检查是否有关联的商品SPU
        QueryWrapper<GoodsSpu> spuQueryWrapper = new QueryWrapper<>();
        spuQueryWrapper.eq("category_id", id);
        Long spuCount = goodsSpuMapper.selectCount(spuQueryWrapper);
        if (spuCount > 0) {
            return R.error("该类别下有关联的商品数据，不能删除");
        }
        
        int result = categoryMapper.deleteById(id);
        return result > 0 ? R.success("删除成功") : R.error("删除失败");
    }
    
    /**
     * 品牌类别关联
     */
    @PostMapping("/brand-relation")
    public R<String> brandRelation(@RequestBody BrandCategoryRelation relation) {
        try {
            // 先删除该类别下的所有品牌关联
            brandCategoryMapper.deleteByCategoryId(relation.getCategoryId());
            
            // 如果有新的品牌关联，则添加
            if (relation.getBrandIds() != null && !relation.getBrandIds().isEmpty()) {
                // 获取类别信息
                Category category = categoryMapper.selectById(relation.getCategoryId());
                if (category == null) {
                    return R.error("类别不存在");
                }
                
                // 构建品牌关联列表
                List<BrandCategory> brandCategoryList = new ArrayList<>();
                for (Long brandId : relation.getBrandIds()) {
                    // 根据brandId获取品牌信息
                    Brand brand = brandMapper.selectById(brandId);
                    if (brand != null) {
                        BrandCategory brandCategory = new BrandCategory();
                        brandCategory.setBrandId(brandId);
                        brandCategory.setBrandName(brand.getBrandName());
                        brandCategory.setCategoryId(relation.getCategoryId());
                        brandCategory.setCategoryName(category.getCategoryName());
                        brandCategoryList.add(brandCategory);
                    }
                }
                
                // 批量插入关联关系
                brandCategoryMapper.batchInsert(brandCategoryList);
            }
            
            return R.success("关联成功");
        } catch (Exception e) {
            return R.error("关联失败：" + e.getMessage());
        }
    }
    
    /**
     * 构建树形结构
     */
    private List<Category> buildTree(List<Category> categoryList, Long parentId) {
        return categoryList.stream()
                .filter(category -> category.getParentId().equals(parentId))
                .peek(category -> category.setChildren(buildTree(categoryList, category.getId())))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 检查是否会导致循环引用
     */
    private boolean isCircularReference(Long categoryId, Long parentId) {
        // 获取所有类别
        List<Category> allCategories = categoryMapper.selectList(null);
        
        // 检查parentId是否是categoryId的子节点
        return isChildNode(categoryId, parentId, allCategories);
    }
    
    /**
     * 递归检查parentId是否是categoryId的子节点
     */
    private boolean isChildNode(Long categoryId, Long parentId, List<Category> allCategories) {
        // 查找parentId的所有子节点
        List<Category> children = allCategories.stream()
                .filter(category -> parentId.equals(category.getParentId()))
                .collect(java.util.stream.Collectors.toList());
        
        // 检查子节点中是否包含categoryId
        for (Category child : children) {
            if (child.getId().equals(categoryId)) {
                return true;
            }
            // 递归检查子节点的子节点
            if (isChildNode(categoryId, child.getId(), allCategories)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 品牌类别关联请求类
     */
    public static class BrandCategoryRelation {
        private Long categoryId;
        private List<Long> brandIds;
        
        public Long getCategoryId() {
            return categoryId;
        }
        
        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }
        
        public List<Long> getBrandIds() {
            return brandIds;
        }
        
        public void setBrandIds(List<Long> brandIds) {
            this.brandIds = brandIds;
        }
    }
}
