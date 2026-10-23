package com.soft231.daycarecenterbackend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.soft231.daycarecenterbackend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 儿童档案实体 — 对应 child 表
 * 记录在园儿童的基本信息、家长联系方式和过敏史
 * classId 外键关联 class_info 表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("child")
public class Child extends BaseEntity {
    /** 儿童姓名 */
    private String name;
    /** 性别：男/女 */
    private String gender;
    /** 出生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    /** 外键 → class_info.id */
    private Long classId;
    /** 家长姓名 */
    private String parentName;
    /** 家长联系电话 */
    private String parentPhone;
    /** 过敏史（如鸡蛋过敏、花粉过敏，无则填"无"） */
    private String allergies;
    /** 状态：1=在园，0=离园 */
    private Integer status;

    /** 班级名称 — 非数据库字段，Service层关联 class_info 填充 */
    @TableField(exist = false)
    private String className;
}
