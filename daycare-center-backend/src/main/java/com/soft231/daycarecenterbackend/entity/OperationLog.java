package com.soft231.daycarecenterbackend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.soft231.daycarecenterbackend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作日志
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_operation_log")
public class OperationLog extends BaseEntity {

    /** 模块名称 */
    private String module;

    /** 操作类型（INSERT/UPDATE/DELETE） */
    private String operation;

    /** 请求方法（类.方法） */
    private String method;

    /** 请求参数 */
    private String requestParams;

    /** 操作人 */
    private String operator;

    /** 请求IP */
    private String ip;

    /** 执行耗时（毫秒） */
    private Long costTime;
}
