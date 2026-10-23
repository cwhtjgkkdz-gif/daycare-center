package com.soft231.daycarecenterbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.entity.Role;

import java.util.List;

/** 角色管理服务接口 */
public interface RoleService {
    /** 分页查询角色 */
    Page<Role> page(int current, int size, String roleName);
    /** 查询全部角色列表 */
    List<Role> list();
    /** 按ID查询 */
    Role getById(Long id);
    /** 新增角色 */
    boolean add(Role role);
    /** 修改角色 */
    boolean update(Role role);
    /** 删除角色（逻辑删除） */
    boolean delete(Long id);
    /** 为角色分配菜单权限 — 先删旧再插新 */
    boolean assignMenus(Long roleId, List<Long> menuIds);
    /** 获取角色的菜单ID列表（用于回显已选中） */
    List<Long> getMenuIds(Long roleId);
}
