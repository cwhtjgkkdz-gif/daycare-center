package com.soft231.daycarecenterbackend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.soft231.daycarecenterbackend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 财务记录实体 — 对应 finance 表
 * 记录收费和退费流水，流水号由系统自动生成
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("finance")
public class Finance extends BaseEntity {
    /** 流水号，格式 FIN-日期-随机码 */
    private String recordNo;
    /** 关联儿童姓名 */
    private String childName;
    /** 类型：收费/退费 */
    private String type;
    /** 金额（元） */
    private BigDecimal amount;
    /** 经办人 */
    private String handler;
    /** 办理时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime handleTime;
    /** 备注 */
    private String remark;
}
