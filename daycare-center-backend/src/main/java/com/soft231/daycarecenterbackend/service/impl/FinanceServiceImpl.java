package com.soft231.daycarecenterbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.entity.Finance;
import com.soft231.daycarecenterbackend.mapper.FinanceMapper;
import com.soft231.daycarecenterbackend.service.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class FinanceServiceImpl implements FinanceService {
    @Autowired private FinanceMapper financeMapper;

    @Override
    public Page<Finance> page(int current, int size, String recordNo, String type, String childName) {
        LambdaQueryWrapper<Finance> w = new LambdaQueryWrapper<>();
        w.like(StringUtils.hasText(recordNo), Finance::getRecordNo, recordNo);
        w.eq(StringUtils.hasText(type), Finance::getType, type);
        w.like(StringUtils.hasText(childName), Finance::getChildName, childName);
        w.orderByDesc(Finance::getCreateTime);
        return financeMapper.selectPage(new Page<>(current, size), w);
    }

    @Override public Finance getById(Long id) { return financeMapper.selectById(id); }

    @Override
    public boolean add(Finance finance) {
        // 自动生成流水号 + 办理时间
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        finance.setRecordNo("FIN-" + date + "-" + uuid);
        if (finance.getHandleTime() == null) {
            finance.setHandleTime(LocalDateTime.now());
        }
        return financeMapper.insert(finance) > 0;
    }

    @Override public boolean update(Finance f) { return financeMapper.updateById(f) > 0; }
    @Override public boolean delete(Long id) { return financeMapper.deleteById(id) > 0; }
}
