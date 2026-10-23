package com.soft231.daycarecenterbackend.aspect;

import java.lang.annotation.*;

/**
 * 操作日志注解 — 标记在Controller方法上，自动记录操作日志
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /** 模块名称 */
    String module() default "";

    /** 操作类型 */
    String operation() default "";
}
