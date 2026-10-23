package com.soft231.daycarecenterbackend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.soft231.daycarecenterbackend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统用户
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity {

    /** 用户名 */
    private String username;

    /** 密码（BCrypt加密） */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 角色ID */
    private Long roleId;

    /** 状态：1=启用，0=禁用 */
    private Integer status;

    /** 角色名称（非数据库字段，关联查询用） */
    @TableField(exist = false)
    private String roleName;
}
