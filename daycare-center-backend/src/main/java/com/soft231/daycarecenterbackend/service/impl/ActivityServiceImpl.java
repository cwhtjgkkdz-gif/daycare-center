package com.soft231.daycarecenterbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.entity.Activity;
import com.soft231.daycarecenterbackend.mapper.ActivityMapper;
import com.soft231.daycarecenterbackend.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ActivityServiceImpl implements ActivityService {
    @Autowired private ActivityMapper activityMapper;

    @Override
    public Page<Activity> page(int current, int size, String title, String type) {
        LambdaQueryWrapper<Activity> w = new LambdaQueryWrapper<>();
        w.like(StringUtils.hasText(title), Activity::getTitle, title);
        w.eq(StringUtils.hasText(type), Activity::getType, type);
        w.orderByDesc(Activity::getCreateTime);
        return activityMapper.selectPage(new Page<>(current, size), w);
    }

    @Override public Activity getById(Long id) { return activityMapper.selectById(id); }
    @Override public boolean add(Activity a) { return activityMapper.insert(a) > 0; }
    @Override public boolean update(Activity a) { return activityMapper.updateById(a) > 0; }
    @Override public boolean delete(Long id) { return activityMapper.deleteById(id) > 0; }
}
