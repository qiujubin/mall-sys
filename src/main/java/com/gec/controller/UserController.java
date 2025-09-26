package com.gec.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.common.PageQuery;
import com.gec.common.R;
import com.gec.domain.entity.User;
import com.gec.domain.entity.Dept;
import com.gec.domain.entity.Role;
import com.gec.dao.UserMapper;
import com.gec.dao.DeptMapper;
import com.gec.dao.RoleMapper;
import com.gec.dto.ImportResult;
import com.gec.util.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private DeptMapper deptMapper;
    
    @Autowired
    private RoleMapper roleMapper;
    
    /**
     * 分页查询用户列表
     */
    @PostMapping("/page")
    public R<Page<User>> page(@RequestBody PageQuery pageQuery) {
        Page<User> page = new Page<>(pageQuery.getCurrent(), pageQuery.getSize());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        
        if (pageQuery.getKeyword() != null && !pageQuery.getKeyword().trim().isEmpty()) {
            queryWrapper.like("nickname", pageQuery.getKeyword())
                       .or()
                       .like("account", pageQuery.getKeyword());
        }
        
        // 添加部门筛选条件
        if (pageQuery.getDeptId() != null) {
            queryWrapper.eq("dept_id", pageQuery.getDeptId());
        }
        
        queryWrapper.orderByDesc("create_time");
        Page<User> result = userMapper.selectPage(page, queryWrapper);
        return R.success(result);
    }
    
    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public R<User> getById(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        return R.success(user);
    }
    
    /**
     * 新增用户
     */
    @PostMapping
    public R<String> save(@RequestBody User user) {
        int result = userMapper.insert(user);
        return result > 0 ? R.success("新增成功") : R.error("新增失败");
    }
    
    /**
     * 修改用户
     */
    @PutMapping
    public R<String> update(@RequestBody User user) {
        int result = userMapper.updateById(user);
        return result > 0 ? R.success("修改成功") : R.error("修改失败");
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        int result = userMapper.deleteById(id);
        return result > 0 ? R.success("删除成功") : R.error("删除失败");
    }
    
    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    public R<String> batchDelete(@RequestBody List<Long> ids) {
        int result = userMapper.deleteBatchIds(ids);
        return result > 0 ? R.success("批量删除成功") : R.error("批量删除失败");
    }
    
    /**
     * 批量导入用户
     */
    @PostMapping("/import")
    public R<ImportResult> importUsers(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.error("上传文件不能为空");
        }
        
        try {
            List<List<String>> data;
            String filename = file.getOriginalFilename();
            
            // 根据文件类型解析数据
            if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
                data = ExcelUtil.parseExcel(file);
            } else if (filename.endsWith(".txt") || filename.endsWith(".csv")) {
                data = ExcelUtil.parseCsv(file);
            } else {
                return R.error("不支持的文件格式，请上传Excel或CSV文件");
            }
            
            if (data.isEmpty()) {
                return R.error("文件内容为空");
            }
            
            // 验证表头
            if (data.size() < 2) {
                return R.error("文件至少需要包含表头和一行数据");
            }
            
            List<String> header = data.get(0);
            if (header.size() < 8) {
                return R.error("文件格式不正确，请下载模板查看正确格式");
            }
            
            // 验证表头字段
            String[] expectedHeaders = {"账号", "密码", "昵称", "电话", "性别", "工号", "部门ID", "角色ID", "状态"};
            if (header.size() >= 8) {
                for (int i = 0; i < Math.min(header.size(), expectedHeaders.length); i++) {
                    if (!expectedHeaders[i].equals(header.get(i).trim())) {
                        return R.error("表头格式不正确，第" + (i+1) + "列应为'" + expectedHeaders[i] + "'，实际为'" + header.get(i) + "'");
                    }
                }
            }
            
            // 开始导入数据
            ImportResult result = new ImportResult();
            List<ImportResult.ImportError> errors = new ArrayList<>();
            
            // 跳过表头，从第二行开始处理
            for (int i = 1; i < data.size(); i++) {
                List<String> row = data.get(i);
                
                try {
                    // 验证必填字段
                    if (row.size() < 8) {
                        errors.add(new ImportResult.ImportError(i + 1, "数据列数不足"));
                        result.setFailCount(result.getFailCount() + 1);
                        continue;
                    }
                    
                    String account = row.get(0).trim();
                    String password = row.get(1).trim();
                    String nickname = row.get(2).trim();
                    String phone = row.get(3).trim();
                    String gender = row.get(4).trim();
                    String employeeId = row.get(5).trim();
                    String deptIdStr = row.get(6).trim();
                    String roleIdStr = row.get(7).trim();
                    String statusStr = row.size() > 8 ? row.get(8).trim() : "1"; // 状态字段可选，默认为1
                    
                    // 验证必填字段
                    if (account.isEmpty() || password.isEmpty() || nickname.isEmpty() || 
                        phone.isEmpty() || gender.isEmpty() || employeeId.isEmpty() || 
                        deptIdStr.isEmpty() || roleIdStr.isEmpty()) {
                        errors.add(new ImportResult.ImportError(i + 1, "必填字段不能为空"));
                        result.setFailCount(result.getFailCount() + 1);
                        continue;
                    }
                    
                    // 验证性别
                    if (!"男".equals(gender) && !"女".equals(gender)) {
                        errors.add(new ImportResult.ImportError(i + 1, "性别只能是'男'或'女'"));
                        result.setFailCount(result.getFailCount() + 1);
                        continue;
                    }
                    
                    // 验证状态
                    Integer status = 1; // 默认启用
                    if (!statusStr.isEmpty()) {
                        try {
                            status = Integer.parseInt(statusStr);
                            if (status != 0 && status != 1) {
                                errors.add(new ImportResult.ImportError(i + 1, "状态只能是0(禁用)或1(启用)"));
                                result.setFailCount(result.getFailCount() + 1);
                                continue;
                            }
                        } catch (NumberFormatException e) {
                            errors.add(new ImportResult.ImportError(i + 1, "状态格式不正确，应为0或1"));
                            result.setFailCount(result.getFailCount() + 1);
                            continue;
                        }
                    }
                    
                    // 验证部门ID和角色ID
                    Long deptId, roleId;
                    try {
                        deptId = Long.parseLong(deptIdStr);
                        roleId = Long.parseLong(roleIdStr);
                    } catch (NumberFormatException e) {
                        errors.add(new ImportResult.ImportError(i + 1, "部门ID或角色ID格式不正确"));
                        result.setFailCount(result.getFailCount() + 1);
                        continue;
                    }
                    
                    // 验证部门ID是否存在
                    QueryWrapper<Dept> deptQuery = new QueryWrapper<>();
                    deptQuery.eq("id", deptId).eq("deleted", 0);
                    Dept dept = deptMapper.selectOne(deptQuery);
                    if (dept == null) {
                        errors.add(new ImportResult.ImportError(i + 1, "部门ID " + deptId + " 不存在"));
                        result.setFailCount(result.getFailCount() + 1);
                        continue;
                    }
                    
                    // 验证角色ID是否存在
                    QueryWrapper<Role> roleQuery = new QueryWrapper<>();
                    roleQuery.eq("id", roleId).eq("deleted", 0);
                    Role role = roleMapper.selectOne(roleQuery);
                    if (role == null) {
                        errors.add(new ImportResult.ImportError(i + 1, "角色ID " + roleId + " 不存在"));
                        result.setFailCount(result.getFailCount() + 1);
                        continue;
                    }
                    
                    // 检查账号是否已存在
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("account", account);
                    User existingUser = userMapper.selectOne(queryWrapper);
                    if (existingUser != null) {
                        errors.add(new ImportResult.ImportError(i + 1, "账号已存在"));
                        result.setFailCount(result.getFailCount() + 1);
                        continue;
                    }
                    
                    // 创建用户对象
                    User user = new User();
                    user.setAccount(account);
                    user.setPassword(password);
                    user.setNickname(nickname);
                    user.setPhone(phone);
                    user.setGender(gender);
                    user.setEmployeeId(employeeId);
                    user.setDeptId(deptId);
                    user.setRoleId(roleId);
                    user.setStatus(status);
                    
                    // 插入用户
                    int insertResult = userMapper.insert(user);
                    if (insertResult > 0) {
                        result.setSuccessCount(result.getSuccessCount() + 1);
                    } else {
                        errors.add(new ImportResult.ImportError(i + 1, "插入数据库失败"));
                        result.setFailCount(result.getFailCount() + 1);
                    }
                    
                } catch (Exception e) {
                    errors.add(new ImportResult.ImportError(i + 1, "处理数据时发生错误: " + e.getMessage()));
                    result.setFailCount(result.getFailCount() + 1);
                }
            }
            
            result.setErrors(errors);
            return R.success(result);
            
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("文件解析失败: " + e.getMessage());
        }
    }
}
