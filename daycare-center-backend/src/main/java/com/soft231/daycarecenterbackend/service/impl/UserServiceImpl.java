package com.soft231.daycarecenterbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.exception.BusinessException;
import com.soft231.daycarecenterbackend.entity.Role;
import com.soft231.daycarecenterbackend.entity.User;
import com.soft231.daycarecenterbackend.mapper.RoleMapper;
import com.soft231.daycarecenterbackend.mapper.UserMapper;
import com.soft231.daycarecenterbackend.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    public Page<User> page(int current, int size, String username, String realName, Integer status) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(username), User::getUsername, username);
        wrapper.like(StringUtils.hasText(realName), User::getRealName, realName);
        wrapper.eq(status != null, User::getStatus, status);
        wrapper.orderByDesc(User::getCreateTime);

        Page<User> page = userMapper.selectPage(new Page<>(current, size), wrapper);

        // 填充角色名称
        for (User user : page.getRecords()) {
            if (user.getRoleId() != null) {
                Role role = roleMapper.selectById(user.getRoleId());
                if (role != null) {
                    user.setRoleName(role.getRoleName());
                }
            }
        }
        return page;
    }

    @Override
    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user != null && user.getRoleId() != null) {
            Role role = roleMapper.selectById(user.getRoleId());
            if (role != null) {
                user.setRoleName(role.getRoleName());
            }
        }
        return user;
    }

    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public boolean add(User user) {
        // 检查用户名唯一
        User exist = getByUsername(user.getUsername());
        if (exist != null) {
            throw new BusinessException("用户名已存在");
        }
        user.setPassword(BCrypt.hashpw(DEFAULT_PASSWORD, BCrypt.gensalt()));
        user.setStatus(1);
        return userMapper.insert(user) > 0;
    }

    @Override
    public boolean update(User user) {
        // 只更新非空字段，不允许修改用户名和密码（通过专门接口修改）
        User target = userMapper.selectById(user.getId());
        if (target != null) {
            if (user.getRealName() != null) target.setRealName(user.getRealName());
            if (user.getPhone() != null) target.setPhone(user.getPhone());
            if (user.getEmail() != null) target.setEmail(user.getEmail());
            if (user.getRoleId() != null) target.setRoleId(user.getRoleId());
            if (user.getStatus() != null) target.setStatus(user.getStatus());
            return userMapper.updateById(target) > 0;
        }
        return false;
    }

    @Override
    public boolean delete(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        return userMapper.updateById(user) > 0;
    }

    @Override
    public boolean resetPassword(Long id) {
        User user = new User();
        user.setId(id);
        user.setPassword(BCrypt.hashpw(DEFAULT_PASSWORD, BCrypt.gensalt()));
        return userMapper.updateById(user) > 0;
    }
}
