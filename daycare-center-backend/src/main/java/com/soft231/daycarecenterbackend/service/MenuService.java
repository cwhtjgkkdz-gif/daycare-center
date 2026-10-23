package com.soft231.daycarecenterbackend.service;

import com.soft231.daycarecenterbackend.entity.Menu;

import java.util.List;

public interface MenuService {

    /** 获取菜单树 */
    List<Menu> getTree();

    /** 获取角色菜单树（带选中状态） */
    List<Menu> getRoleTree(Long roleId);

    /** 获取用户菜单列表（根据角色） */
    List<Menu> getUserMenus(Long roleId);

    Menu getById(Long id);

    boolean add(Menu menu);

    boolean update(Menu menu);

    boolean delete(Long id);
}
