package com.soft231.daycarecenterbackend.service;

import java.util.List;
import java.util.Map;

/** 首页数据统计服务接口 — 为 ECharts 大屏提供数据 */
public interface DashboardService {
    /** 概览数据：在园儿童数/教职工数/班级数/本月收费 */
    Map<String, Object> summary();
    /** 各班级儿童人数 — 柱状图数据 */
    List<Map<String, Object>> childrenPerClass();
    /** 教职工岗位分布 — 饼图数据 */
    List<Map<String, Object>> teacherByPosition();
    /** 近6月收费趋势 — 折线图数据 */
    List<Map<String, Object>> monthlyFees();
    /** 儿童男女比例 — 环形图数据 */
    List<Map<String, Object>> genderRatio();
}
