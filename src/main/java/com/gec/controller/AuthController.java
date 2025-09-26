package com.gec.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gec.common.R;
import com.gec.domain.entity.User;
import com.gec.dao.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        String account = loginData.get("account");
        String password = loginData.get("password");
        
        if (account == null || account.trim().isEmpty()) {
            return R.error("账号不能为空");
        }
        
        if (password == null || password.trim().isEmpty()) {
            return R.error("密码不能为空");
        }
        
        // 查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account)
                   .eq("status", 1);
        User user = userMapper.selectOne(queryWrapper);
        
        if (user == null) {
            return R.error("账号不存在");
        }
        
        // 验证密码（明文比较）
        if (!password.equals(user.getPassword())) {
            return R.error("密码错误");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        
        return R.success(result);
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public R<String> logout() {
        return R.success("登出成功");
    }
}
