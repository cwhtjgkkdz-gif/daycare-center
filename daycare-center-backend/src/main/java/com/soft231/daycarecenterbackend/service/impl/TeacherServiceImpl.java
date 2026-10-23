package com.soft231.daycarecenterbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.entity.Teacher;
import com.soft231.daycarecenterbackend.mapper.TeacherMapper;
import com.soft231.daycarecenterbackend.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {
    @Autowired private TeacherMapper teacherMapper;

    @Override
    public Page<Teacher> page(int current, int size, String name, String position, Integer status) {
        LambdaQueryWrapper<Teacher> w = new LambdaQueryWrapper<>();
        w.like(StringUtils.hasText(name), Teacher::getName, name);
        w.eq(StringUtils.hasText(position), Teacher::getPosition, position);
        w.eq(status != null, Teacher::getStatus, status);
        w.orderByDesc(Teacher::getCreateTime);
        return teacherMapper.selectPage(new Page<>(current, size), w);
    }

    @Override public List<Teacher> list() { return teacherMapper.selectList(null); }
    @Override public Teacher getById(Long id) { return teacherMapper.selectById(id); }
    @Override public boolean add(Teacher t) { return teacherMapper.insert(t) > 0; }
    @Override public boolean update(Teacher t) { return teacherMapper.updateById(t) > 0; }
    @Override public boolean delete(Long id) { return teacherMapper.deleteById(id) > 0; }
}
