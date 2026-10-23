package com.soft231.daycarecenterbackend.config;

import com.soft231.daycarecenterbackend.entity.*;
import com.soft231.daycarecenterbackend.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 数据初始化 — 首次启动时自动插入默认角色、管理员、菜单
 */
@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private RoleMapper roleMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private MenuMapper menuMapper;
    @Autowired private RoleMenuMapper roleMenuMapper;

    @Override
    public void run(String... args) {
        initRoles();
        initAdminUser();
        initMenus();
        initRoleMenus();
    }

    private void initRoles() {
        if (roleMapper.selectCount(null) > 0) return;
        log.info(">>> 初始化默认角色...");
        Role r1 = new Role(); r1.setId(1L); r1.setRoleName("超级管理员"); r1.setRoleCode("ROLE_ADMIN"); r1.setDescription("拥有所有权限"); r1.setCreateTime(LocalDateTime.now()); r1.setUpdateTime(LocalDateTime.now()); roleMapper.insert(r1);
        Role r2 = new Role(); r2.setId(2L); r2.setRoleName("班主任"); r2.setRoleCode("ROLE_TEACHER"); r2.setDescription("班级管理、儿童档案、考勤管理"); r2.setCreateTime(LocalDateTime.now()); r2.setUpdateTime(LocalDateTime.now()); roleMapper.insert(r2);
        Role r3 = new Role(); r3.setId(3L); r3.setRoleName("财务人员"); r3.setRoleCode("ROLE_FINANCE"); r3.setDescription("财务管理、通知查看"); r3.setCreateTime(LocalDateTime.now()); r3.setUpdateTime(LocalDateTime.now()); roleMapper.insert(r3);
    }

    private void initAdminUser() {
        if (userMapper.selectCount(null) > 0) return;
        log.info(">>> 初始化管理员账号 (admin/admin123)...");
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(BCrypt.hashpw("admin123", BCrypt.gensalt()));
        admin.setRealName("系统管理员");
        admin.setRoleId(1L);
        admin.setStatus(1);
        admin.setCreateTime(LocalDateTime.now());
        admin.setUpdateTime(LocalDateTime.now());
        userMapper.insert(admin);
    }

    private void initMenus() {
        if (menuMapper.selectCount(null) > 0) return;
        log.info(">>> 初始化默认菜单...");
        // 一级
        addMenu(1L, 0L, "首页", "C", "/dashboard", "dashboard/index", "HomeFilled", 1);
        addMenu(2L, 0L, "系统管理", "M", "/system", null, "Setting", 2);
        addMenu(3L, 0L, "班级管理", "C", "/class", "class/index", "School", 3);
        addMenu(4L, 0L, "儿童档案", "C", "/child", "child/index", "UserFilled", 4);
        addMenu(5L, 0L, "教职工管理", "C", "/teacher", "teacher/index", "Avatar", 5);
        addMenu(6L, 0L, "园区活动", "C", "/activity", "activity/index", "Trophy", 6);
        addMenu(7L, 0L, "通知公告", "C", "/notice", "notice/index", "Bell", 7);
        addMenu(8L, 0L, "财务管理", "C", "/finance", "finance/index", "Money", 8);
        addMenu(9L, 0L, "考勤管理", "C", "/attendance", "attendance/index", "Clock", 9);
        // 系统管理子菜单
        addMenu(21L, 2L, "用户管理", "C", "/system/user", "system/user/index", "User", 1);
        addMenu(22L, 2L, "角色管理", "C", "/system/role", "system/role/index", "Key", 2);
        addMenu(23L, 2L, "菜单管理", "C", "/system/menu", "system/menu/index", "Menu", 3);
    }

    private void addMenu(Long id, Long parentId, String name, String type, String path, String component, String icon, int sort) {
        Menu m = new Menu();
        m.setId(id); m.setParentId(parentId); m.setMenuName(name); m.setMenuType(type);
        m.setPath(path); m.setComponent(component); m.setIcon(icon); m.setSort(sort);
        m.setVisible(1); m.setCreateTime(LocalDateTime.now()); m.setUpdateTime(LocalDateTime.now());
        menuMapper.insert(m);
    }

    private void initRoleMenus() {
        if (roleMenuMapper.selectCount(null) > 0) return;
        log.info(">>> 初始化角色菜单权限...");
        // 超管: 所有菜单
        Long[] adminMenus = {1L,2L,3L,4L,5L,6L,7L,8L,9L,21L,22L,23L};
        for (Long mid : adminMenus) addRoleMenu(1L, mid);
        // 班主任: 首页、班级、儿童、活动、通知、考勤
        Long[] teacherMenus = {1L,3L,4L,6L,7L,9L};
        for (Long mid : teacherMenus) addRoleMenu(2L, mid);
        // 财务: 首页、通知、财务
        Long[] financeMenus = {1L,7L,8L};
        for (Long mid : financeMenus) addRoleMenu(3L, mid);
    }

    private void addRoleMenu(Long roleId, Long menuId) {
        RoleMenu rm = new RoleMenu();
        rm.setRoleId(roleId); rm.setMenuId(menuId);
        rm.setCreateTime(LocalDateTime.now()); rm.setUpdateTime(LocalDateTime.now());
        roleMenuMapper.insert(rm);
    }
}
