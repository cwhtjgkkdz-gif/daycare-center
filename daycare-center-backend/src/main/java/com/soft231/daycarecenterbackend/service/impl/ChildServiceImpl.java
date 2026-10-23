package com.soft231.daycarecenterbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.entity.Child;
import com.soft231.daycarecenterbackend.entity.ClassInfo;
import com.soft231.daycarecenterbackend.mapper.ChildMapper;
import com.soft231.daycarecenterbackend.mapper.ClassInfoMapper;
import com.soft231.daycarecenterbackend.service.ChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ChildServiceImpl implements ChildService {
    @Autowired
    private ChildMapper childMapper;
    @Autowired
    private ClassInfoMapper classInfoMapper;

    @Override
    public Page<Child> page(int current, int size, String name, Long classId, Integer status) {
        LambdaQueryWrapper<Child> w = new LambdaQueryWrapper<>();
        w.like(StringUtils.hasText(name), Child::getName, name);
        w.eq(classId != null, Child::getClassId, classId);
        w.eq(status != null, Child::getStatus, status);
        w.orderByDesc(Child::getCreateTime);
        Page<Child> result = childMapper.selectPage(new Page<>(current, size), w);
        result.getRecords().forEach(this::fillClassName);
        return result;
    }

    @Override
    public Child getById(Long id) {
        Child child = childMapper.selectById(id);
        if (child != null) fillClassName(child);
        return child;
    }

    @Override
    public boolean add(Child child) {
        return childMapper.insert(child) > 0;
    }

    @Override
    public boolean update(Child child) {
        return childMapper.updateById(child) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return childMapper.deleteById(id) > 0;
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        Child c = new Child();
        c.setId(id);
        c.setStatus(status);
        return childMapper.updateById(c) > 0;
    }

    /**
     * 填充班级名称
     */
    private void fillClassName(Child child) {
        if (child.getClassId() != null) {
            ClassInfo cls = classInfoMapper.selectById(child.getClassId());
            if (cls != null) child.setClassName(cls.getClassName());
        }
    }
}
