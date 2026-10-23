package com.soft231.daycarecenterbackend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.soft231.daycarecenterbackend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class Role extends BaseEntity {

    /** 角色名称 */
    private String roleName;

    /** 角色编码（ROLE_ADMIN, ROLE_TEACHER, ROLE_FINANCE） */
    private String roleCode;

    /** 角色描述 */
    private String description;
}
