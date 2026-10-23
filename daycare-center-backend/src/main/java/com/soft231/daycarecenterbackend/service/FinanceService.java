package com.soft231.daycarecenterbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.entity.Finance;

/** 财务管理服务接口 */
public interface FinanceService {
    /** 分页查询财务记录，支持按流水号/类型/儿童姓名筛选 */
    Page<Finance> page(int current, int size, String recordNo, String type, String childName);
    /** 按ID查询 */
    Finance getById(Long id);
    /** 新增财务记录，流水号和办理时间自动生成 */
    boolean add(Finance finance);
    /** 修改财务记录 */
    boolean update(Finance finance);
    /** 删除财务记录（逻辑删除） */
    boolean delete(Long id);
}
