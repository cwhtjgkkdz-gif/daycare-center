package com.soft231.daycarecenterbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.entity.ClassInfo;
import com.soft231.daycarecenterbackend.mapper.ClassInfoMapper;
import com.soft231.daycarecenterbackend.service.impl.ClassInfoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 班级管理服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("班级管理服务 - 单元测试")
class ClassInfoServiceTest {

    @Mock
    private ClassInfoMapper classInfoMapper;

    @InjectMocks
    private ClassInfoServiceImpl classInfoService;

    private ClassInfo testClass;

    @BeforeEach
    void setUp() {
        testClass = new ClassInfo();
        testClass.setId(1L);
        testClass.setClassName("萌萌班");
        testClass.setClassType("托班");
        testClass.setMaxCapacity(15);
        testClass.setCurrentCount(12);
        testClass.setDescription("1.5-2岁婴幼儿托育班");
    }

    @Test
    @DisplayName("1. 新增班级 — currentCount自动设为0")
    void addShouldSetCurrentCountZero() {
        when(classInfoMapper.insert(any(ClassInfo.class))).thenReturn(1);

        ClassInfo c = new ClassInfo();
        c.setClassName("草莓班");
        c.setClassType("托班");
        c.setCurrentCount(100); // 前端可能乱传值
        boolean result = classInfoService.add(c);

        assertTrue(result);
        assertEquals(0, c.getCurrentCount(), "新增时currentCount必须被强制设为0");
        verify(classInfoMapper, times(1)).insert(c);
    }

    @Test
    @DisplayName("2. 根据ID查询 — 存在时返回班级信息")
    void getByIdShouldReturnClassInfo() {
        when(classInfoMapper.selectById(1L)).thenReturn(testClass);

        ClassInfo result = classInfoService.getById(1L);

        assertNotNull(result);
        assertEquals("萌萌班", result.getClassName());
        assertEquals("托班", result.getClassType());
    }

    @Test
    @DisplayName("3. 查询全部班级列表")
    void listShouldReturnAllNonDeletedClasses() {
        when(classInfoMapper.selectList(null))
                .thenReturn(List.of(testClass));

        List<ClassInfo> result = classInfoService.list();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("4. 删除班级 — 逻辑删除返回true")
    void deleteShouldReturnTrue() {
        when(classInfoMapper.deleteById(1L)).thenReturn(1);

        boolean result = classInfoService.delete(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("5. 修改班级 — 返回true")
    void updateShouldReturnTrue() {
        when(classInfoMapper.updateById(any(ClassInfo.class))).thenReturn(1);

        boolean result = classInfoService.update(testClass);

        assertTrue(result);
    }
}