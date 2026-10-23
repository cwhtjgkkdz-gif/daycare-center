package com.soft231.daycarecenterbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.entity.User;

public interface UserService {

    /** 分页查询用户（含角色名称） */
    Page<User> page(int current, int size, String username, String realName, Integer status);

    /** 根据ID查询 */
    User getById(Long id);

    /** 根据用户名查询 */
    User getByUsername(String username);

    /** 新增用户 */
    boolean add(User user);

    /** 修改用户 */
    boolean update(User user);

    /** 删除用户（逻辑删除） */
    boolean delete(Long id);

    /** 修改状态（启用/禁用） */
    boolean updateStatus(Long id, Integer status);

    /** 重置密码 */
    boolean resetPassword(Long id);
}
