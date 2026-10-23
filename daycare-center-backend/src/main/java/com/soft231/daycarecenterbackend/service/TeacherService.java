package com.soft231.daycarecenterbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.entity.Teacher;
import java.util.List;

/** 教职工管理服务接口 */
public interface TeacherService {
    /** 分页查询教职工，支持按姓名/岗位/状态筛选 */
    Page<Teacher> page(int current, int size, String name, String position, Integer status);
    /** 查询全部教职工列表 */
    List<Teacher> list();
    /** 按ID查询 */
    Teacher getById(Long id);
    /** 新增教职工 */
    boolean add(Teacher teacher);
    /** 修改教职工 */
    boolean update(Teacher teacher);
    /** 删除教职工（逻辑删除） */
    boolean delete(Long id);
}
