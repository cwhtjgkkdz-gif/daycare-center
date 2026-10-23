package com.soft231.daycarecenterbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.exception.BusinessException;
import com.soft231.daycarecenterbackend.entity.Attendance;
import com.soft231.daycarecenterbackend.entity.Child;
import com.soft231.daycarecenterbackend.entity.ClassInfo;
import com.soft231.daycarecenterbackend.mapper.AttendanceMapper;
import com.soft231.daycarecenterbackend.mapper.ChildMapper;
import com.soft231.daycarecenterbackend.mapper.ClassInfoMapper;
import com.soft231.daycarecenterbackend.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    @Autowired private AttendanceMapper attendanceMapper;
    @Autowired private ChildMapper childMapper;
    @Autowired private ClassInfoMapper classInfoMapper;

    @Override
    public Page<Attendance> page(int current, int size, Long classId, LocalDate date) {
        LambdaQueryWrapper<Attendance> w = new LambdaQueryWrapper<>();
        w.eq(classId != null, Attendance::getClassId, classId);
        w.eq(date != null, Attendance::getAttendanceDate, date);
        w.orderByDesc(Attendance::getAttendanceDate);
        Page<Attendance> result = attendanceMapper.selectPage(new Page<>(current, size), w);
        result.getRecords().forEach(this::fillNames);
        return result;
    }

    @Override public Attendance getById(Long id) {
        Attendance att = attendanceMapper.selectById(id);
        if (att != null) fillNames(att);
        return att;
    }

    /** 填充儿童姓名和班级名称 */
    private void fillNames(Attendance att) {
        if (att.getChildId() != null) {
            Child child = childMapper.selectById(att.getChildId());
            if (child != null) att.setChildName(child.getName());
        }
        if (att.getClassId() != null) {
            ClassInfo cls = classInfoMapper.selectById(att.getClassId());
            if (cls != null) att.setClassName(cls.getClassName());
        }
    }

    @Override
    public boolean checkIn(Long childId, Long classId) {
        // 检查今日是否已有记录
        LambdaQueryWrapper<Attendance> w = new LambdaQueryWrapper<>();
        w.eq(Attendance::getChildId, childId);
        w.eq(Attendance::getAttendanceDate, LocalDate.now());
        Attendance exist = attendanceMapper.selectOne(w);
        if (exist != null) {
            throw new BusinessException("该儿童今日已签到");
        }
        Attendance a = new Attendance();
        a.setChildId(childId);
        a.setClassId(classId);
        a.setAttendanceDate(LocalDate.now());
        a.setCheckInTime(LocalDateTime.now());
        a.setStatus("出勤");
        return attendanceMapper.insert(a) > 0;
    }

    @Override
    public boolean checkOut(Long id) {
        Attendance a = new Attendance();
        a.setId(id);
        a.setCheckOutTime(LocalDateTime.now());
        return attendanceMapper.updateById(a) > 0;
    }

    @Override public boolean update(Attendance a) { return attendanceMapper.updateById(a) > 0; }
    @Override public boolean delete(Long id) { return attendanceMapper.deleteById(id) > 0; }

    @Override
    public Map<String, Object> statistics(Long classId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<Attendance> w = new LambdaQueryWrapper<>();
        w.eq(classId != null, Attendance::getClassId, classId);
        w.between(Attendance::getAttendanceDate, startDate, endDate);
        long total = attendanceMapper.selectCount(w);

        w.eq(Attendance::getStatus, "出勤");
        long present = attendanceMapper.selectCount(w);

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("present", present);
        result.put("absent", total - present);
        return result;
    }
}
