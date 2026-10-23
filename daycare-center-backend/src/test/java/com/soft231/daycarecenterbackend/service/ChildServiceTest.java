package com.soft231.daycarecenterbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft231.daycarecenterbackend.entity.Child;
import com.soft231.daycarecenterbackend.mapper.ChildMapper;
import com.soft231.daycarecenterbackend.mapper.ClassInfoMapper;
import com.soft231.daycarecenterbackend.service.impl.ChildServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * ChildService 单元测试 — 增删改查 + 分页 5项测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("儿童档案服务 - 单元测试")
class ChildServiceTest {

    @Mock
    private ChildMapper childMapper;

    @Mock
    private ClassInfoMapper classInfoMapper;

    @InjectMocks
    private ChildServiceImpl childService;

    private Child testChild;

    @BeforeEach
    void setUp() {
        testChild = new Child();
        testChild.setId(1L);
        testChild.setName("张三");
        testChild.setGender("男");
        testChild.setBirthDate(LocalDate.of(2020, 6, 15));
        // classId=null 避免fillClassName触发classInfoMapper调用
        // 需要className的测试单独设置classId和mock
        testChild.setParentName("张爸爸");
        testChild.setParentPhone("13800138000");
        testChild.setAllergies("无");
        testChild.setStatus(1);
    }

    // ======================== 1. 新增测试 ========================

    @Nested
    @DisplayName("1. 新增儿童")
    class AddTests {

        @Test
        @DisplayName("1.1 正常新增 — 返回true")
        void shouldAddSuccessfully() {
            when(childMapper.insert(any(Child.class))).thenReturn(1);

            boolean result = childService.add(testChild);

            assertTrue(result, "新增应返回true");
            verify(childMapper, times(1)).insert(any(Child.class));

            // 验证传入的 Child 数据正确
            ArgumentCaptor<Child> captor = ArgumentCaptor.forClass(Child.class);
            verify(childMapper).insert(captor.capture());
            Child captured = captor.getValue();
            assertEquals("张三", captured.getName());
            assertEquals("男", captured.getGender());
            assertEquals(LocalDate.of(2020, 6, 15), captured.getBirthDate());
        }

        @Test
        @DisplayName("1.2 新增失败 — 数据库返回0行")
        void shouldReturnFalseWhenInsertFails() {
            when(childMapper.insert(any(Child.class))).thenReturn(0);

            boolean result = childService.add(new Child());

            assertFalse(result, "插入0行应返回false");
        }
    }

    // ======================== 2. 删除测试 ========================

    @Nested
    @DisplayName("2. 删除儿童")
    class DeleteTests {

