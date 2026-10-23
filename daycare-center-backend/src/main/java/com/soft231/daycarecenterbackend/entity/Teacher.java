package com.soft231.daycarecenterbackend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.soft231.daycarecenterbackend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 教职工实体 — 对应 teacher 表
 * 记录园区教职工基本信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("teacher")
public class Teacher extends BaseEntity {
    /** 姓名 */
    private String name;
    /** 性别：男/女 */
    private String gender;
    /** 岗位：教师/保育员/后勤/财务 */
    private String position;
    /** 联系电话 */
    private String phone;
    /** 入职日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;
    /** 状态：1=在职，0=离职 */
    private Integer status;
}
