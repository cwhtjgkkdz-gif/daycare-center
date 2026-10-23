package com.soft231.daycarecenterbackend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.soft231.daycarecenterbackend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 菜单/权限
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class Menu extends BaseEntity {

    /** 父菜单ID（0=顶级） */
    private Long parentId;

    /** 菜单名称 */
    private String menuName;

    /** 菜单类型：M=目录，C=菜单，F=按钮 */
    private String menuType;

    /** 路由路径 */
    private String path;

    /** 前端组件路径 */
    private String component;

    /** 权限标识 */
    private String perms;

    /** 图标 */
    private String icon;

    /** 排序 */
    private Integer sort;

    /** 是否可见：1=可见，0=隐藏 */
    private Integer visible;

    /** 子菜单（非数据库字段） */
    @TableField(exist = false)
    private List<Menu> children;

    /** 是否已分配给某角色（非数据库字段，getRoleTree 时填充） */
    @TableField(exist = false)
    private Boolean checked;
}
