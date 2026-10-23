package com.soft231.daycarecenterbackend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.soft231.daycarecenterbackend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 通知公告实体 — 对应 notice 表
 * 用于发布园区通知、公告和温馨提示
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("notice")
public class Notice extends BaseEntity {
    /** 标题 */
    private String title;
    /** 正文内容 */
    private String content;
    /** 类型：通知/公告/温馨提示 */
    private String type;
    /** 发布人 */
    private String publisher;
    /** 发布时间，新增时自动设为当前时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;
}
