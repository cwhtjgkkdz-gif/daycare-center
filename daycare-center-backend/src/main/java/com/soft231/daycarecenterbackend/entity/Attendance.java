package com.soft231.daycarecenterbackend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.soft231.daycarecenterbackend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 考勤记录实体 — 对应 attendance 表
 * 每个儿童每天最多一条记录，通过签到签退记录出勤情况
 * childId + attendanceDate 为联合唯一索引
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("attendance")
public class Attendance extends BaseEntity {
    /** 外键 → child.id */
    private Long childId;
    /** 外键 → class_info.id */
    private Long classId;
    /** 考勤日期 */
    private LocalDate attendanceDate;
    /** 签到时间 */
    private LocalDateTime checkInTime;
    /** 签退时间 */
    private LocalDateTime checkOutTime;
    /** 状态：出勤/缺勤/迟到/请假 */
    private String status;
    /** 备注（请假原因等） */
    private String remark;

    /** 儿童姓名 — 非数据库字段，Service层关联填充 */
    @TableField(exist = false)
    private String childName;

    /** 班级名称 — 非数据库字段，Service层关联填充 */
    @TableField(exist = false)
    private String className;
}
