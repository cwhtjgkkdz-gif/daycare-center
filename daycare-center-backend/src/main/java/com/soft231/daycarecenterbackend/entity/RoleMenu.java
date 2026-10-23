package com.soft231.daycarecenterbackend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.soft231.daycarecenterbackend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色-菜单关联
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_menu")
public class RoleMenu extends BaseEntity {

    /** 角色ID */
    private Long roleId;

    /** 菜单ID */
    private Long menuId;
}
