package com.soft231.daycarecenterbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.entity.ClassInfo;
import java.util.List;

/** 班级管理服务接口 */
public interface ClassInfoService {
    /** 分页查询班级 */
    Page<ClassInfo> page(int current, int size, String className, String classType);
    /** 查询全部班级列表（用于下拉选择等） */
    List<ClassInfo> list();
    /** 按ID查询 */
    ClassInfo getById(Long id);
    /** 新增班级，currentCount 自动设为 0 */
    boolean add(ClassInfo classInfo);
    /** 修改班级 */
    boolean update(ClassInfo classInfo);
    /** 删除班级（逻辑删除） */
    boolean delete(Long id);
}
