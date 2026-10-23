package com.soft231.daycarecenterbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.entity.ClassInfo;
import com.soft231.daycarecenterbackend.mapper.ClassInfoMapper;
import com.soft231.daycarecenterbackend.service.ClassInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.List;

@Service
public class ClassInfoServiceImpl implements ClassInfoService {
    @Autowired private ClassInfoMapper classInfoMapper;

    @Override
    public Page<ClassInfo> page(int current, int size, String className, String classType) {
        LambdaQueryWrapper<ClassInfo> w = new LambdaQueryWrapper<>();
        w.like(StringUtils.hasText(className), ClassInfo::getClassName, className);
        w.eq(StringUtils.hasText(classType), ClassInfo::getClassType, classType);
        w.orderByDesc(ClassInfo::getCreateTime);
        return classInfoMapper.selectPage(new Page<>(current, size), w);
    }

    @Override
    public List<ClassInfo> list() {
        return classInfoMapper.selectList(null);
    }

    @Override public ClassInfo getById(Long id) { return classInfoMapper.selectById(id); }
    @Override public boolean add(ClassInfo c) { c.setCurrentCount(0); return classInfoMapper.insert(c) > 0; }
    @Override public boolean update(ClassInfo c) { return classInfoMapper.updateById(c) > 0; }
    @Override public boolean delete(Long id) { return classInfoMapper.deleteById(id) > 0; }
}
