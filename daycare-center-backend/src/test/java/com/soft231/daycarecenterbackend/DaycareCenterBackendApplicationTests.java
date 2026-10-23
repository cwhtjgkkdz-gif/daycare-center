package com.soft231.daycarecenterbackend;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 应用启动测试 — 需要数据库连接，默认跳过。
 * 运行前需先启动 MySQL 并执行 init.sql。
 */
@SpringBootTest
@Disabled("需要MySQL数据库连接，请先启动数据库后再运行此测试")
class DaycareCenterBackendApplicationTests {

    @Test
    void contextLoads() {
    }

}
