package com.soft231.daycarecenterbackend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.soft231.daycarecenterbackend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 园区活动实体 — 对应 activity 表
 * 记录园区举办的各类活动信息（节日庆典、户外活动、亲子活动等）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("activity")
public class Activity extends BaseEntity {
    /** 活动标题 */
    private String title;
    /** 活动详细内容 */
    private String content;
    /** 活动类型：户外活动/室内活动/节日庆典/亲子活动/实践活动 */
    private String type;
    /** 活动地点 */
    private String location;
    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    /** 发布人 */
    private String publisher;
}
