package com.soft231.daycarecenterbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.entity.Activity;

/** 园区活动服务接口 */
public interface ActivityService {
    /** 分页查询活动，支持按标题和类型筛选 */
    Page<Activity> page(int current, int size, String title, String type);
    /** 按ID查询 */
    Activity getById(Long id);
    /** 新增活动 */
    boolean add(Activity activity);
    /** 修改活动 */
    boolean update(Activity activity);
    /** 删除活动（逻辑删除） */
    boolean delete(Long id);
}