        @Test
        @DisplayName("2.1 正常删除 — 返回true")
        void shouldDeleteSuccessfully() {
            when(childMapper.deleteById(eq(1L))).thenReturn(1);

            boolean result = childService.delete(1L);

            assertTrue(result, "删除应返回true");
            verify(childMapper, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("2.2 删除不存在的记录 — 返回false")
        void shouldReturnFalseWhenDeleteNonexistent() {
            when(childMapper.deleteById(eq(999L))).thenReturn(0);

            boolean result = childService.delete(999L);

            assertFalse(result, "删除不存在记录应返回false");
        }
    }

    // ======================== 3. 修改测试 ========================

    @Nested
    @DisplayName("3. 修改儿童")
    class UpdateTests {

        @Test
        @DisplayName("3.1 正常修改 — 返回true")
        void shouldUpdateSuccessfully() {
            when(childMapper.updateById(any(Child.class))).thenReturn(1);

            Child update = new Child();
            update.setId(1L);
            update.setName("张三改");
            update.setParentPhone("13900139000");

            boolean result = childService.update(update);

            assertTrue(result, "修改应返回true");
            verify(childMapper, times(1)).updateById(any(Child.class));

            ArgumentCaptor<Child> captor = ArgumentCaptor.forClass(Child.class);
            verify(childMapper).updateById(captor.capture());
            assertEquals(1L, captor.getValue().getId());
            assertEquals("张三改", captor.getValue().getName());
            assertEquals("13900139000", captor.getValue().getParentPhone());
        }

        @Test
        @DisplayName("3.2 修改失败 — 返回false")
        void shouldReturnFalseWhenUpdateFails() {
            when(childMapper.updateById(any(Child.class))).thenReturn(0);

            Child update = new Child();
            update.setId(1L);
            boolean result = childService.update(update);

            assertFalse(result, "修改0行应返回false");
        }
    }

    // ======================== 4. 查询测试 ========================

    @Nested
    @DisplayName("4. 根据ID查询儿童")
    class GetByIdTests {

        @Test
        @DisplayName("4.1 正常查询 — 返回完整儿童信息")
        void shouldReturnChildWhenExists() {
            when(childMapper.selectById(eq(1L))).thenReturn(testChild);

            Child result = childService.getById(1L);

            assertNotNull(result, "查询结果不应为null");
            assertEquals(1L, result.getId());
            assertEquals("张三", result.getName());
            assertEquals("男", result.getGender());
            assertEquals(LocalDate.of(2020, 6, 15), result.getBirthDate());
            assertEquals("张爸爸", result.getParentName());
            assertEquals("13800138000", result.getParentPhone());
            assertEquals("无", result.getAllergies());
            assertEquals(1, result.getStatus());
        }

        @Test
        @DisplayName("4.2 查询不存在 — 返回null")
        void shouldReturnNullWhenNotExists() {
            when(childMapper.selectById(eq(999L))).thenReturn(null);

            Child result = childService.getById(999L);

            assertNull(result, "不存在的记录应返回null");
        }
    }

    // ======================== 5. 分页查询测试 ========================

    @Nested
    @DisplayName("5. 分页查询儿童")
    class PageTests {

        @Test
        @DisplayName("5.1 无条件分页 — 返回分页结果")
        void shouldReturnPagedResultsWithoutFilters() {
            // 模拟 MyBatis-Plus 分页
            Page<Child> mockPage = new Page<>(1, 10);
            mockPage.setTotal(1);
            mockPage.setRecords(java.util.List.of(testChild));

            when(childMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(mockPage);

            Page<Child> result = childService.page(1, 10, null, null, null);

            assertNotNull(result, "分页结果不应为null");
            assertEquals(1, result.getTotal(), "总数应为1");
            assertEquals(1, result.getRecords().size(), "记录数应为1");
            assertEquals("张三", result.getRecords().get(0).getName());
        }

        @Test
        @DisplayName("5.2 按姓名模糊查询")
        void shouldFilterByName() {
            when(childMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(new Page<>(1, 10));

            childService.page(1, 10, "张", null, null);

            // 验证调用了 selectPage
            verify(childMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("5.3 按班级ID精确查询")
        void shouldFilterByClassId() {
            when(childMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(new Page<>(1, 10));

            childService.page(1, 10, null, 1L, null);

            verify(childMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("5.4 按状态筛选")
        void shouldFilterByStatus() {
            when(childMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(new Page<>(1, 10));

            childService.page(1, 10, null, null, 1);

            verify(childMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("5.5 组合条件查询 — 姓名+班级+状态")
        void shouldFilterByCombinedConditions() {
            Page<Child> mockPage = new Page<>(1, 10);
            mockPage.setTotal(2);
            mockPage.setRecords(java.util.List.of(testChild));
            when(childMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(mockPage);

            Page<Child> result = childService.page(1, 20, "张", 1L, 1);

            assertNotNull(result);
            assertEquals(2, result.getTotal());
            verify(childMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("5.6 空结果分页")
        void shouldReturnEmptyWhenNoMatch() {
            Page<Child> mockPage = new Page<>(1, 10);
            mockPage.setTotal(0);
            mockPage.setRecords(java.util.List.of());
            when(childMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(mockPage);

            Page<Child> result = childService.page(1, 10, "不存在的名字", null, null);

            assertEquals(0, result.getTotal(), "总数应为0");
            assertTrue(result.getRecords().isEmpty(), "记录列表应为空");
        }

        @Test
        @DisplayName("5.7 修改状态 — 返回true")
        void shouldUpdateStatus() {
            when(childMapper.updateById(any(Child.class))).thenReturn(1);

            boolean result = childService.updateStatus(1L, 1);

            assertTrue(result, "修改状态应返回true");
            verify(childMapper, times(1)).updateById(any(Child.class));
        }
    }
}
