package com.soft231.daycarecenterbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.exception.BusinessException;
import com.soft231.daycarecenterbackend.entity.Role;
import com.soft231.daycarecenterbackend.entity.RoleMenu;
import com.soft231.daycarecenterbackend.mapper.RoleMapper;
import com.soft231.daycarecenterbackend.mapper.RoleMenuMapper;
import com.soft231.daycarecenterbackend.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Override
    public Page<Role> page(int current, int size, String roleName) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(roleName), Role::getRoleName, roleName);
        wrapper.orderByDesc(Role::getCreateTime);
        return roleMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Override
    public List<Role> list() {
        return roleMapper.selectList(null);
    }

    @Override
    public Role getById(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    public boolean add(Role role) {
        return roleMapper.insert(role) > 0;
    }

    @Override
    public boolean update(Role role) {
        return roleMapper.updateById(role) > 0;
    }

    @Override
    public boolean delete(Long id) {
        // 内置角色不允许删除
        Role role = roleMapper.selectById(id);
        if (role != null && "ROLE_ADMIN".equals(role.getRoleCode())) {
            throw new BusinessException("超级管理员角色不可删除");
        }
        return roleMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean assignMenus(Long roleId, List<Long> menuIds) {
        // 删除原有权限
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId, roleId);
        roleMenuMapper.delete(wrapper);
        // 插入新权限
        for (Long menuId : menuIds) {
            RoleMenu rm = new RoleMenu();
            rm.setRoleId(roleId);
            rm.setMenuId(menuId);
            roleMenuMapper.insert(rm);
        }
        return true;
    }

    @Override
    public List<Long> getMenuIds(Long roleId) {
        return roleMenuMapper.selectMenuIdsByRoleId(roleId);
    }
}
