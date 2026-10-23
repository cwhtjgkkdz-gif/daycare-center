package com.soft231.daycarecenterbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.entity.Attendance;
import java.time.LocalDate;
import java.util.Map;

/** 考勤管理服务接口 */
public interface AttendanceService {
    /** 分页查询考勤记录 */
    Page<Attendance> page(int current, int size, Long classId, LocalDate date);
    /** 按ID查询 */
    Attendance getById(Long id);
    /** 儿童签到 — 每日每儿童只能签到一次 */
    boolean checkIn(Long childId, Long classId);
    /** 签退 */
    boolean checkOut(Long id);
    /** 修改考勤记录 */
    boolean update(Attendance attendance);
    /** 删除考勤记录 */
    boolean delete(Long id);
    /** 考勤统计 — 返回 total/present/absent */
    Map<String, Object> statistics(Long classId, LocalDate startDate, LocalDate endDate);
}
