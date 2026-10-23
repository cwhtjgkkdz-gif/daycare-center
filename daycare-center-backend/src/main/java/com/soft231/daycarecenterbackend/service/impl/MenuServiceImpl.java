package com.soft231.daycarecenterbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.soft231.daycarecenterbackend.entity.Menu;
import com.soft231.daycarecenterbackend.mapper.MenuMapper;
import com.soft231.daycarecenterbackend.mapper.RoleMenuMapper;
import com.soft231.daycarecenterbackend.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Override
    public List<Menu> getTree() {
        List<Menu> all = menuMapper.selectList(new LambdaQueryWrapper<Menu>().orderByAsc(Menu::getSort));
        return buildTree(all, 0L);
    }

    @Override
    public List<Menu> getRoleTree(Long roleId) {
        // 查询全部菜单和该角色已拥有的菜单ID
        List<Menu> all = menuMapper.selectList(new LambdaQueryWrapper<Menu>().orderByAsc(Menu::getSort));
        List<Long> roleMenuIds = roleMenuMapper.selectMenuIdsByRoleId(roleId);
        // 标记每个菜单是否已被分配给该角色（前端 el-tree 需要 checked 状态）
        for (Menu menu : all) {
            menu.setChecked(roleMenuIds.contains(menu.getId()));
        }
        return buildTree(all, 0L);
    }

    @Override
    public List<Menu> getUserMenus(Long roleId) {
        List<Long> menuIds = roleMenuMapper.selectMenuIdsByRoleId(roleId);
        if (menuIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Menu> all = menuMapper.selectList(
                new LambdaQueryWrapper<Menu>()
                        .in(Menu::getId, menuIds)
                        .eq(Menu::getVisible, 1)
                        .orderByAsc(Menu::getSort));
        // 只返回菜单和目录类型（不返回按钮）
        List<Menu> menus = all.stream()
                .filter(m -> "M".equals(m.getMenuType()) || "C".equals(m.getMenuType()))
                .collect(Collectors.toList());
        return buildTree(menus, 0L);
    }

    @Override
    public Menu getById(Long id) {
        return menuMapper.selectById(id);
    }

    @Override
    public boolean add(Menu menu) {
        return menuMapper.insert(menu) > 0;
    }

    @Override
    public boolean update(Menu menu) {
        return menuMapper.updateById(menu) > 0;
    }

    @Override
    public boolean delete(Long id) {
        // 有子菜单不能删除
        Long count = menuMapper.selectCount(new LambdaQueryWrapper<Menu>().eq(Menu::getParentId, id));
        if (count > 0) {
            // 同时删除子菜单
            menuMapper.delete(new LambdaQueryWrapper<Menu>().eq(Menu::getParentId, id));
        }
        return menuMapper.deleteById(id) > 0;
    }

    /** 构建树形结构 */
    private List<Menu> buildTree(List<Menu> all, Long parentId) {
        List<Menu> tree = new ArrayList<>();
        for (Menu menu : all) {
            if (menu.getParentId().equals(parentId)) {
                menu.setChildren(buildTree(all, menu.getId()));
                tree.add(menu);
            }
        }
        return tree;
    }
}
