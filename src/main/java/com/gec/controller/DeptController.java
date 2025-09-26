package com.gec.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gec.common.R;
import com.gec.domain.entity.Dept;
import com.gec.domain.entity.User;
import com.gec.dao.DeptMapper;
import com.gec.dao.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门控制器
 */
@RestController
@RequestMapping("/dept")
@CrossOrigin
public class DeptController {
    
    @Autowired
    private DeptMapper deptMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 获取部门树形结构
     */
    @GetMapping("/tree")
    public R<List<Dept>> getDeptTree() {
        QueryWrapper<Dept> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1)
                   .orderByAsc("sort_order", "id");
        List<Dept> deptList = deptMapper.selectList(queryWrapper);
        
        // 构建树形结构
        List<Dept> tree = buildTree(deptList, 0L);
        return R.success(tree);
    }
    
    /**
     * 根据ID查询部门
     */
    @GetMapping("/{id}")
    public R<Dept> getById(@PathVariable Long id) {
        Dept dept = deptMapper.selectById(id);
        return R.success(dept);
    }
    
    /**
     * 新增部门
     */
    @PostMapping
    public R<String> save(@RequestBody Dept dept) {
        // 设置层级
        if (dept.getParentId() == null || dept.getParentId() == 0) {
            dept.setLevel(1);
        } else {
            Dept parentDept = deptMapper.selectById(dept.getParentId());
            dept.setLevel(parentDept.getLevel() + 1);
        }
        
        int result = deptMapper.insert(dept);
        return result > 0 ? R.success("新增成功") : R.error("新增失败");
    }
    
    /**
     * 修改部门
     */
    @PutMapping
    public R<String> update(@RequestBody Dept dept) {
        // 验证父节点ID关联
        if (dept.getParentId() != null && dept.getParentId() != 0) {
            // 不能将自己设为自己的父节点
            if (dept.getId().equals(dept.getParentId())) {
                return R.error("不能将自己设为父节点");
            }
            
            // 检查是否会导致循环引用
            if (isCircularReference(dept.getId(), dept.getParentId())) {
                return R.error("不能将子节点设为父节点，会导致循环引用");
            }
        }
        
        int result = deptMapper.updateById(dept);
        return result > 0 ? R.success("修改成功") : R.error("修改失败");
    }
    
    /**
     * 删除部门
     */
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        // 检查是否有子部门
        QueryWrapper<Dept> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        Long count = deptMapper.selectCount(queryWrapper);
        if (count > 0) {
            return R.error("该部门下有子部门，不能删除");
        }
        
        // 检查是否有用户
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("dept_id", id);
        Long userCount = userMapper.selectCount(userQueryWrapper);
        if (userCount > 0) {
            return R.error("该部门下有用户，不能删除");
        }
        
        int result = deptMapper.deleteById(id);
        return result > 0 ? R.success("删除成功") : R.error("删除失败");
    }
    
    /**
     * 获取部门列表（扁平结构）
     */
    @GetMapping("/list")
    public R<List<Dept>> getDeptList() {
        QueryWrapper<Dept> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1)
                   .orderByAsc("sort_order", "id");
        List<Dept> deptList = deptMapper.selectList(queryWrapper);
        return R.success(deptList);
    }
    
    /**
     * 构建树形结构
     */
    private List<Dept> buildTree(List<Dept> deptList, Long parentId) {
        return deptList.stream()
                .filter(dept -> dept.getParentId().equals(parentId))
                .peek(dept -> dept.setChildren(buildTree(deptList, dept.getId())))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 检查是否会导致循环引用
     */
    private boolean isCircularReference(Long deptId, Long parentId) {
        // 获取所有部门
        List<Dept> allDepts = deptMapper.selectList(null);
        
        // 检查parentId是否是deptId的子节点
        return isChildNode(deptId, parentId, allDepts);
    }
    
    /**
     * 递归检查parentId是否是deptId的子节点
     */
    private boolean isChildNode(Long deptId, Long parentId, List<Dept> allDepts) {
        // 查找parentId的所有子节点
        List<Dept> children = allDepts.stream()
                .filter(dept -> parentId.equals(dept.getParentId()))
                .collect(java.util.stream.Collectors.toList());
        
        // 检查子节点中是否包含deptId
        for (Dept child : children) {
            if (child.getId().equals(deptId)) {
                return true;
            }
            // 递归检查子节点的子节点
            if (isChildNode(deptId, child.getId(), allDepts)) {
                return true;
            }
        }
        
        return false;
    }
}
