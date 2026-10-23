package com.soft231.daycarecenterbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.entity.Child;

public interface ChildService {
    Page<Child> page(int current, int size, String name, Long classId, Integer status);
    Child getById(Long id);
    boolean add(Child child);
    boolean update(Child child);
    boolean delete(Long id);
    boolean updateStatus(Long id, Integer status);
}
