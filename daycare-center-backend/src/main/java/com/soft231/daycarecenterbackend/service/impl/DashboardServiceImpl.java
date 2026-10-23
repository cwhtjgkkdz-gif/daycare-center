package com.soft231.daycarecenterbackend.service.impl;

import com.soft231.daycarecenterbackend.mapper.ChildMapper;
import com.soft231.daycarecenterbackend.mapper.ClassInfoMapper;
import com.soft231.daycarecenterbackend.mapper.TeacherMapper;
import com.soft231.daycarecenterbackend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private ChildMapper childMapper;
    @Autowired private ClassInfoMapper classInfoMapper;
    @Autowired private TeacherMapper teacherMapper;

    @Override
    public Map<String, Object> summary() {
        Map<String, Object> result = new HashMap<>();

        // 在园儿童（status=1 且未逻辑删除）
        Long activeChildren = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM child WHERE status = 1 AND is_deleted = 0", Long.class);
        result.put("totalChildren", activeChildren != null ? activeChildren : 0);

        // 在职教职工（status=1，排除已离职）
        Long activeTeacher = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM teacher WHERE status = 1 AND is_deleted = 0", Long.class);
        result.put("totalTeacher", activeTeacher != null ? activeTeacher : 0);

        // 班级总数
        result.put("totalClasses", classInfoMapper.selectCount(null));

        // 本月收费总额（按 handle_time 判断当月）
        Double monthlyFee = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(amount), 0) FROM finance WHERE type = '收费' " +
                "AND DATE_FORMAT(handle_time, '%Y-%m') = DATE_FORMAT(CURDATE(), '%Y-%m') AND is_deleted = 0",
                Double.class);
        result.put("monthlyFee", monthlyFee != null ? monthlyFee : 0);

        return result;
    }

    @Override
    public List<Map<String, Object>> childrenPerClass() {
        return jdbcTemplate.queryForList(
                "SELECT c.class_name AS name, COUNT(ch.id) AS value " +
                "FROM class_info c LEFT JOIN child ch ON c.id = ch.class_id AND ch.status = 1 AND ch.is_deleted = 0 " +
                "WHERE c.is_deleted = 0 GROUP BY c.id, c.class_name");
    }

    @Override
    public List<Map<String, Object>> teacherByPosition() {
        return jdbcTemplate.queryForList(
                "SELECT position AS name, COUNT(*) AS value FROM teacher " +
                "WHERE status = 1 AND is_deleted = 0 GROUP BY position");
    }

    @Override
    public List<Map<String, Object>> monthlyFees() {
        return jdbcTemplate.queryForList(
                "SELECT DATE_FORMAT(handle_time, '%Y-%m') AS name, COALESCE(SUM(amount), 0) AS value " +
                "FROM finance WHERE type = '收费' AND is_deleted = 0 " +
                "AND handle_time >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) " +
                "GROUP BY DATE_FORMAT(handle_time, '%Y-%m') ORDER BY name");
    }

    @Override
    public List<Map<String, Object>> genderRatio() {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "SELECT CASE WHEN gender = '男' THEN '男' ELSE '女' END AS name, COUNT(*) AS value " +
                "FROM child WHERE status = 1 AND is_deleted = 0 GROUP BY gender");
        return list;
    }
}
