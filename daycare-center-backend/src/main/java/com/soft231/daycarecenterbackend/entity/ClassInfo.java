package com.soft231.daycarecenterbackend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.soft231.daycarecenterbackend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 班级实体 — 对应 class_info 表
 * 记录班级基本信息和容量，currentCount 由儿童数量动态决定
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("class_info")
public class ClassInfo extends BaseEntity {
    /** 班级名称（如萌萌班、芽芽一班） */
    private String className;
    /** 班级类型：托班/小班/中班/大班 */
    private String classType;
    /** 最大容量 */
    private Integer maxCapacity;
    /** 当前在班人数 */
    private Integer currentCount;
    /** 备注 */
    private String description;
}
