package com.soft231.daycarecenterbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.entity.Notice;

/** 通知公告服务接口 */
public interface NoticeService {
    /** 分页查询通知，支持按标题和类型筛选 */
    Page<Notice> page(int current, int size, String title, String type);
    /** 按ID查询 */
    Notice getById(Long id);
    /** 新增通知，publishTime 自动设为当前时间 */
    boolean add(Notice notice);
    /** 修改通知 */
    boolean update(Notice notice);
    /** 删除通知（逻辑删除） */
    boolean delete(Long id);
}
