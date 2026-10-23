package com.soft231.daycarecenterbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.entity.Notice;
import com.soft231.daycarecenterbackend.mapper.NoticeMapper;
import com.soft231.daycarecenterbackend.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class NoticeServiceImpl implements NoticeService {
    @Autowired private NoticeMapper noticeMapper;

    @Override
    public Page<Notice> page(int current, int size, String title, String type) {
        LambdaQueryWrapper<Notice> w = new LambdaQueryWrapper<>();
        w.like(StringUtils.hasText(title), Notice::getTitle, title);
        w.eq(StringUtils.hasText(type), Notice::getType, type);
        w.orderByDesc(Notice::getCreateTime);
        return noticeMapper.selectPage(new Page<>(current, size), w);
    }

    @Override public Notice getById(Long id) { return noticeMapper.selectById(id); }
    @Override public boolean add(Notice n) {
        if (n.getPublishTime() == null) {
            n.setPublishTime(java.time.LocalDateTime.now());
        }
        return noticeMapper.insert(n) > 0;
    }
    @Override public boolean update(Notice n) { return noticeMapper.updateById(n) > 0; }
    @Override public boolean delete(Long id) { return noticeMapper.deleteById(id) > 0; }
}
